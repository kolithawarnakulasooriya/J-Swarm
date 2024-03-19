package ui;

import org.usa.soc.Algorithm;
import org.usa.soc.ObjectiveFunction;
import org.usa.soc.abc.ABC;
import org.usa.soc.aco.ACO;
import org.usa.soc.ba.BA;
import org.usa.soc.choa.Chaotics;
import org.usa.soc.cs.CS;
import org.usa.soc.fa.FA;
import org.usa.soc.mbo.MBO;
import org.usa.soc.ms.MS;
import org.usa.soc.pso.PSO;
import org.usa.soc.tco.TCO;
import org.usa.soc.tsoa.TSOA;
import org.usa.soc.util.Mathamatics;
import org.usa.soc.util.Randoms;
import org.usa.soc.wso.WSO;

import java.util.ArrayList;
import java.util.List;

public class AlgoStore {

    private int a;
    private ObjectiveFunction fn;

    public AlgoStore(int a, ObjectiveFunction fn) {
        this.a = a;
        this.fn = fn;
    }


    public Algorithm getAlgorithm(int sc, int ac) {
        switch (a){
            case 0 : return new PSO(
                    fn,
                    ac,
                    fn.getNumberOfDimensions(),
                    sc,
                    1.496180,
                    1.496180,
                    0.729844,
                    fn.getMin(),
                    fn.getMax(),
                    true);
            case 1: return new ACO(
                    fn,
                    ac,
                    sc,
                    5,
                    fn.getNumberOfDimensions(),
                    fn.getMin(),
                    fn.getMax(),
                    true
            );
            case 2: return new org.usa.soc.cso.CSO(
                    fn,
                    fn.getNumberOfDimensions(),
                    sc,
                    ac,
                    0.2,
                    fn.getMin(),
                    fn.getMax(),
                    10,
                    0.2,
                    0.2,
                    true,
                    0.5,
                    0.2,
                    true
            );
            case 3: {
                return new org.usa.soc.gso.GSO(
                        fn,
                        fn.getNumberOfDimensions(),
                        sc,
                        ac,
                        fn.getMin(),
                        fn.getMax(),
                        5,
                        1,
                        0.4,
                        0.6,
                        2,
                        1,
                        0.08,
                        0.03,
                        true
                );
            }
            case 4: return new MBO(
                    fn,
                    ac,
                    100,
                    30,
                    sc,
                    fn.getNumberOfDimensions(),
                    fn.getMin(),
                    fn.getMax(),
                    true,
                    0.9,
                    0,
                    10,
                    0.5,
                    0.5,
                    10
            );
            case 5: return new MS(
                    fn,
                    sc,
                    ac,
                    fn.getNumberOfDimensions(),
                    50,
                    fn.getMin(),
                    fn.getMax(),
                    0.5,
                    true
            );
            case 6: return new WSO(
                    fn,
                    sc,
                    ac,
                    fn.getNumberOfDimensions(),
                    fn.getMin(),
                    fn.getMax(),
                    0.4,
                    0.4,
                    true
            );
            case 7: return new CS(
                    fn,
                    sc,
                    fn.getNumberOfDimensions(),
                    ac,
                    fn.getMin(),
                    fn.getMax(),
                    1,
                    0.75,
                    true
            );
            case 8: return new FA(
                    fn,
                    sc,
                    fn.getNumberOfDimensions(),
                    ac,
                    fn.getMin(),
                    fn.getMax(),
                    1,
                    0.2,
                    1,
                    true
            );
            case 9: return new ABC(
                    fn,
                    sc,
                    fn.getNumberOfDimensions(),
                    ac,
                    fn.getMin(),
                    fn.getMax(),
                    100,
                    true
            );
            case 10:  return new BA(
                    fn,
                    sc,
                    fn.getNumberOfDimensions(),
                    fn.getMin(),
                    fn.getMax(),
                    ac,
                    0,
                    100,
                    0.9,
                    0.9,
                    100,
                    Randoms.rand(0,1),
                    true
            );
            case 11: return new TCO(
                    fn,
                    sc,
                    fn.getNumberOfDimensions(),
                    ac,
                    fn.getMin(),
                    fn.getMax(),
                    1,
                    0.8,
                    0.72,
                    1.49,
                    true
            );
            case 12: return new org.usa.soc.gwo.GWO(
                    fn,
                    sc,
                    fn.getNumberOfDimensions(),
                    ac,
                    fn.getMin(),
                    fn.getMax(),
                    true
            );
            case 13: return new org.usa.soc.mfa.MFA(
                    fn,
                    sc,
                    fn.getNumberOfDimensions(),
                    ac,
                    fn.getMin(),
                    fn.getMax(),
                    1.0
            );
            case 14: return new org.usa.soc.alo.ALO(
                    fn,
                    ac,
                    sc,
                    fn.getNumberOfDimensions(),
                    fn.getMin(),
                    fn.getMax(),
                    true
            );
            case 15: return new org.usa.soc.also.ALSO(
                    fn,
                    ac,
                    sc,
                    fn.getNumberOfDimensions(),
                    fn.getMin(),
                    fn.getMax(),
                    true,
                    10,
                    10,
                    2.5,
                    1.0,
                    10,
                    10
            );
            case 16: return new org.usa.soc.geo.GEO(
                    fn,
                    ac,
                    sc,
                    fn.getNumberOfDimensions(),
                    fn.getMin(),
                    fn.getMax(),
                    true,
                    2,
                    0.5,
                    0.5,
                    1
            );
            case 17: return new org.usa.soc.avoa.AVOA(
                    fn,
                    ac,
                    sc,
                    fn.getNumberOfDimensions(),
                    fn.getMin(),
                    fn.getMax(),
                    true,
                    2.5,
                    0.8,
                    0.2,
                    0.6,
                    0.4,
                    0.6
            );
            case 18: return new org.usa.soc.tsa.TSA(
                    fn,
                    ac,
                    sc,
                    fn.getNumberOfDimensions(),
                    fn.getMin(),
                    fn.getMax(),
                    true
            );
            case 19: return new org.usa.soc.ssa.SSA(
                    fn,
                    ac,
                    sc,
                    fn.getNumberOfDimensions(),
                    fn.getMin(),
                    fn.getMax(),
                    true,
                    0.1,
                    1.9,
                    1.204,
                    5.25,
                    0.0154,
                    8.0
            );
            case 20: return new org.usa.soc.zoa.ZOA(
                    fn,
                    ac,
                    sc,
                    fn.getNumberOfDimensions(),
                    fn.getMin(),
                    fn.getMax(),
                    true
            );
            case 21:  return new org.usa.soc.jso.JSO(
                    fn,
                    ac,
                    sc,
                    fn.getNumberOfDimensions(),
                    fn.getMin(),
                    fn.getMax(),
                    true,
                    3,
                    0.1
            );
            case 22: return new org.usa.soc.choa.CHOA(
                    fn,
                    ac,
                    sc,
                    fn.getNumberOfDimensions(),
                    fn.getMin(),
                    fn.getMax(),
                    true,
                    2.5,
                    Chaotics.type.QUADRATIC
            );
            case 23: return new org.usa.soc.choa.CHOA(
                    fn,
                    ac,
                    sc,
                    fn.getNumberOfDimensions(),
                    fn.getMin(),
                    fn.getMax(),
                    true,
                    2.5,
                    Chaotics.type.BERNOULLI
            );
            case 24: return new org.usa.soc.choa.CHOA(
                    fn,
                    ac,
                    sc,
                    fn.getNumberOfDimensions(),
                    fn.getMin(),
                    fn.getMax(),
                    true,
                    2.5,
                    Chaotics.type.GAUSS_MOUSE
            );
            case 25: return new org.usa.soc.choa.CHOA(
                    fn,
                    ac,
                    sc,
                    fn.getNumberOfDimensions(),
                    fn.getMin(),
                    fn.getMax(),
                    true,
                    2.5,
                    Chaotics.type.LOGISTIC
            );
            case 26: return new org.usa.soc.choa.CHOA(
                    fn,
                    ac,
                    sc,
                    fn.getNumberOfDimensions(),
                    fn.getMin(),
                    fn.getMax(),
                    true,
                    2.5,
                    Chaotics.type.SINGER
            );
            case 27: return new org.usa.soc.choa.CHOA(
                    fn,
                    ac,
                    sc,
                    fn.getNumberOfDimensions(),
                    fn.getMin(),
                    fn.getMax(),
                    true,
                    2.5,
                    Chaotics.type.TENT
            );
            case 28: return new org.usa.soc.goa.GOA(
                    fn,
                    ac,
                    sc,
                    fn.getNumberOfDimensions(),
                    fn.getMin(),
                    fn.getMax(),
                    true,
                    1,
                    0.00004,
                    0.5,
                    1.5
            );
            case 29: return new org.usa.soc.tsoa.TSOA(
                    fn,
                    ac,
                    sc,
                    fn.getNumberOfDimensions(),
                    fn.getMin(),
                    fn.getMax(),
                    true,
                    0.7,
                    2
            );
        }
        return null;
    }

    public static List<String> generateAlgo() {
        List<String> algo = new ArrayList<>();

        algo.add("PSO - Birds & Fishes");
        algo.add("ACO - Ants as a Colony");
        algo.add("CSO - Cats & Rats");
        algo.add("GSO - Glow Worm Collaboration");
        algo.add("MBO - Bees Marriage Process");
        algo.add("MS - Monkey Search Tree Branches");
        algo.add("WSO - Wasps as a Swarm");
        algo.add("CS - Cuckoo Bird Search for a Nest");
        algo.add("FA - Fireflies Swarm");
        algo.add("ABC - Bees as a Colony");
        algo.add("BA - Bats Navigation");
        algo.add("TCO - Termites Hunting");
        algo.add("GWO Gray Wolf Hunting");
        algo.add("MFA - Moths Find Flames");
        algo.add("ALO - Ant Lion Hunting");
        algo.add("ALSO - Lizards");
        algo.add("GEO - Golden Eagle");
        algo.add("AVOA - African Vulture");
        algo.add("TSA - Tunicate Swarm ");
        algo.add("SSA - Squirrels");
        algo.add("ZOA - Zebras");
        algo.add("JSO - Jellifish");
        algo.add("CHOA_QUADRATIC - Chimps");
        algo.add("CHOA_BERNOULLI - Chimps");
        algo.add("CHOA_GAUSS_MOUSE - Chimps");
        algo.add("CHOA_LOGISTIC - Chimps");
        algo.add("CHOA_SINGER - Chimps");
        algo.add("CHOA_TENT - Chimps");
        algo.add("GOA - Grass Hopper");
        algo.add("TSOA - Tree Search Optimization Algorithm");
        
        return algo;
    }
}
