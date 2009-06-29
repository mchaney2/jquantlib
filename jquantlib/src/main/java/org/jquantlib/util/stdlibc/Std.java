/*
 Copyright (C) 2007 Richard Gomes

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

package org.jquantlib.util.stdlibc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jquantlib.math.E_IBinaryFunction;
import org.jquantlib.math.E_IUnaryFunction;
import org.jquantlib.math.E_UnaryFunction;
import org.jquantlib.math.UnaryFunctionDouble;
import org.jquantlib.math.functions.DoubleFunction;

/**
 * Mimics library libstdc++ from C++ language which exposes top level functions to <code>std:: namespace</code>
 * 
 * @see <a http://gcc.gnu.org/onlinedocs/libstdc++/latest-doxygen/index.html">libstdc++ Source Documentation</a>
 * @see <a href="http://javadude.com/articles/passbyvalue.htm">Java is Pass-by-Value, Dammit!</a>
 * 
 * @author Dominik Holenstein
 * @author Richard Gomes
 * @author Srinivas Hasti
 */

//XXX @SuppressWarnings("PMD.TooManyMethods")
public final class Std {

    
    /**
     * Singleton instance for the whole application.
     * <p>
     * In an application server environment, it could be by class loader depending on scope of the
     * jquantlib library to the module.
     * 
     * @see <a href="http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html">The "Double-Checked Locking is Broken" Declaration </a>
     */
    private volatile static Std instance = null;
    
    public static Std getInstance() {
        if (instance == null) {
            synchronized (Std.class) {
                if (instance == null) {
                    instance = new Std();
                }
            }
        }
        return instance;
    }

    
    
	//
	// public methods
	//

    /* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#adjacent_difference(double[])
     */
    @SuppressWarnings("PMD")
    public final double[] adjacent_difference(final double[] array) {
        return adjacent_difference(array, 0);
    }
    
    /* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#adjacent_difference(double[], int)
     */
    @SuppressWarnings("PMD")
    public final double[] adjacent_difference(final double[] array, final int from) {
        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
        if (array==null) throw new NullPointerException();
        if (from>=array.length) throw new IndexOutOfBoundsException();
        
        final double[] diff = new double[array.length-from];
        for (int i = from; i < array.length; i++) {
            final double curr = array[i]; 
            if (i == 0) {
                diff[i] = array[i];
            } else {
                final double prev = array[i - 1];
                diff[i] = curr - prev;
            }
        }
        return diff;
    }
    

    /* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#apply(org.jquantlib.math.Array, org.jquantlib.math.functions.DoubleFunction)
     */
	public void apply(final double[] array, final DoubleFunction f) {
        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
        if (array==null || f==null) throw new NullPointerException();
        
		apply(array, 0, array.length, f);
	}


	/* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#apply(org.jquantlib.math.Array, int, int, org.jquantlib.math.functions.DoubleFunction)
     */
	public void apply(final double[] array, final int from, final int to, final DoubleFunction f) {
        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
        if (array==null || f==null) throw new NullPointerException();
        if (from > to || to > array.length) throw new IndexOutOfBoundsException();
        
		for(int i=from; i<to; i++) {
        	array[i] = f.apply(array[i]);
        }
	}
	

	//FIXME :: needs code review.
	// The use of E_IUnaryFunction and E_IBinaryFunction are pretty confusing.
	// It's not clear what these interfaces/classes do (where are the comments and references, etc?)
	// and, in particular, E_IBinaryFunction should have 3 generic arguments: 2 parameter types and 1 result type.
	/* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#bind1st(org.jquantlib.math.E_IBinaryFunction, ParameterType)
     */
	public <ParameterType, ReturnType> E_IUnaryFunction<ParameterType, ReturnType> bind1st(
            E_IBinaryFunction<ParameterType, ReturnType> binaryFunction, ParameterType bounded) {
        
        final E_IUnaryFunction<ParameterType, ReturnType> ret = new E_UnaryFunction<ParameterType, ReturnType>() {
            private E_IBinaryFunction<ParameterType, ReturnType> binary;
            private ParameterType bounded;

            @Override
            public ReturnType evaluate(ParameterType x) {
                return binary.evaluate(bounded, x);
            }
        };
        ret.setBinaryFunction(binaryFunction);
        ret.setBoundedValue(bounded);
        return ret;
    }
    
    
    //FIXME :: needs code review.
    // The use of E_IUnaryFunction and E_IBinaryFunction are pretty confusing.
    // It's not clear what these interfaces/classes do (where are the comments and references, etc?)
    // and, in particular, E_IBinaryFunction should have 3 generic arguments: 2 parameter types and 1 result type.
	/* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#bind2nd(org.jquantlib.math.E_IBinaryFunction, ParameterType)
     */
	public <ParameterType, ReturnType> E_IUnaryFunction<ParameterType, ReturnType> bind2nd(
            E_IBinaryFunction<ParameterType, ReturnType> binaryFunction, ParameterType bounded) {
        
        final E_IUnaryFunction<ParameterType, ReturnType> ret = new E_UnaryFunction<ParameterType, ReturnType>() {
            private E_IBinaryFunction<ParameterType, ReturnType> binary;
            private ParameterType bounded;

            @Override
            public ReturnType evaluate(ParameterType x) {
                return binary.evaluate(x, bounded);
            }
        };
        ret.setBinaryFunction(binaryFunction);
        ret.setBoundedValue(bounded);
        return ret;
    }	
	
	
	/* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#min_element(int, int, java.util.List)
     */
    public double min_element(final int from, final int to, final List<Double> array) {
        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
        if (array==null) throw new NullPointerException();
        if (from>to || to>=array.size()) throw new IndexOutOfBoundsException();

        double value = Double.POSITIVE_INFINITY;
        for (int i=from; i<to; i++) {
            double temp = array.get(i); 
            if (temp < value) value = temp;
        }
        return value;
    }
 
    /* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#min_element(int, int, double[])
     */
    public double min_element(final int from, final int to, final double []  array) {
        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
        if (array==null) throw new NullPointerException();
        if (from>to || to>=array.length) throw new IndexOutOfBoundsException();
        
        double value = Double.POSITIVE_INFINITY;
        for (int i = from; i<to; i++) {
            if (array[i] < value) value = array[i];
        }
        return value;
    }
    
    
    /* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#max_element(int, int, java.util.List)
     */
    public double max_element(final int from, final int to, final List<Double> array) {
        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
        if (array==null) throw new NullPointerException();
        if (from>to || to>=array.size()) throw new IndexOutOfBoundsException();
        
        double value = Double.NEGATIVE_INFINITY;
        for (int i=from; i<to; i++) {
            double temp = array.get(i); 
            if (temp > value) value = temp;
        }
        return value;
    }

    
    /* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#max_element(int, int, double[])
     */
    public double max_element(final int from, final int to, double [] array) {
        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
        if (array==null) throw new NullPointerException();
        if (from>to || to>=array.length) throw new IndexOutOfBoundsException();
        
        double value = Double.NEGATIVE_INFINITY;
        for(int i = from; i<to; i++){
            if (array[i] > value) value = array[i];
        }
        return value;
    }

	
    /* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#lower_bound(double[], double)
     */
    @SuppressWarnings("PMD.MethodNamingConventions")
    public int lower_bound(final double[] array, final double val) {
        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
        if (array==null) throw new NullPointerException();

        return lower_bound(array, 0, array.length-1, val);
    }


    /**
     * Finds the first position in which val could be inserted without changing the ordering.
     * 
     * @note Mimics std::lower_bound
     *
     * @see <a href="http://gcc.gnu.org/onlinedocs/libstdc++/latest-doxygen/a01016.html#g0ff3b53e875d75731ff8361958fac68f">std::lower_bound</a>
     */
    @SuppressWarnings("PMD")
    private int lower_bound(final double[] array, int from, int to, final double val) {
        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
        if (array==null) throw new NullPointerException();
        if (from>to || to>=array.length) throw new IndexOutOfBoundsException();

        int len = to - from;
        int half;
        int middle;
        
        while (len > 0) {
            half = len >> 1;
            middle = from;
            middle = middle + half;
            
            if (array[middle] < val) {
                from = middle;
                from++;
                len = len - half -1;
            } else {
                len = half;
            }
        }
        return from;
    }

    
    /* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#upper_bound(double[], double)
     */
	@SuppressWarnings("PMD.MethodNamingConventions")
	public int upper_bound(final double[] array, final double val) {
        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
        if (array==null) throw new NullPointerException();

		return upper_bound(array, 0, array.length-1, val);
	}

	
	/* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#upper_bound(java.lang.Double[], double)
     */
	@SuppressWarnings("PMD.MethodNamingConventions")
	public int upper_bound(final Double[] array, final double val) {
        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
        if (array==null) throw new NullPointerException();

		double[] d = new double[array.length];
		for (int i = 0; i < array.length; i++) {
			d[i] = array[i];
		}
		return upper_bound(d, 0, array.length - 1, val);
	}	


	/**
     * Finds the last position in which val could be inserted without changing the ordering. 
     * 
     * @note Mimics std::upper_bound
     *
     * @see <a href="http://gcc.gnu.org/onlinedocs/libstdc++/latest-doxygen/a01016.html#g9bf525d5276b91ff6441e27386034a75">std::upper_bound</a>
     */
	@SuppressWarnings("PMD.MethodNamingConventions")
	private int upper_bound(final double[] array, int from, int to, final double val) {
        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
        if (array==null) throw new NullPointerException();
        if (from>to || to>=array.length) throw new IndexOutOfBoundsException();

		int len = to - from;
		int half;
		int middle;
		
		while (len > 0) {
			half = len >> 1;
			middle = from;
			middle = middle + half;
			
			if (val < array[middle]){
				len = half;
			} else {
				from = middle;
				from++;
				len = len - half -1;
			}
		}
		return from;
	}

	
	/* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#transform(org.jquantlib.math.Array, org.jquantlib.math.Array, org.jquantlib.math.UnaryFunctionDouble)
     */
	public final void transform(final double[] array, final double[] result, final UnaryFunctionDouble f) {
        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
        if (array==null || result==null || f==null) throw new NullPointerException();

		for (int i=0; i<array.length; i++) {
			result[i] = f.evaluate(array[i]);
		}
	}
	

	/* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#transform(double[], double[], org.jquantlib.math.E_UnaryFunction)
     */
	public final<ParameterType, ReturnType>
	        void transform(final double[] array, final double[] result, final E_UnaryFunction<Double, Double> f) {

	    // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
        if (array==null || result==null || f==null) throw new NullPointerException();
	    
        for(int i=0; i<array.length; i++){
            result[i] = f.evaluate(array[i]);
        }
    }

	
    /* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#min(T)
     */
	// TODO: consider the parallel version of std::min (probably implementing in class GnuParallel)
	// http://gcc.gnu.org/onlinedocs/libstdc++/latest-doxygen/a00964.html#0d0e5aa5b83e8ffa90d57714f03d73bf
    public <T extends Comparable<T>> T min(T... t) {
        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
        if (t==null) throw new NullPointerException();

        List<T> list = Arrays.asList(t);
        Collections.sort(list);
        return list.get(0);
    }


    /* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#max(T)
     */
    // TODO: consider the parallel version of std::max (probably implementing in class GnuParallel)
    // http://gcc.gnu.org/onlinedocs/libstdc++/latest-doxygen/a00964.html#992b78d1946c7c02e46bc3509637f12d
    public <T extends Comparable<T>> T max(T... t) {
        
        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
        if (t==null) throw new NullPointerException();
        
        List<T> list = Arrays.asList(t);
        Collections.sort(list);
        return list.get(list.size() - 1);
    }


    /* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#accumulate(int, int, double[], double)
     */
    public double accumulate(final int from, final int to, final double[] array, final double init) {
        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
        if (array==null) throw new NullPointerException();
        if (from>to || to>=array.length) throw new IndexOutOfBoundsException();

        double sum = init;
        for (int i = from; i < to; i++) {
            sum += array[i];
        }
        return sum;
    }

    
    /* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#accumulate(double[], double)
     */
    public double accumulate(final double[] array, final double init) {
        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
        if (array==null) throw new NullPointerException();

        return accumulate(0, array.length, array, init);
    }

    
    /* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#accumulate(java.util.List, double)
     */
    public double accumulate(final List<Double> array, final double init) {
        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
        if (array==null) throw new NullPointerException();

        double sum = 0.0;
        for (int i = 0; i < array.size(); i++) {
            sum += array.get(i);
        }
        return sum;
    }


    // FIXME: This method must be refactored.
    // There's no such "std::multiplies"
    // This class returns an object which is extended from an abstract class from math package, which
    // is something weird because 2 very related concepts which could not be apart are located in
    // two very different places.
    /* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#multiplies(double)
     */
    public E_UnaryFunction<Double, Double> multiplies(double multiplier) {
        
        E_UnaryFunction<Double, Double> ret = new E_UnaryFunction<Double, Double>() {
            @Override
            public Double evaluate(Double x) {
                return x * params[0];
            }
        };
        ret.setParams(multiplier);
        return ret;
    }

    
    /* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#inner_product(org.jquantlib.math.Array, org.jquantlib.math.Array)
     */
    public double inner_product(final double[] arrayA, final double[] arrayB) {
        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
        if (arrayA==null || arrayB==null) throw new NullPointerException();

        return inner_product(arrayA, arrayB, 0.0);
    }

    
//    /* (non-Javadoc)
//     * @see org.jquantlib.util.stdlibc.StdIntf#inner_product(org.jquantlib.math.Array, org.jquantlib.math.Array, double)
//     */
//    public double inner_product(final double[] arrayA, final double[] arrayB, final double init) {
//        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
//        if (arrayA==null || arrayB==null) throw new NullPointerException();
//
//        return inner_product(arrayA.getData(), arrayB.getData(), init);
//    }

    
//    /* (non-Javadoc)
//     * @see org.jquantlib.util.stdlibc.StdIntf#inner_product(org.jquantlib.math.Array, int, org.jquantlib.math.Array, int, int, double)
//     */
//    public double inner_product(final double[] arrayA, final int fromA, final double[] arrayB, final int fromB, final int length, final double init) {
//        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
//        if (arrayA==null || arrayB==null) throw new NullPointerException();
//        if (fromA<0 || fromA>=arrayA.size() || fromA<0 || fromA>=arrayA.size()) throw new IllegalArgumentException();
//        if (fromA+length>=arrayA.size() || fromB+length>=arrayB.size()) throw new IllegalArgumentException();
//
//        return inner_product(arrayA.getData(), fromA, arrayB.getData(), fromB, length, init);
//    }
    
    
    /* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#inner_product(double[], double[], double)
     */
    public double inner_product(final double[] arrayA, final double[] arrayB, final double init) {
        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
        if (arrayA==null || arrayB==null) throw new NullPointerException();
        if (arrayA.length!=arrayB.length) throw new IllegalArgumentException();

        double innerProduct = init;
        for (int i = 0; i < arrayA.length; i++) {
            innerProduct += arrayA[i] * arrayB[i];
        }
        return innerProduct;
    }

    
    /* (non-Javadoc)
     * @see org.jquantlib.util.stdlibc.StdIntf#inner_product(double[], int, double[], int, int, double)
     */
    public double inner_product(
            final double[] arrayA, int fromA, 
            final double[] arrayB, int fromB, 
            final int length, final double init) {
        
        // TODO: Design by Contract? http://bugs.jquantlib.org/view.php?id=291
        if (arrayA==null || arrayB==null) throw new NullPointerException();
        if (fromA<0 || fromA>=arrayA.length || fromA<0 || fromA>=arrayA.length) throw new IllegalArgumentException();
        if (fromA+length>=arrayA.length || fromB+length>=arrayB.length) throw new IllegalArgumentException();

        double innerProduct = init;
        for (int i = 0; i < length; i++) {
            innerProduct += arrayA[fromA] * arrayB[fromB];
            fromA++;
            fromB++;
        }
        return innerProduct;
    }
    
}
