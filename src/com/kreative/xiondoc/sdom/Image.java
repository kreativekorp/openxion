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
 * @since XIONDoc 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.xiondoc.sdom;

/**
 * An image.
 * The src property is the name of the image file.
 * The width and height properties determine the size of the image on the page.
 * The alt property provides a textual representation if the image cannot load.
 * The title property provides ToolTip text for the image.
 * @since XIONDoc 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class Image implements Span, Block {
	private static final long serialVersionUID = 1L;
	
	private int indent;
	private String src;
	private String width;
	private String height;
	private String alt;
	private String title;
	
	public Image(String src, String width, String height) {
		this.indent = 0;
		this.src = src;
		this.width = width;
		this.height = height;
		this.alt = "";
		this.title = "";
	}
	
	public Image(int indent, String src, String width, String height) {
		this.indent = indent;
		this.src = src;
		this.width = width;
		this.height = height;
		this.alt = "";
		this.title = "";
	}
	
	public Image(String src, String width, String height, String alt, String title) {
		this.indent = 0;
		this.src = src;
		this.width = width;
		this.height = height;
		this.alt = alt;
		this.title = title;
	}
	
	public Image(int indent, String src, String width, String height, String alt, String title) {
		this.indent = indent;
		this.src = src;
		this.width = width;
		this.height = height;
		this.alt = alt;
		this.title = title;
	}
	
	public int getIndent() {
		return this.indent;
	}
	
	public void setIndent(int indent) {
		this.indent = indent;
	}
	
	public String getSrc() {
		return this.src;
	}
	
	public void setSrc(String src) {
		this.src = src;
	}
	
	public boolean hasWidth() {
		return (this.width != null && this.width.length() > 0);
	}
	
	public String getWidth() {
		return this.width;
	}
	
	public void setWidth(String width) {
		this.width = width;
	}
	
	public boolean hasHeight() {
		return (this.height != null && this.height.length() > 0);
	}
	
	public String getHeight() {
		return this.height;
	}
	
	public void setHeight(String height) {
		this.height = height;
	}
	
	public boolean hasAlt() {
		return (this.alt != null && this.alt.length() > 0);
	}
	
	public String getAlt() {
		return this.alt;
	}
	
	public void setAlt(String alt) {
		this.alt = alt;
	}
	
	public boolean hasTitle() {
		return (this.title != null && this.title.length() > 0);
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
}
