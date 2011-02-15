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
 * @since OpenXION 1.1
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion;

/**
 * XNSecurityKey represents a certain area of functionality available
 * to XION scripts subject to restriction by security settings.
 * @since OpenXION 1.1
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public enum XNSecurityKey {
	/**
	 * The security key controlling whether a script can use the "do" keyword
	 * and the "value" function to execute arbitrary XION code.
	 */
	DO_AND_VALUE,
	/**
	 * The security key controlling whether a script can use the "do" keyword
	 * to execute arbitrary code in other scripting languages.
	 */
	EXTERNAL_SCRIPTS,
	/**
	 * The security key controlling whether a script can load additional XNModules.
	 */
	MODULE_LOAD,
	/**
	 * The security key controlling whether a script can collect information
	 * about the name and version of the operating system, Java VM, or Java runtime.
	 */
	SYSTEM_INFO,
	/**
	 * The security key controlling whether a script can read the contents of the clipboard.
	 */
	CLIPBOARD_READ,
	/**
	 * The security key controlling whether a script can modify the contents of the clipboard.
	 */
	CLIPBOARD_WRITE,
	/**
	 * The security key controlling whether a script can launch applications.
	 */
	FILE_LAUNCH,
	/**
	 * The security key controlling whether a script can read files and the contents of directories.
	 */
	FILE_SYSTEM_READ,
	/**
	 * The security key controlling whether a script can create, modify, and delete files and directories.
	 */
	FILE_SYSTEM_WRITE,
	/**
	 * The security key controlling whether a script can launch a web browser and point it to a URL.
	 */
	BROWSER_LAUNCH,
	/**
	 * The security key controlling whether a script can read data from or write data to a URL.
	 */
	INTERNET_ACCESS,
	/**
	 * The security key controlling whether a script can modify search paths.
	 */
	SEARCH_PATHS,
	/**
	 * The security key controlling whether a script can access the printer.
	 */
	PRINTING,
	/**
	 * The security key controlling whether a script can access a modem or make telephone calls.
	 */
	TELEPHONY,
	/**
	 * The security key controlling whether a script can change the message-passing hierarchy.
	 */
	MESSAGE_HIERARCHY,
	/**
	 * The security key controlling whether a script can control other applications.
	 */
	INTERAPP_COMM,
	/**
	 * The security key controlling whether a script can take over control
	 * of the mouse and keyboard within the current environment only.
	 */
	LOCAL_AUTOMATION,
	/**
	 * The security key controlling whether a script can take over control
	 * of the mouse and keyboard for the entire system.
	 */
	GLOBAL_AUTOMATION,
	/**
	 * The security key controlling whether a script can control external hardware.
	 */
	HARDWARE_ACCESS,
	/**
	 * The security key controlling whether a script can read other scripts.
	 */
	SCRIPT_READ,
	/**
	 * The security key controlling whether a script can modify other scripts.
	 */
	SCRIPT_WRITE,
}
