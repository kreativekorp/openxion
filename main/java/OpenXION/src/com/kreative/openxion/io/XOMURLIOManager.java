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

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.XNScriptError;
import com.kreative.openxion.util.BareBonesBrowserLaunch;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.inst.XOMURL;
import com.kreative.openxion.xom.type.XOMURLType;

/**
 * The XOMURLIOManager implements I/O with XOMURLs.
 * @since OpenXION 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XOMURLIOManager implements XNIOManager {
	public static final XOMURLIOManager instance = new XOMURLIOManager();
	
	private Map<Object, XNIOStream> streams = new HashMap<Object, XNIOStream>();
	private Map<Object, XNIOMethod> methods = new HashMap<Object, XNIOMethod>();
	
	private XNIOStream getStream(XNContext ctx, XOMVariant obj) {
		if (streams.containsKey(obj)) {
			return streams.get(obj);
		}
		XOMURL xu = XOMURLType.instance.makeInstanceFrom(ctx, obj);
		if (streams.containsKey(xu)) {
			return streams.get(xu);
		}
		URL u = xu.toURL();
		if (streams.containsKey(u)) {
			return streams.get(u);
		}
		String p = u.toString();
		if (streams.containsKey(p)) {
			return streams.get(p);
		}
		return null;
	}
	
	private XNIOMethod getMethod(XNContext ctx, XOMVariant obj) {
		if (methods.containsKey(obj)) {
			return methods.get(obj);
		}
		XOMURL xu = XOMURLType.instance.makeInstanceFrom(ctx, obj);
		if (methods.containsKey(xu)) {
			return methods.get(xu);
		}
		URL u = xu.toURL();
		if (methods.containsKey(u)) {
			return methods.get(u);
		}
		String p = u.toString();
		if (methods.containsKey(p)) {
			return methods.get(p);
		}
		return null;
	}
	
	public boolean worksWith(XNContext ctx, XOMVariant obj) {
		return XOMURLType.instance.canMakeInstanceFrom(ctx, obj);
	}

	public void open(XNContext ctx, XOMVariant obj) {
		try {
			XOMURL xu = XOMURLType.instance.makeInstanceFrom(ctx, obj);
			URL u = xu.toURL();
			String p = u.toString();
			BareBonesBrowserLaunch.openURL(p);
		} catch (Exception e) {
			throw new XNScriptError(e, "Failed to open");
		}
	}

	public void open(XNContext ctx, XOMVariant obj, XNIOMethod method, String type) {
		try {
			XOMURL xu = XOMURLType.instance.makeInstanceFrom(ctx, obj);
			URL u = xu.toURL();
			URLConnection uc = u.openConnection();
			XNIOStream stream = new XNURLConnectionIOStream(uc);
			streams.put(obj, stream);
			streams.put(xu, stream);
			streams.put(u, stream);
			streams.put(u.toString(), stream);
			methods.put(obj, method);
			methods.put(xu, method);
			methods.put(u, method);
			methods.put(u.toString(), method);
			method.open(ctx, stream, type);
		} catch (IOException ioe) {
			throw new XNScriptError(ioe, "Failed to open");
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj) {
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			return method.read(ctx, stream);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, XOMVariant stop) {
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			return method.read(ctx, stream, stop);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, int len) {
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			return method.read(ctx, stream, len);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, int len, XOMVariant stop) {
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			return method.read(ctx, stream, len, stop);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, long pos) {
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			return method.read(ctx, stream, pos);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, long pos, XOMVariant stop) {
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			return method.read(ctx, stream, pos, stop);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, long pos, int len) {
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			return method.read(ctx, stream, pos, len);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, long pos, int len, XOMVariant stop) {
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			return method.read(ctx, stream, pos, len, stop);
		}
	}

	public void write(XNContext ctx, XOMVariant obj, XOMVariant data) {
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			method.write(ctx, stream, data);
		}
	}

	public void write(XNContext ctx, XOMVariant obj, XOMVariant data, long pos) {
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			method.write(ctx, stream, data, pos);
		}
	}

	public void truncate(XNContext ctx, XOMVariant obj) {
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			method.truncate(ctx, stream);
		}
	}

	public void truncate(XNContext ctx, XOMVariant obj, long pos) {
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			method.truncate(ctx, stream, pos);
		}
	}

	public void close(XNContext ctx, XOMVariant obj) {
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			method.close(ctx, stream);
			List<Object> keys = new Vector<Object>();
			for (Map.Entry<Object,XNIOStream> e : streams.entrySet()) {
				if (e.getValue() == stream) {
					keys.add(e.getKey());
				}
			}
			for (Object o : keys) {
				streams.remove(o);
				methods.remove(o);
			}
			try {
				stream.close();
			} catch (IOException ioe) {}
		}
	}
}
