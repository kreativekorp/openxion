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
 * A 'handle' to a dialect and a range of versions of that dialect.
 * Used when specifying which versions of a dialect support a given term.
 * @since XIONDoc 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class DialectSpec implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private VersionNumberRange versions;
	
	public DialectSpec(String name, VersionNumberRange versions) {
		this.name = name;
		this.versions = versions;
	}
	
	public DialectSpec(String spec) {
		spec = spec.trim();
		int i = 0;
		while (i < spec.length() && Character.isLetter(spec.charAt(i))) {
			i++;
		}
		this.name = spec.substring(0, i);
		this.versions = new VersionNumberRange(spec.substring(i).trim());
	}
	
	public String getName() {
		return this.name;
	}
	
	public VersionNumberRange getVersions() {
		return this.versions;
	}
	
	public boolean matches(String dialectName, VersionNumber dialectVersion) {
		return this.name.equalsIgnoreCase(dialectName) && this.versions.contains(dialectVersion);
	}
	
	public String toString() {
		return this.name + " " + this.versions.toString();
	}
	
	public boolean equals(Object o) {
		if (o instanceof DialectSpec) {
			DialectSpec other = (DialectSpec)o;
			return this.name.equalsIgnoreCase(other.name) && this.versions.equals(other.versions);
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return this.name.toLowerCase().hashCode() ^ this.versions.hashCode();
	}
}
