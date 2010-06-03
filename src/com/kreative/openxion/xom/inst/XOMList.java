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

package com.kreative.openxion.xom.inst;

import java.util.*;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.ast.XNModifier;
import com.kreative.openxion.xom.XOMVariant;

public class XOMList extends XOMVariant {
	private static final long serialVersionUID = 1L;
	
	public static final XOMList EMPTY_LIST = new XOMList();
	
	private List<XOMVariant> theList;
	
	public XOMList() {
		this.theList = new Vector<XOMVariant>();
	}
	
	public XOMList(XOMVariant a) {
		this.theList = new Vector<XOMVariant>();
		this.theList.add(a);
	}
	
	public XOMList(XOMVariant a, XOMVariant b) {
		this.theList = new Vector<XOMVariant>();
		this.theList.add(a);
		this.theList.add(b);
	}
	
	public XOMList(XOMVariant a, XOMVariant b, XOMVariant c) {
		this.theList = new Vector<XOMVariant>();
		this.theList.add(a);
		this.theList.add(b);
		this.theList.add(c);
	}
	
	public XOMList(XOMVariant a, XOMVariant b, XOMVariant c, XOMVariant d) {
		this.theList = new Vector<XOMVariant>();
		this.theList.add(a);
		this.theList.add(b);
		this.theList.add(c);
		this.theList.add(d);
	}
	
	public XOMList(XOMVariant[] a) {
		this.theList = new Vector<XOMVariant>();
		this.theList.addAll(Arrays.asList(a));
	}
	
	public XOMList(XOMVariant[] a, XOMVariant[] b) {
		this.theList = new Vector<XOMVariant>();
		this.theList.addAll(Arrays.asList(a));
		this.theList.addAll(Arrays.asList(b));
	}
	
	public XOMList(Collection<? extends XOMVariant> c) {
		this.theList = new Vector<XOMVariant>();
		this.theList.addAll(c);
	}
	
	public XOMList(Collection<? extends XOMVariant> c, Collection<? extends XOMVariant> d) {
		this.theList = new Vector<XOMVariant>();
		this.theList.addAll(c);
		this.theList.addAll(d);
	}
	
	public Class<? extends XOMVariant> getElementClass() {
		if (theList.isEmpty()) {
			return (Class<? extends XOMVariant>) XOMVariant.class;
		} else {
			Class<?> commonClass = theList.get(0).getClass();
			for (XOMVariant element : theList) {
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
		return (property.equalsIgnoreCase("number"));
	}
	
	public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String property) {
		if (property.equalsIgnoreCase("number")) {
			return new XOMInteger(theList.size());
		} else {
			return super.getProperty(ctx, modifier, property);
		}
	}
	
	protected boolean equalsImpl(Object o) {
		if (o instanceof XOMList) {
			XOMList other = (XOMList)o;
			if ((this.theList == null || this.theList.isEmpty()) && (other.theList == null || other.theList.isEmpty())) {
				return true;
			} else if (this.theList.size() != other.theList.size()) {
				return false;
			} else {
				Iterator<XOMVariant> i = this.theList.iterator();
				Iterator<XOMVariant> j = this.theList.iterator();
				while (i.hasNext() && j.hasNext()) {
					if (!i.next().equals(j.next())) return false;
				}
				if (i.hasNext() || j.hasNext()) return false;
				return true;
			}
		} else {
			return false;
		}
	}
	public int hashCode() {
		if (theList == null) return 0;
		int hc = theList.size();
		for (XOMVariant v : theList) {
			hc ^= v.hashCode();
		}
		return hc;
	}
	public String toDescriptionString() {
		if (theList == null) return "";
		StringBuffer theString = new StringBuffer();
		for (XOMVariant theInstance : theList) {
			theString.append(theInstance.toDescriptionString());
			theString.append(",");
		}
		if (theString.length() > 0) theString.deleteCharAt(theString.length()-1);
		return theString.toString();
	}
	public String toTextString(XNContext ctx) {
		if (theList == null) return "";
		StringBuffer theString = new StringBuffer();
		for (XOMVariant theInstance : theList) {
			theString.append(theInstance.toTextString(ctx));
			theString.append(",");
		}
		if (theString.length() > 0) theString.deleteCharAt(theString.length()-1);
		return theString.toString();
	}
	public List<XOMVariant> toList(XNContext ctx) {
		return theList;
	}
}
