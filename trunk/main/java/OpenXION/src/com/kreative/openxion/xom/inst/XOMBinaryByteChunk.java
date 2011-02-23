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
import com.kreative.openxion.xom.XOMContainerObject;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMBinaryContainer;
import com.kreative.openxion.xom.XOMComparator;
import com.kreative.openxion.xom.type.XOMBinaryType;

public class XOMBinaryByteChunk extends XOMContainerObject implements XOMBinaryContainer {
	private static final long serialVersionUID = 1L;
	
	private XOMVariant parent;
	private int startIndex;
	private int endIndex;
	
	public XOMBinaryByteChunk(XOMVariant parent, int startIndex, int endIndex) {
		this.parent = parent;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}
	
	public boolean canGetParent(XNContext ctx) {
		return true;
	}
	
	public XOMVariant getParent(XNContext ctx) {
		return parent;
	}
	
	private static class BinaryChunkInfo {
		public byte[] parentContent;
		public int chunkCount;
		public int startChunkIndex;
		public int endChunkIndex;
		public int startByteIndex;
		public int endByteIndex;
	}
	
	private BinaryChunkInfo getChunkInfo(XNContext ctx, boolean puttingBefore, boolean puttingAfter) {
		byte[] data;
		if (puttingBefore || puttingAfter) {
			parent.asContainer(ctx, false);
		}
		if (parent.canGetContents(ctx)) {
			data = XOMBinaryType.instance.makeInstanceFrom(ctx, parent.getContents(ctx)).toByteArray();
		} else {
			data = XOMBinaryType.instance.makeInstanceFrom(ctx, parent).toByteArray();
		}
		if (data == null) return null;
		else {
			int[] idx = XIONUtil.index(0, data.length-1, startIndex, endIndex);
			int s = idx[0], e = idx[1];
			if ((puttingBefore && s > (data.length-1)) || (puttingAfter && e > (data.length-1))) {
				int n = ( (puttingBefore && puttingAfter) ? Math.max(s,e) : puttingBefore ? s-1 : puttingAfter ? e : (data.length-1) )-(data.length-1);
				byte[] a = new byte[n];
				data = XIONUtil.binaryConcat(data, a);
				parent.putAfterContents(ctx, new XOMBinary(a));
			}
			if ((puttingBefore && s < 0) || (puttingAfter && e < 0)) {
				int n = Math.abs( (puttingBefore && puttingAfter) ? Math.min(s,e) : puttingBefore ? s : puttingAfter ? e+1 : 0 );
				byte[] a = new byte[n];
				s += n; e += n;
				data = XIONUtil.binaryConcat(a, data);
				parent.putBeforeContents(ctx, new XOMBinary(a));
			}
			BinaryChunkInfo ci = new BinaryChunkInfo();
			ci.parentContent = data;
			ci.chunkCount = data.length;
			ci.startChunkIndex = s;
			ci.endChunkIndex = e;
			ci.startByteIndex = s;
			ci.endByteIndex = e+1;
			if (ci.startByteIndex < 0) ci.startByteIndex = 0;
			else if (ci.startByteIndex > data.length) ci.startByteIndex = data.length;
			if (ci.endByteIndex < 0) ci.endByteIndex = 0;
			else if (ci.endByteIndex > data.length) ci.endByteIndex = data.length;
			if (ci.startByteIndex > ci.endByteIndex) ci.endByteIndex = ci.startByteIndex;
			return ci;
		}
	}
	
	public boolean canDelete(XNContext ctx) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canDeleteBinary(ctx)) {
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
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canDeleteBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false);
			p.deleteBinary(ctx, ci.startByteIndex, ci.endByteIndex);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false);
			byte[] left = XIONUtil.binarySubstring(ci.parentContent, 0, ci.startByteIndex);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent, ci.endByteIndex, ci.chunkCount);
			parent.putIntoContents(ctx, new XOMBinary(XIONUtil.binaryConcat(left, right)));
		}
		else {
			super.delete(ctx);
		}
	}
	
	public boolean canDeleteBinary(XNContext ctx) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canDeleteBinary(ctx)) {
			return true;
		}
		else if (parent.canPutContents(ctx)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void deleteBinary(XNContext ctx, int startByteIndex, int endByteIndex) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canDeleteBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false);
			int s = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+startByteIndex));
			int e = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+endByteIndex));
			p.deleteBinary(ctx, s, e);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false);
			int s = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+startByteIndex));
			int e = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+endByteIndex));
			byte[] left = XIONUtil.binarySubstring(ci.parentContent, 0, s);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent, e, ci.chunkCount);
			parent.putIntoContents(ctx, new XOMBinary(XIONUtil.binaryConcat(left, right)));
		}
		else {
			super.delete(ctx);
		}
	}
	
	public boolean canGetContents(XNContext ctx) {
		return true;
	}
	
	public XOMVariant getContents(XNContext ctx) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canGetBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false);
			return p.getBinary(ctx, ci.startByteIndex, ci.endByteIndex);
		}
		else {
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false);
			return new XOMBinary(XIONUtil.binarySubstring(ci.parentContent, ci.startByteIndex, ci.endByteIndex));
		}
	}
	
	public boolean canGetBinary(XNContext ctx) {
		return true;
	}
	
	public XOMVariant getBinary(XNContext ctx, int startByteIndex, int endByteIndex) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canGetBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false);
			int s = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+startByteIndex));
			int e = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+endByteIndex));
			return p.getBinary(ctx, s, e);
		}
		else {
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false);
			int s = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+startByteIndex));
			int e = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+endByteIndex));
			return new XOMBinary(XIONUtil.binarySubstring(ci.parentContent, s, e));
		}
	}
	
	public boolean canPutContents(XNContext ctx) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canPutBinary(ctx)) {
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
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canPutBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, true, true);
			p.putIntoBinary(ctx, ci.startByteIndex, ci.endByteIndex, contents);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, true, true);
			byte[] left = XIONUtil.binarySubstring(ci.parentContent, 0, ci.startByteIndex);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent, ci.endByteIndex, ci.chunkCount);
			parent.putIntoContents(ctx, new XOMBinary(XIONUtil.binaryConcat(left, XOMBinaryType.instance.makeInstanceFrom(ctx, contents).toByteArray(), right)));
		}
		else {
			super.putIntoContents(ctx, contents);
		}
	}
	
	public void putBeforeContents(XNContext ctx, XOMVariant contents) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canPutBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, true, false);
			p.putBeforeBinary(ctx, ci.startByteIndex, ci.endByteIndex, contents);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, true, false);
			byte[] left = XIONUtil.binarySubstring(ci.parentContent,0, ci.startByteIndex);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent,ci.startByteIndex, ci.chunkCount);
			parent.putIntoContents(ctx, new XOMBinary(XIONUtil.binaryConcat(left, XOMBinaryType.instance.makeInstanceFrom(ctx, contents).toByteArray(), right)));
		}
		else {
			super.putBeforeContents(ctx, contents);
		}
	}
	
	public void putAfterContents(XNContext ctx, XOMVariant contents) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canPutBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, false, true);
			p.putAfterBinary(ctx, ci.startByteIndex, ci.endByteIndex, contents);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, false, true);
			byte[] left = XIONUtil.binarySubstring(ci.parentContent,0, ci.endByteIndex);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent,ci.endByteIndex, ci.chunkCount);
			parent.putIntoContents(ctx, new XOMBinary(XIONUtil.binaryConcat(left, XOMBinaryType.instance.makeInstanceFrom(ctx, contents).toByteArray(), right)));
		}
		else {
			super.putAfterContents(ctx, contents);
		}
	}
	
	public void putIntoContents(XNContext ctx, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canPutBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, true, true);
			p.putIntoBinary(ctx, ci.startByteIndex, ci.endByteIndex, contents, property, pvalue);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, true, true);
			byte[] left = XIONUtil.binarySubstring(ci.parentContent, 0, ci.startByteIndex);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent, ci.endByteIndex, ci.chunkCount);
			parent.putIntoContents(ctx, new XOMBinary(XIONUtil.binaryConcat(left, XOMBinaryType.instance.makeInstanceFrom(ctx, contents).toByteArray(), right)), property, pvalue);
		}
		else {
			super.putIntoContents(ctx, contents, property, pvalue);
		}
	}
	
	public void putBeforeContents(XNContext ctx, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canPutBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, true, false);
			p.putBeforeBinary(ctx, ci.startByteIndex, ci.endByteIndex, contents, property, pvalue);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, true, false);
			byte[] left = XIONUtil.binarySubstring(ci.parentContent,0, ci.startByteIndex);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent,ci.startByteIndex, ci.chunkCount);
			parent.putIntoContents(ctx, new XOMBinary(XIONUtil.binaryConcat(left, XOMBinaryType.instance.makeInstanceFrom(ctx, contents).toByteArray(), right)), property, pvalue);
		}
		else {
			super.putBeforeContents(ctx, contents, property, pvalue);
		}
	}
	
	public void putAfterContents(XNContext ctx, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canPutBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, false, true);
			p.putAfterBinary(ctx, ci.startByteIndex, ci.endByteIndex, contents, property, pvalue);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, false, true);
			byte[] left = XIONUtil.binarySubstring(ci.parentContent,0, ci.endByteIndex);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent,ci.endByteIndex, ci.chunkCount);
			parent.putIntoContents(ctx, new XOMBinary(XIONUtil.binaryConcat(left, XOMBinaryType.instance.makeInstanceFrom(ctx, contents).toByteArray(), right)), property, pvalue);
		}
		else {
			super.putAfterContents(ctx, contents, property, pvalue);
		}
	}
	
	public boolean canPutBinary(XNContext ctx) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canPutBinary(ctx)) {
			return true;
		}
		else if (parent.canPutContents(ctx)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void putIntoBinary(XNContext ctx, int startByteIndex, int endByteIndex, XOMVariant contents) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canPutBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, true, true);
			int s = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+startByteIndex));
			int e = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+endByteIndex));
			p.putIntoBinary(ctx, s, e, contents);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, true, true);
			int s = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+startByteIndex));
			int e = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+endByteIndex));
			byte[] left = XIONUtil.binarySubstring(ci.parentContent,0, s);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent,e, ci.chunkCount);
			parent.putIntoContents(ctx, new XOMBinary(XIONUtil.binaryConcat(left, XOMBinaryType.instance.makeInstanceFrom(ctx, contents).toByteArray(), right)));
		}
		else {
			super.putIntoContents(ctx, contents);
		}
	}
	
	public void putBeforeBinary(XNContext ctx, int startByteIndex, int endByteIndex, XOMVariant contents) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canPutBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, true, false);
			int s = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+startByteIndex));
			int e = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+endByteIndex));
			p.putBeforeBinary(ctx, s, e, contents);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, true, false);
			int s = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+startByteIndex));
			byte[] left = XIONUtil.binarySubstring(ci.parentContent,0, s);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent,s, ci.chunkCount);
			parent.putIntoContents(ctx, new XOMBinary(XIONUtil.binaryConcat(left, XOMBinaryType.instance.makeInstanceFrom(ctx, contents).toByteArray(), right)));
		}
		else {
			super.putBeforeContents(ctx, contents);
		}
	}
	
	public void putAfterBinary(XNContext ctx, int startByteIndex, int endByteIndex, XOMVariant contents) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canPutBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, false, true);
			int s = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+startByteIndex));
			int e = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+endByteIndex));
			p.putAfterBinary(ctx, s, e, contents);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, false, true);
			int e = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+endByteIndex));
			byte[] left = XIONUtil.binarySubstring(ci.parentContent, 0, e);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent, e, ci.chunkCount);
			parent.putIntoContents(ctx, new XOMBinary(XIONUtil.binaryConcat(left, XOMBinaryType.instance.makeInstanceFrom(ctx, contents).toByteArray(), right)));
		}
		else {
			super.putAfterContents(ctx, contents);
		}
	}
	
	public void putIntoBinary(XNContext ctx, int startByteIndex, int endByteIndex, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canPutBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, true, true);
			int s = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+startByteIndex));
			int e = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+endByteIndex));
			p.putIntoBinary(ctx, s, e, contents, property, pvalue);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, true, true);
			int s = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+startByteIndex));
			int e = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+endByteIndex));
			byte[] left = XIONUtil.binarySubstring(ci.parentContent,0, s);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent,e, ci.chunkCount);
			parent.putIntoContents(ctx, new XOMBinary(XIONUtil.binaryConcat(left, XOMBinaryType.instance.makeInstanceFrom(ctx, contents).toByteArray(), right)), property, pvalue);
		}
		else {
			super.putIntoContents(ctx, contents, property, pvalue);
		}
	}
	
	public void putBeforeBinary(XNContext ctx, int startByteIndex, int endByteIndex, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canPutBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, true, false);
			int s = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+startByteIndex));
			int e = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+endByteIndex));
			p.putBeforeBinary(ctx, s, e, contents, property, pvalue);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, true, false);
			int s = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+startByteIndex));
			byte[] left = XIONUtil.binarySubstring(ci.parentContent,0, s);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent,s, ci.chunkCount);
			parent.putIntoContents(ctx, new XOMBinary(XIONUtil.binaryConcat(left, XOMBinaryType.instance.makeInstanceFrom(ctx, contents).toByteArray(), right)), property, pvalue);
		}
		else {
			super.putBeforeContents(ctx, contents, property, pvalue);
		}
	}
	
	public void putAfterBinary(XNContext ctx, int startByteIndex, int endByteIndex, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canPutBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, false, true);
			int s = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+startByteIndex));
			int e = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+endByteIndex));
			p.putAfterBinary(ctx, s, e, contents, property, pvalue);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, false, true);
			int e = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+endByteIndex));
			byte[] left = XIONUtil.binarySubstring(ci.parentContent, 0, e);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent, e, ci.chunkCount);
			parent.putIntoContents(ctx, new XOMBinary(XIONUtil.binaryConcat(left, XOMBinaryType.instance.makeInstanceFrom(ctx, contents).toByteArray(), right)), property, pvalue);
		}
		else {
			super.putAfterContents(ctx, contents, property, pvalue);
		}
	}
	
	public boolean canSortContents(XNContext ctx) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canSortBinary(ctx)) {
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
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canSortBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false);
			p.sortBinary(ctx, ci.startByteIndex, ci.endByteIndex, cmp);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false);
			byte[] left = XIONUtil.binarySubstring(ci.parentContent, 0, ci.startByteIndex);
			byte[] middle = XIONUtil.binarySubstring(ci.parentContent, ci.startByteIndex, ci.endByteIndex);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent, ci.endByteIndex, ci.chunkCount);
			Vector<XOMVariant> middlev = new Vector<XOMVariant>();
			for (byte b : middle) middlev.add(new XOMBinary(new byte[]{b}));
			Collections.sort(middlev, cmp);
			for (int i = 0; i < middle.length; i++) middle[i] = ((XOMBinary)middlev.get(i)).toByteArray()[0];
			parent.putIntoContents(ctx, new XOMBinary(XIONUtil.binaryConcat(left, middle, right)));
		}
		else {
			super.sortContents(ctx, cmp);
		}
	}
	
	public boolean canSortBinary(XNContext ctx) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canSortBinary(ctx)) {
			return true;
		}
		else if (parent.canPutContents(ctx)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void sortBinary(XNContext ctx, int startByteIndex, int endByteIndex, XOMComparator cmp) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canSortBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false);
			int s = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+startByteIndex));
			int e = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+endByteIndex));
			p.sortBinary(ctx, s, e, cmp);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false);
			int s = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+startByteIndex));
			int e = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+endByteIndex));
			byte[] left = XIONUtil.binarySubstring(ci.parentContent, 0, s);
			byte[] middle = XIONUtil.binarySubstring(ci.parentContent, s, e);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent, e, ci.chunkCount);
			Vector<XOMVariant> middlev = new Vector<XOMVariant>();
			for (byte b : middle) middlev.add(new XOMBinary(new byte[]{b}));
			Collections.sort(middlev, cmp);
			for (int i = 0; i < middle.length; i++) middle[i] = ((XOMBinary)middlev.get(i)).toByteArray()[0];
			parent.putIntoContents(ctx, new XOMBinary(XIONUtil.binaryConcat(left, middle, right)));
		}
		else {
			super.sortContents(ctx, cmp);
		}
	}
	
	public boolean canGetProperty(XNContext ctx, String property) {
		return (property.equalsIgnoreCase("number")) ||
		((parent instanceof XOMBinaryContainer) && (((XOMBinaryContainer)parent).canGetBinaryProperty(ctx, property)));
	}
	
	public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String property) {
		if (property.equalsIgnoreCase("number")) {
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false);
			return new XOMInteger(ci.endChunkIndex - ci.startChunkIndex + 1);
		} if (canGetProperty(ctx, property)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false);
			return p.getBinaryProperty(ctx, modifier, property, ci.startByteIndex, ci.endByteIndex);
		} else {
			return super.getProperty(ctx, modifier, property);
		}
	}
	
	public boolean canGetBinaryProperty(XNContext ctx, String property) {
		return (parent instanceof XOMBinaryContainer) && (((XOMBinaryContainer)parent).canGetBinaryProperty(ctx, property));
	}
	
	public XOMVariant getBinaryProperty(XNContext ctx, XNModifier modifier, String property, int startByteIndex, int endByteIndex) {
		if (canGetBinaryProperty(ctx, property)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false);
			int s = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+startByteIndex));
			int e = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+endByteIndex));
			return p.getBinaryProperty(ctx, modifier, property, s, e);
		} else {
			return super.getProperty(ctx, modifier, property);
		}
	}
	
	public boolean canSetProperty(XNContext ctx, String property) {
		return (parent instanceof XOMBinaryContainer) && (((XOMBinaryContainer)parent).canSetBinaryProperty(ctx, property));
	}
	
	public void setProperty(XNContext ctx, String property, XOMVariant value) {
		if (canSetProperty(ctx, property)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false);
			p.setBinaryProperty(ctx, property, ci.startByteIndex, ci.endByteIndex, value);
		} else {
			super.setProperty(ctx, property, value);
		}
	}
	
	public boolean canSetBinaryProperty(XNContext ctx, String property) {
		return (parent instanceof XOMBinaryContainer) && (((XOMBinaryContainer)parent).canSetBinaryProperty(ctx, property));
	}
	
	public void setBinaryProperty(XNContext ctx, String property, int startByteIndex, int endByteIndex, XOMVariant value) {
		if (canSetProperty(ctx, property)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false);
			int s = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+startByteIndex));
			int e = Math.max(ci.startByteIndex, Math.min(ci.endByteIndex, ci.startByteIndex+endByteIndex));
			p.setBinaryProperty(ctx, property, s, e, value);
		} else {
			super.setProperty(ctx, property, value);
		}
	}
	
	protected String toLanguageStringImpl() {
		if (startIndex == endIndex) {
			return "byte " + startIndex + " of " + parent.toLanguageString();
		} else {
			return "bytes " + startIndex + " through " + endIndex + " of " + parent.toLanguageString();
		}
	}
	protected String toTextStringImpl(XNContext ctx) {
		return getContents(ctx).toTextString(ctx);
	}
	protected List<? extends XOMVariant> toListImpl(XNContext ctx) {
		BinaryChunkInfo ci = getChunkInfo(ctx, false, false);
		Vector<XOMVariant> v = new Vector<XOMVariant>();
		if (ci != null) {
			for (int i = ci.startChunkIndex; i <= ci.endChunkIndex; i++) {
				v.add(new XOMBinaryByteChunk(parent, i, i));
			}
		}
		return v;
	}
	protected int hashCodeImpl() {
		return parent.hashCode() ^ startIndex ^ endIndex;
	}
	protected boolean equalsImpl(XOMVariant o) {
		if (o instanceof XOMBinaryByteChunk) {
			XOMBinaryByteChunk other = (XOMBinaryByteChunk)o;
			return (this.parent.equals(other.parent) && this.startIndex == other.startIndex && this.endIndex == other.endIndex);
		} else {
			return false;
		}
	}
}
