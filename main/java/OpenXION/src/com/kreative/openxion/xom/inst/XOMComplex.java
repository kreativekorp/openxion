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
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.xom.XOMValue;
import com.kreative.openxion.xom.XOMVariant;

public class XOMComplex extends XOMValue {
	private static final long serialVersionUID = 1L;
	
	public static final XOMComplex ZERO = new XOMComplex(0, 0);
	public static final XOMComplex ONE = new XOMComplex(1, 0);
	public static final XOMComplex TWO = new XOMComplex(2, 0);
	public static final XOMComplex TEN = new XOMComplex(10, 0);
	public static final XOMComplex PI = new XOMComplex(XOMNumber.BIGDECIMAL_PI, 0);
	public static final XOMComplex E = new XOMComplex(XOMNumber.BIGDECIMAL_E, 0);
	public static final XOMComplex PHI = new XOMComplex(XOMNumber.BIGDECIMAL_PHI, 0);
	public static final XOMComplex GAMMA = new XOMComplex(XOMNumber.BIGDECIMAL_GAMMA, 0);
	public static final XOMComplex NEGATIVE_ONE = new XOMComplex(-1, 0);
	public static final XOMComplex POSITIVE_I = new XOMComplex(0, 1);
	public static final XOMComplex NEGATIVE_I = new XOMComplex(0, -1);
	public static final XOMComplex POSITIVE_I_INFINITY = new XOMComplex(0, Double.POSITIVE_INFINITY);
	public static final XOMComplex NEGATIVE_I_INFINITY = new XOMComplex(0, Double.NEGATIVE_INFINITY);
	public static final XOMComplex POSITIVE_INFINITY = new XOMComplex(Double.POSITIVE_INFINITY, 0);
	public static final XOMComplex NEGATIVE_INFINITY = new XOMComplex(Double.NEGATIVE_INFINITY, 0);
	public static final XOMComplex NaN = new XOMComplex(null, null);
	
	private final XOMNumber re;
	private final XOMNumber im;
	
	public XOMComplex(Number re, Number im) {
		this.re = new XOMNumber(re);
		this.im = new XOMNumber(im);
	}
	
	public boolean isNaN() {
		return re.isNaN() || im.isNaN();
	}
	
	public boolean isInfinite() {
		return re.isInfinite() || im.isInfinite();
	}
	
	public boolean isFinite() {
		return re.isFinite() && im.isFinite();
	}
	
	public boolean isZero() {
		return re.isZero() && im.isZero();
	}
	
	public boolean isRe() {
		return im.isZero();
	}
	
	public boolean isIm() {
		return re.isZero(); // starting life in another world?
	}
	
	public XOMComplex conj() {
		return new XOMComplex(re.toNumber(), im.negate().toNumber());
	}
	
	public XOMComplex negate() {
		return new XOMComplex(re.negate().toNumber(), im.negate().toNumber());
	}
	
	public XOMComplex muli() {
		return new XOMComplex(im.negate().toNumber(), re.toNumber());
	}
	
	public XOMComplex mulni() {
		return new XOMComplex(im.toNumber(), re.negate().toNumber());
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
		return new XOMComplex(re.round(rm).toNumber(), im.round(rm).toNumber());
	}
	
	public XOMComplex frac() {
		return new XOMComplex(re.frac().toNumber(), im.frac().toNumber());
	}
	
	public XOMNumber re() {
		return re;
	}
	
	public XOMNumber im() {
		return im;
	}
	
	public Number[] toNumbers() {
		return new Number[] { re.toNumber(), im.toNumber() };
	}
	
	public BigDecimal[] toBigDecimals() {
		return new BigDecimal[] { re.toBigDecimal(), im.toBigDecimal() };
	}
	
	public double[] toDoubles() {
		return new double[] { re.toDouble(), im.toDouble() };
	}
	
	public double[] toClampedDoubles() {
		return new double[] { re.toClampedDouble(), im.toClampedDouble() };
	}
	
	public XOMNumber[] toXOMNumbers() {
		return new XOMNumber[] { re, im };
	}
	
	public String toLanguageString() {
		if (isNaN()) return "NAN";
		return "(" + re.toLanguageString() + "," + im.toLanguageString() + ")";
	}
	
	public String toTextString(XNContext ctx) {
		if (isNaN()) return "NAN";
		return re.toTextString(ctx) + "," + im.toTextString(ctx);
	}
	
	public List<? extends XOMVariant> toVariantList(XNContext ctx) {
		return Arrays.asList(this);
	}
	
	public List<? extends XOMVariant> toPrimitiveList(XNContext ctx) {
		return Arrays.asList(this);
	}
	
	public int hashCode() {
		return re.hashCode() + 31 * im.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o instanceof XOMComplex) {
			XOMComplex other = (XOMComplex)o;
			return this.re.equals(other.re) && this.im.equals(other.im);
		} else {
			return false;
		}
	}
	
	public XOMComplex add(XOMComplex other, MathContext mc) {
		XOMNumber r = this.re.add(other.re, mc);
		XOMNumber i = this.im.add(other.im, mc);
		return new XOMComplex(r.toNumber(), i.toNumber());
	}
	
	public XOMComplex subtract(XOMComplex other, MathContext mc) {
		XOMNumber r = this.re.subtract(other.re, mc);
		XOMNumber i = this.im.subtract(other.im, mc);
		return new XOMComplex(r.toNumber(), i.toNumber());
	}
	
	public XOMComplex multiply(XOMComplex other, MathContext mc) {
		XOMNumber ac = this.re.multiply(other.re, mc);
		XOMNumber bd = this.im.multiply(other.im, mc);
		XOMNumber bc = this.im.multiply(other.re, mc);
		XOMNumber ad = this.re.multiply(other.im, mc);
		XOMNumber r = ac.subtract(bd, mc);
		XOMNumber i = bc.add(ad, mc);
		return new XOMComplex(r.toNumber(), i.toNumber());
	}
	
	public XOMComplex divide(XOMComplex other, MathContext mc) {
		XOMNumber ac = this.re.multiply(other.re, mc);
		XOMNumber bd = this.im.multiply(other.im, mc);
		XOMNumber bc = this.im.multiply(other.re, mc);
		XOMNumber ad = this.re.multiply(other.im, mc);
		XOMNumber cc = other.re.multiply(other.re, mc);
		XOMNumber dd = other.im.multiply(other.im, mc);
		XOMNumber rn = ac.add(bd, mc);
		XOMNumber in = bc.subtract(ad, mc);
		XOMNumber cd = cc.add(dd, mc);
		XOMNumber r = rn.divide(cd, mc);
		XOMNumber i = in.divide(cd, mc);
		return new XOMComplex(r.toNumber(), i.toNumber());
	}
}
