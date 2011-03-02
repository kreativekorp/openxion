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

public class XOMComplex extends XOMValue {
	private static final long serialVersionUID = 1L;

	public static final XOMComplex ZERO = new XOMComplex(BigDecimal.ZERO, BigDecimal.ZERO);
	public static final XOMComplex ONE = new XOMComplex(BigDecimal.ONE, BigDecimal.ZERO);
	public static final XOMComplex TEN = new XOMComplex(BigDecimal.TEN, BigDecimal.ZERO);
	public static final XOMComplex PI = new XOMComplex(XOMNumber.BIGDECIMAL_PI, BigDecimal.ZERO);
	public static final XOMComplex E = new XOMComplex(XOMNumber.BIGDECIMAL_E, BigDecimal.ZERO);
	public static final XOMComplex PHI = new XOMComplex(XOMNumber.BIGDECIMAL_PHI, BigDecimal.ZERO);
	public static final XOMComplex POSITIVE_INFINITY = new XOMComplex(false, 1.0, 0.0);
	public static final XOMComplex NEGATIVE_INFINITY = new XOMComplex(false, -1.0, 0.0);
	public static final XOMComplex NaN = new XOMComplex(true, 0.0, 0.0);
	public static final XOMComplex I = new XOMComplex(BigDecimal.ZERO, BigDecimal.ONE);
	
	private BigDecimal realPart;
	private BigDecimal imaginaryPart;
	private boolean undefined;
	
	public XOMComplex(BigDecimal real, BigDecimal imaginary) {
		this.realPart = real;
		this.imaginaryPart = imaginary;
		this.undefined = false;
	}
	
	public XOMComplex(BigInteger real, BigInteger imaginary) {
		this.realPart = new BigDecimal(real);
		this.imaginaryPart = new BigDecimal(imaginary);
		this.undefined = false;
	}
	
	public XOMComplex(XOMNumber real, XOMNumber imaginary) {
		if (real.isNaN() || imaginary.isNaN()) {
			this.realPart = BigDecimal.ZERO;
			this.imaginaryPart = BigDecimal.ZERO;
			this.undefined = true;
		} else if (real.isInfinite() || imaginary.isInfinite()) {
			this.realPart = real.signum().toBigDecimal();
			this.imaginaryPart = imaginary.signum().toBigDecimal();
			this.undefined = true;
		} else {
			this.realPart = real.toBigDecimal();
			this.imaginaryPart = imaginary.toBigDecimal();
			this.undefined = false;
		}
	}
	
	public XOMComplex(double real, double imaginary) {
		if (Double.isNaN(real) || Double.isNaN(imaginary)) {
			this.realPart = BigDecimal.ZERO;
			this.imaginaryPart = BigDecimal.ZERO;
			this.undefined = true;
		} else if (Double.isInfinite(real) || Double.isInfinite(imaginary)) {
			this.realPart = BigDecimal.valueOf(Math.signum(real));
			this.imaginaryPart = BigDecimal.valueOf(Math.signum(imaginary));
			this.undefined = true;
		} else {
			this.realPart = BigDecimal.valueOf(real);
			this.imaginaryPart = BigDecimal.valueOf(imaginary);
			this.undefined = false;
		}
	}
	
	public XOMComplex(float real, float imaginary) {
		if (Float.isNaN(real) || Float.isNaN(imaginary)) {
			this.realPart = BigDecimal.ZERO;
			this.imaginaryPart = BigDecimal.ZERO;
			this.undefined = true;
		} else if (Float.isInfinite(real) || Float.isInfinite(imaginary)) {
			this.realPart = BigDecimal.valueOf(Math.signum(real));
			this.imaginaryPart = BigDecimal.valueOf(Math.signum(imaginary));
			this.undefined = true;
		} else {
			this.realPart = BigDecimal.valueOf(real);
			this.imaginaryPart = BigDecimal.valueOf(imaginary);
			this.undefined = false;
		}
	}
	
	public XOMComplex(long real, long imaginary) {
		this.realPart = BigDecimal.valueOf(real);
		this.imaginaryPart = BigDecimal.valueOf(imaginary);
		this.undefined = false;
	}
	
	public XOMComplex(int real, int imaginary) {
		this.realPart = BigDecimal.valueOf(real);
		this.imaginaryPart = BigDecimal.valueOf(imaginary);
		this.undefined = false;
	}
	
	public XOMComplex(short real, short imaginary) {
		this.realPart = BigDecimal.valueOf(real);
		this.imaginaryPart = BigDecimal.valueOf(imaginary);
		this.undefined = false;
	}
	
	public XOMComplex(byte real, byte imaginary) {
		this.realPart = BigDecimal.valueOf(real);
		this.imaginaryPart = BigDecimal.valueOf(imaginary);
		this.undefined = false;
	}
	
	public static XOMComplex makeInfinity(double realSign, double imagSign) {
		return new XOMComplex(false, realSign, imagSign);
	}
	
	private XOMComplex(boolean nan, double x, double y) {
		this.realPart = nan ? BigDecimal.ZERO : BigDecimal.valueOf(x);
		this.imaginaryPart = nan ? BigDecimal.ZERO : BigDecimal.valueOf(y);
		this.undefined = true;
	}
	
	public boolean isUndefined() {
		return realPart == null || imaginaryPart == null || undefined;
	}
	
	public boolean isNaN() {
		return (realPart == null || imaginaryPart == null) || (undefined && ((realPart.compareTo(BigDecimal.ZERO) == 0) && (imaginaryPart.compareTo(BigDecimal.ZERO) == 0)));
	}
	
	public boolean isInfinite() {
		return (realPart != null && imaginaryPart != null) && (undefined && !((realPart.compareTo(BigDecimal.ZERO) == 0) && (imaginaryPart.compareTo(BigDecimal.ZERO) == 0)));
	}
	
	public boolean isReal() {
		return (realPart != null && imaginaryPart != null && !undefined && (imaginaryPart.compareTo(BigDecimal.ZERO) == 0));
	}
	
	public boolean isZero() {
		return (realPart != null && imaginaryPart != null && !undefined && ((realPart.compareTo(BigDecimal.ZERO) == 0) && (imaginaryPart.compareTo(BigDecimal.ZERO) == 0)));
	}
	
	public static final int QUADRANT_NaN = -1;
	public static final int QUADRANT_ZERO = 0x00;
	public static final int QUADRANT_POSITIVE_REAL = 0x01;
	public static final int QUADRANT_NEGATIVE_REAL = 0x02;
	public static final int QUADRANT_POSITIVE_IMAGINARY = 0x04;
	public static final int QUADRANT_NEGATIVE_IMAGINARY = 0x08;
	public static final int QUADRANT_I = QUADRANT_POSITIVE_IMAGINARY | QUADRANT_POSITIVE_REAL;
	public static final int QUADRANT_II = QUADRANT_POSITIVE_IMAGINARY | QUADRANT_NEGATIVE_REAL;
	public static final int QUADRANT_III = QUADRANT_NEGATIVE_IMAGINARY | QUADRANT_NEGATIVE_REAL;
	public static final int QUADRANT_IV = QUADRANT_NEGATIVE_IMAGINARY | QUADRANT_POSITIVE_REAL;
	
	public int getQuadrant() {
		if (realPart == null || imaginaryPart == null) return QUADRANT_NaN;
		else if (undefined) {
			int rc = realPart.compareTo(BigDecimal.ZERO);
			int ic = imaginaryPart.compareTo(BigDecimal.ZERO);
			if (rc == 0 && ic == 0) return QUADRANT_NaN;
			else return ((rc < 0) ? QUADRANT_NEGATIVE_REAL : (rc > 0) ? QUADRANT_POSITIVE_REAL : 0) | ((ic < 0) ? QUADRANT_NEGATIVE_IMAGINARY : (ic > 0) ? QUADRANT_POSITIVE_IMAGINARY : 0);
		}
		else {
			int rc = realPart.compareTo(BigDecimal.ZERO);
			int ic = imaginaryPart.compareTo(BigDecimal.ZERO);
			return ((rc < 0) ? QUADRANT_NEGATIVE_REAL : (rc > 0) ? QUADRANT_POSITIVE_REAL : 0) | ((ic < 0) ? QUADRANT_NEGATIVE_IMAGINARY : (ic > 0) ? QUADRANT_POSITIVE_IMAGINARY : 0);
		}
	}
	
	public int getOppositeQuadrant() {
		if (realPart == null || imaginaryPart == null) return QUADRANT_NaN;
		else if (undefined) {
			int rc = realPart.compareTo(BigDecimal.ZERO);
			int ic = imaginaryPart.compareTo(BigDecimal.ZERO);
			if (rc == 0 && ic == 0) return QUADRANT_NaN;
			else return ((rc < 0) ? QUADRANT_POSITIVE_REAL : (rc > 0) ? QUADRANT_NEGATIVE_REAL : 0) | ((ic < 0) ? QUADRANT_POSITIVE_IMAGINARY : (ic > 0) ? QUADRANT_NEGATIVE_IMAGINARY : 0);
		}
		else {
			int rc = realPart.compareTo(BigDecimal.ZERO);
			int ic = imaginaryPart.compareTo(BigDecimal.ZERO);
			return ((rc < 0) ? QUADRANT_POSITIVE_REAL : (rc > 0) ? QUADRANT_NEGATIVE_REAL : 0) | ((ic < 0) ? QUADRANT_POSITIVE_IMAGINARY : (ic > 0) ? QUADRANT_NEGATIVE_IMAGINARY : 0);
		}
	}
	
	public XOMComplex negate() {
		if (realPart == null || imaginaryPart == null) return NaN;
		else if (undefined) {
			int rc = realPart.compareTo(BigDecimal.ZERO);
			int ic = imaginaryPart.compareTo(BigDecimal.ZERO);
			if (rc == 0 && ic == 0) return NaN;
			else return new XOMComplex(((rc < 0) ? Double.POSITIVE_INFINITY : (rc > 0) ? Double.NEGATIVE_INFINITY : 0.0), ((ic < 0) ? Double.POSITIVE_INFINITY : (ic > 0) ? Double.NEGATIVE_INFINITY : 0.0));
		}
		else return new XOMComplex(realPart.negate(),imaginaryPart.negate());
	}
	
	public XOMComplex conj() {
		if (realPart == null || imaginaryPart == null) return NaN;
		else if (undefined) {
			int rc = realPart.compareTo(BigDecimal.ZERO);
			int ic = imaginaryPart.compareTo(BigDecimal.ZERO);
			if (rc == 0 && ic == 0) return NaN;
			else return new XOMComplex(((rc < 0) ? Double.NEGATIVE_INFINITY : (rc > 0) ? Double.POSITIVE_INFINITY : 0.0), ((ic < 0) ? Double.POSITIVE_INFINITY : (ic > 0) ? Double.NEGATIVE_INFINITY : 0.0));
		}
		else return new XOMComplex(realPart,imaginaryPart.negate());
	}
	
	public XOMComplex ceil() {
		return round(RoundingMode.CEILING);
	}
	
	public XOMComplex floor() {
		return round(RoundingMode.FLOOR);
	}
	
	public XOMComplex round() {
		return round(RoundingMode.HALF_UP);
	}
	
	public XOMComplex aug() {
		return round(RoundingMode.UP);
	}
	
	public XOMComplex trunc() {
		return round(RoundingMode.DOWN);
	}
	
	public XOMComplex rint() {
		return round(RoundingMode.HALF_EVEN);
	}
	
	public XOMComplex round(RoundingMode rm) {
		if (realPart == null || imaginaryPart == null || undefined) return this;
		else return new XOMComplex(realPart.divide(BigDecimal.ONE, 0, rm), imaginaryPart.divide(BigDecimal.ONE, 0, rm));
	}
	
	public XOMComplex frac() {
		if (realPart == null || imaginaryPart == null || undefined) return NaN;
		else return new XOMComplex(
				realPart.subtract(realPart.divide(BigDecimal.ONE, 0, RoundingMode.DOWN)),
				imaginaryPart.subtract(imaginaryPart.divide(BigDecimal.ONE, 0, RoundingMode.DOWN))
		);
	}
	
	public XOMNumber Re() {
		if (realPart == null) return XOMNumber.NaN;
		else if (undefined) {
			int cmp = realPart.compareTo(BigDecimal.ZERO);
			if (cmp < 0) return XOMNumber.NEGATIVE_INFINITY;
			else if (cmp > 0) return XOMNumber.POSITIVE_INFINITY;
			else return XOMNumber.NaN;
		}
		else return new XOMNumber(realPart);
	}
	
	public XOMNumber Im() {
		if (imaginaryPart == null) return XOMNumber.NaN;
		else if (undefined) {
			int cmp = imaginaryPart.compareTo(BigDecimal.ZERO);
			if (cmp < 0) return XOMNumber.NEGATIVE_INFINITY;
			else if (cmp > 0) return XOMNumber.POSITIVE_INFINITY;
			else return XOMNumber.NaN;
		}
		else return new XOMNumber(imaginaryPart);
	}
	
	public BigDecimal realPart() {
		if (realPart == null || imaginaryPart == null || undefined) return null;
		else return realPart;
	}
	
	public BigDecimal imaginaryPart() {
		if (realPart == null || imaginaryPart == null || undefined) return null;
		else return imaginaryPart;
	}
	
	public Number[] toNumbers() {
		if (realPart == null || imaginaryPart == null) return new Double[]{Double.NaN, Double.NaN};
		else if (undefined) {
			int rc = realPart.compareTo(BigDecimal.ZERO);
			int ic = imaginaryPart.compareTo(BigDecimal.ZERO);
			if (rc == 0 && ic == 0) return new Double[]{Double.NaN, Double.NaN};
			else return new Double[]{((rc < 0) ? Double.NEGATIVE_INFINITY : (rc > 0) ? Double.POSITIVE_INFINITY : 0.0), ((ic < 0) ? Double.NEGATIVE_INFINITY : (ic > 0) ? Double.POSITIVE_INFINITY : 0.0)};
		}
		else return new BigDecimal[]{realPart,imaginaryPart};
	}
	
	public BigDecimal[] toBigDecimals() {
		if (realPart == null || imaginaryPart == null || undefined) return null;
		else return new BigDecimal[]{realPart,imaginaryPart};
	}
	
	public double[] toDoubles() {
		if (realPart == null || imaginaryPart == null) return new double[]{Double.NaN, Double.NaN};
		else if (undefined) {
			int rc = realPart.compareTo(BigDecimal.ZERO);
			int ic = imaginaryPart.compareTo(BigDecimal.ZERO);
			if (rc == 0 && ic == 0) return new double[]{Double.NaN, Double.NaN};
			else return new double[]{((rc < 0) ? Double.NEGATIVE_INFINITY : (rc > 0) ? Double.POSITIVE_INFINITY : 0.0), ((ic < 0) ? Double.NEGATIVE_INFINITY : (ic > 0) ? Double.POSITIVE_INFINITY : 0.0)};
		}
		else return new double[]{realPart.doubleValue(),imaginaryPart.doubleValue()};
	}
	
	public XOMNumber[] toXOMNumbers() {
		if (realPart == null || imaginaryPart == null) return new XOMNumber[]{XOMNumber.NaN, XOMNumber.NaN};
		else if (undefined) {
			int rc = realPart.compareTo(BigDecimal.ZERO);
			int ic = imaginaryPart.compareTo(BigDecimal.ZERO);
			if (rc == 0 && ic == 0) return new XOMNumber[]{XOMNumber.NaN, XOMNumber.NaN};
			else return new XOMNumber[]{((rc < 0) ? XOMNumber.NEGATIVE_INFINITY : (rc > 0) ? XOMNumber.POSITIVE_INFINITY : XOMNumber.ZERO), ((ic < 0) ? XOMNumber.NEGATIVE_INFINITY : (ic > 0) ? XOMNumber.POSITIVE_INFINITY : XOMNumber.ZERO)};
		}
		else return new XOMNumber[]{new XOMNumber(realPart),new XOMNumber(imaginaryPart)};
	}
	
	public String toLanguageString() {
		if (realPart == null || imaginaryPart == null) return "NAN";
		else if (undefined) {
			int rc = realPart.compareTo(BigDecimal.ZERO);
			int ic = imaginaryPart.compareTo(BigDecimal.ZERO);
			if (rc == 0 && ic == 0) return "NAN";
			else return "(" + ((rc < 0) ? "-INF" : (rc > 0) ? "INF" : "0") + "," + ((ic < 0) ? "-INF" : (ic > 0) ? "INF" : "0") + ")";
		}
		else return "("
			+ realPart.toString()
				.replaceAll("[Ee][-]([0-9]+)", "''$1")
				.replaceAll("[Ee][+]?([0-9]+)", "'$1")
			+ ","
			+ imaginaryPart.toString()
				.replaceAll("[Ee][-]([0-9]+)", "''$1")
				.replaceAll("[Ee][+]?([0-9]+)", "'$1")
			+ ")";
	}
	public String toTextString(XNContext ctx) {
		if (realPart == null || imaginaryPart == null) return "NAN";
		else if (undefined) {
			int rc = realPart.compareTo(BigDecimal.ZERO);
			int ic = imaginaryPart.compareTo(BigDecimal.ZERO);
			if (rc == 0 && ic == 0) return "NAN";
			else return ((rc < 0) ? "-INF" : (rc > 0) ? "INF" : "0") + "," + ((ic < 0) ? "-INF" : (ic > 0) ? "INF" : "0");
		}
		else return ctx.getNumberFormat().format(realPart)+","+ctx.getNumberFormat().format(imaginaryPart);
	}
	public List<? extends XOMVariant> toList(XNContext ctx) {
		return Arrays.asList(this);
	}
	public int hashCode() {
		return (realPart == null || imaginaryPart == null) ? 0 : undefined ? (realPart.signum() * 3 + imaginaryPart.signum()) : realPart.hashCode() ^ imaginaryPart.hashCode();
	}
	public boolean equals(Object o) {
		if (o instanceof XOMComplex) {
			XOMComplex other = (XOMComplex)o;
			if (this.isNaN() && other.isNaN()) {
				return true;
			}
			else if (this.isNaN() || other.isNaN()) {
				return false;
			}
			else if (this.undefined && other.undefined) {
				return (this.getQuadrant() == other.getQuadrant());
			}
			else if (this.undefined || other.undefined) {
				return false;
			}
			else {
				return this.realPart.compareTo(other.realPart) == 0 && this.imaginaryPart.compareTo(other.imaginaryPart) == 0;
			}
		} else {
			return false;
		}
	}
}
