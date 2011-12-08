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

public class XOMNumber extends XOMValue {
	private static final long serialVersionUID = 1L;
	
	public static final BigDecimal BIGDECIMAL_PI = new BigDecimal("3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117068");
	public static final BigDecimal BIGDECIMAL_E = new BigDecimal("2.718281828459045235360287471352662497757247093699959574966967627724076630353547594571382178525166427");
	public static final BigDecimal BIGDECIMAL_PHI = new BigDecimal("1.618033988749894848204586834365638117720309179805762862135448622705260462818902449707207204189391137");
	
	public static final XOMNumber ZERO = new XOMNumber(BigDecimal.ZERO);
	public static final XOMNumber ONE = new XOMNumber(BigDecimal.ONE);
	public static final XOMNumber TEN = new XOMNumber(BigDecimal.TEN);
	public static final XOMNumber PI = new XOMNumber(BIGDECIMAL_PI);
	public static final XOMNumber E = new XOMNumber(BIGDECIMAL_E);
	public static final XOMNumber PHI = new XOMNumber(BIGDECIMAL_PHI);
	public static final XOMNumber POSITIVE_INFINITY = new XOMNumber(false, false);
	public static final XOMNumber NEGATIVE_INFINITY = new XOMNumber(false, true);
	public static final XOMNumber NaN = new XOMNumber(true, false);
	
	private BigDecimal theNumber;
	private boolean undefined;
	
	public XOMNumber(Number n) {
		if (n == null) {
			this.theNumber = BigDecimal.ZERO;
			this.undefined = true;
		} else if (n instanceof BigDecimal) {
			this.theNumber = (BigDecimal)n;
			this.undefined = false;
		} else if (n instanceof BigInteger) {
			this.theNumber = new BigDecimal((BigInteger)n);
			this.undefined = false;
		} else if (n instanceof Double) {
			double d = n.doubleValue();
			if (Double.isNaN(d) || Double.isInfinite(d)) {
				this.theNumber = Double.isNaN(d) ? BigDecimal.ZERO : (d < 0) ? BigDecimal.ONE.negate() : BigDecimal.ONE;
				this.undefined = true;
			} else {
				this.theNumber = BigDecimal.valueOf(d);
				this.undefined = false;
			}
		} else if (n instanceof Float) {
			float f = n.floatValue();
			if (Float.isNaN(f) || Float.isInfinite(f)) {
				this.theNumber = Float.isNaN(f) ? BigDecimal.ZERO : (f < 0) ? BigDecimal.ONE.negate() : BigDecimal.ONE;
				this.undefined = true;
			} else {
				this.theNumber = new BigDecimal(Float.toString(f));
				this.undefined = false;
			}
		} else if (n instanceof Long || n instanceof Integer || n instanceof Short || n instanceof Byte) {
			this.theNumber = BigDecimal.valueOf(n.longValue());
			this.undefined = false;
		} else {
			double d = n.doubleValue();
			if (Double.isNaN(d) || Double.isInfinite(d)) {
				this.theNumber = Double.isNaN(d) ? BigDecimal.ZERO : (d < 0) ? BigDecimal.ONE.negate() : BigDecimal.ONE;
				this.undefined = true;
			} else {
				this.theNumber = BigDecimal.valueOf(d);
				this.undefined = false;
			}
		}
	}
	
	private XOMNumber(boolean nan, boolean neg) {
		this.theNumber = nan ? BigDecimal.ZERO : neg ? BigDecimal.ONE.negate() : BigDecimal.ONE;
		this.undefined = true;
	}
	
	public boolean isUndefined() {
		return theNumber == null || undefined;
	}
	
	public boolean isNaN() {
		return theNumber == null || (undefined && (theNumber.compareTo(BigDecimal.ZERO) == 0));
	}
	
	public boolean isInfinite() {
		return theNumber != null && (undefined && !(theNumber.compareTo(BigDecimal.ZERO) == 0));
	}
	
	public boolean isZero() {
		return theNumber != null && !undefined && (theNumber.compareTo(BigDecimal.ZERO) == 0);
	}
	
	public static final int SIGN_NaN = Integer.MIN_VALUE;
	public static final int SIGN_NEGATIVE = -1;
	public static final int SIGN_ZERO = 0;
	public static final int SIGN_POSITIVE = 1;
	
	public int getSign() {
		if (theNumber == null) return SIGN_NaN;
		else if (undefined) {
			int cmp = theNumber.compareTo(BigDecimal.ZERO);
			if (cmp < 0) return SIGN_NEGATIVE;
			else if (cmp > 0) return SIGN_POSITIVE;
			else return SIGN_NaN;
		}
		else {
			int cmp = theNumber.compareTo(BigDecimal.ZERO);
			if (cmp < 0) return SIGN_NEGATIVE;
			else if (cmp > 0) return SIGN_POSITIVE;
			else return SIGN_ZERO;
		}
	}
	
	public int getOppositeSign() {
		if (theNumber == null) return SIGN_NaN;
		else if (undefined) {
			int cmp = theNumber.compareTo(BigDecimal.ZERO);
			if (cmp < 0) return SIGN_POSITIVE;
			else if (cmp > 0) return SIGN_NEGATIVE;
			else return SIGN_NaN;
		}
		else {
			int cmp = theNumber.compareTo(BigDecimal.ZERO);
			if (cmp < 0) return SIGN_POSITIVE;
			else if (cmp > 0) return SIGN_NEGATIVE;
			else return SIGN_ZERO;
		}
	}
	
	public XOMNumber abs() {
		if (theNumber == null) return NaN;
		else if (undefined) {
			int cmp = theNumber.compareTo(BigDecimal.ZERO);
			if (cmp == 0) return NaN;
			else return POSITIVE_INFINITY;
		}
		else return new XOMNumber(theNumber.abs());
	}
	
	public XOMNumber negate() {
		if (theNumber == null) return NaN;
		else if (undefined) {
			int cmp = theNumber.compareTo(BigDecimal.ZERO);
			if (cmp < 0) return POSITIVE_INFINITY;
			else if (cmp > 0) return NEGATIVE_INFINITY;
			else return NaN;
		}
		else return new XOMNumber(theNumber.negate());
	}
	
	public XOMNumber signum() {
		if (theNumber == null) return NaN;
		else if (undefined) {
			int cmp = theNumber.compareTo(BigDecimal.ZERO);
			if (cmp > 0) return ONE;
			else if (cmp < 0) return ONE.negate();
			else return NaN;
		}
		else {
			int cmp = theNumber.compareTo(BigDecimal.ZERO);
			if (cmp > 0) return ONE;
			else if (cmp < 0) return ONE.negate();
			else return ZERO;
		}
	}
	
	public XOMNumber ceil() {
		return round(RoundingMode.CEILING);
	}
	
	public XOMNumber floor() {
		return round(RoundingMode.FLOOR);
	}
	
	public XOMNumber round() {
		return round(RoundingMode.HALF_UP);
	}
	
	public XOMNumber aug() {
		return round(RoundingMode.UP);
	}
	
	public XOMNumber trunc() {
		return round(RoundingMode.DOWN);
	}
	
	public XOMNumber rint() {
		return round(RoundingMode.HALF_EVEN);
	}
	
	public XOMNumber round(RoundingMode rm) {
		if (theNumber == null || undefined) return this;
		else return new XOMNumber(theNumber.divide(BigDecimal.ONE, 0, rm));
	}
	
	public XOMNumber frac() {
		if (theNumber == null || undefined) return NaN;
		else return new XOMNumber(theNumber.subtract(theNumber.divide(BigDecimal.ONE, 0, RoundingMode.DOWN)));
	}
	
	public Number toNumber() {
		if (theNumber == null) return Double.NaN;
		else if (undefined) {
			int cmp = theNumber.compareTo(BigDecimal.ZERO);
			if (cmp < 0) return Double.NEGATIVE_INFINITY;
			else if (cmp > 0) return Double.POSITIVE_INFINITY;
			else return Double.NaN;
		}
		else return theNumber;
	}
	
	public BigDecimal toBigDecimal() {
		if (theNumber == null || undefined) return null;
		else return theNumber;
	}
	
	public double toDouble() {
		if (theNumber == null) return Double.NaN;
		else if (undefined) {
			int cmp = theNumber.compareTo(BigDecimal.ZERO);
			if (cmp < 0) return Double.NEGATIVE_INFINITY;
			else if (cmp > 0) return Double.POSITIVE_INFINITY;
			else return Double.NaN;
		}
		else return theNumber.doubleValue();
	}
	
	public long toLong() {
		if (theNumber == null || undefined) return 0;
		else return theNumber.longValue();
	}
	
	public int toInt() {
		if (theNumber == null || undefined) return 0;
		else return theNumber.intValue();
	}
	
	public String toLanguageString() {
		if (theNumber == null) return "NAN";
		else if (undefined) {
			int cmp = theNumber.compareTo(BigDecimal.ZERO);
			if (cmp < 0) return "-INF";
			else if (cmp > 0) return "INF";
			else return "NAN";
		}
		else return theNumber.toString()
			.replaceAll("[Ee][-]([0-9]+)", "''$1")
			.replaceAll("[Ee][+]?([0-9]+)", "'$1");
	}
	public String toTextString(XNContext ctx) {
		if (theNumber == null) return "NAN";
		else if (undefined) {
			int cmp = theNumber.compareTo(BigDecimal.ZERO);
			if (cmp < 0) return "-INF";
			else if (cmp > 0) return "INF";
			else return "NAN";
		}
		else return ctx.getNumberFormat().format(theNumber);
	}
	public List<? extends XOMVariant> toVariantList(XNContext ctx) {
		return Arrays.asList(this);
	}
	public List<? extends XOMVariant> toPrimitiveList(XNContext ctx) {
		return Arrays.asList(this);
	}
	public int hashCode() {
		return (theNumber == null) ? 0 : undefined ? theNumber.signum() : theNumber.hashCode();
	}
	public boolean equals(Object o) {
		if (o instanceof XOMNumber) {
			XOMNumber other = (XOMNumber)o;
			if (this.isNaN() && other.isNaN()) {
				return true;
			}
			else if (this.isNaN() || other.isNaN()) {
				return false;
			}
			else if (this.undefined && other.undefined) {
				return (this.theNumber.compareTo(BigDecimal.ZERO) == other.theNumber.compareTo(BigDecimal.ZERO));
			}
			else if (this.undefined || other.undefined) {
				return false;
			}
			else {
				return this.theNumber.compareTo(other.theNumber) == 0;
			}
		} else {
			return false;
		}
	}
}
