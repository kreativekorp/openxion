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

package com.kreative.openxion.xom;

import java.io.Serializable;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.ast.XNModifier;

/**
 * XOMVariant represents all variants (instances of data types) in XION.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public abstract class XOMVariant implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Returns this variant as originally given by the interpreter.
	 * This may be a variable, a container, or a primitive.
	 * @return this variant as originally given by the interpreter.
	 */
	public abstract XOMVariant asGiven();
	
	/**
	 * If this is a variable, returns the variable's value.
	 * Otherwise, returns this variant as given.
	 * @param ctx the context.
	 * @return if this is a variable, the variable's value;
	 * otherwise, this variant as given.
	 */
	public abstract XOMVariant asValue(XNContext ctx);
	
	/**
	 * If this is a variable, returns the variable's value.
	 * If this is a container, returns the container's contents.
	 * Otherwise, returns this variant as given.
	 * @param ctx the context.
	 * @return if this is a variable, the variable's value;
	 * if this is a container, the container's contents;
	 * otherwise, this variant as given.
	 */
	public abstract XOMVariant asContents(XNContext ctx);
	
	/**
	 * If this is a variable, returns the variable's value as a primitive.
	 * If this is a container, returns the container's contents as a primitive.
	 * Otherwise, returns this variant as given.
	 * @param ctx the context.
	 * @return if this is a variable, the variable's value as a primitive;
	 * if this is a container, the container's contents as a primitive;
	 * otherwise, this variant as given.
	 */
	public abstract XOMVariant asPrimitive(XNContext ctx);
	
	/**
	 * If this is a variable or container, returns this variant as given.
	 * Otherwise, treats this variant as a variable name and returns the
	 * corresponding XOMVariable from the specified XNContext.
	 * @param ctx the context.
	 * @return if this is a variable or container, this variant as given;
	 * otherwise, the XOMVariable corresponding to the variable name given
	 * by this variant's value.
	 */
	public abstract XOMVariant asContainer(XNContext ctx);
	
	/**
	 * If this is a variable, returns this variant as given.
	 * Otherwise, treats this variant as a variable name and returns the
	 * corresponding XOMVariable from the specified XNContext.
	 * @param ctx the context.
	 * @return if this is a variable, this variant as given;
	 * otherwise, the XOMVariable corresponding to the variable name given
	 * by this variant's value.
	 */
	public abstract XOMVariable asVariable(XNContext ctx);
	
	public abstract boolean canGetParent(XNContext ctx);
	public abstract XOMVariant getParent(XNContext ctx);
	
	public abstract boolean canDelete(XNContext ctx);
	public abstract void delete(XNContext ctx);
	
	public abstract boolean canGetContents(XNContext ctx);
	public abstract XOMVariant getContents(XNContext ctx);
	
	public abstract boolean canPutContents(XNContext ctx);
	public abstract void putIntoContents(XNContext ctx, XOMVariant contents);
	public abstract void putBeforeContents(XNContext ctx, XOMVariant contents);
	public abstract void putAfterContents(XNContext ctx, XOMVariant contents);
	public abstract void putIntoContents(XNContext ctx, XOMVariant contents, String property, XOMVariant value);
	public abstract void putBeforeContents(XNContext ctx, XOMVariant contents, String property, XOMVariant value);
	public abstract void putAfterContents(XNContext ctx, XOMVariant contents, String property, XOMVariant value);
	
	public abstract boolean canSortContents(XNContext ctx);
	public abstract void sortContents(XNContext ctx, XOMComparator cmp);
	
	public abstract boolean canGetProperty(XNContext ctx, String property);
	public abstract XOMVariant getProperty(XNContext ctx, XNModifier modifier, String property);
	
	public abstract boolean canSetProperty(XNContext ctx, String property);
	public abstract void setProperty(XNContext ctx, String property, XOMVariant value);
	
	/**
	 * Returns this variant as XION source code.
	 * THIS IS PROVIDED SOLELY FOR DEBUGGING PURPOSES.
	 * NEVER USE THIS INSIDE OPENXION ITSELF!
	 * @return this variant as XION source code.
	 */
	@Deprecated
	public final String toString() {
		return asGiven().toLanguageStringImpl();
	}
	
	/**
	 * Returns this variant as XION source code.
	 * @return this variant as XION source code.
	 */
	public final String toLanguageString() {
		return asGiven().toLanguageStringImpl();
	}
	
	/**
	 * Returns this variant's value as a string.
	 * @return this variant's value as a string.
	 */
	public final String toTextString(XNContext ctx) {
		return asGiven().toTextStringImpl(ctx);
	}
	
	/**
	 * Returns a hash code for this variant.
	 * @return a hash code for this variant.
	 */
	public final int hashCode() {
		return asGiven().hashCodeImpl();
	}
	
	/**
	 * Determines if two XOMHandles refer to the same XOMVrnt.
	 * @return true if the two XOMHandles are equal, false otherwise.
	 */
	public final boolean equals(Object o) {
		if (o instanceof XOMVariant) {
			XOMVariant other = ((XOMVariant)o).asGiven();
			return asGiven().equalsImpl(other);
		} else {
			return false;
		}
	}
	
	protected abstract String toLanguageStringImpl();
	protected abstract String toTextStringImpl(XNContext ctx);
	protected abstract int hashCodeImpl();
	protected abstract boolean equalsImpl(XOMVariant other);
}
