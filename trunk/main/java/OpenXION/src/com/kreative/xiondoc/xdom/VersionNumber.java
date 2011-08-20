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
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A version number.
 * Used to label versions of dialects, modules, and libraries.
 * @since XIONDoc 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class VersionNumber implements Serializable, Comparable<VersionNumber> {
	private static final long serialVersionUID = 1L;
	private static final Pattern NUMERIC_PATTERN = Pattern.compile("[0-9]+");
	private static final Pattern COMPONENT_PATTERN = Pattern.compile("[0-9]+|[A-Za-z]+");
	
	private String[] components;
	
	public VersionNumber(String version) {
		Vector<String> components = new Vector<String>();
		Matcher m = COMPONENT_PATTERN.matcher(version);
		while (m.find()) components.add(m.group());
		this.components = components.toArray(new String[0]);
	}
	
	public String toString() {
		StringBuffer version = new StringBuffer();
		boolean prevNumeric = false;
		for (String component : components) {
			boolean nextNumeric = NUMERIC_PATTERN.matcher(component).matches();
			if (prevNumeric == nextNumeric) version.append('.');
			version.append(component);
			prevNumeric = nextNumeric;
		}
		return version.toString();
	}
	
	private VersionNumber() {}
	public VersionNumber next() {
		int nc = this.components.length;
		VersionNumber v = new VersionNumber();
		v.components = new String[nc];
		for (int i = 0; i < nc; i++) {
			v.components[i] = this.components[i];
		}
		if (nc > 0) {
			String last = this.components[nc-1];
			if (NUMERIC_PATTERN.matcher(last).matches()) {
				v.components[nc-1] = Integer.toString(Integer.parseInt(last)+1);
			} else {
				v.components[nc-1] = Character.toString((char)(last.charAt(0)+1));
			}
		}
		return v;
	}
	
	public int compareTo(VersionNumber other) {
		for (int i = 0; i < this.components.length || i < other.components.length; i++) {
			String thisString = (i < this.components.length) ? this.components[i] : "";
			String otherString = (i < other.components.length) ? other.components[i] : "";
			boolean thisNumeric = NUMERIC_PATTERN.matcher(thisString).matches();
			boolean otherNumeric = NUMERIC_PATTERN.matcher(otherString).matches();
			if (thisNumeric && otherNumeric) {
				int thisNumber = Integer.parseInt(thisString);
				int otherNumber = Integer.parseInt(otherString);
				if (thisNumber != otherNumber) {
					return thisNumber - otherNumber;
				}
			} else if (thisNumeric) {
				return 1;
			} else if (otherNumeric) {
				return -1;
			} else {
				boolean thisIsLast = (i >= this.components.length-1);
				boolean otherIsLast = (i >= other.components.length-1);
				if (thisIsLast && !otherIsLast) {
					return 1;
				} else if (otherIsLast && !thisIsLast) {
					return -1;
				} else if (!thisString.equalsIgnoreCase(otherString)) {
					return thisString.compareToIgnoreCase(otherString);
				}
			}
		}
		return 0;
	}
	
	public boolean equals(Object o) {
		if (o instanceof VersionNumber) {
			VersionNumber other = (VersionNumber)o;
			if (this.components.length == other.components.length) {
				for (int i = 0; i < this.components.length; i++) {
					String thisString = this.components[i];
					String otherString = other.components[i];
					boolean thisNumeric = NUMERIC_PATTERN.matcher(thisString).matches();
					boolean otherNumeric = NUMERIC_PATTERN.matcher(otherString).matches();
					if (thisNumeric && otherNumeric) {
						int thisNumber = Integer.parseInt(thisString);
						int otherNumber = Integer.parseInt(otherString);
						if (thisNumber != otherNumber) {
							return false;
						}
					} else if (thisNumeric || otherNumeric) {
						return false;
					} else if (!thisString.equalsIgnoreCase(otherString)) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		int hash = 0;
		for (String component : components) {
			if (NUMERIC_PATTERN.matcher(component).matches()) {
				hash = ((hash << 4) | (hash >>> 28)) + Integer.parseInt(component);
			} else {
				hash ^= component.toLowerCase().hashCode();
			}
		}
		return hash;
	}
	
	public static void main(String[] args) throws java.io.IOException {
		for (String arg : args) {
			Vector<VersionNumber> v = new Vector<VersionNumber>();
			java.util.Scanner scan = new java.util.Scanner(new java.io.File(arg));
			while (scan.hasNextLine()) v.add(new VersionNumber(scan.nextLine()));
			scan.close();
			java.util.Collections.sort(v);
			for (VersionNumber vv : v) System.out.println(vv.toString() + " -> " + vv.next().toString());
		}
	}
}
