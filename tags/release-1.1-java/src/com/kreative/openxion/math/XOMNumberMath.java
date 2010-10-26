/*
 * Copyright &copy; 2009-2010 Rebecca G. Bettencourt / Kreative Software
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
import java.util.Comparator;
import com.kreative.openxion.xom.inst.XOMNumber;

/**
 * Methods for mathematical operations on and functions of XOMNumbers.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XOMNumberMath {
	private XOMNumberMath(){}
	
	public static final Comparator<XOMNumber> comparator = new Comparator<XOMNumber>() {
		public int compare(XOMNumber o1, XOMNumber o2) {
			return XOMNumberMath.compare(o1,o2);
		}
	};
	
	public static int compare(XOMNumber a, XOMNumber b) {
		if (a.isUndefined() || b.isUndefined()) {
			int am, bm;
			
			if (a.isNaN()) am = 3;
			else if (a.isInfinite()) switch (a.getSign()) {
			case XOMNumber.SIGN_NaN: am = 3; break;
			case XOMNumber.SIGN_NEGATIVE: am = -2; break;
			case XOMNumber.SIGN_POSITIVE: am = 2; break;
			case XOMNumber.SIGN_ZERO: am = 3; break;
			default: am = 3; break;
			}
			else if (a.isUndefined()) am = 3;
			else if (a.isZero()) am = 0;
			else switch (a.getSign()) {
			case XOMNumber.SIGN_NaN: am = 3; break;
			case XOMNumber.SIGN_NEGATIVE: am = -1; break;
			case XOMNumber.SIGN_POSITIVE: am = 1; break;
			case XOMNumber.SIGN_ZERO: am = 0; break;
			default: am = 3; break;
			}
			
			if (b.isNaN()) bm = 3;
			else if (b.isInfinite()) switch (b.getSign()) {
			case XOMNumber.SIGN_NaN: bm = 3; break;
			case XOMNumber.SIGN_NEGATIVE: bm = -2; break;
			case XOMNumber.SIGN_POSITIVE: bm = 2; break;
			case XOMNumber.SIGN_ZERO: bm = 3; break;
			default: bm = 3; break;
			}
			else if (b.isUndefined()) bm = 3;
			else if (b.isZero()) bm = 0;
			else switch (b.getSign()) {
			case XOMNumber.SIGN_NaN: bm = 3; break;
			case XOMNumber.SIGN_NEGATIVE: bm = -1; break;
			case XOMNumber.SIGN_POSITIVE: bm = 1; break;
			case XOMNumber.SIGN_ZERO: bm = 0; break;
			default: bm = 3; break;
			}
			
			return am-bm;
		} else {
			return a.toBigDecimal().compareTo(b.toBigDecimal());
		}
	}
	
	public static XOMNumber add(XOMNumber a, XOMNumber b, MathContext mc, MathProcessor mp) {
		if (a.isNaN() || b.isNaN()) return XOMNumber.NaN;
		else if (a.isInfinite() && b.isInfinite()) {
			if (a.getSign() == b.getSign()) return a;
			else return XOMNumber.NaN;
		}
		else if (a.isInfinite()) {
			return a;
		}
		else if (b.isInfinite()) {
			return b;
		}
		else {
			return new XOMNumber(a.toBigDecimal().add(b.toBigDecimal(), mc));
		}
	}
	
	public static XOMNumber subtract(XOMNumber a, XOMNumber b, MathContext mc, MathProcessor mp) {
		if (a.isNaN() || b.isNaN()) return XOMNumber.NaN;
		else if (a.isInfinite() && b.isInfinite()) {
			if (a.getSign() == b.getOppositeSign()) return a;
			else return XOMNumber.NaN;
		}
		else if (a.isInfinite()) {
			return a;
		}
		else if (b.isInfinite()) {
			return b.negate();
		}
		else {
			return new XOMNumber(a.toBigDecimal().subtract(b.toBigDecimal(), mc));
		}
	}
	
	public static XOMNumber multiply(XOMNumber a, XOMNumber b, MathContext mc, MathProcessor mp) {
		if (a.isNaN() || b.isNaN()) return XOMNumber.NaN;
		else if (a.isInfinite() || b.isInfinite()) {
			if (a.isZero() || b.isZero()) return XOMNumber.NaN;
			else if (a.getSign() == b.getSign()) return XOMNumber.POSITIVE_INFINITY;
			else return XOMNumber.NEGATIVE_INFINITY;
		}
		else {
			return new XOMNumber(a.toBigDecimal().multiply(b.toBigDecimal(), mc));
		}
	}
	
	public static XOMNumber divide(XOMNumber a, XOMNumber b, MathContext mc, MathProcessor mp) {
		if (a.isNaN() || b.isNaN()) return XOMNumber.NaN;
		else if ((a.isInfinite() && b.isInfinite()) || (a.isZero() && b.isZero())) {
			return XOMNumber.NaN;
		}
		else if (b.isZero()) {
			switch (a.getSign()) {
			case XOMNumber.SIGN_POSITIVE: return XOMNumber.POSITIVE_INFINITY;
			case XOMNumber.SIGN_NEGATIVE: return XOMNumber.NEGATIVE_INFINITY;
			default: return XOMNumber.NaN;
			}
		}
		else if (a.isInfinite()) {
			if (b.isZero()) return a;
			else if (a.getSign() == b.getSign()) return XOMNumber.POSITIVE_INFINITY;
			else return XOMNumber.NEGATIVE_INFINITY;
		}
		else if (b.isInfinite() || a.isZero()) {
			return XOMNumber.ZERO;
		}
		else {
			return new XOMNumber(a.toBigDecimal().divide(b.toBigDecimal(), mc));
		}
	}
	
	public static XOMNumber pow(XOMNumber a, XOMNumber b, MathContext mc, MathProcessor mp) {
		if (a.isNaN() || b.isNaN()) return XOMNumber.NaN;
		else if (a.isZero()) {
			switch (b.getSign()) {
			case XOMNumber.SIGN_NEGATIVE: return XOMNumber.POSITIVE_INFINITY;
			case XOMNumber.SIGN_ZERO: return XOMNumber.NaN;
			case XOMNumber.SIGN_POSITIVE: return XOMNumber.ZERO;
			default: return XOMNumber.NaN;
			}
		}
		else if (a.isInfinite()) {
			if (a.getSign() == XOMNumber.SIGN_POSITIVE) {
				switch (b.getSign()) {
				case XOMNumber.SIGN_NEGATIVE: return XOMNumber.ZERO;
				case XOMNumber.SIGN_ZERO: return XOMNumber.NaN;
				case XOMNumber.SIGN_POSITIVE: return XOMNumber.POSITIVE_INFINITY;
				default: return XOMNumber.NaN;
				}
			} else {
				if (b.isZero() || b.isInfinite()) return XOMNumber.NaN;
				else try {
					BigInteger i = b.toBigDecimal().toBigIntegerExact();
					if (i.compareTo(BigInteger.ZERO) < 0) return XOMNumber.ZERO;
					else if (i.testBit(0)) return XOMNumber.NEGATIVE_INFINITY;
					else return XOMNumber.POSITIVE_INFINITY;
				} catch (Exception e) {
					return XOMNumber.NaN;
				}
			}
		}
		else if (b.isZero()) {
			return XOMNumber.ONE;
		}
		else if (b.isInfinite()) {
			BigDecimal av = a.toBigDecimal();
			if (b.getSign() == XOMNumber.SIGN_POSITIVE) {
				if (av.compareTo(BigDecimal.ONE.negate()) <= 0) return XOMNumber.NaN;
				else if (av.compareTo(BigDecimal.ONE) < 0) return XOMNumber.ZERO;
				else if (av.compareTo(BigDecimal.ONE) == 0) return XOMNumber.NaN;
				else return XOMNumber.POSITIVE_INFINITY;
			} else {
				if (av.compareTo(BigDecimal.ONE.negate()) <= 0) return XOMNumber.NaN;
				else if (av.compareTo(BigDecimal.ONE) < 0) return XOMNumber.POSITIVE_INFINITY;
				else if (av.compareTo(BigDecimal.ONE) == 0) return XOMNumber.NaN;
				else return XOMNumber.ZERO;
			}
		}
		else {
			return new XOMNumber(mp.pow(a.toBigDecimal(), b.toBigDecimal(), mc));
		}
	}
	
	public static XOMNumber toDeg(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return n;
		else {
			BigDecimal d = n.toBigDecimal();
			d = d.multiply(BigDecimal.valueOf(180),mc).divide(mp.pi(mc),mc);
			return new XOMNumber(d);
		}
	}
	
	public static XOMNumber toRad(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return n;
		else {
			BigDecimal d = n.toBigDecimal();
			d = d.multiply(mp.pi(mc),mc).divide(BigDecimal.valueOf(180),mc);
			return new XOMNumber(d);
		}
	}
	
	public static XOMNumber annuity(XOMNumber a, XOMNumber b, MathContext mc, MathProcessor mp) {
		if (a.isZero() || b.isZero()) return b;
		else return divide(subtract(XOMNumber.ONE,pow(add(XOMNumber.ONE,a,mc,mp),b.negate(),mc,mp),mc,mp),a,mc,mp);
	}
	
	public static XOMNumber compound(XOMNumber a, XOMNumber b, MathContext mc, MathProcessor mp) {
		if (a.isZero() || b.isZero()) return XOMNumber.ONE;
		else return pow(add(XOMNumber.ONE,a,mc,mp),b,mc,mp);
	}
	
	public static XOMNumber sqrt(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.getSign() == XOMNumber.SIGN_POSITIVE) return XOMNumber.POSITIVE_INFINITY;
			else return XOMNumber.NaN;
		}
		else {
			BigDecimal d = mp.sqrt(n.toBigDecimal(), mc);
			if (d == null) return XOMNumber.NaN;
			else return new XOMNumber(d);
		}
	}
	
	public static XOMNumber cbrt(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.getSign() == XOMNumber.SIGN_POSITIVE) return XOMNumber.POSITIVE_INFINITY;
			else if (n.getSign() == XOMNumber.SIGN_NEGATIVE) return XOMNumber.NEGATIVE_INFINITY;
			else return XOMNumber.NaN;
		}
		else {
			BigDecimal d = mp.cbrt(n.toBigDecimal(), mc);
			if (d == null) return XOMNumber.NaN;
			else return new XOMNumber(d);
		}
	}
	
	public static XOMNumber agm(XOMNumber a, XOMNumber b, MathContext mc, MathProcessor mp) {
		XOMNumber t = new XOMNumber(2);
		while (true) {
			if (a.isNaN() || b.isNaN()) return XOMNumber.NaN;
			XOMNumber m = divide(add(a,b,mc,mp),t,mc,mp);
			XOMNumber g = sqrt(multiply(a,b,mc,mp),mc,mp);
			if (m.equals(g)) return m;
			a = m; b = g;
		}
	}
	
	public static XOMNumber exp(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.getSign() == XOMNumber.SIGN_NEGATIVE) return XOMNumber.ZERO;
			else if (n.getSign() == XOMNumber.SIGN_POSITIVE) return XOMNumber.POSITIVE_INFINITY;
			else return XOMNumber.NaN;
		}
		else {
			BigDecimal d = mp.exp(n.toBigDecimal(), mc);
			if (d == null) return XOMNumber.NaN;
			else return new XOMNumber(d);
		}
	}
	
	public static XOMNumber expm1(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.getSign() == XOMNumber.SIGN_NEGATIVE) return XOMNumber.ONE.negate();
			else if (n.getSign() == XOMNumber.SIGN_POSITIVE) return XOMNumber.POSITIVE_INFINITY;
			else return XOMNumber.NaN;
		}
		else {
			BigDecimal d = mp.expm1(n.toBigDecimal(), mc);
			if (d == null) return XOMNumber.NaN;
			else return new XOMNumber(d);
		}
	}
	
	public static XOMNumber exp2(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.getSign() == XOMNumber.SIGN_NEGATIVE) return XOMNumber.ZERO;
			else if (n.getSign() == XOMNumber.SIGN_POSITIVE) return XOMNumber.POSITIVE_INFINITY;
			else return XOMNumber.NaN;
		}
		else {
			BigDecimal d = mp.pow(BigDecimal.valueOf(2), n.toBigDecimal(), mc);
			if (d == null) return XOMNumber.NaN;
			else return new XOMNumber(d);
		}
	}
	
	public static XOMNumber exp10(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.getSign() == XOMNumber.SIGN_NEGATIVE) return XOMNumber.ZERO;
			else if (n.getSign() == XOMNumber.SIGN_POSITIVE) return XOMNumber.POSITIVE_INFINITY;
			else return XOMNumber.NaN;
		}
		else {
			BigDecimal d = mp.pow(BigDecimal.valueOf(10), n.toBigDecimal(), mc);
			if (d == null) return XOMNumber.NaN;
			else return new XOMNumber(d);
		}
	}
	
	public static XOMNumber log(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.getSign() == XOMNumber.SIGN_POSITIVE) return XOMNumber.POSITIVE_INFINITY;
			else return XOMNumber.NaN;
		}
		else {
			BigDecimal theNumber = n.toBigDecimal();
			int cmp = theNumber.compareTo(BigDecimal.ZERO);
			if (cmp < 0) return XOMNumber.NaN;
			else if (cmp == 0) return XOMNumber.NEGATIVE_INFINITY;
			else {
				BigDecimal d = mp.log(theNumber, mc);
				if (d == null) return XOMNumber.NaN;
				else return new XOMNumber(d);
			}
		}
	}
	
	public static XOMNumber log1p(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.getSign() == XOMNumber.SIGN_POSITIVE) return XOMNumber.POSITIVE_INFINITY;
			else return XOMNumber.NaN;
		}
		else {
			BigDecimal theNumber = n.toBigDecimal();
			int cmp = theNumber.compareTo(BigDecimal.ONE.negate());
			if (cmp < 0) return XOMNumber.NaN;
			else if (cmp == 0) return XOMNumber.NEGATIVE_INFINITY;
			else {
				BigDecimal d = mp.log1p(theNumber, mc);
				if (d == null) return XOMNumber.NaN;
				else return new XOMNumber(d);
			}
		}
	}
	
	public static XOMNumber log2(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.getSign() == XOMNumber.SIGN_POSITIVE) return XOMNumber.POSITIVE_INFINITY;
			else return XOMNumber.NaN;
		}
		else {
			BigDecimal theNumber = n.toBigDecimal();
			int cmp = theNumber.compareTo(BigDecimal.ZERO);
			if (cmp < 0) return XOMNumber.NaN;
			else if (cmp == 0) return XOMNumber.NEGATIVE_INFINITY;
			else {
				BigDecimal d = mp.log2(theNumber, mc);
				if (d == null) return XOMNumber.NaN;
				else return new XOMNumber(d);
			}
		}
	}
	
	public static XOMNumber log10(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.getSign() == XOMNumber.SIGN_POSITIVE) return XOMNumber.POSITIVE_INFINITY;
			else return XOMNumber.NaN;
		}
		else {
			BigDecimal theNumber = n.toBigDecimal();
			int cmp = theNumber.compareTo(BigDecimal.ZERO);
			if (cmp < 0) return XOMNumber.NaN;
			else if (cmp == 0) return XOMNumber.NEGATIVE_INFINITY;
			else {
				BigDecimal d = mp.log10(theNumber, mc);
				if (d == null) return XOMNumber.NaN;
				else return new XOMNumber(d);
			}
		}
	}
	
	public static XOMNumber sin(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMNumber.NaN;
		return new XOMNumber(mp.sin(n.toBigDecimal(), mc));
	}
	
	public static XOMNumber cos(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMNumber.NaN;
		return new XOMNumber(mp.cos(n.toBigDecimal(), mc));
	}
	
	public static XOMNumber tan(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMNumber.NaN;
		else {
			BigDecimal theNumber = n.toBigDecimal();
			BigDecimal s = mp.sin(theNumber, mc);
			BigDecimal c = mp.cos(theNumber, mc);
			int sc = s.compareTo(BigDecimal.ZERO);
			int cc = c.compareTo(BigDecimal.ZERO);
			if (sc == 0) return XOMNumber.ZERO;
			else if (cc == 0) return (sc < 0) ? XOMNumber.NEGATIVE_INFINITY : XOMNumber.POSITIVE_INFINITY;
			else return new XOMNumber(s.divide(c, mc));
		}
	}
	
	public static XOMNumber cot(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMNumber.NaN;
		else {
			BigDecimal theNumber = n.toBigDecimal();
			BigDecimal c = mp.cos(theNumber, mc);
			BigDecimal s = mp.sin(theNumber, mc);
			int cc = c.compareTo(BigDecimal.ZERO);
			int sc = s.compareTo(BigDecimal.ZERO);
			if (cc == 0) return XOMNumber.ZERO;
			else if (sc == 0) return (cc < 0) ? XOMNumber.NEGATIVE_INFINITY : XOMNumber.POSITIVE_INFINITY;
			else return new XOMNumber(c.divide(s, mc));
		}
	}
	
	public static XOMNumber csc(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMNumber.NaN;
		BigDecimal d = mp.sin(n.toBigDecimal(), mc);
		if (d.compareTo(BigDecimal.ZERO) == 0) return XOMNumber.POSITIVE_INFINITY;
		else return new XOMNumber(BigDecimal.ONE.divide(d, mc));
	}
	
	public static XOMNumber sec(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMNumber.NaN;
		BigDecimal d = mp.cos(n.toBigDecimal(), mc);
		if (d.compareTo(BigDecimal.ZERO) == 0) return XOMNumber.POSITIVE_INFINITY;
		else return new XOMNumber(BigDecimal.ONE.divide(d, mc));
	}
	
	public static XOMNumber sinh(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.getSign() == XOMNumber.SIGN_NEGATIVE) return XOMNumber.NEGATIVE_INFINITY;
			else if (n.getSign() == XOMNumber.SIGN_POSITIVE) return XOMNumber.POSITIVE_INFINITY;
			else return XOMNumber.NaN;
		}
		return new XOMNumber(mp.sinh(n.toBigDecimal(), mc));
	}
	
	public static XOMNumber cosh(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.isInfinite()) return XOMNumber.POSITIVE_INFINITY;
			else return XOMNumber.NaN;
		}
		return new XOMNumber(mp.cosh(n.toBigDecimal(), mc));
	}
	
	public static XOMNumber tanh(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.getSign() == XOMNumber.SIGN_NEGATIVE) return XOMNumber.ONE.negate();
			else if (n.getSign() == XOMNumber.SIGN_POSITIVE) return XOMNumber.ONE;
			else return XOMNumber.NaN;
		}
		return new XOMNumber(mp.tanh(n.toBigDecimal(), mc));
	}
	
	public static XOMNumber coth(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.getSign() == XOMNumber.SIGN_NEGATIVE) return XOMNumber.ONE.negate();
			else if (n.getSign() == XOMNumber.SIGN_POSITIVE) return XOMNumber.ONE;
			else return XOMNumber.NaN;
		}
		else {
			BigDecimal d = mp.tanh(n.toBigDecimal(), mc);
			if (d.compareTo(BigDecimal.ZERO) == 0) return XOMNumber.POSITIVE_INFINITY;
			else return new XOMNumber(BigDecimal.ONE.divide(d, mc));
		}
	}
	
	public static XOMNumber csch(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.isInfinite()) return XOMNumber.ZERO;
			else return XOMNumber.NaN;
		}
		else {
			BigDecimal d = mp.sinh(n.toBigDecimal(), mc);
			if (d.compareTo(BigDecimal.ZERO) == 0) return XOMNumber.POSITIVE_INFINITY;
			else return new XOMNumber(BigDecimal.ONE.divide(d, mc));
		}
	}
	
	public static XOMNumber sech(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.isInfinite()) return XOMNumber.ZERO;
			else return XOMNumber.NaN;
		}
		else {
			BigDecimal d = mp.cosh(n.toBigDecimal(), mc);
			if (d.compareTo(BigDecimal.ZERO) == 0) return XOMNumber.POSITIVE_INFINITY;
			else return new XOMNumber(BigDecimal.ONE.divide(d, mc));
		}
	}
	
	public static XOMNumber asin(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMNumber.NaN;
		else {
			BigDecimal theNumber = n.toBigDecimal();
			if (theNumber.abs().compareTo(BigDecimal.ONE) > 0) return XOMNumber.NaN;
			else return new XOMNumber(mp.asin(theNumber, mc));
		}
	}
	
	public static XOMNumber acos(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMNumber.NaN;
		else {
			BigDecimal theNumber = n.toBigDecimal();
			if (theNumber.abs().compareTo(BigDecimal.ONE) > 0) return XOMNumber.NaN;
			else return new XOMNumber(mp.acos(theNumber, mc));
		}
	}
	
	public static XOMNumber atan(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.getSign() == XOMNumber.SIGN_NEGATIVE) return new XOMNumber(mp.pi(mc).divide(BigDecimal.valueOf(2.0)).negate());
			else if (n.getSign() == XOMNumber.SIGN_POSITIVE) return new XOMNumber(mp.pi(mc).divide(BigDecimal.valueOf(2.0)));
			else return XOMNumber.NaN;
		}
		else {
			return new XOMNumber(mp.atan(n.toBigDecimal(), mc));
		}
	}
	
	public static XOMNumber atan2(XOMNumber y, XOMNumber x, MathContext mc, MathProcessor mp) {
		if (y.isNaN() || x.isNaN()) return XOMNumber.NaN;
		else if (y.isInfinite() || x.isInfinite()) {
			if (y.isZero() && x.isInfinite()) {
				switch (x.getSign()) {
				case XOMNumber.SIGN_POSITIVE: return XOMNumber.ZERO;
				case XOMNumber.SIGN_NEGATIVE: return new XOMNumber(mp.pi(mc));
				default: return XOMNumber.NaN;
				}
			} else if (y.isInfinite() && x.isZero()) {
				switch (y.getSign()) {
				case XOMNumber.SIGN_POSITIVE: return new XOMNumber(mp.pi(mc).divide(BigDecimal.valueOf(2),mc));
				case XOMNumber.SIGN_NEGATIVE: return new XOMNumber(mp.pi(mc).divide(BigDecimal.valueOf(2),mc).negate());
				default: return XOMNumber.NaN;
				}
			} else {
				return XOMNumber.NaN;
			}
		}
		else {
			return new XOMNumber(mp.atan2(y.toBigDecimal(), x.toBigDecimal(), mc));
		}
	}
	
	public static XOMNumber acot(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.getSign() == XOMNumber.SIGN_NEGATIVE) return new XOMNumber(mp.pi(mc));
			else if (n.getSign() == XOMNumber.SIGN_POSITIVE) return XOMNumber.ZERO;
			else return XOMNumber.NaN;
		} else {
			return new XOMNumber(mp.pi(mc).divide(BigDecimal.valueOf(2),mc).subtract(mp.atan(n.toBigDecimal(),mc),mc));
		}
	}
	
	public static XOMNumber acsc(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.isInfinite()) return XOMNumber.ZERO;
			else return XOMNumber.NaN;
		} else {
			BigDecimal theNumber = n.toBigDecimal();
			if (theNumber.abs().compareTo(BigDecimal.ONE) < 0) return XOMNumber.NaN;
			else return new XOMNumber(mp.asin(BigDecimal.ONE.divide(theNumber, mc), mc));
		}
	}
	
	public static XOMNumber asec(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.isInfinite()) return new XOMNumber(mp.pi(mc).divide(BigDecimal.valueOf(2),mc));
			else return XOMNumber.NaN;
		} else {
			BigDecimal theNumber = n.toBigDecimal();
			if (theNumber.abs().compareTo(BigDecimal.ONE) < 0) return XOMNumber.NaN;
			else return new XOMNumber(mp.acos(BigDecimal.ONE.divide(theNumber, mc), mc));
		}
	}
	
	public static XOMNumber asinh(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return n;
		else {
			BigDecimal x = n.toBigDecimal();
			BigDecimal sqrtxsqp1 = mp.sqrt(x.multiply(x,mc).add(BigDecimal.ONE,mc),mc);
			BigDecimal asinh = mp.log(x.add(sqrtxsqp1,mc),mc);
			if (asinh == null) return XOMNumber.NaN;
			else return new XOMNumber(asinh);
		}
	}
	
	public static XOMNumber acosh(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.getSign() == XOMNumber.SIGN_POSITIVE) return n;
			else return XOMNumber.NaN;
		}
		else {
			int cmp = n.toBigDecimal().compareTo(BigDecimal.ONE);
			if (cmp < 0) return XOMNumber.NaN;
			else if (cmp == 0) return XOMNumber.ZERO;
			BigDecimal x = n.toBigDecimal();
			BigDecimal sqrtxm1 = mp.sqrt(x.subtract(BigDecimal.ONE,mc),mc);
			BigDecimal sqrtxp1 = mp.sqrt(x.add(BigDecimal.ONE,mc),mc);
			BigDecimal xm1txp1 = sqrtxm1.multiply(sqrtxp1,mc);
			BigDecimal acosh = mp.log(x.add(xm1txp1,mc),mc);
			if (acosh == null) return XOMNumber.NaN;
			else return new XOMNumber(acosh);
		}
	}
	
	public static XOMNumber atanh(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMNumber.NaN;
		else {
			int cmp = n.toBigDecimal().abs().compareTo(BigDecimal.ONE);
			if (cmp > 0) return XOMNumber.NaN;
			else if (cmp == 0) return (n.toBigDecimal().signum() < 0) ? XOMNumber.NEGATIVE_INFINITY : XOMNumber.POSITIVE_INFINITY;
			BigDecimal x = n.toBigDecimal();
			BigDecimal onemx = BigDecimal.ONE.subtract(x,mc);
			BigDecimal sqrt1mx2 = mp.sqrt(BigDecimal.ONE.subtract(x.multiply(x,mc),mc),mc);
			BigDecimal atanh = mp.log(sqrt1mx2.divide(onemx,mc),mc);
			if (atanh == null) return XOMNumber.NaN;
			else return new XOMNumber(atanh);
		}
	}
	
	public static XOMNumber acoth(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.isInfinite()) return XOMNumber.ZERO;
			else return XOMNumber.NaN;
		}
		else {
			int cmp = n.toBigDecimal().abs().compareTo(BigDecimal.ONE);
			if (cmp < 0) return XOMNumber.NaN;
			else if (cmp == 0) return (n.toBigDecimal().signum() < 0) ? XOMNumber.NEGATIVE_INFINITY : XOMNumber.POSITIVE_INFINITY;
			BigDecimal x = n.toBigDecimal();
			BigDecimal a = x.add(BigDecimal.ONE,mc).divide(x.subtract(BigDecimal.ONE,mc),mc);
			BigDecimal acoth = mp.log(a,mc).divide(BigDecimal.valueOf(2));
			if (acoth == null) return XOMNumber.NaN;
			else return new XOMNumber(acoth);
		}
	}
	
	public static XOMNumber acsch(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.isInfinite()) return XOMNumber.ZERO;
			else return XOMNumber.NaN;
		}
		else {
			if (n.isZero()) return XOMNumber.NaN;
			BigDecimal x = n.toBigDecimal();
			BigDecimal rx = BigDecimal.ONE.divide(x,mc);
			BigDecimal rxx = BigDecimal.ONE.divide(x.multiply(x,mc),mc);
			BigDecimal sqrt1prxx = mp.sqrt(BigDecimal.ONE.add(rxx,mc),mc);
			BigDecimal acsch = mp.log(sqrt1prxx.add(rx,mc),mc);
			if (acsch == null) return XOMNumber.NaN;
			else return new XOMNumber(acsch);
		}
	}
	
	public static XOMNumber asech(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) return XOMNumber.NaN;
		else {
			if (n.getSign() == XOMNumber.SIGN_NEGATIVE) return XOMNumber.NaN;
			else if (n.isZero()) return XOMNumber.POSITIVE_INFINITY;
			int cmp = n.toBigDecimal().abs().compareTo(BigDecimal.ONE);
			if (cmp > 0) return XOMNumber.NaN;
			else if (cmp == 0) return XOMNumber.ZERO;
			BigDecimal x = n.toBigDecimal();
			BigDecimal rx = BigDecimal.ONE.divide(x,mc);
			BigDecimal sqrtrxm1 = mp.sqrt(rx.subtract(BigDecimal.ONE,mc),mc);
			BigDecimal sqrtrxp1 = mp.sqrt(rx.add(BigDecimal.ONE,mc),mc);
			BigDecimal rxm1trxp1 = sqrtrxm1.multiply(sqrtrxp1,mc);
			BigDecimal asech = mp.log(rxm1trxp1.add(rx,mc),mc);
			if (asech == null) return XOMNumber.NaN;
			else return new XOMNumber(asech);
		}
	}
	
	public static XOMNumber gamma(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.getSign() == XOMNumber.SIGN_POSITIVE) return XOMNumber.POSITIVE_INFINITY;
			else return XOMNumber.NaN;
		}
		else if (n.isZero()) return XOMNumber.POSITIVE_INFINITY;
		else {
			BigDecimal d = mp.gamma(n.toBigDecimal(), mc);
			if (d == null) return XOMNumber.NaN;
			else return new XOMNumber(d);
		}
	}
	
	public static XOMNumber loggamma(XOMNumber n, MathContext mc, MathProcessor mp) {
		if (n.isUndefined()) {
			if (n.getSign() == XOMNumber.SIGN_POSITIVE) return XOMNumber.POSITIVE_INFINITY;
			else return XOMNumber.NaN;
		}
		else if (n.isZero()) return XOMNumber.POSITIVE_INFINITY;
		else {
			BigDecimal d = mp.loggamma(n.toBigDecimal(), mc);
			if (d == null) return XOMNumber.NaN;
			else return new XOMNumber(d);
		}
	}
	
	public static XOMNumber fact(XOMNumber n, MathContext mc, MathProcessor mp) {
		return gamma(add(n,XOMNumber.ONE,mc,mp),mc,mp);
	}
	
	public static XOMNumber logfact(XOMNumber n, MathContext mc, MathProcessor mp) {
		return loggamma(add(n,XOMNumber.ONE,mc,mp),mc,mp);
	}
	
	public static XOMNumber beta(XOMNumber a, XOMNumber b, MathContext mc, MathProcessor mp) {
		return divide(multiply(gamma(a,mc,mp),gamma(b,mc,mp),mc,mp),gamma(add(a,b,mc,mp),mc,mp),mc,mp);
	}
	
	public static XOMNumber logbeta(XOMNumber a, XOMNumber b, MathContext mc, MathProcessor mp) {
		return subtract(add(loggamma(a,mc,mp),loggamma(b,mc,mp),mc,mp),loggamma(add(a,b,mc,mp),mc,mp),mc,mp);
	}
	
	public static XOMNumber nPr(XOMNumber n, XOMNumber r, MathContext mc, MathProcessor mp) {
		return divide(gamma(add(n,XOMNumber.ONE,mc,mp),mc,mp),gamma(add(subtract(n,r,mc,mp),XOMNumber.ONE,mc,mp),mc,mp),mc,mp);
	}
	
	public static XOMNumber nCr(XOMNumber n, XOMNumber r, MathContext mc, MathProcessor mp) {
		return divide(divide(gamma(add(n,XOMNumber.ONE,mc,mp),mc,mp),gamma(add(r,XOMNumber.ONE,mc,mp),mc,mp),mc,mp),gamma(add(subtract(n,r,mc,mp),XOMNumber.ONE,mc,mp),mc,mp),mc,mp);
	}
}
