/*
 * Copyright &copy; 2009-2011 Rebecca G. Bettencourt / Kreative Software
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
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.xom.type;

import java.text.*;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.xom.XOMValueDataType;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.inst.XOMBinary;

public class XOMBinaryType extends XOMValueDataType<XOMBinary> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMBinaryType instance = new XOMBinaryType();
	public static final XOMListType listInstance = new XOMListType("binaries", DESCRIBABILITY_OF_PRIMITIVES, instance);
	
	private XOMBinaryType() {
		super("binary", DESCRIBABILITY_OF_PRIMITIVES, XOMBinary.class);
	}
	
	protected boolean canMakeInstanceFromImpl(XNContext ctx) {
		return true;
	}
	protected boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		return false;
	}
	protected boolean canMakeInstanceFromImpl(XNContext ctx, String s) {
		if ((s.length() % 2) != 0) return false;
		CharacterIterator ci = new StringCharacterIterator(s);
		for (char ch = ci.first(); ch != CharacterIterator.DONE; ch = ci.next()) {
			if (!((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F') || (ch >= 'a' && ch <= 'f'))) {
				return false;
			}
		}
		return true;
	}
	protected XOMBinary makeInstanceFromImpl(XNContext ctx) {
		return XOMBinary.EMPTY_BINARY;
	}
	protected XOMBinary makeInstanceFromImpl(XNContext ctx, XOMVariant instance) {
		throw new XOMMorphError(typeName);
	}
	protected XOMBinary makeInstanceFromImpl(XNContext ctx, String s) {
		if ((s.length() % 2) != 0) throw new XOMMorphError(typeName);
		byte[] b = new byte[s.length() / 2];
		int bi = 0;
		boolean bstart = true;
		CharacterIterator ci = new StringCharacterIterator(s);
		for (char ch = ci.first(); ch != CharacterIterator.DONE; ch = ci.next()) {
			if (ch >= '0' && ch <= '9') {
				if (bstart) {
					b[bi] = (byte)(((ch - '0') << 4) & 0xF0);
					bstart = false;
				} else {
					b[bi] |= (byte)((ch - '0') & 0xF);
					bi++;
					bstart = true;
				}
			} else if (ch >= 'A' && ch <= 'F') {
				if (bstart) {
					b[bi] = (byte)(((ch - 'A' + 10) << 4) & 0xF0);
					bstart = false;
				} else {
					b[bi] |= (byte)((ch - 'A' + 10) & 0xF);
					bi++;
					bstart = true;
				}
			} else if (ch >= 'a' && ch <= 'f') {
				if (bstart) {
					b[bi] = (byte)(((ch - 'a' + 10) << 4) & 0xF0);
					bstart = false;
				} else {
					b[bi] |= (byte)((ch - 'a' + 10) & 0xF);
					bi++;
					bstart = true;
				}
			} else {
				throw new XOMMorphError(typeName);
			}
		}
		return new XOMBinary(b);
	}
}
