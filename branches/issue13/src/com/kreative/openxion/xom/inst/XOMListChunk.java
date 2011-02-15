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

import java.util.*;

import com.kreative.openxion.XNContext;
import com.kreative.openxion.XNScriptError;
import com.kreative.openxion.ast.XNModifier;
import com.kreative.openxion.util.XIONUtil;
import com.kreative.openxion.xom.XOMContainerObject;
import com.kreative.openxion.xom.XOMListContainer;
import com.kreative.openxion.xom.XOMComparator;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.type.XOMListType;

public class XOMListChunk extends XOMContainerObject implements XOMListContainer {
	private static final long serialVersionUID = 1L;
	
	private XOMVariant parent;
	private int startIndex;
	private int endIndex;
	private boolean singular;
	
	public XOMListChunk(XOMVariant parent, int index) {
		this.parent = parent;
		this.startIndex = index;
		this.endIndex = index;
		this.singular = true;
	}
	
	public XOMListChunk(XOMVariant parent, int startIndex, int endIndex) {
		this.parent = parent;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.singular = false;
	}
	
	public boolean canGetParent() {
		return true;
	}
	
	public XOMVariant getParent() {
		return parent;
	}
	
	private static class ListChunkInfo {
		public List<XOMVariant> parentContent;
		public int chunkCount;
		public int startChunkIndex;
		public int endChunkIndex;
		public int startElementIndex;
		public int endElementIndex;
	}
	
	private ListChunkInfo getChunkInfo(XNContext ctx, boolean puttingBefore, boolean puttingAfter) {
		List<XOMVariant> pl = XOMListType.instance.makeInstanceFrom(ctx, parent.asPrimitive(ctx)).toList();
		if (pl == null) return null;
		else {
			int[] idx = XIONUtil.index(1, pl.size(), startIndex, endIndex);
			int s = idx[0], e = idx[1];
			if ((puttingBefore && s > pl.size()) || (puttingAfter && e > pl.size())) {
				List<XOMVariant> a = new Vector<XOMVariant>();
				int n = ( (puttingBefore && puttingAfter) ? Math.max(s,e) : puttingBefore ? s-1 : puttingAfter ? e : pl.size() )-pl.size();
				while (n-- > 0) {
					a.add(XOMEmpty.EMPTY);
				}
				List<XOMVariant> plnew = new Vector<XOMVariant>();
				plnew.addAll(pl);
				plnew.addAll(a);
				pl = plnew;
				parent.asContainer(ctx).putAfterContents(ctx, new XOMList(a));
			}
			if ((puttingBefore && s < 1) || (puttingAfter && e < 1)) {
				List<XOMVariant> a = new Vector<XOMVariant>();
				int n = 1-( (puttingBefore && puttingAfter) ? Math.min(s,e) : puttingBefore ? s : puttingAfter ? e+1 : 1 );
				while (n-- > 0) {
					a.add(XOMEmpty.EMPTY);
					s++;
					e++;
				}
				List<XOMVariant> plnew = new Vector<XOMVariant>();
				plnew.addAll(a);
				plnew.addAll(pl);
				pl = plnew;
				parent.asContainer(ctx).putBeforeContents(ctx, new XOMList(a));
			}
			ListChunkInfo ci = new ListChunkInfo();
			ci.parentContent = pl;
			ci.chunkCount = pl.size();
			ci.startChunkIndex = s;
			ci.endChunkIndex = e;
			ci.startElementIndex = s-1;
			ci.endElementIndex = e;
			if (ci.startElementIndex < 0) ci.startElementIndex = 0;
			else if (ci.startElementIndex > pl.size()) ci.startElementIndex = pl.size();
			if (ci.endElementIndex < 0) ci.endElementIndex = 0;
			else if (ci.endElementIndex > pl.size()) ci.endElementIndex = pl.size();
			if (ci.startElementIndex > ci.endElementIndex) ci.endElementIndex = ci.startElementIndex;
			return ci;
		}
	}
	
	public List<XOMVariant> toList(XNContext ctx) {
		ListChunkInfo ci = getChunkInfo(ctx, false, false);
		return ci.parentContent.subList(ci.startElementIndex, ci.endElementIndex);
	}
	
	public boolean canDelete(XNContext ctx) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canDeleteList(ctx)) {
			return true;
		}
		else if (parent.asGiven().canPutContents(ctx)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void delete(XNContext ctx) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canDeleteList(ctx)) {
			XOMListContainer p = (XOMListContainer)parent;
			ListChunkInfo ci = getChunkInfo(ctx, false, false);
			p.deleteList(ctx, ci.startElementIndex, ci.endElementIndex);
		}
		else if (parent.asGiven().canPutContents(ctx)) {
			ListChunkInfo ci = getChunkInfo(ctx, false, false);
			List<XOMVariant> left = ci.parentContent.subList(0, ci.startElementIndex);
			List<XOMVariant> right = ci.parentContent.subList(ci.endElementIndex, ci.chunkCount);
			Vector<XOMVariant> v = new Vector<XOMVariant>();
			v.addAll(left); v.addAll(right);
			parent.asContainer(ctx).putIntoContents(ctx, new XOMList(v));
		}
		else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public boolean canDeleteList(XNContext ctx) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canDeleteList(ctx)) {
			return true;
		}
		else if (parent.asGiven().canPutContents(ctx)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void deleteList(XNContext ctx, int startElementIndex, int endElementIndex) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canDeleteList(ctx)) {
			XOMListContainer p = (XOMListContainer)parent;
			ListChunkInfo ci = getChunkInfo(ctx, false, false);
			int s = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+startElementIndex));
			int e = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+endElementIndex));
			p.deleteList(ctx, s, e);
		}
		else if (parent.asGiven().canPutContents(ctx)) {
			ListChunkInfo ci = getChunkInfo(ctx, false, false);
			int s = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+startElementIndex));
			int e = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+endElementIndex));
			List<XOMVariant> left = ci.parentContent.subList(0, s);
			List<XOMVariant> right = ci.parentContent.subList(e, ci.chunkCount);
			Vector<XOMVariant> v = new Vector<XOMVariant>();
			v.addAll(left); v.addAll(right);
			parent.asContainer(ctx).putIntoContents(ctx, new XOMList(v));
		}
		else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public boolean canGetContents(XNContext ctx) {
		return true;
	}
	
	public XOMVariant getContents(XNContext ctx) {
		if (singular) {
			ListChunkInfo ci = getChunkInfo(ctx, false, false);
			if (ci.startElementIndex >= 0 && ci.startElementIndex < ci.parentContent.size() && ci.endElementIndex > 0 && ci.endElementIndex <= ci.parentContent.size()) {
				return ci.parentContent.get(ci.startElementIndex);
			} else {
				return XOMEmpty.EMPTY;
			}
		}
		else if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canGetList(ctx)) {
			XOMListContainer p = (XOMListContainer)parent;
			ListChunkInfo ci = getChunkInfo(ctx, false, false);
			return p.getList(ctx, ci.startElementIndex, ci.endElementIndex);
		}
		else {
			ListChunkInfo ci = getChunkInfo(ctx, false, false);
			return new XOMList(ci.parentContent.subList(ci.startElementIndex, ci.endElementIndex));
		}
	}
	
	public boolean canGetList(XNContext ctx) {
		return true;
	}
	
	public XOMVariant getList(XNContext ctx, int startElementIndex, int endElementIndex) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canGetList(ctx)) {
			XOMListContainer p = (XOMListContainer)parent;
			ListChunkInfo ci = getChunkInfo(ctx, false, false);
			int s = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+startElementIndex));
			int e = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+endElementIndex));
			return p.getList(ctx, s, e);
		}
		else {
			ListChunkInfo ci = getChunkInfo(ctx, false, false);
			int s = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+startElementIndex));
			int e = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+endElementIndex));
			return new XOMList(ci.parentContent.subList(s, e));
		}
	}
	
	public boolean canPutContents(XNContext ctx) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canPutList(ctx)) {
			return true;
		}
		else if (parent.asGiven().canPutContents(ctx)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void putIntoContents(XNContext ctx, XOMVariant contents) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canPutList(ctx)) {
			XOMListContainer p = (XOMListContainer)parent;
			ListChunkInfo ci = getChunkInfo(ctx, true, true);
			p.putIntoList(ctx, ci.startElementIndex, ci.endElementIndex, contents);
		}
		else if (parent.asGiven().canPutContents(ctx)) {
			ListChunkInfo ci = getChunkInfo(ctx, true, true);
			List<XOMVariant> left = ci.parentContent.subList(0, ci.startElementIndex);
			List<XOMVariant> right = ci.parentContent.subList(ci.endElementIndex, ci.chunkCount);
			Vector<XOMVariant> v = new Vector<XOMVariant>();
			v.addAll(left);
			if (contents instanceof XOMList) {
				v.addAll(((XOMList)contents).toList());
			} else {
				v.add(contents);
			}
			v.addAll(right);
			parent.asContainer(ctx).putIntoContents(ctx, new XOMList(v));
		}
		else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public void putBeforeContents(XNContext ctx, XOMVariant contents) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canPutList(ctx)) {
			XOMListContainer p = (XOMListContainer)parent;
			ListChunkInfo ci = getChunkInfo(ctx, true, false);
			p.putBeforeList(ctx, ci.startElementIndex, ci.endElementIndex, contents);
		}
		else if (parent.asGiven().canPutContents(ctx)) {
			ListChunkInfo ci = getChunkInfo(ctx, true, false);
			List<XOMVariant> left = ci.parentContent.subList(0, ci.startElementIndex);
			List<XOMVariant> right = ci.parentContent.subList(ci.startElementIndex, ci.chunkCount);
			Vector<XOMVariant> v = new Vector<XOMVariant>();
			v.addAll(left);
			if (contents instanceof XOMList) {
				v.addAll(((XOMList)contents).toList());
			} else {
				v.add(contents);
			}
			v.addAll(right);
			parent.asContainer(ctx).putIntoContents(ctx, new XOMList(v));
		}
		else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public void putAfterContents(XNContext ctx, XOMVariant contents) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canPutList(ctx)) {
			XOMListContainer p = (XOMListContainer)parent;
			ListChunkInfo ci = getChunkInfo(ctx, false, true);
			p.putAfterList(ctx, ci.startElementIndex, ci.endElementIndex, contents);
		}
		else if (parent.asGiven().canPutContents(ctx)) {
			ListChunkInfo ci = getChunkInfo(ctx, false, true);
			List<XOMVariant> left = ci.parentContent.subList(0, ci.endElementIndex);
			List<XOMVariant> right = ci.parentContent.subList(ci.endElementIndex, ci.chunkCount);
			Vector<XOMVariant> v = new Vector<XOMVariant>();
			v.addAll(left);
			if (contents instanceof XOMList) {
				v.addAll(((XOMList)contents).toList());
			} else {
				v.add(contents);
			}
			v.addAll(right);
			parent.asContainer(ctx).putIntoContents(ctx, new XOMList(v));
		}
		else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public void putIntoContents(XNContext ctx, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canPutList(ctx)) {
			XOMListContainer p = (XOMListContainer)parent;
			ListChunkInfo ci = getChunkInfo(ctx, true, true);
			p.putIntoList(ctx, ci.startElementIndex, ci.endElementIndex, contents, property, pvalue);
		}
		else if (parent.asGiven().canPutContents(ctx)) {
			ListChunkInfo ci = getChunkInfo(ctx, true, true);
			List<XOMVariant> left = ci.parentContent.subList(0, ci.startElementIndex);
			List<XOMVariant> right = ci.parentContent.subList(ci.endElementIndex, ci.chunkCount);
			Vector<XOMVariant> v = new Vector<XOMVariant>();
			v.addAll(left);
			if (contents instanceof XOMList) {
				v.addAll(((XOMList)contents).toList());
			} else {
				v.add(contents);
			}
			v.addAll(right);
			parent.asContainer(ctx).putIntoContents(ctx, new XOMList(v), property, pvalue);
		}
		else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public void putBeforeContents(XNContext ctx, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canPutList(ctx)) {
			XOMListContainer p = (XOMListContainer)parent;
			ListChunkInfo ci = getChunkInfo(ctx, true, false);
			p.putBeforeList(ctx, ci.startElementIndex, ci.endElementIndex, contents, property, pvalue);
		}
		else if (parent.asGiven().canPutContents(ctx)) {
			ListChunkInfo ci = getChunkInfo(ctx, true, false);
			List<XOMVariant> left = ci.parentContent.subList(0, ci.startElementIndex);
			List<XOMVariant> right = ci.parentContent.subList(ci.startElementIndex, ci.chunkCount);
			Vector<XOMVariant> v = new Vector<XOMVariant>();
			v.addAll(left);
			if (contents instanceof XOMList) {
				v.addAll(((XOMList)contents).toList());
			} else {
				v.add(contents);
			}
			v.addAll(right);
			parent.asContainer(ctx).putIntoContents(ctx, new XOMList(v), property, pvalue);
		}
		else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public void putAfterContents(XNContext ctx, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canPutList(ctx)) {
			XOMListContainer p = (XOMListContainer)parent;
			ListChunkInfo ci = getChunkInfo(ctx, false, true);
			p.putAfterList(ctx, ci.startElementIndex, ci.endElementIndex, contents, property, pvalue);
		}
		else if (parent.asGiven().canPutContents(ctx)) {
			ListChunkInfo ci = getChunkInfo(ctx, false, true);
			List<XOMVariant> left = ci.parentContent.subList(0, ci.endElementIndex);
			List<XOMVariant> right = ci.parentContent.subList(ci.endElementIndex, ci.chunkCount);
			Vector<XOMVariant> v = new Vector<XOMVariant>();
			v.addAll(left);
			if (contents instanceof XOMList) {
				v.addAll(((XOMList)contents).toList());
			} else {
				v.add(contents);
			}
			v.addAll(right);
			parent.asContainer(ctx).putIntoContents(ctx, new XOMList(v), property, pvalue);
		}
		else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public boolean canPutList(XNContext ctx) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canPutList(ctx)) {
			return true;
		}
		else if (parent.asGiven().canPutContents(ctx)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void putIntoList(XNContext ctx, int startElementIndex, int endElementIndex, XOMVariant contents) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canPutList(ctx)) {
			XOMListContainer p = (XOMListContainer)parent;
			ListChunkInfo ci = getChunkInfo(ctx, true, true);
			int s = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+startElementIndex));
			int e = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+endElementIndex));
			p.putIntoList(ctx, s, e, contents);
		}
		else if (parent.asGiven().canPutContents(ctx)) {
			ListChunkInfo ci = getChunkInfo(ctx, true, true);
			int s = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+startElementIndex));
			int e = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+endElementIndex));
			List<XOMVariant> left = ci.parentContent.subList(0, s);
			List<XOMVariant> right = ci.parentContent.subList(e, ci.chunkCount);
			Vector<XOMVariant> v = new Vector<XOMVariant>();
			v.addAll(left);
			if (contents instanceof XOMList) {
				v.addAll(((XOMList)contents).toList());
			} else {
				v.add(contents);
			}
			v.addAll(right);
			parent.asContainer(ctx).putIntoContents(ctx, new XOMList(v));
		}
		else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public void putBeforeList(XNContext ctx, int startElementIndex, int endElementIndex, XOMVariant contents) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canPutList(ctx)) {
			XOMListContainer p = (XOMListContainer)parent;
			ListChunkInfo ci = getChunkInfo(ctx, true, false);
			int s = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+startElementIndex));
			int e = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+endElementIndex));
			p.putBeforeList(ctx, s, e, contents);
		}
		else if (parent.asGiven().canPutContents(ctx)) {
			ListChunkInfo ci = getChunkInfo(ctx, true, false);
			int s = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+startElementIndex));
			List<XOMVariant> left = ci.parentContent.subList(0, s);
			List<XOMVariant> right = ci.parentContent.subList(s, ci.chunkCount);
			Vector<XOMVariant> v = new Vector<XOMVariant>();
			v.addAll(left);
			if (contents instanceof XOMList) {
				v.addAll(((XOMList)contents).toList());
			} else {
				v.add(contents);
			}
			v.addAll(right);
			parent.asContainer(ctx).putIntoContents(ctx, new XOMList(v));
		}
		else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public void putAfterList(XNContext ctx, int startElementIndex, int endElementIndex, XOMVariant contents) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canPutList(ctx)) {
			XOMListContainer p = (XOMListContainer)parent;
			ListChunkInfo ci = getChunkInfo(ctx, false, true);
			int s = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+startElementIndex));
			int e = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+endElementIndex));
			p.putAfterList(ctx, s, e, contents);
		}
		else if (parent.asGiven().canPutContents(ctx)) {
			ListChunkInfo ci = getChunkInfo(ctx, false, true);
			int e = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+endElementIndex));
			List<XOMVariant> left = ci.parentContent.subList(0, e);
			List<XOMVariant> right = ci.parentContent.subList(e, ci.chunkCount);
			Vector<XOMVariant> v = new Vector<XOMVariant>();
			v.addAll(left);
			if (contents instanceof XOMList) {
				v.addAll(((XOMList)contents).toList());
			} else {
				v.add(contents);
			}
			v.addAll(right);
			parent.asContainer(ctx).putIntoContents(ctx, new XOMList(v));
		}
		else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public void putIntoList(XNContext ctx, int startElementIndex, int endElementIndex, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canPutList(ctx)) {
			XOMListContainer p = (XOMListContainer)parent;
			ListChunkInfo ci = getChunkInfo(ctx, true, true);
			int s = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+startElementIndex));
			int e = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+endElementIndex));
			p.putIntoList(ctx, s, e, contents, property, pvalue);
		}
		else if (parent.asGiven().canPutContents(ctx)) {
			ListChunkInfo ci = getChunkInfo(ctx, true, true);
			int s = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+startElementIndex));
			int e = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+endElementIndex));
			List<XOMVariant> left = ci.parentContent.subList(0, s);
			List<XOMVariant> right = ci.parentContent.subList(e, ci.chunkCount);
			Vector<XOMVariant> v = new Vector<XOMVariant>();
			v.addAll(left);
			if (contents instanceof XOMList) {
				v.addAll(((XOMList)contents).toList());
			} else {
				v.add(contents);
			}
			v.addAll(right);
			parent.asContainer(ctx).putIntoContents(ctx, new XOMList(v), property, pvalue);
		}
		else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public void putBeforeList(XNContext ctx, int startElementIndex, int endElementIndex, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canPutList(ctx)) {
			XOMListContainer p = (XOMListContainer)parent;
			ListChunkInfo ci = getChunkInfo(ctx, true, false);
			int s = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+startElementIndex));
			int e = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+endElementIndex));
			p.putBeforeList(ctx, s, e, contents, property, pvalue);
		}
		else if (parent.asGiven().canPutContents(ctx)) {
			ListChunkInfo ci = getChunkInfo(ctx, true, false);
			int s = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+startElementIndex));
			List<XOMVariant> left = ci.parentContent.subList(0, s);
			List<XOMVariant> right = ci.parentContent.subList(s, ci.chunkCount);
			Vector<XOMVariant> v = new Vector<XOMVariant>();
			v.addAll(left);
			if (contents instanceof XOMList) {
				v.addAll(((XOMList)contents).toList());
			} else {
				v.add(contents);
			}
			v.addAll(right);
			parent.asContainer(ctx).putIntoContents(ctx, new XOMList(v), property, pvalue);
		}
		else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public void putAfterList(XNContext ctx, int startElementIndex, int endElementIndex, XOMVariant contents, String property, XOMVariant pvalue) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canPutList(ctx)) {
			XOMListContainer p = (XOMListContainer)parent;
			ListChunkInfo ci = getChunkInfo(ctx, false, true);
			int s = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+startElementIndex));
			int e = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+endElementIndex));
			p.putAfterList(ctx, s, e, contents, property, pvalue);
		}
		else if (parent.asGiven().canPutContents(ctx)) {
			ListChunkInfo ci = getChunkInfo(ctx, false, true);
			int e = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+endElementIndex));
			List<XOMVariant> left = ci.parentContent.subList(0, e);
			List<XOMVariant> right = ci.parentContent.subList(e, ci.chunkCount);
			Vector<XOMVariant> v = new Vector<XOMVariant>();
			v.addAll(left);
			if (contents instanceof XOMList) {
				v.addAll(((XOMList)contents).toList());
			} else {
				v.add(contents);
			}
			v.addAll(right);
			parent.asContainer(ctx).putIntoContents(ctx, new XOMList(v), property, pvalue);
		}
		else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public boolean canSortContents(XNContext ctx) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canSortList(ctx)) {
			return true;
		}
		else if (parent.asGiven().canPutContents(ctx)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void sortContents(XNContext ctx, XOMComparator cmp) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canSortList(ctx)) {
			XOMListContainer p = (XOMListContainer)parent;
			ListChunkInfo ci = getChunkInfo(ctx, false, false);
			p.sortList(ctx, ci.startElementIndex, ci.endElementIndex, cmp);
		}
		else if (parent.asGiven().canPutContents(ctx)) {
			ListChunkInfo ci = getChunkInfo(ctx, false, false);
			List<XOMVariant> left = ci.parentContent.subList(0, ci.startElementIndex);
			List<XOMVariant> middle = ci.parentContent.subList(ci.startElementIndex, ci.endElementIndex);
			List<XOMVariant> right = ci.parentContent.subList(ci.endElementIndex, ci.chunkCount);
			Vector<XOMVariant> v = new Vector<XOMVariant>();
			v.addAll(middle);
			Collections.sort(v, cmp);
			v.addAll(0, left);
			v.addAll(right);
			parent.asContainer(ctx).putIntoContents(ctx, new XOMList(v));
		}
		else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public boolean canSortList(XNContext ctx) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canSortList(ctx)) {
			return true;
		}
		else if (parent.asGiven().canPutContents(ctx)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void sortList(XNContext ctx, int startElementIndex, int endElementIndex, XOMComparator cmp) {
		if (parent instanceof XOMListContainer && ((XOMListContainer)parent).canSortList(ctx)) {
			XOMListContainer p = (XOMListContainer)parent;
			ListChunkInfo ci = getChunkInfo(ctx, false, false);
			int s = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+startElementIndex));
			int e = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+endElementIndex));
			p.sortList(ctx, s, e, cmp);
		}
		else if (parent.asGiven().canPutContents(ctx)) {
			ListChunkInfo ci = getChunkInfo(ctx, false, false);
			int s = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+startElementIndex));
			int e = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+endElementIndex));
			List<XOMVariant> left = ci.parentContent.subList(0, s);
			List<XOMVariant> middle = ci.parentContent.subList(s, e);
			List<XOMVariant> right = ci.parentContent.subList(e, ci.chunkCount);
			Vector<XOMVariant> v = new Vector<XOMVariant>();
			v.addAll(middle);
			Collections.sort(v, cmp);
			v.addAll(0, left);
			v.addAll(right);
			parent.asContainer(ctx).putIntoContents(ctx, new XOMList(v));
		}
		else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public boolean canGetProperty(XNContext ctx, String property) {
		return (property.equalsIgnoreCase("number")) ||
		((parent instanceof XOMListContainer) && (((XOMListContainer)parent).canGetListProperty(ctx, property)));
	}
	
	public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String property) {
		if (property.equalsIgnoreCase("number")) {
			ListChunkInfo ci = getChunkInfo(ctx, false, false);
			return new XOMInteger(ci.endChunkIndex - ci.startChunkIndex + 1);
		} else if (canGetProperty(ctx, property)) {
			XOMListContainer p = (XOMListContainer)parent;
			ListChunkInfo ci = getChunkInfo(ctx, false, false);
			return p.getListProperty(ctx, modifier, property, ci.startElementIndex, ci.endElementIndex);
		} else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public boolean canGetListProperty(XNContext ctx, String property) {
		return (parent instanceof XOMListContainer) && (((XOMListContainer)parent).canGetListProperty(ctx, property));
	}
	
	public XOMVariant getListProperty(XNContext ctx, XNModifier modifier, String property, int startElementIndex, int endElementIndex) {
		if (canGetListProperty(ctx, property)) {
			XOMListContainer p = (XOMListContainer)parent;
			ListChunkInfo ci = getChunkInfo(ctx, false, false);
			int s = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+startElementIndex));
			int e = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+endElementIndex));
			return p.getListProperty(ctx, modifier, property, s, e);
		} else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public boolean canSetProperty(XNContext ctx, String property) {
		return (parent instanceof XOMListContainer) && (((XOMListContainer)parent).canSetListProperty(ctx, property));
	}
	
	public void setProperty(XNContext ctx, String property, XOMVariant value) {
		if (canSetProperty(ctx, property)) {
			XOMListContainer p = (XOMListContainer)parent;
			ListChunkInfo ci = getChunkInfo(ctx, false, false);
			p.setListProperty(ctx, property, ci.startElementIndex, ci.endElementIndex, value);
		} else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	public boolean canSetListProperty(XNContext ctx, String property) {
		return (parent instanceof XOMListContainer) && (((XOMListContainer)parent).canSetListProperty(ctx, property));
	}
	
	public void setListProperty(XNContext ctx, String property, int startElementIndex, int endElementIndex, XOMVariant value) {
		if (canSetProperty(ctx, property)) {
			XOMListContainer p = (XOMListContainer)parent;
			ListChunkInfo ci = getChunkInfo(ctx, false, false);
			int s = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+startElementIndex));
			int e = Math.max(ci.startElementIndex, Math.min(ci.endElementIndex, ci.startElementIndex+endElementIndex));
			p.setListProperty(ctx, property, s, e, value);
		} else {
			throw new XNScriptError("Can't understand this");
		}
	}
	
	protected String toLanguageStringImpl() {
		if (startIndex == endIndex) {
			return "element " + startIndex + " of " + parent.toLanguageString();
		} else {
			return "elements " + startIndex + " through " + endIndex + " of " + parent.toLanguageString();
		}
	}
	protected String toTextStringImpl(XNContext ctx) {
		return getContents(ctx).toTextString(ctx);
	}
	protected int hashCodeImpl() {
		return parent.hashCode() ^ startIndex ^ endIndex;
	}
	protected boolean equalsImpl(XOMVariant o) {
		if (o instanceof XOMListChunk) {
			XOMListChunk other = (XOMListChunk)o;
			return this.parent.equals(other.parent) && this.startIndex == other.startIndex && this.endIndex == other.endIndex;
		} else {
			return false;
		}
	}
}
