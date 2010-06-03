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

package com.kreative.openxion.math;

import java.math.*;
import java.text.*;

/**
 * Converts strings from one numeric base to another.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class BaseConvert {
	private BaseConvert() {}
	
	public static String bc(String s, int sb, int db, MathContext mc) {
		String[] ss = s.split(",");
		StringBuffer rs = new StringBuffer();
		for (String sss : ss) {
			rs.append(bci(sss,sb,db,mc));
			rs.append(",");
		}
		if (rs.length() > 0 && rs.charAt(rs.length()-1) == ',') {
			rs.deleteCharAt(rs.length()-1);
		}
		return rs.toString();
	}
	
	private static int charToValue(char ch) {
		if (ch >= '0' && ch <= '9') return (ch-'0');
		else if (ch >= 'A' && ch <= 'Z') return (ch-'A'+10);
		else if (ch >= 'a' && ch <= 'z') return (ch-'a'+10);
		else return 0;
	}
	
	private static char valueToChar(int v) {
		if (v < 10) return (char)('0'+v);
		else return (char)('A'+v-10);
	}
	
	private static String bci(String s, int sb, int db, MathContext mc) {
		s = s.replaceAll(" ", "");
		boolean negative = s.startsWith("-");
		if (s.startsWith("+") || s.startsWith("-")) {
			s = s.substring(1);
		}
		String[] ss = s.split("\\.",2);
		String ip = (ss.length > 0) ? ss[0].replaceAll("[^0-9A-Za-z]", "") : "";
		String fp = (ss.length > 1) ? ss[1].replaceAll("[^0-9A-Za-z]", "") : "";
		BigInteger sbi = BigInteger.valueOf(sb);
		BigDecimal sbd = BigDecimal.valueOf(sb);
		BigInteger dbi = BigInteger.valueOf(db);
		BigDecimal dbd = BigDecimal.valueOf(db);
		BigInteger ipi = BigInteger.ZERO;
		BigDecimal fpd = BigDecimal.ZERO;
		CharacterIterator iit = new StringCharacterIterator(ip);
		for (char ch = iit.first(); ch != CharacterIterator.DONE; ch = iit.next()) {
			ipi = ipi.multiply(sbi).add(BigInteger.valueOf(charToValue(ch)));
		}
		CharacterIterator fit = new StringCharacterIterator(fp);
		for (char ch = fit.last(); ch != CharacterIterator.DONE; ch = fit.previous()) {
			fpd = fpd.add(BigDecimal.valueOf(charToValue(ch))).divide(sbd,mc);
		}
		StringBuffer ipr = new StringBuffer();
		StringBuffer fpr = new StringBuffer();
		while (ipi.compareTo(BigInteger.ZERO) != 0) {
			ipr.insert(0, valueToChar(ipi.mod(dbi).intValue()));
			ipi = ipi.divide(dbi);
		}
		while (fpd.compareTo(BigDecimal.ZERO) != 0 && fpr.length() < mc.getPrecision()) {
			fpd = fpd.multiply(dbd);
			BigDecimal intpart = fpd.setScale(0, RoundingMode.DOWN);
			fpr.append(valueToChar(intpart.intValue()));
			fpd = fpd.subtract(intpart);
		}
		return (negative ? "-" : "") + ((ipr.length() > 0) ? ipr.toString() : "0") + ((fpr.length() > 0) ? ("." + fpr.toString()) : "");
	}
	
	public static String bc(BigInteger i, int db, MathContext mc) {
		boolean negative = i.signum() < 0; i = i.abs();
		BigInteger dbi = BigInteger.valueOf(db);
		BigInteger ipi = i;
		StringBuffer ipr = new StringBuffer();
		while (ipi.compareTo(BigInteger.ZERO) != 0) {
			ipr.insert(0, valueToChar(ipi.mod(dbi).intValue()));
			ipi = ipi.divide(dbi);
		}
		return (negative ? "-" : "") + ((ipr.length() > 0) ? ipr.toString() : "0");
	}
	
	public static String bc(BigDecimal d, int db, MathContext mc) {
		boolean negative = d.signum() < 0; d = d.abs();
		BigInteger dbi = BigInteger.valueOf(db);
		BigDecimal dbd = BigDecimal.valueOf(db);
		BigInteger ipi = d.setScale(0, RoundingMode.DOWN).toBigInteger();
		BigDecimal fpd = d.subtract(d.setScale(0, RoundingMode.DOWN));
		StringBuffer ipr = new StringBuffer();
		StringBuffer fpr = new StringBuffer();
		while (ipi.compareTo(BigInteger.ZERO) != 0) {
			ipr.insert(0, valueToChar(ipi.mod(dbi).intValue()));
			ipi = ipi.divide(dbi);
		}
		while (fpd.compareTo(BigDecimal.ZERO) != 0 && fpr.length() < mc.getPrecision()) {
			fpd = fpd.multiply(dbd);
			BigDecimal intpart = fpd.setScale(0, RoundingMode.DOWN);
			fpr.append(valueToChar(intpart.intValue()));
			fpd = fpd.subtract(intpart);
		}
		return (negative ? "-" : "") + ((ipr.length() > 0) ? ipr.toString() : "0") + ((fpr.length() > 0) ? ("." + fpr.toString()) : "");
	}
}
