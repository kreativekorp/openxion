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

import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.util.*;

import com.kreative.openxion.XNContext;
import com.kreative.openxion.XNSecurityKey;
import com.kreative.openxion.XNScriptError;
import com.kreative.openxion.xom.XOMContainer;
import com.kreative.openxion.xom.XOMComparator;
import com.kreative.openxion.xom.XOMVariant;

public class XOMClipboard extends XOMContainer implements ClipboardOwner {
	private static final long serialVersionUID = 1L;
	
	public static final XOMClipboard CLIPBOARD = new XOMClipboard();
	
	private XOMClipboard() {
		// nothing
	}
	
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// nothing
	}
	
	private void setClipboardString(String s) {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		cb.setContents(new StringSelection(s), this);
	}
	
	private String getClipboardString() {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		if (cb.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
			try {
				return (String)cb.getData(DataFlavor.stringFlavor);
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
		} else {
			return "";
		}
	}
	
	public boolean canGetContents(XNContext ctx) {
		return ctx.allow(XNSecurityKey.CLIPBOARD_READ, "Operation", "Read");
	}
	public XOMVariant getContents(XNContext ctx) {
		if (!ctx.allow(XNSecurityKey.CLIPBOARD_READ, "Operation", "Read"))
			throw new XNScriptError("Security settings do not allow clipboard access");
		return new XOMString(getClipboardString());
	}
	
	public boolean canPutContents(XNContext ctx) {
		return ctx.allow(XNSecurityKey.CLIPBOARD_WRITE, "Operation", "Write");
	}
	public void putIntoContents(XNContext ctx, XOMVariant contents) {
		String s = contents.toTextString(ctx);
		if (!ctx.allow(XNSecurityKey.CLIPBOARD_WRITE, "Operation", "Write", "Value", s))
			throw new XNScriptError("Security settings do not allow clipboard access");
		setClipboardString(s);
	}
	public void putBeforeContents(XNContext ctx, XOMVariant contents) {
		String s = contents.toTextString(ctx);
		if (!ctx.allow(XNSecurityKey.CLIPBOARD_WRITE, "Operation", "Write", "Value", s))
			throw new XNScriptError("Security settings do not allow clipboard access");
		setClipboardString(s + getClipboardString());
	}
	public void putAfterContents(XNContext ctx, XOMVariant contents) {
		String s = contents.toTextString(ctx);
		if (!ctx.allow(XNSecurityKey.CLIPBOARD_WRITE, "Operation", "Write", "Value", s))
			throw new XNScriptError("Security settings do not allow clipboard access");
		setClipboardString(getClipboardString() + s);
	}
	
	public boolean canSortContents(XNContext ctx) {
		return ctx.allow(XNSecurityKey.CLIPBOARD_WRITE, "Operation", "Sort");
	}
	public void sortContents(XNContext ctx, XOMComparator cmp) {
		if (!ctx.allow(XNSecurityKey.CLIPBOARD_WRITE, "Operation", "Sort"))
			throw new XNScriptError("Security settings do not allow clipboard access");
		List<XOMVariant> toSort = new Vector<XOMVariant>();
		String[] strs = getClipboardString().split("\r\n|\r|\n|\u2028|\u2029");
		for (String str : strs) toSort.add(new XOMString(str));
		Collections.sort(toSort, cmp);
		StringBuffer s = new StringBuffer();
		for (XOMVariant v : toSort) s.append(v.toTextString(ctx) + ctx.getLineEnding());
		if (s.length() > 0 && s.substring(s.length()-ctx.getLineEnding().length()).equals(ctx.getLineEnding()))
			s.delete(s.length()-ctx.getLineEnding().length(), s.length());
		setClipboardString(s.toString());
	}
	
	public String toLanguageString() {
		return "the clipboard";
	}
	public String toTextString(XNContext ctx) {
		if (!ctx.allow(XNSecurityKey.CLIPBOARD_READ, "Operation", "Read"))
			return "the clipboard";
		return getClipboardString();
	}
	public List<? extends XOMVariant> toList(XNContext ctx) {
		return Arrays.asList(this);
	}
	public int hashCode() {
		return 0xBEC1126B;
	}
	public boolean equals(Object o) {
		return (o instanceof XOMClipboard);
	}
}
