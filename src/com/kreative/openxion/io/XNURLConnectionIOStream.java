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
import java.net.URLConnection;

/**
 * XNURLConnectionIOStream creates an XNIOStream that communicates with a URLConnection.
 * @since OpenXION 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XNURLConnectionIOStream implements XNIOStream {
	private URLConnection conn;
	private DataInputStream in;
	private DataOutputStream out;
	
	public XNURLConnectionIOStream(URLConnection conn) {
		this.conn = conn;
		this.in = null;
		this.out = null;
	}

	public void close() throws IOException {
		if (in != null) in.close();
		if (out != null) out.close();
	}
	
	public DataInputStream in() throws IOException {
		if (in == null) {
			in = new DataInputStream(conn.getInputStream());
		}
		return in;
	}
	
	public DataOutputStream out() throws IOException {
		if (out == null) {
			out = new DataOutputStream(conn.getOutputStream());
		}
		return out;
	}

	public long getFilePointer() throws IOException {
		throw new IOException("URLs do not support the at keyword");
	}

	public long length() throws IOException {
		return conn.getContentLength();
	}
	
	public int lookahead() throws IOException {
		if (in().markSupported()) {
			in().mark(16);
			int la = in().read();
			in().reset();
			return la;
		} else {
			return -1;
		}
	}

	public int read() throws IOException {
		return in().read();
	}

	public int read(byte[] b) throws IOException {
		return in().read(b);
	}

	public int read(byte[] b, int off, int len) throws IOException {
		return in().read(b, off, len);
	}

	public boolean readBoolean() throws IOException {
		return in().readBoolean();
	}

	public byte readByte() throws IOException {
		return in().readByte();
	}

	public char readChar() throws IOException {
		return in().readChar();
	}

	public double readDouble() throws IOException {
		return in().readDouble();
	}

	public float readFloat() throws IOException {
		return in().readFloat();
	}

	public void readFully(byte[] b) throws IOException, EOFException {
		in().readFully(b);
	}

	public void readFully(byte[] b, int off, int len) throws IOException, EOFException {
		in().readFully(b, off, len);
	}

	public int readInt() throws IOException {
		return in().readInt();
	}

	@SuppressWarnings("deprecation")
	public String readLine() throws IOException {
		return in().readLine();
	}

	public long readLong() throws IOException {
		return in().readLong();
	}

	public short readShort() throws IOException {
		return in().readShort();
	}

	public String readUTF() throws IOException {
		return in().readUTF();
	}

	public int readUnsignedByte() throws IOException {
		return in().readUnsignedByte();
	}

	public int readUnsignedShort() throws IOException {
		return in().readUnsignedShort();
	}

	public void seek(long pos) throws IOException {
		throw new IOException("URLs do not support the at keyword");
	}

	public void setLength(long newLength) throws IOException {
		throw new IOException("URLs do not support truncation");
	}

	public int skipBytes(int n) throws IOException {
		return in().skipBytes(n);
	}

	public void write(byte[] b) throws IOException {
		out().write(b);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		out().write(b, off, len);
	}

	public void write(int b) throws IOException {
		out().write(b);
	}

	public void writeBoolean(boolean v) throws IOException {
		out().writeBoolean(v);
	}

	public void writeByte(int v) throws IOException {
		out().writeByte(v);
	}

	public void writeBytes(String s) throws IOException {
		out().writeBytes(s);
	}

	public void writeChar(int v) throws IOException {
		out().writeChar(v);
	}

	public void writeChars(String s) throws IOException {
		out().writeChars(s);
	}

	public void writeDouble(double v) throws IOException {
		out().writeDouble(v);
	}

	public void writeFloat(float v) throws IOException {
		out().writeFloat(v);
	}

	public void writeInt(int v) throws IOException {
		out().writeInt(v);
	}

	public void writeLong(long v) throws IOException {
		out().writeLong(v);
	}

	public void writeShort(int v) throws IOException {
		out().writeShort(v);
	}

	public void writeUTF(String str) throws IOException {
		out().writeUTF(str);
	}
}
