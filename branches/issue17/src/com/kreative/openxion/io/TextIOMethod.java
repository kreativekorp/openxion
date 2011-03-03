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
import java.util.*;
import com.kreative.openxion.XNContext;
import com.kreative.openxion.XNScriptError;
import com.kreative.openxion.util.XIONUtil;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.inst.XOMString;

/**
 * The TextIOMethod implements I/O using strings.
 * It can work with any text encoding supported by Java.
 * @since OpenXION 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class TextIOMethod implements XNIOMethod {
	public static final TextIOMethod instance = new TextIOMethod();
	
	private Map<XNIOStream, String> encodings = new HashMap<XNIOStream, String>();
	
	public boolean worksWith(String type) {
		if (type.trim().toLowerCase().equalsIgnoreCase("text")) return true;
		else try {
			new String(new byte[0], type.trim());
			return true;
		} catch (UnsupportedEncodingException uee) {
			return false;
		}
	}
	
	public void open(XNContext ctx, XNIOStream stream, String type) {
		type = type.trim();
		if (type.equalsIgnoreCase("text")) {
			type = ctx.getTextEncoding().trim();
		}
		encodings.put(stream, type);
	}

	public XOMVariant read(XNContext ctx, XNIOStream stream) {
		try {
			String enc =
				encodings.containsKey(stream) ?
					encodings.get(stream) :
						ctx.getTextEncoding();
			ByteArrayOutputStream tmp = new ByteArrayOutputStream();
			while (true) {
				String s = new String(tmp.toByteArray(), enc);
				if (s.endsWith("\r")) {
					int l = stream.lookahead();
					if (l >= 0) {
						ByteArrayOutputStream tmp2 = new ByteArrayOutputStream();
						tmp2.write(tmp.toByteArray());
						tmp2.write(l);
						String s2 = new String(tmp2.toByteArray(), enc);
						if (s2.endsWith("\r\n")) {
							int a = stream.read();
							if (a >= 0) tmp.write(a);
							break;
						} else {
							break;
						}
					} else {
						break;
					}
				}
				else if (s.endsWith("\n") || s.endsWith("\u2028") || s.endsWith("\u2029")) {
					break;
				}
				else {
					int a = stream.read();
					if (a < 0) break;
					else tmp.write(a);
				}
			}
			return new XOMString(new String(tmp.toByteArray(), enc));
		} catch (IOException e) {
			throw new XNScriptError(e, "Failed to read");
		}
	}

	public XOMVariant read(XNContext ctx, XNIOStream stream, XOMVariant stop) {
		try {
			String enc =
				encodings.containsKey(stream) ?
					encodings.get(stream) :
						ctx.getTextEncoding();
			String end = stop.toTextString(ctx);
			ByteArrayOutputStream tmp = new ByteArrayOutputStream();
			while (!(new String(tmp.toByteArray(), enc).endsWith(end))) {
				int a = stream.read();
				if (a < 0) break;
				else tmp.write(a);
			}
			return new XOMString(new String(tmp.toByteArray(), enc));
		} catch (IOException e) {
			throw new XNScriptError(e, "Failed to read");
		}
	}

	public XOMVariant read(XNContext ctx, XNIOStream stream, int len) {
		try {
			byte[] b = new byte[len];
			int blen = stream.read(b);
			if (blen < 0) blen = 0;
			b = XIONUtil.binarySubstring(b, 0, blen);
			return new XOMString(new String(b,
					encodings.containsKey(stream) ?
					encodings.get(stream) :
					ctx.getTextEncoding()
			));
		} catch (IOException e) {
			throw new XNScriptError(e, "Failed to read");
		}
	}

	public XOMVariant read(XNContext ctx, XNIOStream stream, int len, XOMVariant stop) {
		try {
			String enc =
				encodings.containsKey(stream) ?
					encodings.get(stream) :
						ctx.getTextEncoding();
			String end = stop.toTextString(ctx);
			ByteArrayOutputStream tmp = new ByteArrayOutputStream();
			while (tmp.size() < len && !(new String(tmp.toByteArray(), enc).endsWith(end))) {
				int a = stream.read();
				if (a < 0) break;
				else tmp.write(a);
			}
			return new XOMString(new String(tmp.toByteArray(), enc));
		} catch (IOException e) {
			throw new XNScriptError(e, "Failed to read");
		}
	}

	public XOMVariant read(XNContext ctx, XNIOStream stream, long pos) {
		try {
			if (pos < 0) pos += stream.length();
			stream.seek(pos);
			String enc =
				encodings.containsKey(stream) ?
					encodings.get(stream) :
						ctx.getTextEncoding();
			ByteArrayOutputStream tmp = new ByteArrayOutputStream();
			while (true) {
				String s = new String(tmp.toByteArray(), enc);
				if (s.endsWith("\r")) {
					int l = stream.lookahead();
					if (l >= 0) {
						ByteArrayOutputStream tmp2 = new ByteArrayOutputStream();
						tmp2.write(tmp.toByteArray());
						tmp2.write(l);
						String s2 = new String(tmp2.toByteArray(), enc);
						if (s2.endsWith("\r\n")) {
							int a = stream.read();
							if (a >= 0) tmp.write(a);
							break;
						} else {
							break;
						}
					} else {
						break;
					}
				}
				else if (s.endsWith("\n") || s.endsWith("\u2028") || s.endsWith("\u2029")) {
					break;
				}
				else {
					int a = stream.read();
					if (a < 0) break;
					else tmp.write(a);
				}
			}
			return new XOMString(new String(tmp.toByteArray(), enc));
		} catch (IOException e) {
			throw new XNScriptError(e, "Failed to read");
		}
	}

	public XOMVariant read(XNContext ctx, XNIOStream stream, long pos, XOMVariant stop) {
		try {
			if (pos < 0) pos += stream.length();
			stream.seek(pos);
			String enc =
				encodings.containsKey(stream) ?
					encodings.get(stream) :
						ctx.getTextEncoding();
			String end = stop.toTextString(ctx);
			ByteArrayOutputStream tmp = new ByteArrayOutputStream();
			while (!(new String(tmp.toByteArray(), enc).endsWith(end))) {
				int a = stream.read();
				if (a < 0) break;
				else tmp.write(a);
			}
			return new XOMString(new String(tmp.toByteArray(), enc));
		} catch (IOException e) {
			throw new XNScriptError(e, "Failed to read");
		}
	}

	public XOMVariant read(XNContext ctx, XNIOStream stream, long pos, int len) {
		try {
			if (pos < 0) pos += stream.length();
			stream.seek(pos);
			byte[] b = new byte[len];
			int blen = stream.read(b);
			if (blen < 0) blen = 0;
			b = XIONUtil.binarySubstring(b, 0, blen);
			return new XOMString(new String(b,
					encodings.containsKey(stream) ?
					encodings.get(stream) :
					ctx.getTextEncoding()
			));
		} catch (IOException e) {
			throw new XNScriptError(e, "Failed to read");
		}
	}

	public XOMVariant read(XNContext ctx, XNIOStream stream, long pos, int len, XOMVariant stop) {
		try {
			if (pos < 0) pos += stream.length();
			stream.seek(pos);
			String enc =
				encodings.containsKey(stream) ?
					encodings.get(stream) :
						ctx.getTextEncoding();
			String end = stop.toTextString(ctx);
			ByteArrayOutputStream tmp = new ByteArrayOutputStream();
			while (tmp.size() < len && !(new String(tmp.toByteArray(), enc).endsWith(end))) {
				int a = stream.read();
				if (a < 0) break;
				else tmp.write(a);
			}
			return new XOMString(new String(tmp.toByteArray(), enc));
		} catch (IOException e) {
			throw new XNScriptError(e, "Failed to read");
		}
	}

	public void write(XNContext ctx, XNIOStream stream, XOMVariant data) {
		try {
			byte[] b = data.toTextString(ctx).getBytes(
					encodings.containsKey(stream) ?
					encodings.get(stream) :
					ctx.getTextEncoding()
			);
			stream.write(b);
		} catch (IOException e) {
			throw new XNScriptError(e, "Failed to write");
		}
	}

	public void write(XNContext ctx, XNIOStream stream, XOMVariant data, long pos) {
		try {
			if (pos < 0) pos += stream.length();
			stream.seek(pos);
			byte[] b = data.toTextString(ctx).getBytes(
					encodings.containsKey(stream) ?
					encodings.get(stream) :
					ctx.getTextEncoding()
			);
			stream.write(b);
		} catch (IOException e) {
			throw new XNScriptError(e, "Failed to write");
		}
	}
	
	public void truncate(XNContext ctx, XNIOStream stream) {
		try {
			stream.setLength(stream.getFilePointer());
		} catch (IOException e) {
			throw new XNScriptError(e, "Failed to truncate");
		}
	}
	
	public void truncate(XNContext ctx, XNIOStream stream, long pos) {
		try {
			if (pos < 0) pos += stream.length();
			stream.setLength(pos);
		} catch (IOException e) {
			throw new XNScriptError(e, "Failed to truncate");
		}
	}

	public void close(XNContext ctx, XNIOStream stream) {
		encodings.remove(stream);
	}
}
