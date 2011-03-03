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

import java.io.File;
import java.util.*;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.XNSecurityKey;
import com.kreative.openxion.XNScriptError;
import com.kreative.openxion.util.XIONUtil;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMSimpleDataType;
import com.kreative.openxion.xom.XOMGetError;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.inst.XOMFile;
import com.kreative.openxion.xom.inst.XOMList;

public class XOMDiskType extends XOMSimpleDataType<XOMFile> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMDiskType instance = new XOMDiskType();
	public static final XOMListType listInstance = new XOMListType("disks", DESCRIBABILITY_OF_PLURAL_FSOBJECTS, instance);
	
	private XOMDiskType() {
		super("disk", DESCRIBABILITY_OF_SINGULAR_FSOBJECTS, XOMFile.class);
	}
	
	/*
	 * Instantiation of root variants of this type.
	 */
	
	public boolean canGetMassInstance(XNContext ctx) {
		return ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetDisks");
	}
	public XOMVariant getMassInstance(XNContext ctx) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetDisks"))
			throw new XNScriptError("Security settings do not allow file system access");
		File[] roots = File.listRoots();
		List<XOMFile> xroots = new Vector<XOMFile>();
		for (File root : roots) {
			xroots.add(new XOMFile(root));
		}
		return new XOMList(xroots);
	}
	
	public boolean canGetInstanceByIndex(XNContext ctx, int index) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetDisk", "Index", Integer.toString(index))) return false;
		File[] roots = File.listRoots();
		index = XIONUtil.index(1, roots.length, index, index)[0];
		return (index >= 1 && index <= roots.length);
	}
	public boolean canGetInstanceByIndex(XNContext ctx, int startIndex, int endIndex) {
		return ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetDisk", "StartIndex", Integer.toString(startIndex), "EndIndex", Integer.toString(endIndex));
	}
	public boolean canGetInstanceByName(XNContext ctx, String name) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetDisk", "Name", name)) return false;
		File[] roots = File.listRoots();
		for (File root : roots) {
			if (root.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	public XOMVariant getInstanceByIndex(XNContext ctx, int index) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetDisk", "Index", Integer.toString(index)))
			throw new XNScriptError("Security settings do not allow file system access");
		File[] roots = File.listRoots();
		index = XIONUtil.index(1, roots.length, index, index)[0];
		if (index >= 1 && index <= roots.length) {
			return new XOMFile(roots[index-1]);
		} else {
			throw new XOMGetError(typeName, index, index);
		}
	}
	public XOMVariant getInstanceByIndex(XNContext ctx, int startIndex, int endIndex) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetDisk", "StartIndex", Integer.toString(startIndex), "EndIndex", Integer.toString(endIndex)))
			throw new XNScriptError("Security settings do not allow file system access");
		File[] roots = File.listRoots();
		int[] indexes = XIONUtil.index(1, roots.length, startIndex, endIndex);
		if (indexes[0] < 1) indexes[0] = 1;
		else if (indexes[0] > roots.length) indexes[0] = roots.length;
		if (indexes[1] < 1) indexes[1] = 1;
		else if (indexes[1] > roots.length) indexes[1] = roots.length;
		List<XOMFile> xroots = new Vector<XOMFile>();
		for (int i = indexes[0]-1; i < indexes[1]; i++) {
			xroots.add(new XOMFile(roots[i]));
		}
		return new XOMList(xroots);
	}
	public XOMVariant getInstanceByName(XNContext ctx, String name) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetDisk", "Name", name))
			throw new XNScriptError("Security settings do not allow file system access");
		File[] roots = File.listRoots();
		for (File root : roots) {
			if (root.getName().equalsIgnoreCase(name)) {
				return new XOMFile(root);
			}
		}
		throw new XOMGetError(typeName, name);
	}
	
	/*
	 * Polymorphism - The data type of an object is determined through these methods.
	 * Unlike in Java, where an object's type is determined by the class hierarchy,
	 * objects in XION can be of any mix of data types (hence the term variant for XION objects).
	 */
	
	protected boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		XOMVariant v = XIONUtil.parseDescriptor(ctx, instance.toTextString(ctx));
		if (v == null) return false;
		v = v.asPrimitive(ctx);
		return v instanceof XOMFile && ((XOMFile)v).isDisk();
	}
	protected boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant left, XOMVariant right) {
		XOMVariant v = XIONUtil.parseDescriptor(ctx, left.toTextString(ctx) + right.toTextString(ctx));
		if (v == null) return false;
		v = v.asPrimitive(ctx);
		return v instanceof XOMFile && ((XOMFile)v).isDisk();
	}
	protected XOMFile makeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		XOMVariant v = XIONUtil.parseDescriptor(ctx, instance.toTextString(ctx));
		if (v == null) throw new XOMMorphError(typeName);
		v = v.asPrimitive(ctx);
		if (v instanceof XOMFile && ((XOMFile)v).isDisk()) return (XOMFile)v;
		else throw new XOMMorphError(typeName);
	}
	protected XOMFile makeInstanceFromImpl(XNContext ctx, XOMVariant left, XOMVariant right) {
		XOMVariant v = XIONUtil.parseDescriptor(ctx, left.toTextString(ctx) + right.toTextString(ctx));
		if (v == null) throw new XOMMorphError(typeName);
		v = v.asPrimitive(ctx);
		if (v instanceof XOMFile && ((XOMFile)v).isDisk()) return (XOMFile)v;
		else throw new XOMMorphError(typeName);
	}
}
