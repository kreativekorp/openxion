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
 * @since OpenXION 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.xom.type;

import java.net.URL;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.util.XIONUtil;
import com.kreative.openxion.xom.XOMDataType;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMGetError;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.inst.XOMURL;
import com.kreative.openxion.xom.inst.XOMEmpty;
import com.kreative.openxion.xom.inst.XOMList;
import com.kreative.openxion.xom.inst.XOMListChunk;

public class XOMURLType extends XOMDataType<XOMURL> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMURLType instance = new XOMURLType();
	public static final XOMListType listInstance = new XOMListType("URLs", 0, instance);
	
	private XOMURLType() {
		super("URL", DESCRIBABLE_BY_NAME, XOMURL.class);
	}
	
	/*
	 * Instantiation of root variants of this type.
	 */
	public boolean canGetInstanceByName(XNContext ctx, String name) {
		try {
			new URL(name);
			return true;
		} catch (Exception e1) {
			try {
				new URL("http://"+name);
				return true;
			} catch (Exception e2) {
				return false;
			}
		}
	}
	public XOMVariant getInstanceByName(XNContext ctx, String name) {
		try {
			URL u = new URL(name);
			return new XOMURL(u);
		} catch (Exception e1) {
			try {
				URL u = new URL("http://"+name);
				return new XOMURL(u);
			} catch (Exception e2) {
				throw new XOMGetError(typeName, name);
			}
		}
	}
	
	/*
	 * Polymorphism - The data type of an object is determined through these methods.
	 * Unlike in Java, where an object's type is determined by the class hierarchy,
	 * objects in XION can be of any mix of data types (hence the term variant for XION objects).
	 */
	
	private boolean canMorphFromDescription(XNContext ctx, String desc) {
		XOMVariant v = XIONUtil.parseDescriptor(ctx, desc.trim());
		return (v instanceof XOMURL);
	}
	private XOMVariant morphFromDescription(XNContext ctx, String desc) {
		XOMVariant v = XIONUtil.parseDescriptor(ctx, desc.trim());
		if (v instanceof XOMURL) return v;
		else return null;
	}
	protected boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		if (instance instanceof XOMURL) {
			return true;
		}
		else if ((instance instanceof XOMList || instance instanceof XOMListChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMURL) {
			return true;
		}
		else if (canMorphFromDescription(ctx, instance.toTextString(ctx))) {
			return true;
		}
		else {
			return false;
		}
	}
	protected boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant left, XOMVariant right) {
		if (left instanceof XOMEmpty) {
			return canMakeInstanceFromImpl(ctx, right);
		}
		else if (right instanceof XOMEmpty) {
			return canMakeInstanceFromImpl(ctx, left);
		}
		else if (canMorphFromDescription(ctx, left.toTextString(ctx) + right.toTextString(ctx))) {
			return true;
		}
		else {
			return false;
		}
	}
	protected XOMURL makeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		if (instance instanceof XOMURL) {
			return (XOMURL)instance;
		}
		else if ((instance instanceof XOMList || instance instanceof XOMListChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMURL) {
			return (XOMURL)instance.toList(ctx).get(0);
		}
		else {
			XOMVariant v = morphFromDescription(ctx, instance.toTextString(ctx));
			if (v instanceof XOMURL) return (XOMURL)v;
			else throw new XOMMorphError(typeName);
		}
	}
	protected XOMURL makeInstanceFromImpl(XNContext ctx, XOMVariant left, XOMVariant right) {
		if (left instanceof XOMEmpty) {
			return makeInstanceFromImpl(ctx, right);
		}
		else if (right instanceof XOMEmpty) {
			return makeInstanceFromImpl(ctx, left);
		}
		else {
			XOMVariant v = morphFromDescription(ctx, left.toTextString(ctx) + right.toTextString(ctx));
			if (v instanceof XOMURL) return (XOMURL)v;
			else throw new XOMMorphError(typeName);
		}
	}
}
