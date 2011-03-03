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
 * XOMValueDataType handles polymorphic methods for primitive value types.
 * @since OpenXION 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 * @param <IT> the corresponding subclass of XOMVariant
 * used to represent the values this data type produces.
 */
public abstract class XOMValueDataType<IT extends XOMVariant> extends XOMDataType<IT> {
	private static final long serialVersionUID = 1L;
	
	protected XOMValueDataType(String typeName, int describability, Class<IT> instanceClass) {
		super(typeName, describability, instanceClass);
	}
	
	protected abstract boolean canMakeInstanceFromImpl(XNContext ctx);
	protected abstract boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant instance);
	protected abstract boolean canMakeInstanceFromImpl(XNContext ctx, String s);
	
	protected abstract IT makeInstanceFromImpl(XNContext ctx);
	protected abstract IT makeInstanceFromImpl(XNContext ctx, XOMVariant instance);
	protected abstract IT makeInstanceFromImpl(XNContext ctx, String s);
	
	public final boolean canMakeInstanceFrom(XNContext ctx, XOMVariant instance) {
		instance = instance.asPrimitive(ctx);
		if (instance instanceof XOMList) {
			List<? extends XOMVariant> l = instance.toPrimitiveList(ctx);
			if (l.size() == 1)
				if (canMakeInstanceFrom(ctx, l.get(0)))
					return true;
		}
		if (instanceClass.isAssignableFrom(instance.getClass()))
			return true;
		else if (instance instanceof XOMEmpty && canMakeInstanceFromImpl(ctx))
			return true;
		else if (canMakeInstanceFromImpl(ctx, instance))
			return true;
		else if (canMakeInstanceFromImpl(ctx, instance.toTextString(ctx)))
			return true;
		else
			return false;
	}
	public final boolean canMakeInstanceFrom(XNContext ctx, XOMVariant left, XOMVariant right) {
		left = left.asPrimitive(ctx);
		right = right.asPrimitive(ctx);
		if (left instanceof XOMEmpty)
			return canMakeInstanceFrom(ctx, right);
		else if (right instanceof XOMEmpty)
			return canMakeInstanceFrom(ctx, left);
		else if (canMakeInstanceFromImpl(ctx, left.toTextString(ctx) + right.toTextString(ctx)))
			return true;
		else
			return false;
	}
	public final IT makeInstanceFrom(XNContext ctx, XOMVariant instance) {
		instance = instance.asPrimitive(ctx);
		if (instance instanceof XOMList) {
			List<? extends XOMVariant> l = instance.toPrimitiveList(ctx);
			if (l.size() == 1)
				if (canMakeInstanceFrom(ctx, l.get(0)))
					return makeInstanceFrom(ctx, l.get(0));
		}
		if (instanceClass.isAssignableFrom(instance.getClass()))
			return instanceClass.cast(instance);
		else if (instance instanceof XOMEmpty && canMakeInstanceFromImpl(ctx))
			return makeInstanceFromImpl(ctx);
		else if (canMakeInstanceFromImpl(ctx, instance))
			return makeInstanceFromImpl(ctx, instance);
		else if (canMakeInstanceFromImpl(ctx, instance.toTextString(ctx)))
			return makeInstanceFromImpl(ctx, instance.toTextString(ctx));
		else
			throw new XOMMorphError(typeName);
	}
	public final IT makeInstanceFrom(XNContext ctx, XOMVariant left, XOMVariant right) {
		left = left.asPrimitive(ctx);
		right = right.asPrimitive(ctx);
		if (left instanceof XOMEmpty)
			return makeInstanceFrom(ctx, right);
		else if (right instanceof XOMEmpty)
			return makeInstanceFrom(ctx, left);
		else if (canMakeInstanceFromImpl(ctx, left.toTextString(ctx) + right.toTextString(ctx)))
			return makeInstanceFromImpl(ctx, left.toTextString(ctx) + right.toTextString(ctx));
		else
			throw new XOMMorphError(typeName);
	}
}
