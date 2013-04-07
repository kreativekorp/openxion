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

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import com.kreative.openxion.XNAudioModule;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.XNExtendedModule;
import com.kreative.openxion.XNInterpreter;
import com.kreative.openxion.XNLexer;
import com.kreative.openxion.XNParser;
import com.kreative.openxion.XNStandardModule;
import com.kreative.openxion.XNStdInOutUI;
import com.kreative.openxion.XNUI;
import com.kreative.openxion.ast.XNStatement;

public class XNScriptEngine extends AbstractScriptEngine {
	private XNScriptEngineFactory factory;
	private XNUI ui;
	private XNContext ctx;
	private XNInterpreter interp;
	private XOMConverter conv;
	
	public XNScriptEngine(XNScriptEngineFactory factory) {
		this.factory = factory;
		this.ui = new XNStdInOutUI(false);
		this.ctx = new XNContext(this.ui);
		this.ctx.loadModule(XNStandardModule.instance());
		this.ctx.loadModule(XNExtendedModule.instance());
		this.ctx.loadModule(XNAudioModule.instance());
		this.interp = new XNInterpreter(this.ctx);
		this.conv = new XOMConverter(this.ctx);
	}
	
	@Override
	public Bindings createBindings() {
		return new SimpleBindings();
	}
	
	@Override
	public Object eval(String script, ScriptContext context) throws ScriptException {
		XNLexer l = new XNLexer(script, new StringReader(script));
		XNParser p = new XNParser(ctx, l);
		List<XNStatement> program = p.parse();
		interp.executeScript(program);
		return conv.toNative(ctx.getResult());
	}
	
	@Override
	public Object eval(Reader script, ScriptContext context) throws ScriptException {
		XNLexer l = new XNLexer(script, script);
		XNParser p = new XNParser(ctx, l);
		List<XNStatement> program = p.parse();
		interp.executeScript(program);
		return conv.toNative(ctx.getResult());
	}
	
	@Override
	public ScriptEngineFactory getFactory() {
		return factory;
	}
}
