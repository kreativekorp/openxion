/**
 * This is the top-level package of OpenXION 1.0, the standard reference
 * implementation of the XION Scripting Language Standard, Version 1.0.
 * <p>
 * To start executing XION scripts requires the following steps:
 * <ol>
 * <li>Create an appropriate XNUI.
 * <li>Create an XNContext to represent the state of the XION interpreter,
 * passing in the created XNUI.
 * <li>Use <code>XNContext.loadModule()</code> to load one or more XNModules.
 * At the very least, load <code>XNStandardModule.instance()</code>.
 * <li>Create an XNInterpreter, passing in the created XNContext.
 * <li>Create an XNLexer, passing in a Reader for the source code.
 * <li>Create an XNParser, passing in the created XNContext and XNLexer.
 * <li>Use <code>XNParser.parse()</code> to generate the abstract syntax tree of the XION program.
 * <li>Pass the generated abstract syntax tree to <code>XNInterpreter.executeScript()</code>.
 * </ol>
 * If the source code is a string, you can skip steps 5 through 8 by
 * using the method <code>XNInterpreter.executeScriptString()</code>.
 * <p>
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
