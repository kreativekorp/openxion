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

import java.util.Arrays;
import java.util.List;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.ast.XNModifier;
import com.kreative.openxion.util.XIONUtil;
import com.kreative.openxion.util.BinaryNumericChunkType;
import com.kreative.openxion.xom.XOMContainer;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMBinaryContainer;
import com.kreative.openxion.xom.type.XOMBinaryType;
import com.kreative.openxion.xom.type.XOMNumberType;
import com.kreative.openxion.xom.type.XOMIntegerType;

public class XOMBinaryNumericChunk extends XOMContainer {
	private static final long serialVersionUID = 1L;
	
	private XOMVariant parent;
	private BinaryNumericChunkType chunkType;
	private int index;
	
	public XOMBinaryNumericChunk(XOMVariant parent, BinaryNumericChunkType chunkType, int index) {
		this.parent = parent;
		this.chunkType = chunkType;
		this.index = index;
	}
	
	public boolean canGetParent(XNContext ctx) {
		return true;
	}
	
	public XOMVariant getParent(XNContext ctx) {
		return parent;
	}
	
	private static class BinaryChunkInfo {
		public byte[] parentContent;
		public int byteCount;
		public int startByteIndex;
		public int endByteIndex;
	}
	
	private BinaryChunkInfo getChunkInfo(XNContext ctx, boolean puttingBefore, boolean puttingAfter, boolean padding) {
		byte[] data;
		if (puttingBefore || puttingAfter) {
			data = XOMBinaryType.instance.makeInstanceFrom(ctx, (parent = parent.asContainer(ctx, false)).getContents(ctx)).toByteArray();
		} else if (parent.canGetContents(ctx)) {
			data = XOMBinaryType.instance.makeInstanceFrom(ctx, parent.getContents(ctx)).toByteArray();
		} else {
			data = XOMBinaryType.instance.makeInstanceFrom(ctx, parent).toByteArray();
		}
		int[] idx = XIONUtil.index(0, data.length-chunkType.length(), index, index);
		int s = idx[0], e = idx[0]+chunkType.length()-1;
		if ((puttingBefore && s > (data.length-1)) || (puttingAfter && e > (data.length-1))) {
			int n = ( (puttingBefore && puttingAfter) ? Math.max(s,e) : puttingBefore ? s-1 : puttingAfter ? e : (data.length-1) )-(data.length-1);
			byte[] a = new byte[n];
			data = XIONUtil.binaryConcat(data, a);
			parent.putAfterContents(ctx, new XOMBinary(a));
		}
		if (padding && (s > (data.length-1) || e > (data.length-1))) {
			int n = Math.max(s,e) - (data.length-1);
			byte[] a = new byte[n];
			data = XIONUtil.binaryConcat(data, a);
		}
		if ((puttingBefore && s < 0) || (puttingAfter && e < 0)) {
			int n = Math.abs( (puttingBefore && puttingAfter) ? Math.min(s,e) : puttingBefore ? s : puttingAfter ? e+1 : 0 );
			byte[] a = new byte[n];
			s += n; e += n;
			data = XIONUtil.binaryConcat(a, data);
			parent.putBeforeContents(ctx, new XOMBinary(a));
		}
		if (padding && (s < 0 || e < 0)) {
			int n = Math.abs(Math.min(s,e));
			byte[] a = new byte[n];
			s += n; e += n;
			data = XIONUtil.binaryConcat(a, data);
		}
		BinaryChunkInfo ci = new BinaryChunkInfo();
		ci.parentContent = data;
		ci.byteCount = data.length;
		ci.startByteIndex = s;
		ci.endByteIndex = e+1;
		if (ci.startByteIndex < 0) ci.startByteIndex = 0;
		else if (ci.startByteIndex > data.length) ci.startByteIndex = data.length;
		if (ci.endByteIndex < 0) ci.endByteIndex = 0;
		else if (ci.endByteIndex > data.length) ci.endByteIndex = data.length;
		if (ci.startByteIndex > ci.endByteIndex) ci.endByteIndex = ci.startByteIndex;
		return ci;
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
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false, false);
			p.deleteBinary(ctx, ci.startByteIndex, ci.endByteIndex);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false, false);
			byte[] left = XIONUtil.binarySubstring(ci.parentContent, 0, ci.startByteIndex);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent, ci.endByteIndex, ci.byteCount);
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
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false, true);
			XOMVariant bin = p.getBinary(ctx, ci.startByteIndex, ci.endByteIndex);
			if (chunkType.isFloat()) {
				return new XOMNumber(chunkType.bigDecimalValueOf(XOMBinaryType.instance.makeInstanceFrom(ctx, bin).toByteArray(), ctx.getUnsigned(), ctx.getLittleEndian()));
			} else {
				return new XOMInteger(chunkType.bigIntegerValueOf(XOMBinaryType.instance.makeInstanceFrom(ctx, bin).toByteArray(), ctx.getUnsigned(), ctx.getLittleEndian()));
			}
		}
		else {
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false, true);
			byte[] b = XIONUtil.binarySubstring(ci.parentContent, ci.startByteIndex, ci.endByteIndex);
			if (chunkType.isFloat()) {
				return new XOMNumber(chunkType.bigDecimalValueOf(b, ctx.getUnsigned(), ctx.getLittleEndian()));
			} else {
				return new XOMInteger(chunkType.bigIntegerValueOf(b, ctx.getUnsigned(), ctx.getLittleEndian()));
			}
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
		if (chunkType.isFloat()) {
			if (XOMNumberType.instance.canMakeInstanceFrom(ctx, contents)) {
				double d = XOMNumberType.instance.makeInstanceFrom(ctx, contents).toDouble();
				contents = new XOMBinary(chunkType.byteArrayValueOf(d, ctx.getLittleEndian()));
			}
		} else {
			if (XOMIntegerType.instance.canMakeInstanceFrom(ctx, contents)) {
				long l = XOMIntegerType.instance.makeInstanceFrom(ctx, contents).toLong();
				contents = new XOMBinary(chunkType.byteArrayValueOf(l, ctx.getLittleEndian()));
			}
		}
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canPutBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, true, true, false);
			p.putIntoBinary(ctx, ci.startByteIndex, ci.endByteIndex, contents);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, true, true, false);
			byte[] left = XIONUtil.binarySubstring(ci.parentContent, 0, ci.startByteIndex);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent, ci.endByteIndex, ci.byteCount);
			parent.putIntoContents(ctx, new XOMBinary(XIONUtil.binaryConcat(left, XOMBinaryType.instance.makeInstanceFrom(ctx, contents).toByteArray(), right)));
		}
		else {
			super.putIntoContents(ctx, contents);
		}
	}
	
	public void putBeforeContents(XNContext ctx, XOMVariant contents) {
		if (chunkType.isFloat()) {
			if (XOMNumberType.instance.canMakeInstanceFrom(ctx, contents)) {
				double d = XOMNumberType.instance.makeInstanceFrom(ctx, contents).toDouble();
				contents = new XOMBinary(chunkType.byteArrayValueOf(d, ctx.getLittleEndian()));
			}
		} else {
			if (XOMIntegerType.instance.canMakeInstanceFrom(ctx, contents)) {
				long l = XOMIntegerType.instance.makeInstanceFrom(ctx, contents).toLong();
				contents = new XOMBinary(chunkType.byteArrayValueOf(l, ctx.getLittleEndian()));
			}
		}
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canPutBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, true, false, false);
			p.putBeforeBinary(ctx, ci.startByteIndex, ci.endByteIndex, contents);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, true, false, false);
			byte[] left = XIONUtil.binarySubstring(ci.parentContent,0, ci.startByteIndex);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent,ci.startByteIndex, ci.byteCount);
			parent.putIntoContents(ctx, new XOMBinary(XIONUtil.binaryConcat(left, XOMBinaryType.instance.makeInstanceFrom(ctx, contents).toByteArray(), right)));
		}
		else {
			super.putBeforeContents(ctx, contents);
		}
	}
	
	public void putAfterContents(XNContext ctx, XOMVariant contents) {
		if (chunkType.isFloat()) {
			if (XOMNumberType.instance.canMakeInstanceFrom(ctx, contents)) {
				double d = XOMNumberType.instance.makeInstanceFrom(ctx, contents).toDouble();
				contents = new XOMBinary(chunkType.byteArrayValueOf(d, ctx.getLittleEndian()));
			}
		} else {
			if (XOMIntegerType.instance.canMakeInstanceFrom(ctx, contents)) {
				long l = XOMIntegerType.instance.makeInstanceFrom(ctx, contents).toLong();
				contents = new XOMBinary(chunkType.byteArrayValueOf(l, ctx.getLittleEndian()));
			}
		}
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canPutBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, false, true, false);
			p.putAfterBinary(ctx, ci.startByteIndex, ci.endByteIndex, contents);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, false, true, false);
			byte[] left = XIONUtil.binarySubstring(ci.parentContent,0, ci.endByteIndex);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent,ci.endByteIndex, ci.byteCount);
			parent.putIntoContents(ctx, new XOMBinary(XIONUtil.binaryConcat(left, XOMBinaryType.instance.makeInstanceFrom(ctx, contents).toByteArray(), right)));
		}
		else {
			super.putBeforeContents(ctx, contents);
		}
	}
	
	public void putIntoContents(XNContext ctx, XOMVariant contents, String property, XOMVariant pvalue) {
		if (chunkType.isFloat()) {
			if (XOMNumberType.instance.canMakeInstanceFrom(ctx, contents)) {
				double d = XOMNumberType.instance.makeInstanceFrom(ctx, contents).toDouble();
				contents = new XOMBinary(chunkType.byteArrayValueOf(d, ctx.getLittleEndian()));
			}
		} else {
			if (XOMIntegerType.instance.canMakeInstanceFrom(ctx, contents)) {
				long l = XOMIntegerType.instance.makeInstanceFrom(ctx, contents).toLong();
				contents = new XOMBinary(chunkType.byteArrayValueOf(l, ctx.getLittleEndian()));
			}
		}
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canPutBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, true, true, false);
			p.putIntoBinary(ctx, ci.startByteIndex, ci.endByteIndex, contents, property, pvalue);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, true, true, false);
			byte[] left = XIONUtil.binarySubstring(ci.parentContent, 0, ci.startByteIndex);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent, ci.endByteIndex, ci.byteCount);
			parent.putIntoContents(ctx, new XOMBinary(XIONUtil.binaryConcat(left, XOMBinaryType.instance.makeInstanceFrom(ctx, contents).toByteArray(), right)), property, pvalue);
		}
		else {
			super.putIntoContents(ctx, contents, property, pvalue);
		}
	}
	
	public void putBeforeContents(XNContext ctx, XOMVariant contents, String property, XOMVariant pvalue) {
		if (chunkType.isFloat()) {
			if (XOMNumberType.instance.canMakeInstanceFrom(ctx, contents)) {
				double d = XOMNumberType.instance.makeInstanceFrom(ctx, contents).toDouble();
				contents = new XOMBinary(chunkType.byteArrayValueOf(d, ctx.getLittleEndian()));
			}
		} else {
			if (XOMIntegerType.instance.canMakeInstanceFrom(ctx, contents)) {
				long l = XOMIntegerType.instance.makeInstanceFrom(ctx, contents).toLong();
				contents = new XOMBinary(chunkType.byteArrayValueOf(l, ctx.getLittleEndian()));
			}
		}
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canPutBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, true, false, false);
			p.putBeforeBinary(ctx, ci.startByteIndex, ci.endByteIndex, contents, property, pvalue);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, true, false, false);
			byte[] left = XIONUtil.binarySubstring(ci.parentContent,0, ci.startByteIndex);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent,ci.startByteIndex, ci.byteCount);
			parent.putIntoContents(ctx, new XOMBinary(XIONUtil.binaryConcat(left, XOMBinaryType.instance.makeInstanceFrom(ctx, contents).toByteArray(), right)), property, pvalue);
		}
		else {
			super.putBeforeContents(ctx, contents, property, pvalue);
		}
	}
	
	public void putAfterContents(XNContext ctx, XOMVariant contents, String property, XOMVariant pvalue) {
		if (chunkType.isFloat()) {
			if (XOMNumberType.instance.canMakeInstanceFrom(ctx, contents)) {
				double d = XOMNumberType.instance.makeInstanceFrom(ctx, contents).toDouble();
				contents = new XOMBinary(chunkType.byteArrayValueOf(d, ctx.getLittleEndian()));
			}
		} else {
			if (XOMIntegerType.instance.canMakeInstanceFrom(ctx, contents)) {
				long l = XOMIntegerType.instance.makeInstanceFrom(ctx, contents).toLong();
				contents = new XOMBinary(chunkType.byteArrayValueOf(l, ctx.getLittleEndian()));
			}
		}
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canPutBinary(ctx)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, false, true, false);
			p.putAfterBinary(ctx, ci.startByteIndex, ci.endByteIndex, contents, property, pvalue);
		}
		else if (parent.canPutContents(ctx)) {
			BinaryChunkInfo ci = getChunkInfo(ctx, false, true, false);
			byte[] left = XIONUtil.binarySubstring(ci.parentContent,0, ci.endByteIndex);
			byte[] right = XIONUtil.binarySubstring(ci.parentContent,ci.endByteIndex, ci.byteCount);
			parent.putIntoContents(ctx, new XOMBinary(XIONUtil.binaryConcat(left, XOMBinaryType.instance.makeInstanceFrom(ctx, contents).toByteArray(), right)), property, pvalue);
		}
		else {
			super.putBeforeContents(ctx, contents, property, pvalue);
		}
	}
	
	public boolean canGetProperty(XNContext ctx, String property) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canGetBinaryProperty(ctx, property)) {
			return true;
		}
		else if (parent.canGetProperty(ctx, property)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String property) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canGetBinaryProperty(ctx, property)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false, false);
			return p.getBinaryProperty(ctx, modifier, property, ci.startByteIndex, ci.endByteIndex);
		}
		else if (parent.canGetProperty(ctx, property)) {
			return parent.getProperty(ctx, modifier, property);
		}
		else {
			return super.getProperty(ctx, modifier, property);
		}
	}
	
	public boolean canSetProperty(XNContext ctx, String property) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canSetBinaryProperty(ctx, property)) {
			return true;
		}
		else if (parent.canSetProperty(ctx, property)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void setProperty(XNContext ctx, String property, XOMVariant value) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canSetBinaryProperty(ctx, property)) {
			XOMBinaryContainer p = (XOMBinaryContainer)parent;
			BinaryChunkInfo ci = getChunkInfo(ctx, false, false, false);
			p.setBinaryProperty(ctx, property, ci.startByteIndex, ci.endByteIndex, value);
		}
		else if (parent.canSetProperty(ctx, property)) {
			parent.setProperty(ctx, property, value);
		}
		else {
			super.setProperty(ctx, property, value);
		}
	}
	
	public String toLanguageString() {
		return chunkType.toString() + " " + index + " of " + parent.toLanguageString();
	}
	public String toTextString(XNContext ctx) {
		return getContents(ctx).toTextString(ctx);
	}
	public List<? extends XOMVariant> toVariantList(XNContext ctx) {
		return Arrays.asList(this);
	}
	public List<? extends XOMVariant> toPrimitiveList(XNContext ctx) {
		return Arrays.asList(getContents(ctx));
	}
	public int hashCode() {
		return parent.hashCode() ^ chunkType.hashCode() ^ index;
	}
	public boolean equals(Object o) {
		if (o instanceof XOMBinaryNumericChunk) {
			XOMBinaryNumericChunk other = (XOMBinaryNumericChunk)o;
			return (this.parent.equals(other.parent) && this.chunkType == other.chunkType && this.index == other.index);
		} else {
			return false;
		}
	}
}
