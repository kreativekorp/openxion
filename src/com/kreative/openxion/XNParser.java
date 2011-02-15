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

import java.io.*;
import java.util.*;
import com.kreative.openxion.ast.*;
import com.kreative.openxion.util.XIONUtil;
import com.kreative.openxion.xom.XOMDataType;
import com.kreative.openxion.xom.XOMVariant;

/**
 * XNParser is the main class responsible for parsing XION scripts
 * (turning words into statements).
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XNParser {
	// Change this number to adjust how many blank lines can be between parts of a block.
	private static final int LOOKAHEAD_LIMIT = 65536;

	private XNContext context;
	private XNLexer lexer;
	private Set<String> knownAdditionalConstants;
	private Set<String> knownAdditionalOrdinals;
	private Map<String, Integer> knownAdditionalDataTypes;
	
	public XNParser(XNContext context, XNLexer lexer) {
		this.context = context;
		this.lexer = lexer;
		knownAdditionalConstants = new HashSet<String>();
		knownAdditionalOrdinals = new HashSet<String>();
		knownAdditionalDataTypes = new HashMap<String, Integer>();
	}
	
	public Object getSource() {
		return lexer.getSource();
	}
	
	public int getCurrentLine() {
		return lexer.getCurrentLine();
	}
	
	public int getCurrentCol() {
		return lexer.getCurrentCol();
	}
	
	/* * * * * * * 
	 * TOKENIZER *
	 * * * * * * */
	
	public XNToken lookToken(int pos) {
		try {
			return lexer.lookToken(pos);
		} catch (IOException e) {
			throw new XNScriptError(e, lexer.getCurrentLine(), lexer.getCurrentCol(), "Could not read script");
		}
	}

	public XNToken getToken() {
		try {
			return lexer.getToken();
		} catch (IOException e) {
			throw new XNScriptError(e, lexer.getCurrentLine(), lexer.getCurrentCol(), "Could not read script");
		}
	}

	public void consumeTokens(int n) {
		try {
			for (int i = 0; i < n; i++) {
				lexer.getToken();
			}
		} catch (IOException e) {
			throw new XNScriptError(e, lexer.getCurrentLine(), lexer.getCurrentCol(), "Could not read script");
		}
	}
	
	/* * * * * * * * * * *
	 * EXPRESSION PARSER *
	 * * * * * * * * * * */
	
	public XNOperator lookOperator(int idx, boolean unary) {
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("^") && lookToken(idx+1).kind == XNToken.SYMBOL && lookToken(idx+1).image.equalsIgnoreCase("^") && lookToken(idx+2).kind == XNToken.SYMBOL && lookToken(idx+2).image.equalsIgnoreCase("^")) return XNOperator.XOR;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("^")) return XNOperator.EXPONENT;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("-")) return unary ? XNOperator.UNARY_SUBTRACT : XNOperator.SUBTRACT;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase(",")) return XNOperator.LIST;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase(":") && lookToken(idx+1).kind == XNToken.SYMBOL && lookToken(idx+1).image.equalsIgnoreCase(":")) return XNOperator.LIST_CONCAT;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase(":")) return XNOperator.LIST_CONCAT;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("!") && lookToken(idx+1).kind == XNToken.SYMBOL && lookToken(idx+1).image.equalsIgnoreCase("=") && lookToken(idx+2).kind == XNToken.SYMBOL && lookToken(idx+2).image.equalsIgnoreCase("=")) return XNOperator.NOT_STRICT_EQUAL;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("!") && lookToken(idx+1).kind == XNToken.SYMBOL && lookToken(idx+1).image.equalsIgnoreCase("=")) return XNOperator.NOT_EQUAL;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("!")) return XNOperator.NOT;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("*") && lookToken(idx+1).kind == XNToken.SYMBOL && lookToken(idx+1).image.equalsIgnoreCase("*")) return XNOperator.EXPONENT;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("*")) return XNOperator.MULTIPLY;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("/")) return XNOperator.DIVIDE;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("\u00B7")) return XNOperator.MULTIPLY;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("\u00D7")) return XNOperator.MULTIPLY;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("\u00F7")) return XNOperator.DIVIDE;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("\u2212")) return unary ? XNOperator.UNARY_SUBTRACT : XNOperator.SUBTRACT;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("\u2260") && lookToken(idx+1).kind == XNToken.SYMBOL && lookToken(idx+1).image.equalsIgnoreCase("\u2260") && lookToken(idx+2).kind == XNToken.SYMBOL && lookToken(idx+2).image.equalsIgnoreCase("\u2260")) return XNOperator.NOT_STRICT_EQUAL;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("\u2260") && lookToken(idx+1).kind == XNToken.SYMBOL && lookToken(idx+1).image.equalsIgnoreCase("\u2260")) return XNOperator.NOT_EQUAL;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("\u2260")) return XNOperator.NOT_EQUAL;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("\u2264")) return XNOperator.LE_NUM;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("\u2265")) return XNOperator.GE_NUM;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("&") && lookToken(idx+1).kind == XNToken.SYMBOL && lookToken(idx+1).image.equalsIgnoreCase("&") && lookToken(idx+2).kind == XNToken.SYMBOL && lookToken(idx+2).image.equalsIgnoreCase("&")) return XNOperator.SHORT_AND;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("&") && lookToken(idx+1).kind == XNToken.SYMBOL && lookToken(idx+1).image.equalsIgnoreCase("&")) return XNOperator.STR_CONCAT_SPACE;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("&")) return XNOperator.STR_CONCAT;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("%")) return XNOperator.REM;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("+")) return XNOperator.ADD;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("<") && lookToken(idx+1).kind == XNToken.SYMBOL && lookToken(idx+1).image.equalsIgnoreCase("=") && lookToken(idx+2).kind == XNToken.SYMBOL && lookToken(idx+2).image.equalsIgnoreCase(">")) return XNOperator.CMP_NUM;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("<") && lookToken(idx+1).kind == XNToken.SYMBOL && lookToken(idx+1).image.equalsIgnoreCase("<")) return XNOperator.SHIFT_LEFT;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("<") && lookToken(idx+1).kind == XNToken.SYMBOL && lookToken(idx+1).image.equalsIgnoreCase("=")) return XNOperator.LE_NUM;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("<") && lookToken(idx+1).kind == XNToken.SYMBOL && lookToken(idx+1).image.equalsIgnoreCase(">")) return XNOperator.NOT_EQUAL;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("<")) return XNOperator.LT_NUM;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("=") && lookToken(idx+1).kind == XNToken.SYMBOL && lookToken(idx+1).image.equalsIgnoreCase("=") && lookToken(idx+2).kind == XNToken.SYMBOL && lookToken(idx+2).image.equalsIgnoreCase("=")) return XNOperator.STRICT_EQUAL;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("=") && lookToken(idx+1).kind == XNToken.SYMBOL && lookToken(idx+1).image.equalsIgnoreCase("=")) return XNOperator.EQUAL;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("=") && lookToken(idx+1).kind == XNToken.SYMBOL && lookToken(idx+1).image.equalsIgnoreCase("<")) return XNOperator.LE_NUM;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("=") && lookToken(idx+1).kind == XNToken.SYMBOL && lookToken(idx+1).image.equalsIgnoreCase(">")) return XNOperator.GE_NUM;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("=")) return XNOperator.EQUAL;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase(">") && lookToken(idx+1).kind == XNToken.SYMBOL && lookToken(idx+1).image.equalsIgnoreCase(">") && lookToken(idx+2).kind == XNToken.SYMBOL && lookToken(idx+2).image.equalsIgnoreCase(">")) return XNOperator.SHIFT_RIGHT_UNSIGNED;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase(">") && lookToken(idx+1).kind == XNToken.SYMBOL && lookToken(idx+1).image.equalsIgnoreCase(">")) return XNOperator.SHIFT_RIGHT_SIGNED;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase(">") && lookToken(idx+1).kind == XNToken.SYMBOL && lookToken(idx+1).image.equalsIgnoreCase("=")) return XNOperator.GE_NUM;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase(">")) return XNOperator.GT_NUM;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("|") && lookToken(idx+1).kind == XNToken.SYMBOL && lookToken(idx+1).image.equalsIgnoreCase("|") && lookToken(idx+2).kind == XNToken.SYMBOL && lookToken(idx+2).image.equalsIgnoreCase("|")) return XNOperator.SHORT_OR;
		if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("|") && lookToken(idx+1).kind == XNToken.SYMBOL && lookToken(idx+1).image.equalsIgnoreCase("|")) return XNOperator.STR_CONCAT;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("and")) return XNOperator.AND;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("are") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("precisely")) return XNOperator.IS_NOT_PRECISELY_A;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("are") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not")) return XNOperator.IS_NOT_A;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("are") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("precisely")) return XNOperator.IS_PRECISELY_A;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("are")) return XNOperator.IS_A;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("aren't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("precisely")) return XNOperator.IS_NOT_PRECISELY_A;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("aren't")) return XNOperator.IS_NOT_A;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("as") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("an")) return XNOperator.AS;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("as") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("a")) return XNOperator.AS;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("as")) return XNOperator.AS;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("begins") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("with")) return XNOperator.STARTS_WITH;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("between")) return XNOperator.BETWEEN;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("bitand")) return XNOperator.BIT_AND;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("bitnot")) return XNOperator.BIT_NOT;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("bitor")) return XNOperator.BIT_OR;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("bitxor")) return XNOperator.BIT_XOR;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("cmp")) return XNOperator.CMP_STR;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("comes") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("before")) return XNOperator.LT_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("comes") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("after")) return XNOperator.GT_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("compared") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("to")) return XNOperator.CMP_STR;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("contains")) return XNOperator.CONTAINS;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("div")) return XNOperator.DIV;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("does") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("contain")) return XNOperator.NOT_CONTAINS;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("does") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("begin") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("with")) return XNOperator.NOT_STARTS_WITH;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("does") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("start") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("with")) return XNOperator.NOT_STARTS_WITH;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("does") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("end") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("with")) return XNOperator.NOT_ENDS_WITH;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("does") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("equal")) return XNOperator.NOT_EQUAL;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("doesn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("contain")) return XNOperator.NOT_CONTAINS;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("doesn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("begin") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("with")) return XNOperator.NOT_STARTS_WITH;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("doesn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("start") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("with")) return XNOperator.NOT_STARTS_WITH;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("doesn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("end") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("with")) return XNOperator.NOT_ENDS_WITH;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("doesn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("equal")) return XNOperator.NOT_EQUAL;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("ends") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("with")) return XNOperator.ENDS_WITH;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("eq")) return XNOperator.STRING_EQUAL;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("equals")) return XNOperator.EQUAL;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("ge")) return XNOperator.GE_STR;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("gt")) return XNOperator.GT_STR;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("instanceof")) return XNOperator.IS_A;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("equal") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("to")) return XNOperator.NOT_EQUAL;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("exactly")) return XNOperator.NOT_STRICT_EQUAL;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("precisely") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("an") && lookToken(idx+4).kind == XNToken.ID && lookToken(idx+4).image.equalsIgnoreCase("element") && lookToken(idx+5).kind == XNToken.ID && lookToken(idx+5).image.equalsIgnoreCase("of")) return XNOperator.NOT_PRECISELY_ELEMENT_OF;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("precisely") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("an") && lookToken(idx+4).kind == XNToken.ID && lookToken(idx+4).image.equalsIgnoreCase("element") && lookToken(idx+5).kind == XNToken.ID && lookToken(idx+5).image.equalsIgnoreCase("in")) return XNOperator.NOT_PRECISELY_ELEMENT_OF;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("precisely") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("an")) return XNOperator.IS_NOT_PRECISELY_A;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("precisely") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("a")) return XNOperator.IS_NOT_PRECISELY_A;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("an") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("element") && lookToken(idx+4).kind == XNToken.ID && lookToken(idx+4).image.equalsIgnoreCase("of")) return XNOperator.NOT_ELEMENT_OF;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("an") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("element") && lookToken(idx+4).kind == XNToken.ID && lookToken(idx+4).image.equalsIgnoreCase("in")) return XNOperator.NOT_ELEMENT_OF;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("an")) return XNOperator.IS_NOT_A;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("a")) return XNOperator.IS_NOT_A;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("less") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("than")) return XNOperator.GE_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("smaller") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("than")) return XNOperator.GE_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("fewer") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("than")) return XNOperator.GE_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("greater") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("than")) return XNOperator.LE_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("bigger") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("than")) return XNOperator.LE_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("more") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("than")) return XNOperator.LE_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("before")) return XNOperator.GE_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("after")) return XNOperator.LE_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("in")) return XNOperator.NOT_IN;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("of")) return XNOperator.NOT_IN;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("within")) return XNOperator.NOT_WITHIN;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("between")) return XNOperator.NOT_BETWEEN;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("not")) return XNOperator.NOT_EQUAL;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("equal") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("to")) return XNOperator.EQUAL;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("exactly")) return XNOperator.STRICT_EQUAL;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("precisely") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("an") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("element") && lookToken(idx+4).kind == XNToken.ID && lookToken(idx+4).image.equalsIgnoreCase("of")) return XNOperator.PRECISELY_ELEMENT_OF;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("precisely") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("an") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("element") && lookToken(idx+4).kind == XNToken.ID && lookToken(idx+4).image.equalsIgnoreCase("in")) return XNOperator.PRECISELY_ELEMENT_OF;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("precisely") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("an")) return XNOperator.IS_PRECISELY_A;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("precisely") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("a")) return XNOperator.IS_PRECISELY_A;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("an") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("element") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("of")) return XNOperator.ELEMENT_OF;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("an") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("element") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("in")) return XNOperator.ELEMENT_OF;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("an")) return XNOperator.IS_A;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("a")) return XNOperator.IS_A;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("less") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("than")) return XNOperator.LT_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("smaller") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("than")) return XNOperator.LT_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("fewer") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("than")) return XNOperator.LT_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("greater") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("than")) return XNOperator.GT_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("bigger") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("than")) return XNOperator.GT_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("more") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("than")) return XNOperator.GT_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("before")) return XNOperator.LT_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("after")) return XNOperator.GT_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("in")) return XNOperator.IN;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("of")) return XNOperator.IN;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("within")) return XNOperator.WITHIN;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("between")) return XNOperator.BETWEEN;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("is")) return XNOperator.EQUAL;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isa")) return XNOperator.IS_A;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("equal") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("to")) return XNOperator.NOT_EQUAL;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("exactly")) return XNOperator.NOT_STRICT_EQUAL;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("precisely") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("an") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("element") && lookToken(idx+4).kind == XNToken.ID && lookToken(idx+4).image.equalsIgnoreCase("of")) return XNOperator.NOT_PRECISELY_ELEMENT_OF;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("precisely") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("an") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("element") && lookToken(idx+4).kind == XNToken.ID && lookToken(idx+4).image.equalsIgnoreCase("in")) return XNOperator.NOT_PRECISELY_ELEMENT_OF;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("precisely") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("an")) return XNOperator.IS_NOT_PRECISELY_A;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("precisely") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("a")) return XNOperator.IS_NOT_PRECISELY_A;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("an") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("element") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("of")) return XNOperator.NOT_ELEMENT_OF;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("an") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("element") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("in")) return XNOperator.NOT_ELEMENT_OF;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("an")) return XNOperator.IS_NOT_A;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("a")) return XNOperator.IS_NOT_A;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("less") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("than")) return XNOperator.GE_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("smaller") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("than")) return XNOperator.GE_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("fewer") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("than")) return XNOperator.GE_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("greater") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("than")) return XNOperator.LE_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("bigger") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("than")) return XNOperator.LE_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("more") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("than")) return XNOperator.LE_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("before")) return XNOperator.GE_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("after")) return XNOperator.LE_NUM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("in")) return XNOperator.NOT_IN;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("of")) return XNOperator.NOT_IN;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("within")) return XNOperator.NOT_WITHIN;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("between")) return XNOperator.NOT_BETWEEN;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("isn't")) return XNOperator.NOT_EQUAL;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("le")) return XNOperator.LE_STR;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("lt")) return XNOperator.LT_STR;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("mod")) return XNOperator.MOD;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("ne")) return XNOperator.NOT_STRING_EQUAL;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("not") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("between")) return XNOperator.NOT_BETWEEN;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("not")) return XNOperator.NOT;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("or")) return XNOperator.OR;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("quot")) return XNOperator.QUOT;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("rem")) return XNOperator.REM;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("starts") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("with")) return XNOperator.STARTS_WITH;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("there's") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("no")) return XNOperator.NOT_EXISTS;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("there's") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("an")) return XNOperator.EXISTS;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("there's") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("a")) return XNOperator.EXISTS;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("there") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("isn't") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("an")) return XNOperator.NOT_EXISTS;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("there") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("isn't") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("a")) return XNOperator.NOT_EXISTS;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("there") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("is") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("not") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("an")) return XNOperator.NOT_EXISTS;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("there") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("is") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("not") && lookToken(idx+3).kind == XNToken.ID && lookToken(idx+3).image.equalsIgnoreCase("a")) return XNOperator.NOT_EXISTS;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("there") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("is") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("no")) return XNOperator.NOT_EXISTS;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("there") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("is") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("an")) return XNOperator.EXISTS;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("there") && lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("is") && lookToken(idx+2).kind == XNToken.ID && lookToken(idx+2).image.equalsIgnoreCase("a")) return XNOperator.EXISTS;
		if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("xor")) return XNOperator.XOR;
		return XNOperator.NULL;
	}
	
	public XNOperator getOperator(boolean unary) {
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("^") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase("^") && lookToken(3).kind == XNToken.SYMBOL && lookToken(3).image.equalsIgnoreCase("^")) { consumeTokens(3); return XNOperator.XOR; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("^")) { consumeTokens(1); return XNOperator.EXPONENT; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("-")) { consumeTokens(1); return unary ? XNOperator.UNARY_SUBTRACT : XNOperator.SUBTRACT; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase(",")) { consumeTokens(1); return XNOperator.LIST; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase(":") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase(":")) { consumeTokens(2); return XNOperator.LIST_CONCAT; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase(":")) { consumeTokens(1); return XNOperator.LIST_CONCAT; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("!") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase("=") && lookToken(3).kind == XNToken.SYMBOL && lookToken(3).image.equalsIgnoreCase("=")) { consumeTokens(3); return XNOperator.NOT_STRICT_EQUAL; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("!") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase("=")) { consumeTokens(2); return XNOperator.NOT_EQUAL; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("!")) { consumeTokens(1); return XNOperator.NOT; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("*") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase("*")) { consumeTokens(2); return XNOperator.EXPONENT; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("*")) { consumeTokens(1); return XNOperator.MULTIPLY; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("/")) { consumeTokens(1); return XNOperator.DIVIDE; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("\u00B7")) { consumeTokens(1); return XNOperator.MULTIPLY; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("\u00D7")) { consumeTokens(1); return XNOperator.MULTIPLY; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("\u00F7")) { consumeTokens(1); return XNOperator.DIVIDE; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("\u2212")) { consumeTokens(1); return unary ? XNOperator.UNARY_SUBTRACT : XNOperator.SUBTRACT; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("\u2260") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase("\u2260") && lookToken(3).kind == XNToken.SYMBOL && lookToken(3).image.equalsIgnoreCase("\u2260")) { consumeTokens(3); return XNOperator.NOT_STRICT_EQUAL; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("\u2260") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase("\u2260")) { consumeTokens(2); return XNOperator.NOT_EQUAL; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("\u2260")) { consumeTokens(1); return XNOperator.NOT_EQUAL; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("\u2264")) { consumeTokens(1); return XNOperator.LE_NUM; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("\u2265")) { consumeTokens(1); return XNOperator.GE_NUM; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("&") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase("&") && lookToken(3).kind == XNToken.SYMBOL && lookToken(3).image.equalsIgnoreCase("&")) { consumeTokens(3); return XNOperator.SHORT_AND; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("&") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase("&")) { consumeTokens(2); return XNOperator.STR_CONCAT_SPACE; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("&")) { consumeTokens(1); return XNOperator.STR_CONCAT; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("%")) { consumeTokens(1); return XNOperator.REM; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("+")) { consumeTokens(1); return XNOperator.ADD; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("<") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase("=") && lookToken(3).kind == XNToken.SYMBOL && lookToken(3).image.equalsIgnoreCase(">")) { consumeTokens(3); return XNOperator.CMP_NUM; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("<") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase("<")) { consumeTokens(2); return XNOperator.SHIFT_LEFT; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("<") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase("=")) { consumeTokens(2); return XNOperator.LE_NUM; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("<") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase(">")) { consumeTokens(2); return XNOperator.NOT_EQUAL; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("<")) { consumeTokens(1); return XNOperator.LT_NUM; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("=") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase("=") && lookToken(3).kind == XNToken.SYMBOL && lookToken(3).image.equalsIgnoreCase("=")) { consumeTokens(3); return XNOperator.STRICT_EQUAL; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("=") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase("=")) { consumeTokens(2); return XNOperator.EQUAL; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("=") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase("<")) { consumeTokens(2); return XNOperator.LE_NUM; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("=") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase(">")) { consumeTokens(2); return XNOperator.GE_NUM; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("=")) { consumeTokens(1); return XNOperator.EQUAL; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase(">") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase(">") && lookToken(3).kind == XNToken.SYMBOL && lookToken(3).image.equalsIgnoreCase(">")) { consumeTokens(3); return XNOperator.SHIFT_RIGHT_UNSIGNED; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase(">") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase(">")) { consumeTokens(2); return XNOperator.SHIFT_RIGHT_SIGNED; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase(">") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase("=")) { consumeTokens(2); return XNOperator.GE_NUM; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase(">")) { consumeTokens(1); return XNOperator.GT_NUM; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("|") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase("|") && lookToken(3).kind == XNToken.SYMBOL && lookToken(3).image.equalsIgnoreCase("|")) { consumeTokens(3); return XNOperator.SHORT_OR; }
		if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("|") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase("|")) { consumeTokens(2); return XNOperator.STR_CONCAT; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("and")) { consumeTokens(1); return XNOperator.AND; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("are") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("precisely")) { consumeTokens(3); return XNOperator.IS_NOT_PRECISELY_A; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("are") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not")) { consumeTokens(2); return XNOperator.IS_NOT_A; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("are") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("precisely")) { consumeTokens(2); return XNOperator.IS_PRECISELY_A; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("are")) { consumeTokens(1); return XNOperator.IS_A; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("aren't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("precisely")) { consumeTokens(2); return XNOperator.IS_NOT_PRECISELY_A; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("aren't")) { consumeTokens(1); return XNOperator.IS_NOT_A; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("as") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("an")) { consumeTokens(2); return XNOperator.AS; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("as") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("a")) { consumeTokens(2); return XNOperator.AS; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("as")) { consumeTokens(1); return XNOperator.AS; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("begins") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("with")) { consumeTokens(2); return XNOperator.STARTS_WITH; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("between")) { consumeTokens(1); return XNOperator.BETWEEN; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("bitand")) { consumeTokens(1); return XNOperator.BIT_AND; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("bitnot")) { consumeTokens(1); return XNOperator.BIT_NOT; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("bitor")) { consumeTokens(1); return XNOperator.BIT_OR; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("bitxor")) { consumeTokens(1); return XNOperator.BIT_XOR; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("cmp")) { consumeTokens(1); return XNOperator.CMP_STR; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("comes") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("before")) { consumeTokens(2); return XNOperator.LT_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("comes") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("after")) { consumeTokens(2); return XNOperator.GT_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("compared") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("to")) { consumeTokens(2); return XNOperator.CMP_STR; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("contains")) { consumeTokens(1); return XNOperator.CONTAINS; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("div")) { consumeTokens(1); return XNOperator.DIV; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("does") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("contain")) { consumeTokens(3); return XNOperator.NOT_CONTAINS; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("does") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("begin") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("with")) { consumeTokens(4); return XNOperator.NOT_STARTS_WITH; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("does") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("start") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("with")) { consumeTokens(4); return XNOperator.NOT_STARTS_WITH; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("does") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("end") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("with")) { consumeTokens(4); return XNOperator.NOT_ENDS_WITH; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("does") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("equal")) { consumeTokens(3); return XNOperator.NOT_EQUAL; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("doesn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("contain")) { consumeTokens(2); return XNOperator.NOT_CONTAINS; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("doesn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("begin") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("with")) { consumeTokens(3); return XNOperator.NOT_STARTS_WITH; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("doesn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("start") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("with")) { consumeTokens(3); return XNOperator.NOT_STARTS_WITH; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("doesn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("end") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("with")) { consumeTokens(3); return XNOperator.NOT_ENDS_WITH; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("doesn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("equal")) { consumeTokens(2); return XNOperator.NOT_EQUAL; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("ends") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("with")) { consumeTokens(2); return XNOperator.ENDS_WITH; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("eq")) { consumeTokens(1); return XNOperator.STRING_EQUAL; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("equals")) { consumeTokens(1); return XNOperator.EQUAL; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("ge")) { consumeTokens(1); return XNOperator.GE_STR; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("gt")) { consumeTokens(1); return XNOperator.GT_STR; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("instanceof")) { consumeTokens(1); return XNOperator.IS_A; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("equal") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("to")) { consumeTokens(4); return XNOperator.NOT_EQUAL; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("exactly")) { consumeTokens(3); return XNOperator.NOT_STRICT_EQUAL; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("precisely") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("an") && lookToken(5).kind == XNToken.ID && lookToken(5).image.equalsIgnoreCase("element") && lookToken(6).kind == XNToken.ID && lookToken(6).image.equalsIgnoreCase("of")) { consumeTokens(6); return XNOperator.NOT_PRECISELY_ELEMENT_OF; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("precisely") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("an") && lookToken(5).kind == XNToken.ID && lookToken(5).image.equalsIgnoreCase("element") && lookToken(6).kind == XNToken.ID && lookToken(6).image.equalsIgnoreCase("in")) { consumeTokens(6); return XNOperator.NOT_PRECISELY_ELEMENT_OF; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("precisely") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("an")) { consumeTokens(4); return XNOperator.IS_NOT_PRECISELY_A; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("precisely") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("a")) { consumeTokens(4); return XNOperator.IS_NOT_PRECISELY_A; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("an") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("element") && lookToken(5).kind == XNToken.ID && lookToken(5).image.equalsIgnoreCase("of")) { consumeTokens(5); return XNOperator.NOT_ELEMENT_OF; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("an") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("element") && lookToken(5).kind == XNToken.ID && lookToken(5).image.equalsIgnoreCase("in")) { consumeTokens(5); return XNOperator.NOT_ELEMENT_OF; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("an")) { consumeTokens(3); return XNOperator.IS_NOT_A; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("a")) { consumeTokens(3); return XNOperator.IS_NOT_A; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("less") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("than")) { consumeTokens(4); return XNOperator.GE_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("smaller") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("than")) { consumeTokens(4); return XNOperator.GE_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("fewer") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("than")) { consumeTokens(4); return XNOperator.GE_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("greater") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("than")) { consumeTokens(4); return XNOperator.LE_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("bigger") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("than")) { consumeTokens(4); return XNOperator.LE_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("more") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("than")) { consumeTokens(4); return XNOperator.LE_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("before")) { consumeTokens(3); return XNOperator.GE_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("after")) { consumeTokens(3); return XNOperator.LE_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("in")) { consumeTokens(3); return XNOperator.NOT_IN; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("of")) { consumeTokens(3); return XNOperator.NOT_IN; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("within")) { consumeTokens(3); return XNOperator.NOT_WITHIN; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("between")) { consumeTokens(3); return XNOperator.NOT_BETWEEN; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("not")) { consumeTokens(2); return XNOperator.NOT_EQUAL; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("equal") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("to")) { consumeTokens(3); return XNOperator.EQUAL; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("exactly")) { consumeTokens(2); return XNOperator.STRICT_EQUAL; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("precisely") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("an") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("element") && lookToken(5).kind == XNToken.ID && lookToken(5).image.equalsIgnoreCase("of")) { consumeTokens(5); return XNOperator.PRECISELY_ELEMENT_OF; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("precisely") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("an") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("element") && lookToken(5).kind == XNToken.ID && lookToken(5).image.equalsIgnoreCase("in")) { consumeTokens(5); return XNOperator.PRECISELY_ELEMENT_OF; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("precisely") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("an")) { consumeTokens(3); return XNOperator.IS_PRECISELY_A; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("precisely") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("a")) { consumeTokens(3); return XNOperator.IS_PRECISELY_A; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("an") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("element") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("of")) { consumeTokens(4); return XNOperator.ELEMENT_OF; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("an") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("element") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("in")) { consumeTokens(4); return XNOperator.ELEMENT_OF; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("an")) { consumeTokens(2); return XNOperator.IS_A; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("a")) { consumeTokens(2); return XNOperator.IS_A; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("less") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("than")) { consumeTokens(3); return XNOperator.LT_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("smaller") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("than")) { consumeTokens(3); return XNOperator.LT_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("fewer") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("than")) { consumeTokens(3); return XNOperator.LT_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("greater") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("than")) { consumeTokens(3); return XNOperator.GT_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("bigger") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("than")) { consumeTokens(3); return XNOperator.GT_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("more") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("than")) { consumeTokens(3); return XNOperator.GT_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("before")) { consumeTokens(2); return XNOperator.LT_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("after")) { consumeTokens(2); return XNOperator.GT_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("in")) { consumeTokens(2); return XNOperator.IN; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("of")) { consumeTokens(2); return XNOperator.IN; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("within")) { consumeTokens(2); return XNOperator.WITHIN; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("between")) { consumeTokens(2); return XNOperator.BETWEEN; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is")) { consumeTokens(1); return XNOperator.EQUAL; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isa")) { consumeTokens(1); return XNOperator.IS_A; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("equal") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("to")) { consumeTokens(3); return XNOperator.NOT_EQUAL; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("exactly")) { consumeTokens(2); return XNOperator.NOT_STRICT_EQUAL; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("precisely") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("an") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("element") && lookToken(5).kind == XNToken.ID && lookToken(5).image.equalsIgnoreCase("of")) { consumeTokens(5); return XNOperator.NOT_PRECISELY_ELEMENT_OF; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("precisely") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("an") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("element") && lookToken(5).kind == XNToken.ID && lookToken(5).image.equalsIgnoreCase("in")) { consumeTokens(5); return XNOperator.NOT_PRECISELY_ELEMENT_OF; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("precisely") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("an")) { consumeTokens(3); return XNOperator.IS_NOT_PRECISELY_A; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("precisely") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("a")) { consumeTokens(3); return XNOperator.IS_NOT_PRECISELY_A; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("an") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("element") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("of")) { consumeTokens(4); return XNOperator.NOT_ELEMENT_OF; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("an") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("element") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("in")) { consumeTokens(4); return XNOperator.NOT_ELEMENT_OF; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("an")) { consumeTokens(2); return XNOperator.IS_NOT_A; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("a")) { consumeTokens(2); return XNOperator.IS_NOT_A; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("less") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("than")) { consumeTokens(3); return XNOperator.GE_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("smaller") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("than")) { consumeTokens(3); return XNOperator.GE_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("fewer") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("than")) { consumeTokens(3); return XNOperator.GE_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("greater") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("than")) { consumeTokens(3); return XNOperator.LE_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("bigger") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("than")) { consumeTokens(3); return XNOperator.LE_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("more") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("than")) { consumeTokens(3); return XNOperator.LE_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("before")) { consumeTokens(2); return XNOperator.GE_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("after")) { consumeTokens(2); return XNOperator.LE_NUM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("in")) { consumeTokens(2); return XNOperator.NOT_IN; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("of")) { consumeTokens(2); return XNOperator.NOT_IN; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("within")) { consumeTokens(2); return XNOperator.NOT_WITHIN; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("between")) { consumeTokens(2); return XNOperator.NOT_BETWEEN; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("isn't")) { consumeTokens(1); return XNOperator.NOT_EQUAL; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("le")) { consumeTokens(1); return XNOperator.LE_STR; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("lt")) { consumeTokens(1); return XNOperator.LT_STR; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("mod")) { consumeTokens(1); return XNOperator.MOD; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("ne")) { consumeTokens(1); return XNOperator.NOT_STRING_EQUAL; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("not") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("between")) { consumeTokens(2); return XNOperator.NOT_BETWEEN; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("not")) { consumeTokens(1); return XNOperator.NOT; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("or")) { consumeTokens(1); return XNOperator.OR; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("quot")) { consumeTokens(1); return XNOperator.QUOT; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("rem")) { consumeTokens(1); return XNOperator.REM; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("starts") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("with")) { consumeTokens(2); return XNOperator.STARTS_WITH; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("there's") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("no")) { consumeTokens(2); return XNOperator.NOT_EXISTS; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("there's") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("an")) { consumeTokens(2); return XNOperator.EXISTS; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("there's") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("a")) { consumeTokens(2); return XNOperator.EXISTS; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("there") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("isn't") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("an")) { consumeTokens(3); return XNOperator.NOT_EXISTS; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("there") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("isn't") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("a")) { consumeTokens(3); return XNOperator.NOT_EXISTS; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("there") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("is") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("not") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("an")) { consumeTokens(4); return XNOperator.NOT_EXISTS; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("there") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("is") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("not") && lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("a")) { consumeTokens(4); return XNOperator.NOT_EXISTS; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("there") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("is") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("no")) { consumeTokens(3); return XNOperator.NOT_EXISTS; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("there") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("is") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("an")) { consumeTokens(3); return XNOperator.EXISTS; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("there") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("is") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("a")) { consumeTokens(3); return XNOperator.EXISTS; }
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("xor")) { consumeTokens(1); return XNOperator.XOR; }
		throw new XNParseError("operator", lookToken(1));
	}
	
	private SortedSet<XNDataType> getKnownDataTypes() {
		TreeSet<XNDataType> knownDataTypes = new TreeSet<XNDataType>();
		for (Map.Entry<String, XOMDataType<? extends XOMVariant>> e : context.getBuiltInDataTypeEntrySet()) {
			knownDataTypes.add(new XNDataType(e.getKey().split(" +"), e.getValue().getDescribability()));
		}
		for (Map.Entry<String, XOMDataType<? extends XOMVariant>> e : context.getUserDataTypeEntrySet()) {
			knownDataTypes.add(new XNDataType(e.getKey().split(" +"), e.getValue().getDescribability()));
		}
		for (Map.Entry<String, Integer> e : knownAdditionalDataTypes.entrySet()) {
			knownDataTypes.add(new XNDataType(e.getKey().split(" +"), e.getValue()));
		}
		return knownDataTypes;
	}
	
	public boolean lookDataType(int idx, XNDataType dt) {
		for (int i = 0; i < dt.name.length; i++) {
			if (!( lookToken(idx+i).kind == XNToken.ID && lookToken(idx+i).image.equalsIgnoreCase(dt.name[i]) )) {
				return false;
			}
		}
		return true;
	}
	
	public XNDataType lookDataType(int idx) {
		for (XNDataType dt : getKnownDataTypes()) {
			if (lookDataType(idx, dt)) return dt;
		}
		return null;
	}
	
	public XNDataType lookDataType(int idx, int mask) {
		for (XNDataType dt : getKnownDataTypes()) {
			if (((dt.describability & mask) != 0) && lookDataType(idx, dt)) return dt;
		}
		return null;
	}
	
	public XNDataType getDataType() {
		for (XNDataType dt : getKnownDataTypes()) {
			if (lookDataType(1, dt)) {
				consumeTokens(dt.name.length);
				return dt;
			}
		}
		throw new XNParseError("data type", lookToken(1));
	}
	
	public XNDataType getDataType(int mask) {
		for (XNDataType dt : getKnownDataTypes()) {
			if (((dt.describability & mask) != 0) && lookDataType(1, dt)) {
				consumeTokens(dt.name.length);
				return dt;
			}
		}
		throw new XNParseError("data type", lookToken(1));
	}
	
	public boolean isNotKeyword(int idx, Collection<String> keywords) {
		return (keywords == null || !(keywords.contains(lookToken(idx).image.toLowerCase())));
	}
	
	private XNExpression getExpExp(Collection<String> keywords) {
		XNExpression l = getFactor(keywords);
		if (lookOperator(1, false).precedence() == XNOperatorPrecedence.EXPONENT && isNotKeyword(1, keywords)) {
			XNOperator o = getOperator(false);
			XNExpression r = getExpExp(keywords);
			return new XNBinaryExpression(l,o,r);
		} else {
			return l;
		}
	}
	
	private XNExpression getExpMul(Collection<String> keywords) {
		XNExpression l = getExpExp(keywords);
		while (lookOperator(1, false).precedence() == XNOperatorPrecedence.MULTIPLY && isNotKeyword(1, keywords)) {
			XNOperator o = getOperator(false);
			XNExpression r = getExpExp(keywords);
			l = new XNBinaryExpression(l,o,r);
		}
		return l;
	}
	
	private XNExpression getExpAdd(Collection<String> keywords) {
		XNExpression l = getExpMul(keywords);
		while (lookOperator(1, false).precedence() == XNOperatorPrecedence.ADD && isNotKeyword(1, keywords)) {
			XNOperator o = getOperator(false);
			XNExpression r = getExpMul(keywords);
			l = new XNBinaryExpression(l,o,r);
		}
		return l;
	}
	
	private XNExpression getExpShift(Collection<String> keywords) {
		XNExpression l = getExpAdd(keywords);
		while (lookOperator(1, false).precedence() == XNOperatorPrecedence.SHIFT && isNotKeyword(1, keywords)) {
			XNOperator o = getOperator(false);
			XNExpression r = getExpAdd(keywords);
			l = new XNBinaryExpression(l,o,r);
		}
		return l;
	}
	
	private XNExpression getExpBitAnd(Collection<String> keywords) {
		XNExpression l = getExpShift(keywords);
		while (lookOperator(1, false).precedence() == XNOperatorPrecedence.BIT_AND && isNotKeyword(1, keywords)) {
			XNOperator o = getOperator(false);
			XNExpression r = getExpShift(keywords);
			l = new XNBinaryExpression(l,o,r);
		}
		return l;
	}
	
	private XNExpression getExpBitXor(Collection<String> keywords) {
		XNExpression l = getExpBitAnd(keywords);
		while (lookOperator(1, false).precedence() == XNOperatorPrecedence.BIT_XOR && isNotKeyword(1, keywords)) {
			XNOperator o = getOperator(false);
			XNExpression r = getExpBitAnd(keywords);
			l = new XNBinaryExpression(l,o,r);
		}
		return l;
	}
	
	private XNExpression getExpBitOr(Collection<String> keywords) {
		XNExpression l = getExpBitXor(keywords);
		while (lookOperator(1, false).precedence() == XNOperatorPrecedence.BIT_OR && isNotKeyword(1, keywords)) {
			XNOperator o = getOperator(false);
			XNExpression r = getExpBitXor(keywords);
			l = new XNBinaryExpression(l,o,r);
		}
		return l;
	}
	
	private XNExpression getExpStrConcat(Collection<String> keywords) {
		XNExpression l = getExpBitOr(keywords);
		while (lookOperator(1, false).precedence() == XNOperatorPrecedence.STR_CONCAT && isNotKeyword(1, keywords)) {
			XNOperator o = getOperator(false);
			XNExpression r = getExpBitOr(keywords);
			l = new XNBinaryExpression(l,o,r);
		}
		return l;
	}
	
	private XNExpression getExpListConcat(Collection<String> keywords) {
		XNExpression l = getExpStrConcat(keywords);
		while (lookOperator(1, false).precedence() == XNOperatorPrecedence.LIST_CONCAT && isNotKeyword(1, keywords)) {
			XNOperator o = getOperator(false);
			XNExpression r = getExpStrConcat(keywords);
			l = new XNBinaryExpression(l,o,r);
		}
		return l;
	}
	
	private XNToken[] getBetweenOption() {
		if (lookToken(1).kind == XNToken.ID) {
			if (lookToken(1).image.equalsIgnoreCase("inclusive")) return new XNToken[]{getToken()};
			if (lookToken(1).image.equalsIgnoreCase("leftinclusive")) return new XNToken[]{getToken()};
			if (lookToken(1).image.equalsIgnoreCase("rightinclusive")) return new XNToken[]{getToken()};
			if (lookToken(1).image.equalsIgnoreCase("exclusive")) return new XNToken[]{getToken()};
			if (lookToken(1).image.equalsIgnoreCase("left") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase("-") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("inclusive")) {
				XNToken a = getToken(); XNToken b = getToken(); XNToken c = getToken(); return new XNToken[]{a,b,c};
			}
			if (lookToken(1).image.equalsIgnoreCase("left") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("inclusive")) {
				XNToken a = getToken(); XNToken b = getToken(); return new XNToken[]{a,b};
			}
			if (lookToken(1).image.equalsIgnoreCase("right") && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase("-") && lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("inclusive")) {
				XNToken a = getToken(); XNToken b = getToken(); XNToken c = getToken(); return new XNToken[]{a,b,c};
			}
			if (lookToken(1).image.equalsIgnoreCase("right") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("inclusive")) {
				XNToken a = getToken(); XNToken b = getToken(); return new XNToken[]{a,b};
			}
		}
		return null;
	}
	
	private XNExpression getExpRel(Collection<String> keywords) {
		XNExpression l = getExpListConcat(keywords);
		while (lookOperator(1, false).precedence() == XNOperatorPrecedence.RELATION && isNotKeyword(1, keywords)) {
			XNOperator o = getOperator(false);
			if (o == XNOperator.BETWEEN || o == XNOperator.NOT_BETWEEN) {
				XNExpression r1;
				XNToken a;
				XNExpression r2;
				XNToken[] opt;
				r1 = getExpListConcat(keywords);
				if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("and")) {
					a = getToken();
				} else {
					throw new XNParseError("and", lookToken(1));
				}
				r2 = getExpListConcat(keywords);
				opt = getBetweenOption();
				l = new XNBetweenExpression(l,o,r1,a,r2,opt);
			} else {
				XNExpression r = getExpListConcat(keywords);
				l = new XNBinaryExpression(l,o,r);
			}
		}
		return l;
	}
	
	private XNExpression getExpIsA(Collection<String> keywords) {
		XNExpression a = getExpRel(keywords);
		while (lookOperator(1, false).precedence() == XNOperatorPrecedence.IS_A && isNotKeyword(1, keywords)) {
			XNOperator o = getOperator(false);
			if (lookDataType(1) != null) {
				XNToken[] dttk = new XNToken[lookDataType(1).name.length];
				for (int i = 0; i < dttk.length; i++) {
					dttk[i] = lookToken(i+1);
				}
				XNDataType dt = getDataType();
				a = new XNInstanceOfExpression(a,o,dt, dttk);
			} else {
				throw new XNParseError("data type", lookToken(1));
			}
		}
		return a;
	}
	
	private XNExpression getExpEqual(Collection<String> keywords) {
		XNExpression l = getExpIsA(keywords);
		while (lookOperator(1, false).precedence() == XNOperatorPrecedence.EQUAL && isNotKeyword(1, keywords)) {
			XNOperator o = getOperator(false);
			XNExpression r = getExpIsA(keywords);
			l = new XNBinaryExpression(l,o,r);
		}
		return l;
	}
	
	private XNExpression getExpAnd(Collection<String> keywords) {
		XNExpression l = getExpEqual(keywords);
		while (lookOperator(1, false).precedence() == XNOperatorPrecedence.AND && isNotKeyword(1, keywords)) {
			XNOperator o = getOperator(false);
			XNExpression r = getExpEqual(keywords);
			l = new XNBinaryExpression(l,o,r);
		}
		return l;
	}
	
	private XNExpression getExpXor(Collection<String> keywords) {
		XNExpression l = getExpAnd(keywords);
		while (lookOperator(1, false).precedence() == XNOperatorPrecedence.XOR && isNotKeyword(1, keywords)) {
			XNOperator o = getOperator(false);
			XNExpression r = getExpAnd(keywords);
			l = new XNBinaryExpression(l,o,r);
		}
		return l;
	}
	
	public boolean lookSingleExpression(int idx, Collection<String> keywords) {
		return lookFactor(idx, keywords);
	}
	
	public XNExpression getSingleExpression(Collection<String> keywords) {
		XNExpression l = getExpXor(keywords);
		while (lookOperator(1, false).precedence() == XNOperatorPrecedence.OR && isNotKeyword(1, keywords)) {
			XNOperator o = getOperator(false);
			XNExpression r = getExpXor(keywords);
			l = new XNBinaryExpression(l,o,r);
		}
		return l;
	}
	
	public boolean lookListExpression(int idx, Collection<String> keywords) {
		if (lookOperator(idx, false).precedence() == XNOperatorPrecedence.LIST && isNotKeyword(idx, keywords)) return true;
		else return lookSingleExpression(idx, keywords);
	}
	
	public XNExpression getListExpression(Collection<String> keywords) {
		XNExpression first;
		if (lookOperator(1, false).precedence() == XNOperatorPrecedence.LIST && isNotKeyword(1, keywords)) {
			first = new XNEmptyExpression(lookToken(1).source, lookToken(1).beginLine, lookToken(1).beginColumn);
		} else {
			first = getSingleExpression(keywords);
		}
		if (lookOperator(1, false).precedence() == XNOperatorPrecedence.LIST && isNotKeyword(1, keywords)) {
			XNListExpression list = new XNListExpression(first);
			while (lookOperator(1, false).precedence() == XNOperatorPrecedence.LIST && isNotKeyword(1, keywords)) {
				getOperator(false);
				XNExpression next;
				if (lookSingleExpression(1, keywords)) {
					next = getSingleExpression(keywords);
				} else {
					next = new XNEmptyExpression(lookToken(1).source, lookToken(1).beginLine, lookToken(1).beginColumn);
				}
				list.exprs.add(next);
			}
			return list;
		} else {
			return first;
		}
	}
	
	public XNToken lookConstant(int idx) {
		if ((lookToken(idx).kind == XNToken.ID || lookToken(idx).kind == XNToken.SYMBOL)
				&& (context.hasConstant(lookToken(idx).image.toLowerCase()) || knownAdditionalConstants.contains(lookToken(idx).image.toLowerCase()))) {
			return lookToken(idx);
		}
		return null;
	}
	
	public XNToken getConstant() {
		if ((lookToken(1).kind == XNToken.ID || lookToken(1).kind == XNToken.SYMBOL)
				&& (context.hasConstant(lookToken(1).image.toLowerCase()) || knownAdditionalConstants.contains(lookToken(1).image.toLowerCase()))) {
			return getToken();
		}
		throw new XNParseError("constant", lookToken(1));
	}
	
	public XNToken lookOrdinal(int idx) {
		if ((lookToken(idx).kind == XNToken.ID || lookToken(idx).kind == XNToken.SYMBOL)
				&& (context.hasOrdinal(lookToken(idx).image.toLowerCase()) || knownAdditionalOrdinals.contains(lookToken(idx).image.toLowerCase()))) {
			return lookToken(idx);
		}
		return null;
	}
	
	public XNToken getOrdinal() {
		if ((lookToken(1).kind == XNToken.ID || lookToken(1).kind == XNToken.SYMBOL)
				&& (context.hasOrdinal(lookToken(1).image.toLowerCase()) || knownAdditionalOrdinals.contains(lookToken(1).image.toLowerCase()))) {
			return getToken();
		}
		throw new XNParseError("ordinal", lookToken(1));
	}
	
	public XNPreposition lookPreposition(int idx) {
		if (lookToken(idx).kind == XNToken.ID) {
			return XNPreposition.forName(lookToken(idx).image);
		} else {
			return null;
		}
	}
	
	public XNPreposition getPreposition() {
		if (lookToken(1).kind == XNToken.ID) {
			XNPreposition prep = XNPreposition.forName(lookToken(1).image);
			if (prep != null) {
				getToken();
				return prep;
			} else {
				throw new XNParseError("into, before, or after", lookToken(1));
			}
		} else {
			throw new XNParseError("into, before, or after", lookToken(1));
		}
	}
	
	public XNModifier lookModifier(int idx) {
		if (lookToken(idx).kind == XNToken.ID) {
			return XNModifier.forName(lookToken(idx).image);
		} else {
			return null;
		}
	}
	
	public XNModifier getModifier() {
		if (lookToken(1).kind == XNToken.ID) {
			XNModifier mod = XNModifier.forName(lookToken(1).image);
			if (mod != null) {
				getToken();
				return mod;
			} else {
				throw new XNParseError("short, abbreviated, or long", lookToken(1));
			}
		} else {
			throw new XNParseError("short, abbreviated, or long", lookToken(1));
		}
	}
	
	public boolean lookRangeSpecifier(int idx) {
		return (
				lookToken(idx).kind == XNToken.ID &&
				(
						lookToken(idx).image.equalsIgnoreCase("to") ||
						lookToken(idx).image.equalsIgnoreCase("thru") ||
						lookToken(idx).image.equalsIgnoreCase("through")
				)
		);
	}
	
	public XNToken getRangeSpecifier() {
		if (lookRangeSpecifier(1)) {
			return getToken();
		} else {
			throw new XNParseError("through", lookToken(1));
		}
	}
	
	public boolean lookOfIn(int idx) {
		return (
				lookToken(idx).kind == XNToken.ID &&
				(
						lookToken(idx).image.equalsIgnoreCase("of") ||
						lookToken(idx).image.equalsIgnoreCase("in")
				)
		);
	}
	
	public XNToken getOfIn(Collection<String> keywords) {
		if (lookOfIn(1)) {
			return getToken();
		} else {
			throw new XNParseError("of or in", lookToken(1));
		}
	}
	
	private static final String INSIDE_INDEXNAMEID_DESCRIPTOR_TAG = " insideINID!!";
	public static final String ALLOW_BARE_SM_DESCRIPTORS_TAG = " argumentsTo()!!";
	
	private boolean isNotInsideIndexNameIdDescriptor(Collection<String> keywords) {
		return (keywords == null || !(keywords.contains(INSIDE_INDEXNAMEID_DESCRIPTOR_TAG)));
	}
	private boolean allowingBareSingleMassDescriptors(Collection<String> keywords) {
		return (keywords != null && keywords.contains(ALLOW_BARE_SM_DESCRIPTORS_TAG));
	}
	
	public boolean lookNewExpression(int idx) {
		return (
				(
						lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("a") &&
						lookToken(idx+1).kind == XNToken.ID && lookToken(idx+1).image.equalsIgnoreCase("new") &&
						lookDataType(idx+2) != null
				) || (
						lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("new") &&
						lookDataType(idx+1) != null
				)
		);
	}
	
	public XNExpression getNewExpression(Collection<String> keywords) {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("a")) {
			getToken();
		}
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("new")) {
			getToken();
		} else {
			throw new XNParseError("new", lookToken(1));
		}
		XNDataType dt = lookDataType(1);
		if (dt == null) {
			throw new XNParseError("data type", lookToken(1));
		}
		XNToken[] dtTokens = new XNToken[dt.name.length];
		for (int i = 0; i < dtTokens.length; i++) {
			dtTokens[i] = getToken();
		}
		if (lookOfIn(1) && isNotInsideIndexNameIdDescriptor(keywords) && lookFactor(2, keywords)) {
			XNToken of = getToken();
			XNExpression pa = getFactor(keywords);
			return new XNNewExpression(dt, dtTokens, of, pa);
		} else {
			return new XNNewExpression(dt, dtTokens);
		}
	}
	
	public boolean lookFactor(int idx, Collection<String> keywords) {
		if (!isNotKeyword(idx, keywords)) return false;
		else if (lookOperator(idx, true).precedence() == XNOperatorPrecedence.UNARY) return true;
		else if (lookNewExpression(idx)) return true;
		else if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("if")) return true;
		else if (lookToken(idx).kind == XNToken.SYMBOL && lookToken(idx).image.equalsIgnoreCase("(")) return true;
		else if (lookToken(idx).kind == XNToken.QUOTED) return true;
		else if (lookToken(idx).kind == XNToken.NUMBER) return true;
		else if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("me")) return true;
		else if (lookToken(idx).kind == XNToken.ID && lookToken(idx).image.equalsIgnoreCase("super")) return true;
		else if (lookConstant(idx) != null) return true;
		else if (lookToken(idx).kind == XNToken.ID) return true;
		else return false;
	}
	
	public XNExpression getFactor(Collection<String> keywords) {
		if (!isNotKeyword(1, keywords)) throw new XNParseError("value", lookToken(1));
		// TRIVIAL CASES
		// unary operator, e.g. not true
		else if (lookOperator(1, true).precedence() == XNOperatorPrecedence.UNARY) {
			XNToken ot = lookToken(1);
			XNOperator o = getOperator(true);
			XNExpression e = getFactor(keywords);
			return new XNUnaryExpression(ot,o,e);
		}
		// new expression, e.g. a new fraction
		else if (lookNewExpression(1)) {
			return getNewExpression(keywords);
		}
		// conditional expression, e.g. if true then 3 else 5
		else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("if")) {
			Collection<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add("then"); myKeywords.add("else");
			XNToken it = getToken();
			XNExpression c = getListExpression(myKeywords);
			if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("then")) {
				XNToken tt = getToken();
				XNExpression tc = getListExpression(myKeywords);
				if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("else")) {
					XNToken et = getToken();
					XNExpression ec = getListExpression(keywords);
					return new XNIfExpression(it, c, tt, tc, et, ec);
				} else {
					throw new XNParseError("else", lookToken(1));
				}
			} else {
				throw new XNParseError("then", lookToken(1));
			}
		}
		// expression in parentheses, e.g. (2+2)
		else if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("(")) {
			consumeTokens(1);
			if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase(")")) {
				consumeTokens(1);
				return new XNEmptyExpression(lookToken(0).source, lookToken(0).beginLine, lookToken(0).beginColumn);
			} else {
				Collection<String> myKeywords;
				if (allowingBareSingleMassDescriptors(keywords)) {
					myKeywords = new HashSet<String>();
					myKeywords.add(ALLOW_BARE_SM_DESCRIPTORS_TAG);
				} else {
					myKeywords = null;
				}
				XNExpression e = getListExpression(myKeywords);
				if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase(")")) {
					consumeTokens(1);
					return e;
				} else {
					throw new XNParseError(")", lookToken(1));
				}
			}
		}
		// quoted literal, e.g. "Hello"
		else if (lookToken(1).kind == XNToken.QUOTED) return new XNStringExpression(getToken());
		// numeric literal, e.g. 3.14159
		else if (lookToken(1).kind == XNToken.NUMBER) return new XNNumberExpression(getToken());
		// me
		else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("me")) return new XNMeExpression(getToken());
		// super
		else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("super")) return new XNSuperExpression(getToken());
		// line number
		else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("__LINE__")) {
			XNToken tk = getToken();
			tk.kind = XNToken.NUMBER;
			tk.image = Integer.toString(tk.beginLine);
			return new XNNumberExpression(tk);
		}
		// file name
		else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("__FILE__")) {
			XNToken tk = getToken();
			if (tk.source instanceof File) {
				tk.kind = XNToken.QUOTED;
				tk.image = XIONUtil.quote(((File)tk.source).getAbsolutePath());
				return new XNStringExpression(tk);
			} else {
				tk.kind = XNToken.ID;
				tk.image = "empty";
				return new XNConstantExpression(tk);
			}
		}
		// constant, e.g. pi
		else if (lookConstant(1) != null) return new XNConstantExpression(getConstant());
		// THE "ALL" PART OF THE SYNTAX TREE
		else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("all")) {
			// mass descriptor with "all the", e.g. all the cards
			if (lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("the") && lookDataType(3, XOMDataType.DESCRIBABLE_BY_MASS) != null) {
				XNVariantMassDescriptor vmd = new XNVariantMassDescriptor();
				vmd.allToken = getToken();
				vmd.theToken = getToken();
				vmd.dtTokens = new XNToken[lookDataType(1, XOMDataType.DESCRIBABLE_BY_MASS).name.length];
				for (int i = 0; i < vmd.dtTokens.length; i++) {
					vmd.dtTokens[i] = lookToken(i+1);
				}
				vmd.datatype = getDataType(XOMDataType.DESCRIBABLE_BY_MASS);
				if (lookOfIn(1) && lookFactor(2, keywords)) {
					vmd.ofInToken = getToken();
					vmd.parentVariant = getFactor(keywords);
				} else {
					vmd.ofInToken = null;
					vmd.parentVariant = null;
				}
				return vmd;
			// mass descriptor with "all", e.g. all cards
			} else if (lookDataType(2, XOMDataType.DESCRIBABLE_BY_MASS) != null) {
				XNVariantMassDescriptor vmd = new XNVariantMassDescriptor();
				vmd.allToken = getToken();
				vmd.theToken = null;
				vmd.dtTokens = new XNToken[lookDataType(1, XOMDataType.DESCRIBABLE_BY_MASS).name.length];
				for (int i = 0; i < vmd.dtTokens.length; i++) {
					vmd.dtTokens[i] = lookToken(i+1);
				}
				vmd.datatype = getDataType(XOMDataType.DESCRIBABLE_BY_MASS);
				if (lookOfIn(1) && lookFactor(2, keywords)) {
					vmd.ofInToken = getToken();
					vmd.parentVariant = getFactor(keywords);
				} else {
					vmd.ofInToken = null;
					vmd.parentVariant = null;
				}
				return vmd;
			// variable or unquoted literal, i.e. all
			} else {
				return new XNVariableExpression(getToken());
			}
		}
		// THE "THE" PART OF THE SYNTAX TREE
		else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("the")) {
			// function call with "the <modifier>", e.g. the short date [of empty]
			if (lookModifier(2) != null && lookToken(3).kind == XNToken.ID) {
				XNFunctionCallPropertyDescriptor fcpd = new XNFunctionCallPropertyDescriptor();
				fcpd.theToken = getToken();
				fcpd.modifierToken = lookToken(1);
				fcpd.modifier = getModifier();
				fcpd.idToken = lookToken(1);
				fcpd.identifier = getToken().image;
				Collection<String> myKeywords = new HashSet<String>();
				if (keywords != null) myKeywords.addAll(keywords);
				myKeywords.add(ALLOW_BARE_SM_DESCRIPTORS_TAG);
				if (lookOfIn(1) && lookFactor(2, myKeywords)) {
					fcpd.ofInToken = getToken();
					fcpd.argument = getFactor(myKeywords);
				} else {
					fcpd.ofInToken = null;
					fcpd.argument = null;
				}
				return fcpd;
			// ordinal range descriptor with "the", e.g. the first through fifth cards
			} else if (lookOrdinal(2) != null && lookRangeSpecifier(3) && lookOrdinal(4) != null && lookDataType(5, XOMDataType.DESCRIBABLE_BY_ORDINAL_RANGE) != null) {
				XNVariantOrdinalDescriptor vod = new XNVariantOrdinalDescriptor();
				vod.theToken = getToken();
				vod.startOrdinal = getOrdinal();
				vod.toToken = getRangeSpecifier();
				vod.endOrdinal = getOrdinal();
				vod.dtTokens = new XNToken[lookDataType(1, XOMDataType.DESCRIBABLE_BY_ORDINAL_RANGE).name.length];
				for (int i = 0; i < vod.dtTokens.length; i++) {
					vod.dtTokens[i] = lookToken(i+1);
				}
				vod.datatype = getDataType(XOMDataType.DESCRIBABLE_BY_ORDINAL_RANGE);
				if (lookOfIn(1) && lookFactor(2, keywords)) {
					vod.ofInToken = getToken();
					vod.parentVariant = getFactor(keywords);
				} else {
					vod.ofInToken = null;
					vod.parentVariant = null;
				}
				return vod;
			// ordinal descriptor with "the", e.g. the first card
			} else if (lookOrdinal(2) != null && lookDataType(3, XOMDataType.DESCRIBABLE_BY_ORDINAL) != null) {
				XNVariantOrdinalDescriptor vod = new XNVariantOrdinalDescriptor();
				vod.theToken = getToken();
				vod.startOrdinal = getOrdinal();
				vod.toToken = null;
				vod.endOrdinal = null;
				vod.dtTokens = new XNToken[lookDataType(1, XOMDataType.DESCRIBABLE_BY_ORDINAL).name.length];
				for (int i = 0; i < vod.dtTokens.length; i++) {
					vod.dtTokens[i] = lookToken(i+1);
				}
				vod.datatype = getDataType(XOMDataType.DESCRIBABLE_BY_ORDINAL);
				if (lookOfIn(1) && lookFactor(2, keywords)) {
					vod.ofInToken = getToken();
					vod.parentVariant = getFactor(keywords);
				} else {
					vod.ofInToken = null;
					vod.parentVariant = null;
				}
				return vod;
			// mass descriptor with "the", e.g. the cards
			} else if (lookDataType(2, XOMDataType.DESCRIBABLE_BY_MASS) != null) {
				XNVariantMassDescriptor vmd = new XNVariantMassDescriptor();
				vmd.allToken = null;
				vmd.theToken = getToken();
				vmd.dtTokens = new XNToken[lookDataType(1, XOMDataType.DESCRIBABLE_BY_MASS).name.length];
				for (int i = 0; i < vmd.dtTokens.length; i++) {
					vmd.dtTokens[i] = lookToken(i+1);
				}
				vmd.datatype = getDataType(XOMDataType.DESCRIBABLE_BY_MASS);
				if (lookOfIn(1) && lookFactor(2, keywords)) {
					vmd.ofInToken = getToken();
					vmd.parentVariant = getFactor(keywords);
				} else {
					vmd.ofInToken = null;
					vmd.parentVariant = null;
				}
				return vmd;
			// singleton descriptor with "the", e.g. the interpreter
			} else if (lookDataType(2, XOMDataType.DESCRIBABLE_BY_SINGLETON) != null) {
				XNVariantSingletonDescriptor vsd = new XNVariantSingletonDescriptor();
				vsd.theToken = getToken();
				vsd.dtTokens = new XNToken[lookDataType(1, XOMDataType.DESCRIBABLE_BY_SINGLETON).name.length];
				for (int i = 0; i < vsd.dtTokens.length; i++) {
					vsd.dtTokens[i] = lookToken(i+1);
				}
				vsd.datatype = getDataType(XOMDataType.DESCRIBABLE_BY_SINGLETON);
				if (lookOfIn(1) && lookFactor(2, keywords)) {
					vsd.ofInToken = getToken();
					vsd.parentVariant = getFactor(keywords);
				} else {
					vsd.ofInToken = null;
					vsd.parentVariant = null;
				}
				return vsd;
			// function call with "the", e.g. the date [of empty]
			} else if (lookToken(2).kind == XNToken.ID) {
				XNFunctionCallPropertyDescriptor fcpd = new XNFunctionCallPropertyDescriptor();
				fcpd.theToken = getToken();
				fcpd.modifierToken = null;
				fcpd.modifier = null;
				fcpd.idToken = lookToken(1);
				fcpd.identifier = getToken().image;
				Collection<String> myKeywords = new HashSet<String>();
				if (keywords != null) myKeywords.addAll(keywords);
				myKeywords.add(ALLOW_BARE_SM_DESCRIPTORS_TAG);
				if (lookOfIn(1) && lookFactor(2, myKeywords)) {
					fcpd.ofInToken = getToken();
					fcpd.argument = getFactor(myKeywords);
				} else {
					fcpd.ofInToken = null;
					fcpd.argument = null;
				}
				return fcpd;
			// variable or unquoted literal, i.e. the
			} else {
				return new XNVariableExpression(getToken());
			}
		}
		// THE "ID" PART OF THE SYNTAX TREE
		else if (lookToken(1).kind == XNToken.ID) {
			XNDataType tmp;
			Collection<String> keywordsToOf = new HashSet<String>();
			if (keywords != null) keywordsToOf.addAll(keywords);
			keywordsToOf.add(INSIDE_INDEXNAMEID_DESCRIPTOR_TAG);
			keywordsToOf.add("to");
			keywordsToOf.add("thru");
			keywordsToOf.add("through");
			keywordsToOf.add("of");
			keywordsToOf.add("in");
			// function call with "<modifier>" and "of", e.g. short date of empty
			if (lookModifier(1) != null && lookToken(2).kind == XNToken.ID && lookOfIn(3) && isNotInsideIndexNameIdDescriptor(keywords) && lookFactor(4, keywords)) {
				XNFunctionCallPropertyDescriptor fcpd = new XNFunctionCallPropertyDescriptor();
				fcpd.theToken = null;
				fcpd.modifierToken = lookToken(1);
				fcpd.modifier = getModifier();
				fcpd.idToken = lookToken(1);
				fcpd.identifier = getToken().image;
				Collection<String> myKeywords = new HashSet<String>();
				if (keywords != null) myKeywords.addAll(keywords);
				myKeywords.add(ALLOW_BARE_SM_DESCRIPTORS_TAG);
				fcpd.ofInToken = getToken();
				fcpd.argument = getFactor(myKeywords);
				return fcpd;
			// function call with "<modifier>" and parentheses, e.g. short date (
			} else if (lookModifier(1) != null && lookToken(2).kind == XNToken.ID && lookToken(3).kind == XNToken.SYMBOL && lookToken(3).image.equalsIgnoreCase("(")) {
				XNFunctionCallPropertyDescriptor fcpd = new XNFunctionCallPropertyDescriptor();
				fcpd.theToken = null;
				fcpd.modifierToken = lookToken(1);
				fcpd.modifier = getModifier();
				fcpd.idToken = lookToken(1);
				fcpd.identifier = getToken().image;
				Collection<String> myKeywords = new HashSet<String>();
				if (keywords != null) myKeywords.addAll(keywords);
				myKeywords.add(ALLOW_BARE_SM_DESCRIPTORS_TAG);
				fcpd.ofInToken = null;
				fcpd.argument = getFactor(myKeywords);
				return fcpd;
			// ordinal range descriptor, e.g. first through fifth cards
			} else if (lookOrdinal(1) != null && lookRangeSpecifier(2) && isNotInsideIndexNameIdDescriptor(keywords) && lookOrdinal(3) != null && lookDataType(4, XOMDataType.DESCRIBABLE_BY_ORDINAL_RANGE) != null) {
				XNVariantOrdinalDescriptor vod = new XNVariantOrdinalDescriptor();
				vod.theToken = null;
				vod.startOrdinal = getOrdinal();
				vod.toToken = getRangeSpecifier();
				vod.endOrdinal = getOrdinal();
				vod.dtTokens = new XNToken[lookDataType(1, XOMDataType.DESCRIBABLE_BY_ORDINAL_RANGE).name.length];
				for (int i = 0; i < vod.dtTokens.length; i++) {
					vod.dtTokens[i] = lookToken(i+1);
				}
				vod.datatype = getDataType(XOMDataType.DESCRIBABLE_BY_ORDINAL_RANGE);
				if (lookOfIn(1) && isNotInsideIndexNameIdDescriptor(keywords) && lookFactor(2, keywords)) {
					vod.ofInToken = getToken();
					vod.parentVariant = getFactor(keywords);
				} else {
					vod.ofInToken = null;
					vod.parentVariant = null;
				}
				return vod;
			// ordinal descriptor, e.g. first card
			} else if (lookOrdinal(1) != null && lookDataType(2, XOMDataType.DESCRIBABLE_BY_ORDINAL) != null) {
				XNVariantOrdinalDescriptor vod = new XNVariantOrdinalDescriptor();
				vod.theToken = null;
				vod.startOrdinal = getOrdinal();
				vod.toToken = null;
				vod.endOrdinal = null;
				vod.dtTokens = new XNToken[lookDataType(1, XOMDataType.DESCRIBABLE_BY_ORDINAL).name.length];
				for (int i = 0; i < vod.dtTokens.length; i++) {
					vod.dtTokens[i] = lookToken(i+1);
				}
				vod.datatype = getDataType(XOMDataType.DESCRIBABLE_BY_ORDINAL);
				if (lookOfIn(1) && isNotInsideIndexNameIdDescriptor(keywords) && lookFactor(2, keywords)) {
					vod.ofInToken = getToken();
					vod.parentVariant = getFactor(keywords);
				} else {
					vod.ofInToken = null;
					vod.parentVariant = null;
				}
				return vod;
			// mass descriptor with "of" or "in", e.g. chars in "Hello"
			} else if ((tmp = lookDataType(1, XOMDataType.DESCRIBABLE_BY_MASS)) != null && lookOfIn(1+tmp.name.length) && isNotInsideIndexNameIdDescriptor(keywords) && lookFactor(2+tmp.name.length, keywords)) {
				XNVariantMassDescriptor vmd = new XNVariantMassDescriptor();
				vmd.allToken = null;
				vmd.theToken = null;
				vmd.dtTokens = new XNToken[lookDataType(1, XOMDataType.DESCRIBABLE_BY_MASS).name.length];
				for (int i = 0; i < vmd.dtTokens.length; i++) {
					vmd.dtTokens[i] = lookToken(i+1);
				}
				vmd.datatype = getDataType(XOMDataType.DESCRIBABLE_BY_MASS);
				vmd.ofInToken = getToken();
				vmd.parentVariant = getFactor(keywords);
				return vmd;
			// singleton descriptor with "of" or "in", e.g. interpreter of empty
			} else if ((tmp = lookDataType(1, XOMDataType.DESCRIBABLE_BY_SINGLETON)) != null && lookOfIn(1+tmp.name.length) && isNotInsideIndexNameIdDescriptor(keywords) && lookFactor(2+tmp.name.length, keywords)) {
				XNVariantSingletonDescriptor vsd = new XNVariantSingletonDescriptor();
				vsd.theToken = null;
				vsd.dtTokens = new XNToken[lookDataType(1, XOMDataType.DESCRIBABLE_BY_SINGLETON).name.length];
				for (int i = 0; i < vsd.dtTokens.length; i++) {
					vsd.dtTokens[i] = lookToken(i+1);
				}
				vsd.datatype = getDataType(XOMDataType.DESCRIBABLE_BY_SINGLETON);
				vsd.ofInToken = getToken();
				vsd.parentVariant = getFactor(keywords);
				return vsd;
			// id descriptor, e.g. card id 1234
			} else if ((tmp = lookDataType(1, XOMDataType.DESCRIBABLE_BY_ID)) != null && lookToken(1+tmp.name.length).kind == XNToken.ID && lookToken(1+tmp.name.length).image.equalsIgnoreCase("id") && lookSingleExpression(2+tmp.name.length, keywordsToOf)) {
				XNVariantIdDescriptor vid = new XNVariantIdDescriptor();
				vid.dtTokens = new XNToken[lookDataType(1, XOMDataType.DESCRIBABLE_BY_ID).name.length];
				for (int i = 0; i < vid.dtTokens.length; i++) {
					vid.dtTokens[i] = lookToken(i+1);
				}
				vid.datatype = getDataType(XOMDataType.DESCRIBABLE_BY_ID);
				vid.idToken = getToken();
				vid.id = getSingleExpression(keywordsToOf);
				if (lookOfIn(1) && isNotInsideIndexNameIdDescriptor(keywords) && lookFactor(2, keywords)) {
					vid.ofInToken = getToken();
					vid.parentVariant = getFactor(keywords);
				} else {
					vid.ofInToken = null;
					vid.parentVariant = null;
				}
				return vid;
			// indexed, indexed range, or name descriptor, e.g. card "Hello"
			} else if ((tmp = lookDataType(1, XOMDataType.DESCRIBABLE_BY_IDXNAMEIR)) != null && lookSingleExpression(1+tmp.name.length, keywordsToOf)) {
				XNVariantIndexNameDescriptor vind = new XNVariantIndexNameDescriptor();
				vind.dtTokens = new XNToken[lookDataType(1, XOMDataType.DESCRIBABLE_BY_IDXNAMEIR).name.length];
				for (int i = 0; i < vind.dtTokens.length; i++) {
					vind.dtTokens[i] = lookToken(i+1);
				}
				vind.datatype = getDataType(XOMDataType.DESCRIBABLE_BY_IDXNAMEIR);
				vind.start = getSingleExpression(keywordsToOf);
				if (((vind.datatype.describability & XOMDataType.DESCRIBABLE_BY_INDEX_RANGE) != 0) && lookRangeSpecifier(1) && isNotInsideIndexNameIdDescriptor(keywords) && lookSingleExpression(2, keywordsToOf)) {
					vind.toToken = getToken();
					vind.end = getSingleExpression(keywordsToOf);
				} else {
					vind.toToken = null;
					vind.end = null;
				}
				if (lookOfIn(1) && isNotInsideIndexNameIdDescriptor(keywords) && lookFactor(2, keywords)) {
					vind.ofInToken = getToken();
					vind.parentVariant = getFactor(keywords);
				} else {
					vind.ofInToken = null;
					vind.parentVariant = null;
				}
				return vind;
			// function call with "of", e.g. date of empty
			} else if (lookToken(1).kind == XNToken.ID && lookOfIn(2) && isNotInsideIndexNameIdDescriptor(keywords) && lookFactor(3, keywords)) {
				XNFunctionCallPropertyDescriptor fcpd = new XNFunctionCallPropertyDescriptor();
				fcpd.theToken = null;
				fcpd.modifierToken = null;
				fcpd.modifier = null;
				fcpd.idToken = lookToken(1);
				fcpd.identifier = getToken().image;
				Collection<String> myKeywords = new HashSet<String>();
				if (keywords != null) myKeywords.addAll(keywords);
				myKeywords.add(ALLOW_BARE_SM_DESCRIPTORS_TAG);
				fcpd.ofInToken = getToken();
				fcpd.argument = getFactor(myKeywords);
				return fcpd;
			// function call with parentheses, e.g. date (
			} else if (lookToken(1).kind == XNToken.ID && lookToken(2).kind == XNToken.SYMBOL && lookToken(2).image.equalsIgnoreCase("(")) {
				XNFunctionCallPropertyDescriptor fcpd = new XNFunctionCallPropertyDescriptor();
				fcpd.theToken = null;
				fcpd.modifierToken = null;
				fcpd.modifier = null;
				fcpd.idToken = lookToken(1);
				fcpd.identifier = getToken().image;
				Collection<String> myKeywords = new HashSet<String>();
				if (keywords != null) myKeywords.addAll(keywords);
				myKeywords.add(ALLOW_BARE_SM_DESCRIPTORS_TAG);
				fcpd.ofInToken = null;
				fcpd.argument = getFactor(myKeywords);
				return fcpd;
			// mass descriptor, e.g. menus
			// only when called for
			} else if (allowingBareSingleMassDescriptors(keywords) && lookDataType(1, XOMDataType.DESCRIBABLE_BY_MASS) != null) {
				XNVariantMassDescriptor vmd = new XNVariantMassDescriptor();
				vmd.allToken = null;
				vmd.theToken = null;
				vmd.dtTokens = new XNToken[lookDataType(1, XOMDataType.DESCRIBABLE_BY_MASS).name.length];
				for (int i = 0; i < vmd.dtTokens.length; i++) {
					vmd.dtTokens[i] = lookToken(i+1);
				}
				vmd.datatype = getDataType(XOMDataType.DESCRIBABLE_BY_MASS);
				if (lookOfIn(1) && isNotInsideIndexNameIdDescriptor(keywords) && lookFactor(2, keywords)) {
					vmd.ofInToken = getToken();
					vmd.parentVariant = getFactor(keywords);
				} else {
					vmd.ofInToken = null;
					vmd.parentVariant = null;
				}
				return vmd;
			// singleton descriptor, e.g. interpreter
			// only when called for
			} else if (allowingBareSingleMassDescriptors(keywords) && lookDataType(1, XOMDataType.DESCRIBABLE_BY_SINGLETON) != null) {
				XNVariantSingletonDescriptor vsd = new XNVariantSingletonDescriptor();
				vsd.theToken = null;
				vsd.dtTokens = new XNToken[lookDataType(1, XOMDataType.DESCRIBABLE_BY_SINGLETON).name.length];
				for (int i = 0; i < vsd.dtTokens.length; i++) {
					vsd.dtTokens[i] = lookToken(i+1);
				}
				vsd.datatype = getDataType(XOMDataType.DESCRIBABLE_BY_SINGLETON);
				if (lookOfIn(1) && isNotInsideIndexNameIdDescriptor(keywords) && lookFactor(2, keywords)) {
					vsd.ofInToken = getToken();
					vsd.parentVariant = getFactor(keywords);
				} else {
					vsd.ofInToken = null;
					vsd.parentVariant = null;
				}
				return vsd;
			// variable or unquoted literal, e.g. x
			} else {
				return new XNVariableExpression(getToken());
			}
		}
		else throw new XNParseError("value", lookToken(1));
	}
	
	/* * * * * * * * *
	 * SCRIPT PARSER *
	 * * * * * * * * */
	
	public boolean lookEOL(int idx) {
		return (lookToken(idx).kind == XNToken.LINE_TERM);
	}
	
	public void getEOL() {
		while (lookToken(1).kind == XNToken.LINE_TERM && !lookToken(1).isEOF()) getToken();
	}
	
	private XNStatement getOTDCreateStatement() {
		if (
				lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("to") &&
				lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("create")
		) {
			XNObjectTypeCreateHandler h = new XNObjectTypeCreateHandler();
			h.beginToken = getToken(); getToken();
			if (lookEOL(1)) {
				getEOL();
				h.body = new Vector<XNStatement>();
				while (true) {
					if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("end") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("create")) {
						consumeTokens(2);
						break;
					} else if (lookToken(1).isEOF()) {
						throw new XNBlockParseError("end create", h.beginToken);
					} else {
						h.body.add(getStatement(null,true));
					}
				}
				h.endToken = lookToken(0);
				if (h.body.isEmpty()) h.body = null;
				return h;
			} else {
				throw new XNParseError("end of line", lookToken(1));
			}
		} else {
			throw new XNParseError("to create", lookToken(1));
		}
	}
	
	private XNStatement getOTDDeleteStatement() {
		if (
				lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("to") &&
				lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("delete")
		) {
			XNObjectTypeDeleteHandler h = new XNObjectTypeDeleteHandler();
			h.beginToken = getToken(); getToken();
			if (lookEOL(1)) {
				getEOL();
				h.body = new Vector<XNStatement>();
				while (true) {
					if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("end") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("delete")) {
						consumeTokens(2);
						break;
					} else if (lookToken(1).isEOF()) {
						throw new XNBlockParseError("end delete", h.beginToken);
					} else {
						h.body.add(getStatement(null,true));
					}
				}
				h.endToken = lookToken(0);
				if (h.body.isEmpty()) h.body = null;
				return h;
			} else {
				throw new XNParseError("end of line", lookToken(1));
			}
		} else {
			throw new XNParseError("to delete", lookToken(1));
		}
	}
	
	private XNStatement getOTDGetContentsStatement() {
		if (
				lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("to") &&
				lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("get") &&
				lookToken(3).kind == XNToken.ID && lookToken(3).image.equalsIgnoreCase("contents")
		) {
			XNObjectTypeGetContentsHandler h = new XNObjectTypeGetContentsHandler();
			h.beginToken = getToken(); getToken(); getToken();
			if (lookEOL(1)) {
				getEOL();
				h.body = new Vector<XNStatement>();
				while (true) {
					if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("end") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("get")) {
						consumeTokens(2);
						break;
					} else if (lookToken(1).isEOF()) {
						throw new XNBlockParseError("end get", h.beginToken);
					} else {
						h.body.add(getStatement(null,true));
					}
				}
				h.endToken = lookToken(0);
				if (h.body.isEmpty()) h.body = null;
				return h;
			} else {
				throw new XNParseError("end of line", lookToken(1));
			}
		} else {
			throw new XNParseError("to get", lookToken(1));
		}
	}
	
	private XNStatement getOTDPutContentsStatement() {
		if (
				lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("to") &&
				lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("put") &&
				lookToken(3).kind == XNToken.ID &&
				lookPreposition(4) != null &&
				lookToken(5).kind == XNToken.ID && lookToken(5).image.equalsIgnoreCase("contents")
		) {
			XNObjectTypePutContentsHandler h = new XNObjectTypePutContentsHandler();
			h.beginToken = getToken();
			getToken();
			h.identifier = getToken().image;
			h.preposition = getPreposition();
			getToken();
			if (lookEOL(1)) {
				getEOL();
				h.body = new Vector<XNStatement>();
				while (true) {
					if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("end") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("put")) {
						consumeTokens(2);
						break;
					} else if (lookToken(1).isEOF()) {
						throw new XNBlockParseError("end put", h.beginToken);
					} else {
						h.body.add(getStatement(null,true));
					}
				}
				h.endToken = lookToken(0);
				if (h.body.isEmpty()) h.body = null;
				return h;
			} else {
				throw new XNParseError("end of line", lookToken(1));
			}
		} else {
			throw new XNParseError("to put", lookToken(1));
		}
	}
	
	private XNStatement getOTDGetPropertyStatement() {
		if (
				lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("to") &&
				lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("get") &&
				lookToken(3).kind == XNToken.ID
		) {
			XNObjectTypePropertyGetter h = new XNObjectTypePropertyGetter();
			h.beginToken = getToken(); getToken(); h.propname = getToken().image;
			if (lookEOL(1)) {
				getEOL();
				h.body = new Vector<XNStatement>();
				while (true) {
					if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("end") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("get")) {
						consumeTokens(2);
						break;
					} else if (lookToken(1).isEOF()) {
						throw new XNBlockParseError("end get", h.beginToken);
					} else {
						h.body.add(getStatement(null,true));
					}
				}
				h.endToken = lookToken(0);
				if (h.body.isEmpty()) h.body = null;
				return h;
			} else {
				throw new XNParseError("end of line", lookToken(1));
			}
		} else {
			throw new XNParseError("to get", lookToken(1));
		}
	}
	
	private XNStatement getOTDSetPropertyStatement() {
		if (
				lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("to") &&
				lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("set") &&
				lookToken(3).kind == XNToken.ID &&
				lookToken(4).kind == XNToken.ID && lookToken(4).image.equalsIgnoreCase("to") &&
				lookToken(5).kind == XNToken.ID
		) {
			XNObjectTypePropertySetter h = new XNObjectTypePropertySetter();
			h.beginToken = getToken();
			getToken();
			h.propname = getToken().image;
			getToken();
			h.identifier = getToken().image;
			if (lookEOL(1)) {
				getEOL();
				h.body = new Vector<XNStatement>();
				while (true) {
					if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("end") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("set")) {
						consumeTokens(2);
						break;
					} else if (lookToken(1).isEOF()) {
						throw new XNBlockParseError("end set", h.beginToken);
					} else {
						h.body.add(getStatement(null,true));
					}
				}
				h.endToken = lookToken(0);
				if (h.body.isEmpty()) h.body = null;
				return h;
			} else {
				throw new XNParseError("end of line", lookToken(1));
			}
		} else {
			throw new XNParseError("to set", lookToken(1));
		}
	}
	
	private XNStatement getOTDStatement(Collection<String> keywords, boolean withEOL) {
		if (lookToken(1).kind == XNToken.ID) {
			XNToken bt = lookToken(1);
			XNStatement st;
			
			if (lookToken(1).image.equalsIgnoreCase("to")) {
				if (lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("put")) st = getOTDPutContentsStatement();
				else if (lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("set")) st = getOTDSetPropertyStatement();
				else if (lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("delete")) st = getOTDDeleteStatement();
				else if (lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("create")) st = getOTDCreateStatement();
				else if (lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("get") && lookToken(3).kind == XNToken.ID) {
					if (lookToken(3).image.equalsIgnoreCase("contents")) st = getOTDGetContentsStatement();
					else st = getOTDGetPropertyStatement();
				} else {
					throw new XNParseError("handler", lookToken(1));
				}
			}
			else if (lookToken(1).image.equalsIgnoreCase("on")) st = getMessageHandler();
			else if (lookToken(1).image.equalsIgnoreCase("function")) st = getFunctionHandler();
			else {
				throw new XNParseError("handler", lookToken(1));
			}

			st.beginToken = bt;
			st.endToken = lookToken(0);
			if (withEOL) {
				if (lookEOL(1)) getEOL();
				else throw new XNParseError("end of line", lookToken(1));
			}
			return st;
		} else {
			throw new XNParseError("handler", lookToken(1));
		}
	}
	
	private XNStatement getObjectTypeDeclaration() {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("object") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("type")) {
			XNToken objectToken = getToken();
			getToken();
			XNObjectTypeDeclaration d = new XNObjectTypeDeclaration();
			d.body = new Vector<XNStatement>();
			
			// This part parses the object name and all the akas and pls.
			d.names = new Vector<XNObjectTypeName>();
			while (true) {
				XNToken type;
				// This part parses the aka or pl keyword.
				// If it finds the extends or implements keyword, or the end of line, this whole section stops parsing object names.
				if (lookToken(1).kind == XNToken.ID) {
					if (lookToken(1).image.equalsIgnoreCase("aka") || lookToken(1).image.equalsIgnoreCase("pl")) {
						type = getToken();
					} else if (lookToken(1).image.equalsIgnoreCase("extends") /* || lookToken(1).image.equalsIgnoreCase("implements") */) {
						break;
					} else {
						type = new XNToken(XNToken.ID, "aka", lexer.getSource(), 0,0,0,0);
					}
				} else if (lookEOL(1)) {
					break;
				} else {
					throw new XNParseError("identifier", lookToken(1));
				}
				// This part parses the object name.
				// If it finds the aka, pl, extends, or implements keyword, or the end of line, it stops taking identifier tokens.
				Vector<String> name = new Vector<String>();
				while (true) {
					if (lookToken(1).kind == XNToken.ID) {
						if (lookToken(1).image.equalsIgnoreCase("aka") || lookToken(1).image.equalsIgnoreCase("pl")) {
							break;
						} else if (lookToken(1).image.equalsIgnoreCase("extends") /* || lookToken(1).image.equalsIgnoreCase("implements") */) {
							break;
						} else {
							name.add(getToken().image);
						}
					} else if (lookEOL(1)) {
						break;
					} else {
						throw new XNParseError("identifier", lookToken(1));
					}
				}
				// If we got a name, we add it.
				if (name.isEmpty()) {
					// nothing
				} else if (type.image.equalsIgnoreCase("pl")) {
					XNObjectTypeName oname = new XNObjectTypeName(name.toArray(new String[0]), true, XOMDataType.DESCRIBABILITY_OF_PLURAL_USER_OBJECTS);
					d.names.add(oname);
					knownAdditionalDataTypes.put(XIONUtil.normalizeVarName(oname.toNameString()), oname.describability);
				} else {
					XNObjectTypeName oname = new XNObjectTypeName(name.toArray(new String[0]), false, XOMDataType.DESCRIBABILITY_OF_SINGULAR_USER_OBJECTS);
					d.names.add(oname);
					knownAdditionalDataTypes.put(XIONUtil.normalizeVarName(oname.toNameString()), oname.describability);
				}
			}
			// If we got no names out of that, that's a script error.
			if (d.names.isEmpty()) {
				throw new XNParseError("identifier", lookToken(1));
			}
			
			// This part parses the extends clause.
			if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("extends")) {
				getToken();
				// This part parses the object name.
				// If it finds the implements keyword or the end of line, it stops taking identifier tokens.
				Vector<String> ename = new Vector<String>();
				while (true) {
					if (lookToken(1).kind == XNToken.ID) {
						/*
						if (lookToken(1).image.equalsIgnoreCase("implements")) {
							break;
						} else {
							ename.add(getToken().image);
						}
						*/
						ename.add(getToken().image);
					} else if (lookEOL(1)) {
						break;
					} else {
						throw new XNParseError("identifier", lookToken(1));
					}
				}
				// If we didn't get a name out of that, that's a script error.
				// Otherwise, we set our extendedtype to that object type name.
				if (ename.isEmpty()) {
					throw new XNParseError("identifier", lookToken(1));
				} else {
					d.extendedtype = new XNDataType(ename.toArray(new String[0]), 0);
				}
			} else {
				d.extendedtype = null;
			}
			
			// This part parses the implements clause.
			/*
			if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("implements")) {
				getToken();
				d.implementedtypes = new Vector<XNDataType>();
				// This part parses the first name.
				Vector<String> firstname = new Vector<String>();
				while (lookToken(1).kind == XNToken.ID) {
					firstname.add(getToken().image);
				}
				if (firstname.isEmpty()) {
					throw new XNParseError("identifier", lookToken(1));
				} else {
					d.implementedtypes.add(new XNDataType(firstname.toArray(new String[0]), 0));
				}
				// This part parses the rest of the names.
				while (lookOperator(1, false).precedence() == XNOperatorPrecedence.LIST) {
					getOperator(false);
					// This part parses each additional name.
					Vector<String> nextname = new Vector<String>();
					while (lookToken(1).kind == XNToken.ID) {
						nextname.add(getToken().image);
					}
					if (nextname.isEmpty()) {
						throw new XNParseError("identifier", lookToken(1));
					} else {
						d.implementedtypes.add(new XNDataType(nextname.toArray(new String[0]), 0));
					}
				}
			} else {
				d.implementedtypes = null;
			}
			*/
			
			//Now we should be at the end of the first line, finally.
			if (lookEOL(1)) {
				getEOL();
				//Now the body!
				XNDataType firstname = d.names.get(0);
				while (true) {
					//Here we have to match the first name of our object.
					boolean reachedEnd = (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("end"));
					for (int i = 0, k = 2; reachedEnd && (i < firstname.name.length); i++, k++) {
						if (!(lookToken(k).kind == XNToken.ID && lookToken(k).image.equalsIgnoreCase(firstname.name[i]))) {
							reachedEnd = false;
						}
					}
					if (reachedEnd) {
						consumeTokens(1+firstname.name.length);
						break;
					} else if (lookToken(1).isEOF()) {
						String expected = "end";
						for (String ds : firstname.name) {
							expected += " "+ds;
						}
						throw new XNBlockParseError(expected, objectToken);
					} else {
						d.body.add(getOTDStatement(null, true));
					}
				}
			} else {
				throw new XNParseError("end of line", lookToken(1));
			}
			if (d.body.isEmpty()) {
				d.body = null;
			}
			return d;
		} else {
			throw new XNParseError("object type declaration", lookToken(1));
		}
	}
	
	private XNHandlerParameter getParameter() {
		String vn;
		XNDataType as;
		XNExpression is;
		if (lookToken(1).kind == XNToken.ID) {
			vn = getToken().image;
		} else {
			throw new XNParseError("variable name", lookToken(1));
		}
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("as")) {
			getToken();
			as = getDataType();
		} else {
			as = null;
		}
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is")) {
			getToken();
			is = getSingleExpression(null);
		} else {
			is = null;
		}
		return new XNHandlerParameter(vn, as, is);
	}
	
	private XNStatement getMessageHandler() {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("on")) {
			XNMessageHandler block = new XNMessageHandler();
			block.messageToken = getToken();
			block.name = null;
			block.parameters = new Vector<XNHandlerParameter>();
			block.body = new Vector<XNStatement>();
			block.endMessageToken = null;
			if (lookToken(1).kind == XNToken.ID) {
				block.name = getToken().image;
			} else {
				throw new XNParseError("message name", lookToken(1));
			}
			if (!lookEOL(1)) {
				block.parameters.add(getParameter());
				while (lookOperator(1, false).precedence() == XNOperatorPrecedence.LIST) {
					getOperator(false);
					block.parameters.add(getParameter());
				}
			}
			if (lookEOL(1)) {
				getEOL();
				while (true) {
					if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("end") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase(block.name)) {
						block.endMessageToken = getToken();
						getToken();
						break;
					} else if (lookToken(1).isEOF()) {
						throw new XNBlockParseError("end "+block.name, block.messageToken);
					} else {
						block.body.add(getStatement(null, true));
					}
				}
			} else {
				throw new XNParseError("end of line", lookToken(1));
			}
			if (block.parameters.isEmpty()) block.parameters = null;
			if (block.body.isEmpty()) block.body = null;
			return block;
		} else {
			throw new XNParseError("on", lookToken(1));
		}
	}
	
	private XNStatement getFunctionHandler() {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("function")) {
			XNFunctionHandler block = new XNFunctionHandler();
			block.functionToken = getToken();
			block.name = null;
			block.parameters = new Vector<XNHandlerParameter>();
			block.body = new Vector<XNStatement>();
			block.endFunctionToken = null;
			if (lookToken(1).kind == XNToken.ID) {
				block.name = getToken().image;
			} else {
				throw new XNParseError("function name", lookToken(1));
			}
			if (!lookEOL(1)) {
				block.parameters.add(getParameter());
				while (lookOperator(1, false).precedence() == XNOperatorPrecedence.LIST) {
					getOperator(false);
					block.parameters.add(getParameter());
				}
			}
			if (lookEOL(1)) {
				getEOL();
				while (true) {
					if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("end") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase(block.name)) {
						block.endFunctionToken = getToken();
						getToken();
						break;
					} else if (lookToken(1).isEOF()) {
						throw new XNBlockParseError("end "+block.name, block.functionToken);
					} else {
						block.body.add(getStatement(null, true));
					}
				}
			} else {
				throw new XNParseError("end of line", lookToken(1));
			}
			if (block.parameters.isEmpty()) block.parameters = null;
			if (block.body.isEmpty()) block.body = null;
			return block;
		} else {
			throw new XNParseError("function", lookToken(1));
		}
	}
	
	private XNRepeatParameters getRepeatParameters() {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("for") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("each")) {
			XNRepeatForEachParameters r = new XNRepeatForEachParameters();
			r.beginToken = getToken();
			getToken();
			if (lookToken(1).kind == XNToken.ID) {
				r.identifier = getToken().image;
			} else {
				throw new XNParseError("variable name", lookToken(1));
			}
			if (lookOfIn(1)) {
				r.inToken = getToken();
			} else {
				throw new XNParseError("in or of", lookToken(1));
			}
			r.list = getListExpression(null);
			r.endToken = lookToken(0);
			return r;
		}
		else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("foreach")) {
			XNRepeatForEachParameters r = new XNRepeatForEachParameters();
			r.beginToken = getToken();
			if (lookToken(1).kind == XNToken.ID) {
				r.identifier = getToken().image;
			} else {
				throw new XNParseError("variable name", lookToken(1));
			}
			if (lookOfIn(1)) {
				r.inToken = getToken();
			} else {
				throw new XNParseError("in or of", lookToken(1));
			}
			r.list = getListExpression(null);
			r.endToken = lookToken(0);
			return r;
		}
		else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("for")) {
			XNRepeatForParameters r = new XNRepeatForParameters();
			r.beginToken = getToken();
			r.count = getListExpression(null);
			if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("times")) {
				getToken();
			}
			r.endToken = lookToken(0);
			return r;
		}
		else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("until")) {
			XNRepeatUntilParameters r = new XNRepeatUntilParameters();
			r.beginToken = getToken();
			r.condition = getListExpression(null);
			r.endToken = lookToken(0);
			return r;
		}
		else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("while")) {
			XNRepeatWhileParameters r = new XNRepeatWhileParameters();
			r.beginToken = getToken();
			r.condition = getListExpression(null);
			r.endToken = lookToken(0);
			return r;
		}
		else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("with")) {
			XNRepeatWithParameters r = new XNRepeatWithParameters();
			r.beginToken = getToken();
			Collection<String> myKeywords = new HashSet<String>();
			myKeywords.add("=");
			myKeywords.add("to");
			myKeywords.add("downto");
			myKeywords.add("down");
			myKeywords.add("step");
			boolean decrement;
			r.identifier = getListExpression(myKeywords);
			if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("=")) {
				r.equalToken = getToken();
			} else {
				throw new XNParseError("=", lookToken(1));
			}
			myKeywords.remove("=");
			r.startvalue = getListExpression(myKeywords);
			if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("to")) {
				r.toToken = getToken();
				decrement = false;
			} else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("downto")) {
				r.toToken = getToken();
				decrement = true;
			} else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("down") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("to")) {
				r.toToken = getToken();
				getToken();
				decrement = true;
			} else {
				throw new XNParseError("to or down to", lookToken(1));
			}
			myKeywords.remove("to");
			myKeywords.remove("downto");
			myKeywords.remove("down");
			r.endvalue = getListExpression(myKeywords);
			if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("step")) {
				r.stepToken = getToken();
				myKeywords.remove("step");
				r.stepvalue = getListExpression(myKeywords);
			} else {
				r.stepToken = null;
				myKeywords.remove("step");
				r.stepvalue = new XNNumberExpression(new XNToken(XNToken.NUMBER, "1", lexer.getSource(), 0,0,0,0));
			}
			if (decrement) {
				XNToken optk = new XNToken(XNToken.SYMBOL, "-", r.stepvalue.getSource(), r.stepvalue.getBeginLine(), r.stepvalue.getBeginCol(), r.stepvalue.getEndLine(), r.stepvalue.getEndCol());
				r.stepvalue = new XNUnaryExpression(optk, XNOperator.UNARY_SUBTRACT, r.stepvalue);
			}
			r.endToken = lookToken(0);
			return r;
		}
		else if (!lookEOL(1)) {
			XNRepeatForParameters r = new XNRepeatForParameters();
			r.beginToken = lookToken(1);
			r.count = getListExpression(null);
			if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("times")) {
				getToken();
			}
			r.endToken = lookToken(0);
			return r;
		}
		else {
			XNRepeatForParameters r = new XNRepeatForParameters();
			r.beginToken = lookToken(1);
			r.count = null;
			r.endToken = lookToken(0);
			return r;
		}
	}
	
	private XNStatement getRepeatBlock() {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("repeat")) {
			XNRepeatBlock block = new XNRepeatBlock();
			block.repeatToken = getToken();
			block.params = getRepeatParameters();
			block.body = new Vector<XNStatement>();
			block.lastlyToken = null;
			block.lastlyBody = new Vector<XNStatement>();
			block.endRepeatToken = null;
			if (lookEOL(1)) {
				getEOL();
				while (true) {
					if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("end") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("repeat")) {
						block.endRepeatToken = getToken();
						getToken();
						break;
					} else if (lookToken(1).kind == XNToken.ID && (lookToken(1).image.equalsIgnoreCase("lastly") || lookToken(1).image.equalsIgnoreCase("then") || lookToken(1).image.equalsIgnoreCase("else"))) {
						block.lastlyToken = getToken();
						if (lookEOL(1)) {
							getEOL();
							while (true) {
								if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("end") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("repeat")) {
									block.endRepeatToken = getToken();
									getToken();
									break;
								} else if (lookToken(1).isEOF()) {
									throw new XNBlockParseError("end repeat", block.repeatToken);
								} else {
									block.lastlyBody.add(getStatement(null, true));
								}
							}
						} else {
							throw new XNParseError("end of line", lookToken(1));
						}
						break;
					} else if (lookToken(1).isEOF()) {
						throw new XNBlockParseError("end repeat", block.repeatToken);
					} else {
						block.body.add(getStatement(null, true));
					}
				}
			} else {
				throw new XNParseError("end of line", lookToken(1));
			}
			if (block.body.isEmpty()) block.body = null;
			if (block.lastlyBody.isEmpty()) block.lastlyBody = null;
			return block;
		} else {
			throw new XNParseError("repeat", lookToken(1));
		}
	}
	
	private void getElseBlock(XNIfBlock block, Collection<String> keywords) {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("else")) {
			block.elseToken = getToken();
			if (lookEOL(1)) {
				getEOL();
				while (true) {
					if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("end") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("if")) {
						block.endIfToken = getToken();
						getToken();
						break;
					} else if (lookToken(1).isEOF()) {
						throw new XNBlockParseError("end if", block.elseToken);
					} else {
						block.elseBlock.add(getStatement(null, true));
					}
				}
			} else {
				Collection<String> myKeywords = new HashSet<String>();
				if (keywords != null) myKeywords.addAll(keywords);
				if (isNotKeyword(1, myKeywords)) block.elseBlock.add(getStatement(myKeywords, false));
				if (lookEOL(1)) {
					int i = 0;
					while (lookToken(i+1).kind == XNToken.LINE_TERM && i < LOOKAHEAD_LIMIT) i++;
					if (lookToken(i+1).kind == XNToken.ID && lookToken(i+1).image.equalsIgnoreCase("end") && lookToken(i+2).kind == XNToken.ID && lookToken(i+2).image.equalsIgnoreCase("if")) {
						getEOL();
						block.endIfToken = getToken();
						getToken();
					}
				}
			}
		} else {
			throw new XNParseError("else", lookToken(1));
		}
	}
	
	private void getThenBlock(XNIfBlock block, Collection<String> keywords) {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("then")) {
			block.thenToken = getToken();
			if (lookEOL(1)) {
				getEOL();
				while (true) {
					if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("end") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("if")) {
						block.endIfToken = getToken();
						getToken();
						break;
					} else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("else")) {
						getElseBlock(block, keywords);
						break;
					} else if (lookToken(1).isEOF()) {
						throw new XNBlockParseError("else or end if", block.thenToken);
					} else {
						block.thenBlock.add(getStatement(null, true));
					}
				}
			} else {
				Collection<String> myKeywords = new HashSet<String>();
				if (keywords != null) myKeywords.addAll(keywords);
				myKeywords.add("else");
				if (isNotKeyword(1, myKeywords)) block.thenBlock.add(getStatement(myKeywords, false));
				if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("else")) {
					getElseBlock(block, keywords);
				} else if (lookEOL(1)) {
					int i = 0;
					while (lookToken(i+1).kind == XNToken.LINE_TERM && i < LOOKAHEAD_LIMIT) i++;
					if (lookToken(i+1).kind == XNToken.ID && lookToken(i+1).image.equalsIgnoreCase("end") && lookToken(i+2).kind == XNToken.ID && lookToken(i+2).image.equalsIgnoreCase("if")) {
						getEOL();
						block.endIfToken = getToken();
						getToken();
					} else if (lookToken(i+1).kind == XNToken.ID && lookToken(i+1).image.equalsIgnoreCase("else")) {
						getEOL();
						getElseBlock(block, keywords);
					}
				}
			}
		} else {
			throw new XNParseError("then", lookToken(1));
		}
	}
	
	private XNIfBlock getIfBlock(Collection<String> keywords) {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("if")) {
			XNIfBlock block = new XNIfBlock();
			block.ifToken = getToken();
			block.condition = null;
			block.thenToken = null;
			block.thenBlock = new Vector<XNStatement>();
			block.elseToken = null;
			block.elseBlock = new Vector<XNStatement>();
			block.endIfToken = null;
			Collection<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add("then");
			block.condition = getListExpression(myKeywords);
			if (lookEOL(1)) getEOL();
			getThenBlock(block, keywords);
			if (block.thenBlock.isEmpty()) block.thenBlock = null;
			if (block.elseBlock.isEmpty()) block.elseBlock = null;
			return block;
		} else {
			throw new XNParseError("if", lookToken(1));
		}
	}
	
	private XNCaseBlock getCaseBlock() {
		XNCaseBlock block;
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("case")) {
			block = new XNCaseBlock();
			block.caseToken = getToken();
			block.caseValues = new Vector<XNExpression>();
			block.caseStatements = new Vector<XNStatement>();
			block.endToken = null;
			block.caseValues.add(getSingleExpression(null));
			while (lookOperator(1, false).precedence() == XNOperatorPrecedence.LIST) {
				getOperator(false);
				block.caseValues.add(getSingleExpression(null));
			}
		} else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("default")) {
			block = new XNCaseBlock();
			block.caseToken = getToken();
			block.caseValues = null;
			block.caseStatements = new Vector<XNStatement>();
			block.endToken = null;
		} else {
			throw new XNParseError("case or default", lookToken(1));
		}
		if (lookEOL(1)) getEOL();
		else throw new XNParseError("end of line", lookToken(1));
		while (true) {
			if (lookToken(1).kind == XNToken.ID && (
					lookToken(1).image.equalsIgnoreCase("case") ||
					lookToken(1).image.equalsIgnoreCase("default") ||
					(
							lookToken(1).image.equalsIgnoreCase("end") &&
							lookToken(2).kind == XNToken.ID &&
							lookToken(2).image.equalsIgnoreCase("switch")
					)
			)) {
				block.endToken = lookToken(0);
				break;
			} else if (lookToken(1).isEOF()) {
				throw new XNBlockParseError("case, default, or end switch", block.caseToken);
			} else {
				block.caseStatements.add(getStatement(null, true));
			}
		}
		if (block.caseStatements.isEmpty()) block.caseStatements = null;
		return block;
	}
	
	private XNStatement getSwitchBlock() {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("switch")) {
			XNSwitchBlock block = new XNSwitchBlock();
			block.switchToken = getToken();
			block.switchOn = getListExpression(null);
			block.cases = new Vector<XNCaseBlock>();
			block.endSwitchToken = null;
			if (lookEOL(1)) getEOL();
			else throw new XNParseError("end of line", lookToken(1));
			while (true) {
				if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("end") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("switch")) {
					block.endSwitchToken = getToken();
					getToken();
					break;
				} else if (lookToken(1).kind == XNToken.ID && (lookToken(1).image.equalsIgnoreCase("case") || lookToken(1).image.equalsIgnoreCase("default"))) {
					block.cases.add(getCaseBlock());
				} else if (lookToken(1).isEOF()) {
					throw new XNBlockParseError("end switch", block.switchToken);
				} else {
					throw new XNParseError("case, default, or end switch", lookToken(1));
				}
			}
			if (block.cases.isEmpty()) block.cases = null;
			return block;
		} else {
			throw new XNParseError("switch", lookToken(1));
		}
	}
	
	private XNStatement getTellBlock(Collection<String> keywords) {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("tell")) {
			XNTellBlock block = new XNTellBlock();
			block.tellToken = getToken();
			block.recipient = null;
			block.toToken = null;
			block.messages = new Vector<XNStatement>();
			block.endTellToken = null;
			Collection<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add("to");
			myKeywords.add(ALLOW_BARE_SM_DESCRIPTORS_TAG);
			block.recipient = getListExpression(myKeywords);
			myKeywords.remove(ALLOW_BARE_SM_DESCRIPTORS_TAG);
			if (lookEOL(1)) {
				getEOL();
				while (true) {
					if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("end") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("tell")) {
						block.endTellToken = getToken();
						getToken();
						break;
					} else if (lookToken(1).isEOF()) {
						throw new XNBlockParseError("end tell", block.tellToken);
					} else {
						block.messages.add(getStatement(null, true));
					}
				}
			} else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("to")) {
				block.toToken = getToken();
				block.messages.add(getStatement(keywords, false));
			} else {
				throw new XNParseError("to or end of line", lookToken(1));
			}
			if (block.messages.isEmpty()) block.messages = null;
			return block;
		} else {
			throw new XNParseError("tell", lookToken(1));
		}
	}
	
	private void getFinallyBlock(XNTryBlock block, Collection<String> keywords) {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("finally")) {
			block.finallyToken = getToken();
			if (lookEOL(1)) {
				getEOL();
				while (true) {
					if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("end") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("try")) {
						block.endTryToken = getToken();
						getToken();
						break;
					} else if (lookToken(1).isEOF()) {
						throw new XNBlockParseError("end try", block.finallyToken);
					} else {
						block.finallyBlock.add(getStatement(null, true));
					}
				}
			} else {
				Collection<String> myKeywords = new HashSet<String>();
				if (keywords != null) myKeywords.addAll(keywords);
				if (isNotKeyword(1, myKeywords)) block.finallyBlock.add(getStatement(myKeywords, false));
				if (lookEOL(1)) {
					int i = 0;
					while (lookToken(i+1).kind == XNToken.LINE_TERM && i < LOOKAHEAD_LIMIT) i++;
					if (lookToken(i+1).kind == XNToken.ID && lookToken(i+1).image.equalsIgnoreCase("end") && lookToken(i+2).kind == XNToken.ID && lookToken(i+2).image.equalsIgnoreCase("try")) {
						getEOL();
						block.endTryToken = getToken();
						getToken();
					}
				}
			}
		} else {
			throw new XNParseError("finally", lookToken(1));
		}
	}
	
	private void getCatchBlock(XNTryBlock block, Collection<String> keywords) {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("catch")) {
			block.catchToken = getToken();
			if (lookToken(1).kind == XNToken.ID) {
				block.catchIdentifier = getToken().image;
			} else {
				throw new XNParseError("variable name", lookToken(1));
			}
			if (lookEOL(1)) {
				getEOL();
				while (true) {
					if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("end") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("try")) {
						block.endTryToken = getToken();
						getToken();
						break;
					} else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("finally")) {
						getFinallyBlock(block, keywords);
						break;
					} else if (lookToken(1).isEOF()) {
						throw new XNBlockParseError("finally or end try", block.catchToken);
					} else {
						block.catchBlock.add(getStatement(null, true));
					}
				}
			} else {
				Collection<String> myKeywords = new HashSet<String>();
				if (keywords != null) myKeywords.addAll(keywords);
				myKeywords.add("finally");
				if (isNotKeyword(1, myKeywords)) block.catchBlock.add(getStatement(myKeywords, false));
				if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("finally")) {
					getFinallyBlock(block, keywords);
				} else if (lookEOL(1)) {
					int i = 0;
					while (lookToken(i+1).kind == XNToken.LINE_TERM && i < LOOKAHEAD_LIMIT) i++;
					if (lookToken(i+1).kind == XNToken.ID && lookToken(i+1).image.equalsIgnoreCase("end") && lookToken(i+2).kind == XNToken.ID && lookToken(i+2).image.equalsIgnoreCase("try")) {
						getEOL();
						block.endTryToken = getToken();
						getToken();
					} else if (lookToken(i+1).kind == XNToken.ID && lookToken(i+1).image.equalsIgnoreCase("finally")) {
						getEOL();
						getFinallyBlock(block, keywords);
					}
				}
			}
		} else {
			throw new XNParseError("catch", lookToken(1));
		}
	}
	
	private XNStatement getTryBlock(Collection<String> keywords) {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("try")) {
			XNTryBlock block = new XNTryBlock();
			block.tryToken = getToken();
			block.tryBlock = new Vector<XNStatement>();
			block.catchToken = null;
			block.catchIdentifier = null;
			block.catchBlock = new Vector<XNStatement>();
			block.finallyToken = null;
			block.finallyBlock = new Vector<XNStatement>();
			block.endTryToken = null;
			if (lookEOL(1)) {
				getEOL();
				while (true) {
					if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("end") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("try")) {
						block.endTryToken = getToken();
						getToken();
						break;
					} else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("finally")) {
						getFinallyBlock(block, keywords);
						break;
					} else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("catch")) {
						getCatchBlock(block, keywords);
						break;
					} else if (lookToken(1).isEOF()) {
						throw new XNBlockParseError("catch, finally, or end try", block.tryToken);
					} else {
						block.tryBlock.add(getStatement(null, true));
					}
				}
			} else {
				Collection<String> myKeywords = new HashSet<String>();
				if (keywords != null) myKeywords.addAll(keywords);
				myKeywords.add("catch");
				myKeywords.add("finally");
				if (isNotKeyword(1, myKeywords)) block.tryBlock.add(getStatement(myKeywords, false));
				if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("finally")) {
					getFinallyBlock(block, keywords);
				} else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("catch")) {
					getCatchBlock(block, keywords);
				} else if (lookEOL(1)) {
					int i = 0;
					while (lookToken(i+1).kind == XNToken.LINE_TERM && i < LOOKAHEAD_LIMIT) i++;
					if (lookToken(i+1).kind == XNToken.ID && lookToken(i+1).image.equalsIgnoreCase("end") && lookToken(i+2).kind == XNToken.ID && lookToken(i+2).image.equalsIgnoreCase("try")) {
						getEOL();
						block.endTryToken = getToken();
						getToken();
					} else if (lookToken(i+1).kind == XNToken.ID && lookToken(i+1).image.equalsIgnoreCase("finally")) {
						getEOL();
						getFinallyBlock(block, keywords);
					} else if (lookToken(i+1).kind == XNToken.ID && lookToken(i+1).image.equalsIgnoreCase("catch")) {
						getEOL();
						getCatchBlock(block, keywords);
					}
				}
			}
			if (block.tryBlock.isEmpty()) block.tryBlock = null;
			if (block.catchBlock.isEmpty()) block.catchBlock = null;
			if (block.finallyBlock.isEmpty()) block.finallyBlock = null;
			return block;
		} else {
			throw new XNParseError("try", lookToken(1));
		}
	}
	
	private XNStatement getConstantDeclaration(Collection<String> keywords) {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("const")) {
			getToken();
			String constname;
			XNExpression constvalue;
			if (lookToken(1).kind == XNToken.ID) {
				constname = getToken().image;
			} else {
				throw new XNParseError("constant name", lookToken(1));
			}
			if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("=")) {
				getToken();
			} else {
				throw new XNParseError("=", lookToken(1));
			}
			constvalue = getListExpression(keywords);
			knownAdditionalConstants.add(constname.toLowerCase());
			return new XNConstantDeclaration(constname, constvalue);
		} else {
			throw new XNParseError("const", lookToken(1));
		}
	}
	
	private XNStatement getDoStatement(Collection<String> keywords) {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("do")) {
			getToken();
			XNExpression what;
			XNExpression lang;
			Collection<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add("as");
			what = getListExpression(myKeywords);
			if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("as")) {
				getToken();
				lang = getListExpression(myKeywords);
			} else {
				lang = null;
			}
			return new XNDoStatement(what, lang);
		} else {
			throw new XNParseError("do", lookToken(1));
		}
	}
	
	private XNStatement getExitStatement(Collection<String> keywords) {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("exit")) {
			getToken();
			String what;
			XNExpression to;
			XNExpression err;
			Collection<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add("with");
			if (lookToken(1).kind == XNToken.ID && (lookToken(1).image.equalsIgnoreCase("repeat") || lookToken(1).image.equalsIgnoreCase("if") || lookToken(1).image.equalsIgnoreCase("switch") || lookToken(1).image.equalsIgnoreCase("tell") || lookToken(1).image.equalsIgnoreCase("try"))) {
				what = getToken().image;
				to = null;
				err = null;
			}
			else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("to")) {
				getToken();
				what = null;
				myKeywords.add(ALLOW_BARE_SM_DESCRIPTORS_TAG);
				to = getListExpression(myKeywords);
				myKeywords.remove(ALLOW_BARE_SM_DESCRIPTORS_TAG);
				if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("with") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("error")) {
					consumeTokens(2);
					err = getListExpression(myKeywords);
				} else {
					err = null;
				}
			}
			else if (lookToken(1).kind == XNToken.ID) {
				what = getToken().image;
				to = null;
				if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("with") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("error")) {
					consumeTokens(2);
					err = getListExpression(myKeywords);
				} else {
					err = null;
				}
			}
			else {
				throw new XNParseError("handler name", lookToken(1));
			}
			if (to != null) return new XNExitStatement(to, err);
			else return new XNExitStatement(what, err);
		} else {
			throw new XNParseError("exit", lookToken(1));
		}
	}
	
	private XNVariableInitializer getVariable(Collection<String> keywords) {
		String vn;
		XNDataType as;
		XNExpression is;
		if (lookToken(1).kind == XNToken.ID) {
			vn = getToken().image;
		} else {
			throw new XNParseError("variable name", lookToken(1));
		}
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("as")) {
			getToken();
			as = getDataType();
		} else {
			as = null;
		}
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("is")) {
			getToken();
			is = getSingleExpression(keywords);
		} else {
			is = null;
		}
		return new XNVariableInitializer(vn, as, is);
	}
	
	private XNStatement getVariableDeclaration(Collection<String> keywords) {
		if (lookToken(1).kind == XNToken.ID && XNVariableScope.forName(lookToken(1).image) != null) {
			XNVariableScope scope = XNVariableScope.forName(getToken().image);
			Vector<XNVariableInitializer> vars = new Vector<XNVariableInitializer>();
			vars.add(getVariable(keywords));
			while (lookOperator(1, false).precedence() == XNOperatorPrecedence.LIST) {
				getOperator(false);
				vars.add(getVariable(keywords));
			}
			return new XNVariableDeclaration(scope, vars);
		} else {
			throw new XNParseError("global, shared, static, or local", lookToken(1));
		}
	}
	
	private XNStatement getIncludeStatement(Collection<String> keywords) {
		boolean require;
		XNExpression scname;
		boolean once;
		boolean ask;
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("include")) {
			getToken();
			require = false;
		} else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("require")) {
			getToken();
			require = true;
		} else {
			throw new XNParseError("include or require", lookToken(1));
		}
		scname = getListExpression(keywords);
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("once")) {
			getToken();
			once = true;
		} else {
			once = false;
		}
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("without")
				&& lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("dialog")) {
			getToken();
			getToken();
			ask = false;
		} else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("with")
				&& lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("dialog")) {
			getToken();
			getToken();
			ask = true;
		} else {
			ask = true;
		}
		return new XNIncludeStatement(require, scname, once, ask);
	}
	
	private XNStatement getNextStatement(Collection<String> keywords) {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("next")) {
			getToken();
			String what;
			if (lookToken(1).kind == XNToken.ID && (lookToken(1).image.equalsIgnoreCase("repeat") || lookToken(1).image.equalsIgnoreCase("case"))) {
				what = getToken().image;
			} else {
				throw new XNParseError("repeat or case", lookToken(1));
			}
			return new XNNextStatement(what);
		} else {
			throw new XNParseError("next", lookToken(1));
		}
	}
	
	private XNStatement getOrdinalDeclaration(Collection<String> keywords) {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("ordinal")) {
			getToken();
			String ordname;
			XNExpression ordvalue;
			if (lookToken(1).kind == XNToken.ID) {
				ordname = getToken().image;
			} else {
				throw new XNParseError("identifier", lookToken(1));
			}
			if (lookToken(1).kind == XNToken.SYMBOL && lookToken(1).image.equalsIgnoreCase("=")) {
				getToken();
			} else {
				throw new XNParseError("=", lookToken(1));
			}
			ordvalue = getListExpression(keywords);
			knownAdditionalOrdinals.add(ordname.toLowerCase());
			return new XNOrdinalDeclaration(ordname, ordvalue);
		} else {
			throw new XNParseError("ordinal", lookToken(1));
		}
	}
	
	private XNStatement getPassStatement(Collection<String> keywords) {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("pass")) {
			getToken();
			XNPassStatement st;
			if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("to")) {
				consumeTokens(1);
				Collection<String> myKeywords = new HashSet<String>();
				if (keywords != null) myKeywords.addAll(keywords);
				myKeywords.add(ALLOW_BARE_SM_DESCRIPTORS_TAG);
				st = new XNPassStatement(getListExpression(myKeywords));
				myKeywords.remove(ALLOW_BARE_SM_DESCRIPTORS_TAG);
			} else if (lookToken(1).kind == XNToken.ID) {
				st = new XNPassStatement(getToken().image);
			} else {
				throw new XNParseError("identifier", lookToken(1));
			}
			return st;
		} else {
			throw new XNParseError("pass", lookToken(1));
		}
	}
	
	private XNStatement getReturnStatement(Collection<String> keywords) {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("return")) {
			getToken();
			XNExpression ret;
			if (lookListExpression(1, keywords)) {
				ret = getListExpression(keywords);
			} else {
				ret = null;
			}
			return new XNReturnStatement(ret);
		} else {
			throw new XNParseError("return", lookToken(1));
		}
	}
	
	private XNStatement getSendStatement(Collection<String> keywords) {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("send")) {
			consumeTokens(1);
			XNExpression msg;
			XNExpression recip;
			boolean reply = false;
			Collection<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add("to");
			myKeywords.add("with");
			myKeywords.add("without");
			msg = getListExpression(myKeywords);
			if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("to")) {
				consumeTokens(1);
				if (keywords == null || !keywords.contains("to")) myKeywords.remove("to");
				myKeywords.add(ALLOW_BARE_SM_DESCRIPTORS_TAG);
				recip = getListExpression(myKeywords);
				myKeywords.remove(ALLOW_BARE_SM_DESCRIPTORS_TAG);
			} else {
				recip = null;
			}
			if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("with") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("reply")) {
				consumeTokens(2);
				reply = true;
			} else if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("without") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("reply")) {
				consumeTokens(2);
				reply = false;
			}
			if (recip == null) return new XNSendStatement(msg, reply);
			else return new XNSendStatement(msg, recip, reply);
		} else {
			throw new XNParseError("send", lookToken(1));
		}
	}
	
	private XNStatement getThrowStatement(Collection<String> keywords) {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("throw")) {
			getToken();
			XNExpression excep = getListExpression(keywords);
			return new XNThrowStatement(excep);
		} else {
			throw new XNParseError("throw", lookToken(1));
		}
	}
	
	private XNStatement getUseStatement(Collection<String> keywords) {
		if (lookToken(1).kind == XNToken.ID && lookToken(1).image.equalsIgnoreCase("use")) {
			getToken();
			XNExpression clname = getListExpression(keywords);
			return new XNUseStatement(clname);
		} else {
			throw new XNParseError("use", lookToken(1));
		}
	}
	
	private XNStatement getCommandStatement(Collection<String> keywords) {
		if (lookToken(1).kind == XNToken.ID) {
			String command = getToken().image;
			List<XNExpression> parameters;
			XNModule.CommandParser m = context.getCommandParser(command.toLowerCase());
			if (m == null) parameters = defaultParseCommand(command, this, keywords);
			else parameters = m.parseCommand(command, this, keywords);
			return new XNCommandStatement(command, m, parameters);
		} else {
			throw new XNParseError("command name", lookToken(1));
		}
	}
	
	public boolean lookStatement(int idx, Collection<String> keywords, boolean withEOL) {
		return (lookToken(idx).kind == XNToken.ID);
	}
	
	public XNStatement getStatement(Collection<String> keywords, boolean withEOL) {
		if (lookToken(1).kind == XNToken.ID) {
			XNToken bt = lookToken(1);
			XNStatement st;
			
			if (lookToken(1).image.equalsIgnoreCase("object") && lookToken(2).kind == XNToken.ID && lookToken(2).image.equalsIgnoreCase("type")) st = getObjectTypeDeclaration();
			else if (lookToken(1).image.equalsIgnoreCase("on")) st = getMessageHandler();
			else if (lookToken(1).image.equalsIgnoreCase("function")) st = getFunctionHandler();
			else if (lookToken(1).image.equalsIgnoreCase("repeat")) st = getRepeatBlock();
			else if (lookToken(1).image.equalsIgnoreCase("if")) st = getIfBlock(keywords);
			else if (lookToken(1).image.equalsIgnoreCase("switch")) st = getSwitchBlock();
			else if (lookToken(1).image.equalsIgnoreCase("tell")) st = getTellBlock(keywords);
			else if (lookToken(1).image.equalsIgnoreCase("try")) st = getTryBlock(keywords);
			else if (lookToken(1).image.equalsIgnoreCase("const")) st = getConstantDeclaration(keywords);
			else if (lookToken(1).image.equalsIgnoreCase("do")) st = getDoStatement(keywords);
			else if (lookToken(1).image.equalsIgnoreCase("exit")) st = getExitStatement(keywords);
			else if (XNVariableScope.forName(lookToken(1).image) != null) st = getVariableDeclaration(keywords);
			else if (lookToken(1).image.equalsIgnoreCase("include")) st = getIncludeStatement(keywords);
			else if (lookToken(1).image.equalsIgnoreCase("next")) st = getNextStatement(keywords);
			else if (lookToken(1).image.equalsIgnoreCase("ordinal")) st = getOrdinalDeclaration(keywords);
			else if (lookToken(1).image.equalsIgnoreCase("pass")) st = getPassStatement(keywords);
			else if (lookToken(1).image.equalsIgnoreCase("require")) st = getIncludeStatement(keywords);
			else if (lookToken(1).image.equalsIgnoreCase("return")) st = getReturnStatement(keywords);
			else if (lookToken(1).image.equalsIgnoreCase("send")) st = getSendStatement(keywords);
			else if (lookToken(1).image.equalsIgnoreCase("throw")) st = getThrowStatement(keywords);
			else if (lookToken(1).image.equalsIgnoreCase("use")) st = getUseStatement(keywords);
			else st = getCommandStatement(keywords);
			
			st.beginToken = bt;
			st.endToken = lookToken(0);
			if (withEOL) {
				if (lookEOL(1)) getEOL();
				else throw new XNParseError("end of line", lookToken(1));
			}
			return st;
		} else {
			throw new XNParseError("statement", lookToken(1));
		}
	}
	
	public List<XNStatement> parse() {
		List<XNStatement> scr = new Vector<XNStatement>();
		if (lookEOL(1)) getEOL();
		while (!lookToken(1).isEOF()) {
			scr.add(getStatement(null, true));
		}
		return scr;
	}
	
	/* * * * * * * * * * * * * * * * *
	 * PARSING NON-BUILT-IN COMMANDS *
	 * * * * * * * * * * * * * * * * */
	
	public static final List<XNExpression> defaultParseCommand(String commandName, XNParser p, Collection<String> keywords) {
		List<XNExpression> params = new Vector<XNExpression>();
		if (p.lookOperator(1, false).precedence() == XNOperatorPrecedence.LIST && p.isNotKeyword(1, keywords)) {
			params.add(new XNEmptyExpression(p.lookToken(1).source, p.lookToken(1).beginLine, p.lookToken(1).beginColumn));
		} else if (p.lookSingleExpression(1, keywords)) {
			params.add(p.getSingleExpression(keywords));
		} else {
			return params;
		}
		while (p.lookOperator(1, false).precedence() == XNOperatorPrecedence.LIST && p.isNotKeyword(1, keywords)) {
			p.getOperator(false);
			if (p.lookSingleExpression(1, keywords)) {
				params.add(p.getSingleExpression(keywords));
			} else {
				params.add(new XNEmptyExpression(p.lookToken(1).source, p.lookToken(1).beginLine, p.lookToken(1).beginColumn));
			}
		}
		return params;
	}
	
	public static final String defaultDescribeCommand(String commandName, List<XNExpression> parameters) {
		String s = "";
		for (XNExpression p : parameters) {
			s += " "+p.toString()+",";
		}
		if (s.endsWith(",")) {
			s = s.substring(0, s.length()-1);
		}
		return s.trim();
	}
}
