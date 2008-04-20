package org.jquantlib.math;

/**
 * Represents a function of one variable; f(x)
 * 
 * @author <Richard Gomes>
 */
public interface UnaryFunctionDouble {

	/**
	 * Computes the value of the function; f(x)
	 * 
	 * @param x
	 * @return f(x)
	 */
	//FIXME Generics on argument return?
	public double evaluate(double x);
	
	//boolean isFailed() TODO error handling
}
