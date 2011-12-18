/*
 * Copyright &copy; 2011 Rebecca G. Bettencourt / Kreative Software
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
 * @since OpenXION 1.4
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.format;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.List;

import com.kreative.openxion.XNContext;
import com.kreative.openxion.math.BaseConvert;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.inst.XOMBinary;
import com.kreative.openxion.xom.inst.XOMComplex;
import com.kreative.openxion.xom.inst.XOMDate;
import com.kreative.openxion.xom.inst.XOMInteger;
import com.kreative.openxion.xom.inst.XOMList;
import com.kreative.openxion.xom.inst.XOMNumber;
import com.kreative.openxion.xom.type.XOMBinaryType;
import com.kreative.openxion.xom.type.XOMComplexType;
import com.kreative.openxion.xom.type.XOMDateType;
import com.kreative.openxion.xom.type.XOMIntegerType;
import com.kreative.openxion.xom.type.XOMNumberType;

public abstract class Formatter {
	public abstract String format(XNContext ctx, XOMVariant v);
	
	public static final Formatter forTypeSpecifier(char ch) {
		switch (ch) {
		case 's': return StringFormatter;
		case 'S': return StringFormatter;
		case 'l': return StringFormatter;
		case 'L': return ListFormatter;
		case 'i': return IntegerFormatter;
		case 'I': return LocalIntegerFormatter;
		case 'n': return NumberFormatter;
		case 'N': return LocalNumberFormatter;
		case 'o': return OctalFormatter;
		case 'O': return OctalFormatter;
		case 'h': return LCHexFormatter;
		case 'H': return UCHexFormatter;
		case 'c': return ComplexFormatter;
		case 'C': return LocalComplexFormatter;
		case 'd': return DateFormatter;
		case 'D': return LocalDateFormatter;
		case 't': return TimeFormatter;
		case 'T': return LocalTimeFormatter;
		case 'b': return BinaryFormatter;
		case 'B': return LocalBinaryFormatter;
		default: return StringFormatter;
		}
	}
	
	public static final Formatter StringFormatter = new Formatter() {
		@Override
		public String format(XNContext ctx, XOMVariant v) {
			return v.toTextString(ctx);
		}
	};
	
	public static final Formatter ListFormatter = new Formatter() {
		@Override
		public String format(XNContext ctx, XOMVariant v) {
			return formatList(ctx, v.toPrimitiveList(ctx));
		}
		private String formatList(XNContext ctx, List<? extends XOMVariant> list) {
			StringBuffer s = new StringBuffer();
			for (XOMVariant v : list) {
				if (s.length() > 0) s.append(", ");
				if (v instanceof XOMList) {
					s.append("(");
					s.append(formatList(ctx, v.toPrimitiveList(ctx)));
					s.append(")");
				} else {
					s.append(v.toTextString(ctx));
				}
			}
			return s.toString();
		}
	};
	
	public static final Formatter IntegerFormatter = new Formatter() {
		@Override
		public String format(XNContext ctx, XOMVariant v) {
			XOMInteger i = XOMIntegerType.instance.makeInstanceFrom(ctx, v, true);
			return i.toTextString(ctx);
		}
	};
	
	public static final Formatter LocalIntegerFormatter = new Formatter() {
		@Override
		public String format(XNContext ctx, XOMVariant v) {
			XOMInteger i = XOMIntegerType.instance.makeInstanceFrom(ctx, v, true);
			if (i.isUndefined()) {
				return i.toTextString(ctx);
			} else {
				return NumberFormat.getIntegerInstance().format(i.toLong());
			}
		}
	};
	
	public static final Formatter NumberFormatter = new Formatter() {
		@Override
		public String format(XNContext ctx, XOMVariant v) {
			XOMNumber n = XOMNumberType.instance.makeInstanceFrom(ctx, v, true);
			return n.toTextString(ctx);
		}
	};
	
	public static final Formatter LocalNumberFormatter = new Formatter() {
		@Override
		public String format(XNContext ctx, XOMVariant v) {
			XOMNumber n = XOMNumberType.instance.makeInstanceFrom(ctx, v, true);
			if (n.isUndefined()) {
				return n.toTextString(ctx);
			} else {
				return NumberFormat.getNumberInstance().format(n.toDouble());
			}
		}
	};
	
	public static final Formatter OctalFormatter = new Formatter() {
		@Override
		public String format(XNContext ctx, XOMVariant v) {
			XOMNumber n = XOMNumberType.instance.makeInstanceFrom(ctx, v, true);
			return BaseConvert.bc(n.toBigDecimal(), 8, ctx.getMathContext());
		}
	};
	
	public static final Formatter UCHexFormatter = new Formatter() {
		@Override
		public String format(XNContext ctx, XOMVariant v) {
			XOMNumber n = XOMNumberType.instance.makeInstanceFrom(ctx, v, true);
			return BaseConvert.bc(n.toBigDecimal(), 16, ctx.getMathContext()).toUpperCase();
		}
	};
	
	public static final Formatter LCHexFormatter = new Formatter() {
		@Override
		public String format(XNContext ctx, XOMVariant v) {
			XOMNumber n = XOMNumberType.instance.makeInstanceFrom(ctx, v, true);
			return BaseConvert.bc(n.toBigDecimal(), 16, ctx.getMathContext()).toLowerCase();
		}
	};
	
	public static final Formatter ComplexFormatter = new Formatter() {
		@Override
		public String format(XNContext ctx, XOMVariant v) {
			XOMComplex c = XOMComplexType.instance.makeInstanceFrom(ctx, v, true);
			return c.toTextString(ctx);
		}
	};
	
	public static final Formatter LocalComplexFormatter = new Formatter() {
		@Override
		public String format(XNContext ctx, XOMVariant v) {
			XOMComplex c = XOMComplexType.instance.makeInstanceFrom(ctx, v, true);
			if (c.isUndefined()) {
				return c.toTextString(ctx);
			} else if (c.isReal()) {
				return NumberFormat.getNumberInstance().format(c.toDoubles()[0]);
			} else {
				double[] cc = c.toDoubles();
				if (cc[1] < 0) {
					return NumberFormat.getNumberInstance().format(cc[0]) + NumberFormat.getNumberInstance().format(cc[1]) + "i";
				} else {
					return NumberFormat.getNumberInstance().format(cc[0]) + "+" + NumberFormat.getNumberInstance().format(cc[1]) + "i";
				}
			}
		}
	};
	
	public static final Formatter DateFormatter = new Formatter() {
		@Override
		public String format(XNContext ctx, XOMVariant v) {
			XOMDate d = XOMDateType.instance.makeInstanceFrom(ctx, v);
			return d.dateEquivalent().toTextString(ctx);
		}
	};
	
	public static final Formatter LocalDateFormatter = new Formatter() {
		@Override
		public String format(XNContext ctx, XOMVariant v) {
			XOMDate d = XOMDateType.instance.makeInstanceFrom(ctx, v);
			return DateFormat.getDateInstance().format(d.toDate());
		}
	};
	
	public static final Formatter TimeFormatter = new Formatter() {
		@Override
		public String format(XNContext ctx, XOMVariant v) {
			XOMDate d = XOMDateType.instance.makeInstanceFrom(ctx, v);
			return d.timeEquivalent().toTextString(ctx);
		}
	};
	
	public static final Formatter LocalTimeFormatter = new Formatter() {
		@Override
		public String format(XNContext ctx, XOMVariant v) {
			XOMDate d = XOMDateType.instance.makeInstanceFrom(ctx, v);
			return DateFormat.getTimeInstance().format(d.toDate());
		}
	};
	
	public static final Formatter BinaryFormatter = new Formatter() {
		@Override
		public String format(XNContext ctx, XOMVariant v) {
			XOMBinary b = XOMBinaryType.instance.makeInstanceFrom(ctx, v);
			return b.toTextString(ctx);
		}
	};
	
	public static final Formatter LocalBinaryFormatter = new Formatter() {
		private final String[] LOOKUP_HEX = new String[] {
			"00","01","02","03","04","05","06","07","08","09","0A","0B","0C","0D","0E","0F",
			"10","11","12","13","14","15","16","17","18","19","1A","1B","1C","1D","1E","1F",
			"20","21","22","23","24","25","26","27","28","29","2A","2B","2C","2D","2E","2F",
			"30","31","32","33","34","35","36","37","38","39","3A","3B","3C","3D","3E","3F",
			"40","41","42","43","44","45","46","47","48","49","4A","4B","4C","4D","4E","4F",
			"50","51","52","53","54","55","56","57","58","59","5A","5B","5C","5D","5E","5F",
			"60","61","62","63","64","65","66","67","68","69","6A","6B","6C","6D","6E","6F",
			"70","71","72","73","74","75","76","77","78","79","7A","7B","7C","7D","7E","7F",
			"80","81","82","83","84","85","86","87","88","89","8A","8B","8C","8D","8E","8F",
			"90","91","92","93","94","95","96","97","98","99","9A","9B","9C","9D","9E","9F",
			"A0","A1","A2","A3","A4","A5","A6","A7","A8","A9","AA","AB","AC","AD","AE","AF",
			"B0","B1","B2","B3","B4","B5","B6","B7","B8","B9","BA","BB","BC","BD","BE","BF",
			"C0","C1","C2","C3","C4","C5","C6","C7","C8","C9","CA","CB","CC","CD","CE","CF",
			"D0","D1","D2","D3","D4","D5","D6","D7","D8","D9","DA","DB","DC","DD","DE","DF",
			"E0","E1","E2","E3","E4","E5","E6","E7","E8","E9","EA","EB","EC","ED","EE","EF",
			"F0","F1","F2","F3","F4","F5","F6","F7","F8","F9","FA","FB","FC","FD","FE","FF"
		};
		@Override
		public String format(XNContext ctx, XOMVariant v) {
			XOMBinary b = XOMBinaryType.instance.makeInstanceFrom(ctx, v);
			StringBuffer s = new StringBuffer();
			for (byte bb : b.toByteArray()) {
				if (s.length() > 0) s.append(' ');
				s.append(LOOKUP_HEX[bb & 0xFF]);
			}
			return s.toString();
		}
	};
}
