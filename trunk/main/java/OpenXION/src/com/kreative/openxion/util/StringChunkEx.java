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

import java.text.*;

/**
 * The StringChunkEx utility class is used to find
 * the boundaries of string chunks (character, line,
 * item, row, column, word, sentence, and paragraph).
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class StringChunkEx {
	private StringChunkEx() {}
	
	public static int count(String s, StringChunkType type, char itemDelimiter, char columnDelimiter, char rowDelimiter) {
		switch (type) {
		case CHARACTER: return s.length();
		case LINE: return countl(s);
		case ITEM: return countd(s,itemDelimiter);
		case COLUMN: return countd(s,columnDelimiter);
		case ROW: return countd(s,rowDelimiter);
		case WORD: return countw(s);
		case SENTENCE: return counts(s);
		case PARAGRAPH: return countp(s);
		default: return 0;
		}
	}
	
	public static int start(String s, StringChunkType type, int start, char itemDelimiter, char columnDelimiter, char rowDelimiter) {
		switch (type) {
		case CHARACTER: return Math.max(0, Math.min(s.length(), start-1));
		case LINE: return startl(s,start);
		case ITEM: return startd(s,start,itemDelimiter);
		case COLUMN: return startd(s,start,columnDelimiter);
		case ROW: return startd(s,start,rowDelimiter);
		case WORD: return startw(s,start);
		case SENTENCE: return starts(s,start);
		case PARAGRAPH: return startp(s,start);
		default: return 0;
		}
	}
	
	public static int end(String s, StringChunkType type, int end, char itemDelimiter, char columnDelimiter, char rowDelimiter) {
		switch (type) {
		case CHARACTER: return Math.max(0, Math.min(s.length(), end));
		case LINE: return endl(s,end);
		case ITEM: return endd(s,end,itemDelimiter);
		case COLUMN: return endd(s,end,columnDelimiter);
		case ROW: return endd(s,end,rowDelimiter);
		case WORD: return endw(s,end);
		case SENTENCE: return ends(s,end);
		case PARAGRAPH: return endp(s,end);
		default: return 0;
		}
	}
	
	private static int countd(String t, char ch) {
		if (t.length() <= 0) return 0;
		int cnt = 0;
		boolean last = false;
		CharacterIterator i = new StringCharacterIterator(t);
		for (char c = i.first(); c != CharacterIterator.DONE; c = i.next()) {
			if (c == ch) {
				cnt++;
				last = true;
			} else {
				last = false;
			}
		}
		if (last) cnt--;
		return cnt+1;
	}
	
	private static int startd(String s, int start, char d) {
		if (start < 1) return 0;
		int cc = 1; int co = 0;
		while (true) {
			if (cc >= start) return co;
			cc++; co = s.indexOf(d, co)+1;
			if (co < 1) return s.length();
		}
	}
	
	private static int endd(String s, int end, char d) {
		if (end < 1) return 0;
		int cc = 0; int co = 0;
		while (true) {
			cc++; co = s.indexOf(d, co);
			if (co < 0) return s.length();
			if (cc >= end) return co;
			co++;
		}
	}
	
	private static int countl(String t) {
		if (t.length() <= 0) return 0;
		int cnt = 0;
		boolean lastCR = false;
		boolean lastBR = false;
		CharacterIterator i = new StringCharacterIterator(t);
		for (char c = i.first(); c != CharacterIterator.DONE; c = i.next()) {
			if (c == '\u2028' || c == '\u2029') {
    			cnt++;
    			lastCR = false;
    			lastBR = true;
    		} else if (c == '\r') {
    			cnt++;
    			lastCR = true;
    			lastBR = true;
    		} else if (c == '\n') {
    			if (!lastCR) cnt++;
    			lastCR = false;
    			lastBR = true;
    		} else {
    			lastCR = false;
    			lastBR = false;
    		}
		}
		if (lastBR) cnt--;
		return cnt+1;
	}
	
	private static int startl(String s, int start) {
		if (start < 1) return 0;
		int cc = 1; int co = 0;
		while (true) {
			if (cc >= start) return co;
			cc++;
			int n = s.indexOf('\n', co);
			int r = s.indexOf('\r', co);
			int l = s.indexOf('\u2028', co);
			int p = s.indexOf('\u2029', co);
			if (r >= 0 && (n == r+1) && (l < 0 || l > r) && (p < 0 || p > r)) {
				co = r+2;
			}
			else if (n >= 0 && (r < 0 || r > n) && (l < 0 || l > n) && (p < 0 || p > n)) {
				co = n+1;
			}
			else if (r >= 0 && (n < 0 || n > r) && (l < 0 || l > r) && (p < 0 || p > r)) {
				co = r+1;
			}
			else if (l >= 0 && (n < 0 || n > l) && (r < 0 || r > l) && (p < 0 || p > l)) {
				co = l+1;
			}
			else if (p >= 0 && (n < 0 || n > p) && (r < 0 || r > p) && (l < 0 || l > p)) {
				co = p+1;
			}
			else {
				return s.length();
			}
		}
	}
	
	private static int endl(String s, int end) {
		if (end < 1) return 0;
		int cc = 0; int co = 0;
		while (true) {
			cc++;
			int n = s.indexOf('\n', co);
			int r = s.indexOf('\r', co);
			int l = s.indexOf('\u2028', co);
			int p = s.indexOf('\u2029', co);
			if (r >= 0 && (n == r+1) && (l < 0 || l > r) && (p < 0 || p > r)) {
				if (cc >= end) return r;
				else co = r+2;
			}
			else if (n >= 0 && (r < 0 || r > n) && (l < 0 || l > n) && (p < 0 || p > n)) {
				if (cc >= end) return n;
				else co = n+1;
			}
			else if (r >= 0 && (n < 0 || n > r) && (l < 0 || l > r) && (p < 0 || p > r)) {
				if (cc >= end) return r;
				else co = r+1;
			}
			else if (l >= 0 && (n < 0 || n > l) && (r < 0 || r > l) && (p < 0 || p > l)) {
				if (cc >= end) return l;
				else co = l+1;
			}
			else if (p >= 0 && (n < 0 || n > p) && (r < 0 || r > p) && (l < 0 || l > p)) {
				if (cc >= end) return p;
				else co = p+1;
			}
			else {
				return s.length();
			}
		}
	}
	
	private static boolean isBreak(char ch) {
		return ch < 0 || ch == '\n' || ch == '\r' || ch == '\u2028' || ch == '\u2029';
	}
	
	private static int countp(String t) {
		int n = 0, s = 0;
		while (s < t.length() && isBreak(t.charAt(s))) s++;
		while (s < t.length()) {
			n++;
			while (s < t.length() && !isBreak(t.charAt(s))) s++;
			while (s < t.length() && isBreak(t.charAt(s))) s++;
		}
		return n;
	}
	
	private static int startp(String t, int w) {
		int n = 0, s = 0;
		while (s < t.length() && isBreak(t.charAt(s))) s++;
		while (s < t.length()) {
			n++;
			if (n == w) return s;
			while (s < t.length() && !isBreak(t.charAt(s))) s++;
			while (s < t.length() && isBreak(t.charAt(s))) s++;
		}
		return (w <= 0)?0:t.length();
	}
	
	private static int endp(String t, int w) {
		int n = 0, s = 0;
		while (s < t.length() && isBreak(t.charAt(s))) s++;
		while (s < t.length()) {
			n++;
			while (s < t.length() && !isBreak(t.charAt(s))) s++;
			if (n == w) return s;
			while (s < t.length() && isBreak(t.charAt(s))) s++;
		}
		return (w <= 0)?0:t.length();
	}
	
	private static boolean isWhite(char ch) {
		return ch <= 0x20 || (ch >= 0x7F && ch <= 0xA0) || Character.isSpaceChar(ch);
	}
	
	private static boolean isSenEnd(char ch) {
		return ch == '.' || ch == '!' || ch == '?';
	}
	
	private static int counts(String t) {
		int n = 0, s = 0;
		while (s < t.length() && isWhite(t.charAt(s))) s++;
		while (s < t.length()) {
			n++;
			while (s < t.length() && !isSenEnd(t.charAt(s))) s++;
			while (s < t.length() && !isWhite(t.charAt(s))) s++;
			while (s < t.length() && isWhite(t.charAt(s))) s++;
		}
		return n;
	}
	
	private static int starts(String t, int w) {
		int n = 0, s = 0;
		while (s < t.length() && isWhite(t.charAt(s))) s++;
		while (s < t.length()) {
			n++;
			if (n == w) return s;
			while (s < t.length() && !isSenEnd(t.charAt(s))) s++;
			while (s < t.length() && !isWhite(t.charAt(s))) s++;
			while (s < t.length() && isWhite(t.charAt(s))) s++;
		}
		return (w <= 0)?0:t.length();
	}
	
	private static int ends(String t, int w) {
		int n = 0, s = 0;
		while (s < t.length() && isWhite(t.charAt(s))) s++;
		while (s < t.length()) {
			n++;
			while (s < t.length() && !isSenEnd(t.charAt(s))) s++;
			while (s < t.length() && !isWhite(t.charAt(s))) s++;
			if (n == w) return s;
			while (s < t.length() && isWhite(t.charAt(s))) s++;
		}
		return (w <= 0)?0:t.length();
	}
	
	private static int countw(String t) {
		int n = 0, s = 0;
		while (s < t.length() && isWhite(t.charAt(s))) s++;
		while (s < t.length()) {
			n++;
			while (s < t.length() && !isWhite(t.charAt(s))) s++;
			while (s < t.length() && isWhite(t.charAt(s))) s++;
		}
		return n;
	}
	
	private static int startw(String t, int w) {
		int n = 0, s = 0;
		while (s < t.length() && isWhite(t.charAt(s))) s++;
		while (s < t.length()) {
			n++;
			if (n == w) return s;
			while (s < t.length() && !isWhite(t.charAt(s))) s++;
			while (s < t.length() && isWhite(t.charAt(s))) s++;
		}
		return (w <= 0)?0:t.length();
	}
	
	private static int endw(String t, int w) {
		int n = 0, s = 0;
		while (s < t.length() && isWhite(t.charAt(s))) s++;
		while (s < t.length()) {
			n++;
			while (s < t.length() && !isWhite(t.charAt(s))) s++;
			if (n == w) return s;
			while (s < t.length() && isWhite(t.charAt(s))) s++;
		}
		return (w <= 0)?0:t.length();
	}
}
