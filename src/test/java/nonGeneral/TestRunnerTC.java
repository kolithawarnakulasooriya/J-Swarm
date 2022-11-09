package nonGeneral;

/*
Settings
 */

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.usa.soc.Algorithm;
import org.usa.soc.ObjectiveFunction;
import org.usa.soc.benchmarks.nonGeneral.classical.multimodal.nonseparable.*;
import org.usa.soc.benchmarks.nonGeneral.classical.multimodal.separable.*;
import org.usa.soc.benchmarks.nonGeneral.classical.unimodal.nonseparable.*;
import org.usa.soc.benchmarks.nonGeneral.classical.unimodal.separable.*;
import org.usa.soc.core.Action;
import org.usa.soc.core.Vector;
import org.usa.soc.util.Mathamatics;
import ui.AlgoStore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.generate;

public class TestRunnerTC {

    private static final int REPEATER = 5;
    private static final int AGENT_COUNT = 1000;
    private static final int STEPS_COUNT = 1000;
    private static final int ALGO_INDEX = 2;
    private static final ObjectiveFunction OBJECTIVE_FUNCTION = new DixonPriceFunction();
    private static final String RESULT_FILE = "data/";

    class Result {
        double bestValue, convergenceValue;
        long time;

        String toStringValue;

        String resultFile;
    }

    public static Algorithm getAlgorithm(int index,ObjectiveFunction fn) {
        AlgorithmStore algorithmStore = new AlgorithmStore();

        switch (index){
            case 0: return algorithmStore.getPSO(fn, AGENT_COUNT, STEPS_COUNT);
            case 1: return algorithmStore.getCSO(fn, AGENT_COUNT, STEPS_COUNT);
            case 2: return algorithmStore.getGSO(fn, AGENT_COUNT, STEPS_COUNT);
            case 3: return algorithmStore.getWSO(fn, AGENT_COUNT, STEPS_COUNT);
            case 4: return algorithmStore.getCS(fn, AGENT_COUNT, STEPS_COUNT);
            case 5: return algorithmStore.getFA(fn, AGENT_COUNT, STEPS_COUNT);
            case 6: return algorithmStore.getABC(fn, AGENT_COUNT, STEPS_COUNT);
            case 7: return algorithmStore.getBA(fn, AGENT_COUNT, STEPS_COUNT);
            case 8: return algorithmStore.getTCO(fn, AGENT_COUNT, STEPS_COUNT);
            case 9: return algorithmStore.getGWO(fn, AGENT_COUNT, STEPS_COUNT);
            case 10: return algorithmStore.getMFA(fn, AGENT_COUNT, STEPS_COUNT);
            case 11: return algorithmStore.getALO(fn, AGENT_COUNT, STEPS_COUNT);
            case 12: return algorithmStore.getGEO(fn, AGENT_COUNT, STEPS_COUNT);
            case 13: return algorithmStore.getALSO(fn, AGENT_COUNT, STEPS_COUNT);
        }
        return null;
    }

    class RunnerTest {
        public Algorithm getAlgorithm(){
            return new AlgoStore(ALGO_INDEX, OBJECTIVE_FUNCTION).getAlgorithm(STEPS_COUNT, AGENT_COUNT);
        }

        private Result RunTest(int threadId,int index, ObjectiveFunction fn){

            Algorithm algorithm = TestRunnerTC.getAlgorithm(index, fn);

            if(algorithm == null){
                return null;
            }

            double fraction = STEPS_COUNT/100;

            String filename = RESULT_FILE+"logs/["+threadId+"]"+algorithm.getClass().getSimpleName()+"_"+algorithm.getFunction().getClass().getSimpleName()+"_"+System.currentTimeMillis()+".csv";
            Path p = createFile(filename);

            algorithm.addStepAction(new Action() {
                @Override
                public void performAction(Vector best, Double bestValue, int step) {
                    if((step % fraction) == 0){
                        System.out.print("\r [("+ threadId +") "+ algorithm.getClass().getSimpleName()+" - "+ algorithm.getFunction().getClass().getSimpleName()+"]:["+ Mathamatics.round(bestValue, 3) +"] ["+step/fraction+"%] "  + generate(() -> "#").limit((long)(step/fraction)).collect(joining()));
                    }
                    appendToFile(p, algorithm.getBestDoubleValue()+","+TimeUnit.MILLISECONDS.convert(algorithm.getNanoDuration(), TimeUnit.NANOSECONDS)+","+algorithm.getConvergenceValue()+"\n", false);
                }
            });
            algorithm.initialize();
            algorithm.runOptimizer();

            Result result = new Result();

            result.bestValue = algorithm.getBestDoubleValue();
            result.time = algorithm.getNanoDuration();
            result.convergenceValue = algorithm.getConvergenceValue();
            result.toStringValue = algorithm.toString();
            result.resultFile = filename;

            return result;
        }
    }

    public void runMultiple(ObjectiveFunction function, int algorithmIndex, String extra) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(REPEATER);
        Result[] results = new Result[REPEATER];
        for(int i=0;i<REPEATER;i++){
            int finalI = i;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println( " ["+ finalI +"] Started");
                    results[finalI] = new RunnerTest().RunTest(finalI, algorithmIndex, function);
                    latch.countDown();
                }
            });
            Thread.sleep(10);
            t.start();
        }
        latch.await();
        Algorithm algorithm = getAlgorithm(algorithmIndex, function);
        StringBuffer sb = new StringBuffer();
        sb.append(new Date().toString()).append(',');
        sb.append(extra).append(',');
        sb.append(algorithm.getClass().getSimpleName()).append(',');
        sb.append(algorithm.getFunction().getClass().getSimpleName()).append(',');
        sb.append(algorithm.getFunction().getNumberOfDimensions()).append(',');
        sb.append(AGENT_COUNT).append(',');
        sb.append(algorithm.getStepsCount()).append(',');
        sb.append(algorithm.getFunction().getExpectedBestValue()).append(',');

        double totalBestValue =0, totalConvergence=0, standardDeviation=0;
        long totalTime=0;
        ArrayList<Double> bestValuesArray = new ArrayList<>();
        StringBuffer sbv = new StringBuffer();

        for(Result result: results){
            totalTime += result.time;
            totalBestValue += result.bestValue;
            totalConvergence += result.convergenceValue;
            bestValuesArray.add(result.bestValue);
            sbv.append(result.resultFile);
            sbv.append(",");
        }
        double [] arr = bestValuesArray.stream().mapToDouble(Double::doubleValue).toArray();
        standardDeviation = new StandardDeviation().evaluate(arr, totalBestValue/ REPEATER);

        sb.append(totalBestValue/REPEATER).append(',');
        sb.append(standardDeviation).append(',');
        sb.append(TimeUnit.MILLISECONDS.convert((totalTime/REPEATER), TimeUnit.NANOSECONDS)).append(',');
        sb.append(totalConvergence/REPEATER).append(',');
        sb.append(Arrays.toString(arr));
        sb.append(sbv).append(',');

        sb.append('\n');

        Path r = createResultFile(algorithm.getClass().getSimpleName());
        appendToFile(r, sb.toString(), true);
    }

    private void runC(List<ObjectiveFunction> fns, int i, String e, Path p){
        for (ObjectiveFunction fn: fns) {
            try{
                new TestRunnerTC().runMultiple(fn,i,e);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public void runT(
            int index,
            List<ObjectiveFunction> a,
            List<ObjectiveFunction> b,
            List<ObjectiveFunction> c,
            List<ObjectiveFunction> d
    ) {
        runC(a, index, "Multi Modal - Non Separable", null);
        runC(b, index, "Multi Modal - Separable", null);
        runC(c, index, "Uni Modal - Non Separable", null);
        runC(d, index, "Uni Modal - Separable", null);
    }

    public static void main(String[] args) throws InterruptedException {

        // list of Objective Functions
        List<ObjectiveFunction> multimodalNonSeparableFunctionList = Arrays.asList(
                new AckleysFunction(),
                new ColvilleFunction(),
                new CrossInTrayFunction(),
                new GoldsteinPrice(),
                new McCormickFunction(),
                new ThreeHumpCamelFunction(),
                new ZakharovFunction()
        );

        List<ObjectiveFunction> multimodalSeparableFunctionList = Arrays.asList(
                new Alpine1Function(),
                new BohachevskFunction(),
                new Bukin4Function(),
                new CsendesFunction(),
                new Debfunction(),
                new EasomFunction(),
                new SphereFunction()
        );

        List<ObjectiveFunction> unimodalNonSeparableFunctionList = Arrays.asList(
                new BealeFunction(),
                new BoothsFunction(),
                new DixonPriceFunction(),
                new MatyasFunction(),
                new SchafferFunction(),
                new Schwefel12Function(),
                new Schwefel22Function()
        );

        List<ObjectiveFunction> unimodalSeparableFunctionList = Arrays.asList(
                new ChungReynoldsSquares(),
                new PowellSumFunction(),
                new QuarticFunction(),
                new SchumerSteiglitzFunction(),
                new StepFunction(),
                new StepintFunction(),
                new SumSquares()
        );

        // start log file writer
        for(int k =0; k< 14;k++){
            new TestRunnerTC().runT(
                    k,
                    multimodalNonSeparableFunctionList,
                    multimodalSeparableFunctionList,
                    unimodalNonSeparableFunctionList,
                    unimodalSeparableFunctionList
            );
        }
    }

    private static void appendToFile(Path path, String data, boolean f){
        if(data.isEmpty()){
            return;
        }
        try {
            if(f)
                System.out.println("\n"+data);
            Files.write(path, data.getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Path createResultFile(){
        return createResultFile(null);
    }

    private static Path createResultFile(String l){
        StringBuffer sb = new StringBuffer();
        sb.append("Date").append(',');
        sb.append("Type").append(',');
        sb.append("Algorithm").append(',');
        sb.append("Function").append(',');
        sb.append("Number of Dimensions").append(',');
        sb.append("Agents Count").append(',');
        sb.append("Steps Count").append(',');
        sb.append("Expected Best Value").append(',');
        sb.append("Actual Mean Best Value").append(',');
        sb.append("STD value").append(',');
        sb.append("Effective Step").append(',');
        sb.append("Execution time").append(',');
        sb.append('\n');

        Path path = Paths.get(l == null ? (RESULT_FILE + "result.csv") : (RESULT_FILE + "result_"+l+".csv"));
        try {
            if(Files.exists(path)){
                return path;
            }
            Files.write(path, sb.toString().getBytes(), StandardOpenOption.CREATE);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    private static Path createFile(String filename){
        Path path = Paths.get(filename);
        File f = new File(RESULT_FILE+"/logs");
        try {
            if(!f.exists()){
                f.mkdirs();
            }
            if(Files.exists(path)){
                return path;
            }
            Files.createFile(path);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
}
