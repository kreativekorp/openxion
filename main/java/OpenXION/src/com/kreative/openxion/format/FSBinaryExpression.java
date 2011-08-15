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

import java.math.BigInteger;
import java.math.MathContext;
import java.util.List;
import java.util.Vector;

import com.kreative.openxion.XNContext;
import com.kreative.openxion.XNInterpreter;
import com.kreative.openxion.XNScriptError;
import com.kreative.openxion.XNInterpreter.NaNComparisonException;
import com.kreative.openxion.ast.XNOperator;
import com.kreative.openxion.math.MathProcessor;
import com.kreative.openxion.math.XOMComplexMath;
import com.kreative.openxion.math.XOMNumberMath;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.inst.XOMBoolean;
import com.kreative.openxion.xom.inst.XOMComplex;
import com.kreative.openxion.xom.inst.XOMInteger;
import com.kreative.openxion.xom.inst.XOMList;
import com.kreative.openxion.xom.inst.XOMNumber;
import com.kreative.openxion.xom.inst.XOMPoint;
import com.kreative.openxion.xom.inst.XOMRectangle;
import com.kreative.openxion.xom.inst.XOMString;
import com.kreative.openxion.xom.type.XOMBooleanType;
import com.kreative.openxion.xom.type.XOMComplexType;
import com.kreative.openxion.xom.type.XOMIntegerType;
import com.kreative.openxion.xom.type.XOMNumberType;
import com.kreative.openxion.xom.type.XOMPointType;
import com.kreative.openxion.xom.type.XOMRectangleType;

public class FSBinaryExpression implements FSExpression {
	private FSExpression left;
	private XNOperator op;
	private FSExpression right;
	
	public FSBinaryExpression(FSExpression left, XNOperator op, FSExpression right) {
		this.left = left;
		this.op = op;
		this.right = right;
	}
	
	@Override
	public XOMVariant evaluate(XNContext ctx, List<? extends XOMVariant> vs) {
		MathContext mc = ctx.getMathContext();
		MathProcessor mp = ctx.getMathProcessor();
		XOMVariant av, bv;
		String as, bs;
		boolean ab, bb;
		List<? extends XOMVariant> al, bl;
		BigInteger ai, bi;
		XOMNumber an, bn;
		XOMComplex ac, bc;
		XOMPoint ap;
		XOMRectangle ar, br;
		int cmp;
		switch (op) {
		case EXPONENT:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			if (av instanceof XOMComplex && bv instanceof XOMComplex) {
				ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMComplexMath.pow(ac, bc, mc, mp);
			}
			else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
				an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMNumberMath.pow(an, bn, mc, mp);
			}
			else if (XOMNumberType.instance.canMakeInstanceFrom(ctx, av, true) && XOMNumberType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMNumberMath.pow(an, bn, mc, mp);
			}
			else if (XOMComplexType.instance.canMakeInstanceFrom(ctx, av, true) && XOMComplexType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMComplexMath.pow(ac, bc, mc, mp);
			}
			else {
				throw new XOMMorphError("number");
			}
		case MULTIPLY:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			if (av instanceof XOMComplex && bv instanceof XOMComplex) {
				ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMComplexMath.multiply(ac, bc, mc, mp);
			}
			else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
				an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMNumberMath.multiply(an, bn, mc, mp);
			}
			else if (XOMNumberType.instance.canMakeInstanceFrom(ctx, av, true) && XOMNumberType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMNumberMath.multiply(an, bn, mc, mp);
			}
			else if (XOMComplexType.instance.canMakeInstanceFrom(ctx, av, true) && XOMComplexType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMComplexMath.multiply(ac, bc, mc, mp);
			}
			else {
				throw new XOMMorphError("number");
			}
		case DIVIDE:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			if (av instanceof XOMComplex && bv instanceof XOMComplex) {
				ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMComplexMath.divide(ac, bc, mc, mp);
			}
			else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
				an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMNumberMath.divide(an, bn, mc, mp);
			}
			else if (XOMNumberType.instance.canMakeInstanceFrom(ctx, av, true) && XOMNumberType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMNumberMath.divide(an, bn, mc, mp);
			}
			else if (XOMComplexType.instance.canMakeInstanceFrom(ctx, av, true) && XOMComplexType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMComplexMath.divide(ac, bc, mc, mp);
			}
			else {
				throw new XOMMorphError("number");
			}
		case QUOT:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			if (av instanceof XOMComplex && bv instanceof XOMComplex) {
				ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMComplexMath.divide(ac, bc, mc, mp).trunc();
			}
			else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
				an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMNumberMath.divide(an, bn, mc, mp).trunc();
			}
			else if (XOMNumberType.instance.canMakeInstanceFrom(ctx, av, true) && XOMNumberType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMNumberMath.divide(an, bn, mc, mp).trunc();
			}
			else if (XOMComplexType.instance.canMakeInstanceFrom(ctx, av, true) && XOMComplexType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMComplexMath.divide(ac, bc, mc, mp).trunc();
			}
			else {
				throw new XOMMorphError("number");
			}
		case REM:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			if (av instanceof XOMComplex && bv instanceof XOMComplex) {
				ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMComplexMath.subtract(ac, XOMComplexMath.multiply(bc, XOMComplexMath.divide(ac, bc, mc, mp).trunc(), mc, mp), mc, mp);
			}
			else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
				an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMNumberMath.subtract(an, XOMNumberMath.multiply(bn, XOMNumberMath.divide(an, bn, mc, mp).trunc(), mc, mp), mc, mp);
			}
			else if (XOMNumberType.instance.canMakeInstanceFrom(ctx, av, true) && XOMNumberType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMNumberMath.subtract(an, XOMNumberMath.multiply(bn, XOMNumberMath.divide(an, bn, mc, mp).trunc(), mc, mp), mc, mp);
			}
			else if (XOMComplexType.instance.canMakeInstanceFrom(ctx, av, true) && XOMComplexType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMComplexMath.subtract(ac, XOMComplexMath.multiply(bc, XOMComplexMath.divide(ac, bc, mc, mp).trunc(), mc, mp), mc, mp);
			}
			else {
				throw new XOMMorphError("number");
			}
		case DIV:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			if (av instanceof XOMComplex && bv instanceof XOMComplex) {
				ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMComplexMath.divide(ac, bc, mc, mp).floor();
			}
			else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
				an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMNumberMath.divide(an, bn, mc, mp).floor();
			}
			else if (XOMNumberType.instance.canMakeInstanceFrom(ctx, av, true) && XOMNumberType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMNumberMath.divide(an, bn, mc, mp).floor();
			}
			else if (XOMComplexType.instance.canMakeInstanceFrom(ctx, av, true) && XOMComplexType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMComplexMath.divide(ac, bc, mc, mp).floor();
			}
			else {
				throw new XOMMorphError("number");
			}
		case MOD:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			if (av instanceof XOMComplex && bv instanceof XOMComplex) {
				ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMComplexMath.subtract(ac, XOMComplexMath.multiply(bc, XOMComplexMath.divide(ac, bc, mc, mp).floor(), mc, mp), mc, mp);
			}
			else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
				an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMNumberMath.subtract(an, XOMNumberMath.multiply(bn, XOMNumberMath.divide(an, bn, mc, mp).floor(), mc, mp), mc, mp);
			}
			else if (XOMNumberType.instance.canMakeInstanceFrom(ctx, av, true) && XOMNumberType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMNumberMath.subtract(an, XOMNumberMath.multiply(bn, XOMNumberMath.divide(an, bn, mc, mp).floor(), mc, mp), mc, mp);
			}
			else if (XOMComplexType.instance.canMakeInstanceFrom(ctx, av, true) && XOMComplexType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMComplexMath.subtract(ac, XOMComplexMath.multiply(bc, XOMComplexMath.divide(ac, bc, mc, mp).floor(), mc, mp), mc, mp);
			}
			else {
				throw new XOMMorphError("number");
			}
		case ADD:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			if (av instanceof XOMComplex && bv instanceof XOMComplex) {
				ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMComplexMath.add(ac, bc, mc, mp);
			}
			else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
				an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMNumberMath.add(an, bn, mc, mp);
			}
			else if (XOMNumberType.instance.canMakeInstanceFrom(ctx, av, true) && XOMNumberType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMNumberMath.add(an, bn, mc, mp);
			}
			else if (XOMComplexType.instance.canMakeInstanceFrom(ctx, av, true) && XOMComplexType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMComplexMath.add(ac, bc, mc, mp);
			}
			else {
				throw new XOMMorphError("number");
			}
		case SUBTRACT:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			if (av instanceof XOMComplex && bv instanceof XOMComplex) {
				ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMComplexMath.subtract(ac, bc, mc, mp);
			}
			else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
				an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMNumberMath.subtract(an, bn, mc, mp);
			}
			else if (XOMNumberType.instance.canMakeInstanceFrom(ctx, av, true) && XOMNumberType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				an = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				bn = XOMNumberType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMNumberMath.subtract(an, bn, mc, mp);
			}
			else if (XOMComplexType.instance.canMakeInstanceFrom(ctx, av, true) && XOMComplexType.instance.canMakeInstanceFrom(ctx, bv, true)) {
				ac = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				bc = XOMComplexType.instance.makeInstanceFrom(ctx, bv, true);
				return XOMComplexMath.subtract(ac, bc, mc, mp);
			}
			else {
				throw new XOMMorphError("number");
			}
		case SHIFT_LEFT:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			ai = XOMIntegerType.instance.makeInstanceFrom(ctx, av, true).toBigInteger();
			bi = XOMIntegerType.instance.makeInstanceFrom(ctx, bv, true).toBigInteger();
			if (ai == null || bi == null) return XOMInteger.NaN;
			return new XOMInteger(ai.shiftLeft(bi.intValue()));
		case SHIFT_RIGHT_SIGNED:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			ai = XOMIntegerType.instance.makeInstanceFrom(ctx, av, true).toBigInteger();
			bi = XOMIntegerType.instance.makeInstanceFrom(ctx, bv, true).toBigInteger();
			if (ai == null || bi == null) return XOMInteger.NaN;
			return new XOMInteger(ai.shiftRight(bi.intValue()));
		case SHIFT_RIGHT_UNSIGNED:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			ai = XOMIntegerType.instance.makeInstanceFrom(ctx, av, true).toBigInteger();
			bi = XOMIntegerType.instance.makeInstanceFrom(ctx, bv, true).toBigInteger();
			if (ai == null || bi == null) return XOMInteger.NaN;
			return new XOMInteger(ai.shiftRight(bi.intValue()));
		case BIT_AND:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			ai = XOMIntegerType.instance.makeInstanceFrom(ctx, av, true).toBigInteger();
			bi = XOMIntegerType.instance.makeInstanceFrom(ctx, bv, true).toBigInteger();
			if (ai == null || bi == null) return XOMInteger.NaN;
			return new XOMInteger(ai.and(bi));
		case BIT_XOR:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			ai = XOMIntegerType.instance.makeInstanceFrom(ctx, av, true).toBigInteger();
			bi = XOMIntegerType.instance.makeInstanceFrom(ctx, bv, true).toBigInteger();
			if (ai == null || bi == null) return XOMInteger.NaN;
			return new XOMInteger(ai.xor(bi));
		case BIT_OR:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			ai = XOMIntegerType.instance.makeInstanceFrom(ctx, av, true).toBigInteger();
			bi = XOMIntegerType.instance.makeInstanceFrom(ctx, bv, true).toBigInteger();
			if (ai == null || bi == null) return XOMInteger.NaN;
			return new XOMInteger(ai.or(bi));
		case STR_CONCAT:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			as = av.toTextString(ctx);
			bs = bv.toTextString(ctx);
			return new XOMString(as + bs);
		case STR_CONCAT_SPACE:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			as = av.toTextString(ctx);
			bs = bv.toTextString(ctx);
			return new XOMString(as + " " + bs);
		case LIST_APPEND:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			al = av.toPrimitiveList(ctx);
			Vector<XOMVariant> realbl = new Vector<XOMVariant>();
			realbl.add(bv);
			return new XOMList(al, realbl);
		case LIST_CONCAT:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			al = av.toPrimitiveList(ctx);
			bl = bv.toPrimitiveList(ctx);
			return new XOMList(al, bl);
		case LT_NUM:
			try {
				av = left.evaluate(ctx, vs);
				bv = right.evaluate(ctx, vs);
				return (new XNInterpreter(ctx).compareVariants(av,bv) < 0) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
			} catch (NaNComparisonException nce) {
				return XOMBoolean.FALSE;
			}
		case GT_NUM:
			try {
				av = left.evaluate(ctx, vs);
				bv = right.evaluate(ctx, vs);
				return (new XNInterpreter(ctx).compareVariants(av,bv) > 0) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
			} catch (NaNComparisonException nce) {
				return XOMBoolean.FALSE;
			}
		case LE_NUM:
			try {
				av = left.evaluate(ctx, vs);
				bv = right.evaluate(ctx, vs);
				return (new XNInterpreter(ctx).compareVariants(av,bv) <= 0) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
			} catch (NaNComparisonException nce) {
				return XOMBoolean.FALSE;
			}
		case GE_NUM:
			try {
				av = left.evaluate(ctx, vs);
				bv = right.evaluate(ctx, vs);
				return (new XNInterpreter(ctx).compareVariants(av,bv) >= 0) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
			} catch (NaNComparisonException nce) {
				return XOMBoolean.FALSE;
			}
		case LT_STR:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			as = av.toTextString(ctx);
			bs = bv.toTextString(ctx);
			return (as.compareToIgnoreCase(bs) < 0) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		case GT_STR:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			as = av.toTextString(ctx);
			bs = bv.toTextString(ctx);
			return (as.compareToIgnoreCase(bs) > 0) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		case LE_STR:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			as = av.toTextString(ctx);
			bs = bv.toTextString(ctx);
			return (as.compareToIgnoreCase(bs) <= 0) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		case GE_STR:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			as = av.toTextString(ctx);
			bs = bv.toTextString(ctx);
			return (as.compareToIgnoreCase(bs) >= 0) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		case CONTAINS:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			as = av.toTextString(ctx).toLowerCase();
			bs = bv.toTextString(ctx).toLowerCase();
			return (as.contains(bs)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		case STARTS_WITH:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			as = av.toTextString(ctx).toLowerCase();
			bs = bv.toTextString(ctx).toLowerCase();
			return (as.startsWith(bs)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		case ENDS_WITH:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			as = av.toTextString(ctx).toLowerCase();
			bs = bv.toTextString(ctx).toLowerCase();
			return (as.endsWith(bs)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		case IN:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			as = av.toTextString(ctx).toLowerCase();
			bs = bv.toTextString(ctx).toLowerCase();
			return (bs.contains(as)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		case WITHIN:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			br = XOMRectangleType.instance.makeInstanceFrom(ctx, bv);
			if (XOMRectangleType.instance.canMakeInstanceFrom(ctx, av)) {
				ar = XOMRectangleType.instance.makeInstanceFrom(ctx, av);
				return br.contains(ar) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
			}
			else if (XOMPointType.instance.canMakeInstanceFrom(ctx, av)) {
				ap = XOMPointType.instance.makeInstanceFrom(ctx, av);
				return br.contains(ap) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
			}
			else {
				throw new XOMMorphError("point");
			}
		case ELEMENT_OF:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			bl = bv.toPrimitiveList(ctx);
			for (XOMVariant v : bl) {
				try {
					if (new XNInterpreter(ctx).compareVariants(av,v) == 0) return XOMBoolean.TRUE;
				} catch (NaNComparisonException nce) {
					// nothing
				}
			}
			return XOMBoolean.FALSE;
		case PRECISELY_ELEMENT_OF:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			bl = bv.toPrimitiveList(ctx);
			for (XOMVariant v : bl) {
				if (av.equals(v)) return XOMBoolean.TRUE;
			}
			return XOMBoolean.FALSE;
		case NOT_CONTAINS:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			as = av.toTextString(ctx).toLowerCase();
			bs = bv.toTextString(ctx).toLowerCase();
			return (!as.contains(bs)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		case NOT_STARTS_WITH:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			as = av.toTextString(ctx).toLowerCase();
			bs = bv.toTextString(ctx).toLowerCase();
			return (!as.startsWith(bs)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		case NOT_ENDS_WITH:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			as = av.toTextString(ctx).toLowerCase();
			bs = bv.toTextString(ctx).toLowerCase();
			return (!as.endsWith(bs)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		case NOT_IN:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			as = av.toTextString(ctx).toLowerCase();
			bs = bv.toTextString(ctx).toLowerCase();
			return (!bs.contains(as)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		case NOT_WITHIN:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			br = XOMRectangleType.instance.makeInstanceFrom(ctx, bv);
			if (XOMRectangleType.instance.canMakeInstanceFrom(ctx, av)) {
				ar = XOMRectangleType.instance.makeInstanceFrom(ctx, av);
				return (!br.contains(ar)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
			}
			else if (XOMPointType.instance.canMakeInstanceFrom(ctx, av)) {
				ap = XOMPointType.instance.makeInstanceFrom(ctx, av);
				return (!br.contains(ap)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
			}
			else {
				throw new XOMMorphError("point");
			}
		case NOT_ELEMENT_OF:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			bl = bv.toPrimitiveList(ctx);
			for (XOMVariant v : bl) {
				try {
					if (new XNInterpreter(ctx).compareVariants(av,v) == 0) return XOMBoolean.FALSE;
				} catch (NaNComparisonException nce) {
					// nothing
				}
			}
			return XOMBoolean.TRUE;
		case NOT_PRECISELY_ELEMENT_OF:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			bl = bv.toPrimitiveList(ctx);
			for (XOMVariant v : bl) {
				if (av.equals(v)) return XOMBoolean.FALSE;
			}
			return XOMBoolean.TRUE;
		case EQUAL:
			try {
				av = left.evaluate(ctx, vs);
				bv = right.evaluate(ctx, vs);
				return (new XNInterpreter(ctx).compareVariants(av,bv) == 0) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
			} catch (NaNComparisonException nce) {
				return XOMBoolean.FALSE;
			}
		case STRICT_EQUAL:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			return (av.equals(bv)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		case STRING_EQUAL:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			return (av.toTextString(ctx).equalsIgnoreCase(bv.toTextString(ctx))) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		case NOT_EQUAL:
			try {
				av = left.evaluate(ctx, vs);
				bv = right.evaluate(ctx, vs);
				return (new XNInterpreter(ctx).compareVariants(av,bv) != 0) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
			} catch (NaNComparisonException nce) {
				return XOMBoolean.FALSE;
			}
		case NOT_STRICT_EQUAL:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			return (!av.equals(bv)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		case NOT_STRING_EQUAL:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			return (!av.toTextString(ctx).equalsIgnoreCase(bv.toTextString(ctx))) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		case CMP_NUM:
			try {
				av = left.evaluate(ctx, vs);
				bv = right.evaluate(ctx, vs);
				cmp = new XNInterpreter(ctx).compareVariants(av,bv);
				return (cmp < 0) ? XOMInteger.ONE.negate() : (cmp > 0) ? XOMInteger.ONE : XOMInteger.ZERO;
			} catch (NaNComparisonException nce) {
				return XOMBoolean.FALSE;
			}
		case CMP_STR:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			as = av.toTextString(ctx);
			bs = bv.toTextString(ctx);
			cmp = as.compareToIgnoreCase(bs);
			return (cmp < 0) ? XOMInteger.ONE.negate() : (cmp > 0) ? XOMInteger.ONE : XOMInteger.ZERO;
		case AND:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			ab = XOMBooleanType.instance.makeInstanceFrom(ctx, av).toBoolean();
			bb = XOMBooleanType.instance.makeInstanceFrom(ctx, bv).toBoolean();
			return (ab && bb) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		case SHORT_AND:
			av = left.evaluate(ctx, vs);
			ab = XOMBooleanType.instance.makeInstanceFrom(ctx, av).toBoolean();
			if (!ab) return XOMBoolean.FALSE;
			bv = right.evaluate(ctx, vs);
			bb = XOMBooleanType.instance.makeInstanceFrom(ctx, bv).toBoolean();
			if (!bb) return XOMBoolean.FALSE;
			return XOMBoolean.TRUE;
		case XOR:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			ab = XOMBooleanType.instance.makeInstanceFrom(ctx, av).toBoolean();
			bb = XOMBooleanType.instance.makeInstanceFrom(ctx, bv).toBoolean();
			return (ab != bb) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		case OR:
			av = left.evaluate(ctx, vs);
			bv = right.evaluate(ctx, vs);
			ab = XOMBooleanType.instance.makeInstanceFrom(ctx, av).toBoolean();
			bb = XOMBooleanType.instance.makeInstanceFrom(ctx, bv).toBoolean();
			return (ab || bb) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		case SHORT_OR:
			av = left.evaluate(ctx, vs);
			ab = XOMBooleanType.instance.makeInstanceFrom(ctx, av).toBoolean();
			if (ab) return XOMBoolean.TRUE;
			bv = right.evaluate(ctx, vs);
			bb = XOMBooleanType.instance.makeInstanceFrom(ctx, bv).toBoolean();
			if (bb) return XOMBoolean.TRUE;
			return XOMBoolean.FALSE;
		default:
			throw new XNScriptError("Can't understand this");
		}
	}
}
