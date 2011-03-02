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

import java.awt.Rectangle;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import com.kreative.openxion.XNContext;
import com.kreative.openxion.ast.XNModifier;
import com.kreative.openxion.xom.XOMValue;
import com.kreative.openxion.xom.XOMVariant;

public class XOMRectangle extends XOMValue {
	private static final long serialVersionUID = 1L;
	
	private BigInteger left;
	private BigInteger top;
	private BigInteger right;
	private BigInteger bottom;
	
	public XOMRectangle(int left, int top, int right, int bottom) {
		this.left = BigInteger.valueOf(left);
		this.top = BigInteger.valueOf(top);
		this.right = BigInteger.valueOf(right);
		this.bottom = BigInteger.valueOf(bottom);
	}
	
	public XOMRectangle(BigInteger left, BigInteger top, BigInteger right, BigInteger bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
	
	public XOMRectangle(Rectangle r) {
		this.left = BigInteger.valueOf(r.x);
		this.top = BigInteger.valueOf(r.y);
		this.right = BigInteger.valueOf(r.x+r.width);
		this.bottom = BigInteger.valueOf(r.y+r.height);
	}
	
	public BigInteger left() { return left; }
	public BigInteger top() { return top; }
	public BigInteger right() { return right; }
	public BigInteger bottom() { return bottom; }
	public XOMPoint topLeft() { return new XOMPoint(left, top); }
	public XOMPoint topRight() { return new XOMPoint(right, top); }
	public XOMPoint bottomLeft() { return new XOMPoint(left, bottom); }
	public XOMPoint bottomRight() { return new XOMPoint(right, bottom); }
	public XOMPoint center() { return new XOMPoint(left.add(right).shiftLeft(1), top.add(bottom).shiftLeft(1)); }
	public BigInteger width() { return right.subtract(left); }
	public BigInteger height() { return bottom.subtract(top); }
	
	public boolean contains(XOMPoint p) {
		return (p.x().compareTo(left) >= 0 && p.x().compareTo(right) < 0 && p.y().compareTo(top) >= 0 && p.y().compareTo(bottom) < 0);
	}
	
	public boolean contains(XOMRectangle r) {
		return !(r.left.compareTo(right) >= 0 || r.right.compareTo(left) < 0 || r.top.compareTo(bottom) >= 0 || r.bottom.compareTo(top) < 0);
	}
	
	public boolean canGetProperty(XNContext ctx, String property) {
		return (
				property.equalsIgnoreCase("top") || property.equalsIgnoreCase("left") ||
				property.equalsIgnoreCase("right") || property.equalsIgnoreCase("bottom") ||
				property.equalsIgnoreCase("topLeft") || property.equalsIgnoreCase("topRight") ||
				property.equalsIgnoreCase("botLeft") || property.equalsIgnoreCase("bottomLeft") ||
				property.equalsIgnoreCase("botRight") || property.equalsIgnoreCase("bottomRight") ||
				property.equalsIgnoreCase("loc") || property.equalsIgnoreCase("location") ||
				property.equalsIgnoreCase("center") ||
				property.equalsIgnoreCase("width") || property.equalsIgnoreCase("height")
		);
	}
	public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String property) {
		if (property.equalsIgnoreCase("top")) {
			return new XOMInteger(top);
		} else if (property.equalsIgnoreCase("left")) {
			return new XOMInteger(left);
		} else if (property.equalsIgnoreCase("right")) {
			return new XOMInteger(right);
		} else if (property.equalsIgnoreCase("bottom")) {
			return new XOMInteger(bottom);
		} else if (property.equalsIgnoreCase("topLeft")) {
			return new XOMPoint(left, top);
		} else if (property.equalsIgnoreCase("topRight")) {
			return new XOMPoint(right, top);
		} else if (property.equalsIgnoreCase("botLeft") || property.equalsIgnoreCase("bottomLeft")) {
			return new XOMPoint(left, bottom);
		} else if (property.equalsIgnoreCase("botRight") || property.equalsIgnoreCase("bottomRight")) {
			return new XOMPoint(right, bottom);
		} else if (property.equalsIgnoreCase("loc") || property.equalsIgnoreCase("location") || property.equalsIgnoreCase("center")) {
			return new XOMPoint(left.add(right).shiftLeft(1), top.add(bottom).shiftLeft(1));
		} else if (property.equalsIgnoreCase("width")) {
			return new XOMInteger(right.subtract(left));
		} else if (property.equalsIgnoreCase("height")) {
			return new XOMInteger(bottom.subtract(top));
		} else {
			return super.getProperty(ctx, modifier, property);
		}
	}
	
	public Rectangle toRectangle() {
		return new Rectangle(
				left.intValue(),
				top.intValue(),
				right.subtract(left).intValue(),
				bottom.subtract(top).intValue()
		);
	}
	
	protected String toLanguageStringImpl() {
		return "\""+((left == null)?"0":left.toString())+","+((top == null)?"0":top.toString())+","+((right == null)?"0":right.toString())+","+((bottom == null)?"0":bottom.toString())+"\"";
	}
	protected String toTextStringImpl(XNContext ctx) {
		return ((left == null)?"0":left.toString())+","+((top == null)?"0":top.toString())+","+((right == null)?"0":right.toString())+","+((bottom == null)?"0":bottom.toString());
	}
	protected List<? extends XOMVariant> toListImpl(XNContext ctx) {
		return Arrays.asList(this);
	}
	protected int hashCodeImpl() {
		return left.hashCode() ^ top.hashCode() ^ right.hashCode() ^ bottom.hashCode();
	}
	protected boolean equalsImpl(XOMVariant o) {
		if (o instanceof XOMRectangle) {
			XOMRectangle other = (XOMRectangle)o;
			return this.left.compareTo(other.left) == 0 && this.top.compareTo(other.top) == 0 && this.right.compareTo(other.right) == 0 && this.bottom.compareTo(other.bottom) == 0;
		} else {
			return false;
		}
	}
}
