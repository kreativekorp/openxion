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

package com.kreative.openxion.util;

import java.text.*;
import java.util.*;

/**
 * A DateFormat that allows formatting and parsing
 * as the number of seconds since January 1, 1904
 * (the Mac OS epoch).
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class SecondsDateFormat extends DateFormat {
	private static final long serialVersionUID = 1L;
	
	public static final SecondsDateFormat instance = new SecondsDateFormat();

	private static final Date EPOCH = new GregorianCalendar(1904, GregorianCalendar.JANUARY, 1, 0, 0, 0).getTime();
	
	@Override
	public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
		if (toAppendTo == null) toAppendTo = new StringBuffer();
		toAppendTo.append(Long.toString((date.getTime() - EPOCH.getTime()) / 1000L));
		return toAppendTo;
	}
	
	private Long parseLong(String source, ParsePosition pos) {
		int start = pos.getIndex();
		int end = start;
		if ((end < source.length()) && ((source.charAt(end) == '+') || (source.charAt(end) == '-'))) end++;
		while ((end < source.length() && Character.isDigit(source.charAt(end)))) end++;
		try {
			long l = Long.parseLong(source.substring(start,end).trim());
			pos.setIndex(end);
			return l;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Date parse(String source, ParsePosition pos) {
		Long l = parseLong(source, pos);
		return (l == null) ? null : new Date(EPOCH.getTime() + (l * 1000L));
	}
}
