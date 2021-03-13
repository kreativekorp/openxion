/*
 * Copyright &copy; 2010-2011 Rebecca G. Bettencourt / Kreative Software
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
 * @since OpenXION 1.2
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.binpack;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class FPUtilities {
	private FPUtilities() {}
	
	public static int optimalSignWidth(int floatWidth) {
		return (floatWidth > 0) ? 1 : 0;
	}
	
	public static int optimalExponentWidth(int floatWidth) {
		     if (floatWidth < 2) return 0;
		else if (floatWidth < 4) return 1;
		else if (floatWidth < 6) return 2;
		else if (floatWidth < 8) return 3;
		else if (floatWidth < 12) return 4;
		else if (floatWidth < 20) return 5;
		else if (floatWidth < 24) return 6;
		else if (floatWidth < 32) return 7;
		else if (floatWidth < 48) return 8;
		else if (floatWidth < 56) return 9;
		else if (floatWidth < 64) return 10;
		else if (floatWidth < 80) return 11;
		else if (floatWidth < 96) return 12;
		else if (floatWidth < 112) return 13;
		else if (floatWidth < 128) return 14;
		else if (floatWidth < 160) return 15;
		else if (floatWidth < 192) return 16;
		else if (floatWidth < 224) return 17;
		else if (floatWidth < 256) return 18;
		else if (floatWidth < 320) return 19;
		else if (floatWidth < 384) return 20;
		else if (floatWidth < 448) return 21;
		else if (floatWidth < 512) return 22;
		else                       return 23;
	}
	
	public static int optimalMantissaWidth(int floatWidth) {
		return (floatWidth > 2) ? (floatWidth - optimalExponentWidth(floatWidth) - 1) : 0;
	}
	
	public static int optimalBias(int exponentWidth) {
		return ((1 << (exponentWidth - 1)) - 1);
	}
	
	public static BigInteger[] splitFloat(BigInteger rawFloat, int signWidth, int exponentWidth, int mantissaWidth) {
		BigInteger rawSign = rawFloat.shiftRight(exponentWidth + mantissaWidth).and(BigInteger.ONE.shiftLeft(signWidth).subtract(BigInteger.ONE));
		BigInteger rawExponent = rawFloat.shiftRight(mantissaWidth).and(BigInteger.ONE.shiftLeft(exponentWidth).subtract(BigInteger.ONE));
		BigInteger rawMantissa = rawFloat.and(BigInteger.ONE.shiftLeft(mantissaWidth).subtract(BigInteger.ONE));
		return new BigInteger[] { rawSign, rawExponent, rawMantissa };
	}
	
	public static BigInteger joinFloat(BigInteger rawSign, BigInteger rawExponent, BigInteger rawMantissa, int signWidth, int exponentWidth, int mantissaWidth) {
		return (rawSign.and(BigInteger.ONE.shiftLeft(signWidth).subtract(BigInteger.ONE)).shiftLeft(exponentWidth + mantissaWidth))
			.or(rawExponent.and(BigInteger.ONE.shiftLeft(exponentWidth).subtract(BigInteger.ONE)).shiftLeft(mantissaWidth))
			.or(rawMantissa.and(BigInteger.ONE.shiftLeft(mantissaWidth).subtract(BigInteger.ONE)));
	}
	
	public static Number decodeFloat(BigInteger rawSign, BigInteger rawExponent, BigInteger rawMantissa, int signWidth, int exponentWidth, int mantissaWidth, int bias, MathContext mc) {
		if (signWidth < 0 || signWidth > 1 || exponentWidth < 0 || mantissaWidth < 0) throw new IllegalArgumentException();
		else if (rawExponent.compareTo(BigInteger.ZERO) == 0) {
			// zero or subnormal
			if (rawMantissa.compareTo(BigInteger.ZERO) == 0) {
				// zero
				// must use double, instead of BigDecimal, in order to preserve sign
				boolean isNegative = (rawSign.compareTo(BigInteger.ZERO) != 0);
				return isNegative ? -0.0 : 0.0;
			} else {
				// subnormal
				boolean isNegative = (rawSign.compareTo(BigInteger.ZERO) != 0);
				int negativeExponent = bias + mantissaWidth - 1;
				BigDecimal mantissa = new BigDecimal(rawMantissa);
				if (negativeExponent < 0) {
					BigDecimal multiplier = BigDecimal.valueOf(2L).pow(-negativeExponent);
					return isNegative ? mantissa.multiply(multiplier, mc).negate() : mantissa.multiply(multiplier, mc);
				} else {
					BigDecimal multiplier = BigDecimal.valueOf(2L).pow(negativeExponent);
					return isNegative ? mantissa.divide(multiplier, mc).negate() : mantissa.divide(multiplier, mc);
				}
			}
		} else if (rawExponent.compareTo(BigInteger.ONE.shiftLeft(exponentWidth).subtract(BigInteger.ONE)) == 0) {
			// infinity or NaN
			if (rawMantissa.compareTo(BigInteger.ZERO) == 0) {
				// infinity
				boolean isNegative = (rawSign.compareTo(BigInteger.ZERO) != 0);
				return isNegative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
			} else {
				// NaN
				boolean isNegative = (rawSign.compareTo(BigInteger.ZERO) != 0);
				boolean isQuietNaN = rawMantissa.testBit(mantissaWidth-1);
				BigInteger mantissa = rawMantissa.clearBit(mantissaWidth-1);
				long rawDouble = 0x7FF0000000000000L;
				if (isNegative) rawDouble |= 0x8000000000000000L;
				if (isQuietNaN) rawDouble |= 0x0008000000000000L;
				rawDouble |= (mantissa.longValue() & 0x0007FFFFFFFFFFFFL);
				return Double.longBitsToDouble(rawDouble);
			}
		} else {
			// normal
			boolean isNegative = (rawSign.compareTo(BigInteger.ZERO) != 0);
			int negativeExponent = bias + mantissaWidth - rawExponent.intValue();
			BigDecimal mantissa = new BigDecimal(rawMantissa.setBit(mantissaWidth));
			if (negativeExponent < 0) {
				BigDecimal multiplier = BigDecimal.valueOf(2L).pow(-negativeExponent);
				return isNegative ? mantissa.multiply(multiplier, mc).negate() : mantissa.multiply(multiplier, mc);
			} else {
				BigDecimal multiplier = BigDecimal.valueOf(2L).pow(negativeExponent);
				return isNegative ? mantissa.divide(multiplier, mc).negate() : mantissa.divide(multiplier, mc);
			}
		}
	}
	
	public static BigInteger[] encodeFloat(Number v, int signWidth, int exponentWidth, int mantissaWidth, int bias, MathContext mc) {
		if (signWidth < 0 || signWidth > 1 || exponentWidth < 0 || mantissaWidth < 0) throw new IllegalArgumentException();
		else if (v instanceof BigDecimal) {
			BigDecimal d = (BigDecimal)v;
			if (d.compareTo(BigDecimal.ZERO) == 0) {
				return encodeZero(false, signWidth, exponentWidth, mantissaWidth);
			} else {
				return encodeFiniteNonZero(d, signWidth, exponentWidth, mantissaWidth, bias, mc);
			}
		} else if (v instanceof BigInteger) {
			BigInteger i = (BigInteger)v;
			if (i.compareTo(BigInteger.ZERO) == 0) {
				return encodeZero(false, signWidth, exponentWidth, mantissaWidth);
			} else {
				return encodeFiniteNonZero(new BigDecimal(i), signWidth, exponentWidth, mantissaWidth, bias, mc);
			}
		} else if (v instanceof Double) {
			double d = v.doubleValue();
			if (Double.isNaN(d)) {
				long rawDouble = Double.doubleToRawLongBits(d);
				boolean isNegative = ((rawDouble & 0x8000000000000000L) != 0L);
				boolean isQuietNaN = ((rawDouble & 0x0008000000000000L) != 0L);
				long diagnosticCode = ((rawDouble & 0x0007FFFFFFFFFFFFL));
				return encodeNaN(isNegative, isQuietNaN, diagnosticCode, signWidth, exponentWidth, mantissaWidth);
			} else if (Double.isInfinite(d)) {
				return encodeInfinity(d < 0.0, signWidth, exponentWidth, mantissaWidth);
			} else if (d == 0.0) {
				long rawDouble = Double.doubleToRawLongBits(d);
				boolean isNegative = ((rawDouble & 0x8000000000000000L) != 0L);
				return encodeZero(isNegative, signWidth, exponentWidth, mantissaWidth);
			} else {
				return encodeFiniteNonZero(BigDecimal.valueOf(d), signWidth, exponentWidth, mantissaWidth, bias, mc);
			}
		} else if (v instanceof Float) {
			float f = v.floatValue();
			if (Float.isNaN(f)) {
				int rawFloat = Float.floatToRawIntBits(f);
				boolean isNegative = ((rawFloat & 0x80000000) != 0);
				boolean isQuietNaN = ((rawFloat & 0x00400000) != 0);
				int diagnosticCode = ((rawFloat & 0x003FFFFF));
				return encodeNaN(isNegative, isQuietNaN, diagnosticCode, signWidth, exponentWidth, mantissaWidth);
			} else if (Float.isInfinite(f)) {
				return encodeInfinity(f < 0.0f, signWidth, exponentWidth, mantissaWidth);
			} else if (f == 0.0f) {
				int rawFloat = Float.floatToRawIntBits(f);
				boolean isNegative = ((rawFloat & 0x80000000) != 0);
				return encodeZero(isNegative, signWidth, exponentWidth, mantissaWidth);
			} else {
				return encodeFiniteNonZero(new BigDecimal(Float.toString(f)), signWidth, exponentWidth, mantissaWidth, bias, mc);
			}
		} else if (v instanceof Long) {
			long l = v.longValue();
			if (l == 0l) {
				return encodeZero(false, signWidth, exponentWidth, mantissaWidth);
			} else {
				return encodeFiniteNonZero(BigDecimal.valueOf(l), signWidth, exponentWidth, mantissaWidth, bias, mc);
			}
		} else if (v instanceof Integer || v instanceof Short || v instanceof Byte) {
			int i = v.intValue();
			if (i == 0) {
				return encodeZero(false, signWidth, exponentWidth, mantissaWidth);
			} else {
				return encodeFiniteNonZero(BigDecimal.valueOf(i), signWidth, exponentWidth, mantissaWidth, bias, mc);
			}
		} else {
			double d = v.doubleValue();
			if (Double.isNaN(d)) {
				long rawDouble = Double.doubleToRawLongBits(d);
				boolean isNegative = ((rawDouble & 0x8000000000000000L) != 0L);
				boolean isQuietNaN = ((rawDouble & 0x0008000000000000L) != 0L);
				long diagnosticCode = ((rawDouble & 0x0007FFFFFFFFFFFFL));
				return encodeNaN(isNegative, isQuietNaN, diagnosticCode, signWidth, exponentWidth, mantissaWidth);
			} else if (Double.isInfinite(d)) {
				return encodeInfinity(d < 0.0, signWidth, exponentWidth, mantissaWidth);
			} else if (d == 0.0) {
				long rawDouble = Double.doubleToRawLongBits(d);
				boolean isNegative = ((rawDouble & 0x8000000000000000L) != 0L);
				return encodeZero(isNegative, signWidth, exponentWidth, mantissaWidth);
			} else {
				return encodeFiniteNonZero(BigDecimal.valueOf(d), signWidth, exponentWidth, mantissaWidth, bias, mc);
			}
		}
	}
	
	private static BigInteger[] encodeNaN(boolean isNegative, boolean isQuietNaN, long diagnosticCode, int signWidth, int exponentWidth, int mantissaWidth) {
		BigInteger mantissa = BigInteger.valueOf(diagnosticCode);
		if (isQuietNaN) mantissa = mantissa.setBit(mantissaWidth-1);
		else mantissa = mantissa.clearBit(mantissaWidth-1);
		return new BigInteger[] {
				(isNegative ? BigInteger.ONE.negate() : BigInteger.ZERO),
				BigInteger.ONE.negate(),
				mantissa
		};
	}
	
	private static BigInteger[] encodeInfinity(boolean isNegative, int signWidth, int exponentWidth, int mantissaWidth) {
		return new BigInteger[] {
				(isNegative ? BigInteger.ONE.negate() : BigInteger.ZERO),
				BigInteger.ONE.negate(),
				BigInteger.ZERO
		};
	}
	
	private static BigInteger[] encodeZero(boolean isNegative, int signWidth, int exponentWidth, int mantissaWidth) {
		return new BigInteger[] {
				(isNegative ? BigInteger.ONE.negate() : BigInteger.ZERO),
				BigInteger.ZERO,
				BigInteger.ZERO
		};
	}
	
	private static BigInteger[] encodeFiniteNonZero(BigDecimal v, int signWidth, int exponentWidth, int mantissaWidth, int bias, MathContext mc) {
		// writing floating-point numbers is HARD, especially when your big number library uses DECIMAL
		boolean isNegative = (v.compareTo(BigDecimal.ZERO) < 0);
		v = v.abs();
		// this is the HARD part; as written, it still screws up sometimes (see evil trick)
		int exponent = ilog2(v, mc) + bias;
		// while loop is part of an evil trick
		while (true) {
			if (exponent >= ((1 << exponentWidth) - 1)) {
				// overflow -> infinity
				return new BigInteger[] {
						(isNegative ? BigInteger.ONE.negate() : BigInteger.ZERO),
						BigInteger.ONE.negate(),
						BigInteger.ZERO
				};
			} else if (exponent <= 0) {
				// underflow -> subnormal
				int negativeExponent = bias + mantissaWidth - 1;
				BigInteger mantissa;
				if (negativeExponent < 0) {
					BigDecimal multiplier = BigDecimal.valueOf(2L).pow(-negativeExponent);
					mantissa = v.divide(multiplier, mc).setScale(0, mc.getRoundingMode()).toBigIntegerExact();
				} else {
					BigDecimal multiplier = BigDecimal.valueOf(2L).pow(negativeExponent);
					mantissa = v.multiply(multiplier, mc).setScale(0, mc.getRoundingMode()).toBigIntegerExact();
				}
				if (mantissa.testBit(mantissaWidth)) {
					// the other part of the evil trick
					// this is essentially a GOTO! that's how evil this is!
					exponent++;
					continue;
				} else {
					return new BigInteger[] {
							(isNegative ? BigInteger.ONE.negate() : BigInteger.ZERO),
							BigInteger.ZERO,
							mantissa
					};
				}
			} else {
				// normal
				int negativeExponent = bias + mantissaWidth - exponent;
				BigInteger mantissa;
				if (negativeExponent < 0) {
					BigDecimal multiplier = BigDecimal.valueOf(2L).pow(-negativeExponent);
					mantissa = v.divide(multiplier, mc).setScale(0, mc.getRoundingMode()).toBigIntegerExact();
				} else {
					BigDecimal multiplier = BigDecimal.valueOf(2L).pow(negativeExponent);
					mantissa = v.multiply(multiplier, mc).setScale(0, mc.getRoundingMode()).toBigIntegerExact();
				}
				if (!mantissa.testBit(mantissaWidth)) {
					// the other part of the evil trick
					// this is essentially a GOTO! that's how evil this is!
					if (mantissa.testBit(mantissaWidth+1)) {
						exponent++;
						continue;
					} else {
						exponent--;
						continue;
					}
				} else {
					return new BigInteger[] {
							(isNegative ? BigInteger.ONE.negate() : BigInteger.ZERO),
							BigInteger.valueOf(exponent),
							mantissa
					};
				}
			}
		}
	}
	
	private static int ilog2(BigDecimal v, MathContext mc) {
		// provide a first approximation
		// (v.precision()-v.scale()-1) is essentially the base-10 logarithm of v
		// by multiplying this by 100000/30103 (dividing by 30103/100000, or log2(10) ≈ 0.30103)
		// we can approximate the base 2 logarithm
		// naively, this would give us 0, 0, 0, 0, 3, 3, 3, 6, 6, 6, 10, 10, 10, 10, etc.
		// so we add the first digit of the (base-10) mantissa for a better approximation
		int approximation = (int)(
				(
						((long)v.precision()-(long)v.scale()-1L)*100000L
						+ (long)(v.unscaledValue().toString().charAt(0)-'0')*10000L
				) / 30103L
		);
		// now go searching for the true logarithm
		approximation += 3;
		while (true) {
			BigDecimal power = BigDecimal.valueOf(2L).pow(Math.abs(approximation));
			if (approximation < 0) power = BigDecimal.ONE.divide(power, mc);
			if (power.compareTo(v) <= 0) return approximation;
			approximation--;
		}
		// WARNING: this will go into an infinite loop with n <= 0
		// since this is a private method, we assume this has been checked beforehand
	}
}
