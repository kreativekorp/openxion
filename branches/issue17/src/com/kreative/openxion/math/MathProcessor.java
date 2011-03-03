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
 * Methods for mathematical functions of BigDecimals.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public abstract class MathProcessor {
	public abstract BigDecimal e(MathContext mc);
	public abstract BigDecimal pi(MathContext mc);
	public abstract BigDecimal acos(BigDecimal arg, MathContext mc);
	public abstract BigDecimal asin(BigDecimal arg, MathContext mc);
	public abstract BigDecimal atan(BigDecimal arg, MathContext mc);
	public abstract BigDecimal atan2(BigDecimal y, BigDecimal x, MathContext mc);
	public abstract BigDecimal cbrt(BigDecimal arg, MathContext mc);
	public abstract BigDecimal cos(BigDecimal arg, MathContext mc);
	public abstract BigDecimal cosh(BigDecimal arg, MathContext mc);
	public abstract BigDecimal exp(BigDecimal arg, MathContext mc);
	public abstract BigDecimal expm1(BigDecimal arg, MathContext mc);
	public abstract BigDecimal gamma(BigDecimal arg, MathContext mc);
	public abstract BigDecimal hypot(BigDecimal x, BigDecimal y, MathContext mc);
	public abstract BigDecimal log(BigDecimal arg, MathContext mc);
	public abstract BigDecimal log10(BigDecimal arg, MathContext mc);
	public abstract BigDecimal log1p(BigDecimal arg, MathContext mc);
	public abstract BigDecimal log2(BigDecimal arg, MathContext mc);
	public abstract BigDecimal loggamma(BigDecimal arg, MathContext mc);
	public abstract BigDecimal pow(BigDecimal a, BigDecimal b, MathContext mc);
	public abstract BigDecimal sin(BigDecimal arg, MathContext mc);
	public abstract BigDecimal sinh(BigDecimal arg, MathContext mc);
	public abstract BigDecimal sqrt(BigDecimal arg, MathContext mc);
	public abstract BigDecimal tan(BigDecimal arg, MathContext mc);
	public abstract BigDecimal tanh(BigDecimal arg, MathContext mc);
}
