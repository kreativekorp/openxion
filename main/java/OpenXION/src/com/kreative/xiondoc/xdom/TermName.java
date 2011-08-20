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
 * @since XIONDoc 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.xiondoc.xdom;

import java.io.Serializable;

/**
 * The name of a term, along with a list of the dialects, modules, and libraries that support it.
 * Support is tied to the name because it is the name that is recognized or not.
 * Used to record the names of a term.
 * @since XIONDoc 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class TermName implements Serializable, Comparable<TermName> {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private DialectSpecList dialects;
	
	public TermName(String name, DialectSpecList dialects) {
		this.name = name;
		this.dialects = dialects;
	}
	
	public String getName() {
		return this.name;
	}
	
	public DialectSpecList getDialects() {
		return this.dialects;
	}
	
	public String toString() {
		return name;
	}
	
	public int compareTo(TermName other) {
		return this.name.compareToIgnoreCase(other.name);
	}
	
	public boolean equals(Object o) {
		if (o instanceof TermName) {
			TermName other = (TermName)o;
			return this.name.equalsIgnoreCase(other.name) && this.dialects.equals(other.dialects);
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return this.name.toLowerCase().hashCode() ^ this.dialects.hashCode();
	}
}
