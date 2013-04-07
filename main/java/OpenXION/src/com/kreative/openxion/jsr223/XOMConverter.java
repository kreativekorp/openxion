/*
 * Copyright &copy; 2013 Rebecca G. Bettencourt / Kreative Software
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
 * @since OpenXION 1.5
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.jsr223;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.kreative.openxion.XNContext;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.inst.*;

/**
 * XOMConverter is used by the OpenXION JSR223 script engine to convert
 * XOMObjects to native Java types and vice-versa.
 * @since OpenXION 1.5
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XOMConverter {
	private XNContext ctx;
	
	public XOMConverter(XNContext ctx) {
		this.ctx = ctx;
	}
	
	public Object toNative(XOMVariant v) {
		if (v == null) {
			return null;
		} else if (v instanceof XOMBinary) {
			return ((XOMBinary)v).toByteArray();
		} else if (v instanceof XOMBoolean) {
			return ((XOMBoolean)v).toBoolean();
		} else if (v instanceof XOMClipboard) {
			return Toolkit.getDefaultToolkit().getSystemClipboard();
		} else if (v instanceof XOMColor) {
			return ((XOMColor)v).toColor();
		} else if (v instanceof XOMComplex) {
			return ((XOMComplex)v).toNumbers();
		} else if (v instanceof XOMDate) {
			return ((XOMDate)v).toDate();
		} else if (v instanceof XOMDictionary) {
			Map<String, XOMVariant> xm = ((XOMDictionary)v).toMap();
			Map<String, Object> jm = new LinkedHashMap<String, Object>();
			for (Map.Entry<String, XOMVariant> e : xm.entrySet()) {
				jm.put(e.getKey(), toNative(e.getValue()));
			}
			return jm;
		} else if (v instanceof XOMEmpty) {
			return null;
		} else if (v instanceof XOMFile) {
			return ((XOMFile)v).toFile();
		} else if (v instanceof XOMInteger) {
			return ((XOMInteger)v).toNumber();
		} else if (v instanceof XOMList) {
			List<? extends XOMVariant> xl = ((XOMList)v).toVariantList(ctx);
			List<Object> jl = new Vector<Object>();
			for (XOMVariant i : xl) jl.add(toNative(i));
			return jl;
		} else if (v instanceof XOMNumber) {
			return ((XOMNumber)v).toNumber();
		} else if (v instanceof XOMPoint) {
			return ((XOMPoint)v).toPoint();
		} else if (v instanceof XOMRectangle) {
			return ((XOMRectangle)v).toRectangle();
		} else if (v instanceof XOMString) {
			return ((XOMString)v).toTextString(ctx);
		} else if (v instanceof XOMURL) {
			return ((XOMURL)v).toURL();
		} else {
			return v;
		}
	}
	
	public XOMVariant fromNative(Object o) {
		if (o == null) {
			return XOMEmpty.EMPTY;
		} else if (o instanceof XOMVariant) {
			return (XOMVariant)o;
		} else if (o instanceof XOMVariant[]) {
			return new XOMList(Arrays.asList((XOMVariant[])o));
		} else if (o instanceof Boolean) {
			return ((Boolean)o).booleanValue() ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		} else if (o instanceof Byte) {
			return new XOMBinary(new byte[]{((Byte)o).byteValue()});
		} else if (o instanceof Character) {
			return new XOMString(o.toString());
		} else if (o instanceof Enum) {
			return new XOMString(((Enum<?>)o).name());
		} else if (o instanceof Number) {
			return new XOMNumber((Number)o);
		} else if (o instanceof String) {
			return new XOMString((String)o);
		} else if (o instanceof StringBuffer) {
			return new XOMString(o.toString());
		} else if (o instanceof StringBuilder) {
			return new XOMString(o.toString());
		} else if (o instanceof byte[]) {
			return new XOMBinary((byte[])o);
		} else if (o instanceof Byte[]) {
			byte[] b = new byte[((Byte[])o).length];
			for (int i = 0; i < b.length; i++) b[i] = ((Byte[])o)[i];
			return new XOMBinary(b);
		} else if (o instanceof Number[] && ((Number[])o).length == 2) {
			XOMNumber r = new XOMNumber(((Number[])o)[0]);
			XOMNumber i = new XOMNumber(((Number[])o)[1]);
			return new XOMComplex(r, i);
		} else if (o instanceof Object[]) {
			List<XOMVariant> l = new Vector<XOMVariant>();
			for (Object oo : (Object[])o) l.add(fromNative(oo));
			return new XOMList(l);
		} else if (o instanceof Collection) {
			List<XOMVariant> l = new Vector<XOMVariant>();
			for (Object oo : (Collection<?>)o) l.add(fromNative(oo));
			return new XOMList(l);
		} else if (o instanceof Map) {
			Map<String, XOMVariant> m = new LinkedHashMap<String, XOMVariant>();
			for (Map.Entry<?,?> e : ((Map<?,?>)o).entrySet()) {
				m.put(fromNative(e.getKey()).toTextString(ctx), fromNative(e.getValue()));
			}
			return new XOMDictionary(m);
		} else if (o instanceof Clipboard) {
			return XOMClipboard.CLIPBOARD;
		} else if (o instanceof Color) {
			return new XOMColor((Color)o);
		} else if (o instanceof Date) {
			return new XOMDate((Date)o);
		} else if (o instanceof GregorianCalendar) {
			return new XOMDate((GregorianCalendar)o);
		} else if (o instanceof File) {
			return new XOMFile((File)o);
		} else if (o instanceof Point) {
			return new XOMPoint((Point)o);
		} else if (o instanceof Rectangle) {
			return new XOMRectangle((Rectangle)o);
		} else if (o instanceof URL) {
			return new XOMURL((URL)o);
		} else {
			throw new UnsupportedOperationException("Cannot convert " + o.getClass().toString() + " to an XOMVariant.");
		}
	}
}
