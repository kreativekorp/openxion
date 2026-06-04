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
 * Methods for mathematical functions of arbitrary numbers.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public abstract class MathProcessor {
	public abstract Number e(MathContext mc);
	public abstract Number pi(MathContext mc);
	public abstract Number acos(Number a, MathContext mc);
	public abstract Number asin(Number a, MathContext mc);
	public abstract Number atan(Number a, MathContext mc);
	public abstract Number atan2(Number y, Number x, MathContext mc);
	public abstract Number cbrt(Number a, MathContext mc);
	public abstract Number cos(Number a, MathContext mc);
	public abstract Number cosh(Number a, MathContext mc);
	public abstract Number erf(Number a, MathContext mc);
	public abstract Number erfc(Number a, MathContext mc);
	public abstract Number erfcx(Number a, MathContext mc);
	public abstract Number exp(Number a, MathContext mc);
	public abstract Number expm1(Number a, MathContext mc);
	public abstract Number gamma(Number a, MathContext mc);
	public abstract Number hypot(Number y, Number x, MathContext mc);
	public abstract Number log(Number a, MathContext mc);
	public abstract Number log10(Number a, MathContext mc);
	public abstract Number log1p(Number a, MathContext mc);
	public abstract Number log2(Number a, MathContext mc);
	public abstract Number loggamma(Number a, MathContext mc);
	public abstract Number pow(Number b, Number a, MathContext mc);
	public abstract Number sin(Number a, MathContext mc);
	public abstract Number sinh(Number a, MathContext mc);
	public abstract Number sqrt(Number a, MathContext mc);
	public abstract Number tan(Number a, MathContext mc);
	public abstract Number tanh(Number a, MathContext mc);
}
