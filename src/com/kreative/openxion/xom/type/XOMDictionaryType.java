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

package com.kreative.openxion.xom.type;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.util.XIONUtil;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.XOMDataType;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.inst.XOMDictionary;
import com.kreative.openxion.xom.inst.XOMEmpty;
import com.kreative.openxion.xom.inst.XOMList;
import com.kreative.openxion.xom.inst.XOMReference;

public class XOMDictionaryType extends XOMDataType<XOMDictionary> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMDictionaryType instance = new XOMDictionaryType();
	public static final XOMListType listInstance = new XOMListType("dictionaries", DESCRIBABILITY_OF_PRIMITIVES, instance);
	
	private XOMDictionaryType() {
		super("dictionary", DESCRIBABILITY_OF_PRIMITIVES, XOMDictionary.class);
	}
	
	public boolean canMakeInstanceFrom(XNContext ctx, XOMVariant instance) {
		instance = instance.asPrimitive(ctx);
		if (instance instanceof XOMList) {
			List<? extends XOMVariant> l = instance.toPrimitiveList(ctx);
			if (l.size() == 1)
				if (canMakeInstanceFrom(ctx, l.get(0)))
					return true;
		}
		if (instance instanceof XOMReference) {
			if (canMakeInstanceFrom(ctx, ((XOMReference)instance).dereference(true)))
				return true;
		}
		if (instanceClass.isAssignableFrom(instance.getClass()))
			return true;
		else if (instance instanceof XOMEmpty)
			return true;
		else if (canMakeInstanceFromImpl(ctx, instance.toTextString(ctx)))
			return true;
		else
			return false;
	}
	public boolean canMakeInstanceFrom(XNContext ctx, XOMVariant left, XOMVariant right) {
		left = left.asPrimitive(ctx);
		right = right.asPrimitive(ctx);
		if (left instanceof XOMEmpty)
			return canMakeInstanceFrom(ctx, right);
		else if (right instanceof XOMEmpty)
			return canMakeInstanceFrom(ctx, left);
		else if (canMakeInstanceFrom(ctx, left) && canMakeInstanceFrom(ctx, right))
			return true;
		else if (canMakeInstanceFromImpl(ctx, left.toTextString(ctx) + right.toTextString(ctx)))
			return true;
		else
			return false;
	}
	public XOMDictionary makeInstanceFrom(XNContext ctx, XOMVariant instance) {
		instance = instance.asPrimitive(ctx);
		if (instance instanceof XOMList) {
			List<? extends XOMVariant> l = instance.toPrimitiveList(ctx);
			if (l.size() == 1)
				if (canMakeInstanceFrom(ctx, l.get(0)))
					return makeInstanceFrom(ctx, l.get(0));
		}
		if (instance instanceof XOMReference) {
			if (canMakeInstanceFrom(ctx, ((XOMReference)instance).dereference(true)))
				return makeInstanceFrom(ctx, ((XOMReference)instance).dereference(true));
		}
		if (instanceClass.isAssignableFrom(instance.getClass()))
			return instanceClass.cast(instance);
		else if (instance instanceof XOMEmpty)
			return XOMDictionary.EMPTY_DICTIONARY;
		else if (canMakeInstanceFromImpl(ctx, instance.toTextString(ctx)))
			return makeInstanceFromImpl(ctx, instance.toTextString(ctx));
		else
			throw new XOMMorphError(typeName);
	}
	public XOMDictionary makeInstanceFrom(XNContext ctx, XOMVariant left, XOMVariant right) {
		left = left.asPrimitive(ctx);
		right = right.asPrimitive(ctx);
		if (left instanceof XOMEmpty)
			return makeInstanceFrom(ctx, right);
		else if (right instanceof XOMEmpty)
			return makeInstanceFrom(ctx, left);
		else if (canMakeInstanceFrom(ctx, left) && canMakeInstanceFrom(ctx, right))
			return makeInstanceFromImpl(makeInstanceFrom(ctx, left), makeInstanceFrom(ctx, right));
		else if (canMakeInstanceFromImpl(ctx, left.toTextString(ctx) + right.toTextString(ctx)))
			return makeInstanceFromImpl(ctx, left.toTextString(ctx) + right.toTextString(ctx));
		else
			throw new XOMMorphError(typeName);
	}
	
	private boolean canMakeInstanceFromImpl(XNContext ctx, String s) {
		return s.equals("") || XIONUtil.canParseDictionary(ctx, s);
	}
	
	private XOMDictionary makeInstanceFromImpl(XNContext ctx, String s) {
		if (s.equals("")) return XOMDictionary.EMPTY_DICTIONARY;
		XOMDictionary d = XIONUtil.parseDictionary(ctx, s);
		if (d != null) return d;
		else throw new XOMMorphError(typeName);
	}
	
	private XOMDictionary makeInstanceFromImpl(XOMDictionary left, XOMDictionary right) {
		Map<String,XOMVariant> v = new LinkedHashMap<String,XOMVariant>();
		v.putAll(((XOMDictionary)left).toMap());
		v.putAll(((XOMDictionary)right).toMap());
		return new XOMDictionary(v);
	}
}
