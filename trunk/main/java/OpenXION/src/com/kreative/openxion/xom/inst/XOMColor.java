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

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import com.kreative.openxion.XNContext;
import com.kreative.openxion.ast.XNModifier;
import com.kreative.openxion.xom.XOMPrimitiveValue;
import com.kreative.openxion.xom.XOMVariant;

public class XOMColor extends XOMPrimitiveValue {
	private static final long serialVersionUID = 1L;
	
	private int red;
	private int green;
	private int blue;
	private int alpha;
	private float[] hsb;
	
	public XOMColor(int r, int g, int b) {
		this.red = r;
		this.green = g;
		this.blue = b;
		this.alpha = 65535;
		this.hsb = null;
	}
	
	public XOMColor(int r, int g, int b, int a) {
		this.red = r;
		this.green = g;
		this.blue = b;
		this.alpha = a;
		this.hsb = null;
	}
	
	public XOMColor(float[] rgba) {
		this.red = (int)(rgba[0] * 65535.0f);
		this.green = (int)(rgba[1] * 65535.0f);
		this.blue = (int)(rgba[2] * 65535.0f);
		this.alpha = (rgba.length > 3) ? (int)(rgba[3] * 65535.0f) : 65535;
		this.hsb = null;
	}
	
	public XOMColor(Color c) {
		this.red = c.getRed() * 0x0101;
		this.green = c.getGreen() * 0x0101;
		this.blue = c.getBlue() * 0x0101;
		this.alpha = c.getAlpha() * 0x0101;
		this.hsb = null;
	}
	
	public float[] toRGBAFloatArray() {
		return new float[]{ red/65535.0f, green/65535.0f, blue/65535.0f, alpha/65535.0f };
	}
	
	public Color toColor() {
		return new Color(red/65535.0f, green/65535.0f, blue/65535.0f, alpha/65535.0f);
	}
	
	public boolean canGetProperty(XNContext ctx, String property) {
		return (
				property.equalsIgnoreCase("red") ||
				property.equalsIgnoreCase("green") ||
				property.equalsIgnoreCase("blue") ||
				property.equalsIgnoreCase("alpha") ||
				property.equalsIgnoreCase("hue") ||
				property.equalsIgnoreCase("saturation") ||
				property.equalsIgnoreCase("brightness")
		);
	}
	public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String property) {
		if (property.equalsIgnoreCase("red")) {
			return new XOMInteger(red);
		} else if (property.equalsIgnoreCase("green")) {
			return new XOMInteger(green);
		} else if (property.equalsIgnoreCase("blue")) {
			return new XOMInteger(blue);
		} else if (property.equalsIgnoreCase("alpha")) {
			return new XOMInteger(alpha);
		} else if (property.equalsIgnoreCase("hue")) {
			if (hsb == null) hsb = Color.RGBtoHSB(red/0x0101, green/0x0101, blue/0x0101, null);
			return new XOMNumber(hsb[0]);
		} else if (property.equalsIgnoreCase("saturation")) {
			if (hsb == null) hsb = Color.RGBtoHSB(red/0x0101, green/0x0101, blue/0x0101, null);
			return new XOMNumber(hsb[1]);
		} else if (property.equalsIgnoreCase("brightness")) {
			if (hsb == null) hsb = Color.RGBtoHSB(red/0x0101, green/0x0101, blue/0x0101, null);
			return new XOMNumber(hsb[2]);
		} else {
			return super.getProperty(ctx, modifier, property);
		}
	}
	
	protected String toLanguageStringImpl() {
		if (alpha == 65535)
			return "\""+red+","+green+","+blue+"\"";
		else
			return "\""+red+","+green+","+blue+","+alpha+"\"";
	}
	protected String toTextStringImpl(XNContext ctx) {
		if (alpha == 65535)
			return red+","+green+","+blue;
		else
			return red+","+green+","+blue+","+alpha;
	}
	protected List<? extends XOMVariant> toListImpl(XNContext ctx) {
		return Arrays.asList(this);
	}
	protected int hashCodeImpl() {
		return red ^ (green << 16) ^ blue ^ (alpha << 16);
	}
	protected boolean equalsImpl(XOMVariant o) {
		if (o instanceof XOMColor) {
			XOMColor other = (XOMColor)o;
			return this.red == other.red && this.green == other.green && this.blue == other.blue && this.alpha == other.alpha;
		} else {
			return false;
		}
	}
}
