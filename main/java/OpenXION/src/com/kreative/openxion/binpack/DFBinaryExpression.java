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

import java.util.Map;

public class DFBinaryExpression implements DFExpression {
	public static enum Operation {
		BOOLEAN_OR,
		BOOLEAN_XOR,
		BOOLEAN_AND,
		BOOLEAN_EQUAL,
		EQUAL,
		NOT_EQUAL,
		COMPARE,
		LESS_THAN,
		LESS_OR_EQUAL,
		GREATER_THAN,
		GREATER_OR_EQUAL,
		BITWISE_OR,
		BITWISE_XOR,
		BITWISE_AND,
		SHIFT_LEFT,
		SHIFT_RIGHT,
		UNSIGNED_SHIFT_RIGHT,
		ADD,
		SUBTRACT,
		MULTIPLY,
		DIVIDE,
		MOD;
	}
	
	private Operation op;
	private DFExpression left;
	private DFExpression right;
	
	public DFBinaryExpression(Operation op, DFExpression left, DFExpression right) {
		this.op = op;
		this.left = left;
		this.right = right;
	}
	
	public int evaluate() {
		switch (op) {
		case BOOLEAN_OR: return ((left.evaluate() != 0) || (right.evaluate() != 0)) ? 1 : 0;
		case BOOLEAN_XOR: return ((left.evaluate() != 0) != (right.evaluate() != 0)) ? 1 : 0;
		case BOOLEAN_AND: return ((left.evaluate() != 0) && (right.evaluate() != 0)) ? 1 : 0;
		case BOOLEAN_EQUAL: return ((left.evaluate() != 0) == (right.evaluate() != 0)) ? 1 : 0;
		case EQUAL: return (left.evaluate() == right.evaluate()) ? 1 : 0;
		case NOT_EQUAL: return (left.evaluate() != right.evaluate()) ? 1 : 0;
		case COMPARE:
			int cl = left.evaluate();
			int cr = right.evaluate();
			return (cl < cr) ? -1 : (cl > cr) ? 1 : 0;
		case LESS_THAN: return (left.evaluate() < right.evaluate()) ? 1 : 0;
		case LESS_OR_EQUAL: return (left.evaluate() <= right.evaluate()) ? 1 : 0;
		case GREATER_THAN: return (left.evaluate() > right.evaluate()) ? 1 : 0;
		case GREATER_OR_EQUAL: return (left.evaluate() >= right.evaluate()) ? 1 : 0;
		case BITWISE_OR: return left.evaluate() | right.evaluate();
		case BITWISE_XOR: return left.evaluate() ^ right.evaluate();
		case BITWISE_AND: return left.evaluate() & right.evaluate();
		case SHIFT_LEFT: return left.evaluate() << right.evaluate();
		case SHIFT_RIGHT: return left.evaluate() >> right.evaluate();
		case UNSIGNED_SHIFT_RIGHT: return left.evaluate() >>> right.evaluate();
		case ADD: return left.evaluate() + right.evaluate();
		case SUBTRACT: return left.evaluate() - right.evaluate();
		case MULTIPLY: return left.evaluate() * right.evaluate();
		case DIVIDE: return left.evaluate() / right.evaluate();
		case MOD: return left.evaluate() % right.evaluate();
		default: return 0;
		}
	}
	
	public int evaluate(Map<?,?> fieldValues, BitInputStream in, long length) {
		switch (op) {
		case BOOLEAN_OR: return ((left.evaluate(fieldValues, in, length) != 0) || (right.evaluate(fieldValues, in, length) != 0)) ? 1 : 0;
		case BOOLEAN_XOR: return ((left.evaluate(fieldValues, in, length) != 0) != (right.evaluate(fieldValues, in, length) != 0)) ? 1 : 0;
		case BOOLEAN_AND: return ((left.evaluate(fieldValues, in, length) != 0) && (right.evaluate(fieldValues, in, length) != 0)) ? 1 : 0;
		case BOOLEAN_EQUAL: return ((left.evaluate(fieldValues, in, length) != 0) == (right.evaluate(fieldValues, in, length) != 0)) ? 1 : 0;
		case EQUAL: return (left.evaluate(fieldValues, in, length) == right.evaluate(fieldValues, in, length)) ? 1 : 0;
		case NOT_EQUAL: return (left.evaluate(fieldValues, in, length) != right.evaluate(fieldValues, in, length)) ? 1 : 0;
		case COMPARE:
			int cl = left.evaluate(fieldValues, in, length);
			int cr = right.evaluate(fieldValues, in, length);
			return (cl < cr) ? -1 : (cl > cr) ? 1 : 0;
		case LESS_THAN: return (left.evaluate(fieldValues, in, length) < right.evaluate(fieldValues, in, length)) ? 1 : 0;
		case LESS_OR_EQUAL: return (left.evaluate(fieldValues, in, length) <= right.evaluate(fieldValues, in, length)) ? 1 : 0;
		case GREATER_THAN: return (left.evaluate(fieldValues, in, length) > right.evaluate(fieldValues, in, length)) ? 1 : 0;
		case GREATER_OR_EQUAL: return (left.evaluate(fieldValues, in, length) >= right.evaluate(fieldValues, in, length)) ? 1 : 0;
		case BITWISE_OR: return left.evaluate(fieldValues, in, length) | right.evaluate(fieldValues, in, length);
		case BITWISE_XOR: return left.evaluate(fieldValues, in, length) ^ right.evaluate(fieldValues, in, length);
		case BITWISE_AND: return left.evaluate(fieldValues, in, length) & right.evaluate(fieldValues, in, length);
		case SHIFT_LEFT: return left.evaluate(fieldValues, in, length) << right.evaluate(fieldValues, in, length);
		case SHIFT_RIGHT: return left.evaluate(fieldValues, in, length) >> right.evaluate(fieldValues, in, length);
		case UNSIGNED_SHIFT_RIGHT: return left.evaluate(fieldValues, in, length) >>> right.evaluate(fieldValues, in, length);
		case ADD: return left.evaluate(fieldValues, in, length) + right.evaluate(fieldValues, in, length);
		case SUBTRACT: return left.evaluate(fieldValues, in, length) - right.evaluate(fieldValues, in, length);
		case MULTIPLY: return left.evaluate(fieldValues, in, length) * right.evaluate(fieldValues, in, length);
		case DIVIDE: return left.evaluate(fieldValues, in, length) / right.evaluate(fieldValues, in, length);
		case MOD: return left.evaluate(fieldValues, in, length) % right.evaluate(fieldValues, in, length);
		default: return 0;
		}
	}
	
	public int evaluate(Map<?,?> fieldValues, BitOutputStream out) {
		switch (op) {
		case BOOLEAN_OR: return ((left.evaluate(fieldValues, out) != 0) || (right.evaluate(fieldValues, out) != 0)) ? 1 : 0;
		case BOOLEAN_XOR: return ((left.evaluate(fieldValues, out) != 0) != (right.evaluate(fieldValues, out) != 0)) ? 1 : 0;
		case BOOLEAN_AND: return ((left.evaluate(fieldValues, out) != 0) && (right.evaluate(fieldValues, out) != 0)) ? 1 : 0;
		case BOOLEAN_EQUAL: return ((left.evaluate(fieldValues, out) != 0) == (right.evaluate(fieldValues, out) != 0)) ? 1 : 0;
		case EQUAL: return (left.evaluate(fieldValues, out) == right.evaluate(fieldValues, out)) ? 1 : 0;
		case NOT_EQUAL: return (left.evaluate(fieldValues, out) != right.evaluate(fieldValues, out)) ? 1 : 0;
		case COMPARE:
			int cl = left.evaluate(fieldValues, out);
			int cr = right.evaluate(fieldValues, out);
			return (cl < cr) ? -1 : (cl > cr) ? 1 : 0;
		case LESS_THAN: return (left.evaluate(fieldValues, out) < right.evaluate(fieldValues, out)) ? 1 : 0;
		case LESS_OR_EQUAL: return (left.evaluate(fieldValues, out) <= right.evaluate(fieldValues, out)) ? 1 : 0;
		case GREATER_THAN: return (left.evaluate(fieldValues, out) > right.evaluate(fieldValues, out)) ? 1 : 0;
		case GREATER_OR_EQUAL: return (left.evaluate(fieldValues, out) >= right.evaluate(fieldValues, out)) ? 1 : 0;
		case BITWISE_OR: return left.evaluate(fieldValues, out) | right.evaluate(fieldValues, out);
		case BITWISE_XOR: return left.evaluate(fieldValues, out) ^ right.evaluate(fieldValues, out);
		case BITWISE_AND: return left.evaluate(fieldValues, out) & right.evaluate(fieldValues, out);
		case SHIFT_LEFT: return left.evaluate(fieldValues, out) << right.evaluate(fieldValues, out);
		case SHIFT_RIGHT: return left.evaluate(fieldValues, out) >> right.evaluate(fieldValues, out);
		case UNSIGNED_SHIFT_RIGHT: return left.evaluate(fieldValues, out) >>> right.evaluate(fieldValues, out);
		case ADD: return left.evaluate(fieldValues, out) + right.evaluate(fieldValues, out);
		case SUBTRACT: return left.evaluate(fieldValues, out) - right.evaluate(fieldValues, out);
		case MULTIPLY: return left.evaluate(fieldValues, out) * right.evaluate(fieldValues, out);
		case DIVIDE: return left.evaluate(fieldValues, out) / right.evaluate(fieldValues, out);
		case MOD: return left.evaluate(fieldValues, out) % right.evaluate(fieldValues, out);
		default: return 0;
		}
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("(");
		s.append(left.toString());
		switch (op) {
		case BOOLEAN_OR: s.append("||"); break;
		case BOOLEAN_XOR: s.append("^^"); break;
		case BOOLEAN_AND: s.append("&&"); break;
		case EQUAL: s.append("=="); break;
		case NOT_EQUAL: s.append("!="); break;
		case COMPARE: s.append("<=>"); break;
		case LESS_THAN: s.append("<"); break;
		case LESS_OR_EQUAL: s.append("<="); break;
		case GREATER_THAN: s.append(">"); break;
		case GREATER_OR_EQUAL: s.append(">="); break;
		case BITWISE_OR: s.append("|"); break;
		case BITWISE_XOR: s.append("^"); break;
		case BITWISE_AND: s.append("&"); break;
		case SHIFT_LEFT: s.append("<<"); break;
		case SHIFT_RIGHT: s.append(">>"); break;
		case UNSIGNED_SHIFT_RIGHT: s.append(">>>"); break;
		case ADD: s.append("+"); break;
		case SUBTRACT: s.append("-"); break;
		case MULTIPLY: s.append("*"); break;
		case DIVIDE: s.append("/"); break;
		case MOD: s.append("%"); break;
		default: s.append("<"+op.name().toLowerCase()+">"); break;
		}
		s.append(right.toString());
		s.append(")");
		return s.toString();
	}
}
