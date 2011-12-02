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

package com.kreative.openxion;

import java.io.Serializable;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import com.kreative.openxion.ast.XNMessageHandler;
import com.kreative.openxion.ast.XNFunctionHandler;
import com.kreative.openxion.ast.XNVariableScope;
import com.kreative.openxion.io.XNIOManager;
import com.kreative.openxion.io.XNIOMethod;
import com.kreative.openxion.math.MathProcessor;
import com.kreative.openxion.math.FastMath;
import com.kreative.openxion.util.XIONUtil;
import com.kreative.openxion.util.XNNumberFormat;
import com.kreative.openxion.xom.XOMDataType;
import com.kreative.openxion.xom.XOMStaticVariableMap;
import com.kreative.openxion.xom.XOMVariableMap;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.inst.XOMEmpty;

/**
 * XNContext is the main class responsible for the state of the
 * XION interpreter, including loaded modules, security settings,
 * variables, the message-passing hierarchy, and the call stack.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XNContext implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	private XNUI ui;
	private XNSecurityProfile security;
	private XNContext parent;
	
	/**
	 * Creates a new XNContext.
	 */
	public XNContext(XNUI ui) {
		this.ui = ui;
		this.security = new XNSecurityProfile();
		this.parent = null;
		initLanguageConstructs();
		initRuntime();
		initEnvironment();
		initStack();
	}
	
	/**
	 * Creates a new XNContext.
	 */
	public XNContext(XNUI ui, XNSecurityProfile security) {
		this.ui = ui;
		this.security = security;
		this.parent = null;
		initLanguageConstructs();
		initRuntime();
		initEnvironment();
		initStack();
	}
	
	/**
	 * Returns the XNUI used by this context to communicate with the outside world.
	 * @return the XNUI used by this context to communicate with the outside world.
	 */
	public XNUI getUI() {
		return ui;
	}
	
	/**
	 * Changes the XNUI used by this context to communicate with the outside world.
	 * @param ui the XNUI used by this context to communicate with the outside world.
	 */
	public void setUI(XNUI ui) {
		this.ui = ui;
	}
	
	/**
	 * Returns the security profile used to control security settings in this context.
	 * @return the security profile used to control security settings in this context.
	 */
	public XNSecurityProfile getSecurityProfile() {
		return security;
	}
	
	/**
	 * Changes the security profile used to control security settings in this context.
	 * @param security the security profile used to control security settings in this context.
	 */
	public void setSecurityProfile(XNSecurityProfile security) {
		this.security = security;
	}
	
	/**
	 * Returns the XNContext this XNContext was forked from, or null if this XNContext was created anew.
	 * @return the XNContext this XNContext was forked from, or null if this XNContext was created anew.
	 */
	public XNContext getParent() {
		return parent;
	}
	
	/**
	 * Resets the entire XNContext, so that it is in
	 * exactly the same state as when it was first
	 * instantiated. All loaded modules will need
	 * to be reloaded.
	 */
	public void resetAll() {
		initLanguageConstructs();
		initRuntime();
		initEnvironment();
		initStack();
	}
	
	/**
	 * Resets just the run-time state of the XNContext.
	 * Loaded modules will <i>not</i> need to be reloaded.
	 */
	public void reset() {
		initRuntime();
		initEnvironment();
		initStack();
	}
	
	/**
	 * Resets only the environment (global properties like username,
	 * applicationPaths, documentPaths, itemDelimiter, etc.).
	 */
	public void resetEnvironment() {
		initEnvironment();
	}
	
	/**
	 * Resets only the call stack.
	 */
	public void resetStack() {
		initStack();
	}
	
	/**
	 * Determines whether the current security settings allow for certain functionality.
	 * If security settings are set to ask the user, the user will be prompted.
	 * If the user chooses to always allow or always deny, the current security settings
	 * will be modified to reflect the user's choice.
	 * @param type the security key for the functionality being requested.
	 * @return true if the functionality is allowed, false otherwise.
	 */
	@SuppressWarnings("unchecked")
	public boolean allow(XNSecurityKey type, String... details) {
		if (security.containsKey(type)) {
			switch (security.get(type)) {
			case ALLOW: return true;
			case DENY: return false;
			}
		}
		XNSecurityKey[] t = new XNSecurityKey[]{ type };
		boolean[] a = new boolean[1];
		boolean[] fa = new boolean[1];
		Map<String,String> d = new LinkedHashMap<String,String>();
		for (int i = 0; i < details.length; i+=2) d.put(details[i], details[i+1]);
		Map<String,String>[] da = (Map<String,String>[])new Map[]{d};
		ui.promptSecurity(t, a, fa, da);
		if (fa[0]) security.put(type, a[0] ? XNSecurityValue.ALLOW : XNSecurityValue.DENY);
		return a[0];
	}
	
	/**
	 * Creates a new XNContext with the same loaded modules,
	 * global variables, and environment as this XNContext.
	 * @return a forked XNContext.
	 */
	public XNContext fork(boolean withStack) {
		return new XNContext(this, withStack);
	}
	private XNContext(XNContext parent, boolean withStack) {
		this.ui = parent.ui;
		this.security = parent.security;
		this.parent = parent;
		initLanguageConstructs(parent);
		initRuntime(parent);
		initEnvironment(parent);
		if (withStack) initStack(parent);
		else initStack();
	}
	
	/**
	 * Merges this XNContext's loaded modules, global variables,
	 * and environment back into the XNContext this XNContext
	 * was originally forked from.
	 */
	public void join(boolean withStack) {
		if (parent != null) {
			parent.initLanguageConstructs(this);
			parent.initRuntime(this);
			parent.initEnvironment(this);
			if (withStack) parent.initStack(this);
		}
	}
	
	/* LANGUAGE CONSTRUCTS */
	private Map<String, XOMVariant> builtInConstants;
	private Map<String, Integer> builtInOrdinals;
	private Map<String, XOMDataType<? extends XOMVariant>> builtInDataTypes;
	private Map<String, XNModule.CommandParser> commandParsers;
	private Map<String, XNModule.Command> commandInterpreters;
	private Map<String, XNModule.Function> functionInterpreters;
	private Map<String, XNModule.Property> globalProperties;
	private Map<String, XNModule.ExternalLanguage> externalLanguages;
	private Map<String, XNModule.Version> versions;
	private List<XNIOManager> ioManagers;
	private List<XNIOMethod> ioMethods;
	
	/* RUNTIME GLOBAL CONTEXT */
	private Map<String, XOMVariant> userConstants;
	private Map<String, Integer> userOrdinals;
	private Map<String, XOMDataType<? extends XOMVariant>> userDataTypes;
	private XOMVariableMap globalVariables;
	private XOMStaticVariableMap staticVariables;
	private Map<String, XNMessageHandler> globalUserCommands;
	private Map<String, XNFunctionHandler> globalUserFunctions;
	private Set<String> includedScripts;
	
	/* ENVIRONMENT */
	private String applicationPaths;
	private String documentPaths;
	private String includePaths;
	private char itemDelimiter;
	private char columnDelimiter;
	private char rowDelimiter;
	private boolean littleEndian;
	private boolean unsigned;
	private XNNumberFormat numberFormat;
	private String textEncoding;
	private String lineEnding;
	private MathContext mc;
	private MathProcessor mp;
	private Map<String,String> messages;
	
	/* CALL STACK */
	private XNResponder firstResponder;
	private Stack<XNResponder> responderStack;
	private Stack<XNStackFrame> callStack;
	private XOMVariant result;
	
	/* LANGUAGE CONSTRUCTS */
	
	private void initLanguageConstructs() {
		builtInConstants = new HashMap<String, XOMVariant>();
		builtInOrdinals = new HashMap<String, Integer>();
		builtInDataTypes = new HashMap<String, XOMDataType<? extends XOMVariant>>();
		commandParsers = new HashMap<String, XNModule.CommandParser>();
		commandInterpreters = new HashMap<String, XNModule.Command>();
		functionInterpreters = new HashMap<String, XNModule.Function>();
		globalProperties = new HashMap<String, XNModule.Property>();
		externalLanguages = new HashMap<String, XNModule.ExternalLanguage>();
		versions = new HashMap<String, XNModule.Version>();
		ioManagers = new Vector<XNIOManager>();
		ioMethods = new Vector<XNIOMethod>();
	}
	
	private void initLanguageConstructs(XNContext parent) {
		initLanguageConstructs();
		builtInConstants.putAll(parent.builtInConstants);
		builtInOrdinals.putAll(parent.builtInOrdinals);
		builtInDataTypes.putAll(parent.builtInDataTypes);
		commandParsers.putAll(parent.commandParsers);
		commandInterpreters.putAll(parent.commandInterpreters);
		functionInterpreters.putAll(parent.functionInterpreters);
		globalProperties.putAll(parent.globalProperties);
		externalLanguages.putAll(parent.externalLanguages);
		versions.putAll(parent.versions);
		ioManagers.addAll(parent.ioManagers);
		ioMethods.addAll(parent.ioMethods);
	}
	
	/* RUNTIME GLOBAL CONTEXT */
	
	private void initRuntime() {
		userConstants = new HashMap<String, XOMVariant>();
		userOrdinals = new HashMap<String, Integer>();
		userDataTypes = new HashMap<String, XOMDataType<? extends XOMVariant>>();
		globalVariables = new XOMVariableMap();
		staticVariables = new XOMStaticVariableMap();
		globalUserCommands = new HashMap<String, XNMessageHandler>();
		globalUserFunctions = new HashMap<String, XNFunctionHandler>();
		includedScripts = new HashSet<String>();
	}
	
	private void initRuntime(XNContext parent) {
		initRuntime();
		userConstants.putAll(parent.userConstants);
		userOrdinals.putAll(parent.userOrdinals);
		userDataTypes.putAll(parent.userDataTypes);
		globalVariables.merge(parent.globalVariables);
		staticVariables.merge(parent.staticVariables);
		globalUserCommands.putAll(parent.globalUserCommands);
		globalUserFunctions.putAll(parent.globalUserFunctions);
		includedScripts.addAll(parent.includedScripts);
	}
	
	/* ENVIRONMENT */
	
	private void initEnvironment() {
		applicationPaths = null;
		documentPaths = null;
		includePaths = null;
		itemDelimiter = ',';
		columnDelimiter = '\uFFF0';
		rowDelimiter = '\uFFF1';
		littleEndian = false;
		unsigned = false;
		numberFormat = new XNNumberFormat(null);
		textEncoding = "ISO-8859-1";
		lineEnding = "\n";
		mc = new MathContext(100, RoundingMode.HALF_EVEN);
		mp = FastMath.instance;
		messages = new HashMap<String,String>();
	}
	
	private void initEnvironment(XNContext parent) {
		applicationPaths = parent.applicationPaths;
		documentPaths = parent.documentPaths;
		includePaths = parent.includePaths;
		itemDelimiter = parent.itemDelimiter;
		columnDelimiter = parent.columnDelimiter;
		rowDelimiter = parent.rowDelimiter;
		littleEndian = parent.littleEndian;
		unsigned = parent.unsigned;
		numberFormat = parent.numberFormat;
		textEncoding = parent.textEncoding;
		lineEnding = parent.lineEnding;
		mc = parent.mc;
		mp = parent.mp;
		messages = parent.messages;
	}
	
	/* CALL STACK */
	
	private void initStack() {
		firstResponder = null;
		responderStack = new Stack<XNResponder>();
		callStack = new Stack<XNStackFrame>();
		result = null;
	}
	
	private void initStack(XNContext parent) {
		initStack();
		firstResponder = parent.firstResponder;
		responderStack.addAll(parent.responderStack);
		callStack.addAll(parent.callStack);
		result = parent.result;
	}
	
	/* LANGUAGE CONSTRUCTS */
	
	public void loadModule(XNModule m) {
		for (Map.Entry<String, XOMVariant> e : m.constants.entrySet()) {
			builtInConstants.put(XIONUtil.normalizeVarName(e.getKey()), e.getValue());
		}
		for (Map.Entry<String, Integer> e : m.ordinals.entrySet()) {
			builtInOrdinals.put(XIONUtil.normalizeVarName(e.getKey()), e.getValue());
		}
		for (Map.Entry<String, XOMDataType<? extends XOMVariant>> e : m.dataTypes.entrySet()) {
			builtInDataTypes.put(XIONUtil.normalizeVarName(e.getKey()), e.getValue());
		}
		for (Map.Entry<String, XNModule.CommandParser> e : m.commandParsers.entrySet()) {
			commandParsers.put(XIONUtil.normalizeVarName(e.getKey()), e.getValue());
		}
		for (Map.Entry<String, XNModule.Command> e : m.commands.entrySet()) {
			commandInterpreters.put(XIONUtil.normalizeVarName(e.getKey()), e.getValue());
		}
		for (Map.Entry<String, XNModule.Function> e : m.functions.entrySet()) {
			functionInterpreters.put(XIONUtil.normalizeVarName(e.getKey()), e.getValue());
		}
		for (Map.Entry<String, XNModule.Property> e : m.properties.entrySet()) {
			globalProperties.put(XIONUtil.normalizeVarName(e.getKey()), e.getValue());
		}
		for (Map.Entry<String, XNModule.ExternalLanguage> e : m.externalLanguages.entrySet()) {
			externalLanguages.put(XIONUtil.normalizeVarName(e.getKey()), e.getValue());
		}
		for (Map.Entry<String, XNModule.Version> e : m.versions.entrySet()) {
			versions.put(XIONUtil.normalizeVarName(e.getKey()), e.getValue());
		}
		for (XNIOManager e : m.ioManagers) {
			ioManagers.add(e);
		}
		for (XNIOMethod e : m.ioMethods) {
			ioMethods.add(e);
		}
	}
	
	/* UNION OF LANGUAGE CONSTRUCTS AND RUNTIME GLOBAL CONTEXT */
	
	public XOMVariant getConstant(String name) {
		name = XIONUtil.normalizeVarName(name);
		return userConstants.containsKey(name) ? userConstants.get(name) : builtInConstants.get(name);
	}
	
	public boolean hasConstant(String name) {
		name = XIONUtil.normalizeVarName(name);
		return builtInConstants.containsKey(name) || userConstants.containsKey(name);
	}
	
	public int getOrdinal(String name) {
		name = XIONUtil.normalizeVarName(name);
		return userOrdinals.containsKey(name) ? userOrdinals.get(name) : builtInOrdinals.get(name);
	}
	
	public boolean hasOrdinal(String name) {
		name = XIONUtil.normalizeVarName(name);
		return builtInOrdinals.containsKey(name) || userOrdinals.containsKey(name);
	}
	
	public XOMDataType<? extends XOMVariant> getDataType(String name) {
		name = XIONUtil.normalizeVarName(name);
		return userDataTypes.containsKey(name) ? userDataTypes.get(name) : builtInDataTypes.get(name);
	}
	
	public boolean hasDataType(String name) {
		name = XIONUtil.normalizeVarName(name);
		return builtInDataTypes.containsKey(name) || userDataTypes.containsKey(name);
	}
	
	public Set<Map.Entry<String, XOMDataType<? extends XOMVariant>>> getBuiltInDataTypeEntrySet() {
		return builtInDataTypes.entrySet();
	}
	
	public Set<Map.Entry<String, XOMDataType<? extends XOMVariant>>> getUserDataTypeEntrySet() {
		return userDataTypes.entrySet();
	}
	
	/* LANGUAGE CONSTRUCTS */
	
	public XNModule.CommandParser getCommandParser(String name) {
		return commandParsers.get(XIONUtil.normalizeVarName(name));
	}
	
	public boolean hasCommandParser(String name) {
		return commandParsers.containsKey(XIONUtil.normalizeVarName(name));
	}
	
	public XNModule.Command getCommandInterpreter(String name) {
		return commandInterpreters.get(XIONUtil.normalizeVarName(name));
	}
	
	public boolean hasCommandInterpreter(String name) {
		return commandInterpreters.containsKey(XIONUtil.normalizeVarName(name));
	}
	
	public XNModule.Function getFunctionInterpreter(String name) {
		return functionInterpreters.get(XIONUtil.normalizeVarName(name));
	}
	
	public boolean hasFunctionInterpreter(String name) {
		return functionInterpreters.containsKey(XIONUtil.normalizeVarName(name));
	}
	
	public XNModule.Property getGlobalProperty(String name) {
		return globalProperties.get(XIONUtil.normalizeVarName(name));
	}
	
	public boolean hasGlobalProperty(String name) {
		return globalProperties.containsKey(XIONUtil.normalizeVarName(name));
	}
	
	public XNModule.ExternalLanguage getExternalLanguage(String name) {
		return externalLanguages.get(XIONUtil.normalizeVarName(name));
	}
	
	public boolean hasExternalLanguage(String name) {
		return externalLanguages.containsKey(XIONUtil.normalizeVarName(name));
	}
	
	public XNModule.Version getVersion(String name) {
		return versions.get(XIONUtil.normalizeVarName(name));
	}
	
	public boolean hasVersion(String name) {
		return versions.containsKey(XIONUtil.normalizeVarName(name));
	}
	
	public XNIOManager getIOManager(XOMVariant obj) {
		for (XNIOManager io : ioManagers) {
			if (io.worksWith(this, obj)) return io;
		}
		return null;
	}
	
	public boolean hasIOManager(XOMVariant obj) {
		for (XNIOManager io : ioManagers) {
			if (io.worksWith(this, obj)) return true;
		}
		return false;
	}
	
	public XNIOMethod getIOMethod(String type) {
		for (XNIOMethod io : ioMethods) {
			if (io.worksWith(type)) return io;
		}
		return null;
	}
	
	public boolean hasIOMethod(String type) {
		for (XNIOMethod io : ioMethods) {
			if (io.worksWith(type)) return true;
		}
		return false;
	}
	
	/* RUNTIME GLOBAL CONTEXT */
	
	public void addUserConstant(String name, XOMVariant value) {
		userConstants.put(XIONUtil.normalizeVarName(name), value);
	}
	
	public void addUserOrdinal(String name, int value) {
		userOrdinals.put(XIONUtil.normalizeVarName(name), value);
	}
	
	public void addUserDataType(String name, XOMDataType<? extends XOMVariant> type) {
		userDataTypes.put(XIONUtil.normalizeVarName(name), type);
	}
	
	public XOMVariableMap globalVariables() {
		return globalVariables;
	}
	
	public XOMStaticVariableMap staticVariables() {
		return staticVariables;
	}
	
	public void defineGlobalUserCommand(String name, XNMessageHandler handler) {
		name = XIONUtil.normalizeVarName(name);
		globalUserCommands.put(name, handler);
	}
	
	public XNMessageHandler getGlobalUserCommand(String name) {
		name = XIONUtil.normalizeVarName(name);
		return globalUserCommands.get(name);
	}
	
	public void defineGlobalUserFunction(String name, XNFunctionHandler handler) {
		name = XIONUtil.normalizeVarName(name);
		globalUserFunctions.put(name, handler);
	}
	
	public XNFunctionHandler getGlobalUserFunction(String name) {
		name = XIONUtil.normalizeVarName(name);
		return globalUserFunctions.get(name);
	}
	
	public void addIncludedScript(String path) {
		includedScripts.add(path);
	}
	
	public boolean hasIncludedScript(String path) {
		return includedScripts.contains(path);
	}
	
	/* ENVIRONMENT */
	
	public String getApplicationPaths() {
		return applicationPaths;
	}
	
	public void setApplicationPaths(String s) {
		applicationPaths = s;
	}
	
	public String getDocumentPaths() {
		return documentPaths;
	}
	
	public void setDocumentPaths(String s) {
		documentPaths = s;
	}
	
	public String getIncludePaths() {
		return includePaths;
	}
	
	public void setIncludePaths(String s) {
		includePaths = s;
	}
	
	public char getItemDelimiter() {
		return itemDelimiter;
	}
	
	public void setItemDelimiter(char ch) {
		itemDelimiter = ch;
	}
	
	public char getColumnDelimiter() {
		return columnDelimiter;
	}
	
	public void setColumnDelimiter(char ch) {
		columnDelimiter = ch;
	}
	
	public char getRowDelimiter() {
		return rowDelimiter;
	}
	
	public void setRowDelimiter(char ch) {
		rowDelimiter = ch;
	}
	
	public boolean getLittleEndian() {
		return littleEndian;
	}
	
	public void setLittleEndian(boolean v) {
		littleEndian = v;
	}
	
	public boolean getUnsigned() {
		return unsigned;
	}
	
	public void setUnsigned(boolean v) {
		unsigned = v;
	}
	
	public XNNumberFormat getNumberFormat() {
		return numberFormat;
	}
	
	public void setNumberFormat(String s) {
		numberFormat = new XNNumberFormat(s);
	}
	
	public String getTextEncoding() {
		return textEncoding;
	}
	
	public void setTextEncoding(String s) {
		textEncoding = s;
	}
	
	public String getLineEnding() {
		return lineEnding;
	}
	
	public void setLineEnding(String s) {
		lineEnding = s;
	}
	
	public MathContext getMathContext() {
		return mc;
	}
	
	public void setMathContext(MathContext mc) {
		this.mc = mc;
	}
	
	public MathProcessor getMathProcessor() {
		return mp;
	}
	
	public void setMathProcessor(MathProcessor mp) {
		this.mp = mp;
	}
	
	public String getMessage(String messageID) {
		return messages.containsKey(messageID) ? messages.get(messageID) : messageID;
	}
	
	public void addMessages(Map<String,String> m) {
		messages.putAll(m);
	}
	
	/* CALL STACK */
	
	public XNResponder getFirstResponder() {
		return firstResponder;
	}
	
	public void setFirstResponder(XNResponder resp) {
		firstResponder = resp;
	}
	
	public void pushResponder(XNResponder resp) {
		responderStack.push(resp);
	}
	
	public void popResponder() {
		if (!responderStack.isEmpty()) {
			responderStack.pop();
		}
	}
	
	public XNResponder getCurrentResponder() {
		if (!responderStack.isEmpty()) {
			return responderStack.peek();
		} else {
			return null;
		}
	}
	
	public void pushStackFrame(XNStackFrame frame) {
		callStack.push(frame);
	}
	
	public void popStackFrame() {
		if (!callStack.isEmpty()) {
			callStack.pop();
		}
	}
	
	public XNStackFrame getCurrentStackFrame() {
		if (!callStack.isEmpty()) {
			return callStack.peek();
		} else {
			return null;
		}
	}
	
	public XOMVariant getResult() {
		return (result == null) ? XOMEmpty.EMPTY : result;
	}
	
	public void setResult(XOMVariant result) {
		this.result = result;
	}
	
	/* VARIABLES */
	
	public void setVariableScope(String name, XNVariableScope scope) {
		if (getCurrentStackFrame() != null) {
			getCurrentStackFrame().setVariableScope(name, scope);
		}
	}
	
	public XOMVariableMap getVariableMap(String name) {
		XNVariableScope scope = XNVariableScope.GLOBAL;
		String handlerName = "";
		if (getCurrentStackFrame() != null) {
			scope = getCurrentStackFrame().getVariableScope(name);
			if (scope == null) scope = XNVariableScope.LOCAL;
			handlerName = getCurrentStackFrame().getHandlerName();
			if (handlerName == null) handlerName = "";
		}
		switch (scope) {
		case GLOBAL:
			return globalVariables();
		case SHARED:
			if (getCurrentResponder() != null)
				return getCurrentResponder().sharedVariables();
			else
				return globalVariables();
		case STATIC:
			if (getCurrentResponder() != null)
				return getCurrentResponder().staticVariables().forHandler(handlerName);
			else
				return staticVariables().forHandler(handlerName);
		case LOCAL:
		default:
			if (getCurrentStackFrame() != null)
				return getCurrentStackFrame().localVariables();
			else if (getCurrentResponder() != null)
				return getCurrentResponder().sharedVariables();
			else
				return globalVariables();
		}
	}
}
