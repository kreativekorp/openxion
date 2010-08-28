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

package com.kreative.openxion.xom.inst;

import java.io.*;
import java.util.*;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.XNSecurityKey;
import com.kreative.openxion.XNScriptError;
import com.kreative.openxion.util.XIONUtil;
import com.kreative.openxion.ast.XNModifier;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.type.XOMDateType;

public class XOMFile extends XOMVariant {
	private static final long serialVersionUID = 1L;
	
	private File theFile;
	
	public XOMFile(String path) {
		this.theFile = new File(path).getAbsoluteFile();
	}
	
	public XOMFile(File theFile) {
		this.theFile = theFile.getAbsoluteFile();
	}
	
	public boolean isFolder() {
		return theFile != null && theFile.isDirectory();
	}
	
	public boolean isFile() {
		return theFile != null && !theFile.isDirectory();
	}
	
	public boolean isDisk() {
		return theFile != null && theFile.isDirectory() && theFile.getParentFile() == null;
	}
	
	public boolean isFork() {
		return theFile != null && !theFile.isDirectory() && theFile.getParentFile() != null && theFile.getParentFile().getName().equals("..namedfork");
	}
	
	public File toFile() {
		return theFile;
	}
	
	public boolean hasParent(XNContext ctx) {
		return ctx.allow(XNSecurityKey.FILE_SYSTEM_READ) && !(theFile == null || theFile.getParentFile() == null);
	}
	public XOMVariant getParent(XNContext ctx) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ))
			throw new XNScriptError("Security settings do not allow file system access");
		if (theFile == null || theFile.getParentFile() == null) {
			return null;
		} else {
			return new XOMFile(theFile.getParentFile());
		}
	}
	
	public boolean canDelete(XNContext ctx) {
		return ctx.allow(XNSecurityKey.FILE_SYSTEM_WRITE);
	}
	public void delete(XNContext ctx) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_WRITE))
			throw new XNScriptError("Security settings do not allow file system access");
		if (!theFile.delete()) {
			super.delete(ctx);
		}
	}
	
	public boolean canGetProperty(XNContext ctx, String property) {
		return ctx.allow(XNSecurityKey.FILE_SYSTEM_READ) && (
				property.equalsIgnoreCase("name") ||
				property.equalsIgnoreCase("path") ||
				property.equalsIgnoreCase("modificationDate") ||
				(property.equalsIgnoreCase("size") && !theFile.isDirectory()) ||
				(property.equalsIgnoreCase("count") && theFile.isDirectory())
		);
	}
	public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String property) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ))
			throw new XNScriptError("Security settings do not allow file system access");
		if (property.equalsIgnoreCase("name")) {
			return new XOMString(theFile.getName());
		} else if (property.equalsIgnoreCase("path")) {
			return new XOMString(theFile.getAbsolutePath());
		} else if (property.equalsIgnoreCase("modificationDate")) {
			return new XOMDate(new Date(theFile.lastModified()));
		} else if (property.equalsIgnoreCase("size") && !theFile.isDirectory()) {
			return new XOMInteger(theFile.length());
		} else if (property.equalsIgnoreCase("count") && theFile.isDirectory()) {
			return new XOMInteger(theFile.listFiles().length);
		} else {
			return super.getProperty(ctx, modifier, property);
		}
	}
	
	public boolean canSetProperty(XNContext ctx, String property) {
		return ctx.allow(XNSecurityKey.FILE_SYSTEM_WRITE) && (
				property.equalsIgnoreCase("name") ||
				property.equalsIgnoreCase("path") ||
				property.equalsIgnoreCase("modificationDate")
		);
	}
	public void setProperty(XNContext ctx, String property, XOMVariant value) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_WRITE))
			throw new XNScriptError("Security settings do not allow file system access");
		if (property.equalsIgnoreCase("name")) {
			theFile.renameTo(new File(theFile.getParentFile(), value.toTextString(ctx)));
		} else if (property.equalsIgnoreCase("path")) {
			theFile.renameTo(new File(value.toTextString(ctx)));
		} else if (property.equalsIgnoreCase("modificationDate")) {
			theFile.setLastModified(XOMDateType.instance.makeInstanceFrom(ctx, value).toDate().getTime());
		} else {
			super.setProperty(ctx, property, value);
		}
	}
	
	protected boolean equalsImpl(Object o) {
		if (o instanceof XOMFile) {
			XOMFile other = (XOMFile)o;
			if (this.theFile == null && other.theFile == null) {
				return true;
			}
			else if (this.theFile == null || other.theFile == null) {
				return false;
			}
			else {
				return this.theFile.equals(other.theFile);
			}
		} else {
			return false;
		}
	}
	public int hashCode() {
		return (theFile == null) ? 0 : theFile.hashCode();
	}
	public String toDescriptionString() {
		if (theFile.isDirectory()) {
			if (theFile.getParentFile() == null) {
				return "disk "+XIONUtil.quote(theFile.getAbsolutePath());
			} else {
				return "folder "+XIONUtil.quote(theFile.getAbsolutePath());
			}
		} else {
			if (theFile.getParentFile() != null && theFile.getParentFile().getName().equals("..namedfork")) {
				return "fork "+XIONUtil.quote(theFile.getAbsolutePath());
			} else {
				return "file "+XIONUtil.quote(theFile.getAbsolutePath());
			}
		}
	}
	public String toTextString(XNContext ctx) {
		if (theFile.isDirectory()) {
			if (theFile.getParentFile() == null) {
				return "disk "+XIONUtil.quote(theFile.getAbsolutePath());
			} else {
				return "folder "+XIONUtil.quote(theFile.getAbsolutePath());
			}
		} else {
			if (theFile.getParentFile() != null && theFile.getParentFile().getName().equals("..namedfork")) {
				return "fork "+XIONUtil.quote(theFile.getAbsolutePath());
			} else {
				return "file "+XIONUtil.quote(theFile.getAbsolutePath());
			}
		}
	}
	public List<XOMVariant> toList(XNContext ctx) {
		Vector<XOMVariant> v = new Vector<XOMVariant>();
		v.add(this);
		return v;
	}
}
