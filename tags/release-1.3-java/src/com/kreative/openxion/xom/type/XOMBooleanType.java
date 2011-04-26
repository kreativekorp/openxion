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
import com.kreative.openxion.xom.XOMPrimitiveDataType;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.inst.XOMBoolean;

public class XOMBooleanType extends XOMPrimitiveDataType<XOMBoolean> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMBooleanType instance = new XOMBooleanType();
	public static final XOMListType listInstance = new XOMListType("booleans", DESCRIBABILITY_OF_PRIMITIVES, instance);
	
	private XOMBooleanType() {
		super("boolean", DESCRIBABILITY_OF_PRIMITIVES, XOMBoolean.class);
	}
	
	protected boolean canMakeInstanceFromImpl(XNContext ctx) {
		return false;
	}
	protected boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		return false;
	}
	protected boolean canMakeInstanceFromImpl(XNContext ctx, String s) {
		return (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false"));
	}
	protected XOMBoolean makeInstanceFromImpl(XNContext ctx) {
		throw new XOMMorphError(typeName);
	}
	protected XOMBoolean makeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		throw new XOMMorphError(typeName);
	}
	protected XOMBoolean makeInstanceFromImpl(XNContext ctx, String s) {
		if (s.equalsIgnoreCase("true")) return XOMBoolean.TRUE;
		else if (s.equalsIgnoreCase("false")) return XOMBoolean.FALSE;
		else throw new XOMMorphError(typeName);
	}
}
