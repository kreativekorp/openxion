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

package com.kreative.openxion.xom.type;

import com.kreative.openxion.XNContext;
import com.kreative.openxion.util.BinaryNumericChunkType;
import com.kreative.openxion.xom.XOMDataType;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.inst.XOMBinaryNumericChunk;
import com.kreative.openxion.xom.inst.XOMEmpty;
import com.kreative.openxion.xom.inst.XOMList;

public class XOMBinaryNumericChunkType extends XOMDataType<XOMBinaryNumericChunk> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMBinaryNumericChunkType tinyIntInstance = new XOMBinaryNumericChunkType(BinaryNumericChunkType.TINYINT);
	public static final XOMBinaryNumericChunkType shortIntInstance = new XOMBinaryNumericChunkType(BinaryNumericChunkType.SHORTINT);
	public static final XOMBinaryNumericChunkType mediumIntInstance = new XOMBinaryNumericChunkType(BinaryNumericChunkType.MEDIUMINT);
	public static final XOMBinaryNumericChunkType longIntInstance = new XOMBinaryNumericChunkType(BinaryNumericChunkType.LONGINT);
	public static final XOMBinaryNumericChunkType halfFloatInstance = new XOMBinaryNumericChunkType(BinaryNumericChunkType.HALFFLOAT);
	public static final XOMBinaryNumericChunkType singleFloatInstance = new XOMBinaryNumericChunkType(BinaryNumericChunkType.SINGLEFLOAT);
	public static final XOMBinaryNumericChunkType doubleFloatInstance = new XOMBinaryNumericChunkType(BinaryNumericChunkType.DOUBLEFLOAT);
	public static final XOMListType tinyIntListInstance = new XOMListType(tinyIntInstance.ct.toPluralString(), DESCRIBABILITY_OF_PLURAL_BINARY_NUMERIC_CHUNKS, tinyIntInstance);
	public static final XOMListType shortIntListInstance = new XOMListType(shortIntInstance.ct.toPluralString(), DESCRIBABILITY_OF_PLURAL_BINARY_NUMERIC_CHUNKS, shortIntInstance);
	public static final XOMListType mediumIntListInstance = new XOMListType(mediumIntInstance.ct.toPluralString(), DESCRIBABILITY_OF_PLURAL_BINARY_NUMERIC_CHUNKS, mediumIntInstance);
	public static final XOMListType longIntListInstance = new XOMListType(longIntInstance.ct.toPluralString(), DESCRIBABILITY_OF_PLURAL_BINARY_NUMERIC_CHUNKS, longIntInstance);
	public static final XOMListType halfFloatListInstance = new XOMListType(halfFloatInstance.ct.toPluralString(), DESCRIBABILITY_OF_PLURAL_BINARY_NUMERIC_CHUNKS, halfFloatInstance);
	public static final XOMListType singleFloatListInstance = new XOMListType(singleFloatInstance.ct.toPluralString(), DESCRIBABILITY_OF_PLURAL_BINARY_NUMERIC_CHUNKS, singleFloatInstance);
	public static final XOMListType doubleFloatListInstance = new XOMListType(doubleFloatInstance.ct.toPluralString(), DESCRIBABILITY_OF_PLURAL_BINARY_NUMERIC_CHUNKS, doubleFloatInstance);
	
	private BinaryNumericChunkType ct;
	
	private XOMBinaryNumericChunkType(BinaryNumericChunkType ct) {
		super(ct.toString(), DESCRIBABILITY_OF_SINGULAR_BINARY_NUMERIC_CHUNKS, XOMBinaryNumericChunk.class);
		this.ct = ct;
	}
	
	/*
	 * Instantiation of child variants of this type.
	 */
	
	public boolean canGetChildVariantByIndex(XNContext ctx, XOMVariant parent, int index) {
		return true;
	}
	public XOMVariant getChildVariantByIndex(XNContext ctx, XOMVariant parent, int index) {
		return new XOMBinaryNumericChunk(parent, ct, index);
	}
	
	/*
	 * Polymorphism - The data type of an object is determined through these methods.
	 * Unlike in Java, where an object's type is determined by the class hierarchy,
	 * objects in XION can be of any mix of data types (hence the term variant for XION objects).
	 */
	
	protected boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		if (instance instanceof XOMBinaryNumericChunk) {
			return true;
		}
		else if ((instance instanceof XOMList || instance instanceof XOMBinaryNumericChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMBinaryNumericChunk) {
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
	protected XOMBinaryNumericChunk makeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		if (instance instanceof XOMBinaryNumericChunk) {
			return (XOMBinaryNumericChunk)instance;
		}
		else if ((instance instanceof XOMList || instance instanceof XOMBinaryNumericChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMBinaryNumericChunk) {
			return (XOMBinaryNumericChunk)instance.toList(ctx).get(0);
		}
		else {
			throw new XOMMorphError(typeName);
		}
	}
	protected XOMBinaryNumericChunk makeInstanceFromImpl(XNContext ctx, XOMVariant left, XOMVariant right) {
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
