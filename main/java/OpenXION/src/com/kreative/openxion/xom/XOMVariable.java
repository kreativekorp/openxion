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

import java.util.*;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.XNScriptError;
import com.kreative.openxion.ast.XNModifier;
import com.kreative.openxion.xom.inst.XOMString;
import com.kreative.openxion.xom.inst.XOMBinary;
import com.kreative.openxion.xom.inst.XOMList;

/**
 * XOMVariable represents a variable in a XION program.
 * It is specially handled separately from other kinds of XOMVariants.
 * <p>
 * Starting with OpenXION 1.3, the actual type and contents of the variable
 * is not stored here. Rather, it is stored in the XNContext, where it belongs.
 * This is a requirement for proper handling of unquoted literals,
 * and is part of Issue the Thirteenth.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XOMVariable extends XOMVariant {
	private static final long serialVersionUID = 1L;
	
	private String name;
	
	public XOMVariable(String name) {
		this.name = name;
	}
	
	public boolean isDeclared(XNContext ctx) {
		return ctx.getVariableMap(name).isVariableDeclared(ctx, name);
	}
	
	public XOMVariant unwrap(XNContext ctx) {
		return getContents(ctx);
	}
	
	public final boolean canGetParent(XNContext ctx) {
		return getContents(ctx).canGetParent(ctx);
	}
	public final XOMVariant getParent(XNContext ctx) {
		return getContents(ctx).getParent(ctx);
	}
	
	public final boolean canDelete(XNContext ctx) {
		return getContents(ctx).canDelete(ctx);
	}
	public final void delete(XNContext ctx) {
		getContents(ctx).delete(ctx);
	}
	
	public final boolean canGetContents(XNContext ctx) {
		return true;
	}
	public final XOMVariant getContents(XNContext ctx) {
		XOMVariableMap vm = ctx.getVariableMap(name);
		if (vm.isVariableDeclared(ctx, name))
			return vm.getVariable(ctx, name);
		else
			return new XOMString(name);
	}
	
	public final boolean canPutContents(XNContext ctx) {
		return true;
	}
	public final void putIntoContents(XNContext ctx, XOMVariant contents) {
		ctx.getVariableMap(name).setVariable(ctx, name, contents.unwrap(ctx));
	}
	public final void putBeforeContents(XNContext ctx, XOMVariant contents) {
		ctx.getVariableMap(name).prependVariable(ctx, name, contents.unwrap(ctx));
	}
	public final void putAfterContents(XNContext ctx, XOMVariant contents) {
		ctx.getVariableMap(name).appendVariable(ctx, name, contents.unwrap(ctx));
	}
	public final void putIntoContents(XNContext ctx, XOMVariant contents, String property, XOMVariant value) {
		throw new XNScriptError("Can't understand this");
	}
	public final void putBeforeContents(XNContext ctx, XOMVariant contents, String property, XOMVariant value) {
		throw new XNScriptError("Can't understand this");
	}
	public final void putAfterContents(XNContext ctx, XOMVariant contents, String property, XOMVariant value) {
		throw new XNScriptError("Can't understand this");
	}
	
	public final boolean canSortContents(XNContext ctx) {
		XOMVariant v = ctx.getVariableMap(name).getVariable(ctx, name);
		return v != null;
	}
	public final void sortContents(XNContext ctx, XOMComparator cmp) {
		XOMVariant v = ctx.getVariableMap(name).getVariable(ctx, name);
		if (v != null) {
			List<XOMVariant> toSort = new Vector<XOMVariant>();
			if (v instanceof XOMList) {
				toSort.addAll(((XOMList)v).toList(ctx));
			} else if (v instanceof XOMBinary) {
				for (byte b : ((XOMBinary)v).toByteArray())
					toSort.add(new XOMBinary(new byte[]{b}));
			} else {
				for (String s : v.toTextString(ctx).split("\r\n|\r|\n|\u2028|\u2029"))
					toSort.add(new XOMString(s));
			}
			Collections.sort(toSort, cmp);
			if (v instanceof XOMList) {
				ctx.getVariableMap(name).setVariable(ctx, name, new XOMList(toSort));
			} else if (v instanceof XOMBinary) {
				byte[] b = new byte[toSort.size()];
				for (int i = 0; i < b.length; i++) {
					b[i] = ((XOMBinary)toSort.get(i)).toByteArray()[0];
				}
				ctx.getVariableMap(name).setVariable(ctx, name, new XOMBinary(b));
			} else {
				String endl = ctx.getLineEnding();
				StringBuffer s = new StringBuffer();
				for (XOMVariant line : toSort) {
					s.append(line.toTextString(ctx));
					s.append(endl);
				}
				if (s.length() >= endl.length()) {
					s.delete(s.length()-endl.length(), s.length());
				}
				ctx.getVariableMap(name).setVariable(ctx, name, new XOMString(s.toString()));
			}
		}
		else throw new XNScriptError("Can't understand this");
	}
	
	public final boolean canGetProperty(XNContext ctx, String property) {
		return getContents(ctx).canGetProperty(ctx, property);
	}
	public final XOMVariant getProperty(XNContext ctx, XNModifier modifier, String property) {
		return getContents(ctx).getProperty(ctx, modifier, property);
	}
	
	public final boolean canSetProperty(XNContext ctx, String property) {
		return getContents(ctx).canSetProperty(ctx, property);
	}
	public final void setProperty(XNContext ctx, String property, XOMVariant value) {
		getContents(ctx).setProperty(ctx, property, value);
	}
	
	public boolean equalsImpl(Object o) {
		return (o instanceof XOMVariable) && this.name.equalsIgnoreCase(((XOMVariable)o).name);
	}
	public int hashCode() {
		return name.hashCode();
	}
	public String toDescriptionString() {
		return name;
	}
	public String toTextString(XNContext ctx) {
		return getContents(ctx).toTextString(ctx);
	}
	public List<XOMVariant> toList(XNContext ctx) {
		return getContents(ctx).toList(ctx);
	}
}
