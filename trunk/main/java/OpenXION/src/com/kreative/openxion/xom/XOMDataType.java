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

/**
 * XOMDataType represents data types in XION.
 * It is responsible for retrieving or creating variants,
 * and morphing variants from one data type to another.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 * @param <IT> the corresponding subclass of XOMVariant
 * used to represent the values this data type produces.
 */
public abstract class XOMDataType<IT extends XOMVariant> implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	public static final int DESCRIBABLE_BY_SINGLETON = 0x1;
	public static final int DESCRIBABLE_BY_INDEX = 0x2;
	public static final int DESCRIBABLE_BY_INDEX_RANGE = 0x4;
	public static final int DESCRIBABLE_BY_ORDINAL = 0x8;
	public static final int DESCRIBABLE_BY_ORDINAL_RANGE = 0x10;
	public static final int DESCRIBABLE_BY_ID = 0x20;
	public static final int DESCRIBABLE_BY_NAME = 0x40;
	public static final int DESCRIBABLE_BY_MASS = 0x80;
	
	/* used by parser */
	public static final int DESCRIBABLE_BY_IDXNAMEIR =
		DESCRIBABLE_BY_INDEX |
		DESCRIBABLE_BY_NAME |
		DESCRIBABLE_BY_INDEX_RANGE;
	
	/* used for built in data types */
	public static final int DESCRIBABILITY_OF_PRIMITIVES =
		0;
	public static final int DESCRIBABILITY_OF_SINGULAR_INTERPRETERS =
		DESCRIBABLE_BY_SINGLETON;
	public static final int DESCRIBABILITY_OF_PLURAL_INTERPRETERS =
		0;
	public static final int DESCRIBABILITY_OF_SINGULAR_BINARY_NUMERIC_CHUNKS =
		DESCRIBABLE_BY_INDEX |
		DESCRIBABLE_BY_ORDINAL;
	public static final int DESCRIBABILITY_OF_PLURAL_BINARY_NUMERIC_CHUNKS =
		0;
	public static final int DESCRIBABILITY_OF_SINGULAR_CHUNKS =
		DESCRIBABLE_BY_INDEX |
		DESCRIBABLE_BY_INDEX_RANGE |
		DESCRIBABLE_BY_ORDINAL |
		DESCRIBABLE_BY_ORDINAL_RANGE;
	public static final int DESCRIBABILITY_OF_PLURAL_CHUNKS =
		DESCRIBABLE_BY_INDEX_RANGE |
		DESCRIBABLE_BY_MASS |
		DESCRIBABLE_BY_ORDINAL_RANGE;
	public static final int DESCRIBABILITY_OF_SINGULAR_FSOBJECTS =
		DESCRIBABLE_BY_INDEX |
		DESCRIBABLE_BY_NAME |
		DESCRIBABLE_BY_ORDINAL;
	public static final int DESCRIBABILITY_OF_PLURAL_FSOBJECTS =
		DESCRIBABLE_BY_INDEX_RANGE |
		DESCRIBABLE_BY_MASS |
		DESCRIBABLE_BY_ORDINAL_RANGE;
	public static final int DESCRIBABILITY_OF_SINGULAR_USER_OBJECTS =
		DESCRIBABLE_BY_INDEX |
		DESCRIBABLE_BY_ORDINAL |
		DESCRIBABLE_BY_ID |
		DESCRIBABLE_BY_NAME;
	public static final int DESCRIBABILITY_OF_PLURAL_USER_OBJECTS =
		DESCRIBABLE_BY_INDEX_RANGE |
		DESCRIBABLE_BY_MASS |
		DESCRIBABLE_BY_ORDINAL_RANGE;
	
	protected String typeName;
	protected int describability;
	protected Class<IT> instanceClass;
	protected XOMDataType(String typeName, int describability, Class<IT> instanceClass) {
		this.typeName = typeName;
		this.describability = describability;
		this.instanceClass = instanceClass;
	}
	public final String getTypeName() {
		return typeName;
	}
	public final int getDescribability() {
		return describability;
	}
	public final Class<IT> getInstanceClass() {
		return instanceClass;
	}
	
	/*
	 * Instantiation of root variants of this type.
	 */
	
	public boolean canGetSingletonInstance(XNContext ctx) { return false; }
	public boolean canGetMassInstance(XNContext ctx) { return false; }
	public XOMVariant getSingletonInstance(XNContext ctx) { throw new XOMGetError(typeName); }
	public XOMVariant getMassInstance(XNContext ctx) { throw new XOMGetError(typeName); }

	public boolean canGetInstanceByIndex(XNContext ctx, int index) { return false; }
	public boolean canGetInstanceByIndex(XNContext ctx, int startIndex, int endIndex) { return false; }
	public boolean canGetInstanceByID(XNContext ctx, int id) { return false; }
	public boolean canGetInstanceByName(XNContext ctx, String name) { return false; }
	public XOMVariant getInstanceByIndex(XNContext ctx, int index) { throw new XOMGetError(typeName, index, index); }
	public XOMVariant getInstanceByIndex(XNContext ctx, int startIndex, int endIndex) { throw new XOMGetError(typeName, startIndex, endIndex); }
	public XOMVariant getInstanceByID(XNContext ctx, int id) { throw new XOMGetError(typeName, id); }
	public XOMVariant getInstanceByName(XNContext ctx, String name) { throw new XOMGetError(typeName, name); }
	
	public boolean canCreateInstance(XNContext ctx) { return false; }
	public XOMVariant createInstance(XNContext ctx) { throw new XOMCreateError(typeName); }
	
	public boolean canCreateInstanceByIndex(XNContext ctx, int index) { return false; }
	public boolean canCreateInstanceByIndex(XNContext ctx, int startIndex, int endIndex) { return false; }
	public boolean canCreateInstanceByID(XNContext ctx, int id) { return false; }
	public boolean canCreateInstanceByName(XNContext ctx, String name) { return false; }
	public XOMVariant createInstanceByIndex(XNContext ctx, int index) { throw new XOMCreateError(typeName, index, index); }
	public XOMVariant createInstanceByIndex(XNContext ctx, int startIndex, int endIndex) { throw new XOMCreateError(typeName, startIndex, endIndex); }
	public XOMVariant createInstanceByID(XNContext ctx, int id) { throw new XOMCreateError(typeName, id); }
	public XOMVariant createInstanceByName(XNContext ctx, String name) { throw new XOMCreateError(typeName, name); }

	public boolean canCreateInstanceByIndexWith(XNContext ctx, int index, XOMVariant contents) { return false; }
	public boolean canCreateInstanceByIndexWith(XNContext ctx, int startIndex, int endIndex, XOMVariant contents) { return false; }
	public boolean canCreateInstanceByIDWith(XNContext ctx, int id, XOMVariant contents) { return false; }
	public boolean canCreateInstanceByNameWith(XNContext ctx, String name, XOMVariant contents) { return false; }
	public XOMVariant createInstanceByIndexWith(XNContext ctx, int index, XOMVariant contents) { throw new XOMCreateError(typeName, index, index); }
	public XOMVariant createInstanceByIndexWith(XNContext ctx, int startIndex, int endIndex, XOMVariant contents) { throw new XOMCreateError(typeName, startIndex, endIndex); }
	public XOMVariant createInstanceByIDWith(XNContext ctx, int id, XOMVariant contents) { throw new XOMCreateError(typeName, id); }
	public XOMVariant createInstanceByNameWith(XNContext ctx, String name, XOMVariant contents) { throw new XOMCreateError(typeName, name); }
	
	/*
	 * Instantiation of child variants of this type.
	 */

	public boolean canGetChildSingletonVariant(XNContext ctx, XOMVariant parent) { return false; }
	public boolean canGetChildMassVariant(XNContext ctx, XOMVariant parent) { return false; }
	public XOMVariant getChildSingletonVariant(XNContext ctx, XOMVariant parent) { throw new XOMGetError(typeName); }
	public XOMVariant getChildMassVariant(XNContext ctx, XOMVariant parent) { throw new XOMGetError(typeName); }

	public boolean canGetChildVariantByIndex(XNContext ctx, XOMVariant parent, int index) { return false; }
	public boolean canGetChildVariantByIndex(XNContext ctx, XOMVariant parent, int startIndex, int endIndex) { return false; }
	public boolean canGetChildVariantByID(XNContext ctx, XOMVariant parent, int id) { return false; }
	public boolean canGetChildVariantByName(XNContext ctx, XOMVariant parent, String name) { return false; }
	public XOMVariant getChildVariantByIndex(XNContext ctx, XOMVariant parent, int index) { throw new XOMGetError(typeName, index, index); }
	public XOMVariant getChildVariantByIndex(XNContext ctx, XOMVariant parent, int startIndex, int endIndex) { throw new XOMGetError(typeName, startIndex, endIndex); }
	public XOMVariant getChildVariantByID(XNContext ctx, XOMVariant parent, int id) { throw new XOMGetError(typeName, id); }
	public XOMVariant getChildVariantByName(XNContext ctx, XOMVariant parent, String name) { throw new XOMGetError(typeName, name); }
	
	public boolean canCreateChildVariant(XNContext ctx, XOMVariant parent) { return false; }
	public XOMVariant createChildVariant(XNContext ctx, XOMVariant parent) { throw new XOMCreateError(typeName); }

	public boolean canCreateChildVariantByIndex(XNContext ctx, XOMVariant parent, int index) { return false; }
	public boolean canCreateChildVariantByIndex(XNContext ctx, XOMVariant parent, int startIndex, int endIndex) { return false; }
	public boolean canCreateChildVariantByID(XNContext ctx, XOMVariant parent, int id) { return false; }
	public boolean canCreateChildVariantByName(XNContext ctx, XOMVariant parent, String name) { return false; }
	public XOMVariant createChildVariantByIndex(XNContext ctx, XOMVariant parent, int index) { throw new XOMCreateError(typeName, index, index); }
	public XOMVariant createChildVariantByIndex(XNContext ctx, XOMVariant parent, int startIndex, int endIndex) { throw new XOMCreateError(typeName, startIndex, endIndex); }
	public XOMVariant createChildVariantByID(XNContext ctx, XOMVariant parent, int id) { throw new XOMCreateError(typeName, id); }
	public XOMVariant createChildVariantByName(XNContext ctx, XOMVariant parent, String name) { throw new XOMCreateError(typeName, name); }

	public boolean canCreateChildVariantByIndexWith(XNContext ctx, XOMVariant parent, int index, XOMVariant contents) { return false; }
	public boolean canCreateChildVariantByIndexWith(XNContext ctx, XOMVariant parent, int startIndex, int endIndex, XOMVariant contents) { return false; }
	public boolean canCreateChildVariantByIDWith(XNContext ctx, XOMVariant parent, int id, XOMVariant contents) { return false; }
	public boolean canCreateChildVariantByNameWith(XNContext ctx, XOMVariant parent, String name, XOMVariant contents) { return false; }
	public XOMVariant createChildVariantByIndexWith(XNContext ctx, XOMVariant parent, int index, XOMVariant contents) { throw new XOMCreateError(typeName, index, index); }
	public XOMVariant createChildVariantByIndexWith(XNContext ctx, XOMVariant parent, int startIndex, int endIndex, XOMVariant contents) { throw new XOMCreateError(typeName, startIndex, endIndex); }
	public XOMVariant createChildVariantByIDWith(XNContext ctx, XOMVariant parent, int id, XOMVariant contents) { throw new XOMCreateError(typeName, id); }
	public XOMVariant createChildVariantByNameWith(XNContext ctx, XOMVariant parent, String name, XOMVariant contents) { throw new XOMCreateError(typeName, name); }
	
	/*
	 * Polymorphism - The data type of an object is determined through these methods.
	 * Unlike in Java, where an object's type is determined by the class hierarchy,
	 * objects in XION can be of any mix of data types (hence the term variant for XION objects).
	 */
	
	public final boolean canMakeInstanceFrom(XNContext ctx, XOMVariant instance) {
		return canMakeInstanceFromImpl(ctx, instance.unwrap(ctx));
	}
	public final boolean canMakeInstanceFrom(XNContext ctx, XOMVariant left, XOMVariant right) {
		return canMakeInstanceFromImpl(ctx, left.unwrap(ctx), right.unwrap(ctx));
	}
	public final IT makeInstanceFrom(XNContext ctx, XOMVariant instance) {
		return makeInstanceFromImpl(ctx, instance.unwrap(ctx));
	}
	public final IT makeInstanceFrom(XNContext ctx, XOMVariant left, XOMVariant right) {
		return makeInstanceFromImpl(ctx, left.unwrap(ctx), right.unwrap(ctx));
	}
	protected abstract boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant instance);
	protected abstract boolean canMakeInstanceFromImpl(XNContext ctx, XOMVariant left, XOMVariant right);
	protected abstract IT makeInstanceFromImpl(XNContext ctx, XOMVariant instance);
	protected abstract IT makeInstanceFromImpl(XNContext ctx, XOMVariant left, XOMVariant right);
}
