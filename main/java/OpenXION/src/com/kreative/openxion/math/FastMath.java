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

package com.kreative.openxion.math;

import java.math.MathContext;

/**
 * Methods for mathematical functions of arbitrary numbers
 * using double precision floating point (the fastest method).
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class FastMath extends MathProcessor {
	public static final FastMath instance = new FastMath();
	
	public Double e(MathContext mc) { return Math.E; }
	public Double pi(MathContext mc) { return Math.PI; }
	public Double acos(Number a, MathContext mc) { return Math.acos(a.doubleValue()); }
	public Double asin(Number a, MathContext mc) { return Math.asin(a.doubleValue()); }
	public Double atan(Number a, MathContext mc) { return Math.atan(a.doubleValue()); }
	
	public Double atan2(Number y, Number x, MathContext mc) {
		return Math.atan2(y.doubleValue(), x.doubleValue());
	}
	
	public Double cbrt(Number a, MathContext mc) { return Math.cbrt(a.doubleValue()); }
	public Double cos(Number a, MathContext mc) { return Math.cos(a.doubleValue()); }
	public Double cosh(Number a, MathContext mc) { return Math.cosh(a.doubleValue()); }
	public Double erf(Number a, MathContext mc) { return 1-erfc(a.doubleValue()); }
	public Double erfc(Number a, MathContext mc) { return erfc(a.doubleValue()); }
	public Double erfcx(Number a, MathContext mc) { return erfcx(a.doubleValue()); }
	public Double exp(Number a, MathContext mc) { return Math.exp(a.doubleValue()); }
	public Double expm1(Number a, MathContext mc) { return Math.expm1(a.doubleValue()); }
	public Double gamma(Number a, MathContext mc) { return gamma(a.doubleValue()); }
	
	public Double hypot(Number y, Number x, MathContext mc) {
		return Math.hypot(y.doubleValue(), x.doubleValue());
	}
	
	public Double log(Number a, MathContext mc) { return Math.log(a.doubleValue()); }
	public Double log10(Number a, MathContext mc) { return Math.log10(a.doubleValue()); }
	public Double log1p(Number a, MathContext mc) { return Math.log1p(a.doubleValue()); }
	public Double log2(Number a, MathContext mc) { return Math.log(a.doubleValue())/Math.log(2); }
	public Double loggamma(Number a, MathContext mc) { return loggamma(a.doubleValue()); }
	
	public Double pow(Number b, Number a, MathContext mc) {
		return Math.pow(b.doubleValue(), a.doubleValue());
	}
	
	public Double sin(Number a, MathContext mc) { return Math.sin(a.doubleValue()); }
	public Double sinh(Number a, MathContext mc) { return Math.sinh(a.doubleValue()); }
	public Double sqrt(Number a, MathContext mc) { return Math.sqrt(a.doubleValue()); }
	public Double tan(Number a, MathContext mc) { return Math.tan(a.doubleValue()); }
	public Double tanh(Number a, MathContext mc) { return Math.tanh(a.doubleValue()); }
	
	private static double erfc(double x) {
		if (Double.isNaN(x)) return x;
		if (x < 0) return 2 - erfc(-x);
		if (x == 0) return 1;
		if (Double.isInfinite(x)) return 0;
		return Math.exp(-x*x) * erfcx(x);
	}
	
	private static double erfcx(double x) {
		if (Double.isNaN(x)) return x;
		if (x < 0) return Math.exp(x*x) * erfc(x);
		if (x == 0) return 1;
		if (Double.isInfinite(x)) return 0;
		double y = 0.56418958354775629 / (x + 2.06955023132914151);
		y *= (x*x + 2.71078540045147805*x + 5.80755613130301624) / (x*x + 3.47954057099518960*x + 12.06166887286239555);
		y *= (x*x + 3.47469513777439592*x + 12.07402036406381411) / (x*x + 3.72068443960225092*x + 8.44319781003968454);
		y *= (x*x + 4.00561509202259545*x + 9.30596659485887898) / (x*x + 3.90225704029924078*x + 6.36161630953880464);
		y *= (x*x + 5.16722705817812584*x + 9.12661617673673262) / (x*x + 4.03296893109262491*x + 5.13578530585681539);
		y *= (x*x + 5.95908795446633271*x + 9.19435612886969243) / (x*x + 4.11240942957450885*x + 4.48640329523408675);
		return y;
	}
	
	private static final double GAMMA_G = 7;
	private static final double[] GAMMA_P = {
		0.99999999999980993227684700473478,
		676.520368121885098567009190444019,
		-1259.13921672240287047156078755283,
		771.3234287776530788486528258894,
		-176.61502916214059906584551354,
		12.507343278686904814458936853,
		-0.13857109526572011689554707,
		9.984369578019570859563e-6,
		1.50563273514931155834e-7
	};
	
	private static double[] gammaTZX(double z) {
		z--;
		double x = GAMMA_P[0];
		for (int i = 1; i < GAMMA_P.length; i++) x += GAMMA_P[i] / (z + i);
		return new double[] { z + GAMMA_G + 0.5, z + 0.5, x };
	}
	
	private static final double SQRT_TWO_PI = 2.5066282746310005024157652848110453;
	
	private static double gamma(double z) {
		if (Double.isNaN(z)) return z;
		if (z <= 0 && Math.ceil(z) == Math.floor(z)) return Double.NaN;
		if (z == 1 || z == 2) return 1;
		if (Double.isInfinite(z)) return z;
		if (z < 0.5) return Math.PI / (Math.sin(Math.PI * z) * gamma(1 - z));
		double[] tzx = gammaTZX(z);
		return SQRT_TWO_PI * Math.pow(tzx[0], tzx[1]) * Math.exp(-tzx[0]) * tzx[2];
	}
	
	private static final double LOG_SQRT_TWO_PI = 0.9189385332046727417803297364056176;
	
	private static double loggamma(double z) {
		if (Double.isNaN(z)) return z;
		if (z <= 0 && Math.ceil(z) == Math.floor(z)) return Double.NaN;
		if (z == 1 || z == 2) return 0;
		if (Double.isInfinite(z)) return z;
		if (z < 0.5) return Math.log(Math.abs(Math.PI / Math.sin(Math.PI * z))) - loggamma(1 - z);
		double[] tzx = gammaTZX(z);
		return LOG_SQRT_TWO_PI + tzx[1] * Math.log(tzx[0]) - tzx[0] + Math.log(tzx[2]);
	}
}
