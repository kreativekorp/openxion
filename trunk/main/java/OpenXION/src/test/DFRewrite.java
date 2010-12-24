/*
 * Copyright &copy; 2010 Rebecca G. Bettencourt / Kreative Software
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

package test;

import java.io.StringReader;
import java.util.List;
import com.kreative.openxion.binpack.DataField;
import com.kreative.openxion.binpack.DataFormatParser;

public class DFRewrite {
	public static void main(String[] args) {
		Boolean simpleInput = null;
		Boolean simpleOutput = null;
		for (String arg : args) {
			if (arg.startsWith("-")) {
				if (arg.equals("-s")) simpleInput = Boolean.TRUE;
				else if (arg.equals("-S")) simpleOutput = Boolean.TRUE;
				else if (arg.equals("-f")) simpleInput = Boolean.FALSE;
				else if (arg.equals("-F")) simpleOutput = Boolean.FALSE;
				else if (arg.equals("-a")) simpleInput = null;
				else if (arg.equals("-A")) simpleOutput = null;
				else System.err.println("Unknown option: " + arg);
			} else try {
				List<DataField> format;
				if (simpleInput == null) {
					format = new DataFormatParser(new StringReader(arg)).parseAuto();
				} else if (simpleInput.booleanValue()) {
					format = new DataFormatParser(new StringReader(arg)).parseShortForm();
				} else {
					format = new DataFormatParser(new StringReader(arg)).parseLongForm();
				}
				String fs;
				if (simpleOutput == null) {
					if (simpleInput == null) {
						if (new DataFormatParser(new StringReader(arg)).isShortForm()) {
							fs = DataFormatParser.toShortString(format);
						} else {
							fs = DataFormatParser.toLongString(format);
						}
					} else if (simpleInput.booleanValue()) {
						fs = DataFormatParser.toShortString(format);
					} else {
						fs = DataFormatParser.toLongString(format);
					}
				} else if (simpleOutput.booleanValue()) {
					fs = DataFormatParser.toShortString(format);
				} else {
					fs = DataFormatParser.toLongString(format);
				}
				System.out.println(fs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
