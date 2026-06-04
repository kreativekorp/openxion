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

import java.math.MathContext;
import com.kreative.openxion.xom.inst.XOMComplex;
import com.kreative.openxion.xom.inst.XOMNumber;

/**
 * Methods for mathematical operations on and functions of XOMNumbers.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XOMNumberMath {
	private XOMNumberMath(){}
	
	public static XOMNumber fma(XOMNumber a, XOMNumber b, XOMNumber c, MathContext mc) {
		return a.multiply(b, mc).add(c, mc);
	}
	
	public static XOMNumber toDegrees(XOMNumber n, MathContext mc, MathProcessor mp) {
		return n.multiply(XOMNumber.ONE_EIGHTY, mc).divide(new XOMNumber(mp.pi(mc)), mc);
	}
	
	public static XOMNumber toRadians(XOMNumber n, MathContext mc, MathProcessor mp) {
		return n.multiply(new XOMNumber(mp.pi(mc)), mc).divide(XOMNumber.ONE_EIGHTY, mc);
	}
	
	public static XOMNumber pow(XOMNumber a, XOMNumber b, MathContext mc, MathProcessor mp) {
		if (a.isNaN() || b.isNaN()) return XOMNumber.NaN;
		if (b.isZero()) return (a.isFinite() && !a.isZero()) ? XOMNumber.ONE : XOMNumber.NaN;
		if (b.equals(XOMNumber.ONE)) return a;
		if (b.isInfinite()) {
			if (a.isNeg() || a.equals(XOMNumber.ONE)) return XOMNumber.NaN;
			if (a.compareTo(XOMNumber.ONE) != b.compareTo(XOMNumber.ZERO)) return XOMNumber.ZERO;
			return XOMNumber.POSITIVE_INFINITY;
		}
		return new XOMNumber(mp.pow(a.toNumber(), b.toNumber(), mc));
	}
	
	public static XOMNumber annuity(XOMNumber a, XOMNumber b, MathContext mc, MathProcessor mp) {
		if (a.isZero() || b.isZero()) return b;
		XOMNumber opa = XOMNumber.ONE.add(a, mc);
		XOMNumber opatnb = pow(opa, b.negate(), mc, mp);
		XOMNumber omopatnb = XOMNumber.ONE.subtract(opatnb, mc);
		return omopatnb.divide(a, mc);
	}
	
	public static XOMNumber compound(XOMNumber a, XOMNumber b, MathContext mc, MathProcessor mp) {
		if (a.isZero() || b.isZero()) return XOMNumber.ONE;
		XOMNumber opa = XOMNumber.ONE.add(a, mc);
		return pow(opa, b, mc, mp);
	}
	
	public static XOMNumber sqrt(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.sqrt(n.toNumber(), mc));
	}
	
	public static XOMNumber cbrt(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.cbrt(n.toNumber(), mc));
	}
	
	public static XOMNumber qtrt(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.sqrt(mp.sqrt(n.toNumber(), mc), mc));
	}
	
	public static XOMNumber twrt(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.sqrt(mp.sqrt(mp.cbrt(n.toNumber(), mc), mc), mc));
	}
	
	public static XOMNumber hypot(XOMNumber y, XOMNumber x, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.hypot(y.toNumber(), x.toNumber(), mc));
	}
	
	public static XOMNumber agm(XOMNumber a, XOMNumber b, MathContext mc, MathProcessor mp) {
		while (true) {
			if (a.isNaN() || b.isNaN()) return XOMNumber.NaN;
			if (a.equals(b)) return a;
			XOMNumber c = a.add(b, mc).divide(XOMNumber.TWO, mc);
			XOMNumber d = sqrt(a.multiply(b, mc), mc, mp);
			a = c; b = d;
		}
	}
	
	public static XOMNumber exp(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.exp(n.toNumber(), mc));
	}
	
	public static XOMNumber expm1(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.expm1(n.toNumber(), mc));
	}
	
	public static XOMNumber exp2(XOMNumber n, MathContext mc, MathProcessor mp) {
		return pow(XOMNumber.TWO, n, mc, mp);
	}
	
	public static XOMNumber exp10(XOMNumber n, MathContext mc, MathProcessor mp) {
		return pow(XOMNumber.TEN, n, mc, mp);
	}
	
	public static XOMNumber log(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.log(n.toNumber(), mc));
	}
	
	public static XOMNumber log1p(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.log1p(n.toNumber(), mc));
	}
	
	public static XOMNumber log2(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.log2(n.toNumber(), mc));
	}
	
	public static XOMNumber log10(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.log10(n.toNumber(), mc));
	}
	
	public static XOMNumber eml(XOMNumber a, XOMNumber b, MathContext mc, MathProcessor mp) {
		return exp(a, mc, mp).subtract(log(b, mc, mp), mc);
	}
	
	public static XOMNumber edl(XOMNumber a, XOMNumber b, MathContext mc, MathProcessor mp) {
		return exp(a, mc, mp).divide(log(b, mc, mp), mc);
	}
	
	public static XOMNumber lme(XOMNumber a, XOMNumber b, MathContext mc, MathProcessor mp) {
		return log(a, mc, mp).subtract(exp(b, mc, mp), mc);
	}
	
	public static XOMNumber lde(XOMNumber a, XOMNumber b, MathContext mc, MathProcessor mp) {
		return log(a, mc, mp).divide(exp(b, mc, mp), mc);
	}
	
	public static XOMNumber sin(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.sin(n.toNumber(), mc));
	}
	
	public static XOMNumber cos(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.cos(n.toNumber(), mc));
	}
	
	public static XOMNumber tan(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.tan(n.toNumber(), mc));
	}
	
	public static XOMNumber cot(XOMNumber n, MathContext mc, MathProcessor mp) {
		return XOMNumber.ONE.divide(tan(n, mc, mp), mc);
	}
	
	public static XOMNumber sec(XOMNumber n, MathContext mc, MathProcessor mp) {
		return XOMNumber.ONE.divide(cos(n, mc, mp), mc);
	}
	
	public static XOMNumber csc(XOMNumber n, MathContext mc, MathProcessor mp) {
		return XOMNumber.ONE.divide(sin(n, mc, mp), mc);
	}
	
	public static XOMNumber sinh(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.sinh(n.toNumber(), mc));
	}
	
	public static XOMNumber cosh(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.cosh(n.toNumber(), mc));
	}
	
	public static XOMNumber tanh(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.tanh(n.toNumber(), mc));
	}
	
	public static XOMNumber coth(XOMNumber n, MathContext mc, MathProcessor mp) {
		return XOMNumber.ONE.divide(tanh(n, mc, mp), mc);
	}
	
	public static XOMNumber sech(XOMNumber n, MathContext mc, MathProcessor mp) {
		return XOMNumber.ONE.divide(cosh(n, mc, mp), mc);
	}
	
	public static XOMNumber csch(XOMNumber n, MathContext mc, MathProcessor mp) {
		return XOMNumber.ONE.divide(sinh(n, mc, mp), mc);
	}
	
	public static XOMNumber asin(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.asin(n.toNumber(), mc));
	}
	
	public static XOMNumber acos(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.acos(n.toNumber(), mc));
	}
	
	public static XOMNumber atan(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.atan(n.toNumber(), mc));
	}
	
	public static XOMNumber atan2(XOMNumber y, XOMNumber x, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.atan2(y.toNumber(), x.toNumber(), mc));
	}
	
	public static XOMNumber acot(XOMNumber n, MathContext mc, MathProcessor mp) {
		return atan2(XOMNumber.ONE, n, mc, mp);
	}
	
	public static XOMNumber asec(XOMNumber n, MathContext mc, MathProcessor mp) {
		return acos(XOMNumber.ONE.divide(n, mc), mc, mp);
	}
	
	public static XOMNumber acsc(XOMNumber n, MathContext mc, MathProcessor mp) {
		return asin(XOMNumber.ONE.divide(n, mc), mc, mp);
	}
	
	public static XOMNumber asinh(XOMNumber n, MathContext mc, MathProcessor mp) {
		XOMNumber nspo = n.multiply(n, mc).add(XOMNumber.ONE, mc);
		return log(sqrt(nspo, mc, mp).add(n, mc), mc, mp);
	}
	
	public static XOMNumber acosh(XOMNumber n, MathContext mc, MathProcessor mp) {
		XOMNumber nsmo = n.multiply(n, mc).subtract(XOMNumber.ONE, mc);
		return log(sqrt(nsmo, mc, mp).add(n, mc), mc, mp);
	}
	
	public static XOMNumber atanh(XOMNumber n, MathContext mc, MathProcessor mp) {
		XOMNumber opn = XOMNumber.ONE.add(n, mc);
		XOMNumber omn = XOMNumber.ONE.subtract(n, mc);
		return log(opn.divide(omn, mc), mc, mp).divide(XOMNumber.TWO, mc);
	}
	
	public static XOMNumber acoth(XOMNumber n, MathContext mc, MathProcessor mp) {
		XOMNumber npo = n.add(XOMNumber.ONE, mc);
		XOMNumber nmo = n.subtract(XOMNumber.ONE, mc);
		return log(npo.divide(nmo, mc), mc, mp).divide(XOMNumber.TWO, mc);
	}
	
	public static XOMNumber asech(XOMNumber n, MathContext mc, MathProcessor mp) {
		return acosh(XOMNumber.ONE.divide(n, mc), mc, mp);
	}
	
	public static XOMNumber acsch(XOMNumber n, MathContext mc, MathProcessor mp) {
		return asinh(XOMNumber.ONE.divide(n, mc), mc, mp);
	}
	
	public static XOMNumber erf(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.erf(n.toNumber(), mc));
	}
	
	public static XOMNumber erfc(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.erfc(n.toNumber(), mc));
	}
	
	public static XOMNumber erfcx(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.erfcx(n.toNumber(), mc));
	}
	
	public static XOMNumber erfi(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isNaN() || n.isInfinite() || n.isZero()) return n;
		return XOMComplexMath.erfi(new XOMComplex(n.toNumber(), 0), mc, mp).re();
	}
	
	public static XOMNumber gamma(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.gamma(n.toNumber(), mc));
	}
	
	public static XOMNumber loggamma(XOMNumber n, MathContext mc, MathProcessor mp) {
		return new XOMNumber(mp.loggamma(n.toNumber(), mc));
	}
	
	public static XOMNumber fact(XOMNumber n, MathContext mc, MathProcessor mp) {
		return gamma(XOMNumber.ONE.add(n, mc), mc, mp);
	}
	
	public static XOMNumber logfact(XOMNumber n, MathContext mc, MathProcessor mp) {
		return loggamma(XOMNumber.ONE.add(n, mc), mc, mp);
	}
	
	public static XOMNumber beta(XOMNumber a, XOMNumber b, MathContext mc, MathProcessor mp) {
		XOMNumber a1 = gamma(a, mc, mp);
		XOMNumber b1 = gamma(b, mc, mp);
		XOMNumber ab = gamma(a.add(b, mc), mc, mp);
		return a1.multiply(b1, mc).divide(ab, mc);
	}
	
	public static XOMNumber logbeta(XOMNumber a, XOMNumber b, MathContext mc, MathProcessor mp) {
		XOMNumber a1 = loggamma(a, mc, mp);
		XOMNumber b1 = loggamma(b, mc, mp);
		XOMNumber ab = loggamma(a.add(b, mc), mc, mp);
		return a1.add(b1, mc).subtract(ab, mc);
	}
	
	public static XOMNumber nPr(XOMNumber n, XOMNumber r, MathContext mc, MathProcessor mp) {
		XOMNumber n1 = gamma(XOMNumber.ONE.add(n, mc), mc, mp);
		XOMNumber nr = gamma(XOMNumber.ONE.add(n, mc).subtract(r, mc), mc, mp);
		return n1.divide(nr, mc);
	}
	
	public static XOMNumber lognPr(XOMNumber n, XOMNumber r, MathContext mc, MathProcessor mp) {
		XOMNumber n1 = loggamma(XOMNumber.ONE.add(n, mc), mc, mp);
		XOMNumber nr = loggamma(XOMNumber.ONE.add(n, mc).subtract(r, mc), mc, mp);
		return n1.subtract(nr, mc);
	}
	
	public static XOMNumber nCr(XOMNumber n, XOMNumber r, MathContext mc, MathProcessor mp) {
		XOMNumber n1 = gamma(XOMNumber.ONE.add(n, mc), mc, mp);
		XOMNumber r1 = gamma(XOMNumber.ONE.add(r, mc), mc, mp);
		XOMNumber nr = gamma(XOMNumber.ONE.add(n, mc).subtract(r, mc), mc, mp);
		return n1.divide(r1, mc).divide(nr, mc);
	}
	
	public static XOMNumber lognCr(XOMNumber n, XOMNumber r, MathContext mc, MathProcessor mp) {
		XOMNumber n1 = loggamma(XOMNumber.ONE.add(n, mc), mc, mp);
		XOMNumber r1 = loggamma(XOMNumber.ONE.add(r, mc), mc, mp);
		XOMNumber nr = loggamma(XOMNumber.ONE.add(n, mc).subtract(r, mc), mc, mp);
		return n1.subtract(r1, mc).subtract(nr, mc);
	}
}
