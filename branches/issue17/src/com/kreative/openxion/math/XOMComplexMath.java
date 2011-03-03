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
import com.kreative.openxion.xom.inst.XOMComplex;

/**
 * Methods for mathematical operations on and functions of XOMComplexes.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XOMComplexMath {
	private XOMComplexMath(){}
	
	public static XOMComplex add(XOMComplex a, XOMComplex b, MathContext mc, MathProcessor mp) {
		if (a.isNaN() || b.isNaN()) return XOMComplex.NaN;
		else if (a.isInfinite() && b.isInfinite()) {
			if (a.getQuadrant() == b.getQuadrant()) return a;
			else return XOMComplex.NaN;
		}
		else if (a.isInfinite()) {
			return a;
		}
		else if (b.isInfinite()) {
			return b;
		}
		else {
			BigDecimal r = a.realPart().add(b.realPart(), mc);
			BigDecimal i = a.imaginaryPart().add(b.imaginaryPart(), mc);
			return new XOMComplex(r, i);
		}
	}
	
	public static XOMComplex subtract(XOMComplex a, XOMComplex b, MathContext mc, MathProcessor mp) {
		if (a.isNaN() || b.isNaN()) return XOMComplex.NaN;
		else if (a.isInfinite() && b.isInfinite()) {
			if (a.getQuadrant() == b.getOppositeQuadrant()) return a;
			else return XOMComplex.NaN;
		}
		else if (a.isInfinite()) {
			return a;
		}
		else if (b.isInfinite()) {
			return b.negate();
		}
		else {
			BigDecimal r = a.realPart().subtract(b.realPart(), mc);
			BigDecimal i = a.imaginaryPart().subtract(b.imaginaryPart(), mc);
			return new XOMComplex(r, i);
		}
	}
	
	public static XOMComplex multiply(XOMComplex a, XOMComplex b, MathContext mc, MathProcessor mp) {
		if (a.isNaN() || b.isNaN()) return XOMComplex.NaN;
		else if (a.isInfinite() || b.isInfinite()) {
			if (a.isZero() || b.isZero()) return XOMComplex.NaN;
			else {
				int aa = a.realPart().compareTo(BigDecimal.ZERO);
				int bb = a.imaginaryPart().compareTo(BigDecimal.ZERO);
				int cc = b.realPart().compareTo(BigDecimal.ZERO);
				int dd = b.imaginaryPart().compareTo(BigDecimal.ZERO);
				int rr = aa*cc - bb*dd;
				int ii = aa*dd + bb*cc;
				if (rr == 0 || ii == 0) return XOMComplex.NaN;
				return XOMComplex.makeInfinity((double)rr, (double)ii);
			}
		}
		else {
			BigDecimal ac = a.realPart().multiply(b.realPart(), mc);
			BigDecimal bd = a.imaginaryPart().multiply(b.imaginaryPart(), mc);
			BigDecimal bc = a.imaginaryPart().multiply(b.realPart(), mc);
			BigDecimal ad = a.realPart().multiply(b.imaginaryPart(), mc);
			BigDecimal r = ac.subtract(bd, mc);
			BigDecimal i = bc.add(ad, mc);
			return new XOMComplex(r, i);
		}
	}
	
	public static XOMComplex divide(XOMComplex a, XOMComplex b, MathContext mc, MathProcessor mp) {
		if (a.isNaN() || b.isNaN()) return XOMComplex.NaN;
		else if ((a.isInfinite() && b.isInfinite()) || (a.isZero() && b.isZero())) {
			return XOMComplex.NaN;
		}
		else if (b.isZero()) {
			int aa = a.realPart().compareTo(BigDecimal.ZERO);
			int bb = a.imaginaryPart().compareTo(BigDecimal.ZERO);
			if (aa == 0 || bb == 0) return XOMComplex.NaN;
			return XOMComplex.makeInfinity((double)aa, (double)bb);
		}
		else if (a.isInfinite()) {
			if (b.isZero()) return a;
			else {
				int aa = a.realPart().compareTo(BigDecimal.ZERO);
				int bb = a.imaginaryPart().compareTo(BigDecimal.ZERO);
				int cc = b.realPart().compareTo(BigDecimal.ZERO);
				int dd = b.imaginaryPart().compareTo(BigDecimal.ZERO);
				int rr = aa*cc - bb*dd;
				int ii = aa*dd + bb*cc;
				if (rr == 0 || ii == 0) return XOMComplex.NaN;
				return XOMComplex.makeInfinity((double)rr, (double)ii);
			}
		}
		else if (b.isInfinite() || a.isZero()) {
			return XOMComplex.ZERO;
		}
		else {
			BigDecimal ac = a.realPart().multiply(b.realPart(), mc);
			BigDecimal bd = a.imaginaryPart().multiply(b.imaginaryPart(), mc);
			BigDecimal bc = a.imaginaryPart().multiply(b.realPart(), mc);
			BigDecimal ad = a.realPart().multiply(b.imaginaryPart(), mc);
			BigDecimal cc = b.realPart().multiply(b.realPart(), mc);
			BigDecimal dd = b.imaginaryPart().multiply(b.imaginaryPart(), mc);
			BigDecimal acbd = ac.add(bd, mc);
			BigDecimal bcad = bc.subtract(ad, mc);
			BigDecimal ccdd = cc.add(dd, mc);
			BigDecimal r = acbd.divide(ccdd, mc);
			BigDecimal i = bcad.divide(ccdd, mc);
			return new XOMComplex(r, i);
		}
	}
	
	public static XOMComplex pow(XOMComplex a, XOMComplex b, MathContext mc, MathProcessor mp) {
		if (a.isNaN() || b.isNaN()) return XOMComplex.NaN;
		else if (a.isZero()) {
			if (b.getQuadrant() == XOMComplex.QUADRANT_POSITIVE_REAL) {
				return XOMComplex.ZERO;
			} else if (b.getQuadrant() == XOMComplex.QUADRANT_NEGATIVE_REAL) {
				return XOMComplex.POSITIVE_INFINITY;
			} else {
				return XOMComplex.NaN;
			}
		}
		else if (a.isInfinite() || b.isInfinite()) {
			if (a.getQuadrant() == XOMComplex.QUADRANT_POSITIVE_REAL) {
				if (b.getQuadrant() == XOMComplex.QUADRANT_POSITIVE_REAL) {
					return XOMComplex.POSITIVE_INFINITY;
				} else if (b.getQuadrant() == XOMComplex.QUADRANT_NEGATIVE_REAL) {
					return XOMComplex.ZERO;
				} else {
					return XOMComplex.NaN;
				}
			} else {
				return XOMComplex.NaN;
			}
		}
		else if (b.isZero()) {
			return XOMComplex.ONE;
		}
		else {
			BigDecimal aa = a.realPart();
			BigDecimal bb = a.imaginaryPart();
			BigDecimal cc = b.realPart();
			BigDecimal dd = b.imaginaryPart();
			BigDecimal r = mp.hypot(aa, bb, mc);
			BigDecimal s = mp.atan2(bb, aa, mc);
			BigDecimal m = mp.pow(r, cc, mc).multiply(mp.exp(s.negate().multiply(dd,mc),mc),mc);
			BigDecimal n = dd.multiply(mp.log(r,mc),mc).add(cc.multiply(s,mc),mc);
			BigDecimal rr = m.multiply(mp.cos(n,mc),mc);
			BigDecimal ii = m.multiply(mp.sin(n,mc),mc);
			return new XOMComplex(rr, ii);
		}
	}
	
	public static XOMComplex abs(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.realPart() == null || n.imaginaryPart() == null) return XOMComplex.NaN;
		else if (n.isUndefined()) {
			int rc = n.realPart().compareTo(BigDecimal.ZERO);
			int ic = n.imaginaryPart().compareTo(BigDecimal.ZERO);
			if (rc == 0 && ic == 0) return XOMComplex.NaN;
			else return new XOMComplex(((rc != 0) ? Double.POSITIVE_INFINITY : 0.0), ((ic != 0) ? Double.POSITIVE_INFINITY : 0.0));
		}
		else {
			BigDecimal theSqrt = mp.hypot(n.realPart(), n.imaginaryPart(), mc);
			if (theSqrt == null) return XOMComplex.NaN;
			else return new XOMComplex(theSqrt, BigDecimal.ZERO);
		}
	}
	
	public static XOMComplex arg(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.realPart() == null || n.imaginaryPart() == null) return XOMComplex.NaN;
		else if (n.isUndefined()) {
			int rc = n.realPart().compareTo(BigDecimal.ZERO);
			int ic = n.imaginaryPart().compareTo(BigDecimal.ZERO);
			if ((rc == 0 && ic == 0) || (rc != 0 && ic != 0)) return XOMComplex.NaN;
			BigDecimal theArg = mp.atan2(n.imaginaryPart(), n.realPart(), mc);
			if (theArg == null) return XOMComplex.NaN;
			else return new XOMComplex(theArg, BigDecimal.ZERO);
		}
		else {
			BigDecimal theArg = mp.atan2(n.imaginaryPart(), n.realPart(), mc);
			if (theArg == null) return XOMComplex.NaN;
			else return new XOMComplex(theArg, BigDecimal.ZERO);
		}
	}
	
	public static XOMComplex signum(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.realPart() == null || n.imaginaryPart() == null) return XOMComplex.NaN;
		else if (n.isUndefined()) {
			int rc = n.realPart().compareTo(BigDecimal.ZERO);
			int ic = n.imaginaryPart().compareTo(BigDecimal.ZERO);
			if ((rc == 0 && ic == 0) || (rc != 0 && ic != 0)) return XOMComplex.NaN;
			else if (rc == 0 && ic != 0) return (ic < 0) ? new XOMComplex(BigDecimal.ZERO, BigDecimal.ONE.negate()) : new XOMComplex(BigDecimal.ZERO, BigDecimal.ONE);
			else if (ic == 0 && rc != 0) return (rc < 0) ? new XOMComplex(BigDecimal.ONE.negate(), BigDecimal.ZERO) : new XOMComplex(BigDecimal.ONE, BigDecimal.ZERO);
			else return XOMComplex.NaN;
		}
		else {
			BigDecimal theSqrt = mp.hypot(n.realPart(), n.imaginaryPart(), mc);
			if (theSqrt == null) return XOMComplex.NaN;
			else return new XOMComplex(n.realPart().divide(theSqrt,mc), n.imaginaryPart().divide(theSqrt,mc));
		}
	}
	
	public static XOMComplex annuity(XOMComplex a, XOMComplex b, MathContext mc, MathProcessor mp) {
		if (a.isZero() || b.isZero()) return b;
		else return divide(subtract(XOMComplex.ONE,pow(add(XOMComplex.ONE,a,mc,mp),b.negate(),mc,mp),mc,mp),a,mc,mp);
	}
	
	public static XOMComplex compound(XOMComplex a, XOMComplex b, MathContext mc, MathProcessor mp) {
		if (a.isZero() || b.isZero()) return XOMComplex.ONE;
		else return pow(add(XOMComplex.ONE,a,mc,mp),b,mc,mp);
	}
	
	public static XOMComplex sqrt(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.realPart() == null || n.imaginaryPart() == null) return XOMComplex.NaN;
		else if (n.isUndefined()) {
			int rc = n.realPart().compareTo(BigDecimal.ZERO);
			int ic = n.imaginaryPart().compareTo(BigDecimal.ZERO);
			if (rc <= 0 || ic != 0) return XOMComplex.NaN;
			else return XOMComplex.POSITIVE_INFINITY;
		}
		else {
			BigDecimal r = mp.hypot(n.realPart(), n.imaginaryPart(), mc);
			BigDecimal rpa = r.add(n.realPart(), mc).divide(BigDecimal.valueOf(2.0), mc);
			BigDecimal ipa = r.subtract(n.realPart(), mc).divide(BigDecimal.valueOf(2.0), mc);
			BigDecimal rp = mp.sqrt(rpa, mc);
			BigDecimal ip = mp.sqrt(ipa, mc);
			if (n.imaginaryPart().compareTo(BigDecimal.ZERO) < 0) ip = ip.negate();
			return new XOMComplex(rp, ip);
		}
	}
	
	public static XOMComplex cbrt(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.realPart() == null || n.imaginaryPart() == null) return XOMComplex.NaN;
		else if (n.isUndefined()) {
			int rc = n.realPart().compareTo(BigDecimal.ZERO);
			int ic = n.imaginaryPart().compareTo(BigDecimal.ZERO);
			if (rc <= 0 || ic != 0) return XOMComplex.NaN;
			else return XOMComplex.POSITIVE_INFINITY;
		}
		else if (n.isZero()) return XOMComplex.ZERO;
		else {
			BigDecimal lr3 = mp.log(mp.hypot(n.realPart(), n.imaginaryPart(), mc), mc).divide(BigDecimal.valueOf(3.0), mc);
			BigDecimal li3 = mp.atan2(n.imaginaryPart(), n.realPart(), mc).divide(BigDecimal.valueOf(3.0), mc);
			BigDecimal e = mp.exp(lr3, mc);
			BigDecimal c = mp.cos(li3, mc);
			BigDecimal s = mp.sin(li3, mc);
			return new XOMComplex(e.multiply(c, mc), e.multiply(s, mc));
		}
	}
	
	public static XOMComplex agm(XOMComplex a, XOMComplex b, MathContext mc, MathProcessor mp) {
		XOMComplex t = new XOMComplex(2,0);
		while (true) {
			if (a.isNaN() || b.isNaN()) return XOMComplex.NaN;
			XOMComplex m = divide(add(a,b,mc,mp),t,mc,mp);
			XOMComplex g = sqrt(multiply(a,b,mc,mp),mc,mp);
			if (m.equals(g)) return m;
			a = m; b = g;
		}
	}
	
	public static XOMComplex exp(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.realPart() == null || n.imaginaryPart() == null) return XOMComplex.NaN;
		else if (n.isUndefined()) {
			int rc = n.realPart().compareTo(BigDecimal.ZERO);
			int ic = n.imaginaryPart().compareTo(BigDecimal.ZERO);
			if (rc == 0 || ic != 0) return XOMComplex.NaN;
			else if (rc < 0) return XOMComplex.ZERO;
			else return XOMComplex.POSITIVE_INFINITY;
		}
		else {
			BigDecimal e = mp.exp(n.realPart(), mc);
			BigDecimal c = mp.cos(n.imaginaryPart(), mc);
			BigDecimal s = mp.sin(n.imaginaryPart(), mc);
			return new XOMComplex(e.multiply(c, mc), e.multiply(s, mc));
		}
	}
	
	public static XOMComplex expm1(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.realPart() == null || n.imaginaryPart() == null) return XOMComplex.NaN;
		else if (n.isUndefined()) {
			int rc = n.realPart().compareTo(BigDecimal.ZERO);
			int ic = n.imaginaryPart().compareTo(BigDecimal.ZERO);
			if (rc == 0 || ic != 0) return XOMComplex.NaN;
			else if (rc < 0) return XOMComplex.ZERO;
			else return XOMComplex.POSITIVE_INFINITY;
		}
		else {
			BigDecimal e = mp.exp(n.realPart(), mc);
			BigDecimal c = mp.cos(n.imaginaryPart(), mc);
			BigDecimal s = mp.sin(n.imaginaryPart(), mc);
			return new XOMComplex(e.multiply(c, mc).subtract(BigDecimal.ONE), e.multiply(s, mc));
		}
	}
	
	public static XOMComplex exp2(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.realPart() == null || n.imaginaryPart() == null) return XOMComplex.NaN;
		else if (n.isUndefined()) {
			int rc = n.realPart().compareTo(BigDecimal.ZERO);
			int ic = n.imaginaryPart().compareTo(BigDecimal.ZERO);
			if (rc == 0 || ic != 0) return XOMComplex.NaN;
			else if (rc < 0) return XOMComplex.ZERO;
			else return XOMComplex.POSITIVE_INFINITY;
		}
		else {
			return pow(new XOMComplex(2.0,0.0),n,mc,mp);
		}
	}
	
	public static XOMComplex exp10(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.realPart() == null || n.imaginaryPart() == null) return XOMComplex.NaN;
		else if (n.isUndefined()) {
			int rc = n.realPart().compareTo(BigDecimal.ZERO);
			int ic = n.imaginaryPart().compareTo(BigDecimal.ZERO);
			if (rc == 0 || ic != 0) return XOMComplex.NaN;
			else if (rc < 0) return XOMComplex.ZERO;
			else return XOMComplex.POSITIVE_INFINITY;
		}
		else {
			return pow(new XOMComplex(10.0,0.0),n,mc,mp);
		}
	}
	
	public static XOMComplex log(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.realPart() == null || n.imaginaryPart() == null) return XOMComplex.NaN;
		else if (n.isUndefined()) {
			int rc = n.realPart().compareTo(BigDecimal.ZERO);
			int ic = n.imaginaryPart().compareTo(BigDecimal.ZERO);
			if (rc > 0 && ic == 0) return XOMComplex.POSITIVE_INFINITY;
			else return XOMComplex.NaN;
		}
		else if (n.isZero()) return XOMComplex.NEGATIVE_INFINITY;
		else {
			BigDecimal rp = mp.log(mp.hypot(n.realPart(), n.imaginaryPart(), mc), mc);
			BigDecimal ip = mp.atan2(n.imaginaryPart(), n.realPart(), mc);
			return new XOMComplex(rp, ip);
		}
	}
	
	public static XOMComplex log1p(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.realPart() == null || n.imaginaryPart() == null) return XOMComplex.NaN;
		else if (n.isUndefined()) {
			int rc = n.realPart().compareTo(BigDecimal.ZERO);
			int ic = n.imaginaryPart().compareTo(BigDecimal.ZERO);
			if (rc > 0 && ic == 0) return XOMComplex.POSITIVE_INFINITY;
			else return XOMComplex.NaN;
		}
		else {
			n = new XOMComplex(n.realPart().add(BigDecimal.ONE), n.imaginaryPart());
			if (n.isZero()) return XOMComplex.NEGATIVE_INFINITY;
			else {
				BigDecimal rp = mp.log(mp.hypot(n.realPart(), n.imaginaryPart(), mc), mc);
				BigDecimal ip = mp.atan2(n.imaginaryPart(), n.realPart(), mc);
				return new XOMComplex(rp, ip);
			}
		}
	}
	
	public static XOMComplex log2(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.realPart() == null || n.imaginaryPart() == null) return XOMComplex.NaN;
		else if (n.isUndefined()) {
			int rc = n.realPart().compareTo(BigDecimal.ZERO);
			int ic = n.imaginaryPart().compareTo(BigDecimal.ZERO);
			if (rc > 0 && ic == 0) return XOMComplex.POSITIVE_INFINITY;
			else return XOMComplex.NaN;
		}
		else if (n.isZero()) return XOMComplex.NEGATIVE_INFINITY;
		else {
			BigDecimal rp = mp.log2(mp.hypot(n.realPart(), n.imaginaryPart(), mc), mc);
			BigDecimal ip = mp.atan2(n.imaginaryPart(), n.realPart(), mc);
			BigDecimal b = mp.log(BigDecimal.valueOf(2), mc);
			return new XOMComplex(rp, ip.divide(b,mc));
		}
	}
	
	public static XOMComplex log10(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.realPart() == null || n.imaginaryPart() == null) return XOMComplex.NaN;
		else if (n.isUndefined()) {
			int rc = n.realPart().compareTo(BigDecimal.ZERO);
			int ic = n.imaginaryPart().compareTo(BigDecimal.ZERO);
			if (rc > 0 && ic == 0) return XOMComplex.POSITIVE_INFINITY;
			else return XOMComplex.NaN;
		}
		else if (n.isZero()) return XOMComplex.NEGATIVE_INFINITY;
		else {
			BigDecimal rp = mp.log10(mp.hypot(n.realPart(), n.imaginaryPart(), mc), mc);
			BigDecimal ip = mp.atan2(n.imaginaryPart(), n.realPart(), mc);
			BigDecimal b = mp.log(BigDecimal.valueOf(10), mc);
			return new XOMComplex(rp, ip.divide(b,mc));
		}
	}
	
	public static XOMComplex sin(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMComplex.NaN;
		else {
			BigDecimal s = mp.sin(n.realPart(), mc);
			BigDecimal ch = mp.cosh(n.imaginaryPart(), mc);
			BigDecimal c = mp.cos(n.realPart(), mc);
			BigDecimal sh = mp.sinh(n.imaginaryPart(), mc);
			return new XOMComplex(s.multiply(ch,mc), c.multiply(sh,mc));
		}
	}
	
	public static XOMComplex cos(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMComplex.NaN;
		else {
			BigDecimal c = mp.cos(n.realPart(), mc);
			BigDecimal ch = mp.cosh(n.imaginaryPart(), mc);
			BigDecimal s = mp.sin(n.realPart(), mc);
			BigDecimal sh = mp.sinh(n.imaginaryPart(), mc);
			return new XOMComplex(c.multiply(ch,mc), s.multiply(sh,mc).negate());
		}
	}
	
	public static XOMComplex tan(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMComplex.NaN;
		else return divide(sin(n,mc,mp),cos(n,mc,mp),mc,mp);
	}
	
	public static XOMComplex cot(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMComplex.NaN;
		else return divide(cos(n,mc,mp),sin(n,mc,mp),mc,mp);
	}
	
	public static XOMComplex csc(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMComplex.NaN;
		else return divide(XOMComplex.ONE,sin(n,mc,mp),mc,mp);
	}
	
	public static XOMComplex sec(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMComplex.NaN;
		else return divide(XOMComplex.ONE,cos(n,mc,mp),mc,mp);
	}
	
	public static XOMComplex sinh(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMComplex.NaN;
		else {
			XOMComplex ix = new XOMComplex(n.imaginaryPart().negate(), n.realPart());
			XOMComplex sinix = sin(ix,mc,mp);
			XOMComplex nisinix = new XOMComplex(sinix.imaginaryPart(), sinix.realPart().negate());
			return nisinix;
		}
	}
	
	public static XOMComplex cosh(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMComplex.NaN;
		else {
			XOMComplex ix = new XOMComplex(n.imaginaryPart().negate(), n.realPart());
			XOMComplex cosix = cos(ix,mc,mp);
			return cosix;
		}
	}
	
	public static XOMComplex tanh(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMComplex.NaN;
		else {
			XOMComplex ix = new XOMComplex(n.imaginaryPart().negate(), n.realPart());
			XOMComplex tanix = tan(ix,mc,mp);
			XOMComplex nitanix = new XOMComplex(tanix.imaginaryPart(), tanix.realPart().negate());
			return nitanix;
		}
	}
	
	public static XOMComplex coth(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMComplex.NaN;
		else if (n.isZero()) return XOMComplex.POSITIVE_INFINITY;
		else {
			XOMComplex ix = new XOMComplex(n.imaginaryPart().negate(), n.realPart());
			XOMComplex cotix = cot(ix,mc,mp);
			XOMComplex icotix = new XOMComplex(cotix.imaginaryPart().negate(), cotix.realPart());
			return icotix;
		}
	}
	
	public static XOMComplex csch(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMComplex.NaN;
		else {
			XOMComplex ix = new XOMComplex(n.imaginaryPart().negate(), n.realPart());
			XOMComplex cscix = csc(ix,mc,mp);
			XOMComplex icscix = new XOMComplex(cscix.imaginaryPart().negate(), cscix.realPart());
			return icscix;
		}
	}
	
	public static XOMComplex sech(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMComplex.NaN;
		else if (n.isZero()) return XOMComplex.POSITIVE_INFINITY;
		else {
			XOMComplex ix = new XOMComplex(n.imaginaryPart().negate(), n.realPart());
			XOMComplex secix = sec(ix,mc,mp);
			return secix;
		}
	}
	
	public static XOMComplex asin(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMComplex.NaN;
		else {
			XOMComplex s = sqrt(subtract(XOMComplex.ONE,multiply(n,n,mc,mp),mc,mp),mc,mp);
			XOMComplex a = add(multiply(XOMComplex.I,n,mc,mp),s,mc,mp);
			XOMComplex l = log(a,mc,mp);
			return multiply(XOMComplex.I.negate(),l,mc,mp);
		}
	}
	
	public static XOMComplex acos(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMComplex.NaN;
		else {
			XOMComplex s = sqrt(subtract(XOMComplex.ONE,multiply(n,n,mc,mp),mc,mp),mc,mp);
			XOMComplex a = add(n,multiply(XOMComplex.I,s,mc,mp),mc,mp);
			XOMComplex l = log(a,mc,mp);
			return multiply(XOMComplex.I.negate(),l,mc,mp);
		}
	}
	
	public static XOMComplex atan(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMComplex.NaN;
		else {
			XOMComplex ix = multiply(XOMComplex.I,n,mc,mp);
			XOMComplex ln1mix = log(subtract(XOMComplex.ONE,ix,mc,mp),mc,mp);
			XOMComplex ln1pix = log(add(XOMComplex.ONE,ix,mc,mp),mc,mp);
			XOMComplex a = subtract(ln1mix,ln1pix,mc,mp);
			return divide(multiply(XOMComplex.I,a,mc,mp),new XOMComplex(2,0),mc,mp);
		}
	}
	
	public static XOMComplex acot(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMComplex.NaN;
		else {
			XOMComplex ix = divide(XOMComplex.I,n,mc,mp);
			XOMComplex ln1mix = log(subtract(XOMComplex.ONE,ix,mc,mp),mc,mp);
			XOMComplex ln1pix = log(add(XOMComplex.ONE,ix,mc,mp),mc,mp);
			XOMComplex a = subtract(ln1mix,ln1pix,mc,mp);
			return divide(multiply(XOMComplex.I,a,mc,mp),new XOMComplex(2,0),mc,mp);
		}
	}
	
	public static XOMComplex acsc(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMComplex.NaN;
		else {
			XOMComplex s = sqrt(subtract(XOMComplex.ONE,divide(XOMComplex.ONE,multiply(n,n,mc,mp),mc,mp),mc,mp),mc,mp);
			XOMComplex a = add(s,divide(XOMComplex.I,n,mc,mp),mc,mp);
			XOMComplex l = log(a,mc,mp);
			return multiply(XOMComplex.I.negate(),l,mc,mp);
		}
	}
	
	public static XOMComplex asec(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMComplex.NaN;
		else {
			XOMComplex s = sqrt(subtract(XOMComplex.ONE,divide(XOMComplex.ONE,multiply(n,n,mc,mp),mc,mp),mc,mp),mc,mp);
			XOMComplex a = add(multiply(XOMComplex.I,s,mc,mp),divide(XOMComplex.ONE,n,mc,mp),mc,mp);
			XOMComplex l = log(a,mc,mp);
			return multiply(XOMComplex.I.negate(),l,mc,mp);
		}
	}
	
	public static XOMComplex asinh(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return n;
		else {
			XOMComplex x = n;
			XOMComplex sqrtxsqp1 = sqrt(add(multiply(x,x,mc,mp),XOMComplex.ONE,mc,mp),mc,mp);
			return log(add(x,sqrtxsqp1,mc,mp),mc,mp);
		}
	}
	
	public static XOMComplex acosh(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.getQuadrant() == XOMComplex.QUADRANT_POSITIVE_REAL) return XOMComplex.POSITIVE_INFINITY;
			else return XOMComplex.NaN;
		}
		else {
			XOMComplex x = n;
			XOMComplex sqrtxm1 = sqrt(subtract(x,XOMComplex.ONE,mc,mp),mc,mp);
			XOMComplex sqrtxp1 = sqrt(add(x,XOMComplex.ONE,mc,mp),mc,mp);
			XOMComplex xm1txp1 = multiply(sqrtxm1,sqrtxp1,mc,mp);
			return log(add(x,xm1txp1,mc,mp),mc,mp);
		}
	}
	
	public static XOMComplex atanh(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMComplex.NaN;
		else {
			if (n.imaginaryPart().compareTo(BigDecimal.ZERO) == 0 && n.realPart().abs().compareTo(BigDecimal.ONE) == 0) {
				return (n.realPart().signum() < 0) ? XOMComplex.NEGATIVE_INFINITY : XOMComplex.POSITIVE_INFINITY;
			}
			XOMComplex x = n;
			XOMComplex onemx = subtract(XOMComplex.ONE,x,mc,mp);
			XOMComplex sqrt1mx2 = sqrt(subtract(XOMComplex.ONE,multiply(x,x,mc,mp),mc,mp),mc,mp);
			return log(divide(sqrt1mx2,onemx,mc,mp),mc,mp);
		}
	}
	
	public static XOMComplex acoth(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.isInfinite()) return XOMComplex.ZERO;
			else return XOMComplex.NaN;
		}
		else {
			if (n.imaginaryPart().compareTo(BigDecimal.ZERO) == 0 && n.realPart().abs().compareTo(BigDecimal.ONE) == 0) {
				return (n.realPart().signum() < 0) ? XOMComplex.NEGATIVE_INFINITY : XOMComplex.POSITIVE_INFINITY;
			}
			XOMComplex x = n;
			XOMComplex a = divide(add(x,XOMComplex.ONE,mc,mp),subtract(x,XOMComplex.ONE,mc,mp),mc,mp);
			return divide(log(a,mc,mp),new XOMComplex(2,0),mc,mp);
		}
	}
	
	public static XOMComplex acsch(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.isInfinite()) return XOMComplex.ZERO;
			else return XOMComplex.NaN;
		}
		else {
			if (n.isZero()) return XOMComplex.NaN;
			XOMComplex x = n;
			XOMComplex rx = divide(XOMComplex.ONE,x,mc,mp);
			XOMComplex rxx = divide(XOMComplex.ONE,multiply(x,x,mc,mp),mc,mp);
			XOMComplex sqrt1prxx = sqrt(add(XOMComplex.ONE,rxx,mc,mp),mc,mp);
			return log(add(sqrt1prxx,rx,mc,mp),mc,mp);
		}
	}
	
	public static XOMComplex asech(XOMComplex n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMComplex.NaN;
		else {
			if (n.isZero()) return XOMComplex.POSITIVE_INFINITY;
			XOMComplex x = n;
			XOMComplex rx = divide(XOMComplex.ONE,x,mc,mp);
			XOMComplex sqrtrxm1 = sqrt(subtract(rx,XOMComplex.ONE,mc,mp),mc,mp);
			XOMComplex sqrtrxp1 = sqrt(add(rx,XOMComplex.ONE,mc,mp),mc,mp);
			XOMComplex rxm1trxp1 = multiply(sqrtrxm1,sqrtrxp1,mc,mp);
			return log(add(rxm1trxp1,rx,mc,mp),mc,mp);
		}
	}
	
	private static final int G = 7;
	private static final XOMComplex[] P = new XOMComplex[] {
		new XOMComplex(0.99999999999980993,0.0), new XOMComplex(676.5203681218851,0.0), new XOMComplex(-1259.1392167224028,0.0),
		new XOMComplex(771.32342877765313,0.0), new XOMComplex(-176.61502916214059,0.0), new XOMComplex(12.507343278686905,0.0),
		new XOMComplex(-0.13857109526572012,0.0), new XOMComplex(9.9843695780195716e-6,0.0), new XOMComplex(1.5056327351493116e-7,0.0)
	};
	public static XOMComplex gamma(XOMComplex z, MathContext mc, MathProcessor mp) {
		if (z.isNaN()) return XOMComplex.NaN;
		else if (z.isInfinite()) return (z.getQuadrant() == XOMComplex.QUADRANT_POSITIVE_REAL) ? XOMComplex.POSITIVE_INFINITY : XOMComplex.NaN;
		else if (
				z.realPart().compareTo(BigDecimal.ZERO) < 0
				&& z.imaginaryPart().compareTo(BigDecimal.ZERO) == 0
				&& z.realPart().setScale(0,BigDecimal.ROUND_HALF_EVEN).compareTo(z.realPart()) == 0
		) return XOMComplex.NaN;
		else if (z.isZero()) return XOMComplex.POSITIVE_INFINITY;
		else if (z.realPart().compareTo(BigDecimal.valueOf(0.5)) < 0) {
			return divide(XOMComplex.PI,multiply(sin(multiply(XOMComplex.PI,z,mc,mp),mc,mp),gamma(subtract(XOMComplex.ONE,z,mc,mp),mc,mp),mc,mp),mc,mp);
		}
		else {
			z = subtract(z,XOMComplex.ONE,mc,mp);
			XOMComplex x = P[0];
			for (int i = 1; i < G+2; i++) {
				x = add(x,divide(P[i],add(z,new XOMComplex(i,0),mc,mp),mc,mp),mc,mp);
			}
			XOMComplex t = add(z,new XOMComplex(G + 0.5,0.0),mc,mp);
			return multiply(multiply(multiply(sqrt(multiply(new XOMComplex(2,0),XOMComplex.PI,mc,mp),mc,mp),pow(t,add(z,new XOMComplex(0.5,0),mc,mp),mc,mp),mc,mp),exp(t.negate(),mc,mp),mc,mp),x,mc,mp);
		}
	}
	public static XOMComplex loggamma(XOMComplex z, MathContext mc, MathProcessor mp) {
		return log(gamma(z,mc,mp),mc,mp);
	}
	public static XOMComplex fact(XOMComplex n, MathContext mc, MathProcessor mp) {
		return gamma(add(n,XOMComplex.ONE,mc,mp),mc,mp);
	}
	public static XOMComplex logfact(XOMComplex n, MathContext mc, MathProcessor mp) {
		return loggamma(add(n,XOMComplex.ONE,mc,mp),mc,mp);
	}
	public static XOMComplex beta(XOMComplex a, XOMComplex b, MathContext mc, MathProcessor mp) {
		return divide(multiply(gamma(a,mc,mp),gamma(b,mc,mp),mc,mp),gamma(add(a,b,mc,mp),mc,mp),mc,mp);
	}
	public static XOMComplex logbeta(XOMComplex a, XOMComplex b, MathContext mc, MathProcessor mp) {
		return subtract(add(loggamma(a,mc,mp),loggamma(b,mc,mp),mc,mp),loggamma(add(a,b,mc,mp),mc,mp),mc,mp);
	}
	public static XOMComplex nPr(XOMComplex n, XOMComplex r, MathContext mc, MathProcessor mp) {
		return divide(gamma(add(n,XOMComplex.ONE,mc,mp),mc,mp),gamma(add(subtract(n,r,mc,mp),XOMComplex.ONE,mc,mp),mc,mp),mc,mp);
	}
	public static XOMComplex nCr(XOMComplex n, XOMComplex r, MathContext mc, MathProcessor mp) {
		return divide(divide(gamma(add(n,XOMComplex.ONE,mc,mp),mc,mp),gamma(add(r,XOMComplex.ONE,mc,mp),mc,mp),mc,mp),gamma(add(subtract(n,r,mc,mp),XOMComplex.ONE,mc,mp),mc,mp),mc,mp);
	}
}
