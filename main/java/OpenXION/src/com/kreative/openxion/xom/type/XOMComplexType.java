/*
 * Copyright &copy; 2009-2026 Rebecca G. Bettencourt / Kreative Software
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
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.inst.XOMComplex;
import com.kreative.openxion.xom.inst.XOMNumber;
import com.kreative.openxion.xom.inst.XOMInteger;

public class XOMComplexType extends XOMNumericDataType<XOMComplex> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMComplexType instance = new XOMComplexType();
	public static final XOMListType listInstance = new XOMListType("complexes", DESCRIBABILITY_OF_PRIMITIVES, instance);
	
	private XOMComplexType() {
		super("complex", DESCRIBABILITY_OF_PRIMITIVES, XOMComplex.class);
	}
	
	protected boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant instance, boolean acceptEmpty) {
		if (instance instanceof XOMInteger) return true;
		if (instance instanceof XOMNumber) return true;
		if (instance instanceof XOMComplex) return true;
		return false;
	}
	
	protected boolean canMakeInstanceFromImpl(XNContext ctx, String s, boolean acceptEmpty) {
		if (s.equals("")) return acceptEmpty;
		s = s.replace("''", "E-").replace("'", "E+");
		String[] ss = s.split(",");
		if (ss.length == 1) {
			return XOMNumberType.instance.canMakeInstanceFromImpl(ctx, ss[0], false);
		}
		if (ss.length == 2) {
			return XOMNumberType.instance.canMakeInstanceFromImpl(ctx, ss[0], false)
				&& XOMNumberType.instance.canMakeInstanceFromImpl(ctx, ss[1], false);
		}
		return false;
	}
	
	protected XOMComplex makeInstanceFromImpl(XNContext ctx) {
		return XOMComplex.ZERO;
	}
	
	protected XOMComplex makeInstanceFromImpl(XNContext ctx, XOMVariant instance, boolean acceptEmpty) {
		if (instance instanceof XOMInteger) {
			XOMInteger i = (XOMInteger)instance;
			return new XOMComplex(i.toNumber(), 0);
		}
		if (instance instanceof XOMNumber) {
			XOMNumber n = (XOMNumber)instance;
			return new XOMComplex(n.toNumber(), 0);
		}
		if (instance instanceof XOMComplex) {
			return (XOMComplex)instance;
		}
		throw new XOMMorphError(typeName);
	}
	
	protected XOMComplex makeInstanceFromImpl(XNContext ctx, String s, boolean acceptEmpty) {
		if (s.equals("")) {
			if (acceptEmpty) return XOMComplex.ZERO;
			throw new XOMMorphError(typeName);
		}
		s = s.replace("''", "E-").replace("'", "E+");
		String[] ss = s.split(",");
		if (ss.length == 1) {
			XOMNumber r = XOMNumberType.instance.makeInstanceFromImpl(ctx, ss[0], false);
			return new XOMComplex(r.toNumber(), 0);
		}
		if (ss.length == 2) {
			XOMNumber r = XOMNumberType.instance.makeInstanceFromImpl(ctx, ss[0], false);
			XOMNumber i = XOMNumberType.instance.makeInstanceFromImpl(ctx, ss[1], false);
			return new XOMComplex(r.toNumber(), i.toNumber());
		}
		throw new XOMMorphError(typeName);
	}
}
