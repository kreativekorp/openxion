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

import com.kreative.openxion.XNContext;
import com.kreative.openxion.ast.XNModifier;

/**
 * The XOMListContainer interface is implemented by any container
 * that can hold a list.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public interface XOMListContainer {
	public boolean canDeleteList(XNContext ctx);
	public void deleteList(XNContext ctx, int startElementIndex, int endElementIndex);
	
	public boolean canGetList(XNContext ctx);
	public XOMVariant getList(XNContext ctx, int startElementIndex, int endElementIndex);
	
	public boolean canPutList(XNContext ctx);
	public void putIntoList(XNContext ctx, int startElementIndex, int endElementIndex, XOMVariant contents);
	public void putBeforeList(XNContext ctx, int startElementIndex, int endElementIndex, XOMVariant contents);
	public void putAfterList(XNContext ctx, int startElementIndex, int endElementIndex, XOMVariant contents);
	public void putIntoList(XNContext ctx, int startElementIndex, int endElementIndex, XOMVariant contents, String property, XOMVariant value);
	public void putBeforeList(XNContext ctx, int startElementIndex, int endElementIndex, XOMVariant contents, String property, XOMVariant value);
	public void putAfterList(XNContext ctx, int startElementIndex, int endElementIndex, XOMVariant contents, String property, XOMVariant value);
	
	public boolean canSortList(XNContext ctx);
	public void sortList(XNContext ctx, int startElementIndex, int endElementIndex, XOMComparator cmp);
	
	public boolean canGetListProperty(XNContext ctx, String property);
	public XOMVariant getListProperty(XNContext ctx, XNModifier modifier, String property, int startElementIndex, int endElementIndex);
	
	public boolean canSetListProperty(XNContext ctx, String property);
	public void setListProperty(XNContext ctx, String property, int startElementIndex, int endElementIndex, XOMVariant value);
}
