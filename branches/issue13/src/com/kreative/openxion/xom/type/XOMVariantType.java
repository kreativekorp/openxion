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

import java.util.Vector;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.xom.XOMSimpleDataType;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.inst.XOMList;
import com.kreative.openxion.xom.inst.XOMListChunk;

public class XOMVariantType extends XOMSimpleDataType<XOMVariant> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMVariantType instance = new XOMVariantType();
	public static final XOMListType listInstance = new XOMListType("variants", DESCRIBABILITY_OF_PRIMITIVES, instance);
	
	private XOMVariantType() {
		super("variant", DESCRIBABILITY_OF_PRIMITIVES, XOMVariant.class);
	}
	
	protected boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		return true;
	}
	protected boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant left, XOMVariant right) {
		return true;
	}
	protected XOMVariant makeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		return instance.asValue(ctx);
	}
	protected XOMVariant makeInstanceFromImpl(XNContext ctx, XOMVariant left, XOMVariant right) {
		Vector<XOMVariant> v = new Vector<XOMVariant>();
		if (left instanceof XOMList)
			v.addAll(((XOMList)left).toList());
		else if (left instanceof XOMListChunk)
			v.addAll(((XOMListChunk)left).toList(ctx));
		else v.add(left);
		if (right instanceof XOMList)
			v.addAll(((XOMList)right).toList());
		else if (right instanceof XOMListChunk)
			v.addAll(((XOMListChunk)right).toList(ctx));
		else v.add(right);
		return new XOMList(v);
	}
}