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

package com.kreative.openxion;

/**
 * XNScriptError is the parent class of any exception
 * thrown when an error is encountered in XION code.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XNScriptError extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private int line, col;
	private Exception originalError;
	
	public XNScriptError() {
		super();
		this.line = 0;
		this.col = 0;
		this.originalError = null;
	}
	
	public XNScriptError(String s) {
		super(s);
		this.line = 0;
		this.col = 0;
		this.originalError = null;
	}
	
	public XNScriptError(int line, int col) {
		super();
		this.line = line;
		this.col = col;
		this.originalError = null;
	}
	
	public XNScriptError(int line, int col, String s) {
		super(s);
		this.line = line;
		this.col = col;
		this.originalError = null;
	}
	
	public XNScriptError(Exception oe) {
		super();
		this.line = 0;
		this.col = 0;
		this.originalError = oe;
	}
	
	public XNScriptError(Exception oe, String s) {
		super(s);
		this.line = 0;
		this.col = 0;
		this.originalError = oe;
	}
	
	public XNScriptError(Exception oe, int line, int col) {
		super();
		this.line = line;
		this.col = col;
		this.originalError = oe;
	}
	
	public XNScriptError(Exception oe, int line, int col, String s) {
		super(s);
		this.line = line;
		this.col = col;
		this.originalError = oe;
	}
	
	public int getLine() {
		return line;
	}
	
	public int getCol() {
		return col;
	}
	
	/* package */ void setLineAndCol(int line, int col) {
		if (this.line == 0 && this.col == 0) {
			this.line = line;
			this.col = col;
		}
	}
	
	public Exception getOriginalException() {
		return originalError;
	}
}
