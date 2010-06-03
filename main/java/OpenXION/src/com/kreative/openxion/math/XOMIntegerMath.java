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

import java.util.Comparator;
import com.kreative.openxion.xom.inst.XOMInteger;

/**
 * Methods for mathematical operations on and functions of XOMIntegers.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XOMIntegerMath {
	private XOMIntegerMath(){}
	
	public static final Comparator<XOMInteger> comparator = new Comparator<XOMInteger>() {
		public int compare(XOMInteger o1, XOMInteger o2) {
			return XOMIntegerMath.compare(o1,o2);
		}
	};
	
	public static int compare(XOMInteger a, XOMInteger b) {
		if (a.isUndefined() || b.isUndefined()) {
			int am, bm;
			
			if (a.isNaN()) am = 3;
			else if (a.isInfinite()) switch (a.getSign()) {
			case XOMInteger.SIGN_NaN: am = 3; break;
			case XOMInteger.SIGN_NEGATIVE: am = -2; break;
			case XOMInteger.SIGN_POSITIVE: am = 2; break;
			case XOMInteger.SIGN_ZERO: am = 3; break;
			default: am = 3; break;
			}
			else if (a.isUndefined()) am = 3;
			else if (a.isZero()) am = 0;
			else switch (a.getSign()) {
			case XOMInteger.SIGN_NaN: am = 3; break;
			case XOMInteger.SIGN_NEGATIVE: am = -1; break;
			case XOMInteger.SIGN_POSITIVE: am = 1; break;
			case XOMInteger.SIGN_ZERO: am = 0; break;
			default: am = 3; break;
			}
			
			if (b.isNaN()) bm = 3;
			else if (b.isInfinite()) switch (b.getSign()) {
			case XOMInteger.SIGN_NaN: bm = 3; break;
			case XOMInteger.SIGN_NEGATIVE: bm = -2; break;
			case XOMInteger.SIGN_POSITIVE: bm = 2; break;
			case XOMInteger.SIGN_ZERO: bm = 3; break;
			default: bm = 3; break;
			}
			else if (b.isUndefined()) bm = 3;
			else if (b.isZero()) bm = 0;
			else switch (b.getSign()) {
			case XOMInteger.SIGN_NaN: bm = 3; break;
			case XOMInteger.SIGN_NEGATIVE: bm = -1; break;
			case XOMInteger.SIGN_POSITIVE: bm = 1; break;
			case XOMInteger.SIGN_ZERO: bm = 0; break;
			default: bm = 3; break;
			}
			
			return am-bm;
		} else {
			return a.toBigInteger().compareTo(b.toBigInteger());
		}
	}
	
	public static XOMInteger add(XOMInteger a, XOMInteger b) {
		if (a.isNaN() || b.isNaN()) return XOMInteger.NaN;
		else if (a.isInfinite() && b.isInfinite()) {
			if (a.getSign() == b.getSign()) return a;
			else return XOMInteger.NaN;
		}
		else if (a.isInfinite()) {
			return a;
		}
		else if (b.isInfinite()) {
			return b;
		}
		else {
			return new XOMInteger(a.toBigInteger().add(b.toBigInteger()));
		}
	}
	
	public static XOMInteger subtract(XOMInteger a, XOMInteger b) {
		if (a.isNaN() || b.isNaN()) return XOMInteger.NaN;
		else if (a.isInfinite() && b.isInfinite()) {
			if (a.getSign() == b.getOppositeSign()) return a;
			else return XOMInteger.NaN;
		}
		else if (a.isInfinite()) {
			return a;
		}
		else if (b.isInfinite()) {
			return b.negate();
		}
		else {
			return new XOMInteger(a.toBigInteger().subtract(b.toBigInteger()));
		}
	}
	
	public static XOMInteger multiply(XOMInteger a, XOMInteger b) {
		if (a.isNaN() || b.isNaN()) return XOMInteger.NaN;
		else if (a.isInfinite() || b.isInfinite()) {
			if (a.isZero() || b.isZero()) return XOMInteger.NaN;
			else if (a.getSign() == b.getSign()) return XOMInteger.POSITIVE_INFINITY;
			else return XOMInteger.NEGATIVE_INFINITY;
		}
		else {
			return new XOMInteger(a.toBigInteger().multiply(b.toBigInteger()));
		}
	}
	
	public static XOMInteger divide(XOMInteger a, XOMInteger b) {
		if (a.isNaN() || b.isNaN()) return XOMInteger.NaN;
		else if ((a.isInfinite() && b.isInfinite()) || (a.isZero() && b.isZero())) {
			return XOMInteger.NaN;
		}
		else if (b.isZero()) {
			switch (a.getSign()) {
			case XOMInteger.SIGN_POSITIVE: return XOMInteger.POSITIVE_INFINITY;
			case XOMInteger.SIGN_NEGATIVE: return XOMInteger.NEGATIVE_INFINITY;
			default: return XOMInteger.NaN;
			}
		}
		else if (a.isInfinite()) {
			if (b.isZero()) return a;
			else if (a.getSign() == b.getSign()) return XOMInteger.POSITIVE_INFINITY;
			else return XOMInteger.NEGATIVE_INFINITY;
		}
		else if (b.isInfinite() || a.isZero()) {
			return XOMInteger.ZERO;
		}
		else {
			return new XOMInteger(a.toBigInteger().divide(b.toBigInteger()));
		}
	}
}
