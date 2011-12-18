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
 * @since OpenXION 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.xom.inst;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.XNScriptError;
import com.kreative.openxion.ast.XNModifier;
import com.kreative.openxion.util.XIONUtil;
import com.kreative.openxion.xom.XOMBinaryContainer;
import com.kreative.openxion.xom.XOMComparator;
import com.kreative.openxion.xom.XOMContainer;
import com.kreative.openxion.xom.XOMDictionaryContainer;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.type.XOMDictionaryType;
import com.kreative.openxion.xom.type.XOMVariantType;

public class XOMDictionaryChunk extends XOMContainer {
	private static final long serialVersionUID = 1L;
	
	private XOMVariant parent;
	private String key;
	
	public XOMDictionaryChunk(XOMVariant parent, String key) {
		this.parent = parent;
		this.key = key;
	}
	
	public boolean canGetParent(XNContext ctx) {
		return true;
	}
	
	public XOMVariant getParent(XNContext ctx) {
		return parent;
	}
	
	public boolean canDelete(XNContext ctx) {
		if (parent instanceof XOMDictionaryContainer && ((XOMDictionaryContainer)parent).canDeleteEntry(ctx)) {
			return true;
		}
		else if (parent.canPutContents(ctx)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void delete(XNContext ctx) {
		if (parent instanceof XOMDictionaryContainer && ((XOMDictionaryContainer)parent).canDeleteEntry(ctx)) {
			XOMDictionaryContainer p = (XOMDictionaryContainer)parent;
			p.deleteEntry(ctx, key);
		}
		else if (parent.canPutContents(ctx)) {
			Map<String,XOMVariant> m = new LinkedHashMap<String, XOMVariant>();
			m.putAll(XOMDictionaryType.instance.makeInstanceFrom(ctx, parent.asPrimitive(ctx)).toMap());
			m.remove(key);
			parent.asContainer(ctx, false).putIntoContents(ctx, new XOMDictionary(m));
		}
		else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public boolean canGetContents(XNContext ctx) {
		return true;
	}
	
	public XOMVariant getContents(XNContext ctx) {
		if (parent instanceof XOMDictionaryContainer && ((XOMDictionaryContainer)parent).canGetEntry(ctx)) {
			XOMDictionaryContainer p = (XOMDictionaryContainer)parent;
			return p.getEntry(ctx, key);
		}
		else if (parent.canGetContents(ctx)) {
			Map<String,XOMVariant> m = XOMDictionaryType.instance.makeInstanceFrom(ctx, parent).toMap();
			return m.containsKey(key) ? m.get(key) : XOMEmpty.EMPTY;
		}
		else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public boolean canPutContents(XNContext ctx) {
		if (parent instanceof XOMDictionaryContainer && ((XOMDictionaryContainer)parent).canPutEntry(ctx)) {
			return true;
		}
		else if (parent.canPutContents(ctx)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void putIntoContents(XNContext ctx, XOMVariant contents) {
		if (parent instanceof XOMDictionaryContainer && ((XOMDictionaryContainer)parent).canPutEntry(ctx)) {
			XOMDictionaryContainer p = (XOMDictionaryContainer)parent;
			p.putIntoEntry(ctx, key, contents);
		}
		else if (parent.canPutContents(ctx)) {
			Map<String,XOMVariant> m = new LinkedHashMap<String, XOMVariant>();
			m.putAll(XOMDictionaryType.instance.makeInstanceFrom(ctx, parent).toMap());
			m.put(key, contents);
			parent.asContainer(ctx, false).putIntoContents(ctx, new XOMDictionary(m));
		}
		else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public void putBeforeContents(XNContext ctx, XOMVariant contents) {
		if (parent instanceof XOMDictionaryContainer && ((XOMDictionaryContainer)parent).canPutEntry(ctx)) {
			XOMDictionaryContainer p = (XOMDictionaryContainer)parent;
			p.putBeforeEntry(ctx, key, contents);
		}
		else if (parent.canPutContents(ctx)) {
			Map<String,XOMVariant> m = new LinkedHashMap<String, XOMVariant>();
			m.putAll(XOMDictionaryType.instance.makeInstanceFrom(ctx, parent).toMap());
			m.put(key, m.containsKey(key) ? XOMVariantType.instance.makeInstanceFrom(ctx, contents, m.get(key)) : contents);
			parent.asContainer(ctx, false).putIntoContents(ctx, new XOMDictionary(m));
		}
		else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public void putAfterContents(XNContext ctx, XOMVariant contents) {
		if (parent instanceof XOMDictionaryContainer && ((XOMDictionaryContainer)parent).canPutEntry(ctx)) {
			XOMDictionaryContainer p = (XOMDictionaryContainer)parent;
			p.putAfterEntry(ctx, key, contents);
		}
		else if (parent.canPutContents(ctx)) {
			Map<String,XOMVariant> m = new LinkedHashMap<String, XOMVariant>();
			m.putAll(XOMDictionaryType.instance.makeInstanceFrom(ctx, parent).toMap());
			m.put(key, m.containsKey(key) ? XOMVariantType.instance.makeInstanceFrom(ctx, m.get(key), contents) : contents);
			parent.asContainer(ctx, false).putIntoContents(ctx, new XOMDictionary(m));
		}
		else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public boolean canSortContents(XNContext ctx) {
		if (parent instanceof XOMDictionaryContainer && ((XOMDictionaryContainer)parent).canSortEntry(ctx)) {
			return true;
		}
		else if (parent.canPutContents(ctx)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void sortContents(XNContext ctx, XOMComparator cmp) {
		if (parent instanceof XOMDictionaryContainer && ((XOMDictionaryContainer)parent).canSortEntry(ctx)) {
			XOMDictionaryContainer p = (XOMDictionaryContainer)parent;
			p.sortEntry(ctx, key, cmp);
		} else if (parent.canPutContents(ctx)) {
			Map<String,XOMVariant> m = new LinkedHashMap<String,XOMVariant>();
			m.putAll(XOMDictionaryType.instance.makeInstanceFrom(ctx, parent).toMap());
			XOMVariant value = m.get(key);
			List<XOMVariant> toSort = new Vector<XOMVariant>();
			if (value instanceof XOMList) {
				toSort.addAll(((XOMList)value).toPrimitiveList(ctx));
			} else if (value instanceof XOMDictionary) {
				for (String s : ((XOMDictionary)value).toMap().keySet())
					toSort.add(new XOMString(s));
			} else if (value instanceof XOMBinary) {
				byte[] bb = ((XOMBinary)value).toByteArray();
				for (byte b : bb) toSort.add(new XOMBinary(new byte[]{b}));
			} else {
				String[] strs = value.toTextString(ctx).split("\r\n|\r|\n|\u2028|\u2029");
				for (String str : strs) toSort.add(new XOMString(str));
			}
			Collections.sort(toSort, cmp);
			if (value instanceof XOMList) {
				value = new XOMList(toSort);
			} else if (value instanceof XOMDictionary) {
				Map<String,XOMVariant> oldMap = ((XOMDictionary)value).toMap();
				Map<String,XOMVariant> newMap = new LinkedHashMap<String,XOMVariant>();
				for (XOMVariant key : toSort) {
					newMap.put(key.toTextString(ctx), oldMap.get(key.toTextString(ctx)));
				}
				value = new XOMDictionary(newMap);
			} else if (value instanceof XOMBinary) {
				byte[] bb = new byte[toSort.size()];
				for (int i = 0; i < toSort.size(); i++) bb[i] = ((XOMBinary)toSort.get(i)).toByteArray()[0];
				value = new XOMBinary(bb);
			} else {
				StringBuffer s = new StringBuffer();
				for (XOMVariant v : toSort) s.append(v.toTextString(ctx) + ctx.getLineEnding());
				if (s.length() > 0 && s.substring(s.length()-ctx.getLineEnding().length()).equals(ctx.getLineEnding()))
					s.delete(s.length()-ctx.getLineEnding().length(), s.length());
				value = new XOMString(s.toString());
			}
			m.put(key, value);
			parent.asContainer(ctx, false).putIntoContents(ctx, new XOMDictionary(m));
		}
		else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public boolean canGetProperty(XNContext ctx, String property) {
		if (parent instanceof XOMBinaryContainer && ((XOMBinaryContainer)parent).canGetBinaryProperty(ctx, property)) {
			return true;
		}
		else {
			Map<String,XOMVariant> m = XOMDictionaryType.instance.makeInstanceFrom(ctx, parent).toMap();
			return m.containsKey(key) && m.get(key).canGetProperty(ctx, property);
		}
	}
	
	public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String property) {
		if (parent instanceof XOMDictionaryContainer && ((XOMDictionaryContainer)parent).canGetEntryProperty(ctx, property)) {
			XOMDictionaryContainer p = (XOMDictionaryContainer)parent;
			return p.getEntryProperty(ctx, modifier, property, key);
		}
		else {
			Map<String,XOMVariant> m = XOMDictionaryType.instance.makeInstanceFrom(ctx, parent).toMap();
			if (!m.containsKey(key)) throw new XNScriptError("Can't get that property");
			return m.get(key).getProperty(ctx, modifier, property);
		}
	}
	
	public boolean canSetProperty(XNContext ctx, String property) {
		if (parent instanceof XOMDictionaryContainer && ((XOMDictionaryContainer)parent).canSetEntryProperty(ctx, property)) {
			return true;
		}
		else {
			Map<String,XOMVariant> m = XOMDictionaryType.instance.makeInstanceFrom(ctx, parent).toMap();
			return m.containsKey(key) && m.get(key).canSetProperty(ctx, property);
		}
	}
	
	public void setProperty(XNContext ctx, String property, XOMVariant value) {
		if (parent instanceof XOMDictionaryContainer && ((XOMDictionaryContainer)parent).canSetEntryProperty(ctx, property)) {
			XOMDictionaryContainer p = (XOMDictionaryContainer)parent;
			p.setEntryProperty(ctx, property, key, value);
		}
		else {
			Map<String,XOMVariant> m = XOMDictionaryType.instance.makeInstanceFrom(ctx, parent).toMap();
			if (!m.containsKey(key)) throw new XNScriptError("Can't set that property");
			m.get(key).setProperty(ctx, property, value);
		}
	}
	
	public String toLanguageString() {
		return "entry " + XIONUtil.quote(key) + " of " + parent.toLanguageString();
	}
	public String toTextString(XNContext ctx) {
		return getContents(ctx).toTextString(ctx);
	}
	public List<? extends XOMVariant> toVariantList(XNContext ctx) {
		return Arrays.asList(this);
	}
	public List<? extends XOMVariant> toPrimitiveList(XNContext ctx) {
		return Arrays.asList(getContents(ctx));
	}
	public int hashCode() {
		return parent.hashCode() ^ key.hashCode();
	}
	public boolean equals(Object o) {
		if (o instanceof XOMDictionaryChunk) {
			XOMDictionaryChunk other = (XOMDictionaryChunk)o;
			return (this.parent.equals(other.parent) && this.key.equals(other.key));
		} else {
			return false;
		}
	}
}
