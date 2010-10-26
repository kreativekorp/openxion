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
 * @since OpenXION 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.xiondoc;

public class NameTermPair implements Comparable<NameTermPair> {
	private String s;
	private Term t;
	
	public NameTermPair(String s, Term t) {
		this.s = s;
		this.t = t;
	}
	
	public String getName() {
		return s;
	}
	
	public Term getTerm() {
		return t;
	}
	
	public String toString() {
		return s;
	}
	
	public boolean equals(Object o) {
		if (o instanceof NameTermPair) {
			NameTermPair other = (NameTermPair)o;
			return this.s.equalsIgnoreCase(other.s) && (this.t == other.t);
		}
		return false;
	}
	
	public int compareTo(NameTermPair other) {
		String n1 = this.s;
		String n2 = other.s;
		if (Character.isLetterOrDigit(n1.charAt(0)) && !Character.isLetterOrDigit(n2.charAt(0))) return -1;
		else if (!Character.isLetterOrDigit(n1.charAt(0)) && Character.isLetterOrDigit(n2.charAt(0))) return 1;
		else if (!n1.equalsIgnoreCase(n2)) return n1.compareToIgnoreCase(n2);
		else return this.t.getType().compareTo(other.t.getType());
	}
	
	public int hashCode() {
		return s.toLowerCase().hashCode() ^ t.hashCode();
	}
}
