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

package com.kreative.openxion.binpack;

public enum DataType {
	//TYPE  (CHV, SZ, ELABORATIONTYPE              ,  SIZE, ENDNS,  ELAB,  CCNT,  RTNS),
	BOOLEAN ('T',  8, null                         ,  true,  true, false, false,  true),
	ENUM    ('E',  8, ElaborationType.KV_UNSIGNED  ,  true,  true,  true, false,  true),
	BITFIELD('J',  8, ElaborationType.KV_SIGNED    ,  true,  true,  true, false,  true),
	BINT    ('B', 32, null                         ,  true,  true, false, false,  true),
	OINT    ('O', 32, null                         ,  true,  true, false, false,  true),
	HINT    ('H', 32, null                         ,  true,  true, false, false,  true),
	UINT    ('U', 32, null                         ,  true,  true, false, false,  true),
	SINT    ('I', 32, null                         ,  true,  true, false, false,  true),
	UFIXED  ('R', 32, null                         ,  true,  true, false, false,  true),
	SFIXED  ('Q', 32, null                         ,  true,  true, false, false,  true),
	FLOAT   ('F', 32, ElaborationType.FP_FORMAT    ,  true,  true,  true, false,  true),
	COMPLEX ('K', 32, ElaborationType.FP_FORMAT    ,  true,  true,  true, false,  true),
	CHAR    ('C',  8, ElaborationType.TEXT_ENCODING,  true,  true,  true, false,  true),
	PSTRING ('P',  8, ElaborationType.TEXT_ENCODING,  true,  true,  true, false,  true),
	CSTRING ('S',  8, ElaborationType.TEXT_ENCODING,  true, false,  true, false,  true),
	DATE    ('D', 32, ElaborationType.DATE_FORMAT  ,  true,  true,  true, false,  true),
	COLOR   ('X', 32, ElaborationType.COLOR_FORMAT ,  true,  true,  true, false,  true),
	FILLER  ('Z',  8, null                         ,  true, false, false, false, false),
	MAGIC   ('M', 32, ElaborationType.VALUE        ,  true,  true,  true, false, false),
	ALIGN   ('A', 32, null                         ,  true, false, false, false, false),
	BINARY  ('*',  0, null                         , false, false, false,  true,  true),
	STRUCT  ('$',  0, ElaborationType.STRUCT       , false, false,  true, false,  true),
	OFFSET  ('@',  0, null                         , false, false, false,  true, false);
	//GLNVWY
	
	private char charValue;
	private int defaultSize;
	private ElaborationType elaborationType;
	private boolean usesSize;
	private boolean usesEndianness;
	private boolean usesElaboration;
	private boolean usesCustomCount;
	private boolean returns;
	
	private DataType(char charValue, int defaultSize, ElaborationType elaborationType, boolean usesSize, boolean usesEndianness, boolean usesElaboration, boolean usesCustomCount, boolean returns) {
		this.charValue = Character.toUpperCase(charValue);
		this.defaultSize = defaultSize;
		this.elaborationType = elaborationType;
		this.usesSize = usesSize;
		this.usesEndianness = usesEndianness;
		this.usesElaboration = usesElaboration;
		this.usesCustomCount = usesCustomCount;
		this.returns = returns;
	}
	
	public char charValue() {
		return charValue;
	}
	
	public int defaultSize() {
		return defaultSize;
	}
	
	public ElaborationType elaborationType() {
		return elaborationType;
	}
	
	public boolean usesSize() {
		return usesSize;
	}
	
	public boolean usesEndianness() {
		return usesEndianness;
	}
	
	public boolean usesElaboration() {
		return usesElaboration;
	}
	
	public boolean usesCustomCount() {
		return usesCustomCount;
	}
	
	public boolean returns() {
		return returns;
	}
	
	public char toChar() {
		return charValue;
	}
	
	public String toString() {
		return name().toLowerCase();
	}
	
	public String toShortString() {
		return Character.toString(charValue);
	}
	
	public static DataType fromChar(char ch) {
		ch = Character.toUpperCase(ch);
		for (DataType dt : values()) {
			if (dt.charValue == ch) {
				return dt;
			}
		}
		return null;
	}
	
	public static DataType fromString(String s) {
		for (DataType dt : values()) {
			if (dt.name().equalsIgnoreCase(s)) {
				return dt;
			}
		}
		return null;
	}
	
	public static DataType fromShortString(String s) {
		if (s.length() == 1) {
			char ch = Character.toUpperCase(s.charAt(0));
			for (DataType dt : values()) {
				if (dt.charValue == ch) {
					return dt;
				}
			}
		}
		return null;
	}
}
