/*
 * Copyright &copy; 2010-2011 Rebecca G. Bettencourt / Kreative Software
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

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.inst.XOMFile;
import com.kreative.openxion.xom.inst.XOMURL;
import com.kreative.openxion.xom.type.XOMFileType;
import com.kreative.openxion.xom.type.XOMURLType;

public class XNIOStreamInfoMap {
	private Map<Object, XNIOStreamInfo> streamInfo = new HashMap<Object, XNIOStreamInfo>();
	
	public XNIOStreamInfo getFileStreamInfo(XNContext ctx, XOMVariant obj) {
		if (streamInfo.containsKey(obj)) return streamInfo.get(obj);
		XOMFile xf = XOMFileType.instance.makeInstanceFrom(ctx, obj);
		if (streamInfo.containsKey(xf)) return streamInfo.get(xf);
		File f = xf.toFile().getAbsoluteFile();
		if (streamInfo.containsKey(f)) return streamInfo.get(f);
		String p = f.getAbsolutePath();
		if (streamInfo.containsKey(p)) return streamInfo.get(p);
		return null;
	}
	
	public XNIOStreamInfo getURLStreamInfo(XNContext ctx, XOMVariant obj) {
		if (streamInfo.containsKey(obj)) return streamInfo.get(obj);
		XOMURL xu = XOMURLType.instance.makeInstanceFrom(ctx, obj);
		if (streamInfo.containsKey(xu)) return streamInfo.get(xu);
		URL u = xu.toURL();
		if (streamInfo.containsKey(u)) return streamInfo.get(u);
		String p = u.toString();
		if (streamInfo.containsKey(p)) return streamInfo.get(p);
		return null;
	}
	
	public void setFileStreamInfo(XNContext ctx, XOMVariant obj, XNIOStreamInfo si) {
		streamInfo.put(obj, si);
		XOMFile xf = XOMFileType.instance.makeInstanceFrom(ctx, obj);
		streamInfo.put(xf, si);
		File f = xf.toFile().getAbsoluteFile();
		streamInfo.put(f, si);
		String p = f.getAbsolutePath();
		streamInfo.put(p, si);
	}
	
	public void setURLStreamInfo(XNContext ctx, XOMVariant obj, XNIOStreamInfo si) {
		streamInfo.put(obj, si);
		XOMURL xu = XOMURLType.instance.makeInstanceFrom(ctx, obj);
		streamInfo.put(xu, si);
		URL u = xu.toURL();
		streamInfo.put(u, si);
		String p = u.toString();
		streamInfo.put(p, si);
	}
	
	public void removeStreamInfo(XNIOStreamInfo si) {
		List<Object> keys = new Vector<Object>();
		for (Map.Entry<Object,XNIOStreamInfo> e : streamInfo.entrySet()) {
			if (e.getValue() == si) {
				keys.add(e.getKey());
			}
		}
		for (Object o : keys) {
			streamInfo.remove(o);
		}
	}
}
