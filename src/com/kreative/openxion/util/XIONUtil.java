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
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.util;

import java.io.*;
import java.text.*;
import java.util.*;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.XNLexer;
import com.kreative.openxion.XNParser;
import com.kreative.openxion.XNInterpreter;
import com.kreative.openxion.ast.XNModifier;
import com.kreative.openxion.ast.XNExpression;
import com.kreative.openxion.ast.XNVariantDescriptor;
import com.kreative.openxion.xom.XOMVariant;

/**
 * The XIONUtil utility class contains miscellaneous methods used
 * throughout OpenXION. These include: resolving negative indexes and
 * special ordinals, decoding escaped and quoted strings, creating
 * literal regular expressions, splitting and concatenating byte
 * arrays, storing the long user name, locating applications and
 * documents, and launching files.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XIONUtil {
	private XIONUtil() {}
	
	public static String normalizeVarName(String s) {
		return (s == null) ? "" : s.toLowerCase().trim().replaceAll(" +", " ");
	}
	
	public static XOMVariant parseDescriptor(XNContext ctx, String s) {
		try {
			XNLexer lexer = new XNLexer(new StringReader(s));
			XNParser parser = new XNParser(ctx, lexer);
			if (parser.lookListExpression(1, null)) {
				XNExpression expr = parser.getListExpression(null);
				if (expr instanceof XNVariantDescriptor) {
					if (parser.getToken().isEOF()) {
						return new XNInterpreter(ctx).evaluateExpression(expr);
					} else {
						return null;
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	public static final int INDEX_ANY = Integer.MIN_VALUE;
	public static final int INDEX_MIDDLE = Integer.MIN_VALUE + 1;
	public static final int INDEX_PREVIOUS = Integer.MIN_VALUE + 2;
	public static final int INDEX_CURRENT = Integer.MIN_VALUE + 3;
	public static final int INDEX_NEXT = Integer.MIN_VALUE + 4;
	public static final int INDEX_RECENT = Integer.MIN_VALUE + 5;
	
	private static final Random indexRand = new Random();
	
	public static int[] index(int min, int max, int current, int recent, int start, int end) {
		int randomIndex = ((max-min+1) > 0) ? (min + indexRand.nextInt(max-min+1)) : min;
		int middleIndex = min + (max-min+1)/2;
		if (start < 0) {
			switch (start) {
			case INDEX_ANY: start = randomIndex; break;
			case INDEX_MIDDLE: start = middleIndex; break;
			case INDEX_PREVIOUS: start = current-1; if (start < min) start = max; break;
			case INDEX_CURRENT: start = current; break;
			case INDEX_NEXT: start = current+1; if (start > max) start = min; break;
			case INDEX_RECENT: start = recent; break;
			default: start += max+1; break;
			}
		}
		if (end < 0) {
			switch (end) {
			case INDEX_ANY: end = randomIndex; break;
			case INDEX_MIDDLE: end = middleIndex; break;
			case INDEX_PREVIOUS: end = current-1; if (end < min) end = max; break;
			case INDEX_CURRENT: end = current; break;
			case INDEX_NEXT: end = current+1; if (end > max) end = min; break;
			case INDEX_RECENT: end = recent; break;
			default: end += max+1; break;
			}
		}
		return new int[]{start,end};
	}
	
	public static int[] index(int min, int max, int start, int end) {
		int randomIndex = ((max-min+1) > 0) ? (min + indexRand.nextInt(max-min+1)) : min;
		int middleIndex = min + (max-min+1)/2;
		if (start < 0) {
			switch (start) {
			case INDEX_ANY: start = randomIndex; break;
			case INDEX_MIDDLE: start = middleIndex; break;
			default: start += max+1; break;
			}
		}
		if (end < 0) {
			switch (end) {
			case INDEX_ANY: end = randomIndex; break;
			case INDEX_MIDDLE: end = middleIndex; break;
			default: end += max+1; break;
			}
		}
		return new int[]{start,end};
	}
	
	public static Random getRandom() {
		return indexRand;
	}
	
	public static String quote(String s) {
		StringBuffer quoted = new StringBuffer(s.length()+s.length()/10+2);
		quoted.append('"');
		CharacterIterator ci = new StringCharacterIterator(s);
		for (char ch = ci.first(); ch != CharacterIterator.DONE; ch = ci.next()) {
			switch (ch) {
			case '\\': quoted.append("\\\\"); break;
			case '\"': quoted.append("\\\""); break;
			case '\u0007': quoted.append("\\a"); break;
			case '\b': quoted.append("\\b"); break;
			case '\t': quoted.append("\\t"); break;
			case '\n': quoted.append("\\n"); break;
			case '\u000B': quoted.append("\\v"); break;
			case '\f': quoted.append("\\f"); break;
			case '\r': quoted.append("\\r"); break;
			case '\u000E': quoted.append("\\o"); break;
			case '\u000F': quoted.append("\\i"); break;
			case '\u001A': quoted.append("\\z"); break;
			case '\u001B': quoted.append("\\e"); break;
			case '\u007F': quoted.append("\\d"); break;
			case '\u2028': quoted.append("\\u2028"); break;
			case '\u2029': quoted.append("\\u2029"); break;
			default:
				if (Character.getType(ch) == Character.CONTROL) {
					String h = "0000"+Integer.toHexString(ch).toUpperCase();
					quoted.append("\\u"+h.substring(h.length()-4));
				} else {
					quoted.append(ch);
				}
				break;
			}
		}
		quoted.append('"');
		return quoted.toString();
	}
	
	public static String unquote(String s, String encoding) {
		if (s.startsWith("\"") && s.endsWith("\"")) {
			s = s.substring(1, s.length()-1);
		}
		StringBuffer unquoted = new StringBuffer(s.length());
		CharacterIterator ci = new StringCharacterIterator(s);
		for (char ch = ci.first(); ch != CharacterIterator.DONE; ch = ci.next()) {
			if (ch == '\\') {
				ch = ci.next();
				switch (ch) {
				case '\\': unquoted.append("\\"); break;
				case '\'': unquoted.append("'"); break;
				case '"': unquoted.append("\""); break;
				case ' ': unquoted.append(" "); break;
				case '0': unquoted.append("\u0000"); break;
				case 'a': unquoted.append("\u0007"); break;
				case 'b': unquoted.append("\b"); break;
				case 'd': unquoted.append("\u007F"); break;
				case 'e': unquoted.append("\u001B"); break;
				case 'f': unquoted.append("\f"); break;
				case 'i': unquoted.append("\u000F"); break;
				case 'l': unquoted.append("\r\n"); break;
				case 'n': unquoted.append("\n"); break;
				case 'o': unquoted.append("\u000E"); break;
				case 'r': unquoted.append("\r"); break;
				case 't': unquoted.append("\t"); break;
				case 'v': unquoted.append("\u000B"); break;
				case 'z': unquoted.append("\u001A"); break;
				case 'C': unquoted.append("\uFFF0"); break;
				case 'R': unquoted.append("\uFFF1"); break;
				case 'P': unquoted.append("\uFFF2"); break;
				case 'S': unquoted.append("\uFFF3"); break;
				case 'E': unquoted.append("\uFFFF"); break;
				case 'x': {
					char a = ci.next();
					char b = ci.next();
					int av = (a >= '0' && a <= '9') ? (a - '0') : (a >= 'A' && a <= 'F') ? (a - 'A' + 10) : (a >= 'a' && a <= 'f') ? (a - 'a' + 10) : -1;
					int bv = (b >= '0' && b <= '9') ? (b - '0') : (b >= 'A' && b <= 'F') ? (b - 'A' + 10) : (b >= 'a' && b <= 'f') ? (b - 'a' + 10) : -1;
					if (av < 0 || bv < 0) {
						unquoted.append("\\x");
						if (a != CharacterIterator.DONE) unquoted.append(a);
						if (b != CharacterIterator.DONE) unquoted.append(b);
					} else try {
						byte[] bytes = new byte[]{(byte)((av << 4) | bv)};
						String string = new String(bytes, encoding);
						unquoted.append(string);
					} catch (UnsupportedEncodingException uee) {
						unquoted.append((char)((av << 4) | bv));
					}
				} break;
				case 'u': {
					char a = ci.next();
					char b = ci.next();
					char c = ci.next();
					char d = ci.next();
					int av = (a >= '0' && a <= '9') ? (a - '0') : (a >= 'A' && a <= 'F') ? (a - 'A' + 10) : (a >= 'a' && a <= 'f') ? (a - 'a' + 10) : -1;
					int bv = (b >= '0' && b <= '9') ? (b - '0') : (b >= 'A' && b <= 'F') ? (b - 'A' + 10) : (b >= 'a' && b <= 'f') ? (b - 'a' + 10) : -1;
					int cv = (c >= '0' && c <= '9') ? (c - '0') : (c >= 'A' && c <= 'F') ? (c - 'A' + 10) : (c >= 'a' && c <= 'f') ? (c - 'a' + 10) : -1;
					int dv = (d >= '0' && d <= '9') ? (d - '0') : (d >= 'A' && d <= 'F') ? (d - 'A' + 10) : (d >= 'a' && d <= 'f') ? (d - 'a' + 10) : -1;
					if (av < 0 || bv < 0 || cv < 0 || dv < 0) {
						unquoted.append("\\u");
						if (a != CharacterIterator.DONE) unquoted.append(a);
						if (b != CharacterIterator.DONE) unquoted.append(b);
						if (c != CharacterIterator.DONE) unquoted.append(c);
						if (d != CharacterIterator.DONE) unquoted.append(d);
					} else {
						unquoted.append((char)((av << 12) | (bv << 8) | (cv << 4) | dv));
					}
				} break;
				case 'w': {
					char a = ci.next();
					char b = ci.next();
					char c = ci.next();
					char d = ci.next();
					char e = ci.next();
					char f = ci.next();
					int av = (a >= '0' && a <= '9') ? (a - '0') : (a >= 'A' && a <= 'F') ? (a - 'A' + 10) : (a >= 'a' && a <= 'f') ? (a - 'a' + 10) : -1;
					int bv = (b >= '0' && b <= '9') ? (b - '0') : (b >= 'A' && b <= 'F') ? (b - 'A' + 10) : (b >= 'a' && b <= 'f') ? (b - 'a' + 10) : -1;
					int cv = (c >= '0' && c <= '9') ? (c - '0') : (c >= 'A' && c <= 'F') ? (c - 'A' + 10) : (c >= 'a' && c <= 'f') ? (c - 'a' + 10) : -1;
					int dv = (d >= '0' && d <= '9') ? (d - '0') : (d >= 'A' && d <= 'F') ? (d - 'A' + 10) : (d >= 'a' && d <= 'f') ? (d - 'a' + 10) : -1;
					int ev = (e >= '0' && e <= '9') ? (e - '0') : (e >= 'A' && e <= 'F') ? (e - 'A' + 10) : (e >= 'a' && e <= 'f') ? (e - 'a' + 10) : -1;
					int fv = (f >= '0' && f <= '9') ? (f - '0') : (f >= 'A' && f <= 'F') ? (f - 'A' + 10) : (f >= 'a' && f <= 'f') ? (f - 'a' + 10) : -1;
					if (av < 0 || bv < 0 || cv < 0 || dv < 0 || ev < 0 || fv < 0) {
						unquoted.append("\\w");
						if (a != CharacterIterator.DONE) unquoted.append(a);
						if (b != CharacterIterator.DONE) unquoted.append(b);
						if (c != CharacterIterator.DONE) unquoted.append(c);
						if (d != CharacterIterator.DONE) unquoted.append(d);
						if (e != CharacterIterator.DONE) unquoted.append(e);
						if (f != CharacterIterator.DONE) unquoted.append(f);
					} else {
						int cp = (av << 20) | (bv << 16) | (cv << 12) | (dv << 8) | (ev << 4) | fv;
						unquoted.append(Character.toChars(cp));
					}
				} break;
				case CharacterIterator.DONE: unquoted.append("\\"); break;
				default: unquoted.append("\\"+ch); break;
				}
			} else {
				unquoted.append(ch);
			}
		}
		return unquoted.toString();
	}
	
	private static final String REGEX_MATCH_CHARS = "\\.()[]{}*+?^$|";
	private static final String REGEX_REPLACE_CHARS = "\\$";
	
	public static String makeRegexForExactMatch(String exactMatchString) {
		StringBuffer s = new StringBuffer(exactMatchString.length()*2);
		CharacterIterator it = new StringCharacterIterator(exactMatchString);
		for (char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
			if (REGEX_MATCH_CHARS.contains(""+ch)) {
				s.append('\\');
			}
			s.append(ch);
		}
		return s.toString();
	}
	
	public static String makeRegexForExactReplace(String exactReplaceString) {
		StringBuffer s = new StringBuffer(exactReplaceString.length()*2);
		CharacterIterator it = new StringCharacterIterator(exactReplaceString);
		for (char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
			if (REGEX_REPLACE_CHARS.contains(""+ch)) {
				s.append('\\');
			}
			s.append(ch);
		}
		return s.toString();
	}
	
	public static byte[] binarySubstring(byte[] b, int start) {
		try {
			ByteArrayOutputStream s = new ByteArrayOutputStream();
			s.write(b, start, b.length-start);
			s.close();
			return s.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}
	
	public static byte[] binarySubstring(byte[] b, int start, int end) {
		try {
			ByteArrayOutputStream s = new ByteArrayOutputStream();
			s.write(b, start, end-start);
			s.close();
			return s.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}
	
	public static byte[] binaryConcat(byte[] a, byte[] b) {
		try {
			ByteArrayOutputStream s = new ByteArrayOutputStream();
			s.write(a);
			s.write(b);
			s.close();
			return s.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}
	
	public static byte[] binaryConcat(byte[] a, byte[] b, byte[] c) {
		try {
			ByteArrayOutputStream s = new ByteArrayOutputStream();
			s.write(a);
			s.write(b);
			s.write(c);
			s.close();
			return s.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}
	
	public static boolean binaryEndsWith(byte[] b, byte[] end) {
		if (b.length < end.length) return false;
		for (int i = b.length-end.length, j = 0; i < b.length && j < end.length; i++, j++) {
			if (b[i] != end[j]) return false;
		}
		return true;
	}
	
	private static final String[] LOOKUP_HEX = new String[] {
		"00","01","02","03","04","05","06","07","08","09","0A","0B","0C","0D","0E","0F",
		"10","11","12","13","14","15","16","17","18","19","1A","1B","1C","1D","1E","1F",
		"20","21","22","23","24","25","26","27","28","29","2A","2B","2C","2D","2E","2F",
		"30","31","32","33","34","35","36","37","38","39","3A","3B","3C","3D","3E","3F",
		"40","41","42","43","44","45","46","47","48","49","4A","4B","4C","4D","4E","4F",
		"50","51","52","53","54","55","56","57","58","59","5A","5B","5C","5D","5E","5F",
		"60","61","62","63","64","65","66","67","68","69","6A","6B","6C","6D","6E","6F",
		"70","71","72","73","74","75","76","77","78","79","7A","7B","7C","7D","7E","7F",
		"80","81","82","83","84","85","86","87","88","89","8A","8B","8C","8D","8E","8F",
		"90","91","92","93","94","95","96","97","98","99","9A","9B","9C","9D","9E","9F",
		"A0","A1","A2","A3","A4","A5","A6","A7","A8","A9","AA","AB","AC","AD","AE","AF",
		"B0","B1","B2","B3","B4","B5","B6","B7","B8","B9","BA","BB","BC","BD","BE","BF",
		"C0","C1","C2","C3","C4","C5","C6","C7","C8","C9","CA","CB","CC","CD","CE","CF",
		"D0","D1","D2","D3","D4","D5","D6","D7","D8","D9","DA","DB","DC","DD","DE","DF",
		"E0","E1","E2","E3","E4","E5","E6","E7","E8","E9","EA","EB","EC","ED","EE","EF",
		"F0","F1","F2","F3","F4","F5","F6","F7","F8","F9","FA","FB","FC","FD","FE","FF"
	};
	
	public static String binaryToString(byte[] theData) {
		if (theData == null) return "";
		StringBuffer theString = new StringBuffer(theData.length*2);
		for (byte b : theData) theString.append(LOOKUP_HEX[b & 0xFF]);
		return theString.toString();
	}
	
	public static String getUserName(XNContext ctx, XNModifier modifier) {
		if (modifier == XNModifier.SHORT || modifier == XNModifier.ABBREVIATED) {
			try {
				return System.getProperty("user.name");
			} catch (Exception e) {}
		}
		String v = ctx.getUsername();
		if (v != null) return v;
		try {
			return System.getProperty("user.name");
		} catch (Exception e) {}
		return "";
	}
	
	private static String osString = null;
	
	public static boolean isMacOS() {
		if (osString == null) {
			try {
				osString = System.getProperty("os.name").toUpperCase();
			} catch (Exception e) {
				osString = "";
			}
		}
		return osString.contains("MAC OS");
	}
	
	public static boolean isWindows() {
		if (osString == null) {
			try {
				osString = System.getProperty("os.name").toUpperCase();
			} catch (Exception e) {
				osString = "";
			}
		}
		return osString.contains("WINDOWS");
	}
	
	public static String getApplicationPaths(XNContext ctx) {
		String v = ctx.getApplicationPaths();
		if (v != null) return v;
		String lineEnding = ctx.getLineEnding();
		try {
			if (osString == null) {
				try {
					osString = System.getProperty("os.name").toUpperCase();
				} catch (Exception e) {
					osString = "";
				}
			}
			String userHome = System.getProperty("user.home");
			if (userHome.endsWith("/") || userHome.endsWith("\\")) {
				userHome = userHome.substring(0, userHome.length()-1);
			}
			if (osString.contains("MAC OS")) {
				return
						"/Applications/" + lineEnding +
						"/Applications/Utilities/" + lineEnding +
						"/Applications (Mac OS 9)/" + lineEnding +
						userHome + "/Applications/" + lineEnding +
						"/Developer/Applications/" + lineEnding +
						"/Developer/Applications/Utilities/" + lineEnding +
						"/bin/" + lineEnding +
						"/sbin/" + lineEnding +
						"/usr/bin/" + lineEnding +
						"/usr/sbin/" + lineEnding +
						"/usr/local/bin/" + lineEnding +
						"/usr/local/sbin/" + lineEnding +
						"/usr/shared/bin/" + lineEnding +
						"/usr/shared/sbin/" + lineEnding +
						"/opt/bin/" + lineEnding +
						"/opt/sbin/" + lineEnding +
						"/opt/local/bin/" + lineEnding +
						"/opt/local/sbin/" + lineEnding +
						"/opt/shared/bin/" + lineEnding +
						"/opt/shared/sbin/" + lineEnding
				;
			}
			else if (osString.contains("WINDOWS")) {
				return
						"C:\\Program Files\\" + lineEnding +
						"C:\\Windows\\" + lineEnding +
						"C:\\Windows\\System32\\" + lineEnding
				;
			}
			else {
				return
						"/bin/" + lineEnding +
						"/sbin/" + lineEnding +
						"/usr/bin/" + lineEnding +
						"/usr/sbin/" + lineEnding +
						"/usr/local/bin/" + lineEnding +
						"/usr/local/sbin/" + lineEnding +
						"/usr/shared/bin/" + lineEnding +
						"/usr/shared/sbin/" + lineEnding +
						"/opt/bin/" + lineEnding +
						"/opt/sbin/" + lineEnding +
						"/opt/local/bin/" + lineEnding +
						"/opt/local/sbin/" + lineEnding +
						"/opt/shared/bin/" + lineEnding +
						"/opt/shared/sbin/" + lineEnding
				;
			}
		} catch (Exception e) {}
		return "";
	}
	
	public static String getDocumentPaths(XNContext ctx) {
		String v = ctx.getDocumentPaths();
		if (v != null) return v;
		String lineEnding = ctx.getLineEnding();
		try {
			if (osString == null) {
				try {
					osString = System.getProperty("os.name").toUpperCase();
				} catch (Exception e) {
					osString = "";
				}
			}
			String userHome = System.getProperty("user.home");
			if (userHome.endsWith("/") || userHome.endsWith("\\")) {
				userHome = userHome.substring(0, userHome.length()-1);
			}
			if (osString.contains("MAC OS")) {
				return
						userHome + "/Desktop/" + lineEnding +
						userHome + "/Documents/" + lineEnding +
						userHome + "/Downloads/" + lineEnding +
						userHome + "/Pictures/" + lineEnding +
						userHome + "/Music/" + lineEnding +
						userHome + "/Movies/" + lineEnding +
						userHome + "/Public/" + lineEnding +
						userHome + "/Sites/" + lineEnding +
						userHome + "/" + lineEnding
				;
			}
			else if (osString.contains("WINDOWS")) {
				return
						userHome + "\\Desktop\\" + lineEnding +
						userHome + "\\My Documents\\" + lineEnding +
						userHome + "\\My Photos\\" + lineEnding +
						userHome + "\\My Music\\" + lineEnding +
						userHome + "\\My Videos\\" + lineEnding +
						userHome + "\\" + lineEnding
				;
			}
			else {
				return
						userHome + "/" + lineEnding
				;
			}
		} catch (Exception e) {}
		return "";
	}
	
	public static File locateApplication(XNContext ctx, String name, boolean ask) {
		try {
			if (name.contains(System.getProperty("file.separator"))) return new File(name);
		} catch (Exception e) {}
		String[] extensions;
		if (osString == null) {
			try {
				osString = System.getProperty("os.name").toUpperCase();
			} catch (Exception e) {
				osString = "";
			}
		}
		if (osString.contains("MAC OS")) extensions = new String[]{"",".app"};
		else if (osString.contains("WINDOWS")) extensions = new String[]{"",".exe",".com",".bat"};
		else extensions = new String[]{"",".sh"};
		String[] apaths = getApplicationPaths(ctx).split("(\r|\n|\u2028|\u2029)+");
		for (String path : apaths) {
			for (String ext : extensions) {
				File f = new File(path, name+ext);
				if (f != null && f.exists()) return f;
			}
		}
		if (ask) {
			return ctx.getUI().answerFile("Where is "+name+"?", new String[0], 0, 0);
		}
		return null;
	}
	
	public static File locateApplicationOrDocument(XNContext ctx, String name, boolean ask) {
		try {
			if (name.contains(System.getProperty("file.separator"))) return new File(name);
		} catch (Exception e) {}
		String[] extensions;
		if (osString == null) {
			try {
				osString = System.getProperty("os.name").toUpperCase();
			} catch (Exception e) {
				osString = "";
			}
		}
		if (osString.contains("MAC OS")) extensions = new String[]{"",".app"};
		else if (osString.contains("WINDOWS")) extensions = new String[]{"",".exe",".com",".bat"};
		else extensions = new String[]{"",".sh"};
		String[] apaths = getApplicationPaths(ctx).split("(\r|\n|\u2028|\u2029)+");
		for (String path : apaths) {
			for (String ext : extensions) {
				File f = new File(path, name+ext);
				if (f != null && f.exists()) return f;
			}
		}
		String[] dpaths = getDocumentPaths(ctx).split("(\r|\n|\u2028|\u2029)+");
		for (String path : dpaths) {
			File f = new File(path, name);
			if (f != null && f.exists()) return f;
		}
		if (ask) {
			return ctx.getUI().answerFile("Where is "+name+"?", new String[0], 0, 0);
		}
		return null;
	}
	
	public static File locateDocument(XNContext ctx, String name, boolean ask) {
		try {
			if (name.contains(System.getProperty("file.separator"))) return new File(name);
		} catch (Exception e) {}
		String[] dpaths = getDocumentPaths(ctx).split("(\r|\n|\u2028|\u2029)+");
		for (String path : dpaths) {
			File f = new File(path, name);
			if (f != null && f.exists()) return f;
		}
		if (ask) {
			return ctx.getUI().answerFile("Where is "+name+"?", new String[0], 0, 0);
		}
		return null;
	}
	
	public static void launch(File f) throws IOException {
		if (osString == null) {
			try {
				osString = System.getProperty("os.name").toUpperCase();
			} catch (Exception e) {
				osString = "";
			}
		}
		if (osString.contains("MAC OS")) {
			Runtime.getRuntime().exec(new String[] {"open", f.getAbsolutePath()});
		}
		else if (osString.contains("WINDOWS")) {
			Runtime.getRuntime().exec(new String[] {"cmd", "/c", "start", "\"X\"", f.getAbsolutePath()});
		}
		else {
			Runtime.getRuntime().exec(new String[] {f.getAbsolutePath()});
		}
	}
	
	public static void launch(File app, File doc) throws IOException {
		if (osString == null) {
			try {
				osString = System.getProperty("os.name").toUpperCase();
			} catch (Exception e) {
				osString = "";
			}
		}
		if (osString.contains("MAC OS")) {
			Runtime.getRuntime().exec(new String[] {"open", "-a", app.getAbsolutePath(), doc.getAbsolutePath()});
		}
		else if (osString.contains("WINDOWS")) {
			Runtime.getRuntime().exec(new String[] {"cmd", "/c", "start", "\"X\"", app.getAbsolutePath(), doc.getAbsolutePath()});
		}
		else {
			Runtime.getRuntime().exec(new String[] {app.getAbsolutePath(), doc.getAbsolutePath()});
		}
	}
	
	public static void unlaunch(File f) throws IOException {
		if (osString == null) {
			try {
				osString = System.getProperty("os.name").toUpperCase();
			} catch (Exception e) {
				osString = "";
			}
		}
		if (osString.contains("MAC OS")) {
			Runtime.getRuntime().exec(new String[] {"osascript", "-e", "tell application \""+f.getAbsolutePath()+"\" to quit"});
		}
		else {
			throw new IOException("Inter-process communication not supported");
		}
	}
	
	public static void unlaunch(File app, File doc) throws IOException {
		if (osString == null) {
			try {
				osString = System.getProperty("os.name").toUpperCase();
			} catch (Exception e) {
				osString = "";
			}
		}
		if (osString.contains("MAC OS")) {
			Runtime.getRuntime().exec(new String[] {"osascript", "-e", "tell application \""+app.getAbsolutePath()+"\" to close (every document whose path is \""+doc.getAbsolutePath()+"\")"});
		}
		else {
			throw new IOException("Inter-process communication not supported");
		}
	}
}
