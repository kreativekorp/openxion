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

public class XNFunctionCallPropertyDescriptor extends XNExpression {
	private static final long serialVersionUID = 2000L;
	
	public XNToken theToken;
	public XNToken modifierToken;
	public XNModifier modifier;
	public XNToken idToken;
	public String identifier;
	public XNToken ofInToken;
	public XNExpression argument;
	
	public boolean isBuiltInFunction() {
		return (theToken != null || ofInToken != null);
	}
	
	public boolean isUserFunction() {
		return (theToken == null && ofInToken == null);
	}
	
	public Object getSource() {
		if (theToken != null) return theToken.source;
		else if (modifierToken != null) return modifierToken.source;
		else if (idToken != null) return idToken.source;
		else if (ofInToken != null) return ofInToken.source;
		else if (argument != null) return argument.getSource();
		else return null;
	}
	
	public int getBeginCol() {
		if (theToken != null) return theToken.beginColumn;
		else if (modifierToken != null) return modifierToken.beginColumn;
		else if (idToken != null) return idToken.beginColumn;
		else if (ofInToken != null) return ofInToken.beginColumn;
		else if (argument != null) return argument.getBeginCol();
		else return 0;
	}

	public int getBeginLine() {
		if (theToken != null) return theToken.beginLine;
		else if (modifierToken != null) return modifierToken.beginLine;
		else if (idToken != null) return idToken.beginLine;
		else if (ofInToken != null) return ofInToken.beginLine;
		else if (argument != null) return argument.getBeginLine();
		else return 0;
	}

	public int getEndCol() {
		if (argument != null) return argument.getEndCol();
		else if (ofInToken != null) return ofInToken.endColumn;
		else if (idToken != null) return idToken.endColumn;
		else if (modifierToken != null) return modifierToken.endColumn;
		else if (theToken != null) return theToken.endColumn;
		else return 0;
	}

	public int getEndLine() {
		if (argument != null) return argument.getEndLine();
		else if (ofInToken != null) return ofInToken.endLine;
		else if (idToken != null) return idToken.endLine;
		else if (modifierToken != null) return modifierToken.endLine;
		else if (theToken != null) return theToken.endLine;
		else return 0;
	}

	public String toString() {
		String s = "the";
		if (modifier != null) s += " "+modifier;
		if (identifier != null) s += " "+identifier;
		if (argument != null) s += " of "+argument.toString();
		return s;
	}
}
