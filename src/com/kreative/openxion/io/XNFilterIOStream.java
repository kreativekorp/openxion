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
 * @since OpenXION 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.io;

import java.io.*;

/**
 * An XNFilterIOStream contains some other XNIOStream, which it uses
 * as its basic source of and sink for data, possibly transforming
 * the data along the way or providing additional functionality.
 * The class XNFilterIOStream itself simply implements all methods
 * of XNIOStream by passing all requests to the contained XNIOStream.
 * Subclasses of XNFilterIOStream may further override some of these
 * methods and may also provide additional methods or fields.
 * @since OpenXION 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XNFilterIOStream implements XNIOStream {
	protected XNIOStream stream;
	
	public XNFilterIOStream(XNIOStream stream) {
		this.stream = stream;
	}
	
	public void close() throws IOException {
		stream.close();
	}

	public long getFilePointer() throws IOException {
		return stream.getFilePointer();
	}

	public long length() throws IOException {
		return stream.length();
	}
	
	public int lookahead() throws IOException {
		return stream.lookahead();
	}

	public int read() throws IOException {
		return stream.read();
	}

	public int read(byte[] b) throws IOException {
		return stream.read(b);
	}

	public int read(byte[] b, int off, int len) throws IOException {
		return stream.read(b, off, len);
	}

	public boolean readBoolean() throws IOException {
		return stream.readBoolean();
	}

	public byte readByte() throws IOException {
		return stream.readByte();
	}

	public char readChar() throws IOException {
		return stream.readChar();
	}

	public double readDouble() throws IOException {
		return stream.readDouble();
	}

	public float readFloat() throws IOException {
		return stream.readFloat();
	}

	public void readFully(byte[] b) throws IOException, EOFException {
		stream.readFully(b);
	}

	public void readFully(byte[] b, int off, int len) throws IOException, EOFException {
		stream.readFully(b, off, len);
	}

	public int readInt() throws IOException {
		return stream.readInt();
	}

	public String readLine() throws IOException {
		return stream.readLine();
	}

	public long readLong() throws IOException {
		return stream.readLong();
	}

	public short readShort() throws IOException {
		return stream.readShort();
	}

	public String readUTF() throws IOException {
		return stream.readUTF();
	}

	public int readUnsignedByte() throws IOException {
		return stream.readUnsignedByte();
	}

	public int readUnsignedShort() throws IOException {
		return stream.readUnsignedShort();
	}

	public void seek(long pos) throws IOException {
		stream.seek(pos);
	}

	public void setLength(long newLength) throws IOException {
		stream.setLength(newLength);
	}

	public int skipBytes(int n) throws IOException {
		return stream.skipBytes(n);
	}

	public void write(byte[] b) throws IOException {
		stream.write(b);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		stream.write(b, off, len);
	}

	public void write(int b) throws IOException {
		stream.write(b);
	}

	public void writeBoolean(boolean v) throws IOException {
		stream.writeBoolean(v);
	}

	public void writeByte(int v) throws IOException {
		stream.writeByte(v);
	}

	public void writeBytes(String s) throws IOException {
		stream.writeBytes(s);
	}

	public void writeChar(int v) throws IOException {
		stream.writeChar(v);
	}

	public void writeChars(String s) throws IOException {
		stream.writeChars(s);
	}

	public void writeDouble(double v) throws IOException {
		stream.writeDouble(v);
	}

	public void writeFloat(float v) throws IOException {
		stream.writeFloat(v);
	}

	public void writeInt(int v) throws IOException {
		stream.writeInt(v);
	}

	public void writeLong(long v) throws IOException {
		stream.writeLong(v);
	}

	public void writeShort(int v) throws IOException {
		stream.writeShort(v);
	}

	public void writeUTF(String str) throws IOException {
		stream.writeUTF(str);
	}
}
