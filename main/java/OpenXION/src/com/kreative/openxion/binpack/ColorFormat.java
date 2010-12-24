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

import java.awt.Color;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Arrays;

public class ColorFormat {
	public static enum ChannelOrder {
		// TrueColor with Alpha
		ARGB, ARBG, AGRB, AGBR, ABRG, ABGR,
		RAGB, RABG, RGAB, RGBA, RBAG, RBGA,
		GARB, GABR, GRAB, GRBA, GBAR, GBRA,
		BARG, BAGR, BRAG, BRGA, BGAR, BGRA,
		// TrueColor without Alpha
		RGB, RBG, GRB, GBR, BRG, BGR,
		// Grayscale with or without Alpha
		AY, YA, Y, A,
		// HSV with Alpha
		AHSV, AHVS, ASHV, ASVH, AVHS, AVSH,
		HASV, HAVS, HSAV, HSVA, HVAS, HVSA,
		SAHV, SAVH, SHAV, SHVA, SVAH, SVHA,
		VAHS, VASH, VHAS, VHSA, VSAH, VSHA,
		// HSV without Alpha
		HSV, HVS, SHV, SVH, VHS, VSH,
		// HSL with Alpha
		AHSL, AHLS, ASHL, ASLH, ALHS, ALSH,
		HASL, HALS, HSAL, HSLA, HLAS, HLSA,
		SAHL, SALH, SHAL, SHLA, SLAH, SLHA,
		LAHS, LASH, LHAS, LHSA, LSAH, LSHA,
		// HSL without Alpha
		HSL, HLS, SHL, SLH, LHS, LSH,
		// CMYK
		CMYK, CMKY, CYMK, CYKM, CKMY, CKYM,
		MCYK, MCKY, MYCK, MYKC, MKCY, MKYC,
		YCMK, YCKM, YMCK, YMKC, YKCM, YKMC,
		KCMY, KCYM, KMCY, KMYC, KYCM, KYMC,
		// CMY
		CMY, CYM, MCY, MYC, YCM, YMC,
		// YIQ
		YIQ, YQI, IYQ, IQY, QYI, QIY,
		// YUV
		YUV, YVU, UYV, UVY, VYU, VUY,
		// XYZ
		XYZ, XZY, YXZ, YZX, ZXY, ZYX;
	}
	
	private ChannelOrder channelOrder;
	private int[] channelBits;
	
	public ColorFormat(ChannelOrder channelOrder, int... channelBits) {
		if (channelOrder == null) {
			throw new IllegalArgumentException("Invalid channel order");
		} else if (channelBits.length != channelOrder.name().length()) {
			throw new IllegalArgumentException("Number of channels do not match");
		} else {
			this.channelOrder = channelOrder;
			this.channelBits = channelBits;
		}
	}
	
	public ColorFormat(String format) {
		String cos = format.replaceAll("[^A-Za-z]", "").toUpperCase();
		this.channelOrder = ChannelOrder.valueOf(cos);
		if (this.channelOrder == null) {
			throw new IllegalArgumentException("Invalid channel order");
		}
		this.channelBits = new int[this.channelOrder.name().length()];
		String[] cbs = format.replaceAll("[^0-9]", " ").trim().split("\\s+");
		if (cbs.length == 1 && cbs[0].length() == 0) {
			throw new IllegalArgumentException("Number of channels do not match");
		} else if (cbs.length != this.channelBits.length) {
			throw new IllegalArgumentException("Number of channels do not match");
		} else {
			for (int i = 0; i < cbs.length; i++) {
				this.channelBits[i] = Integer.parseInt(cbs[i]);
			}
		}
	}
	
	public ColorFormat(int width, String format) {
		String cos = format.replaceAll("[^A-Za-z]", "").toUpperCase();
		this.channelOrder = ChannelOrder.valueOf(cos);
		if (this.channelOrder == null) {
			throw new IllegalArgumentException("Invalid channel order");
		}
		this.channelBits = new int[this.channelOrder.name().length()];
		String[] cbs = format.replaceAll("[^0-9]", " ").trim().split("\\s+");
		if (cbs.length == 1 && cbs[0].length() == 0) cbs = new String[0];
		if (cbs.length > this.channelBits.length) {
			throw new IllegalArgumentException("Number of channels do not match");
		} else {
			for (int i = 0; i < cbs.length; i++) {
				this.channelBits[i] = Integer.parseInt(cbs[i]);
				width -= this.channelBits[i];
			}
			if (cbs.length < this.channelBits.length) {
				int ncr = this.channelBits.length - cbs.length;
				int npc = width / ncr;
				for (int i = cbs.length; i < this.channelBits.length; i++) {
					this.channelBits[i] = npc;
					width -= npc;
				}
				if (width > 0) {
					int o;
					if ((o = cos.indexOf('G')) >= 0) this.channelBits[o] += width; // GREEN
					else if ((o = cos.indexOf('H')) >= 0) this.channelBits[o] += width; // HUE
					else if ((o = cos.indexOf('K')) >= 0) this.channelBits[o] += width; // BLACK
					else if ((o = cos.indexOf('Y')) >= 0) this.channelBits[o] += width; // GRAY, LUMA
					else this.channelBits[this.channelBits.length/2] += width; // ANYTHING
				}
			}
		}
	}
	
	public ChannelOrder channelOrder() {
		return channelOrder;
	}
	
	public int channelCount() {
		return channelBits.length;
	}
	
	public int channelWidth(int ch) {
		return channelBits[ch];
	}
	
	public boolean equals(Object o) {
		if (o instanceof ColorFormat) {
			ColorFormat other = (ColorFormat)o;
			if (this.channelOrder != other.channelOrder) return false;
			if (this.channelBits.length != other.channelBits.length) return false;
			for (int i = 0; i < this.channelBits.length; i++) {
				if (this.channelBits[i] != other.channelBits[i]) return false;
			}
			return true;
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return channelOrder.hashCode() ^ Arrays.hashCode(channelBits);
	}
	
	public String toString() {
		String cos = channelOrder.name().toLowerCase();
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < channelBits.length; i++) {
			s.append(cos.charAt(i));
			s.append(channelBits[i]);
		}
		return s.toString();
	}
	
	public float[] toFloatArray(Number[] values) {
		if (values.length != channelBits.length) {
			throw new IllegalArgumentException("Number of channels do not match");
		} else {
			float[] r = new float[values.length];
			for (int i = 0; i < values.length; i++) {
				r[i] = values[i].floatValue() / BigInteger.ONE.shiftLeft(channelBits[i]).subtract(BigInteger.ONE).floatValue();
			}
			return r;
		}
	}
	
	public float[] toFloatArray(long[] values) {
		if (values.length != channelBits.length) {
			throw new IllegalArgumentException("Number of channels do not match");
		} else {
			float[] r = new float[values.length];
			for (int i = 0; i < values.length; i++) {
				r[i] = (float)values[i] / BigInteger.ONE.shiftLeft(channelBits[i]).subtract(BigInteger.ONE).floatValue();
			}
			return r;
		}
	}
	
	public float[] toFloatArray(int[] values) {
		if (values.length != channelBits.length) {
			throw new IllegalArgumentException("Number of channels do not match");
		} else {
			float[] r = new float[values.length];
			for (int i = 0; i < values.length; i++) {
				r[i] = (float)values[i] / BigInteger.ONE.shiftLeft(channelBits[i]).subtract(BigInteger.ONE).floatValue();
			}
			return r;
		}
	}
	
	public BigInteger[] toBigIntArray(float[] values) {
		if (values.length != channelBits.length) {
			throw new IllegalArgumentException("Number of channels do not match");
		} else {
			MathContext mc = MathContext.DECIMAL128;
			BigInteger[] r = new BigInteger[values.length];
			for (int i = 0; i < values.length; i++) {
				BigInteger m = BigInteger.ONE.shiftLeft(channelBits[i]).subtract(BigInteger.ONE);
				r[i] = BigDecimal.valueOf(values[i]).multiply(new BigDecimal(m, mc), mc).toBigInteger();
			}
			return r;
		}
	}
	
	public long[] toLongArray(float[] values) {
		if (values.length != channelBits.length) {
			throw new IllegalArgumentException("Number of channels do not match");
		} else {
			long[] r = new long[values.length];
			for (int i = 0; i < values.length; i++) {
				r[i] = (long)((double)values[i] * BigInteger.ONE.shiftLeft(channelBits[i]).subtract(BigInteger.ONE).doubleValue());
			}
			return r;
		}
	}
	
	public int[] toIntArray(float[] values) {
		if (values.length != channelBits.length) {
			throw new IllegalArgumentException("Number of channels do not match");
		} else {
			int[] r = new int[values.length];
			for (int i = 0; i < values.length; i++) {
				r[i] = (int)((double)values[i] * BigInteger.ONE.shiftLeft(channelBits[i]).subtract(BigInteger.ONE).doubleValue());
			}
			return r;
		}
	}
	
	public float[] toRGBAFloatArray(float[] values) {
		String cos = channelOrder.name().toUpperCase();
		switch (channelOrder) {
		case ARGB: case ARBG: case AGRB: case AGBR: case ABRG: case ABGR:
		case RAGB: case RABG: case RGAB: case RGBA: case RBAG: case RBGA:
		case GARB: case GABR: case GRAB: case GRBA: case GBAR: case GBRA:
		case BARG: case BAGR: case BRAG: case BRGA: case BGAR: case BGRA:
			return new float[] {
					values[cos.indexOf('R')],
					values[cos.indexOf('G')],
					values[cos.indexOf('B')],
					values[cos.indexOf('A')],
			};
		case RGB: case RBG: case GRB: case GBR: case BRG: case BGR:
			return new float[] {
					values[cos.indexOf('R')],
					values[cos.indexOf('G')],
					values[cos.indexOf('B')],
					1.0f,
			};
		case AY: case YA:
			return new float[] {
					values[cos.indexOf('Y')],
					values[cos.indexOf('Y')],
					values[cos.indexOf('Y')],
					values[cos.indexOf('A')],
			};
		case Y:
			return new float[] {
					values[cos.indexOf('Y')],
					values[cos.indexOf('Y')],
					values[cos.indexOf('Y')],
					1.0f,
			};
		case A:
			return new float[] {
					0.0f,
					0.0f,
					0.0f,
					values[cos.indexOf('A')],
			};
		case AHSV: case AHVS: case ASHV: case ASVH: case AVHS: case AVSH:
		case HASV: case HAVS: case HSAV: case HSVA: case HVAS: case HVSA:
		case SAHV: case SAVH: case SHAV: case SHVA: case SVAH: case SVHA:
		case VAHS: case VASH: case VHAS: case VHSA: case VSAH: case VSHA:
			int rgb1 = Color.HSBtoRGB(
					values[cos.indexOf('H')],
					values[cos.indexOf('S')],
					values[cos.indexOf('V')]
			);
			return new float[] {
					((rgb1 >> 16) & 0xFF) / 255.0f,
					((rgb1 >> 8) & 0xFF) / 255.0f,
					(rgb1 & 0xFF) / 255.0f,
					values[cos.indexOf('A')],
			};
		case HSV: case HVS: case SHV: case SVH: case VHS: case VSH:
			int rgb2 = Color.HSBtoRGB(
					values[cos.indexOf('H')],
					values[cos.indexOf('S')],
					values[cos.indexOf('V')]
			);
			return new float[] {
					((rgb2 >> 16) & 0xFF) / 255.0f,
					((rgb2 >> 8) & 0xFF) / 255.0f,
					(rgb2 & 0xFF) / 255.0f,
					1.0f,
			};
		case AHSL: case AHLS: case ASHL: case ASLH: case ALHS: case ALSH:
		case HASL: case HALS: case HSAL: case HSLA: case HLAS: case HLSA:
		case SAHL: case SALH: case SHAL: case SHLA: case SLAH: case SLHA:
		case LAHS: case LASH: case LHAS: case LHSA: case LSAH: case LSHA:
			float hh1 = values[cos.indexOf('H')];
			float ss1 = values[cos.indexOf('S')];
			float ll1 = values[cos.indexOf('L')];
			float h1 = hh1;
			ll1 *= 2;
			ss1 *= (ll1 <= 1) ? ll1 : 2 - ll1;
			float v1 = (ll1 + ss1) / 2;
			float s1 = ((ll1 + ss1) == 0) ? 0 : ((2 * ss1) / (ll1 + ss1));
			int rgb3 = Color.HSBtoRGB(h1, s1, v1);
			return new float[] {
					((rgb3 >> 16) & 0xFF) / 255.0f,
					((rgb3 >> 8) & 0xFF) / 255.0f,
					(rgb3 & 0xFF) / 255.0f,
					values[cos.indexOf('A')],
			};
		case HSL: case HLS: case SHL: case SLH: case LHS: case LSH:
			float hh2 = values[cos.indexOf('H')];
			float ss2 = values[cos.indexOf('S')];
			float ll2 = values[cos.indexOf('L')];
			float h2 = hh2;
			ll2 *= 2;
			ss2 *= (ll2 <= 1) ? ll2 : 2 - ll2;
			float v2 = (ll2 + ss2) / 2;
			float s2 = ((ll2 + ss2) == 0) ? 0 : ((2 * ss2) / (ll2 + ss2));
			int rgb4 = Color.HSBtoRGB(h2, s2, v2);
			return new float[] {
					((rgb4 >> 16) & 0xFF) / 255.0f,
					((rgb4 >> 8) & 0xFF) / 255.0f,
					(rgb4 & 0xFF) / 255.0f,
					1.0f,
			};
		case CMYK: case CMKY: case CYMK: case CYKM: case CKMY: case CKYM:
		case MCYK: case MCKY: case MYCK: case MYKC: case MKCY: case MKYC:
		case YCMK: case YCKM: case YMCK: case YMKC: case YKCM: case YKMC:
		case KCMY: case KCYM: case KMCY: case KMYC: case KYCM: case KYMC:
			float k1 = values[cos.indexOf('K')];
			float c1 = k1 + (values[cos.indexOf('C')] * (1.0f-k1));
			float m1 = k1 + (values[cos.indexOf('M')] * (1.0f-k1));
			float y1 = k1 + (values[cos.indexOf('Y')] * (1.0f-k1));
			return new float[] { 1.0f - c1, 1.0f - m1, 1.0f - y1, 1.0f };
		case CMY: case CYM: case MCY: case MYC: case YCM: case YMC:
			return new float[] {
					1.0f - values[cos.indexOf('C')],
					1.0f - values[cos.indexOf('M')],
					1.0f - values[cos.indexOf('Y')],
					1.0f,
			};
		case YIQ: case YQI: case IYQ: case IQY: case QYI: case QIY:
			double[] rgb5 = YIQtoRGB(
					values[cos.indexOf('Y')],
					values[cos.indexOf('I')],
					values[cos.indexOf('Q')]
			);
			return new float[]{ (float)rgb5[0], (float)rgb5[1], (float)rgb5[2] };
		case YUV: case YVU: case UYV: case UVY: case VYU: case VUY:
			double[] rgb6 = YUVtoRGB(
					values[cos.indexOf('Y')],
					values[cos.indexOf('U')],
					values[cos.indexOf('V')]
			);
			return new float[]{ (float)rgb6[0], (float)rgb6[1], (float)rgb6[2] };
		case XYZ: case XZY: case YXZ: case YZX: case ZXY: case ZYX:
			double[] rgb8 = XYZtosRGB(
					values[cos.indexOf('X')],
					values[cos.indexOf('Y')],
					values[cos.indexOf('Z')]
			);
			return new float[]{ (float)rgb8[0], (float)rgb8[1], (float)rgb8[2] };
		default:
			throw new IllegalArgumentException("Invalid channel order");
		}
	}
	
	public float[] fromRGBAFloatArray(float[] rgb) {
		float[] values;
		String cos = channelOrder.name().toUpperCase();
		switch (channelOrder) {
		case ARGB: case ARBG: case AGRB: case AGBR: case ABRG: case ABGR:
		case RAGB: case RABG: case RGAB: case RGBA: case RBAG: case RBGA:
		case GARB: case GABR: case GRAB: case GRBA: case GBAR: case GBRA:
		case BARG: case BAGR: case BRAG: case BRGA: case BGAR: case BGRA:
			values = new float[4];
			values[cos.indexOf('R')] = rgb[0];
			values[cos.indexOf('G')] = rgb[1];
			values[cos.indexOf('B')] = rgb[2];
			values[cos.indexOf('A')] = rgb[3];
			return values;
		case RGB: case RBG: case GRB: case GBR: case BRG: case BGR:
			values = new float[3];
			values[cos.indexOf('R')] = rgb[0];
			values[cos.indexOf('G')] = rgb[1];
			values[cos.indexOf('B')] = rgb[2];
			return values;
		case AY: case YA:
			values = new float[2];
			values[cos.indexOf('Y')] = 0.3f*rgb[0] + 0.59f*rgb[1] + 0.11f*rgb[2];
			values[cos.indexOf('A')] = rgb[3];
			return values;
		case Y:
			values = new float[1];
			values[cos.indexOf('Y')] = 0.3f*rgb[0] + 0.59f*rgb[1] + 0.11f*rgb[2];
			return values;
		case A:
			values = new float[1];
			values[cos.indexOf('A')] = rgb[3];
			return values;
		case AHSV: case AHVS: case ASHV: case ASVH: case AVHS: case AVSH:
		case HASV: case HAVS: case HSAV: case HSVA: case HVAS: case HVSA:
		case SAHV: case SAVH: case SHAV: case SHVA: case SVAH: case SVHA:
		case VAHS: case VASH: case VHAS: case VHSA: case VSAH: case VSHA:
			values = new float[4];
			float[] hsv1 = Color.RGBtoHSB(
					(int)(rgb[0]*255.0f),
					(int)(rgb[1]*255.0f),
					(int)(rgb[2]*255.0f),
					null
			);
			values[cos.indexOf('H')] = hsv1[0];
			values[cos.indexOf('S')] = hsv1[1];
			values[cos.indexOf('V')] = hsv1[2];
			values[cos.indexOf('A')] = hsv1[3];
			return values;
		case HSV: case HVS: case SHV: case SVH: case VHS: case VSH:
			values = new float[3];
			float[] hsv2 = Color.RGBtoHSB(
					(int)(rgb[0]*255.0f),
					(int)(rgb[1]*255.0f),
					(int)(rgb[2]*255.0f),
					null
			);
			values[cos.indexOf('H')] = hsv2[0];
			values[cos.indexOf('S')] = hsv2[1];
			values[cos.indexOf('V')] = hsv2[2];
			return values;
		case AHSL: case AHLS: case ASHL: case ASLH: case ALHS: case ALSH:
		case HASL: case HALS: case HSAL: case HSLA: case HLAS: case HLSA:
		case SAHL: case SALH: case SHAL: case SHLA: case SLAH: case SLHA:
		case LAHS: case LASH: case LHAS: case LHSA: case LSAH: case LSHA:
			values = new float[4];
			float[] hsv3 = Color.RGBtoHSB(
					(int)(rgb[0]*255.0f),
					(int)(rgb[1]*255.0f),
					(int)(rgb[2]*255.0f),
					null
			);
			float hh1 = hsv3[0];
			float ll1 = (2 - hsv3[1]) * hsv3[2];
			float ss1 = hsv3[1] * hsv3[2];
			if (ll1 != 0) ss1 /= (ll1 <= 1) ? ll1 : 2 - ll1;
			ll1 /= 2;
			values[cos.indexOf('H')] = hh1;
			values[cos.indexOf('S')] = ss1;
			values[cos.indexOf('L')] = ll1;
			values[cos.indexOf('A')] = rgb[3];
			return values;
		case HSL: case HLS: case SHL: case SLH: case LHS: case LSH:
			values = new float[3];
			float[] hsv4 = Color.RGBtoHSB(
					(int)(rgb[0]*255.0f),
					(int)(rgb[1]*255.0f),
					(int)(rgb[2]*255.0f),
					null
			);
			float hh2 = hsv4[0];
			float ll2 = (2 - hsv4[1]) * hsv4[2];
			float ss2 = hsv4[1] * hsv4[2];
			if (ll2 != 0) ss2 /= (ll2 <= 1) ? ll2 : 2 - ll2;
			ll2 /= 2;
			values[cos.indexOf('H')] = hh2;
			values[cos.indexOf('S')] = ss2;
			values[cos.indexOf('L')] = ll2;
			return values;
		case CMYK: case CMKY: case CYMK: case CYKM: case CKMY: case CKYM:
		case MCYK: case MCKY: case MYCK: case MYKC: case MKCY: case MKYC:
		case YCMK: case YCKM: case YMCK: case YMKC: case YKCM: case YKMC:
		case KCMY: case KCYM: case KMCY: case KMYC: case KYCM: case KYMC:
			values = new float[4];
			float c1 = 1.0f - rgb[0];
			float m1 = 1.0f - rgb[1];
			float y1 = 1.0f - rgb[2];
			float k1 = Math.min(c1, Math.min(m1, y1));
			values[cos.indexOf('C')] = ((1.0f-k1) == 0) ? 0.0f : ((c1-k1)/(1.0f-k1));
			values[cos.indexOf('M')] = ((1.0f-k1) == 0) ? 0.0f : ((m1-k1)/(1.0f-k1));
			values[cos.indexOf('Y')] = ((1.0f-k1) == 0) ? 0.0f : ((y1-k1)/(1.0f-k1));
			values[cos.indexOf('K')] = k1;
			return values;
		case CMY: case CYM: case MCY: case MYC: case YCM: case YMC:
			values = new float[3];
			values[cos.indexOf('C')] = 1.0f - rgb[0];
			values[cos.indexOf('M')] = 1.0f - rgb[1];
			values[cos.indexOf('Y')] = 1.0f - rgb[2];
			return values;
		case YIQ: case YQI: case IYQ: case IQY: case QYI: case QIY:
			values = new float[3];
			double[] yiq = RGBtoYIQ( rgb[0], rgb[1], rgb[2] );
			values[cos.indexOf('Y')] = (float)yiq[0];
			values[cos.indexOf('I')] = (float)yiq[1];
			values[cos.indexOf('Q')] = (float)yiq[2];
			return values;
		case YUV: case YVU: case UYV: case UVY: case VYU: case VUY:
			values = new float[3];
			double[] yuv = RGBtoYUV( rgb[0], rgb[1], rgb[2] );
			values[cos.indexOf('Y')] = (float)yuv[0];
			values[cos.indexOf('U')] = (float)yuv[1];
			values[cos.indexOf('V')] = (float)yuv[2];
			return values;
		case XYZ: case XZY: case YXZ: case YZX: case ZXY: case ZYX:
			values = new float[3];
			double[] xyz = sRGBtoXYZ( rgb[0], rgb[1], rgb[2] );
			values[cos.indexOf('X')] = (float)xyz[0];
			values[cos.indexOf('Y')] = (float)xyz[1];
			values[cos.indexOf('Z')] = (float)xyz[2];
			return values;
		default:
			throw new IllegalArgumentException("Invalid channel order");
		}
	}
	
	private static final double[] RGBtoYIQ(double r, double g, double b) {
		double y = +0.3000*r +0.5900*g +0.1100*b;
		double i = +0.5990*r -0.2773*g -0.3217*b;
		double q = +0.2130*r -0.5251*g +0.3121*b;
		return new double[] { y, i, q };
	}
	
	private static final double[] YIQtoRGB(double y, double i, double q) {
		double r = y +0.9469*i +0.6236*q;
		double g = y -0.2748*i -0.6357*q;
		double b = y -1.1086*i +1.7090*q;
		return new double[] { r, g, b };
	}
	
	private static final double[] RGBtoYUV(double r, double g, double b) {
		double y = +0.29900*r +0.58700*g +0.11400*b;
		double u = -0.14713*r -0.28886*g +0.43600*b;
		double v = +0.61500*r -0.51499*g -0.10001*b;
		return new double[] { y, u, v };
	}
	
	private static final double[] YUVtoRGB(double y, double u, double v) {
		double r = y            +1.13983*v;
		double g = y -0.39465*u -0.58060*v;
		double b = y +2.03211*u           ;
		return new double[] { r, g, b };
	}
	
	private static final double[] XYZtosRGB(double x, double y, double z) {
		double rl = +3.2410*x -1.5374*y -0.4986*z;
		double gl = -0.9692*x +1.8760*y +0.0416*z;
		double bl = +0.0556*x -0.2040*y +1.0570*z;
		double r = (rl <= 0.0031308) ? (12.92 * rl) : (1.055 * Math.pow(rl, 1/2.4) - 0.055);
		double g = (gl <= 0.0031308) ? (12.92 * gl) : (1.055 * Math.pow(gl, 1/2.4) - 0.055);
		double b = (bl <= 0.0031308) ? (12.92 * bl) : (1.055 * Math.pow(bl, 1/2.4) - 0.055);
		return new double[] { r, g, b };
	}
	
	private static final double[] sRGBtoXYZ(double r, double g, double b) {
		double rl = (r <= 0.04045) ? (r / 12.92) : Math.pow((r+0.055)/1.055, 2.4);
		double gl = (g <= 0.04045) ? (g / 12.92) : Math.pow((g+0.055)/1.055, 2.4);
		double bl = (b <= 0.04045) ? (b / 12.92) : Math.pow((b+0.055)/1.055, 2.4);
		double x = +0.4124*rl +0.3576*gl +0.1805*bl;
		double y = +0.2126*rl +0.7152*gl +0.0722*bl;
		double z = +0.0193*rl +0.1192*gl +0.9505*bl;
		return new double[] { x, y, z };
	}
}
