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

package com.kreative.openxion.ast;

import java.util.ArrayList;
import java.util.List;
import com.kreative.openxion.XNToken;

public class XNDictionaryExpression extends XNExpression {
	private static final long serialVersionUID = 0xD1C7109A121L;
	
	public XNToken openingBrace;
	public List<XNExpression> keyExprs;
	public List<XNToken> equalSigns;
	public List<XNExpression> valueExprs;
	public XNToken closingBrace;
	
	public XNDictionaryExpression() {
		openingBrace = null;
		keyExprs = new ArrayList<XNExpression>();
		equalSigns = new ArrayList<XNToken>();
		valueExprs = new ArrayList<XNExpression>();
		closingBrace = null;
	}
	
	public Object getSource() {
		return openingBrace.source;
	}
	
	public int getBeginLine() {
		return openingBrace.beginLine;
	}
	
	public int getBeginCol() {
		return openingBrace.beginColumn;
	}
	
	public int getEndLine() {
		return closingBrace.endLine;
	}
	
	public int getEndCol() {
		return closingBrace.endColumn;
	}
	
	public String toString() {
		if (keyExprs.isEmpty() || valueExprs.isEmpty()) return "{}";
		StringBuffer s = new StringBuffer();
		s.append("{ ");
		for (int i = 0; i < keyExprs.size() && i < valueExprs.size(); i++) {
			s.append(keyExprs.get(i).toString());
			s.append(" = ");
			s.append(valueExprs.get(i).toString());
			s.append("; ");
		}
		s.delete(s.length()-2, s.length());
		s.append(" }");
		return s.toString();
	}
}
