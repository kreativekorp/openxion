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

package com.kreative.openxion.xom;

import java.math.BigDecimal;
import java.text.Collator;
import java.text.CollationKey;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Calendar;
import com.kreative.openxion.XNInterpreter;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.ast.XNExpression;
import com.kreative.openxion.ast.XNVariableScope;
import com.kreative.openxion.xom.inst.XOMNumber;
import com.kreative.openxion.xom.type.XOMStringType;
import com.kreative.openxion.xom.type.XOMNumberType;
import com.kreative.openxion.xom.type.XOMDateType;

/**
 * XOMComparator encapsulates the parameters for
 * and provides the basic comparison routine for
 * the standard XION sort command.
 * @since OpenXION 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XOMComparator implements Comparator<XOMVariant> {
	public static final int ORDER_ASCENDING = 0;
	public static final int ORDER_DESCENDING = 1;
	
	public static final int TYPE_TEXT = 0;
	public static final int TYPE_INTERNATIONAL = 1;
	public static final int TYPE_NUMERIC = 2;
	public static final int TYPE_DATETIME = 3;
	
	private Collator coll;
	private XNInterpreter interp;
	private XNContext ctx;
	private int order;
	private int type;
	private XNExpression filter;
	private IdentityHashMap<XOMVariant,Object> map;
	
	public XOMComparator(XNContext ctx) {
		this(ctx, ORDER_ASCENDING, TYPE_TEXT, null);
	}
	
	public XOMComparator(XNContext ctx, int type) {
		this(ctx, ORDER_ASCENDING, type, null);
	}
	
	public XOMComparator(XNContext ctx, int order, int type) {
		this(ctx, order, type, null);
	}
	
	public XOMComparator(XNContext ctx, XNExpression filter) {
		this(ctx, ORDER_ASCENDING, TYPE_TEXT, filter);
	}
	
	public XOMComparator(XNContext ctx, int type, XNExpression filter) {
		this(ctx, ORDER_ASCENDING, type, filter);
	}
	
	public XOMComparator(XNContext ctx, int order, int type, XNExpression filter) {
		this.coll = Collator.getInstance();
		this.coll.setStrength(Collator.PRIMARY);
		this.coll.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
		this.interp = new XNInterpreter(ctx);
		this.ctx = ctx;
		this.order = order;
		this.type = type;
		this.filter = filter;
		this.map = new IdentityHashMap<XOMVariant,Object>();
	}
	
	public int getSortOrder() { return order; }
	public int getSortType() { return type; }
	public XNExpression getSortFilter() { return filter; }
	
	public Object getKey(XOMVariant o) {
		if (map.containsKey(o)) {
			return map.get(o);
		} else {
			if (filter != null && interp != null) {
				ctx.setVariableScope("each", XNVariableScope.LOCAL);
				ctx.getVariableMap("each").declareVariable(ctx, "each", XOMStringType.instance, o);
				o = interp.evaluateExpression(filter);
			}
			switch (type) {
			case TYPE_TEXT:
				try {
					String s = XOMStringType.instance.makeInstanceFrom(ctx,o).toTextString(ctx);
					map.put(o, s);
					return s;
				} catch (Exception e) {
					String s = o.toTextString(ctx);
					map.put(o, s);
					return s;
				}
			case TYPE_INTERNATIONAL:
				try {
					CollationKey k = coll.getCollationKey(XOMStringType.instance.makeInstanceFrom(ctx,o).toTextString(ctx));
					map.put(o, k);
					return k;
				} catch (Exception e) {
					String s = o.toTextString(ctx);
					map.put(o, s);
					return s;
				}
			case TYPE_NUMERIC:
				try {
					XOMNumber n = XOMNumberType.instance.makeInstanceFrom(ctx,o,false);
					Object q = n.isUndefined() ? n.toDouble() : n.toBigDecimal();
					map.put(o, q);
					return q;
				} catch (Exception e) {
					String s = o.toTextString(ctx);
					map.put(o, s);
					return s;
				}
			case TYPE_DATETIME:
				try {
					Calendar c = XOMDateType.instance.makeInstanceFrom(ctx,o).toCalendar();
					map.put(o, c);
					return c;
				} catch (Exception e) {
					String s = o.toTextString(ctx);
					map.put(o, s);
					return s;
				}
			default:
				String s = o.toTextString(ctx);
				map.put(o, s);
				return s;
			}
		}
	}
	
	public int compare(XOMVariant o1, XOMVariant o2) {
		int cmp;
		Object k1 = getKey(o1);
		Object k2 = getKey(o2);
		if (k1 instanceof CollationKey && k2 instanceof CollationKey) {
			cmp = ((CollationKey)k1).compareTo((CollationKey)k2);
		}
		else if (k1 instanceof BigDecimal && k2 instanceof BigDecimal) {
			cmp = ((BigDecimal)k1).compareTo((BigDecimal)k2);
		}
		else if ((k1 instanceof BigDecimal || k1 instanceof Double) && (k2 instanceof BigDecimal || k2 instanceof Double)) {
			double d1 = (k1 instanceof BigDecimal) ? ((BigDecimal)k1).doubleValue() : ((Double)k1);
			double d2 = (k2 instanceof BigDecimal) ? ((BigDecimal)k2).doubleValue() : ((Double)k2);
			cmp = Double.compare(d1, d2);
		}
		else if (k1 instanceof Calendar && k2 instanceof Calendar) {
			cmp = ((Calendar)k1).compareTo((Calendar)k2);
		}
		else if (k1 instanceof String && k2 instanceof String) {
			cmp = ((String)k1).compareToIgnoreCase((String)k2);
		}
		else {
			cmp = o1.toTextString(ctx).compareToIgnoreCase(o2.toTextString(ctx));
		}
		switch (order) {
		case ORDER_ASCENDING:
			return cmp;
		case ORDER_DESCENDING:
			return -cmp;
		default:
			return cmp;
		}
	}
}
