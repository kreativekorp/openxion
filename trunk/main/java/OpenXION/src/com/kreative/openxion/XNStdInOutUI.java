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

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * XNStdInOutUI is the XNUI that uses standard input and output
 * for user interaction.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XNStdInOutUI implements XNUI {
	private Class<?> terminalClass;
	private Class<?> consoleReaderClass;
	private Object consoleReaderObject;
	private Method consoleReaderUseHistory;
	private Method consoleReaderReadlnPrompt;
	private Method consoleReaderReadlnPwprompt;
	private Class<?> historyClass;
	private Object historyObject;
	private Method historyAdd;
	private Scanner in;
	private PrintWriter out;
	private boolean fancyPrompts;
	
	public XNStdInOutUI(boolean fancyPrompts) {
		try {
			terminalClass = Class.forName("jline.Terminal");
			terminalClass.getMethod("setupTerminal").invoke(null);
			consoleReaderClass = Class.forName("jline.ConsoleReader");
			consoleReaderObject = consoleReaderClass.newInstance();
			consoleReaderUseHistory = consoleReaderClass.getMethod("setUseHistory", boolean.class);
			consoleReaderReadlnPrompt = consoleReaderClass.getMethod("readLine", String.class);
			consoleReaderReadlnPwprompt = consoleReaderClass.getMethod("readLine", String.class, Character.class);
			historyClass = Class.forName("jline.History");
			historyObject = consoleReaderClass.getMethod("getHistory").invoke(consoleReaderObject);
			historyAdd = historyClass.getMethod("addToHistory", String.class);
			consoleReaderUseHistory.invoke(consoleReaderObject, false);
			in = null;
		} catch (Exception e) {
			terminalClass = null;
			consoleReaderClass = null;
			consoleReaderObject = null;
			consoleReaderUseHistory = null;
			consoleReaderReadlnPrompt = null;
			consoleReaderReadlnPwprompt = null;
			historyClass = null;
			historyObject = null;
			historyAdd = null;
			in = new Scanner(System.in);
		}
		try {
			out = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"), true);
		} catch (UnsupportedEncodingException uee) {
			out = new PrintWriter(new OutputStreamWriter(System.out), true);
		}
		this.fancyPrompts = fancyPrompts;
	}
	
	public boolean fancyPrompts() {
		return fancyPrompts;
	}
	
	public void setFancyPrompts(boolean fancyPrompts) {
		this.fancyPrompts = fancyPrompts;
	}
	
	void print(String s) {
		out.print(s);
		out.flush();
	}
	
	void println(String s) {
		out.println(s);
	}
	
	String getCommandLine() {
		if (consoleReaderObject != null && consoleReaderReadlnPrompt != null && historyObject != null && historyAdd != null) {
			try {
				String line = (String)consoleReaderReadlnPrompt.invoke(consoleReaderObject, ">");
				if (line != null) {
					line = line.trim();
					while (line.endsWith("\\")) {
						if (line.endsWith("\\\\")) {
							line = line.substring(0, line.length()-2).trim() + "\n";
						} else {
							line = line.substring(0, line.length()-1).trim() + " ";
						}
						String moreline = (String)consoleReaderReadlnPrompt.invoke(consoleReaderObject, "-");
						if (moreline != null) {
							line += moreline.trim();
						}
					}
					line = line.trim();
					historyAdd.invoke(historyObject, line);
					return line;
				} else {
					out.println();
					return null;
				}
			} catch (Exception e) {
				return null;
			}
		} else if (in != null && out != null) {
			out.print(">");
			out.flush();
			if (in.hasNextLine()) {
				String line = in.nextLine().trim();
				while (line.endsWith("\\")) {
					if (line.endsWith("\\\\")) {
						line = line.substring(0, line.length()-2).trim() + "\n";
					} else {
						line = line.substring(0, line.length()-1).trim() + " ";
					}
					out.print("-");
					out.flush();
					if (in.hasNextLine()) {
						line += in.nextLine().trim();
					}
				}
				return line.trim();
			} else {
				out.println();
				return null;
			}
		} else {
			return null;
		}
	}
	
	private String getLine() {
		if (consoleReaderObject != null && consoleReaderReadlnPrompt != null) {
			try {
				return (String)consoleReaderReadlnPrompt.invoke(consoleReaderObject, "");
			} catch (Exception e) {
				return null;
			}
		} else if (in != null) {
			if (in.hasNextLine()) return in.nextLine();
			else return null;
		} else {
			return null;
		}
	}
	
	private String getPasswordLine() {
		if (consoleReaderObject != null && consoleReaderReadlnPwprompt != null) {
			try {
				return (String)consoleReaderReadlnPwprompt.invoke(consoleReaderObject, "", Character.valueOf('*'));
			} catch (Exception e) {
				return null;
			}
		} else if (in != null && out != null) {
			out.print("\u001B[8m");
			out.flush();
			String t;
			if (in.hasNextLine()) t = in.nextLine();
			else t = null;
			out.print("\u001B[0m");
			out.flush();
			return t;
		} else {
			return null;
		}
	}
	
	public String answer(String prompt, String[] options, int x, int y) {
		if (fancyPrompts) {
			println(prompt);
			if (options == null || options.length == 0) {
				println("  [[1:OK]]");
			} else {
				for (int i = 0; i < options.length; i++) {
					if (i == options.length-1) {
						print("  [[" + (i+1) + ":" + options[i] + "]]");
					} else {
						print("  [" + (i+1) + ":" + options[i] + "]");
					}
				}
				println("");
			}
		} else {
			println(prompt);
			if (options == null || options.length == 0) {
				println("  \u001B[1m1: OK\u001B[0m");
			} else {
				for (int i = 0; i < options.length; i++) {
					if (i == options.length-1) {
						println("  \u001B[1m" + (i+1) + ": " + options[i] + "\u001B[0m");
					} else {
						println("  " + (i+1) + ": " + options[i]);
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
		println(prompt);
		return new File(getLine());
	}
	
	public File answerFile(String prompt, String[] types, int x, int y) {
		println(prompt);
		return new File(getLine());
	}
	
	public File answerFolder(String prompt, int x, int y) {
		println(prompt);
		return new File(getLine());
	}
	
	public String answerList(String prompt, String[] options, int x, int y) {
		println(prompt);
		if (options != null && options.length > 0) {
			for (int i = 0; i < options.length; i++) {
				println("  " + (i+1) + ": " + options[i]);
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
			println(prompt);
			String t = getLine();
			
			if (fancyPrompts) {
				println("  [1:Change] [2:Cancel] [[3:OK]]");
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
		println(prompt);
		return new File(getLine());
	}
	
	public File askFolder(String prompt, String deftext, int x, int y) {
		println(prompt);
		return new File(getLine());
	}
	
	public String askPassword(String prompt, String deftext, int x, int y) {
		while (true) {
			println(prompt);
			String t = getPasswordLine();
			
			if (fancyPrompts) {
				println("  [1:Change] [2:Cancel] [[3:OK]]");
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
		print("\u0007");
	}
	
	public void promptSecurity(XNSecurityKey[] type, boolean[] allow, boolean[] forall, Map<String,String>[] details) {
		println("\u001B[1m======================== SECURITY WARNING ========================\u001B[0m");
		for (XNSecurityKey t : type) {
			switch (t) {
			case DO_AND_VALUE: println("This script is requesting the execution of arbitrary XION code."); break;
			case EXTERNAL_SCRIPTS: println("This script is requesting the execution of arbitrary code."); break;
			case MODULE_LOAD: println("This script is requesting to load another module."); break;
			case SYSTEM_INFO: println("This script is requesting information about your system."); break;
			case CLIPBOARD_READ: println("This script is requesting read access to the clipboard."); break;
			case CLIPBOARD_WRITE: println("This script is requesting write access to the clipboard."); break;
			case FILE_LAUNCH: println("This script is requesting to launch an external program."); break;
			case FILE_SYSTEM_READ: println("This script is requesting read access to the file system."); break;
			case FILE_SYSTEM_WRITE: println("This script is requesting write access to the file system."); break;
			case BROWSER_LAUNCH: println("This script is requesting to launch a web browser."); break;
			case INTERNET_ACCESS: println("This script is requesting access to the Internet."); break;
			case SEARCH_PATHS: println("This script is requesting to change search paths."); break;
			case PRINTING: println("This script is requesting access to the printer."); break;
			case TELEPHONY: println("This script is requesting to make calls or access a modem."); break;
			case MESSAGE_HIERARCHY: println("This script is requesting to change the message-passing hierarchy."); break;
			case INTERAPP_COMM: println("This script is requesting control of an external program."); break;
			case LOCAL_AUTOMATION: println("This script is requesting control of the mouse and keyboard."); break;
			case GLOBAL_AUTOMATION: println("This script is requesting control of the mouse and keyboard."); break;
			case HARDWARE_ACCESS: println("This script is requesting control of external hardware."); break;
			case SCRIPT_READ: println("This script is requesting read access to other scripts."); break;
			case SCRIPT_WRITE: println("This script is requesting write access to other scripts."); break;
			default: println("This script is requesting "+t.name()+"."); break;
			}
		}
		println("Would you like to allow this, or prevent the script from continuing?");
		println("\u001B[1mDo not allow unless you trust the source of this script.\u001B[0m");
		while (true) {
			if (fancyPrompts) {
				println("  [1:Allow] [2:Allow All] [3:Deny] [4:Deny All] [5:Kill Script] [6:Details]");
			} else {
				println("  1: Allow");
				println("  2: Allow All");
				println("  3: Deny");
				println("  4: Deny All");
				println("  5: Kill Script");
				println("  6: Details");
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
						println("SecurityKey: " + type[j].name());
						for (Map.Entry<String,String> en : details[j].entrySet()) {
							println(en.getKey() + ": " + en.getValue());
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
						println("SecurityKey: " + type[j].name());
						for (Map.Entry<String,String> en : details[j].entrySet()) {
							println(en.getKey() + ": " + en.getValue());
						}
					}
				}
			}
		}
	}
	
	public void put(String s) {
		println(s);
	}
}
