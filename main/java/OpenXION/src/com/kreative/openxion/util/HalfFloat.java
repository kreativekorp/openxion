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

package com.kreative.openxion.util;

/**
 * The HalfFloat class encodes and decodes 16-bit
 * IEEE-like floating point numbers with 1 sign bit,
 * 5 exponent bits, and 10 mantissa bits.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class HalfFloat extends Number implements Comparable<HalfFloat> {
	private static final long serialVersionUID = 1L;
	private float f;
	
	public static final HalfFloat MAX_VALUE = intBitsToHalfFloat(0x7BFF);
	public static final HalfFloat MIN_VALUE = intBitsToHalfFloat(0xFBFF);
	public static final HalfFloat NaN = intBitsToHalfFloat(0x7FFF);
	public static final HalfFloat NEGATIVE_INFINITY = intBitsToHalfFloat(0xFC00);
	public static final HalfFloat POSITIVE_INFINITY = intBitsToHalfFloat(0x7C00);
	public static final int SIZE = 16;
	public static final Class<HalfFloat> TYPE = HalfFloat.class;
	
	public HalfFloat(float value) {
		this.f = value;
	}
	
	public HalfFloat(double value) {
		this.f = (float)value;
	}
	
	public HalfFloat(String s) {
		this.f = new Float(s);
	}
	
	private static final int emax = (1 << 5)-1;
	private static final int bias = (1 << 4)-1;
	private static final int mantMask = (1 << 10)-1;
	private static final int mantMsbMask = 1 << 9;
	private static final int expMask = ((1 << 5)-1) << 10;
	private static final int signMask = 1 << 15;
	
	public static HalfFloat intBitsToHalfFloat(int bits) {
		int s = (bits & signMask) >>> 15;
		int e = (bits & expMask) >>> 10;
		int m = (bits & mantMask);
		int floatBits = (int)(s << 31); // preserve sign
		if (e == 0) {
			// zero or subnormal
			if (m != 0) {
				// subnormal; must be normalized
				int ne = 1-bias+127;
				int nm = m << 13;
				while ((nm & 0x800000) == 0) {
					nm <<= 1;
					ne--;
				}
				floatBits |= (ne & 0xFF) << 23;
				floatBits |= (nm & 0x7FFFFF);
			}
		} else if (e == emax) {
			// infinity or nan
			floatBits |= 0x7F800000;
			floatBits |= (m & mantMsbMask) << 13; // preserve distinction between quiet nan and signaling nan
			floatBits |= (m & (mantMsbMask-1));
		} else {
			// normal number
			int ne = e-bias+127;
			int nm = m << 13;
			floatBits |= (ne & 0xFF) << 23;
			floatBits |= (nm & 0x7FFFFF);
		}
		return new HalfFloat(Float.intBitsToFloat(floatBits));
	}
	
	public static int halfFloatToIntBits(HalfFloat hf) {
		int floatBits = Float.floatToIntBits(hf.f);
		return halfFloatToXIntBits(floatBits);
	}
	
	public static int halfFloatToRawIntBits(HalfFloat hf) {
		int floatBits = Float.floatToRawIntBits(hf.f);
		return halfFloatToXIntBits(floatBits);
	}
	
	private static int halfFloatToXIntBits(int floatBits) {
		int s = (floatBits & 0x80000000) >>> 31;
		int e = (floatBits & 0x7F800000) >>> 23;
		int m = (floatBits & 0x007FFFFF);
		int bits = (s << 15); // preserve sign
		if (e == 0) {
			// zero or subnormal
			// we'll take a shortcut here since subnormal double-precision floats
			// are not likely to be anywhere near the range for minifloats
		} else if (e == 255) {
			// infinity or nan
			bits |= expMask;
			bits |= (m & 0x400000) >>> 13; // preserve distinction between quiet nan and signaling nan
			bits |= (m & (mantMsbMask-1));
		} else {
			// normal number
			int ne = e-127+bias;
			int nm = m >>> 13;
			if (ne >= emax) {
				// overflow; substitute infinity
				bits |= expMask;
			} else if (ne <= 0) {
				// must be subnormalized
				nm |= 1 << 10;
				nm >>>= 1-ne;
				bits |= (nm & mantMask);
			} else {
				// perfectly comfy
				bits |= (ne << 10) & expMask;
				bits |= (nm & mantMask);
			}
		}
		return bits;
	}
	
	public byte byteValue() {
		return (byte)f;
	}
	
	public static int compare(HalfFloat hf1, HalfFloat hf2) {
		return Float.compare(hf1.f, hf2.f);
	}
	
	public int compareTo(HalfFloat hf) {
		return Float.compare(f, hf.f);
	}

	public double doubleValue() {
		return f;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof HalfFloat) {
			return f == ((HalfFloat)obj).f;
		} else if (obj instanceof Number) {
			return f == ((Number)obj).floatValue();
		} else {
			return false;
		}
	}

	public float floatValue() {
		return f;
	}
	
	public int hashCode() {
		return ((Float)f).hashCode();
	}

	public int intValue() {
		return (int)f;
	}
	
	public boolean isInfinite() {
		return Float.isInfinite(f);
	}
	
	public static boolean isInfinite(HalfFloat hf) {
		return Float.isInfinite(hf.f);
	}
	
	public boolean isNaN() {
		return Float.isNaN(f);
	}
	
	public static boolean isNaN(HalfFloat hf) {
		return Float.isNaN(hf.f);
	}

	public long longValue() {
		return (long)f;
	}
	
	public HalfFloat parseHalfFloat(String s) {
		return new HalfFloat(Float.parseFloat(s));
	}
	
	public short shortValue() {
		return (short)f;
	}
	
	public static String toHexString(HalfFloat hf) {
		return Float.toHexString(hf.f);
	}
	
	public String toString() {
		return Float.toString(f);
	}
	
	public static String toString(HalfFloat hf) {
		return Float.toString(hf.f);
	}
	
	public static HalfFloat valueOf(HalfFloat hf) {
		return hf;
	}
	
	public static HalfFloat valueOf(String s) {
		return new HalfFloat(s);
	}
}
