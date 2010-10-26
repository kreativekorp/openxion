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

package com.kreative.openxion.ast;

import java.util.List;

public class XNObjectTypeDeclaration extends XNStatement {
	private static final long serialVersionUID = 1L;
	public List<XNObjectTypeName> names;
	public XNDataType extendedtype;
	/* public List<XNDataType> implementedtypes; */
	public List<XNStatement> body;
	
	public String singularNameString() {
		for (XNObjectTypeName n : names) {
			if (!n.plural) return n.toNameString();
		}
		return "";
	}
	
	public String pluralNameString() {
		for (XNObjectTypeName n : names) {
			if (n.plural) return n.toNameString();
		}
		return singularNameString() + "s";
	}

	protected String toString(String indent) {
		String s = indent+"object type";
		if (names != null) {
			boolean first = true;
			for (XNObjectTypeName n : names) {
				if (first && !n.plural) {
					for (String d : n.name) {
						s += " "+d;
					}
				} else {
					s += " "+n.toString()+",";
				}
				first = false;
			}
			if (s.endsWith(",")) s = s.substring(0, s.length()-1);
		}
		if (extendedtype != null) {
			s += " extends";
			for (String d : extendedtype.name) {
				s += " "+d;
			}
		}
		/*
		if (implementedtypes != null) {
			s += " implements";
			for (XNDataType dt : implementedtypes) {
				for (String d : dt.name) {
					s += " "+d;
				}
				s += ",";
			}
			s = s.substring(0, s.length()-1);
		}
		*/
		if (body != null) {
			for (XNStatement st : body) {
				s += "\n" + st.toString(indent+"\t");
			}
		}
		s += "\n"+indent+"end";
		if (names != null && names.size() > 0) {
			for (String d : names.get(0).name) {
				s += " "+d;
			}
		}
		return s;
	}
}
