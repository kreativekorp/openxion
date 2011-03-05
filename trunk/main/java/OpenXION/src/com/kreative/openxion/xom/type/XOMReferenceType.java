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
 * @since OpenXION 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.xom.type;

import java.util.List;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.util.XIONUtil;
import com.kreative.openxion.xom.XOMDataType;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.inst.XOMEmpty;
import com.kreative.openxion.xom.inst.XOMList;
import com.kreative.openxion.xom.inst.XOMReference;

public class XOMReferenceType extends XOMDataType<XOMReference> {
	private static final long serialVersionUID = 1L;
	
	public static final XOMReferenceType instance = new XOMReferenceType();
	public static final XOMListType listInstance = new XOMListType("references", DESCRIBABILITY_OF_PRIMITIVES, instance);
	
	private XOMReferenceType() {
		super("reference", DESCRIBABILITY_OF_PRIMITIVES, XOMReference.class);
		this.referentType = XOMVariantType.instance;
	}
	
	private XOMDataType<? extends XOMVariant> referentType;
	public XOMReferenceType(XOMDataType<? extends XOMVariant> referentType) {
		super(referentType.getTypeName() + " reference", DESCRIBABILITY_OF_PRIMITIVES, XOMReference.class);
		this.referentType = referentType;
	}
	
	public boolean canMakeInstanceFrom(XNContext ctx, XOMVariant instance) {
		instance = instance.asPrimitive(ctx);
		if (instance instanceof XOMList) {
			List<? extends XOMVariant> l = instance.toPrimitiveList(ctx);
			if (l.size() == 1)
				if (canMakeInstanceFrom(ctx, l.get(0)))
					return true;
		}
		if (instance instanceof XOMReference) {
			XOMReference r = (XOMReference)instance;
			if (r.dereference(false) == null) return true;
			else return referentType.canMakeInstanceFrom(ctx, r.dereference(false));
		} else if (instance instanceof XOMEmpty) {
			return true;
		} else {
			XOMVariant v = XIONUtil.parseDescriptor(ctx, instance.toTextString(ctx));
			if (v instanceof XOMReference) {
				XOMReference r = (XOMReference)v;
				if (r.dereference(false) == null) return true;
				else return referentType.canMakeInstanceFrom(ctx, r.dereference(false));
			} else {
				return referentType.canMakeInstanceFrom(ctx, v);
			}
		}
	}
	public boolean canMakeInstanceFrom(XNContext ctx, XOMVariant left, XOMVariant right) {
		left = left.asPrimitive(ctx);
		right = right.asPrimitive(ctx);
		if (left instanceof XOMEmpty)
			return canMakeInstanceFrom(ctx, right);
		else if (right instanceof XOMEmpty)
			return canMakeInstanceFrom(ctx, left);
		else {
			XOMVariant v = XIONUtil.parseDescriptor(ctx, left.toTextString(ctx) + right.toTextString(ctx));
			if (v instanceof XOMReference) {
				XOMReference r = (XOMReference)v;
				if (r.dereference(false) == null) return true;
				else return referentType.canMakeInstanceFrom(ctx, r.dereference(false));
			} else {
				return referentType.canMakeInstanceFrom(ctx, v);
			}
		}
	}
	public XOMReference makeInstanceFrom(XNContext ctx, XOMVariant instance) {
		instance = instance.asPrimitive(ctx);
		if (instance instanceof XOMList) {
			List<? extends XOMVariant> l = instance.toPrimitiveList(ctx);
			if (l.size() == 1)
				if (canMakeInstanceFrom(ctx, l.get(0)))
					return makeInstanceFrom(ctx, l.get(0));
		}
		if (instance instanceof XOMReference) {
			XOMReference r = (XOMReference)instance;
			if (r.dereference(false) == null) return XOMReference.NULL_REFERENCE;
			else if (referentType.canMakeInstanceFrom(ctx, r.dereference(false))) return r;
			else throw new XOMMorphError(referentType.getTypeName() + " reference");
		} else if (instance instanceof XOMEmpty) {
			return XOMReference.NULL_REFERENCE;
		} else {
			XOMVariant v = XIONUtil.parseDescriptor(ctx, instance.toTextString(ctx));
			if (v instanceof XOMReference) {
				XOMReference r = (XOMReference)v;
				if (r.dereference(false) == null) return XOMReference.NULL_REFERENCE;
				else if (referentType.canMakeInstanceFrom(ctx, r.dereference(false))) return r;
				else throw new XOMMorphError(referentType.getTypeName() + " reference");
			} else {
				if (referentType.canMakeInstanceFrom(ctx, v)) return new XOMReference(v);
				else throw new XOMMorphError(referentType.getTypeName() + " reference");
			}
		}
	}
	public XOMReference makeInstanceFrom(XNContext ctx, XOMVariant left, XOMVariant right) {
		left = left.asPrimitive(ctx);
		right = right.asPrimitive(ctx);
		if (left instanceof XOMEmpty)
			return makeInstanceFrom(ctx, right);
		else if (right instanceof XOMEmpty)
			return makeInstanceFrom(ctx, left);
		else {
			XOMVariant v = XIONUtil.parseDescriptor(ctx, left.toTextString(ctx) + right.toTextString(ctx));
			if (v instanceof XOMReference) {
				XOMReference r = (XOMReference)v;
				if (r.dereference(false) == null) return XOMReference.NULL_REFERENCE;
				else if (referentType.canMakeInstanceFrom(ctx, r.dereference(false))) return r;
				else throw new XOMMorphError(referentType.getTypeName() + " reference");
			} else {
				if (referentType.canMakeInstanceFrom(ctx, v)) return new XOMReference(v);
				else throw new XOMMorphError(referentType.getTypeName() + " reference");
			}
		}
	}
}
