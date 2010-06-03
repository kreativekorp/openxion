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

package com.kreative.openxion.xom.inst;

import java.awt.Point;
import java.math.BigInteger;
import java.util.*;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.xom.XOMVariant;

public class XOMPoint extends XOMVariant {
	private static final long serialVersionUID = 1L;
	
	private BigInteger x;
	private BigInteger y;
	
	public XOMPoint(int x, int y) {
		this.x = BigInteger.valueOf(x);
		this.y = BigInteger.valueOf(y);
	}
	
	public XOMPoint(BigInteger x, BigInteger y) {
		this.x = x;
		this.y = y;
	}
	
	public XOMPoint(Point p) {
		this.x = BigInteger.valueOf(p.x);
		this.y = BigInteger.valueOf(p.y);
	}
	
	public BigInteger x() { return x; }
	public BigInteger y() { return y; }
	
	public Point toPoint() {
		return new Point(x.intValue(), y.intValue());
	}
	
	protected boolean equalsImpl(Object o) {
		if (o instanceof XOMPoint) {
			XOMPoint other = (XOMPoint)o;
			return this.x.compareTo(other.x) == 0 && this.y.compareTo(other.y) == 0;
		} else {
			return false;
		}
	}
	public int hashCode() {
		return x.hashCode() ^ y.hashCode();
	}
	public String toDescriptionString() {
		return ((x == null)?"0":x.toString())+","+((y == null)?"0":y.toString());
	}
	public String toTextString(XNContext ctx) {
		return ((x == null)?"0":x.toString())+","+((y == null)?"0":y.toString());
	}
	public List<XOMVariant> toList(XNContext ctx) {
		Vector<XOMVariant> v = new Vector<XOMVariant>();
		v.add(this);
		return v;
	}
}
