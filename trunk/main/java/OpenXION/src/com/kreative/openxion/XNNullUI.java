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
 * @since OpenXION 1.2
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion;

import java.io.File;
import java.util.Map;

/**
 * XNNullUI is an XNUI that responds to all prompts as if the user canceled.
 * @since OpenXION 1.2
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XNNullUI implements XNUI {
	public String answer(String prompt, String[] options, int x, int y) {
		if (options == null || options.length == 0) {
			return "OK";
		} else {
			return options[options.length-1];
		}
	}
	
	public File answerDisk(String prompt, int x, int y) {
		return null;
	}
	
	public File answerFile(String prompt, String[] types, int x, int y) {
		return null;
	}
	
	public File answerFolder(String prompt, int x, int y) {
		return null;
	}
	
	public String answerList(String prompt, String[] options, int x, int y) {
		return "";
	}
	
	public String ask(String prompt, String deftext, int x, int y) {
		return null;
	}
	
	public File askFile(String prompt, String deftext, int x, int y) {
		return null;
	}
	
	public File askFolder(String prompt, String deftext, int x, int y) {
		return null;
	}
	
	public String askPassword(String prompt, String deftext, int x, int y) {
		return null;
	}
	
	public void beep() {
		// nothing
	}
	
	public void promptSecurity(XNSecurityKey[] type, boolean[] allow, boolean[] forall, Map<String,String>[] details) {
		for (int k = 0; k < allow.length; k++) allow[k] = false;
		for (int k = 0; k < forall.length; k++) forall[k] = false;
	}
	
	public void put(String s) {
		// nothing
	}
}
