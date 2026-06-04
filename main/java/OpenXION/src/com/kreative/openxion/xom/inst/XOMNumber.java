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

package com.kreative.openxion.xom.inst;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.xom.XOMValue;
import com.kreative.openxion.xom.XOMVariant;

public class XOMNumber extends XOMValue implements Comparable<XOMNumber> {
	private static final long serialVersionUID = 1L;
	
	public static final BigDecimal BIGDECIMAL_PI = new BigDecimal("3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117068");
	public static final BigDecimal BIGDECIMAL_E = new BigDecimal("2.718281828459045235360287471352662497757247093699959574966967627724076630353547594571382178525166427");
	public static final BigDecimal BIGDECIMAL_PHI = new BigDecimal("1.618033988749894848204586834365638117720309179805762862135448622705260462818902449707207204189391137");
	public static final BigDecimal BIGDECIMAL_GAMMA = new BigDecimal("0.5772156649015328606065120900824024310421593359399235988057672348848677267776646709369470632917467495");
	
	public static final XOMNumber ZERO = new XOMNumber(0);
	public static final XOMNumber ONE = new XOMNumber(1);
	public static final XOMNumber TWO = new XOMNumber(2);
	public static final XOMNumber THREE = new XOMNumber(3);
	public static final XOMNumber FOUR = new XOMNumber(4);
	public static final XOMNumber TEN = new XOMNumber(10);
	public static final XOMNumber TWELVE = new XOMNumber(12);
	public static final XOMNumber ONE_EIGHTY = new XOMNumber(180);
	public static final XOMNumber PI = new XOMNumber(BIGDECIMAL_PI);
	public static final XOMNumber E = new XOMNumber(BIGDECIMAL_E);
	public static final XOMNumber PHI = new XOMNumber(BIGDECIMAL_PHI);
	public static final XOMNumber GAMMA = new XOMNumber(BIGDECIMAL_GAMMA);
	public static final XOMNumber NEGATIVE_ONE = new XOMNumber(-1);
	public static final XOMNumber POSITIVE_INFINITY = new XOMNumber(Double.POSITIVE_INFINITY);
	public static final XOMNumber NEGATIVE_INFINITY = new XOMNumber(Double.NEGATIVE_INFINITY);
	public static final XOMNumber NaN = new XOMNumber(null);
	
	private final BigDecimal bigValue;
	private final double doubleValue;
	
	public XOMNumber(Number n) {
		if (n == null) {
			this.bigValue = null;
			this.doubleValue = Double.NaN;
		} else if (n instanceof BigDecimal) {
			this.bigValue = (BigDecimal)n;
			this.doubleValue = bigValue.doubleValue();
		} else if (n instanceof BigInteger) {
			this.bigValue = new BigDecimal((BigInteger)n);
			this.doubleValue = bigValue.doubleValue();
		} else if (n instanceof Double || n instanceof Float) {
			double v = n.doubleValue();
			if (Double.isNaN(v) || Double.isInfinite(v)) {
				this.bigValue = null;
				this.doubleValue = v;
			} else if (v == 0) {
				this.bigValue = BigDecimal.ZERO;
				this.doubleValue = v;
			} else {
				this.bigValue = BigDecimal.valueOf(v);
				this.doubleValue = bigValue.doubleValue();
			}
		} else if (n instanceof Long || n instanceof Integer || n instanceof Short || n instanceof Byte) {
			this.bigValue = BigDecimal.valueOf(n.longValue());
			this.doubleValue = bigValue.doubleValue();
		} else {
			throw new IllegalArgumentException("unknown subclass of java.lang.Number: " + n.getClass());
		}
	}
	
	public boolean isNaN() {
		return bigValue == null && Double.isNaN(doubleValue);
	}
	
	public boolean isInfinite() {
		return bigValue == null && Double.isInfinite(doubleValue);
	}
	
	public boolean isFinite() {
		return bigValue != null;
	}
	
	public boolean isZero() {
		return (bigValue != null) ? (bigValue.signum() == 0) : (doubleValue == 0);
	}
	
	public boolean isPos() {
		return (bigValue != null) ? (bigValue.signum() > 0) : (doubleValue > 0);
	}
	
	public boolean isNeg() {
		return (bigValue != null) ? (bigValue.signum() < 0) : (doubleValue < 0);
	}
	
	public XOMNumber abs() {
		if (bigValue != null && bigValue.signum() != 0) return new XOMNumber(bigValue.abs());
		return new XOMNumber(Math.abs(doubleValue));
	}
	
	public XOMNumber negate() {
		if (bigValue != null && bigValue.signum() != 0) return new XOMNumber(bigValue.negate());
		return new XOMNumber(-doubleValue);
	}
	
	public XOMNumber signum() {
		if (bigValue != null && bigValue.signum() != 0) return new XOMNumber(bigValue.signum());
		return new XOMNumber(Math.signum(doubleValue));
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
		if (bigValue == null || bigValue.signum() == 0) return this;
		return new XOMNumber(bigValue.divide(BigDecimal.ONE, 0, rm));
	}
	
	public XOMNumber frac() {
		if (bigValue == null || bigValue.signum() == 0) return this;
		BigDecimal intValue = bigValue.divide(BigDecimal.ONE, 0, RoundingMode.DOWN);
		return new XOMNumber(bigValue.subtract(intValue));
	}
	
	public Number toNumber() {
		return (bigValue != null && bigValue.signum() != 0) ? bigValue : doubleValue;
	}
	
	public BigDecimal toBigDecimal() {
		return bigValue;
	}
	
	public double toDouble() {
		return doubleValue;
	}
	
	public double toClampedDouble() {
		if (bigValue == null || bigValue.signum() == 0) return doubleValue;
		if (doubleValue == 0) return bigValue.signum() * Double.MIN_VALUE;
		if (Double.isInfinite(doubleValue)) return bigValue.signum() * Double.MAX_VALUE;
		return doubleValue;
	}
	
	public long toLong() {
		return (bigValue != null) ? bigValue.longValue() : 0;
	}
	
	public int toInt() {
		return (bigValue != null) ? bigValue.intValue() : 0;
	}
	
	public String toLanguageString() {
		if (bigValue != null) return bigValue.toString()
			.replaceAll("[Ee][-]([0-9]+)", "''$1")
			.replaceAll("[Ee][+]?([0-9]+)", "'$1");
		return (doubleValue < 0) ? "-INF" : (doubleValue > 0) ? "INF" : "NAN";
	}
	
	public String toTextString(XNContext ctx) {
		if (bigValue != null) return ctx.getNumberFormat().format(bigValue);
		return (doubleValue < 0) ? "-INF" : (doubleValue > 0) ? "INF" : "NAN";
	}
	
	public List<? extends XOMVariant> toVariantList(XNContext ctx) {
		return Arrays.asList(this);
	}
	
	public List<? extends XOMVariant> toPrimitiveList(XNContext ctx) {
		return Arrays.asList(this);
	}
	
	public int hashCode() {
		return (bigValue != null) ? bigValue.hashCode() : 0;
	}
	
	public boolean equals(Object o) {
		if (o instanceof XOMNumber) {
			XOMNumber other = (XOMNumber)o;
			if (this.isFinite() && other.isFinite()) return (
				this.bigValue.compareTo(other.bigValue) == 0);
			if (this.isFinite() || other.isFinite()) return false;
			return this.doubleValue == other.doubleValue;
		} else {
			return false;
		}
	}
	
	public int compareTo(XOMNumber other) {
		if (this.isFinite() && other.isFinite())
			return this.bigValue.compareTo(other.bigValue);
		Double a = Double.valueOf(this.toClampedDouble());
		Double b = Double.valueOf(other.toClampedDouble());
		return a.compareTo(b);
	}
	
	public XOMNumber add(XOMNumber other, MathContext mc) {
		if (this.isFinite() && other.isFinite())
			return new XOMNumber(this.bigValue.add(other.bigValue, mc));
		return new XOMNumber(this.toClampedDouble() + other.toClampedDouble());
	}
	
	public XOMNumber subtract(XOMNumber other, MathContext mc) {
		if (this.isFinite() && other.isFinite())
			return new XOMNumber(this.bigValue.subtract(other.bigValue, mc));
		return new XOMNumber(this.toClampedDouble() - other.toClampedDouble());
	}
	
	public XOMNumber multiply(XOMNumber other, MathContext mc) {
		if (this.isFinite() && !this.isZero() && other.isFinite() && !other.isZero())
			return new XOMNumber(this.bigValue.multiply(other.bigValue, mc));
		return new XOMNumber(this.toClampedDouble() * other.toClampedDouble());
	}
	
	public XOMNumber divide(XOMNumber other, MathContext mc) {
		if (this.isFinite() && !this.isZero() && other.isFinite() && !other.isZero())
			return new XOMNumber(this.bigValue.divide(other.bigValue, mc));
		return new XOMNumber(this.toClampedDouble() / other.toClampedDouble());
	}
}
