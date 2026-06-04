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
import java.util.Arrays;
import java.util.List;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.xom.XOMValue;
import com.kreative.openxion.xom.XOMVariant;

public class XOMInteger extends XOMValue implements Comparable<XOMInteger> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMInteger ZERO = new XOMInteger(0);
	public static final XOMInteger ONE = new XOMInteger(1);
	public static final XOMInteger TWO = new XOMInteger(2);
	public static final XOMInteger TEN = new XOMInteger(10);
	public static final XOMInteger NEGATIVE_ONE = new XOMInteger(-1);
	public static final XOMInteger POSITIVE_INFINITY = new XOMInteger(Double.POSITIVE_INFINITY);
	public static final XOMInteger NEGATIVE_INFINITY = new XOMInteger(Double.NEGATIVE_INFINITY);
	public static final XOMInteger NaN = new XOMInteger(null);
	
	private final BigInteger bigValue;
	private final double doubleValue;
	
	public XOMInteger(Number n) {
		if (n == null) {
			this.bigValue = null;
			this.doubleValue = Double.NaN;
		} else if (n instanceof BigInteger) {
			this.bigValue = (BigInteger)n;
			this.doubleValue = bigValue.doubleValue();
		} else if (n instanceof BigDecimal) {
			this.bigValue = ((BigDecimal)n).toBigInteger();
			this.doubleValue = bigValue.doubleValue();
		} else if (n instanceof Double || n instanceof Float) {
			double v = n.doubleValue();
			if (Double.isNaN(v) || Double.isInfinite(v)) {
				this.bigValue = null;
				this.doubleValue = v;
			} else if (v == 0) {
				this.bigValue = BigInteger.ZERO;
				this.doubleValue = v;
			} else {
				this.bigValue = BigDecimal.valueOf(v).toBigInteger();
				this.doubleValue = bigValue.doubleValue();
			}
		} else if (n instanceof Long || n instanceof Integer || n instanceof Short || n instanceof Byte) {
			this.bigValue = BigInteger.valueOf(n.longValue());
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
	
	public XOMInteger abs() {
		if (bigValue != null && bigValue.signum() != 0) return new XOMInteger(bigValue.abs());
		return new XOMInteger(Math.abs(doubleValue));
	}
	
	public XOMInteger negate() {
		if (bigValue != null && bigValue.signum() != 0) return new XOMInteger(bigValue.negate());
		return new XOMInteger(-doubleValue);
	}
	
	public XOMInteger signum() {
		if (bigValue != null && bigValue.signum() != 0) return new XOMInteger(bigValue.signum());
		return new XOMInteger(Math.signum(doubleValue));
	}
	
	public Number toNumber() {
		return (bigValue != null && bigValue.signum() != 0) ? bigValue : doubleValue;
	}
	
	public BigInteger toBigInteger() {
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
		if (bigValue != null) return bigValue.toString();
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
		if (o instanceof XOMInteger) {
			XOMInteger other = (XOMInteger)o;
			if (this.isFinite() && other.isFinite()) return (
				this.bigValue.compareTo(other.bigValue) == 0);
			if (this.isFinite() || other.isFinite()) return false;
			return this.doubleValue == other.doubleValue;
		} else {
			return false;
		}
	}
	
	public int compareTo(XOMInteger other) {
		if (this.isFinite() && other.isFinite())
			return this.bigValue.compareTo(other.bigValue);
		Double a = Double.valueOf(this.toClampedDouble());
		Double b = Double.valueOf(other.toClampedDouble());
		return a.compareTo(b);
	}
	
	public XOMInteger add(XOMInteger other) {
		if (this.isFinite() && other.isFinite())
			return new XOMInteger(this.bigValue.add(other.bigValue));
		return new XOMInteger(this.toClampedDouble() + other.toClampedDouble());
	}
	
	public XOMInteger subtract(XOMInteger other) {
		if (this.isFinite() && other.isFinite())
			return new XOMInteger(this.bigValue.subtract(other.bigValue));
		return new XOMInteger(this.toClampedDouble() - other.toClampedDouble());
	}
	
	public XOMInteger multiply(XOMInteger other) {
		if (this.isFinite() && !this.isZero() && other.isFinite() && !other.isZero())
			return new XOMInteger(this.bigValue.multiply(other.bigValue));
		return new XOMInteger(this.toClampedDouble() * other.toClampedDouble());
	}
	
	public XOMInteger divide(XOMInteger other) {
		if (this.isFinite() && !this.isZero() && other.isFinite() && !other.isZero())
			return new XOMInteger(this.bigValue.divide(other.bigValue));
		return new XOMInteger(this.toClampedDouble() / other.toClampedDouble());
	}
}
