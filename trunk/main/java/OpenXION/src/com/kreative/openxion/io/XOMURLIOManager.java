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
import com.kreative.openxion.XNContext;
import com.kreative.openxion.XNSecurityKey;
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
	
	private XNIOStreamInfoMap sim = new XNIOStreamInfoMap();
	
	public boolean worksWith(XNContext ctx, XOMVariant obj) {
		return XOMURLType.instance.canMakeInstanceFrom(ctx, obj);
	}

	public void open(XNContext ctx, XOMVariant obj) {
		if (!ctx.allow(XNSecurityKey.BROWSER_LAUNCH, "Operation", "Open", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow open URL");
		try {
			XOMURL xu = XOMURLType.instance.makeInstanceFrom(ctx, obj);
			URL u = xu.toURL();
			String p = u.toString();
			BareBonesBrowserLaunch.openURL(p);
		} catch (Exception e) {
			throw new XNScriptError(e, "Can't open that URL");
		}
	}

	public void open(XNContext ctx, XOMVariant obj, XNIOMethod method, String type) {
		if (!ctx.allow(XNSecurityKey.INTERNET_ACCESS, "Operation", "Open", "Object", obj.toDescriptionString(), "Method", method.getClass().getSimpleName(), "Type", type))
			throw new XNScriptError("Security settings do not allow open URL");
		try {
			XOMURL xu = XOMURLType.instance.makeInstanceFrom(ctx, obj);
			URL u = xu.toURL();
			String p = u.toString();
			if (sim.getURLStreamInfo(ctx, obj) != null) {
				throw new XNScriptError("URL \""+p+"\" is already open");
			} else {
				URLConnection uc = u.openConnection();
				XNIOStream stream = new XNURLConnectionIOStream(uc);
				XNIOStreamInfo si = new XNIOStreamInfo(stream, method);
				sim.setURLStreamInfo(ctx, obj, si);
				si.open(ctx, type);
			}
		} catch (IOException ioe) {
			throw new XNScriptError(ioe, "Can't open that URL");
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj) {
		if (!ctx.allow(XNSecurityKey.INTERNET_ACCESS, "Operation", "Read", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow read from URL");
		XNIOStreamInfo si = sim.getURLStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("URL not open");
		} else {
			return si.read(ctx);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, XOMVariant stop) {
		if (!ctx.allow(XNSecurityKey.INTERNET_ACCESS, "Operation", "Read", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow read from URL");
		XNIOStreamInfo si = sim.getURLStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("URL not open");
		} else {
			return si.read(ctx, stop);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, int len) {
		if (!ctx.allow(XNSecurityKey.INTERNET_ACCESS, "Operation", "Read", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow read from URL");
		XNIOStreamInfo si = sim.getURLStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("URL not open");
		} else {
			return si.read(ctx, len);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, int len, XOMVariant stop) {
		if (!ctx.allow(XNSecurityKey.INTERNET_ACCESS, "Operation", "Read", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow read from URL");
		XNIOStreamInfo si = sim.getURLStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("URL not open");
		} else {
			return si.read(ctx, len, stop);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, long pos) {
		if (!ctx.allow(XNSecurityKey.INTERNET_ACCESS, "Operation", "Read", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow read from URL");
		XNIOStreamInfo si = sim.getURLStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("URL not open");
		} else {
			return si.read(ctx, pos);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, long pos, XOMVariant stop) {
		if (!ctx.allow(XNSecurityKey.INTERNET_ACCESS, "Operation", "Read", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow read from URL");
		XNIOStreamInfo si = sim.getURLStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("URL not open");
		} else {
			return si.read(ctx, pos, stop);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, long pos, int len) {
		if (!ctx.allow(XNSecurityKey.INTERNET_ACCESS, "Operation", "Read", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow read from URL");
		XNIOStreamInfo si = sim.getURLStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("URL not open");
		} else {
			return si.read(ctx, pos, len);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, long pos, int len, XOMVariant stop) {
		if (!ctx.allow(XNSecurityKey.INTERNET_ACCESS, "Operation", "Read", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow read from URL");
		XNIOStreamInfo si = sim.getURLStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("URL not open");
		} else {
			return si.read(ctx, pos, len, stop);
		}
	}

	public void write(XNContext ctx, XOMVariant obj, XOMVariant data) {
		if (!ctx.allow(XNSecurityKey.INTERNET_ACCESS, "Operation", "Write", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow write to URL");
		XNIOStreamInfo si = sim.getURLStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("URL not open");
		} else {
			si.write(ctx, data);
		}
	}

	public void write(XNContext ctx, XOMVariant obj, XOMVariant data, long pos) {
		if (!ctx.allow(XNSecurityKey.INTERNET_ACCESS, "Operation", "Write", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow write to URL");
		XNIOStreamInfo si = sim.getURLStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("URL not open");
		} else {
			si.write(ctx, data, pos);
		}
	}

	public void truncate(XNContext ctx, XOMVariant obj) {
		if (!ctx.allow(XNSecurityKey.INTERNET_ACCESS, "Operation", "Truncate", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow truncate URL");
		XNIOStreamInfo si = sim.getURLStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("URL not open");
		} else {
			si.truncate(ctx);
		}
	}

	public void truncate(XNContext ctx, XOMVariant obj, long pos) {
		if (!ctx.allow(XNSecurityKey.INTERNET_ACCESS, "Operation", "Truncate", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow truncate URL");
		XNIOStreamInfo si = sim.getURLStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("URL not open");
		} else {
			si.truncate(ctx, pos);
		}
	}

	public void close(XNContext ctx, XOMVariant obj) {
		XNIOStreamInfo si = sim.getURLStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("URL not open");
		} else {
			si.close(ctx);
			sim.removeStreamInfo(si);
		}
	}
}
