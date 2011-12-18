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

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

class TrCachedCharacterSet implements TrCharacterSet {
	private TrCharacterSet set;
	
	TrCachedCharacterSet(TrCharacterSet set) {
		this.set = set;
	}
	
	private int length = -1;
	@Override
	public int length() {
		if (length >= 0) {
			return length;
		} else {
			length = set.length();
			return length;
		}
	}
	
	private BitSet containsChecked = new BitSet();
	private BitSet contains = new BitSet();
	@Override
	public boolean contains(int codePoint) {
		if (codePoint < 0) return false;
		if (containsChecked.get(codePoint)) {
			return contains.get(codePoint);
		} else {
			containsChecked.set(codePoint);
			if (set.contains(codePoint)) {
				contains.set(codePoint);
				return true;
			} else {
				return false;
			}
		}
	}
	
	private Map<Integer,Integer> indexOf = new HashMap<Integer,Integer>();
	@Override
	public int indexOf(int codePoint) {
		if (codePoint < 0) return -1;
		if (indexOf.containsKey(codePoint)) {
			return indexOf.get(codePoint);
		} else {
			int index = set.indexOf(codePoint);
			indexOf.put(codePoint, index);
			return index;
		}
	}
	
	private Map<Integer,Integer> charAt = new HashMap<Integer,Integer>();
	@Override
	public int charAt(int index) {
		if (index < 0) return -1;
		if (charAt.containsKey(index)) {
			return charAt.get(index);
		} else {
			int ch = set.charAt(index);
			charAt.put(index, ch);
			return ch;
		}
	}
}
