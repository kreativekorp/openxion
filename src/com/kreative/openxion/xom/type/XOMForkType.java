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

import java.io.File;
import java.util.*;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.util.XIONUtil;
import com.kreative.openxion.xom.XOMDataType;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMGetError;
import com.kreative.openxion.xom.XOMCreateError;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.inst.XOMFile;
import com.kreative.openxion.xom.inst.XOMEmpty;
import com.kreative.openxion.xom.inst.XOMList;
import com.kreative.openxion.xom.inst.XOMListChunk;

public class XOMForkType extends XOMDataType<XOMFile> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMForkType instance = new XOMForkType();
	public static final XOMListType listInstance = new XOMListType("forks", DESCRIBABILITY_OF_PLURAL_FSOBJECTS, instance);
	
	private XOMForkType() {
		super("fork", DESCRIBABILITY_OF_SINGULAR_FSOBJECTS, XOMFile.class);
	}
	
	/*
	 * Instantiation of child variants of this type.
	 */
	
	public boolean canGetChildMassVariant(XNContext ctx, XOMVariant parent) {
		if (XOMFileType.instance.canMakeInstanceFrom(ctx, parent)) {
			return true;
		} else {
			return false;
		}
	}
	public XOMVariant getChildMassVariant(XNContext ctx, XOMVariant parent) {
		if (XOMFileType.instance.canMakeInstanceFrom(ctx, parent)) {
			File theFile = XOMFileType.instance.makeInstanceFrom(ctx, parent).toFile();
			File theForkHandle = new File(theFile, "..namedfork");
			File theDataFork = new File(theForkHandle, "data");
			File theRsrcFork = new File(theForkHandle, "rsrc");
			List<XOMFile> xforks = new Vector<XOMFile>();
			if (theDataFork.exists()) xforks.add(new XOMFile(theDataFork));
			if (theRsrcFork.exists()) xforks.add(new XOMFile(theRsrcFork));
			return new XOMList(xforks);
		} else {
			throw new XOMGetError(typeName);
		}
	}
	
	public boolean canGetChildVariantByIndex(XNContext ctx, XOMVariant parent, int index) {
		if (XOMFileType.instance.canMakeInstanceFrom(ctx, parent)) {
			File theFile = XOMFileType.instance.makeInstanceFrom(ctx, parent).toFile();
			File theForkHandle = new File(theFile, "..namedfork");
			File theDataFork = new File(theForkHandle, "data");
			File theRsrcFork = new File(theForkHandle, "rsrc");
			List<XOMFile> xforks = new Vector<XOMFile>();
			if (theDataFork.exists()) xforks.add(new XOMFile(theDataFork));
			if (theRsrcFork.exists()) xforks.add(new XOMFile(theRsrcFork));
			index = XIONUtil.index(1, xforks.size(), index, index)[0];
			return (index >= 1 && index <= xforks.size());
		} else {
			return false;
		}
	}
	public boolean canGetChildVariantByIndex(XNContext ctx, XOMVariant parent, int startIndex, int endIndex) {
		if (XOMFileType.instance.canMakeInstanceFrom(ctx, parent)) {
			return true;
		} else {
			return false;
		}
	}
	public boolean canGetChildVariantByName(XNContext ctx, XOMVariant parent, String name) {
		if (XOMFileType.instance.canMakeInstanceFrom(ctx, parent)) {
			File theFile = XOMFileType.instance.makeInstanceFrom(ctx, parent).toFile();
			File theForkHandle = new File(theFile, "..namedfork");
			File theFork = new File(theForkHandle, name);
			return theFork.exists();
		} else {
			return false;
		}
	}
	public XOMVariant getChildVariantByIndex(XNContext ctx, XOMVariant parent, int index) {
		if (XOMFileType.instance.canMakeInstanceFrom(ctx, parent)) {
			File theFile = XOMFileType.instance.makeInstanceFrom(ctx, parent).toFile();
			File theForkHandle = new File(theFile, "..namedfork");
			File theDataFork = new File(theForkHandle, "data");
			File theRsrcFork = new File(theForkHandle, "rsrc");
			List<XOMFile> xforks = new Vector<XOMFile>();
			if (theDataFork.exists()) xforks.add(new XOMFile(theDataFork));
			if (theRsrcFork.exists()) xforks.add(new XOMFile(theRsrcFork));
			index = XIONUtil.index(1, xforks.size(), index, index)[0];
			if (index >= 1 && index <= xforks.size()) {
				return xforks.get(index-1);
			} else {
				throw new XOMGetError(typeName, index, index);
			}
		} else {
			throw new XOMGetError(typeName, index, index);
		}
	}
	public XOMVariant getChildVariantByIndex(XNContext ctx, XOMVariant parent, int startIndex, int endIndex) {
		if (XOMFileType.instance.canMakeInstanceFrom(ctx, parent)) {
			File theFile = XOMFileType.instance.makeInstanceFrom(ctx, parent).toFile();
			File theForkHandle = new File(theFile, "..namedfork");
			File theDataFork = new File(theForkHandle, "data");
			File theRsrcFork = new File(theForkHandle, "rsrc");
			List<XOMFile> xforks = new Vector<XOMFile>();
			if (theDataFork.exists()) xforks.add(new XOMFile(theDataFork));
			if (theRsrcFork.exists()) xforks.add(new XOMFile(theRsrcFork));
			int[] indexes = XIONUtil.index(1, xforks.size(), startIndex, endIndex);
			if (indexes[0] < 1) indexes[0] = 1;
			else if (indexes[0] > xforks.size()) indexes[0] = xforks.size();
			if (indexes[1] < 1) indexes[1] = 1;
			else if (indexes[1] > xforks.size()) indexes[1] = xforks.size();
			return new XOMList(xforks.subList(indexes[0]-1, indexes[1]));
		} else {
			throw new XOMGetError(typeName, startIndex, endIndex);
		}
	}
	public XOMVariant getChildVariantByName(XNContext ctx, XOMVariant parent, String name) {
		if (XOMFileType.instance.canMakeInstanceFrom(ctx, parent)) {
			File theFile = XOMFileType.instance.makeInstanceFrom(ctx, parent).toFile();
			File theForkHandle = new File(theFile, "..namedfork");
			File theFork = new File(theForkHandle, name);
			//if (theFork.exists()) {
				return new XOMFile(theFork);
			//} else {
			//	throw new XOMGetError(typeName, name);
			//}
		} else {
			throw new XOMGetError(typeName, name);
		}
	}
	
	public boolean canCreateChildVariantByName(XNContext ctx, XOMVariant parent, String name) {
		if (XOMFileType.instance.canMakeInstanceFrom(ctx, parent)) {
			return true;
		} else {
			return false;
		}
	}
	public XOMVariant createChildVariantByName(XNContext ctx, XOMVariant parent, String name) {
		if (XOMFileType.instance.canMakeInstanceFrom(ctx, parent)) {
			File theFile = XOMFileType.instance.makeInstanceFrom(ctx, parent).toFile();
			File theForkHandle = new File(theFile, "..namedfork");
			File theFork = new File(theForkHandle, name);
			try {
				if (theFork.createNewFile()) {
					return new XOMFile(theFork);
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
	
	private boolean canMorphFromDescription(XNContext ctx, String desc) {
		XOMVariant v = XIONUtil.parseDescriptor(ctx, desc.trim());
		return (v instanceof XOMFile && ((XOMFile)v).isFork());
	}
	private XOMVariant morphFromDescription(XNContext ctx, String desc) {
		XOMVariant v = XIONUtil.parseDescriptor(ctx, desc.trim());
		if (v instanceof XOMFile && ((XOMFile)v).isFork()) return v;
		else return null;
	}
	protected boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		if (instance instanceof XOMFile && ((XOMFile)instance).isFork()) {
			return true;
		}
		else if ((instance instanceof XOMList || instance instanceof XOMListChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMFile && ((XOMFile)instance.toList(ctx).get(0)).isFork()) {
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
	protected XOMFile makeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		if (instance instanceof XOMFile && ((XOMFile)instance).isFork()) {
			return (XOMFile)instance;
		}
		else if ((instance instanceof XOMList || instance instanceof XOMListChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMFile && ((XOMFile)instance.toList(ctx).get(0)).isFork()) {
			return (XOMFile)instance.toList(ctx).get(0);
		}
		else {
			XOMVariant v = morphFromDescription(ctx, instance.toTextString(ctx));
			if (v instanceof XOMFile) return (XOMFile)v;
			else throw new XOMMorphError(typeName);
		}
	}
	protected XOMFile makeInstanceFromImpl(XNContext ctx, XOMVariant left, XOMVariant right) {
		if (left instanceof XOMEmpty) {
			return makeInstanceFromImpl(ctx, right);
		}
		else if (right instanceof XOMEmpty) {
			return makeInstanceFromImpl(ctx, left);
		}
		else {
			XOMVariant v = morphFromDescription(ctx, left.toTextString(ctx) + right.toTextString(ctx));
			if (v instanceof XOMFile) return (XOMFile)v;
			else throw new XOMMorphError(typeName);
		}
	}
}
