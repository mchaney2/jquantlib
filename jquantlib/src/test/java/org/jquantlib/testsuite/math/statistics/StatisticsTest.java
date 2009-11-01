package org.jquantlib.testsuite.math.statistics;

import static org.junit.Assert.fail;

import org.jquantlib.QL;
import org.jquantlib.math.matrixutilities.Array;
import org.jquantlib.math.statistics.IStatistics;
import org.jquantlib.math.statistics.Statistics;
import org.junit.Ignore;
import org.junit.Test;

public class StatisticsTest {

    private static final Array data    = new Array(new double[] { 3.0, 4.0, 5.0, 2.0, 3.0, 4.0, 5.0, 6.0, 4.0, 7.0 });
    private static final Array weights = new Array(new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 });


    public StatisticsTest() {
        QL.info("Testing volatility model construction...");
    }

    private void check(final IStatistics s, final String name) {

        for (int i = 0; i<data.size(); i++){
            s.add(data.get(i), weights.get(i) );
        }

        double calculated, expected;
        double tolerance;

        if(s.samples()!=data.size()){
            fail("wrong number of samples \n" +
                    "calculated: " + s.samples() + "\n" +
                    "expected: " + data.size());
        }
        expected = weights.accumulate();
        calculated = s.weightSum();
        if (calculated != expected){
            fail(name  + ": wrong sum of weights\n"
            + "    calculated: " + calculated + "\n"
            + "    expected:   " + expected);
        }

        expected = data.min();
        calculated = s.min();
        if(calculated != expected){
            fail(name + ": wrong minimum value \n" +
                    "calculated: " + calculated + "\n" +
                    "expected: " + expected);
        }

        expected = data.max();
        calculated = s.max();
        if(calculated != expected){
            fail(name + ": wrong maxmimum value \n" +
                    "calculated: " + expected + "\n" +
                    "expected: " + expected);
        }

        expected = 4.3;
        tolerance = 1.0e-9;
        calculated = s.mean();
        if(Math.abs(calculated - expected)>tolerance){
            fail(name + "wrong mean value" + "\n" +
                    "calculated: " + calculated + "\n" +
                    "expected: " + expected);
        }

        expected = 2.3333333333;
        calculated = s.variance();
        if(Math.abs(calculated - expected) > tolerance){
            fail(name + "wrong variance" + "\n" +
                    "calculated: " + calculated + "\n" +
                    "expected: " + expected);
        }

        expected = 1.4944341181;
        calculated = s.standardDeviation();
        if (Math.abs(calculated-expected) > tolerance){
            fail(name + "wrong standard deviation" + "\n" +
                    "calculated: " + calculated + "\n" +
                    "expected: " + expected);
        }

        expected = 0.359543071407;
        calculated = s.skewness();
        if (Math.abs(calculated-expected) > tolerance){
            fail(name + "wrong skewness" + "\n" +
                    "calculated: " + calculated + "\n" +
                    "expected: " + expected);
        }

        expected = -0.151799637209;
        calculated = s.kurtosis();
        if (Math.abs(calculated-expected) > tolerance){
            fail(name + "wrong skewness" + "\n" +
                    "calculated: " + calculated + "\n" +
                    "expected: " + expected);
        }
    }

    @Ignore
    @Test
    public void testStatistics(){
        QL.info("Testing statistics ...");
        check(new Statistics(), "Statistics");
    }

    @Ignore
    @Test
    public void testIncrementalStatistics(){
        QL.info("Testing statistics ...");
        // check(new IncrementalStatistics(), "IncrementalStatistics");
    }



    // TODO: code review :: please verify against QL/C++ code
    public void checkSequence(final IStatistics statistics, final String name, final int dimension){
        /*
        GenericSequenceStatistics ss = new GenericSequenceStatistics(statistics, dimension);
        int i;
        for(i = 0; i<data.size(); i++){
            double [] temp  = new double [dimension];
            java.util.Arrays.fill(temp, 0, dimension, data[i]);
            ss.add(temp, weights[i]);
        }
        */


    }






}
