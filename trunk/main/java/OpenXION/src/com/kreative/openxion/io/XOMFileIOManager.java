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
 * @since OpenXION 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.io;

import java.io.*;
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
	
	private XNIOStreamInfoMap sim = new XNIOStreamInfoMap();
	
	public boolean worksWith(XNContext ctx, XOMVariant obj) {
		return XOMFileType.instance.canMakeInstanceFrom(ctx, obj);
	}

	public void open(XNContext ctx, XOMVariant obj) {
		open(ctx, obj, TextIOMethod.instance, "text");
	}

	public void open(XNContext ctx, XOMVariant obj, XNIOMethod method, String type) {
		boolean write;
		if (ctx.allow(XNSecurityKey.FILE_SYSTEM_WRITE, "Operation", "Open", "Object", obj.toDescriptionString(), "Method", method.getClass().getSimpleName(), "Type", type)) write = true;
		else if (ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "Open", "Object", obj.toDescriptionString(), "Method", method.getClass().getSimpleName(), "Type", type)) write = false;
		else throw new XNScriptError("Security settings do not allow open file");
		try {
			XOMFile xf = XOMFileType.instance.makeInstanceFrom(ctx, obj);
			File f = xf.toFile().getAbsoluteFile();
			String p = f.getAbsolutePath();
			if (sim.getFileStreamInfo(ctx, obj) != null) {
				throw new XNScriptError("File \""+p+"\" is already open");
			} else {
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
				XNIOStreamInfo si = new XNIOStreamInfo(stream, method);
				sim.setFileStreamInfo(ctx, obj, si);
				si.open(ctx, type);
			}
		} catch (IOException ioe) {
			throw new XNScriptError(ioe, "Can't create that file");
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "Read", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow read from file");
		XNIOStreamInfo si = sim.getFileStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("File not open");
		} else {
			return si.read(ctx);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, XOMVariant stop) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "Read", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow read from file");
		XNIOStreamInfo si = sim.getFileStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("File not open");
		} else {
			return si.read(ctx, stop);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, int len) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "Read", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow read from file");
		XNIOStreamInfo si = sim.getFileStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("File not open");
		} else {
			return si.read(ctx, len);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, int len, XOMVariant stop) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "Read", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow read from file");
		XNIOStreamInfo si = sim.getFileStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("File not open");
		} else {
			return si.read(ctx, len, stop);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, long pos) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "Read", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow read from file");
		XNIOStreamInfo si = sim.getFileStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("File not open");
		} else {
			return si.read(ctx, pos);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, long pos, XOMVariant stop) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "Read", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow read from file");
		XNIOStreamInfo si = sim.getFileStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("File not open");
		} else {
			return si.read(ctx, pos, stop);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, long pos, int len) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "Read", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow read from file");
		XNIOStreamInfo si = sim.getFileStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("File not open");
		} else {
			return si.read(ctx, pos, len);
		}
	}

	public XOMVariant read(XNContext ctx, XOMVariant obj, long pos, int len, XOMVariant stop) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_READ, "Operation", "Read", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow read from file");
		XNIOStreamInfo si = sim.getFileStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("File not open");
		} else {
			return si.read(ctx, pos, len, stop);
		}
	}

	public void write(XNContext ctx, XOMVariant obj, XOMVariant data) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_WRITE, "Operation", "Write", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow write to file");
		XNIOStreamInfo si = sim.getFileStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("File not open");
		} else {
			if (si.justOpened()) si.truncate(ctx);
			si.write(ctx, data);
		}
	}

	public void write(XNContext ctx, XOMVariant obj, XOMVariant data, long pos) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_WRITE, "Operation", "Write", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow write to file");
		XNIOStreamInfo si = sim.getFileStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("File not open");
		} else {
			si.write(ctx, data, pos);
		}
	}

	public void truncate(XNContext ctx, XOMVariant obj) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_WRITE, "Operation", "Truncate", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow truncate file");
		XNIOStreamInfo si = sim.getFileStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("File not open");
		} else {
			si.truncate(ctx);
		}
	}

	public void truncate(XNContext ctx, XOMVariant obj, long pos) {
		if (!ctx.allow(XNSecurityKey.FILE_SYSTEM_WRITE, "Operation", "Truncate", "Object", obj.toDescriptionString()))
			throw new XNScriptError("Security settings do not allow truncate file");
		XNIOStreamInfo si = sim.getFileStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("File not open");
		} else {
			si.truncate(ctx, pos);
		}
	}

	public void close(XNContext ctx, XOMVariant obj) {
		XNIOStreamInfo si = sim.getFileStreamInfo(ctx, obj);
		if (si == null) {
			throw new XNScriptError("File not open");
		} else {
			si.close(ctx);
			sim.removeStreamInfo(si);
		}
	}
}
