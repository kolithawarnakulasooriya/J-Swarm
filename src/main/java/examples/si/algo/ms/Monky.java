package examples.si.algo.ms;

import org.usa.soc.si.Agent;
import org.usa.soc.si.ObjectiveFunction;
import org.usa.soc.core.ds.Vector;
import org.usa.soc.util.Randoms;
import org.usa.soc.util.Validator;

public class Monky extends Agent {

    private int maxHeightOfTheTree;

    private ObjectiveFunction fn;
    public Monky(double[] maxBoundary, double[] minBoundary, int numberOfDimensions, int maxHeightOfTheTree, ObjectiveFunction fn) {
        this.maxBoundary = maxBoundary;
        this.minBoundary = minBoundary;
        this.numberOfDimensions = numberOfDimensions;
        this.maxHeightOfTheTree = maxHeightOfTheTree;
        this.fn=fn;
        this.position = Randoms.getRandomVector(numberOfDimensions, minBoundary, maxBoundary,0,1);
    }

    public Vector getBestRoot() {
        return position.getClonedVector();
    }

    public void climbTree(double c1, boolean isGlobalMinima, Vector bestRoot) {

        Vector position = this.position.getClonedVector();
        for(int i=0; i< this.maxHeightOfTheTree;i++){
            Vector v = createRandomRoot(position, c1);
            Vector r1 = position.operate(Vector.OPERATOR.ADD, v);
            Vector r2 = position.operate(Vector.OPERATOR.SUB, v);

            double vr = this.fn.setParameters(position.getPositionIndexes()).call();
            double vr1 = this.fn.setParameters(r1.getPositionIndexes()).call();
            double vr2 = this.fn.setParameters(r2.getPositionIndexes()).call();

            if(Validator.validateBestValue(vr1, vr, isGlobalMinima)){
                position.setVector(position.operate(Vector.OPERATOR.SUB,r1), minBoundary, maxBoundary);
            }

            if(Validator.validateBestValue(vr2, vr, isGlobalMinima)){
                position.setVector(position.operate(Vector.OPERATOR.SUB,r2), minBoundary, maxBoundary);
            }
        }
        this.position.setVector(this.getBestRoot().operate(Vector.OPERATOR.ADD,position), this.minBoundary, this.maxBoundary);
    }

    private Vector createRandomRoot(Vector position, double c1) {

        double c1r1 = c1 * Randoms.rand(0,1);
        return position.getClonedVector().operate(Vector.OPERATOR.MULP, c1r1);
    }
}
