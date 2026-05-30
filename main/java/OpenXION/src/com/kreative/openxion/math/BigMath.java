/*
 * Copyright &copy; 2009-2011 Rebecca G. Bettencourt / Kreative Software
 * <p>
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <a href="http://www.mozilla.org/MPL/">http://www.mozilla.org/MPL/</a>
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Alternatively, the contents of this file may be used under the terms
 * of the GNU Lesser General Public License (the "LGPL License"), in which
 * case the provisions of LGPL License are applicable instead of those
 * above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the LGPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the LGPL License.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.math;

import java.math.*;

/**
 * Methods for mathematical functions of BigDecimals
 * using power series, Newton's method, etc. (the most accurate method).
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class BigMath extends MathProcessor {
	public static final BigMath instance = new BigMath();
	
	private static final BigDecimal TWO = BigDecimal.valueOf(2.0);
	private static final BigDecimal THREE = BigDecimal.valueOf(3.0);
	private static final BigDecimal FOUR = BigDecimal.valueOf(4.0);
	
	/**
	 * Calculates cube root using Newton's method.
	 */
	public BigDecimal cbrt(BigDecimal n, MathContext mc) {
		double dg = Math.cbrt(n.doubleValue());
		if (Double.isNaN(dg)) return null;
		if (dg == 0.0) return BigDecimal.ZERO;
		if (Double.isInfinite(dg)) dg = Double.MAX_VALUE;
		BigDecimal g = BigDecimal.valueOf(dg);
		BigDecimal r = n.divide(g,mc).divide(g,mc).add(g,mc).add(g,mc).divide(THREE,mc);
		while (g.compareTo(r) != 0) {
			g = r;
			r = n.divide(g,mc).divide(g,mc).add(g,mc).add(g,mc).divide(THREE,mc);
		}
		return r;
	}
	
	/**
	 * Calculates cosine using Taylor series.
	 */
	public BigDecimal cos(BigDecimal n, MathContext mc) {
		n = n.abs();
		BigDecimal pi = pi(mc);
		BigDecimal twopi = TWO.multiply(pi,mc);
		BigDecimal pitwo = pi.divide(TWO,mc);
		BigDecimal threepitwo = pi.add(pitwo,mc);
		if (n.compareTo(BigDecimal.ZERO) == 0 || n.compareTo(twopi) == 0) {
			return BigDecimal.ONE;
		}
		else if (n.compareTo(pitwo) == 0 || n.compareTo(threepitwo) == 0) {
			return BigDecimal.ZERO;
		}
		else if (n.compareTo(pi) == 0) {
			return BigDecimal.ONE.negate();
		}
		else if (n.compareTo(twopi) > 0) {
			BigDecimal ntp = n.divideToIntegralValue(twopi);
			return cos(n.subtract((twopi.multiply(ntp,mc)),mc),mc);
		}
		else {
			BigDecimal i = BigDecimal.ZERO;
			BigDecimal f = BigDecimal.ONE;
			BigDecimal p = BigDecimal.ONE;
			BigDecimal a = BigDecimal.ZERO;
			BigDecimal b = BigDecimal.ONE;
			while (a.compareTo(b) != 0) {
				i = i.add(BigDecimal.ONE);
				f = f.divide(i,mc);
				i = i.add(BigDecimal.ONE);
				f = f.negate().divide(i,mc);
				p = p.multiply(n,mc).multiply(n,mc);
				a = b;
				b = b.add(p.multiply(f,mc),mc);
			}
			return b;
		}
	}
	
	/**
	 * Calculates hyperbolic cosine.
	 */
	public BigDecimal cosh(BigDecimal n, MathContext mc) {
		BigDecimal numer = exp(TWO.multiply(n,mc),mc);
		BigDecimal denom = TWO.multiply(exp(n,mc),mc);
		return numer.add(BigDecimal.ONE,mc).divide(denom,mc);
	}
	
	/**
	 * Calculates e using Taylor series.
	 */
	public BigDecimal e(MathContext mc) {
		BigDecimal a = BigDecimal.ZERO;
		BigDecimal b = BigDecimal.ONE;
		BigDecimal i = BigDecimal.ONE;
		BigDecimal f = BigDecimal.ONE;
		while (a.compareTo(b) != 0) {
			a = b;
			b = b.add(f,mc);
			i = i.add(BigDecimal.ONE);
			f = f.divide(i,mc);
		}
		return b;
	}
	
	/**
	 * Calculates e^x using Taylor series.
	 */
	public BigDecimal exp(BigDecimal n, MathContext mc) {
		int cmp = n.compareTo(BigDecimal.ZERO);
		if (cmp == 0) return BigDecimal.ONE;
		else if (cmp < 0) return BigDecimal.ONE.divide(exp(n.negate(),mc),mc);
		else {
			BigDecimal a = BigDecimal.ZERO;
			BigDecimal b = BigDecimal.ONE;
			BigDecimal i = BigDecimal.ONE;
			BigDecimal f = BigDecimal.ONE;
			BigDecimal p = n;
			while (a.compareTo(b) != 0) {
				a = b;
				b = b.add(p.multiply(f,mc),mc);
				i = i.add(BigDecimal.ONE);
				f = f.divide(i,mc);
				p = p.multiply(n,mc);
			}
			return b;
		}
	}
	
	/**
	 * Calculates e^x - 1.
	 */
	public BigDecimal expm1(BigDecimal n, MathContext mc) {
		return exp(n,mc).subtract(BigDecimal.ONE);
	}
	
	/**
	 * Calculates sqrt(a^2 + b^2).
	 */
	public BigDecimal hypot(BigDecimal x, BigDecimal y, MathContext mc) {
		return sqrt(x.multiply(x,mc).add(y.multiply(y,mc),mc),mc);
	}
	
	/**
	 * Calculates natural logarithm using Newton's method.
	 */
	public BigDecimal log(BigDecimal n, MathContext mc) {
		double dg = Math.log(n.doubleValue());
		if (Double.isNaN(dg)) return null;
		if (Double.isInfinite(dg) && dg < 0) return null;
		if (Double.isInfinite(dg) && dg > 0) dg = Double.MAX_VALUE;
		// additional digits of precision are needed here
		// to avoid the common problem of g and r alternating
		// between the same two equally good approximations
		// that differ only in the last few digits
		// (I'm sure there's a mathematical term for that)
		MathContext dmc = new MathContext(mc.getPrecision()+(int)Math.log(mc.getPrecision()), mc.getRoundingMode());
		BigDecimal g = BigDecimal.valueOf(dg);
		BigDecimal r = g.subtract(BigDecimal.ONE,dmc).add(n.divide(exp(g,dmc),dmc));
		while (g.subtract(r,mc).compareTo(BigDecimal.ZERO) != 0) {
			g = r;
			r = g.subtract(BigDecimal.ONE,dmc).add(n.divide(exp(g,dmc),dmc));
		}
		return r.round(mc);
	}
	
	/**
	 * Calculates natural logarithm of 1+n.
	 */
	public BigDecimal log1p(BigDecimal n, MathContext mc) {
		return log(n.add(BigDecimal.ONE),mc);
	}
	
	/**
	 * Calculates pi.
	 */
	public BigDecimal pi(MathContext mc) {
		BigDecimal a = BigDecimal.ONE;
		BigDecimal b = a.divide(sqrt(TWO,mc),mc);
		BigDecimal t = BigDecimal.valueOf(0.25);
		BigDecimal x = BigDecimal.ONE;
		BigDecimal y;
		while (a.compareTo(b) != 0) {
			y = a;
			a = a.add(b,mc).divide(TWO,mc);
			b = sqrt(b.multiply(y,mc),mc);
			t = t.subtract(x.multiply(y.subtract(a,mc).multiply(y.subtract(a,mc),mc),mc),mc);
			x = x.multiply(TWO,mc);
		}
		return a.add(b,mc).multiply(a.add(b,mc),mc).divide(t.multiply(FOUR,mc),mc);
	}
	
	/**
	 * Calculates sine using Taylor series.
	 */
	public BigDecimal sin(BigDecimal n, MathContext mc) {
		BigDecimal na = n.abs();
		BigDecimal pi = pi(mc);
		BigDecimal twopi = TWO.multiply(pi,mc);
		BigDecimal pitwo = pi.divide(TWO,mc);
		BigDecimal threepitwo = pi.add(pitwo,mc);
		if (na.compareTo(BigDecimal.ZERO) == 0 || na.compareTo(pi) == 0 || na.compareTo(twopi) == 0) {
			return BigDecimal.ZERO;
		}
		else if (n.compareTo(pitwo) == 0 || n.compareTo(threepitwo.negate()) == 0) {
			return BigDecimal.ONE;
		}
		else if (n.compareTo(pitwo.negate()) == 0 || n.compareTo(threepitwo) == 0) {
			return BigDecimal.ONE.negate();
		}
		if (n.abs().compareTo(twopi) > 0) {
			BigDecimal ntp = n.divideToIntegralValue(twopi);
			return sin(n.subtract((twopi.multiply(ntp,mc)),mc),mc);
		}
		else {
			BigDecimal i = BigDecimal.ONE;
			BigDecimal f = BigDecimal.ONE;
			BigDecimal p = n;
			BigDecimal a = BigDecimal.ZERO;
			BigDecimal b = n;
			while (a.compareTo(b) != 0) {
				i = i.add(BigDecimal.ONE);
				f = f.divide(i,mc);
				i = i.add(BigDecimal.ONE);
				f = f.negate().divide(i,mc);
				p = p.multiply(n,mc).multiply(n,mc);
				a = b;
				b = b.add(p.multiply(f,mc),mc);
			}
			return b;
		}
	}
	
	/**
	 * Calculates hyperbolic sine.
	 */
	public BigDecimal sinh(BigDecimal n, MathContext mc) {
		BigDecimal numer = exp(TWO.multiply(n,mc),mc);
		BigDecimal denom = TWO.multiply(exp(n,mc),mc);
		return numer.subtract(BigDecimal.ONE,mc).divide(denom,mc);
	}
	
	/**
	 * Calculates square root using Newton's method.
	 */
	public BigDecimal sqrt(BigDecimal n, MathContext mc) {
		double dg = Math.sqrt(n.doubleValue());
		if (Double.isNaN(dg)) return null;
		if (dg == 0.0) return BigDecimal.ZERO;
		if (Double.isInfinite(dg)) dg = Double.MAX_VALUE;
		BigDecimal g = BigDecimal.valueOf(dg);
		BigDecimal r = n.divide(g,mc).add(g,mc).divide(TWO,mc);
		while (g.compareTo(r) != 0) {
			g = r;
			r = n.divide(g,mc).add(g,mc).divide(TWO,mc);
		}
		return r;
	}
	
	/**
	 * Calculates tangent.
	 */
	public BigDecimal tan(BigDecimal n, MathContext mc) {
		BigDecimal na = n.abs();
		BigDecimal pi = pi(mc);
		BigDecimal twopi = TWO.multiply(pi,mc);
		BigDecimal pitwo = pi.divide(TWO,mc);
		BigDecimal threepitwo = pi.add(pitwo,mc);
		if (na.compareTo(BigDecimal.ZERO) == 0 || na.compareTo(pi) == 0 || na.compareTo(twopi) == 0) {
			return BigDecimal.ZERO;
		}
		else if (na.compareTo(pitwo) == 0 || na.compareTo(threepitwo) == 0) {
			return null;
		}
		else {
			BigDecimal s = sin(n,mc);
			BigDecimal c = cos(n,mc);
			if (s.compareTo(BigDecimal.ZERO) == 0) return null;
			else return s.divide(c,mc);
		}
	}
	
	/**
	 * Calculates hyperbolic tangent.
	 */
	public BigDecimal tanh(BigDecimal n, MathContext mc) {
		BigDecimal numer = exp(TWO.multiply(n,mc),mc);
		return numer.subtract(BigDecimal.ONE,mc).divide(numer.add(BigDecimal.ONE,mc),mc);
	}
	
	/*
	 * I have obviously given up at this point.
	 * It was arctangent that did me in.
	 */
	
	@Override
	public BigDecimal acos(BigDecimal arg, MathContext mc) {
		double r = java.lang.Math.acos(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal asin(BigDecimal arg, MathContext mc) {
		double r = java.lang.Math.asin(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal atan(BigDecimal arg, MathContext mc) {
		double r = java.lang.Math.atan(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal atan2(BigDecimal y, BigDecimal x, MathContext mc) {
		double r = java.lang.Math.atan2(y.doubleValue(), x.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal log10(BigDecimal arg, MathContext mc) {
		double r = java.lang.Math.log10(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal log2(BigDecimal arg, MathContext mc) {
		double r = java.lang.Math.log(arg.doubleValue()) / java.lang.Math.log(2);
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal pow(BigDecimal a, BigDecimal b, MathContext mc) {
		double r = java.lang.Math.pow(a.doubleValue(), b.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal erf(BigDecimal arg, MathContext mc) {
		double r = 1 - erfc(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal erfc(BigDecimal arg, MathContext mc) {
		double r = erfc(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal erfcx(BigDecimal arg, MathContext mc) {
		double r = erfcx(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	private static double erfc(double x) {
		if (Double.isNaN(x)) return Double.NaN;
		else if (x < 0) return 2 - erfc(-x);
		else if (x == 0) return 1;
		else if (Double.isInfinite(x)) return 0;
		else return Math.exp(-x*x) * erfcx(x);
	}

	private static double erfcx(double x) {
		if (Double.isNaN(x)) return Double.NaN;
		else if (x < 0) return Math.exp(x*x) * erfc(x);
		else if (x == 0) return 1;
		else if (Double.isInfinite(x)) return 0;
		else {
			double y = 0.56418958354775629 / (x + 2.06955023132914151);
			y *= (x*x + 2.71078540045147805*x + 5.80755613130301624) / (x*x + 3.47954057099518960*x + 12.06166887286239555);
			y *= (x*x + 3.47469513777439592*x + 12.07402036406381411) / (x*x + 3.72068443960225092*x + 8.44319781003968454);
			y *= (x*x + 4.00561509202259545*x + 9.30596659485887898) / (x*x + 3.90225704029924078*x + 6.36161630953880464);
			y *= (x*x + 5.16722705817812584*x + 9.12661617673673262) / (x*x + 4.03296893109262491*x + 5.13578530585681539);
			y *= (x*x + 5.95908795446633271*x + 9.19435612886969243) / (x*x + 4.11240942957450885*x + 4.48640329523408675);
			return y;
		}
	}

	private static final double GAMMA_G = 7;
	private static final double[] GAMMA_P = {
		0.99999999999980993227684700473478,
		676.520368121885098567009190444019,
		-1259.13921672240287047156078755283,
		771.3234287776530788486528258894,
		-176.61502916214059906584551354,
		12.507343278686904814458936853,
		-0.13857109526572011689554707,
		9.984369578019570859563e-6,
		1.50563273514931155834e-7
	};
	private static double[] gammaTZX(double z) {
		z--;
		double x = GAMMA_P[0];
		for (int i = 1; i < GAMMA_P.length; i++) x += GAMMA_P[i] / (z + i);
		return new double[] { z + GAMMA_G + 0.5, z + 0.5, x };
	}

	private static double gamma(double z) {
		if (Double.isNaN(z)) return z;
		if (z <= 0 && Math.ceil(z) == Math.floor(z)) return Double.NaN;
		if (z == 1 || z == 2) return 1;
		if (Double.isInfinite(z)) return z;
		if (z < 0.5) return Math.PI / (Math.sin(Math.PI * z) * gamma(1 - z));
		double[] tzx = gammaTZX(z);
		return Math.sqrt(Math.PI * 2) * Math.pow(tzx[0], tzx[1]) * Math.exp(-tzx[0]) * tzx[2];
	}

	private static double loggamma(double z) {
		if (Double.isNaN(z)) return z;
		if (z <= 0 && Math.ceil(z) == Math.floor(z)) return Double.NaN;
		if (z == 1 || z == 2) return 0;
		if (Double.isInfinite(z)) return z;
		if (z < 0.5) return Math.log(Math.abs(Math.PI / Math.sin(Math.PI * z))) - loggamma(1 - z);
		double[] tzx = gammaTZX(z);
		return Math.log(Math.sqrt(Math.PI * 2)) + tzx[1] * Math.log(tzx[0]) - tzx[0] + Math.log(tzx[2]);
	}

	@Override
	public BigDecimal gamma(BigDecimal arg, MathContext mc) {
		double r = gamma(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal loggamma(BigDecimal arg, MathContext mc) {
		double r = loggamma(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}
}
