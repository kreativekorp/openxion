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
import java.util.Vector;

public class DFExpressionLexer {
	// Change this number to adjust how long tokens can be.
	private static final int LOOKAHEAD_LIMIT = 65536;
	
	private Reader reader;
	private Vector<String> buffer;
	private String lastToken;
	
	public DFExpressionLexer(Reader r) {
		this.reader = r;
		this.buffer = new Vector<String>();
		this.lastToken = null;
	}
	
	private String internalGetNextToken() throws IOException {
		int firstChar = reader.read();
		while (Character.isWhitespace(firstChar)) {
			firstChar = reader.read();
		}
		if (firstChar < 0) {
			return null;
		} else if (firstChar == '"' || firstChar == '\'' || firstChar == '`') {
			StringBuffer s = new StringBuffer();
			s.append(Character.toChars(firstChar));
			while (true) {
				int nextChar = reader.read();
				if (nextChar < 0) break;
				s.append(Character.toChars(nextChar));
				if (nextChar == firstChar) break;
			}
			return s.toString();
		} else if (Character.isDigit(firstChar) || Character.isLetter(firstChar) || firstChar == '_') {
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
			StringBuffer s;
			String ss;
			reader.mark(LOOKAHEAD_LIMIT);
			int secondChar = reader.read();
			int thirdChar = reader.read();
			s = new StringBuffer();
			s.append(Character.toChars(firstChar));
			if (secondChar >= 0) s.append(Character.toChars(secondChar));
			if (thirdChar >= 0) s.append(Character.toChars(thirdChar));
			ss = s.toString();
			if (ss.equals(">>>") || ss.equals("<=>")) {
				return ss;
			}
			s = new StringBuffer();
			s.append(Character.toChars(firstChar));
			if (secondChar >= 0) s.append(Character.toChars(secondChar));
			ss = s.toString();
			if (
					ss.equals("!!") || ss.equals("<<") || ss.equals(">>")
					|| ss.equals("<=") || ss.equals(">=")
					|| ss.equals("==") || ss.equals("!=") || ss.equals("<>")
					|| ss.equals("&&") || ss.equals("^^") || ss.equals("||")
			) {
				reader.reset();
				reader.read();
				return ss;
			}
			s = new StringBuffer();
			s.append(Character.toChars(firstChar));
			ss = s.toString();
			reader.reset();
			return ss;
		}
	}
	
	public String lookToken(int i) throws IOException {
		if (i < 1) return lastToken;
		while (i > buffer.size()) {
			String token = internalGetNextToken();
			if (token == null) return null;
			else buffer.add(token);
		}
		return buffer.get(i-1);
	}
	
	public String getToken() throws IOException {
		if (buffer.isEmpty()) {
			return lastToken = internalGetNextToken();
		} else {
			return lastToken = buffer.remove(0);
		}
	}
}
