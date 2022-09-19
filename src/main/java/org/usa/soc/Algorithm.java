package org.usa.soc;

import org.usa.soc.core.Action;
import org.usa.soc.core.Vector;
import org.usa.soc.util.Randoms;
import org.usa.soc.util.StringFormatter;

import java.util.Arrays;

public abstract class Algorithm implements Cloneable {

    protected boolean isLocalMinima;

    protected double[] minBoundary;
    protected double[] maxBoundary;

    protected Action stepAction;

    protected ObjectiveFunction<Double> objectiveFunction;

    protected int stepsCount;

    private Double bestValue = Double.POSITIVE_INFINITY;

    private double convergenceValue = Double.MAX_VALUE;
    private double gradiantDecent = Double.MAX_VALUE;

    private double meanBestValue = Double.POSITIVE_INFINITY;

    public Algorithm(
            ObjectiveFunction<Double> objectiveFunction,
            int stepsCount,
            int numberOfDimensions,
            double[] minBoundary,
            double[] maxBoundary,
            long nanoDuration,
            boolean isLocalMinima
    ) {
        this.isLocalMinima = isLocalMinima;
        this.minBoundary = minBoundary;
        this.maxBoundary = maxBoundary;
        this.objectiveFunction = objectiveFunction;
        this.stepsCount = stepsCount;
        this.numberOfDimensions = numberOfDimensions;
        this.nanoDuration = nanoDuration;
    }

    protected Algorithm(){

    }

    protected int numberOfDimensions;

    protected Vector gBest;

    protected long nanoDuration;

    private boolean isInitialized = false;

    public void runOptimizer(){
        this.runOptimizer(0);
    };

    public abstract void runOptimizer(int time);

    public abstract void initialize();

    protected boolean isInitialized(){
        return this.isInitialized;
    }

    protected void setInitialized(boolean v){
        this.isInitialized = v;
    }

    public Vector getGBest() { return this.gBest; }

    public Double getBestDoubleValue() {
        return this.objectiveFunction.setParameters(this.getGBest().getPositionIndexes()).call();
    }
    public long getNanoDuration() {
        return nanoDuration;
    }

    public boolean isMinima() {
        return this.isLocalMinima;
    }

    public String getBestVariables() {
        return this.getGBest().toString();
    }

    public ObjectiveFunction getFunction() {
        return this.objectiveFunction;
    }

    @Override
    public Algorithm clone() throws CloneNotSupportedException{
        return null;
    }
    public void addStepAction(Action a){
        this.stepAction =a;
    }

    public void stepCompleted(int time, long step){

        double xValue = objectiveFunction.setParameters(this.gBest.getPositionIndexes()).call();
        // calculate convergence
        calculateMeanValue(step, xValue);
        calculateConvergenceValue(step, xValue);
        calculateGradiantDecent(step, xValue);

        this.bestValue = xValue;

        if(time == 0){
            return;
        }
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void calculateMeanValue(long step, double xValue) {

        if(getMeanBestValue() == Double.POSITIVE_INFINITY){
            meanBestValue = xValue / (step+1);
        }else{
            meanBestValue = ((getMeanBestValue() * (step)) + xValue) / (step + 1);
        }
    }

    private void calculateGradiantDecent(long step, double xValue) {
        double dg = (xValue) / (step+1);
        if(dg == Double.POSITIVE_INFINITY){
            this.gradiantDecent =0;
        }else{
            this.gradiantDecent = getMeanBestValue() - Randoms.rand(0,1)* dg;
        }
        System.out.println(gradiantDecent);

    }

    private void calculateConvergenceValue(long step, double xValue) {
        double dev = Math.pow(Math.abs(objectiveFunction.getExpectedBestValue() - getBestValue()), (1/(step+1)));
        this.convergenceValue = 1 - ((objectiveFunction.getExpectedBestValue() - xValue) / dev);
    }

    public abstract double[][] getDataPoints();

    public int getStepsCount() {
        return stepsCount;
    }

    public double getConvergenceValue() {
        return convergenceValue;
    }

    public double getBestValue() {
        return bestValue;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append("Name: " + this.getClass().getSimpleName());
        sb.append('\n');

        sb.append("Function: " + this.getFunction().getClass().getSimpleName());
        sb.append('\n');

        sb.append("Best Value: ");
        sb.append(this.getBestDoubleValue());
        sb.append('\n');

        sb.append("Expected Best Value: ");
        sb.append(this.getFunction().getExpectedBestValue());
        sb.append('\n');

        sb.append("Minimum Obtained Value: ");
        sb.append(Arrays.toString(this.getFunction().getMin()));
        sb.append('\n');

        sb.append("Maximum Obtained Value: ");
        sb.append(Arrays.toString(this.getFunction().getMax()));
        sb.append('\n');

        sb.append("Number of Dimensions: ");
        sb.append(this.getFunction().getNumberOfDimensions());
        sb.append('\n');

        sb.append("Execution Time: ");
        sb.append(this.getNanoDuration()/ 1000000);
        sb.append('\n');

        sb.append("Best Position: ");
        sb.append(this.getBestVariables());
        sb.append('\n');

        sb.append("Expected Best Position: ");
        sb.append(StringFormatter.toString(this.getFunction().getExpectedParameters()));
        sb.append('\n');

        sb.append("Convergence: ");
        sb.append(this.getConvergenceValue());
        sb.append('\n');

        sb.append("Gradiant Decent: ");
        sb.append(this.getGradiantDecent());
        sb.append('\n');

        sb.append("Mean Best Value: ");
        sb.append(this.getMeanBestValue());
        sb.append('\n');

        return sb.toString();
    }

    public double getGradiantDecent() {
        return gradiantDecent;
    }

    public double getMeanBestValue() {
        return meanBestValue;
    }
}
