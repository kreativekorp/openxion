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
import com.kreative.openxion.xom.XOMContainerDataType;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.inst.XOMBinaryByteChunk;

public class XOMBinaryByteChunkType extends XOMContainerDataType<XOMBinaryByteChunk> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMBinaryByteChunkType instance = new XOMBinaryByteChunkType();
	public static final XOMListType listInstance = new XOMListType("bytes", DESCRIBABILITY_OF_PLURAL_CHUNKS, instance);
	
	private XOMBinaryByteChunkType() {
		super("byte", DESCRIBABILITY_OF_SINGULAR_CHUNKS, XOMBinaryByteChunk.class);
	}
	
	public boolean canGetChildMassVariant(XNContext ctx, XOMVariant parent) {
		return true;
	}
	public XOMVariant getChildMassVariant(XNContext ctx, XOMVariant parent) {
		return new XOMBinaryByteChunk(parent, 0, -1);
	}
	public boolean canGetChildVariantByIndex(XNContext ctx, XOMVariant parent, int index) {
		return true;
	}
	public boolean canGetChildVariantByIndex(XNContext ctx, XOMVariant parent, int startIndex, int endIndex) {
		return true;
	}
	public XOMVariant getChildVariantByIndex(XNContext ctx, XOMVariant parent, int index) {
		return new XOMBinaryByteChunk(parent, index, index);
	}
	public XOMVariant getChildVariantByIndex(XNContext ctx, XOMVariant parent, int startIndex, int endIndex) {
		return new XOMBinaryByteChunk(parent, startIndex, endIndex);
	}
}
