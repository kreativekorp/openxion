/*
 * Copyright &copy; 2010-2011 Rebecca G. Bettencourt / Kreative Software
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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class DataFormatParser {
	// Change this number to adjust how long tokens can be.
	private static final int LOOKAHEAD_LIMIT = 65536;
	
	private static final List<DataField> POINT = new Vector<DataField>();
	private static final List<DataField> RECT = new Vector<DataField>();
	private static final List<DataField> COLOR = new Vector<DataField>();
	static {
		POINT.add(new DataField(DataType.SINT, 16, false, null, null, "y", "Y coordinate"));
		POINT.add(new DataField(DataType.SINT, 16, false, null, null, "x", "X coordinate"));
		RECT.add(new DataField(DataType.SINT, 16, false, null, null, "top", "top Y coordinate"));
		RECT.add(new DataField(DataType.SINT, 16, false, null, null, "left", "left X coordinate"));
		RECT.add(new DataField(DataType.SINT, 16, false, null, null, "bottom", "bottom Y coordinate"));
		RECT.add(new DataField(DataType.SINT, 16, false, null, null, "right", "right X coordinate"));
		COLOR.add(new DataField(DataType.UINT, 16, false, null, null, "red", "red channel intensity"));
		COLOR.add(new DataField(DataType.UINT, 16, false, null, null, "green", "green channel intensity"));
		COLOR.add(new DataField(DataType.UINT, 16, false, null, null, "blue", "blue channel intensity"));
	}
	
	private static final Object[][] TYPES = {
		{ "character", DataType.CHAR },
		{ "rectangle", DataType.STRUCT, 0, false, RECT },
		{ "rgbcolour", DataType.STRUCT, 0, false, COLOR },
		{ "bitfield", DataType.BITFIELD },
		{ "datetime", DataType.DATE },
		{ "rgbcolor", DataType.STRUCT, 0, false, COLOR },
		{ "boolean", DataType.BOOLEAN },
		{ "complex", DataType.COMPLEX },
		{ "pstring", DataType.PSTRING },
		{ "cstring", DataType.CSTRING },
		{ "eightcc", DataType.CHAR, 64 },
		{ "wstring", DataType.PSTRING, 16 },
		{ "lstring", DataType.PSTRING, 32 },
		{ "wcharbe", DataType.CHAR, 16, false, "UTF-16BE" },
		{ "wcharle", DataType.CHAR, 16, true, "UTF-16BE" },
		{ "ufixed", DataType.UFIXED },
		{ "sfixed", DataType.SFIXED },
		{ "filler", DataType.FILLER },
		{ "binary", DataType.BINARY },
		{ "struct", DataType.STRUCT },
		{ "offset", DataType.OFFSET },
		{ "string", DataType.CSTRING },
		{ "colour", DataType.COLOR },
		{ "bshort", DataType.BINT, 16 },
		{ "oshort", DataType.OINT, 16 },
		{ "hshort", DataType.HINT, 16 },
		{ "ushort", DataType.UINT, 16 },
		{ "sshort", DataType.SINT, 16 },
		{ "single", DataType.FLOAT, 32 },
		{ "double", DataType.FLOAT, 64 },
		{ "fourcc", DataType.CHAR, 32 },
		{ "ostype", DataType.CHAR, 32, false, "MACROMAN" },
		{ "symbol", DataType.CHAR, 64, false, "ISO-8859-1" },
		{ "float", DataType.FLOAT },
		{ "color", DataType.COLOR },
		{ "magic", DataType.MAGIC },
		{ "align", DataType.ALIGN },
		{ "fixed", DataType.SFIXED },
		{ "bbyte", DataType.BINT, 8 },
		{ "obyte", DataType.OINT, 8 },
		{ "hbyte", DataType.HINT, 8 },
		{ "ubyte", DataType.UINT, 8 },
		{ "sbyte", DataType.SINT, 8 },
		{ "short", DataType.SINT, 16 },
		{ "blong", DataType.BINT, 64 },
		{ "olong", DataType.OINT, 64 },
		{ "hlong", DataType.HINT, 64 },
		{ "ulong", DataType.UINT, 64 },
		{ "slong", DataType.SINT, 64 },
		{ "onecc", DataType.CHAR, 8 },
		{ "twocc", DataType.CHAR, 16 },
		{ "wchar", DataType.CHAR, 16, false, "UTF-16BE" },
		{ "point", DataType.STRUCT, 0, false, POINT },
		{ "enum", DataType.ENUM },
		{ "bint", DataType.BINT },
		{ "oint", DataType.OINT },
		{ "hint", DataType.HINT },
		{ "uint", DataType.UINT },
		{ "sint", DataType.SINT },
		{ "char", DataType.CHAR },
		{ "date", DataType.DATE },
		{ "bool", DataType.BOOLEAN },
		{ "time", DataType.DATE },
		{ "blob", DataType.BINARY },
		{ "byte", DataType.SINT, 8 },
		{ "long", DataType.SINT, 64 },
		{ "half", DataType.FLOAT, 16 },
		{ "real", DataType.FLOAT, 32 },
		{ "quad", DataType.FLOAT, 128 },
		{ "rect", DataType.STRUCT, 0, false, RECT },
		{ "int", DataType.SINT },
		{ "occ", DataType.CHAR, 8 },
		{ "tcc", DataType.CHAR, 16 },
		{ "fcc", DataType.CHAR, 32 },
		{ "ecc", DataType.CHAR, 64 },
	};
	
	private Reader reader;
	
	public DataFormatParser(Reader r) {
		this.reader = r;
	}
	
	public boolean isShortForm() throws IOException {
		parseWhitespace();
		reader.mark(LOOKAHEAD_LIMIT);
		int numchars = 0;
		while (Character.isLetter(reader.read())) {
			numchars++;
		}
		reader.reset();
		return (numchars < 2);
	}
	
	public List<DataField> parseAuto() throws IOException {
		if (isShortForm()) {
			return parseShortForm();
		} else {
			return parseLongForm();
		}
	}
	
	public List<DataField> parseLongForm() throws IOException {
		List<DataField> format = new Vector<DataField>();
		while (!atEnd()) {
			DataType datatype;
			int size;
			boolean littleEndian;
			Object elaboration;
			DFExpression count;
			String name;
			String description;
			
			Object[] dt = parseDataType();
			datatype = (DataType)dt[1];
			if (dt.length > 2) {
				size = ((Number)dt[2]).intValue();
			} else if (datatype.usesSize()) {
				parseWhitespace();
				size = parseFieldSize(datatype.defaultSize());
			} else {
				size = 0;
			}
			if (dt.length > 3) {
				littleEndian = ((Boolean)dt[3]).booleanValue();
			} else if (datatype.usesEndianness()) {
				parseWhitespace();
				littleEndian = parseEndianness();
			} else {
				littleEndian = false;
			}
			if (dt.length > 4) {
				elaboration = dt[4];
			} else if (datatype.usesElaboration()) {
				parseWhitespace();
				elaboration = parseElaboration(size, datatype.elaborationType());
			} else {
				elaboration = null;
			}
			parseWhitespace();
			count = parseItemCount();
			parseWhitespace();
			name = parseFieldName();
			parseWhitespace();
			description = parseFieldDescription();
			parseWhitespace();
			parseTerminator();
			
			format.add(new DataField(datatype, size, littleEndian, elaboration, count, name, description));
		}
		return format;
	}
	
	public List<DataField> parseShortForm() throws IOException {
		List<DataField> format = new Vector<DataField>();
		while (!atEnd()) {
			DataType datatype;
			int size;
			boolean littleEndian;
			Object elaboration;
			DFExpression count;
			String name;
			String description;
			
			Object[] dt = parseShortDataType();
			datatype = (DataType)dt[0];
			if (datatype.usesSize()) {
				parseWhitespace();
				size = parseFieldSize(datatype.defaultSize());
			} else {
				size = 0;
			}
			if (datatype.usesEndianness()) {
				littleEndian = ((Boolean)dt[1]).booleanValue();
			} else {
				littleEndian = false;
			}
			if (datatype.usesElaboration()) {
				parseWhitespace();
				elaboration = parseElaboration(size, datatype.elaborationType());
			} else {
				elaboration = null;
			}
			parseWhitespace();
			count = parseItemCount();
			parseWhitespace();
			name = parseShortFieldName();
			parseWhitespace();
			description = parseShortFieldDescription();
			
			format.add(new DataField(datatype, size, littleEndian, elaboration, count, name, description));
		}
		return format;
	}
	
	public static String toLongString(List<DataField> format) {
		StringBuffer s = new StringBuffer();
		for (DataField df : format) {
			DataType dt = df.type();
			s.append(dt.toString());
			if (dt.usesSize()) s.append(df.size());
			if (dt.usesEndianness()) s.append(endiannessToString(df.littleEndian()));
			if (dt.usesElaboration() && df.elaboration() != null) {
				s.append(" ");
				s.append(elaborationToString(dt.elaborationType(), df.elaboration()));
			}
			if (df.count() != null) {
				s.append(" ");
				s.append(itemCountToString(df.count()));
			}
			if (df.name() != null) {
				s.append(" ");
				s.append(fieldNameToString(df.name()));
			}
			if (df.description() != null) {
				s.append(" ");
				s.append(fieldDescriptionToString(df.description()));
			}
			s.append(";\n");
		}
		if (s.length() > 0 && s.charAt(s.length()-1) == '\n') {
			s.deleteCharAt(s.length()-1);
		}
		return s.toString();
	}
	
	public static String toShortString(List<DataField> format) {
		StringBuffer s = new StringBuffer();
		for (DataField df : format) {
			DataType dt = df.type();
			if (dt.usesEndianness()) {
				if (df.littleEndian()) {
					s.append(dt.toShortString().toLowerCase());
				} else {
					s.append(dt.toShortString().toUpperCase());
				}
			} else {
				s.append(dt.toShortString());
			}
			if (dt.usesSize()) s.append(df.size());
			if (dt.usesElaboration() && df.elaboration() != null) s.append(elaborationToShortString(dt.elaborationType(), df.elaboration()));
			if (df.count() != null) s.append(itemCountToString(df.count()));
			if (df.name() != null) s.append(fieldNameToShortString(df.name()));
			if (df.description() != null) s.append(fieldDescriptionToShortString(df.description()));
		}
		return s.toString();
	}
	
	private boolean atEnd() throws IOException {
		reader.mark(LOOKAHEAD_LIMIT);
		while (true) {
			int ch = reader.read();
			if (ch < 0) {
				return true;
			} else if (Character.isWhitespace(ch)) {
				reader.mark(LOOKAHEAD_LIMIT);
			} else {
				reader.reset();
				return false;
			}
		}
	}
	
	private void parseWhitespace() throws IOException {
		reader.mark(LOOKAHEAD_LIMIT);
		while (Character.isWhitespace(reader.read())) {
			reader.mark(LOOKAHEAD_LIMIT);
		}
		reader.reset();
	}
	
	private Object[] parseDataType() throws IOException {
		reader.mark(LOOKAHEAD_LIMIT);
		for (Object[] dt : TYPES) {
			char[] nc = new char[dt[0].toString().length()];
			reader.read(nc);
			if (new String(nc).equalsIgnoreCase(dt[0].toString())) {
				return dt;
			} else {
				reader.reset();
			}
		}
		throw new RuntimeException("Parse error: expected data type");
	}
	
	private Object[] parseShortDataType() throws IOException {
		reader.mark(LOOKAHEAD_LIMIT);
		int ch = reader.read();
		DataType dt = DataType.fromChar((char)ch);
		if (dt == null) {
			reader.reset();
			throw new RuntimeException("Parse error: expected data type");
		} else {
			return new Object[]{ dt, Character.isLowerCase(ch) };
		}
	}
	
	private int parseFieldSize(int def) throws IOException {
		reader.mark(LOOKAHEAD_LIMIT);
		int numchars = 0;
		while (Character.isDigit(reader.read())) {
			numchars++;
		}
		reader.reset();
		if (numchars == 0) {
			return def;
		}
		int size = 0;
		while (numchars-->0) {
			size *= 10;
			size += Character.digit(reader.read(), 10);
		}
		return size;
	}
	
	private boolean parseEndianness() throws IOException {
		reader.mark(LOOKAHEAD_LIMIT);
		int char1 = reader.read();
		int char2 = reader.read();
		if ((char1 == 'b' || char1 == 'B') && (char2 == 'e' || char2 == 'E')) return false;
		if ((char1 == 'l' || char1 == 'L') && (char2 == 'e' || char2 == 'E')) return true;
		reader.reset();
		return false;
	}
	
	private static String endiannessToString(boolean littleEndian) {
		return littleEndian ? "le" : "be";
	}
	
	private Object parseElaboration(int size, ElaborationType elabType) throws IOException {
		reader.mark(LOOKAHEAD_LIMIT);
		if (reader.read() == '{') {
			int level = 0;
			StringBuffer e = new StringBuffer();
			while (true) {
				int ch = reader.read();
				if (ch < 0) {
					break;
				} else if (ch == '{') {
					e.append(Character.toChars(ch));
					level++;
				} else if (ch == '}') {
					if (level == 0) break;
					e.append(Character.toChars(ch));
					level--;
				} else {
					e.append(Character.toChars(ch));
				}
			}
			String es = e.toString();
			switch (elabType) {
			case TEXT_ENCODING: return es.trim().toUpperCase();
			case DATE_FORMAT: return parseDateFormat(es);
			case COLOR_FORMAT: return new ColorFormat(size, es);
			case FP_FORMAT: return parseFPFormat(es);
			case VALUE: return parseInt(es);
			case KV_SIGNED: return parseIntStringMap(-1, es);
			case KV_UNSIGNED: return parseIntStringMap(size, es);
			case STRUCT: return new DataFormatParser(new StringReader(es)).parseAuto();
			default: return es;
			}
		} else {
			reader.reset();
			switch (elabType) {
			case TEXT_ENCODING: return "ISO-8859-1";
			case DATE_FORMAT: return new DateFormat(DateFormat.UNIX);
			case COLOR_FORMAT: return new ColorFormat(size, "ARGB");
			case FP_FORMAT:
				return new int[]{
					FPUtilities.optimalSignWidth(size),
					FPUtilities.optimalExponentWidth(size),
					FPUtilities.optimalMantissaWidth(size),
					FPUtilities.optimalBias(FPUtilities.optimalExponentWidth(size))
				};
			case VALUE: return BigInteger.ZERO;
			case KV_SIGNED: return new HashMap<BigInteger,String>();
			case KV_UNSIGNED: return new HashMap<BigInteger,String>();
			case STRUCT: return new Vector<DataType>();
			default: return "";
			}
		}
	}
	
	private static String elaborationToString(ElaborationType elabType, Object elab) {
		StringBuffer s = new StringBuffer();
		s.append("{");
		switch (elabType) {
		case TEXT_ENCODING: s.append(elab.toString().toUpperCase()); break;
		case DATE_FORMAT: s.append(dateFormatToString((DateFormat)elab)); break;
		case COLOR_FORMAT: s.append(((ColorFormat)elab).toString()); break;
		case FP_FORMAT: s.append(fpFormatToString((int[])elab)); break;
		case VALUE: s.append(elab.toString()); break;
		case KV_SIGNED: case KV_UNSIGNED:
			Map<?,?> m = (Map<?,?>)elab;
			s.append("\n\t");
			s.append(mapToString(m).trim().replace("\n", "\n\t"));
			s.append("\n");
			break;
		case STRUCT:
			@SuppressWarnings("unchecked")
			List<DataField> format = (List<DataField>)elab;
			s.append("\n\t");
			s.append(toLongString(format).trim().replace("\n", "\n\t"));
			s.append("\n");
			break;
		default: s.append(elab.toString()); break;
		}
		s.append("}");
		return s.toString();
	}
	
	private static String elaborationToShortString(ElaborationType elabType, Object elab) {
		StringBuffer s = new StringBuffer();
		s.append("{");
		switch (elabType) {
		case TEXT_ENCODING: s.append(elab.toString().toUpperCase()); break;
		case DATE_FORMAT: s.append(dateFormatToString((DateFormat)elab)); break;
		case COLOR_FORMAT: s.append(((ColorFormat)elab).toString()); break;
		case FP_FORMAT: s.append(fpFormatToString((int[])elab)); break;
		case VALUE: s.append(elab.toString()); break;
		case KV_SIGNED: case KV_UNSIGNED:
			Map<?,?> m = (Map<?,?>)elab;
			s.append(mapToShortString(m));
			break;
		case STRUCT:
			@SuppressWarnings("unchecked")
			List<DataField> format = (List<DataField>)elab;
			s.append(toShortString(format));
			break;
		default: s.append(elab.toString()); break;
		}
		s.append("}");
		return s.toString();
	}
	
	private DFExpression parseItemCount() throws IOException {
		reader.mark(LOOKAHEAD_LIMIT);
		if (reader.read() == '[') {
			int level = 0;
			StringBuffer c = new StringBuffer();
			while (true) {
				int ch = reader.read();
				if (ch < 0) {
					break;
				} else if (ch == '[') {
					c.append(Character.toChars(ch));
					level++;
				} else if (ch == ']') {
					if (level == 0) break;
					c.append(Character.toChars(ch));
					level--;
				} else {
					c.append(Character.toChars(ch));
				}
			}
			String cs = c.toString().trim();
			if (cs.length() == 0) return null;
			else return new DFExpressionParser(new DFExpressionLexer(new StringReader(cs))).parse();
		} else {
			reader.reset();
			return null;
		}
	}
	
	private static String itemCountToString(DFExpression expr) {
		return "[" + expr.toString() + "]";
	}
	
	private String parseFieldName() throws IOException {
		reader.mark(LOOKAHEAD_LIMIT);
		int firstChar = reader.read();
		if (firstChar == '`') {
			StringBuffer s = new StringBuffer();
			while (true) {
				int nextChar = reader.read();
				if (nextChar < 0 || nextChar == '`') break;
				else s.append(Character.toChars(nextChar));
			}
			return s.toString();
		} else if (firstChar == '_' || Character.isLetter(firstChar)) {
			reader.mark(LOOKAHEAD_LIMIT);
			int numChars = 0;
			while (true) {
				int nextChar = reader.read();
				if (nextChar < 0 || !(
						Character.isDigit(nextChar)
						|| Character.isLetter(nextChar)
						|| (nextChar == '_')
				)) break;
				numChars++;
			}
			reader.reset();
			StringBuffer s = new StringBuffer();
			s.append(Character.toChars(firstChar));
			while (numChars-->0) {
				s.append(Character.toChars(reader.read()));
			}
			return s.toString();
		} else {
			reader.reset();
			return null;
		}
	}
	
	private String parseShortFieldName() throws IOException {
		reader.mark(LOOKAHEAD_LIMIT);
		int firstChar = reader.read();
		if (firstChar == '`') {
			StringBuffer s = new StringBuffer();
			while (true) {
				int nextChar = reader.read();
				if (nextChar < 0 || nextChar == '`') break;
				else s.append(Character.toChars(nextChar));
			}
			return s.toString();
		} else {
			reader.reset();
			return null;
		}
	}
	
	private static String fieldNameToString(String s) {
		if (s.matches("^[A-Za-z_][A-Za-z0-9_]*$")) return s;
		else return "`" + s.replace("`", "") + "`";
	}
	
	private static String fieldNameToShortString(String s) {
		return "`" + s.replace("`", "") + "`";
	}
	
	private String parseFieldDescription() throws IOException {
		reader.mark(LOOKAHEAD_LIMIT);
		int firstChar = reader.read();
		if (firstChar == '"' || firstChar == '\'') {
			StringBuffer s = new StringBuffer();
			while (true) {
				int nextChar = reader.read();
				if (nextChar < 0 || nextChar == firstChar) break;
				else s.append(Character.toChars(nextChar));
			}
			return s.toString();
		} else if (firstChar < 0 || firstChar == ';') {
			reader.reset();
			return null;
		} else {
			reader.mark(LOOKAHEAD_LIMIT);
			int numChars = 0;
			while (true) {
				int nextChar = reader.read();
				if (nextChar < 0 || nextChar == ';') break;
				numChars++;
			}
			reader.reset();
			StringBuffer s = new StringBuffer();
			s.append(Character.toChars(firstChar));
			while (numChars-->0) {
				s.append(Character.toChars(reader.read()));
			}
			String ds = s.toString().trim();
			if (ds.length() == 0) return null;
			else return ds;
		}
	}
	
	private String parseShortFieldDescription() throws IOException {
		reader.mark(LOOKAHEAD_LIMIT);
		int firstChar = reader.read();
		if (firstChar == '"' || firstChar == '\'') {
			StringBuffer s = new StringBuffer();
			while (true) {
				int nextChar = reader.read();
				if (nextChar < 0 || nextChar == firstChar) break;
				else s.append(Character.toChars(nextChar));
			}
			return s.toString();
		} else {
			reader.reset();
			return null;
		}
	}
	
	private static String fieldDescriptionToString(String s) {
		if (!s.contains("\"")) {
			return "\"" + s + "\"";
		} else if (!s.contains("'")) {
			return "'" + s + "'";
		} else if (!s.contains(";")) {
			return s;
		} else {
			return "\"" + s.replace("\"", "") + "\"";
		}
	}
	
	private static String fieldDescriptionToShortString(String s) {
		if (!s.contains("\"")) {
			return "\"" + s + "\"";
		} else if (!s.contains("'")) {
			return "'" + s + "'";
		} else {
			return "\"" + s.replace("\"", "") + "\"";
		}
	}
	
	private void parseTerminator() throws IOException {
		reader.mark(LOOKAHEAD_LIMIT);
		int ch = reader.read();
		if (!(ch < 0 || ch == ';')) {
			reader.reset();
			throw new RuntimeException("Parse error: expected terminator");
		}
	}
	
	private DateFormat parseDateFormat(String s) {
		String[] ss = s.trim().split("\\s*[.,:;]\\s*");
		switch (ss.length) {
		case 7:
			return new DateFormat(
					Integer.parseInt(ss[0]), Calendar.JANUARY+Integer.parseInt(ss[1])-1, Integer.parseInt(ss[2]), // Y M D
					Integer.parseInt(ss[3]), Integer.parseInt(ss[4]), Integer.parseInt(ss[5]), // H M S
					Integer.parseInt(ss[6]) // scale
			);
		case 6:
			return new DateFormat(
					Integer.parseInt(ss[0]), Calendar.JANUARY+Integer.parseInt(ss[1])-1, Integer.parseInt(ss[2]), // Y M D
					Integer.parseInt(ss[3]), Integer.parseInt(ss[4]), Integer.parseInt(ss[5]) // H M S
			);
		case 4:
			return new DateFormat(
					Integer.parseInt(ss[0]), Calendar.JANUARY+Integer.parseInt(ss[1])-1, Integer.parseInt(ss[2]), // Y M D
					Integer.parseInt(ss[3]) // scale
			);
		case 3:
			return new DateFormat(
					Integer.parseInt(ss[0]), Calendar.JANUARY+Integer.parseInt(ss[1])-1, Integer.parseInt(ss[2]) // Y M D
			);
		case 2: return new DateFormat(Integer.parseInt(ss[0]), Integer.parseInt(ss[1])); // Y scale
		case 1: return new DateFormat(Integer.parseInt(ss[0])); // Y
		default: throw new RuntimeException("Invalid date format");
		}
	}
	
	private static String dateFormatToString(DateFormat df) {
		Calendar epoch = df.getEpoch();
		int scale = df.getScale();
		StringBuffer s = new StringBuffer();
		s.append(epoch.get(Calendar.YEAR));
		s.append(",");
		s.append(epoch.get(Calendar.MONTH) - Calendar.JANUARY + 1);
		s.append(",");
		s.append(epoch.get(Calendar.DAY_OF_MONTH));
		s.append(",");
		s.append(epoch.get(Calendar.HOUR_OF_DAY));
		s.append(",");
		s.append(epoch.get(Calendar.MINUTE));
		s.append(",");
		s.append(epoch.get(Calendar.SECOND));
		s.append(",");
		s.append(scale);
		return s.toString();
	}
	
	private int[] parseFPFormat(String s) {
		String[] ss = s.trim().split("\\s*[.,:;]\\s*");
		switch (ss.length) {
		case 4:
			return new int[] {
					Integer.parseInt(ss[0]), Integer.parseInt(ss[1]), Integer.parseInt(ss[2]), // s e m
					Integer.parseInt(ss[3]) // bias
			};
		case 3:
			return new int[] {
					Integer.parseInt(ss[0]), Integer.parseInt(ss[1]), Integer.parseInt(ss[2]), // s e m
					FPUtilities.optimalBias(Integer.parseInt(ss[1])) // bias
			};
		default:
			throw new RuntimeException("Invalid FP format");
		}
	}
	
	private static String fpFormatToString(int[] i) {
		StringBuffer s = new StringBuffer();
		s.append(i[0]);
		s.append(".");
		s.append(i[1]);
		s.append(".");
		s.append(i[2]);
		s.append(".");
		s.append(i[3]);
		return s.toString();
	}
	
	private BigInteger parseInt(String s) {
		s = s.trim();
		if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
			BigInteger r = BigInteger.ZERO;
			for (char ch : s.substring(1, s.length()-1).toCharArray()) {
				r = r.shiftLeft(8);
				r = r.or(BigInteger.valueOf((long)ch & 0xFFL));
			}
			return r;
		}
		if (s.startsWith("0x") || s.startsWith("0X") || s.startsWith("U+") || s.startsWith("U-") || s.startsWith("u+") || s.startsWith("u-")) {
			return new BigInteger(s.substring(2).trim(), 16);
		}
		if (s.startsWith("0o") || s.startsWith("0O")) {
			return new BigInteger(s.substring(2).trim(), 8);
		}
		if (s.startsWith("0b") || s.startsWith("0B")) {
			return new BigInteger(s.substring(2).trim(), 2);
		}
		if (s.startsWith("$")) {
			return new BigInteger(s.substring(1).trim(), 16);
		}
		if (s.startsWith("0") && s.length() > 1) {
			return new BigInteger(s.substring(1).trim(), 8);
		}
		return new BigInteger(s);
	}
	
	private Map<BigInteger, String> parseIntStringMap(int width, String s) {
		BigInteger km = (width > 0) ? BigInteger.ONE.shiftLeft(width).subtract(BigInteger.ONE) : null;
		Map<BigInteger, String> kvm = new HashMap<BigInteger, String>();
		String[] kvs = s.trim().split("\\s*[,;]\\s*");
		BigInteger klast = BigInteger.ONE.negate();
		for (String kvl : kvs) {
			String[] kvf = kvl.split("\\s*[.:=]\\s*", 2);
			if (kvf.length > 1) {
				klast = parseInt(kvf[0]);
				if (km != null) klast = klast.and(km);
				kvm.put(klast, kvf[1]);
			} else {
				klast = klast.add(BigInteger.ONE);
				if (km != null) klast = klast.and(km);
				kvm.put(klast, kvf[0]);
			}
		}
		return kvm;
	}
	
	private static String mapToString(Map<?,?> m) {
		TreeMap<Comparable<?>, Object> tm = new TreeMap<Comparable<?>, Object>();
		for (Map.Entry<?,?> e : m.entrySet()) {
			if (e.getKey() instanceof Comparable) {
				tm.put((Comparable<?>)e.getKey(), e.getValue());
			} else {
				tm = null;
				break;
			}
		}
		if (tm != null) m = tm;
		
		StringBuffer s = new StringBuffer();
		for (Map.Entry<?,?> e : m.entrySet()) {
			s.append(e.getKey().toString());
			s.append(" = ");
			s.append(e.getValue().toString());
			s.append(";\n");
		}
		if (s.length() > 0 && s.charAt(s.length()-1) == '\n') {
			s.deleteCharAt(s.length()-1);
		}
		return s.toString();
	}
	
	private static String mapToShortString(Map<?,?> m) {
		TreeMap<Comparable<?>, Object> tm = new TreeMap<Comparable<?>, Object>();
		for (Map.Entry<?,?> e : m.entrySet()) {
			if (e.getKey() instanceof Comparable) {
				tm.put((Comparable<?>)e.getKey(), e.getValue());
			} else {
				tm = null;
				break;
			}
		}
		if (tm != null) m = tm;
		
		StringBuffer s = new StringBuffer();
		for (Map.Entry<?,?> e : m.entrySet()) {
			s.append(e.getKey().toString());
			s.append("=");
			s.append(e.getValue().toString());
			s.append(";");
		}
		if (s.length() > 0 && s.charAt(s.length()-1) == ';') {
			s.deleteCharAt(s.length()-1);
		}
		return s.toString();
	}
}
