package TSOA;

import org.junit.jupiter.api.Test;
import org.usa.soc.Algorithm;
import org.usa.soc.ObjectiveFunction;
import org.usa.soc.benchmarks.DynamicUnimodalObjectiveFunctions.Function1;
import org.usa.soc.benchmarks.singleObjective.AckleysFunction;
import org.usa.soc.core.Action;
import org.usa.soc.core.Vector;
import utils.AssertUtil;

public class TestTSOA {

    private static final int LIMIT = 2;
    private static final double PRECISION_VAL = 10;
    private Algorithm algo;

    private Algorithm getAlgorithm(ObjectiveFunction fn, int p, int s, double df) {
        return new org.usa.soc.tsoa.TSOA(
                fn,
                p,
                s,
                fn.getNumberOfDimensions(),
                fn.getMin(),
                fn.getMax(),
                true,
                df,
                2
        );
    }

    public void evaluate(Algorithm algo, double best, double[] variables, int D, double variance) {
        AssertUtil.evaluate(
                algo.getBestDoubleValue(),
                best,
                algo.getGBest(),
                variables,
                D,
                variance,
                LIMIT
        );
    }

    @Test
    public void testFunction1Function() {

        ObjectiveFunction fn = new Function1(100);
        algo = getAlgorithm(fn, 500, 1000, 0.7);
        algo.addStepAction(new Action() {
            @Override
            public void performAction(Vector best, Double bestValue, int step) {
                System.out.println(bestValue + " | "+ step);
            }
        });
        algo.initialize();
        try {
            algo.runOptimizer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(algo.getBestDoubleValue() + " " + algo);
        evaluate(algo, fn.getExpectedBestValue(), fn.getExpectedParameters(), fn.getNumberOfDimensions(), PRECISION_VAL);
    }
}
