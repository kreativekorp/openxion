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

package com.kreative.openxion.xom;

import java.io.Serializable;
import java.util.List;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.XNScriptError;
import com.kreative.openxion.ast.XNModifier;

/**
 * XOMVariant represents all variants (instances of data types) in XION.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public abstract class XOMVariant implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	public XOMVariant unwrap(XNContext ctx) {
		return this;
	}
	
	public boolean hasParent(XNContext ctx) { return false; }
	public XOMVariant getParent(XNContext ctx) { throw new XNScriptError("Can't understand this"); }

	public boolean canDelete(XNContext ctx) { return false; }
	public void delete(XNContext ctx) { throw new XNScriptError("Can't delete this"); }
	
	public boolean canGetContents(XNContext ctx) { return false; }
	public XOMVariant getContents(XNContext ctx) { throw new XNScriptError("Can't get contents of this"); }
	
	public boolean canPutContents(XNContext ctx) { return false; }
	public void putIntoContents(XNContext ctx, XOMVariant contents) { throw new XNScriptError("Can't put into this"); }
	public void putBeforeContents(XNContext ctx, XOMVariant contents) { throw new XNScriptError("Can't put before this"); }
	public void putAfterContents(XNContext ctx, XOMVariant contents) { throw new XNScriptError("Can't put after this"); }
	public void putIntoContents(XNContext ctx, XOMVariant contents, String property, XOMVariant value) { throw new XNScriptError("Can't put into this"); }
	public void putBeforeContents(XNContext ctx, XOMVariant contents, String property, XOMVariant value) { throw new XNScriptError("Can't put before this"); }
	public void putAfterContents(XNContext ctx, XOMVariant contents, String property, XOMVariant value) { throw new XNScriptError("Can't put after this"); }
	
	public boolean canSortContents(XNContext ctx) { return false; }
	public void sortContents(XNContext ctx, XOMComparator cmp) { throw new XNScriptError("Can't sort this"); }
	
	public boolean canGetProperty(XNContext ctx, String property) { return false; }
	public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String property) { throw new XNScriptError("Can't get that property"); }
	
	public boolean canSetProperty(XNContext ctx, String property) { return false; }
	public void setProperty(XNContext ctx, String property, XOMVariant value) { throw new XNScriptError("Can't set that property"); }
	
	public final boolean equals(Object o) {
		return equalsImpl(o);
	}
	protected abstract boolean equalsImpl(Object o);
	public abstract int hashCode();
	public final String toString() {
		return toDescriptionString();
	}
	public abstract String toDescriptionString();
	public abstract String toTextString(XNContext ctx);
	public abstract List<XOMVariant> toList(XNContext ctx);
}
