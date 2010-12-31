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
import java.util.*;
import com.kreative.openxion.ast.XNExpression;
import com.kreative.openxion.ast.XNModifier;
import com.kreative.openxion.io.XNIOManager;
import com.kreative.openxion.io.XNIOMethod;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMDataType;

/**
 * XNModule is the main class responsible for defining
 * built-in constants, ordinals, and data types, parsing
 * arguments for and executing built-in commands, and
 * evaluating built-in functions.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public abstract class XNModule implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	protected Map<String, XOMVariant> constants;
	protected Map<String, Integer> ordinals;
	protected Map<String, XOMDataType<? extends XOMVariant>> dataTypes;
	protected Map<String, CommandParser> commandParsers;
	protected Map<String, Command> commands;
	protected Map<String, Function> functions;
	protected Map<String, Property> properties;
	protected Map<String, ExternalLanguage> externalLanguages;
	protected Map<String, Version> versions;
	protected List<XNIOManager> ioManagers;
	protected List<XNIOMethod> ioMethods;
	
	protected XNModule() {
		constants = new HashMap<String, XOMVariant>();
		ordinals = new HashMap<String, Integer>();
		dataTypes = new HashMap<String, XOMDataType<? extends XOMVariant>>();
		commandParsers = new HashMap<String, CommandParser>();
		commands = new HashMap<String, Command>();
		functions = new HashMap<String, Function>();
		properties = new HashMap<String, Property>();
		externalLanguages = new HashMap<String, ExternalLanguage>();
		versions = new HashMap<String, Version>();
		ioManagers = new Vector<XNIOManager>();
		ioMethods = new Vector<XNIOMethod>();
	}
	
	/**
	 * An XNModule.CommandParser is responsible for parsing the arguments
	 * to a specific built-in command or set of built-in commands.
	 * @since OpenXION 0.9
	 * @author Rebecca G. Bettencourt, Kreative Software
	 */
	public static interface CommandParser {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords);
		public String describeCommand(String commandName, List<XNExpression> parameters);
	}
	
	/**
	 * An XNModule.Command is responsible for executing
	 * a specific built-in command or set of built-in commands.
	 * @since OpenXION 0.9
	 * @author Rebecca G. Bettencourt, Kreative Software
	 */
	public static interface Command {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters);
	}
	
	/**
	 * An XNModule.Function is responsible for evaluating
	 * a specific built-in function of set of built-in functions.
	 * @since OpenXION 0.9
	 * @author Rebecca G. Bettencourt, Kreative Software
	 */
	public static interface Function {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter);
	}
	
	/**
	 * An XNModule.Property is responsible for getting and setting
	 * a specific global property or set of global properties.
	 * @since OpenXION 0.9
	 * @author Rebecca G. Bettencourt, Kreative Software
	 */
	public static interface Property {
		public boolean canGetProperty(XNContext ctx, String propertyName);
		public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String propertyName);
		public boolean canSetProperty(XNContext ctx, String propertyName);
		public void setProperty(XNContext ctx, String propertyName, XOMVariant value);
	}
	
	/**
	 * An XNModule.ExternalLanguage is responsible for executing
	 * scripts written in other scripting languages, a capability
	 * exposed by the statement <code>do</code> <i>script</i> <code>as</code> <i>language</i>.
	 * @since OpenXION 0.9
	 * @author Rebecca G. Bettencourt, Kreative Software
	 */
	public static interface ExternalLanguage {
		public XOMVariant execute(String script);
	}
	
	/**
	 * An XNModule.Version is responsible for keeping the name and version number
	 * of a component.
	 * @since OpenXION 1.0
	 * @author Rebecca G. Bettencourt, Kreative Software
	 */
	public static class Version {
		private String name;
		private String version;
		public Version(String name, String version) {
			this.name = name; this.version = version;
		}
		public String name() {
			return name;
		}
		public String version() {
			return version;
		}
	}
}
