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

package com.kreative.openxion;

import java.io.Serializable;
import java.util.*;
import com.kreative.openxion.ast.XNVariableScope;
import com.kreative.openxion.ast.XNMessageHandler;
import com.kreative.openxion.ast.XNFunctionHandler;
import com.kreative.openxion.util.XIONUtil;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMDataType;
import com.kreative.openxion.xom.XOMVariable;

/**
 * XNStackFrame represents the local state of the invocation of a handler.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XNStackFrame implements Serializable, Cloneable {
	private static final long serialVersionUID = 9999L;

	private String handlerName;
	private List<XOMVariant> parameters;
	private Map<String, XNVariableScope> variableScopes;
	private Map<String, XOMVariable> localVariables;
	private Map<String, XNMessageHandler> localUserCommands;
	private Map<String, XNFunctionHandler> localUserFunctions;
	
	public XNStackFrame(String handlerName, List<XOMVariant> parameters) {
		this.handlerName = handlerName;
		this.parameters = parameters;
		this.variableScopes = new HashMap<String, XNVariableScope>();
		this.localVariables = new HashMap<String, XOMVariable>();
		this.localUserCommands = new HashMap<String, XNMessageHandler>();
		this.localUserFunctions = new HashMap<String, XNFunctionHandler>();
	}
	
	public String getHandlerName() {
		return handlerName;
	}
	
	public List<XOMVariant> getParameters() {
		return parameters;
	}
	
	public XNVariableScope getVariableScope(String name) {
		name = XIONUtil.normalizeVarName(name);
		if (variableScopes.containsKey(name))
			return variableScopes.get(name);
		else
			return XNVariableScope.LOCAL;
	}
	
	public void setVariableScope(String name, XNVariableScope scope) {
		name = XIONUtil.normalizeVarName(name);
		variableScopes.put(name, scope);
	}
	
	public XOMVariable createLocalVariable(XNContext ctx, String name, XOMDataType<? extends XOMVariant> type, XOMVariant initialvalue) {
		name = XIONUtil.normalizeVarName(name);
		if (!localVariables.containsKey(name)) {
			XOMVariable v = new XOMVariable(ctx, type, initialvalue);
			localVariables.put(name, v);
			return v;
		} else {
			return localVariables.get(name);
		}
	}
	
	public XOMVariable getLocalVariable(XNContext ctx, String name) {
		name = XIONUtil.normalizeVarName(name);
		if (!localVariables.containsKey(name)) {
			return null;
		} else {
			return localVariables.get(name);
		}
	}
	
	public void defineLocalUserCommand(String name, XNMessageHandler handler) {
		name = XIONUtil.normalizeVarName(name);
		localUserCommands.put(name, handler);
	}
	
	public XNMessageHandler getLocalUserCommand(String name) {
		name = XIONUtil.normalizeVarName(name);
		return localUserCommands.get(name);
	}
	
	public void defineLocalUserFunction(String name, XNFunctionHandler handler) {
		name = XIONUtil.normalizeVarName(name);
		localUserFunctions.put(name, handler);
	}
	
	public XNFunctionHandler getLocalUserFunction(String name) {
		name = XIONUtil.normalizeVarName(name);
		return localUserFunctions.get(name);
	}
}
