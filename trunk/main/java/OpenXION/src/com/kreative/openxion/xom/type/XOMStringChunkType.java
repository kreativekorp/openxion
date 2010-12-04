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

package com.kreative.openxion.xom.type;

import com.kreative.openxion.XNContext;
import com.kreative.openxion.util.StringChunkType;
import com.kreative.openxion.util.StringChunkEx;
import com.kreative.openxion.xom.XOMDataType;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.XOMGetError;
import com.kreative.openxion.xom.inst.XOMStringChunk;
import com.kreative.openxion.xom.inst.XOMEmpty;
import com.kreative.openxion.xom.inst.XOMList;

public class XOMStringChunkType extends XOMDataType<XOMStringChunk> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMStringChunkType characterInstance = new XOMStringChunkType(StringChunkType.CHARACTER);
	public static final XOMStringChunkType wordInstance = new XOMStringChunkType(StringChunkType.WORD);
	public static final XOMStringChunkType sentenceInstance = new XOMStringChunkType(StringChunkType.SENTENCE);
	public static final XOMStringChunkType paragraphInstance = new XOMStringChunkType(StringChunkType.PARAGRAPH);
	public static final XOMStringChunkType lineInstance = new XOMStringChunkType(StringChunkType.LINE);
	public static final XOMStringChunkType itemInstance = new XOMStringChunkType(StringChunkType.ITEM);
	public static final XOMStringChunkType rowInstance = new XOMStringChunkType(StringChunkType.ROW);
	public static final XOMStringChunkType columnInstance = new XOMStringChunkType(StringChunkType.COLUMN);
	public static final XOMListType characterListInstance = new XOMListType(characterInstance.ct.toPluralString(), DESCRIBABILITY_OF_PLURAL_CHUNKS, characterInstance);
	public static final XOMListType wordListInstance = new XOMListType(wordInstance.ct.toPluralString(), DESCRIBABILITY_OF_PLURAL_CHUNKS, wordInstance);
	public static final XOMListType sentenceListInstance = new XOMListType(sentenceInstance.ct.toPluralString(), DESCRIBABILITY_OF_PLURAL_CHUNKS, sentenceInstance);
	public static final XOMListType paragraphListInstance = new XOMListType(paragraphInstance.ct.toPluralString(), DESCRIBABILITY_OF_PLURAL_CHUNKS, paragraphInstance);
	public static final XOMListType lineListInstance = new XOMListType(lineInstance.ct.toPluralString(), DESCRIBABILITY_OF_PLURAL_CHUNKS, lineInstance);
	public static final XOMListType itemListInstance = new XOMListType(itemInstance.ct.toPluralString(), DESCRIBABILITY_OF_PLURAL_CHUNKS, itemInstance);
	public static final XOMListType rowListInstance = new XOMListType(rowInstance.ct.toPluralString(), DESCRIBABILITY_OF_PLURAL_CHUNKS, rowInstance);
	public static final XOMListType columnListInstance = new XOMListType(columnInstance.ct.toPluralString(), DESCRIBABILITY_OF_PLURAL_CHUNKS, columnInstance);
	
	private StringChunkType ct;
	
	private XOMStringChunkType(StringChunkType ct) {
		super(ct.toString(), DESCRIBABILITY_OF_SINGULAR_CHUNKS | DESCRIBABLE_BY_NAME, XOMStringChunk.class);
		this.ct = ct;
	}
	
	/*
	 * Instantiation of child variants of this type.
	 */
	
	public boolean canGetChildMassVariant(XNContext ctx, XOMVariant parent) {
		return true;
	}
	public XOMVariant getChildMassVariant(XNContext ctx, XOMVariant parent) {
		return new XOMStringChunk(parent, ct, 1, -1);
	}
	public boolean canGetChildVariantByIndex(XNContext ctx, XOMVariant parent, int index) {
		return true;
	}
	public boolean canGetChildVariantByIndex(XNContext ctx, XOMVariant parent, int startIndex, int endIndex) {
		return true;
	}
	public XOMVariant getChildVariantByIndex(XNContext ctx, XOMVariant parent, int index) {
		return new XOMStringChunk(parent, ct, index, index);
	}
	public XOMVariant getChildVariantByIndex(XNContext ctx, XOMVariant parent, int startIndex, int endIndex) {
		return new XOMStringChunk(parent, ct, startIndex, endIndex);
	}
	public boolean canGetChildVariantByName(XNContext ctx, XOMVariant parent, String name) {
		String s = parent.toTextString(ctx);
		char id = ctx.getItemDelimiter();
		char cd = ctx.getColumnDelimiter();
		char rd = ctx.getRowDelimiter();
		int n = StringChunkEx.count(s, ct, id, cd, rd);
		for (int i = 1; i <= n; i++) {
			int st = StringChunkEx.start(s, ct, i, id, cd, rd);
			int en = StringChunkEx.end(s, ct, i, id, cd, rd);
			if (s.substring(st, en).equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	public XOMVariant getChildVariantByName(XNContext ctx, XOMVariant parent, String name) {
		String s = parent.toTextString(ctx);
		char id = ctx.getItemDelimiter();
		char cd = ctx.getColumnDelimiter();
		char rd = ctx.getRowDelimiter();
		int n = StringChunkEx.count(s, ct, id, cd, rd);
		for (int i = 1; i <= n; i++) {
			int st = StringChunkEx.start(s, ct, i, id, cd, rd);
			int en = StringChunkEx.end(s, ct, i, id, cd, rd);
			if (s.substring(st, en).equalsIgnoreCase(name)) {
				return new XOMStringChunk(parent, ct, i, i);
			}
		}
		throw new XOMGetError(typeName);
	}
	
	/*
	 * Polymorphism - The data type of an object is determined through these methods.
	 * Unlike in Java, where an object's type is determined by the class hierarchy,
	 * objects in XION can be of any mix of data types (hence the term variant for XION objects).
	 */
	
	protected boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		if (instance instanceof XOMStringChunk) {
			return true;
		}
		else if ((instance instanceof XOMList || instance instanceof XOMStringChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMStringChunk) {
			return true;
		}
		else {
			return false;
		}
	}
	protected boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant left, XOMVariant right) {
		if (left instanceof XOMEmpty) {
			return canMakeInstanceFromImpl(ctx, right);
		}
		else if (right instanceof XOMEmpty) {
			return canMakeInstanceFromImpl(ctx, left);
		}
		else {
			return false;
		}
	}
	protected XOMStringChunk makeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		if (instance instanceof XOMStringChunk) {
			return (XOMStringChunk)instance;
		}
		else if ((instance instanceof XOMList || instance instanceof XOMStringChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMStringChunk) {
			return (XOMStringChunk)instance.toList(ctx).get(0);
		}
		else {
			throw new XOMMorphError(typeName);
		}
	}
	protected XOMStringChunk makeInstanceFromImpl(XNContext ctx, XOMVariant left, XOMVariant right) {
		if (left instanceof XOMEmpty) {
			return makeInstanceFromImpl(ctx, right);
		}
		else if (right instanceof XOMEmpty) {
			return makeInstanceFromImpl(ctx, left);
		}
		else {
			throw new XOMMorphError(typeName);
		}
	}
}