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
import java.util.*;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.XNSecurityKey;
import com.kreative.openxion.XNScriptError;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.inst.XOMFile;
import com.kreative.openxion.xom.type.XOMFileType;

/**
 * The XOMFileIOManager implements I/O with XOMFiles.
 * @since OpenXION 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XOMFileIOManager implements XNIOManager {
	public static final XOMFileIOManager instance = new XOMFileIOManager();
	
	private Map<Object, XNIOStream> streams = new HashMap<Object, XNIOStream>();
	private Map<Object, XNIOMethod> methods = new HashMap<Object, XNIOMethod>();
	
	private XNIOStream getStream(XNContext ctx, XOMVariant obj) {
		if (streams.containsKey(obj)) {
			return streams.get(obj);
		}
		XOMFile xf = XOMFileType.instance.makeInstanceFrom(ctx, obj);
		if (streams.containsKey(xf)) {
			return streams.get(xf);
		}
		File f = xf.toFile().getAbsoluteFile();
		if (streams.containsKey(f)) {
			return streams.get(f);
		}
		String p = f.getAbsolutePath();
		if (streams.containsKey(p)) {
			return streams.get(p);
		}
		return null;
	}
	
	private XNIOMethod getMethod(XNContext ctx, XOMVariant obj) {
		if (methods.containsKey(obj)) {
			return methods.get(obj);
		}
		XOMFile xf = XOMFileType.instance.makeInstanceFrom(ctx, obj);
		if (methods.containsKey(xf)) {
			return methods.get(xf);
		}
		File f = xf.toFile().getAbsoluteFile();
		if (methods.containsKey(f)) {
			return methods.get(f);
		}
		String p = f.getAbsolutePath();
		if (methods.containsKey(p)) {
			return methods.get(p);
		}
		return null;
	}
	
	public boolean worksWith(XNContext ctx, XOMVariant obj) {
		return XOMFileType.instance.canMakeInstanceFrom(ctx, obj);
	}

	public void open(XNContext ctx, XOMVariant obj) {
		open(ctx, obj, TextIOMethod.instance, "text");
	}

	public void open(XNContext ctx, XOMVariant obj, XNIOMethod method, String type) {
		boolean write;
		if (ctx.allow(XNSecurityKey.FILE_SYSTEM_WRITE)) write = true;
		else if (ctx.allow(XNSecurityKey.FILE_SYSTEM_READ)) write = false;
		else throw new XNScriptError("Security settings do not allow open file");
		try {
			XOMFile xf = XOMFileType.instance.makeInstanceFrom(ctx, obj);
			File f = xf.toFile().getAbsoluteFile();
			String p = f.getAbsolutePath();
			XNIOStream stream;
			if (write) {
				try {
					stream = new XNFileIOStream(f, "rwd");
				} catch (IOException e) {
					stream = new XNFileIOStream(f, "r");
				}
			} else {
				stream = new XNFileIOStream(f, "r");
			}
			streams.put(obj, stream);
			streams.put(xf, stream);
			streams.put(f, stream);
			streams.put(p, stream);
			methods.put(obj, method);
			methods.put(xf, method);
			methods.put(f, method);
			methods.put(p, method);
			method.open(ctx, stream, type);
		} catch (IOException ioe) {
			throw new XNScriptError(ioe, "Failed to open");
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ))
			throw new XNScriptError("Security settings do not allow read from file");
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			return method.read(ctx, stream);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, XOMVariant stop) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ))
			throw new XNScriptError("Security settings do not allow read from file");
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			return method.read(ctx, stream, stop);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, int len) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ))
			throw new XNScriptError("Security settings do not allow read from file");
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			return method.read(ctx, stream, len);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, int len, XOMVariant stop) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ))
			throw new XNScriptError("Security settings do not allow read from file");
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			return method.read(ctx, stream, len, stop);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, long pos) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ))
			throw new XNScriptError("Security settings do not allow read from file");
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			return method.read(ctx, stream, pos);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, long pos, XOMVariant stop) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ))
			throw new XNScriptError("Security settings do not allow read from file");
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			return method.read(ctx, stream, pos, stop);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, long pos, int len) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ))
			throw new XNScriptError("Security settings do not allow read from file");
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			return method.read(ctx, stream, pos, len);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, long pos, int len, XOMVariant stop) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ))
			throw new XNScriptError("Security settings do not allow read from file");
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			return method.read(ctx, stream, pos, len, stop);
		}
	}

	public void write(XNContext ctx, XOMVariant obj, XOMVariant data) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_WRITE))
			throw new XNScriptError("Security settings do not allow write to file");
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			method.write(ctx, stream, data);
		}
	}

	public void write(XNContext ctx, XOMVariant obj, XOMVariant data, long pos) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_WRITE))
			throw new XNScriptError("Security settings do not allow write to file");
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			method.write(ctx, stream, data, pos);
		}
	}

	public void truncate(XNContext ctx, XOMVariant obj) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_WRITE))
			throw new XNScriptError("Security settings do not allow truncate file");
		XNIOStream stream = getStream(ctx, obj);
		XNIOMethod method = getMethod(ctx, obj);
		if (stream == null || method == null) {
			throw new XNScriptError("File not open");
		} else {
			method.truncate(ctx, stream);
		}
	}

	public void truncate(XNContext ctx, XOMVariant obj, long pos) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_WRITE))
			throw new XNScriptError("Security settings do not allow truncate file");
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
