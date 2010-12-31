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

package com.kreative.openxion.binpack;

import java.util.Map;

public class DFFieldExpression implements DFExpression {
	private DFFieldExpression parent;
	private Object key;
	
	public DFFieldExpression(Object key) {
		this.parent = null;
		this.key = key;
	}
	
	public DFFieldExpression(DFFieldExpression parent, Object key) {
		this.parent = parent;
		this.key = key;
	}
	
	public int evaluate() {
		return 0;
	}
	
	public Map<?,?> evaluateToMap(Map<?,?> fieldValues, BitInputStream in, long length) {
		Map<?,?> map = (parent != null) ? parent.evaluateToMap(fieldValues, in, length) : fieldValues;
		if (map != null && map.containsKey(key) && map.get(key) instanceof Map) {
			return (Map<?,?>)map.get(key);
		} else {
			return null;
		}
	}
	
	public int evaluate(Map<?,?> fieldValues, BitInputStream in, long length) {
		Map<?,?> map = (parent != null) ? parent.evaluateToMap(fieldValues, in, length) : fieldValues;
		if (map != null && map.containsKey(key) && map.get(key) instanceof Number) {
			return ((Number)map.get(key)).intValue();
		} else {
			return 0;
		}
	}
	
	public Map<?,?> evaluateToMap(Map<?,?> fieldValues, BitOutputStream out) {
		Map<?,?> map = (parent != null) ? parent.evaluateToMap(fieldValues, out) : fieldValues;
		if (map != null && map.containsKey(key) && map.get(key) instanceof Map) {
			return (Map<?,?>)map.get(key);
		} else {
			return null;
		}
	}
	
	public int evaluate(Map<?,?> fieldValues, BitOutputStream out) {
		Map<?,?> map = (parent != null) ? parent.evaluateToMap(fieldValues, out) : fieldValues;
		if (map != null && map.containsKey(key) && map.get(key) instanceof Number) {
			return ((Number)map.get(key)).intValue();
		} else {
			return 0;
		}
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		if (parent != null) {
			s.append(parent.toString());
			s.append(".");
		}
		s.append(key.toString());
		return s.toString();
	}
}
