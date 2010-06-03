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
 * @since OpenXION 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.io;

import com.kreative.openxion.XNContext;
import com.kreative.openxion.xom.XOMVariant;

/**
 * An XNIOMethod is responsible for managing input and output for an
 * XNIOManager for a specific method as specified by the as keyword.
 * @since OpenXION 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public interface XNIOMethod {
	public boolean worksWith(String type);
	public void open(XNContext ctx, XNIOStream stream, String type);
	public XOMVariant read(XNContext ctx, XNIOStream stream);
	public XOMVariant read(XNContext ctx, XNIOStream stream, XOMVariant stop);
	public XOMVariant read(XNContext ctx, XNIOStream stream, int len);
	public XOMVariant read(XNContext ctx, XNIOStream stream, int len, XOMVariant stop);
	public XOMVariant read(XNContext ctx, XNIOStream stream, long pos);
	public XOMVariant read(XNContext ctx, XNIOStream stream, long pos, XOMVariant stop);
	public XOMVariant read(XNContext ctx, XNIOStream stream, long pos, int len);
	public XOMVariant read(XNContext ctx, XNIOStream stream, long pos, int len, XOMVariant stop);
	public void write(XNContext ctx, XNIOStream stream, XOMVariant data);
	public void write(XNContext ctx, XNIOStream stream, XOMVariant data, long pos);
	public void truncate(XNContext ctx, XNIOStream stream);
	public void truncate(XNContext ctx, XNIOStream stream, long pos);
	public void close(XNContext ctx, XNIOStream stream);
}
