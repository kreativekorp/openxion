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
 * @since OpenXION 1.1
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion;

import java.io.*;
import java.util.*;

/**
 * An XNSecurityProfile controls the current security settings for an XION script.
 * @since OpenXION 1.1
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XNSecurityProfile extends HashMap<XNSecurityKey, XNSecurityValue> {
	private static final long serialVersionUID = 1L;
	
	public XNSecurityProfile() {
		super();
	}
	
	public XNSecurityProfile(XNSecurityValue defaultSetting) {
		super();
		for (XNSecurityKey k : XNSecurityKey.values()) {
			this.put(k, defaultSetting);
		}
	}
	
	public XNSecurityProfile(Map<XNSecurityKey, XNSecurityValue> profile) {
		super();
		this.putAll(profile);
	}
	
	public XNSecurityProfile(XNSecurityValue defaultSetting, Map<XNSecurityKey, XNSecurityValue> profile) {
		super();
		for (XNSecurityKey k : XNSecurityKey.values()) {
			this.put(k, defaultSetting);
		}
		this.putAll(profile);
	}
	
	public XNSecurityProfile read(Scanner scanner) {
		while (scanner.hasNextLine()) {
			String s = scanner.nextLine().trim();
			if (!s.startsWith("#") && s.contains("=")) {
				String[] ss = s.split("=", 2);
				XNSecurityKey k = XNSecurityKey.valueOf(ss[0].trim());
				XNSecurityValue v = XNSecurityValue.valueOf(ss[1].trim());
				if (k != null && v != null) {
					this.put(k, v);
				}
			}
		}
		return this;
	}
	
	public XNSecurityProfile write(PrintWriter printer) {
		for (Map.Entry<XNSecurityKey, XNSecurityValue> e : this.entrySet()) {
			printer.println(e.getKey().name() + "=" + e.getValue().name());
		}
		return this;
	}
}
