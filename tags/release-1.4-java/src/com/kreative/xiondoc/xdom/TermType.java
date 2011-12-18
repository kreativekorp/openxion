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

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * The type of an XION term; whether it is a command, function, property, etc.
 * @since XIONDoc 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public enum TermType {
	CONTROL_STRUCTURE ( "cs", "structure", "control structure", "control structures" ),
	KEYWORD           ( "kw", "keyword",   "other keyword",     "other keywords"     ),
	EVENT             ( "ev", "event",     "event",             "events"             ),
	COMMAND           ( "cm", "command",   "command",           "commands"           ),
	FUNCTION          ( "fn", "function",  "function",          "functions"          ),
	DATA_TYPE         ( "dt", "datatype",  "data type",         "data types"         ),
	PROPERTY          ( "pr", "property",  "property",          "properties"         ),
	OPERATOR          ( "op", "operator",  "operator",          "operators"          ),
	CONSTANT          ( "cn", "constant",  "constant",          "constants"          ),
	ORDINAL           ( "or", "ordinal",   "ordinal",           "ordinals"           ),
	IO_METHOD         ( "mt", "iomethod",  "I/O method",        "I/O methods"        ),
	IO_MANAGER        ( "mg", "iomanager", "I/O manager",       "I/O managers"       ),
	EXTERNAL_LANGUAGE ( "xl", "extlang",   "external language", "external languages" ),
	VERSION           ( "vr", "version",   "version",           "versions"           );
	
	private String twoletter, xml, singular, plural;
	
	private TermType(String twoletter, String xml, String singular, String plural) {
		this.twoletter = twoletter;
		this.xml = xml;
		this.singular = singular;
		this.plural = plural;
	}
	
	private static String tcase(String oldstr) {
		StringBuffer newstr = new StringBuffer(oldstr.length());
		CharacterIterator ci = new StringCharacterIterator(oldstr);
		for (char pch = ' ', ch = ci.first(); ch != CharacterIterator.DONE; pch = ch, ch = ci.next()) {
			if (!Character.isLetter(pch)) newstr.append(Character.toTitleCase(ch));
			else newstr.append(Character.toLowerCase(ch));
		}
		return newstr.toString();
	}
	
	public String getCode() { return twoletter; }
	public String getTagName() { return xml; }
	public String getSingular() { return singular; }
	public String getSingularTitleCase() { return tcase(singular); }
	public String getPlural() { return plural; }
	public String getPluralTitleCase() { return tcase(plural); }
	
	public static TermType forCode(String code) {
		for (TermType t : values()) {
			if (t.twoletter.equalsIgnoreCase(code)) return t;
		}
		return null;
	}
	
	public static TermType forTagName(String tag) {
		for (TermType t : values()) {
			if (t.xml.equalsIgnoreCase(tag)) return t;
		}
		return null;
	}
	
	public static TermType forSingular(String s) {
		for (TermType t : values()) {
			if (t.singular.equalsIgnoreCase(s)) return t;
		}
		return null;
	}
	
	public static TermType forPlural(String p) {
		for (TermType t : values()) {
			if (t.plural.equalsIgnoreCase(p)) return t;
		}
		return null;
	}
}
