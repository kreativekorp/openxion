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

package com.kreative.openxion.ast;

import com.kreative.openxion.XNToken;

public class XNVariantOrdinalDescriptor extends XNVariantDescriptor {
	private static final long serialVersionUID = 1004L;
	
	public XNToken theToken;
	public XNToken startOrdinal;
	public XNToken toToken;
	public XNToken endOrdinal;
	
	protected Object internalGetSource() {
		if (theToken != null) return theToken.source;
		else if (startOrdinal != null) return startOrdinal.source;
		else if (toToken != null) return toToken.source;
		else if (endOrdinal != null) return endOrdinal.source;
		else if (dtTokens != null && dtTokens.length > 0) return dtTokens[0].source;
		else return null;
	}
	
	protected int internalGetBeginCol() {
		if (theToken != null) return theToken.beginColumn;
		else if (startOrdinal != null) return startOrdinal.beginColumn;
		else if (toToken != null) return toToken.beginColumn;
		else if (endOrdinal != null) return endOrdinal.beginColumn;
		else if (dtTokens != null && dtTokens.length > 0) return dtTokens[0].beginColumn;
		else return 0;
	}
	
	protected int internalGetBeginLine() {
		if (theToken != null) return theToken.beginLine;
		else if (startOrdinal != null) return startOrdinal.beginLine;
		else if (toToken != null) return toToken.beginLine;
		else if (endOrdinal != null) return endOrdinal.beginLine;
		else if (dtTokens != null && dtTokens.length > 0) return dtTokens[0].beginLine;
		else return 0;
	}
	
	protected int internalGetEndCol() {
		if (dtTokens != null && dtTokens.length > 0) return dtTokens[dtTokens.length-1].endColumn;
		else if (endOrdinal != null) return endOrdinal.endColumn;
		else if (toToken != null) return toToken.endColumn;
		else if (startOrdinal != null) return startOrdinal.endColumn;
		else if (theToken != null) return theToken.endColumn;
		else return 0;
	}
	
	protected int internalGetEndLine() {
		if (dtTokens != null && dtTokens.length > 0) return dtTokens[dtTokens.length-1].endLine;
		else if (endOrdinal != null) return endOrdinal.endLine;
		else if (toToken != null) return toToken.endLine;
		else if (startOrdinal != null) return startOrdinal.endLine;
		else if (theToken != null) return theToken.endLine;
		else return 0;
	}
	
	protected String internalToString() {
		String s = "the";
		if (startOrdinal != null) s += " "+startOrdinal.image.toLowerCase();
		if (endOrdinal != null) s += " through "+endOrdinal.image.toLowerCase();
		if (datatype != null) {
			for (String dt : datatype.name) {
				s += " "+dt;
			}
		}
		return s;
	}
}
