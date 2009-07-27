/*
 Copyright (C) 2008 Richard Gomes

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

/*
 Copyright (C) 2007 Cristina Duminuco
 Copyright (C) 2007 Chiara Fornarola
 Copyright (C) 2003, 2004, 2005, 2006 Ferdinando Ametrano
 Copyright (C) 2006 Mark Joshi
 Copyright (C) 2001, 2002, 2003 Sadruddin Rejeb
 Copyright (C) 2006 StatPro Italia srl

 This file is part of QuantLib, a free-software/open-source library
 for financial quantitative analysts and developers - http://quantlib.org/

 QuantLib is free software: you can redistribute it and/or modify it
 under the terms of the QuantLib license.  You should have received a
 copy of the license along with this program; if not, please email
 <quantlib-dev@lists.sf.net>. The license is also available online at
 <http://quantlib.org/license.shtml>.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the license for more details.
 */

package org.jquantlib.pricingengines;

import org.jquantlib.instruments.Option;
import org.jquantlib.instruments.PlainVanillaPayoff;
import org.jquantlib.lang.annotation.DiscountFactor;
import org.jquantlib.lang.annotation.NonNegative;
import org.jquantlib.lang.annotation.Price;
import org.jquantlib.lang.annotation.StdDev;
import org.jquantlib.math.Closeness;
import org.jquantlib.math.distributions.CumulativeNormalDistribution;
import org.jquantlib.math.distributions.Derivative;
import org.jquantlib.math.solvers1D.NewtonSafe;

/**
 * 
 * Black 1976 formula
 * 
 * @author Richard Gomes
 * @author Srinivas Hasti
 */
// TODO: adjust formulas (LaTeX)
public class BlackFormula {

	/**
	 * Black 1976 formula
	 * 
	 * @Note instead of volatility it uses standard deviation, i.e.
	 *       volatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double blackFormula(
			final Option.Type optionType,
			@Price final double strike, 
			@Price final double forward,
			@StdDev final double stddev) {

		return blackFormula(optionType, strike, forward, stddev, 1.0, 0.0);
	}

	/**
	 * Black 1976 formula
	 * 
	 * @Note: Instead of volatility it uses standard deviation, i.e.
	 *        volatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double blackFormula(
			final Option.Type optionType,
			@Price final double strike, 
			@Price final double forward,
			@StdDev final double stddev,
			@DiscountFactor final double discount) {

		return blackFormula(optionType, strike, forward, stddev, discount, 0.0);
	}

	/**
	 * 
	 * Black 1976 formula
	 * 
	 * @Note: Instead of volatility it uses standard deviation, i.e.
	 *        volatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double blackFormula(
			final Option.Type optionType,
			@Price double strike, 
			@Price double forward,
			@StdDev final double stddev,
			@DiscountFactor final double discount,
			@Price final double displacement) {

		if (!(strike >= 0.0))       throw new ArithmeticException("strike must be non-negative"); // TODO: message
		if (!(forward > 0.0))       throw new ArithmeticException("forward must be positive"); // TODO: message
		if (!(stddev >= 0.0))       throw new ArithmeticException("stddev must be non-negative"); // TODO: message
		if (!(discount > 0.0))      throw new ArithmeticException("discount must be positive"); // TODO: message
		if (!(displacement >= 0.0)) throw new ArithmeticException("displacement must be non-negative"); // TODO: message

		forward = forward + displacement;
		strike = strike + displacement;
		if (stddev == 0.0)
			return Math.max((forward - strike) * optionType.toInteger(), (0.0d)) * discount;

		if (strike == 0.0) // strike=0 iff displacement=0
			return (optionType == Option.Type.CALL ? forward * discount : 0.0);

		@Price final double d1 = Math.log(forward / strike) / stddev + 0.5 * stddev;
		@Price final double d2 = d1 - stddev;

		// TODO: code review
		final CumulativeNormalDistribution phi = new CumulativeNormalDistribution();
		@Price final double result = discount * optionType.toInteger()
				* (forward * phi.op(optionType.toInteger() * d1) - strike * phi.op(optionType.toInteger() * d2));

		if (result >= 0.0) return result;
		
		throw new ArithmeticException("a negative value was calculated"); // TODO: add more logging
	}

	// ---
	// ---
	// ---

	/**
	 * Black 1976 formula
	 * 
	 * @Note instead of volatility it uses standard deviation, i.e.
	 *       volatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double blackFormula(
			final PlainVanillaPayoff payoff, 
			@Price final double strike,
			@Price final double forward, 
			@StdDev final double stddev) {

		return blackFormula(payoff, strike, forward, stddev, 1.0, 0.0);
	}

	/**
	 * Black 1976 formula
	 * 
	 * @Note: Instead of volatility it uses standard deviation, i.e.
	 *        volatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double blackFormula(
			final PlainVanillaPayoff payoff, 
			@Price final double strike,
			@Price final double forward, 
			@StdDev final double stddev,
			@DiscountFactor final double discount) {

		return blackFormula(payoff, strike, forward, stddev, discount, 0.0);
	}

	/**
	 * 
	 * Black 1976 formula
	 * 
	 * @Note: Instead of volatility it uses standard deviation, i.e.
	 *        volatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double blackFormula(
			final PlainVanillaPayoff payoff, 
			@Price double strike,
			@Price double forward, 
			@StdDev final double stddev,
			@DiscountFactor final double discount,
			@Price final double displacement) {

		return blackFormula(payoff.optionType(), payoff.strike(), forward, stddev, discount, displacement);
	}

	// ---
	// ---
	// ---

	/**
	 * Approximated Black 1976 implied standard deviation, i.e.
	 * volatility*sqrt(timeToMaturity).
	 * <p>
	 * It is calculated using Brenner and Subrahmanyan (1988) and Feinstein
	 * (1988) approximation for at-the-money forward option, with the extended
	 * moneyness approximation by Corrado and Miller (1996)
	 */

	public static /*@Price*/ double blackFormulaImpliedStdDevApproximation(
			final Option.Type optionType, 
			@Price final double strike,
			@Price final double forward, 
			@Price final double blackPrice) {

		return blackFormulaImpliedStdDevApproximation(optionType, strike, forward, blackPrice, 1.0, 0.0);
	}

	/**
	 * Approximated Black 1976 implied standard deviation, i.e.
	 * volatility*sqrt(timeToMaturity).
	 * <p>
	 * It is calculated using Brenner and Subrahmanyan (1988) and Feinstein
	 * (1988) approximation for at-the-money forward option, with the extended
	 * moneyness approximation by Corrado and Miller (1996)
	 */

	public static /*@Price*/ double blackFormulaImpliedStdDevApproximation(
			final Option.Type optionType, 
			@Price final double strike,
			@Price final double forward, 
			@Price final double blackPrice,
			@DiscountFactor final double discount) {

		return blackFormulaImpliedStdDevApproximation(optionType, strike, forward, blackPrice, discount, 0.0);
	}

	/**
	 * Approximated Black 1976 implied standard deviation, i.e.
	 * volatility*sqrt(timeToMaturity).
	 * <p>
	 * It is calculated using Brenner and Subrahmanyan (1988) and Feinstein
	 * (1988) approximation for at-the-money forward option, with the extended
	 * moneyness approximation by Corrado and Miller (1996)
	 */
	public static /*@Price*/ double blackFormulaImpliedStdDevApproximation(
			final Option.Type optionType, 
			@Price double strike,
			@Price double forward, 
			@Price final double blackPrice,
			@DiscountFactor final double discount,
			@Price final double displacement) {

		if (strike < 0.0)       throw new IllegalArgumentException("strike must be non-negative"); // TODO: message
		if (forward <= 0.0)     throw new IllegalArgumentException("forward must be positive"); // TODO: message
		if (displacement < 0.0) throw new IllegalArgumentException("displacement must be non-negative"); // TODO: message
		if (blackPrice < 0.0)   throw new IllegalArgumentException("blackPrice must be non-negative"); // TODO: message
		if (discount <= 0.0)    throw new IllegalArgumentException("discount must be positive"); // TODO: message

		double stddev;
		forward = forward + displacement;
		strike = strike + displacement;
		if (Closeness.isClose(strike, forward)) {
			// Brenner-Subrahmanyan (1988) and Feinstein (1988) ATM approx.
			stddev = blackPrice / discount * Math.sqrt(2.0 * Math.PI) / forward;
		} else {
			// Corrado and Miller extended moneyness approximation
			double moneynessDelta = optionType.toInteger() * (forward - strike);
			double moneynessDelta_2 = moneynessDelta / 2.0;
			double temp = blackPrice / discount - moneynessDelta_2;
			double moneynessDelta_PI = moneynessDelta * moneynessDelta / Math.PI;
			double temp2 = temp * temp - moneynessDelta_PI;
			if (temp2 < 0.0) {
				// approximation breaks down, 2 alternatives:
				// 1. zero it
				temp2 = 0.0;
				// 2. Manaster-Koehler (1982) efficient Newton-Raphson seed
				// return std::fabs(std::log(forward/strike))*std::sqrt(2.0); -- commented out in original C++
			}
			temp2 = Math.sqrt(temp2);
			temp += temp2;
			temp *= Math.sqrt(2.0 * Math.PI);
			stddev = temp / (forward + strike);
		}
		
		if (stddev >= 0.0) return stddev;
		throw new ArithmeticException("a negative value was calculated"); // TODO: add more logging
	}

	// ---
	// ---
	// ---

	/**
	 * Approximated Black 1976 implied standard deviation, i.e.
	 * volatility*sqrt(timeToMaturity).
	 * <p>
	 * It is calculated using Brenner and Subrahmanyan (1988) and Feinstein
	 * (1988) approximation for at-the-money forward option, with the extended
	 * moneyness approximation by Corrado and Miller (1996)
	 */

	public static /*@Price*/ double blackFormulaImpliedStdDevApproximation(
			final PlainVanillaPayoff payoff, 
			@Price final double strike,
			@Price final double forward, 
			@Price final double blackPrice) {

		// TODO : complete
		return blackFormulaImpliedStdDevApproximation(payoff, strike, forward, blackPrice, 1.0, 0.0);
	}

	/**
	 * Approximated Black 1976 implied standard deviation, i.e.
	 * volatility*sqrt(timeToMaturity).
	 * <p>
	 * It is calculated using Brenner and Subrahmanyan (1988) and Feinstein
	 * (1988) approximation for at-the-money forward option, with the extended
	 * moneyness approximation by Corrado and Miller (1996)
	 */

	public static /*@Price*/ double blackFormulaImpliedStdDevApproximation(
			final PlainVanillaPayoff payoff, 
			@Price final double strike,
			@Price final double forward, 
			@Price final double blackPrice,
			@DiscountFactor final double discount) {

		// TODO : complete
		return blackFormulaImpliedStdDevApproximation(payoff, strike, forward, blackPrice, discount, 0.0);
	}

	/**
	 * Approximated Black 1976 implied standard deviation, i.e.
	 * volatility*sqrt(timeToMaturity).
	 * <p>
	 * It is calculated using Brenner and Subrahmanyan (1988) and Feinstein
	 * (1988) approximation for at-the-money forward option, with the extended
	 * moneyness approximation by Corrado and Miller (1996)
	 */

	public static /*@Price*/ double blackFormulaImpliedStdDevApproximation(
			final PlainVanillaPayoff payoff, 
			@Price final double strike,
			@Price final double forward, 
			@Price final double blackPrice,
			@DiscountFactor final double discount,
			@Price final double displacement) {

		return blackFormulaImpliedStdDevApproximation(payoff.optionType(), payoff.strike(), forward, blackPrice, discount, displacement);
	}

	// ---
	// ---
	// ---

	/**
	 * Black 1976 implied standard deviation, i.e.
	 * volatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double blackFormulaImpliedStdDev(
			final Option.Type optionType, 
			@Price final double strike,
			@Price final double forward, 
			@Price final double blackPrice) {

		return blackFormulaImpliedStdDev(optionType, strike, forward, blackPrice, 1.0, Double.NaN, 1.0e-6, 0.0);

	}

	/**
	 * Black 1976 implied standard deviation, i.e.
	 * volatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double blackFormulaImpliedStdDev(
			final Option.Type optionType, 
			@Price final double strike,
			@Price final double forward, 
			@Price final double blackPrice,
			@DiscountFactor final double discount) {

		return blackFormulaImpliedStdDev(optionType, strike, forward, blackPrice, discount, Double.NaN, 1.0e-6, 0.0);

	}

	/**
	 * Black 1976 implied standard deviation, i.e.
	 * volatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double blackFormulaImpliedStdDev(
			final Option.Type optionType, @Price final double strike,
			@Price final double forward, @Price final double blackPrice,
			@DiscountFactor final double discount,
			@Price final double guess) {

		return blackFormulaImpliedStdDev(optionType, strike, forward, blackPrice, discount, guess, 1.0e-6, 0.0);

	}

	/**
	 * Black 1976 implied standard deviation, i.e.
	 * volatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double blackFormulaImpliedStdDev(
			final Option.Type optionType, 
			@Price final double strike,
			@Price final double forward, 
			@Price final double blackPrice,
			@DiscountFactor final double discount,
			@Price final double guess, 
			@Price final double accuracy) {

		return blackFormulaImpliedStdDev(optionType, strike, forward, blackPrice, discount, guess, accuracy, 0.0);

	}

	/**
	 * Black 1976 implied standard deviation, i.e.
	 * volatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double blackFormulaImpliedStdDev(
			final Option.Type optionType, 
			@Price double strike,
			@Price double forward, 
			@Price final double blackPrice,
			@DiscountFactor final double discount,
			@Price double guess, 
			@Price final double accuracy,
			@Price final double displacement) {
		
//---
// TODO: This block of code was removed because there's no option to pass maxIterations in the original C++ code
//---
//		return blackFormulaImpliedStdDev(optionType, strike, forward, blackPrice, discount, guess, accuracy, displacement, 1);
//	}
//
//	/**
//	 * Black 1976 implied standard deviation, i.e.
//	 * volatility*sqrt(timeToMaturity)
//	 */
//	// TODO: Move the code 
//	public static /*@Price*/ double blackFormulaImpliedStdDev(
//			final Option.Type optionType, 
//			@Price double strike,
//			@Price double forward, 
//			@Price final double blackPrice,
//			@DiscountFactor final doublediscount, 
//			@Price double guess,
//			@Price final double accuracy,
//			@Price final double displacement,
//			final int maxIterations) {
//---
		//TODO: The original C++ code does not have this line and calls to solver.setMaxIterations(100)
		final int maxIterations=100; 		
//---

		if (strike < 0.0)       throw new IllegalArgumentException("strike must be non-negative"); // TODO: message
		if (forward <= 0.0)     throw new IllegalArgumentException("forward must be positive"); // TODO: message
		if (displacement < 0.0) throw new IllegalArgumentException("displacement must be non-negative"); // TODO: message
		if (blackPrice < 0.0)   throw new IllegalArgumentException("blackPrice must be non-negative"); // TODO: message
		if (discount <= 0.0)    throw new IllegalArgumentException("discount must be positive"); // TODO: message

		strike = strike + displacement;
		forward = forward + displacement;
		if (Double.isNaN(guess)) {
			guess = blackFormulaImpliedStdDevApproximation(optionType, strike, forward, blackPrice, discount, displacement);
		} else if (guess < 0.0) {
			throw new IllegalArgumentException("stddev guess (" + guess + ") must be non-negative");
		}
		BlackImpliedStdDevHelper f = new BlackImpliedStdDevHelper(optionType, strike, forward, blackPrice / discount);
		NewtonSafe solver = new NewtonSafe();
		solver.setMaxEvaluations(maxIterations);
		double minSdtDev = 0.0, maxstddev = 3.0;
		double stddev = solver.solve(f, accuracy, guess, minSdtDev, maxstddev);
		
		if (stddev >= 0.0) return stddev;
		throw new ArithmeticException("a negative value was calculated"); // TODO: add more logging
	}

	// ---
	// ---
	// ---

	/**
	 * Black 1976 implied standard deviation, i.e.
	 * volatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double blackFormulaImpliedStdDev(
			final PlainVanillaPayoff payoff, 
			@Price final double strike,
			@Price final double forward, 
			@Price final double blackPrice) {

		return blackFormulaImpliedStdDev(payoff, strike, forward, blackPrice, 1.0, Double.NaN, 1.0e-6, 0.0);
	}

	/**
	 * Black 1976 implied standard deviation, i.e.
	 * volatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double blackFormulaImpliedStdDev(
			final PlainVanillaPayoff payoff, 
			@Price final double strike,
			@Price final double forward, 
			@Price final double blackPrice,
			@DiscountFactor final double discount) {

		return blackFormulaImpliedStdDev(payoff, strike, forward, blackPrice, discount, Double.NaN, 1.0e-6, 0.0);
	}

	/**
	 * Black 1976 implied standard deviation, i.e.
	 * volatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double blackFormulaImpliedStdDev(
			final PlainVanillaPayoff payoff, 
			@Price final double strike,
			@Price final double forward, 
			@Price final double blackPrice,
			@DiscountFactor final double discount,
			@Price final double guess) {

		return blackFormulaImpliedStdDev(payoff, strike, forward, blackPrice, discount, guess, 1.0e-6, 0.0);
	}

	/**
	 * Black 1976 implied standard deviation, i.e.
	 * volatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double blackFormulaImpliedStdDev(
			final PlainVanillaPayoff payoff, 
			@Price final double strike,
			@Price final double forward, 
			@Price final double blackPrice,
			@DiscountFactor final double discount,
			@Price final double guess, 
			@Price final double accuracy) {

		return blackFormulaImpliedStdDev(payoff.optionType(), strike, forward, blackPrice, discount, guess, accuracy, 0.0);
	}

	/**
	 * Black 1976 implied standard deviation, i.e.
	 * volatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double blackFormulaImpliedStdDev(
			final PlainVanillaPayoff payoff, 
			@Price final double strike,
			@Price final double forward, 
			@Price final double blackPrice,
			@DiscountFactor final double discount,
			@Price final double guess, 
			@Price final double accuracy,
			@Price final double displacement) {

		return blackFormulaImpliedStdDev(payoff.optionType(), strike, forward, blackPrice, discount, guess, accuracy, displacement);
	}

	// ---
	// ---
	// ---

	/**
	 * Black 1976 probability of being in the money (in the bond martingale
	 * measure), i.e. N(d2). It is a risk-neutral probability, not the real
	 * world one.
	 * 
	 * @Note Instead of volatility it uses standard deviation, i.e.
	 *       volatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double blackFormulaCashItmProbability(
			final Option.Type optionType, 
			@Price final double strike,
			@Price final double forward, 
			@StdDev final double stddev) {

		return blackFormulaCashItmProbability(optionType, strike, forward, stddev, 0.0);
	}

	/**
	 * Black 1976 probability of being in the money (in the bond martingale
	 * measure), i.e. N(d2). It is a risk-neutral probability, not the real
	 * world one.
	 * 
	 * @Note Instead of volatility it uses standard deviation, i.e.
	 *       volatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double blackFormulaCashItmProbability(
			final Option.Type optionType, 
			@Price final double strike,
			@Price final double forward, 
			@StdDev final double stddev,
			@Price final double displacement) {

        if (stddev==0.0) return (forward * optionType.toInteger() > strike *optionType.toInteger() ? 1.0 : 0.0);
        if (strike==0.0) return (optionType==Option.Type.CALL ? 1.0 : 0.0);
        double d1 = Math.log((forward+displacement)/(strike+displacement))/stddev + 0.5*stddev;
        double d2 = d1 - stddev;

        // TODO: code review
        final CumulativeNormalDistribution phi = new CumulativeNormalDistribution();
        return phi.op(optionType.toInteger() * d2);
	}

	// ---
	// ---
	// ---

	/**
	 * Black 1976 probability of being in the money (in the bond martingale
	 * measure), i.e. N(d2). It is a risk-neutral probability, not the real
	 * world one.
	 * 
	 * @Note Instead of volatility it uses standard deviation, i.e.
	 *       volatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double blackFormulaCashItmProbability(
			final PlainVanillaPayoff payoff, 
			@Price final double strike,
			@Price final double forward, 
			@StdDev final double stddev,
			@Price final double displacement) {

		return blackFormulaCashItmProbability(payoff.optionType(), strike, forward, stddev, displacement);
	}

	
	// ---
	// ---
	// ---

	/**
	 * Black 1976 formula for standard deviation derivative
	 * <p>
	 * 
	 * @Note Instead of volatility it uses standard deviation, i.e.
	 *       volatilitysqrt(timeToMaturity), and it returns the derivative with
	 *       respect to the standard deviation. If T is the time to maturity
	 *       Black vega would be blackstddevDerivative(strike, forward,
	 *       stddev)sqrt(T)
	 */
	public static /*@Price*/ double blackFormulaStdDevDerivative(
			@Price final double strike, 
			@Price final double forward,
			@StdDev final double stddev) {

		return blackFormulaStdDevDerivative(strike, forward, stddev, 1.0, 0.0);
	}

	/**
	 * Black 1976 formula for standard deviation derivative
	 * <p>
	 * 
	 * @Note Instead of volatility it uses standard deviation, i.e.
	 *       volatilitysqrt(timeToMaturity), and it returns the derivative with
	 *       respect to the standard deviation. If T is the time to maturity
	 *       Black vega would be blackstddevDerivative(strike, forward,
	 *       stddev)sqrt(T)
	 */
	public static /*@Price*/ double blackFormulaStdDevDerivative(
			@Price final double strike, 
			@Price final double forward,
			@StdDev final double stddev,
			@DiscountFactor final double discount) {

		return blackFormulaStdDevDerivative(strike, forward, stddev, discount, 0.0);
	}

	/**
	 * Black 1976 formula for standard deviation derivative
	 * <p>
	 * 
	 * @Note Instead of volatility it uses standard deviation, i.e.
	 *       volatilitysqrt(timeToMaturity), and it returns the derivative with
	 *       respect to the standard deviation. If T is the time to maturity
	 *       Black vega would be blackstddevDerivative(strike, forward,
	 *       stddev)sqrt(T)
	 */
	public static /*@Price*/ double blackFormulaStdDevDerivative(
			@Price double strike, 
			@Price double forward,
			@StdDev final double stddev,
			@DiscountFactor final double discount,
			@Price final double displacement) {

		if (strike < 0.0)       throw new IllegalArgumentException("strike must be non-negative"); // TODO: message
		if (forward <= 0.0)     throw new IllegalArgumentException("forward must be positive"); // TODO: message
		if (stddev < 0.0)   throw new IllegalArgumentException("blackPrice must be non-negative"); // TODO: message
		if (discount <= 0.0)    throw new IllegalArgumentException("discount must be positive"); // TODO: message
		if (displacement < 0.0) throw new IllegalArgumentException("displacement must be non-negative"); // TODO: message
		
		forward = forward + displacement;
		strike = strike + displacement;

		final double d1 = Math.log(forward/strike)/stddev + .5*stddev;

		// TODO: code review
		final CumulativeNormalDistribution cdf = new CumulativeNormalDistribution();
		return discount * forward * cdf.derivative(d1);
	}

	// ---
	// ---
	// ---

	/**
	 * Black 1976 formula for standard deviation derivative
	 * <p>
	 * 
	 * @Note Instead of volatility it uses standard deviation, i.e.
	 *       volatilitysqrt(timeToMaturity), and it returns the derivative with
	 *       respect to the standard deviation. If T is the time to maturity
	 *       Black vega would be blackstddevDerivative(strike, forward,
	 *       stddev)sqrt(T)
	 */
	public static /*@Price*/ double blackFormulastddevDerivative(
			final PlainVanillaPayoff payoff, 
			@Price final double forward,
			@StdDev final double stddev) {

		return blackFormulaStdDevDerivative(payoff, forward, stddev, 1.0, 0.0);
	}

	/**
	 * Black 1976 formula for standard deviation derivative
	 * <p>
	 * 
	 * @Note Instead of volatility it uses standard deviation, i.e.
	 *       volatilitysqrt(timeToMaturity), and it returns the derivative with
	 *       respect to the standard deviation. If T is the time to maturity
	 *       Black vega would be blackstddevDerivative(strike, forward,
	 *       stddev)sqrt(T)
	 */
	public static /*@Price*/ double blackFormulastddevDerivative(
			final PlainVanillaPayoff payoff, 
			@Price final double forward,
			@StdDev final double stddev,
			@DiscountFactor final double discount) {

		return blackFormulaStdDevDerivative(payoff, forward, stddev, discount, 0.0);
	}

	/**
	 * Black 1976 formula for standard deviation derivative
	 * <p>
	 * 
	 * @Note Instead of volatility it uses standard deviation, i.e.
	 *       volatilitysqrt(timeToMaturity), and it returns the derivative with
	 *       respect to the standard deviation. If T is the time to maturity
	 *       Black vega would be blackstddevDerivative(strike, forward,
	 *       stddev)sqrt(T)
	 */
	public static /*@Price*/ double blackFormulaStdDevDerivative(
			final PlainVanillaPayoff payoff, 
			@Price final double forward,
			@StdDev final double stddev,
			@DiscountFactor final double discount,
			@Price final double displacement) {

        return blackFormulaStdDevDerivative(payoff.strike(), forward, stddev, discount, displacement);
	}

	// ---
	// ---
	// ---

	/**
	 * Black style formula when forward is normal rather than log-normal. This
	 * is essentially the model of Bachelier.
	 * 
	 * @Note Bachelier model needs absolute volatility, not percentage
	 *       volatility. Standard deviation is
	 *       absoluteVolatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double bachelierBlackFormula(
            final PlainVanillaPayoff payoff, 
            @Price final  double forward,
            @StdDev final double stddev,
            @Price final  double discount) {

	    return bachelierBlackFormula(payoff.optionType(), payoff.strike(), forward, stddev, discount);
	}

	/**
	 * Black style formula when forward is normal rather than log-normal. This
	 * is essentially the model of Bachelier.
	 * 
	 * @Note Bachelier model needs absolute volatility, not percentage
	 *       volatility. Standard deviation is
	 *       absoluteVolatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double bachelierBlackFormula(
			final Option.Type optionType, 
			@Price final   double strike,
			@Price final   double forward, 
			@StdDev final double stddev,
			final @DiscountFactor double discount) {
	    
        if (stddev < 0.0)    throw new IllegalArgumentException("blackPrice must be non-negative"); // TODO: message
        if (discount <= 0.0) throw new IllegalArgumentException("discount must be positive"); // TODO: message

        double d = (forward - strike) * optionType.ordinal(), h = d / stddev;

        if (stddev == 0.0) return discount * Math.max(d, 0.0);

        // TODO: code review
        final CumulativeNormalDistribution phi = new CumulativeNormalDistribution();
        @NonNegative double result = discount * stddev * phi.derivative(h) + d * phi.op(h);
        if (result < 0.0) throw new RuntimeException("negative value");
        return result;
	}

	// ---
	// ---
	// ---

	/**
	 * Black style formula when forward is normal rather than log-normal. This
	 * is essentially the model of Bachelier.
	 * 
	 * @Note Bachelier model needs absolute volatility, not percentage
	 *       volatility. Standard deviation is
	 *       absoluteVolatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double bachelierBlackFormula(
            final Option.Type optionType, 
            @Price final  double strike,
            @Price final  double forward, 
            @StdDev final double stddev) {

		return bachelierBlackFormula(optionType, strike, forward, stddev, 1.0);
	}

	/**
	 * Black style formula when forward is normal rather than log-normal. This
	 * is essentially the model of Bachelier.
	 * 
	 * @Note Bachelier model needs absolute volatility, not percentage
	 *       volatility. Standard deviation is
	 *       absoluteVolatility*sqrt(timeToMaturity)
	 */
	public static /*@Price*/ double bachelierBlackFormula(
            final PlainVanillaPayoff payoff, 
            @Price final  double forward,
            @StdDev final double stddev) {
	    
        return bachelierBlackFormula(payoff, forward, stddev, 1.0);
	}

	

	
	//
	// private inner classes
	//
	
	private static class BlackImpliedStdDevHelper implements Derivative {

		private final double halfOptionType_;
		private final double signedStrike_, signedForward_;
		private final double undiscountedBlackPrice_, signedMoneyness_;
		private final CumulativeNormalDistribution N_;
		
		public BlackImpliedStdDevHelper(
				Option.Type optionType, 
				double strike,
				double forward, 
				double undiscountedBlackPrice) {
			this(optionType, strike, forward, undiscountedBlackPrice, 0.0d);
		}

		public BlackImpliedStdDevHelper(
				Option.Type optionType, double strike,
				double forward, 
				double undiscountedBlackPrice,
				double displacement) {
			
			if (strike < 0.0)       throw new IllegalArgumentException("strike must be non-negative"); // TODO: message
			if (forward <= 0.0)     throw new IllegalArgumentException("forward must be positive"); // TODO: message
			if (displacement < 0.0) throw new IllegalArgumentException("displacement must be non-negative"); // TODO: message
			
			this.halfOptionType_ = (0.5 * optionType.toInteger());
            this.signedStrike_ = (optionType.toInteger() * (strike + displacement));
            this.signedForward_ = (optionType.toInteger() * (forward + displacement));
            this.undiscountedBlackPrice_ = (undiscountedBlackPrice);
            if (undiscountedBlackPrice < 0.0)
                throw new IllegalArgumentException("undiscounted Black price must be non-negative");
            signedMoneyness_ = optionType.toInteger() * Math.log((forward + displacement) / (strike + displacement));

            // TODO: code review
            this.N_ = new CumulativeNormalDistribution();
		}

		public double op(@NonNegative double stddev) {
		    
		    //XXX :; Remove QL_EXTRA_SAFETY_CHECKS block
            if (stddev < 0.0) throw new IllegalArgumentException("stddev must be non-negative");
		    //----
            
            if (stddev == 0.0) return Math.max(signedForward_ - signedStrike_, 0.0d) - undiscountedBlackPrice_;
            
            double temp = halfOptionType_ * stddev;
            double d = signedMoneyness_ / stddev;
            double signedD1 = d + temp;
            double signedD2 = d - temp;
            double result = signedForward_ * N_.op(signedD1) - signedStrike_ * N_.op(signedD2);
            // numerical inaccuracies can yield a negative answer
            return Math.max(0.0, result) - undiscountedBlackPrice_;
		}

		public double derivative(@NonNegative double stddev) {
		    
            //XXX :; Remove QL_EXTRA_SAFETY_CHECKS block
            if (stddev < 0.0) throw new IllegalArgumentException("stddev must be non-negative");
            //----
            
            double signedD1 = signedMoneyness_ / stddev + halfOptionType_ * stddev;
            return signedForward_ * N_.derivative(signedD1);
		}

	}

}
