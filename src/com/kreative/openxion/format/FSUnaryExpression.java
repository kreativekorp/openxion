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
import java.util.List;

import com.kreative.openxion.XNContext;
import com.kreative.openxion.XNScriptError;
import com.kreative.openxion.ast.XNOperator;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.inst.XOMBoolean;
import com.kreative.openxion.xom.inst.XOMComplex;
import com.kreative.openxion.xom.inst.XOMInteger;
import com.kreative.openxion.xom.inst.XOMNumber;
import com.kreative.openxion.xom.inst.XOMReference;
import com.kreative.openxion.xom.type.XOMBooleanType;
import com.kreative.openxion.xom.type.XOMComplexType;
import com.kreative.openxion.xom.type.XOMIntegerType;
import com.kreative.openxion.xom.type.XOMNumberType;
import com.kreative.openxion.xom.type.XOMReferenceType;

public class FSUnaryExpression implements FSExpression {
	private XNOperator op;
	private FSExpression arg;
	
	public FSUnaryExpression(XNOperator op, FSExpression arg) {
		this.op = op;
		this.arg = arg;
	}
	
	@Override
	public XOMVariant evaluate(XNContext ctx, List<? extends XOMVariant> vs) {
		XOMVariant av;
		boolean ab;
		BigInteger ai;
		switch (op) {
		case NOT:
			av = arg.evaluate(ctx, vs);
			ab = XOMBooleanType.instance.makeInstanceFrom(ctx, av).toBoolean();
			return ab ? XOMBoolean.FALSE : XOMBoolean.TRUE;
		case UNARY_SUBTRACT:
			av = arg.evaluate(ctx, vs);
			if (av instanceof XOMInteger) {
				return ((XOMInteger)av).negate();
			}
			else if (av instanceof XOMNumber) {
				return ((XOMNumber)av).negate();
			}
			else if (av instanceof XOMComplex) {
				return ((XOMComplex)av).negate();
			}
			else if (XOMIntegerType.instance.canMakeInstanceFrom(ctx, av, true)) {
				XOMInteger i = XOMIntegerType.instance.makeInstanceFrom(ctx, av, true);
				return i.negate();
			}
			else if (XOMNumberType.instance.canMakeInstanceFrom(ctx, av, true)) {
				XOMNumber n = XOMNumberType.instance.makeInstanceFrom(ctx, av, true);
				return n.negate();
			}
			else if (XOMComplexType.instance.canMakeInstanceFrom(ctx, av, true)) {
				XOMComplex c = XOMComplexType.instance.makeInstanceFrom(ctx, av, true);
				return c.negate();
			}
			else {
				throw new XOMMorphError("number");
			}
		case BIT_NOT:
			av = arg.evaluate(ctx, vs);
			ai = XOMIntegerType.instance.makeInstanceFrom(ctx, av, true).toBigInteger();
			if (ai == null) return XOMInteger.NaN;
			return new XOMInteger(ai.not());
		case REFERENCE_TO:
			return new XOMReference(arg.evaluate(ctx, vs));
		case REFERENT_OF:
			av = arg.evaluate(ctx, vs);
			return XOMReferenceType.instance.makeInstanceFrom(ctx, av).dereference(true);
		default:
			throw new XNScriptError("Can't understand this");
		}
	}
}
