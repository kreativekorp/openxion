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

package com.kreative.openxion.tr;

import java.util.ArrayList;
import java.util.List;

public class Matchor {
	private TrCharacterSet cs;
	
	Matchor(TrCharacterSet cs) {
		this.cs = cs;
	}
	
	public boolean matchesAny(String in) {
		int i = 0;
		while (i < in.length()) {
			int ch = in.codePointAt(i++);
			if (ch >= 0x10000) i++;
			if (cs.contains(ch)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean matchesAll(String in) {
		int i = 0;
		while (i < in.length()) {
			int ch = in.codePointAt(i++);
			if (ch >= 0x10000) i++;
			if (!cs.contains(ch)) {
				return false;
			}
		}
		return true;
	}
	
	public int findIn(String in, int i) {
		while (i < in.length()) {
			int ch = in.codePointAt(i);
			if (cs.contains(ch)) {
				return i;
			}
			if (ch >= 0x10000) {
				i += 2;
			} else {
				i++;
			}
		}
		return -1;
	}
	
	public int findIn(String in) {
		return findIn(in, 0);
	}
	
	public int findLastIn(String in, int offset) {
		int last = -1;
		int i = 0;
		while (i < offset) {
			int ch = in.codePointAt(i);
			if (cs.contains(ch)) {
				last = i;
			}
			if (ch >= 0x10000) {
				i += 2;
			} else {
				i++;
			}
		}
		return last;
	}
	
	public int findLastIn(String in) {
		return findLastIn(in, in.length());
	}
	
	public String[] split(String in) {
		List<String> components = new ArrayList<String>();
		StringBuffer s = new StringBuffer();
		int i = 0;
		while (i < in.length()) {
			int ch = in.codePointAt(i++);
			if (ch >= 0x10000) i++;
			if (cs.contains(ch)) {
				components.add(s.toString());
				s = new StringBuffer();
			} else {
				s.append(Character.toChars(ch));
			}
		}
		if (s.length() > 0) {
			components.add(s.toString());
		}
		return components.toArray(new String[0]);
	}
	
	public String[] split(String in, int limit) {
		if (limit <= 0) return new String[0];
		if (limit <= 1) return new String[]{in};
		List<String> components = new ArrayList<String>();
		StringBuffer s = new StringBuffer();
		int i = 0;
		while (i < in.length()) {
			int ch = in.codePointAt(i++);
			if (ch >= 0x10000) i++;
			if (cs.contains(ch)) {
				components.add(s.toString());
				s = new StringBuffer();
				if (components.size() >= limit-1) {
					components.add(in.substring(i));
					break;
				}
			} else {
				s.append(Character.toChars(ch));
			}
		}
		if (s.length() > 0) {
			components.add(s.toString());
		}
		return components.toArray(new String[0]);
	}
}
