/*
 Copyright (C) 2009 Ueli Hofstetter
 Copyright (C) 2009 Richard Gomes

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
package org.jquantlib.math.functions;

import org.jquantlib.math.Ops;


/**
 * This method binds the 1st argument of a binary predicate to a scalar value, effectively enabling
 * a binary predicate to be called in a context intended for a unary predicate.
 *
 * @author Ueli Hofstetter
 * @author Richard Gomes
 */
public final class Bind1stPredicate implements Ops.DoublePredicate {

    private final double scalar;                // 1st argument
    private final Ops.BinaryDoublePredicate f;  // 2nd argument
    
	public Bind1stPredicate(final double scalar, final Ops.BinaryDoublePredicate f) {
	    this.scalar = scalar;
	    this.f = f;
	}

	
	//
    // implements DoublePredicate
    //
    
	@Override
	public boolean op(final double a) {
		return f.op(scalar, a);
	}

}
