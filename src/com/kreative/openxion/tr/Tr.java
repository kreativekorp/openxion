/*
 * Copyright &copy; 2011 Rebecca G. Bettencourt / Kreative Software
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
 * @since OpenXION 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.tr;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Scanner;

public class Tr {
	public static void main(String[] args) {
		boolean parsingOptions = true;
		boolean c = false, d = false, s = false, e = false;
		String encoding = null;
		TrPattern a = null, b = null;
		for (String arg : args) {
			if (parsingOptions && arg.startsWith("-")) {
				CharacterIterator it = new StringCharacterIterator(arg.substring(1));
				for (char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
					switch (ch) {
					case 'c': c = true; break;
					case 'd': d = true; break;
					case 's': s = true; break;
					case 'e': e = true; break;
					default:
						System.err.println("tr: illegal option -- " + ch);
						printUsage();
						return;
					}
				}
			} else if (e) {
				e = false;
				encoding = arg;
			} else {
				parsingOptions = false;
				if (a == null) {
					a = TrPattern.compile(arg, c);
				} else if (b == null) {
					b = TrPattern.compile(arg, false);
				} else {
					printUsage();
					return;
				}
			}
		}
		Transformor tx;
		if (d) {
			if (s) {
				if (a == null || b == null) {
					printUsage();
					return;
				} else {
					tx = new Multiplexor(a.deletor(), b.squeezor());
				}
			} else {
				if (a == null || b != null) {
					printUsage();
					return;
				} else {
					tx = a.deletor();
				}
			}
		} else {
			if (s) {
				if (a == null) {
					printUsage();
					return;
				} else if (b == null) {
					tx = a.squeezor();
				} else {
					tx = new Multiplexor(TrPattern.translator(a, b), b.squeezor());
				}
			} else {
				if (a == null || b == null) {
					printUsage();
					return;
				} else {
					tx = TrPattern.translator(a, b);
				}
			}
		}
		try {
			Scanner sc = (encoding == null) ? new Scanner(System.in) : new Scanner(System.in, encoding);
			PrintWriter pr = new PrintWriter((encoding == null) ? new OutputStreamWriter(System.out) : new OutputStreamWriter(System.out, encoding), true);
			while (sc.hasNextLine()) {
				String text = sc.nextLine();
				text = tx.transformAll(text);
				pr.println(text);
			}
		} catch (IOException ioe) {
			System.err.println("tr: " + ioe.getMessage());
		}
	}
	
	private static void printUsage() {
		System.err.println("usage: tr [-e encoding] [-cs] string1 string2");
		System.err.println("       tr [-e encoding] [-c] -d string1");
		System.err.println("       tr [-e encoding] [-c] -s string1");
		System.err.println("       tr [-e encoding] [-c] -ds string1 string2");
	}
}
