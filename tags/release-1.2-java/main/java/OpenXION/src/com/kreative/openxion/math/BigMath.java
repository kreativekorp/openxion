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
	
	private static final int G = 7;
	private static final double[] P = new double[] {
		0.99999999999980993, 676.5203681218851, -1259.1392167224028,
		771.32342877765313, -176.61502916214059, 12.507343278686905,
		-0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7
	};
	private static double gamma(double z) {
		if (Double.isNaN(z)) return Double.NaN;
		else if (Double.isInfinite(z)) return (z > 0.0) ? Double.POSITIVE_INFINITY : Double.NaN;
		else if (z < 0.0 && Math.round(z) == z) return Double.NaN;
		else if (z == 0.0) return Double.POSITIVE_INFINITY;
		else if (z == 1.0 || z == 2.0) return 1.0;
		else if (z < 0.5) {
			return Math.PI / (Math.sin(Math.PI * z) * gamma(1.0-z));
		}
		else {
			z -= 1.0;
			double x = P[0];
			for (int i = 1; i < G+2; i++) {
				x += P[i]/(z+i);
			}
			double t = z + G + 0.5;
			return Math.sqrt(2*Math.PI) * Math.pow(t, z+0.5) * Math.exp(-t) * x;
		}
	}

	@Override
	public BigDecimal gamma(BigDecimal arg, MathContext mc) {
		double r = gamma(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal loggamma(BigDecimal arg, MathContext mc) {
		double r = Math.log(gamma(arg.doubleValue()));
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}
}
