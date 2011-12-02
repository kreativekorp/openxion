/*
 * Copyright &copy; 2011 Rebecca G. Bettencourt / Kreative Software
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
 * @since OpenXION 1.4
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.kreative.openxion.ast.XNFunctionCallPropertyDescriptor;
import com.kreative.openxion.ast.XNListExpression;
import com.kreative.openxion.ast.XNStatement;
import com.kreative.openxion.ast.XNStringExpression;
import com.kreative.openxion.util.XIONUtil;

public class XNMessageExtractor {
	private XNContext context;
	private String textEncoding;
	
	public XNMessageExtractor(XNContext context, String textEncoding) {
		this.context = context;
		this.textEncoding = textEncoding;
	}
	
	public Map<String,List<XNStringExpression>> extractFromFile(File file, String textEncoding) throws IOException {
		XNLexer lexer = new XNLexer(file, new InputStreamReader(new FileInputStream(file), textEncoding));
		XNParser parser = new XNParser(context, lexer);
		List<XNStatement> program = parser.parse();
		return extractFrom(program);
	}
	
	public Map<String,List<XNStringExpression>> extractFromString(String s) {
		XNLexer lexer = new XNLexer(s, new StringReader(s));
		XNParser parser = new XNParser(context, lexer);
		List<XNStatement> program = parser.parse();
		return extractFrom(program);
	}
	
	public Map<String,List<XNStringExpression>> extractFrom(List<XNStatement> program) {
		Map<String,List<XNStringExpression>> messages = new LinkedHashMap<String,List<XNStringExpression>>();
		for (XNStatement statement : program) {
			extractFrom(statement, messages);
		}
		return messages;
	}
	
	private void extractFrom(Object o, Map<String,List<XNStringExpression>> messages) {
		if (o == null) return;
		
		if (o instanceof XNFunctionCallPropertyDescriptor) {
			XNFunctionCallPropertyDescriptor fc = (XNFunctionCallPropertyDescriptor)o;
			if (fc.identifier.equals("'")) {
				if (fc.argument instanceof XNStringExpression) {
					XNStringExpression expr = (XNStringExpression)fc.argument;
					String exprs = XIONUtil.unquote(expr.literal.image, textEncoding);
					if (messages.containsKey(exprs)) {
						messages.get(exprs).add(expr);
					} else {
						List<XNStringExpression> exprl = new LinkedList<XNStringExpression>();
						exprl.add(expr);
						messages.put(exprs, exprl);
					}
				} else if (fc.argument instanceof XNListExpression) {
					XNListExpression le = (XNListExpression)fc.argument;
					if (le.exprs.size() > 0 && le.exprs.get(0) instanceof XNStringExpression) {
						XNStringExpression expr = (XNStringExpression)le.exprs.get(0);
						String exprs = XIONUtil.unquote(expr.literal.image, textEncoding);
						if (messages.containsKey(exprs)) {
							messages.get(exprs).add(expr);
						} else {
							List<XNStringExpression> exprl = new LinkedList<XNStringExpression>();
							exprl.add(expr);
							messages.put(exprs, exprl);
						}
					}
				}
			}
		}
		
		if (o instanceof Object[]) {
			for (Object oo : (Object[])o) {
				extractFrom(oo, messages);
			}
		} else if (o instanceof Collection) {
			for (Object oo : (Collection<?>)o) {
				extractFrom(oo, messages);
			}
		} else if (o instanceof Map) {
			for (Object oo : ((Map<?,?>)o).entrySet()) {
				Map.Entry<?,?> e = (Map.Entry<?,?>)oo;
				extractFrom(e.getValue(), messages);
			}
		} else {
			Class<?> c = o.getClass();
			while (c.getSimpleName().startsWith("XN")) {
				Field[] flds = c.getDeclaredFields();
				for (Field fld : flds) {
					if (Modifier.isStatic(fld.getModifiers())) continue;
					else if (!Modifier.isPublic(fld.getModifiers())) continue;
					else try {
						fld.setAccessible(true);
						extractFrom(fld.get(o), messages);
					} catch (IllegalAccessException iae) {}
				}
				c = c.getSuperclass();
			}
		}
	}
}
