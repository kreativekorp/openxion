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

import java.util.List;
import com.kreative.openxion.XNToken;

public class XNIfBlock extends XNStatement {
	private static final long serialVersionUID = 112L;
	public XNToken ifToken;
	public XNExpression condition;
	public XNToken thenToken;
	public List<XNStatement> thenBlock;
	public XNToken elseToken;
	public List<XNStatement> elseBlock;
	public XNToken endIfToken;
	
	public int getIfLine() { if (ifToken == null) return 0; else return ifToken.beginLine; }
	public int getIfCol() { if (ifToken == null) return 0; else return ifToken.beginColumn; }
	public int getThenLine() { if (thenToken == null) return 0; else return thenToken.beginLine; }
	public int getThenCol() { if (thenToken == null) return 0; else return thenToken.beginColumn; }
	public int getElseLine() { if (elseToken == null) return 0; else return elseToken.beginLine; }
	public int getElseCol() { if (elseToken == null) return 0; else return elseToken.beginColumn; }
	public int getEndIfLine() { if (endIfToken == null) return 0; else return endIfToken.beginLine; }
	public int getEndIfCol() { if (endIfToken == null) return 0; else return endIfToken.beginColumn; }
	
	protected String toString(String indent) {
		String s = indent+"if "+condition.toString()+" then";
		if (thenBlock != null) {
			for (XNStatement st : thenBlock) s += "\n" + st.toString(indent+"\t");
		}
		if (elseBlock != null) {
			s += "\n"+indent+"else";
			for (XNStatement st : elseBlock) s += "\n" + st.toString(indent+"\t");
		}
		s += "\n"+indent+"end if";
		return s;
	}
}
