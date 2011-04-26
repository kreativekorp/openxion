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

import com.kreative.openxion.XNScriptError;

/**
 * XOMCreateError is thrown whenever an attempt is made
 * to create a variant, but the data type cannot be
 * instantiated using that kind of descriptor.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XOMCreateError extends XNScriptError {
	private static final long serialVersionUID = 1L;
	
	public XOMCreateError(String typename) {
		super("Can't create "+typename);
	}
	
	public XOMCreateError(String typename, int startIndex, int endIndex) {
		super("Can't create "+typename+" "+
				((startIndex == endIndex)?Integer.toString(startIndex):(startIndex+" through "+endIndex)));
	}
	
	public XOMCreateError(String typename, int id) {
		super("Can't create "+typename+" id "+id);
	}
	
	public XOMCreateError(String typename, String name) {
		super("Can't create "+typename+" \""+name+"\"");
	}
}
