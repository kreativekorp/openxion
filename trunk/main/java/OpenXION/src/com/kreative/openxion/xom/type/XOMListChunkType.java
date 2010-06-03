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

import java.util.List;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.xom.XOMDataType;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.XOMGetError;
import com.kreative.openxion.xom.inst.XOMEmpty;
import com.kreative.openxion.xom.inst.XOMList;
import com.kreative.openxion.xom.inst.XOMListChunk;

public class XOMListChunkType extends XOMDataType<XOMListChunk> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMListChunkType instance = new XOMListChunkType();
	public static final XOMListType listInstance = new XOMListType("elements", DESCRIBABILITY_OF_PLURAL_CHUNKS, instance);
	
	private XOMListChunkType() {
		super("element", DESCRIBABILITY_OF_SINGULAR_CHUNKS | DESCRIBABLE_BY_NAME, XOMListChunk.class);
	}
	
	/*
	 * Instantiation of child variants of this type.
	 */
	
	public boolean canGetChildMassVariant(XNContext ctx, XOMVariant parent) {
		return true;
	}
	public XOMVariant getChildMassVariant(XNContext ctx, XOMVariant parent) {
		return new XOMListChunk(parent, 1, -1);
	}
	public boolean canGetChildVariantByIndex(XNContext ctx, XOMVariant parent, int index) {
		return true;
	}
	public boolean canGetChildVariantByIndex(XNContext ctx, XOMVariant parent, int startIndex, int endIndex) {
		return true;
	}
	public XOMVariant getChildVariantByIndex(XNContext ctx, XOMVariant parent, int index) {
		return new XOMListChunk(parent, index, index);
	}
	public XOMVariant getChildVariantByIndex(XNContext ctx, XOMVariant parent, int startIndex, int endIndex) {
		return new XOMListChunk(parent, startIndex, endIndex);
	}
	public boolean canGetChildVariantByName(XNContext ctx, XOMVariant parent, String name) {
		List<XOMVariant> l = parent.toList(ctx);
		for (int i = 0; i < l.size(); i++) {
			if (l.get(i).toTextString(ctx).equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	public XOMVariant getChildVariantByName(XNContext ctx, XOMVariant parent, String name) {
		List<XOMVariant> l = parent.toList(ctx);
		for (int i = 0; i < l.size(); i++) {
			if (l.get(i).toTextString(ctx).equalsIgnoreCase(name)) {
				return new XOMListChunk(parent, i+1, i+1);
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
		if (instance instanceof XOMListChunk) {
			return true;
		}
		else if ((instance instanceof XOMList || instance instanceof XOMListChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMListChunk) {
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
	protected XOMListChunk makeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		if (instance instanceof XOMListChunk) {
			return (XOMListChunk)instance;
		}
		else if ((instance instanceof XOMList || instance instanceof XOMListChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMListChunk) {
			return (XOMListChunk)instance.toList(ctx).get(0);
		}
		else {
			throw new XOMMorphError(typeName);
		}
	}
	protected XOMListChunk makeInstanceFromImpl(XNContext ctx, XOMVariant left, XOMVariant right) {
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
