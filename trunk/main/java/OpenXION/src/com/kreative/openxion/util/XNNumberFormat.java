/*
 * Copyright &copy; 2010-2011 Rebecca G. Bettencourt / Kreative Software
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
 * @since OpenXION 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.util;

import java.math.*;
import java.text.*;

/**
 * The XNNumberFormat class allows formatting and parsing of
 * numbers using the XION numberFormat property.
 * @since OpenXION 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XNNumberFormat {
	private static final long serialVersionUID = 1L;
	
	private String pattern;
	private DecimalFormat nonExpFormat;
	private DecimalFormat expFormat;
	private boolean expFormatUsesQuote;
	private boolean expLimitSet;
	private int minExp;
	private int maxExp;
	private BigDecimal bdmin, bdmax;
	private BigInteger bimin, bimax;
	private double dmin, dmax;
	
	public XNNumberFormat(String pattern) {
		if (pattern == null || pattern.trim().length() == 0) {
			this.pattern = "0.######";
			this.nonExpFormat = new DecimalFormat("0.######");
			this.expFormat = new DecimalFormat("0.######");
			this.expFormatUsesQuote = false;
			this.expLimitSet = false;
			this.minExp = 0;
			this.maxExp = 0;
		} else {
			this.pattern = pattern.trim();
			this.expFormatUsesQuote = false;
			this.expLimitSet = false;
			StringBuffer nesb = new StringBuffer();
			StringBuffer esb = new StringBuffer();
			StringBuffer mesb = new StringBuffer();
			StringBuffer xesb = new StringBuffer();
			int part = 0;
			CharacterIterator i = new StringCharacterIterator(pattern);
			for (char ch = i.first(); ch != CharacterIterator.DONE; ch = i.next()) {
				switch (part) {
				case 0:
					switch (ch) {
					case '0': case '.': case '#':
					case '%': case '\u2030': case '\u00A4':
						nesb.append(ch);
						esb.append(ch);
						break;
					case '1': case '2': case '3':
					case '4': case '5': case '6':
					case '7': case '8': case '9':
						nesb.append('0');
						esb.append('0');
						break;
					case 'E': case 'e':
						esb.append('E');
						expFormatUsesQuote = false;
						part = 1;
						break;
					case '\'':
						esb.append('E');
						expFormatUsesQuote = true;
						part = 1;
						break;
					}
					break;
				case 1:
					switch (ch) {
					case '0':
					case '1': case '2': case '3':
					case '4': case '5': case '6':
					case '7': case '8': case '9':
						esb.append('0');
						break;
					case '<':
						part = 2;
						break;
					case '>':
						part = 3;
						break;
					}
					break;
				case 2:
					switch (ch) {
					case '0':
					case '1': case '2': case '3':
					case '4': case '5': case '6':
					case '7': case '8': case '9':
					case '-':
						expLimitSet = true;
						mesb.append(ch);
						break;
					case '<':
						part = 2;
						break;
					case '>':
						part = 3;
						break;
					}
					break;
				case 3:
					switch (ch) {
					case '0':
					case '1': case '2': case '3':
					case '4': case '5': case '6':
					case '7': case '8': case '9':
					case '-':
						expLimitSet = true;
						xesb.append(ch);
						break;
					case '<':
						part = 2;
						break;
					case '>':
						part = 3;
						break;
					}
					break;
				}
			}
			this.nonExpFormat = new DecimalFormat(nesb.toString());
			this.expFormat = new DecimalFormat(esb.toString());
			try {
				this.minExp = Integer.parseInt(mesb.toString());
			} catch (NumberFormatException nfe) {
				this.minExp = 0;
			}
			try {
				this.maxExp = Integer.parseInt(xesb.toString());
			} catch (NumberFormatException nfe) {
				this.maxExp = 0;
			}
		}
		bdmin = BigDecimal.valueOf(1, -(minExp+1));
		bdmax = BigDecimal.valueOf(1, -maxExp);
		bimin = ((minExp+1) < 0) ? BigInteger.ZERO : BigInteger.TEN.pow((minExp+1));
		bimax = (maxExp < 0) ? BigInteger.ZERO : BigInteger.TEN.pow(maxExp);
		dmin = Math.pow(10, (minExp+1));
		dmax = Math.pow(10, maxExp);
	}
	
	public String pattern() {
		return pattern;
	}

	public String format(Number number) {
		if (number == null) {
			return "NAN";
		} else if (number instanceof BigDecimal) {
			BigDecimal d = (BigDecimal)number;
			String s;
			if (expLimitSet) {
				if (d.compareTo(BigDecimal.ZERO) == 0) {
					s = nonExpFormat.format(d);
				} else if ((d.compareTo(bdmin.negate()) > 0) && (d.compareTo(bdmin) < 0)) {
					s = expFormat.format(d);
				} else if ((d.compareTo(bdmax.negate()) <= 0) || (d.compareTo(bdmax) >= 0)) {
					s = expFormat.format(d);
				} else {
					s = nonExpFormat.format(d);
				}
			} else {
				s = expFormat.format(d);
			}
			if (expFormatUsesQuote) {
				s = s.replace("E-", "''").replace("E+", "'").replace("E", "'");
			}
			return s.replace(",", ".");
		} else if (number instanceof BigInteger) {
			BigInteger i = (BigInteger)number;
			String s;
			if (expLimitSet) {
				if (i.compareTo(BigInteger.ZERO) == 0) {
					s = nonExpFormat.format(i);
				} else if ((i.compareTo(bimin.negate()) > 0) && (i.compareTo(bimin) < 0)) {
					s = expFormat.format(i);
				} else if ((i.compareTo(bimax.negate()) <= 0) || (i.compareTo(bimax) >= 0)) {
					s = expFormat.format(i);
				} else {
					s = nonExpFormat.format(i);
				}
			} else {
				s = expFormat.format(i);
			}
			if (expFormatUsesQuote) {
				s = s.replace("E-", "''").replace("E+", "'").replace("E", "'");
			}
			return s.replace(",", ".");
		} else if (number instanceof Double) {
			double d = (Double)number;
			if (Double.isNaN(d)) return "NAN";
			else if (Double.isInfinite(d)) return (d < 0) ? "-INF" : "INF";
			else {
				String s;
				if (expLimitSet) {
					if (d == 0) s = nonExpFormat.format(d);
					else if ((d > -dmin) && (d < dmin)) s = expFormat.format(d);
					else if ((d <= -dmax) || (d >= dmax)) s = expFormat.format(d);
					else s = nonExpFormat.format(d);
				} else {
					s = expFormat.format(d);
				}
				if (expFormatUsesQuote) {
					s = s.replace("E-", "''").replace("E+", "'").replace("E", "'");
				}
				return s.replace(",", ".");
			}
		} else if (number instanceof Float) {
			float f = (Float)number;
			if (Float.isNaN(f)) return "NAN";
			else if (Float.isInfinite(f)) return (f < 0) ? "-INF" : "INF";
			else {
				String s;
				if (expLimitSet) {
					if (f == 0) s = nonExpFormat.format(f);
					else if ((f > -dmin) && (f < dmin)) s = expFormat.format(f);
					else if ((f <= -dmax) || (f >= dmax)) s = expFormat.format(f);
					else s = nonExpFormat.format(f);
				} else {
					s = expFormat.format(f);
				}
				if (expFormatUsesQuote) {
					s = s.replace("E-", "''").replace("E+", "'").replace("E", "'");
				}
				return s.replace(",", ".");
			}
		} else if (number instanceof Long) {
			long l = (Long)number;
			String s;
			if (expLimitSet) {
				if (l == 0) s = nonExpFormat.format(l);
				else if ((l > -dmin) && (l < dmin)) s = expFormat.format(l);
				else if ((l <= -dmax) || (l >= dmax)) s = expFormat.format(l);
				else s = nonExpFormat.format(l);
			} else {
				s = expFormat.format(l);
			}
			if (expFormatUsesQuote) {
				s = s.replace("E-", "''").replace("E+", "'").replace("E", "'");
			}
			return s.replace(",", ".");
		} else if (number instanceof Integer) {
			int i = (Integer)number;
			String s;
			if (expLimitSet) {
				if (i == 0) s = nonExpFormat.format(i);
				else if ((i > -dmin) && (i < dmin)) s = expFormat.format(i);
				else if ((i <= -dmax) || (i >= dmax)) s = expFormat.format(i);
				else s = nonExpFormat.format(i);
			} else {
				s = expFormat.format(i);
			}
			if (expFormatUsesQuote) {
				s = s.replace("E-", "''").replace("E+", "'").replace("E", "'");
			}
			return s.replace(",", ".");
		} else {
			double d = number.doubleValue();
			if (Double.isNaN(d)) return "NAN";
			else if (Double.isInfinite(d)) return (d < 0) ? "-INF" : "INF";
			else {
				String s;
				if (expLimitSet) {
					if (d == 0) s = nonExpFormat.format(d);
					else if ((d > -dmin) && (d < dmin)) s = expFormat.format(d);
					else if ((d <= -dmax) || (d >= dmax)) s = expFormat.format(d);
					else s = nonExpFormat.format(d);
				} else {
					s = expFormat.format(d);
				}
				if (expFormatUsesQuote) {
					s = s.replace("E-", "''").replace("E+", "'").replace("E", "'");
				}
				return s.replace(",", ".");
			}
		}
	}
	
	public BigDecimal parseBigDecimal(String s) {
		s = s.trim().replace("''", "E-").replace("'", "E+");
		return new BigDecimal(s);
	}
	
	public BigInteger parseBigInteger(String s) {
		s = s.trim().replace("''", "E-").replace("'", "E+");
		return new BigDecimal(s).toBigIntegerExact();
	}
	
	public double parseDouble(String s) {
		s = s.trim();
		if (s.equalsIgnoreCase("nan")) {
			return Double.NaN;
		}
		else if (s.equalsIgnoreCase("inf")) {
			return Double.POSITIVE_INFINITY;
		}
		else if (s.equalsIgnoreCase("-inf")) {
			return Double.NEGATIVE_INFINITY;
		}
		else {
			s = s.replace("''", "E-").replace("'", "E+");
			return new BigDecimal(s).doubleValue();
		}
	}
	
	public double parseFloat(String s) {
		s = s.trim();
		if (s.equalsIgnoreCase("nan")) {
			return Double.NaN;
		}
		else if (s.equalsIgnoreCase("inf")) {
			return Double.POSITIVE_INFINITY;
		}
		else if (s.equalsIgnoreCase("-inf")) {
			return Double.NEGATIVE_INFINITY;
		}
		else {
			s = s.replace("''", "E-").replace("'", "E+");
			return new BigDecimal(s).floatValue();
		}
	}
}
