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
 * @since OpenXION 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.ast;

import com.kreative.openxion.XNToken;

public class XNNewExpression extends XNExpression {
	private static final long serialVersionUID = 103L;
	public XNDataType datatype;
	public XNToken[] datatypeTokens;
	public XNToken ofInToken;
	public XNExpression parentVariant;
	
	public XNNewExpression(XNDataType dt, XNToken[] dttk) {
		this.datatype = dt; this.datatypeTokens = dttk; this.ofInToken = null; this.parentVariant = null;
	}
	
	public XNNewExpression(XNDataType dt, XNToken[] dttk, XNToken of, XNExpression parent) {
		this.datatype = dt; this.datatypeTokens = dttk; this.ofInToken = of; this.parentVariant = parent;
	}
	
	public Object getSource() {
		return datatypeTokens[0].source;
	}
	
	public int getBeginLine() {
		return datatypeTokens[0].beginLine;
	}
	
	public int getBeginCol() {
		return datatypeTokens[0].beginColumn;
	}
	
	public int getEndLine() {
		if (parentVariant != null) {
			return parentVariant.getEndLine();
		} else {
			return datatypeTokens[datatypeTokens.length-1].endLine;
		}
	}
	
	public int getEndCol() {
		if (parentVariant != null) {
			return parentVariant.getEndCol();
		} else {
			return datatypeTokens[datatypeTokens.length-1].endColumn;
		}
	}
	
	public String toString() {
		String s = "a new";
		for (String d : datatype.name) s += " "+d;
		if (parentVariant != null) {
			s += " of "+parentVariant.toString();
		}
		return s;
	}
}
