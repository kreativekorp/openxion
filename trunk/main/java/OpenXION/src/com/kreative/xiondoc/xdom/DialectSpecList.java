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

import java.util.HashSet;

/**
 * A list of dialects and versions of those dialects.
 * Used when specifying which dialects and versions of those dialects support a given term.
 * @since XIONDoc 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class DialectSpecList extends HashSet<DialectSpec> {
	private static final long serialVersionUID = 1L;
	
	public DialectSpecList() {
		// nothing
	}
	
	public DialectSpecList(String spec) {
		DialectSpec last = null;
		for (String subspec : spec.split(",")) {
			subspec = subspec.trim();
			if (subspec.length() > 0) {
				if (last == null || Character.isLetter(subspec.charAt(0))) {
					add(last = new DialectSpec(subspec));
				} else {
					add(last = new DialectSpec(last.getName(), new VersionNumberRange(subspec)));
				}
			}
		}
	}
	
	public boolean matches(String dialectName, VersionNumber dialectVersion) {
		for (DialectSpec spec : this) {
			if (spec.matches(dialectName, dialectVersion)) {
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		for (DialectSpec spec : this) {
			if (s.length() > 0) s.append(", ");
			s.append(spec.toString());
		}
		return s.toString();
	}
}
