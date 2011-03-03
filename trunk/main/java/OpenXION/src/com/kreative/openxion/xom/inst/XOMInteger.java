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

package com.kreative.openxion.xom.inst;

import java.math.*;
import java.util.Arrays;
import java.util.List;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.xom.XOMValue;
import com.kreative.openxion.xom.XOMVariant;

public class XOMInteger extends XOMValue {
	private static final long serialVersionUID = 1L;
	
	public static final XOMInteger ZERO = new XOMInteger(BigInteger.ZERO);
	public static final XOMInteger ONE = new XOMInteger(BigInteger.ONE);
	public static final XOMInteger TEN = new XOMInteger(BigInteger.TEN);
	public static final XOMInteger POSITIVE_INFINITY = new XOMInteger(false, false);
	public static final XOMInteger NEGATIVE_INFINITY = new XOMInteger(false, true);
	public static final XOMInteger NaN = new XOMInteger(true, false);
	
	private BigInteger theInteger;
	private boolean undefined;
	
	public XOMInteger(Number n) {
		if (n instanceof BigInteger) {
			this.theInteger = (BigInteger)n;
			this.undefined = false;
		} else if (n instanceof BigDecimal) {
			this.theInteger = ((BigDecimal)n).toBigInteger();
			this.undefined = false;
		} else if (n instanceof Double) {
			double d = n.doubleValue();
			if (Double.isNaN(d) || Double.isInfinite(d)) {
				this.theInteger = Double.isNaN(d) ? BigInteger.ZERO : (d < 0) ? BigInteger.ONE.negate() : BigInteger.ONE;
				this.undefined = true;
			} else {
				this.theInteger = BigDecimal.valueOf(d).toBigInteger();
				this.undefined = false;
			}
		} else if (n instanceof Float) {
			float f = n.floatValue();
			if (Float.isNaN(f) || Float.isInfinite(f)) {
				this.theInteger = Float.isNaN(f) ? BigInteger.ZERO : (f < 0) ? BigInteger.ONE.negate() : BigInteger.ONE;
				this.undefined = true;
			} else {
				this.theInteger = new BigDecimal(Float.toString(f)).toBigInteger();
				this.undefined = false;
			}
		} else {
			this.theInteger = BigInteger.valueOf(n.longValue());
			this.undefined = false;
		}
	}
	
	private XOMInteger(boolean nan, boolean neg) {
		this.theInteger = nan ? BigInteger.ZERO : neg ? BigInteger.ONE.negate() : BigInteger.ONE;
		this.undefined = true;
	}
	
	public boolean isUndefined() {
		return theInteger == null || undefined;
	}
	
	public boolean isNaN() {
		return theInteger == null || (undefined && theInteger.equals(BigInteger.ZERO));
	}
	
	public boolean isInfinite() {
		return theInteger != null && (undefined && !theInteger.equals(BigInteger.ZERO));
	}
	
	public boolean isZero() {
		return theInteger != null && !undefined && theInteger.equals(BigInteger.ZERO);
	}
	
	public static final int SIGN_NaN = Integer.MIN_VALUE;
	public static final int SIGN_NEGATIVE = -1;
	public static final int SIGN_ZERO = 0;
	public static final int SIGN_POSITIVE = 1;
	
	public int getSign() {
		if (theInteger == null) return SIGN_NaN;
		else if (undefined) {
			int cmp = theInteger.compareTo(BigInteger.ZERO);
			if (cmp < 0) return SIGN_NEGATIVE;
			else if (cmp > 0) return SIGN_POSITIVE;
			else return SIGN_NaN;
		}
		else {
			int cmp = theInteger.compareTo(BigInteger.ZERO);
			if (cmp < 0) return SIGN_NEGATIVE;
			else if (cmp > 0) return SIGN_POSITIVE;
			else return SIGN_ZERO;
		}
	}
	
	public int getOppositeSign() {
		if (theInteger == null) return SIGN_NaN;
		else if (undefined) {
			int cmp = theInteger.compareTo(BigInteger.ZERO);
			if (cmp < 0) return SIGN_POSITIVE;
			else if (cmp > 0) return SIGN_NEGATIVE;
			else return SIGN_NaN;
		}
		else {
			int cmp = theInteger.compareTo(BigInteger.ZERO);
			if (cmp < 0) return SIGN_POSITIVE;
			else if (cmp > 0) return SIGN_NEGATIVE;
			else return SIGN_ZERO;
		}
	}
	
	public XOMInteger abs() {
		if (theInteger == null) return NaN;
		else if (undefined) {
			int cmp = theInteger.compareTo(BigInteger.ZERO);
			if (cmp == 0) return NaN;
			else return POSITIVE_INFINITY;
		}
		else return new XOMInteger(theInteger.abs());
	}
	
	public XOMInteger negate() {
		if (theInteger == null) return NaN;
		else if (undefined) {
			int cmp = theInteger.compareTo(BigInteger.ZERO);
			if (cmp < 0) return POSITIVE_INFINITY;
			else if (cmp > 0) return NEGATIVE_INFINITY;
			else return NaN;
		}
		else return new XOMInteger(theInteger.negate());
	}
	
	public XOMInteger signum() {
		if (theInteger == null) return NaN;
		else if (undefined) {
			int cmp = theInteger.compareTo(BigInteger.ZERO);
			if (cmp > 0) return ONE;
			else if (cmp < 0) return ONE.negate();
			else return NaN;
		}
		else {
			int cmp = theInteger.compareTo(BigInteger.ZERO);
			if (cmp > 0) return ONE;
			else if (cmp < 0) return ONE.negate();
			else return ZERO;
		}
	}
	
	public Number toNumber() {
		if (theInteger == null) return Double.NaN;
		else if (undefined) {
			int cmp = theInteger.compareTo(BigInteger.ZERO);
			if (cmp < 0) return Double.NEGATIVE_INFINITY;
			else if (cmp > 0) return Double.POSITIVE_INFINITY;
			else return Double.NaN;
		}
		else return theInteger;
	}
	
	public BigInteger toBigInteger() {
		if (theInteger == null || undefined) return null;
		else return theInteger;
	}
	
	public double toDouble() {
		if (theInteger == null) return Double.NaN;
		else if (undefined) {
			int cmp = theInteger.compareTo(BigInteger.ZERO);
			if (cmp < 0) return Double.NEGATIVE_INFINITY;
			else if (cmp > 0) return Double.POSITIVE_INFINITY;
			else return Double.NaN;
		}
		else return theInteger.doubleValue();
	}
	
	public long toLong() {
		if (theInteger == null || undefined) return 0;
		else return theInteger.longValue();
	}
	
	public int toInt() {
		if (theInteger == null || undefined) return 0;
		else return theInteger.intValue();
	}
	
	public String toLanguageString() {
		if (theInteger == null) return "NAN";
		else if (undefined) {
			int cmp = theInteger.compareTo(BigInteger.ZERO);
			if (cmp < 0) return "-INF";
			else if (cmp > 0) return "INF";
			else return "NAN";
		}
		else return theInteger.toString();
	}
	public String toTextString(XNContext ctx) {
		if (theInteger == null) return "NAN";
		else if (undefined) {
			int cmp = theInteger.compareTo(BigInteger.ZERO);
			if (cmp < 0) return "-INF";
			else if (cmp > 0) return "INF";
			else return "NAN";
		}
		else return ctx.getNumberFormat().format(theInteger);
	}
	public List<? extends XOMVariant> toVariantList(XNContext ctx) {
		return Arrays.asList(this);
	}
	public List<? extends XOMVariant> toPrimitiveList(XNContext ctx) {
		return Arrays.asList(this);
	}
	public int hashCode() {
		return (theInteger == null) ? 0 : undefined ? theInteger.signum() : theInteger.hashCode();
	}
	public boolean equals(Object o) {
		if (o instanceof XOMInteger) {
			XOMInteger other = (XOMInteger)o;
			if (this.isNaN() && other.isNaN()) {
				return true;
			}
			else if (this.isNaN() || other.isNaN()) {
				return false;
			}
			else if (this.undefined && other.undefined) {
				return (this.theInteger.compareTo(BigInteger.ZERO) == other.theInteger.compareTo(BigInteger.ZERO));
			}
			else if (this.undefined || other.undefined) {
				return false;
			}
			else {
				return this.theInteger.compareTo(other.theInteger) == 0;
			}
		} else {
			return false;
		}
	}
}
