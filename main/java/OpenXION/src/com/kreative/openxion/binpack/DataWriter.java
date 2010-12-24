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

public class DataWriter {
	private List<DataField> format;
	
	public DataWriter(List<DataField> format) {
		this.format = format;
	}
	
	public byte[] pack(List<?> l) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		pack(l, out);
		return out.toByteArray();
	}
	
	public void pack(List<?> l, File f) throws IOException {
		pack(l, new FileOutputStream(f));
	}
	
	public void pack(List<?> l, OutputStream out) throws IOException {
		pack(l, new BitOutputStream(out));
	}
	
	public void pack(List<?> l, BitOutputStream out) throws IOException {
		pack(format, l, new MapStack<String,Object>(), out);
	}
	
	public byte[] pack(Map<String,?> m) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		pack(m, out);
		return out.toByteArray();
	}
	
	public void pack(Map<String,?> m, File f) throws IOException {
		pack(m, new FileOutputStream(f));
	}
	
	public void pack(Map<String,?> m, OutputStream out) throws IOException {
		pack(m, new BitOutputStream(out));
	}
	
	public void pack(Map<String,?> m, BitOutputStream out) throws IOException {
		pack(format, m, new MapStack<String,Object>(), out);
	}
	
	private static void pack(List<DataField> format, List<?> l, MapStack<String,Object> map, BitOutputStream out) throws IOException {
		Map<String,Object> m = new HashMap<String,Object>();
		Iterator<?> li = l.iterator();
		for (DataField df : format) {
			if (df.type().returns()) {
				Object o = li.hasNext() ? li.next() : null;
				if (df.name() != null) {
					m.put(df.name(), o);
				}
			}
		}
		pack(format, l, m, map, out);
	}
	
	private static void pack(List<DataField> format, Map<String,?> m, MapStack<String,Object> map, BitOutputStream out) throws IOException {
		List<Object> l = new ArrayList<Object>();
		for (DataField df : format) {
			if (df.type().returns()) {
				if (df.name() != null && m.containsKey(df.name())) {
					l.add(m.get(df.name()));
				} else {
					l.add(null);
				}
			}
		}
		pack(format, l, m, map, out);
	}
	
	@SuppressWarnings("unchecked")
	private static void pack(List<DataField> format, List<?> l, Map<String,?> m, MapStack<String,Object> map, BitOutputStream out) throws IOException {
		map.push((Map<String,Object>)m);
		Iterator<?> li = l.iterator();
		for (DataField df : format) {
			if (df.type().returns()) {
				Object o = li.hasNext() ? li.next() : null;
				packFieldWithCount(df, o, map, out);
			} else {
				packFieldWithCount(df, null, map, out);
			}
		}
		map.pop();
	}
	
	private static void packFieldWithCount(DataField df, Object o, MapStack<String,Object> map, BitOutputStream out) throws IOException {
		if (df.count() == null || df.type().usesCustomCount()) {
			packFieldWithoutCount(df, o, map, out);
		} else {
			int count = df.count().evaluate(map, out);
			if (df.type().returns()) {
				Collection<?> c;
				if (o instanceof Collection) c = (Collection<?>)o;
				else { ArrayList<Object> cc = new ArrayList<Object>(); cc.add(o); c = cc; }
				Iterator<?> ci = c.iterator();
				while (count-->0) {
					Object oo = ci.hasNext() ? ci.next() : null;
					packFieldWithoutCount(df, oo, map, out);
				}
			} else {
				while (count-->0) {
					packFieldWithoutCount(df, null, map, out);
				}
			}
		}
	}
	
	private static void packFieldWithoutCount(DataField df, Object o, MapStack<String,Object> map, BitOutputStream out) throws IOException {
		// here we all care about is type, size, endianness, and elaboration
		// (count is accounted for in the above method, and name is accounted for two methods above)
		switch (df.type()) {
		case BOOLEAN:
			boolean bv = ((o instanceof Boolean) && ((Boolean)o).booleanValue());
			if (df.littleEndian()) out.writeIntegerLE(df.size(), bv ? BigInteger.ONE : BigInteger.ZERO);
			else out.writeInteger(df.size(), bv ? BigInteger.ONE : BigInteger.ZERO);
			break;
		case ENUM:
			String esv = (o == null ? "" : o.toString());
			for (Map.Entry<?,?> e : ((Map<?,?>)df.elaboration()).entrySet()) {
				if (e.getValue().toString().equalsIgnoreCase(esv)) {
					BigInteger ev = (BigInteger)e.getKey();
					if (df.littleEndian()) out.writeIntegerLE(df.size(), ev);
					else out.writeInteger(df.size(), ev);
					return;
				}
			}
			BigInteger eiv;
			if (o instanceof BigInteger) eiv = (BigInteger)o;
			else if (o instanceof BigDecimal) eiv = ((BigDecimal)o).toBigInteger();
			else if (o instanceof Number) eiv = BigInteger.valueOf(((Number)o).longValue());
			else if (o != null) eiv = new BigInteger(o.toString());
			else eiv = BigInteger.ZERO;
			if (df.littleEndian()) out.writeIntegerLE(df.size(), eiv);
			else out.writeInteger(df.size(), eiv);
			break;
		case BITFIELD:
			BitSet bfv = new BitSet();
			Collection<?> bfl;
			if (o instanceof Collection) bfl = ((Collection<?>)o);
			else { List<Object> l = new ArrayList<Object>(); l.add(o); bfl = l; }
			Map<?,?> bfm = (Map<?,?>)df.elaboration();
			for (int i = 0; i < df.size(); i++) {
				BigInteger bi = BigInteger.valueOf(i);
				if (bfm.containsKey(bi) && bfl.contains(bfm.get(bi))) {
					bfv.set(i);
				} else if (bfm.containsKey(i) && bfl.contains(bfm.get(i))) {
					bfv.set(i);
				}
			}
			if (df.littleEndian()) out.writeBitsLE(df.size(), bfv);
			else out.writeBits(df.size(), bfv);
			break;
		case BINT:
			BigInteger biv = (o == null) ? BigInteger.ZERO : new BigInteger(o.toString(), 2);
			if (df.littleEndian()) out.writeIntegerLE(df.size(), biv);
			else out.writeInteger(df.size(), biv);
			break;
		case OINT:
			BigInteger oiv = (o == null) ? BigInteger.ZERO : new BigInteger(o.toString(), 8);
			if (df.littleEndian()) out.writeIntegerLE(df.size(), oiv);
			else out.writeInteger(df.size(), oiv);
			break;
		case HINT:
			BigInteger hiv = (o == null) ? BigInteger.ZERO : new BigInteger(o.toString(), 16);
			if (df.littleEndian()) out.writeIntegerLE(df.size(), hiv);
			else out.writeInteger(df.size(), hiv);
			break;
		case UINT:
		case SINT:
			BigInteger uiv;
			if (o instanceof BigInteger) uiv = (BigInteger)o;
			else if (o instanceof BigDecimal) uiv = ((BigDecimal)o).toBigInteger();
			else if (o instanceof Number) uiv = BigInteger.valueOf(((Number)o).longValue());
			else if (o != null) uiv = new BigInteger(o.toString());
			else uiv = BigInteger.ZERO;
			if (df.littleEndian()) out.writeIntegerLE(df.size(), uiv);
			else out.writeInteger(df.size(), uiv);
			break;
		case UFIXED:
		case SFIXED:
			BigDecimal fxv;
			if (o instanceof BigDecimal) fxv = (BigDecimal)o;
			else if (o instanceof BigInteger) fxv = new BigDecimal((BigInteger)o);
			else if (o instanceof Number) fxv = BigDecimal.valueOf(((Number)o).doubleValue());
			else if (o != null) fxv = new BigDecimal(o.toString());
			else fxv = BigDecimal.ZERO;
			fxv = fxv.multiply(BigDecimal.valueOf(2).pow(df.size()/2), MathContext.DECIMAL128);
			if (df.littleEndian()) out.writeIntegerLE(df.size(), fxv.toBigInteger());
			else out.writeInteger(df.size(), fxv.toBigInteger());
			break;
		case FLOAT:
			Number flv;
			if (o instanceof Number) flv = (Number)o;
			else if (o != null) flv = new BigDecimal(o.toString());
			else flv = 0;
			int[] fpfmt = (int[])df.elaboration();
			if (df.littleEndian()) {
				out.writeFloatLE(fpfmt[0], fpfmt[1], fpfmt[2], fpfmt[3], MathContext.DECIMAL128, flv);
			} else {
				out.writeFloat(fpfmt[0], fpfmt[1], fpfmt[2], fpfmt[3], MathContext.DECIMAL128, flv);
			}
			break;
		case COMPLEX:
			Number crv, civ;
			if (o instanceof Number[]) {
				Number[] ccv = (Number[])o;
				crv = (ccv.length > 0) ? ccv[0] : 0;
				civ = (ccv.length > 1) ? ccv[1] : 0;
			}
			else if (o != null) {
				crv = new BigDecimal(o.toString());
				civ = 0;
			}
			else {
				crv = 0;
				civ = 0;
			}
			int[] fpfmt1 = (int[])df.elaboration();
			if (df.littleEndian()) {
				out.writeFloatLE(fpfmt1[0], fpfmt1[1], fpfmt1[2], fpfmt1[3], MathContext.DECIMAL128, crv);
				out.writeFloatLE(fpfmt1[0], fpfmt1[1], fpfmt1[2], fpfmt1[3], MathContext.DECIMAL128, civ);
			} else {
				out.writeFloat(fpfmt1[0], fpfmt1[1], fpfmt1[2], fpfmt1[3], MathContext.DECIMAL128, crv);
				out.writeFloat(fpfmt1[0], fpfmt1[1], fpfmt1[2], fpfmt1[3], MathContext.DECIMAL128, civ);
			}
			break;
		case CHAR:
			if ((df.size() & 7) != 0) throw new IOException("Character values must be of a byte-multiple width");
			int chwidth = (df.size() >> 3);
			byte[] chb1 = (o == null ? new byte[0] : o.toString().getBytes(df.elaboration().toString()));
			byte[] chb2 = new byte[chwidth];
			for (int i = 0; i < chb1.length && i < chb2.length; i++) chb2[i] = chb1[i];
			if (df.littleEndian()) {
				for (int i = 0, j = chb2.length-1; i < chb2.length/2; i++, j--) {
					byte k = chb2[i];
					chb2[i] = chb2[j];
					chb2[j] = k;
				}
			}
			out.write(chb2);
			break;
		case PSTRING:
			byte[] pstrb = (o == null ? new byte[0] : o.toString().getBytes(df.elaboration().toString()));
			if (df.littleEndian()) out.writeIntegerLE(df.size(), BigInteger.valueOf(pstrb.length));
			else out.writeInteger(df.size(), BigInteger.valueOf(pstrb.length));
			out.write(pstrb);
			break;
		case CSTRING:
			byte[] cstrb = (o == null ? new byte[0] : o.toString().getBytes(df.elaboration().toString()));
			out.write(cstrb);
			out.writeInteger(df.size(), BigInteger.ZERO);
			break;
		case DATE:
			if (o instanceof Calendar) {
				Calendar c = (Calendar)o;
				DateFormat datefmt = (DateFormat)df.elaboration();
				if (df.littleEndian()) {
					out.writeIntegerLE(df.size(), BigInteger.valueOf(datefmt.calendarToLong(c)));
				} else {
					out.writeInteger(df.size(), BigInteger.valueOf(datefmt.calendarToLong(c)));
				}
			} else if (o instanceof Date) {
				Calendar c = new GregorianCalendar();
				c.setTime((Date)o);
				DateFormat datefmt = (DateFormat)df.elaboration();
				if (df.littleEndian()) {
					out.writeIntegerLE(df.size(), BigInteger.valueOf(datefmt.calendarToLong(c)));
				} else {
					out.writeInteger(df.size(), BigInteger.valueOf(datefmt.calendarToLong(c)));
				}
			} else {
				out.writeInteger(df.size(), BigInteger.ZERO);
			}
			break;
		case COLOR:
			float[] color = (o instanceof float[]) ? (float[])o : new float[4];
			ColorFormat colorfmt = (ColorFormat)df.elaboration();
			BigInteger[] colorvals = colorfmt.toBigIntArray(colorfmt.fromRGBAFloatArray(color));
			BigInteger colorval = BigInteger.ZERO;
			for (int i = 0; i < colorfmt.channelCount(); i++) {
				colorval = colorval.shiftLeft(colorfmt.channelWidth(i)).or(colorvals[i]);
			}
			if (df.littleEndian()) out.writeIntegerLE(df.size(), colorval);
			else out.writeInteger(df.size(), colorval);
			break;
		case FILLER:
			out.writeBits(df.size(), new BitSet());
			break;
		case MAGIC:
			BigInteger magic = ((BigInteger)df.elaboration());
			if (df.littleEndian()) out.writeIntegerLE(df.size(), magic);
			else out.writeInteger(df.size(), magic);
			break;
		case ALIGN:
			while (!out.atBitBoundary(df.size())) {
				out.writeBit(false);
			}
			break;
		case BINARY:
			if (df.count() != null) {
				int len = df.count().evaluate(map, out);
				byte[] b = (o instanceof byte[]) ? (byte[])o : new byte[0];
				for (int i = 0; i < b.length && i < len; i++) {
					out.writeByte(b[i]);
				}
				for (int i = b.length; i < len; i++) {
					out.writeByte(0);
				}
			} else if (o instanceof byte[]) {
				byte[] b = (byte[])o;
				out.write(b);
			}
			break;
		case STRUCT:
			@SuppressWarnings("unchecked")
			List<DataField> format = (List<DataField>)df.elaboration();
			if (o instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String,?> m = (Map<String,?>)o;
				pack(format, m, map, out);
			} else if (o instanceof List) {
				List<?> l = (List<?>)o;
				pack(format, l, map, out);
			} else {
				pack(format, new ArrayList<Object>(), new HashMap<String,Object>(), map, out);
			}
			break;
		case OFFSET:
			if (df.count() != null) {
				int offset = df.count().evaluate(map, out);
				long bitoffset = (long)offset << 3L;
				if (bitoffset < out.bitsWritten()) {
					throw new IOException("Can't seek backward in a pack operation");
				}
				while (out.bitsWritten() < bitoffset) {
					out.writeBit(false);
				}
			}
			break;
		default:
			throw new RuntimeException("Unknown data type: " + df.type().toString());
		}
	}
}
