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
import java.math.*;
import java.util.*;

import com.kreative.openxion.ast.*;
import com.kreative.openxion.math.*;
import com.kreative.openxion.util.*;
import com.kreative.openxion.xom.*;
import com.kreative.openxion.xom.inst.*;
import com.kreative.openxion.xom.type.*;

/**
 * XNInterpreter is the main class responsible for executing XION code.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XNInterpreter {
	private XNContext context;
	
	public XNInterpreter(XNContext context) {
		this.context = context;
	}
	
	/* * * * * * * *
	 * EXPRESSIONS *
	 * * * * * * * */
	
	public XOMVariant evaluateExpressionString(String s) {
		if (s == null) return XOMEmpty.EMPTY;
		XNLexer lexer = new XNLexer(s, new StringReader(s));
		XNParser parser = new XNParser(context, lexer);
		XNExpression expr = parser.getListExpression(null);
		if (parser.getToken().isEOF()) {
			return evaluateExpression(expr);
		} else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public XOMVariant evaluateExpressionStringOrLiteral(String s) {
		if (s == null) return XOMEmpty.EMPTY;
		XNLexer lexer = new XNLexer(s, new StringReader(s));
		XNParser parser = new XNParser(context, lexer);
		try {
			XNExpression expr = parser.getListExpression(null);
			if (parser.getToken().isEOF()) {
				return evaluateExpression(expr);
			} else {
				return new XOMString(s);
			}
		} catch (XNParseError pe) {
			return new XOMString(s);
		}
	}
	
	public XOMVariant evaluateExpression(XNExpression expr) {
		if (expr == null) return XOMEmpty.EMPTY;
		try {
			if (expr instanceof XNStringExpression) {
				String theString = XIONUtil.unquote(((XNStringExpression)expr).literal.image, context.getTextEncoding());
				return new XOMString(theString);
			}
			else if (expr instanceof XNNumberExpression) {
				String theNumber = ((XNNumberExpression)expr).literal.image;
				try {
					BigDecimal d = new BigDecimal(theNumber.replace("''", "E-").replace("'", "E+"));
					try {
						BigInteger i = d.toBigIntegerExact();
						return new XOMInteger(i);
					} catch (Exception e) {
						return new XOMNumber(d);
					}
				} catch (Exception e) {
					return new XOMString(theNumber);
				}
			}
			else if (expr instanceof XNEmptyExpression) {
				return XOMEmpty.EMPTY;
			}
			else if (expr instanceof XNConstantExpression) {
				String constName = ((XNConstantExpression)expr).constant.image;
				XOMVariant theConstant = context.getConstant(constName);
				if (theConstant != null) return theConstant;
				else throw new XNScriptError("The constant "+constName+" is not defined");
			}
			else if (expr instanceof XNMeExpression) {
				XNResponder resp = context.getCurrentResponder();
				if (resp instanceof XOMVariant) return (XOMVariant)resp;
				else return XOMInterpreter.INTERPRETER;
			}
			else if (expr instanceof XNSuperExpression) {
				XNResponder resp = context.getCurrentResponder();
				if (resp instanceof XOMUserObject) return ((XOMUserObject)resp).asSuper();
				else if (resp instanceof XOMVariant) return (XOMVariant)resp;
				else return XOMInterpreter.INTERPRETER;
			}
			else if (expr instanceof XNUnaryExpression) {
				XNOperator op = ((XNUnaryExpression)expr).operator;
				XNExpression a = ((XNUnaryExpression)expr).argument;
				XOMVariant av;
				boolean ab;
				BigInteger ai;
				switch (op) {
				case NOT:
					av = evaluateExpression(a).asPrimitive(context);
					ab = XOMBooleanType.instance.makeInstanceFrom(context, av).toBoolean();
					return ab ? XOMBoolean.FALSE : XOMBoolean.TRUE;
				case UNARY_SUBTRACT:
					av = evaluateExpression(a).asPrimitive(context);
					if (av instanceof XOMInteger) {
						return ((XOMInteger)av).negate();
					}
					else if (av instanceof XOMNumber) {
						return ((XOMNumber)av).negate();
					}
					else if (av instanceof XOMComplex) {
						return ((XOMComplex)av).negate();
					}
					else if (XOMIntegerType.instance.canMakeInstanceFrom(context, av, true)) {
						XOMInteger i = XOMIntegerType.instance.makeInstanceFrom(context, av, true);
						return i.negate();
					}
					else if (XOMNumberType.instance.canMakeInstanceFrom(context, av, true)) {
						XOMNumber n = XOMNumberType.instance.makeInstanceFrom(context, av, true);
						return n.negate();
					}
					else if (XOMComplexType.instance.canMakeInstanceFrom(context, av, true)) {
						XOMComplex c = XOMComplexType.instance.makeInstanceFrom(context, av, true);
						return c.negate();
					}
					else {
						throw new XOMMorphError("number");
					}
				case BIT_NOT:
					av = evaluateExpression(a).asPrimitive(context);
					ai = XOMIntegerType.instance.makeInstanceFrom(context, av, true).toBigInteger();
					if (ai == null) return XOMInteger.NaN;
					return new XOMInteger(ai.not());
				case EXISTS:
					if (a instanceof XNVariantDescriptor) {
						return variantExists((XNVariantDescriptor)a) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
					} else {
						throw new XNScriptError("Can't understand this");
					}
				case NOT_EXISTS:
					if (a instanceof XNVariantDescriptor) {
						return variantExists((XNVariantDescriptor)a) ? XOMBoolean.FALSE : XOMBoolean.TRUE;
					} else {
						throw new XNScriptError("Can't understand this");
					}
				case REFERENCE_TO:
					return new XOMReference(evaluateExpression(a));
				case REFERENT_OF:
					av = evaluateExpression(a).asPrimitive(context);
					return XOMReferenceType.instance.makeInstanceFrom(context, av).dereference(true);
				default:
					throw new XNScriptError("Can't understand this");
				}
			}
			else if (expr instanceof XNBinaryExpression) {
				XNOperator op = ((XNBinaryExpression)expr).operator;
				XNExpression a = ((XNBinaryExpression)expr).left;
				XNExpression b = ((XNBinaryExpression)expr).right;
				MathContext mc = context.getMathContext();
				MathProcessor mp = context.getMathProcessor();
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
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					if (av instanceof XOMComplex && bv instanceof XOMComplex) {
						ac = XOMComplexType.instance.makeInstanceFrom(context, av, true);
						bc = XOMComplexType.instance.makeInstanceFrom(context, bv, true);
						return XOMComplexMath.pow(ac, bc, mc, mp);
					}
					else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
						an = XOMNumberType.instance.makeInstanceFrom(context, av, true);
						bn = XOMNumberType.instance.makeInstanceFrom(context, bv, true);
						return XOMNumberMath.pow(an, bn, mc, mp);
					}
					else if (XOMNumberType.instance.canMakeInstanceFrom(context, av, true) && XOMNumberType.instance.canMakeInstanceFrom(context, bv, true)) {
						an = XOMNumberType.instance.makeInstanceFrom(context, av, true);
						bn = XOMNumberType.instance.makeInstanceFrom(context, bv, true);
						return XOMNumberMath.pow(an, bn, mc, mp);
					}
					else if (XOMComplexType.instance.canMakeInstanceFrom(context, av, true) && XOMComplexType.instance.canMakeInstanceFrom(context, bv, true)) {
						ac = XOMComplexType.instance.makeInstanceFrom(context, av, true);
						bc = XOMComplexType.instance.makeInstanceFrom(context, bv, true);
						return XOMComplexMath.pow(ac, bc, mc, mp);
					}
					else {
						throw new XOMMorphError("number");
					}
				case MULTIPLY:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					if (av instanceof XOMComplex && bv instanceof XOMComplex) {
						ac = XOMComplexType.instance.makeInstanceFrom(context, av, true);
						bc = XOMComplexType.instance.makeInstanceFrom(context, bv, true);
						return XOMComplexMath.multiply(ac, bc, mc, mp);
					}
					else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
						an = XOMNumberType.instance.makeInstanceFrom(context, av, true);
						bn = XOMNumberType.instance.makeInstanceFrom(context, bv, true);
						return XOMNumberMath.multiply(an, bn, mc, mp);
					}
					else if (XOMNumberType.instance.canMakeInstanceFrom(context, av, true) && XOMNumberType.instance.canMakeInstanceFrom(context, bv, true)) {
						an = XOMNumberType.instance.makeInstanceFrom(context, av, true);
						bn = XOMNumberType.instance.makeInstanceFrom(context, bv, true);
						return XOMNumberMath.multiply(an, bn, mc, mp);
					}
					else if (XOMComplexType.instance.canMakeInstanceFrom(context, av, true) && XOMComplexType.instance.canMakeInstanceFrom(context, bv, true)) {
						ac = XOMComplexType.instance.makeInstanceFrom(context, av, true);
						bc = XOMComplexType.instance.makeInstanceFrom(context, bv, true);
						return XOMComplexMath.multiply(ac, bc, mc, mp);
					}
					else {
						throw new XOMMorphError("number");
					}
				case DIVIDE:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					if (av instanceof XOMComplex && bv instanceof XOMComplex) {
						ac = XOMComplexType.instance.makeInstanceFrom(context, av, true);
						bc = XOMComplexType.instance.makeInstanceFrom(context, bv, true);
						return XOMComplexMath.divide(ac, bc, mc, mp);
					}
					else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
						an = XOMNumberType.instance.makeInstanceFrom(context, av, true);
						bn = XOMNumberType.instance.makeInstanceFrom(context, bv, true);
						return XOMNumberMath.divide(an, bn, mc, mp);
					}
					else if (XOMNumberType.instance.canMakeInstanceFrom(context, av, true) && XOMNumberType.instance.canMakeInstanceFrom(context, bv, true)) {
						an = XOMNumberType.instance.makeInstanceFrom(context, av, true);
						bn = XOMNumberType.instance.makeInstanceFrom(context, bv, true);
						return XOMNumberMath.divide(an, bn, mc, mp);
					}
					else if (XOMComplexType.instance.canMakeInstanceFrom(context, av, true) && XOMComplexType.instance.canMakeInstanceFrom(context, bv, true)) {
						ac = XOMComplexType.instance.makeInstanceFrom(context, av, true);
						bc = XOMComplexType.instance.makeInstanceFrom(context, bv, true);
						return XOMComplexMath.divide(ac, bc, mc, mp);
					}
					else {
						throw new XOMMorphError("number");
					}
				case QUOT:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					if (av instanceof XOMComplex && bv instanceof XOMComplex) {
						ac = XOMComplexType.instance.makeInstanceFrom(context, av, true);
						bc = XOMComplexType.instance.makeInstanceFrom(context, bv, true);
						return XOMComplexMath.divide(ac, bc, mc, mp).trunc();
					}
					else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
						an = XOMNumberType.instance.makeInstanceFrom(context, av, true);
						bn = XOMNumberType.instance.makeInstanceFrom(context, bv, true);
						return XOMNumberMath.divide(an, bn, mc, mp).trunc();
					}
					else if (XOMNumberType.instance.canMakeInstanceFrom(context, av, true) && XOMNumberType.instance.canMakeInstanceFrom(context, bv, true)) {
						an = XOMNumberType.instance.makeInstanceFrom(context, av, true);
						bn = XOMNumberType.instance.makeInstanceFrom(context, bv, true);
						return XOMNumberMath.divide(an, bn, mc, mp).trunc();
					}
					else if (XOMComplexType.instance.canMakeInstanceFrom(context, av, true) && XOMComplexType.instance.canMakeInstanceFrom(context, bv, true)) {
						ac = XOMComplexType.instance.makeInstanceFrom(context, av, true);
						bc = XOMComplexType.instance.makeInstanceFrom(context, bv, true);
						return XOMComplexMath.divide(ac, bc, mc, mp).trunc();
					}
					else {
						throw new XOMMorphError("number");
					}
				case REM:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					if (av instanceof XOMComplex && bv instanceof XOMComplex) {
						ac = XOMComplexType.instance.makeInstanceFrom(context, av, true);
						bc = XOMComplexType.instance.makeInstanceFrom(context, bv, true);
						return XOMComplexMath.subtract(ac, XOMComplexMath.multiply(bc, XOMComplexMath.divide(ac, bc, mc, mp).trunc(), mc, mp), mc, mp);
					}
					else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
						an = XOMNumberType.instance.makeInstanceFrom(context, av, true);
						bn = XOMNumberType.instance.makeInstanceFrom(context, bv, true);
						return XOMNumberMath.subtract(an, XOMNumberMath.multiply(bn, XOMNumberMath.divide(an, bn, mc, mp).trunc(), mc, mp), mc, mp);
					}
					else if (XOMNumberType.instance.canMakeInstanceFrom(context, av, true) && XOMNumberType.instance.canMakeInstanceFrom(context, bv, true)) {
						an = XOMNumberType.instance.makeInstanceFrom(context, av, true);
						bn = XOMNumberType.instance.makeInstanceFrom(context, bv, true);
						return XOMNumberMath.subtract(an, XOMNumberMath.multiply(bn, XOMNumberMath.divide(an, bn, mc, mp).trunc(), mc, mp), mc, mp);
					}
					else if (XOMComplexType.instance.canMakeInstanceFrom(context, av, true) && XOMComplexType.instance.canMakeInstanceFrom(context, bv, true)) {
						ac = XOMComplexType.instance.makeInstanceFrom(context, av, true);
						bc = XOMComplexType.instance.makeInstanceFrom(context, bv, true);
						return XOMComplexMath.subtract(ac, XOMComplexMath.multiply(bc, XOMComplexMath.divide(ac, bc, mc, mp).trunc(), mc, mp), mc, mp);
					}
					else {
						throw new XOMMorphError("number");
					}
				case DIV:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					if (av instanceof XOMComplex && bv instanceof XOMComplex) {
						ac = XOMComplexType.instance.makeInstanceFrom(context, av, true);
						bc = XOMComplexType.instance.makeInstanceFrom(context, bv, true);
						return XOMComplexMath.divide(ac, bc, mc, mp).floor();
					}
					else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
						an = XOMNumberType.instance.makeInstanceFrom(context, av, true);
						bn = XOMNumberType.instance.makeInstanceFrom(context, bv, true);
						return XOMNumberMath.divide(an, bn, mc, mp).floor();
					}
					else if (XOMNumberType.instance.canMakeInstanceFrom(context, av, true) && XOMNumberType.instance.canMakeInstanceFrom(context, bv, true)) {
						an = XOMNumberType.instance.makeInstanceFrom(context, av, true);
						bn = XOMNumberType.instance.makeInstanceFrom(context, bv, true);
						return XOMNumberMath.divide(an, bn, mc, mp).floor();
					}
					else if (XOMComplexType.instance.canMakeInstanceFrom(context, av, true) && XOMComplexType.instance.canMakeInstanceFrom(context, bv, true)) {
						ac = XOMComplexType.instance.makeInstanceFrom(context, av, true);
						bc = XOMComplexType.instance.makeInstanceFrom(context, bv, true);
						return XOMComplexMath.divide(ac, bc, mc, mp).floor();
					}
					else {
						throw new XOMMorphError("number");
					}
				case MOD:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					if (av instanceof XOMComplex && bv instanceof XOMComplex) {
						ac = XOMComplexType.instance.makeInstanceFrom(context, av, true);
						bc = XOMComplexType.instance.makeInstanceFrom(context, bv, true);
						return XOMComplexMath.subtract(ac, XOMComplexMath.multiply(bc, XOMComplexMath.divide(ac, bc, mc, mp).floor(), mc, mp), mc, mp);
					}
					else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
						an = XOMNumberType.instance.makeInstanceFrom(context, av, true);
						bn = XOMNumberType.instance.makeInstanceFrom(context, bv, true);
						return XOMNumberMath.subtract(an, XOMNumberMath.multiply(bn, XOMNumberMath.divide(an, bn, mc, mp).floor(), mc, mp), mc, mp);
					}
					else if (XOMNumberType.instance.canMakeInstanceFrom(context, av, true) && XOMNumberType.instance.canMakeInstanceFrom(context, bv, true)) {
						an = XOMNumberType.instance.makeInstanceFrom(context, av, true);
						bn = XOMNumberType.instance.makeInstanceFrom(context, bv, true);
						return XOMNumberMath.subtract(an, XOMNumberMath.multiply(bn, XOMNumberMath.divide(an, bn, mc, mp).floor(), mc, mp), mc, mp);
					}
					else if (XOMComplexType.instance.canMakeInstanceFrom(context, av, true) && XOMComplexType.instance.canMakeInstanceFrom(context, bv, true)) {
						ac = XOMComplexType.instance.makeInstanceFrom(context, av, true);
						bc = XOMComplexType.instance.makeInstanceFrom(context, bv, true);
						return XOMComplexMath.subtract(ac, XOMComplexMath.multiply(bc, XOMComplexMath.divide(ac, bc, mc, mp).floor(), mc, mp), mc, mp);
					}
					else {
						throw new XOMMorphError("number");
					}
				case ADD:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					if (av instanceof XOMComplex && bv instanceof XOMComplex) {
						ac = XOMComplexType.instance.makeInstanceFrom(context, av, true);
						bc = XOMComplexType.instance.makeInstanceFrom(context, bv, true);
						return XOMComplexMath.add(ac, bc, mc, mp);
					}
					else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
						an = XOMNumberType.instance.makeInstanceFrom(context, av, true);
						bn = XOMNumberType.instance.makeInstanceFrom(context, bv, true);
						return XOMNumberMath.add(an, bn, mc, mp);
					}
					else if (XOMNumberType.instance.canMakeInstanceFrom(context, av, true) && XOMNumberType.instance.canMakeInstanceFrom(context, bv, true)) {
						an = XOMNumberType.instance.makeInstanceFrom(context, av, true);
						bn = XOMNumberType.instance.makeInstanceFrom(context, bv, true);
						return XOMNumberMath.add(an, bn, mc, mp);
					}
					else if (XOMComplexType.instance.canMakeInstanceFrom(context, av, true) && XOMComplexType.instance.canMakeInstanceFrom(context, bv, true)) {
						ac = XOMComplexType.instance.makeInstanceFrom(context, av, true);
						bc = XOMComplexType.instance.makeInstanceFrom(context, bv, true);
						return XOMComplexMath.add(ac, bc, mc, mp);
					}
					else {
						throw new XOMMorphError("number");
					}
				case SUBTRACT:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					if (av instanceof XOMComplex && bv instanceof XOMComplex) {
						ac = XOMComplexType.instance.makeInstanceFrom(context, av, true);
						bc = XOMComplexType.instance.makeInstanceFrom(context, bv, true);
						return XOMComplexMath.subtract(ac, bc, mc, mp);
					}
					else if (av instanceof XOMNumber && bv instanceof XOMNumber) {
						an = XOMNumberType.instance.makeInstanceFrom(context, av, true);
						bn = XOMNumberType.instance.makeInstanceFrom(context, bv, true);
						return XOMNumberMath.subtract(an, bn, mc, mp);
					}
					else if (XOMNumberType.instance.canMakeInstanceFrom(context, av, true) && XOMNumberType.instance.canMakeInstanceFrom(context, bv, true)) {
						an = XOMNumberType.instance.makeInstanceFrom(context, av, true);
						bn = XOMNumberType.instance.makeInstanceFrom(context, bv, true);
						return XOMNumberMath.subtract(an, bn, mc, mp);
					}
					else if (XOMComplexType.instance.canMakeInstanceFrom(context, av, true) && XOMComplexType.instance.canMakeInstanceFrom(context, bv, true)) {
						ac = XOMComplexType.instance.makeInstanceFrom(context, av, true);
						bc = XOMComplexType.instance.makeInstanceFrom(context, bv, true);
						return XOMComplexMath.subtract(ac, bc, mc, mp);
					}
					else {
						throw new XOMMorphError("number");
					}
				case SHIFT_LEFT:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					ai = XOMIntegerType.instance.makeInstanceFrom(context, av, true).toBigInteger();
					bi = XOMIntegerType.instance.makeInstanceFrom(context, bv, true).toBigInteger();
					if (ai == null || bi == null) return XOMInteger.NaN;
					return new XOMInteger(ai.shiftLeft(bi.intValue()));
				case SHIFT_RIGHT_SIGNED:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					ai = XOMIntegerType.instance.makeInstanceFrom(context, av, true).toBigInteger();
					bi = XOMIntegerType.instance.makeInstanceFrom(context, bv, true).toBigInteger();
					if (ai == null || bi == null) return XOMInteger.NaN;
					return new XOMInteger(ai.shiftRight(bi.intValue()));
				case SHIFT_RIGHT_UNSIGNED:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					ai = XOMIntegerType.instance.makeInstanceFrom(context, av, true).toBigInteger();
					bi = XOMIntegerType.instance.makeInstanceFrom(context, bv, true).toBigInteger();
					if (ai == null || bi == null) return XOMInteger.NaN;
					return new XOMInteger(ai.shiftRight(bi.intValue()));
				case BIT_AND:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					ai = XOMIntegerType.instance.makeInstanceFrom(context, av, true).toBigInteger();
					bi = XOMIntegerType.instance.makeInstanceFrom(context, bv, true).toBigInteger();
					if (ai == null || bi == null) return XOMInteger.NaN;
					return new XOMInteger(ai.and(bi));
				case BIT_XOR:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					ai = XOMIntegerType.instance.makeInstanceFrom(context, av, true).toBigInteger();
					bi = XOMIntegerType.instance.makeInstanceFrom(context, bv, true).toBigInteger();
					if (ai == null || bi == null) return XOMInteger.NaN;
					return new XOMInteger(ai.xor(bi));
				case BIT_OR:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					ai = XOMIntegerType.instance.makeInstanceFrom(context, av, true).toBigInteger();
					bi = XOMIntegerType.instance.makeInstanceFrom(context, bv, true).toBigInteger();
					if (ai == null || bi == null) return XOMInteger.NaN;
					return new XOMInteger(ai.or(bi));
				case STR_CONCAT:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					as = av.toTextString(context);
					bs = bv.toTextString(context);
					return new XOMString(as + bs);
				case STR_CONCAT_SPACE:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					as = av.toTextString(context);
					bs = bv.toTextString(context);
					return new XOMString(as + " " + bs);
				case LIST_APPEND:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					al = av.toPrimitiveList(context);
					Vector<XOMVariant> realbl = new Vector<XOMVariant>();
					realbl.add(bv);
					return new XOMList(al, realbl);
				case LIST_CONCAT:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					al = av.toPrimitiveList(context);
					bl = bv.toPrimitiveList(context);
					return new XOMList(al, bl);
				case LT_NUM:
					try {
						av = evaluateExpression(a).asPrimitive(context);
						bv = evaluateExpression(b).asPrimitive(context);
						return (compareVariants(av,bv) < 0) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
					} catch (NaNComparisonException nce) {
						return XOMBoolean.FALSE;
					}
				case GT_NUM:
					try {
						av = evaluateExpression(a).asPrimitive(context);
						bv = evaluateExpression(b).asPrimitive(context);
						return (compareVariants(av,bv) > 0) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
					} catch (NaNComparisonException nce) {
						return XOMBoolean.FALSE;
					}
				case LE_NUM:
					try {
						av = evaluateExpression(a).asPrimitive(context);
						bv = evaluateExpression(b).asPrimitive(context);
						return (compareVariants(av,bv) <= 0) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
					} catch (NaNComparisonException nce) {
						return XOMBoolean.FALSE;
					}
				case GE_NUM:
					try {
						av = evaluateExpression(a).asPrimitive(context);
						bv = evaluateExpression(b).asPrimitive(context);
						return (compareVariants(av,bv) >= 0) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
					} catch (NaNComparisonException nce) {
						return XOMBoolean.FALSE;
					}
				case LT_STR:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					as = av.toTextString(context);
					bs = bv.toTextString(context);
					return (as.compareToIgnoreCase(bs) < 0) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
				case GT_STR:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					as = av.toTextString(context);
					bs = bv.toTextString(context);
					return (as.compareToIgnoreCase(bs) > 0) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
				case LE_STR:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					as = av.toTextString(context);
					bs = bv.toTextString(context);
					return (as.compareToIgnoreCase(bs) <= 0) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
				case GE_STR:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					as = av.toTextString(context);
					bs = bv.toTextString(context);
					return (as.compareToIgnoreCase(bs) >= 0) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
				case CONTAINS:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					as = av.toTextString(context).toLowerCase();
					bs = bv.toTextString(context).toLowerCase();
					return (as.contains(bs)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
				case STARTS_WITH:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					as = av.toTextString(context).toLowerCase();
					bs = bv.toTextString(context).toLowerCase();
					return (as.startsWith(bs)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
				case ENDS_WITH:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					as = av.toTextString(context).toLowerCase();
					bs = bv.toTextString(context).toLowerCase();
					return (as.endsWith(bs)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
				case IN:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					as = av.toTextString(context).toLowerCase();
					bs = bv.toTextString(context).toLowerCase();
					return (bs.contains(as)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
				case WITHIN:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					br = XOMRectangleType.instance.makeInstanceFrom(context, bv);
					if (XOMRectangleType.instance.canMakeInstanceFrom(context, av)) {
						ar = XOMRectangleType.instance.makeInstanceFrom(context, av);
						return br.contains(ar) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
					}
					else if (XOMPointType.instance.canMakeInstanceFrom(context, av)) {
						ap = XOMPointType.instance.makeInstanceFrom(context, av);
						return br.contains(ap) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
					}
					else {
						throw new XOMMorphError("point");
					}
				case ELEMENT_OF:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					bl = bv.toPrimitiveList(context);
					for (XOMVariant v : bl) {
						try {
							if (compareVariants(av,v) == 0) return XOMBoolean.TRUE;
						} catch (NaNComparisonException nce) {
							// nothing
						}
					}
					return XOMBoolean.FALSE;
				case PRECISELY_ELEMENT_OF:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					bl = bv.toPrimitiveList(context);
					for (XOMVariant v : bl) {
						if (av.equals(v)) return XOMBoolean.TRUE;
					}
					return XOMBoolean.FALSE;
				case NOT_CONTAINS:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					as = av.toTextString(context).toLowerCase();
					bs = bv.toTextString(context).toLowerCase();
					return (!as.contains(bs)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
				case NOT_STARTS_WITH:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					as = av.toTextString(context).toLowerCase();
					bs = bv.toTextString(context).toLowerCase();
					return (!as.startsWith(bs)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
				case NOT_ENDS_WITH:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					as = av.toTextString(context).toLowerCase();
					bs = bv.toTextString(context).toLowerCase();
					return (!as.endsWith(bs)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
				case NOT_IN:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					as = av.toTextString(context).toLowerCase();
					bs = bv.toTextString(context).toLowerCase();
					return (!bs.contains(as)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
				case NOT_WITHIN:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					br = XOMRectangleType.instance.makeInstanceFrom(context, bv);
					if (XOMRectangleType.instance.canMakeInstanceFrom(context, av)) {
						ar = XOMRectangleType.instance.makeInstanceFrom(context, av);
						return (!br.contains(ar)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
					}
					else if (XOMPointType.instance.canMakeInstanceFrom(context, av)) {
						ap = XOMPointType.instance.makeInstanceFrom(context, av);
						return (!br.contains(ap)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
					}
					else {
						throw new XOMMorphError("point");
					}
				case NOT_ELEMENT_OF:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					bl = bv.toPrimitiveList(context);
					for (XOMVariant v : bl) {
						try {
							if (compareVariants(av,v) == 0) return XOMBoolean.FALSE;
						} catch (NaNComparisonException nce) {
							// nothing
						}
					}
					return XOMBoolean.TRUE;
				case NOT_PRECISELY_ELEMENT_OF:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					bl = bv.toPrimitiveList(context);
					for (XOMVariant v : bl) {
						if (av.equals(v)) return XOMBoolean.FALSE;
					}
					return XOMBoolean.TRUE;
				case EQUAL:
					try {
						av = evaluateExpression(a).asPrimitive(context);
						bv = evaluateExpression(b).asPrimitive(context);
						return (compareVariants(av,bv) == 0) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
					} catch (NaNComparisonException nce) {
						return XOMBoolean.FALSE;
					}
				case STRICT_EQUAL:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					return (av.equals(bv)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
				case STRING_EQUAL:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					return (av.toTextString(context).equalsIgnoreCase(bv.toTextString(context))) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
				case NOT_EQUAL:
					try {
						av = evaluateExpression(a).asPrimitive(context);
						bv = evaluateExpression(b).asPrimitive(context);
						return (compareVariants(av,bv) != 0) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
					} catch (NaNComparisonException nce) {
						return XOMBoolean.FALSE;
					}
				case NOT_STRICT_EQUAL:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					return (!av.equals(bv)) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
				case NOT_STRING_EQUAL:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					return (!av.toTextString(context).equalsIgnoreCase(bv.toTextString(context))) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
				case CMP_NUM:
					try {
						av = evaluateExpression(a).asPrimitive(context);
						bv = evaluateExpression(b).asPrimitive(context);
						cmp = compareVariants(av,bv);
						return (cmp < 0) ? XOMInteger.ONE.negate() : (cmp > 0) ? XOMInteger.ONE : XOMInteger.ZERO;
					} catch (NaNComparisonException nce) {
						return XOMBoolean.FALSE;
					}
				case CMP_STR:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					as = av.toTextString(context);
					bs = bv.toTextString(context);
					cmp = as.compareToIgnoreCase(bs);
					return (cmp < 0) ? XOMInteger.ONE.negate() : (cmp > 0) ? XOMInteger.ONE : XOMInteger.ZERO;
				case AND:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					ab = XOMBooleanType.instance.makeInstanceFrom(context, av).toBoolean();
					bb = XOMBooleanType.instance.makeInstanceFrom(context, bv).toBoolean();
					return (ab && bb) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
				case SHORT_AND:
					av = evaluateExpression(a).asPrimitive(context);
					ab = XOMBooleanType.instance.makeInstanceFrom(context, av).toBoolean();
					if (!ab) return XOMBoolean.FALSE;
					bv = evaluateExpression(b).asPrimitive(context);
					bb = XOMBooleanType.instance.makeInstanceFrom(context, bv).toBoolean();
					if (!bb) return XOMBoolean.FALSE;
					return XOMBoolean.TRUE;
				case XOR:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					ab = XOMBooleanType.instance.makeInstanceFrom(context, av).toBoolean();
					bb = XOMBooleanType.instance.makeInstanceFrom(context, bv).toBoolean();
					return (ab != bb) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
				case OR:
					av = evaluateExpression(a).asPrimitive(context);
					bv = evaluateExpression(b).asPrimitive(context);
					ab = XOMBooleanType.instance.makeInstanceFrom(context, av).toBoolean();
					bb = XOMBooleanType.instance.makeInstanceFrom(context, bv).toBoolean();
					return (ab || bb) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
				case SHORT_OR:
					av = evaluateExpression(a).asPrimitive(context);
					ab = XOMBooleanType.instance.makeInstanceFrom(context, av).toBoolean();
					if (ab) return XOMBoolean.TRUE;
					bv = evaluateExpression(b).asPrimitive(context);
					bb = XOMBooleanType.instance.makeInstanceFrom(context, bv).toBoolean();
					if (bb) return XOMBoolean.TRUE;
					return XOMBoolean.FALSE;
				default:
					throw new XNScriptError("Can't understand this");
				}
			}
			else if (expr instanceof XNInstanceOfExpression) {
				XNOperator op = ((XNInstanceOfExpression)expr).operator;
				String dtn = ((XNInstanceOfExpression)expr).datatype.toNameString();
				XOMDataType<? extends XOMVariant> dt = context.getDataType(dtn);
				XNExpression a = ((XNInstanceOfExpression)expr).argument;
				XOMVariant av = evaluateExpression(a).asValue(context);
				switch (op) {
				case IS_A:
					if (dt == null) return XOMBoolean.FALSE;
					else if (dt.canMakeInstanceFrom(context, av)) return XOMBoolean.TRUE;
					else return XOMBoolean.FALSE;
				case IS_PRECISELY_A:
					if (dt == null) return XOMBoolean.FALSE;
					else if (dt.getInstanceClass().isAssignableFrom(av.getClass())) return XOMBoolean.TRUE;
					else return XOMBoolean.FALSE;
				case IS_NOT_A:
					if (dt == null) return XOMBoolean.TRUE;
					else if (dt.canMakeInstanceFrom(context, av)) return XOMBoolean.FALSE;
					else return XOMBoolean.TRUE;
				case IS_NOT_PRECISELY_A:
					if (dt == null) return XOMBoolean.TRUE;
					else if (dt.getInstanceClass().isAssignableFrom(av.getClass())) return XOMBoolean.FALSE;
					else return XOMBoolean.TRUE;
				case AS:
					if (dt == null) throw new XNScriptError("Unknown variant type");
					else return dt.makeInstanceFrom(context, av);
				default:
					throw new XNScriptError("Can't understand this");
				}
			}
			else if (expr instanceof XNBetweenExpression) {
				try {
					XOMVariant l = evaluateExpression(((XNBetweenExpression)expr).left).asPrimitive(context);
					XNOperator op = ((XNBetweenExpression)expr).operator;
					XOMVariant rs = evaluateExpression(((XNBetweenExpression)expr).rightStart).asPrimitive(context);
					XOMVariant re = evaluateExpression(((XNBetweenExpression)expr).rightEnd).asPrimitive(context);
					int cs = compareVariants(rs, l);
					int ce = compareVariants(l, re);
					switch (op) {
					case BETWEEN:
						if (cs < 0 && ce < 0) return XOMBoolean.TRUE;
						else if (cs == 0 && ((XNBetweenExpression)expr).isLeftInclusive()) return XOMBoolean.TRUE;
						else if (ce == 0 && ((XNBetweenExpression)expr).isRightInclusive()) return XOMBoolean.TRUE;
						else return XOMBoolean.FALSE;
					case NOT_BETWEEN:
						if (cs < 0 && ce < 0) return XOMBoolean.FALSE;
						else if (cs == 0 && ((XNBetweenExpression)expr).isLeftInclusive()) return XOMBoolean.FALSE;
						else if (ce == 0 && ((XNBetweenExpression)expr).isRightInclusive()) return XOMBoolean.FALSE;
						else return XOMBoolean.TRUE;
					default:
						throw new XNScriptError("Can't understand this");
					}
				} catch (NaNComparisonException nce) {
					return XOMBoolean.FALSE;
				}
			}
			else if (expr instanceof XNIfExpression) {
				XNExpression c = ((XNIfExpression)expr).condition;
				XNExpression t = ((XNIfExpression)expr).trueCase;
				XNExpression f = ((XNIfExpression)expr).falseCase;
				if (XOMBooleanType.instance.makeInstanceFrom(context, evaluateExpression(c).asPrimitive(context)).toBoolean()) {
					return evaluateExpression(t);
				} else {
					return evaluateExpression(f);
				}
			}
			else if (expr instanceof XNListExpression) {
				List<XOMVariant> theList = new Vector<XOMVariant>();
				for (XNExpression e : ((XNListExpression)expr).exprs) {
					theList.add(evaluateExpression(e).asPrimitive(context));
				}
				return new XOMList(theList);
			}
			else if (expr instanceof XNDictionaryExpression) {
				XNDictionaryExpression de = (XNDictionaryExpression)expr;
				Map<String, XOMVariant> theMap = new LinkedHashMap<String, XOMVariant>();
				for (int i = 0; i < de.keyExprs.size() && i < de.valueExprs.size(); i++) {
					String k = evaluateExpression(de.keyExprs.get(i)).toTextString(context);
					XOMVariant v = evaluateExpression(de.valueExprs.get(i)).asPrimitive(context);
					theMap.put(k, v);
				}
				return new XOMDictionary(theMap);
			}
			else if (expr instanceof XNVariableExpression) {
				String name = ((XNVariableExpression)expr).varname.image;
				return new XOMVariable(context.getVariableMap(name), name);
			}
			else if (expr instanceof XNNewExpression) {
				XNDataType dataTypeObj = ((XNNewExpression)expr).datatype;
				String dataTypeStr = dataTypeObj.toNameString();
				XOMDataType<? extends XOMVariant> dataType = context.getDataType(dataTypeStr);
				XNExpression parentExpression = ((XNNewExpression)expr).parentVariant;
				XOMVariant parent = (parentExpression == null) ? null : evaluateExpression(parentExpression);
				if (parent != null) {
					return dataType.createChildVariant(context, parent);
				} else {
					return dataType.createInstance(context);
				}
			}
			else if (expr instanceof XNVariantDescriptor) {
				XNDataType dataTypeObj = ((XNVariantDescriptor)expr).datatype;
				String dataTypeStr = dataTypeObj.toNameString();
				XOMDataType<? extends XOMVariant> dataType = context.getDataType(dataTypeStr);
				XNExpression parentExpression = ((XNVariantDescriptor)expr).parentVariant;
				XOMVariant parent = (parentExpression == null) ? null : evaluateExpression(parentExpression);
				if (expr instanceof XNVariantIdDescriptor) {
					XNExpression idExpression = ((XNVariantIdDescriptor)expr).id;
					XOMVariant idVar = evaluateExpression(idExpression).asPrimitive(context);
					XOMInteger idInt = XOMIntegerType.instance.makeInstanceFrom(context, idVar, true);
					if (parent != null) {
						return dataType.getChildVariantByID(context, parent, idInt.toInt());
					} else {
						return dataType.getInstanceByID(context, idInt.toInt());
					}
				} else if (expr instanceof XNVariantIndexNameDescriptor) {
					XNExpression startExpr = ((XNVariantIndexNameDescriptor)expr).start;
					XNExpression endExpr = ((XNVariantIndexNameDescriptor)expr).end;
					if (startExpr != null && endExpr != null) {
						int start = XOMIntegerType.instance.makeInstanceFrom(context, evaluateExpression(startExpr).asPrimitive(context), true).toInt();
						int end = XOMIntegerType.instance.makeInstanceFrom(context, evaluateExpression(endExpr).asPrimitive(context), true).toInt();
						if (parent != null) {
							return dataType.getChildVariantByIndex(context, parent, start, end);
						} else {
							return dataType.getInstanceByIndex(context, start, end);
						}
					} else if (startExpr != null) {
						XOMVariant idxNameVar = evaluateExpression(startExpr).asPrimitive(context);
						if (!idxNameVar.toTextString(context).equals("") && XOMIntegerType.instance.canMakeInstanceFrom(context, idxNameVar, true)) {
							int index = XOMIntegerType.instance.makeInstanceFrom(context, idxNameVar, true).toInt();
							if (parent != null) {
								return dataType.getChildVariantByIndex(context, parent, index);
							} else {
								return dataType.getInstanceByIndex(context, index);
							}
						} else {
							String name = idxNameVar.toTextString(context);
							if (parent != null) {
								return dataType.getChildVariantByName(context, parent, name);
							} else {
								return dataType.getInstanceByName(context, name);
							}
						}
					} else if (endExpr != null) {
						XOMVariant idxNameVar = evaluateExpression(endExpr).asPrimitive(context);
						if (!idxNameVar.toTextString(context).equals("") && XOMIntegerType.instance.canMakeInstanceFrom(context, idxNameVar, true)) {
							int index = XOMIntegerType.instance.makeInstanceFrom(context, idxNameVar, true).toInt();
							if (parent != null) {
								return dataType.getChildVariantByIndex(context, parent, index);
							} else {
								return dataType.getInstanceByIndex(context, index);
							}
						} else {
							String name = idxNameVar.toTextString(context);
							if (parent != null) {
								return dataType.getChildVariantByName(context, parent, name);
							} else {
								return dataType.getInstanceByName(context, name);
							}
						}
					} else {
						throw new XNScriptError("Can't understand this");
					}
				} else if (expr instanceof XNVariantMassDescriptor) {
					if (parent != null) {
						return dataType.getChildMassVariant(context, parent);
					} else {
						return dataType.getMassInstance(context);
					}
				} else if (expr instanceof XNVariantOrdinalDescriptor) {
					XNToken startOrdinal = ((XNVariantOrdinalDescriptor)expr).startOrdinal;
					XNToken endOrdinal = ((XNVariantOrdinalDescriptor)expr).endOrdinal;
					if (startOrdinal != null && endOrdinal != null) {
						int start = context.getOrdinal(startOrdinal.image);
						int end = context.getOrdinal(endOrdinal.image);
						if (parent != null) {
							return dataType.getChildVariantByIndex(context, parent, start, end);
						} else {
							return dataType.getInstanceByIndex(context, start, end);
						}
					} else if (startOrdinal != null) {
						int start = context.getOrdinal(startOrdinal.image);
						if (parent != null) {
							return dataType.getChildVariantByIndex(context, parent, start);
						} else {
							return dataType.getInstanceByIndex(context, start);
						}
					} else if (endOrdinal != null) {
						int end = context.getOrdinal(endOrdinal.image);
						if (parent != null) {
							return dataType.getChildVariantByIndex(context, parent, end);
						} else {
							return dataType.getInstanceByIndex(context, end);
						}
					} else {
						throw new XNScriptError("Can't understand this");
					}
				} else if (expr instanceof XNVariantSingletonDescriptor) {
					if (parent != null) {
						return dataType.getChildSingletonVariant(context, parent);
					} else {
						return dataType.getSingletonInstance(context);
					}
				} else {
					throw new XNScriptError("Can't understand this");
				}
			}
			else if (expr instanceof XNFunctionCallPropertyDescriptor) {
				XNFunctionCallPropertyDescriptor fc = (XNFunctionCallPropertyDescriptor)expr;
				XOMVariant argument = (fc.argument == null) ? null : evaluateExpression(fc.argument).asValue(context);
				if (fc.isBuiltInFunction()) {
					if (argument != null && argument.canGetProperty(context, fc.identifier)) {
						return argument.getProperty(context, fc.modifier, fc.identifier);
					} else if (
							(argument == null || XOMInterpreterType.instance.canMakeInstanceFrom(context, argument)) &&
							context.hasGlobalProperty(fc.identifier) &&
							context.getGlobalProperty(fc.identifier).canGetProperty(context, fc.identifier)
					) {
						return context.getGlobalProperty(fc.identifier).getProperty(context, fc.modifier, fc.identifier);
					} else {
						return evaluateBuiltInFunction(fc.identifier, fc.modifier, argument);
					}
				} else {
					XOMVariant ret = evaluateFunction(fc.identifier, fc.modifier, fc.argument, argument);
					return (ret == null) ? XOMEmpty.EMPTY : ret;
				}
			}
			else {
				throw new XNScriptError("Can't understand this");
			}
		} catch (XNScriptError err) {
			err.setLineAndCol(expr.getBeginLine(), expr.getBeginCol());
			throw err;
		}
	}
	
	public static class NaNComparisonException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	public int compareVariants(XOMVariant av, XOMVariant bv) throws NaNComparisonException {
		if (av instanceof XOMNumber && bv instanceof XOMNumber) {
			XOMNumber an = XOMNumberType.instance.makeInstanceFrom(context, av, false);
			XOMNumber bn = XOMNumberType.instance.makeInstanceFrom(context, bv, false);
			if (an.isNaN() || bn.isNaN()) throw new NaNComparisonException();
			return XOMNumberMath.compare(an, bn);
		}
		else if (av instanceof XOMDate && bv instanceof XOMDate) {
			XOMDate ad = XOMDateType.instance.makeInstanceFrom(context, av);
			XOMDate bd = XOMDateType.instance.makeInstanceFrom(context, bv);
			return ad.toCalendar().compareTo(bd.toCalendar());
		}
		else if (XOMNumberType.instance.canMakeInstanceFrom(context, av, false) && XOMNumberType.instance.canMakeInstanceFrom(context, bv, false)) {
			XOMNumber an = XOMNumberType.instance.makeInstanceFrom(context, av, false);
			XOMNumber bn = XOMNumberType.instance.makeInstanceFrom(context, bv, false);
			if (an.isNaN() || bn.isNaN()) throw new NaNComparisonException();
			return XOMNumberMath.compare(an, bn);
		}
		else if (XOMDateType.instance.canMakeInstanceFrom(context, av) && XOMDateType.instance.canMakeInstanceFrom(context, bv)) {
			XOMDate ad = XOMDateType.instance.makeInstanceFrom(context, av);
			XOMDate bd = XOMDateType.instance.makeInstanceFrom(context, bv);
			return ad.toCalendar().compareTo(bd.toCalendar());
		}
		else {
			return av.toTextString(context).compareToIgnoreCase(bv.toTextString(context));
		}
	}
	
	protected XOMVariant evaluateFunction(String functionName, XNModifier modifier, XNExpression paramExpr, XOMVariant parameter) {
		XNResponder resp = context.getFirstResponder();
		
		// try local declarations first
		if (context.getCurrentStackFrame() != null) {
			XNFunctionHandler fh = context.getCurrentStackFrame().getLocalUserFunction(functionName);
			if (fh != null) {
				context.pushResponder(resp);
				XNHandlerExit exit = null;
				try {
					List<XOMVariant> params = new ArrayList<XOMVariant>();
					if (paramExpr instanceof XNListExpression) {
						params.addAll(parameter.toPrimitiveList(context));
					} else {
						params.add(parameter.asPrimitive(context));
					}
					exit = evaluateUserFunction(fh, params);
				} finally {
					context.popResponder();
				}
				if (exit.status() == XNHandlerExitStatus.PASSED) {
					if (exit.nextResponderValue() != null) {
						resp = exit.nextResponderValue();
					} else if ("all".equalsIgnoreCase(exit.blockTypeValue())) {
						resp = null;
					}
				} else if (exit.status() == XNHandlerExitStatus.RETURNED) {
					return exit.returnValue();
				} else {
					throw new XNScriptError("Internal error: function evaluation");
				}
			}
		}
		
		do {

			// then climb up the chain of responsibility
			while (resp != null) {
				context.pushResponder(resp);
				XNHandlerExit exit = null;
				try {
					exit = resp.evaluateFunction(context, functionName, modifier, parameter);
				} finally {
					context.popResponder();
				}
				if (exit.status() == XNHandlerExitStatus.PASSED) {
					if (exit.nextResponderValue() != null) {
						resp = exit.nextResponderValue();
					} else if ("all".equalsIgnoreCase(exit.blockTypeValue())) {
						resp = null;
					} else {
						resp = resp.nextResponder();
					}
				} else if (exit.status() == XNHandlerExitStatus.RETURNED) {
					return exit.returnValue();
				} else {
					throw new XNScriptError("Internal error: function evaluation");
				}
			}

			// try global declarations
			XNFunctionHandler fh = context.getGlobalUserFunction(functionName);
			if (fh != null) {
				context.pushResponder(resp);
				XNHandlerExit exit = null;
				try {
					List<XOMVariant> params = new ArrayList<XOMVariant>();
					if (paramExpr instanceof XNListExpression) {
						params.addAll(parameter.toPrimitiveList(context));
					} else {
						params.add(parameter.asPrimitive(context));
					}
					exit = evaluateUserFunction(fh, params);
				} finally {
					context.popResponder();
				}
				if (exit.status() == XNHandlerExitStatus.PASSED) {
					if (exit.nextResponderValue() != null) {
						resp = exit.nextResponderValue();
					} else if ("all".equalsIgnoreCase(exit.blockTypeValue())) {
						resp = null;
					}
				} else if (exit.status() == XNHandlerExitStatus.RETURNED) {
					return exit.returnValue();
				} else {
					throw new XNScriptError("Internal error: function evaluation");
				}
			}
			
		} while (resp != null);
		
		// finally go with the built-in function
		return evaluateBuiltInFunction(functionName, modifier, parameter);
	}
	
	private XNHandlerExit evaluateUserFunction(XNFunctionHandler handler, List<XOMVariant> parameters) {
		XNStackFrame f = new XNStackFrame(handler.name, parameters);
		if (handler.parameters != null) {
			for (int i = 0; i < handler.parameters.size(); i++) {
				XNHandlerParameter param = handler.parameters.get(i);
				String paramName = param.name;
				XNDataType paramDatatypeObj = param.datatype;
				XNExpression paramValueExpr = param.value;
				f.setVariableScope(paramName, XNVariableScope.LOCAL);
				XOMDataType<? extends XOMVariant> paramDatatype =
					(paramDatatypeObj == null) ?
							XOMStringType.instance :
								context.getDataType(paramDatatypeObj.toNameString());
				if (paramDatatype == null) throw new XNScriptError("Unrecognized data type");
				XOMVariant paramValue =
					(i < parameters.size()) ?
							parameters.get(i) :
								(paramValueExpr == null) ?
										XOMEmpty.EMPTY :
											evaluateExpression(paramValueExpr).asPrimitive(context);
				f.localVariables().declareVariable(context, paramName, paramDatatype, paramValue);
			}
		}
		context.pushStackFrame(f);
		XNHandlerExit exit = null;
		try {
			exit = executeStatements(handler.body);
		} finally {
			context.popStackFrame();
		}
		switch (exit.status()) {
		case ENDED:
			return XNHandlerExit.returned();
		case RETURNED:
			return exit;
		case EXITED:
			if (exit.blockTypeValue().equalsIgnoreCase(handler.name)) {
				if (exit.errorValue() != null) {
					throw new XNScriptError(exit.errorValue().toTextString(context));
				} else {
					return XNHandlerExit.returned();
				}
			} else {
				throw new XNScriptError("Found exit "+exit.blockTypeValue()+" outside a "+exit.blockTypeValue()+" block");
			}
		case PASSED:
			return exit;
		case NEXTED:
			throw new XNScriptError("Found next "+exit.blockTypeValue()+" outside a "+exit.blockTypeValue()+" block");
		default:
			return XNHandlerExit.returned();
		}
	}
	
	private XOMVariant evaluateBuiltInFunction(String functionName, XNModifier modifier, XOMVariant parameter) {
		XNModule.Function m = context.getFunctionInterpreter(functionName);
		if (m != null) {
			XOMVariant ret = m.evaluateFunction(context, functionName, modifier, parameter);
			return (ret == null) ? XOMEmpty.EMPTY : ret;
		} else {
			throw new XNScriptError("Can't understand "+functionName);
		}
	}
	
	private boolean variantExists(XNVariantDescriptor expr) {
		XNDataType dataTypeObj = expr.datatype;
		String dataTypeStr = dataTypeObj.toNameString();
		XOMDataType<? extends XOMVariant> dataType = context.getDataType(dataTypeStr);
		XNExpression parentExpression = expr.parentVariant;
		XOMVariant parent = (parentExpression == null) ? null : evaluateExpression(parentExpression);
		if (expr instanceof XNVariantIdDescriptor) {
			XNExpression idExpression = ((XNVariantIdDescriptor)expr).id;
			XOMVariant idVar = evaluateExpression(idExpression).asPrimitive(context);
			XOMInteger idInt = XOMIntegerType.instance.makeInstanceFrom(context, idVar, true);
			if (parent != null) {
				return dataType.canGetChildVariantByID(context, parent, idInt.toInt());
			} else {
				return dataType.canGetInstanceByID(context, idInt.toInt());
			}
		} else if (expr instanceof XNVariantIndexNameDescriptor) {
			XNExpression startExpr = ((XNVariantIndexNameDescriptor)expr).start;
			XNExpression endExpr = ((XNVariantIndexNameDescriptor)expr).end;
			if (startExpr != null && endExpr != null) {
				int start = XOMIntegerType.instance.makeInstanceFrom(context, evaluateExpression(startExpr).asPrimitive(context), true).toInt();
				int end = XOMIntegerType.instance.makeInstanceFrom(context, evaluateExpression(endExpr).asPrimitive(context), true).toInt();
				if (parent != null) {
					return dataType.canGetChildVariantByIndex(context, parent, start, end);
				} else {
					return dataType.canGetInstanceByIndex(context, start, end);
				}
			} else if (startExpr != null) {
				XOMVariant idxNameVar = evaluateExpression(startExpr).asPrimitive(context);
				if (!idxNameVar.toTextString(context).equals("") && XOMIntegerType.instance.canMakeInstanceFrom(context, idxNameVar, true)) {
					int index = XOMIntegerType.instance.makeInstanceFrom(context, idxNameVar, true).toInt();
					if (parent != null) {
						return dataType.canGetChildVariantByIndex(context, parent, index);
					} else {
						return dataType.canGetInstanceByIndex(context, index);
					}
				} else {
					String name = idxNameVar.toTextString(context);
					if (parent != null) {
						return dataType.canGetChildVariantByName(context, parent, name);
					} else {
						return dataType.canGetInstanceByName(context, name);
					}
				}
			} else if (endExpr != null) {
				XOMVariant idxNameVar = evaluateExpression(endExpr).asPrimitive(context);
				if (!idxNameVar.toTextString(context).equals("") && XOMIntegerType.instance.canMakeInstanceFrom(context, idxNameVar, true)) {
					int index = XOMIntegerType.instance.makeInstanceFrom(context, idxNameVar, true).toInt();
					if (parent != null) {
						return dataType.canGetChildVariantByIndex(context, parent, index);
					} else {
						return dataType.canGetInstanceByIndex(context, index);
					}
				} else {
					String name = idxNameVar.toTextString(context);
					if (parent != null) {
						return dataType.canGetChildVariantByName(context, parent, name);
					} else {
						return dataType.canGetInstanceByName(context, name);
					}
				}
			} else {
				throw new XNScriptError("Can't understand this");
			}
		} else if (expr instanceof XNVariantMassDescriptor) {
			if (parent != null) {
				return dataType.canGetChildMassVariant(context, parent);
			} else {
				return dataType.canGetMassInstance(context);
			}
		} else if (expr instanceof XNVariantOrdinalDescriptor) {
			XNToken startOrdinal = ((XNVariantOrdinalDescriptor)expr).startOrdinal;
			XNToken endOrdinal = ((XNVariantOrdinalDescriptor)expr).endOrdinal;
			if (startOrdinal != null && endOrdinal != null) {
				int start = context.getOrdinal(startOrdinal.image);
				int end = context.getOrdinal(endOrdinal.image);
				if (parent != null) {
					return dataType.canGetChildVariantByIndex(context, parent, start, end);
				} else {
					return dataType.canGetInstanceByIndex(context, start, end);
				}
			} else if (startOrdinal != null) {
				int start = context.getOrdinal(startOrdinal.image);
				if (parent != null) {
					return dataType.canGetChildVariantByIndex(context, parent, start);
				} else {
					return dataType.canGetInstanceByIndex(context, start);
				}
			} else if (endOrdinal != null) {
				int end = context.getOrdinal(endOrdinal.image);
				if (parent != null) {
					return dataType.canGetChildVariantByIndex(context, parent, end);
				} else {
					return dataType.canGetInstanceByIndex(context, end);
				}
			} else {
				throw new XNScriptError("Can't understand this");
			}
		} else if (expr instanceof XNVariantSingletonDescriptor) {
			if (parent != null) {
				return dataType.canGetChildSingletonVariant(context, parent);
			} else {
				return dataType.canGetSingletonInstance(context);
			}
		} else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	/* * * * * * * * * * * * *
	 * COMMANDS & STATEMENTS *
	 * * * * * * * * * * * * */
	
	public void executeScriptFile(File file, String textEncoding) throws IOException {
		if (file == null) return;
		context.addMessages(XIONUtil.getMessagesForScriptFile(file, textEncoding));
		XNLexer lexer = new XNLexer(file, new InputStreamReader(new FileInputStream(file), textEncoding));
		XNParser parser = new XNParser(context, lexer);
		List<XNStatement> program = parser.parse();
		executeScript(program);
	}
	
	public void executeScriptString(String s) {
		if (s == null) return;
		XNLexer lexer = new XNLexer(s, new StringReader(s));
		XNParser parser = new XNParser(context, lexer);
		List<XNStatement> program = parser.parse();
		executeScript(program);
	}
	
	public void executeScript(List<XNStatement> stats) {
		if (stats == null) return;
		try {
			XNHandlerExit exit = executeStatements(stats);
			switch (exit.status()) {
			case RETURNED:
				context.setResult((exit.returnValue() == null) ? XOMEmpty.EMPTY : exit.returnValue());
				break;
			case EXITED:
				if (exit.blockTypeValue() != null) {
					throw new XNScriptError("Found exit "+exit.blockTypeValue()+" outside a "+exit.blockTypeValue()+" block");
				}
				if (exit.errorValue() != null) {
					throw new XNScriptError(exit.errorValue().toTextString(context));
				}
				break;
			case NEXTED:
				throw new XNScriptError("Found next "+exit.blockTypeValue()+" outside a "+exit.blockTypeValue()+" block");
			}
		} catch (XNExitedToInterpreterException exex) {}
	}
	
	public void sendMessageString(XNResponder recip, String message) {
		XNResponder first = context.getFirstResponder();
		context.setFirstResponder(recip);
		context.pushResponder(recip);
		try {
			XNLexer lexer = new XNLexer(message, new StringReader(message));
			XNParser parser = new XNParser(context, lexer);
			List<XNStatement> stats = parser.parse();
			XNHandlerExit exit = executeStatements(stats);
			switch (exit.status()) {
			case RETURNED:
				context.setResult((exit.returnValue() == null) ? XOMEmpty.EMPTY : exit.returnValue());
				break;
			case EXITED:
				if (exit.blockTypeValue() != null && !exit.blockTypeValue().equalsIgnoreCase("tell")) {
					throw new XNScriptError("Found exit "+exit.blockTypeValue()+" outside a "+exit.blockTypeValue()+" block");
				}
				if (exit.errorValue() != null) {
					throw new XNScriptError(exit.errorValue().toTextString(context));
				}
				break;
			case NEXTED:
				throw new XNScriptError("Found next "+exit.blockTypeValue()+" outside a "+exit.blockTypeValue()+" block");
			}
		}
		finally {
			context.popResponder();
			context.setFirstResponder(first);
		}
	}
	
	public void sendMessage(XNResponder recip, List<XNStatement> message) {
		XNResponder first = context.getFirstResponder();
		context.setFirstResponder(recip);
		context.pushResponder(recip);
		try {
			XNHandlerExit exit = executeStatements(message);
			switch (exit.status()) {
			case RETURNED:
				context.setResult((exit.returnValue() == null) ? XOMEmpty.EMPTY : exit.returnValue());
				break;
			case EXITED:
				if (exit.blockTypeValue() != null && !exit.blockTypeValue().equalsIgnoreCase("tell")) {
					throw new XNScriptError("Found exit "+exit.blockTypeValue()+" outside a "+exit.blockTypeValue()+" block");
				}
				if (exit.errorValue() != null) {
					throw new XNScriptError(exit.errorValue().toTextString(context));
				}
				break;
			case NEXTED:
				throw new XNScriptError("Found next "+exit.blockTypeValue()+" outside a "+exit.blockTypeValue()+" block");
			}
		}
		finally {
			context.popResponder();
			context.setFirstResponder(first);
		}
	}
	
	public XNHandlerExit executeStatements(List<XNStatement> stats) {
		if (stats == null) return XNHandlerExit.ended();
		for (XNStatement stat : stats) {
			XNHandlerExit exit = executeStatement(stat);
			if (exit.status() != XNHandlerExitStatus.ENDED) {
				return exit;
			}
		}
		return XNHandlerExit.ended();
	}
	
	public XNHandlerExit executeStatement(XNStatement stat) {
		if (stat == null) return XNHandlerExit.ended();
		try {
			if (stat instanceof XNCommandStatement) {
				String commandName = ((XNCommandStatement)stat).commandName;
				List<XNExpression> params = ((XNCommandStatement)stat).parameters;
				executeCommand(commandName, params);
				return XNHandlerExit.ended();
			} else if (stat instanceof XNConstantDeclaration) {
				String constantName = ((XNConstantDeclaration)stat).identifier;
				XNExpression evalue = ((XNConstantDeclaration)stat).value;
				if (!context.hasConstant(constantName)) {
					context.addUserConstant(constantName, evaluateExpression(evalue).asPrimitive(context));
				}
				return XNHandlerExit.ended();
			} else if (stat instanceof XNDoStatement) {
				XOMVariant what = evaluateExpression(((XNDoStatement)stat).whatToDo).asPrimitive(context);
				XNExpression elang = ((XNDoStatement)stat).language;
				XOMVariant vlang = (elang == null) ? XOMEmpty.EMPTY : evaluateExpression(elang).asPrimitive(context);
				String slang = vlang.toTextString(context);
				if (slang.equalsIgnoreCase("") || slang.equalsIgnoreCase("xion") || slang.equalsIgnoreCase("openxion") || slang.equalsIgnoreCase("hypertalk")) {
					if (!context.allow(XNSecurityKey.DO_AND_VALUE, "Code", what.toTextString(context), "Language", slang))
						throw new XNScriptError("Security settings do not allow do");
					executeScriptString(what.toTextString(context));
					return XNHandlerExit.ended();
				} else if (context.hasExternalLanguage(slang)) {
					if (!context.allow(XNSecurityKey.EXTERNAL_SCRIPTS, "Code", what.toTextString(context), "Language", slang))
						throw new XNScriptError("Security settings do not allow do");
					XOMVariant returnValue = context.getExternalLanguage(slang).execute(what.toTextString(context));
					if (returnValue != null) {
						context.setResult(returnValue);
					}
					return XNHandlerExit.ended();
				} else {
					throw new XNScriptError("The "+slang+" language is not supported");
				}
			} else if (stat instanceof XNExitStatement) {
				if (((XNExitStatement)stat).whatToExitTo == null) {
					if (((XNExitStatement)stat).error != null) {
						XOMVariant err = evaluateExpression(((XNExitStatement)stat).error).asPrimitive(context);
						return XNHandlerExit.exitedBlockWithError(((XNExitStatement)stat).whatToExit, err);
					} else {
						return XNHandlerExit.exitedBlock(((XNExitStatement)stat).whatToExit);
					}
				} else {
					XOMVariant exitTo = evaluateExpression(((XNExitStatement)stat).whatToExitTo).asValue(context);
					if (XOMInterpreterType.instance.canMakeInstanceFrom(context, exitTo)) {
						if (((XNExitStatement)stat).error != null) {
							XOMVariant err = evaluateExpression(((XNExitStatement)stat).error).asPrimitive(context);
							throw new XNExitedToInterpreterException(err.toTextString(context));
						} else {
							throw new XNExitedToInterpreterException();
						}
					} else {
						throw new XNScriptError("Can't exit to this");
					}
				}
			} else if (stat instanceof XNFunctionHandler) {
				if (context.getCurrentStackFrame() != null) {
					context.getCurrentStackFrame().defineLocalUserFunction(((XNFunctionHandler)stat).name, (XNFunctionHandler)stat);
				} else {
					context.defineGlobalUserFunction(((XNFunctionHandler)stat).name, (XNFunctionHandler)stat);
				}
				return XNHandlerExit.ended();
			} else if (stat instanceof XNIfBlock) {
				XOMVariant condition = evaluateExpression(((XNIfBlock)stat).condition).asPrimitive(context);
				if (XOMBooleanType.instance.makeInstanceFrom(context, condition).toBoolean()) {
					XNHandlerExit exit = executeStatements(((XNIfBlock)stat).thenBlock);
					if (exit.status() == XNHandlerExitStatus.EXITED && "if".equalsIgnoreCase(exit.blockTypeValue())) {
						exit = XNHandlerExit.ended();
					}
					return exit;
				} else if (((XNIfBlock)stat).elseBlock != null) {
					XNHandlerExit exit = executeStatements(((XNIfBlock)stat).elseBlock);
					if (exit.status() == XNHandlerExitStatus.EXITED && "if".equalsIgnoreCase(exit.blockTypeValue())) {
						exit = XNHandlerExit.ended();
					}
					return exit;
				} else {
					return XNHandlerExit.ended();
				}
			} else if (stat instanceof XNIncludeStatement) {
				XNIncludeStatement is = (XNIncludeStatement)stat;
				String path = evaluateExpression(is.scriptName).toTextString(context);
				File file = XIONUtil.locateInclude(context, path, is.ask);
				if (file != null) {
					if (!(is.once && context.hasIncludedScript(file.getAbsolutePath()))) {
						context.addIncludedScript(file.getAbsolutePath());
						try {
							executeScriptFile(file, context.getTextEncoding());
						} catch (IOException ioe) {
							if (is.require) {
								throw new XNScriptError("Cannot read required include " + path);
							}
						}
					}
					return XNHandlerExit.ended();
				} else if (is.require) {
					throw new XNScriptError("Cannot find required include " + path);
				} else {
					return XNHandlerExit.ended();
				}
			} else if (stat instanceof XNMessageHandler) {
				if (context.getCurrentStackFrame() != null) {
					context.getCurrentStackFrame().defineLocalUserCommand(((XNMessageHandler)stat).name, (XNMessageHandler)stat);
				} else {
					context.defineGlobalUserCommand(((XNMessageHandler)stat).name, (XNMessageHandler)stat);
				}
				return XNHandlerExit.ended();
			} else if (stat instanceof XNNextStatement) {
				return XNHandlerExit.nextedBlock(((XNNextStatement)stat).whatToNext);
			} else if (stat instanceof XNObjectTypeDeclaration) {
				XOMUserObjectType t = new XOMUserObjectType(context, (XNObjectTypeDeclaration)stat);
				for (XNObjectTypeName name : ((XNObjectTypeDeclaration)stat).names) {
					String nameString = "";
					for (String s : name.name) nameString += " "+s;
					nameString = XIONUtil.normalizeVarName(nameString);
					if (!context.hasDataType(nameString)) {
						if (name.plural) {
							context.addUserDataType(nameString, t.listType());
						} else {
							context.addUserDataType(nameString, t);
						}
					}
				}
				return XNHandlerExit.ended();
			} else if (stat instanceof XNOrdinalDeclaration) {
				String ordinalName = ((XNOrdinalDeclaration)stat).identifier;
				XNExpression evalue = ((XNOrdinalDeclaration)stat).value;
				if (!context.hasOrdinal(ordinalName)) {
					XOMInteger i = XOMIntegerType.instance.makeInstanceFrom(context, evaluateExpression(evalue).asPrimitive(context), true);
					context.addUserOrdinal(ordinalName, i.toInt());
				}
				return XNHandlerExit.ended();
			} else if (stat instanceof XNPassStatement) {
				if (((XNPassStatement)stat).whatToPassTo == null) {
					return XNHandlerExit.passed();
				} else {
					XOMVariant passTo = evaluateExpression(((XNPassStatement)stat).whatToPassTo).asValue(context);
					if (passTo instanceof XNResponder) {
						return XNHandlerExit.passedTo((XNResponder)passTo);
					} else if (XOMInterpreterType.instance.canMakeInstanceFrom(context, passTo)) {
						return XNHandlerExit.passedToInterpreter();
					} else {
						throw new XNScriptError("Can't pass to this");
					}
				}
			} else if (stat instanceof XNRepeatBlock) {
				XNRepeatParameters rp = ((XNRepeatBlock)stat).params;
				List<XNStatement> body = ((XNRepeatBlock)stat).body;
				List<XNStatement> lastlyBody = ((XNRepeatBlock)stat).lastlyBody;
				if (rp instanceof XNRepeatForParameters) {
					XNExpression countExpr = ((XNRepeatForParameters)rp).count;
					XOMVariant countVar = (countExpr == null) ? null : evaluateExpression(countExpr).asPrimitive(context);
					boolean infinite;
					int countInt;
					if (countVar == null) {
						infinite = true;
						countInt = Integer.MAX_VALUE;
					} else {
						XOMNumber i = XOMNumberType.instance.makeInstanceFrom(context, countVar, true);
						if (i.isZero() || i.getSign() == XOMInteger.SIGN_POSITIVE) {
							if (i.isInfinite()) {
								infinite = true;
								countInt = Integer.MAX_VALUE;
							} else try {
								infinite = false;
								countInt = i.toBigDecimal().intValueExact();
							} catch (Exception e) {
								throw new XNScriptError("Expected non-negative integer here");
							}
						} else {
							throw new XNScriptError("Expected non-negative integer here");
						}
					}
					while (infinite || countInt-- > 0) {
						XNHandlerExit exit = executeStatements(body);
						if (exit.status() == XNHandlerExitStatus.EXITED && "repeat".equalsIgnoreCase(exit.blockTypeValue())) {
							return XNHandlerExit.ended();
						} else if (exit.status() == XNHandlerExitStatus.NEXTED && "repeat".equalsIgnoreCase(exit.blockTypeValue())) {
							// keep looping
						} else if (exit.status() != XNHandlerExitStatus.ENDED) {
							return exit;
						}
					}
					if (lastlyBody != null) return executeStatements(lastlyBody);
					else return XNHandlerExit.ended();
				}
				else if (rp instanceof XNRepeatWhileParameters) {
					XNExpression condition = ((XNRepeatWhileParameters)rp).condition;
					while (XOMBooleanType.instance.makeInstanceFrom(context, evaluateExpression(condition).asPrimitive(context)).toBoolean()) {
						XNHandlerExit exit = executeStatements(body);
						if (exit.status() == XNHandlerExitStatus.EXITED && "repeat".equalsIgnoreCase(exit.blockTypeValue())) {
							return XNHandlerExit.ended();
						} else if (exit.status() == XNHandlerExitStatus.NEXTED && "repeat".equalsIgnoreCase(exit.blockTypeValue())) {
							// keep looping
						} else if (exit.status() != XNHandlerExitStatus.ENDED) {
							return exit;
						}
					}
					if (lastlyBody != null) return executeStatements(lastlyBody);
					else return XNHandlerExit.ended();
				}
				else if (rp instanceof XNRepeatUntilParameters) {
					XNExpression condition = ((XNRepeatUntilParameters)rp).condition;
					do {
						XNHandlerExit exit = executeStatements(body);
						if (exit.status() == XNHandlerExitStatus.EXITED && "repeat".equalsIgnoreCase(exit.blockTypeValue())) {
							return XNHandlerExit.ended();
						} else if (exit.status() == XNHandlerExitStatus.NEXTED && "repeat".equalsIgnoreCase(exit.blockTypeValue())) {
							// keep looping
						} else if (exit.status() != XNHandlerExitStatus.ENDED) {
							return exit;
						}
					} while (!XOMBooleanType.instance.makeInstanceFrom(context, evaluateExpression(condition).asPrimitive(context)).toBoolean());
					if (lastlyBody != null) return executeStatements(lastlyBody);
					else return XNHandlerExit.ended();
				}
				else if (rp instanceof XNRepeatWithParameters) {
					XOMVariant dest = evaluateExpression(((XNRepeatWithParameters)rp).identifier).asVariable(context, true);
					XOMVariant start = evaluateExpression(((XNRepeatWithParameters)rp).startvalue).asPrimitive(context);
					XOMVariant end = evaluateExpression(((XNRepeatWithParameters)rp).endvalue).asPrimitive(context);
					XOMNumber step =
						(((XNRepeatWithParameters)rp).stepvalue == null) ?
								(((XNRepeatWithParameters)rp).toToken.image.toLowerCase().contains("down") ? XOMNumber.ONE.negate() : XOMNumber.ONE) :
									XOMNumberType.instance.makeInstanceFrom(context, evaluateExpression(((XNRepeatWithParameters)rp).stepvalue).asPrimitive(context), true);
					dest.putIntoContents(context, start);
					while (true) {
						try {
							if (step.getSign() == XOMNumber.SIGN_POSITIVE) {
								if (compareVariants(dest,end) > 0) break;
							}
							else if (step.getSign() == XOMNumber.SIGN_NEGATIVE) {
								if (compareVariants(dest,end) < 0) break;
							}
							else if (step.getSign() == XOMNumber.SIGN_NaN) {
								break;
							}
						} catch (NaNComparisonException nce) {
							break;
						}
						
						XNHandlerExit exit = executeStatements(body);
						if (exit.status() == XNHandlerExitStatus.EXITED && "repeat".equalsIgnoreCase(exit.blockTypeValue())) {
							return XNHandlerExit.ended();
						} else if (exit.status() == XNHandlerExitStatus.NEXTED && "repeat".equalsIgnoreCase(exit.blockTypeValue())) {
							// keep looping
						} else if (exit.status() != XNHandlerExitStatus.ENDED) {
							return exit;
						}
						
						MathContext mc = context.getMathContext();
						MathProcessor mp = context.getMathProcessor();
						if (dest instanceof XOMNumber && step instanceof XOMNumber) {
							XOMNumber destn = XOMNumberType.instance.makeInstanceFrom(context, dest, true);
							XOMNumber stepn = XOMNumberType.instance.makeInstanceFrom(context, step, true);
							dest.putIntoContents(context, XOMNumberMath.add(destn, stepn, mc, mp));
						}
						else if (XOMNumberType.instance.canMakeInstanceFrom(context, dest, true) && XOMNumberType.instance.canMakeInstanceFrom(context, step, true)) {
							XOMNumber destn = XOMNumberType.instance.makeInstanceFrom(context, dest, true);
							XOMNumber stepn = XOMNumberType.instance.makeInstanceFrom(context, step, true);
							dest.putIntoContents(context, XOMNumberMath.add(destn, stepn, mc, mp));
						}
						else {
							throw new XOMMorphError("number");
						}
					}
					if (lastlyBody != null) return executeStatements(lastlyBody);
					else return XNHandlerExit.ended();
				}
				else if (rp instanceof XNRepeatForEachParameters) {
					String name = ((XNRepeatForEachParameters)rp).identifier;
					List<? extends XOMVariant> list = evaluateExpression(((XNRepeatForEachParameters)rp).list).toVariantList(context);
					for (XOMVariant item : list) {
						XOMVariant dest = new XOMVariable(context.getVariableMap(name), name);
						dest.putIntoContents(context, item);
						XNHandlerExit exit = executeStatements(body);
						if (exit.status() == XNHandlerExitStatus.EXITED && "repeat".equalsIgnoreCase(exit.blockTypeValue())) {
							return XNHandlerExit.ended();
						} else if (exit.status() == XNHandlerExitStatus.NEXTED && "repeat".equalsIgnoreCase(exit.blockTypeValue())) {
							// keep looping
						} else if (exit.status() != XNHandlerExitStatus.ENDED) {
							return exit;
						}
					}
					if (lastlyBody != null) return executeStatements(lastlyBody);
					else return XNHandlerExit.ended();
				}
				else {
					throw new XNScriptError("Can't understand this");
				}
			} else if (stat instanceof XNReturnStatement) {
				if (((XNReturnStatement)stat).whatToReturn == null) {
					return XNHandlerExit.returned();
				} else {
					return XNHandlerExit.returned(evaluateExpression(((XNReturnStatement)stat).whatToReturn).asValue(context));
				}
			} else if (stat instanceof XNSendStatement) {
				String message = evaluateExpression(((XNSendStatement)stat).message).toTextString(context);
				XOMVariant recip = evaluateExpression(((XNSendStatement)stat).recipient).asValue(context);
				boolean reply = ((XNSendStatement)stat).withReply;
				if (recip instanceof XNResponder) {
					sendMessageString((XNResponder)recip, message);
					if (!reply) context.setResult(null);
					return XNHandlerExit.ended();
				} else if (XOMInterpreterType.instance.canMakeInstanceFrom(context, recip)) {
					if (!context.allow(XNSecurityKey.DO_AND_VALUE, "Code", message, "Language", "XION via Send"))
						throw new XNScriptError("Security settings do not allow send");
					sendMessageString(null, message);
					if (!reply) context.setResult(null);
					return XNHandlerExit.ended();
				} else {
					throw new XNScriptError("Can't send to this");
				}
			} else if (stat instanceof XNSwitchBlock) {
				XOMVariant switchOn = evaluateExpression(((XNSwitchBlock)stat).switchOn).asPrimitive(context);
				List<XNCaseBlock> possibleCases = new Vector<XNCaseBlock>();
				List<XNCaseBlock> defaultCases = new Vector<XNCaseBlock>();
				for (XNCaseBlock caseBlock : ((XNSwitchBlock)stat).cases) {
					if (possibleCases.isEmpty()) {
						if (caseBlock.caseValues != null && !caseBlock.caseValues.isEmpty()) {
							for (XNExpression caseValue : caseBlock.caseValues) {
								XOMVariant caseOf = evaluateExpression(caseValue).asPrimitive(context);
								try { if (compareVariants(switchOn, caseOf) == 0) {
									possibleCases.add(caseBlock);
								}} catch (NaNComparisonException nce) {}
							}
						}
					} else {
						possibleCases.add(caseBlock);
					}
					if (defaultCases.isEmpty()) {
						if (caseBlock.caseValues == null || caseBlock.caseValues.isEmpty()) {
							defaultCases.add(caseBlock);
						}
					} else {
						defaultCases.add(caseBlock);
					}
				}
				if (possibleCases.isEmpty()) possibleCases = defaultCases;
				for (XNCaseBlock caseOf : possibleCases) {
					XNHandlerExit exit = executeStatements(caseOf.caseStatements);
					if (exit.status() == XNHandlerExitStatus.EXITED && "switch".equalsIgnoreCase(exit.blockTypeValue())) {
						return XNHandlerExit.ended();
					}
					else if (exit.status() == XNHandlerExitStatus.NEXTED && "case".equalsIgnoreCase(exit.blockTypeValue())) {
						continue;
					}
					else {
						return exit;
					}
				}
				return XNHandlerExit.ended();
			} else if (stat instanceof XNTellBlock) {
				List<XNStatement> message = ((XNTellBlock)stat).messages;
				XOMVariant recip = evaluateExpression(((XNTellBlock)stat).recipient).asValue(context);
				if (recip instanceof XNResponder) {
					sendMessage((XNResponder)recip, message);
					return XNHandlerExit.ended();
				} else if (XOMInterpreterType.instance.canMakeInstanceFrom(context, recip)) {
					sendMessage(null, message);
					return XNHandlerExit.ended();
				} else {
					throw new XNScriptError("Can't tell this");
				}
			} else if (stat instanceof XNThrowStatement) {
				XNExpression err = ((XNThrowStatement)stat).whatToThrow;
				throw new XNScriptError(evaluateExpression(err).toTextString(context));
			} else if (stat instanceof XNTryBlock) {
				XNHandlerExit exit = XNHandlerExit.ended();
				try {
					if (((XNTryBlock)stat).tryBlock != null) {
						exit = executeStatements(((XNTryBlock)stat).tryBlock);
						if (exit.status() == XNHandlerExitStatus.EXITED && "try".equalsIgnoreCase(exit.blockTypeValue())) {
							exit = XNHandlerExit.ended();
						}
					}
				} catch (XNScriptError err) {
					if (((XNTryBlock)stat).catchBlock != null) {
						context.getVariableMap(((XNTryBlock)stat).catchIdentifier).declareVariable(context, ((XNTryBlock)stat).catchIdentifier, XOMStringType.instance, new XOMString(err.getMessage()));
						exit = executeStatements(((XNTryBlock)stat).catchBlock);
						if (exit.status() == XNHandlerExitStatus.EXITED && "try".equalsIgnoreCase(exit.blockTypeValue())) {
							exit = XNHandlerExit.ended();
						}
					}
				} finally {
					if (((XNTryBlock)stat).finallyBlock != null) {
						exit = executeStatements(((XNTryBlock)stat).finallyBlock);
						if (exit.status() == XNHandlerExitStatus.EXITED && "try".equalsIgnoreCase(exit.blockTypeValue())) {
							exit = XNHandlerExit.ended();
						}
					}
				}
				return exit;
			} else if (stat instanceof XNUseStatement) {
				XNExpression cln = ((XNUseStatement)stat).className;
				String[] classNames = evaluateExpression(cln).toTextString(context).split("[,:;]");
				if (!context.allow(XNSecurityKey.MODULE_LOAD, "Modules", classNames.toString()))
					throw new XNScriptError("Security settings do not allow use");
				for (String className : classNames) {
					try {
						Class<? extends XNModule> module = Class.forName(className).asSubclass(XNModule.class);
						XNModule m = null;
						try {
							java.lang.reflect.Method in = module.getMethod("instance");
							m = (XNModule)in.invoke(null);
						} catch (Exception e) {
							m = module.newInstance();
						}
						if (m != null) {
							context.loadModule(m);
						} else {
							throw new XNScriptError("Can't load module "+className);
						}
					}
					catch (ClassNotFoundException cnfe) {
						throw new XNScriptError("Can't find module "+className);
					}
					catch (ClassCastException cce) {
						throw new XNScriptError("Can't find module "+className);
					}
					catch (IllegalAccessException iae) {
						throw new XNScriptError("Can't load module "+className);
					}
					catch (InstantiationException ie) {
						throw new XNScriptError("Can't load module "+className);
					}
				}
				return XNHandlerExit.ended();
			} else if (stat instanceof XNVariableDeclaration) {
				XNVariableScope scope = ((XNVariableDeclaration)stat).scope;
				for (XNVariableInitializer init : ((XNVariableDeclaration)stat).vars) {
					String name = init.name;
					XNDataType datatypeObj = init.datatype;
					XNExpression valueExpr = init.value;
					context.setVariableScope(name, scope);
					if (context.getVariableMap(name).getVariable(context, name) == null) {
						XOMDataType<? extends XOMVariant> datatype =
							(datatypeObj == null) ?
									XOMStringType.instance :
										context.getDataType(datatypeObj.toNameString());
						if (datatype == null) throw new XNScriptError("Unrecognized data type");
						XOMVariant value =
							(valueExpr == null) ?
									XOMEmpty.EMPTY :
										evaluateExpression(valueExpr).asPrimitive(context);
						context.getVariableMap(name).declareVariable(context, name, datatype, value);
					}
				}
				return XNHandlerExit.ended();
			} else {
				throw new XNScriptError("Can't understand this");
			}
		} catch (XNScriptError err) {
			err.setLineAndCol(stat.getBeginLine(), stat.getBeginCol());
			throw err;
		}
	}
	
	private void executeCommand(String commandName, List<XNExpression> parameters) {
		XNResponder resp = context.getFirstResponder();
		
		// try local declarations first
		if (context.getCurrentStackFrame() != null) {
			XNMessageHandler mh = context.getCurrentStackFrame().getLocalUserCommand(commandName);
			if (mh != null) {
				List<XOMVariant> paramValues = new Vector<XOMVariant>();
				for (XNExpression param : parameters) {
					paramValues.add(evaluateExpression(param).asPrimitive(context));
				}
				context.pushResponder(resp);
				XNHandlerExit exit = null;
				try {
					exit = executeUserCommand(mh, paramValues);
				} finally {
					context.popResponder();
				}
				if (exit.status() == XNHandlerExitStatus.PASSED) {
					if (exit.nextResponderValue() != null) {
						resp = exit.nextResponderValue();
					} else if ("all".equalsIgnoreCase(exit.blockTypeValue())) {
						resp = null;
					}
				} else if (exit.status() == XNHandlerExitStatus.ENDED) {
					return;
				} else {
					throw new XNScriptError("Internal error: command execution");
				}
			}
		}
		
		do {

			// then climb up the chain of responsibility
			while (resp != null) {
				context.pushResponder(resp);
				XNHandlerExit exit = null;
				try {
					exit = resp.executeCommand(context, commandName, parameters);
				} finally {
					context.popResponder();
				}
				if (exit.status() == XNHandlerExitStatus.PASSED) {
					if (exit.nextResponderValue() != null) {
						resp = exit.nextResponderValue();
					} else if ("all".equalsIgnoreCase(exit.blockTypeValue())) {
						resp = null;
					} else {
						resp = resp.nextResponder();
					}
				} else if (exit.status() == XNHandlerExitStatus.ENDED) {
					return;
				} else {
					throw new XNScriptError("Internal error: command execution");
				}
			}

			// try global declarations
			XNMessageHandler mh = context.getGlobalUserCommand(commandName);
			if (mh != null) {
				List<XOMVariant> paramValues = new Vector<XOMVariant>();
				for (XNExpression param : parameters) {
					paramValues.add(evaluateExpression(param).asPrimitive(context));
				}
				context.pushResponder(resp);
				XNHandlerExit exit = null;
				try {
					exit = executeUserCommand(mh, paramValues);
				} finally {
					context.popResponder();
				}
				if (exit.status() == XNHandlerExitStatus.PASSED) {
					if (exit.nextResponderValue() != null) {
						resp = exit.nextResponderValue();
					} else if ("all".equalsIgnoreCase(exit.blockTypeValue())) {
						resp = null;
					}
				} else if (exit.status() == XNHandlerExitStatus.ENDED) {
					return;
				} else {
					throw new XNScriptError("Internal error: command execution");
				}
			}

		} while (resp != null);
		
		// finally go with the built-in command
		executeBuiltInCommand(commandName, parameters);
	}
	
	private XNHandlerExit executeUserCommand(XNMessageHandler handler, List<XOMVariant> parameters) {
		XNStackFrame f = new XNStackFrame(handler.name, parameters);
		if (handler.parameters != null) {
			for (int i = 0; i < handler.parameters.size(); i++) {
				XNHandlerParameter param = handler.parameters.get(i);
				String paramName = param.name;
				XNDataType paramDatatypeObj = param.datatype;
				XNExpression paramValueExpr = param.value;
				f.setVariableScope(paramName, XNVariableScope.LOCAL);
				XOMDataType<? extends XOMVariant> paramDatatype =
					(paramDatatypeObj == null) ?
							XOMStringType.instance :
								context.getDataType(paramDatatypeObj.toNameString());
				if (paramDatatype == null) throw new XNScriptError("Unrecognized data type");
				XOMVariant paramValue =
					(i < parameters.size()) ?
							parameters.get(i) :
								(paramValueExpr == null) ?
										XOMEmpty.EMPTY :
											evaluateExpression(paramValueExpr).asPrimitive(context);
				f.localVariables().declareVariable(context, paramName, paramDatatype, paramValue);
			}
		}
		context.pushStackFrame(f);
		XNHandlerExit exit = null;
		try {
			exit = executeStatements(handler.body);
		} finally {
			context.popStackFrame();
		}
		switch (exit.status()) {
		case ENDED:
			return exit;
		case RETURNED:
			if (exit.returnValue() != null) context.setResult(exit.returnValue());
			return XNHandlerExit.ended();
		case EXITED:
			if (exit.blockTypeValue().equalsIgnoreCase(handler.name)) {
				if (exit.errorValue() != null) {
					throw new XNScriptError(exit.errorValue().toTextString(context));
				} else {
					return XNHandlerExit.ended();
				}
			} else {
				throw new XNScriptError("Found exit "+exit.blockTypeValue()+" outside a "+exit.blockTypeValue()+" block");
			}
		case PASSED:
			return exit;
		case NEXTED:
			throw new XNScriptError("Found next "+exit.blockTypeValue()+" outside a "+exit.blockTypeValue()+" block");
		default:
			return XNHandlerExit.ended();
		}
	}
	
	private void executeBuiltInCommand(String commandName, List<XNExpression> parameters) {
		XNModule.Command m = context.getCommandInterpreter(commandName);
		if (m != null) {
			XOMVariant ret = m.executeCommand(this, context, commandName, parameters);
			if (ret != null) context.setResult(ret);
		} else {
			throw new XNScriptError("Can't understand "+commandName);
		}
	}
}
