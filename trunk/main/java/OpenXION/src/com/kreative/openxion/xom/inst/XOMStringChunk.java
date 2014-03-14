/*
 * Copyright &copy; 2009-2014 Rebecca G. Bettencourt / Kreative Software
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
import com.kreative.openxion.util.StringChunkDefinition;
import com.kreative.openxion.util.StringChunkType;
import com.kreative.openxion.xom.XOMContainer;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMStringContainer;
import com.kreative.openxion.xom.XOMComparator;

public class XOMStringChunk extends XOMContainer implements XOMStringContainer {
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
	
	private StringChunkDefinition.ChunkInfo getChunkInfo(XNContext ctx, boolean puttingBefore, boolean puttingAfter) {
		String ts;
		if (puttingBefore || puttingAfter) {
			ts = (parent = parent.asContainer(ctx, false)).getContents(ctx).toTextString(ctx);
		} else if (parent.canGetContents(ctx)) {
			ts = parent.getContents(ctx).toTextString(ctx);
		} else {
			ts = parent.toTextString(ctx);
		}
		String nl = ctx.getLineEnding();
		String id = Character.toString(ctx.getItemDelimiter());
		String cd = Character.toString(ctx.getColumnDelimiter());
		String rd = Character.toString(ctx.getRowDelimiter());
		StringChunkDefinition def = chunkType.getDefinition(nl, id, cd, rd);
		StringChunkDefinition.ChunkInfo ci = def.resolveChunk(ts, 0, ts.length(), startIndex, endIndex, puttingBefore, puttingAfter);
		if (ci.getStringToAppend() != null) parent.putAfterContents(ctx, new XOMString(ci.getStringToAppend()));
		if (ci.getStringToPrepend() != null) parent.putBeforeContents(ctx, new XOMString(ci.getStringToPrepend()));
		return ci;
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
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,false);
			p.deleteString(ctx, ci.getStartIndex(), ci.getDeleteEndIndex());
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,false);
			String left = ci.getSourceString().substring(0, ci.getStartIndex());
			String right = ci.getSourceString().substring(ci.getDeleteEndIndex());
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
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,false);
			int s = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+startCharIndex));
			int e = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+endCharIndex));
			p.deleteString(ctx, s, e);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,false);
			int s = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+startCharIndex));
			int e = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+endCharIndex));
			String left = ci.getSourceString().substring(0, s);
			String right = ci.getSourceString().substring(e);
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
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,false);
			return p.getString(ctx, ci.getStartIndex(), ci.getEndIndex());
		}
		else {
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,false);
			return new XOMString(ci.getContent());
		}
	}
	
	public boolean canGetString(XNContext ctx) {
		return true;
	}
	
	public XOMVariant getString(XNContext ctx, int startCharIndex, int endCharIndex) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canGetString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,false);
			int s = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+startCharIndex));
			int e = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+endCharIndex));
			return p.getString(ctx, s, e);
		}
		else {
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,false);
			int s = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+startCharIndex));
			int e = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+endCharIndex));
			return new XOMString(ci.getSourceString().substring(s, e));
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
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,true,true);
			p.putIntoString(ctx, ci.getStartIndex(), ci.getEndIndex(), contents);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,true,true);
			String left = ci.getSourceString().substring(0, ci.getStartIndex());
			String right = ci.getSourceString().substring(ci.getEndIndex());
			parent.putIntoContents(ctx, new XOMString(left + contents.toTextString(ctx) + right));
		}
		else {
			super.putIntoContents(ctx, contents);
		}
	}
	
	public void putBeforeContents(XNContext ctx, XOMVariant contents) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,true,false);
			p.putBeforeString(ctx, ci.getStartIndex(), ci.getEndIndex(), contents);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,true,false);
			String left = ci.getSourceString().substring(0, ci.getStartIndex());
			String right = ci.getSourceString().substring(ci.getStartIndex());
			parent.putIntoContents(ctx, new XOMString(left + contents.toTextString(ctx) + right));
		}
		else {
			super.putBeforeContents(ctx, contents);
		}
	}
	
	public void putAfterContents(XNContext ctx, XOMVariant contents) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,true);
			p.putAfterString(ctx, ci.getStartIndex(), ci.getEndIndex(), contents);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,true);
			String left = ci.getSourceString().substring(0, ci.getEndIndex());
			String right = ci.getSourceString().substring(ci.getEndIndex());
			parent.putIntoContents(ctx, new XOMString(left + contents.toTextString(ctx) + right));
		}
		else {
			super.putAfterContents(ctx, contents);
		}
	}
	
	public void putIntoContents(XNContext ctx, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,true,true);
			p.putIntoString(ctx, ci.getStartIndex(), ci.getEndIndex(), contents, property, pvalue);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,true,true);
			String left = ci.getSourceString().substring(0, ci.getStartIndex());
			String right = ci.getSourceString().substring(ci.getEndIndex());
			parent.putIntoContents(ctx, new XOMString(left + contents.toTextString(ctx) + right), property, pvalue);
		}
		else {
			super.putIntoContents(ctx, contents, property, pvalue);
		}
	}
	
	public void putBeforeContents(XNContext ctx, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,true,false);
			p.putBeforeString(ctx, ci.getStartIndex(), ci.getEndIndex(), contents, property, pvalue);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,true,false);
			String left = ci.getSourceString().substring(0, ci.getStartIndex());
			String right = ci.getSourceString().substring(ci.getStartIndex());
			parent.putIntoContents(ctx, new XOMString(left + contents.toTextString(ctx) + right), property, pvalue);
		}
		else {
			super.putBeforeContents(ctx, contents, property, pvalue);
		}
	}
	
	public void putAfterContents(XNContext ctx, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,true);
			p.putAfterString(ctx, ci.getStartIndex(), ci.getEndIndex(), contents, property, pvalue);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx, false,true);
			String left = ci.getSourceString().substring(0, ci.getEndIndex());
			String right = ci.getSourceString().substring(ci.getEndIndex());
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
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,true,true);
			int s = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+startCharIndex));
			int e = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+endCharIndex));
			p.putIntoString(ctx, s, e, contents);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,true,true);
			int s = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+startCharIndex));
			int e = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+endCharIndex));
			String left = ci.getSourceString().substring(0, s);
			String right = ci.getSourceString().substring(e);
			parent.putIntoContents(ctx, new XOMString(left + contents.toTextString(ctx) + right));
		}
		else {
			super.putIntoContents(ctx, contents);
		}
	}
	
	public void putBeforeString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,true,false);
			int s = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+startCharIndex));
			int e = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+endCharIndex));
			p.putBeforeString(ctx, s, e, contents);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,true,false);
			int s = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+startCharIndex));
			String left = ci.getSourceString().substring(0, s);
			String right = ci.getSourceString().substring(s);
			parent.putIntoContents(ctx, new XOMString(left + contents.toTextString(ctx) + right));
		}
		else {
			super.putBeforeContents(ctx, contents);
		}
	}
	
	public void putAfterString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,true);
			int s = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+startCharIndex));
			int e = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+endCharIndex));
			p.putAfterString(ctx, s, e, contents);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,true);
			int e = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+endCharIndex));
			String left = ci.getSourceString().substring(0, e);
			String right = ci.getSourceString().substring(e);
			parent.putIntoContents(ctx, new XOMString(left + contents.toTextString(ctx) + right));
		}
		else {
			super.putAfterContents(ctx, contents);
		}
	}
	
	public void putIntoString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,true,true);
			int s = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+startCharIndex));
			int e = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+endCharIndex));
			p.putIntoString(ctx, s, e, contents, property, pvalue);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,true,true);
			int s = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+startCharIndex));
			int e = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+endCharIndex));
			String left = ci.getSourceString().substring(0, s);
			String right = ci.getSourceString().substring(e);
			parent.putIntoContents(ctx, new XOMString(left + contents.toTextString(ctx) + right), property, pvalue);
		}
		else {
			super.putIntoContents(ctx, contents, property, pvalue);
		}
	}
	
	public void putBeforeString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,true,false);
			int s = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+startCharIndex));
			int e = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+endCharIndex));
			p.putBeforeString(ctx, s, e, contents, property, pvalue);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,true,false);
			int s = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+startCharIndex));
			String left = ci.getSourceString().substring(0, s);
			String right = ci.getSourceString().substring(s);
			parent.putIntoContents(ctx, new XOMString(left + contents.toTextString(ctx) + right), property, pvalue);
		}
		else {
			super.putBeforeContents(ctx, contents, property, pvalue);
		}
	}
	
	public void putAfterString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMStringContainer && ((XOMStringContainer)parent).canPutString(ctx)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,true);
			int s = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+startCharIndex));
			int e = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+endCharIndex));
			p.putAfterString(ctx, s, e, contents, property, pvalue);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,true);
			int e = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+endCharIndex));
			String left = ci.getSourceString().substring(0, e);
			String right = ci.getSourceString().substring(e);
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
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx, false, false);
			Vector<XOMVariant> v = new Vector<XOMVariant>();
			Vector<Integer> tweenStarts = new Vector<Integer>();
			Vector<Integer> tweenEnds = new Vector<Integer>();
			tweenStarts.add(0);
			for (int i = ci.getFirstChunkIndex(); i <= ci.getLastChunkIndex(); i++) {
				XOMStringChunk ch = new XOMStringChunk(parent, chunkType, i, i);
				v.add(ch);
				StringChunkDefinition.ChunkInfo chci = ch.getChunkInfo(ctx, false, false);
				tweenEnds.add(chci.getStartIndex());
				tweenStarts.add(chci.getEndIndex());
			}
			tweenEnds.add(ci.getSourceString().length());
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
					StringChunkDefinition.ChunkInfo chci = ch.getChunkInfo(ctx, false, false);
					starts.add(chci.getStartIndex());
					ends.add(chci.getEndIndex());
				}
			}
			int[] s = new int[starts.size()];
			for (int i = 0; i < starts.size(); i++) s[i] = starts.get(i);
			int[] e = new int[ends.size()];
			for (int i = 0; i < ends.size(); i++) e[i] = ends.get(i);
			p.rearrangeString(ctx, s, e);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx, false, false);
			Vector<XOMVariant> v = new Vector<XOMVariant>();
			Vector<String> tweens = new Vector<String>();
			int tweenStart = 0;
			for (int i = ci.getFirstChunkIndex(); i <= ci.getLastChunkIndex(); i++) {
				XOMStringChunk ch = new XOMStringChunk(parent, chunkType, i, i);
				v.add(ch);
				StringChunkDefinition.ChunkInfo chci = ch.getChunkInfo(ctx, false, false);
				tweens.add(ci.getSourceString().substring(tweenStart, chci.getStartIndex()));
				tweenStart = chci.getEndIndex();
			}
			tweens.add(ci.getSourceString().substring(tweenStart, ci.getSourceString().length()));
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
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,false);
			int[] s = new int[startCharIndex.length+2];
			int[] e = new int[endCharIndex.length+2];
			s[0] = 0;
			e[0] = ci.getStartIndex();
			for (int i = 0; i < startCharIndex.length; i++)
				s[i+1] = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+startCharIndex[i]));
			for (int i = 0; i < endCharIndex.length; i++)
				e[i+1] = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+endCharIndex[i]));
			s[s.length-1] = ci.getEndIndex();
			e[e.length-1] = ci.getSourceString().length();
			p.rearrangeString(ctx, s, e);
		}
		else if (parent.canPutContents(ctx)) {
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,false);
			StringBuffer s = new StringBuffer();
			s.append(ci.getSourceString().substring(0, ci.getStartIndex()));
			for (int i = 0; i < startCharIndex.length && i < endCharIndex.length; i++) {
				int ss = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+startCharIndex[i]));
				int ee = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+endCharIndex[i]));
				s.append(ci.getSourceString().substring(ss, ee));
			}
			s.append(ci.getSourceString().substring(ci.getEndIndex()));
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
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,false);
			return new XOMInteger(ci.getLastChunkIndex() - ci.getFirstChunkIndex() + 1);
		} else if (canGetProperty(ctx, property)) {
			XOMStringContainer p = (XOMStringContainer)parent;
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,false);
			return p.getStringProperty(ctx, modifier, property, ci.getStartIndex(), ci.getEndIndex());
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
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,false);
			int s = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+startCharIndex));
			int e = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+endCharIndex));
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
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,false);
			p.setStringProperty(ctx, property, ci.getStartIndex(), ci.getEndIndex(), value);
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
			StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,false);
			int s = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+startCharIndex));
			int e = Math.max(ci.getStartIndex(), Math.min(ci.getEndIndex(), ci.getStartIndex()+endCharIndex));
			p.setStringProperty(ctx, property, s, e, value);
		} else {
			super.setProperty(ctx, property, value);
		}
	}
	
	public String toLanguageString() {
		if (startIndex == endIndex) {
			return chunkType.toString() + " " + startIndex + " of " + parent.toLanguageString();
		} else {
			return chunkType.toPluralString() + " " + startIndex + " through " + endIndex + " of " + parent.toLanguageString();
		}
	}
	public String toTextString(XNContext ctx) {
		return getContents(ctx).toTextString(ctx);
	}
	public List<? extends XOMVariant> toVariantList(XNContext ctx) {
		StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,false);
		Vector<XOMVariant> v = new Vector<XOMVariant>();
		if (ci != null) {
			for (int i = ci.getFirstChunkIndex(); i <= ci.getLastChunkIndex(); i++) {
				v.add(new XOMStringChunk(parent, chunkType, i, i));
			}
		}
		return v;
	}
	public List<? extends XOMVariant> toPrimitiveList(XNContext ctx) {
		StringChunkDefinition.ChunkInfo ci = getChunkInfo(ctx,false,false);
		Vector<XOMVariant> v = new Vector<XOMVariant>();
		if (ci != null) {
			for (int i = ci.getFirstChunkIndex(); i <= ci.getLastChunkIndex(); i++) {
				v.add(new XOMString(new XOMStringChunk(parent, chunkType, i, i).toTextString(ctx)));
			}
		}
		return v;
	}
	public int hashCode() {
		return parent.hashCode() ^ chunkType.hashCode() ^ startIndex ^ endIndex;
	}
	public boolean equals(Object o) {
		if (o instanceof XOMStringChunk) {
			XOMStringChunk other = (XOMStringChunk)o;
			return (this.parent.equals(other.parent) && this.chunkType == other.chunkType && this.startIndex == other.startIndex && this.endIndex == other.endIndex);
		} else {
			return false;
		}
	}
}
