/*
 * Copyright &copy; 2011 Rebecca G. Bettencourt / Kreative Software
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
 * @since OpenXION 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.xom.type;

import com.kreative.openxion.XNContext;
import com.kreative.openxion.xom.XOMContainerDataType;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.inst.XOMDictionary;
import com.kreative.openxion.xom.inst.XOMDictionaryChunk;

public class XOMDictionaryChunkType extends XOMContainerDataType<XOMDictionaryChunk> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMDictionaryChunkType instance = new XOMDictionaryChunkType();
	public static final XOMListType listInstance = new XOMListType("entries", DESCRIBABILITY_OF_PRIMITIVES, instance);
	
	private XOMDictionaryChunkType() {
		super("entry", DESCRIBABLE_BY_INDEX | DESCRIBABLE_BY_ORDINAL | DESCRIBABLE_BY_NAME, XOMDictionaryChunk.class);
	}
	
	public boolean canGetChildVariantByIndex(XNContext ctx, XOMVariant parent, int index) {
		if (XOMDictionaryType.instance.canMakeInstanceFrom(ctx, parent)) {
			XOMDictionary dictionary = XOMDictionaryType.instance.makeInstanceFrom(ctx, parent);
			return (dictionary.toMap().containsKey(Integer.toString(index)));
		} else {
			return false;
		}
	}
	public boolean canGetChildVariantByName(XNContext ctx, XOMVariant parent, String name) {
		if (XOMDictionaryType.instance.canMakeInstanceFrom(ctx, parent)) {
			XOMDictionary dictionary = XOMDictionaryType.instance.makeInstanceFrom(ctx, parent);
			return (dictionary.toMap().containsKey(name));
		} else {
			return false;
		}
	}
	public XOMVariant getChildVariantByIndex(XNContext ctx, XOMVariant parent, int index) {
		return new XOMDictionaryChunk(parent, Integer.toString(index));
	}
	public XOMVariant getChildVariantByName(XNContext ctx, XOMVariant parent, String name) {
		return new XOMDictionaryChunk(parent, name);
	}
}
