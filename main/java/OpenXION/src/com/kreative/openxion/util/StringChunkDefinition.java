/*
 * Copyright &copy; 2014 Rebecca G. Bettencourt / Kreative Software
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
 * @since OpenXION 1.5
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.util;

import java.util.ArrayList;
import java.util.List;

/**
 * The StringChunkDefinition class defines how to find boundaries of
 * string chunks (characters, lines, items, rows, columns, words,
 * sentences, and paragraphs). It is a more efficient replacement for
 * the StringChunkEx utility class in previous versions of OpenXION.
 * @since OpenXION 1.5
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public abstract class StringChunkDefinition {
	protected abstract int findFirst(String s, int start, int end);
	protected abstract int findEnd(String s, int start, int end);
	protected abstract int findNext(String s, int start, int end);
	public boolean isDelimited() { return false; }
	public String getDelimiter() { return null; }
	
	public static final class Character extends StringChunkDefinition {
		private static final Character instance = new Character();
		public static Character getInstance() { return instance; }
		private Character() {}
		@Override
		protected int findFirst(String s, int start, int end) {
			return start;
		}
		@Override
		protected int findEnd(String s, int start, int end) {
			if (start < end) {
				boolean highSurrogate = java.lang.Character.isHighSurrogate(s.charAt(start));
				start++;
				if (highSurrogate && start < end && java.lang.Character.isLowSurrogate(s.charAt(start))) {
					start++;
				}
			}
			return start;
		}
		@Override
		protected int findNext(String s, int start, int end) {
			return start;
		}
	}
	
	public static final class Line extends StringChunkDefinition {
		public static Line getInstance(String lineEnding) { return new Line(lineEnding); }
		private final String lineEnding;
		private Line(String lineEnding) {
			this.lineEnding = lineEnding;
		}
		@Override
		protected int findFirst(String s, int start, int end) {
			return start;
		}
		@Override
		protected int findEnd(String s, int start, int end) {
			while (start < end && !isLineBreak(s.charAt(start))) {
				start++;
			}
			return start;
		}
		@Override
		protected int findNext(String s, int start, int end) {
			if (start < end && isLineBreak(s.charAt(start))) {
				boolean cr = (s.charAt(start) == '\r');
				start++;
				if (cr && start < end && s.charAt(start) == '\n') {
					start++;
				}
			}
			return start;
		}
		@Override
		public boolean isDelimited() {
			return true;
		}
		@Override
		public String getDelimiter() {
			return lineEnding;
		}
		private static boolean isLineBreak(char ch) {
			return ch == '\n' || ch == '\r' || ch == '\u2028' || ch == '\u2029';
		}
	}
	
	public static final class Item extends StringChunkDefinition {
		public static Item getInstance(String itemDelimiter) { return new Item(itemDelimiter); }
		private final String itemDelimiter;
		private Item(String itemDelimiter) {
			this.itemDelimiter = itemDelimiter;
		}
		@Override
		protected int findFirst(String s, int start, int end) {
			return start;
		}
		@Override
		protected int findEnd(String s, int start, int end) {
			start = s.indexOf(itemDelimiter, start);
			if (start < 0 || start > end) {
				start = end;
			}
			return start;
		}
		@Override
		protected int findNext(String s, int start, int end) {
			if (start < end && s.substring(start).startsWith(itemDelimiter)) {
				start += itemDelimiter.length();
			}
			return start;
		}
		@Override
		public boolean isDelimited() {
			return true;
		}
		@Override
		public String getDelimiter() {
			return itemDelimiter;
		}
	}
	
	public static final class Word extends StringChunkDefinition {
		private static final Word instance = new Word();
		public static Word getInstance() { return instance; }
		private Word() {}
		@Override
		protected int findFirst(String s, int start, int end) {
			while (start < end && isWhiteSpace(s.charAt(start))) {
				start++;
			}
			return start;
		}
		@Override
		protected int findEnd(String s, int start, int end) {
			while (start < end && !isWhiteSpace(s.charAt(start))) {
				start++;
			}
			return start;
		}
		@Override
		protected int findNext(String s, int start, int end) {
			while (start < end && isWhiteSpace(s.charAt(start))) {
				start++;
			}
			return start;
		}
		private static boolean isWhiteSpace(char ch) {
			return ch <= 0x20 || (ch >= 0x7F && ch <= 0xA0) || java.lang.Character.isSpaceChar(ch);
		}
	}
	
	public static final class Sentence extends StringChunkDefinition {
		private static final Sentence instance = new Sentence();
		public static Sentence getInstance() { return instance; }
		private Sentence() {}
		@Override
		protected int findFirst(String s, int start, int end) {
			while (start < end && isWhiteSpace(s.charAt(start))) {
				start++;
			}
			return start;
		}
		@Override
		protected int findEnd(String s, int start, int end) {
			while (start < end && !isSentenceEnder(s.charAt(start))) {
				start++;
			}
			while (start < end && !isWhiteSpace(s.charAt(start))) {
				start++;
			}
			return start;
		}
		@Override
		protected int findNext(String s, int start, int end) {
			while (start < end && isWhiteSpace(s.charAt(start))) {
				start++;
			}
			return start;
		}
		private static boolean isWhiteSpace(char ch) {
			return ch <= 0x20 || (ch >= 0x7F && ch <= 0xA0) || java.lang.Character.isSpaceChar(ch);
		}
		private static boolean isSentenceEnder(char ch) {
			return ch == '.' || ch == '!' || ch == '?';
		}
	}
	
	public static final class Paragraph extends StringChunkDefinition {
		private static final Paragraph instance = new Paragraph();
		public static Paragraph getInstance() { return instance; }
		private Paragraph() {}
		@Override
		protected int findFirst(String s, int start, int end) {
			while (start < end && isLineBreak(s.charAt(start))) {
				start++;
			}
			return start;
		}
		@Override
		protected int findEnd(String s, int start, int end) {
			while (start < end && !isLineBreak(s.charAt(start))) {
				start++;
			}
			return start;
		}
		@Override
		protected int findNext(String s, int start, int end) {
			while (start < end && isLineBreak(s.charAt(start))) {
				start++;
			}
			return start;
		}
		private static boolean isLineBreak(char ch) {
			return ch == '\n' || ch == '\r' || ch == '\u2028' || ch == '\u2029';
		}
	}
	
	public final int countChunks(String s, int start, int end) {
		int n = 0;
		start = findFirst(s, start, end);
		while (start < end) {
			n++;
			start = findEnd(s, start, end);
			start = findNext(s, start, end);
		}
		return n;
	}
	
	public static final class ChunkLocation {
		private final String content;
		private final int firstChunkIndex;
		private final int lastChunkIndex;
		private final int startIndex;
		private final int endIndex;
		private final int deleteEndIndex;
		protected ChunkLocation(String s, int ws, int we, int i, int o, int u) {
			this.content = s.substring(i, o);
			this.firstChunkIndex = ws;
			this.lastChunkIndex = we;
			this.startIndex = i;
			this.endIndex = o;
			this.deleteEndIndex = u;
		}
		public String getContent() { return content; }
		public int getFirstChunkIndex() { return firstChunkIndex; }
		public int getLastChunkIndex() { return lastChunkIndex; }
		public int getStartIndex() { return startIndex; }
		public int getEndIndex() { return endIndex; }
		public int getDeleteEndIndex() { return deleteEndIndex; }
	}
	
	public final ChunkLocation[] splitChunks(String s, int start, int end) {
		List<ChunkLocation> l = new ArrayList<ChunkLocation>();
		int n = 0;
		start = findFirst(s, start, end);
		while (start < end) {
			n++;
			int chunkEnd = findEnd(s, start, end);
			int nextStart = findNext(s, chunkEnd, end);
			l.add(new ChunkLocation(s, n, n, start, chunkEnd, nextStart));
			start = nextStart;
		}
		return l.toArray(new ChunkLocation[0]);
	}
	
	public final ChunkLocation findChunk(String s, int start, int end, int firstChunkIndex, int lastChunkIndex) {
		Integer startIndex = ((firstChunkIndex < 1) ? start : null);
		Integer endIndex = ((lastChunkIndex < 1) ? start : null);
		Integer deleteEndIndex = ((lastChunkIndex < 0) ? start : null);
		if (startIndex == null || endIndex == null || deleteEndIndex == null) {
			int n = 0;
			start = findFirst(s, start, end);
			while (start < end) {
				if (n == lastChunkIndex) {
					deleteEndIndex = start;
					if (startIndex != null && endIndex != null) break;
				}
				n++;
				if (n == firstChunkIndex) {
					startIndex = start;
					if (endIndex != null && deleteEndIndex != null) break;
				}
				start = findEnd(s, start, end);
				if (n == lastChunkIndex) {
					endIndex = start;
					if (startIndex != null && deleteEndIndex != null) break;
				}
				start = findNext(s, start, end);
			}
			if (startIndex == null) startIndex = end;
			if (endIndex == null) endIndex = end;
			if (deleteEndIndex == null) deleteEndIndex = end;
			if (endIndex < startIndex) endIndex = startIndex;
			if (deleteEndIndex < endIndex) deleteEndIndex = endIndex;
		}
		return new ChunkLocation(s, firstChunkIndex, lastChunkIndex, startIndex, endIndex, deleteEndIndex);
	}
	
	public final ChunkLocation findChunkByContent(String s, int start, int end, String content) {
		int n = 0;
		start = findFirst(s, start, end);
		while (start < end) {
			n++;
			int chunkEnd = findEnd(s, start, end);
			int nextStart = findNext(s, chunkEnd, end);
			if (s.substring(start, chunkEnd).equals(content)) {
				return new ChunkLocation(s, n, n, start, chunkEnd, nextStart);
			}
			start = nextStart;
		}
		return null;
	}
	
	public final ChunkLocation findChunkByContentIgnoreCase(String s, int start, int end, String content) {
		int n = 0;
		start = findFirst(s, start, end);
		while (start < end) {
			n++;
			int chunkEnd = findEnd(s, start, end);
			int nextStart = findNext(s, chunkEnd, end);
			if (s.substring(start, chunkEnd).equalsIgnoreCase(content)) {
				return new ChunkLocation(s, n, n, start, chunkEnd, nextStart);
			}
			start = nextStart;
		}
		return null;
	}
	
	public static final class ChunkInfo {
		protected final String sourceString;
		protected final String stringToAppend;
		protected final String stringToPrepend;
		protected final ChunkLocation location;
		protected ChunkInfo(String s, String a, String p, ChunkLocation l) {
			this.sourceString = s;
			this.stringToAppend = a;
			this.stringToPrepend = p;
			this.location = l;
		}
		public String getSourceString() { return sourceString; }
		public String getStringToAppend() { return stringToAppend; }
		public String getStringToPrepend() { return stringToPrepend; }
		public ChunkLocation getLocation() { return location; }
		public String getContent() { return location.content; }
		public int getFirstChunkIndex() { return location.firstChunkIndex; }
		public int getLastChunkIndex() { return location.lastChunkIndex; }
		public int getStartIndex() { return location.startIndex; }
		public int getEndIndex() { return location.endIndex; }
		public int getDeleteEndIndex() { return location.deleteEndIndex; }
	}
	
	public final ChunkInfo resolveChunk(String s, int start, int end, int firstChunkIndex, int lastChunkIndex, boolean forPrepend, boolean forAppend) {
		int n = countChunks(s, start, end);
		int[] chunkIndex = XIONUtil.index(1, n, firstChunkIndex, lastChunkIndex);
		firstChunkIndex = chunkIndex[0];
		lastChunkIndex = chunkIndex[1];
		String toAppend = null;
		String toPrepend = null;
		if (isDelimited()) {
			if ((forPrepend && firstChunkIndex > n) || (forAppend && lastChunkIndex > n)) {
				StringBuffer a = new StringBuffer();
				int m = ((forPrepend && forAppend) ? Math.max(firstChunkIndex, lastChunkIndex) : forPrepend ? firstChunkIndex : forAppend ? lastChunkIndex : n) - n;
				if (n == 0) {
					m--;
					n++;
				}
				while (m-- > 0) {
					a.append(getDelimiter());
					n++;
				}
				s = s.substring(0, end) + a.toString() + s.substring(end);
				end += a.length();
				toAppend = a.toString();
			}
			if ((forPrepend && firstChunkIndex < 1) || (forAppend && lastChunkIndex < 1)) {
				StringBuffer a = new StringBuffer();
				int m = 1 - ((forPrepend && forAppend) ? Math.min(firstChunkIndex, lastChunkIndex) : forPrepend ? firstChunkIndex : forAppend ? lastChunkIndex: 1);
				while (m-- > 0) {
					a.append(getDelimiter());
					n++;
					firstChunkIndex++;
					lastChunkIndex++;
				}
				s = s.substring(0, start) + a.toString() + s.substring(start);
				end += a.length();
				toPrepend = a.toString();
			}
		}
		ChunkLocation location = findChunk(s, start, end, firstChunkIndex, lastChunkIndex);
		return new ChunkInfo(s, toAppend, toPrepend, location);
	}
	
	public final ChunkInfo resolveChunkByContent(String s, int start, int end, String content) {
		ChunkLocation location = findChunkByContent(s, start, end, content);
		return new ChunkInfo(s, null, null, location);
	}
	
	public final ChunkInfo resolveChunkByContentIgnoreCase(String s, int start, int end, String content) {
		ChunkLocation location = findChunkByContentIgnoreCase(s, start, end, content);
		return new ChunkInfo(s, null, null, location);
	}
}
