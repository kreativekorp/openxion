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

public class XNVariantIndexNameDescriptor extends XNVariantDescriptor {
	private static final long serialVersionUID = 1003L;
	
	public XNExpression start;
	public XNToken toToken;
	public XNExpression end;
	
	protected int internalGetBeginCol() {
		if (dtTokens != null && dtTokens.length > 0) return dtTokens[0].beginColumn;
		else if (start != null) return start.getBeginCol();
		else if (toToken != null) return toToken.beginColumn;
		else if (end != null) return end.getBeginCol();
		else return 0;
	}
	
	protected int internalGetBeginLine() {
		if (dtTokens != null && dtTokens.length > 0) return dtTokens[0].beginLine;
		else if (start != null) return start.getBeginLine();
		else if (toToken != null) return toToken.beginLine;
		else if (end != null) return end.getBeginLine();
		else return 0;
	}
	
	protected int internalGetEndCol() {
		if (end != null) return end.getEndCol();
		else if (toToken != null) return toToken.endColumn;
		else if (start != null) return start.getEndCol();
		else if (dtTokens != null && dtTokens.length > 0) return dtTokens[dtTokens.length-1].endColumn;
		else return 0;
	}
	
	protected int internalGetEndLine() {
		if (end != null) return end.getEndLine();
		else if (toToken != null) return toToken.endLine;
		else if (start != null) return start.getEndLine();
		else if (dtTokens != null && dtTokens.length > 0) return dtTokens[dtTokens.length-1].endLine;
		else return 0;
	}
	
	protected String internalToString() {
		String s = "";
		if (datatype != null) {
			for (String dt : datatype.name) {
				s += " "+dt;
			}
		}
		if (start != null) s += " "+start.toString();
		if (end != null) s += " through "+end.toString();
		return s.trim();
	}
}
