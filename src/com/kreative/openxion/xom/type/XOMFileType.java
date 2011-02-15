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
import com.kreative.openxion.xom.XOMSimpleDataType;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMGetError;
import com.kreative.openxion.xom.XOMCreateError;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.inst.XOMFile;
import com.kreative.openxion.xom.inst.XOMList;

public class XOMFileType extends XOMSimpleDataType<XOMFile> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMFileType instance = new XOMFileType();
	public static final XOMListType listInstance = new XOMListType("files", DESCRIBABILITY_OF_PLURAL_FSOBJECTS, instance);
	
	private XOMFileType() {
		super("file", DESCRIBABILITY_OF_SINGULAR_FSOBJECTS, XOMFile.class);
	}
	
	/*
	 * Instantiation of root variants of this type.
	 */
	
	public boolean canGetMassInstance(XNContext ctx) {
		return ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetFiles");
	}
	public XOMVariant getMassInstance(XNContext ctx) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetFiles"))
			throw new XNScriptError("Security settings do not allow file system access");
		File theDir = new File(".");
		File[] theFiles = theDir.listFiles();
		List<XOMFile> theXFiles = new Vector<XOMFile>();
		for (File theFile : theFiles) {
			if (!theFile.isDirectory()) {
				theXFiles.add(new XOMFile(theFile));
			}
		}
		return new XOMList(theXFiles);
	}
	
	public boolean canGetInstanceByIndex(XNContext ctx, int index) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetFile", "Index", Integer.toString(index))) return false;
		List<XOMVariant> theXFiles = ((XOMList)getMassInstance(ctx)).toList();
		index = XIONUtil.index(1, theXFiles.size(), index, index)[0];
		return (index >= 1 && index <= theXFiles.size());
	}
	public boolean canGetInstanceByIndex(XNContext ctx, int startIndex, int endIndex) {
		return ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetFile", "StartIndex", Integer.toString(startIndex), "EndIndex", Integer.toString(endIndex));
	}
	public boolean canGetInstanceByName(XNContext ctx, String name) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetFile", "Name", name)) return false;
		File theFile = new File(name);
		return (theFile.exists() && !theFile.isDirectory());
	}
	public XOMVariant getInstanceByIndex(XNContext ctx, int index) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetFile", "Index", Integer.toString(index)))
			throw new XNScriptError("Security settings do not allow file system access");
		List<XOMVariant> theXFiles = ((XOMList)getMassInstance(ctx)).toList();
		index = XIONUtil.index(1, theXFiles.size(), index, index)[0];
		if (index >= 1 && index <= theXFiles.size()) {
			return theXFiles.get(index-1);
		} else {
			throw new XOMGetError(typeName, index, index);
		}
	}
	public XOMVariant getInstanceByIndex(XNContext ctx, int startIndex, int endIndex) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetFile", "StartIndex", Integer.toString(startIndex), "EndIndex", Integer.toString(endIndex)))
			throw new XNScriptError("Security settings do not allow file system access");
		List<XOMVariant> theXFiles = ((XOMList)getMassInstance(ctx)).toList();
		int[] indexes = XIONUtil.index(1, theXFiles.size(), startIndex, endIndex);
		if (indexes[0] < 1) indexes[0] = 1;
		else if (indexes[0] > theXFiles.size()) indexes[0] = theXFiles.size();
		if (indexes[1] < 1) indexes[1] = 1;
		else if (indexes[1] > theXFiles.size()) indexes[1] = theXFiles.size();
		return new XOMList(theXFiles.subList(indexes[0]-1, indexes[1]));
	}
	public XOMVariant getInstanceByName(XNContext ctx, String name) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetFile", "Name", name))
			throw new XNScriptError("Security settings do not allow file system access");
		File theFile = new File(name);
		if (/* theFile.exists() && */ !theFile.isDirectory()) {
			return new XOMFile(theFile);
		} else {
			throw new XOMGetError(typeName, name);
		}
	}
	
	public boolean canCreateInstanceByName(XNContext ctx, String name) {
		return ctx.allow(XNSecurityKey.FILE_SYSTEM_WRITE, "Operation", "Create", "Name", name);
	}
	public XOMVariant createInstanceByName(XNContext ctx, String name) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_WRITE, "Operation", "Create", "Name", name))
			throw new XNScriptError("Security settings do not allow file system access");
		try {
			File theNewFile = new File(name);
			if (theNewFile.createNewFile()) {
				return new XOMFile(theNewFile);
			} else {
				throw new XOMCreateError(typeName, name);
			}
		} catch (Exception e) {
			throw new XOMCreateError(typeName, name);
		}
	}
	
	/*
	 * Instantiation of child variants of this type.
	 */
	
	public boolean canGetChildMassVariant(XNContext ctx, XOMVariant parent) {
		return ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetFiles", "Parent", parent.toTextString(ctx)) && (XOMFolderType.instance.canMakeInstanceFrom(ctx, parent));
	}
	public XOMVariant getChildMassVariant(XNContext ctx, XOMVariant parent) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetFiles", "Parent", parent.toTextString(ctx)))
			throw new XNScriptError("Security settings do not allow file system access");
		if (XOMFolderType.instance.canMakeInstanceFrom(ctx, parent)) {
			File theDir = XOMFolderType.instance.makeInstanceFrom(ctx, parent).toFile();
			File[] theFiles = theDir.listFiles();
			List<XOMFile> theXFiles = new Vector<XOMFile>();
			for (File theFile : theFiles) {
				if (!theFile.isDirectory()) {
					theXFiles.add(new XOMFile(theFile));
				}
			}
			return new XOMList(theXFiles);
		} else {
			throw new XOMGetError(typeName);
		}
	}
	
	public boolean canGetChildVariantByIndex(XNContext ctx, XOMVariant parent, int index) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetFile", "Parent", parent.toTextString(ctx), "Index", Integer.toString(index))) return false;
		if (!canGetChildMassVariant(ctx, parent)) return false;
		List<? extends XOMVariant> theXFiles = ((XOMList)getChildMassVariant(ctx, parent)).toList();
		index = XIONUtil.index(1, theXFiles.size(), index, index)[0];
		return (index >= 1 && index <= theXFiles.size());
	}
	public boolean canGetChildVariantByIndex(XNContext ctx, XOMVariant parent, int startIndex, int endIndex) {
		return ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetFile", "Parent", parent.toTextString(ctx), "StartIndex", Integer.toString(startIndex), "EndIndex", Integer.toString(endIndex));
	}
	public boolean canGetChildVariantByName(XNContext ctx, XOMVariant parent, String name) {
		if (ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetFile", "Parent", parent.toTextString(ctx), "Name", name) && XOMFolderType.instance.canMakeInstanceFrom(ctx, parent)) {
			File theDir = XOMFolderType.instance.makeInstanceFrom(ctx, parent).toFile();
			File theFile = new File(theDir, name);
			return (theFile.exists() && !theFile.isDirectory());
		} else {
			return false;
		}
	}
	public XOMVariant getChildVariantByIndex(XNContext ctx, XOMVariant parent, int index) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetFile", "Parent", parent.toTextString(ctx), "Index", Integer.toString(index)))
			throw new XNScriptError("Security settings do not allow file system access");
		List<? extends XOMVariant> theXFiles = ((XOMList)getChildMassVariant(ctx, parent)).toList();
		index = XIONUtil.index(1, theXFiles.size(), index, index)[0];
		if (index >= 1 && index <= theXFiles.size()) {
			return theXFiles.get(index-1);
		} else {
			throw new XOMGetError(typeName, index, index);
		}
	}
	public XOMVariant getChildVariantByIndex(XNContext ctx, XOMVariant parent, int startIndex, int endIndex) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetFile", "Parent", parent.toTextString(ctx), "StartIndex", Integer.toString(startIndex), "EndIndex", Integer.toString(endIndex)))
			throw new XNScriptError("Security settings do not allow file system access");
		List<? extends XOMVariant> theXFiles = ((XOMList)getChildMassVariant(ctx, parent)).toList();
		int[] indexes = XIONUtil.index(1, theXFiles.size(), startIndex, endIndex);
		if (indexes[0] < 1) indexes[0] = 1;
		else if (indexes[0] > theXFiles.size()) indexes[0] = theXFiles.size();
		if (indexes[1] < 1) indexes[1] = 1;
		else if (indexes[1] > theXFiles.size()) indexes[1] = theXFiles.size();
		return new XOMList(theXFiles.subList(indexes[0]-1, indexes[1]));
	}
	public XOMVariant getChildVariantByName(XNContext ctx, XOMVariant parent, String name) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "GetFile", "Parent", parent.toTextString(ctx), "Name", name))
			throw new XNScriptError("Security settings do not allow file system access");
		if (XOMFolderType.instance.canMakeInstanceFrom(ctx, parent)) {
			File theDir = XOMFolderType.instance.makeInstanceFrom(ctx, parent).toFile();
			File theFile = new File(theDir, name);
			if (/* theFile.exists() && */ !theFile.isDirectory()) {
				return new XOMFile(theFile);
			} else {
				throw new XOMGetError(typeName, name);
			}
		} else {
			throw new XOMGetError(typeName, name);
		}
	}
	
	public boolean canCreateChildVariantByName(XNContext ctx, XOMVariant parent, String name) {
		return (ctx.allow(XNSecurityKey.FILE_SYSTEM_WRITE, "Operation", "Create", "Parent", parent.toTextString(ctx), "Name", name) && XOMFolderType.instance.canMakeInstanceFrom(ctx, parent));
	}
	public XOMVariant createChildVariantByName(XNContext ctx, XOMVariant parent, String name) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_WRITE, "Operation", "Create", "Parent", parent.toTextString(ctx), "Name", name))
			throw new XNScriptError("Security settings do not allow file system access");
		if (XOMFolderType.instance.canMakeInstanceFrom(ctx, parent)) {
			try {
				File theDir = XOMFolderType.instance.makeInstanceFrom(ctx, parent).toFile();
				File theNewFile = new File(theDir, name);
				if (theNewFile.createNewFile()) {
					return new XOMFile(theNewFile);
				} else {
					throw new XOMCreateError(typeName, name);
				}
			} catch (Exception e) {
				throw new XOMCreateError(typeName, name);
			}
		} else {
			throw new XOMCreateError(typeName, name);
		}
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
		return v instanceof XOMFile && ((XOMFile)v).isFile();
	}
	protected boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant left, XOMVariant right) {
		XOMVariant v = XIONUtil.parseDescriptor(ctx, left.toTextString(ctx) + right.toTextString(ctx));
		if (v == null) return false;
		v = v.asPrimitive(ctx);
		return v instanceof XOMFile && ((XOMFile)v).isFile();
	}
	protected XOMFile makeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		XOMVariant v = XIONUtil.parseDescriptor(ctx, instance.toTextString(ctx));
		if (v == null) throw new XOMMorphError(typeName);
		v = v.asPrimitive(ctx);
		if (v instanceof XOMFile && ((XOMFile)v).isFile()) return (XOMFile)v;
		else throw new XOMMorphError(typeName);
	}
	protected XOMFile makeInstanceFromImpl(XNContext ctx, XOMVariant left, XOMVariant right) {
		XOMVariant v = XIONUtil.parseDescriptor(ctx, left.toTextString(ctx) + right.toTextString(ctx));
		if (v == null) throw new XOMMorphError(typeName);
		v = v.asPrimitive(ctx);
		if (v instanceof XOMFile && ((XOMFile)v).isFile()) return (XOMFile)v;
		else throw new XOMMorphError(typeName);
	}
}
