/*
 Copyright (C) 2008 Anand Mani

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

package org.jquantlib.math.interpolations;

import org.jquantlib.math.Array;
import org.jquantlib.math.interpolations.factories.ForwardFlat;

/**
 * Forward-flat interpolation between discrete points
 * <p>
 * Interpolations are not instantiated directly by applications, but via a factory class.
 *
 * @see ForwardFlat
 * 
 * @author Anand Mani
 */
public class ForwardFlatInterpolation extends AbstractInterpolation {

	//
    // private fields
    //
    
    private Array vp;


    //
    // private constructors
    //
    
    /**
     * Constructor for a forward-flat interpolation between discrete points
     * <p>
     * Interpolations are not instantiated directly by applications, but via a factory class.
     * 
     * @see ForwardFlat
     */
	private ForwardFlatInterpolation() {
		// access denied to default constructor
	}


	//
    // static public methods
    //

    /**
     * This is a factory method intended to create this interpolation.
     * 
     * @see ForwardFlat
     */
    static public Interpolator getInterpolator() /* @ReadOnly */{
        ForwardFlatInterpolation forwardFlatInterpolation = new ForwardFlatInterpolation();
        return new ForwardFlatInterpolationImpl(forwardFlatInterpolation);
    }


	//
	// overrides AbstractInterpolation
	//
	
	@Override
	protected double primitiveImpl(final double x) /* @ReadOnly */{
		int i = locate(x);
		double dx = x - vx.get(i);
		return vp.get(i) + dx * vy.get(i);
	}

	@Override
	protected double derivativeImpl(final double x) /* @ReadOnly */{
		return 0.0;
	}

	@Override
	protected double secondDerivativeImpl(final double x) /* @ReadOnly */{
		return 0.0;
	}

    //
    // Overrides AbstractInterpolation
    //
    
    /**
     * {@inheritDoc}
     * 
     * @note Class factory is responsible for initializing <i>vx</i> and <i>vy</i>  
     */
	@Override
	public void update() {
		super.update();

		vp = new Array(vx.length);
		vp.set(0, 0.0);
		for (int i=1; i<vx.length; i++) {
			double dx = vx.get(i) - vx.get(i-1);
			double value = vp.get(i-1) + dx * vy.get(i-1); 
			vp.set(i, value); 
		}
	}

	
	//
	// implements Ops.DoubleOp
	//

    @Override
	protected double evaluateImpl(final double x) /* @ReadOnly */{
		int n = vx.length;
		if (x >= vx.get(n-1))
			return vy.get(n-1);
		int i = locate(x);
		return vy.get(i);
	}


	//
	// private inner classes
	//

	/**
	 * This class is a default implementation for {@link ForwardFlatInterpolation} instances.
	 * 
	 * @author Anand Mani
	 */

	private static class ForwardFlatInterpolationImpl implements Interpolator {
		private ForwardFlatInterpolation delegate;

		public ForwardFlatInterpolationImpl(final ForwardFlatInterpolation delegate) {
			this.delegate = delegate;
		}

	    @Override
		public final Interpolation interpolate(final Array x, final Array y) /* @ReadOnly */{
			return interpolate(x.length, x, y);
		}

	    @Override
		public final Interpolation interpolate(final int size, final Array x, final Array y) /* @ReadOnly */{
			delegate.vx = x.copyOfRange(0, size);
			delegate.vy = y.copyOfRange(0, size);
			delegate.update();
			return delegate;
		}

	    @Override
		public final boolean global() {
			return false; // only CubicSpline and Sabr are global, whatever it means!
		}
	}

}
