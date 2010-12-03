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
 * @since OpenXION 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.xiondoc;

public class Article implements Comparable<Article> {
	private String code;
	private String title;
	private String summary;
	private String content;
	
	public Article(String code, String title, String summary, String content) {
		this.code = code;
		this.title = title;
		this.summary = summary;
		this.content = content;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getSummary() {
		return summary;
	}
	
	public String getContent() {
		return content;
	}
	
	public String toString() {
		return title;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Article) {
			Article other = (Article)o;
			return this.code.equalsIgnoreCase(other.code) && this.title.equalsIgnoreCase(other.title) && this.content.equalsIgnoreCase(other.content);
		}
		return false;
	}
	
	public int compareTo(Article other) {
		return this.title.compareToIgnoreCase(other.title);
	}
	
	public int hashCode() {
		return code.toLowerCase().hashCode() ^ title.toLowerCase().hashCode() ^ content.toLowerCase().hashCode();
	}
}
