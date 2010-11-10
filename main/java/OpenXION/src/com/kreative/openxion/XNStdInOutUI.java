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

import java.io.*;
import java.util.*;

/**
 * XNStdInOutUI is the XNUI that uses standard input and output
 * for user interaction.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XNStdInOutUI implements XNUI {
	private Scanner in;
	private PrintWriter out;
	private boolean fancyPrompts;
	
	public XNStdInOutUI(boolean fancyPrompts) {
		reset();
		setFancyPrompts(fancyPrompts);
	}
	
	public void reset() {
		in = new Scanner(System.in);
		try {
			out = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"), true);
		} catch (UnsupportedEncodingException uee) {
			out = new PrintWriter(new OutputStreamWriter(System.out), true);
		}
	}
	
	public boolean fancyPrompts() {
		return fancyPrompts;
	}
	
	public void setFancyPrompts(boolean fancyPrompts) {
		this.fancyPrompts = fancyPrompts;
	}
	
	private String getLine() {
		if (in.hasNextLine()) return in.nextLine();
		else return null;
	}
	
	public String answer(String prompt, String[] options, int x, int y) {
		if (fancyPrompts) {
			out.println(prompt);
			if (options == null || options.length == 0) {
				out.println("  [[1:OK]]");
			} else {
				for (int i = 0; i < options.length; i++) {
					if (i == options.length-1) {
						out.print("  [[" + (i+1) + ":" + options[i] + "]]");
					} else {
						out.print("  [" + (i+1) + ":" + options[i] + "]");
					}
				}
				out.println();
			}
		} else {
			out.println(prompt);
			if (options == null || options.length == 0) {
				out.println("  \u001B[1m1: OK\u001B[0m");
			} else {
				for (int i = 0; i < options.length; i++) {
					if (i == options.length-1) {
						out.println("  \u001B[1m" + (i+1) + ": " + options[i] + "\u001B[0m");
					} else {
						out.println("  " + (i+1) + ": " + options[i]);
					}
				}
			}
		}
		String s = getLine().trim();
		if (options == null || options.length == 0) {
			return "OK";
		} else {
			try {
				int i = Integer.parseInt(s);
				if (i >= 1 && i <= options.length) {
					return options[i-1];
				}
			} catch (Exception e) {}
			
			for (String option : options) {
				if (s.equalsIgnoreCase(option.trim())) return option;
			}
			
			return options[options.length-1];
		}
	}
	
	public File answerDisk(String prompt, int x, int y) {
		out.println(prompt);
		return new File(getLine());
	}
	
	public File answerFile(String prompt, String[] types, int x, int y) {
		out.println(prompt);
		return new File(getLine());
	}
	
	public File answerFolder(String prompt, int x, int y) {
		out.println(prompt);
		return new File(getLine());
	}
	
	public String answerList(String prompt, String[] options, int x, int y) {
		out.println(prompt);
		if (options != null && options.length > 0) {
			for (int i = 0; i < options.length; i++) {
				out.println("  " + (i+1) + ": " + options[i]);
			}
		}
		String s = getLine().trim();
		if (options == null || options.length == 0) {
			return "";
		} else {
			try {
				int i = Integer.parseInt(s);
				if (i >= 1 && i <= options.length) {
					return options[i-1];
				}
			} catch (Exception e) {}
			
			for (String option : options) {
				if (s.equalsIgnoreCase(option.trim())) return option;
			}
			
			return "";
		}
	}
	
	public String ask(String prompt, String deftext, int x, int y) {
		while (true) {
			out.println(prompt);
			String t = getLine();
			
			if (fancyPrompts) {
				out.println("  [1:Change] [2:Cancel] [[3:OK]]");
				String s = getLine().trim();
				try {
					int i = Integer.parseInt(s);
					if (i == 1) continue;
					else if (i == 2) return null;
					else if (i == 3) return t;
				} catch (Exception e) {}

				if (s.equalsIgnoreCase("change")) continue;
				else if (s.equalsIgnoreCase("cancel")) return null;
				else if (s.equalsIgnoreCase("ok")) return t;
			}
			
			return t;
		}
	}
	
	public File askFile(String prompt, String deftext, int x, int y) {
		out.println(prompt);
		return new File(getLine());
	}
	
	public File askFolder(String prompt, String deftext, int x, int y) {
		out.println(prompt);
		return new File(getLine());
	}
	
	public String askPassword(String prompt, String deftext, int x, int y) {
		while (true) {
			out.println(prompt);
			out.print("\u001B[8m");
			String t = getLine();
			out.print("\u001B[0m");
			
			if (fancyPrompts) {
				out.println("  [1:Change] [2:Cancel] [[3:OK]]");
				String s = getLine().trim();
				try {
					int i = Integer.parseInt(s);
					if (i == 1) continue;
					else if (i == 2) return null;
					else if (i == 3) return t;
				} catch (Exception e) {}

				if (s.equalsIgnoreCase("change")) continue;
				else if (s.equalsIgnoreCase("cancel")) return null;
				else if (s.equalsIgnoreCase("ok")) return t;
			}
			
			return t;
		}
	}
	
	public void beep() {
		out.print("\u0007");
	}
	
	public void promptSecurity(XNSecurityKey[] type, boolean[] allow, boolean[] forall, Map<String,String>[] details) {
		out.println("\u001B[1m======================== SECURITY WARNING ========================\u001B[0m");
		for (XNSecurityKey t : type) {
			switch (t) {
			case DO_AND_VALUE: out.println("This script is requesting the execution of arbitrary XION code."); break;
			case EXTERNAL_SCRIPTS: out.println("This script is requesting the execution of arbitrary code."); break;
			case MODULE_LOAD: out.println("This script is requesting to load another module."); break;
			case SYSTEM_INFO: out.println("This script is requesting information about your system."); break;
			case CLIPBOARD_READ: out.println("This script is requesting read access to the clipboard."); break;
			case CLIPBOARD_WRITE: out.println("This script is requesting write access to the clipboard."); break;
			case FILE_LAUNCH: out.println("This script is requesting to launch an external program."); break;
			case FILE_SYSTEM_READ: out.println("This script is requesting read access to the file system."); break;
			case FILE_SYSTEM_WRITE: out.println("This script is requesting write access to the file system."); break;
			case BROWSER_LAUNCH: out.println("This script is requesting to launch a web browser."); break;
			case INTERNET_ACCESS: out.println("This script is requesting access to the Internet."); break;
			case SEARCH_PATHS: out.println("This script is requesting to change search paths."); break;
			case PRINTING: out.println("This script is requesting access to the printer."); break;
			case TELEPHONY: out.println("This script is requesting to make calls or access a modem."); break;
			case MESSAGE_HIERARCHY: out.println("This script is requesting to change the message-passing hierarchy."); break;
			case INTERAPP_COMM: out.println("This script is requesting control of an external program."); break;
			case LOCAL_AUTOMATION: out.println("This script is requesting control of the mouse and keyboard."); break;
			case GLOBAL_AUTOMATION: out.println("This script is requesting control of the mouse and keyboard."); break;
			case HARDWARE_ACCESS: out.println("This script is requesting control of external hardware."); break;
			case SCRIPT_READ: out.println("This script is requesting read access to other scripts."); break;
			case SCRIPT_WRITE: out.println("This script is requesting write access to other scripts."); break;
			default: out.println("This script is requesting "+t.name()+"."); break;
			}
		}
		out.println("Would you like to allow this, or prevent the script from continuing?");
		out.println("\u001B[1mDo not allow unless you trust the source of this script.\u001B[0m");
		while (true) {
			if (fancyPrompts) {
				out.println("  [1:Allow] [2:Allow All] [3:Deny] [4:Deny All] [5:Kill Script] [6:Details]");
			} else {
				out.println("  1: Allow");
				out.println("  2: Allow All");
				out.println("  3: Deny");
				out.println("  4: Deny All");
				out.println("  5: Kill Script");
				out.println("  6: Details");
			}
			String s = getLine().trim();
			try {
				int i = Integer.parseInt(s);
				switch (i) {
				case 1:
					for (int k = 0; k < allow.length; k++) allow[k] = true;
					for (int k = 0; k < forall.length; k++) forall[k] = false;
					return;
				case 2:
					for (int k = 0; k < allow.length; k++) allow[k] = true;
					for (int k = 0; k < forall.length; k++) forall[k] = true;
					return;
				case 3:
					for (int k = 0; k < allow.length; k++) allow[k] = false;
					for (int k = 0; k < forall.length; k++) forall[k] = false;
					return;
				case 4:
					for (int k = 0; k < allow.length; k++) allow[k] = true;
					for (int k = 0; k < forall.length; k++) forall[k] = true;
					return;
				case 5:
					throw new XNExitedToInterpreterException("User requested end of script execution.");
				case 6:
					for (int j = 0; j < type.length && j < details.length; j++) {
						out.println("SecurityKey: " + type[j].name());
						for (Map.Entry<String,String> en : details[j].entrySet()) {
							out.println(en.getKey() + ": " + en.getValue());
						}
					}
					break;
				}
			} catch (NumberFormatException e) {
				s = s.replaceAll("\\s+", "");
				if (s.equalsIgnoreCase("allow")) {
					for (int k = 0; k < allow.length; k++) allow[k] = true;
					for (int k = 0; k < forall.length; k++) forall[k] = false;
					return;
				} else if (s.equalsIgnoreCase("allowall")) {
					for (int k = 0; k < allow.length; k++) allow[k] = true;
					for (int k = 0; k < forall.length; k++) forall[k] = true;
					return;
				} else if (s.equalsIgnoreCase("deny")) {
					for (int k = 0; k < allow.length; k++) allow[k] = false;
					for (int k = 0; k < forall.length; k++) forall[k] = false;
					return;
				} else if (s.equalsIgnoreCase("denyall")) {
					for (int k = 0; k < allow.length; k++) allow[k] = true;
					for (int k = 0; k < forall.length; k++) forall[k] = true;
					return;
				} else if (s.equalsIgnoreCase("kill") || s.equalsIgnoreCase("killscript")) {
					throw new XNExitedToInterpreterException("User requested end of script execution.");
				} else if (s.equalsIgnoreCase("detail") || s.equalsIgnoreCase("details")) {
					for (int j = 0; j < type.length && j < details.length; j++) {
						out.println("SecurityKey: " + type[j].name());
						for (Map.Entry<String,String> en : details[j].entrySet()) {
							out.println(en.getKey() + ": " + en.getValue());
						}
					}
				}
			}
		}
	}
	
	public void put(String s) {
		out.println(s);
	}
}
