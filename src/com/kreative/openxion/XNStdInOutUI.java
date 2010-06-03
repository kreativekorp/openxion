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
import java.util.Scanner;

/**
 * XNTUI is the XNUI that uses standard input and output
 * for user interaction.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XNStdInOutUI implements XNUI {
	private Scanner in;
	private PrintWriter out;
	
	public XNStdInOutUI() {
		reset();
	}
	
	public void reset() {
		in = new Scanner(System.in);
		try {
			out = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"), true);
		} catch (UnsupportedEncodingException uee) {
			out = new PrintWriter(new OutputStreamWriter(System.out), true);
		}
	}
	
	private String getLine() {
		if (in.hasNextLine()) return in.nextLine();
		else return null;
	}
	
	public String answer(String prompt, String[] options, int x, int y) {
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
			
			return t;
		}
	}
	
	public void beep() {
		out.print("\u0007");
	}
	
	public void put(String s) {
		out.println(s);
	}
}
