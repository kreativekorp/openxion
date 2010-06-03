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

package com.kreative.openxion.xom;

import java.util.*;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.ast.XNModifier;
import com.kreative.openxion.xom.inst.XOMString;
import com.kreative.openxion.xom.inst.XOMBinary;
import com.kreative.openxion.xom.inst.XOMList;
import com.kreative.openxion.xom.type.XOMListType;
import com.kreative.openxion.xom.type.XOMBinaryType;

/**
 * XOMVariable represents a variable in a XION program.
 * It is specially handled separately from other kinds of XOMVariants.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XOMVariable extends XOMVariant implements XOMStringContainer, XOMBinaryContainer, XOMListContainer {
	private static final long serialVersionUID = 1L;
	
	private XOMDataType<? extends XOMVariant> type;
	private XOMVariant value;
	
	public XOMVariable(XNContext ctx, XOMDataType<? extends XOMVariant> type, XOMVariant value) {
		this.type = type;
		this.value = type.makeInstanceFrom(ctx, value);
	}
	
	public XOMVariant unwrap() {
		return value.unwrap();
	}
	
	public boolean hasParent(XNContext ctx) {
		return value.hasParent(ctx);
	}
	public XOMVariant getParent(XNContext ctx) {
		return value.getParent(ctx);
	}
	
	public boolean canDelete(XNContext ctx) {
		return value.canDelete(ctx);
	}
	public void delete(XNContext ctx) {
		value.delete(ctx);
	}
	
	public boolean canGetContents(XNContext ctx) {
		return true;
	}
	public XOMVariant getContents(XNContext ctx) {
		return value;
	}
	
	public boolean canPutContents(XNContext ctx) {
		return true;
	}
	public void putIntoContents(XNContext ctx, XOMVariant contents) {
		value = type.makeInstanceFrom(ctx, contents);
	}
	public void putBeforeContents(XNContext ctx, XOMVariant contents) {
		value = type.makeInstanceFrom(ctx, contents, value);
	}
	public void putAfterContents(XNContext ctx, XOMVariant contents) {
		value = type.makeInstanceFrom(ctx, value, contents);
	}
	public void putIntoContents(XNContext ctx, XOMVariant contents, String property, XOMVariant pvalue) {
		XOMVariant newValue = type.makeInstanceFrom(ctx, contents);
		newValue.setProperty(ctx, property, pvalue);
		value = newValue;
	}
	public void putBeforeContents(XNContext ctx, XOMVariant contents, String property, XOMVariant pvalue) {
		XOMVariant newValue = type.makeInstanceFrom(ctx, contents);
		newValue.setProperty(ctx, property, pvalue);
		value = type.makeInstanceFrom(ctx, newValue, value);
	}
	public void putAfterContents(XNContext ctx, XOMVariant contents, String property, XOMVariant pvalue) {
		XOMVariant newValue = type.makeInstanceFrom(ctx, contents);
		newValue.setProperty(ctx, property, pvalue);
		value = type.makeInstanceFrom(ctx, value, newValue);
	}
	
	public boolean canSortContents(XNContext ctx) {
		return true;
	}
	public void sortContents(XNContext ctx, XOMComparator cmp) {
		List<XOMVariant> toSort = new Vector<XOMVariant>();
		if (type instanceof XOMListType) {
			List<XOMVariant> vars = value.toList(ctx);
			toSort.addAll(vars);
		} else if (type instanceof XOMBinaryType) {
			byte[] bb = ((XOMBinaryType)type).makeInstanceFrom(ctx, value).toByteArray();
			for (byte b : bb) toSort.add(new XOMBinary(new byte[]{b}));
		} else {
			String[] strs = value.toTextString(ctx).split("\r\n|\r|\n|\u2028|\u2029");
			for (String str : strs) toSort.add(new XOMString(str));
		}
		Collections.sort(toSort, cmp);
		if (type instanceof XOMListType) {
			value = type.makeInstanceFrom(ctx, new XOMList(toSort));
		} else if (type instanceof XOMBinaryType) {
			byte[] bb = new byte[toSort.size()];
			for (int i = 0; i < toSort.size(); i++) bb[i] = ((XOMBinary)toSort.get(i)).toByteArray()[0];
			value = type.makeInstanceFrom(ctx, new XOMBinary(bb));
		} else {
			StringBuffer s = new StringBuffer();
			for (XOMVariant v : toSort) s.append(v.toTextString(ctx) + ctx.getLineEnding());
			if (s.length() > 0 && s.substring(s.length()-ctx.getLineEnding().length()).equals(ctx.getLineEnding()))
				s.delete(s.length()-ctx.getLineEnding().length(), s.length());
			value = type.makeInstanceFrom(ctx, new XOMString(s.toString()));
		}
	}
	
	public boolean canGetProperty(XNContext ctx, String property) {
		return value.canGetProperty(ctx, property);
	}
	public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String property) {
		return value.getProperty(ctx, modifier, property);
	}
	public boolean canSetProperty(XNContext ctx, String property) {
		return value.canSetProperty(ctx, property);
	}
	public void setProperty(XNContext ctx, String property, XOMVariant pvalue) {
		value.setProperty(ctx, property, pvalue);
	}
	
	public boolean canGetStringProperty(XNContext ctx, String property) {
		return (value instanceof XOMStringContainer) && ((XOMStringContainer)value).canGetStringProperty(ctx, property);
	}
	public XOMVariant getStringProperty(XNContext ctx, XNModifier modifier, String property, int s, int e) {
		if (value instanceof XOMStringContainer) return ((XOMStringContainer)value).getStringProperty(ctx, modifier, property, s, e);
		else return super.getProperty(ctx, modifier, property);
	}
	public boolean canSetStringProperty(XNContext ctx, String property) {
		return (value instanceof XOMStringContainer) && ((XOMStringContainer)value).canSetStringProperty(ctx, property);
	}
	public void setStringProperty(XNContext ctx, String property, int s, int e, XOMVariant pvalue) {
		if (value instanceof XOMStringContainer) ((XOMStringContainer)value).setStringProperty(ctx, property, s, e, pvalue);
		else super.setProperty(ctx, property, pvalue);
	}
	
	public boolean canGetListProperty(XNContext ctx, String property) {
		return (value instanceof XOMListContainer) && ((XOMListContainer)value).canGetListProperty(ctx, property);
	}
	public XOMVariant getListProperty(XNContext ctx, XNModifier modifier, String property, int s, int e) {
		if (value instanceof XOMListContainer) return ((XOMListContainer)value).getListProperty(ctx, modifier, property, s, e);
		else return super.getProperty(ctx, modifier, property);
	}
	public boolean canSetListProperty(XNContext ctx, String property) {
		return (value instanceof XOMListContainer) && ((XOMListContainer)value).canSetListProperty(ctx, property);
	}
	public void setListProperty(XNContext ctx, String property, int s, int e, XOMVariant pvalue) {
		if (value instanceof XOMListContainer) ((XOMListContainer)value).setListProperty(ctx, property, s, e, pvalue);
		else super.setProperty(ctx, property, pvalue);
	}
	
	public boolean canGetBinaryProperty(XNContext ctx, String property) {
		return (value instanceof XOMBinaryContainer) && ((XOMBinaryContainer)value).canGetBinaryProperty(ctx, property);
	}
	public XOMVariant getBinaryProperty(XNContext ctx, XNModifier modifier, String property, int s, int e) {
		if (value instanceof XOMBinaryContainer) return ((XOMBinaryContainer)value).getBinaryProperty(ctx, modifier, property, s, e);
		else return super.getProperty(ctx, modifier, property);
	}
	public boolean canSetBinaryProperty(XNContext ctx, String property) {
		return (value instanceof XOMBinaryContainer) && ((XOMBinaryContainer)value).canSetBinaryProperty(ctx, property);
	}
	public void setBinaryProperty(XNContext ctx, String property, int s, int e, XOMVariant pvalue) {
		if (value instanceof XOMBinaryContainer) ((XOMBinaryContainer)value).setBinaryProperty(ctx, property, s, e, pvalue);
		else super.setProperty(ctx, property, pvalue);
	}
	
	public boolean equalsImpl(Object o) {
		return value.equalsImpl(o);
	}
	public int hashCode() {
		return value.hashCode();
	}
	public String toDescriptionString() {
		return value.toDescriptionString();
	}
	public String toTextString(XNContext ctx) {
		return value.toTextString(ctx);
	}
	public List<XOMVariant> toList(XNContext ctx) {
		return value.toList(ctx);
	}
	
	/*
	 * Returning false for these should make XOM*Chunk use getContents
	 * and putIntoContents instead of these. It makes things harder for them
	 * but easier for us. :)
	 */

	public boolean canDeleteString(XNContext ctx) {
		return false;
	}

	public boolean canGetString(XNContext ctx) {
		return false;
	}

	public boolean canPutString(XNContext ctx) {
		return false;
	}
	
	public boolean canRearrangeString(XNContext ctx) {
		return false;
	}

	public void deleteString(XNContext ctx, int startCharIndex, int endCharIndex) {
		// nothing
	}

	public XOMVariant getString(XNContext ctx, int startCharIndex, int endCharIndex) {
		return null;
	}

	public void putAfterString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents) {
		// nothing
	}

	public void putBeforeString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents) {
		// nothing
	}

	public void putIntoString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents) {
		// nothing
	}

	public void putAfterString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents, String property, XOMVariant pvalue) {
		// nothing
	}

	public void putBeforeString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents, String property, XOMVariant pvalue) {
		// nothing
	}

	public void putIntoString(XNContext ctx, int startCharIndex, int endCharIndex, XOMVariant contents, String property, XOMVariant pvalue) {
		// nothing
	}
	
	public void rearrangeString(XNContext ctx, int[] startIndexes, int[] endIndexes) {
		// nothing
	}

	public boolean canDeleteBinary(XNContext ctx) {
		return false;
	}

	public boolean canGetBinary(XNContext ctx) {
		return false;
	}

	public boolean canPutBinary(XNContext ctx) {
		return false;
	}
	
	public boolean canSortBinary(XNContext ctx) {
		return false;
	}

	public void deleteBinary(XNContext ctx, int startByteIndex, int endByteIndex) {
		// nothing
	}

	public XOMVariant getBinary(XNContext ctx, int startByteIndex, int endByteIndex) {
		return null;
	}

	public void putAfterBinary(XNContext ctx, int startByteIndex, int endByteIndex, XOMVariant contents) {
		// nothing
	}

	public void putBeforeBinary(XNContext ctx, int startByteIndex, int endByteIndex, XOMVariant contents) {
		// nothing
	}

	public void putIntoBinary(XNContext ctx, int startByteIndex, int endByteIndex, XOMVariant contents) {
		// nothing
	}

	public void putAfterBinary(XNContext ctx, int startByteIndex, int endByteIndex, XOMVariant contents, String property, XOMVariant pvalue) {
		// nothing
	}

	public void putBeforeBinary(XNContext ctx, int startByteIndex, int endByteIndex, XOMVariant contents, String property, XOMVariant pvalue) {
		// nothing
	}

	public void putIntoBinary(XNContext ctx, int startByteIndex, int endByteIndex, XOMVariant contents, String property, XOMVariant pvalue) {
		// nothing
	}
	
	public void sortBinary(XNContext ctx, int startByteIndex, int endByteIndex, XOMComparator cmp) {
		// nothing
	}

	public boolean canDeleteList(XNContext ctx) {
		return false;
	}

	public boolean canGetList(XNContext ctx) {
		return false;
	}

	public boolean canPutList(XNContext ctx) {
		return false;
	}
	
	public boolean canSortList(XNContext ctx) {
		return false;
	}

	public void deleteList(XNContext ctx, int startElementIndex, int endElementIndex) {
		// nothing
	}

	public XOMVariant getList(XNContext ctx, int startElementIndex, int endElementIndex) {
		return null;
	}

	public void putAfterList(XNContext ctx, int startElementIndex, int endElementIndex, XOMVariant contents) {
		// nothing
	}

	public void putBeforeList(XNContext ctx, int startElementIndex, int endElementIndex, XOMVariant contents) {
		// nothing
	}

	public void putIntoList(XNContext ctx, int startElementIndex, int endElementIndex, XOMVariant contents) {
		// nothing
	}

	public void putAfterList(XNContext ctx, int startElementIndex, int endElementIndex, XOMVariant contents, String property, XOMVariant pvalue) {
		// nothing
	}

	public void putBeforeList(XNContext ctx, int startElementIndex, int endElementIndex, XOMVariant contents, String property, XOMVariant pvalue) {
		// nothing
	}

	public void putIntoList(XNContext ctx, int startElementIndex, int endElementIndex, XOMVariant contents, String property, XOMVariant pvalue) {
		// nothing
	}
	
	public void sortList(XNContext ctx, int startElementIndex, int endElementIndex, XOMComparator cmp) {
		// nothing
	}
}
