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

import java.math.*;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.xom.XOMDataType;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.inst.XOMComplex;
import com.kreative.openxion.xom.inst.XOMNumber;
import com.kreative.openxion.xom.inst.XOMInteger;
import com.kreative.openxion.xom.inst.XOMEmpty;
import com.kreative.openxion.xom.inst.XOMList;
import com.kreative.openxion.xom.inst.XOMListChunk;

public class XOMIntegerType extends XOMDataType<XOMInteger> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMIntegerType instance = new XOMIntegerType();
	public static final XOMListType listInstance = new XOMListType("integers", DESCRIBABILITY_OF_PRIMITIVES, instance);
	
	private XOMIntegerType() {
		super("integer", DESCRIBABILITY_OF_PRIMITIVES, XOMInteger.class);
	}
	
	/*
	 * Polymorphism - The data type of an object is determined through these methods.
	 * Unlike in Java, where an object's type is determined by the class hierarchy,
	 * objects in XION can be of any mix of data types (hence the term variant for XION objects).
	 */
	
	protected boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		if (instance instanceof XOMEmpty) {
			return true;
		}
		else if (instance instanceof XOMInteger) {
			return true;
		}
		else if (instance instanceof XOMNumber) {
			try {
				((XOMNumber)instance).toBigDecimal().toBigIntegerExact();
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		else if (instance instanceof XOMComplex) {
			if (((XOMComplex)instance).isReal()) {
				try {
					((XOMComplex)instance).toBigDecimals()[0].toBigIntegerExact();
					return true;
				} catch (Exception e) {
					return false;
				}
			} else {
				return false;
			}
		}
		else if ((instance instanceof XOMList || instance instanceof XOMListChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMInteger) {
			return true;
		}
		else if ((instance instanceof XOMList || instance instanceof XOMListChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMNumber) {
			try {
				((XOMNumber)instance.toList(ctx).get(0)).toBigDecimal().toBigIntegerExact();
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		else if ((instance instanceof XOMList || instance instanceof XOMListChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMComplex) {
			if (((XOMComplex)instance.toList(ctx).get(0)).isReal()) {
				try {
					((XOMComplex)instance.toList(ctx).get(0)).toBigDecimals()[0].toBigIntegerExact();
					return true;
				} catch (Exception e) {
					return false;
				}
			} else {
				return false;
			}
		}
		else {
			String s = instance.toTextString(ctx);
			if (s.equals("") || s.equalsIgnoreCase("inf") || s.equalsIgnoreCase("-inf") || s.equalsIgnoreCase("nan")) return true;
			s = s.replace("''", "E-").replace("'", "E+");
			try {
				ctx.getNumberFormat().parseBigInteger(s);
				return true;
			} catch (Exception e1) {
				return false;
			}
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
			String s = left.toTextString(ctx) + right.toTextString(ctx);
			if (s.equals("") || s.equalsIgnoreCase("inf") || s.equalsIgnoreCase("-inf") || s.equalsIgnoreCase("nan")) return true;
			s = s.replace("''", "E-").replace("'", "E+");
			try {
				ctx.getNumberFormat().parseBigInteger(s);
				return true;
			} catch (Exception e1) {
				return false;
			}
		}
	}
	protected XOMInteger makeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		if (instance instanceof XOMEmpty) {
			return XOMInteger.ZERO;
		}
		else if (instance instanceof XOMInteger) {
			return ((XOMInteger)instance);
		}
		else if (instance instanceof XOMNumber) {
			try {
				BigInteger i = ((XOMNumber)instance).toBigDecimal().toBigIntegerExact();
				return new XOMInteger(i);
			} catch (Exception e) {
				throw new XOMMorphError(typeName);
			}
		}
		else if (instance instanceof XOMComplex) {
			if (((XOMComplex)instance).isReal()) {
				try {
					BigInteger i = ((XOMComplex)instance).toBigDecimals()[0].toBigIntegerExact();
					return new XOMInteger(i);
				} catch (Exception e) {
					throw new XOMMorphError(typeName);
				}
			} else {
				throw new XOMMorphError(typeName);
			}
		}
		else if ((instance instanceof XOMList || instance instanceof XOMListChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMInteger) {
			return ((XOMInteger)instance.toList(ctx).get(0));
		}
		else if ((instance instanceof XOMList || instance instanceof XOMListChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMNumber) {
			try {
				BigInteger i = ((XOMNumber)instance.toList(ctx).get(0)).toBigDecimal().toBigIntegerExact();
				return new XOMInteger(i);
			} catch (Exception e) {
				throw new XOMMorphError(typeName);
			}
		}
		else if ((instance instanceof XOMList || instance instanceof XOMListChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMComplex) {
			if (((XOMComplex)instance.toList(ctx).get(0)).isReal()) {
				try {
					BigInteger i = ((XOMComplex)instance.toList(ctx).get(0)).toBigDecimals()[0].toBigIntegerExact();
					return new XOMInteger(i);
				} catch (Exception e) {
					throw new XOMMorphError(typeName);
				}
			} else {
				throw new XOMMorphError(typeName);
			}
		}
		else {
			String s = instance.toTextString(ctx);
			if (s.equals("")) return XOMInteger.ZERO;
			else if (s.equalsIgnoreCase("inf")) return XOMInteger.POSITIVE_INFINITY;
			else if (s.equalsIgnoreCase("-inf")) return XOMInteger.NEGATIVE_INFINITY;
			else if (s.equalsIgnoreCase("nan")) return XOMInteger.NaN;
			s = s.replace("''", "E-").replace("'", "E+");
			try {
				BigInteger i = ctx.getNumberFormat().parseBigInteger(s);
				return new XOMInteger(i);
			} catch (Exception e1) {
				throw new XOMMorphError(typeName);
			}
		}
	}
	protected XOMInteger makeInstanceFromImpl(XNContext ctx, XOMVariant left, XOMVariant right) {
		if (left instanceof XOMEmpty) {
			return makeInstanceFromImpl(ctx, right);
		}
		else if (right instanceof XOMEmpty) {
			return makeInstanceFromImpl(ctx, left);
		}
		else {
			String s = left.toTextString(ctx) + right.toTextString(ctx);
			if (s.equals("")) return XOMInteger.ZERO;
			else if (s.equalsIgnoreCase("inf")) return XOMInteger.POSITIVE_INFINITY;
			else if (s.equalsIgnoreCase("-inf")) return XOMInteger.NEGATIVE_INFINITY;
			else if (s.equalsIgnoreCase("nan")) return XOMInteger.NaN;
			s = s.replace("''", "E-").replace("'", "E+");
			try {
				BigInteger i = ctx.getNumberFormat().parseBigInteger(s);
				return new XOMInteger(i);
			} catch (Exception e1) {
				throw new XOMMorphError(typeName);
			}
		}
	}
}
