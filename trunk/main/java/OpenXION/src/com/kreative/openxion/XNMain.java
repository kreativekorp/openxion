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
import com.kreative.openxion.ast.XNStatement;
import com.kreative.openxion.xom.XOMVariable;
import com.kreative.openxion.xom.inst.XOMString;
import com.kreative.openxion.xom.type.XOMStringType;

/**
 * XNMain is the command line interface to the XION interpreter.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XNMain {
	public static final String XION_NAME = "OpenXION";
	public static final String XION_VERSION = "1.2";
	
	public static void main(String[] args) {
		final XNStdInOutUI ui = new XNStdInOutUI(true);
		final XNContext ctx = new XNContext(ui);
		readEnviron(ctx);
		ctx.loadModule(XNStandardModule.instance());
		ctx.loadModule(XNExtendedModule.instance());
		final XNInterpreter interp = new XNInterpreter(ctx);
		if (args.length == 0) {
			shell(ui, interp, ctx, false);
		} else {
			boolean somethingOfConsequenceHappened = false;
			String textEncoding = "UTF-8";
			boolean processOptions = true;
			Option lastOption = Option.SCRIPT_FILE;
			boolean stackTrace = false;
			boolean testMode = false;
			int testsTotal = 0;
			int testsPassed = 0;
			int testsFailed = 0;
			for (String arg : args) {
				if (processOptions && arg.startsWith("-")) {
					if (arg.equals("-i")) {
						somethingOfConsequenceHappened = true;
						shell(ui, interp, ctx, stackTrace);
					}
					else if (arg.equals("-f")) lastOption = Option.SCRIPT_FILE;
					else if (arg.equals("-e")) lastOption = Option.EXPRESSION;
					else if (arg.equals("-c")) lastOption = Option.STATEMENT;
					else if (arg.equals("-m")) lastOption = Option.MODULE;
					else if (arg.equals("-E")) lastOption = Option.TEXT_ENCODING;
					else if (arg.equals("-D")) lastOption = Option.VARIABLE;
					else if (arg.equals("-s")) lastOption = Option.SECURITY_PROFILE;
					else if (arg.equals("-h") || arg.equals("-help") || arg.equals("--help")) {
						somethingOfConsequenceHappened = true;
						help(ui);
					}
					else if (arg.equals("-v") || arg.equals("-version") || arg.equals("--version")) {
						somethingOfConsequenceHappened = true;
						version(ui);
					}
					else if (arg.equals("-p")) ui.setFancyPrompts(false);
					else if (arg.equals("-P")) ui.setFancyPrompts(true);
					else if (arg.equals("-r")) ctx.reset();
					else if (arg.equals("-R")) ctx.resetAll();
					else if (arg.equals("-S")) stackTrace = true;
					else if (arg.equals("-T")) testMode = true;
					else if (arg.equals("--")) processOptions = false;
					else System.err.println("Unrecognized option: "+arg);
				} else {
					switch (lastOption) {
					case SCRIPT_FILE:
						somethingOfConsequenceHappened = true;
						if (testMode) {
							testsTotal++;
							boolean fail = false;
							
							ui.println("Testing: "+arg+"...");
							ByteArrayOutputStream capture = new ByteArrayOutputStream();
							PrintStream captureStream = new PrintStream(capture);
							ctx.setUI(new TestUI(ui, captureStream));
							
							try {
								ctx.reset();
								XNLexer lex = new XNLexer(new InputStreamReader(new FileInputStream(arg), textEncoding));
								XNParser par = new XNParser(ctx, lex);
								List<XNStatement> program = par.parse();
								interp.executeScript(program);
							} catch (IOException e) {
								captureStream.println("Could not read script file: "+arg);
								if (stackTrace) e.printStackTrace();
							} catch (XNScriptError se) {
								captureStream.println(se.getMessage() + " on line " + se.getLine() + " at character " + se.getCol());
								if (stackTrace) se.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
								System.exit(0);
							}
							
							ctx.setUI(ui);
							ByteArrayInputStream captureStream2 = new ByteArrayInputStream(capture.toByteArray());
							try {
								Process p = Runtime.getRuntime().exec(new String[]{"diff", new File(arg+".out").getAbsolutePath(), "-"});
								byte[] stuff = new byte[8192];
								int len;
								while ((len = captureStream2.read(stuff)) > 0) {
									p.getOutputStream().write(stuff, 0, len);
								}
								p.getOutputStream().close();
								while ((len = p.getInputStream().read(stuff)) > 0) {
									fail = true;
									System.out.write(stuff, 0, len);
								}
							} catch (IOException ioe) {}
							
							if (fail) testsFailed++;
							else testsPassed++;
						} else {
							try {
								XNLexer lex = new XNLexer(new InputStreamReader(new FileInputStream(arg), textEncoding));
								XNParser par = new XNParser(ctx, lex);
								List<XNStatement> program = par.parse();
								interp.executeScript(program);
							} catch (IOException e) {
								System.err.println("Could not read script file: "+arg);
								if (stackTrace) e.printStackTrace();
							} catch (XNScriptError se) {
								System.err.println(se.getMessage() + " on line " + se.getLine() + " at character " + se.getCol());
								if (stackTrace) se.printStackTrace();
							}
						}
						break;
					case EXPRESSION:
						somethingOfConsequenceHappened = true;
						try {
							ui.println(interp.evaluateExpressionString(arg).unwrap().toTextString(ctx));
						} catch (XNScriptError se) {
							System.err.println(se.getMessage());
							if (stackTrace) se.printStackTrace();
						}
						break;
					case STATEMENT:
						somethingOfConsequenceHappened = true;
						try {
							interp.executeScriptString(arg);
						} catch (XNScriptError se) {
							System.err.println(se.getMessage());
							if (stackTrace) se.printStackTrace();
						}
						break;
					case MODULE:
						try {
							Class<?> clazz = Class.forName(arg);
							Class<? extends XNModule> modclazz = clazz.asSubclass(XNModule.class);
							XNModule module = modclazz.newInstance();
							ctx.loadModule(module);
						} catch (Exception e) {
							System.err.println("Could not load module: "+arg);
							if (stackTrace) e.printStackTrace();
						}
						break;
					case TEXT_ENCODING:
						textEncoding = arg;
						break;
					case VARIABLE:
						String[] ss = arg.split("=", 2);
						if (ss.length == 2) {
							XOMVariable v = ctx.getGlobalVariable(ss[0]);
							if (v == null) ctx.createGlobalVariable(ss[0], XOMStringType.instance, new XOMString(ss[1]));
							else v.putIntoContents(ctx, new XOMString(ss[1]));
						} else {
							System.err.println("Can't understand -D option: "+arg);
						}
						break;
					case SECURITY_PROFILE:
						if (arg.equalsIgnoreCase("allow")) ctx.setSecurityProfile(new XNSecurityProfile(XNSecurityValue.ALLOW));
						else if (arg.equalsIgnoreCase("ask")) ctx.setSecurityProfile(new XNSecurityProfile(XNSecurityValue.ASK));
						else if (arg.equalsIgnoreCase("deny")) ctx.setSecurityProfile(new XNSecurityProfile(XNSecurityValue.DENY));
						else if (arg.matches("[A-Z0-9_]+=[A-Z0-9_]+")) {
							String[] sss = arg.split("=", 2);
							XNSecurityKey k = XNSecurityKey.valueOf(sss[0]);
							XNSecurityValue v = XNSecurityValue.valueOf(sss[1]);
							if (k != null && v != null) {
								ctx.getSecurityProfile().put(k, v);
							} else {
								try {
									ctx.getSecurityProfile().read(new Scanner(new File(arg), "UTF-8"));
								} catch (IOException ioe) {
									System.err.println("Could not load security profile: "+arg);
								}
							}
						} else {
							try {
								ctx.getSecurityProfile().read(new Scanner(new File(arg), "UTF-8"));
							} catch (IOException ioe) {
								System.err.println("Could not load security profile: "+arg);
							}
						}
						break;
					}
					lastOption = Option.SCRIPT_FILE;
				}
			}
			if (testsTotal > 0) {
				ui.println("PASSED: "+testsPassed+"/"+testsTotal+"  FAILED: "+testsFailed+"/"+testsTotal);
			}
			if (!somethingOfConsequenceHappened) {
				shell(ui, interp, ctx, stackTrace);
			}
		}
		writeEnviron(ctx);
	}
	
	private static enum Option {
		SCRIPT_FILE,
		EXPRESSION,
		STATEMENT,
		MODULE,
		TEXT_ENCODING,
		VARIABLE,
		SECURITY_PROFILE
	}
	
	private static void shell(XNStdInOutUI ui, XNInterpreter interp, XNContext ctx, boolean stackTrace) {
		while (true) {
			String line = ui.getCommandLine(false);
			if (line != null) {
				if (line.length() > 0) {
					if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) {
						break;
					} else {
						try {
							interp.executeScriptString(line);
						} catch (XNScriptError se1) {
							try {
								ui.println(interp.evaluateExpressionString(line).unwrap().toTextString(ctx));
							} catch (XNScriptError se3) {
								if (stackTrace) {
									System.err.println("Attempted as statement:");
									se1.printStackTrace();
									System.err.println("Attempted as expression:");
									se3.printStackTrace();
								} else {
									ui.println(se1.getMessage());
								}
							}
						}
					}
				}
			} else {
				break;
			}
		}
	}
	
	private static void help(XNStdInOutUI ui) {
		// // // // // // //<---10---><---20---><---30---><---40---><---50---><---60---><---70---><---80--->
		ui.println("Usage: xion [options] [--] [programfile] [programfile] [...]");
		ui.println("  -c statement        execute the specified statements");
		ui.println("  -D var=value        set the value of a global variable");
		ui.println("  -E encoding         specify the text encoding used to read script files");
		ui.println("  -e expression       evaluate and print the specified expression");
		ui.println("  -f programfile      execute the specified script file");
		ui.println("  -h                  print help screen");
		ui.println("  -i                  start an interactive shell");
		ui.println("  -m classname        load an XNModule with the specified class name");
		ui.println("  -P                  use fancy prompts simulating dialog boxes (default)");
		ui.println("  -p                  use simple, more traditional prompts");
		ui.println("  -R                  clear runtime state AND unload all modules");
		ui.println("  -r                  clear runtime state ONLY");
		ui.println("  -S                  print a stack trace for every exception");
		ui.println("  -s allow|ask|deny   set security settings to allow|ask|deny every request");
		ui.println("  -s key=value        set security setting for one kind of request only");
		ui.println("  -s file             read security settings from file as key=value pairs");
		ui.println("  -T                  instead of printing output, print file name and");
		ui.println("                      diff of output against .out file (testing mode)");
		ui.println("                      (-s allow recommended with this option)");
		ui.println("  -v                  print OpenXION, Java, and OS version numbers");
		ui.println("  --help              print help screen");
		ui.println("  --version           print OpenXION, Java, and OS version numbers");
		ui.println("  --                  treat remaining arguments as file names");
	}
	
	private static void version(XNStdInOutUI ui) {
		ui.println(XION_NAME + " " + XION_VERSION);
		ui.println(System.getProperty("java.runtime.name") + " " + System.getProperty("java.runtime.version"));
		ui.println(System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version"));
		ui.println(System.getProperty("os.name") + " " + System.getProperty("os.version"));
	}
	
	private static File getEnvironFile() {
		String os = System.getProperty("os.name").toUpperCase();
		String h = System.getProperty("user.home");
		if (os.contains("MAC OS")) {
			File f = new File(h);
			f = new File(f, "Library"); if (!f.exists()) f.mkdir();
			f = new File(f, "Preferences"); if (!f.exists()) f.mkdir();
			return new File(f, "com.kreative.openxion.conf");
		}
		else if (os.contains("WINDOWS")) {
			File f = new File(h);
			f = new File(f, "Application Data"); if (!f.exists()) f.mkdir();
			f = new File(f, "OpenXION"); if (!f.exists()) f.mkdir();
			return new File(f, "xion.conf");
		}
		else {
			File f = new File(h);
			return new File(f, ".xion.conf");
		}
	}
	
	private static void readEnviron(XNContext ctx) {
		try {
			StringBuffer u = new StringBuffer();
			StringBuffer a = new StringBuffer();
			StringBuffer d = new StringBuffer();
			StringBuffer i = new StringBuffer();
			Scanner sc = new Scanner(getEnvironFile(), "UTF-8");
			while (sc.hasNextLine()) {
				String s = sc.nextLine();
				if (s.startsWith("Username=")) u.append(s.substring(9)+"\n");
				if (s.startsWith("Applications=")) a.append(s.substring(13)+"\n");
				if (s.startsWith("Documents=")) d.append(s.substring(10)+"\n");
				if (s.startsWith("Includes=")) i.append(s.substring(9)+"\n");
			}
			sc.close();
			if (u.length() > 0) {
				if (u.charAt(u.length()-1) == '\n') {
					u.deleteCharAt(u.length()-1);
				}
				ctx.setUsername(u.toString());
			}
			if (a.length() > 0) {
				ctx.setApplicationPaths(a.toString());
			}
			if (d.length() > 0) {
				ctx.setDocumentPaths(d.toString());
			}
			if (i.length() > 0) {
				ctx.setIncludePaths(i.toString());
			}
		} catch (IOException ioe) {}
	}
	
	private static void writeEnviron(XNContext ctx) {
		try {
			PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(getEnvironFile()), "UTF-8"), true);
			String u = ctx.getUsername();
			String a = ctx.getApplicationPaths();
			String d = ctx.getDocumentPaths();
			String i = ctx.getIncludePaths();
			if (u != null) {
				String[] ss = u.split("\r|\n");
				for (String s : ss) {
					if (s.length() > 0) {
						out.println("Username="+s);
					}
				}
			}
			if (a != null) {
				String[] ss = a.split("\r|\n");
				for (String s : ss) {
					if (s.length() > 0) {
						out.println("Applications="+s);
					}
				}
			}
			if (d != null) {
				String[] ss = d.split("\r|\n");
				for (String s : ss) {
					if (s.length() > 0) {
						out.println("Documents="+s);
					}
				}
			}
			if (i != null) {
				String[] ss = d.split("\r|\n");
				for (String s : ss) {
					if (s.length() > 0) {
						out.println("Includes="+s);
					}
				}
			}
			out.close();
		} catch (IOException ioe) {}
	}
	
	private static class TestUI implements XNUI {
		private XNUI ui;
		private PrintWriter put;
		public TestUI(XNUI ui, PrintStream put) {
			this.ui = ui;
			try {
				this.put = new PrintWriter(new OutputStreamWriter(put, "UTF-8"), true);
			} catch (UnsupportedEncodingException uee) {
				this.put = new PrintWriter(new OutputStreamWriter(put), true);
			}
		}
		public void put(String s) {
			put.println(s);
		}
		public String answer(String prompt, String[] options, int x, int y) {
			return ui.answer(prompt, options, x, y);
		}
		public File answerDisk(String prompt, int x, int y) {
			return ui.answerDisk(prompt, x, y);
		}
		public File answerFile(String prompt, String[] types, int x, int y) {
			return ui.answerFile(prompt, types, x, y);
		}
		public File answerFolder(String prompt, int x, int y) {
			return ui.answerFolder(prompt, x, y);
		}
		public String answerList(String prompt, String[] options, int x, int y) {
			return ui.answerList(prompt, options, x, y);
		}
		public String ask(String prompt, String deftext, int x, int y) {
			return ui.ask(prompt, deftext, x, y);
		}
		public File askFile(String prompt, String deftext, int x, int y) {
			return ui.askFile(prompt, deftext, x, y);
		}
		public File askFolder(String prompt, String deftext, int x, int y) {
			return ui.askFolder(prompt, deftext, x, y);
		}
		public String askPassword(String prompt, String deftext, int x, int y) {
			return ui.askPassword(prompt, deftext, x, y);
		}
		public void beep() {
			ui.beep();
		}
		public void promptSecurity(XNSecurityKey[] type, boolean[] allow, boolean[] forall, Map<String,String>[] details) {
			ui.promptSecurity(type, allow, forall, details);
		}
	}
}
