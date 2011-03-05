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

package com.kreative.openxion.xom.inst;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.ast.XNModifier;
import com.kreative.openxion.util.XIONUtil;
import com.kreative.openxion.xom.XOMValue;
import com.kreative.openxion.xom.XOMVariant;

public class XOMDictionary extends XOMValue {
	private static final long serialVersionUID = 1L;
	
	public static final XOMDictionary EMPTY_DICTIONARY = new XOMDictionary();
	
	private LinkedHashMap<String,XOMVariant> theDictionary;
	
	private XOMDictionary() {
		theDictionary = new LinkedHashMap<String,XOMVariant>();
	}
	
	public XOMDictionary(Map<String,? extends XOMVariant> m) {
		theDictionary = new LinkedHashMap<String,XOMVariant>();
		theDictionary.putAll(m);
	}
	
	public Map<String,XOMVariant> toMap() {
		return Collections.unmodifiableMap(theDictionary);
	}
	
	public Class<? extends XOMVariant> getValueClass() {
		if (theDictionary.isEmpty()) {
			return (Class<? extends XOMVariant>) XOMVariant.class;
		} else {
			Class<?> commonClass = theDictionary.values().iterator().next().getClass();
			for (XOMVariant element : theDictionary.values()) {
				Set<Class<?>> elementClasses = new HashSet<Class<?>>();
				Class<?> elementClass = element.getClass();
				while (elementClass != null) {
					elementClasses.add(elementClass);
					elementClass = elementClass.getSuperclass();
				}
				while (!elementClasses.contains(commonClass)) {
					commonClass = commonClass.getSuperclass();
				}
			}
			return (Class<? extends XOMVariant>) commonClass.asSubclass(XOMVariant.class);
		}
	}
	
	public boolean canGetProperty(XNContext ctx, String property) {
		return (
				property.equalsIgnoreCase("number") ||
				property.equalsIgnoreCase("keys") ||
				property.equalsIgnoreCase("values")
		);
	}
	
	public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String property) {
		if (property.equalsIgnoreCase("number")) {
			return new XOMInteger(theDictionary.size());
		} else if (property.equalsIgnoreCase("keys")) {
			List<XOMString> v = new Vector<XOMString>();
			for (String s : theDictionary.keySet())
				v.add(new XOMString(s));
			return new XOMList(v);
		} else if (property.equalsIgnoreCase("values")) {
			return new XOMList(theDictionary.values());
		} else {
			return super.getProperty(ctx, modifier, property);
		}
	}
	
	public String toLanguageString() {
		if (theDictionary.isEmpty()) return "{}";
		StringBuffer theString = new StringBuffer();
		theString.append("{ ");
		for (Map.Entry<String, XOMVariant> e : theDictionary.entrySet()) {
			theString.append(XIONUtil.quote(e.getKey()));
			theString.append(" = ");
			theString.append(e.getValue().toLanguageString());
			theString.append("; ");
		}
		theString.delete(theString.length()-2, theString.length());
		theString.append(" }");
		return theString.toString();
	}
	public String toTextString(XNContext ctx) {
		if (theDictionary.isEmpty()) return "{}";
		StringBuffer theString = new StringBuffer();
		theString.append("{\n");
		theString.append(toTextString("\t"));
		theString.append("}");
		return theString.toString();
	}
	private String toTextString(String indent) {
		StringBuffer theString = new StringBuffer();
		for (Map.Entry<String,XOMVariant> e : theDictionary.entrySet()) {
			theString.append(indent);
			theString.append(XIONUtil.quote(e.getKey()));
			theString.append(" = ");
			if (e.getValue() instanceof XOMDictionary) {
				XOMDictionary d = (XOMDictionary)e.getValue();
				if (d.theDictionary.isEmpty()) {
					theString.append("{}");
				} else {
					theString.append("{\n");
					theString.append(d.toTextString(indent+"\t"));
					theString.append(indent);
					theString.append("}");
				}
			} else {
				theString.append(e.getValue().toLanguageString());
			}
			theString.append("\n");
		}
		return theString.toString();
	}
	public List<? extends XOMVariant> toVariantList(XNContext ctx) {
		return Arrays.asList(this);
	}
	public List<? extends XOMVariant> toPrimitiveList(XNContext ctx) {
		return Arrays.asList(this);
	}
	public int hashCode() {
		if (theDictionary == null) return 0;
		int hc = theDictionary.size();
		for (Map.Entry<String,XOMVariant> e : theDictionary.entrySet()) {
			hc ^= e.getKey().hashCode() ^ e.getValue().hashCode();
		}
		return hc;
	}
	public boolean equals(Object o) {
		if (o instanceof XOMDictionary) {
			XOMDictionary other = (XOMDictionary)o;
			if ((this.theDictionary == null || this.theDictionary.isEmpty()) && (other.theDictionary == null || other.theDictionary.isEmpty())) {
				return true;
			} else if (this.theDictionary.size() != other.theDictionary.size()) {
				return false;
			} else {
				return this.theDictionary.equals(other.theDictionary);
			}
		} else {
			return false;
		}
	}
}
