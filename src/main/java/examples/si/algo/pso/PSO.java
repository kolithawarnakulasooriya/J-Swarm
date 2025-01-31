package examples.si.algo.pso;

import org.usa.soc.core.AbsAgent;
import org.usa.soc.si.SIAlgorithm;
import org.usa.soc.si.ObjectiveFunction;
import org.usa.soc.core.ds.Vector;
import org.usa.soc.util.Mathamatics;
import org.usa.soc.util.Randoms;
import org.usa.soc.util.Validator;

import java.util.ArrayList;

public class PSO extends SIAlgorithm implements Cloneable {

    private int particleCount;

    private final Double c1;
    private final Double c2;
    private final Double wMax;
    private final Double wMin;

    public PSO(
            ObjectiveFunction<Double> objectiveFunction,
            int particleCount,
            int numberOfDimensions,
            int stepsCount,
            double c1,
            double c2,
            double w,
            double[] minBoundary,
            double[] maxBoundary,
            boolean isGlobalMinima) {

        this.particleCount = particleCount;
        this.numberOfDimensions = numberOfDimensions;
        this.objectiveFunction = objectiveFunction;
        this.stepsCount = stepsCount;
        this.minBoundary = minBoundary;
        this.maxBoundary = maxBoundary;
        this.c1 = c1;
        this.c2 = c2;
        this.wMax = w;
        this.wMin = w;

        setFirstAgents("particles", new ArrayList<>(particleCount));
        this.gBest = new Vector(numberOfDimensions);
        this.isGlobalMinima.setValue(isGlobalMinima);
        this.getGBest().resetAllValues(isGlobalMinima ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY);
    }

    public PSO(
            ObjectiveFunction<Double> objectiveFunction,
            int particleCount,
            int numberOfDimensions,
            int stepsCount,
            double c1,
            double c2,
            double wMax,
            double wMin,
            double[] minBoundary,
            double[] maxBoundary,
            boolean isGlobalMinima) {

        this.particleCount = particleCount;
        this.numberOfDimensions = numberOfDimensions;
        this.objectiveFunction = objectiveFunction;
        this.stepsCount = stepsCount;
        this.c1 = c1;
        this.c2 = c2;
        this.wMax = wMax;
        this.wMin = wMin;
        this.minBoundary = minBoundary;
        this.maxBoundary = maxBoundary;
        setFirstAgents("particles", new ArrayList<>(particleCount));
        this.gBest = new Vector(numberOfDimensions);
        this.isGlobalMinima.setValue(isGlobalMinima);
        this.getGBest().resetAllValues(isGlobalMinima ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY);
    }

    @Override
    public void step() throws Exception {
            // update positions
            for (AbsAgent agent : getFirstAgents()) {
                Particle p = (Particle)agent;
                p.updatePbest(this.getObjectiveFunction(), this.isGlobalMinima.isSet());
                this.updateGBest(p.getPBest(), this.getGBest());
            }

            // update velocity factor
            for (AbsAgent agent : getFirstAgents()) {
                Particle p = (Particle)agent;
                Vector v = p.updateVelocity(this.getGBest(), this.c1, this.c2, this.calculateW(wMax, wMin, (int) currentStep));
                if(Validator.validateBestValue(
                        getObjectiveFunction().setParameters(v.getPositionIndexes()).call(),
                        getObjectiveFunction().setParameters(p.getPosition().getPositionIndexes()).call(),
                        isGlobalMinima.isSet()
                )){
                 p.setPosition(v.getClonedVector());
                }
            }
    }

    private Double calculateW(Double wMax, Double wMin, int step) {
        if (wMax == wMin)
            return wMin;
        else {
            return wMax - step * ((wMax - wMin) / this.getStepsCount());
        }
    }

    @Override
    public void initialize() {

        this.setInitialized(true);

        Validator.checkBoundaries(this.minBoundary, this.maxBoundary, this.numberOfDimensions);
        Validator.checkMinMax(wMax, wMin);

        // initialize particles
        for (int i = 0; i < this.particleCount; i++) {
            Particle p = new Particle(this.minBoundary, this.maxBoundary, this.numberOfDimensions);
            Vector vc = getRandomPosition(Randoms.getRandomVector(numberOfDimensions, this.minBoundary, this.maxBoundary));
            p.setPosition(vc);
            p.setPBest(vc);
            getFirstAgents().add(p);
            this.updateGBest(((Particle)getFirstAgents().get(i)).getPBest(), this.getGBest());
        }

        gBest = Randoms.getRandomVector(numberOfDimensions, minBoundary, maxBoundary);
        updateBestValue();
    }

    private Vector getRandomPosition(Vector v) {

        Double[] center = Mathamatics.getCenterPoint(this.numberOfDimensions, this.minBoundary, this.maxBoundary);

        if (getObjectiveFunction().setParameters(v.getPositionIndexes()).validateRange()) {
            return v;
        }

        boolean[] isPlus = new boolean[numberOfDimensions];
        for (int i = 0; i < numberOfDimensions; i++) {
            isPlus[i] = v.getValue(i) < center[i];
        }

        while (!isTerminated(isPlus, v.getPositionIndexes(), center, numberOfDimensions)) {

            for (int i = 0; i < numberOfDimensions; i++) {
                Double dt = v.getValue(i) + (isPlus[i] ? 0.1 : -0.1);
                v.setValue(dt, i);
                if (getObjectiveFunction().setParameters(v.getPositionIndexes()).validateRange()) {
                    return v;
                }
            }
        }

        return v;
    }

    private boolean isTerminated(boolean[] isPlus, Double[] positionIndexes, Double[] center, int D) {
        boolean shouldRun = true;
        for (int i = 0; i < D; i++) {
            if (isPlus[i]) {
                shouldRun = shouldRun && positionIndexes[i] < center[i];
            } else {
                shouldRun = shouldRun && positionIndexes[i] > center[i];
            }
        }
        return !shouldRun;
    }

    private void updateGBest(Vector pBestPosition, Vector gBestPosition) {

        ObjectiveFunction tfn = this.getObjectiveFunction().setParameters(pBestPosition.getPositionIndexes());
        Double fpbest = tfn.call();
        Double fgbest = this.getObjectiveFunction().setParameters(gBestPosition.getPositionIndexes()).call();

        if (Validator.validateBestValue(fpbest, fgbest, isGlobalMinima.isSet())) {
            this.gBest.setVector(getRandomPosition(pBestPosition.getClonedVector()), minBoundary, maxBoundary);
            updateBestValue();
        }
    }

    @Override
    public SIAlgorithm clone() throws CloneNotSupportedException {
        return new PSO(getObjectiveFunction(), particleCount,
                numberOfDimensions,
                getStepsCount(),
                c1,
                c2,
                wMax,
                wMin,
                minBoundary,
                maxBoundary,
                isGlobalMinima.isSet());
    }
}
