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
 * A range of version numbers.
 * Used to specify which versions of a specific dialect, module, or library contain a term.
 * @since XIONDoc 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class VersionNumberRange implements Serializable, Comparable<VersionNumberRange> {
	private static final long serialVersionUID = 1L;
	private static final Pattern  RANGE_PATTERN = Pattern.compile("(\\+*)([0-9A-Za-z.]+)(\\+*)-(-*)([0-9A-Za-z.]+)(-*)");
	private static final Pattern SINGLE_PATTERN = Pattern.compile("[0-9A-Za-z.]+");
	private static final Pattern  START_PATTERN = Pattern.compile("(\\+*)([0-9A-Za-z.]+)(\\+*)");
	private static final Pattern    END_PATTERN = Pattern.compile("(-*)([0-9A-Za-z.]+)(-*)");
	private static final Pattern   STAR_PATTERN = Pattern.compile("(\\**)([0-9A-Za-z.]+)(\\**)");
	
	private VersionNumber start;
	private boolean startInclusive;
	private VersionNumber end;
	private boolean endInclusive;
	
	public VersionNumberRange(VersionNumber v) {
		this.start = v;
		this.startInclusive = true;
		this.end = v;
		this.endInclusive = true;
	}
	
	public VersionNumberRange(VersionNumber start, VersionNumber end) {
		this.start = start;
		this.startInclusive = true;
		this.end = end;
		this.endInclusive = true;
	}
	
	public VersionNumberRange(VersionNumber start, boolean startInclusive, VersionNumber end, boolean endInclusive) {
		this.start = start;
		this.startInclusive = startInclusive;
		this.end = end;
		this.endInclusive = endInclusive;
	}
	
	public VersionNumberRange(String versions) {
		Matcher m;
		versions = versions.replaceAll("\\s+", "");
		if ((m = RANGE_PATTERN.matcher(versions)).matches()) {
			this.start = new VersionNumber(m.group(2));
			this.startInclusive = (m.group(1).length() == 0);
			this.end = new VersionNumber(m.group(5));
			this.endInclusive = (m.group(4).length() == 0);
		} else if ((m = SINGLE_PATTERN.matcher(versions)).matches()) {
			this.start = this.end = new VersionNumber(m.group());
			this.startInclusive = this.endInclusive = true;
		} else if ((m = START_PATTERN.matcher(versions)).matches()) {
			this.start = new VersionNumber(m.group(2));
			this.startInclusive = (m.group(1).length() == 0);
			this.end = null;
			this.endInclusive = true;
		} else if ((m = END_PATTERN.matcher(versions)).matches()) {
			this.start = null;
			this.startInclusive = true;
			this.end = new VersionNumber(m.group(2));
			this.endInclusive = (m.group(1).length() == 0);
		} else if ((m = STAR_PATTERN.matcher(versions)).matches()) {
			this.start = new VersionNumber(m.group(2));
			this.startInclusive = true;
			this.end = this.start.next();
			this.endInclusive = false;
		} else {
			this.start = this.end = null;
			this.startInclusive = this.endInclusive = true;
		}
	}
	
	public String toString() {
		if (this.start == null && this.end == null) {
			return "";
		} else if (this.end == null) {
			if (this.startInclusive)
				return this.start.toString() + "+";
			else
				return "+" + this.start.toString();
		} else if (this.start == null) {
			if (this.endInclusive)
				return this.end.toString() + "-";
			else
				return "-" + this.end.toString();
		} else if (this.startInclusive && this.endInclusive) {
			if (this.start.compareTo(this.end) == 0)
				return this.start.toString();
			else
				return this.start.toString() + "-" + this.end.toString();
		} else if (this.endInclusive) {
			return "+" + this.start.toString() + "-" + this.end.toString();
		} else if (this.startInclusive) {
			return this.start.toString() + "--" + this.end.toString();
		} else {
			return "+" + this.start.toString() + "--" + this.end.toString();
		}
	}
	
	public String toEnglishString() {
		if (this.start == null && this.end == null) {
			return "any version";
		} else if (this.end == null) {
			if (this.startInclusive)
				return this.start.toString() + " or above";
			else
				return "above " + this.start.toString();
		} else if (this.start == null) {
			if (this.endInclusive)
				return this.end.toString() + " or below";
			else
				return "below " + this.end.toString();
		} else if (this.startInclusive && this.endInclusive) {
			if (this.start.compareTo(this.end) == 0)
				return this.start.toString() + " only";
			else
				return this.start.toString() + " through " + this.end.toString();
		} else if (this.endInclusive) {
			return this.end.toString() + " or below but above " + this.start.toString();
		} else if (this.startInclusive) {
			return this.start.toString() + " or above but below " + this.end.toString();
		} else {
			return "above " + this.start.toString() + " but below " + this.end.toString();
		}
	}
	
	public boolean contains(VersionNumber version) {
		if (this.start == null && this.end == null) {
			return true;
		} else if (this.end == null) {
			int cmp = version.compareTo(this.start);
			return (cmp > 0 || (cmp == 0 && this.startInclusive));
		} else if (this.start == null) {
			int cmp = version.compareTo(this.end);
			return (cmp < 0 || (cmp == 0 && this.endInclusive));
		} else {
			int cmp1 = version.compareTo(this.start);
			int cmp2 = version.compareTo(this.end);
			return (cmp1 > 0 || (cmp1 == 0 && this.startInclusive))
				&& (cmp2 < 0 || (cmp2 == 0 && this.endInclusive));
		}
	}
	
	public boolean contains(VersionNumberRange other) {
		if (this.start == null && this.end == null) {
			return true;
		} else if (this.end == null) {
			if (other.start == null) return false;
			int cmp = other.start.compareTo(this.start);
			return (cmp > 0 || (cmp == 0 && (this.startInclusive || !other.startInclusive)));
		} else if (this.start == null) {
			if (other.end == null) return false;
			int cmp = other.end.compareTo(this.end);
			return (cmp < 0 || (cmp == 0 && (this.endInclusive || !other.endInclusive)));
		} else {
			if (other.start == null || other.end == null) return false;
			int cmp1 = other.start.compareTo(this.start);
			int cmp2 = other.end.compareTo(this.end);
			return (cmp1 > 0 || (cmp1 == 0 && (this.startInclusive || !other.startInclusive)))
				&& (cmp2 < 0 || (cmp2 == 0 && (this.endInclusive || !other.endInclusive)));
		}
	}
	
	public boolean intersects(VersionNumberRange other) {
		if (this.start == null && this.end == null) {
			return true;
		} else if (this.end == null) {
			if (other.end == null) return true;
			int cmp = other.end.compareTo(this.start);
			return (cmp > 0 || (cmp == 0 && this.startInclusive && other.endInclusive));
		} else if (this.start == null) {
			if (other.start == null) return true;
			int cmp = other.start.compareTo(this.end);
			return (cmp < 0 || (cmp == 0 && this.endInclusive && other.startInclusive));
		} else {
			if (other.start == null || other.end == null) return other.intersects(this);
			int cmp1 = other.end.compareTo(this.start);
			int cmp2 = other.start.compareTo(this.end);
			return (cmp1 > 0 || (cmp1 == 0 && this.startInclusive && other.endInclusive))
				&& (cmp2 < 0 || (cmp2 == 0 && this.endInclusive && other.startInclusive));
		}
	}
	
	public int compareTo(VersionNumberRange other) {
		if (this.start != null && other.start != null) {
			int cmp = this.start.compareTo(other.start);
			if (cmp != 0) return cmp;
			if (this.startInclusive && !other.startInclusive) return -1;
			if (other.startInclusive && !this.startInclusive) return 1;
		}
		else if (other.start != null) return -1;
		else if (this.start != null) return 1;
		
		if (this.end != null && other.end != null) {
			int cmp = this.end.compareTo(other.end);
			if (cmp != 0) return cmp;
			if (this.endInclusive && !other.endInclusive) return 1;
			if (other.endInclusive && !this.endInclusive) return -1;
		}
		else if (other.end != null) return 1;
		else if (this.end != null) return -1;
		
		return 0;
	}
	
	public boolean equals(Object o) {
		if (o instanceof VersionNumberRange) {
			VersionNumberRange other = (VersionNumberRange)o;
			return (
				(this.start == null) ? (other.start == null) :
				(other.start == null) ? (this.start == null) :
				(this.start.equals(other.start) && (this.startInclusive == other.startInclusive))
			) && (
				(this.end == null) ? (other.end == null) :
				(other.end == null) ? (this.end == null) :
				(this.end.equals(other.end) && (this.endInclusive == other.endInclusive))
			);
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return ((this.start == null) ? 0 : (this.start.hashCode() ^ (this.startInclusive ? 0 : 0xFFFF0000)))
			^  ((this.end   == null) ? 0 : (this.end  .hashCode() ^ (this.endInclusive   ? 0 : 0x0000FFFF)));
	}
	
	public static void main(String[] args) throws java.io.IOException {
		String[] testv = new String[]{ "1.0", "1.1", "1.2", "1.3", "1.4", "1.5", "2.0", "3.0" };
		VersionNumber[] testvn = new VersionNumber[testv.length];
		for (int i = 0; i < testv.length; i++) testvn[i] = new VersionNumber(testv[i]);
		
		for (VersionNumber vn : testvn) System.out.print(vn.toString() + "\t");
		System.out.println("DESCRIPTION");
		
		for (String arg : args) {
			Vector<VersionNumberRange> v = new Vector<VersionNumberRange>();
			java.util.Scanner scan = new java.util.Scanner(new java.io.File(arg));
			while (scan.hasNextLine()) v.add(new VersionNumberRange(scan.nextLine()));
			scan.close();
			java.util.Collections.sort(v);
			for (VersionNumberRange vv : v) {
				for (VersionNumber vn : testvn) System.out.print(vv.contains(vn) + "\t");
				System.out.println(vv.toString() + " -> " + vv.toEnglishString());
			}
		}
	}
}
