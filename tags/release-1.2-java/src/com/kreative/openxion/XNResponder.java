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

import java.util.List;
import com.kreative.openxion.ast.XNExpression;
import com.kreative.openxion.ast.XNModifier;
import com.kreative.openxion.xom.XOMDataType;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.XOMVariable;

/**
 * An XNResponder is anything that can execute XION commands
 * or evaluate XION functions.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public interface XNResponder {
	public XNResponder nextResponder();
	
	public XOMVariable createSharedVariable(XNContext ctx, String name, XOMDataType<? extends XOMVariant> type, XOMVariant initialvalue);
	public XOMVariable getSharedVariable(XNContext ctx, String name);
	public XOMVariable createStaticVariable(XNContext ctx, String handlerName, String variableName, XOMDataType<? extends XOMVariant> type, XOMVariant initialvalue);
	public XOMVariable getStaticVariable(XNContext ctx, String handlerName, String variableName);
	
	public XNHandlerExit executeCommand(XNContext ctx, String commandName, List<XNExpression> parameters);
	public XNHandlerExit evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter);
}
