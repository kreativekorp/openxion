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
 * @since OpenXION 1.4
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.format;

import com.kreative.openxion.XNContext;
import com.kreative.openxion.xom.XOMVariant;

public class TrimmingFormatter extends Formatter {
	private int minLength;
	private int maxLength;
	private String leftPad;
	private String rightPad;
	private Formatter formatter;
	
	public TrimmingFormatter(Formatter formatter) {
		this(0, Integer.MAX_VALUE, null, " ", formatter);
	}
	
	public TrimmingFormatter(int length, Formatter formatter) {
		this(length, length, null, " ", formatter);
	}
	
	public TrimmingFormatter(int minLength, int maxLength, Formatter formatter) {
		this(minLength, maxLength, null, " ", formatter);
	}
	
	public TrimmingFormatter(int minLength, int maxLength, String leftPad, String rightPad, Formatter formatter) {
		this.minLength = (minLength < 0) ? 0 : minLength;
		this.maxLength = (maxLength < minLength) ? minLength : maxLength;
		this.leftPad = (leftPad == null || leftPad.length() == 0) ? null : leftPad;
		this.rightPad = (rightPad == null || rightPad.length() == 0) ? ((leftPad == null || leftPad.length() == 0) ? " " : null) : rightPad;
		this.formatter = formatter;
	}
	
	@Override
	public String format(XNContext ctx, XOMVariant v) {
		StringBuffer s = new StringBuffer();
		s.append(formatter.format(ctx, v));
		if (rightPad != null) {
			if (leftPad == null) {
				if (s.length() < minLength) {
					while (s.length() < minLength) s.append(rightPad);
					s.delete(minLength, s.length());
				} else if (s.length() > maxLength) {
					s.delete(maxLength, s.length());
				}
			} else {
				if (s.length() < minLength) {
					while (s.length() < minLength) {
						s.insert(0, leftPad);
						s.append(rightPad);
					}
					s.delete(0, (s.length() - minLength)/2);
					s.delete(minLength, s.length());
				} else if (s.length() > maxLength) {
					s.delete(0, (s.length() - maxLength)/2);
					s.delete(maxLength, s.length());
				}
			}
		} else {
			if (s.length() < minLength) {
				while (s.length() < minLength) s.insert(0, leftPad);
				s.delete(0, s.length() - minLength);
			} else if (s.length() > maxLength) {
				s.delete(0, s.length() - maxLength);
			}
		}
		return s.toString();
	}
}
