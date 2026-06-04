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
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.inst.XOMComplex;
import com.kreative.openxion.xom.inst.XOMNumber;
import com.kreative.openxion.xom.inst.XOMInteger;

public class XOMIntegerType extends XOMNumericDataType<XOMInteger> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMIntegerType instance = new XOMIntegerType();
	public static final XOMListType listInstance = new XOMListType("integers", DESCRIBABILITY_OF_PRIMITIVES, instance);
	
	private XOMIntegerType() {
		super("integer", DESCRIBABILITY_OF_PRIMITIVES, XOMInteger.class);
	}
	
	protected boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant instance, boolean acceptEmpty) {
		if (instance instanceof XOMInteger) {
			return true;
		}
		if (instance instanceof XOMNumber) {
			XOMNumber n = (XOMNumber)instance;
			return n.isFinite() && n.frac().isZero();
		}
		if (instance instanceof XOMComplex) {
			XOMComplex c = (XOMComplex)instance;
			return c.isRe() && c.re().isFinite() && c.re().frac().isZero();
		}
		return false;
	}
	
	protected boolean canMakeInstanceFromImpl(XNContext ctx, String s, boolean acceptEmpty) {
		if (s.equals("")) return acceptEmpty;
		s = s.replace("''", "E-").replace("'", "E+");
		try { ctx.getNumberFormat().parseBigInteger(s); return true; }
		catch (Exception e) { return false; }
	}
	
	protected XOMInteger makeInstanceFromImpl(XNContext ctx) {
		return XOMInteger.ZERO;
	}
	
	protected XOMInteger makeInstanceFromImpl(XNContext ctx, XOMVariant instance, boolean acceptEmpty) {
		if (instance instanceof XOMInteger) {
			return (XOMInteger)instance;
		}
		if (instance instanceof XOMNumber) {
			XOMNumber n = (XOMNumber)instance;
			if (n.isFinite() && n.frac().isZero())
				return new XOMInteger(n.toNumber());
		}
		if (instance instanceof XOMComplex) {
			XOMComplex c = (XOMComplex)instance;
			if (c.isRe() && c.re().isFinite() && c.re().frac().isZero())
				return new XOMInteger(c.re().toNumber());
		}
		throw new XOMMorphError(typeName);
	}
	
	protected XOMInteger makeInstanceFromImpl(XNContext ctx, String s, boolean acceptEmpty) {
		if (s.equals("")) {
			if (acceptEmpty) return XOMInteger.ZERO;
			throw new XOMMorphError(typeName);
		}
		s = s.replace("''", "E-").replace("'", "E+");
		try { return new XOMInteger(ctx.getNumberFormat().parseBigInteger(s)); }
		catch (Exception e) { throw new XOMMorphError(typeName); }
	}
}
