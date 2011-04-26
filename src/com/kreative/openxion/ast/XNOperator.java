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

package com.kreative.openxion.ast;

public enum XNOperator {
	NULL					("",								XNOperatorPrecedence.NULL),
	UNARY_SUBTRACT			("-",								XNOperatorPrecedence.UNARY),
	NOT						("not",								XNOperatorPrecedence.UNARY),
	BIT_NOT					("bitnot",							XNOperatorPrecedence.UNARY),
	EXISTS					("there is a",						XNOperatorPrecedence.UNARY),
	NOT_EXISTS				("there isn't a",					XNOperatorPrecedence.UNARY),
	REFERENCE_TO			("a reference to",					XNOperatorPrecedence.UNARY),
	REFERENT_OF				("the referent of",					XNOperatorPrecedence.UNARY),
	EXPONENT				("^",								XNOperatorPrecedence.EXPONENT),
	MULTIPLY				("*", 								XNOperatorPrecedence.MULTIPLY),
	DIVIDE					("/",								XNOperatorPrecedence.MULTIPLY),
	QUOT					("quot",							XNOperatorPrecedence.MULTIPLY),
	REM						("rem",								XNOperatorPrecedence.MULTIPLY),
	DIV						("div",								XNOperatorPrecedence.MULTIPLY),
	MOD						("mod",								XNOperatorPrecedence.MULTIPLY),
	ADD						("+",								XNOperatorPrecedence.ADD),
	SUBTRACT				("-",								XNOperatorPrecedence.ADD),
	SHIFT_LEFT				("<<",								XNOperatorPrecedence.SHIFT),
	SHIFT_RIGHT_SIGNED		(">>",								XNOperatorPrecedence.SHIFT),
	SHIFT_RIGHT_UNSIGNED	(">>>",								XNOperatorPrecedence.SHIFT),
	BIT_AND					("bitand",							XNOperatorPrecedence.BIT_AND),
	BIT_XOR					("bitxor",							XNOperatorPrecedence.BIT_XOR),
	BIT_OR					("bitor",							XNOperatorPrecedence.BIT_OR),
	STR_CONCAT				("&",								XNOperatorPrecedence.STR_CONCAT),
	STR_CONCAT_SPACE		("&&",								XNOperatorPrecedence.STR_CONCAT),
	LIST_CONCAT				(":",								XNOperatorPrecedence.LIST_CONCAT),
	LT_NUM					("<",								XNOperatorPrecedence.RELATION),
	GT_NUM					(">",								XNOperatorPrecedence.RELATION),
	LE_NUM					("<=",								XNOperatorPrecedence.RELATION),
	GE_NUM					(">=",								XNOperatorPrecedence.RELATION),
	LT_STR					("lt",								XNOperatorPrecedence.RELATION),
	GT_STR					("gt",								XNOperatorPrecedence.RELATION),
	LE_STR					("le",								XNOperatorPrecedence.RELATION),
	GE_STR					("ge",								XNOperatorPrecedence.RELATION),
	BETWEEN					("between",							XNOperatorPrecedence.RELATION),
	NOT_BETWEEN				("not between",						XNOperatorPrecedence.RELATION),
	CONTAINS				("contains",						XNOperatorPrecedence.RELATION),
	STARTS_WITH				("starts with",						XNOperatorPrecedence.RELATION),
	ENDS_WITH				("ends with",						XNOperatorPrecedence.RELATION),
	IN						("is in",							XNOperatorPrecedence.RELATION),
	WITHIN					("is within",						XNOperatorPrecedence.RELATION),
	ELEMENT_OF				("is an element of",				XNOperatorPrecedence.RELATION),
	PRECISELY_ELEMENT_OF	("is precisely an element of",		XNOperatorPrecedence.RELATION),
	NOT_CONTAINS			("doesn't contain",					XNOperatorPrecedence.RELATION),
	NOT_STARTS_WITH			("doesn't start with",				XNOperatorPrecedence.RELATION),
	NOT_ENDS_WITH			("doesn't end with",				XNOperatorPrecedence.RELATION),
	NOT_IN					("isn't in",						XNOperatorPrecedence.RELATION),
	NOT_WITHIN				("isn't within",					XNOperatorPrecedence.RELATION),
	NOT_ELEMENT_OF			("isn't an element of",				XNOperatorPrecedence.RELATION),
	NOT_PRECISELY_ELEMENT_OF("isn't precisely an element of",	XNOperatorPrecedence.RELATION),
	IS_A					("is a",							XNOperatorPrecedence.IS_A),
	IS_PRECISELY_A			("is precisely a",					XNOperatorPrecedence.IS_A),
	IS_NOT_A				("isn't a",							XNOperatorPrecedence.IS_A),
	IS_NOT_PRECISELY_A		("isn't precisely a",				XNOperatorPrecedence.IS_A),
	AS						("as",								XNOperatorPrecedence.IS_A),
	EQUAL					("=",								XNOperatorPrecedence.EQUAL),
	STRICT_EQUAL			("===",								XNOperatorPrecedence.EQUAL),
	STRING_EQUAL			("eq",								XNOperatorPrecedence.EQUAL),
	NOT_EQUAL				("<>",								XNOperatorPrecedence.EQUAL),
	NOT_STRICT_EQUAL		("!==",								XNOperatorPrecedence.EQUAL),
	NOT_STRING_EQUAL		("ne",								XNOperatorPrecedence.EQUAL),
	CMP_NUM					("<=>",								XNOperatorPrecedence.EQUAL),
	CMP_STR					("cmp",								XNOperatorPrecedence.EQUAL),
	AND						("and",								XNOperatorPrecedence.AND),
	SHORT_AND				("&&&",								XNOperatorPrecedence.AND),
	XOR						("xor",								XNOperatorPrecedence.XOR),
	OR						("or",								XNOperatorPrecedence.OR),
	SHORT_OR				("|||",								XNOperatorPrecedence.OR),
	LIST					(",",								XNOperatorPrecedence.LIST);
	
	private XNOperatorPrecedence pr;
	private String sr;
	
	private XNOperator(String sr, XNOperatorPrecedence pr) {
		this.pr = pr;
		this.sr = sr;
	}
	
	public XNOperatorPrecedence precedence() {
		return this.pr;
	}
	
	public String toString() {
		return this.sr;
	}
}
