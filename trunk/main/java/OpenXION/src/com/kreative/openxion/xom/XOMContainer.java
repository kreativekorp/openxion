/*
 * Copyright &copy; 2011 Rebecca G. Bettencourt / Kreative Software
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
 * @since OpenXION 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.xom;

import com.kreative.openxion.XNContext;
import com.kreative.openxion.XNScriptError;
import com.kreative.openxion.ast.XNModifier;
import com.kreative.openxion.util.XIONUtil;

/**
 * XOMContainerObject is the recommended base class for
 * a container or potentially-container object in XION.
 * @since OpenXION 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public abstract class XOMContainer extends XOMVariant {
	private static final long serialVersionUID = 1L;
	
	public final XOMVariant asValue(XNContext ctx) {
		return this;
	}
	public final XOMVariant asPrimitive(XNContext ctx) {
		if (canGetContents(ctx)) return getContents(ctx).asPrimitive(ctx);
		else return this;
	}
	public final XOMVariant asContainer(XNContext ctx, boolean resolveVariableNames) {
		if (canGetContents(ctx)) return this;
		String s = toTextString(ctx);
		if (!resolveVariableNames) throw new XNScriptError("Expected a variable name but found " + s);
		XOMVariable v = XIONUtil.parseVariableName(ctx, s);
		if (v == null) throw new XNScriptError("Expected a variable name but found " + s);
		else return v;
	}
	public final XOMVariable asVariable(XNContext ctx, boolean resolveVariableNames) {
		String s = toTextString(ctx);
		if (!resolveVariableNames) throw new XNScriptError("Expected a variable name but found " + s);
		XOMVariable v = XIONUtil.parseVariableName(ctx, s);
		if (v == null) throw new XNScriptError("Expected a variable name but found " + s);
		else return v;
	}
	
	public boolean canGetParent(XNContext ctx) { return false; }
	public XOMVariant getParent(XNContext ctx) { throw new XNScriptError("Can't understand this"); }
	
	public boolean canDelete(XNContext ctx) { return false; }
	public void delete(XNContext ctx) { throw new XNScriptError("Can't understand this"); }
	
	public abstract boolean canGetContents(XNContext ctx);
	public abstract XOMVariant getContents(XNContext ctx);
	
	public boolean canPutContents(XNContext ctx) { return false; }
	public void putIntoContents(XNContext ctx, XOMVariant contents) { throw new XNScriptError("Can't understand this"); }
	public void putBeforeContents(XNContext ctx, XOMVariant contents) { throw new XNScriptError("Can't understand this"); }
	public void putAfterContents(XNContext ctx, XOMVariant contents) { throw new XNScriptError("Can't understand this"); }
	public void putIntoContents(XNContext ctx, XOMVariant contents, String property, XOMVariant value) { throw new XNScriptError("Can't understand this"); }
	public void putBeforeContents(XNContext ctx, XOMVariant contents, String property, XOMVariant value) { throw new XNScriptError("Can't understand this"); }
	public void putAfterContents(XNContext ctx, XOMVariant contents, String property, XOMVariant value) { throw new XNScriptError("Can't understand this"); }
	
	public boolean canSortContents(XNContext ctx) { return false; }
	public void sortContents(XNContext ctx, XOMComparator cmp) { throw new XNScriptError("Can't understand this"); }
	
	public boolean canGetProperty(XNContext ctx, String property) { return false; }
	public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String property) { throw new XNScriptError("Can't understand this"); }
	
	public boolean canSetProperty(XNContext ctx, String property) { return false; }
	public void setProperty(XNContext ctx, String property, XOMVariant value) { throw new XNScriptError("Can't understand this"); }
}
