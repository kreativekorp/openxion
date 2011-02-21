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
import com.kreative.openxion.xom.XOMValueDataType;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.inst.XOMColor;

public class XOMColorType extends XOMValueDataType<XOMColor> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMColorType instance = new XOMColorType();
	public static final XOMListType listInstance = new XOMListType("colors", DESCRIBABILITY_OF_PRIMITIVES, instance);
	
	private XOMColorType() {
		super("color", DESCRIBABILITY_OF_PRIMITIVES, XOMColor.class);
	}
	
	protected boolean canMakeInstanceFromImpl(XNContext ctx) {
		return false;
	}
	protected boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		return false;
	}
	protected boolean canMakeInstanceFromImpl(XNContext ctx, String s) {
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
	protected XOMColor makeInstanceFromImpl(XNContext ctx) {
		throw new XOMMorphError(typeName);
	}
	protected XOMColor makeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		throw new XOMMorphError(typeName);
	}
	protected XOMColor makeInstanceFromImpl(XNContext ctx, String s) {
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
