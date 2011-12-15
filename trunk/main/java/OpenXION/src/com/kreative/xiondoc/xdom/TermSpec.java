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
 * A 'handle' on a term, consisting of the type of the term and the name of the term.
 * Used to create cross-references to other terms.
 * @since XIONDoc 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class TermSpec implements Serializable, Comparable<TermSpec> {
	private static final long serialVersionUID = 1L;
	
	private TermType type;
	private String name;
	
	public TermSpec(TermType type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public TermSpec(String spec) {
		String[] ss = spec.split(":", 2);
		if (ss.length >= 2) {
			this.type = TermType.forCode(ss[0].trim());
			this.name = ss[1].trim();
		} else {
			this.type = TermType.FUNCTION;
			this.name = spec.trim();
		}
	}
	
	public TermSpec(String spec, TermType def) {
		String[] ss = spec.split(":", 2);
		if (ss.length >= 2) {
			this.type = TermType.forCode(ss[0].trim());
			this.name = ss[1].trim();
		} else {
			this.type = def;
			this.name = spec.trim();
		}
	}
	
	public TermType getType() {
		return this.type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String toString() {
		return this.type.getCode() + ":" + this.name;
	}
	
	public int compareTo(TermSpec other) {
		int cmp = this.type.compareTo(other.type);
		if (cmp != 0) return cmp;
		
		String a = this.name;
		String b = other.name;
		boolean aIsLetter = a.length() > 0 && Character.isLetterOrDigit(a.charAt(0));
		boolean bIsLetter = b.length() > 0 && Character.isLetterOrDigit(b.charAt(0));
		if (aIsLetter == bIsLetter) {
			return a.compareToIgnoreCase(b);
		} else if (aIsLetter) {
			return -1;
		} else if (bIsLetter) {
			return 1;
		} else {
			return a.compareToIgnoreCase(b);
		}
	}
	
	public boolean equals(Object o) {
		if (o instanceof TermSpec) {
			TermSpec other = (TermSpec)o;
			return (this.type == other.type) && this.name.equalsIgnoreCase(other.name);
		} else {
			return true;
		}
	}
	
	public int hashCode() {
		return this.type.hashCode() ^ this.name.toLowerCase().hashCode();
	}
}
