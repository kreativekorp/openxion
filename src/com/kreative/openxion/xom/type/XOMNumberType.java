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

import java.math.*;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.inst.XOMComplex;
import com.kreative.openxion.xom.inst.XOMNumber;
import com.kreative.openxion.xom.inst.XOMInteger;

public class XOMNumberType extends XOMNumericDataType<XOMNumber> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMNumberType instance = new XOMNumberType();
	public static final XOMListType listInstance = new XOMListType("numbers", DESCRIBABILITY_OF_PRIMITIVES, instance);
	
	private XOMNumberType() {
		super("number", DESCRIBABILITY_OF_PRIMITIVES, XOMNumber.class);
	}
	
	protected boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant instance, boolean acceptEmpty) {
		if (instance instanceof XOMInteger) {
			return true;
		} else if (instance instanceof XOMComplex) {
			if (((XOMComplex)instance).isReal()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	protected boolean canMakeInstanceFromImpl(XNContext ctx, String s, boolean acceptEmpty) {
		if (s.equals("")) return acceptEmpty;
		if (s.equalsIgnoreCase("inf") || s.equalsIgnoreCase("-inf") || s.equalsIgnoreCase("nan")) return true;
		s = s.replace("''", "E-").replace("'", "E+");
		try {
			ctx.getNumberFormat().parseBigDecimal(s);
			return true;
		} catch (Exception e1) {
			return false;
		}
	}
	protected XOMNumber makeInstanceFromImpl(XNContext ctx) {
		return XOMNumber.ZERO;
	}
	protected XOMNumber makeInstanceFromImpl(XNContext ctx, XOMVariant instance, boolean acceptEmpty) {
		if (instance instanceof XOMInteger) {
			return new XOMNumber(new BigDecimal(((XOMInteger)instance).toBigInteger()));
		} else if (instance instanceof XOMComplex) {
			if (((XOMComplex)instance).isReal()) {
				return new XOMNumber(((XOMComplex)instance).toBigDecimals()[0]);
			} else {
				throw new XOMMorphError(typeName);
			}
		} else {
			throw new XOMMorphError(typeName);
		}
	}
	protected XOMNumber makeInstanceFromImpl(XNContext ctx, String s, boolean acceptEmpty) {
		if (s.equals("")) {
			if (acceptEmpty) return XOMNumber.ZERO;
			else throw new XOMMorphError(typeName);
		}
		else if (s.equalsIgnoreCase("inf")) return XOMNumber.POSITIVE_INFINITY;
		else if (s.equalsIgnoreCase("-inf")) return XOMNumber.NEGATIVE_INFINITY;
		else if (s.equalsIgnoreCase("nan")) return XOMNumber.NaN;
		s = s.replace("''", "E-").replace("'", "E+");
		try {
			return new XOMNumber(ctx.getNumberFormat().parseBigDecimal(s));
		} catch (Exception e1) {
			throw new XOMMorphError(typeName);
		}
	}
}
