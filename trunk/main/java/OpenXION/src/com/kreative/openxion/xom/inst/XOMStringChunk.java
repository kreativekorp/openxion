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

package com.kreative.openxion.xom.inst;

import java.util.*;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.ast.XNModifier;
import com.kreative.openxion.util.XIONUtil;
import com.kreative.openxion.util.StringChunkType;
import com.kreative.openxion.util.StringChunkEx;
import com.kreative.openxion.xom.XOMContainerObject;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMStringContainer;
import com.kreative.openxion.xom.XOMComparator;

public class XOMStringChunk extends XOMContainerObject implements XOMStringContainer {
	private static final long serialVersionUID = 1L;
	
	private XOMVariant parent;
	private StringChunkType chunkType;
	private int startIndex;
	private int endIndex;
	
	public XOMStringChunk(XOMVariant parent, StringChunkType chunkType, int startIndex, int endIndex) {
		this.parent = parent;
		this.chunkType = chunkType;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}
	
	public boolean canGetParent(XNContext ctx) {
		return true;
	}
	
	public XOMVariant getParent(XNContext ctx) {
		return parent;
	}
	
	private static class StringChunkInfo {
		public String parentContent;
		//public int chunkCount;
		public int startChunkIndex;
		public int endChunkIndex;
		public int startCharIndex;
		public int endCharIndex;
		public int deleteEndCharIndex;
	}
	
	private StringChunkInfo getChunkInfo(XNContext ctx, boolean puttingBefore, boolean puttingAfter) {
		String ts;
		if (puttingBefore || puttingAfter) {
			parent.asContainer(ctx, false);
		}
		if (parent.canGetContents(ctx)) {
			ts = parent.getContents(ctx).toTextString(ctx);
		} else {
			ts = parent.toTextString(ctx);
		}
		if (ts == null) return null;
		else {
			char id = ctx.getItemDelimiter();
			char cd = ctx.getColumnDelimiter();
			char rd = ctx.getRowDelimiter();
			int count = StringChunkEx.count(ts, chunkType, id, cd, rd);
			int[] idx = XIONUtil.index(1, count, startIndex, endIndex);
			int s = idx[0], e = idx[1];
			if ((puttingBefore && s > count) || (puttingAfter && e > count)) {
				if (chunkType == StringChunkType.LINE) {
					String a = "";
					int n = ( (puttingBefore && puttingAfter) ? Math.max(s,e) : puttingBefore ? s : puttingAfter ? e : count )-count;
					if (count == 0) {
						n--;
						count++;
					}
					while (n-- > 0) {
						a += ctx.getLineEnding();
						count++;
					}
					ts += a;
					parent.putAfterContents(ctx, new XOMString(a));
				}
				else if (chunkType == StringChunkType.ITEM || chunkType == StringChunkType.ROW || chunkType == StringChunkType.COLUMN) {
					char d;
					switch (chunkType) {
					case ITEM: d = id; break;
					case ROW: d = rd; break;
					case COLUMN: d = cd; break;
					default: d = 0xFFFF; break;
					}
					String a = "";
					int n = ( (puttingBefore && puttingAfter) ? Math.max(s,e) : puttingBefore ? s : puttingAfter ? e : count )-count;
					if (count == 0) {
						n--;
						count++;
					}
					while (n-- > 0) {
						a += d;
						count++;
					}
					ts += a;
					parent.putAfterContents(ctx, new XOMString(a));
				}
			}
			if ((puttingBefore && s < 1) || (puttingAfter && e < 1)) {
				if (chunkType == StringChunkType.LINE) {
					String a = "";
					int n = 1-( (puttingBefore && puttingAfter) ? Math.min(s,e) : puttingBefore ? s : puttingAfter ? e : 1 );
					if (count == 0) {
						n--;
						count++;
					}
					while (n-- > 0) {
						a += ctx.getLineEnding();
						count++;
						s++;
						e++;
					}
					ts = a + ts;
					parent.putBeforeContents(ctx, new XOMString(a));
				}
				else if (chunkType == StringChunkType.ITEM || chunkType == StringChunkType.ROW || chunkType == StringChunkType.COLUMN) {
					char d;
					switch (chunkType) {
					case ITEM: d = id; break;
					case ROW: d = rd; break;
					case COLUMN: d = cd; break;
					default: d = 0xFFFF; break;
					}
					String a = "";
					int n = 1-( (puttingBefore && puttingAfter) ? Math.min(s,e) : puttingBefore ? s : puttingAfter ? e : 1 );
					if (count == 0) {
						n--;
						count++;
					}
					while (n-- > 0) {
						a += d;
						count++;
						s++;
						e++;
					}
					ts = a + ts;
					parent.putBeforeContents(ctx, new XOMString(a));
				}
			}
			StringChunkInfo ci = new StringChunkInfo();
			ci.parentContent = ts;
			//ci.chunkCount = count;
			ci.startChunkIndex = s;
			ci.endChunkIndex = e;
			ci.startCharIndex = StringChunkEx.start(ts, chunkType, s, id, cd, rd);
			ci.endCharIndex = StringChunkEx.end(ts, chunkType, e, id, cd, rd);
			ci.deleteEndCharIndex = StringChunkEx.start(ts, chunkType, e+1, id, cd, rd);
			if (ci.startCharIndex > ci.endCharIndex) ci.endCharIndex = ci.startCharIndex;
			if (ci.startCharIndex > ci.deleteEndCharIndex) ci.deleteEndCharIndex = ci.startCharIndex;
			return ci;
		}
	}
	
	public boolean canDelete(XNContext ctx) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canDeleteString(ctx)) {
			return true;
		}
		else if (parent.canPutContents(ctx)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void delete(XNContext ctx) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canDeleteString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkInfo ci = getChunkInfo(ctx,false,false);
			p.deleteString(ctx, ci.startCharIndex, ci.deleteEndCharIndex);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkInfo ci = getChunkInfo(ctx,false,false);
			String left = ci.parentContent.substring(0, ci.startCharIndex);
			String right = ci.parentContent.substring(ci.deleteEndCharIndex);
			parent.putIntoContents(ctx, new XOMString(left + right));
		}
		else {
			super.delete(ctx);
		}
	}
	
	public boolean canDeleteString(XNContext ctx) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canDeleteString(ctx)) {
			return true;
		}
		else if (parent.canPutContents(ctx)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void deleteString(XNContext ctx, int startCharIndex, int endCharIndex) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canDeleteString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkInfo ci = getChunkInfo(ctx,false,false);
			int s = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+startCharIndex));
			int e = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+endCharIndex));
			p.deleteString(ctx, s, e);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkInfo ci = getChunkInfo(ctx,false,false);
			int s = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+startCharIndex));
			int e = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+endCharIndex));
			String left = ci.parentContent.substring(0, s);
			String right = ci.parentContent.substring(e);
			parent.putIntoContents(ctx, new XOMString(left + right));
		}
		else {
			super.delete(ctx);
		}
	}
	
	public boolean canGetContents(XNContext ctx) {
		return true;
	}
	
	public XOMVariant getContents(XNContext ctx) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canGetString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkInfo ci = getChunkInfo(ctx,false,false);
			return p.getString(ctx, ci.startCharIndex, ci.endCharIndex);
		}
		else {
			StringChunkInfo ci = getChunkInfo(ctx,false,false);
			return new XOMString(ci.parentContent.substring(ci.startCharIndex, ci.endCharIndex));
		}
	}
	
	public boolean canGetString(XNContext ctx) {
		return true;
	}
	
	public XOMVariant getString(XNContext ctx, int startCharIndex, int endCharIndex) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canGetString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkInfo ci = getChunkInfo(ctx,false,false);
			int s = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+startCharIndex));
			int e = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+endCharIndex));
			return p.getString(ctx, s, e);
		}
		else {
			StringChunkInfo ci = getChunkInfo(ctx,false,false);
			int s = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+startCharIndex));
			int e = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+endCharIndex));
			return new XOMString(ci.parentContent.substring(s, e));
		}
	}
	
	public boolean canPutContents(XNContext ctx) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			return true;
		}
		else if (parent.canPutContents(ctx)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void putIntoContents(XNContext ctx, XOMVariant contents) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkInfo ci = getChunkInfo(ctx,true,true);
			p.putIntoString(ctx, ci.startCharIndex, ci.endCharIndex, contents);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkInfo ci = getChunkInfo(ctx,true,true);
			String left = ci.parentContent.substring(0, ci.startCharIndex);
			String right = ci.parentContent.substring(ci.endCharIndex);
			parent.putIntoContents(ctx, new XOMString(left + contents.toTextString(ctx) + right));
		}
		else {
			super.putIntoContents(ctx, contents);
		}
	}
	
	public void putBeforeContents(XNContext ctx, XOMVariant contents) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkInfo ci = getChunkInfo(ctx,true,false);
			p.putBeforeString(ctx, ci.startCharIndex, ci.endCharIndex, contents);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkInfo ci = getChunkInfo(ctx,true,false);
			String left = ci.parentContent.substring(0, ci.startCharIndex);
			String right = ci.parentContent.substring(ci.startCharIndex);
			parent.putIntoContents(ctx, new XOMString(left + contents.toTextString(ctx) + right));
		}
		else {
			super.putBeforeContents(ctx, contents);
		}
	}
	
	public void putAfterContents(XNContext ctx, XOMVariant contents) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkInfo ci = getChunkInfo(ctx,false,true);
			p.putAfterString(ctx, ci.startCharIndex, ci.endCharIndex, contents);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkInfo ci = getChunkInfo(ctx,false,true);
			String left = ci.parentContent.substring(0, ci.endCharIndex);
			String right = ci.parentContent.substring(ci.endCharIndex);
			parent.putIntoContents(ctx, new XOMString(left + contents.toTextString(ctx) + right));
		}
		else {
			super.putAfterContents(ctx, contents);
		}
	}
	
	public void putIntoContents(XNContext ctx, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkInfo ci = getChunkInfo(ctx,true,true);
			p.putIntoString(ctx, ci.startCharIndex, ci.endCharIndex, contents, property, pvalue);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkInfo ci = getChunkInfo(ctx,true,true);
			String left = ci.parentContent.substring(0, ci.startCharIndex);
			String right = ci.parentContent.substring(ci.endCharIndex);
			parent.putIntoContents(ctx, new XOMString(left + contents.toTextString(ctx) + right), property, pvalue);
		}
		else {
			super.putIntoContents(ctx, contents, property, pvalue);
		}
	}
	
	public void putBeforeContents(XNContext ctx, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkInfo ci = getChunkInfo(ctx,true,false);
			p.putBeforeString(ctx, ci.startCharIndex, ci.endCharIndex, contents, property, pvalue);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkInfo ci = getChunkInfo(ctx,true,false);
			String left = ci.parentContent.substring(0, ci.startCharIndex);
			String right = ci.parentContent.substring(ci.startCharIndex);
			parent.putIntoContents(ctx, new XOMString(left + contents.toTextString(ctx) + right), property, pvalue);
		}
		else {
			super.putBeforeContents(ctx, contents, property, pvalue);
		}
	}
	
	public void putAfterContents(XNContext ctx, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkInfo ci = getChunkInfo(ctx,false,true);
			p.putAfterString(ctx, ci.startCharIndex, ci.endCharIndex, contents, property, pvalue);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkInfo ci = getChunkInfo(ctx, false,true);
			String left = ci.parentContent.substring(0, ci.endCharIndex);
			String right = ci.parentContent.substring(ci.endCharIndex);
			parent.putIntoContents(ctx, new XOMString(left + contents.toTextString(ctx) + right), property, pvalue);
		}
		else {
			super.putAfterContents(ctx, contents, property, pvalue);
		}
	}
	
	public boolean canPutString(XNContext ctx) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			return true;
		}
		else if (parent.canPutContents(ctx)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void putIntoString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkInfo ci = getChunkInfo(ctx,true,true);
			int s = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+startCharIndex));
			int e = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+endCharIndex));
			p.putIntoString(ctx, s, e, contents);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkInfo ci = getChunkInfo(ctx,true,true);
			int s = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+startCharIndex));
			int e = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+endCharIndex));
			String left = ci.parentContent.substring(0, s);
			String right = ci.parentContent.substring(e);
			parent.putIntoContents(ctx, new XOMString(left + contents.toTextString(ctx) + right));
		}
		else {
			super.putIntoContents(ctx, contents);
		}
	}
	
	public void putBeforeString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkInfo ci = getChunkInfo(ctx,true,false);
			int s = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+startCharIndex));
			int e = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+endCharIndex));
			p.putBeforeString(ctx, s, e, contents);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkInfo ci = getChunkInfo(ctx,true,false);
			int s = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+startCharIndex));
			String left = ci.parentContent.substring(0, s);
			String right = ci.parentContent.substring(s);
			parent.putIntoContents(ctx, new XOMString(left + contents.toTextString(ctx) + right));
		}
		else {
			super.putBeforeContents(ctx, contents);
		}
	}
	
	public void putAfterString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkInfo ci = getChunkInfo(ctx,false,true);
			int s = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+startCharIndex));
			int e = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+endCharIndex));
			p.putAfterString(ctx, s, e, contents);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkInfo ci = getChunkInfo(ctx,false,true);
			int e = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+endCharIndex));
			String left = ci.parentContent.substring(0, e);
			String right = ci.parentContent.substring(e);
			parent.putIntoContents(ctx, new XOMString(left + contents.toTextString(ctx) + right));
		}
		else {
			super.putAfterContents(ctx, contents);
		}
	}
	
	public void putIntoString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkInfo ci = getChunkInfo(ctx,true,true);
			int s = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+startCharIndex));
			int e = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+endCharIndex));
			p.putIntoString(ctx, s, e, contents, property, pvalue);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkInfo ci = getChunkInfo(ctx,true,true);
			int s = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+startCharIndex));
			int e = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+endCharIndex));
			String left = ci.parentContent.substring(0, s);
			String right = ci.parentContent.substring(e);
			parent.putIntoContents(ctx, new XOMString(left + contents.toTextString(ctx) + right), property, pvalue);
		}
		else {
			super.putIntoContents(ctx, contents, property, pvalue);
		}
	}
	
	public void putBeforeString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkInfo ci = getChunkInfo(ctx,true,false);
			int s = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+startCharIndex));
			int e = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+endCharIndex));
			p.putBeforeString(ctx, s, e, contents, property, pvalue);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkInfo ci = getChunkInfo(ctx,true,false);
			int s = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+startCharIndex));
			String left = ci.parentContent.substring(0, s);
			String right = ci.parentContent.substring(s);
			parent.putIntoContents(ctx, new XOMString(left + contents.toTextString(ctx) + right), property, pvalue);
		}
		else {
			super.putBeforeContents(ctx, contents, property, pvalue);
		}
	}
	
	public void putAfterString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkInfo ci = getChunkInfo(ctx,false,true);
			int s = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+startCharIndex));
			int e = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+endCharIndex));
			p.putAfterString(ctx, s, e, contents, property, pvalue);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkInfo ci = getChunkInfo(ctx,false,true);
			int e = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+endCharIndex));
			String left = ci.parentContent.substring(0, e);
			String right = ci.parentContent.substring(e);
			parent.putIntoContents(ctx, new XOMString(left + contents.toTextString(ctx) + right), property, pvalue);
		}
		else {
			super.putAfterContents(ctx, contents, property, pvalue);
		}
	}
	
	public boolean canSortContents(XNContext ctx) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canRearrangeString(ctx)) {
			return true;
		}
		else if (parent.canPutContents(ctx)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void sortContents(XNContext ctx, XOMComparator cmp) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canRearrangeString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkInfo ci = getChunkInfo(ctx, false, false);
			Vector<XOMVariant> v = new Vector<XOMVariant>();
			Vector<Integer> tweenStarts = new Vector<Integer>();
			Vector<Integer> tweenEnds = new Vector<Integer>();
			tweenStarts.add(0);
			for (int i = ci.startChunkIndex; i <= ci.endChunkIndex; i++) {
				XOMStringChunk ch = new XOMStringChunk(parent, chunkType, i, i);
				v.add(ch);
				StringChunkInfo chci = ch.getChunkInfo(ctx, false, false);
				tweenEnds.add(chci.startCharIndex);
				tweenStarts.add(chci.endCharIndex);
			}
			tweenEnds.add(ci.parentContent.length());
			Collections.sort(v, cmp);
			Vector<Integer> starts = new Vector<Integer>();
			Vector<Integer> ends = new Vector<Integer>();
			while ((!tweenStarts.isEmpty() && !tweenEnds.isEmpty()) || !v.isEmpty()) {
				if (!tweenStarts.isEmpty() && !tweenEnds.isEmpty()) {
					starts.add(tweenStarts.remove(0));
					ends.add(tweenEnds.remove(0));
				}
				if (!v.isEmpty()) {
					XOMStringChunk ch = (XOMStringChunk)v.remove(0);
					StringChunkInfo chci = ch.getChunkInfo(ctx, false, false);
					starts.add(chci.startCharIndex);
					ends.add(chci.endCharIndex);
				}
			}
			int[] s = new int[starts.size()];
			for (int i = 0; i < starts.size(); i++) s[i] = starts.get(i);
			int[] e = new int[ends.size()];
			for (int i = 0; i < ends.size(); i++) e[i] = ends.get(i);
			p.rearrangeString(ctx, s, e);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkInfo ci = getChunkInfo(ctx, false, false);
			Vector<XOMVariant> v = new Vector<XOMVariant>();
			Vector<String> tweens = new Vector<String>();
			int tweenStart = 0;
			for (int i = ci.startChunkIndex; i <= ci.endChunkIndex; i++) {
				XOMStringChunk ch = new XOMStringChunk(parent, chunkType, i, i);
				v.add(ch);
				StringChunkInfo chci = ch.getChunkInfo(ctx, false, false);
				tweens.add(ci.parentContent.substring(tweenStart, chci.startCharIndex));
				tweenStart = chci.endCharIndex;
			}
			tweens.add(ci.parentContent.substring(tweenStart, ci.parentContent.length()));
			Collections.sort(v, cmp);
			StringBuffer s = new StringBuffer();
			while (!tweens.isEmpty() || !v.isEmpty()) {
				if (!tweens.isEmpty()) {
					s.append(tweens.remove(0));
				}
				if (!v.isEmpty()) {
					s.append(v.remove(0).toTextString(ctx));
				}
			}
			parent.putIntoContents(ctx, new XOMString(s.toString()));
		}
		else {
			super.sortContents(ctx, cmp);
		}
	}
	
	public boolean canRearrangeString(XNContext ctx) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canRearrangeString(ctx)) {
			return true;
		}
		else if (parent.canPutContents(ctx)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void rearrangeString(XNContext ctx, int[] startCharIndex, int[] endCharIndex) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkInfo ci = getChunkInfo(ctx,false,false);
			int[] s = new int[startCharIndex.length+2];
			int[] e = new int[endCharIndex.length+2];
			s[0] = 0;
			e[0] = ci.startCharIndex;
			for (int i = 0; i < startCharIndex.length; i++)
				s[i+1] = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+startCharIndex[i]));
			for (int i = 0; i < endCharIndex.length; i++)
				e[i+1] = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+endCharIndex[i]));
			s[s.length-1] = ci.endCharIndex;
			e[e.length-1] = ci.parentContent.length();
			p.rearrangeString(ctx, s, e);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkInfo ci = getChunkInfo(ctx,false,false);
			StringBuffer s = new StringBuffer();
			s.append(ci.parentContent.substring(0, ci.startCharIndex));
			for (int i = 0; i < startCharIndex.length && i < endCharIndex.length; i++) {
				int ss = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+startCharIndex[i]));
				int ee = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+endCharIndex[i]));
				s.append(ci.parentContent.substring(ss, ee));
			}
			s.append(ci.parentContent.substring(ci.endCharIndex));
			parent.putIntoContents(ctx, new XOMString(s.toString()));
		}
		else {
			super.sortContents(ctx, null);
		}
	}
	
	public boolean canGetProperty(XNContext ctx, String property) {
		return property.equalsIgnoreCase("number") ||
		((parent instanceof XOMStringContainer) && (((XOMStringContainer)parent).canGetStringProperty(ctx, property)));
	}
	
	public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String property) {
		if (property.equalsIgnoreCase("number")) {
			StringChunkInfo ci = getChunkInfo(ctx,false,false);
			return new XOMInteger(ci.endChunkIndex - ci.startChunkIndex + 1);
		} else if (canGetProperty(ctx, property)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkInfo ci = getChunkInfo(ctx,false,false);
			return p.getStringProperty(ctx, modifier, property, ci.startCharIndex, ci.endCharIndex);
		} else {
			return super.getProperty(ctx, modifier, property);
		}
	}
	
	public boolean canGetStringProperty(XNContext ctx, String property) {
		return (parent instanceof XOMStringContainer) && (((XOMStringContainer)parent).canGetStringProperty(ctx, property));
	}
	
	public XOMVariant getStringProperty(XNContext ctx, XNModifier modifier, String property, int startCharIndex, int endCharIndex) {
		if (canGetStringProperty(ctx, property)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkInfo ci = getChunkInfo(ctx,false,false);
			int s = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+startCharIndex));
			int e = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+endCharIndex));
			return p.getStringProperty(ctx, modifier, property, s, e);
		} else {
			return super.getProperty(ctx, modifier, property);
		}
	}
	
	public boolean canSetProperty(XNContext ctx, String property) {
		return (parent instanceof XOMStringContainer) && (((XOMStringContainer)parent).canSetStringProperty(ctx, property));
	}
	
	public void setProperty(XNContext ctx, String property, XOMVariant value) {
		if (canSetProperty(ctx, property)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkInfo ci = getChunkInfo(ctx,false,false);
			p.setStringProperty(ctx, property, ci.startCharIndex, ci.endCharIndex, value);
		} else {
			super.setProperty(ctx, property, value);
		}
	}
	
	public boolean canSetStringProperty(XNContext ctx, String property) {
		return (parent instanceof XOMStringContainer) && (((XOMStringContainer)parent).canSetStringProperty(ctx, property));
	}
	
	public void setStringProperty(XNContext ctx, String property, int startCharIndex, int endCharIndex, XOMVariant value) {
		if (canSetProperty(ctx, property)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkInfo ci = getChunkInfo(ctx,false,false);
			int s = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+startCharIndex));
			int e = Math.max(ci.startCharIndex, Math.min(ci.endCharIndex, ci.startCharIndex+endCharIndex));
			p.setStringProperty(ctx, property, s, e, value);
		} else {
			super.setProperty(ctx, property, value);
		}
	}
	
	protected String toLanguageStringImpl() {
		if (startIndex == endIndex) {
			return chunkType.toString() + " " + startIndex + " of " + parent.toLanguageString();
		} else {
			return chunkType.toPluralString() + " " + startIndex + " through " + endIndex + " of " + parent.toLanguageString();
		}
	}
	protected String toTextStringImpl(XNContext ctx) {
		return getContents(ctx).toTextString(ctx);
	}
	protected List<? extends XOMVariant> toListImpl(XNContext ctx) {
		StringChunkInfo ci = getChunkInfo(ctx,false,false);
		Vector<XOMVariant> v = new Vector<XOMVariant>();
		if (ci != null) {
			for (int i = ci.startChunkIndex; i <= ci.endChunkIndex; i++) {
				v.add(new XOMStringChunk(parent, chunkType, i, i));
			}
		}
		return v;
	}
	protected int hashCodeImpl() {
		return parent.hashCode() ^ chunkType.hashCode() ^ startIndex ^ endIndex;
	}
	protected boolean equalsImpl(XOMVariant o) {
		if (o instanceof XOMStringChunk) {
			XOMStringChunk other = (XOMStringChunk)o;
			return (this.parent.equals(other.parent) && this.chunkType == other.chunkType && this.startIndex == other.startIndex && this.endIndex == other.endIndex);
		} else {
			return false;
		}
	}
}
