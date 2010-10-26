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
 * @since OpenXION 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.io;

import java.io.*;

/**
 * An XNLittleEndianIOStream is an XNFilterIOStream that
 * overrides the read and write methods inherited from the
 * DataInput and DataOutput interfaces to reverse
 * the endianness of any data written to the contained
 * XNIOStream through those methods. Since RandomAccessFile,
 * DataInputStream, DataOutputStream, and the Java virtual
 * machine itself all use big-endian values, it is assumed
 * that the same methods in the contained XNIOStream also
 * use big-endian values; therefore, the effect of reversing
 * the endianness will be to use little-endian values.
 * @since OpenXION 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XNLittleEndianIOStream extends XNFilterIOStream {
	public XNLittleEndianIOStream(XNIOStream stream) {
		super(stream);
	}
	
	public char readChar() throws IOException {
		return Character.reverseBytes(stream.readChar());
	}

	public double readDouble() throws IOException {
		return Double.longBitsToDouble(Long.reverseBytes(stream.readLong()));
	}

	public float readFloat() throws IOException {
		return Float.intBitsToFloat(Integer.reverseBytes(stream.readInt()));
	}

	public int readInt() throws IOException {
		return Integer.reverseBytes(stream.readInt());
	}

	public long readLong() throws IOException {
		return Long.reverseBytes(stream.readLong());
	}

	public short readShort() throws IOException {
		return Short.reverseBytes(stream.readShort());
	}

	public String readUTF() throws IOException {
		int len = Short.reverseBytes(stream.readShort());
		if (len < 0) len += 0x10000;
		byte[] b = new byte[len];
		stream.read(b);
		return new String(b, "UTF-8");
	}

	public int readUnsignedShort() throws IOException {
		int v = Short.reverseBytes(stream.readShort());
		if (v < 0) v += 0x10000;
		return v;
	}

	public void writeChar(int v) throws IOException {
		stream.writeChar(Character.reverseBytes((char)v));
	}

	public void writeChars(String s) throws IOException {
		for (char ch : s.toCharArray()) {
			stream.writeChar(Character.reverseBytes(ch));
		}
	}

	public void writeDouble(double v) throws IOException {
		stream.writeLong(Long.reverseBytes(Double.doubleToRawLongBits(v)));
	}

	public void writeFloat(float v) throws IOException {
		stream.writeInt(Integer.reverseBytes(Float.floatToRawIntBits(v)));
	}

	public void writeInt(int v) throws IOException {
		stream.writeInt(Integer.reverseBytes(v));
	}

	public void writeLong(long v) throws IOException {
		stream.writeLong(Long.reverseBytes(v));
	}

	public void writeShort(int v) throws IOException {
		stream.writeShort(Short.reverseBytes((short)v));
	}

	public void writeUTF(String str) throws IOException {
		byte[] b = str.getBytes("UTF-8");
		stream.writeShort(Short.reverseBytes((short)b.length));
		stream.write(b);
	}
}
