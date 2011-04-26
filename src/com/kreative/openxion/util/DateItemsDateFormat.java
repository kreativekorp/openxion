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

import java.text.*;
import java.util.*;

/**
 * A DateFormat that allows formatting and parsing of
 * HyperCard's dateItems format. This consists of seven
 * comma-delimited numbers: the year, the month (with 1
 * for January), the day of month, the hour (from 0 to 23),
 * the minute, the second, and the day of week (with 1
 * for Sunday and 7 for Saturday).
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class DateItemsDateFormat extends DateFormat {
	private static final long serialVersionUID = 1L;
	
	public static final DateItemsDateFormat instance = new DateItemsDateFormat();
	
	private static int monthToInt(int month) {
		switch (month) {
		case GregorianCalendar.JANUARY: return 1;
		case GregorianCalendar.FEBRUARY: return 2;
		case GregorianCalendar.MARCH: return 3;
		case GregorianCalendar.APRIL: return 4;
		case GregorianCalendar.MAY: return 5;
		case GregorianCalendar.JUNE: return 6;
		case GregorianCalendar.JULY: return 7;
		case GregorianCalendar.AUGUST: return 8;
		case GregorianCalendar.SEPTEMBER: return 9;
		case GregorianCalendar.OCTOBER: return 10;
		case GregorianCalendar.NOVEMBER: return 11;
		case GregorianCalendar.DECEMBER: return 12;
		case GregorianCalendar.UNDECIMBER: return 13;
		default: return 0;
		}
	}
	
	private static int dayOfWeekToInt(int day) {
		switch (day) {
		case GregorianCalendar.SUNDAY: return 1;
		case GregorianCalendar.MONDAY: return 2;
		case GregorianCalendar.TUESDAY: return 3;
		case GregorianCalendar.WEDNESDAY: return 4;
		case GregorianCalendar.THURSDAY: return 5;
		case GregorianCalendar.FRIDAY: return 6;
		case GregorianCalendar.SATURDAY: return 7;
		default: return 0;
		}
	}
	
	private static int intToMonth(int i) {
		switch (i) {
		case 1: return GregorianCalendar.JANUARY;
		case 2: return GregorianCalendar.FEBRUARY;
		case 3: return GregorianCalendar.MARCH;
		case 4: return GregorianCalendar.APRIL;
		case 5: return GregorianCalendar.MAY;
		case 6: return GregorianCalendar.JUNE;
		case 7: return GregorianCalendar.JULY;
		case 8: return GregorianCalendar.AUGUST;
		case 9: return GregorianCalendar.SEPTEMBER;
		case 10: return GregorianCalendar.OCTOBER;
		case 11: return GregorianCalendar.NOVEMBER;
		case 12: return GregorianCalendar.DECEMBER;
		case 13: return GregorianCalendar.UNDECIMBER;
		default: return 0;
		}
	}
	
	@Override
	public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
		if (toAppendTo == null) toAppendTo = new StringBuffer();
		GregorianCalendar gcal = new GregorianCalendar();
		gcal.setTime(date);
		toAppendTo.append(
				gcal.get(GregorianCalendar.YEAR)
				+","+monthToInt(gcal.get(GregorianCalendar.MONTH))
				+","+gcal.get(GregorianCalendar.DAY_OF_MONTH)
				+","+gcal.get(GregorianCalendar.HOUR_OF_DAY)
				+","+gcal.get(GregorianCalendar.MINUTE)
				+","+gcal.get(GregorianCalendar.SECOND)
				+","+dayOfWeekToInt(gcal.get(GregorianCalendar.DAY_OF_WEEK))
		);
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
	
	private boolean parseComma(String source, ParsePosition pos) {
		int i = pos.getIndex();
		while (i < source.length() && Character.isSpaceChar(source.charAt(i))) i++;
		if (i < source.length() && source.charAt(i) == ',') i++;
		else return false;
		while (i < source.length() && Character.isSpaceChar(source.charAt(i))) i++;
		pos.setIndex(i);
		return true;
	}

	@Override
	public Date parse(String source, ParsePosition pos) {
		ParsePosition p = new ParsePosition(pos.getIndex());
		Long yr = parseLong(source, p); if (yr == null) return null;
		if (!parseComma(source, p)) return null;
		Long mo = parseLong(source, p); if (mo == null) return null;
		if (!parseComma(source, p)) return null;
		Long dm = parseLong(source, p); if (dm == null) return null;
		if (!parseComma(source, p)) return null;
		Long hr = parseLong(source, p); if (hr == null) return null;
		if (!parseComma(source, p)) return null;
		Long mi = parseLong(source, p); if (mi == null) return null;
		if (!parseComma(source, p)) return null;
		Long sc = parseLong(source, p); if (sc == null) return null;
		if (!parseComma(source, p)) return null;
		Long dw = parseLong(source, p); if (dw == null) return null;
		pos.setIndex(p.getIndex());
		return new GregorianCalendar(
				yr.intValue(),intToMonth(mo.intValue()),dm.intValue(),
				hr.intValue(),mi.intValue(),sc.intValue()
		).getTime();
	}
}
