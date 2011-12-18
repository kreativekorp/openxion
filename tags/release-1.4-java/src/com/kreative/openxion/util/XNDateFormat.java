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
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.util;

import java.text.DateFormat;
import java.util.*;

/**
 * XNDateFormat enumerates the date formats supported
 * by <code>XOMDate</code> and the <code>convert</code>
 * command.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public enum XNDateFormat {
	SECONDS,
	DATEITEMS,
	SHORT_TIME,
	ABBREV_TIME,
	LONG_TIME,
	ENGLISH_TIME,
	SHORT_DATE,
	ABBREV_DATE,
	LONG_DATE,
	ENGLISH_DATE;
	
	public static XNDateFormat forName(String name) {
		name = name.toLowerCase().trim().replace(" ", "");
		
		if (name.equals("englishdate")) return ENGLISH_DATE;
		if (name.equals("longdate")) return LONG_DATE;
		if (name.equals("mediumdate")) return ABBREV_DATE;
		if (name.equals("meddate")) return ABBREV_DATE;
		if (name.equals("abbreviateddate")) return ABBREV_DATE;
		if (name.equals("abbrevdate")) return ABBREV_DATE;
		if (name.equals("abbrdate")) return ABBREV_DATE;
		if (name.equals("shortdate")) return SHORT_DATE;
		
		if (name.equals("englishtime")) return ENGLISH_TIME;
		if (name.equals("longtime")) return LONG_TIME;
		if (name.equals("mediumtime")) return ABBREV_TIME;
		if (name.equals("medtime")) return ABBREV_TIME;
		if (name.equals("abbreviatedtime")) return ABBREV_TIME;
		if (name.equals("abbrevtime")) return ABBREV_TIME;
		if (name.equals("abbrtime")) return ABBREV_TIME;
		if (name.equals("shorttime")) return SHORT_TIME;
		
		if (name.equals("dateitems")) return DATEITEMS;
		if (name.equals("seconds")) return SECONDS;
		
		return null;
	}
	
	public static List<XNDateFormat> allForName(String name) {
		List<XNDateFormat> formats = new Vector<XNDateFormat>();
		name = name.toLowerCase().trim().replace(" ", "");
		
		if (name.equals("englishdate") || name.equals("date") || name.equals("datetime") || name.equals("any")) formats.add(ENGLISH_DATE);
		if (name.equals("longdate") || name.equals("date") || name.equals("datetime") || name.equals("any")) formats.add(LONG_DATE);
		if (name.equals("mediumdate")
				|| name.equals("meddate")
				|| name.equals("abbreviateddate")
				|| name.equals("abbrevdate")
				|| name.equals("abbrdate") || name.equals("date") || name.equals("datetime") || name.equals("any")) formats.add(ABBREV_DATE);
		if (name.equals("shortdate") || name.equals("date") || name.equals("datetime") || name.equals("any")) formats.add(SHORT_DATE);
		
		if (name.equals("englishtime") || name.equals("time") || name.equals("datetime") || name.equals("any")) formats.add(ENGLISH_TIME);
		if (name.equals("longtime") || name.equals("time") || name.equals("datetime") || name.equals("any")) formats.add(LONG_TIME);
		if (name.equals("mediumtime")
				|| name.equals("medtime")
				|| name.equals("abbreviatedtime")
				|| name.equals("abbrevtime")
				|| name.equals("abbrtime") || name.equals("time") || name.equals("datetime") || name.equals("any")) formats.add(ABBREV_TIME);
		if (name.equals("shorttime") || name.equals("time") || name.equals("datetime") || name.equals("any")) formats.add(SHORT_TIME);
		
		if (name.equals("dateitems") || name.equals("any")) formats.add(DATEITEMS);
		if (name.equals("seconds") || name.equals("any")) formats.add(SECONDS);
		
		return formats;
	}
	
	public DateFormat toJavaDateFormat() {
		switch (this) {
			case SECONDS: return SecondsDateFormat.instance;
			case DATEITEMS: return DateItemsDateFormat.instance;
			case SHORT_TIME: return DateFormat.getTimeInstance(DateFormat.SHORT);
			case ABBREV_TIME: return DateFormat.getTimeInstance(DateFormat.MEDIUM);
			case LONG_TIME: return DateFormat.getTimeInstance(DateFormat.LONG);
			case ENGLISH_TIME: return DateFormat.getTimeInstance(DateFormat.LONG, Locale.US);
			case SHORT_DATE: return DateFormat.getDateInstance(DateFormat.SHORT);
			case ABBREV_DATE: return DateFormat.getDateInstance(DateFormat.MEDIUM);
			case LONG_DATE: return DateFormat.getDateInstance(DateFormat.LONG);
			case ENGLISH_DATE: return DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
			default: return SecondsDateFormat.instance;
		}
	}
	
	public XNDateFormat dateEquivalent() {
		switch (this) {
		case SHORT_TIME: case SHORT_DATE: return SHORT_DATE;
		case ABBREV_TIME: case ABBREV_DATE: return ABBREV_DATE;
		case LONG_TIME: case LONG_DATE: return LONG_DATE;
		case ENGLISH_TIME: case ENGLISH_DATE: return ENGLISH_DATE;
		default: return LONG_DATE;
		}
	}
	
	public XNDateFormat timeEquivalent() {
		switch (this) {
		case SHORT_TIME: case SHORT_DATE: return SHORT_TIME;
		case ABBREV_TIME: case ABBREV_DATE: return ABBREV_TIME;
		case LONG_TIME: case LONG_DATE: return LONG_TIME;
		case ENGLISH_TIME: case ENGLISH_DATE: return ENGLISH_TIME;
		default: return LONG_TIME;
		}
	}
}
