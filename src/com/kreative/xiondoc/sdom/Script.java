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
 * An XION script, rendered in a monospaced font with line breaks and whitespace preserved.
 * The content is the XION script.
 * @since XIONDoc 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class Script implements Block {
	private static final long serialVersionUID = 1L;
	
	private int indent;
	private String script;
	
	public Script(String script) {
		this.indent = 0;
		this.script = script;
	}
	
	public Script(String script, boolean reformat) {
		this.indent = 0;
		this.script = reformat ? reformat(script) : script;
	}
	
	public Script(int indent, String script) {
		this.indent = indent;
		this.script = script;
	}
	
	public Script(int indent, String script, boolean reformat) {
		this.indent = indent;
		this.script = reformat ? reformat(script) : script;
	}
	
	public int getIndent() {
		return this.indent;
	}
	
	public void setIndent(int indent) {
		this.indent = indent;
	}
	
	public String getScript() {
		return this.script;
	}
	
	public void setScript(String script) {
		this.script = script;
	}
	
	public void setScript(String script, boolean reformat) {
		this.script = reformat ? reformat(script) : script;
	}
	
	public String toString() {
		return this.script;
	}
	
	private static String reformat(String script) {
		// split into lines
		String[] lines = script.split("\r\n|\r|\n|\u2028|\u2029");
		// remove trailing whitespace from all lines
		for (int i = 0; i < lines.length; i++) {
			int l = lines[i].length();
			while (l > 0 && Character.isWhitespace(lines[i].charAt(l-1))) l--;
			lines[i] = lines[i].substring(0, l);
		}
		// remove blank lines from end
		int e = lines.length;
		while (e > 0 && lines[e-1].length() == 0) e--;
		// remove blank lines from beginning
		int s = 0;
		while (s < e && lines[s].length() == 0) s++;
		// find the number of leading whitespace characters on the first non-blank line
		int wsl = 0;
		if (s < e)
			while (wsl < lines[s].length() && Character.isWhitespace(lines[s].charAt(wsl)))
				wsl++;
		// remove only that number of leading whitespace characters from all lines
		for (int i = s; i < e; i++) {
			int o = 0;
			while (o < wsl && o < lines[i].length() && Character.isWhitespace(lines[i].charAt(o))) o++;
			lines[i] = lines[i].substring(o);
		}
		// join the lines
		StringBuffer newscript = new StringBuffer();
		for (int i = s; i < e; i++) {
			newscript.append(lines[i]);
			newscript.append('\n');
		}
		if (newscript.length() > 0) newscript.deleteCharAt(newscript.length()-1);
		return newscript.toString();
	}
}
