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
import com.kreative.openxion.util.XIONUtil;
import com.kreative.openxion.xom.XOMComparator;
import com.kreative.openxion.xom.XOMContainerObject;
import com.kreative.openxion.xom.XOMVariableMap;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.type.XOMUserObjectType;

public class XOMUserObject extends XOMContainerObject implements XNResponder {
	private static final long serialVersionUID = 1L;
	
	public static final XOMUserObject NULL = new XOMUserObject();
	
	/*
	 * You may assume these are either both null, or both defined
	 */
	private XOMUserObjectType type;
	private XOMVariableMap sharedVariables;
	private Map<String, XOMVariableMap> staticVariables;
	private int id;
	
	private XOMUserObject() {
		this.type = null;
		sharedVariables = null;
		staticVariables = null;
		this.id = 0;
	}
	
	private XOMUserObject(XOMUserObjectType type, XOMVariableMap shv, Map<String, XOMVariableMap> stv, int id) {
		this.type = type;
		sharedVariables = shv;
		staticVariables = stv;
		this.id = id;
	}
	
	public XOMUserObject(XOMUserObjectType type, int id) {
		this.type = (type == null) ? null : type;
		sharedVariables = (type == null) ? null : new XOMVariableMap();
		staticVariables = (type == null) ? null : new HashMap<String, XOMVariableMap>();
		this.id = (type == null) ? 0 : id;
	}
	
	public XOMUserObject asSuper() {
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
		return sharedVariables;
	}
	
	public XOMVariableMap staticVariables(String name) {
		name = XIONUtil.normalizeVarName(name);
		if (staticVariables.containsKey(name))
			return staticVariables.get(name);
		XOMVariableMap vm = new XOMVariableMap();
		staticVariables.put(name, vm);
		return vm;
	}
	
	public boolean canDelete(XNContext ctx) {
		if (type == null) return false;
		else return type.canDelete(ctx, this);
	}
	public void delete(XNContext ctx) {
		if (type == null) throw new XNScriptError("Can't access a null object");
		else type.delete(ctx, this);
	}
	
	public boolean canGetContents(XNContext ctx) {
		if (type == null) return false;
		else return type.canGetContents(ctx, this);
	}
	public XOMVariant getContents(XNContext ctx) {
		if (type == null) throw new XNScriptError("Can't access a null object");
		else return type.getContents(ctx, this);
	}
	
	public boolean canPutContents(XNContext ctx) {
		if (type == null) return false;
		else return type.canPutContents(ctx, this);
	}
	public void putIntoContents(XNContext ctx, XOMVariant contents) {
		if (type == null) throw new XNScriptError("Can't access a null object");
		else type.putIntoContents(ctx, this, contents);
	}
	public void putBeforeContents(XNContext ctx, XOMVariant contents) {
		if (type == null) throw new XNScriptError("Can't access a null object");
		else type.putBeforeContents(ctx, this, contents);
	}
	public void putAfterContents(XNContext ctx, XOMVariant contents) {
		if (type == null) throw new XNScriptError("Can't access a null object");
		else type.putAfterContents(ctx, this, contents);
	}
	public void putIntoContents(XNContext ctx, XOMVariant contents, String property, XOMVariant pvalue) {
		if (type == null) throw new XNScriptError("Can't access a null object");
		else type.putIntoContents(ctx, this, contents, property, pvalue);
	}
	public void putBeforeContents(XNContext ctx, XOMVariant contents, String property, XOMVariant pvalue) {
		if (type == null) throw new XNScriptError("Can't access a null object");
		else type.putBeforeContents(ctx, this, contents, property, pvalue);
	}
	public void putAfterContents(XNContext ctx, XOMVariant contents, String property, XOMVariant pvalue) {
		if (type == null) throw new XNScriptError("Can't access a null object");
		else type.putAfterContents(ctx, this, contents, property, pvalue);
	}
	
	public boolean canSortContents(XNContext ctx) {
		return false;
	}
	public void sortContents(XNContext ctx, XOMComparator cmp) {
		throw new XNScriptError("Can't understand this");
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
	
	protected String toLanguageStringImpl() {
		if (type == null) return "empty";
		else return type.toLanguageString(this);
	}
	protected String toTextStringImpl(XNContext ctx) {
		if (type == null) return "";
		else return type.toTextString(ctx, this);
	}
	protected int hashCodeImpl() {
		return id;
	}
	protected boolean equalsImpl(XOMVariant o) {
		return this == o;
	}
}