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
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion;

import java.io.*;
import java.util.Vector;

/**
 * XNLexer is the main class responsible for tokenizing XION scripts
 * (turning letters into words).
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XNLexer {
	// Change this number to adjust how long tokens can be.
	private static final int LOOKAHEAD_LIMIT = 65536;
	
	private Object source;
	private XNReader reader;
	private Vector<XNToken> buffer;
	private XNToken lastToken;
	
	public XNLexer(Object source, Reader reader) {
		this.source = source;
		this.reader = new XNReader(reader);
		this.buffer = new Vector<XNToken>();
		this.lastToken = null;
	}
	
	private static boolean isNumStart(int ch) {
		return ch == '.' || Character.isDigit(ch);
	}
	
	private static boolean isNumPart(int ch) {
		return ch == '.' || ch == '\'' || Character.isDigit(ch);
	}
	
	private static boolean isIdStart(int ch) {
		int t = Character.getType(ch);
		return ch == '\''
			|| Character.isLetter(ch)
			|| t == Character.NON_SPACING_MARK
			|| t == Character.COMBINING_SPACING_MARK
			|| t == Character.PRIVATE_USE
			|| t == Character.CONNECTOR_PUNCTUATION;
	}
	
	private static boolean isIdPart(int ch) {
		int t = Character.getType(ch);
		return ch == '.' || ch == '\''
			|| Character.isDigit(ch)
			|| Character.isLetter(ch)
			|| t == Character.NON_SPACING_MARK
			|| t == Character.COMBINING_SPACING_MARK
			|| t == Character.PRIVATE_USE
			|| t == Character.CONNECTOR_PUNCTUATION;
	}
	
	private static boolean isLineTerm(int ch) {
		return ch < 0 || ch == '\n' || ch == '\r' || ch == '\u2028' || ch == '\u2029';
	}
	
	private static boolean isWhiteSpace(int ch) {
		return ch <= 0x20 || (ch >= 0x7F && ch <= 0xA0) || Character.isSpaceChar(ch);
	}
	
	// Get a token from the input stream, with line and column numbering but with no buffering, linking, or special token handling.
	private XNToken internalGetNextToken1() throws IOException {
		reader.mark(LOOKAHEAD_LIMIT);
		int firstChar = reader.read();
		int numChars = 1;
		// LINE TERMINATOR
		if (isLineTerm(firstChar)) {
			if (firstChar < 0) {
				return new XNToken(XNToken.LINE_TERM, "",
						source,
						reader.getMarkedLine(), reader.getMarkedCol(),
						reader.getMarkedLine(), reader.getMarkedCol());
			} else {
				int nextChar = firstChar;
				while (nextChar >= 0 && isLineTerm(nextChar = reader.read())) numChars++;
				char[] cbuf = new char[numChars];
				reader.reset();
				reader.read(cbuf);
				return new XNToken(XNToken.LINE_TERM, new String(cbuf),
						source,
						reader.getMarkedLine(), reader.getMarkedCol(),
						reader.getLine(), reader.getCol());
			}
		}
		// QUOTED LITERAL
		else if (firstChar == '\"') {
			while (true) {
				int nextChar = reader.read();
				if (isLineTerm(nextChar)) break;
				else {
					numChars++;
					if (nextChar == '\"') break;
					else if (nextChar == '\\') {
						// hit an escape sequence
						nextChar = reader.read();
						if (isLineTerm(nextChar)) break;
						else {
							numChars++;
						}
					}
				}
			}
			char[] cbuf = new char[numChars];
			reader.reset();
			reader.read(cbuf);
			return new XNToken(XNToken.QUOTED, new String(cbuf),
					source,
					reader.getMarkedLine(), reader.getMarkedCol(),
					reader.getLine(), reader.getCol());
		}
		// NUMERIC LITERAL
		else if (isNumStart(firstChar)) {
			while (isNumPart(reader.read())) numChars++;
			char[] cbuf = new char[numChars];
			reader.reset();
			reader.read(cbuf);
			return new XNToken(XNToken.NUMBER, new String(cbuf),
					source,
					reader.getMarkedLine(), reader.getMarkedCol(),
					reader.getLine(), reader.getCol());
		}
		// IDENTIFIER TOKEN
		else if (isIdStart(firstChar)) {
			while (isIdPart(reader.read())) numChars++;
			char[] cbuf = new char[numChars];
			reader.reset();
			reader.read(cbuf);
			return new XNToken(XNToken.ID, new String(cbuf),
					source,
					reader.getMarkedLine(), reader.getMarkedCol(),
					reader.getLine(), reader.getCol());
		}
		// COMMENT
		else if (firstChar == '#' || firstChar == '\u2014' || firstChar == '\u2015') {
			while (!isLineTerm(reader.read())) numChars++;
			char[] cbuf = new char[numChars];
			reader.reset();
			reader.read(cbuf);
			return new XNToken(XNToken.COMMENT, new String(cbuf),
					source,
					reader.getMarkedLine(), reader.getMarkedCol(),
					reader.getLine(), reader.getCol());
		}
		else if (firstChar == '-' || firstChar == '/') {
			int secondChar = reader.read(); numChars++;
			if (firstChar == secondChar) {
				while (!isLineTerm(reader.read())) numChars++;
				char[] cbuf = new char[numChars];
				reader.reset();
				reader.read(cbuf);
				return new XNToken(XNToken.COMMENT, new String(cbuf),
						source,
						reader.getMarkedLine(), reader.getMarkedCol(),
						reader.getLine(), reader.getCol());
			// OR MAYBE SYMBOL TOKEN
			} else {
				reader.reset();
				reader.read();
				return new XNToken(XNToken.SYMBOL, new String(Character.toChars(firstChar)),
						source,
						reader.getMarkedLine(), reader.getMarkedCol(),
						reader.getLine(), reader.getCol());
			}
		}
		else if (firstChar == '\u221E' || firstChar == '~') {
			int secondChar = reader.read(); numChars++;
			if (firstChar == secondChar) {
				int nextToLastChar = firstChar;
				int lastChar = secondChar;
				// loop invariant: [nextToLastChar, lastChar] contain the last two characters read
				// at this point, we've read [°, °]
				while (nextToLastChar == firstChar && lastChar == firstChar) {
					nextToLastChar = lastChar; lastChar = reader.read(); numChars++;
				}
				// at this point, we're read [°, something else]
				while ((nextToLastChar >= 0 && nextToLastChar != firstChar) || (lastChar >= 0 && lastChar != firstChar)) {
					nextToLastChar = lastChar; lastChar = reader.read(); numChars++;
				}
				// at this point, we've read [°, °] or maybe [eof, eof] or even [°, eof]
				// end loop invariant
				while (reader.read() == firstChar) numChars++;
				char[] cbuf = new char[numChars];
				reader.reset();
				reader.read(cbuf);
				return new XNToken(XNToken.COMMENT, new String(cbuf),
						source,
						reader.getMarkedLine(), reader.getMarkedCol(),
						reader.getLine(), reader.getCol());
			// OR MAYBE SYMBOL TOKEN
			} else {
				reader.reset();
				reader.read();
				return new XNToken(XNToken.SYMBOL, new String(Character.toChars(firstChar)),
						source,
						reader.getMarkedLine(), reader.getMarkedCol(),
						reader.getLine(), reader.getCol());
			}
		}
		// LINE CONTINUATOR
		else if (firstChar == '\u00AC' || firstChar == '\\') {
			while (true) {
				int nextChar = reader.read(); numChars++;
				if (isLineTerm(nextChar)) {
					while (nextChar >= 0 && isLineTerm(nextChar = reader.read())) numChars++;
					char[] cbuf = new char[numChars];
					reader.reset();
					reader.read(cbuf);
					return new XNToken(XNToken.CONTINUATOR, new String(cbuf),
							source,
							reader.getMarkedLine(), reader.getMarkedCol(),
							reader.getLine(), reader.getCol());
				// OR MAYBE SYMBOL TOKEN
				} else if (!isWhiteSpace(nextChar)) {
					reader.reset();
					reader.read();
					return new XNToken(XNToken.SYMBOL, new String(Character.toChars(firstChar)),
							source,
							reader.getMarkedLine(), reader.getMarkedCol(),
							reader.getLine(), reader.getCol());
				}
			}
		}
		// WHITESPACE
		else if (isWhiteSpace(firstChar)) {
			return new XNToken(XNToken.WHITESPACE, new String(Character.toChars(firstChar)),
					source,
					reader.getMarkedLine(), reader.getMarkedCol(),
					reader.getLine(), reader.getCol());
		}
		// SYMBOL TOKEN
		else {
			return new XNToken(XNToken.SYMBOL, new String(Character.toChars(firstChar)),
					source,
					reader.getMarkedLine(), reader.getMarkedCol(),
					reader.getLine(), reader.getCol());
		}
	}
	
	// Get a token from the input stream, with line and column numbering and special token handling, but with no buffering or linking.
	private XNToken internalGetNextToken2() throws IOException {
		XNToken t = internalGetNextToken1();
		while (t.kind == XNToken.COMMENT || t.kind == XNToken.CONTINUATOR || t.kind == XNToken.WHITESPACE) {
			XNToken tt = internalGetNextToken1();
			tt.specialToken = t;
			t = tt;
		}
		return t;
	}
	
	// Get a token from the input stream, with line and column numbering, buffering, linking, and special token handling.
	public XNToken getToken() throws IOException {
		XNToken t = (!buffer.isEmpty()) ? buffer.remove(0) : internalGetNextToken2();
		if (this.lastToken != null) this.lastToken.next = t;
		this.lastToken = t;
		return t;
	}
	
	// Look ahead at tokens further along in the input stream.
	public XNToken lookToken(int pos) throws IOException {
		if (pos < 1) return lastToken;
		else {
			while (buffer.size() < pos) {
				buffer.add(internalGetNextToken2());
			}
			return buffer.get(pos-1);
		}
	}
	
	public Object getSource() {
		return source;
	}
	
	public int getCurrentLine() {
		return reader.getLine();
	}
	
	public int getCurrentCol() {
		return reader.getCol();
	}
    
	// BufferedReader that keeps track of line and column numbers.
    private static class XNReader extends BufferedReader {
    	private static final int SKIP_BUFFER_SIZE = 65536;
    	private int line, col;
    	private int mline, mcol;
    	private boolean lastCR, mlastCR;
    	public XNReader(Reader in) {
    		super(in);
    		line = 1; col = 1;
    		mline = 1; mcol = 1;
    		lastCR = false;
    		mlastCR = false;
    	}
    	public XNReader(Reader in, int sz) {
    		super(in, sz);
    		line = 1; col = 1;
    		mline = 1; mcol = 1;
    		lastCR = false;
    		mlastCR = false;
    	}
    	public void mark(int readAheadLimit) throws IOException {
    		super.mark(readAheadLimit);
    		mline = line;
    		mcol = col;
    		mlastCR = lastCR;
    	}
    	public int read() throws IOException {
    		int ch = super.read();
    		if (ch == '\u2028' || ch == '\u2029') {
    			line++;
    			col = 1;
    			lastCR = false;
    		} else if (ch == '\r') {
    			line++;
    			col = 1;
    			lastCR = true;
    		} else if (ch == '\n') {
    			if (!lastCR) line++;
    			col = 1;
    			lastCR = false;
    		} else {
    			col++;
    			lastCR = false;
    		}
    		return ch;
    	}
    	public int read(char[] cbuf, int off, int len) throws IOException {
    		int rlen = super.read(cbuf, off, len);
    		for (int i = off, n = 0; n < rlen; i++, n++) {
        		if (cbuf[i] == '\u2028' || cbuf[i] == '\u2029') {
        			line++;
        			col = 1;
        			lastCR = false;
        		} else if (cbuf[i] == '\r') {
        			line++;
        			col = 1;
        			lastCR = true;
        		} else if (cbuf[i] == '\n') {
        			if (!lastCR) line++;
        			col = 1;
        			lastCR = false;
        		} else {
        			col++;
        			lastCR = false;
        		}
    		}
    		return rlen;
    	}
    	public String readLine() throws IOException {
    		String s = super.readLine();
    		line++; col = 1; lastCR = false;
    		return s;
    	}
    	public void reset() throws IOException {
    		super.reset();
    		line = mline;
    		col = mcol;
    		lastCR = mlastCR;
    	}
    	public long skip(long n) throws IOException {
			// this calls read() above, which already takes care of line numbers
    		long rlen = 0;
    		char[] cbuf = new char[SKIP_BUFFER_SIZE];
    		while (n > SKIP_BUFFER_SIZE) {
    			rlen += this.read(cbuf, 0, SKIP_BUFFER_SIZE);
    			n -= SKIP_BUFFER_SIZE;
    		}
    		rlen += this.read(cbuf, 0, (int)n);
    		return rlen;
    	}
    	public int getMarkedLine() { return mline; }
    	public int getMarkedCol() { return mcol; }
    	public int getLine() { return line; }
    	public int getCol() { return col; }
    }
}
