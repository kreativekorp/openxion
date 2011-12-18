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
 * The precedence of an operator.
 * @since XIONDoc 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public enum Precedence {
	NULL        (""                    ),
	UNARY       ("Unary & Conditional" ),
	EXPONENT    ("Exponentiation"      ),
	MULTIPLY    ("Multiplication"      ),
	ADD         ("Addition"            ),
	SHIFT       ("Bit Shift"           ),
	BIT_AND     ("Bitwise AND"         ),
	BIT_XOR     ("Bitwise XOR"         ),
	BIT_OR      ("Bitwise OR"          ),
	STR_CONCAT  ("String Concatenation"),
	LIST_CONCAT ("List Concatenation"  ),
	RELATION    ("Relational"          ),
	IS_A        ("Polymorphic"         ),
	EQUAL       ("Equivalence"         ),
	AND         ("Boolean AND"         ),
	XOR         ("Boolean XOR"         ),
	OR          ("Boolean OR"          ),
	LIST        ("List Construction"   );
	
	private String name;
	
	private Precedence(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static Precedence forName(String name) {
		for (Precedence p : values()) {
			if (p.name.equalsIgnoreCase(name)) return p;
		}
		return null;
	}
	
	public static Precedence forString(String s) {
		for (Precedence p : values()) {
			if (p.name.equalsIgnoreCase(s)) return p;
		}
		for (Precedence p : values()) {
			if (p.name.toLowerCase().contains(s.toLowerCase())) return p;
		}
		return null;
	}
}
