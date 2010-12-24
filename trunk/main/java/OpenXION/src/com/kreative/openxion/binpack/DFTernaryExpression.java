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

public class DFTernaryExpression implements DFExpression {
	public static enum Operation {
		CONDITIONAL,
		BETWEEN_INCLUSIVE,
		BETWEEN_LEFT_INCLUSIVE,
		BETWEEN_RIGHT_INCLUSIVE,
		BETWEEN_EXCLUSIVE,
		NOT_BETWEEN_INCLUSIVE,
		NOT_BETWEEN_LEFT_INCLUSIVE,
		NOT_BETWEEN_RIGHT_INCLUSIVE,
		NOT_BETWEEN_EXCLUSIVE;
	}
	
	private Operation op;
	private DFExpression det;
	private DFExpression left;
	private DFExpression right;
	
	public DFTernaryExpression(Operation op, DFExpression det, DFExpression left, DFExpression right) {
		this.op = op;
		this.det = det;
		this.left = left;
		this.right = right;
	}
	
	public int evaluate() {
		int dv = det.evaluate();
		switch (op) {
		case CONDITIONAL: return (dv != 0) ? left.evaluate() : right.evaluate();
		case BETWEEN_INCLUSIVE: return (left.evaluate() <= dv && dv <= right.evaluate()) ? 1 : 0;
		case BETWEEN_LEFT_INCLUSIVE: return (left.evaluate() <= dv && dv < right.evaluate()) ? 1 : 0;
		case BETWEEN_RIGHT_INCLUSIVE: return (left.evaluate() < dv && dv <= right.evaluate()) ? 1 : 0;
		case BETWEEN_EXCLUSIVE: return (left.evaluate() < dv && dv < right.evaluate()) ? 1 : 0;
		case NOT_BETWEEN_INCLUSIVE: return (left.evaluate() <= dv && dv <= right.evaluate()) ? 0 : 1;
		case NOT_BETWEEN_LEFT_INCLUSIVE: return (left.evaluate() <= dv && dv < right.evaluate()) ? 0 : 1;
		case NOT_BETWEEN_RIGHT_INCLUSIVE: return (left.evaluate() < dv && dv <= right.evaluate()) ? 0 : 1;
		case NOT_BETWEEN_EXCLUSIVE: return (left.evaluate() < dv && dv < right.evaluate()) ? 0 : 1;
		default: return 0;
		}
	}
	
	public int evaluate(Map<?,?> fieldValues, BitInputStream in, long length) {
		int dv = det.evaluate(fieldValues, in, length);
		switch (op) {
		case CONDITIONAL: return (dv != 0) ? left.evaluate(fieldValues, in, length) : right.evaluate(fieldValues, in, length);
		case BETWEEN_INCLUSIVE: return (left.evaluate(fieldValues, in, length) <= dv && dv <= right.evaluate(fieldValues, in, length)) ? 1 : 0;
		case BETWEEN_LEFT_INCLUSIVE: return (left.evaluate(fieldValues, in, length) <= dv && dv < right.evaluate(fieldValues, in, length)) ? 1 : 0;
		case BETWEEN_RIGHT_INCLUSIVE: return (left.evaluate(fieldValues, in, length) < dv && dv <= right.evaluate(fieldValues, in, length)) ? 1 : 0;
		case BETWEEN_EXCLUSIVE: return (left.evaluate(fieldValues, in, length) < dv && dv < right.evaluate(fieldValues, in, length)) ? 1 : 0;
		case NOT_BETWEEN_INCLUSIVE: return (left.evaluate(fieldValues, in, length) <= dv && dv <= right.evaluate(fieldValues, in, length)) ? 0 : 1;
		case NOT_BETWEEN_LEFT_INCLUSIVE: return (left.evaluate(fieldValues, in, length) <= dv && dv < right.evaluate(fieldValues, in, length)) ? 0 : 1;
		case NOT_BETWEEN_RIGHT_INCLUSIVE: return (left.evaluate(fieldValues, in, length) < dv && dv <= right.evaluate(fieldValues, in, length)) ? 0 : 1;
		case NOT_BETWEEN_EXCLUSIVE: return (left.evaluate(fieldValues, in, length) < dv && dv < right.evaluate(fieldValues, in, length)) ? 0 : 1;
		default: return 0;
		}
	}
	
	public int evaluate(Map<?,?> fieldValues, BitOutputStream out) {
		int dv = det.evaluate(fieldValues, out);
		switch (op) {
		case CONDITIONAL: return (dv != 0) ? left.evaluate(fieldValues, out) : right.evaluate(fieldValues, out);
		case BETWEEN_INCLUSIVE: return (left.evaluate(fieldValues, out) <= dv && dv <= right.evaluate(fieldValues, out)) ? 1 : 0;
		case BETWEEN_LEFT_INCLUSIVE: return (left.evaluate(fieldValues, out) <= dv && dv < right.evaluate(fieldValues, out)) ? 1 : 0;
		case BETWEEN_RIGHT_INCLUSIVE: return (left.evaluate(fieldValues, out) < dv && dv <= right.evaluate(fieldValues, out)) ? 1 : 0;
		case BETWEEN_EXCLUSIVE: return (left.evaluate(fieldValues, out) < dv && dv < right.evaluate(fieldValues, out)) ? 1 : 0;
		case NOT_BETWEEN_INCLUSIVE: return (left.evaluate(fieldValues, out) <= dv && dv <= right.evaluate(fieldValues, out)) ? 0 : 1;
		case NOT_BETWEEN_LEFT_INCLUSIVE: return (left.evaluate(fieldValues, out) <= dv && dv < right.evaluate(fieldValues, out)) ? 0 : 1;
		case NOT_BETWEEN_RIGHT_INCLUSIVE: return (left.evaluate(fieldValues, out) < dv && dv <= right.evaluate(fieldValues, out)) ? 0 : 1;
		case NOT_BETWEEN_EXCLUSIVE: return (left.evaluate(fieldValues, out) < dv && dv < right.evaluate(fieldValues, out)) ? 0 : 1;
		default: return 0;
		}
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("(");
		s.append(det.toString());
		switch (op) {
		case CONDITIONAL: s.append("?"); break;
		default: s.append("<"+op.name().toLowerCase()+">"); break;
		}
		s.append(left.toString());
		s.append(":");
		s.append(right.toString());
		s.append(")");
		return s.toString();
	}
}
