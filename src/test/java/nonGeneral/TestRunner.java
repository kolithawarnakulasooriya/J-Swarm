package nonGeneral;

/*
Settings
 */

import examples.si.benchmarks.cec2018.ModifiedInvertedDTLZ1;
import examples.si.benchmarks.cec2018.ModifiedInvertedDTLZ7;
import examples.si.benchmarks.nonGeneral.classical.multimodal.nonseparable.*;
import examples.si.benchmarks.nonGeneral.classical.multimodal.separable.*;
import examples.si.benchmarks.nonGeneral.classical.unimodal.nonseparable.*;
import examples.si.benchmarks.nonGeneral.classical.unimodal.separable.*;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import org.usa.soc.si.SIAlgorithm;
import org.usa.soc.si.ObjectiveFunction;
import org.usa.soc.core.action.StepAction;
import org.usa.soc.core.ds.Vector;
import org.usa.soc.util.Mathamatics;
import examples.si.AlgorithmFactory;

import java.awt.*;
import java.nio.file.Files;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.generate;

public class TestRunner {

    private static final int REPEATER = 5;
    private static final int AGENT_COUNT = 1000;
    private static final int STEPS_COUNT = 1000;
    private static int ALGO_INDEX = 2;

    public SIAlgorithm getAlgorithm(ObjectiveFunction fn){
        return new AlgorithmFactory(ALGO_INDEX, fn).getAlgorithm(STEPS_COUNT, AGENT_COUNT);
    }

    private  static SIAlgorithm SIAlgorithm = null;

    private String RunTest(ObjectiveFunction fn, String extra){

        double[] meanBestValueTrial = new double[STEPS_COUNT];
        double[] meanMeanBestValueTrial = new double[STEPS_COUNT];
        double[] meanConvergence = new double[STEPS_COUNT];
        double meanBestValue =0;
        long meanExecutionTime =0;
        double std = 0;
        List<String[]> dataLines = new ArrayList<>();
        double fraction = STEPS_COUNT/100;
        double[] bestValuesArray = new double[REPEATER];
        String filename = "data/"+System.currentTimeMillis() + ".csv";
        Path p = createFile(filename);
        SIAlgorithm = getAlgorithm(fn);
        appendToFile(p, SIAlgorithm.getClass().getSimpleName() + ","+ SIAlgorithm.getFunction().getClass().getSimpleName());
        System.out.println(extra + "=>" + fn.getClass().getSimpleName() + ", " + SIAlgorithm.getName());
        for(int i=0; i<REPEATER; i++){
            SIAlgorithm = getAlgorithm(fn);
            SIAlgorithm.initialize();
            System.out.println();
            SIAlgorithm.addStepAction(new StepAction() {
                @Override
                public void performAction(Vector best, Double bestValue, int step) {
                    if((SIAlgorithm.getCurrentStep() % fraction) == 0){
                        System.out.print("\r ["+ Mathamatics.round(bestValue, 3) +"] ["+step/fraction+"%] "  + generate(() -> "#").limit((long)(step/fraction)).collect(joining()));
                    }
                    if(step >1) {
                        meanBestValueTrial[step-2] += bestValue;
                        meanConvergence[step - 2] += SIAlgorithm.getConvergenceValue();
                        meanMeanBestValueTrial[step - 2] += SIAlgorithm.getMeanBestValue();
                    }
                }
            });
            try {
                SIAlgorithm.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            meanBestValue += SIAlgorithm.getBestDoubleValue();
            bestValuesArray[i] = SIAlgorithm.getBestDoubleValue();
            meanExecutionTime += SIAlgorithm.getNanoDuration();
        }

        meanBestValue /= REPEATER;
        meanExecutionTime /= REPEATER;
        std = new StandardDeviation().evaluate(bestValuesArray, meanBestValue);
        appendToFile(p, "Mean Best Value: ," + meanBestValue);
        appendToFile(p, "Mean Execution Time: (ms): ,"+ TimeUnit.MILLISECONDS.convert(meanExecutionTime, TimeUnit.NANOSECONDS));
        appendToFile(p, "MBV, MMBV, MC");

        for(int j = 0; j< SIAlgorithm.getStepsCount(); j++){
            meanBestValueTrial[j] /= REPEATER;
            meanMeanBestValueTrial[j] /= REPEATER;
            meanConvergence[j] /= REPEATER;
            appendToFile(p, meanBestValueTrial[j] +","+meanMeanBestValueTrial[j] +","+meanConvergence[j]);
        }
        System.out.println();

        StringBuffer sb = new StringBuffer();
        sb.append(new Date().toString()).append(',');
        sb.append(extra).append(',');
        sb.append(SIAlgorithm.getClass().getSimpleName()).append(',');
        sb.append(SIAlgorithm.getFunction().getClass().getSimpleName()).append(',');
        sb.append(SIAlgorithm.getFunction().getNumberOfDimensions()).append(',');
        sb.append(AGENT_COUNT).append(',');
        sb.append(SIAlgorithm.getStepsCount()).append(',');
        sb.append(SIAlgorithm.getFunction().getExpectedBestValue()).append(',');
        sb.append(meanBestValue).append(',');
        sb.append(std).append(',');
        sb.append(TimeUnit.MILLISECONDS.convert(meanExecutionTime, TimeUnit.NANOSECONDS)).append(',');
        sb.append(filename).append(',');

        sb.append('\n');

        return sb.toString();

//        List<XYChart> charts = new ArrayList<XYChart>();
//
//        XYChart chart = new XYChartBuilder().xAxisTitle("step").yAxisTitle("best value").width(2000).height(400).build();
//        XYSeries series = chart.addSeries("Best Value", null, meanBestValueTrial);
//        series.setMarker(SeriesMarkers.NONE);
//        charts.add(chart);
//
//        XYChart chart1 = new XYChartBuilder().xAxisTitle("step").yAxisTitle("mean best value").width(600).height(400).build();
//        XYSeries series1 = chart1.addSeries("Mean Best Value", null, meanMeanBestValueTrial);
//        series1.setMarker(SeriesMarkers.NONE);
//        charts.add(chart1);
//
//        XYChart chart2 = new XYChartBuilder().xAxisTitle("step").yAxisTitle("convergence value").width(600).height(400).build();
//        XYSeries series2 = chart2.addSeries("Convergence Value", null, meanConvergence);
//        series2.setMarker(SeriesMarkers.NONE);
//        charts.add(chart2);
//
//        new SwingWrapper<XYChart>(charts).displayChartMatrix();
    }

    public static void main(String[] args) {

        // list of Objective Functions
        List<ObjectiveFunction> multimodalNonSeparableFunctionList = Arrays.asList(
                // new AckleysFunction(),
                // new ColvilleFunction(),
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

        List<ObjectiveFunction> cec2018FunctionList = Arrays.asList(
                new ModifiedInvertedDTLZ1(30, 10),
                new ModifiedInvertedDTLZ7(30, 20)
        );

        Toolkit.getDefaultToolkit().beep();

        // start log file writer
        Path p = createResultFile();

//        for (ObjectiveFunction fn: multimodalNonSeparableFunctionList) {
//            String s = new TestRunner().RunTest(fn,"Multi Modal - Non Separable");
//            appendToFile(p, s);
//        }
//
//        for (ObjectiveFunction fn: multimodalSeparableFunctionList) {
//            String s = new TestRunner().RunTest(fn,"Multi Modal - Separable");
//            appendToFile(p, s);
//        }
//
//        for (ObjectiveFunction fn: unimodalNonSeparableFunctionList) {
//            String s = new TestRunner().RunTest(fn,"Uni Modal - Non Separable");
//            appendToFile(p, s);
//        }
//
//        for (ObjectiveFunction fn: unimodalSeparableFunctionList) {
//            String s = new TestRunner().RunTest(fn,"Uni Modal - Separable");
//            appendToFile(p, s);
//        }

        int[] x = new int[]{9,14,15,17,10,22,23,24,25,26,27,7,2,8,16,28,3,12,21,13,0,19,11,18,6,20};

        for(int y: x){
            ALGO_INDEX = y;
            for (ObjectiveFunction fn : cec2018FunctionList) {
                String s = new TestRunner().RunTest(fn, "CEC-2018");
                System.out.println(s);
                appendToFile(p, s);
            }
        }

    }

    private static void appendToFile(Path path, String data){
        try {
            //System.out.println(data);
            Files.write(path, data.getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Path createResultFile(){
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
        sb.append("Execution time").append(',');
        sb.append('\n');

        Path path = Paths.get("data/result.csv");
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
        try {
            if(Files.exists(path)){
                return path;
            }
            Files.write(path, (new Date().toString()+"\n").getBytes(), StandardOpenOption.CREATE);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
}
