/*
 * Copyright &copy; 2009-2010 Rebecca G. Bettencourt / Kreative Software
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

import com.kreative.openxion.XNContext;
import com.kreative.openxion.ast.XNModifier;

/**
 * The XOMStringContainer interface is implemented by any container
 * that can hold a string.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public interface XOMStringContainer {
	public boolean canDeleteString(XNContext ctx);
	public void deleteString(XNContext ctx, int startCharIndex, int endCharIndex);
	
	public boolean canGetString(XNContext ctx);
	public XOMVariant getString(XNContext ctx, int startCharIndex, int endCharIndex);
	
	public boolean canPutString(XNContext ctx);
	public void putIntoString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents);
	public void putBeforeString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents);
	public void putAfterString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents);
	public void putIntoString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents, String property, XOMVariant value);
	public void putBeforeString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents, String property, XOMVariant value);
	public void putAfterString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents, String property, XOMVariant value);
	
	public boolean canRearrangeString(XNContext ctx);
	public void rearrangeString(XNContext ctx, int[] startIndexes, int[] endIndexes);
	
	public boolean canGetStringProperty(XNContext ctx, String property);
	public XOMVariant getStringProperty(XNContext ctx, XNModifier modifier, String property, int startCharIndex, int endCharIndex);
	
	public boolean canSetStringProperty(XNContext ctx, String property);
	public void setStringProperty(XNContext ctx, String property, int startCharIndex, int endCharIndex, XOMVariant value);
}
