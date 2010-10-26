/*
 * Copyright &copy; 2009-2010 Rebecca G. Bettencourt / Kreative Software
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

import java.math.*;

/**
 * The BinaryNumericChunkType enum represents a numeric chunk
 * (tinyInt, smallInt, mediumInt, longInt, halfFloat, singleFloat,
 * doubleFloat) of a binary.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public enum BinaryNumericChunkType {
	TINYINT(1,false),
	SHORTINT(2,false),
	MEDIUMINT(4,false),
	LONGINT(8,false),
	HALFFLOAT(2,true),
	SINGLEFLOAT(4,true),
	DOUBLEFLOAT(8,true);
	
	private int length;
	private boolean isFloat;
	
	private BinaryNumericChunkType(int length, boolean isFloat) {
		this.length = length;
		this.isFloat = isFloat;
	}
	
	public int length() {
		return length;
	}
	
	public boolean isFloat() {
		return isFloat;
	}
	
	private long getBits(byte[] b, boolean littleEndian) {
		long r = 0;
		if (littleEndian) {
			for (int i = b.length-1; i >= 0; i--) {
				r <<= 8;
				r |= b[i] & 0xFF;
			}
		} else {
			for (int i = 0; i < b.length; i++) {
				r <<= 8;
				r |= b[i] & 0xFF;
			}
		}
		return r;
	}
	
	public BigInteger bigIntegerValueOf(byte[] b, boolean unsigned, boolean littleEndian) {
		long r = getBits(b, littleEndian);
		if (isFloat) {
			switch (length) {
			case 2: return BigInteger.valueOf((long)HalfFloat.intBitsToHalfFloat((int)r).floatValue());
			case 4: return BigInteger.valueOf((long)Float.intBitsToFloat((int)r));
			case 8: return BigInteger.valueOf((long)Double.longBitsToDouble((long)r));
			default: return BigInteger.ZERO;
			}
		} else {
			if (unsigned) {
				if (r < 0) {
					BigInteger d = BigInteger.valueOf(Long.MIN_VALUE).abs();
					return d.add(d).add(BigInteger.valueOf(r));
				} else {
					return BigInteger.valueOf(r);
				}
			}
			else switch (length) {
			case 1: return BigInteger.valueOf((long)(byte)r);
			case 2: return BigInteger.valueOf((long)(short)r);
			case 4: return BigInteger.valueOf((long)(int)r);
			case 8: return BigInteger.valueOf((long)(long)r);
			default: return BigInteger.ZERO;
			}
		}
	}
	
	public BigDecimal bigDecimalValueOf(byte[] b, boolean unsigned, boolean littleEndian) {
		long r = getBits(b, littleEndian);
		if (isFloat) {
			switch (length) {
			case 2: return BigDecimal.valueOf(HalfFloat.intBitsToHalfFloat((int)r).floatValue());
			case 4: return BigDecimal.valueOf(Float.intBitsToFloat((int)r));
			case 8: return BigDecimal.valueOf(Double.longBitsToDouble((long)r));
			default: return BigDecimal.ZERO;
			}
		} else {
			if (unsigned) {
				if (r < 0) {
					BigDecimal d = BigDecimal.valueOf(Long.MIN_VALUE).abs();
					return d.add(d).add(BigDecimal.valueOf(r));
				} else {
					return BigDecimal.valueOf(r);
				}
			}
			else switch (length) {
			case 1: return BigDecimal.valueOf((long)(byte)r);
			case 2: return BigDecimal.valueOf((long)(short)r);
			case 4: return BigDecimal.valueOf((long)(int)r);
			case 8: return BigDecimal.valueOf((long)(long)r);
			default: return BigDecimal.ZERO;
			}
		}
	}
	
	private byte[] putBits(long r, boolean littleEndian) {
		byte[] b = new byte[length];
		if (littleEndian) {
			for (int i = 0; i < length; i++) {
				b[i] = (byte)r;
				r >>>= 8;
			}
		} else {
			for (int i = length-1; i >= 0; i--) {
				b[i] = (byte)r;
				r >>>= 8;
			}
		}
		return b;
	}
	
	public byte[] byteArrayValueOf(int v, boolean littleEndian) {
		long r;
		if (isFloat) {
			switch (length) {
			case 2: r = HalfFloat.halfFloatToRawIntBits(new HalfFloat(v)); break;
			case 4: r = Float.floatToRawIntBits((float)v); break;
			case 8: r = Double.doubleToRawLongBits((double)v); break;
			default: r = 0;
			}
		} else {
			r = (long)v;
		}
		return putBits(r, littleEndian);
	}
	
	public byte[] byteArrayValueOf(long v, boolean littleEndian) {
		long r;
		if (isFloat) {
			switch (length) {
			case 2: r = HalfFloat.halfFloatToRawIntBits(new HalfFloat(v)); break;
			case 4: r = Float.floatToRawIntBits((float)v); break;
			case 8: r = Double.doubleToRawLongBits((double)v); break;
			default: r = 0;
			}
		} else {
			r = (long)v;
		}
		return putBits(r, littleEndian);
	}
	
	public byte[] byteArrayValueOf(float v, boolean littleEndian) {
		long r;
		if (isFloat) {
			switch (length) {
			case 2: r = HalfFloat.halfFloatToRawIntBits(new HalfFloat(v)); break;
			case 4: r = Float.floatToRawIntBits((float)v); break;
			case 8: r = Double.doubleToRawLongBits((double)v); break;
			default: r = 0;
			}
		} else {
			r = (long)v;
		}
		return putBits(r, littleEndian);
	}
	
	public byte[] byteArrayValueOf(double v, boolean littleEndian) {
		long r;
		if (isFloat) {
			switch (length) {
			case 2: r = HalfFloat.halfFloatToRawIntBits(new HalfFloat(v)); break;
			case 4: r = Float.floatToRawIntBits((float)v); break;
			case 8: r = Double.doubleToRawLongBits((double)v); break;
			default: r = 0;
			}
		} else {
			r = (long)v;
		}
		return putBits(r, littleEndian);
	}
	
	public String toString() {
		return name().toLowerCase().replace("int","Int").replace("float","Float");
	}
	
	public String toPluralString() {
		return name().toLowerCase().replace("int","Int").replace("float","Float")+"s";
	}
}
