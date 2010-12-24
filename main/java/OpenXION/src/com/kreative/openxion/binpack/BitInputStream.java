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
import java.util.BitSet;

public class BitInputStream extends InputStream implements Closeable, DataInput {
	private DataInputStream in;
	private int bitpos;
	private int bittmp;
	private long bitsread;
	private int markbitpos;
	private int markbittmp;
	private long markbitsread;
	
	public BitInputStream(InputStream in) {
		this.in = new DataInputStream(in);
		this.bitpos = 0;
		this.bittmp = 0;
		this.bitsread = 0L;
		this.markbitpos = 0;
		this.markbittmp = 0;
		this.markbitsread = 0L;
	}
	
	public boolean atBitBoundary(int multiple) {
		if (multiple < 0) throw new IllegalArgumentException();
		else if (multiple == 0 || multiple == 1) return true;
		else return ((bitsread % (long)multiple) == 0L);
	}
	
	public boolean atByteBoundary(int multiple) {
		if (multiple < 0) throw new IllegalArgumentException();
		else if (multiple == 0) return true;
		else if (multiple == 1) return (bitpos == 0);
		else return ((bitpos == 0) && (((bitsread >> 3L) % (long)multiple) == 0L));
	}
	
	public long bitsRead() {
		return bitsread;
	}
	
	public long bytesRead() {
		return (bitsread >> 3L);
	}
	
	public boolean readBit() throws IOException {
		if (bitpos == 0) {
			int tmp = in.readByte();
			bitpos = 128;
			bittmp = tmp;
		}
		boolean res = ((bittmp & bitpos) != 0);
		bitpos >>= 1;
		bitsread++;
		return res;
	}
	
	public boolean skipBit() throws IOException {
		if (bitpos == 0) {
			int tmp = in.read();
			if (tmp < 0) return false;
			bitpos = 128;
			bittmp = tmp;
		}
		bitpos >>= 1;
		bitsread++;
		return true;
	}
	
	public boolean availableBit() throws IOException {
		if (bitpos == 0) {
			return in.available() > 0;
		} else {
			return true;
		}
	}
	
	public BitSet readBits(int n) throws IOException {
		if (n < 0) throw new IllegalArgumentException();
		else if (n == 0) return new BitSet();
		else {
			BitSet b = new BitSet();
			while (n > 0) {
				n--;
				if (readBit()) b.set(n);
			}
			return b;
		}
	}
	
	public BitSet readBitsLE(int n) throws IOException {
		if (n < 0) throw new IllegalArgumentException();
		else if (n == 0) return new BitSet();
		else if (bitpos != 0 || ((n & 7) != 0)) 
			throw new IOException("Can't read little-endian values unless on a byte boundry with a byte-multiple width");
		else {
			int nb = (n >> 3) - 1;
			BitSet b = new BitSet();
			while (n > 0) {
				n--;
				if (readBit()) b.set((n & 7) | ((nb - (n >> 3)) << 3));
			}
			return b;
		}
	}
	
	public long skipBits(long n) throws IOException {
		if (n < 0) throw new IllegalArgumentException();
		else if (n == 0) return 0;
		else {
			long a = 0;
			while (n > 0 && bitpos > 0) {
				if (skipBit()) {
					n--;
					a++;
				} else {
					return a;
				}
			}
			if (n >= 8) {
				long bs = in.skip(n >> 3);
				bitsread += (bs << 3);
				n -= (bs << 3);
				a += (bs << 3);
			}
			while (n > 0) {
				if (skipBit()) {
					n--;
					a++;
				} else {
					return a;
				}
			}
			return a;
		}
	}
	
	public long availableBits() throws IOException {
		int m = bitpos;
		int b = 0;
		while (m > 0) {
			m >>= 1;
			b++;
		}
		return (long)b + ((long)in.available() << 3L);
	}
	
	public BigInteger readInteger(int n) throws IOException {
		if (n < 0) throw new IllegalArgumentException();
		else if (n == 0) return BigInteger.ZERO;
		else {
			n--;
			BigInteger i = (readBit() ? BigInteger.ONE.negate().shiftLeft(n) : BigInteger.ZERO);
			while (n > 0) {
				n--;
				if (readBit()) i = i.setBit(n);
			}
			return i;
		}
	}
	
	public BigInteger readIntegerLE(int n) throws IOException {
		if (n < 0) throw new IllegalArgumentException();
		else if (n == 0) return BigInteger.ZERO;
		else if (bitpos != 0 || ((n & 7) != 0)) 
			throw new IOException("Can't read little-endian values unless on a byte boundry with a byte-multiple width");
		else {
			int on = n;
			int nb = (n >> 3) - 1;
			BigInteger i = BigInteger.ZERO;
			while (n > 0) {
				n--;
				if (readBit()) i = i.setBit((n & 7) | ((nb - (n >> 3)) << 3));
			}
			if (i.testBit(on-1)) {
				i = i.xor(BigInteger.ONE.negate().shiftLeft(on));
			}
			return i;
		}
	}
	
	public BigInteger readUnsignedInteger(int n) throws IOException {
		if (n < 0) throw new IllegalArgumentException();
		else if (n == 0) return BigInteger.ZERO;
		else {
			BigInteger i = BigInteger.ZERO;
			while (n > 0) {
				n--;
				if (readBit()) i = i.setBit(n);
			}
			return i;
		}
	}
	
	public BigInteger readUnsignedIntegerLE(int n) throws IOException {
		if (n < 0) throw new IllegalArgumentException();
		else if (n == 0) return BigInteger.ZERO;
		else if (bitpos != 0 || ((n & 7) != 0)) 
			throw new IOException("Can't read little-endian values unless on a byte boundry with a byte-multiple width");
		else {
			int nb = (n >> 3) - 1;
			BigInteger i = BigInteger.ZERO;
			while (n > 0) {
				n--;
				if (readBit()) i = i.setBit((n & 7) | ((nb - (n >> 3)) << 3));
			}
			return i;
		}
	}
	
	public Number readFloat(int n, MathContext mc) throws IOException {
		if (n < 0) throw new IllegalArgumentException();
		else {
			int s = FPUtilities.optimalSignWidth(n);
			int e = FPUtilities.optimalExponentWidth(n);
			int m = FPUtilities.optimalMantissaWidth(n);
			return readFloat(s, e, m, mc);
		}
	}
	
	public Number readFloatLE(int n, MathContext mc) throws IOException {
		if (n < 0) throw new IllegalArgumentException();
		else {
			int s = FPUtilities.optimalSignWidth(n);
			int e = FPUtilities.optimalExponentWidth(n);
			int m = FPUtilities.optimalMantissaWidth(n);
			return readFloatLE(s, e, m, mc);
		}
	}
	
	public Number readFloat(int s, int e, int m, MathContext mc) throws IOException {
		if (s < 0 || s > 1 || e < 0 || m < 0) throw new IllegalArgumentException();
		else {
			int b = FPUtilities.optimalBias(e);
			return readFloat(s, e, m, b, mc);
		}
	}
	
	public Number readFloatLE(int s, int e, int m, MathContext mc) throws IOException {
		if (s < 0 || s > 1 || e < 0 || m < 0) throw new IllegalArgumentException();
		else {
			int b = FPUtilities.optimalBias(e);
			return readFloatLE(s, e, m, b, mc);
		}
	}
	
	public Number readFloat(int s, int e, int m, int b, MathContext mc) throws IOException {
		if (s < 0 || s > 1 || e < 0 || m < 0) throw new IllegalArgumentException();
		else {
			BigInteger si = readUnsignedInteger(s);
			BigInteger ei = readUnsignedInteger(e);
			BigInteger mi = readUnsignedInteger(m);
			return FPUtilities.decodeFloat(si, ei, mi, s, e, m, b, mc);
		}
	}
	
	public Number readFloatLE(int s, int e, int m, int b, MathContext mc) throws IOException {
		if (s < 0 || s > 1 || e < 0 || m < 0) throw new IllegalArgumentException();
		else if (bitpos != 0 || (((s+e+m) & 7) != 0)) 
			throw new IOException("Can't read little-endian values unless on a byte boundry with a byte-multiple width");
		else {
			BigInteger i = readUnsignedIntegerLE(s+e+m);
			BigInteger[] ii = FPUtilities.splitFloat(i, s, e, m);
			return FPUtilities.decodeFloat(ii[0], ii[1], ii[2], s, e, m, b, mc);
		}
	}
	
	public int available() throws IOException {
		return in.available();
	}
	
	public void close() throws IOException {
		in.close();
	}
	
	public void mark(int readlimit) {
		in.mark(readlimit);
		markbitpos = bitpos;
		markbittmp = bittmp;
		markbitsread = bitsread;
	}
	
	public boolean markSupported() {
		return in.markSupported();
	}
	
	public int read() throws IOException {
		if (bitpos == 0) {
			int res = in.read();
			if (res >= 0) bitsread += 8L;
			return res;
		} else {
			int tmp = in.read();
			if (tmp >= 0) {
				int res = 0;
				int rpos = 128;
				while (bitpos > 0) {
					if ((bittmp & bitpos) != 0)
						res |= rpos;
					rpos >>= 1;
					bitpos >>= 1;
					bitsread++;
				}
				bitpos = 128;
				bittmp = tmp;
				while (rpos > 0) {
					if ((bittmp & bitpos) != 0)
						res |= rpos;
					rpos >>= 1;
					bitpos >>= 1;
					bitsread++;
				}
				return res;
			} else {
				return -1;
			}
		}
	}
	
	public int read(byte[] b) throws IOException {
		if (bitpos == 0) {
			int res = in.read(b);
			if (res >= 0) bitsread += ((long)res << 3L);
			return res;
		} else {
			int res = 0;
			int tmp = 0;
			while (res < b.length && (tmp = read()) >= 0) {
				b[res++] = (byte)tmp;
			}
			return res;
		}
	}
	
	public int read(byte[] b, int off, int len) throws IOException {
		if (bitpos == 0) {
			int res = in.read(b, off, len);
			if (res >= 0) bitsread += ((long)res << 3L);
			return res;
		} else {
			int res = 0;
			int tmp = 0;
			while (res < len && (tmp = read()) >= 0) {
				b[off++] = (byte)tmp;
				res++;
			}
			return res;
		}
	}
	
	public void reset() throws IOException {
		in.reset();
		bitpos = markbitpos;
		bittmp = markbittmp;
		bitsread = markbitsread;
	}
	
	public long skip(long n) throws IOException {
		if (bitpos == 0) {
			long res = in.skip(n);
			if (res >= 0) bitsread += ((long)res << 3L);
			return res;
		} else {
			return skipBits(n << 3L) >> 3L;
		}
	}

	public void readFully(byte[] b) throws IOException {
		if (bitpos == 0) {
			in.readFully(b);
			bitsread += ((long)b.length << 3L);
		} else {
			int res = 0;
			while (res < b.length) {
				b[res++] = readByte();
			}
		}
	}

	public void readFully(byte[] b, int off, int len) throws IOException {
		if (bitpos == 0) {
			in.readFully(b, off, len);
			bitsread += ((long)len << 3L);
		} else {
			int res = 0;
			while (res < len) {
				b[off++] = readByte();
				res++;
			}
		}
	}

	public int skipBytes(int n) throws IOException {
		return (int)skip((long)n);
	}

	public boolean readBoolean() throws IOException {
		if (bitpos == 0) {
			boolean res = in.readBoolean();
			bitsread += 8L;
			return res;
		} else {
			return (readByte() != 0);
		}
	}

	public byte readByte() throws IOException {
		if (bitpos == 0) {
			byte res = in.readByte();
			bitsread += 8L;
			return res;
		} else {
			int tmp = in.readByte();
			byte res = 0;
			int rpos = 128;
			while (bitpos > 0) {
				if ((bittmp & bitpos) != 0)
					res |= rpos;
				rpos >>= 1;
				bitpos >>= 1;
				bitsread++;
			}
			bitpos = 128;
			bittmp = tmp;
			while (rpos > 0) {
				if ((bittmp & bitpos) != 0)
					res |= rpos;
				rpos >>= 1;
				bitpos >>= 1;
				bitsread++;
			}
			return res;
		}
	}

	public int readUnsignedByte() throws IOException {
		if (bitpos == 0) {
			int res = in.readUnsignedByte();
			bitsread += 8L;
			return res;
		} else {
			return (readByte() & 0xFF);
		}
	}

	public short readShort() throws IOException {
		if (bitpos == 0) {
			short res = in.readShort();
			bitsread += 16L;
			return res;
		} else {
			int tmp = in.readShort();
			short res = 0;
			int rpos = 32768;
			while (bitpos > 0) {
				if ((bittmp & bitpos) != 0)
					res |= rpos;
				rpos >>= 1;
				bitpos >>= 1;
				bitsread++;
			}
			bitpos = 32768;
			bittmp = tmp;
			while (rpos > 0) {
				if ((bittmp & bitpos) != 0)
					res |= rpos;
				rpos >>= 1;
				bitpos >>= 1;
				bitsread++;
			}
			return res;
		}
	}
	
	public short readShortLE() throws IOException {
		if (bitpos == 0) {
			short res = Short.reverseBytes(in.readShort());
			bitsread += 16L;
			return res;
		} else {
			throw new IOException("Can't read little-endian values unless on a byte boundry with a byte-multiple width");
		}
	}

	public int readUnsignedShort() throws IOException {
		if (bitpos == 0) {
			int res = in.readUnsignedShort();
			bitsread += 16L;
			return res;
		} else {
			return (readShort() & 0xFFFF);
		}
	}
	
	public int readUnsignedShortLE() throws IOException {
		if (bitpos == 0) {
			int res = Integer.reverseBytes(in.readUnsignedShort()) >>> 16;
			bitsread += 16L;
			return res;
		} else {
			throw new IOException("Can't read little-endian values unless on a byte boundry with a byte-multiple width");
		}
	}

	public char readChar() throws IOException {
		if (bitpos == 0) {
			char res = in.readChar();
			bitsread += 16L;
			return res;
		} else {
			int tmp = in.readChar();
			char res = 0;
			int rpos = 32768;
			while (bitpos > 0) {
				if ((bittmp & bitpos) != 0)
					res |= rpos;
				rpos >>= 1;
				bitpos >>= 1;
				bitsread++;
			}
			bitpos = 32768;
			bittmp = tmp;
			while (rpos > 0) {
				if ((bittmp & bitpos) != 0)
					res |= rpos;
				rpos >>= 1;
				bitpos >>= 1;
				bitsread++;
			}
			return res;
		}
	}
	
	public char readCharLE() throws IOException {
		if (bitpos == 0) {
			char res = Character.reverseBytes(in.readChar());
			bitsread += 16L;
			return res;
		} else {
			throw new IOException("Can't read little-endian values unless on a byte boundry with a byte-multiple width");
		}
	}

	public int readInt() throws IOException {
		if (bitpos == 0) {
			int res = in.readInt();
			bitsread += 32L;
			return res;
		} else {
			int tmp = in.readInt();
			int res = 0;
			int rpos = -2147483648;
			while (bitpos != 0) {
				if ((bittmp & bitpos) != 0)
					res |= rpos;
				rpos >>>= 1;
				bitpos >>>= 1;
				bitsread++;
			}
			bitpos = -2147483648;
			bittmp = tmp;
			while (rpos != 0) {
				if ((bittmp & bitpos) != 0)
					res |= rpos;
				rpos >>>= 1;
				bitpos >>>= 1;
				bitsread++;
			}
			return res;
		}
	}
	
	public int readIntLE() throws IOException {
		if (bitpos == 0) {
			int res = Integer.reverseBytes(in.readInt());
			bitsread += 32L;
			return res;
		} else {
			throw new IOException("Can't read little-endian values unless on a byte boundry with a byte-multiple width");
		}
	}

	public long readLong() throws IOException {
		if (bitpos == 0) {
			long res = in.readLong();
			bitsread += 64L;
			return res;
		} else {
			long tmp = in.readLong();
			long res = 0;
			long rpos = Long.MIN_VALUE;
			while (bitpos != 0) {
				if ((bittmp & bitpos) != 0)
					res |= rpos;
				rpos >>>= 1L;
				bitpos >>>= 1;
				bitsread++;
			}
			long bitpos = Long.MIN_VALUE;
			long bittmp = tmp;
			while (rpos != 0) {
				if ((bittmp & bitpos) != 0)
					res |= rpos;
				rpos >>>= 1;
				bitpos >>>= 1;
				bitsread++;
			}
			this.bitpos = (int)bitpos;
			this.bittmp = (int)bittmp;
			return res;
		}
	}
	
	public long readLongLE() throws IOException {
		if (bitpos == 0) {
			long res = Long.reverseBytes(in.readLong());
			bitsread += 64L;
			return res;
		} else {
			throw new IOException("Can't read little-endian values unless on a byte boundry with a byte-multiple width");
		}
	}

	public float readFloat() throws IOException {
		if (bitpos == 0) {
			float res = in.readFloat();
			bitsread += 32L;
			return res;
		} else {
			return Float.intBitsToFloat(readInt());
		}
	}
	
	public float readFloatLE() throws IOException {
		if (bitpos == 0) {
			float res = Float.intBitsToFloat(Integer.reverseBytes(in.readInt()));
			bitsread += 32L;
			return res;
		} else {
			throw new IOException("Can't read little-endian values unless on a byte boundry with a byte-multiple width");
		}
	}

	public double readDouble() throws IOException {
		if (bitpos == 0) {
			double res = in.readDouble();
			bitsread += 64L;
			return res;
		} else {
			return Double.longBitsToDouble(readLong());
		}
	}
	
	public double readDoubleLE() throws IOException {
		if (bitpos == 0) {
			double res = Double.longBitsToDouble(Long.reverseBytes(in.readLong()));
			bitsread += 64L;
			return res;
		} else {
			throw new IOException("Can't read little-endian values unless on a byte boundry with a byte-multiple width");
		}
	}

	public String readLine() throws IOException {
		throw new UnsupportedOperationException();
	}

	public String readUTF() throws IOException {
		throw new UnsupportedOperationException();
	}
}
