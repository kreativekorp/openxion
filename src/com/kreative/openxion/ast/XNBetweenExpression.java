/*
 * Copyright &copy; 2009-2010 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.openxion.ast;

import com.kreative.openxion.XNToken;

public class XNBetweenExpression extends XNExpression {
	private static final long serialVersionUID = 130L;
	public XNExpression left;
	public XNOperator operator;
	public XNExpression rightStart;
	public XNToken andToken;
	public XNExpression rightEnd;
	public XNToken[] options;
	
	public XNBetweenExpression(XNExpression l, XNOperator op, XNExpression r1, XNToken a, XNExpression r2, XNToken[] opt) {
		this.left = l; this.operator = op; this.rightStart = r1; this.andToken = a; this.rightEnd = r2; this.options = opt;
	}
	
	public int getBeginCol() {
		return left.getBeginCol();
	}

	public int getBeginLine() {
		return left.getBeginLine();
	}

	public int getEndCol() {
		if (options != null && options.length > 0) {
			return options[options.length-1].endColumn;
		} else {
			return rightEnd.getEndCol();
		}
	}

	public int getEndLine() {
		if (options != null && options.length > 0) {
			return options[options.length-1].endLine;
		} else {
			return rightEnd.getEndLine();
		}
	}
	
	private String optionString() {
		String s = "";
		if (options != null) {
			for (XNToken t : options) {
				s += t.image.toLowerCase();
			}
		}
		return s;
	}
	
	public boolean isLeftInclusive() {
		String s = optionString();
		return s.startsWith("inc") || s.startsWith("left") || s.equals("");
	}
	
	public boolean isRightInclusive() {
		String s = optionString();
		return s.startsWith("inc") || s.startsWith("right") || s.equals("");
	}
	
	public String toString() {
		String s = "( "+left+" "+operator+" "+rightStart+" "+andToken.image.toLowerCase()+" "+rightEnd;
		if (options != null) {
			for (XNToken o : options) {
				s += " "+o.image.trim().toLowerCase();
			}
		}
		return s + " )";
	}
}
