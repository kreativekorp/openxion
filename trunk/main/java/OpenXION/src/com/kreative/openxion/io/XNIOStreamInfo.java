/*
 * Copyright &copy; 2010 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.openxion.io;

import java.io.IOException;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.xom.XOMVariant;

public class XNIOStreamInfo {
	private XNIOStream stream;
	private XNIOMethod method;
	private boolean justOpened;
	
	public XNIOStreamInfo(XNIOStream stream, XNIOMethod method) {
		this.stream = stream;
		this.method = method;
		this.justOpened = true;
	}
	
	public boolean justOpened() {
		return justOpened;
	}
	
	public void open(XNContext ctx, String type) {
		justOpened = true;
		method.open(ctx, stream, type);
	}
	
	public XOMVariant read(XNContext ctx) {
		justOpened = false;
		return method.read(ctx, stream);
	}

	public XOMVariant read(XNContext ctx, XOMVariant stop) {
		justOpened = false;
		return method.read(ctx, stream, stop);
	}

	public XOMVariant read(XNContext ctx, int len) {
		justOpened = false;
		return method.read(ctx, stream, len);
	}

	public XOMVariant read(XNContext ctx, int len, XOMVariant stop) {
		justOpened = false;
		return method.read(ctx, stream, len, stop);
	}

	public XOMVariant read(XNContext ctx, long pos) {
		justOpened = false;
		return method.read(ctx, stream, pos);
	}

	public XOMVariant read(XNContext ctx, long pos, XOMVariant stop) {
		justOpened = false;
		return method.read(ctx, stream, pos, stop);
	}

	public XOMVariant read(XNContext ctx, long pos, int len) {
		justOpened = false;
		return method.read(ctx, stream, pos, len);
	}

	public XOMVariant read(XNContext ctx, long pos, int len, XOMVariant stop) {
		justOpened = false;
		return method.read(ctx, stream, pos, len, stop);
	}

	public void write(XNContext ctx, XOMVariant data) {
		justOpened = false;
		method.write(ctx, stream, data);
	}

	public void write(XNContext ctx, XOMVariant data, long pos) {
		justOpened = false;
		method.write(ctx, stream, data, pos);
	}

	public void truncate(XNContext ctx) {
		justOpened = false;
		method.truncate(ctx, stream);
	}

	public void truncate(XNContext ctx, long pos) {
		justOpened = false;
		method.truncate(ctx, stream, pos);
	}
	
	public void close(XNContext ctx) {
		justOpened = false;
		method.close(ctx, stream);
		try { stream.close(); } catch (IOException ioe) {}
	}
}
