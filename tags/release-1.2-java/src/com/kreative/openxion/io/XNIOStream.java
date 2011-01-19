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
 * XNIOStream is a generalization of RandomAccessFile
 * that enables XNIOMethods to work irrespective of
 * the XNIOManager's underlying implementation.
 * @since OpenXION 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public interface XNIOStream extends DataInput, DataOutput, Closeable {
	public void		close() throws IOException;
	public long		getFilePointer() throws IOException;
	public long		length() throws IOException;
	public int      lookahead() throws IOException;
	
	public int		read() throws IOException;
	public int		read(byte[] b) throws IOException;
	public int		read(byte[] b, int off, int len) throws IOException;
	public boolean	readBoolean() throws IOException;
	public byte		readByte() throws IOException;
	public char		readChar() throws IOException;
	public double	readDouble() throws IOException;
	public float	readFloat() throws IOException;
	public void		readFully(byte[] b) throws IOException, EOFException;
	public void		readFully(byte[] b, int off, int len) throws IOException, EOFException;
	public int		readInt() throws IOException;
	public String	readLine() throws IOException;
	public long		readLong() throws IOException;
	public short	readShort() throws IOException;
	public int		readUnsignedByte() throws IOException;
	public int		readUnsignedShort() throws IOException;
	public String	readUTF() throws IOException;
	
	public void		seek(long pos) throws IOException;
	public void		setLength(long newLength) throws IOException;
	public int		skipBytes(int n) throws IOException;
	
	public void		write(byte[] b) throws IOException;
	public void		write(byte[] b, int off, int len) throws IOException;
	public void		write(int b) throws IOException;
	public void		writeBoolean(boolean v) throws IOException;
	public void		writeByte(int v) throws IOException;
	public void		writeBytes(String s) throws IOException;
	public void		writeChar(int v) throws IOException;
	public void		writeChars(String s) throws IOException;
	public void		writeDouble(double v) throws IOException;
	public void		writeFloat(float v) throws IOException;
	public void		writeInt(int v) throws IOException;
	public void		writeLong(long v) throws IOException;
	public void		writeShort(int v) throws IOException;
	public void		writeUTF(String str) throws IOException;
}
