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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.XNScriptError;
import com.kreative.openxion.ast.XNModifier;
import com.kreative.openxion.xom.inst.XOMBinary;
import com.kreative.openxion.xom.inst.XOMDictionary;
import com.kreative.openxion.xom.inst.XOMList;
import com.kreative.openxion.xom.inst.XOMString;

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
public final class XOMVariable extends XOMVariant {
	private static final long serialVersionUID = 1L;
	
	private String name;
	
	public XOMVariable(String name) {
		this.name = name;
	}
	
	public boolean declared(XNContext ctx) {
		XOMVariant v = ctx.getVariableMap(name).getVariable(ctx, name);
		return v != null;
	}
	
	public final XOMVariant asGiven() {
		return this;
	}
	public final XOMVariant asValue(XNContext ctx) {
		XOMVariant v = ctx.getVariableMap(name).getVariable(ctx, name);
		if (v != null) return v.asGiven();
		else return new XOMString(name);
	}
	public final XOMVariant asContents(XNContext ctx) {
		XOMVariant v = ctx.getVariableMap(name).getVariable(ctx, name);
		if (v != null) return v.asGiven();
		else return new XOMString(name);
	}
	public final XOMVariant asPrimitive(XNContext ctx) {
		XOMVariant v = ctx.getVariableMap(name).getVariable(ctx, name);
		if (v != null) return v.asPrimitive(ctx);
		else return new XOMString(name);
	}
	public final XOMVariant asContainer(XNContext ctx) {
		return this;
	}
	public final XOMVariable asVariable(XNContext ctx) {
		return this;
	}
	
	public final boolean canGetParent(XNContext ctx) {
		return getContents(ctx).asGiven().canGetParent(ctx);
	}
	public final XOMVariant getParent(XNContext ctx) {
		return getContents(ctx).asGiven().getParent(ctx);
	}
	
	public final boolean canDelete(XNContext ctx) {
		return getContents(ctx).asGiven().canDelete(ctx);
	}
	public final void delete(XNContext ctx) {
		getContents(ctx).asGiven().delete(ctx);
	}
	
	public final boolean canGetContents(XNContext ctx) {
		return true;
	}
	public final XOMVariant getContents(XNContext ctx) {
		XOMVariant v = ctx.getVariableMap(name).getVariable(ctx, name);
		if (v != null) return v.asGiven();
		else return new XOMString(name);
	}
	
	public final boolean canPutContents(XNContext ctx) {
		return true;
	}
	public final void putIntoContents(XNContext ctx, XOMVariant contents) {
		ctx.getVariableMap(name).setVariable(ctx, name, contents.asValue(ctx));
	}
	public final void putBeforeContents(XNContext ctx, XOMVariant contents) {
		ctx.getVariableMap(name).prependVariable(ctx, name, contents.asValue(ctx));
	}
	public final void putAfterContents(XNContext ctx, XOMVariant contents) {
		ctx.getVariableMap(name).appendVariable(ctx, name, contents.asValue(ctx));
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
			v = v.asPrimitive(ctx);
			List<XOMVariant> toSort = new Vector<XOMVariant>();
			if (v instanceof XOMList) {
				toSort.addAll(((XOMList)v).toList());
			} else if (v instanceof XOMDictionary) {
				for (String s : ((XOMDictionary)v).toMap().keySet())
					toSort.add(new XOMString(s));
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
			} else if (v instanceof XOMDictionary) {
				Map<String, XOMVariant> oldMap = ((XOMDictionary)v).toMap();
				Map<String, XOMVariant> newMap = new LinkedHashMap<String, XOMVariant>();
				for (XOMVariant key : toSort)
					newMap.put(key.toTextString(ctx), oldMap.get(key.toTextString(ctx)).asGiven());
				ctx.getVariableMap(name).setVariable(ctx, name, new XOMDictionary(newMap));
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
		return getContents(ctx).asGiven().canGetProperty(ctx, property);
	}
	public final XOMVariant getProperty(XNContext ctx, XNModifier modifier, String property) {
		return getContents(ctx).asGiven().getProperty(ctx, modifier, property);
	}
	
	public final boolean canSetProperty(XNContext ctx, String property) {
		return getContents(ctx).asGiven().canSetProperty(ctx, property);
	}
	public final void setProperty(XNContext ctx, String property, XOMVariant value) {
		getContents(ctx).asGiven().setProperty(ctx, property, value);
	}
	
	protected final String toLanguageStringImpl() {
		return name;
	}
	protected final String toTextStringImpl(XNContext ctx) {
		XOMVariant v = ctx.getVariableMap(name).getVariable(ctx, name);
		if (v != null) return v.toTextString(ctx);
		else return name;
	}
	protected final int hashCodeImpl() {
		return this.name.toLowerCase().hashCode();
	}
	protected final boolean equalsImpl(XOMVariant other) {
		if (other instanceof XOMVariable) {
			XOMVariable v = (XOMVariable)other;
			return this.name.equalsIgnoreCase(v.name);
		} else {
			return false;
		}
	}
}