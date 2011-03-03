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

package com.kreative.openxion;

/**
 * XNParseError is thrown by XNParser when it encounters something in a script
 * that does not make any sense grammatically.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XNParseError extends XNScriptError {
	private static final long serialVersionUID = 1L;
	
	private String expected;
	private XNToken found;
	
	private static String describe(String found) {
		if (found.trim().length() == 0) return "end of line";
		else return found;
	}
	
	public XNParseError(String expected, XNToken found) {
		super(found.beginLine, found.beginColumn, "Expected "+expected+" but found "+describe(found.image));
		this.expected = expected;
		this.found = found;
	}
	
	public String getExpected() {
		return expected;
	}
	
	public XNToken getFound() {
		return found;
	}
}
