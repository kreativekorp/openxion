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
 * @since OpenXION 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.xiondoc;

import java.io.*;

public class XIONDoc {
	public static final String XIONDOC_NAME = "XIONDoc";
	public static final String XIONDOC_VERSION = "1.1";
	
	public static void main(String[] args) {
		if (args.length == 0) {
			help();
		} else {
			boolean processOptions = true;
			String textEncoding = "UTF-8";
			XIONDocReader reader = new XNDReader();
			XIONDocWriter writer = new HTMLDWriter();
			File outputFile = null;
			DocumentationSet outputSet = null;
			Option lastOption = Option.DOCUMENTATION_FILE;
			for (String arg : args) {
				if (processOptions && arg.startsWith("-")) {
					if (arg.equals("-f")) lastOption = Option.DOCUMENTATION_FILE;
					else if (arg.equals("-E")) lastOption = Option.TEXT_ENCODING;
					else if (arg.equals("-R")) lastOption = Option.READER;
					else if (arg.equals("-W")) lastOption = Option.WRITER;
					else if (arg.equals("-o")) lastOption = Option.OUTPUT_FILE;
					else if (arg.equals("-h") || arg.equals("-help") || arg.equals("--help")) help();
					else if (arg.equals("-v") || arg.equals("-version") || arg.equals("--version")) version();
					else if (arg.equals("--")) processOptions = false;
					else System.err.println("Unrecognized option: "+arg);
				} else {
					switch (lastOption) {
					case DOCUMENTATION_FILE:
						try {
							File f = new File(arg);
							RandomAccessFile raf = new RandomAccessFile(f, "r");
							byte[] stuff = new byte[(int)raf.length()];
							raf.readFully(stuff);
							raf.close();
							String xnd = new String(stuff, textEncoding);
							if (outputFile != null && outputSet != null) {
								reader.read(xnd, outputSet);
							} else {
								DocumentationSet d = new DocumentationSet();
								reader.read(xnd, d);
								File f2 = writer.derive(reader.derive(f));
								writer.write(d, f2);
							}
						} catch (IOException ioe) {
							System.err.println("Error generating documentation from "+arg+":");
							ioe.printStackTrace();
						}
						break;
					case TEXT_ENCODING:
						textEncoding = arg;
						break;
					case READER:
						try {
							reader = Class.forName(arg).asSubclass(XIONDocReader.class).newInstance();
						} catch (Exception e) {
							System.err.println("Error loading reader "+arg+":");
							e.printStackTrace();
						}
						break;
					case WRITER:
						try {
							writer = Class.forName(arg).asSubclass(XIONDocWriter.class).newInstance();
						} catch (Exception e) {
							System.err.println("Error loading writer "+arg+":");
							e.printStackTrace();
						}
						break;
					case OUTPUT_FILE:
						if (outputFile != null && outputSet != null) {
							try {
								writer.write(outputSet, outputFile);
							} catch (IOException ioe) {
								System.err.println("Error generating documentation:");
								ioe.printStackTrace();
							}
						}
						outputFile = new File(arg);
						outputSet = new DocumentationSet();
						break;
					}
					lastOption = Option.DOCUMENTATION_FILE;
				}
			}
			if (outputFile != null && outputSet != null) {
				try {
					writer.write(outputSet, outputFile);
				} catch (IOException ioe) {
					System.err.println("Error generating documentation:");
					ioe.printStackTrace();
				}
			}
		}
	}
	
	private static enum Option {
		DOCUMENTATION_FILE,
		TEXT_ENCODING,
		READER,
		WRITER,
		OUTPUT_FILE
	}
	
	private static void help() {
		// // // // // // //<---10---><---20---><---30---><---40---><---50---><---60---><---70---><---80--->
		System.out.println("Usage: xiondoc [options] [--] [docfile] [docfile] [...]");
		System.out.println("  -E encoding     specify the text encoding used to read documentation files");
		System.out.println("                  (default is UTF-8)");
		System.out.println("  -f docfile      generate documentation from the specified documentation file");
		System.out.println("  -h              print help screen");
		// // // // // // //<---10---><---20---><---30---><---40---><---50---><---60---><---70---><---80--->
		System.out.println("  -o outputfile   generate a single unified documentation set at the given path");
		System.out.println("                  (default is to generate a documentation set for each file)");
		System.out.println("  -R classname    use the specified documentation reader");
		System.out.println("                  (default is com.kreative.xiondoc.XNDReader)");
		System.out.println("  -v              print XIONDoc, Java, and OS version numbers");
		System.out.println("  -W classname    use the specified documentation writer");
		System.out.println("                  (default is com.kreative.xiondoc.HTMLDWriter)");
		System.out.println("  --help          print help screen");
		System.out.println("  --version       print XIONDoc, Java, and OS version numbers");
		System.out.println("  --              treat remaining arguments as file names");
	}
	
	private static void version() {
		System.out.println(XIONDOC_NAME + " " + XIONDOC_VERSION);
		System.out.println(System.getProperty("java.runtime.name") + " " + System.getProperty("java.runtime.version"));
		System.out.println(System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version"));
		System.out.println(System.getProperty("os.name") + " " + System.getProperty("os.version"));
	}
}
