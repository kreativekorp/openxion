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

import java.util.LinkedHashMap;
import java.util.Map;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.xom.inst.XOMEmpty;
import com.kreative.openxion.xom.inst.XOMString;
import com.kreative.openxion.xom.type.XOMStringType;

/**
 * XOMVariableMap keeps a map of variable assignments.
 * @since OpenXION 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XOMVariableMap {
	private static final class CIString {
		private String s;
		public CIString(String s) {
			this.s = s;
		}
		public String toString() {
			return s;
		}
		public int hashCode() {
			return s.toLowerCase().hashCode();
		}
		public boolean equals(Object o) {
			return s.equalsIgnoreCase(o.toString());
		}
	}
	
	private Map<CIString, XOMDataType<? extends XOMVariant>> types;
	private Map<CIString, XOMVariant> values;
	
	public XOMVariableMap() {
		types = new LinkedHashMap<CIString, XOMDataType<? extends XOMVariant>>();
		values = new LinkedHashMap<CIString, XOMVariant>();
	}
	
	public XOMVariable declareVariable(XNContext ctx, String name) {
		CIString ciname = new CIString(name);
		types.put(ciname, XOMStringType.instance);
		values.put(ciname, XOMString.EMPTY_STRING);
		return new XOMVariable(this, name);
	}
	
	public XOMVariable declareVariable(XNContext ctx, String name, XOMDataType<? extends XOMVariant> type) {
		CIString ciname = new CIString(name);
		types.put(ciname, type);
		values.put(ciname, type.makeInstanceFrom(ctx, XOMEmpty.EMPTY));
		return new XOMVariable(this, name);
	}
	
	public XOMVariable declareVariable(XNContext ctx, String name, XOMVariant value) {
		CIString ciname = new CIString(name);
		types.put(ciname, XOMStringType.instance);
		values.put(ciname, new XOMString(value.toTextString(ctx)));
		return new XOMVariable(this, name);
	}
	
	public XOMVariable declareVariable(XNContext ctx, String name, XOMDataType<? extends XOMVariant> type, XOMVariant value) {
		CIString ciname = new CIString(name);
		types.put(ciname, type);
		values.put(ciname, type.makeInstanceFrom(ctx, value));
		return new XOMVariable(this, name);
	}
	
	public boolean isVariableDeclared(XNContext ctx, String name) {
		CIString ciname = new CIString(name);
		return (types.containsKey(ciname) || values.containsKey(ciname));
	}
	
	public XOMVariant getVariable(XNContext ctx, String name) {
		CIString ciname = new CIString(name);
		return (values.containsKey(ciname)) ? values.get(ciname) : null;
	}
	
	public void setVariable(XNContext ctx, String name, XOMVariant value) {
		CIString ciname = new CIString(name);
		if (!types.containsKey(ciname)) types.put(ciname, XOMStringType.instance);
		values.put(ciname, types.get(ciname).makeInstanceFrom(ctx, value.asPrimitive(ctx)));
	}
	
	public void prependVariable(XNContext ctx, String name, XOMVariant value) {
		CIString ciname = new CIString(name);
		if (!types.containsKey(ciname)) types.put(ciname, XOMStringType.instance);
		if (!values.containsKey(ciname)) values.put(ciname, XOMEmpty.EMPTY);
		values.put(ciname, types.get(ciname).makeInstanceFrom(ctx, value.asPrimitive(ctx), values.get(ciname)));
	}
	
	public void appendVariable(XNContext ctx, String name, XOMVariant value) {
		CIString ciname = new CIString(name);
		if (!types.containsKey(ciname)) types.put(ciname, XOMStringType.instance);
		if (!values.containsKey(ciname)) values.put(ciname, XOMEmpty.EMPTY);
		values.put(ciname, types.get(ciname).makeInstanceFrom(ctx, values.get(ciname), value.asPrimitive(ctx)));
	}
	
	public void merge(XOMVariableMap vm) {
		types.putAll(vm.types);
		values.putAll(vm.values);
	}
}
