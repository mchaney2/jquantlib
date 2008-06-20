package cern.jet.math;

import cern.colt.function.DComplexDComplexDComplexFunction;
import cern.colt.function.DComplexDComplexFunction;
import cern.colt.function.DComplexDComplexRealProcedure;
import cern.colt.function.DComplexDComplexRealRealFunction;
import cern.colt.function.DComplexProcedure;
import cern.colt.function.DComplexRealDComplexFunction;
import cern.colt.function.DComplexRealFunction;
import cern.colt.function.RealDComplexDComplexFunction;
import cern.colt.function.RealDComplexFunction;

/**
 * Complex function objects to be passed to generic methods.
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 */
public class DComplexFunctions {

	public static final DComplexFunctions functions = new DComplexFunctions();

	/***************************************************************************
	 * <H3>Unary functions</H3>
	 **************************************************************************/

	public static final DComplexRealFunction abs = new DComplexRealFunction() {
		public final double apply(double[] x) {
			double absX = Math.abs(x[0]);
			double absY = Math.abs(x[1]);
			if (absX == 0.0 && absY == 0.0) {
				return 0.0;
			} else if (absX >= absY) {
				double d = x[1] / x[0];
				return absX * Math.sqrt(1.0 + d * d);
			} else {
				double d = x[0] / x[1];
				return absY * Math.sqrt(1.0 + d * d);
			}
		}
	};

	public static final DComplexDComplexFunction acos = new DComplexDComplexFunction() {
		public final double[] apply(double[] x) {
			double[] z = new double[2];

			double re, im;

			re = 1.0 - ((x[0] * x[0]) - (x[1] * x[1]));
			im = -((x[0] * x[1]) + (x[1] * x[0]));

			z[0] = re;
			z[1] = im;
			z = DComplex.sqrt(z);

			re = -z[1];
			im = z[0];

			z[0] = x[0] + re;
			z[1] = x[1] + im;

			re = Math.log(DComplex.abs(z));
			im = Math.atan2(z[1], z[0]);

			z[0] = im;
			z[1] = -re;
			return z;
		}
	};

	public static final DComplexRealFunction arg = new DComplexRealFunction() {
		public final double apply(double[] x) {
			return Math.atan2(x[1], x[0]);
		}
	};

	public static final DComplexDComplexFunction asin = new DComplexDComplexFunction() {
		public final double[] apply(double[] x) {
			double[] z = new double[2];

			double re, im;

			re = 1.0 - ((x[0] * x[0]) - (x[1] * x[1]));
			im = -((x[0] * x[1]) + (x[1] * x[0]));

			z[0] = re;
			z[1] = im;
			z = DComplex.sqrt(z);

			re = -z[1];
			im = z[0];

			z[0] = z[0] + re;
			z[1] = z[1] + im;

			re = Math.log(DComplex.abs(z));
			im = Math.atan2(z[1], z[0]);

			z[0] = im;
			z[1] = -re;
			return z;
		}
	};

	public static final DComplexDComplexFunction atan = new DComplexDComplexFunction() {
		public final double[] apply(double[] x) {
			double[] z = new double[2];

			double re, im;

			z[0] = -x[0];
			z[1] = 1.0 - x[1];

			re = x[0];
			im = 1.0 + x[1];

			z = DComplex.div(z, re, im);

			re = Math.log(DComplex.abs(z));
			im = Math.atan2(z[1], z[0]);

			z[0] = 0.5 * im;
			z[1] = -0.5 * re;

			return z;
		}
	};

	public static final DComplexDComplexFunction conj = new DComplexDComplexFunction() {
		public final double[] apply(double[] x) {
			double[] z = new double[2];
			z[0] = x[0];
			z[1] = -x[1];
			return z;
		}
	};

	public static final DComplexDComplexFunction cos = new DComplexDComplexFunction() {
		public final double[] apply(double[] x) {
			double[] z = new double[2];

			double re1, im1, re2, im2;
			double scalar;
			double iz_re, iz_im;

			iz_re = -x[1];
			iz_im = x[0];

			scalar = Math.exp(iz_re);
			re1 = scalar * Math.cos(iz_im);
			im1 = scalar * Math.sin(iz_im);

			scalar = Math.exp(-iz_re);
			re2 = scalar * Math.cos(-iz_im);
			im2 = scalar * Math.sin(-iz_im);

			re1 = re1 + re2;
			im1 = im1 + im2;

			z[0] = 0.5 * re1;
			z[1] = 0.5 * im1;

			return z;
		}
	};

	public static final DComplexDComplexFunction exp = new DComplexDComplexFunction() {
		public final double[] apply(double[] x) {
			double[] z = new double[2];
			double scalar = Math.exp(x[0]);
			z[0] = scalar * Math.cos(x[1]);
			z[1] = scalar * Math.sin(x[1]);
			return z;
		}
	};

	public static final DComplexDComplexFunction identity = new DComplexDComplexFunction() {
		public final double[] apply(double[] x) {
			return x;
		}
	};

	public static final DComplexDComplexFunction inv = new DComplexDComplexFunction() {
		public final double[] apply(double[] x) {
			double[] z = new double[2];
			if (x[1] != 0.0) {
				double scalar;
				if (Math.abs(x[0]) >= Math.abs(z[1])) {
					scalar = 1.0 / (x[0] + x[1] * (x[1] / x[0]));
					z[0] = scalar;
					z[1] = scalar * (-x[1] / x[0]);
				} else {
					scalar = 1.0 / (x[0] * (x[0] / x[1]) + x[1]);
					z[0] = scalar * (x[0] / x[1]);
					z[1] = -scalar;
				}
			} else {
				z[0] = 1 / x[0];
				z[1] = 0;
			}
			return z;
		}
	};

	public static final DComplexDComplexFunction log = new DComplexDComplexFunction() {
		public final double[] apply(double[] x) {
			double[] z = new double[2];
			z[0] = Math.log(DComplex.abs(x));
			z[1] = DComplex.arg(x);
			return z;
		}
	};

	public static final DComplexDComplexFunction neg = new DComplexDComplexFunction() {
		public final double[] apply(double[] x) {
			return new double[] { -x[0], -x[1] };
		}
	};

	public static final DComplexDComplexFunction sin = new DComplexDComplexFunction() {
		public final double[] apply(double[] x) {
			double[] z = new double[2];
			double re1, im1, re2, im2;
			double scalar;
			double iz_re, iz_im;

			iz_re = -x[1];
			iz_im = x[0];

			scalar = Math.exp(iz_re);
			re1 = scalar * Math.cos(iz_im);
			im1 = scalar * Math.sin(iz_im);

			scalar = Math.exp(-iz_re);
			re2 = scalar * Math.cos(-iz_im);
			im2 = scalar * Math.sin(-iz_im);

			re1 = re1 - re2;
			im1 = im1 - im2;

			z[0] = 0.5 * im1;
			z[1] = -0.5 * re1;

			return z;
		}
	};

	public static final DComplexDComplexFunction sqrt = new DComplexDComplexFunction() {
		public final double[] apply(double[] x) {
			double[] z = new double[2];
			double absx = DComplex.abs(x);
			double tmp;
			if (absx > 0.0) {
				if (x[0] > 0.0) {
					tmp = Math.sqrt(0.5 * (absx + x[0]));
					z[0] = tmp;
					z[1] = 0.5 * (x[1] / tmp);
				} else {
					tmp = Math.sqrt(0.5 * (absx - x[0]));
					if (x[1] < 0.0) {
						tmp = -tmp;
					}
					z[0] = 0.5 * (x[1] / tmp);
					z[1] = tmp;
				}
			} else {
				z[0] = 0.0;
				z[1] = 0.0;
			}
			return z;
		}
	};

	public static final DComplexDComplexFunction square = new DComplexDComplexFunction() {
		public final double[] apply(double[] x) {
			double[] z = new double[2];
			z[0] = x[0] * x[0] - x[1] * x[1];
			z[1] = x[1] * x[0] + x[0] * x[1];
			return z;
		}
	};

	public static final DComplexDComplexFunction tan = new DComplexDComplexFunction() {
		public final double[] apply(double[] x) {
			double[] z = new double[2];
			double scalar;
			double iz_re, iz_im;
			double re1, im1, re2, im2, re3, im3;
			double cs_re, cs_im;

			iz_re = -x[1];
			iz_im = x[0];

			scalar = Math.exp(iz_re);
			re1 = scalar * Math.cos(iz_im);
			im1 = scalar * Math.sin(iz_im);

			scalar = Math.exp(-iz_re);
			re2 = scalar * Math.cos(-iz_im);
			im2 = scalar * Math.sin(-iz_im);

			re3 = re1 - re2;
			im3 = im1 - im2;

			z[0] = 0.5 * im3;
			z[1] = -0.5 * re3;

			re3 = re1 + re2;
			im3 = im1 + im2;

			cs_re = 0.5 * re3;
			cs_im = 0.5 * im3;

			z = DComplex.div(z, cs_re, cs_im);

			return z;
		}
	};

	/***************************************************************************
	 * <H3>Binary functions</H3>
	 **************************************************************************/

	public static final DComplexDComplexDComplexFunction div = new DComplexDComplexDComplexFunction() {
		public final double[] apply(double[] x, double[] y) {
			double re = y[0];
			double im = y[1];

			double[] z = new double[2];
			double scalar;

			if (Math.abs(re) >= Math.abs(im)) {
				scalar = 1.0 / (re + im * (im / re));

				z[0] = scalar * (x[0] + x[1] * (im / re));
				z[1] = scalar * (x[1] - x[0] * (im / re));

			} else {
				scalar = 1.0 / (re * (re / im) + im);

				z[0] = scalar * (x[0] * (re / im) + x[1]);
				z[1] = scalar * (x[1] * (re / im) - x[0]);
			}

			return z;
		}
	};

	public static final DComplexDComplexRealRealFunction equals = new DComplexDComplexRealRealFunction() {
		public final double apply(double[] x, double[] y, double tol) {
			if (DComplex.abs(x[0] - y[0], x[1] - y[1]) <= Math.abs(tol)) {
				return 1;
			} else {
				return 0;
			}
		}
	};

	public static final DComplexDComplexRealProcedure isEqual = new DComplexDComplexRealProcedure() {
		public final boolean apply(double[] x, double[] y, double tol) {
			if (DComplex.abs(x[0] - y[0], x[1] - y[1]) <= Math.abs(tol)) {
				return true;
			} else {
				return false;
			}
		}
	};

	public static final DComplexDComplexDComplexFunction minus = new DComplexDComplexDComplexFunction() {
		public final double[] apply(double[] x, double[] y) {
			double[] z = new double[2];
			z[0] = x[0] - y[0];
			z[1] = x[1] - y[1];
			return z;
		}
	};

	public static final DComplexDComplexDComplexFunction mult = new DComplexDComplexDComplexFunction() {
		public final double[] apply(double[] x, double[] y) {
			double[] z = new double[2];
			z[0] = x[0] * y[0] - x[1] * y[1];
			z[1] = x[1] * y[0] + x[0] * y[1];
			return z;
		}
	};

	public static final DComplexDComplexDComplexFunction multConjFirst = new DComplexDComplexDComplexFunction() {
		public final double[] apply(double[] x, double[] y) {
			double[] z = new double[2];
			z[0] = x[0] * y[0] + x[1] * y[1];
			z[1] = -x[1] * y[0] + x[0] * y[1];
			return z;
		}
	};

	public static final DComplexDComplexDComplexFunction multConjSecond = new DComplexDComplexDComplexFunction() {
		public final double[] apply(double[] x, double[] y) {
			double[] z = new double[2];
			z[0] = x[0] * y[0] + x[1] * y[1];
			z[1] = x[1] * y[0] - x[0] * y[1];
			return z;
		}
	};

	public static final DComplexDComplexDComplexFunction plus = new DComplexDComplexDComplexFunction() {
		public final double[] apply(double[] x, double[] y) {
			double[] z = new double[2];
			z[0] = x[0] + y[0];
			z[1] = x[1] + y[1];
			return z;
		}
	};

	public static final DComplexRealDComplexFunction pow1 = new DComplexRealDComplexFunction() {
		public final double[] apply(double[] x, double y) {
			double[] z = new double[2];
			double re = y * Math.log(DComplex.abs(x));
			double im = y * DComplex.arg(x);
			double scalar = Math.exp(re);
			z[0] = scalar * Math.cos(im);
			z[1] = scalar * Math.sin(im);
			return z;
		}
	};

	public static final RealDComplexDComplexFunction pow2 = new RealDComplexDComplexFunction() {
		public final double[] apply(double x, double[] y) {
			double[] z = new double[2];
			double re = Math.log(Math.abs(x));
			double im = Math.atan2(0.0, x);

			double re2 = (re * y[0]) - (im * y[1]);
			double im2 = (re * y[1]) + (im * y[0]);

			double scalar = Math.exp(re2);

			z[0] = scalar * Math.cos(im2);
			z[1] = scalar * Math.sin(im2);
			return z;
		}
	};

	public static final DComplexDComplexDComplexFunction pow3 = new DComplexDComplexDComplexFunction() {
		public final double[] apply(double[] x, double[] y) {
			double[] z = new double[2];
			double re = Math.log(DComplex.abs(x));
			double im = DComplex.arg(x);

			double re2 = (re * y[0]) - (im * y[1]);
			double im2 = (re * y[1]) + (im * y[0]);

			double scalar = Math.exp(re2);

			z[0] = scalar * Math.cos(im2);
			z[1] = scalar * Math.sin(im2);
			return z;
		}
	};

	public static DComplexDComplexFunction bindArg1(final DComplexDComplexDComplexFunction function, final double[] c) {
		return new DComplexDComplexFunction() {
			public final double[] apply(double[] var) {
				return function.apply(c, var);
			}
		};
	}

	public static DComplexDComplexFunction bindArg2(final DComplexDComplexDComplexFunction function, final double[] c) {
		return new DComplexDComplexFunction() {
			public final double[] apply(double[] var) {
				return function.apply(var, c);
			}
		};
	}

	public static DComplexDComplexDComplexFunction chain(final DComplexDComplexDComplexFunction f, final DComplexDComplexFunction g, final DComplexDComplexFunction h) {
		return new DComplexDComplexDComplexFunction() {
			public final double[] apply(double[] x, double[] y) {
				return f.apply(g.apply(x), h.apply(y));
			}
		};
	}

	public static DComplexDComplexDComplexFunction chain(final DComplexDComplexFunction g, final DComplexDComplexDComplexFunction h) {
		return new DComplexDComplexDComplexFunction() {
			public final double[] apply(double[] x, double[] y) {
				return g.apply(h.apply(x, y));
			}
		};
	}

	public static DComplexDComplexFunction chain(final DComplexDComplexFunction g, final DComplexDComplexFunction h) {
		return new DComplexDComplexFunction() {
			public final double[] apply(double[] x) {
				return g.apply(h.apply(x));
			}
		};
	}

	public static DComplexDComplexFunction constant(final double[] c) {
		return new DComplexDComplexFunction() {
			public final double[] apply(double[] x) {
				return c;
			}
		};
	}

	public static DComplexDComplexFunction div(final double[] b) {
		return mult(DComplex.inv(b));
	}

	public static DComplexDComplexFunction div(final double b) {
		double[] tmp = new double[] { b, 0 };
		return mult(DComplex.inv(tmp));
	}

	public static DComplexRealFunction equals(final double[] y) {
		return new DComplexRealFunction() {
			public final double apply(double[] x) {
				if (x[0] == y[0] && x[1] == y[1]) {
					return 1;
				} else {
					return 0;
				}
			}
		};
	}

	public static DComplexProcedure isEqual(final double[] y) {
		return new DComplexProcedure() {
			public final boolean apply(double[] x) {
				if (x[0] == y[0] && x[1] == y[1]) {
					return true;
				} else {
					return false;
				}
			}
		};
	}

	public static DComplexDComplexFunction minus(final double[] x) {
		double[] negb = new double[2];
		negb[0] = -x[0];
		negb[1] = -x[1];
		return plus(negb);
	}

	public static DComplexDComplexDComplexFunction minusMult(final double[] constant) {
		double[] negconstant = new double[2];
		negconstant[0] = -constant[0];
		negconstant[1] = -constant[1];
		return plusMult(negconstant);
	}

	public static DComplexDComplexFunction mult(final double[] x) {
		return new DComplexMult(x);
	}

	public static DComplexDComplexFunction mult(final double x) {
		return new DComplexMult(new double[] { x, 0 });
	}

	public static DComplexDComplexFunction plus(final double[] y) {
		return new DComplexDComplexFunction() {
			public final double[] apply(double[] x) {
				double[] z = new double[2];
				z[0] = x[0] + y[0];
				z[1] = x[1] + y[1];
				return z;
			}
		};
	}

	public static DComplexDComplexDComplexFunction plusMult(double[] constant) {
		return new DComplexPlusMult(constant);
	}

	public static DComplexDComplexFunction pow1(final double y) {
		return new DComplexDComplexFunction() {
			public final double[] apply(double[] x) {
				double[] z = new double[2];
				double re = y * Math.log(DComplex.abs(x));
				double im = y * DComplex.arg(x);
				double scalar = Math.exp(re);
				z[0] = scalar * Math.cos(im);
				z[1] = scalar * Math.sin(im);
				return z;
			}
		};
	}

	public static RealDComplexFunction pow2(final double[] y) {
		return new RealDComplexFunction() {
			public final double[] apply(double x) {
				double[] z = new double[2];
				double re = Math.log(Math.abs(x));
				double im = Math.atan2(0.0, x);

				double re2 = (re * y[0]) - (im * y[1]);
				double im2 = (re * y[1]) + (im * y[0]);

				double scalar = Math.exp(re2);

				z[0] = scalar * Math.cos(im2);
				z[1] = scalar * Math.sin(im2);
				return z;
			}
		};
	}

	public static DComplexDComplexFunction pow3(final double[] y) {
		return new DComplexDComplexFunction() {
			public final double[] apply(double[] x) {
				double[] z = new double[2];
				double re = Math.log(DComplex.abs(x));
				double im = DComplex.arg(x);

				double re2 = (re * y[0]) - (im * y[1]);
				double im2 = (re * y[1]) + (im * y[0]);

				double scalar = Math.exp(re2);

				z[0] = scalar * Math.cos(im2);
				z[1] = scalar * Math.sin(im2);
				return z;
			}
		};
	}

	public static DComplexDComplexFunction random() {
		return new RandomComplexFunction();
	}

	private static class RandomComplexFunction implements DComplexDComplexFunction {

		public double[] apply(double[] argument) {
			return new double[] { Math.random(), Math.random() };
		}

	}

	public static DComplexDComplexDComplexFunction swapArgs(final DComplexDComplexDComplexFunction function) {
		return new DComplexDComplexDComplexFunction() {
			public final double[] apply(double[] x, double[] y) {
				return function.apply(y, x);
			}
		};
	}
}
