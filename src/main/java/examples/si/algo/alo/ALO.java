package examples.si.algo.alo;

import org.usa.soc.core.AbsAgent;
import org.usa.soc.core.ds.Markers;
import org.usa.soc.si.Agent;
import org.usa.soc.si.SIAlgorithm;
import org.usa.soc.si.ObjectiveFunction;
import org.usa.soc.core.ds.Vector;
import org.usa.soc.util.Randoms;
import org.usa.soc.util.Validator;

import java.awt.*;
import java.util.Arrays;

public class ALO extends SIAlgorithm {

    private int numberOfAnts;
    private double minFs, maxFs;

    public ALO(
            ObjectiveFunction objectiveFunction,
            int numberOfAnts,
            int numberOfIterations,
            int numberOfDimensions,
            double[] minBoundary,
            double[] maxBoundary,
            boolean isGlobalMinima
    ) {
        this.objectiveFunction = objectiveFunction;
        this.numberOfAnts = numberOfAnts;
        this.stepsCount = numberOfIterations;
        this.minBoundary = minBoundary;
        this.maxBoundary = maxBoundary;
        this.numberOfDimensions = numberOfDimensions;
        this.isGlobalMinima.setValue(isGlobalMinima);
        this.gBest = Randoms.getRandomVector(numberOfDimensions, minBoundary, maxBoundary);

        try{
            addAgents("ants", Markers.CIRCLE, Color.BLUE);
            addAgents("antLions", Markers.CROSS, Color.RED);
        }catch (Exception e){
            e.printStackTrace();
        }

        this.minFs = Double.MIN_VALUE;
        this.maxFs = Double.MAX_VALUE;
    }

    @Override
    public void step() throws Exception{

        double I = calculateI((int) currentStep, stepsCount);
        for (AbsAgent a: agents.get("ants").getAgents()) {
            int selectedAntLionIndex = getAntLionIndexFromRouletteWheel();

            Vector minValuesVector = new Vector(numberOfDimensions).setMaxVector();
            Vector maxValuesVector = new Vector(numberOfDimensions).setMinVector();

            for (AbsAgent a1: agents.get("ants").getAgents()){
                for(int i=0; i< a1.getPosition().getNumberOfDimensions();i++){
                    double v = a1.getPosition().getValue(i);
                    minValuesVector.setValue(Math.min(v, minValuesVector.getValue(i)), i);
                    maxValuesVector.setValue(Math.max(v, maxValuesVector.getValue(i)), i);
                }
            }

            Vector RA = getNewPosition(minValuesVector, maxValuesVector, agents.get("antLions").getAgents().get(selectedAntLionIndex).getPosition(), I,  (int)currentStep);
            Vector RE = getNewPosition(minValuesVector, maxValuesVector, gBest, I, (int)currentStep);
            Vector R = RA.operate(Vector.OPERATOR.ADD, RE).operate(Vector.OPERATOR.DIV, 2.0).fixVector(minBoundary, maxBoundary);

            a.setPosition(R);
            ((Agent)a).setFitnessValue(getObjectiveFunction().setParameters(a.getPosition().getPositionIndexes()).call());

        }

        for(int i=0; i<numberOfAnts; i++){
            if(Validator.validateBestValue(
                    ((Ant)agents.get("ants").getAgents().get(i)).getFitnessValue(),
                    ((Ant)agents.get("antLions").getAgents().get(i)).getFitnessValue(), !isGlobalMinima.isSet())){
                    agents.get("antLions").getAgents().set(i, ((Ant)agents.get("ants").getAgents().get(i)).cloneAnt());
            }
        }

        Ant elite = (Ant)agents.get("antLions").getAgents().get(0);
        for(int i=0; i<numberOfAnts; i++) {
            Ant a = (Ant)agents.get("antLions").getAgents().get(i);
            if(Validator.validateBestValue(a.getFitnessValue(), elite.getFitnessValue(), isGlobalMinima.isSet())){
                elite = a.cloneAnt();
            }else{
                agents.get("antLions").getAgents().set(i, elite.cloneAnt());
            }
        }

        if(Validator.validateBestValue(
                getObjectiveFunction().setParameters(elite.getPosition().getPositionIndexes()).call(),
                getObjectiveFunction().setParameters(this.gBest.getPositionIndexes()).call(),
                isGlobalMinima.isSet()
        )){
            this.gBest.setVector(elite.getPosition());
        }

    }

    private Vector getNewPosition(Vector minValuesVector, Vector maxValuesVector, Vector currentVector, Double I, int step){
        double C = Randoms.rand(0,1) < 0.5 ? 1.0 : -1.0;
        Vector CI = minValuesVector.operate(Vector.OPERATOR.DIV, I)
                .operate(Vector.OPERATOR.MULP, C)
                .operate(Vector.OPERATOR.ADD, currentVector);

        double D = Randoms.rand(0,1) >= 0.5 ? 1.0 : -1.0;
        Vector DI = maxValuesVector.operate(Vector.OPERATOR.DIV, I)
                .operate(Vector.OPERATOR.MULP, D)
                .operate(Vector.OPERATOR.ADD, currentVector);


        double []randomWalk = getRandomWalk(step);
        double rw = randomWalk[randomWalk.length -1];
        Arrays.sort(randomWalk);

        Vector newPos = new Vector(numberOfDimensions);

        for(int i=0; i< numberOfDimensions;i++){
            double val =  (rw - randomWalk[0]) * (DI.getValue(i) - CI.getValue(i));
            val /= ((randomWalk[randomWalk.length -1] - randomWalk[0])+1);
            val += CI.getValue(i);
            newPos.setValue(val, i);
        }

        return newPos;
    }

    private double[] getRandomWalk(int step) {
        double randomWalk[] = new double[step + 1];
        randomWalk[0] = 0;

        for(int i=1;i<=step;i++){
            randomWalk[i] = randomWalk[i-1] + 2*((Randoms.rand(0,1) < 0.5 ? 1 : 0) -1);
        }
        return randomWalk;
    }

    private double calculateI(int step, int stepsCount) {
        double w;
        if((double)step > 0.1*stepsCount){
            w = 2;
        }else if((double)step > 0.5*stepsCount){
            w = 3;
        }else if((double)step > 0.75*stepsCount){
            w = 4;
        }else if((double)step > 0.9*stepsCount){
            w = 5;
        }else if((double)step > 0.95*stepsCount){
            w =6;
        }else{
            w = 1;
        }
        return Math.pow(10, w) * (((double)step + 1) / (double)stepsCount);
    }

    @Override
    public void initialize() {
        this.setInitialized(true);
        Validator.checkBoundaries(this.minBoundary, this.maxBoundary, this.numberOfDimensions);

        for (int i = 0; i < this.numberOfAnts; i++) {
            Ant ant = new Ant(numberOfDimensions, minBoundary, maxBoundary);
            ant.setFitnessValue(getObjectiveFunction().setParameters(ant.getPosition().getPositionIndexes()).call());
            agents.get("ants").getAgents().add(ant);
        }

        Ant elite = new Ant(numberOfDimensions, minBoundary, maxBoundary);
        for (int i = 0; i < this.numberOfAnts; i++) {
            Ant antLion = new Ant(numberOfDimensions, minBoundary, maxBoundary);
            antLion.setFitnessValue(getObjectiveFunction().setParameters(antLion.getPosition().getPositionIndexes()).call());
            agents.get("antLions").getAgents().add(antLion);
            this.maxFs = Math.max(this.maxFs, antLion.getFitnessValue());
            this.minFs = Math.min(this.minFs, antLion.getFitnessValue());

            if(Validator.validateBestValue(antLion.getFitnessValue(), elite.getFitnessValue(), isGlobalMinima.isSet())){
                elite = antLion;
            }
        }
        this.gBest.setVector(elite.getPosition());
    }

    private int getAntLionIndexFromRouletteWheel(){
        double deltaFs = maxFs - minFs;
        double fsb = isGlobalMinima.isSet() ? maxFs : minFs;
        double p0 = Randoms.rand(0,1);

        for(int i=0; i<numberOfAnts; i++){
            double p = Math.abs(((Ant)agents.get("antLions").getAgents().get(i)).getFitnessValue() - fsb) / deltaFs;
            if(p > p0){
                return i;
            }
        }
        return 0;
    }
}
