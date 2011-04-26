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

package com.kreative.openxion.xom.inst;

import java.util.Arrays;
import java.util.List;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.XNScriptError;
import com.kreative.openxion.ast.XNModifier;
import com.kreative.openxion.xom.XOMObject;
import com.kreative.openxion.xom.XOMVariant;

public class XOMReference extends XOMObject {
	private static final long serialVersionUID = 1L;
	
	public static final XOMReference NULL_REFERENCE = new XOMReference();
	
	private XOMVariant theReferent;
	
	private XOMReference() {
		theReferent = null;
	}
	
	public XOMReference(XOMVariant v) {
		theReferent = v;
	}
	
	public XOMVariant dereference(boolean notNull) {
		if (theReferent == null && notNull) return XOMEmpty.EMPTY;
		return theReferent;
	}
	
	public final boolean canGetParent(XNContext ctx) {
		if (theReferent == null) return false;
		return true;
	}
	public final XOMVariant getParent(XNContext ctx) {
		if (theReferent == null) throw new XNScriptError("Can't access a null object");
		return theReferent;
	}
	
	public final boolean canDelete(XNContext ctx) {
		if (theReferent == null) return false;
		return theReferent.canDelete(ctx);
	}
	public final void delete(XNContext ctx) {
		if (theReferent == null) throw new XNScriptError("Can't access a null object");
		theReferent.delete(ctx);
	}
	
	public final boolean canGetProperty(XNContext ctx, String property) {
		if (theReferent == null) return false;
		return theReferent.canGetProperty(ctx, property);
	}
	public final XOMVariant getProperty(XNContext ctx, XNModifier modifier, String property) {
		if (theReferent == null) throw new XNScriptError("Can't access a null object");
		return theReferent.getProperty(ctx, modifier, property);
	}
	
	public final boolean canSetProperty(XNContext ctx, String property) {
		if (theReferent == null) return false;
		return theReferent.canSetProperty(ctx, property);
	}
	public final void setProperty(XNContext ctx, String property, XOMVariant value) {
		if (theReferent == null) throw new XNScriptError("Can't access a null object");
		theReferent.setProperty(ctx, property, value);
	}
	
	public String toLanguageString() {
		if (theReferent == null) return "empty";
		else return "a reference to " + theReferent.toLanguageString();
	}
	public String toTextString(XNContext ctx) {
		if (theReferent == null) return "";
		else return theReferent.toLanguageString();
	}
	public List<? extends XOMVariant> toVariantList(XNContext ctx) {
		return Arrays.asList(this);
	}
	public List<? extends XOMVariant> toPrimitiveList(XNContext ctx) {
		return Arrays.asList(this);
	}
	public int hashCode() {
		return (theReferent == null) ? 0 : theReferent.hashCode();
	}
	public boolean equals(Object o) {
		if (o instanceof XOMReference) {
			XOMReference other = (XOMReference)o;
			if (this.theReferent == null && other.theReferent == null) return true;
			else if (this.theReferent == null || other.theReferent == null) return false;
			else return this.theReferent.equals(other.theReferent);
		} else {
			return false;
		}
	}
}
