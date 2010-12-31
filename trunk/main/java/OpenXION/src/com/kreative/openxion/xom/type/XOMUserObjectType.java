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

package com.kreative.openxion.xom.type;

import java.util.*;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.XNInterpreter;
import com.kreative.openxion.XNHandlerExit;
import com.kreative.openxion.XNHandlerExitStatus;
import com.kreative.openxion.XNScriptError;
import com.kreative.openxion.XNStackFrame;
import com.kreative.openxion.ast.XNModifier;
import com.kreative.openxion.ast.XNPreposition;
import com.kreative.openxion.ast.XNExpression;
import com.kreative.openxion.ast.XNStatement;
import com.kreative.openxion.ast.XNObjectTypeDeclaration;
import com.kreative.openxion.ast.XNObjectTypeCreateHandler;
import com.kreative.openxion.ast.XNObjectTypeDeleteHandler;
import com.kreative.openxion.ast.XNObjectTypeGetContentsHandler;
import com.kreative.openxion.ast.XNObjectTypePutContentsHandler;
import com.kreative.openxion.ast.XNObjectTypePropertyGetter;
import com.kreative.openxion.ast.XNObjectTypePropertySetter;
import com.kreative.openxion.ast.XNMessageHandler;
import com.kreative.openxion.ast.XNFunctionHandler;
import com.kreative.openxion.ast.XNHandlerParameter;
import com.kreative.openxion.ast.XNDataType;
import com.kreative.openxion.ast.XNVariableScope;
import com.kreative.openxion.util.XIONUtil;
import com.kreative.openxion.xom.XOMDataType;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMCreateError;
import com.kreative.openxion.xom.XOMGetError;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.inst.XOMUserObject;
import com.kreative.openxion.xom.inst.XOMEmpty;
import com.kreative.openxion.xom.inst.XOMList;
import com.kreative.openxion.xom.inst.XOMListChunk;
import com.kreative.openxion.xom.inst.XOMInteger;
import com.kreative.openxion.xom.inst.XOMString;

public class XOMUserObjectType extends XOMDataType<XOMUserObject> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMUserObjectType rootInstance = new XOMUserObjectType();
	public static final XOMListType rootListInstance = rootInstance.listType;
	
	private XNObjectTypeDeclaration declaration;
	private XOMUserObjectType superType;
	private XOMListType listType;
	
	private List<XOMUserObject> objects;
	private Map<XOMUserObject, Integer> objectIds;
	private Map<XOMUserObject, String> objectNames;
	private Map<Integer, XOMUserObject> objectsById;
	private Map<String, XOMUserObject> objectsByName;
	
	private XOMUserObjectType() {
		super("object", DESCRIBABILITY_OF_PRIMITIVES, XOMUserObject.class);
		this.declaration = null;
		this.superType = null;
		this.listType = new XOMListType("objects", DESCRIBABILITY_OF_PRIMITIVES, this);
		
		objects = new Vector<XOMUserObject>();
		objectIds = new HashMap<XOMUserObject, Integer>();
		objectNames = new HashMap<XOMUserObject, String>();
		objectsById = new HashMap<Integer, XOMUserObject>();
		objectsByName = new HashMap<String, XOMUserObject>();
	}

	public XOMUserObjectType(XNContext ctx, XNObjectTypeDeclaration declaration) {
		super(declaration.singularNameString(), DESCRIBABILITY_OF_SINGULAR_USER_OBJECTS, XOMUserObject.class);
		this.declaration = declaration;
		if (declaration.extendedtype == null) {
			this.superType = rootInstance;
		} else {
			String et = declaration.extendedtype.toNameString();
			if (et.trim().length() == 0) {
				this.superType = rootInstance;
			} else {
				XOMDataType<? extends XOMVariant> dt = ctx.getDataType(et);
				if (dt == null) {
					throw new XNScriptError("Unknown data type");
				} else if (dt instanceof XOMUserObjectType) {
					this.superType = (XOMUserObjectType)dt;
				} else {
					throw new XNScriptError("Can't extend that data type");
				}
			}
		}
		this.listType = new XOMListType(declaration.pluralNameString(), DESCRIBABILITY_OF_PLURAL_USER_OBJECTS, this);
		
		objects = new Vector<XOMUserObject>();
		objectIds = new HashMap<XOMUserObject, Integer>();
		objectNames = new HashMap<XOMUserObject, String>();
		objectsById = new HashMap<Integer, XOMUserObject>();
		objectsByName = new HashMap<String, XOMUserObject>();
	}
	
	public XOMUserObjectType superType() {
		return superType;
	}
	
	public XOMListType listType() {
		return listType;
	}
	
	/*
	 * Instantiation of root variants of this type.
	 */
	
	public boolean canGetMassInstance(XNContext ctx) {
		return true;
	}
	public boolean canGetInstanceByIndex(XNContext ctx, int index) {
		index = XIONUtil.index(1, objects.size(), index, index)[0];
		return (index >= 1 && index <= objects.size());
	}
	public boolean canGetInstanceByIndex(XNContext ctx, int startIndex, int endIndex) {
		return true;
	}
	public boolean canGetInstanceByID(XNContext ctx, int id) {
		return objectsById.containsKey(id);
	}
	public boolean canGetInstanceByName(XNContext ctx, String name) {
		return objectsByName.containsKey(name);
	}
	
	public XOMVariant getMassInstance(XNContext ctx) {
		return new XOMList(objects);
	}
	public XOMVariant getInstanceByIndex(XNContext ctx, int index) {
		index = XIONUtil.index(1, objects.size(), index, index)[0];
		if (index >= 1 && index <= objects.size()) {
			return objects.get(index-1);
		} else {
			throw new XOMGetError(typeName, index, index);
		}
	}
	public XOMVariant getInstanceByIndex(XNContext ctx, int startIndex, int endIndex) {
		int[] indexes = XIONUtil.index(1, objects.size(), startIndex, endIndex);
		if (indexes[0] < 1) indexes[0] = 1;
		else if (indexes[0] > objects.size()) indexes[0] = objects.size();
		if (indexes[1] < 1) indexes[1] = 1;
		else if (indexes[1] > objects.size()) indexes[1] = objects.size();
		return new XOMList(objects.subList(indexes[0]-1, indexes[1]));
	}
	public XOMVariant getInstanceByID(XNContext ctx, int id) {
		if (objectsById.containsKey(id)) return objectsById.get(id);
		else throw new XOMGetError(typeName, id);
	}
	public XOMVariant getInstanceByName(XNContext ctx, String name) {
		if (objectsByName.containsKey(name)) return objectsByName.get(name);
		else throw new XOMGetError(typeName, name);
	}
	
	public boolean canCreateInstance(XNContext ctx) {
		return (declaration != null);
	}
	public boolean canCreateInstanceByName(XNContext ctx, String name) {
		return (declaration != null);
	}
	
	private static int nextId = 1;
	private static synchronized XOMUserObject createNewInstance(XNContext ctx, XOMUserObjectType type, String name) {
		// create the object
		XOMUserObject newInstance = new XOMUserObject(type, nextId);
		// assign index, ID, and name
		for (XOMUserObjectType t = type; t != null; t = t.superType) {
			t.objects.add(newInstance);
			t.objectIds.put(newInstance, nextId);
			t.objectNames.put(newInstance, name);
			t.objectsById.put(nextId, newInstance);
			t.objectsByName.put(name, newInstance);
		}
		nextId++;
		// execute the create handler
		for (XOMUserObjectType t = type; t != null; t = t.superType) {
			if (t.declaration != null && t.declaration.body != null) {
				for (XNStatement stat : t.declaration.body) {
					if (stat instanceof XNObjectTypeCreateHandler) {
						XNHandlerExit exit = executeHandler(new XNInterpreter(ctx), ctx, newInstance, "create", ((XNObjectTypeCreateHandler)stat).body, null);
						if (exit.status() == XNHandlerExitStatus.PASSED) break;
						else return newInstance;
					}
				}
			}
		}
		return newInstance;
	}
	
	public XOMVariant createInstance(XNContext ctx) {
		if (declaration != null) {
			return createNewInstance(ctx, this, "");
		} else {
			throw new XOMCreateError(typeName);
		}
	}
	public XOMVariant createInstanceByName(XNContext ctx, String name) {
		if (declaration != null) {
			return createNewInstance(ctx, this, name);
		} else {
			throw new XOMCreateError(typeName, name);
		}
	}
	
	/*
	 * Encapsulation - All manipulation of an object's state is done through these methods.
	 * Inheritance - If this type doesn't have handlers for these methods, control is passed to its supertype.
	 */

	public boolean canDelete(XNContext ctx, XOMUserObject instance) {
		return true;
	}
	private static void executeDeleteHandler(XNContext ctx, XOMUserObjectType type, XOMUserObject instance) {
		for (XOMUserObjectType t = type; t != null; t = t.superType) {
			if (t.declaration != null && t.declaration.body != null) {
				for (XNStatement stat : t.declaration.body) {
					if (stat instanceof XNObjectTypeDeleteHandler) {
						XNHandlerExit exit = executeHandler(new XNInterpreter(ctx), ctx, instance, "delete", ((XNObjectTypeDeleteHandler)stat).body, null);
						if (exit.status() == XNHandlerExitStatus.PASSED) break;
						else return;
					}
				}
			}
		}
	}
	private static void forgetInstance(XNContext ctx, XOMUserObjectType type, XOMUserObject instance) {
		for (XOMUserObjectType t = type; t != null; t = t.superType) {
			t.objects.remove(instance);
			t.objectIds.remove(instance);
			t.objectNames.remove(instance);
			t.objectsById.clear();
			for (Map.Entry<XOMUserObject, Integer> e : t.objectIds.entrySet()) {
				t.objectsById.put(e.getValue(), e.getKey());
			}
			t.objectsByName.clear();
			for (Map.Entry<XOMUserObject, String> e : t.objectNames.entrySet()) {
				t.objectsByName.put(e.getValue(), e.getKey());
			}
		}
	}
	public void delete(XNContext ctx, XOMUserObject instance) {
		executeDeleteHandler(ctx, this, instance);
		forgetInstance(ctx, this, instance);
	}
	
	public boolean canGetContents(XNContext ctx, XOMUserObject instance) {
		if (declaration != null && declaration.body != null) for (XNStatement stat : declaration.body) {
			if (stat instanceof XNObjectTypeGetContentsHandler) return true;
		}
		if (superType != null) {
			return superType.canGetContents(ctx, instance);
		} else {
			return false;
		}
	}
	public XOMVariant getContents(XNContext ctx, XOMUserObject instance) {
		if (declaration != null && declaration.body != null) for (XNStatement stat : declaration.body) {
			if (stat instanceof XNObjectTypeGetContentsHandler) {
				XNHandlerExit exit = evaluateHandler(new XNInterpreter(ctx), ctx, instance, "get", ((XNObjectTypeGetContentsHandler)stat).body, null);
				if (exit.status() == XNHandlerExitStatus.PASSED) break;
				else return exit.returnValue();
			}
		}
		if (superType != null) {
			return superType.getContents(ctx, instance);
		} else {
			throw new XNScriptError("Can't get contents of this");
		}
	}
	
	public boolean canPutContents(XNContext ctx, XOMUserObject instance) {
		if (declaration != null && declaration.body != null) for (XNStatement stat : declaration.body) {
			if (stat instanceof XNObjectTypePutContentsHandler) return true;
		}
		if (superType != null) {
			return superType.canPutContents(ctx, instance);
		} else {
			return false;
		}
	}
	public void putIntoContents(XNContext ctx, XOMUserObject instance, XOMVariant contents) {
		if (declaration != null && declaration.body != null) for (XNStatement stat : declaration.body) {
			if (stat instanceof XNObjectTypePutContentsHandler && ((XNObjectTypePutContentsHandler)stat).preposition == XNPreposition.INTO) {
				Map<String, XOMVariant> parameters = new LinkedHashMap<String, XOMVariant>();
				parameters.put(((XNObjectTypePutContentsHandler)stat).identifier, contents);
				XNHandlerExit exit = executeHandler(new XNInterpreter(ctx), ctx, instance, "put", ((XNObjectTypePutContentsHandler)stat).body, parameters);
				if (exit.status() == XNHandlerExitStatus.PASSED) break;
				else return;
			}
		}
		if (superType != null) {
			superType.putIntoContents(ctx, instance, contents);
		} else {
			throw new XNScriptError("Can't put into this");
		}
	}
	public void putBeforeContents(XNContext ctx, XOMUserObject instance, XOMVariant contents) {
		if (declaration != null && declaration.body != null) for (XNStatement stat : declaration.body) {
			if (stat instanceof XNObjectTypePutContentsHandler && ((XNObjectTypePutContentsHandler)stat).preposition == XNPreposition.BEFORE) {
				Map<String, XOMVariant> parameters = new LinkedHashMap<String, XOMVariant>();
				parameters.put(((XNObjectTypePutContentsHandler)stat).identifier, contents);
				XNHandlerExit exit = executeHandler(new XNInterpreter(ctx), ctx, instance, "put", ((XNObjectTypePutContentsHandler)stat).body, parameters);
				if (exit.status() == XNHandlerExitStatus.PASSED) break;
				else return;
			}
		}
		if (superType != null) {
			superType.putBeforeContents(ctx, instance, contents);
		} else {
			throw new XNScriptError("Can't put before this");
		}
	}
	public void putAfterContents(XNContext ctx, XOMUserObject instance, XOMVariant contents) {
		if (declaration != null && declaration.body != null) for (XNStatement stat : declaration.body) {
			if (stat instanceof XNObjectTypePutContentsHandler && ((XNObjectTypePutContentsHandler)stat).preposition == XNPreposition.AFTER) {
				Map<String, XOMVariant> parameters = new LinkedHashMap<String, XOMVariant>();
				parameters.put(((XNObjectTypePutContentsHandler)stat).identifier, contents);
				XNHandlerExit exit = executeHandler(new XNInterpreter(ctx), ctx, instance, "put", ((XNObjectTypePutContentsHandler)stat).body, parameters);
				if (exit.status() == XNHandlerExitStatus.PASSED) break;
				else return;
			}
		}
		if (superType != null) {
			superType.putAfterContents(ctx, instance, contents);
		} else {
			throw new XNScriptError("Can't put after this");
		}
	}
	public void putIntoContents(XNContext ctx, XOMUserObject instance, XOMVariant contents, String property, XOMVariant pvalue) {
		XOMUserObject newValue = makeInstanceFrom(ctx, contents);
		setProperty(ctx, newValue, property, pvalue);
		putIntoContents(ctx, instance, newValue);
	}
	public void putBeforeContents(XNContext ctx, XOMUserObject instance, XOMVariant contents, String property, XOMVariant pvalue) {
		XOMUserObject newValue = makeInstanceFrom(ctx, contents);
		setProperty(ctx, newValue, property, pvalue);
		putBeforeContents(ctx, instance, newValue);
	}
	public void putAfterContents(XNContext ctx, XOMUserObject instance, XOMVariant contents, String property, XOMVariant pvalue) {
		XOMUserObject newValue = makeInstanceFrom(ctx, contents);
		setProperty(ctx, newValue, property, pvalue);
		putAfterContents(ctx, instance, newValue);
	}
	
	public boolean canGetProperty(XNContext ctx, XOMUserObject instance, String property) {
		if (property.equalsIgnoreCase("number")) {
			return objects.contains(instance);
		}
		else if (property.equalsIgnoreCase("id")) {
			return objectIds.containsKey(instance);
		}
		else if (property.equalsIgnoreCase("name")) {
			return objectNames.containsKey(instance);
		}
		else {
			if (declaration != null && declaration.body != null) for (XNStatement stat : declaration.body) {
				if (stat instanceof XNObjectTypePropertyGetter && ((XNObjectTypePropertyGetter)stat).propname.equalsIgnoreCase(property)) return true;
			}
			if (superType != null) {
				return superType.canGetProperty(ctx, instance, property);
			} else {
				return false;
			}
		}
	}
	public XOMVariant getProperty(XNContext ctx, XOMUserObject instance, XNModifier modifier, String property) {
		if (property.equalsIgnoreCase("number")) {
			if (objects.contains(instance))
				return new XOMInteger(objects.indexOf(instance)+1);
			else
				throw new XNScriptError("Can't get that property");
		}
		else if (property.equalsIgnoreCase("id")) {
			if (objectIds.containsKey(instance))
				return new XOMInteger(objectIds.get(instance));
			else
				throw new XNScriptError("Can't get that property");
		}
		else if (property.equalsIgnoreCase("name")) {
			if (objectNames.containsKey(instance))
				return new XOMString(objectNames.get(instance));
			else
				throw new XNScriptError("Can't get that property");
		}
		else {
			if (declaration != null && declaration.body != null) for (XNStatement stat : declaration.body) {
				if (stat instanceof XNObjectTypePropertyGetter && ((XNObjectTypePropertyGetter)stat).propname.equalsIgnoreCase(property)) {
					XNHandlerExit exit = evaluateHandler(new XNInterpreter(ctx), ctx, instance, "get", ((XNObjectTypePropertyGetter)stat).body, null);
					if (exit.status() == XNHandlerExitStatus.PASSED) break;
					else return exit.returnValue();
				}
			}
			if (superType != null) {
				return superType.getProperty(ctx, instance, modifier, property);
			} else {
				throw new XNScriptError("Can't get that property");
			}
		}
	}
	
	public boolean canSetProperty(XNContext ctx, XOMUserObject instance, String property) {
		if (property.equalsIgnoreCase("number") || property.equalsIgnoreCase("id")) {
			return false;
		}
		else if (property.equalsIgnoreCase("name")) {
			return true;
		}
		else {
			if (declaration != null && declaration.body != null) for (XNStatement stat : declaration.body) {
				if (stat instanceof XNObjectTypePropertySetter && ((XNObjectTypePropertySetter)stat).propname.equalsIgnoreCase(property)) return true;
			}
			if (superType != null) {
				return superType.canSetProperty(ctx, instance, property);
			} else {
				return false;
			}
		}
	}
	public void setProperty(XNContext ctx, XOMUserObject instance, String property, XOMVariant value) {
		if (property.equalsIgnoreCase("number") || property.equalsIgnoreCase("id")) {
			throw new XNScriptError("Can't set that property");
		}
		else if (property.equalsIgnoreCase("name")) {
			if (objectNames.containsKey(instance)) {
				objectNames.put(instance, value.toTextString(ctx));
				objectsByName.clear();
				for (Map.Entry<XOMUserObject, String> e : objectNames.entrySet()) {
					objectsByName.put(e.getValue(), e.getKey());
				}
			} else {
				throw new XNScriptError("Can't set that property");
			}
		}
		else {
			if (declaration != null && declaration.body != null) for (XNStatement stat : declaration.body) {
				if (stat instanceof XNObjectTypePropertySetter && ((XNObjectTypePropertySetter)stat).propname.equalsIgnoreCase(property)) {
					Map<String, XOMVariant> parameters = new LinkedHashMap<String, XOMVariant>();
					parameters.put(((XNObjectTypePropertySetter)stat).identifier, value);
					XNHandlerExit exit = executeHandler(new XNInterpreter(ctx), ctx, instance, "set", ((XNObjectTypePropertySetter)stat).body, parameters);
					if (exit.status() == XNHandlerExitStatus.PASSED) break;
					else return;
				}
			}
			if (superType != null) {
				superType.setProperty(ctx, instance, property, value);
			} else {
				throw new XNScriptError("Can't set that property");
			}
		}
	}
	
	public XNHandlerExit evaluateFunction(XNContext ctx, XOMUserObject instance, String functionName, XNModifier modifier, XOMVariant parameter) {
		if (declaration != null && declaration.body != null) for (XNStatement stat : declaration.body) {
			if (stat instanceof XNFunctionHandler && ((XNFunctionHandler)stat).name.equalsIgnoreCase(functionName)) {
				return evaluateFunctionHandler(new XNInterpreter(ctx), ctx, instance, (XNFunctionHandler)stat, parameter.toList(ctx));
			}
		}
		return XNHandlerExit.passed();
	}

	public XNHandlerExit executeCommand(XNContext ctx, XOMUserObject instance, String commandName, List<XNExpression> parameters) {
		if (declaration != null && declaration.body != null) for (XNStatement stat : declaration.body) {
			if (stat instanceof XNMessageHandler && ((XNMessageHandler)stat).name.equalsIgnoreCase(commandName)) {
				List<XOMVariant> paramValues = new Vector<XOMVariant>();
				for (XNExpression param : parameters) {
					paramValues.add(new XNInterpreter(ctx).evaluateExpression(param).unwrap());
				}
				return executeCommandHandler(new XNInterpreter(ctx), ctx, instance, (XNMessageHandler)stat, paramValues);
			}
		}
		return XNHandlerExit.passed();
	}
	
	/*
	 * Polymorphism - The data type of an object is determined through these methods.
	 * Unlike in Java, where an object's type is determined by the class hierarchy,
	 * objects in XION can be of any mix of data types (hence the term variant for XION objects).
	 */
	
	public String toDescriptionString(XOMUserObject instance) {
		return typeName + " id " + objectIds.get(instance);
	}
	
	public String toTextString(XNContext ctx, XOMUserObject instance) {
		if (canGetContents(ctx, instance)) {
			return getContents(ctx, instance).unwrap().toTextString(ctx);
		} else {
			return toDescriptionString(instance);
		}
	}

	private boolean canMorphFromDescription(XNContext ctx, String desc) {
		desc = desc.trim();
		if (desc.length() == 0) return true;
		XOMVariant v = XIONUtil.parseDescriptor(ctx, desc);
		return (v instanceof XOMUserObject && ((XOMUserObject)v).isInstanceOf(this));
	}
	
	private XOMVariant morphFromDescription(XNContext ctx, String desc) {
		desc = desc.trim();
		if (desc.length() == 0) return XOMUserObject.NULL;
		XOMVariant v = XIONUtil.parseDescriptor(ctx, desc);
		if (v instanceof XOMUserObject && ((XOMUserObject)v).isInstanceOf(this)) return v;
		else return null;
	}
	
	@Override
	protected boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		if (instance instanceof XOMEmpty) {
			return true;
		}
		else if (instance instanceof XOMUserObject) {
			XOMUserObject u = (XOMUserObject)instance;
			return u.isInstanceOf(this);
		}
		else if ((instance instanceof XOMList || instance instanceof XOMListChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMUserObject) {
			XOMUserObject u = (XOMUserObject)instance.toList(ctx).get(0);
			return u.isInstanceOf(this);
		}
		else if (canMorphFromDescription(ctx, instance.toTextString(ctx))) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	protected boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant left, XOMVariant right) {
		if (left instanceof XOMEmpty && right instanceof XOMEmpty) {
			return true;
		}
		else if (left instanceof XOMEmpty) {
			return canMakeInstanceFromImpl(ctx, right);
		}
		else if (right instanceof XOMEmpty) {
			return canMakeInstanceFromImpl(ctx, left);
		}
		else if (canMorphFromDescription(ctx, left.toTextString(ctx) + right.toTextString(ctx))) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	protected XOMUserObject makeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		if (instance instanceof XOMEmpty) {
			return XOMUserObject.NULL;
		}
		else if (instance instanceof XOMUserObject) {
			XOMUserObject u = (XOMUserObject)instance;
			if (u.isInstanceOf(this)) return u;
			else throw new XOMMorphError(declaration.singularNameString());
		}
		else if ((instance instanceof XOMList || instance instanceof XOMListChunk) && instance.toList(ctx).size() == 1 && instance.toList(ctx).get(0) instanceof XOMUserObject) {
			XOMUserObject u = (XOMUserObject)instance.toList(ctx).get(0);
			if (u.isInstanceOf(this)) return u;
			else throw new XOMMorphError(declaration.singularNameString());
		}
		else {
			XOMVariant v = morphFromDescription(ctx, instance.toTextString(ctx));
			if (v instanceof XOMUserObject) return (XOMUserObject)v;
			else throw new XOMMorphError(declaration.singularNameString());
		}
	}

	@Override
	protected XOMUserObject makeInstanceFromImpl(XNContext ctx, XOMVariant left, XOMVariant right) {
		if (left instanceof XOMEmpty && right instanceof XOMEmpty) {
			return XOMUserObject.NULL;
		}
		else if (left instanceof XOMEmpty) {
			return makeInstanceFromImpl(ctx, right);
		}
		else if (right instanceof XOMEmpty) {
			return makeInstanceFromImpl(ctx, left);
		}
		else {
			XOMVariant v = morphFromDescription(ctx, left.toTextString(ctx) + right.toTextString(ctx));
			if (v instanceof XOMUserObject) return (XOMUserObject)v;
			else throw new XOMMorphError(declaration.singularNameString());
		}
	}
	
	/*
	 * Code for execution of handlers.
	 */
	
	private static XNHandlerExit evaluateHandler(XNInterpreter interp, XNContext ctx, XOMUserObject obj, String name, List<XNStatement> body, Map<String, XOMVariant> parameters) {
		List<XOMVariant> pvalues = new Vector<XOMVariant>();
		if (parameters != null) pvalues.addAll(parameters.values());
		XNStackFrame f = new XNStackFrame(name, pvalues);
		if (parameters != null) {
			for (Map.Entry<String, XOMVariant> e : parameters.entrySet()) {
				String paramName = e.getKey();
				f.setVariableScope(paramName, XNVariableScope.LOCAL);
				XOMVariant paramValue = e.getValue().unwrap();
				f.createLocalVariable(ctx, paramName, XOMVariantType.instance, paramValue);
			}
		}
		ctx.pushStackFrame(f);
		ctx.pushResponder(obj);
		XNHandlerExit exit = null;
		try {
			exit = interp.executeStatements(body);
		} finally {
			ctx.popStackFrame();
			ctx.popResponder();
		}
		switch (exit.status()) {
		case ENDED:
			return XNHandlerExit.returned();
		case RETURNED:
			return exit;
		case EXITED:
			if (exit.blockTypeValue().equalsIgnoreCase(name)) {
				if (exit.errorValue() != null) {
					throw new XNScriptError(exit.errorValue().toTextString(ctx));
				} else {
					return XNHandlerExit.returned();
				}
			} else {
				throw new XNScriptError("Found exit "+exit.blockTypeValue()+" outside a "+exit.blockTypeValue()+" block");
			}
		case PASSED:
			return exit;
		case NEXTED:
			throw new XNScriptError("Found next "+exit.blockTypeValue()+" outside a "+exit.blockTypeValue()+" block");
		default:
			return XNHandlerExit.returned();
		}
	}
	
	private static XNHandlerExit executeHandler(XNInterpreter interp, XNContext ctx, XOMUserObject obj, String name, List<XNStatement> body, Map<String, XOMVariant> parameters) {
		List<XOMVariant> pvalues = new Vector<XOMVariant>();
		if (parameters != null) pvalues.addAll(parameters.values());
		XNStackFrame f = new XNStackFrame(name, pvalues);
		if (parameters != null) {
			for (Map.Entry<String, XOMVariant> e : parameters.entrySet()) {
				String paramName = e.getKey();
				f.setVariableScope(paramName, XNVariableScope.LOCAL);
				XOMVariant paramValue = e.getValue().unwrap();
				f.createLocalVariable(ctx, paramName, XOMVariantType.instance, paramValue);
			}
		}
		ctx.pushStackFrame(f);
		ctx.pushResponder(obj);
		XNHandlerExit exit = null;
		try {
			exit = interp.executeStatements(body);
		} finally {
			ctx.popStackFrame();
			ctx.popResponder();
		}
		switch (exit.status()) {
		case ENDED:
			return exit;
		case RETURNED:
			if (exit.returnValue() != null) ctx.setResult(exit.returnValue());
			return XNHandlerExit.ended();
		case EXITED:
			if (exit.blockTypeValue().equalsIgnoreCase(name)) {
				if (exit.errorValue() != null) {
					throw new XNScriptError(exit.errorValue().toTextString(ctx));
				} else {
					return XNHandlerExit.ended();
				}
			} else {
				throw new XNScriptError("Found exit "+exit.blockTypeValue()+" outside a "+exit.blockTypeValue()+" block");
			}
		case PASSED:
			return exit;
		case NEXTED:
			throw new XNScriptError("Found next "+exit.blockTypeValue()+" outside a "+exit.blockTypeValue()+" block");
		default:
			return XNHandlerExit.ended();
		}
	}
	
	private static XNHandlerExit evaluateFunctionHandler(XNInterpreter interp, XNContext ctx, XOMUserObject obj, XNFunctionHandler handler, List<XOMVariant> parameters) {
		XNStackFrame f = new XNStackFrame(handler.name, parameters);
		if (handler.parameters != null) {
			for (int i = 0; i < handler.parameters.size(); i++) {
				XNHandlerParameter param = handler.parameters.get(i);
				String paramName = param.name;
				XNDataType paramDatatypeObj = param.datatype;
				XNExpression paramValueExpr = param.value;
				f.setVariableScope(paramName, XNVariableScope.LOCAL);
				XOMDataType<? extends XOMVariant> paramDatatype =
					(paramDatatypeObj == null) ?
							XOMStringType.instance :
								ctx.getDataType(paramDatatypeObj.toNameString());
				if (paramDatatype == null) throw new XNScriptError("Unrecognized data type");
				XOMVariant paramValue =
					(i < parameters.size()) ?
							parameters.get(i) :
								(paramValueExpr == null) ?
										XOMEmpty.EMPTY :
											interp.evaluateExpression(paramValueExpr).unwrap();
				f.createLocalVariable(ctx, paramName, paramDatatype, paramValue);
			}
		}
		ctx.pushStackFrame(f);
		ctx.pushResponder(obj);
		XNHandlerExit exit = null;
		try {
			exit = interp.executeStatements(handler.body);
		} finally {
			ctx.popStackFrame();
			ctx.popResponder();
		}
		switch (exit.status()) {
		case ENDED:
			return XNHandlerExit.returned();
		case RETURNED:
			return exit;
		case EXITED:
			if (exit.blockTypeValue().equalsIgnoreCase(handler.name)) {
				if (exit.errorValue() != null) {
					throw new XNScriptError(exit.errorValue().toTextString(ctx));
				} else {
					return XNHandlerExit.returned();
				}
			} else {
				throw new XNScriptError("Found exit "+exit.blockTypeValue()+" outside a "+exit.blockTypeValue()+" block");
			}
		case PASSED:
			return exit;
		case NEXTED:
			throw new XNScriptError("Found next "+exit.blockTypeValue()+" outside a "+exit.blockTypeValue()+" block");
		default:
			return XNHandlerExit.returned();
		}
	}
	
	private static XNHandlerExit executeCommandHandler(XNInterpreter interp, XNContext ctx, XOMUserObject obj, XNMessageHandler handler, List<XOMVariant> parameters) {
		XNStackFrame f = new XNStackFrame(handler.name, parameters);
		if (handler.parameters != null) {
			for (int i = 0; i < handler.parameters.size(); i++) {
				XNHandlerParameter param = handler.parameters.get(i);
				String paramName = param.name;
				XNDataType paramDatatypeObj = param.datatype;
				XNExpression paramValueExpr = param.value;
				f.setVariableScope(paramName, XNVariableScope.LOCAL);
				XOMDataType<? extends XOMVariant> paramDatatype =
					(paramDatatypeObj == null) ?
							XOMStringType.instance :
								ctx.getDataType(paramDatatypeObj.toNameString());
				if (paramDatatype == null) throw new XNScriptError("Unrecognized data type");
				XOMVariant paramValue =
					(i < parameters.size()) ?
							parameters.get(i) :
								(paramValueExpr == null) ?
										XOMEmpty.EMPTY :
											interp.evaluateExpression(paramValueExpr).unwrap();
				f.createLocalVariable(ctx, paramName, paramDatatype, paramValue);
			}
		}
		ctx.pushStackFrame(f);
		ctx.pushResponder(obj);
		XNHandlerExit exit = null;
		try {
			exit = interp.executeStatements(handler.body);
		} finally {
			ctx.popStackFrame();
			ctx.popResponder();
		}
		switch (exit.status()) {
		case ENDED:
			return exit;
		case RETURNED:
			if (exit.returnValue() != null) ctx.setResult(exit.returnValue());
			return XNHandlerExit.ended();
		case EXITED:
			if (exit.blockTypeValue().equalsIgnoreCase(handler.name)) {
				if (exit.errorValue() != null) {
					throw new XNScriptError(exit.errorValue().toTextString(ctx));
				} else {
					return XNHandlerExit.ended();
				}
			} else {
				throw new XNScriptError("Found exit "+exit.blockTypeValue()+" outside a "+exit.blockTypeValue()+" block");
			}
		case PASSED:
			return exit;
		case NEXTED:
			throw new XNScriptError("Found next "+exit.blockTypeValue()+" outside a "+exit.blockTypeValue()+" block");
		default:
			return XNHandlerExit.ended();
		}
	}
}
