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

import java.io.File;
import java.util.Map;

/**
 * An XNUI is responsible for any interaction between the OpenXION
 * interpreter and the outside world.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public interface XNUI {
	public void put(String s);
	public String answer(String prompt, String[] options, int x, int y);
	public String answerList(String prompt, String[] options, int x, int y);
	public File answerFile(String prompt, String[] types, int x, int y);
	public File answerFolder(String prompt, int x, int y);
	public File answerDisk(String prompt, int x, int y);
	public String ask(String prompt, String deftext, int x, int y);
	public String askPassword(String prompt, String deftext, int x, int y);
	public File askFile(String prompt, String deftext, int x, int y);
	public File askFolder(String prompt, String deftext, int x, int y);
	public void beep();
	public void promptSecurity(XNSecurityKey[] type, boolean[] allow, boolean[] forall, Map<String,String>[] details);
}
