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

import java.util.List;
import java.util.Vector;

import com.kreative.openxion.XNContext;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.inst.XOMBoolean;
import com.kreative.openxion.xom.inst.XOMInteger;
import com.kreative.openxion.xom.type.XOMBooleanType;
import com.kreative.openxion.xom.type.XOMIntegerType;

public class ExpressionFormatStringComponent extends Vector<FormatStringComponent> implements FormatStringComponent {
	private static final long serialVersionUID = 1L;
	private FSExpression arg;
	
	public ExpressionFormatStringComponent(FSExpression arg) {
		this.arg = arg;
	}
	
	@Override
	public String format(XNContext ctx, List<? extends XOMVariant> vs) {
		switch (this.size()) {
		case 0: return "";
		case 1: return this.get(0).format(ctx, vs);
		default:
			XOMVariant v = arg.evaluate(ctx, vs);
			int idx;
			if (v instanceof XOMInteger) {
				idx = ((XOMInteger)v).toInt();
			} else if (v instanceof XOMBoolean) {
				idx = (((XOMBoolean)v).toBoolean() ? 0 : 1);
			} else if (XOMIntegerType.instance.canMakeInstanceFrom(ctx, v, true)) {
				idx = XOMIntegerType.instance.makeInstanceFrom(ctx, v, true).toInt();
			} else if (XOMBooleanType.instance.canMakeInstanceFrom(ctx, v)) {
				idx = (XOMBooleanType.instance.makeInstanceFrom(ctx, v).toBoolean() ? 0 : 1);
			} else {
				throw new XOMMorphError("integer or true or false");
			}
			if (idx < 0) {
				return this.get(0).format(ctx, vs);
			} else if (idx < this.size()) {
				return this.get(idx).format(ctx, vs);
			} else {
				return this.get(this.size()-1).format(ctx, vs);
			}
		}
	}
}
