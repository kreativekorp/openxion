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

package com.kreative.openxion.xom.inst;

import java.util.*;
import java.text.*;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.util.XIONUtil;
import com.kreative.openxion.util.XNDateFormat;
import com.kreative.openxion.xom.XOMValue;
import com.kreative.openxion.xom.XOMMorphError;
import com.kreative.openxion.xom.XOMVariant;

public class XOMDate extends XOMValue {
	private static final long serialVersionUID = 1L;
	
	private XNDateFormat theFormat;
	private GregorianCalendar theDate;
	
	public XOMDate() {
		this.theFormat = XNDateFormat.SHORT_DATE;
		this.theDate = new GregorianCalendar();
	}
	
	public XOMDate(XNDateFormat theFormat) {
		this.theFormat = theFormat;
		this.theDate = new GregorianCalendar();
	}
	
	public XOMDate(GregorianCalendar theDate) {
		this.theFormat = XNDateFormat.SHORT_DATE;
		this.theDate = theDate;
	}
	
	public XOMDate(Date theDate) {
		this.theFormat = XNDateFormat.SHORT_DATE;
		this.theDate = new GregorianCalendar();
		this.theDate.setTime(theDate);
	}
	
	public XOMDate(XNDateFormat theFormat, GregorianCalendar theDate) {
		this.theFormat = theFormat;
		this.theDate = theDate;
	}
	
	public XOMDate(XNDateFormat theFormat, Date theDate) {
		this.theFormat = theFormat;
		this.theDate = new GregorianCalendar();
		this.theDate.setTime(theDate);
	}
	
	public XOMDate(String s) {
		theDate = new GregorianCalendar();
		Date parsedDate = null;
		ParsePosition pos = new ParsePosition(0);
		List<XNDateFormat> formats = XNDateFormat.allForName("datetime");
		for (XNDateFormat fmt : formats) {
			parsedDate = fmt.toJavaDateFormat().parse(s,pos);
			if (parsedDate != null && s.substring(pos.getIndex()).trim().length() == 0) {
				theDate.setTime(parsedDate);
				theFormat = fmt;
				return;
			}
		}
		throw new XOMMorphError("date");
	}
	
	public GregorianCalendar toCalendar() {
		return theDate;
	}
	
	public Date toDate() {
		return theDate.getTime();
	}
	
	public String toLanguageString() {
		if (theFormat == null || theDate == null) return "\"\"";
		return XIONUtil.quote(theFormat.toJavaDateFormat().format(theDate.getTime()));
	}
	public String toTextString(XNContext ctx) {
		if (theFormat == null || theDate == null) return "";
		return theFormat.toJavaDateFormat().format(theDate.getTime());
	}
	public List<? extends XOMVariant> toList(XNContext ctx) {
		return Arrays.asList(this);
	}
	public int hashCode() {
		return theFormat.hashCode() ^ theDate.hashCode();
	}
	public boolean equals(Object o) {
		if (o instanceof XOMDate) {
			XOMDate other = (XOMDate)o;
			return this.theFormat == other.theFormat && this.theDate.equals(other.theDate);
		} else {
			return false;
		}
	}
}
