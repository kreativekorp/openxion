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

import com.kreative.openxion.XNContext;
import com.kreative.openxion.xom.XOMDataType;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.inst.XOMColor;
import com.kreative.openxion.xom.inst.XOMEmpty;
import com.kreative.openxion.xom.inst.XOMList;
import com.kreative.openxion.xom.inst.XOMListChunk;

public class XOMColorType extends XOMDataType<XOMColor> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMColorType instance = new XOMColorType();
	public static final XOMListType listInstance = new XOMListType("colors", DESCRIBABILITY_OF_PRIMITIVES, instance);
	
	private XOMColorType() {
		super("color", DESCRIBABILITY_OF_PRIMITIVES, XOMColor.class);
	}
	
	/*
	 * Polymorphism - The data type of an object is determined through these methods.
	 * Unlike in Java, where an object's type is determined by the class hierarchy,
	 * objects in XION can be of any mix of data types (hence the term variant for XION objects).
	 */
	
	protected boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		if (instance instanceof XOMColor) {
			return true;
		}
		else if ((instance instanceof XOMList || instance instanceof XOMListChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMColor) {
			return true;
		}
		else {
			String s = instance.toTextString(ctx);
			String[] ss = s.split(",");
			if (ss.length == 3) {
				try {
					int r = Integer.parseInt(ss[0]);
					int g = Integer.parseInt(ss[1]);
					int b = Integer.parseInt(ss[2]);
					if (r < 0 || g < 0 || b < 0 || r >= 65536 || g >= 65536 || b >= 65536) return false;
					return true;
				} catch (Exception e) {
					return false;
				}
			} else if (ss.length == 4) {
				try {
					int r = Integer.parseInt(ss[0]);
					int g = Integer.parseInt(ss[1]);
					int b = Integer.parseInt(ss[2]);
					int a = Integer.parseInt(ss[3]);
					if (r < 0 || g < 0 || b < 0 || a < 0 || r >= 65536 || g >= 65536 || b >= 65536 || a >= 65536) return false;
					return true;
				} catch (Exception e) {
					return false;
				}
			} else {
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
			String[] ss = s.split(",");
			if (ss.length == 3) {
				try {
					int r = Integer.parseInt(ss[0]);
					int g = Integer.parseInt(ss[1]);
					int b = Integer.parseInt(ss[2]);
					if (r < 0 || g < 0 || b < 0 || r >= 65536 || g >= 65536 || b >= 65536) return false;
					return true;
				} catch (Exception e) {
					return false;
				}
			} else if (ss.length == 4) {
				try {
					int r = Integer.parseInt(ss[0]);
					int g = Integer.parseInt(ss[1]);
					int b = Integer.parseInt(ss[2]);
					int a = Integer.parseInt(ss[3]);
					if (r < 0 || g < 0 || b < 0 || a < 0 || r >= 65536 || g >= 65536 || b >= 65536 || a >= 65536) return false;
					return true;
				} catch (Exception e) {
					return false;
				}
			} else {
				return false;
			}
		}
	}
	protected XOMColor makeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		if (instance instanceof XOMColor) {
			return (XOMColor)instance;
		}
		else if ((instance instanceof XOMList || instance instanceof XOMListChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMColor) {
			return ((XOMColor)instance.toList(ctx).get(0));
		}
		else {
			String s = instance.toTextString(ctx);
			String[] ss = s.split(",");
			if (ss.length == 3) {
				try {
					int r = Integer.parseInt(ss[0]);
					int g = Integer.parseInt(ss[1]);
					int b = Integer.parseInt(ss[2]);
					if (r < 0 || g < 0 || b < 0 || r >= 65536 || g >= 65536 || b >= 65536)
						throw new XOMMorphError(typeName);
					return new XOMColor(r,g,b);
				} catch (Exception e) {
					throw new XOMMorphError(typeName);
				}
			} else if (ss.length == 4) {
				try {
					int r = Integer.parseInt(ss[0]);
					int g = Integer.parseInt(ss[1]);
					int b = Integer.parseInt(ss[2]);
					int a = Integer.parseInt(ss[3]);
					if (r < 0 || g < 0 || b < 0 || a < 0 || r >= 65536 || g >= 65536 || b >= 65536 || a >= 65536)
						throw new XOMMorphError(typeName);
					return new XOMColor(r,g,b,a);
				} catch (Exception e) {
					throw new XOMMorphError(typeName);
				}
			} else {
				throw new XOMMorphError(typeName);
			}
		}
	}
	protected XOMColor makeInstanceFromImpl(XNContext ctx, XOMVariant left, XOMVariant right) {
		if (left instanceof XOMEmpty) {
			return makeInstanceFromImpl(ctx, right);
		}
		else if (right instanceof XOMEmpty) {
			return makeInstanceFromImpl(ctx, left);
		}
		else {
			String s = left.toTextString(ctx) + right.toTextString(ctx);
			String[] ss = s.split(",");
			if (ss.length == 3) {
				try {
					int r = Integer.parseInt(ss[0]);
					int g = Integer.parseInt(ss[1]);
					int b = Integer.parseInt(ss[2]);
					if (r < 0 || g < 0 || b < 0 || r >= 65536 || g >= 65536 || b >= 65536)
						throw new XOMMorphError(typeName);
					return new XOMColor(r,g,b);
				} catch (Exception e) {
					throw new XOMMorphError(typeName);
				}
			} else if (ss.length == 4) {
				try {
					int r = Integer.parseInt(ss[0]);
					int g = Integer.parseInt(ss[1]);
					int b = Integer.parseInt(ss[2]);
					int a = Integer.parseInt(ss[3]);
					if (r < 0 || g < 0 || b < 0 || a < 0 || r >= 65536 || g >= 65536 || b >= 65536 || a >= 65536)
						throw new XOMMorphError(typeName);
					return new XOMColor(r,g,b,a);
				} catch (Exception e) {
					throw new XOMMorphError(typeName);
				}
			} else {
				throw new XOMMorphError(typeName);
			}
		}
	}
}
