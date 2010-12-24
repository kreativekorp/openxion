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
import java.math.*;
import java.util.*;

public class DataReader {
	private List<DataField> format;
	
	public DataReader(List<DataField> format) {
		this.format = format;
	}
	
	public List<Object> unpack(byte[] b) throws IOException {
		return unpack(new ByteArrayInputStream(b), b.length);
	}
	
	public List<Object> unpack(File f) throws IOException {
		return unpack(new FileInputStream(f), f.length());
	}
	
	public List<Object> unpack(InputStream in, long length) throws IOException {
		return unpack(new BitInputStream(in), length);
	}
	
	public List<Object> unpack(BitInputStream in, long length) throws IOException {
		return unpack(format, new MapStack<String,Object>(), in, length, false).listed;
	}
	
	public Map<String,Object> unpackNamed(byte[] b) throws IOException {
		return unpackNamed(new ByteArrayInputStream(b), b.length);
	}
	
	public Map<String,Object> unpackNamed(File f) throws IOException {
		return unpackNamed(new FileInputStream(f), f.length());
	}
	
	public Map<String,Object> unpackNamed(InputStream in, long length) throws IOException {
		return unpackNamed(new BitInputStream(in), length);
	}
	
	public Map<String,Object> unpackNamed(BitInputStream in, long length) throws IOException {
		return unpack(format, new MapStack<String,Object>(), in, length, true).named;
	}
	
	private static class UnpackResult {
		public List<Object> listed = new ArrayList<Object>();
		public Map<String,Object> named = new HashMap<String,Object>();
	}
	
	private static UnpackResult unpack(List<DataField> format, MapStack<String,Object> map, BitInputStream in, long length, boolean named) throws IOException {
		UnpackResult res = new UnpackResult();
		map.push(res.named);
		for (DataField df : format) {
			if (df.type().returns()) {
				Object o = unpackFieldWithCount(df, map, in, length, named);
				res.listed.add(o);
				if (df.name() != null) {
					res.named.put(df.name(), o);
				}
			} else {
				unpackFieldWithCount(df, map, in, length, named);
			}
		}
		map.pop();
		return res;
	}
	
	private static Object unpackFieldWithCount(DataField df, MapStack<String,Object> map, BitInputStream in, long length, boolean named) throws IOException {
		if (df.count() == null || df.type().usesCustomCount()) {
			return unpackFieldWithoutCount(df, map, in, length, named);
		} else {
			int count = df.count().evaluate(map, in, length);
			if (df.type().returns()) {
				List<Object> res = new ArrayList<Object>((count < 10) ? 10 : count);
				while (count-->0) {
					res.add(unpackFieldWithoutCount(df, map, in, length, named));
				}
				return res;
			} else {
				while (count-->0) {
					unpackFieldWithoutCount(df, map, in, length, named);
				}
				return null;
			}
		}
	}
	
	private static Object unpackFieldWithoutCount(DataField df, MapStack<String,Object> map, BitInputStream in, long length, boolean named) throws IOException {
		// here we all care about is type, size, endianness, and elaboration
		// (count is accounted for in the above method, and name is accounted for two methods above)
		switch (df.type()) {
		case BOOLEAN:
			return !in.readBits(df.size()).isEmpty();
		case ENUM:
			if (df.littleEndian()) {
				return ((Map<?,?>)df.elaboration()).get(in.readUnsignedIntegerLE(df.size()));
			} else {
				return ((Map<?,?>)df.elaboration()).get(in.readUnsignedInteger(df.size()));
			}
		case BITFIELD:
			BitSet bfv = df.littleEndian() ? in.readBitsLE(df.size()) : in.readBits(df.size());
			List<Object> bfl = new ArrayList<Object>();
			Map<?,?> bfm = (Map<?,?>)df.elaboration();
			for (int i = 0; i < df.size(); i++) {
				if (bfv.get(i)) {
					BigInteger bi = BigInteger.valueOf(i);
					if (bfm.containsKey(bi)) {
						bfl.add(bfm.get(bi));
					} else if (bfm.containsKey(i)) {
						bfl.add(bfm.get(i));
					}
				}
			}
			return bfl;
		case BINT:
			if (df.littleEndian()) {
				return in.readUnsignedIntegerLE(df.size()).toString(2);
			} else {
				return in.readUnsignedInteger(df.size()).toString(2);
			}
		case OINT:
			if (df.littleEndian()) {
				return in.readUnsignedIntegerLE(df.size()).toString(8);
			} else {
				return in.readUnsignedInteger(df.size()).toString(8);
			}
		case HINT:
			if (df.littleEndian()) {
				return in.readUnsignedIntegerLE(df.size()).toString(16);
			} else {
				return in.readUnsignedInteger(df.size()).toString(16);
			}
		case UINT:
			if (df.littleEndian()) {
				return in.readUnsignedIntegerLE(df.size());
			} else {
				return in.readUnsignedInteger(df.size());
			}
		case SINT:
			if (df.littleEndian()) {
				return in.readIntegerLE(df.size());
			} else {
				return in.readInteger(df.size());
			}
		case UFIXED:
			if (df.littleEndian()) {
				return new BigDecimal(in.readUnsignedIntegerLE(df.size()), MathContext.DECIMAL128)
					.divide(BigDecimal.valueOf(2).pow(df.size()/2), MathContext.DECIMAL128);
			} else {
				return new BigDecimal(in.readUnsignedInteger(df.size()), MathContext.DECIMAL128)
					.divide(BigDecimal.valueOf(2).pow(df.size()/2), MathContext.DECIMAL128);
			}
		case SFIXED:
			if (df.littleEndian()) {
				return new BigDecimal(in.readIntegerLE(df.size()), MathContext.DECIMAL128)
					.divide(BigDecimal.valueOf(2).pow(df.size()/2), MathContext.DECIMAL128);
			} else {
				return new BigDecimal(in.readInteger(df.size()), MathContext.DECIMAL128)
					.divide(BigDecimal.valueOf(2).pow(df.size()/2), MathContext.DECIMAL128);
			}
		case FLOAT:
			int[] fpfmt = (int[])df.elaboration();
			if (df.littleEndian()) {
				return in.readFloatLE(fpfmt[0], fpfmt[1], fpfmt[2], fpfmt[3], MathContext.DECIMAL128);
			} else {
				return in.readFloat(fpfmt[0], fpfmt[1], fpfmt[2], fpfmt[3], MathContext.DECIMAL128);
			}
		case COMPLEX:
			int[] fpfmt1 = (int[])df.elaboration();
			if (df.littleEndian()) {
				Number r = in.readFloatLE(fpfmt1[0], fpfmt1[1], fpfmt1[2], fpfmt1[3], MathContext.DECIMAL128);
				Number i = in.readFloatLE(fpfmt1[0], fpfmt1[1], fpfmt1[2], fpfmt1[3], MathContext.DECIMAL128);
				return new Number[]{r,i};
			} else {
				Number r = in.readFloat(fpfmt1[0], fpfmt1[1], fpfmt1[2], fpfmt1[3], MathContext.DECIMAL128);
				Number i = in.readFloat(fpfmt1[0], fpfmt1[1], fpfmt1[2], fpfmt1[3], MathContext.DECIMAL128);
				return new Number[]{r,i};
			}
		case CHAR:
			if ((df.size() & 7) != 0) throw new IOException("Character values must be of a byte-multiple width");
			int chwidth = (df.size() >> 3);
			byte[] chb = new byte[chwidth];
			in.readFully(chb);
			if (df.littleEndian()) {
				for (int i = 0, j = chb.length-1; i < chb.length/2; i++, j--) {
					byte k = chb[i];
					chb[i] = chb[j];
					chb[j] = k;
				}
			}
			return new String(chb, df.elaboration().toString());
		case PSTRING:
			int pstrwidth = (df.littleEndian() ? in.readUnsignedIntegerLE(df.size()) : in.readUnsignedInteger(df.size())).intValue();
			byte[] pstrb = new byte[pstrwidth];
			in.readFully(pstrb);
			return new String(pstrb, df.elaboration().toString());
		case CSTRING:
			if ((df.size() & 7) != 0) throw new IOException("C-string values must be of a byte-multiple width");
			int cstrwidth = (df.size() >> 3);
			byte[] cstrb = new byte[cstrwidth];
			in.readFully(cstrb);
			ByteArrayOutputStream cstrout = new ByteArrayOutputStream();
			while (true) {
				boolean end = true;
				for (byte b : cstrb) {
					if (b != 0) end = false;
				}
				if (end) break;
				cstrout.write(cstrb[0]);
				for (int i = 1; i < cstrb.length; i++) {
					cstrb[i-1] = cstrb[i];
				}
				cstrb[cstrb.length-1] = in.readByte();
			}
			return new String(cstrout.toByteArray(), df.elaboration().toString());
		case DATE:
			DateFormat datefmt = (DateFormat)df.elaboration();
			if (df.littleEndian()) {
				return datefmt.longToCalendar(in.readIntegerLE(df.size()).longValue());
			} else {
				return datefmt.longToCalendar(in.readInteger(df.size()).longValue());
			}
		case COLOR:
			ColorFormat colorfmt = (ColorFormat)df.elaboration();
			BigInteger colorval = df.littleEndian() ? in.readUnsignedIntegerLE(df.size()) : in.readUnsignedInteger(df.size());
			Number[] colorvals = new Number[colorfmt.channelCount()];
			int colorshift = 0;
			for (int i = colorfmt.channelCount()-1; i >= 0; i--) {
				colorvals[i] = colorval.shiftRight(colorshift).and(BigInteger.ONE.shiftLeft(colorfmt.channelWidth(i)).subtract(BigInteger.ONE));
				colorshift += colorfmt.channelWidth(i);
			}
			return colorfmt.toRGBAFloatArray(colorfmt.toFloatArray(colorvals));
		case FILLER:
			in.skipBits(df.size());
			return null;
		case MAGIC:
			BigInteger magicMask = BigInteger.ONE.shiftLeft(df.size()).subtract(BigInteger.ONE);
			BigInteger magicTarget = ((BigInteger)df.elaboration()).and(magicMask);
			BigInteger magicBullet = df.littleEndian() ? in.readUnsignedIntegerLE(df.size()) : in.readUnsignedInteger(df.size());
			if (magicBullet.compareTo(magicTarget) != 0) {
				throw new IOException("Magic numbers do not match");
			}
			return null;
		case ALIGN:
			while (!in.atBitBoundary(df.size())) {
				in.skipBit();
			}
			return null;
		case BINARY:
			if (df.count() != null) {
				int len = df.count().evaluate(map, in, length);
				byte[] b = new byte[len];
				in.readFully(b);
				return b;
			}
			return new byte[0];
		case STRUCT:
			@SuppressWarnings("unchecked")
			List<DataField> format = (List<DataField>)df.elaboration();
			UnpackResult ur = unpack(format, map, in, length, named);
			return (named ? ur.named : ur.listed);
		case OFFSET:
			if (df.count() != null) {
				int offset = df.count().evaluate(map, in, length);
				in.reset();
				in.skipBytes(offset);
			}
			return null;
		default:
			throw new RuntimeException("Unknown data type: " + df.type().toString());
		}
	}
}
