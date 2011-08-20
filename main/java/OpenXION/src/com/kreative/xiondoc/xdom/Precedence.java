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
	NULL        ( 0,  ""                     ),
	UNARY       ( 1,  "Unary & Conditional"  ),
	EXPONENT    ( 2,  "Exponentiation"       ),
	MULTIPLY    ( 3,  "Multiplication"       ),
	ADD         ( 4,  "Addition"             ),
	SHIFT       ( 5,  "Bit Shift"            ),
	BIT_AND     ( 6,  "Bitwise AND"          ),
	BIT_XOR     ( 7,  "Bitwise XOR"          ),
	BIT_OR      ( 8,  "Bitwise OR"           ),
	STR_CONCAT  ( 9,  "String Concatenation" ),
	LIST_CONCAT ( 10, "List Concatenation"   ),
	RELATION    ( 11, "Relational"           ),
	IS_A        ( 12, "Polymorphic"          ),
	EQUAL       ( 13, "Equivalence"          ),
	AND         ( 14, "Boolean AND"          ),
	XOR         ( 15, "Boolean XOR"          ),
	OR          ( 16, "Boolean OR"           ),
	LIST        ( 17, "List Construction"    );
	
	private int number;
	private String name;
	
	private Precedence(int number, String name) {
		this.number = number;
		this.name = name;
	}
	
	public int getNumber() {
		return number;
	}
	
	public String getName() {
		return name;
	}
	
	public static Precedence forNumber(int number) {
		for (Precedence p : values()) {
			if (p.number == number) return p;
		}
		return null;
	}
	
	public static Precedence forName(String name) {
		for (Precedence p : values()) {
			if (p.name.equalsIgnoreCase(name)) return p;
		}
		return null;
	}
	
	public static Precedence forString(String s) {
		try {
			int i = Integer.parseInt(s);
			for (Precedence p : values()) {
				if (p.number == i) return p;
			}
		} catch (NumberFormatException e) {}
		for (Precedence p : values()) {
			if (p.name.equalsIgnoreCase(s)) return p;
		}
		for (Precedence p : values()) {
			if (p.name.toLowerCase().contains(s.toLowerCase())) return p;
		}
		return null;
	}
}
