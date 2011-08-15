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

import java.math.BigDecimal;

import com.kreative.openxion.XNContext;
import com.kreative.openxion.ast.XNOperator;
import com.kreative.openxion.ast.XNOperatorPrecedence;
import com.kreative.openxion.util.XIONUtil;
import com.kreative.openxion.xom.inst.XOMEmpty;
import com.kreative.openxion.xom.inst.XOMNumber;
import com.kreative.openxion.xom.inst.XOMString;

public class FSParser {
	private XNContext ctx;
	private String text;
	private int pos;
	
	public FSParser(XNContext ctx, String text) {
		this.ctx = ctx;
		this.text = text;
		this.pos = 0;
	}
	
	public FormatString parseFormatString() {
		FormatString s = new FormatString();
		while (pos < text.length()) {
			s.add(parseFormatStringComponent());
		}
		return s;
	}
	
	private FormatStringComponent parseFormatStringComponent() {
		if (text.charAt(pos) == '^') {
			pos++;
			if (pos < text.length()) {
				if (Character.isDigit(text.charAt(pos))) {
					return parseParameterFormatStringComponent();
				} else {
					switch (text.charAt(pos)) {
					case '{': pos++; return parseExpressionFormatStringComponent();
					case '}': pos++; return new LiteralFormatStringComponent("}");
					case '|': pos++; return new LiteralFormatStringComponent("|");
					case '^': pos++; return new LiteralFormatStringComponent("^");
					default: return new LiteralFormatStringComponent("^");
					}
				}
			} else {
				return new LiteralFormatStringComponent("^");
			}
		} else {
			return parseLiteralFormatStringComponent();
		}
	}
	
	private LiteralFormatStringComponent parseLiteralFormatStringComponent() {
		int start = pos;
		pos = text.indexOf('^', pos);
		if (pos < 0) pos = text.length();
		return new LiteralFormatStringComponent(text.substring(start, pos));
	}
	
	private ParameterFormatStringComponent parseParameterFormatStringComponent() {
		int paramNumber = 0;
		int minLength = 0;
		int maxLength = Integer.MAX_VALUE;
		boolean padSet = false;
		String leftPad = "";
		String rightPad = " ";
		Formatter formatter = Formatter.StringFormatter;
		while (pos < text.length() && Character.isDigit(text.charAt(pos))) {
			paramNumber *= 10;
			paramNumber += Character.getNumericValue(text.charAt(pos));
			pos++;
		}
		while ( (pos+1 < text.length()) && (
				(text.charAt(pos) == 'l') || (text.charAt(pos) == 'r') || (
						((text.charAt(pos) == 'n') || (text.charAt(pos) == 'm')) &&
						Character.isDigit(text.charAt(pos+1))
				)
		) ) {
			switch (text.charAt(pos)) {
			case 'l':
				if (!padSet) {
					padSet = true;
					leftPad = "";
					rightPad = "";
				}
				leftPad += Character.toString(text.charAt(pos+1));
				pos += 2;
				break;
			case 'r':
				if (!padSet) {
					padSet = true;
					leftPad = "";
					rightPad = "";
				}
				rightPad += Character.toString(text.charAt(pos+1));
				pos += 2;
				break;
			case 'n':
				pos++;
				minLength = 0;
				while (pos < text.length() && Character.isDigit(text.charAt(pos))) {
					minLength *= 10;
					minLength += Character.getNumericValue(text.charAt(pos));
					pos++;
				}
				break;
			case 'm':
				pos++;
				maxLength = 0;
				while (pos < text.length() && Character.isDigit(text.charAt(pos))) {
					maxLength *= 10;
					maxLength += Character.getNumericValue(text.charAt(pos));
					pos++;
				}
				break;
			}
		}
		if (pos < text.length() && Character.isLetter(text.charAt(pos))) {
			formatter = Formatter.forTypeSpecifier(text.charAt(pos));
			pos++;
		}
		if (pos < text.length() && text.charAt(pos) == '~') {
			pos++;
		}
		return new ParameterFormatStringComponent(
				paramNumber,
				new TrimmingFormatter(minLength, maxLength, leftPad, rightPad, formatter)
		);
	}
	
	private ExpressionFormatStringComponent parseExpressionFormatStringComponent() {
		FSExpression expr = parseExpression();
		ExpressionFormatStringComponent comp = new ExpressionFormatStringComponent(expr);
		while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) pos++;
		if (pos < text.length() && text.charAt(pos) == '?') pos++;
		while (pos < text.length() && text.charAt(pos) != '}') {
			FormatString s = new FormatString();
			while (pos < text.length() && text.charAt(pos) != '|' && text.charAt(pos) != '}') {
				s.add(parseFormatStringSubcomponent());
			}
			if (pos < text.length() && text.charAt(pos) == '|') pos++;
			comp.add(s);
		}
		if (pos < text.length() && text.charAt(pos) == '}') pos++;
		return comp;
	}
	
	private FormatStringComponent parseFormatStringSubcomponent() {
		if (text.charAt(pos) == '^') {
			pos++;
			if (pos < text.length()) {
				if (Character.isDigit(text.charAt(pos))) {
					return parseParameterFormatStringComponent();
				} else {
					switch (text.charAt(pos)) {
					case '{': pos++; return parseExpressionFormatStringComponent();
					case '}': pos++; return new LiteralFormatStringComponent("}");
					case '|': pos++; return new LiteralFormatStringComponent("|");
					case '^': pos++; return new LiteralFormatStringComponent("^");
					default: return new LiteralFormatStringComponent("^");
					}
				}
			} else {
				return new LiteralFormatStringComponent("^");
			}
		} else {
			return parseLiteralFormatStringSubcomponent();
		}
	}
	
	private LiteralFormatStringComponent parseLiteralFormatStringSubcomponent() {
		int start = pos;
		int cp = text.indexOf('^', pos); if (cp < 0) cp = text.length();
		int pp = text.indexOf('|', pos); if (pp < 0) pp = text.length();
		int bp = text.indexOf('}', pos); if (bp < 0) bp = text.length();
		pos = Math.min(cp, Math.min(pp, bp));
		return new LiteralFormatStringComponent(text.substring(start, pos));
	}
	
	private void skipWhitespace() {
		while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) pos++;
	}
	
	private XNOperator lookOperator(boolean unary) {
		String s = text.substring(pos);
		// format string extensions
		if (s.startsWith("&&&&")) return XNOperator.BIT_AND;
		if (s.startsWith("^^^^")) return XNOperator.BIT_XOR;
		if (s.startsWith("||||")) return XNOperator.BIT_OR;
		if (s.startsWith("~")) return XNOperator.BIT_NOT;
		if (s.startsWith("%%")) return XNOperator.MOD;
		if (s.startsWith("\\\\")) return XNOperator.DIV;
		if (s.startsWith("\\")) return XNOperator.QUOT;
		// standard XION operators
		if (s.startsWith("^^^")) return XNOperator.XOR;
		if (s.startsWith("^")) return XNOperator.EXPONENT;
		if (s.startsWith("-")) return unary ? XNOperator.UNARY_SUBTRACT : XNOperator.SUBTRACT;
		if (s.startsWith("::")) return XNOperator.LIST_APPEND;
		if (s.startsWith(":")) return XNOperator.LIST_APPEND;
		if (s.startsWith("@@")) return XNOperator.LIST_CONCAT;
		if (s.startsWith("@")) return XNOperator.LIST_CONCAT;
		if (s.startsWith("!==")) return XNOperator.NOT_STRICT_EQUAL;
		if (s.startsWith("!=")) return XNOperator.NOT_EQUAL;
		if (s.startsWith("!")) return XNOperator.NOT;
		if (s.startsWith("**")) return XNOperator.EXPONENT;
		if (s.startsWith("*")) return XNOperator.MULTIPLY;
		if (s.startsWith("/")) return XNOperator.DIVIDE;
		if (s.startsWith("\u00B7")) return XNOperator.MULTIPLY;
		if (s.startsWith("\u00D7")) return XNOperator.MULTIPLY;
		if (s.startsWith("\u00F7")) return XNOperator.DIVIDE;
		if (s.startsWith("\u2212")) return unary ? XNOperator.UNARY_SUBTRACT : XNOperator.SUBTRACT;
		if (s.startsWith("\u2260\u2260\u2260")) return XNOperator.NOT_STRICT_EQUAL;
		if (s.startsWith("\u2260\u2260")) return XNOperator.NOT_EQUAL;
		if (s.startsWith("\u2260")) return XNOperator.NOT_EQUAL;
		if (s.startsWith("\u2264")) return XNOperator.LE_NUM;
		if (s.startsWith("\u2265")) return XNOperator.GE_NUM;
		if (s.startsWith("&&&")) return XNOperator.SHORT_AND;
		if (s.startsWith("&&")) return XNOperator.STR_CONCAT_SPACE;
		if (s.startsWith("&")) return XNOperator.STR_CONCAT;
		if (s.startsWith("%")) return XNOperator.REM;
		if (s.startsWith("+")) return XNOperator.ADD;
		if (s.startsWith("<=>")) return XNOperator.CMP_NUM;
		if (s.startsWith("<<")) return XNOperator.SHIFT_LEFT;
		if (s.startsWith("<=")) return XNOperator.LE_NUM;
		if (s.startsWith("<>")) return XNOperator.NOT_EQUAL;
		if (s.startsWith("<")) return XNOperator.LT_NUM;
		if (s.startsWith("===")) return XNOperator.STRICT_EQUAL;
		if (s.startsWith("==")) return XNOperator.EQUAL;
		if (s.startsWith("=<")) return XNOperator.LE_NUM;
		if (s.startsWith("=>")) return XNOperator.GE_NUM;
		if (s.startsWith("=")) return XNOperator.EQUAL;
		if (s.startsWith(">>>")) return XNOperator.SHIFT_RIGHT_UNSIGNED;
		if (s.startsWith(">>")) return XNOperator.SHIFT_RIGHT_SIGNED;
		if (s.startsWith(">=")) return XNOperator.GE_NUM;
		if (s.startsWith(">")) return XNOperator.GT_NUM;
		if (s.startsWith("|||")) return XNOperator.SHORT_OR;
		if (s.startsWith("||")) return XNOperator.STR_CONCAT;
		return XNOperator.NULL;
	}
	
	private XNOperator parseOperator(boolean unary) {
		String s = text.substring(pos);
		// format string extensions
		if (s.startsWith("&&&&")) { pos += 4; return XNOperator.BIT_AND; }
		if (s.startsWith("^^^^")) { pos += 4; return XNOperator.BIT_XOR; }
		if (s.startsWith("||||")) { pos += 4; return XNOperator.BIT_OR; }
		if (s.startsWith("~")) { pos += 1; return XNOperator.BIT_NOT; }
		if (s.startsWith("%%")) { pos += 2; return XNOperator.MOD; }
		if (s.startsWith("\\\\")) { pos += 2; return XNOperator.DIV; }
		if (s.startsWith("\\")) { pos += 1; return XNOperator.QUOT; }
		// standard XION operators
		if (s.startsWith("^^^")) { pos += 3; return XNOperator.XOR; }
		if (s.startsWith("^")) { pos += 1; return XNOperator.EXPONENT; }
		if (s.startsWith("-")) { pos += 1; return unary ? XNOperator.UNARY_SUBTRACT : XNOperator.SUBTRACT; }
		if (s.startsWith("::")) { pos += 2; return XNOperator.LIST_APPEND; }
		if (s.startsWith(":")) { pos += 1; return XNOperator.LIST_APPEND; }
		if (s.startsWith("@@")) { pos += 2; return XNOperator.LIST_CONCAT; }
		if (s.startsWith("@")) { pos += 1; return XNOperator.LIST_CONCAT; }
		if (s.startsWith("!==")) { pos += 3; return XNOperator.NOT_STRICT_EQUAL; }
		if (s.startsWith("!=")) { pos += 2; return XNOperator.NOT_EQUAL; }
		if (s.startsWith("!")) { pos += 1; return XNOperator.NOT; }
		if (s.startsWith("**")) { pos += 2; return XNOperator.EXPONENT; }
		if (s.startsWith("*")) { pos += 1; return XNOperator.MULTIPLY; }
		if (s.startsWith("/")) { pos += 1; return XNOperator.DIVIDE; }
		if (s.startsWith("\u00B7")) { pos += 1; return XNOperator.MULTIPLY; }
		if (s.startsWith("\u00D7")) { pos += 1; return XNOperator.MULTIPLY; }
		if (s.startsWith("\u00F7")) { pos += 1; return XNOperator.DIVIDE; }
		if (s.startsWith("\u2212")) { pos += 1; return unary ? XNOperator.UNARY_SUBTRACT : XNOperator.SUBTRACT; }
		if (s.startsWith("\u2260\u2260\u2260")) { pos += 3; return XNOperator.NOT_STRICT_EQUAL; }
		if (s.startsWith("\u2260\u2260")) { pos += 2; return XNOperator.NOT_EQUAL; }
		if (s.startsWith("\u2260")) { pos += 1; return XNOperator.NOT_EQUAL; }
		if (s.startsWith("\u2264")) { pos += 1; return XNOperator.LE_NUM; }
		if (s.startsWith("\u2265")) { pos += 1; return XNOperator.GE_NUM; }
		if (s.startsWith("&&&")) { pos += 3; return XNOperator.SHORT_AND; }
		if (s.startsWith("&&")) { pos += 2; return XNOperator.STR_CONCAT_SPACE; }
		if (s.startsWith("&")) { pos += 1; return XNOperator.STR_CONCAT; }
		if (s.startsWith("%")) { pos += 1; return XNOperator.REM; }
		if (s.startsWith("+")) { pos += 1; return XNOperator.ADD; }
		if (s.startsWith("<=>")) { pos += 3; return XNOperator.CMP_NUM; }
		if (s.startsWith("<<")) { pos += 2; return XNOperator.SHIFT_LEFT; }
		if (s.startsWith("<=")) { pos += 2; return XNOperator.LE_NUM; }
		if (s.startsWith("<>")) { pos += 2; return XNOperator.NOT_EQUAL; }
		if (s.startsWith("<")) { pos += 1; return XNOperator.LT_NUM; }
		if (s.startsWith("===")) { pos += 3; return XNOperator.STRICT_EQUAL; }
		if (s.startsWith("==")) { pos += 2; return XNOperator.EQUAL; }
		if (s.startsWith("=<")) { pos += 2; return XNOperator.LE_NUM; }
		if (s.startsWith("=>")) { pos += 2; return XNOperator.GE_NUM; }
		if (s.startsWith("=")) { pos += 1; return XNOperator.EQUAL; }
		if (s.startsWith(">>>")) { pos += 3; return XNOperator.SHIFT_RIGHT_UNSIGNED; }
		if (s.startsWith(">>")) { pos += 2; return XNOperator.SHIFT_RIGHT_SIGNED; }
		if (s.startsWith(">=")) { pos += 2; return XNOperator.GE_NUM; }
		if (s.startsWith(">")) { pos += 1; return XNOperator.GT_NUM; }
		if (s.startsWith("|||")) { pos += 3; return XNOperator.SHORT_OR; }
		if (s.startsWith("||")) { pos += 2; return XNOperator.STR_CONCAT; }
		return XNOperator.NULL;
	}
	
	private FSExpression parseFactor() {
		skipWhitespace();
		if (pos < text.length()) {
			if (lookOperator(true).precedence() == XNOperatorPrecedence.UNARY) {
				XNOperator o = parseOperator(true);
				FSExpression a = parseFactor();
				return new FSUnaryExpression(o, a);
			}
			else if (text.charAt(pos) == '(') {
				pos++;
				FSExpression e = parseExpression();
				skipWhitespace();
				if (pos < text.length() && text.charAt(pos) == ')') pos++;
				return e;
			}
			else if (text.charAt(pos) == '"') {
				int start = pos;
				pos++;
				while (pos < text.length()) {
					if (text.charAt(pos) == '\n' || text.charAt(pos) == '\r' ||
						text.charAt(pos) == '\u2028' || text.charAt(pos) == '\u2029') {
						break;
					}
					else if (text.charAt(pos) == '"') {
						pos++;
						break;
					}
					else if (text.charAt(pos) == '\\') {
						pos += 2;
					}
					else {
						pos++;
					}
				}
				return new FSLiteralExpression(new XOMString(XIONUtil.unquote(text.substring(start, pos), ctx.getTextEncoding())));
			}
			else if (Character.isDigit(text.charAt(pos)) || text.charAt(pos) == '.') {
				StringBuffer s = new StringBuffer();
				while (Character.isDigit(text.charAt(pos)) || (text.charAt(pos) == '.' && s.indexOf(".") < 0)) {
					s.append(text.charAt(pos));
					pos++;
				}
				return new FSLiteralExpression(new XOMNumber(new BigDecimal(s.toString())));
			}
			else if (text.charAt(pos) == '^' && pos+1 < text.length() && Character.isDigit(text.charAt(pos+1))) {
				pos++;
				int paramNumber = 0;
				while (pos < text.length() && Character.isDigit(text.charAt(pos))) {
					paramNumber *= 10;
					paramNumber += Character.getNumericValue(text.charAt(pos));
					pos++;
				}
				if (pos < text.length() && text.charAt(pos) == '~') {
					pos++;
				}
				return new FSParameterExpression(paramNumber);
			}
			else {
				int start = pos;
				pos++;
				while (pos < text.length() && (
						Character.isLetterOrDigit(text.charAt(pos)) ||
						text.charAt(pos) == '_' || text.charAt(pos) == '.' ||
						text.charAt(pos) == '\''
				)) pos++;
				return new FSLiteralExpression(new XOMString(text.substring(start, pos)));
			}
		} else {
			return new FSLiteralExpression(XOMEmpty.EMPTY);
		}
	}
	
	private FSExpression parseExpExp() {
		FSExpression l = parseFactor();
		skipWhitespace();
		if (pos < text.length() && text.charAt(pos) != '?' && text.charAt(pos) != '}' && lookOperator(false).precedence() == XNOperatorPrecedence.EXPONENT) {
			XNOperator o = parseOperator(false);
			FSExpression r = parseExpExp();
			return new FSBinaryExpression(l, o, r);
		} else {
			return l;
		}
	}
	
	private FSExpression parseExpMul() {
		FSExpression l = parseExpExp();
		skipWhitespace();
		while (pos < text.length() && text.charAt(pos) != '?' && text.charAt(pos) != '}' && lookOperator(false).precedence() == XNOperatorPrecedence.MULTIPLY) {
			XNOperator o = parseOperator(false);
			FSExpression r = parseExpExp();
			l = new FSBinaryExpression(l, o, r);
			skipWhitespace();
		}
		return l;
	}
	
	private FSExpression parseExpAdd() {
		FSExpression l = parseExpMul();
		skipWhitespace();
		while (pos < text.length() && text.charAt(pos) != '?' && text.charAt(pos) != '}' && lookOperator(false).precedence() == XNOperatorPrecedence.ADD) {
			XNOperator o = parseOperator(false);
			FSExpression r = parseExpMul();
			l = new FSBinaryExpression(l, o, r);
			skipWhitespace();
		}
		return l;
	}
	
	private FSExpression parseExpShift() {
		FSExpression l = parseExpAdd();
		skipWhitespace();
		while (pos < text.length() && text.charAt(pos) != '?' && text.charAt(pos) != '}' && lookOperator(false).precedence() == XNOperatorPrecedence.SHIFT) {
			XNOperator o = parseOperator(false);
			FSExpression r = parseExpAdd();
			l = new FSBinaryExpression(l, o, r);
			skipWhitespace();
		}
		return l;
	}
	
	private FSExpression parseExpBitAnd() {
		FSExpression l = parseExpShift();
		skipWhitespace();
		while (pos < text.length() && text.charAt(pos) != '?' && text.charAt(pos) != '}' && lookOperator(false).precedence() == XNOperatorPrecedence.BIT_AND) {
			XNOperator o = parseOperator(false);
			FSExpression r = parseExpShift();
			l = new FSBinaryExpression(l, o, r);
			skipWhitespace();
		}
		return l;
	}
	
	private FSExpression parseExpBitXor() {
		FSExpression l = parseExpBitAnd();
		skipWhitespace();
		while (pos < text.length() && text.charAt(pos) != '?' && text.charAt(pos) != '}' && lookOperator(false).precedence() == XNOperatorPrecedence.BIT_XOR) {
			XNOperator o = parseOperator(false);
			FSExpression r = parseExpBitAnd();
			l = new FSBinaryExpression(l, o, r);
			skipWhitespace();
		}
		return l;
	}
	
	private FSExpression parseExpBitOr() {
		FSExpression l = parseExpBitXor();
		skipWhitespace();
		while (pos < text.length() && text.charAt(pos) != '?' && text.charAt(pos) != '}' && lookOperator(false).precedence() == XNOperatorPrecedence.BIT_OR) {
			XNOperator o = parseOperator(false);
			FSExpression r = parseExpBitXor();
			l = new FSBinaryExpression(l, o, r);
			skipWhitespace();
		}
		return l;
	}
	
	private FSExpression parseExpStrConcat() {
		FSExpression l = parseExpBitOr();
		skipWhitespace();
		while (pos < text.length() && text.charAt(pos) != '?' && text.charAt(pos) != '}' && lookOperator(false).precedence() == XNOperatorPrecedence.STR_CONCAT) {
			XNOperator o = parseOperator(false);
			FSExpression r = parseExpBitOr();
			l = new FSBinaryExpression(l, o, r);
			skipWhitespace();
		}
		return l;
	}
	
	private FSExpression parseExpListConcat() {
		FSExpression l = parseExpStrConcat();
		skipWhitespace();
		while (pos < text.length() && text.charAt(pos) != '?' && text.charAt(pos) != '}' && lookOperator(false).precedence() == XNOperatorPrecedence.LIST_CONCAT) {
			XNOperator o = parseOperator(false);
			FSExpression r = parseExpStrConcat();
			l = new FSBinaryExpression(l, o, r);
			skipWhitespace();
		}
		return l;
	}
	
	private FSExpression parseExpRel() {
		FSExpression l = parseExpListConcat();
		skipWhitespace();
		while (pos < text.length() && text.charAt(pos) != '?' && text.charAt(pos) != '}' && lookOperator(false).precedence() == XNOperatorPrecedence.RELATION) {
			XNOperator o = parseOperator(false);
			FSExpression r = parseExpListConcat();
			l = new FSBinaryExpression(l, o, r);
			skipWhitespace();
		}
		return l;
	}
	
	private FSExpression parseExpEqual() {
		FSExpression l = parseExpRel();
		skipWhitespace();
		while (pos < text.length() && text.charAt(pos) != '?' && text.charAt(pos) != '}' && lookOperator(false).precedence() == XNOperatorPrecedence.EQUAL) {
			XNOperator o = parseOperator(false);
			FSExpression r = parseExpRel();
			l = new FSBinaryExpression(l, o, r);
			skipWhitespace();
		}
		return l;
	}
	
	private FSExpression parseExpAnd() {
		FSExpression l = parseExpEqual();
		skipWhitespace();
		while (pos < text.length() && text.charAt(pos) != '?' && text.charAt(pos) != '}' && lookOperator(false).precedence() == XNOperatorPrecedence.AND) {
			XNOperator o = parseOperator(false);
			FSExpression r = parseExpEqual();
			l = new FSBinaryExpression(l, o, r);
			skipWhitespace();
		}
		return l;
	}
	
	private FSExpression parseExpXor() {
		FSExpression l = parseExpAnd();
		skipWhitespace();
		while (pos < text.length() && text.charAt(pos) != '?' && text.charAt(pos) != '}' && lookOperator(false).precedence() == XNOperatorPrecedence.XOR) {
			XNOperator o = parseOperator(false);
			FSExpression r = parseExpAnd();
			l = new FSBinaryExpression(l, o, r);
			skipWhitespace();
		}
		return l;
	}
	
	private FSExpression parseExpression() {
		FSExpression l = parseExpXor();
		skipWhitespace();
		while (pos < text.length() && text.charAt(pos) != '?' && text.charAt(pos) != '}' && lookOperator(false).precedence() == XNOperatorPrecedence.OR) {
			XNOperator o = parseOperator(false);
			FSExpression r = parseExpXor();
			l = new FSBinaryExpression(l, o, r);
			skipWhitespace();
		}
		return l;
	}
}
