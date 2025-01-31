package examples.si.algo.mbo;

import org.usa.soc.core.ds.Vector;
import org.usa.soc.si.Agent;
import org.usa.soc.util.Randoms;

public class Drone extends Agent {
    public Drone(int numberOfDimensions, double[] minBoundary, double[] maxBoundary) {
        this.position = Randoms.getRandomVector(numberOfDimensions, minBoundary, maxBoundary);
        this.maxBoundary = maxBoundary;
        this.minBoundary = minBoundary;
    }

}
