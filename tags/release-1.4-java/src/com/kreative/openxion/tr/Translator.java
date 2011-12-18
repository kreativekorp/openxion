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

public class Translator implements Transformor {
	private TrCharacterSet incs;
	private TrCharacterSet outcs;
	
	Translator(TrCharacterSet inputSet, TrCharacterSet outputSet) {
		this.incs = inputSet;
		this.outcs = outputSet;
	}
	
	@Override
	public String transformFirst(String in) {
		StringBuffer out = new StringBuffer();
		int i = 0;
		while (i < in.length()) {
			int ch = in.codePointAt(i++);
			if (ch >= 0x10000) i++;
			int index = incs.indexOf(ch);
			if (index < 0) {
				out.append(Character.toChars(ch));
			} else {
				if (index >= outcs.length())
					index = outcs.length()-1;
				int rep = outcs.charAt(index);
				if (rep >= 0) {
					out.append(Character.toChars(rep));
				}
				out.append(in.substring(i));
				break;
			}
		}
		return out.toString();
	}
	
	@Override
	public String transformAll(String in) {
		StringBuffer out = new StringBuffer();
		int i = 0;
		while (i < in.length()) {
			int ch = in.codePointAt(i++);
			if (ch >= 0x10000) i++;
			int index = incs.indexOf(ch);
			if (index < 0) {
				out.append(Character.toChars(ch));
			} else {
				if (index >= outcs.length())
					index = outcs.length()-1;
				int rep = outcs.charAt(index);
				if (rep >= 0) {
					out.append(Character.toChars(rep));
				}
			}
		}
		return out.toString();
	}
}
