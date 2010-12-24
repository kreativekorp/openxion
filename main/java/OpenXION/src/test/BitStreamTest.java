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

package test;

import java.io.*;
import java.math.MathContext;
import java.util.Arrays;
import com.kreative.openxion.binpack.BitInputStream;
import com.kreative.openxion.binpack.BitOutputStream;

public class BitStreamTest {
	public static void main(String[] args) throws IOException {
		byte[] b = new byte[768];
		for (int i = 0; i < 768; i++) {
			b[i] = (byte)i;
		}
		ByteArrayInputStream inOneEar;
		ByteArrayOutputStream outTheOther;
		BitInputStream in;
		BitOutputStream out;
		byte[] d;
		boolean f;
		
		System.out.print("bit... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		while (in.availableBit()) out.writeBit(in.readBit());
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("bits... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		while (in.availableBit()) out.writeBits(3, in.readBits(3));
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("bitsLE... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		while (in.availableBit()) out.writeBitsLE(24, in.readBitsLE(24));
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("r/w... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		while (in.available() > 0) out.write(in.read());
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("byte... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		while (in.available() > 0) out.writeByte(in.readByte());
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("ubyte... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		while (in.available() > 0) out.writeByte(in.readUnsignedByte());
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("short... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		while (in.available() > 0) out.writeShort(in.readShort());
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("shortLE... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		while (in.available() > 0) out.writeShortLE(in.readShortLE());
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("ushort... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		while (in.available() > 0) out.writeShort(in.readUnsignedShort());
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("ushortLE... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		while (in.available() > 0) out.writeShortLE(in.readUnsignedShortLE());
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("char... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		while (in.available() > 0) out.writeChar(in.readChar());
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("charLE... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		while (in.available() > 0) out.writeCharLE(in.readCharLE());
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("int... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		while (in.available() > 0) out.writeInt(in.readInt());
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("intLE... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		while (in.available() > 0) out.writeIntLE(in.readIntLE());
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("long... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		while (in.available() > 0) out.writeLong(in.readLong());
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("longLE... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		while (in.available() > 0) out.writeLongLE(in.readLongLE());
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("r/w2... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		out.writeBits(3, in.readBits(3));
		while (in.available() > 0) out.write(in.read());
		out.writeBits(5, in.readBits(5));
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("byte2... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		out.writeBits(3, in.readBits(3));
		while (in.available() > 0) out.writeByte(in.readByte());
		out.writeBits(5, in.readBits(5));
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("ubyte2... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		out.writeBits(3, in.readBits(3));
		while (in.available() > 0) out.writeByte(in.readUnsignedByte());
		out.writeBits(5, in.readBits(5));
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("short2... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		out.writeBits(11, in.readBits(11));
		while (in.available() > 0) out.writeShort(in.readShort());
		out.writeBits(5, in.readBits(5));
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("ushort2... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		out.writeBits(11, in.readBits(11));
		while (in.available() > 0) out.writeShort(in.readUnsignedShort());
		out.writeBits(5, in.readBits(5));
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("char2... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		out.writeBits(13, in.readBits(13));
		while (in.available() > 0) out.writeChar(in.readChar());
		out.writeBits(3, in.readBits(3));
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("int2... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		out.writeBits(30, in.readBits(30));
		while (in.available() > 0) out.writeInt(in.readInt());
		out.writeBits(2, in.readBits(2));
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("long2... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		out.writeBits(57, in.readBits(57));
		while (in.available() > 0) out.writeLong(in.readLong());
		out.writeBits(7, in.readBits(7));
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("fully... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		byte[] tmp1 = new byte[768];
		in.readFully(tmp1);
		out.write(tmp1);
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("fully2... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		out.writeBits(7, in.readBits(7));
		byte[] tmp2 = new byte[767];
		in.readFully(tmp2);
		out.write(tmp2);
		out.writeBits(1, in.readBits(1));
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("r/wa... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		byte[] tmp3 = new byte[768];
		in.read(tmp3);
		out.write(tmp3);
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("r/wa2... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		out.writeBits(7, in.readBits(7));
		byte[] tmp4 = new byte[767];
		in.read(tmp4);
		out.write(tmp4);
		out.writeBits(1, in.readBits(1));
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("bigint... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		while (in.availableBit()) out.writeInteger(3, in.readInteger(3));
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("bigint2... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		while (in.availableBit()) out.writeInteger(6, in.readInteger(6));
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("bigintLE... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		while (in.availableBit()) out.writeIntegerLE(24, in.readIntegerLE(24));
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("ubigint... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		while (in.availableBit()) out.writeInteger(3, in.readUnsignedInteger(3));
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("ubigint2... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		while (in.availableBit()) out.writeInteger(6, in.readUnsignedInteger(6));
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("ubigintLE... ");
		inOneEar = new ByteArrayInputStream(b);
		outTheOther = new ByteArrayOutputStream();
		in = new BitInputStream(inOneEar);
		out = new BitOutputStream(outTheOther);
		while (in.availableBit()) out.writeIntegerLE(24, in.readUnsignedIntegerLE(24));
		in.close();
		out.close();
		d = outTheOther.toByteArray();
		System.out.println((Arrays.equals(b,d) && (in.bitsRead() == b.length*8) && (out.bitsWritten() == b.length*8)) ? "PASSED" : "FAILED");
		
		System.out.print("readFloat... ");
		f = true;
		for (int i = Float.floatToRawIntBits(Float.POSITIVE_INFINITY); i >= 0; i -= 0x10000) {
			if ((i & 0x3FFFF) == 0) {
				String h = "00000000" + Integer.toHexString(i).toUpperCase();
				h = h.substring(h.length() - 8);
				System.out.print("\rreadFloat... " + h + "... ");
			}
			int j = i | Integer.MIN_VALUE;
			out = new BitOutputStream(outTheOther = new ByteArrayOutputStream());
			out.writeInt(i);
			out.writeInt(j);
			out.close();
			in = new BitInputStream(inOneEar = new ByteArrayInputStream(outTheOther.toByteArray()));
			float p = in.readFloat(32, MathContext.DECIMAL128).floatValue();
			float q = in.readFloat(32, MathContext.DECIMAL128).floatValue();
			in.close();
			if (Float.floatToRawIntBits(p) != i || Float.floatToRawIntBits(q) != j) {
				String h1 = "00000000" + Integer.toHexString(i).toUpperCase();
				h1 = h1.substring(h1.length() - 8);
				String h2 = "00000000" + Integer.toHexString(Float.floatToRawIntBits(p)).toUpperCase();
				h2 = h2.substring(h2.length() - 8);
				System.out.println("FAILED: " + h1 + " -> " + h2);
				f = false;
				break;
			}
		}
		if (f) System.out.println("PASSED");
		
		System.out.print("readFloatLE... ");
		f = true;
		for (int i = Float.floatToRawIntBits(Float.POSITIVE_INFINITY); i >= 0; i -= 0x10000) {
			if ((i & 0x3FFFF) == 0) {
				String h = "00000000" + Integer.toHexString(i).toUpperCase();
				h = h.substring(h.length() - 8);
				System.out.print("\rreadFloatLE... " + h + "... ");
			}
			int j = i | Integer.MIN_VALUE;
			out = new BitOutputStream(outTheOther = new ByteArrayOutputStream());
			out.writeIntLE(i);
			out.writeIntLE(j);
			out.close();
			in = new BitInputStream(inOneEar = new ByteArrayInputStream(outTheOther.toByteArray()));
			float p = in.readFloatLE(32, MathContext.DECIMAL128).floatValue();
			float q = in.readFloatLE(32, MathContext.DECIMAL128).floatValue();
			in.close();
			if (Float.floatToRawIntBits(p) != i || Float.floatToRawIntBits(q) != j) {
				String h1 = "00000000" + Integer.toHexString(i).toUpperCase();
				h1 = h1.substring(h1.length() - 8);
				String h2 = "00000000" + Integer.toHexString(Float.floatToRawIntBits(p)).toUpperCase();
				h2 = h2.substring(h2.length() - 8);
				System.out.println("FAILED: " + h1 + " -> " + h2);
				f = false;
				break;
			}
		}
		if (f) System.out.println("PASSED");
		
		System.out.print("writeFloat... ");
		f = true;
		for (int i = Float.floatToRawIntBits(Float.POSITIVE_INFINITY); i >= 0; i -= 0x10000) {
			if ((i & 0x3FFFF) == 0) {
				String h = "00000000" + Integer.toHexString(i).toUpperCase();
				h = h.substring(h.length() - 8);
				System.out.print("\rwriteFloat... " + h + "... ");
			}
			int j = i | Integer.MIN_VALUE;
			out = new BitOutputStream(outTheOther = new ByteArrayOutputStream());
			out.writeFloat(32, MathContext.DECIMAL128, Float.intBitsToFloat(i));
			out.writeFloat(32, MathContext.DECIMAL128, Float.intBitsToFloat(j));
			out.close();
			in = new BitInputStream(inOneEar = new ByteArrayInputStream(outTheOther.toByteArray()));
			int p = in.readInt();
			int q = in.readInt();
			in.close();
			if (p != i || q != j) {
				String h1 = "00000000" + Integer.toHexString(i).toUpperCase();
				h1 = h1.substring(h1.length() - 8);
				String h2 = "00000000" + Integer.toHexString(p).toUpperCase();
				h2 = h2.substring(h2.length() - 8);
				System.out.println("FAILED: " + h1 + " -> " + h2);
				f = false;
				break;
			}
		}
		if (f) System.out.println("PASSED");
		
		System.out.print("writeFloatLE... ");
		f = true;
		for (int i = Float.floatToRawIntBits(Float.POSITIVE_INFINITY); i >= 0; i -= 0x10000) {
			if ((i & 0x3FFFF) == 0) {
				String h = "00000000" + Integer.toHexString(i).toUpperCase();
				h = h.substring(h.length() - 8);
				System.out.print("\rwriteFloatLE... " + h + "... ");
			}
			int j = i | Integer.MIN_VALUE;
			out = new BitOutputStream(outTheOther = new ByteArrayOutputStream());
			out.writeFloatLE(32, MathContext.DECIMAL128, Float.intBitsToFloat(i));
			out.writeFloatLE(32, MathContext.DECIMAL128, Float.intBitsToFloat(j));
			out.close();
			in = new BitInputStream(inOneEar = new ByteArrayInputStream(outTheOther.toByteArray()));
			int p = in.readIntLE();
			int q = in.readIntLE();
			in.close();
			if (p != i || q != j) {
				String h1 = "00000000" + Integer.toHexString(i).toUpperCase();
				h1 = h1.substring(h1.length() - 8);
				String h2 = "00000000" + Integer.toHexString(p).toUpperCase();
				h2 = h2.substring(h2.length() - 8);
				System.out.println("FAILED: " + h1 + " -> " + h2);
				f = false;
				break;
			}
		}
		if (f) System.out.println("PASSED");
	}
}
