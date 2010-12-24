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

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateFormat {
	public static final Calendar MATLAB = new GregorianCalendar(0, Calendar.JANUARY, 1);
	public static final Calendar TURBO_DB = new GregorianCalendar(0, Calendar.JANUARY, 1);
	public static final Calendar SYMBIAN = new GregorianCalendar(1, Calendar.JANUARY, 1);
	public static final Calendar MS_DOTNET = new GregorianCalendar(1, Calendar.JANUARY, 1);
	public static final Calendar REXX = new GregorianCalendar(1, Calendar.JANUARY, 1);
	public static final Calendar NTFS = new GregorianCalendar(1601, Calendar.JANUARY, 1);
	public static final Calendar COBOL = new GregorianCalendar(1601, Calendar.JANUARY, 1);
	public static final Calendar WIN32 = new GregorianCalendar(1601, Calendar.JANUARY, 1);
	public static final Calendar WIN64 = new GregorianCalendar(1601, Calendar.JANUARY, 1);
	public static final Calendar MS_SQL = new GregorianCalendar(1753, Calendar.JANUARY, 1);
	public static final Calendar MUMPS = new GregorianCalendar(1840, Calendar.DECEMBER, 31);
	public static final Calendar VMS = new GregorianCalendar(1858, Calendar.NOVEMBER, 17);
	public static final Calendar MS_COM_DATE = new GregorianCalendar(1899, Calendar.DECEMBER, 30);
	public static final Calendar MS_EXCEL = new GregorianCalendar(1899, Calendar.DECEMBER, 31);
	public static final Calendar LOTUS_123 = new GregorianCalendar(1899, Calendar.DECEMBER, 31);
	public static final Calendar NTP = new GregorianCalendar(1900, Calendar.JANUARY, 1);
	public static final Calendar IBM_CICS = new GregorianCalendar(1900, Calendar.JANUARY, 1);
	public static final Calendar MATHEMATICA = new GregorianCalendar(1900, Calendar.JANUARY, 1);
	public static final Calendar RISC_OS = new GregorianCalendar(1900, Calendar.JANUARY, 1);
	public static final Calendar LISP = new GregorianCalendar(1900, Calendar.JANUARY, 1);
	public static final Calendar LABVIEW = new GregorianCalendar(1904, Calendar.JANUARY, 1);
	public static final Calendar MAC_OS = new GregorianCalendar(1904, Calendar.JANUARY, 1);
	public static final Calendar PALM_OS = new GregorianCalendar(1904, Calendar.JANUARY, 1);
	public static final Calendar MP4 = new GregorianCalendar(1904, Calendar.JANUARY, 1);
	public static final Calendar IGOR_PRO = new GregorianCalendar(1904, Calendar.JANUARY, 1);
	public static final Calendar S_PLUS = new GregorianCalendar(1960, Calendar.JANUARY, 1);
	public static final Calendar SAS = new GregorianCalendar(1960, Calendar.JANUARY, 1);
	public static final Calendar PICKOS = new GregorianCalendar(1967, Calendar.DECEMBER, 31);
	public static final Calendar UNIX = new GregorianCalendar(1970, Calendar.JANUARY, 1);
	public static final Calendar AMIGAOS = new GregorianCalendar(1978, Calendar.JANUARY, 1);
	public static final Calendar MSDOS = new GregorianCalendar(1980, Calendar.JANUARY, 1);
	public static final Calendar OS_2 = new GregorianCalendar(1980, Calendar.JANUARY, 1);
	public static final Calendar BREW = new GregorianCalendar(1980, Calendar.JANUARY, 6);
	public static final Calendar GPS = new GregorianCalendar(1980, Calendar.JANUARY, 6);
	public static final Calendar NETFS = new GregorianCalendar(1981, Calendar.JANUARY, 1);
	public static final Calendar APPLESINGLE = new GregorianCalendar(2000, Calendar.JANUARY, 1);
	public static final Calendar APPLEDOUBLE = new GregorianCalendar(2000, Calendar.JANUARY, 1);
	public static final Calendar COCOA = new GregorianCalendar(2001, Calendar.JANUARY, 1);
	
	public static final int SECONDS = 0;
	public static final int MILLISECONDS = -3;
	public static final int MICROSECONDS = -6;
	public static final int NANOSECONDS = -9;
	
	private Calendar epoch;
	private int scale;
	
	public DateFormat(Calendar epoch) {
		this.epoch = epoch;
		this.scale = 0;
	}
	
	public DateFormat(Calendar epoch, int scale) {
		this.epoch = epoch;
		this.scale = scale;
	}
	
	public DateFormat(int year) {
		this.epoch = new GregorianCalendar(year, Calendar.JANUARY, 1);
		this.scale = 0;
	}
	
	public DateFormat(int year, int scale) {
		this.epoch = new GregorianCalendar(year, Calendar.JANUARY, 1);
		this.scale = scale;
	}
	
	public DateFormat(int year, int month, int day) {
		this.epoch = new GregorianCalendar(year, month, day);
		this.scale = 0;
	}
	
	public DateFormat(int year, int month, int day, int scale) {
		this.epoch = new GregorianCalendar(year, month, day);
		this.scale = scale;
	}
	
	public DateFormat(int year, int month, int day, int hour, int minute, int second) {
		this.epoch = new GregorianCalendar(year, month, day, hour, minute, second);
		this.scale = 0;
	}
	
	public DateFormat(int year, int month, int day, int hour, int minute, int second, int scale) {
		this.epoch = new GregorianCalendar(year, month, day, hour, minute, second);
		this.scale = scale;
	}
	
	public Calendar getEpoch() {
		return epoch;
	}
	
	public int getScale() {
		return scale;
	}
	
	public Calendar longToCalendar(long s) {
		if (scale < -3) {
			for (int i = scale; i < -3; i++) {
				s /= 10L;
			}
		} else if (scale > -3) {
			for (int i = scale; i > -3; i--) {
				s *= 10L;
			}
		}
		GregorianCalendar g = new GregorianCalendar();
		g.setTimeInMillis(epoch.getTimeInMillis() + s);
		return g;
	}
	
	public long calendarToLong(Calendar g) {
		long s = g.getTimeInMillis() - epoch.getTimeInMillis();
		if (scale < -3) {
			for (int i = scale; i < -3; i++) {
				s *= 10L;
			}
		} else if (scale > -3) {
			for (int i = scale; i > -3; i--) {
				s /= 10L;
			}
		}
		return s;
	}
	
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o instanceof DateFormat) {
			DateFormat other = (DateFormat)o;
			return (this.epoch.equals(other.epoch) && this.scale == other.scale);
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return this.epoch.hashCode() ^ this.scale;
	}
	
	public String toString() {
		return this.epoch.toString() + ", " + this.scale;
	}
}
