/*
 Copyright (C) 2009 Ueli Hofstetter

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
 Copyright (C) 2001, 2002, 2003 Nicolas Di Cesare

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

package org.jquantlib.math.optimization;

import org.jquantlib.math.Array;

public abstract class CostFunction {

    public CostFunction() {
        if (System.getProperty("EXPERIMENTAL") == null) {
            throw new UnsupportedOperationException("Work in progress");
        } 
    }
    
    //! method to overload to compute the cost function value in x
    public abstract double value(Array x);
    
    //FIXME: Disposable implementation
    //! method to overload to compute the cost function values in x
    //virtual Disposable<Array> values(const Array& x) const =0;
    
    //! method to overload to compute grad_f, the first derivative of
    //  the cost function with respect to x
    public abstract Array values(Array x);
    
    
    public void gradient(Array grad,  Array x) {
        //FIXME: Implement method
        double eps = 0;//finiteDifferenceEpsilon();
        double fp = 0;
        double fm = 0;
        Array xx = new Array(x);
        for (int i=0; i<x.size(); i++) {
            xx.set(i, xx.get(i) + eps);
            fp = value(xx);
            xx.set(i, xx.get(i) - 2.0*eps);
            fm = value(xx);
            grad.set(i, 0.5*(fp - fm)/eps);
            xx.set(i, x.get(i));
        }
    }
    
    //! method to overload to compute grad_f, the first derivative of
    //  the cost function with respect to x and also the cost function
    public double valueAndGradient(Array grad, Array x){
        //FIXME: missing implementation see above
        //gradient(grad, x);
        return value(x);
    }
    
    /*
     * Moved ParametersTransformation in external top level class
     */

}