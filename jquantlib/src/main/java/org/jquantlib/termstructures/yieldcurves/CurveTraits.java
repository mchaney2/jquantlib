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

package org.jquantlib.termstructures.yieldcurves;

import org.jquantlib.math.Array;
import org.jquantlib.termstructures.IYieldTermStructure;
import org.jquantlib.util.Date;

/**
 * 
 * @author Richard Gomes
 */
public interface CurveTraits {

    /**
     * value at reference
     */ 
    public double initialValue();
    
    /**
     * initial guess
     */
    public double initialGuess();
    
    /**
     * further guesses
     */
    public double guess(final IYieldTermStructure c, final Date d);

    /**
     * possible constraints based on previous values
     */
    public double minValueAfter(int i, final Array data);
    
    /**
     * possible constraints based on maximum values
     */
    public double maxValueAfter(int i, final Array data);
    
    /**
     * update with new guess
     */
    public void updateGuess(final Array data, double value, int i);

}