/*
 Copyright (C) 2007 Dominik Holenstein

 This source code is release under the BSD License.
 
 This file is part of JQuantLib, a free-software/open-source library
 for financial quantitative analysts and developers - http://jquantlib.org/

 JQuantLib is free software: you can redistribute it and/or modify it
 under the terms of the JQuantLib license.  You should have received a
 copy of the license along with this program; if not, please email
 <jquant-devel@lists.sourceforge.net>. The license is also available online at
 <http://www.jquantlib.org/index.php/LICENSE.TXT>.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the license for more details.
 
 JQuantLib is based on QuantLib. http://quantlib.org/
 When applicable, the original copyright notice follows this notice.
 */

package org.jquantlib.testsuite.math.distributions;

// import static org.junit.Assert.assertEquals; --> not JUnit 4.4 conform

import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.jquantlib.math.distributions.InverseCumulativeNormal;
import org.jquantlib.math.distributions.alternativeimpls.TESTICN;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dominik Holenstein
 */

public class InverseCumulativeNormalTest {
	
    private final static Logger logger = LoggerFactory.getLogger(InverseCumulativeNormalTest.class);

	public InverseCumulativeNormalTest() {
		logger.info("\n\n::::: "+this.getClass().getSimpleName()+" :::::");
	}
	
	//new testcase for the inversecumulativenormal distribution, the old testcase can be found in revision 719
	@Test 
	public void testInverseCumulativNormal(){
		logger.info("Running new test");
		
		//normal values generated by quantlib - digits down to 1e-16
		double[][] normal_testvalues = {   {0.01,-2.3263478743880279},
				{0.1, -1.2815515641401563},
				{0.2, -0.84162123272661848},
				{0.3, -0.52440051327929527},
				{0.4, -0.25334710285999862},
				{0.5, 0.00000000000000000},
				{0.6, 0.25334710285999862},
				{0.7, 0.52440051327929516},
				{0.8, 0.84162123272661860},
				{0.9, 1.2815515641401563},
				{0.99, 2.3263478743880279}};
		
		//high precision values generated by quantlib using halleys method - digits down to 1e-16
		double[][] precision_testvalues = {   {0.01,-2.3263478740408416},
				{0.1, -1.2815515655446004},
				{0.2, -0.84162123357291430},
				{0.3, -0.52440051270804078},
				{0.4, -0.25334710313579978},
				{0.5, 0.00000000000000000},
				{0.6, 0.25334710313579961},
				{0.7, 0.52440051270804056},
				{0.8, 0.84162123357291441},
				{0.9, 1.2815515655446004},
				{0.99, 2.3263478740408412}};
		
		InverseCumulativeNormal icn = new InverseCumulativeNormal();
		
		
		//test the normal values
		for (int i = 0; i<normal_testvalues.length; i++){		
			double x_position = normal_testvalues[i][0];
			double tolerance = 1.0e-15;//(Math.abs(x_position)<3.01) ? 1.0e-15: 1.0e-10; 
			
			double normal_expected = normal_testvalues[i][1];
			double computed_normal = icn.op(x_position);
			if (Math.abs(normal_expected-computed_normal)>tolerance) {
				fail("x_position " + x_position + " normal_expected: " + normal_expected + " normal_computed: " + normal_expected);
			}
		}
			
		//turn precision switch on and run again, use reflection 
		final Field fields[] = icn.getClass().getDeclaredFields();
	    for (int i = 0; i < fields.length; ++i) {
	      if ("highPrecision".equals(fields[i].getName())) {
	        try {
	          fields[i].setAccessible(true);
	          fields[i].setBoolean(icn, true);
	        } 
	        catch (IllegalAccessException ex) {}
	      }
	    }

		for (int i = 0; i<precision_testvalues.length; i++){		
			double x_position = precision_testvalues[i][0];
			double tolerance = 1.0e-15;//(Math.abs(x_position)<3.01) ? 1.0e-15: 1.0e-10; 
			
			double precision_expected = precision_testvalues[i][1];
			double computed_precision = icn.op(x_position);
			
			if (Math.abs(precision_expected-computed_precision)>tolerance) {
				fail("x_position " + x_position + " precision_expected: " + precision_expected + " precision_computed: " + computed_precision);
			}
		}
	}
	
	@Test 
	public void testExtremes(){
		double z = -40;
		double tolerance = 1.0e-15;
		
		InverseCumulativeNormal icn = new InverseCumulativeNormal();
		
		// assertEquals(0, icn.evaluate(z),tolerance); --> not JUnit 4.4 conform
		if (Math.abs(icn.op(z)) > tolerance) {
			fail("z: " + z + " expected: " + 0.0 + " realized: " + icn.op(z));
		}
		
		z = -10;
		// assertEquals(0, icn.evaluate(z),tolerance); --> not JUnit 4.4 conform
		if (Math.abs(icn.op(z)) > tolerance) {
			fail("z: " + z + " expected: " + 0.0 + " realized: " + icn.op(z));
		}
		
		z = 10;
		//assertEquals(1.0, icn.evaluate(z),tolerance); --> not JUnit 4.4 conform
		if (Math.abs(icn.op(z)) > (tolerance + 1.0)) {
			fail("z: " + z + " expected: " + 1.0 + " realized: " + icn.op(z));
		}
		
		z = 40;
		// assertEquals(1.0, icn.evaluate(z),tolerance); --> not JUnit 4.4 conform
		if (Math.abs(icn.op(z)) > (tolerance + 1.0)) {
			fail("z: " + z + " expected: " + 1.0 + " realized: " + icn.op(z));
		}		
	}
	
}
