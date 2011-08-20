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
 * @since XIONDoc 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.xiondoc.xdom;

/**
 * A kind of object descriptor.
 * @since XIONDoc 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public enum Descriptor {
	SINGLETON           ( "singleton",           "the $",                               "singleton"     ),
	CHILD_SINGLETON     ( "child-singleton",     "the $ of steve",                      "singleton"     ),
	INDEX               ( "index",               "$ 12",                                "index"         ),
	CHILD_INDEX         ( "child-index",         "$ 12 of steve",                       "index"         ),
	INDEX_RANGE         ( "index-range",         "$ 2 through 7",                       "index range"   ),
	CHILD_INDEX_RANGE   ( "child-index-range",   "$ 2 through 7 of steve",              "index range"   ),
	ORDINAL             ( "ordinal",             "the fifth $",                         "ordinal"       ),
	CHILD_ORDINAL       ( "child-ordinal",       "the fifth $ of steve",                "ordinal"       ),
	ORDINAL_RANGE       ( "ordinal-range",       "the third through eighth $",          "ordinal range" ),
	CHILD_ORDINAL_RANGE ( "child-ordinal-range", "the third through eighth $ of steve", "ordinal range" ),
	ID                  ( "id",                  "$ id 1719",                           "ID"            ),
	CHILD_ID            ( "child-id",            "$ id 1719 of steve",                  "ID"            ),
	NAME                ( "name",                "$ \"steve\"",                         "name"          ),
	CHILD_NAME          ( "child-name",          "$ \"andy\" of steve",                 "name"          ),
	MASS                ( "mass",                "the $",                               "mass"          ),
	CHILD_MASS          ( "child-mass",          "the $ of steve",                      "mass"          );
	
	private String code, example, name;
	
	private Descriptor(String code, String example, String name) {
		this.code = code;
		this.example = example;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getExample(String term) {
		return example.replace("$", term);
	}
	
	public String getName() {
		return name+" descriptor";
	}
	
	public static Descriptor forCode(String code) {
		for (Descriptor dt : values()) {
			if (dt.code.equalsIgnoreCase(code)) return dt;
		}
		return null;
	}
}
