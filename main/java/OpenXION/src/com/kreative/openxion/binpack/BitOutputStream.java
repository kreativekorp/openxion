/*
 * Copyright &copy; 2010 Rebecca G. Bettencourt / Kreative Software
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

import java.io.*;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.BitSet;

public class BitOutputStream extends OutputStream implements Closeable, DataOutput {
	private DataOutputStream out;
	private int bitpos;
	private int bittmp;
	private long bitswritten;
	
	public BitOutputStream(OutputStream out) {
		this.out = new DataOutputStream(out);
		this.bitpos = 0;
		this.bittmp = 0;
		this.bitswritten = 0L;
	}
	
	public boolean atBitBoundary(int multiple) {
		if (multiple < 0) throw new IllegalArgumentException();
		else if (multiple == 0 || multiple == 1) return true;
		else return ((bitswritten % (long)multiple) == 0L);
	}
	
	public boolean atByteBoundary(int multiple) {
		if (multiple < 0) throw new IllegalArgumentException();
		else if (multiple == 0) return true;
		else if (multiple == 1) return (bitpos == 0);
		else return ((bitpos == 0) && (((bitswritten >> 3L) % (long)multiple) == 0L));
	}
	
	public long bitsWritten() {
		return bitswritten;
	}
	
	public long bytesWritten() {
		return (bitswritten >> 3L);
	}
	
	public void writeBit(boolean bit) throws IOException {
		if (bitpos == 0) {
			bitpos = 128;
			bittmp = 0;
		}
		if (bit) bittmp |= bitpos;
		bitpos >>= 1;
		bitswritten++;
		if (bitpos == 0) {
			out.writeByte(bittmp);
		}
	}
	
	public void writeBits(int n, BitSet bits) throws IOException {
		if (n < 0) throw new IllegalArgumentException();
		else while (n > 0) {
			n--;
			writeBit(bits.get(n));
		}
	}
	
	public void writeBitsLE(int n, BitSet bits) throws IOException {
		if (n < 0) throw new IllegalArgumentException();
		else if (n == 0); // nothing
		else if (bitpos != 0 || ((n & 7) != 0)) 
			throw new IOException("Can't write little-endian values unless on a byte boundry with a byte-multiple width");
		else {
			int nb = (n >> 3) - 1;
			while (n > 0) {
				n--;
				writeBit(bits.get((n & 7) | ((nb - (n >> 3)) << 3)));
			}
		}
	}
	
	public void writeInteger(int n, BigInteger i) throws IOException {
		if (n < 0) throw new IllegalArgumentException();
		else while (n > 0) {
			n--;
			writeBit(i.testBit(n));
		}
	}
	
	public void writeIntegerLE(int n, BigInteger i) throws IOException {
		if (n < 0) throw new IllegalArgumentException();
		else if (n == 0); // nothing
		else if (bitpos != 0 || ((n & 7) != 0)) 
			throw new IOException("Can't write little-endian values unless on a byte boundry with a byte-multiple width");
		else {
			int nb = (n >> 3) - 1;
			while (n > 0) {
				n--;
				writeBit(i.testBit((n & 7) | ((nb - (n >> 3)) << 3)));
			}
		}
	}
	
	public void writeFloat(int n, MathContext mc, Number v) throws IOException {
		if (n < 0) throw new IllegalArgumentException();
		else {
			int s = FPUtilities.optimalSignWidth(n);
			int e = FPUtilities.optimalExponentWidth(n);
			int m = FPUtilities.optimalMantissaWidth(n);
			writeFloat(s, e, m, mc, v);
		}
	}
	
	public void writeFloatLE(int n, MathContext mc, Number v) throws IOException {
		if (n < 0) throw new IllegalArgumentException();
		else {
			int s = FPUtilities.optimalSignWidth(n);
			int e = FPUtilities.optimalExponentWidth(n);
			int m = FPUtilities.optimalMantissaWidth(n);
			writeFloatLE(s, e, m, mc, v);
		}
	}
	
	public void writeFloat(int s, int e, int m, MathContext mc, Number v) throws IOException {
		if (s < 0 || s > 1 || e < 0 || m < 0) throw new IllegalArgumentException();
		else {
			int b = FPUtilities.optimalBias(e);
			writeFloat(s, e, m, b, mc, v);
		}
	}

	public void writeFloatLE(int s, int e, int m, MathContext mc, Number v) throws IOException {
		if (s < 0 || s > 1 || e < 0 || m < 0) throw new IllegalArgumentException();
		else {
			int b = FPUtilities.optimalBias(e);
			writeFloatLE(s, e, m, b, mc, v);
		}
	}
	
	public void writeFloat(int s, int e, int m, int b, MathContext mc, Number v) throws IOException {
		if (s < 0 || s > 1 || e < 0 || m < 0) throw new IllegalArgumentException();
		else {
			BigInteger[] r = FPUtilities.encodeFloat(v, s, e, m, b, mc);
			writeInteger(s, r[0]);
			writeInteger(e, r[1]);
			writeInteger(m, r[2]);
		}
	}
	
	public void writeFloatLE(int s, int e, int m, int b, MathContext mc, Number v) throws IOException {
		if (s < 0 || s > 1 || e < 0 || m < 0) throw new IllegalArgumentException();
		else if (bitpos != 0 || (((s+e+m) & 7) != 0)) 
			throw new IOException("Can't write little-endian values unless on a byte boundry with a byte-multiple width");
		else {
			BigInteger[] r = FPUtilities.encodeFloat(v, s, e, m, b, mc);
			BigInteger i = FPUtilities.joinFloat(r[0], r[1], r[2], s, e, m);
			writeIntegerLE(s+e+m, i);
		}
	}
	
	public void close() throws IOException {
		if (bitpos != 0) {
			out.writeByte(bittmp);
		}
		out.flush();
		out.close();
	}
	
	public void flush() throws IOException {
		out.flush();
	}
	
	public void write(int b) throws IOException {
		if (bitpos == 0) {
			out.write(b);
			bitswritten += 8L;
		} else {
			int pos = 128;
			while (pos != 0) {
				writeBit((b & pos) != 0);
				pos >>>= 1;
			}
		}
	}
	
	public void write(byte[] b) throws IOException {
		if (bitpos == 0) {
			out.write(b);
			bitswritten += ((long)b.length << 3L);
		} else {
			for (byte bb : b) {
				int pos = 128;
				while (pos != 0) {
					writeBit((bb & pos) != 0);
					pos >>>= 1;
				}
			}
		}
	}
	
	public void write(byte[] b, int off, int len) throws IOException {
		if (bitpos == 0) {
			out.write(b, off, len);
			bitswritten += ((long)len << 3L);
		} else {
			for (int i = 0; i < len; off++, i++) {
				int pos = 128;
				while (pos != 0) {
					writeBit((b[off] & pos) != 0);
					pos >>>= 1;
				}
			}
		}
	}

	public void writeBoolean(boolean v) throws IOException {
		if (bitpos == 0) {
			out.writeBoolean(v);
			bitswritten += 8L;
		} else {
			writeByte(v ? 1 : 0);
		}
	}

	public void writeByte(int v) throws IOException {
		if (bitpos == 0) {
			out.writeByte(v);
			bitswritten += 8L;
		} else {
			int pos = 128;
			while (pos != 0) {
				writeBit((v & pos) != 0);
				pos >>>= 1;
			}
		}
	}

	public void writeShort(int v) throws IOException {
		if (bitpos == 0) {
			out.writeShort(v);
			bitswritten += 16L;
		} else {
			int pos = 32768;
			while (pos != 0) {
				writeBit((v & pos) != 0);
				pos >>>= 1;
			}
		}
	}
	
	public void writeShortLE(int v) throws IOException {
		if (bitpos == 0) {
			out.writeShort(Short.reverseBytes((short)v));
			bitswritten += 16L;
		} else {
			throw new IOException("Can't write little-endian values unless on a byte boundry with a byte-multiple width");
		}
	}

	public void writeChar(int v) throws IOException {
		if (bitpos == 0) {
			out.writeChar(v);
			bitswritten += 16L;
		} else {
			int pos = 32768;
			while (pos != 0) {
				writeBit((v & pos) != 0);
				pos >>>= 1;
			}
		}
	}
	
	public void writeCharLE(int v) throws IOException {
		if (bitpos == 0) {
			out.writeChar(Character.reverseBytes((char)v));
			bitswritten += 16L;
		} else {
			throw new IOException("Can't write little-endian values unless on a byte boundry with a byte-multiple width");
		}
	}

	public void writeInt(int v) throws IOException {
		if (bitpos == 0) {
			out.writeInt(v);
			bitswritten += 32L;
		} else {
			int pos = Integer.MIN_VALUE;
			while (pos != 0) {
				writeBit((v & pos) != 0);
				pos >>>= 1;
			}
		}
	}
	
	public void writeIntLE(int v) throws IOException {
		if (bitpos == 0) {
			out.writeInt(Integer.reverseBytes(v));
			bitswritten += 32L;
		} else {
			throw new IOException("Can't write little-endian values unless on a byte boundry with a byte-multiple width");
		}
	}

	public void writeLong(long v) throws IOException {
		if (bitpos == 0) {
			out.writeLong(v);
			bitswritten += 64L;
		} else {
			long pos = Long.MIN_VALUE;
			while (pos != 0L) {
				writeBit((v & pos) != 0L);
				pos >>>= 1L;
			}
		}
	}
	
	public void writeLongLE(long v) throws IOException {
		if (bitpos == 0) {
			out.writeLong(Long.reverseBytes(v));
			bitswritten += 64L;
		} else {
			throw new IOException("Can't write little-endian values unless on a byte boundry with a byte-multiple width");
		}
	}

	public void writeFloat(float v) throws IOException {
		if (bitpos == 0) {
			out.writeFloat(v);
			bitswritten += 32L;
		} else {
			writeInt(Float.floatToRawIntBits(v));
		}
	}
	
	public void writeFloatLE(float v) throws IOException {
		if (bitpos == 0) {
			out.writeInt(Integer.reverseBytes(Float.floatToRawIntBits(v)));
			bitswritten += 32L;
		} else {
			throw new IOException("Can't write little-endian values unless on a byte boundry with a byte-multiple width");
		}
	}

	public void writeDouble(double v) throws IOException {
		if (bitpos == 0) {
			out.writeDouble(v);
			bitswritten += 64L;
		} else {
			writeLong(Double.doubleToRawLongBits(v));
		}
	}
	
	public void writeDoubleLE(double v) throws IOException {
		if (bitpos == 0) {
			out.writeLong(Long.reverseBytes(Double.doubleToRawLongBits(v)));
			bitswritten += 64L;
		} else {
			throw new IOException("Can't write little-endian values unless on a byte boundry with a byte-multiple width");
		}
	}

	public void writeBytes(String s) throws IOException {
		if (bitpos == 0) {
			out.writeBytes(s);
			bitswritten += ((long)s.length() << 3L);
		} else {
			CharacterIterator i = new StringCharacterIterator(s);
			for (char ch = i.first(); ch != CharacterIterator.DONE; ch = i.next()) {
				writeByte(ch);
			}
		}
	}

	public void writeChars(String s) throws IOException {
		if (bitpos == 0) {
			out.writeChars(s);
			bitswritten += ((long)s.length() << 4L);
		} else {
			CharacterIterator i = new StringCharacterIterator(s);
			for (char ch = i.first(); ch != CharacterIterator.DONE; ch = i.next()) {
				writeChar(ch);
			}
		}
	}
	
	public void writeCharsLE(String s) throws IOException {
		if (bitpos == 0) {
			CharacterIterator i = new StringCharacterIterator(s);
			for (char ch = i.first(); ch != CharacterIterator.DONE; ch = i.next()) {
				out.writeChar(Character.reverseBytes(ch));
				bitswritten += 16L;
			}
		} else {
			throw new IOException("Can't write little-endian values unless on a byte boundry with a byte-multiple width");
		}
	}

	public void writeUTF(String str) throws IOException {
		throw new UnsupportedOperationException();
	}
}
