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

public class TrComplementCharacterSet implements TrCharacterSet {
	private TrCharacterSet set;
	
	TrComplementCharacterSet(TrCharacterSet set) {
		this.set = set;
	}
	
	private BitSet bs = null;
	private BitSet bs() {
		if (bs == null) {
			bs = new BitSet();
			for (int i = 0; i < 0x110000; i++) {
				if (!this.set.contains(i)) {
					bs.set(i);
				}
			}
		}
		return bs;
	}

	@Override
	public int length() {
		return bs().cardinality();
	}

	@Override
	public boolean contains(int codePoint) {
		return !(codePoint < 0 || codePoint >= 0x110000 || this.set.contains(codePoint));
	}

	@Override
	public int indexOf(int codePoint) {
		if (codePoint < 0 || codePoint >= 0x110000 || this.set.contains(codePoint)) return -1;
		for (
				int idx = 0, ch = bs().nextSetBit(0);
				ch >= 0;
				idx++, ch = bs().nextSetBit(ch+1)
		) {
			if (ch == codePoint) return idx;
		}
		return -1;
	}

	@Override
	public int charAt(int index) {
		if (index < 0 || index >= 0x110000) return -1;
		for (
				int idx = 0, ch = bs().nextSetBit(0);
				ch >= 0;
				idx++, ch = bs().nextSetBit(ch+1)
		) {
			if (idx == index) return ch;
		}
		return -1;
	}
}
