/*
 * Copyright &copy; 2010 Rebecca G. Bettencourt / Kreative Software
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
 * @since OpenXION 1.2
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.binpack;

import java.io.IOException;

public class DFExpressionParser {
	private DFExpressionLexer lexer;
	
	public DFExpressionParser(DFExpressionLexer lexer) {
		this.lexer = lexer;
	}
	
	public DFExpression parse() throws IOException {
		DFExpression expr = parseExpression();
		String s = lexer.getToken();
		if (s != null) throw new RuntimeException("Parse error: expected end of expression but found " + s);
		else return expr;
	}
	
	private int parseInt(String s) {
		s = s.trim();
		if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
			int r = 0;
			for (char ch : s.substring(1, s.length()-1).toCharArray()) {
				r <<= 8;
				r |= ((int)ch & 0xFF);
			}
			return r;
		}
		if (s.startsWith("0x") || s.startsWith("0X")) {
			return (int)Long.parseLong(s.substring(2).trim(), 16);
		}
		if (s.startsWith("0o") || s.startsWith("0O")) {
			return (int)Long.parseLong(s.substring(2).trim(), 8);
		}
		if (s.startsWith("0b") || s.startsWith("0B")) {
			return (int)Long.parseLong(s.substring(2).trim(), 2);
		}
		if (s.startsWith("0") && s.length() > 1) {
			return (int)Long.parseLong(s.substring(1).trim(), 8);
		}
		return (int)Long.parseLong(s);
	}
	
	private DFExpression parseFactor() throws IOException {
		String s = lexer.getToken();
		if (s == null) {
			throw new RuntimeException("Parse error: expected factor but found end of expression");
		} else if (s.equals("!!")) {
			return new DFUnaryExpression(DFUnaryExpression.Operation.BOOLEAN_IDENTITY, parseFactor());
		} else if (s.equals("!")) {
			return new DFUnaryExpression(DFUnaryExpression.Operation.BOOLEAN_NOT, parseFactor());
		} else if (s.equals("~")) {
			return new DFUnaryExpression(DFUnaryExpression.Operation.BITWISE_NOT, parseFactor());
		} else if (s.equals("+")) {
			return new DFUnaryExpression(DFUnaryExpression.Operation.IDENTITY, parseFactor());
		} else if (s.equals("-")) {
			return new DFUnaryExpression(DFUnaryExpression.Operation.NEGATE, parseFactor());
		} else if (s.equals("(")) {
			DFExpression expr = parseExpression();
			s = lexer.getToken();
			if (s == null || !s.equals(")")) throw new RuntimeException("Parse error: expected ) but found " + ((s == null) ? "end of expression" : s));
			else return expr;
		} else if (s.equals("#") || s.equalsIgnoreCase("length")) {
			return new DFLengthExpression();
		} else if (s.equals("@") || s.equalsIgnoreCase("position")) {
			return new DFPositionExpression();
		} else if (s.equals("*") || s.equalsIgnoreCase("remaining")) {
			return new DFRemainingExpression();
		} else if (s.startsWith("'") || s.startsWith("\"") || Character.isDigit(s.charAt(0))) {
			return new DFConstantExpression(parseInt(s));
		} else if (s.startsWith("`") || s.startsWith("_") || Character.isLetter(s.charAt(0))) {
			if (s.startsWith("`") && s.endsWith("`")) s = s.substring(1, s.length()-1);
			DFFieldExpression expr = new DFFieldExpression(s);
			while (".".equals(lexer.lookToken(1))) {
				lexer.getToken();
				s = lexer.getToken();
				if (s == null || !(s.startsWith("`") || s.startsWith("_") || Character.isLetter(s.charAt(0)))) {
					throw new RuntimeException("Parse error: expected field name but found " + ((s == null) ? "end of expression" : s));
				}
				if (s.startsWith("`") && s.endsWith("`")) s = s.substring(1, s.length()-1);
				expr = new DFFieldExpression(expr, s);
			}
			return expr;
		} else {
			throw new RuntimeException("Parse error: expected factor but found " + s);
		}
	}
	
	private DFExpression parseMultiplicationExpression() throws IOException {
		DFExpression expr = parseFactor();
		while (true) {
			String s = lexer.lookToken(1);
			if (s == null) {
				break;
			} else if (s.equals("*")) {
				lexer.getToken();
				expr = new DFBinaryExpression(DFBinaryExpression.Operation.MULTIPLY, expr, parseFactor());
			} else if (s.equals("/")) {
				lexer.getToken();
				expr = new DFBinaryExpression(DFBinaryExpression.Operation.DIVIDE, expr, parseFactor());
			} else if (s.equals("%")) {
				lexer.getToken();
				expr = new DFBinaryExpression(DFBinaryExpression.Operation.MOD, expr, parseFactor());
			} else {
				break;
			}
		}
		return expr;
	}
	
	private DFExpression parseAdditionExpression() throws IOException {
		DFExpression expr = parseMultiplicationExpression();
		while (true) {
			String s = lexer.lookToken(1);
			if (s == null) {
				break;
			} else if (s.equals("+")) {
				lexer.getToken();
				expr = new DFBinaryExpression(DFBinaryExpression.Operation.ADD, expr, parseMultiplicationExpression());
			} else if (s.equals("-")) {
				lexer.getToken();
				expr = new DFBinaryExpression(DFBinaryExpression.Operation.SUBTRACT, expr, parseMultiplicationExpression());
			} else {
				break;
			}
		}
		return expr;
	}
	
	private DFExpression parseShiftExpression() throws IOException {
		DFExpression expr = parseAdditionExpression();
		while (true) {
			String s = lexer.lookToken(1);
			if (s == null) {
				break;
			} else if (s.equals("<<")) {
				lexer.getToken();
				expr = new DFBinaryExpression(DFBinaryExpression.Operation.SHIFT_LEFT, expr, parseAdditionExpression());
			} else if (s.equals(">>")) {
				lexer.getToken();
				expr = new DFBinaryExpression(DFBinaryExpression.Operation.SHIFT_RIGHT, expr, parseAdditionExpression());
			} else if (s.equals(">>>")) {
				lexer.getToken();
				expr = new DFBinaryExpression(DFBinaryExpression.Operation.UNSIGNED_SHIFT_RIGHT, expr, parseAdditionExpression());
			} else {
				break;
			}
		}
		return expr;
	}
	
	private DFExpression parseBitAndExpression() throws IOException {
		DFExpression expr = parseShiftExpression();
		while ("&".equals(lexer.lookToken(1))) {
			lexer.getToken();
			expr = new DFBinaryExpression(DFBinaryExpression.Operation.BITWISE_AND, expr, parseShiftExpression());
		}
		return expr;
	}
	
	private DFExpression parseBitXorExpression() throws IOException {
		DFExpression expr = parseBitAndExpression();
		while ("^".equals(lexer.lookToken(1))) {
			lexer.getToken();
			expr = new DFBinaryExpression(DFBinaryExpression.Operation.BITWISE_XOR, expr, parseBitAndExpression());
		}
		return expr;
	}
	
	private DFExpression parseBitOrExpression() throws IOException {
		DFExpression expr = parseBitXorExpression();
		while ("|".equals(lexer.lookToken(1))) {
			lexer.getToken();
			expr = new DFBinaryExpression(DFBinaryExpression.Operation.BITWISE_OR, expr, parseBitXorExpression());
		}
		return expr;
	}
	
	private DFExpression parseComparisonExpression() throws IOException {
		DFExpression expr = parseBitOrExpression();
		while (true) {
			String s = lexer.lookToken(1);
			if (s == null) {
				break;
			} else if (s.equals("<=")) {
				lexer.getToken();
				expr = new DFBinaryExpression(DFBinaryExpression.Operation.LESS_OR_EQUAL, expr, parseBitOrExpression());
			} else if (s.equals(">=")) {
				lexer.getToken();
				expr = new DFBinaryExpression(DFBinaryExpression.Operation.GREATER_OR_EQUAL, expr, parseBitOrExpression());
			} else if (s.equals("<")) {
				lexer.getToken();
				expr = new DFBinaryExpression(DFBinaryExpression.Operation.LESS_THAN, expr, parseBitOrExpression());
			} else if (s.equals(">")) {
				lexer.getToken();
				expr = new DFBinaryExpression(DFBinaryExpression.Operation.GREATER_THAN, expr, parseBitOrExpression());
			} else {
				break;
			}
		}
		return expr;
	}
	
	private DFExpression parseEqualityExpression() throws IOException {
		DFExpression expr = parseComparisonExpression();
		while (true) {
			String s = lexer.lookToken(1);
			if (s == null) {
				break;
			} else if (s.equals("==") || s.equals("=")) {
				lexer.getToken();
				expr = new DFBinaryExpression(DFBinaryExpression.Operation.EQUAL, expr, parseComparisonExpression());
			} else if (s.equals("!=") || s.equals("<>")) {
				lexer.getToken();
				expr = new DFBinaryExpression(DFBinaryExpression.Operation.NOT_EQUAL, expr, parseComparisonExpression());
			} else if (s.equals("<=>")) {
				lexer.getToken();
				expr = new DFBinaryExpression(DFBinaryExpression.Operation.COMPARE, expr, parseComparisonExpression());
			} else {
				break;
			}
		}
		return expr;
	}
	
	private DFExpression parseAndExpression() throws IOException {
		DFExpression expr = parseEqualityExpression();
		while ("&&".equals(lexer.lookToken(1))) {
			lexer.getToken();
			expr = new DFBinaryExpression(DFBinaryExpression.Operation.BOOLEAN_AND, expr, parseEqualityExpression());
		}
		return expr;
	}
	
	private DFExpression parseXorExpression() throws IOException {
		DFExpression expr = parseAndExpression();
		while ("^^".equals(lexer.lookToken(1))) {
			lexer.getToken();
			expr = new DFBinaryExpression(DFBinaryExpression.Operation.BOOLEAN_XOR, expr, parseAndExpression());
		}
		return expr;
	}
	
	private DFExpression parseOrExpression() throws IOException {
		DFExpression expr = parseXorExpression();
		while ("||".equals(lexer.lookToken(1))) {
			lexer.getToken();
			expr = new DFBinaryExpression(DFBinaryExpression.Operation.BOOLEAN_OR, expr, parseXorExpression());
		}
		return expr;
	}
	
	private DFExpression parseExpression() throws IOException {
		DFExpression expr = parseOrExpression();
		if ("?".equals(lexer.lookToken(1))) {
			lexer.getToken();
			DFExpression trueCase = parseExpression();
			String s = lexer.getToken();
			if (s == null || !s.equals(":")) throw new RuntimeException("Parse error: expected : but found " + ((s == null) ? "end of expression" : s));
			DFExpression falseCase = parseExpression();
			expr = new DFTernaryExpression(DFTernaryExpression.Operation.CONDITIONAL, expr, trueCase, falseCase);
		}
		return expr;
	}
	
	public static void main(String[] args) {
		for (String arg : args) {
			try {
				System.out.println(new DFExpressionParser(new DFExpressionLexer(new java.io.StringReader(arg))).parse().evaluate());
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}
}
