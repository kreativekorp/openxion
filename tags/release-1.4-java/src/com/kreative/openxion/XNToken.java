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

package com.kreative.openxion;

import java.io.Serializable;

/**
 * XNToken represents a token, or word, in the XION grammar.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XNToken implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	public static final int LINE_TERM = 0;
	public static final int QUOTED = 1;
	public static final int NUMBER = 2;
	public static final int ID = 3;
	public static final int SYMBOL = 4;
	public static final int COMMENT = 5;
	public static final int CONTINUATOR = 6;
	public static final int WHITESPACE = 7;
	
	public int kind;
	public String image;
	public Object source;
	public int beginLine;
	public int beginColumn;
	public int endLine;
	public int endColumn;
	public XNToken next;
	public XNToken specialToken;
	
	public XNToken(int kind, String image, Object source, int bl, int bc, int el, int ec) {
		this.kind = kind;
		this.image = image;
		this.source = source;
		this.beginLine = bl;
		this.beginColumn = bc;
		this.endLine = el;
		this.endColumn = ec;
	}
	
	public String toString() {
		return this.image;
	}
	
	public boolean isEOF() {
		return ((kind == LINE_TERM) && (image == null || image.length() == 0))
			|| ((kind == ID) && (image != null) && image.equals("__END__"));
	}
}
