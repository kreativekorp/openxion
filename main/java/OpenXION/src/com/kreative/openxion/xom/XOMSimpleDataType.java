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

package com.kreative.openxion.xom;

import java.util.List;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.xom.inst.XOMEmpty;
import com.kreative.openxion.xom.inst.XOMList;

/**
 * XOMSimpleDataType handles some instances
 * of the polymorphic methods for simple data types.
 * @since OpenXION 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 * @param <IT> the corresponding subclass of XOMVariant
 * used to represent the values this data type produces.
 */
public abstract class XOMSimpleDataType<IT extends XOMVariant> extends XOMDataType<IT> {
	private static final long serialVersionUID = 1L;
	
	protected XOMSimpleDataType(String typeName, int describability, Class<IT> instanceClass) {
		super(typeName, describability, instanceClass);
	}
	
	protected abstract boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant instance);
	protected abstract boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant left, XOMVariant right);
	protected abstract IT makeInstanceFromImpl(XNContext ctx, XOMVariant instance);
	protected abstract IT makeInstanceFromImpl(XNContext ctx, XOMVariant left, XOMVariant right);
	
	public final boolean canMakeInstanceFrom(XNContext ctx, XOMVariant instance) {
		instance = instance.asValue(ctx);
		if (instanceClass.isAssignableFrom(instance.getClass())) {
			return true;
		} else if (instance instanceof XOMList) {
			List<? extends XOMVariant> l = instance.toVariantList(ctx);
			if (l.size() == 1) {
				if (instanceClass.isAssignableFrom(l.get(0).getClass())) {
					return true;
				} else {
					return canMakeInstanceFromImpl(ctx, l.get(0));
				}
			} else {
				return canMakeInstanceFromImpl(ctx, instance);
			}
		} else {
			return canMakeInstanceFromImpl(ctx, instance);
		}
	}
	public final boolean canMakeInstanceFrom(XNContext ctx, XOMVariant left, XOMVariant right) {
		left = left.asValue(ctx);
		right = right.asValue(ctx);
		if (left instanceof XOMEmpty) {
			return canMakeInstanceFrom(ctx, right);
		} else if (right instanceof XOMEmpty) {
			return canMakeInstanceFrom(ctx, left);
		} else {
			return canMakeInstanceFromImpl(ctx, left, right);
		}
	}
	public final IT makeInstanceFrom(XNContext ctx, XOMVariant instance) {
		instance = instance.asValue(ctx);
		if (instanceClass.isAssignableFrom(instance.getClass())) {
			return instanceClass.cast(instance);
		} else if (instance instanceof XOMList) {
			List<? extends XOMVariant> l = instance.toVariantList(ctx);
			if (l.size() == 1) {
				if (instanceClass.isAssignableFrom(l.get(0).getClass())) {
					return instanceClass.cast(l.get(0));
				} else {
					return makeInstanceFromImpl(ctx, l.get(0));
				}
			} else {
				return makeInstanceFromImpl(ctx, instance);
			}
		} else {
			return makeInstanceFromImpl(ctx, instance);
		}
	}
	public final IT makeInstanceFrom(XNContext ctx, XOMVariant left, XOMVariant right) {
		left = left.asValue(ctx);
		right = right.asValue(ctx);
		if (left instanceof XOMEmpty) {
			return makeInstanceFrom(ctx, right);
		} else if (right instanceof XOMEmpty) {
			return makeInstanceFrom(ctx, left);
		} else {
			return makeInstanceFromImpl(ctx, left, right);
		}
	}
}
