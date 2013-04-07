/*
 * Copyright &copy; 2012-2013 Rebecca G. Bettencourt / Kreative Software
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
 * @since OpenXION 1.5
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.jsr223;

import java.util.Arrays;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import com.kreative.openxion.XNMain;
import com.kreative.openxion.util.XIONUtil;

public class XNScriptEngineFactory implements ScriptEngineFactory {
	@Override
	public String getEngineName() {
		return XNMain.XION_NAME;
	}
	
	@Override
	public String getEngineVersion() {
		return XNMain.XION_VERSION;
	}
	
	@Override
	public String getLanguageName() {
		return "XION";
	}
	
	@Override
	public String getLanguageVersion() {
		return "1.0";
	}
	
	@Override
	public Object getParameter(String key) {
		if (key.equals(ScriptEngine.NAME)) return "xion";
		else if (key.equals(ScriptEngine.ENGINE)) return XNMain.XION_NAME;
		else if (key.equals(ScriptEngine.ENGINE_VERSION)) return XNMain.XION_VERSION;
		else if (key.equals(ScriptEngine.LANGUAGE)) return "XION";
		else if (key.equals(ScriptEngine.LANGUAGE_VERSION)) return "1.0";
		else if (key.equals("THREADING")) return null; // assume not threadsafe until fully implemented
		else return null;
	}
	
	@Override
	public List<String> getNames() {
		return Arrays.asList("xn", "xion", "XION", "openxion", "OpenXION");
	}
	
	@Override
	public List<String> getExtensions() {
		return Arrays.asList("xn");
	}
	
	@Override
	public List<String> getMimeTypes() {
		return Arrays.asList("application/x-xion", "application/xion", "text/x-xion", "text/xion");
	}
	
	@Override
	public String getMethodCallSyntax(String obj, String m, String... args) {
		StringBuffer tellString = new StringBuffer();
		tellString.append("tell java object ");
		tellString.append(XIONUtil.quote(obj));
		tellString.append(" to ");
		tellString.append(m);
		for (int i = 0; i < args.length; i++) {
			if (i == 0) tellString.append(" ");
			else tellString.append(", ");
			tellString.append(args[i]);
		}
		return tellString.toString();
	}
	
	@Override
	public String getOutputStatement(String toDisplay) {
		return "put " + XIONUtil.quote(toDisplay);
	}
	
	@Override
	public String getProgram(String... statements) {
		StringBuffer program = new StringBuffer();
		for (int i = 0; i < statements.length; i++) {
			if (i > 0) program.append("\n");
			program.append(statements[i]);
		}
		return program.toString();
	}
	
	@Override
	public ScriptEngine getScriptEngine() {
		return new XNScriptEngine(this);
	}
}
