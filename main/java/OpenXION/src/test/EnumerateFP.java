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
 * @since OpenXION 1.2
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package test;

import java.math.BigInteger;
import java.math.MathContext;
import com.kreative.openxion.binpack.FPUtilities;

public class EnumerateFP {
	public static void main(String[] args) {
		int s, e, m, b;
		switch (args.length) {
		case 0:
			s = FPUtilities.optimalSignWidth(8);
			e = FPUtilities.optimalExponentWidth(8);
			m = FPUtilities.optimalMantissaWidth(8);
			b = FPUtilities.optimalBias(e);
			enumerateFP(s, e, m, b);
			break;
		case 1:
			if (args[0].equalsIgnoreCase("all")) {
				for (int i = 1; i <= 512; i++) {
					for (e = 1, m = i; e <= i && m >= 1; e++, m--) {
						System.err.println("=== 1, " + e + ", " + m + " ===");
						System.out.println("=== 1, " + e + ", " + m + " ===");
						enumerateFP(1, e, m, FPUtilities.optimalBias(e));
						System.out.println();
					}
				}
			} else {
				int n = Integer.parseInt(args[0]);
				s = FPUtilities.optimalSignWidth(n);
				e = FPUtilities.optimalExponentWidth(n);
				m = FPUtilities.optimalMantissaWidth(n);
				b = FPUtilities.optimalBias(e);
				enumerateFP(s, e, m, b);
			}
			break;
		case 2:
			s = 1;
			e = Integer.parseInt(args[0]);
			m = Integer.parseInt(args[1]);
			b = FPUtilities.optimalBias(e);
			enumerateFP(s, e, m, b);
			break;
		case 3:
			s = Integer.parseInt(args[0]);
			e = Integer.parseInt(args[1]);
			m = Integer.parseInt(args[2]);
			b = FPUtilities.optimalBias(e);
			enumerateFP(s, e, m, b);
			break;
		default:
			s = Integer.parseInt(args[0]);
			e = Integer.parseInt(args[1]);
			m = Integer.parseInt(args[2]);
			b = Integer.parseInt(args[3]);
			enumerateFP(s, e, m, b);
			break;
		}
		
	}
	
	private static void enumerateFP(int s, int e, int m, int b) {
		int n = s + e + m;
		for (BigInteger i = BigInteger.ZERO; i.compareTo(BigInteger.ONE.shiftLeft(n)) < 0; i = i.add(BigInteger.ONE)) {
			BigInteger[] r = FPUtilities.splitFloat(i, s, e, m);
			Number f = FPUtilities.decodeFloat(r[0], r[1], r[2], s, e, m, b, MathContext.DECIMAL128);
			String h = i.toString(16).toUpperCase();
			while (h.length() < (n+3)/4) h = "0" + h;
			System.out.println(h + " = " + f.toString());
		}
	}
}
