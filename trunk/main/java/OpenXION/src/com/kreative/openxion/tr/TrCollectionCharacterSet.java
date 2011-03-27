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
import java.util.Collection;
import java.util.List;

class TrCollectionCharacterSet implements TrCharacterSet {
	private List<TrCharacterSet> sets;
	
	TrCollectionCharacterSet(TrCharacterSet... sets) {
		this.sets = new ArrayList<TrCharacterSet>();
		for (TrCharacterSet set : sets) {
			this.sets.add(set);
		}
	}
	
	TrCollectionCharacterSet(Collection<? extends TrCharacterSet> sets) {
		this.sets = new ArrayList<TrCharacterSet>();
		this.sets.addAll(sets);
	}
	
	@Override
	public int length() {
		int length = 0;
		for (TrCharacterSet set : this.sets) {
			length += set.length();
		}
		return length;
	}
	
	@Override
	public boolean contains(int codePoint) {
		for (TrCharacterSet set : this.sets) {
			if (set.contains(codePoint)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int indexOf(int codePoint) {
		int off = 0;
		for (TrCharacterSet set : this.sets) {
			if (set.contains(codePoint)) {
				return off + set.indexOf(codePoint);
			} else {
				off += set.length();
			}
		}
		return -1;
	}
	
	@Override
	public int charAt(int index) {
		for (TrCharacterSet set : this.sets) {
			if (index < set.length()) {
				return set.charAt(index);
			} else {
				index -= set.length();
			}
		}
		return -1;
	}
}
