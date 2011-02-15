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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.XOMDataType;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.inst.XOMDictionary;
import com.kreative.openxion.xom.inst.XOMEmpty;
import com.kreative.openxion.xom.inst.XOMList;
import com.kreative.openxion.xom.inst.XOMString;

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
			List<XOMVariant> v = ((XOMList)instance).toList();
			if (v.size() == 1)
				if (canMakeInstanceFrom(ctx, v.get(0).asGiven()))
					return true;
		}
		if (instance instanceof XOMDictionary) {
			return true;
		} else if (instance instanceof XOMEmpty) {
			return true;
		} else {
			String s = instance.toTextString(ctx);
			return s.equals("") || KEY_PATTERN.matcher(s).find();
		}
	}
	public boolean canMakeInstanceFrom(XNContext ctx, XOMVariant left, XOMVariant right) {
		left = left.asPrimitive(ctx);
		right = right.asPrimitive(ctx);
		if (left instanceof XOMEmpty) {
			return canMakeInstanceFrom(ctx, right);
		} else if (right instanceof XOMEmpty) {
			return canMakeInstanceFrom(ctx, left);
		} else if (left instanceof XOMDictionary && right instanceof XOMDictionary) {
			return true;
		} else {
			String s = left.toTextString(ctx) + right.toTextString(ctx);
			return s.equals("") || KEY_PATTERN.matcher(s).find();
		}
	}
	public XOMDictionary makeInstanceFrom(XNContext ctx, XOMVariant instance) {
		instance = instance.asPrimitive(ctx);
		if (instance instanceof XOMList) {
			List<XOMVariant> v = ((XOMList)instance).toList();
			if (v.size() == 1)
				if (canMakeInstanceFrom(ctx, v.get(0).asGiven()))
					return makeInstanceFrom(ctx, v.get(0).asGiven());
		}
		if (instance instanceof XOMDictionary) {
			return (XOMDictionary)instance;
		} else if (instance instanceof XOMEmpty) {
			return XOMDictionary.EMPTY_DICTIONARY;
		} else {
			String s = instance.toTextString(ctx);
			return makeInstanceFromImpl(ctx, s);
		}
	}
	public XOMDictionary makeInstanceFrom(XNContext ctx, XOMVariant left, XOMVariant right) {
		left = left.asPrimitive(ctx);
		right = right.asPrimitive(ctx);
		if (left instanceof XOMEmpty) {
			return makeInstanceFrom(ctx, right);
		} else if (right instanceof XOMEmpty) {
			return makeInstanceFrom(ctx, left);
		} else if (left instanceof XOMDictionary && right instanceof XOMDictionary) {
			Map<String,XOMVariant> v = new LinkedHashMap<String,XOMVariant>();
			v.putAll(((XOMDictionary)left).toMap());
			v.putAll(((XOMDictionary)right).toMap());
			return new XOMDictionary(v);
		} else {
			String s = left.toTextString(ctx) + right.toTextString(ctx);
			return makeInstanceFromImpl(ctx, s);
		}
	}
	
	// TODO use parser to convert
	private static final Pattern KEY_PATTERN = Pattern.compile("^([^\t].*?) = ");
	private static final Pattern SUBMAP_PATTERN = Pattern.compile("\\{(\r\n|\r|\n|\u2028|\u2029)(\t(.*?)(\r\n|\r|\n|\u2028|\u2029))*\\}");
	
	private XOMDictionary makeInstanceFromImpl(XNContext ctx, String s) {
		if (s.equals("")) return XOMDictionary.EMPTY_DICTIONARY;
		Matcher m = KEY_PATTERN.matcher(s);
		LinkedHashMap<String,XOMVariant> v = new LinkedHashMap<String,XOMVariant>();
		if (!m.find()) throw new XOMMorphError(typeName);
		while (true) {
			String key = m.group(1);
			int vstart = m.end();
			boolean vnext = m.find();
			int vend = vnext ? m.start() : s.length();
			String value = s.substring(vstart, vend);
			while (true) {
				if (value.endsWith("\r\n")) {
					vend -= 2;
					value = s.substring(vstart, vend);
				} else if (value.endsWith("\r") || value.endsWith("\n")) {
					vend--;
					value = s.substring(vstart, vend);
				} else if (value.endsWith("\u2028") || value.endsWith("\u2029")) {
					vend--;
					value = s.substring(vstart, vend);
				} else {
					break;
				}
			}
			Matcher m2 = SUBMAP_PATTERN.matcher(value);
			if (m2.matches()) {
				String smv = m2.group(2).replaceAll("^\t", "");
				if (canMakeInstanceFrom(ctx, new XOMString(smv))) {
					XOMDictionary d = makeInstanceFrom(ctx, new XOMString(smv));
					v.put(key, d);
				} else {
					v.put(key, new XOMString(value));
				}
			} else {
				v.put(key, new XOMString(value));
			}
			if (!vnext) break;
		}
		return new XOMDictionary(v);
	}
}
