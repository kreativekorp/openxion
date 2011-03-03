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

import java.text.*;
import java.util.*;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.xom.XOMDataType;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.inst.XOMList;
import com.kreative.openxion.xom.inst.XOMString;

public class XOMListType extends XOMDataType<XOMList> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMListType instance = new XOMListType();
	public static final XOMListType listInstance = new XOMListType("lists", DESCRIBABILITY_OF_PRIMITIVES, instance);
	
	private XOMListType() {
		super("list", DESCRIBABILITY_OF_PRIMITIVES, XOMList.class);
		this.elementInstanceType = XOMVariantType.instance;
	}
	
	private XOMDataType<? extends XOMVariant> elementInstanceType;
	public XOMListType(String typeName, int describability, XOMDataType<? extends XOMVariant> elementInstanceType) {
		super(typeName, describability, XOMList.class);
		this.elementInstanceType = elementInstanceType;
	}
	
	/*
	 * Instantiation of root variants of this type.
	 */
	
	public final boolean canGetSingletonInstance(XNContext ctx) {
		return elementInstanceType.canGetSingletonInstance(ctx);
	}
	public final boolean canGetMassInstance(XNContext ctx) {
		return elementInstanceType.canGetMassInstance(ctx);
	}
	public final XOMVariant getSingletonInstance(XNContext ctx) {
		return elementInstanceType.getSingletonInstance(ctx);
	}
	public final XOMVariant getMassInstance(XNContext ctx) {
		return elementInstanceType.getMassInstance(ctx);
	}
	
	public final boolean canGetInstanceByIndex(XNContext ctx, int index) {
		return elementInstanceType.canGetInstanceByIndex(ctx, index);
	}
	public final boolean canGetInstanceByIndex(XNContext ctx, int startIndex, int endIndex) {
		return elementInstanceType.canGetInstanceByIndex(ctx, startIndex, endIndex);
	}
	public final boolean canGetInstanceByID(XNContext ctx, int id) {
		return elementInstanceType.canGetInstanceByID(ctx, id);
	}
	public final boolean canGetInstanceByName(XNContext ctx, String name) {
		return elementInstanceType.canGetInstanceByName(ctx, name);
	}
	public final XOMVariant getInstanceByIndex(XNContext ctx, int index) {
		return elementInstanceType.getInstanceByIndex(ctx, index);
	}
	public final XOMVariant getInstanceByIndex(XNContext ctx, int startIndex, int endIndex) {
		return elementInstanceType.getInstanceByIndex(ctx, startIndex, endIndex);
	}
	public final XOMVariant getInstanceByID(XNContext ctx, int id) {
		return elementInstanceType.getInstanceByID(ctx, id);
	}
	public final XOMVariant getInstanceByName(XNContext ctx, String name) {
		return elementInstanceType.getInstanceByName(ctx, name);
	}
	
	public final boolean canCreateInstanceByIndex(XNContext ctx, int index) {
		return elementInstanceType.canCreateInstanceByIndex(ctx, index);
	}
	public final boolean canCreateInstanceByIndex(XNContext ctx, int startIndex, int endIndex) {
		return elementInstanceType.canCreateInstanceByIndex(ctx, startIndex, endIndex);
	}
	public final boolean canCreateInstanceByID(XNContext ctx, int id) {
		return elementInstanceType.canCreateInstanceByID(ctx, id);
	}
	public final boolean canCreateInstanceByName(XNContext ctx, String name) {
		return elementInstanceType.canCreateInstanceByName(ctx, name);
	}
	public final XOMVariant createInstanceByIndex(XNContext ctx, int index) {
		return elementInstanceType.createInstanceByIndex(ctx, index);
	}
	public final XOMVariant createInstanceByIndex(XNContext ctx, int startIndex, int endIndex) {
		return elementInstanceType.createInstanceByIndex(ctx, startIndex, endIndex);
	}
	public final XOMVariant createInstanceByID(XNContext ctx, int id) {
		return elementInstanceType.createInstanceByID(ctx, id);
	}
	public final XOMVariant createInstanceByName(XNContext ctx, String name) {
		return elementInstanceType.createInstanceByName(ctx, name);
	}
	
	public final boolean canCreateInstanceByIndexWith(XNContext ctx, int index, XOMVariant contents) {
		return elementInstanceType.canCreateInstanceByIndexWith(ctx, index, contents);
	}
	public final boolean canCreateInstanceByIndexWith(XNContext ctx, int startIndex, int endIndex, XOMVariant contents) {
		return elementInstanceType.canCreateInstanceByIndexWith(ctx, startIndex, endIndex, contents);
	}
	public final boolean canCreateInstanceByIDWith(XNContext ctx, int id, XOMVariant contents) {
		return elementInstanceType.canCreateInstanceByIDWith(ctx, id, contents);
	}
	public final boolean canCreateInstanceByNameWith(XNContext ctx, String name, XOMVariant contents) {
		return elementInstanceType.canCreateInstanceByNameWith(ctx, name, contents);
	}
	public final XOMVariant createInstanceByIndexWith(XNContext ctx, int index, XOMVariant contents) {
		return elementInstanceType.createInstanceByIndexWith(ctx, index, contents);
	}
	public final XOMVariant createInstanceByIndexWith(XNContext ctx, int startIndex, int endIndex, XOMVariant contents) {
		return elementInstanceType.createInstanceByIndexWith(ctx, startIndex, endIndex, contents);
	}
	public final XOMVariant createInstanceByIDWith(XNContext ctx, int id, XOMVariant contents) {
		return elementInstanceType.createInstanceByIDWith(ctx, id, contents);
	}
	public final XOMVariant createInstanceByNameWith(XNContext ctx, String name, XOMVariant contents) {
		return elementInstanceType.createInstanceByNameWith(ctx, name, contents);
	}
	
	/*
	 * Instantiation of child variants of this type.
	 */
	
	public final boolean canGetChildSingletonVariant(XNContext ctx, XOMVariant parent) {
		return elementInstanceType.canGetChildSingletonVariant(ctx, parent);
	}
	public final boolean canGetChildMassVariant(XNContext ctx, XOMVariant parent) {
		return elementInstanceType.canGetChildMassVariant(ctx, parent);
	}
	public final XOMVariant getChildSingletonVariant(XNContext ctx, XOMVariant parent) {
		return elementInstanceType.getChildSingletonVariant(ctx, parent);
	}
	public final XOMVariant getChildMassVariant(XNContext ctx, XOMVariant parent) {
		return elementInstanceType.getChildMassVariant(ctx, parent);
	}
	
	public final boolean canGetChildVariantByIndex(XNContext ctx, XOMVariant parent, int index) {
		return elementInstanceType.canGetChildVariantByIndex(ctx, parent, index);
	}
	public final boolean canGetChildVariantByIndex(XNContext ctx, XOMVariant parent, int startIndex, int endIndex) {
		return elementInstanceType.canGetChildVariantByIndex(ctx, parent, startIndex, endIndex);
	}
	public final boolean canGetChildVariantByID(XNContext ctx, XOMVariant parent, int id) {
		return elementInstanceType.canGetChildVariantByID(ctx, parent, id);
	}
	public final boolean canGetChildVariantByName(XNContext ctx, XOMVariant parent, String name) {
		return elementInstanceType.canGetChildVariantByName(ctx, parent, name);
	}
	public final XOMVariant getChildVariantByIndex(XNContext ctx, XOMVariant parent, int index) {
		return elementInstanceType.getChildVariantByIndex(ctx, parent, index);
	}
	public final XOMVariant getChildVariantByIndex(XNContext ctx, XOMVariant parent, int startIndex, int endIndex) {
		return elementInstanceType.getChildVariantByIndex(ctx, parent, startIndex, endIndex);
	}
	public final XOMVariant getChildVariantByID(XNContext ctx, XOMVariant parent, int id) {
		return elementInstanceType.getChildVariantByID(ctx, parent, id);
	}
	public final XOMVariant getChildVariantByName(XNContext ctx, XOMVariant parent, String name) {
		return elementInstanceType.getChildVariantByName(ctx, parent, name);
	}
	
	public final boolean canCreateChildVariantByIndex(XNContext ctx, XOMVariant parent, int index) {
		return elementInstanceType.canCreateChildVariantByIndex(ctx, parent, index);
	}
	public final boolean canCreateChildVariantByIndex(XNContext ctx, XOMVariant parent, int startIndex, int endIndex) {
		return elementInstanceType.canCreateChildVariantByIndex(ctx, parent, startIndex, endIndex);
	}
	public final boolean canCreateChildVariantByID(XNContext ctx, XOMVariant parent, int id) {
		return elementInstanceType.canCreateChildVariantByID(ctx, parent, id);
	}
	public final boolean canCreateChildVariantByName(XNContext ctx, XOMVariant parent, String name) {
		return elementInstanceType.canCreateChildVariantByName(ctx, parent, name);
	}
	public final XOMVariant createChildVariantByIndex(XNContext ctx, XOMVariant parent, int index) {
		return elementInstanceType.createChildVariantByIndex(ctx, parent, index);
	}
	public final XOMVariant createChildVariantByIndex(XNContext ctx, XOMVariant parent, int startIndex, int endIndex) {
		return elementInstanceType.createChildVariantByIndex(ctx, parent, startIndex, endIndex);
	}
	public final XOMVariant createChildVariantByID(XNContext ctx, XOMVariant parent, int id) {
		return elementInstanceType.createChildVariantByID(ctx, parent, id);
	}
	public final XOMVariant createChildVariantByName(XNContext ctx, XOMVariant parent, String name) {
		return elementInstanceType.createChildVariantByName(ctx, parent, name);
	}
	
	public final boolean canCreateChildVariantByIndexWith(XNContext ctx, XOMVariant parent, int index, XOMVariant contents) {
		return elementInstanceType.canCreateChildVariantByIndexWith(ctx, parent, index, contents);
	}
	public final boolean canCreateChildVariantByIndexWith(XNContext ctx, XOMVariant parent, int startIndex, int endIndex, XOMVariant contents) {
		return elementInstanceType.canCreateChildVariantByIndexWith(ctx, parent, startIndex, endIndex, contents);
	}
	public final boolean canCreateChildVariantByIDWith(XNContext ctx, XOMVariant parent, int id, XOMVariant contents) {
		return elementInstanceType.canCreateChildVariantByIDWith(ctx, parent, id, contents);
	}
	public final boolean canCreateChildVariantByNameWith(XNContext ctx, XOMVariant parent, String name, XOMVariant contents) {
		return elementInstanceType.canCreateChildVariantByNameWith(ctx, parent, name, contents);
	}
	public final XOMVariant createChildVariantByIndexWith(XNContext ctx, XOMVariant parent, int index, XOMVariant contents) {
		return elementInstanceType.createChildVariantByIndexWith(ctx, parent, index, contents);
	}
	public final XOMVariant createChildVariantByIndexWith(XNContext ctx, XOMVariant parent, int startIndex, int endIndex, XOMVariant contents) {
		return elementInstanceType.createChildVariantByIndexWith(ctx, parent, startIndex, endIndex, contents);
	}
	public final XOMVariant createChildVariantByIDWith(XNContext ctx, XOMVariant parent, int id, XOMVariant contents) {
		return elementInstanceType.createChildVariantByIDWith(ctx, parent, id, contents);
	}
	public final XOMVariant createChildVariantByNameWith(XNContext ctx, XOMVariant parent, String name, XOMVariant contents) {
		return elementInstanceType.createChildVariantByNameWith(ctx, parent, name, contents);
	}
	
	/*
	 * Polymorphism - The data type of an object is determined through these methods.
	 * Unlike in Java, where an object's type is determined by the class hierarchy,
	 * objects in XION can be of any mix of data types (hence the term variant for XION objects).
	 */
	
	private boolean allCanMorph(XNContext ctx, List<? extends XOMVariant> elements) {
		for (XOMVariant element : elements) {
			if (elementInstanceType.canMakeInstanceFrom(ctx, element));
			else return false;
		}
		return true;
	}
	private boolean allCanMorph(XNContext ctx, String[] ss) {
		for (String s : ss) {
			if (elementInstanceType.canMakeInstanceFrom(ctx, new XOMString(s)));
			else return false;
		}
		return true;
	}
	private String[] splitElements(String in) {
		Vector<String> out = new Vector<String>();
		CharacterIterator ci = new StringCharacterIterator(in);
		boolean done = false;
		int level = 0;
		while (!done) {
			StringBuffer tmp = new StringBuffer();
			while (true) {
				char ch = ci.next();
				if (ch == CharacterIterator.DONE) { done = true; break; } 
				else if (ch == '(') { level++; tmp.append(ch); }
				else if (ch == ')') { level--; tmp.append(ch); }
				else if (ch == ',' && level == 0) { done = false; break; }
				else tmp.append(ch);
			}
			out.add(tmp.toString());
		}
		return out.toArray(new String[0]);
	}
	public boolean canMakeInstanceFrom(XNContext ctx, XOMVariant instance) {
		instance = instance.asPrimitive(ctx);
		if (allCanMorph(ctx, instance.toPrimitiveList(ctx)))
			return true;
		String[] ss = splitElements(instance.toTextString(ctx));
		return allCanMorph(ctx, ss);
	}
	public boolean canMakeInstanceFrom(XNContext ctx, XOMVariant left, XOMVariant right) {
		left = left.asPrimitive(ctx);
		right = right.asPrimitive(ctx);
		if (canMakeInstanceFrom(ctx, left) && canMakeInstanceFrom(ctx, right)) {
			return true;
		}
		String[] ss = splitElements(left.toTextString(ctx) + right.toTextString(ctx));
		return allCanMorph(ctx, ss);
	}
	public XOMList makeInstanceFrom(XNContext ctx, XOMVariant instance) {
		instance = instance.asPrimitive(ctx);
		List<? extends XOMVariant> v = instance.toPrimitiveList(ctx);
		if (allCanMorph(ctx, v)) {
			List<XOMVariant> newElements = new Vector<XOMVariant>();
			for (XOMVariant e : v) {
				newElements.add(elementInstanceType.makeInstanceFrom(ctx, e));
			}
			return new XOMList(newElements);
		}
		String[] ss = splitElements(instance.toTextString(ctx));
		if (allCanMorph(ctx, ss)) {
			List<XOMVariant> newElements = new Vector<XOMVariant>();
			for (String s : ss) {
				newElements.add(elementInstanceType.makeInstanceFrom(ctx, new XOMString(s)));
			}
			return new XOMList(newElements);
		}
		throw new XOMMorphError(typeName);
	}
	public XOMList makeInstanceFrom(XNContext ctx, XOMVariant left, XOMVariant right) {
		left = left.asPrimitive(ctx);
		right = right.asPrimitive(ctx);
		if (canMakeInstanceFrom(ctx, left) && canMakeInstanceFrom(ctx, right)) {
			Vector<XOMVariant> v = new Vector<XOMVariant>();
			v.addAll(makeInstanceFrom(ctx, left).toVariantList(ctx));
			v.addAll(makeInstanceFrom(ctx, right).toVariantList(ctx));
			return new XOMList(v);
		}
		String[] ss = splitElements(left.toTextString(ctx) + right.toTextString(ctx));
		if (allCanMorph(ctx, ss)) {
			List<XOMVariant> newElements = new Vector<XOMVariant>();
			for (String s : ss) {
				newElements.add(elementInstanceType.makeInstanceFrom(ctx, new XOMString(s)));
			}
			return new XOMList(newElements);
		}
		throw new XOMMorphError(typeName);
	}
}
