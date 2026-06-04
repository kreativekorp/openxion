/*
 * Copyright &copy; 2009-2026 Rebecca G. Bettencourt / Kreative Software
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Methods for mathematical functions of arbitrary numbers
 * using power series, Newton's method, etc. (the most accurate method).
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class BigMath extends MathProcessor {
	public static final BigMath instance = new BigMath();
	
	private static final BigDecimal TWO = BigDecimal.valueOf(2);
	private static final BigDecimal THREE = BigDecimal.valueOf(3);
	private static final BigDecimal FOUR = BigDecimal.valueOf(4);
	private static final BigDecimal NEGATIVE_ONE = BigDecimal.valueOf(-1);
	
	private static final Map<MathContext,BigDecimal> eCache = new HashMap<MathContext,BigDecimal>();
	private static final Map<MathContext,BigDecimal> piCache = new HashMap<MathContext,BigDecimal>();
	
	/* Calculates e using Taylor series. */
	public BigDecimal e(MathContext mc) {
		BigDecimal c = eCache.get(mc);
		if (c != null) return c;
		BigDecimal a = BigDecimal.ZERO;
		BigDecimal b = BigDecimal.ONE;
		BigDecimal i = BigDecimal.ONE;
		BigDecimal f = BigDecimal.ONE;
		while (a.compareTo(b) != 0) {
			a = b;
			b = b.add(f, mc);
			i = i.add(BigDecimal.ONE);
			f = f.divide(i, mc);
		}
		eCache.put(mc, b);
		return b;
	}
	
	/* Calculates pi using... whatever this method is. */
	public BigDecimal pi(MathContext mc) {
		BigDecimal c = piCache.get(mc);
		if (c != null) return c;
		BigDecimal a = BigDecimal.ONE;
		BigDecimal b = BigDecimal.ONE.divide((BigDecimal)sqrt(TWO, mc), mc);
		BigDecimal t = BigDecimal.valueOf(0.25);
		BigDecimal x = BigDecimal.ONE;
		while (a.compareTo(b) != 0) {
			BigDecimal y = a;
			a = a.add(b, mc).divide(TWO, mc);
			b = (BigDecimal)sqrt(b.multiply(y, mc), mc);
			BigDecimal ya = y.subtract(a, mc);
			t = t.subtract(x.multiply(ya, mc).multiply(ya, mc), mc);
			x = x.multiply(TWO, mc);
		}
		BigDecimal ab = a.add(b, mc);
		c = ab.multiply(ab, mc).divide(t.multiply(FOUR, mc), mc);
		piCache.put(mc, c);
		return c;
	}
	
	/* Converts n to a BigDecimal. If n is not a finite number, returns null. */
	private BigDecimal toBigDecimal(Number n) {
		if (n == null) return null;
		if (n instanceof BigDecimal) return (BigDecimal)n;
		if (n instanceof BigInteger) return new BigDecimal((BigInteger)n);
		if (n instanceof Double || n instanceof Float) {
			if (Double.isNaN(n.doubleValue())) return null;
			if (Double.isInfinite(n.doubleValue())) return null;
			return BigDecimal.valueOf(n.doubleValue());
		}
		if (n instanceof Long || n instanceof Integer || n instanceof Short || n instanceof Byte) {
			return BigDecimal.valueOf(n.longValue());
		}
		throw new IllegalArgumentException("unknown subclass of java.lang.Number: " + n.getClass());
	}
	
	public Number acos(Number a, MathContext mc) { return FastMath.instance.acos(a, mc); }
	public Number asin(Number a, MathContext mc) { return FastMath.instance.asin(a, mc); }
	public Number atan(Number a, MathContext mc) { return FastMath.instance.atan(a, mc); }
	public Number atan2(Number y, Number x, MathContext mc) { return FastMath.instance.atan2(y, x, mc); }
	
	/* Calculates cube root using Newton's method. */
	public Number cbrt(Number a, MathContext mc) {
		BigDecimal n = toBigDecimal(a);
		if (n == null) return Math.cbrt(a.doubleValue());
		int signum = n.signum(); // odd function
		if (signum < 0) n = n.negate();
		if (signum == 0) return BigDecimal.ZERO;
		BigDecimal g = BigDecimal.valueOf(Math.cbrt(n.doubleValue()));
		BigDecimal r = n.divide(g, mc).divide(g, mc).add(g, mc).add(g, mc).divide(THREE, mc);
		while (g.compareTo(r) != 0) {
			g = r;
			r = n.divide(g, mc).divide(g, mc).add(g, mc).add(g, mc).divide(THREE, mc);
		}
		return (signum < 0) ? r.negate() : r;
	}
	
	/* Calculates cosine using Taylor series. */
	public Number cos(Number x, MathContext mc) {
		BigDecimal n = toBigDecimal(x);
		if (n == null) return Math.cos(x.doubleValue());
		n = n.abs(); // even function
		BigDecimal pi = pi(mc);
		BigDecimal twopi = pi.multiply(TWO, mc);
		if (n.compareTo(twopi) > 0) {
			BigDecimal ntp = n.divideToIntegralValue(twopi);
			n = n.subtract(twopi.multiply(ntp, mc), mc);
		}
		if (n.signum() == 0) return BigDecimal.ONE;
		if (n.compareTo(pi) == 0) return NEGATIVE_ONE;
		if (n.compareTo(twopi) == 0) return BigDecimal.ONE;
		BigDecimal halfpi = pi.divide(TWO, mc);
		BigDecimal threehalfpi = pi.add(halfpi, mc);
		if (n.compareTo(halfpi) == 0) return BigDecimal.ZERO;
		if (n.compareTo(threehalfpi) == 0) return BigDecimal.ZERO;
		BigDecimal i = BigDecimal.ZERO;
		BigDecimal f = BigDecimal.ONE;
		BigDecimal p = BigDecimal.ONE;
		BigDecimal a = BigDecimal.ZERO;
		BigDecimal b = BigDecimal.ONE;
		while (a.compareTo(b) != 0) {
			i = i.add(BigDecimal.ONE);
			f = f.divide(i, mc);
			i = i.add(BigDecimal.ONE);
			f = f.negate().divide(i, mc);
			p = p.multiply(n, mc).multiply(n, mc);
			a = b;
			b = b.add(p.multiply(f, mc), mc);
		}
		return b;
	}
	
	public Number cosh(Number a, MathContext mc) {
		BigDecimal n = toBigDecimal(a);
		if (n == null) return Math.cosh(a.doubleValue());
		BigDecimal numer = (BigDecimal)exp(TWO.multiply(n, mc), mc);
		BigDecimal denom = TWO.multiply((BigDecimal)exp(n, mc), mc);
		return numer.add(BigDecimal.ONE, mc).divide(denom, mc);
	}
	
	public Number erf(Number a, MathContext mc) { return FastMath.instance.erf(a, mc); }
	public Number erfc(Number a, MathContext mc) { return FastMath.instance.erfc(a, mc); }
	public Number erfcx(Number a, MathContext mc) { return FastMath.instance.erfcx(a, mc); }
	
	/* Calculates e^x using Taylor series. */
	public Number exp(Number x, MathContext mc) {
		BigDecimal n = toBigDecimal(x);
		if (n == null) return Math.exp(x.doubleValue());
		int signum = n.signum();
		if (signum < 0) n = n.negate();
		if (signum == 0) return BigDecimal.ONE;
		BigDecimal a = BigDecimal.ZERO;
		BigDecimal b = BigDecimal.ONE;
		BigDecimal i = BigDecimal.ONE;
		BigDecimal f = BigDecimal.ONE;
		BigDecimal p = n;
		while (a.compareTo(b) != 0) {
			a = b;
			b = b.add(p.multiply(f, mc), mc);
			i = i.add(BigDecimal.ONE);
			f = f.divide(i, mc);
			p = p.multiply(n, mc);
		}
		return (signum < 0) ? BigDecimal.ONE.divide(b, mc) : b;
	}
	
	public Number expm1(Number a, MathContext mc) {
		BigDecimal n = toBigDecimal(a);
		if (n == null) return Math.expm1(a.doubleValue());
		return ((BigDecimal)exp(n, mc)).subtract(BigDecimal.ONE, mc);
	}
	
	public Number gamma(Number a, MathContext mc) { return FastMath.instance.gamma(a, mc); }
	
	public Number hypot(Number y, Number x, MathContext mc) {
		BigDecimal n = toBigDecimal(y);
		BigDecimal m = toBigDecimal(x);
		if (n == null || m == null) return Math.hypot(y.doubleValue(), x.doubleValue());
		return sqrt(n.multiply(n, mc).add(m.multiply(m, mc), mc), mc);
	}
	
	/* Calculates natural logarithm using Newton's method. */
	public Number log(Number a, MathContext mc) {
		BigDecimal n = toBigDecimal(a);
		if (n == null || n.signum() <= 0) return Math.log(a.doubleValue());
		// Use additional precision to hide oscillations.
		int d = mc.getPrecision() + (int)Math.ceil(Math.sqrt(mc.getPrecision())) + 1;
		MathContext dmc = new MathContext(d, mc.getRoundingMode());
		BigDecimal g = BigDecimal.valueOf(Math.log(n.doubleValue()));
		BigDecimal r = g.subtract(BigDecimal.ONE, dmc).add(n.divide((BigDecimal)exp(g, dmc), dmc), dmc);
		while (g.round(mc).compareTo(r.round(mc)) != 0) {
			g = r;
			r = g.subtract(BigDecimal.ONE, dmc).add(n.divide((BigDecimal)exp(g, dmc), dmc), dmc);
		}
		return r.round(mc);
	}
	
	public Number log10(Number a, MathContext mc) {
		BigDecimal n = toBigDecimal(a);
		if (n == null || n.signum() <= 0) return Math.log(a.doubleValue());
		return ((BigDecimal)log(n, mc)).divide((BigDecimal)log(BigDecimal.TEN, mc), mc);
	}
	
	public Number log1p(Number a, MathContext mc) {
		BigDecimal n = toBigDecimal(a);
		if (n == null) return Math.log1p(a.doubleValue());
		return log(n.add(BigDecimal.ONE, mc), mc);
	}
	
	public Number log2(Number a, MathContext mc) {
		BigDecimal n = toBigDecimal(a);
		if (n == null || n.signum() <= 0) return Math.log(a.doubleValue());
		return ((BigDecimal)log(n, mc)).divide((BigDecimal)log(TWO, mc), mc);
	}
	
	public Number loggamma(Number a, MathContext mc) { return FastMath.instance.loggamma(a, mc); }
	public Number pow(Number b, Number a, MathContext mc) { return FastMath.instance.pow(b, a, mc); }
	
	/* Calculates sine using Taylor series. */
	public Number sin(Number x, MathContext mc) {
		BigDecimal n = toBigDecimal(x);
		if (n == null) return Math.sin(x.doubleValue());
		int signum = n.signum(); // odd function
		if (signum < 0) n = n.negate();
		if (signum == 0) return BigDecimal.ZERO;
		BigDecimal pi = pi(mc);
		BigDecimal twopi = pi.multiply(TWO, mc);
		if (n.compareTo(twopi) > 0) {
			BigDecimal ntp = n.divideToIntegralValue(twopi);
			n = n.subtract(twopi.multiply(ntp, mc), mc);
		}
		if (n.signum() == 0) return BigDecimal.ZERO;
		if (n.compareTo(pi) == 0) return BigDecimal.ZERO;
		if (n.compareTo(twopi) == 0) return BigDecimal.ZERO;
		BigDecimal halfpi = pi.divide(TWO, mc);
		BigDecimal threehalfpi = pi.add(halfpi, mc);
		if (n.compareTo(halfpi) == 0) return (signum < 0) ? NEGATIVE_ONE : BigDecimal.ONE;
		if (n.compareTo(threehalfpi) == 0) return (signum < 0) ? BigDecimal.ONE : NEGATIVE_ONE;
		BigDecimal i = BigDecimal.ONE;
		BigDecimal f = BigDecimal.ONE;
		BigDecimal p = n;
		BigDecimal a = BigDecimal.ZERO;
		BigDecimal b = n;
		while (a.compareTo(b) != 0) {
			i = i.add(BigDecimal.ONE);
			f = f.divide(i, mc);
			i = i.add(BigDecimal.ONE);
			f = f.negate().divide(i, mc);
			p = p.multiply(n, mc).multiply(n, mc);
			a = b;
			b = b.add(p.multiply(f, mc), mc);
		}
		return (signum < 0) ? b.negate() : b;
	}
	
	public Number sinh(Number a, MathContext mc) {
		BigDecimal n = toBigDecimal(a);
		if (n == null) return Math.sinh(a.doubleValue());
		BigDecimal numer = (BigDecimal)exp(TWO.multiply(n, mc), mc);
		BigDecimal denom = TWO.multiply((BigDecimal)exp(n, mc), mc);
		return numer.subtract(BigDecimal.ONE, mc).divide(denom, mc);
	}
	
	/* Calculates square root using Newton's method. */
	public Number sqrt(Number a, MathContext mc) {
		BigDecimal n = toBigDecimal(a);
		if (n == null) return Math.sqrt(a.doubleValue());
		int signum = n.signum();
		if (signum < 0) return Double.NaN;
		if (signum == 0) return BigDecimal.ZERO;
		BigDecimal g = BigDecimal.valueOf(Math.sqrt(a.doubleValue()));
		BigDecimal r = n.divide(g, mc).add(g, mc).divide(TWO, mc);
		while (g.compareTo(r) != 0) {
			g = r;
			r = n.divide(g, mc).add(g, mc).divide(TWO, mc);
		}
		return r;
	}
	
	public Number tan(Number a, MathContext mc) {
		BigDecimal n = toBigDecimal(a);
		if (n == null) return Math.tan(a.doubleValue());
		BigDecimal s = (BigDecimal)sin(n, mc);
		BigDecimal c = (BigDecimal)cos(n, mc);
		if (s.signum() == 0) return BigDecimal.ZERO;
		if (c.signum() == 0) return Double.NaN;
		return s.divide(c, mc);
	}
	
	public Number tanh(Number a, MathContext mc) {
		BigDecimal n = toBigDecimal(a);
		if (n == null) return Math.tanh(a.doubleValue());
		BigDecimal numer = (BigDecimal)exp(TWO.multiply(n, mc), mc);
		return numer.subtract(BigDecimal.ONE, mc).divide(numer.add(BigDecimal.ONE, mc), mc);
	}
}
