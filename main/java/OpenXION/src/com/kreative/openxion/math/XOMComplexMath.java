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
 * Methods for mathematical operations on and functions of XOMComplexes.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XOMComplexMath {
	private XOMComplexMath(){}
	
	public static XOMComplex fma(XOMComplex a, XOMComplex b, XOMComplex c, MathContext mc) {
		return a.multiply(b, mc).add(c, mc);
	}
	
	public static XOMComplex abs(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMNumber h = XOMNumberMath.hypot(n.im(), n.re(), mc, mp);
		return new XOMComplex(h.toNumber(), 0);
	}
	
	public static XOMComplex arg(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMNumber a = XOMNumberMath.atan2(n.im(), n.re(), mc, mp);
		return new XOMComplex(a.toNumber(), 0);
	}
	
	public static XOMComplex signum(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isRe()) return new XOMComplex(n.re().signum().toNumber(), 0);
		if (n.isIm()) return new XOMComplex(0, n.im().signum().toNumber());
		XOMNumber h = XOMNumberMath.hypot(n.im(), n.re(), mc, mp);
		XOMNumber r = n.re().divide(h, mc);
		XOMNumber i = n.im().divide(h, mc);
		return new XOMComplex(r.toNumber(), i.toNumber());
	}
	
	public static XOMComplex pow(XOMComplex a, XOMComplex b, MathContext mc, MathProcessor mp) {
		if (a.isNaN() || b.isNaN()) return XOMComplex.NaN;
		if (b.isZero()) return (a.isFinite() && !a.isZero()) ? XOMComplex.ONE : XOMComplex.NaN;
		if (b.equals(XOMComplex.ONE)) return a;
		XOMNumber h = XOMNumberMath.hypot(a.im(), a.re(), mc, mp);
		XOMNumber t = XOMNumberMath.atan2(a.im(), a.re(), mc, mp);
		XOMNumber e = XOMNumberMath.exp(t.multiply(b.im(), mc).negate(), mc, mp);
		XOMNumber m = XOMNumberMath.pow(h, b.re(), mc, mp).multiply(e, mc);
		XOMNumber n = XOMNumberMath.log(h,mc,mp).multiply(b.im(),mc).add(t.multiply(b.re(),mc),mc);
		XOMNumber r = XOMNumberMath.cos(n, mc, mp).multiply(m, mc);
		XOMNumber i = XOMNumberMath.sin(n, mc, mp).multiply(m, mc);
		return new XOMComplex(r.toNumber(), i.toNumber());
	}
	
	public static XOMComplex annuity(XOMComplex a, XOMComplex b, MathContext mc, MathProcessor mp) {
		if (a.isZero() || b.isZero()) return b;
		XOMComplex opa = XOMComplex.ONE.add(a, mc);
		XOMComplex opatnb = pow(opa, b.negate(), mc, mp);
		XOMComplex omopatnb = XOMComplex.ONE.subtract(opatnb, mc);
		return omopatnb.divide(a, mc);
	}
	
	public static XOMComplex compound(XOMComplex a, XOMComplex b, MathContext mc, MathProcessor mp) {
		if (a.isZero() || b.isZero()) return XOMComplex.ONE;
		XOMComplex opa = XOMComplex.ONE.add(a, mc);
		return pow(opa, b, mc, mp);
	}
	
	public static XOMComplex sqrt(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMNumber h = XOMNumberMath.hypot(n.im(), n.re(), mc, mp);
		XOMNumber a = XOMNumberMath.atan2(n.im(), n.re(), mc, mp);
		XOMNumber h2 = XOMNumberMath.sqrt(h, mc, mp);
		XOMNumber a2 = a.divide(XOMNumber.TWO, mc);
		XOMNumber r = h2.multiply(XOMNumberMath.cos(a2, mc, mp), mc);
		XOMNumber i = h2.multiply(XOMNumberMath.sin(a2, mc, mp), mc);
		return new XOMComplex(r.toNumber(), i.toNumber());
	}
	
	public static XOMComplex cbrt(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMNumber h = XOMNumberMath.hypot(n.im(), n.re(), mc, mp);
		XOMNumber a = XOMNumberMath.atan2(n.im(), n.re(), mc, mp);
		XOMNumber h3 = XOMNumberMath.cbrt(h, mc, mp);
		XOMNumber a3 = a.divide(XOMNumber.THREE, mc);
		XOMNumber r = h3.multiply(XOMNumberMath.cos(a3, mc, mp), mc);
		XOMNumber i = h3.multiply(XOMNumberMath.sin(a3, mc, mp), mc);
		return new XOMComplex(r.toNumber(), i.toNumber());
	}
	
	public static XOMComplex qtrt(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMNumber h = XOMNumberMath.hypot(n.im(), n.re(), mc, mp);
		XOMNumber a = XOMNumberMath.atan2(n.im(), n.re(), mc, mp);
		XOMNumber h4 = XOMNumberMath.qtrt(h, mc, mp);
		XOMNumber a4 = a.divide(XOMNumber.FOUR, mc);
		XOMNumber r = h4.multiply(XOMNumberMath.cos(a4, mc, mp), mc);
		XOMNumber i = h4.multiply(XOMNumberMath.sin(a4, mc, mp), mc);
		return new XOMComplex(r.toNumber(), i.toNumber());
	}
	
	public static XOMComplex twrt(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMNumber h = XOMNumberMath.hypot(n.im(), n.re(), mc, mp);
		XOMNumber a = XOMNumberMath.atan2(n.im(), n.re(), mc, mp);
		XOMNumber h12 = XOMNumberMath.twrt(h, mc, mp);
		XOMNumber a12 = a.divide(XOMNumber.TWELVE, mc);
		XOMNumber r = h12.multiply(XOMNumberMath.cos(a12, mc, mp), mc);
		XOMNumber i = h12.multiply(XOMNumberMath.sin(a12, mc, mp), mc);
		return new XOMComplex(r.toNumber(), i.toNumber());
	}
	
	public static XOMComplex hypot(XOMComplex y, XOMComplex x, MathContext mc, MathProcessor mp) {
		return sqrt(y.multiply(y, mc).add(x.multiply(x, mc), mc), mc, mp);
	}
	
	public static XOMComplex agm(XOMComplex a, XOMComplex b, MathContext mc, MathProcessor mp) {
		while (true) {
			if (a.isNaN() || b.isNaN()) return XOMComplex.NaN;
			if (a.equals(b)) return a;
			XOMComplex c = a.add(b, mc).divide(XOMComplex.TWO, mc);
			XOMComplex d = sqrt(a.multiply(b, mc), mc, mp);
			a = c; b = d;
		}
	}
	
	public static XOMComplex exp(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMNumber e = XOMNumberMath.exp(n.re(), mc, mp);
		XOMNumber c = XOMNumberMath.cos(n.im(), mc, mp);
		XOMNumber s = XOMNumberMath.sin(n.im(), mc, mp);
		XOMNumber r = e.multiply(c, mc);
		XOMNumber i = e.multiply(s, mc);
		return new XOMComplex(r.toNumber(), i.toNumber());
	}
	
	public static XOMComplex expm1(XOMComplex n, MathContext mc, MathProcessor mp) {
		return exp(n, mc, mp).subtract(XOMComplex.ONE, mc);
	}
	
	public static XOMComplex exp2(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMNumber e = XOMNumberMath.exp2(n.re(), mc, mp);
		XOMNumber a = n.im().multiply(XOMNumberMath.log(XOMNumber.TWO, mc, mp), mc);
		XOMNumber c = XOMNumberMath.cos(a, mc, mp);
		XOMNumber s = XOMNumberMath.sin(a, mc, mp);
		XOMNumber r = e.multiply(c, mc);
		XOMNumber i = e.multiply(s, mc);
		return new XOMComplex(r.toNumber(), i.toNumber());
	}
	
	public static XOMComplex exp10(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMNumber e = XOMNumberMath.exp10(n.re(), mc, mp);
		XOMNumber a = n.im().multiply(XOMNumberMath.log(XOMNumber.TEN, mc, mp), mc);
		XOMNumber c = XOMNumberMath.cos(a, mc, mp);
		XOMNumber s = XOMNumberMath.sin(a, mc, mp);
		XOMNumber r = e.multiply(c, mc);
		XOMNumber i = e.multiply(s, mc);
		return new XOMComplex(r.toNumber(), i.toNumber());
	}
	
	public static XOMComplex log(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMNumber h = XOMNumberMath.hypot(n.im(), n.re(), mc, mp);
		XOMNumber a = XOMNumberMath.atan2(n.im(), n.re(), mc, mp);
		XOMNumber r = XOMNumberMath.log(h, mc, mp);
		return new XOMComplex(r.toNumber(), a.toNumber());
	}
	
	public static XOMComplex log1p(XOMComplex n, MathContext mc, MathProcessor mp) {
		return log(XOMComplex.ONE.add(n, mc), mc, mp);
	}
	
	public static XOMComplex log2(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMNumber h = XOMNumberMath.hypot(n.im(), n.re(), mc, mp);
		XOMNumber a = XOMNumberMath.atan2(n.im(), n.re(), mc, mp);
		XOMNumber r = XOMNumberMath.log2(h, mc, mp);
		XOMNumber i = a.divide(XOMNumberMath.log(XOMNumber.TWO, mc, mp), mc);
		return new XOMComplex(r.toNumber(), i.toNumber());
	}
	
	public static XOMComplex log10(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMNumber h = XOMNumberMath.hypot(n.im(), n.re(), mc, mp);
		XOMNumber a = XOMNumberMath.atan2(n.im(), n.re(), mc, mp);
		XOMNumber r = XOMNumberMath.log10(h, mc, mp);
		XOMNumber i = a.divide(XOMNumberMath.log(XOMNumber.TEN, mc, mp), mc);
		return new XOMComplex(r.toNumber(), i.toNumber());
	}
	
	public static XOMComplex eml(XOMComplex a, XOMComplex b, MathContext mc, MathProcessor mp) {
		return exp(a, mc, mp).subtract(log(b, mc, mp), mc);
	}
	
	public static XOMComplex edl(XOMComplex a, XOMComplex b, MathContext mc, MathProcessor mp) {
		return exp(a, mc, mp).divide(log(b, mc, mp), mc);
	}
	
	public static XOMComplex lme(XOMComplex a, XOMComplex b, MathContext mc, MathProcessor mp) {
		return log(a, mc, mp).subtract(exp(b, mc, mp), mc);
	}
	
	public static XOMComplex lde(XOMComplex a, XOMComplex b, MathContext mc, MathProcessor mp) {
		return log(a, mc, mp).divide(exp(b, mc, mp), mc);
	}
	
	public static XOMComplex sin(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMNumber s = XOMNumberMath.sin(n.re(), mc, mp);
		XOMNumber ch = XOMNumberMath.cosh(n.im(), mc, mp);
		XOMNumber c = XOMNumberMath.cos(n.re(), mc, mp);
		XOMNumber sh = XOMNumberMath.sinh(n.im(), mc, mp);
		XOMNumber r = s.multiply(ch, mc);
		XOMNumber i = c.multiply(sh, mc);
		return new XOMComplex(r.toNumber(), i.toNumber());
	}
	
	public static XOMComplex cos(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMNumber c = XOMNumberMath.cos(n.re(), mc, mp);
		XOMNumber ch = XOMNumberMath.cosh(n.im(), mc, mp);
		XOMNumber s = XOMNumberMath.sin(n.re(), mc, mp);
		XOMNumber sh = XOMNumberMath.sinh(n.im(), mc, mp);
		XOMNumber r = c.multiply(ch, mc);
		XOMNumber i = s.multiply(sh, mc).negate();
		return new XOMComplex(r.toNumber(), i.toNumber());
	}
	
	public static XOMComplex tan(XOMComplex n, MathContext mc, MathProcessor mp) {
		return sin(n, mc, mp).divide(cos(n, mc, mp), mc);
	}
	
	public static XOMComplex cot(XOMComplex n, MathContext mc, MathProcessor mp) {
		return cos(n, mc, mp).divide(sin(n, mc, mp), mc);
	}
	
	public static XOMComplex sec(XOMComplex n, MathContext mc, MathProcessor mp) {
		return XOMComplex.ONE.divide(cos(n, mc, mp), mc);
	}
	
	public static XOMComplex csc(XOMComplex n, MathContext mc, MathProcessor mp) {
		return XOMComplex.ONE.divide(sin(n, mc, mp), mc);
	}
	
	public static XOMComplex sinh(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMNumber sh = XOMNumberMath.sinh(n.re(), mc, mp);
		XOMNumber c = XOMNumberMath.cos(n.im(), mc, mp);
		XOMNumber ch = XOMNumberMath.cosh(n.re(), mc, mp);
		XOMNumber s = XOMNumberMath.sin(n.im(), mc, mp);
		XOMNumber r = sh.multiply(c, mc);
		XOMNumber i = ch.multiply(s, mc);
		return new XOMComplex(r.toNumber(), i.toNumber());
	}
	
	public static XOMComplex cosh(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMNumber ch = XOMNumberMath.cosh(n.re(), mc, mp);
		XOMNumber c = XOMNumberMath.cos(n.im(), mc, mp);
		XOMNumber sh = XOMNumberMath.sinh(n.re(), mc, mp);
		XOMNumber s = XOMNumberMath.sin(n.im(), mc, mp);
		XOMNumber r = ch.multiply(c, mc);
		XOMNumber i = sh.multiply(s, mc);
		return new XOMComplex(r.toNumber(), i.toNumber());
	}
	
	public static XOMComplex tanh(XOMComplex n, MathContext mc, MathProcessor mp) {
		return sinh(n, mc, mp).divide(cosh(n, mc, mp), mc);
	}
	
	public static XOMComplex coth(XOMComplex n, MathContext mc, MathProcessor mp) {
		return cosh(n, mc, mp).divide(sinh(n, mc, mp), mc);
	}
	
	public static XOMComplex sech(XOMComplex n, MathContext mc, MathProcessor mp) {
		return XOMComplex.ONE.divide(cosh(n, mc, mp), mc);
	}
	
	public static XOMComplex csch(XOMComplex n, MathContext mc, MathProcessor mp) {
		return XOMComplex.ONE.divide(sinh(n, mc, mp), mc);
	}
	
	public static XOMComplex asin(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMComplex s = sqrt(XOMComplex.ONE.subtract(n.multiply(n, mc), mc), mc, mp);
		return log(s.add(n.muli(), mc), mc, mp).mulni();
	}
	
	public static XOMComplex acos(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMComplex s = sqrt(XOMComplex.ONE.subtract(n.multiply(n, mc), mc), mc, mp);
		return log(n.add(s.muli(), mc), mc, mp).mulni();
	}
	
	public static XOMComplex atan(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMComplex z = XOMComplex.POSITIVE_I.multiply(n, mc);
		XOMComplex lomz = log(XOMComplex.ONE.subtract(z, mc), mc, mp);
		XOMComplex lopz = log(XOMComplex.ONE.add(z, mc), mc, mp);
		return lomz.subtract(lopz, mc).muli().divide(XOMComplex.TWO, mc);
	}
	
	public static XOMComplex acot(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMComplex z = XOMComplex.POSITIVE_I.divide(n, mc);
		XOMComplex lomz = log(XOMComplex.ONE.subtract(z, mc), mc, mp);
		XOMComplex lopz = log(XOMComplex.ONE.add(z, mc), mc, mp);
		return lomz.subtract(lopz, mc).muli().divide(XOMComplex.TWO, mc);
	}
	
	public static XOMComplex asec(XOMComplex n, MathContext mc, MathProcessor mp) {
		return acos(XOMComplex.ONE.divide(n, mc), mc, mp);
	}
	
	public static XOMComplex acsc(XOMComplex n, MathContext mc, MathProcessor mp) {
		return asin(XOMComplex.ONE.divide(n, mc), mc, mp);
	}
	
	public static XOMComplex asinh(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMComplex nspo = n.multiply(n, mc).add(XOMComplex.ONE, mc);
		return log(sqrt(nspo, mc, mp).add(n, mc), mc, mp);
	}
	
	public static XOMComplex acosh(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMComplex nsmo = n.multiply(n, mc).subtract(XOMComplex.ONE, mc);
		return log(sqrt(nsmo, mc, mp).add(n, mc), mc, mp);
	}
	
	public static XOMComplex atanh(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMComplex opn = XOMComplex.ONE.add(n, mc);
		XOMComplex omn = XOMComplex.ONE.subtract(n, mc);
		return log(opn.divide(omn, mc), mc, mp).divide(XOMComplex.TWO, mc);
	}
	
	public static XOMComplex acoth(XOMComplex n, MathContext mc, MathProcessor mp) {
		XOMComplex npo = n.add(XOMComplex.ONE, mc);
		XOMComplex nmo = n.subtract(XOMComplex.ONE, mc);
		return log(npo.divide(nmo, mc), mc, mp).divide(XOMComplex.TWO, mc);
	}
	
	public static XOMComplex asech(XOMComplex n, MathContext mc, MathProcessor mp) {
		return acosh(XOMComplex.ONE.divide(n, mc), mc, mp);
	}
	
	public static XOMComplex acsch(XOMComplex n, MathContext mc, MathProcessor mp) {
		return asinh(XOMComplex.ONE.divide(n, mc), mc, mp);
	}
	
	public static XOMComplex erf(XOMComplex z, MathContext mc, MathProcessor mp) {
		if (z.isNaN()) return XOMComplex.NaN;
		if (z.isInfinite()) {
			if (z.isRe()) return z.re().isNeg() ? XOMComplex.NEGATIVE_ONE : XOMComplex.ONE;
			if (z.isIm()) return z.im().isNeg() ? XOMComplex.NEGATIVE_I_INFINITY : XOMComplex.POSITIVE_I_INFINITY;
			return XOMComplex.NaN;
		}
		if (z.isZero()) return XOMComplex.ZERO;
		return XOMComplex.ONE.subtract(erfc(z, mc, mp), mc);
	}
	
	private static final XOMComplex[] ERFC = new XOMComplex[] {
		new XOMComplex(0.56418958354775629, 0), new XOMComplex(2.06955023132914151, 0),
		new XOMComplex(2.71078540045147805, 0), new XOMComplex(5.80755613130301624, 0),
		new XOMComplex(3.47954057099518960, 0), new XOMComplex(12.06166887286239555, 0),
		new XOMComplex(3.47469513777439592, 0), new XOMComplex(12.07402036406381411, 0),
		new XOMComplex(3.72068443960225092, 0), new XOMComplex(8.44319781003968454, 0),
		new XOMComplex(4.00561509202259545, 0), new XOMComplex(9.30596659485887898, 0),
		new XOMComplex(3.90225704029924078, 0), new XOMComplex(6.36161630953880464, 0),
		new XOMComplex(5.16722705817812584, 0), new XOMComplex(9.12661617673673262, 0),
		new XOMComplex(4.03296893109262491, 0), new XOMComplex(5.13578530585681539, 0),
		new XOMComplex(5.95908795446633271, 0), new XOMComplex(9.19435612886969243, 0),
		new XOMComplex(4.11240942957450885, 0), new XOMComplex(4.48640329523408675, 0)
	};
	
	public static XOMComplex erfc(XOMComplex z, MathContext mc, MathProcessor mp) {
		if (z.isNaN()) return XOMComplex.NaN;
		if (z.isInfinite()) {
			if (z.isRe()) return z.re().isNeg() ? XOMComplex.TWO : XOMComplex.ZERO;
			return XOMComplex.NaN;
		}
		if (z.isZero()) return XOMComplex.ONE;
		if (z.re().isNeg()) return XOMComplex.TWO.subtract(erfc(z.negate(), mc, mp), mc);
		return exp(z.multiply(z, mc).negate(), mc, mp).multiply(erfcx(z, mc, mp), mc);
	}
	
	public static XOMComplex erfcx(XOMComplex z, MathContext mc, MathProcessor mp) {
		if (z.isNaN()) return XOMComplex.NaN;
		if (z.isInfinite()) {
			if (z.isRe()) return z.re().isNeg() ? XOMComplex.POSITIVE_INFINITY : XOMComplex.ZERO;
			return XOMComplex.NaN;
		}
		if (z.isZero()) return XOMComplex.ONE;
		if (z.re().isNeg()) return exp(z.multiply(z, mc), mc, mp).multiply(erfc(z, mc, mp), mc);
		XOMComplex zz = z.multiply(z, mc);
		XOMComplex w = ERFC[0].divide(ERFC[1].add(z, mc), mc);
		w = w.multiply(zz.add(ERFC[2].multiply(z, mc), mc).add(ERFC[3], mc).divide(zz.add(ERFC[4].multiply(z, mc), mc).add(ERFC[5], mc), mc), mc);
		w = w.multiply(zz.add(ERFC[6].multiply(z, mc), mc).add(ERFC[7], mc).divide(zz.add(ERFC[8].multiply(z, mc), mc).add(ERFC[9], mc), mc), mc);
		w = w.multiply(zz.add(ERFC[10].multiply(z, mc), mc).add(ERFC[11], mc).divide(zz.add(ERFC[12].multiply(z, mc), mc).add(ERFC[13], mc), mc), mc);
		w = w.multiply(zz.add(ERFC[14].multiply(z, mc), mc).add(ERFC[15], mc).divide(zz.add(ERFC[16].multiply(z, mc), mc).add(ERFC[17], mc), mc), mc);
		w = w.multiply(zz.add(ERFC[18].multiply(z, mc), mc).add(ERFC[19], mc).divide(zz.add(ERFC[20].multiply(z, mc), mc).add(ERFC[21], mc), mc), mc);
		return w;
	}
	
	public static XOMComplex erfi(XOMComplex z, MathContext mc, MathProcessor mp) {
		if (z.isNaN()) return XOMComplex.NaN;
		if (z.isInfinite()) {
			if (z.isRe()) return z.re().isNeg() ? XOMComplex.NEGATIVE_INFINITY : XOMComplex.POSITIVE_INFINITY;
			if (z.isIm()) return z.im().isNeg() ? XOMComplex.NEGATIVE_I : XOMComplex.POSITIVE_I;
			return XOMComplex.NaN;
		}
		if (z.isZero()) return XOMComplex.ZERO;
		return erf(XOMComplex.POSITIVE_I.multiply(z, mc), mc, mp).multiply(XOMComplex.NEGATIVE_I, mc);
	}
	
	private static final XOMComplex ONE_HALF = new XOMComplex(0.5, 0);
	private static final XOMComplex GAMMA_G = new XOMComplex(7, 0);
	private static final XOMComplex[] GAMMA_P = {
		new XOMComplex(0.99999999999980993227684700473478, 0),
		new XOMComplex(676.520368121885098567009190444019, 0),
		new XOMComplex(-1259.13921672240287047156078755283, 0),
		new XOMComplex(771.3234287776530788486528258894, 0),
		new XOMComplex(-176.61502916214059906584551354, 0),
		new XOMComplex(12.507343278686904814458936853, 0),
		new XOMComplex(-0.13857109526572011689554707, 0),
		new XOMComplex(9.984369578019570859563e-6, 0),
		new XOMComplex(1.50563273514931155834e-7, 0)
	};
	
	private static XOMComplex[] gammaTZX(XOMComplex z, MathContext mc) {
		z = z.subtract(XOMComplex.ONE, mc);
		XOMComplex x = GAMMA_P[0];
		for (int i = 1; i < GAMMA_P.length; i++) {
			x = x.add(GAMMA_P[i].divide(new XOMComplex(i, 0).add(z, mc), mc), mc);
		}
		XOMComplex t = z.add(GAMMA_G, mc).add(ONE_HALF, mc);
		return new XOMComplex[] { t, z.add(ONE_HALF, mc), x };
	}
	
	public static XOMComplex gamma(XOMComplex z, MathContext mc, MathProcessor mp) {
		if (z.isNaN()) return XOMComplex.NaN;
		if (z.isInfinite()) return (z.isRe() && z.re().isPos()) ? XOMComplex.POSITIVE_INFINITY : XOMComplex.NaN;
		if (z.isRe() && !z.re().isPos() && z.re().frac().isZero()) return XOMComplex.NaN;
		if (z.re().toDouble() < 0.5) {
			XOMComplex pi = new XOMComplex(mp.pi(mc), 0);
			XOMComplex s = sin(pi.multiply(z, mc), mc, mp);
			XOMComplex t = gamma(XOMComplex.ONE.subtract(z, mc), mc, mp);
			return pi.divide(s.multiply(t, mc), mc);
		}
		XOMComplex[] tzx = gammaTZX(z, mc);
		XOMComplex pi = new XOMComplex(mp.pi(mc), 0);
		XOMComplex k = sqrt(XOMComplex.TWO.multiply(pi, mc), mc, mp);
		XOMComplex p = k.multiply(pow(tzx[0], tzx[1], mc, mp), mc);
		XOMComplex e = p.multiply(exp(tzx[0].negate(), mc, mp), mc);
		return e.multiply(tzx[2], mc);
	}
	
	public static XOMComplex loggamma(XOMComplex z, MathContext mc, MathProcessor mp) {
		if (z.isNaN()) return XOMComplex.NaN;
		if (z.isInfinite()) return (z.isRe() && z.re().isPos()) ? XOMComplex.POSITIVE_INFINITY : XOMComplex.NaN;
		if (z.isRe() && !z.re().isPos() && z.re().frac().isZero()) return XOMComplex.NaN;
		if (z.re().toDouble() < 0.5) {
			XOMComplex pi = new XOMComplex(mp.pi(mc), 0);
			XOMComplex s = sin(pi.multiply(z, mc), mc, mp);
			XOMComplex t = loggamma(XOMComplex.ONE.subtract(z, mc), mc, mp);
			return log(pi.divide(s, mc), mc, mp).subtract(t, mc);
		}
		XOMComplex[] tzx = gammaTZX(z, mc);
		XOMComplex pi = new XOMComplex(mp.pi(mc), 0);
		XOMComplex k = log(sqrt(XOMComplex.TWO.multiply(pi, mc), mc, mp), mc, mp);
		XOMComplex p = k.add(tzx[1].multiply(log(tzx[0], mc, mp), mc), mc);
		XOMComplex e = p.subtract(tzx[0], mc);
		return e.add(log(tzx[2], mc, mp), mc);
	}
	
	public static XOMComplex fact(XOMComplex n, MathContext mc, MathProcessor mp) {
		return gamma(XOMComplex.ONE.add(n, mc), mc, mp);
	}
	
	public static XOMComplex logfact(XOMComplex n, MathContext mc, MathProcessor mp) {
		return loggamma(XOMComplex.ONE.add(n, mc), mc, mp);
	}
	
	public static XOMComplex beta(XOMComplex a, XOMComplex b, MathContext mc, MathProcessor mp) {
		XOMComplex a1 = gamma(a, mc, mp);
		XOMComplex b1 = gamma(b, mc, mp);
		XOMComplex ab = gamma(a.add(b, mc), mc, mp);
		return a1.multiply(b1, mc).divide(ab, mc);
	}
	
	public static XOMComplex logbeta(XOMComplex a, XOMComplex b, MathContext mc, MathProcessor mp) {
		XOMComplex a1 = loggamma(a, mc, mp);
		XOMComplex b1 = loggamma(b, mc, mp);
		XOMComplex ab = loggamma(a.add(b, mc), mc, mp);
		return a1.add(b1, mc).subtract(ab, mc);
	}
	
	public static XOMComplex nPr(XOMComplex n, XOMComplex r, MathContext mc, MathProcessor mp) {
		XOMComplex n1 = gamma(XOMComplex.ONE.add(n, mc), mc, mp);
		XOMComplex nr = gamma(XOMComplex.ONE.add(n, mc).subtract(r, mc), mc, mp);
		return n1.divide(nr, mc);
	}
	
	public static XOMComplex lognPr(XOMComplex n, XOMComplex r, MathContext mc, MathProcessor mp) {
		XOMComplex n1 = loggamma(XOMComplex.ONE.add(n, mc), mc, mp);
		XOMComplex nr = loggamma(XOMComplex.ONE.add(n, mc).subtract(r, mc), mc, mp);
		return n1.subtract(nr, mc);
	}
	
	public static XOMComplex nCr(XOMComplex n, XOMComplex r, MathContext mc, MathProcessor mp) {
		XOMComplex n1 = gamma(XOMComplex.ONE.add(n, mc), mc, mp);
		XOMComplex r1 = gamma(XOMComplex.ONE.add(r, mc), mc, mp);
		XOMComplex nr = gamma(XOMComplex.ONE.add(n, mc).subtract(r, mc), mc, mp);
		return n1.divide(r1, mc).divide(nr, mc);
	}
	
	public static XOMComplex lognCr(XOMComplex n, XOMComplex r, MathContext mc, MathProcessor mp) {
		XOMComplex n1 = loggamma(XOMComplex.ONE.add(n, mc), mc, mp);
		XOMComplex r1 = loggamma(XOMComplex.ONE.add(r, mc), mc, mp);
		XOMComplex nr = loggamma(XOMComplex.ONE.add(n, mc).subtract(r, mc), mc, mp);
		return n1.subtract(r1, mc).subtract(nr, mc);
	}
}
