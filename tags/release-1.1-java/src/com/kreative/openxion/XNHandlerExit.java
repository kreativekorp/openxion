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

import java.io.Serializable;
import com.kreative.openxion.xom.XOMVariant;

/**
 * XNHandlerExit is used by the interpreter to report the status of a handler
 * once it has finished executing.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XNHandlerExit implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	private XNHandlerExitStatus status;
	private XOMVariant returnValue;
	private XNResponder nextResponderValue;
	private XOMVariant errorValue;
	private String blockTypeValue;
	
	public XNHandlerExitStatus status() {
		return status;
	}
	
	public XOMVariant returnValue() {
		return returnValue;
	}
	
	public XNResponder nextResponderValue() {
		return nextResponderValue;
	}
	
	public XOMVariant errorValue() {
		return errorValue;
	}
	
	public String blockTypeValue() {
		return blockTypeValue;
	}
	
	private XNHandlerExit(XNHandlerExitStatus s, XOMVariant r, XNResponder n, XOMVariant e, String b) {
		status = s; returnValue = r; nextResponderValue = n; errorValue = e; blockTypeValue = b;
	}
	
	private static final XNHandlerExit END = new XNHandlerExit(XNHandlerExitStatus.ENDED, null, null, null, null);
	public static XNHandlerExit ended() {
		return END;
	}
	
	private static final XNHandlerExit RETURN = new XNHandlerExit(XNHandlerExitStatus.RETURNED, null, null, null, null);
	public static XNHandlerExit returned() {
		return RETURN;
	}
	public static XNHandlerExit returned(XOMVariant value) {
		return new XNHandlerExit(XNHandlerExitStatus.RETURNED, value, null, null, null);
	}
	
	private static final XNHandlerExit PASS = new XNHandlerExit(XNHandlerExitStatus.PASSED, null, null, null, null);
	public static XNHandlerExit passed() {
		return PASS;
	}
	public static XNHandlerExit passedTo(XNResponder next) {
		return new XNHandlerExit(XNHandlerExitStatus.PASSED, null, next, null, null);
	}
	public static XNHandlerExit passedToInterpreter() {
		return new XNHandlerExit(XNHandlerExitStatus.PASSED, null, null, null, "all");
	}
	
	public static XNHandlerExit exitedBlock(String blockType) {
		return new XNHandlerExit(XNHandlerExitStatus.EXITED, null, null, null, blockType);
	}
	public static XNHandlerExit exitedBlockWithError(String blockType, XOMVariant error) {
		return new XNHandlerExit(XNHandlerExitStatus.EXITED, null, null, error, blockType);
	}
	
	public static XNHandlerExit nextedBlock(String blockType) {
		return new XNHandlerExit(XNHandlerExitStatus.NEXTED, null, null, null, blockType);
	}
}
