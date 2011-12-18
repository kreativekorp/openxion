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
 * The XOMBinaryContainer interface is implemented by a container
 * that holds a byte array and requires special handling of binary chunks.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public interface XOMBinaryContainer {
	public boolean canDeleteBinary(XNContext ctx);
	public void deleteBinary(XNContext ctx, int startByteIndex, int endByteIndex);
	
	public boolean canGetBinary(XNContext ctx);
	public XOMVariant getBinary(XNContext ctx, int startByteIndex, int endByteIndex);
	
	public boolean canPutBinary(XNContext ctx);
	public void putIntoBinary(XNContext ctx, int startByteIndex, int endByteIndex, XOMVariant contents);
	public void putBeforeBinary(XNContext ctx, int startByteIndex, int endByteIndex, XOMVariant contents);
	public void putAfterBinary(XNContext ctx, int startByteIndex, int endByteIndex, XOMVariant contents);
	public void putIntoBinary(XNContext ctx, int startByteIndex, int endByteIndex, XOMVariant contents, String property, XOMVariant value);
	public void putBeforeBinary(XNContext ctx, int startByteIndex, int endByteIndex, XOMVariant contents, String property, XOMVariant value);
	public void putAfterBinary(XNContext ctx, int startByteIndex, int endByteIndex, XOMVariant contents, String property, XOMVariant value);
	
	public boolean canSortBinary(XNContext ctx);
	public void sortBinary(XNContext ctx, int startByteIndex, int endByteIndex, XOMComparator cmp);
	
	public boolean canGetBinaryProperty(XNContext ctx, String property);
	public XOMVariant getBinaryProperty(XNContext ctx, XNModifier modifier, String property, int startByteIndex, int endByteIndex);
	
	public boolean canSetBinaryProperty(XNContext ctx, String property);
	public void setBinaryProperty(XNContext ctx, String property, int startByteIndex, int endByteIndex, XOMVariant value);
}
