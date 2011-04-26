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

/**
 * Methods for mathematical functions of BigDecimals
 * by converting to double and back (the fastest method by far!).
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class FastMath extends MathProcessor {
	public static final FastMath instance = new FastMath();
	
	@Override
	public BigDecimal acos(BigDecimal arg, MathContext mc) {
		double r = Math.acos(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal asin(BigDecimal arg, MathContext mc) {
		double r = Math.asin(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal atan(BigDecimal arg, MathContext mc) {
		double r = Math.atan(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal atan2(BigDecimal y, BigDecimal x, MathContext mc) {
		double r = Math.atan2(y.doubleValue(), x.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal cbrt(BigDecimal arg, MathContext mc) {
		double r = Math.cbrt(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal cos(BigDecimal arg, MathContext mc) {
		double r = Math.cos(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal cosh(BigDecimal arg, MathContext mc) {
		double r = Math.cosh(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal e(MathContext mc) {
		return BigDecimal.valueOf(Math.E);
	}

	@Override
	public BigDecimal exp(BigDecimal arg, MathContext mc) {
		double r = Math.exp(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal expm1(BigDecimal arg, MathContext mc) {
		double r = Math.expm1(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal hypot(BigDecimal x, BigDecimal y, MathContext mc) {
		double r = Math.hypot(x.doubleValue(), y.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal log(BigDecimal arg, MathContext mc) {
		double r = Math.log(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal log10(BigDecimal arg, MathContext mc) {
		double r = Math.log10(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal log1p(BigDecimal arg, MathContext mc) {
		double r = Math.log1p(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal log2(BigDecimal arg, MathContext mc) {
		double r = Math.log(arg.doubleValue()) / Math.log(2);
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal pi(MathContext mc) {
		return BigDecimal.valueOf(Math.PI);
	}

	@Override
	public BigDecimal pow(BigDecimal a, BigDecimal b, MathContext mc) {
		double r = Math.pow(a.doubleValue(), b.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal sin(BigDecimal arg, MathContext mc) {
		double r = Math.sin(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal sinh(BigDecimal arg, MathContext mc) {
		double r = Math.sinh(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal sqrt(BigDecimal arg, MathContext mc) {
		double r = Math.sqrt(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal tan(BigDecimal arg, MathContext mc) {
		double r = Math.tan(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal tanh(BigDecimal arg, MathContext mc) {
		double r = Math.tanh(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}
	
	private static final int G = 7;
	private static final double[] P = new double[] {
		0.99999999999980993, 676.5203681218851, -1259.1392167224028,
		771.32342877765313, -176.61502916214059, 12.507343278686905,
		-0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7
	};
	private static double gamma(double z) {
		if (Double.isNaN(z)) return Double.NaN;
		else if (Double.isInfinite(z)) return (z > 0.0) ? Double.POSITIVE_INFINITY : Double.NaN;
		else if (z < 0.0 && Math.round(z) == z) return Double.NaN;
		else if (z == 0.0) return Double.POSITIVE_INFINITY;
		else if (z == 1.0 || z == 2.0) return 1.0;
		else if (z < 0.5) {
			return Math.PI / (Math.sin(Math.PI * z) * gamma(1.0-z));
		}
		else {
			z -= 1.0;
			double x = P[0];
			for (int i = 1; i < G+2; i++) {
				x += P[i]/(z+i);
			}
			double t = z + G + 0.5;
			return Math.sqrt(2*Math.PI) * Math.pow(t, z+0.5) * Math.exp(-t) * x;
		}
	}

	@Override
	public BigDecimal gamma(BigDecimal arg, MathContext mc) {
		double r = gamma(arg.doubleValue());
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}

	@Override
	public BigDecimal loggamma(BigDecimal arg, MathContext mc) {
		double r = Math.log(gamma(arg.doubleValue()));
		if (Double.isNaN(r) || Double.isInfinite(r)) return null;
		else return BigDecimal.valueOf(r);
	}
}
