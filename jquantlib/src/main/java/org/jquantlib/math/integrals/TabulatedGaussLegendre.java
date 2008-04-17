package org.jquantlib.math.integrals;

import org.jquantlib.math.UnaryFunctionDouble;

/**
 * @author <Richard Gomes>
 */
public class TabulatedGaussLegendre {


	public TabulatedGaussLegendre(){
		setOrder(20);
	}

	public TabulatedGaussLegendre(int order){
		setOrder(order);
	}

    public double evaluate(UnaryFunctionDouble f) {
        if (w_==null){
        	throw new ArithmeticException("Null weights");
        }
        if (x_==null){
            throw new ArithmeticException("Null abscissas");
        	
        }
        int startIdx;
        double val;

        final boolean isOrderOdd = ((order_ & 1) != 0);

        if (isOrderOdd) {
          if (!(n_>0)){
        	  throw new ArithmeticException("assume at least 1 point in quadrature");
          }
          val = w_[0]*f.evaluate(x_[0]);
          startIdx=1;
        } else {
          val = 0.0;
          startIdx=0;
        }

        for (int i=startIdx; i<n_; ++i) {
        	double w = w_[i];
        	double x = x_[i];
            val += w*f.evaluate(x);
            val += w*f.evaluate(-x);
        }
        return val;
    }

	
	
//	public int getOrder() {
//		return order_;
//	}
//	
    public void setOrder(int order) {
        switch(order) {
          case(6):
            order_=order; x_=x6; w_=w6; n_=n6;
            break;
          case(7):
            order_=order; x_=x7; w_=w7; n_=n7;
            break;
          case(12):
            order_=order; x_=x12; w_=w12; n_=n12;
            break;
          case(20):
            order_=order; x_=x20; w_=w20; n_=n20;
            break;
          default:
            throw new ArithmeticException("order " + order + " not supported");
        }
    }


    // Abscissas and Weights from Abramowitz and Stegun

    /* order 6 */
    private static final double x6[] = { 	0.238619186083197,
        									0.661209386466265,
                                            0.932469514203152 };

    private static final double w6[] = { 	0.467913934572691,
        									0.360761573048139,
                                            0.171324492379170 };

    private static final int n6 = 3;

    /* order 7 */
    private static final double x7[] = { 	0.000000000000000,
        									0.405845151377397,
                                            0.741531185599394,
                                            0.949107912342759 };

    private static final double w7[] = { 	0.417959183673469,
        									0.381830050505119,
                                            0.279705391489277,
                                            0.129484966168870 };

    private static final int n7 = 4;

    /* order 12 */
    private static final double x12[] = { 	0.125233408511469,
        									0.367831498998180,
                                            0.587317954286617,
                                            0.769902674194305,
                                            0.904117256370475,
                                            0.981560634246719 };

    private static final double w12[] = { 	0.249147045813403,
        									0.233492536538355,
                                            0.203167426723066,
                                            0.160078328543346,
                                            0.106939325995318,
                                            0.047175336386512 };

    private static final int n12 = 6;

    /* order 20 */
    private static final double x20[] = { 	0.076526521133497,
        									0.227785851141645,
                                            0.373706088715420,
                                            0.510867001950827,
                                            0.636053680726515,
                                            0.746331906460151,
                                            0.839116971822219,
                                            0.912234428251326,
                                            0.963971927277914,
                                            0.993128599185095 };

    private static final double w20[] = { 	0.152753387130726,
        									0.149172986472604,
                                            0.142096109318382,
                                            0.131688638449177,
                                            0.118194531961518,
                                            0.101930119817240,
                                            0.083276741576704,
                                            0.062672048334109,
                                            0.040601429800387,
                                            0.017614007139152 };

    private static final int n20 = 10;

	private int order_;
	private double[] x_;
	private double[] w_;
	private int n_;

}