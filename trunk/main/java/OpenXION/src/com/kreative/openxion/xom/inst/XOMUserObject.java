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

package com.kreative.openxion.xom.inst;

import java.util.*;

import com.kreative.openxion.XNContext;
import com.kreative.openxion.XNResponder;
import com.kreative.openxion.XNHandlerExit;
import com.kreative.openxion.XNScriptError;
import com.kreative.openxion.ast.XNModifier;
import com.kreative.openxion.ast.XNExpression;
import com.kreative.openxion.xom.XOMObject;
import com.kreative.openxion.xom.XOMStaticVariableMap;
import com.kreative.openxion.xom.XOMVariableMap;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.type.XOMUserObjectType;

public class XOMUserObject extends XOMObject implements XNResponder {
	private static final long serialVersionUID = 1L;
	
	public static final XOMUserObject NULL = new XOMUserObject();
	
	/*
	 * You may assume these are either both null, or both defined
	 */
	private XOMUserObjectType type;
	private XOMVariableMap sharedVariables;
	private XOMStaticVariableMap staticVariables;
	private int id;
	
	private XOMUserObject() {
		this.type = null;
		this.sharedVariables = null;
		this.staticVariables = null;
		this.id = 0;
	}
	
	private XOMUserObject(XOMUserObjectType type, XOMVariableMap shvars, XOMStaticVariableMap statvars, int id) {
		this.type = type;
		this.sharedVariables = shvars;
		this.staticVariables = statvars;
		this.id = id;
	}
	
	public XOMUserObject(XOMUserObjectType type, int id) {
		this.type = (type == null) ? null : type;
		this.sharedVariables = (type == null) ? null : new XOMVariableMap();
		this.staticVariables = (type == null) ? null : new XOMStaticVariableMap();
		this.id = (type == null) ? 0 : id;
	}
	
	public XOMVariant asSuper() {
		if (type != null && type.superType() != null) {
			return new XOMUserObject(type.superType(), sharedVariables, staticVariables, id);
		} else {
			return this;
		}
	}
	
	public boolean isInstanceOf(XOMUserObjectType ut) {
		if (type == null || ut == null) return true;
		else {
			XOMUserObjectType thisType = type;
			while (thisType != null) {
				if (thisType.equals(ut)) return true;
				else thisType = thisType.superType();
			}
			return false;
		}
	}
	
	public XOMVariableMap sharedVariables() {
		if (sharedVariables == null) throw new XNScriptError("Can't access a null object");
		return sharedVariables;
	}
	
	public XOMStaticVariableMap staticVariables() {
		if (staticVariables == null) throw new XNScriptError("Can't access a null object");
		return staticVariables;
	}

	public boolean canDelete(XNContext ctx) {
		if (type == null) return false;
		else return type.canDelete(ctx, this);
	}
	public void delete(XNContext ctx) {
		if (type == null) throw new XNScriptError("Can't access a null object");
		else type.delete(ctx, this);
	}
	
	public boolean canGetProperty(XNContext ctx, String property) {
		if (type == null) return false;
		else return type.canGetProperty(ctx, this, property);
	}
	public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String property) {
		if (type == null) throw new XNScriptError("Can't access a null object");
		else return type.getProperty(ctx, this, modifier, property);
	}
	
	public boolean canSetProperty(XNContext ctx, String property) {
		if (type == null) return false;
		else return type.canSetProperty(ctx, this, property);
	}
	public void setProperty(XNContext ctx, String property, XOMVariant value) {
		if (type == null) throw new XNScriptError("Can't access a null object");
		else type.setProperty(ctx, this, property, value);
	}

	public XNHandlerExit evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
		if (type == null) return XNHandlerExit.passed();
		else return type.evaluateFunction(ctx, this, functionName, modifier, parameter);
	}

	public XNHandlerExit executeCommand(XNContext ctx, String commandName, List<XNExpression> parameters) {
		if (type == null) return XNHandlerExit.passed();
		else return type.executeCommand(ctx, this, commandName, parameters);
	}

	public XNResponder nextResponder() {
		if (type != null && type.superType() != null) {
			return new XOMUserObject(type.superType(), sharedVariables, staticVariables, id);
		} else {
			return null;
		}
	}
	
	public String toLanguageString() {
		if (type == null) return "empty";
		else return type.toLanguageString(this);
	}
	public String toTextString(XNContext ctx) {
		if (type == null) return "";
		else return type.toTextString(ctx, this);
	}
	public List<? extends XOMVariant> toList(XNContext ctx) {
		return Arrays.asList(this);
	}
	public int hashCode() {
		return id;
	}
	public boolean equals(Object o) {
		return this == o;
	}
}
