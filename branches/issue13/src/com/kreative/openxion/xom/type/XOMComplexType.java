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
import com.kreative.openxion.xom.XOMDataType;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.inst.XOMComplex;
import com.kreative.openxion.xom.inst.XOMNumber;
import com.kreative.openxion.xom.inst.XOMInteger;
import com.kreative.openxion.xom.inst.XOMEmpty;
import com.kreative.openxion.xom.inst.XOMList;
import com.kreative.openxion.xom.inst.XOMListChunk;

public class XOMComplexType extends XOMDataType<XOMComplex> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMComplexType instance = new XOMComplexType();
	public static final XOMListType listInstance = new XOMListType("complexes", DESCRIBABILITY_OF_PRIMITIVES, instance);
	
	private XOMComplexType() {
		super("complex", DESCRIBABILITY_OF_PRIMITIVES, XOMComplex.class);
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
			return true;
		}
		else if (instance instanceof XOMComplex) {
			return true;
		}
		else if ((instance instanceof XOMList || instance instanceof XOMListChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMInteger) {
			return true;
		}
		else if ((instance instanceof XOMList || instance instanceof XOMListChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMNumber) {
			return true;
		}
		else if ((instance instanceof XOMList || instance instanceof XOMListChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMComplex) {
			return true;
		}
		else {
			String s = instance.toTextString(ctx);
			if (s.equals("") || s.equalsIgnoreCase("inf") || s.equalsIgnoreCase("-inf") || s.equalsIgnoreCase("nan")) return true;
			s = s.replace("''", "E-").replace("'", "E+");
			String[] ss = s.split(",");
			if (ss.length < 1 || ss.length > 2) {
				return false;
			} else {
				if (ss.length > 1) {
					try {
						ctx.getNumberFormat().parseBigDecimal(ss[1]);
					} catch (Exception e1) {
						return false;
					}
				}
				try {
					ctx.getNumberFormat().parseBigDecimal(ss[0]);
				} catch (Exception e1) {
					return false;
				}
				return true;
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
			String[] ss = s.split(",");
			if (ss.length < 1 || ss.length > 2) {
				return false;
			} else {
				if (ss.length > 1) {
					try {
						ctx.getNumberFormat().parseBigDecimal(ss[1]);
					} catch (Exception e1) {
						return false;
					}
				}
				try {
					ctx.getNumberFormat().parseBigDecimal(ss[0]);
				} catch (Exception e1) {
					return false;
				}
				return true;
			}
		}
	}
	protected XOMComplex makeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		if (instance instanceof XOMEmpty) {
			return XOMComplex.ZERO;
		}
		else if (instance instanceof XOMInteger) {
			return new XOMComplex(new BigDecimal(((XOMInteger)instance).toBigInteger()), BigDecimal.ZERO);
		}
		else if (instance instanceof XOMNumber) {
			return new XOMComplex(((XOMNumber)instance).toBigDecimal(), BigDecimal.ZERO);
		}
		else if (instance instanceof XOMComplex) {
			return ((XOMComplex)instance);
		}
		else if ((instance instanceof XOMList || instance instanceof XOMListChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMInteger) {
			return new XOMComplex(new BigDecimal(((XOMInteger)instance.toList(ctx).get(0)).toBigInteger()), BigDecimal.ZERO);
		}
		else if ((instance instanceof XOMList || instance instanceof XOMListChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMNumber) {
			return new XOMComplex(((XOMNumber)instance.toList(ctx).get(0)).toBigDecimal(), BigDecimal.ZERO);
		}
		else if ((instance instanceof XOMList || instance instanceof XOMListChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMComplex) {
			return ((XOMComplex)instance.toList(ctx).get(0));
		}
		else {
			String s = instance.toTextString(ctx);
			if (s.equals("")) return XOMComplex.ZERO;
			else if (s.equalsIgnoreCase("inf")) return XOMComplex.POSITIVE_INFINITY;
			else if (s.equalsIgnoreCase("-inf")) return XOMComplex.NEGATIVE_INFINITY;
			else if (s.equalsIgnoreCase("nan")) return XOMComplex.NaN;
			s = s.replace("''", "E-").replace("'", "E+");
			String[] ss = s.split(",");
			if (ss.length < 1 || ss.length > 2) {
				throw new XOMMorphError(typeName);
			} else {
				BigDecimal rp = BigDecimal.ZERO;
				BigDecimal ip = BigDecimal.ZERO;
				if (ss.length > 1) {
					try {
						ip = ctx.getNumberFormat().parseBigDecimal(ss[1]);
					} catch (Exception e1) {
						throw new XOMMorphError(typeName);
					}
				}
				try {
					rp = ctx.getNumberFormat().parseBigDecimal(ss[0]);
				} catch (Exception e1) {
					throw new XOMMorphError(typeName);
				}
				return new XOMComplex(rp, ip);
			}
		}
	}
	protected XOMComplex makeInstanceFromImpl(XNContext ctx, XOMVariant left, XOMVariant right) {
		if (left instanceof XOMEmpty) {
			return makeInstanceFromImpl(ctx, right);
		}
		else if (right instanceof XOMEmpty) {
			return makeInstanceFromImpl(ctx, left);
		}
		else {
			String s = left.toTextString(ctx) + right.toTextString(ctx);
			if (s.equals("")) return XOMComplex.ZERO;
			else if (s.equalsIgnoreCase("inf")) return XOMComplex.POSITIVE_INFINITY;
			else if (s.equalsIgnoreCase("-inf")) return XOMComplex.NEGATIVE_INFINITY;
			else if (s.equalsIgnoreCase("nan")) return XOMComplex.NaN;
			s = s.replace("''", "E-").replace("'", "E+");
			String[] ss = s.split(",");
			if (ss.length < 1 || ss.length > 2) {
				throw new XOMMorphError(typeName);
			} else {
				BigDecimal rp = BigDecimal.ZERO;
				BigDecimal ip = BigDecimal.ZERO;
				if (ss.length > 1) {
					try {
						ip = ctx.getNumberFormat().parseBigDecimal(ss[1]);
					} catch (Exception e1) {
						throw new XOMMorphError(typeName);
					}
				}
				try {
					rp = ctx.getNumberFormat().parseBigDecimal(ss[0]);
				} catch (Exception e1) {
					throw new XOMMorphError(typeName);
				}
				return new XOMComplex(rp, ip);
			}
		}
	}
}
