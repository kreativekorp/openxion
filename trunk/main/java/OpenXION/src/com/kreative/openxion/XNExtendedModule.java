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

package com.kreative.openxion;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import com.kreative.openxion.ast.XNEmptyExpression;
import com.kreative.openxion.ast.XNExpression;
import com.kreative.openxion.ast.XNModifier;
import com.kreative.openxion.ast.XNStringExpression;
import com.kreative.openxion.binpack.*;
import com.kreative.openxion.io.XOMURLIOManager;
import com.kreative.openxion.tr.Matchor;
import com.kreative.openxion.tr.Multiplexor;
import com.kreative.openxion.tr.TrPattern;
import com.kreative.openxion.tr.Transformor;
import com.kreative.openxion.util.Base64;
import com.kreative.openxion.util.EndlessInputStream;
import com.kreative.openxion.util.XIONUtil;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.inst.*;
import com.kreative.openxion.xom.type.*;

/**
 * XNExtendedModule is the XNModule responsible for additional
 * constants, ordinals, data types, commands, and functions
 * included in OpenXION but not covered by
 * the XION Scripting Language Standard, Version 1.0.
 * <p>
 * The constants, ordinals, data types, commands, and functions
 * provided by this module are not guaranteed to be in any
 * other particular XION implementation.
 * @since OpenXION 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XNExtendedModule extends XNModule {
	private static final long serialVersionUID = 3L;
	
	public static final String MODULE_NAME = "OpenXION Extended Module";
	public static final String MODULE_VERSION = "1.3.1";
	
	private static XNExtendedModule instance = null;
	public static final synchronized XNExtendedModule instance() {
		if (instance == null) {
			instance = new XNExtendedModule();
		}
		return instance;
	}
	
	private XNExtendedModule() {
		super();
		
		dataTypes.put("clipboard", XOMClipboardType.instance);
		dataTypes.put("clipboards", XOMClipboardType.listInstance);
		dataTypes.put("url", XOMURLType.instance);
		dataTypes.put("urls", XOMURLType.listInstance);
		
		commandParsers.put("sql", p_sql);
		commands.put("sql", c_sql);
		
		functions.put("atob", f_atob);
		functions.put("btoa", f_btoa);
		functions.put("getenv", f_getenv);
		functions.put("heapspace", f_heapspace);
		functions.put("htmldecode", f_htmldecode);
		functions.put("htmlencode", f_htmlencode);
		functions.put("javaname", f_javaname);
		functions.put("javaversion", f_javaversion);
		functions.put("pack", f_pack);
		functions.put("regcountfields", f_regcountfields);
		functions.put("regexplode", f_regexplode);
		functions.put("reginstr", f_reginstr);
		functions.put("regmatch", f_regmatch);
		functions.put("regnthfield", f_regnthfield);
		functions.put("regoffset", f_regoffset);
		functions.put("regreplace", f_regreplace);
		functions.put("regreplaceall", f_regreplaceall);
		functions.put("regrinstr", f_regrinstr);
		functions.put("trcountfields", f_trcountfields);
		functions.put("trexplode", f_trexplode);
		functions.put("trinstr", f_trinstr);
		functions.put("trmatch", f_trmatch);
		functions.put("trnthfield", f_trnthfield);
		functions.put("troffset", f_troffset);
		functions.put("trreplace", f_trreplace);
		functions.put("trreplaceall", f_trreplaceall);
		functions.put("trrinstr", f_trrinstr);
		functions.put("unpack", f_unpack);
		functions.put("urldecode", f_urldecode);
		functions.put("urlencode", f_urlencode);
		functions.put("urlquerydecode", f_urlquerydecode);
		functions.put("urlqueryencode", f_urlqueryencode);
		functions.put("vmname", f_vmname);
		functions.put("vmversion", f_vmversion);
		functions.put("ygndecode", f_ygndecode);
		functions.put("ygnencode", f_ygnencode);
		
		versions.put("extendedmodule", new Version(XNExtendedModule.MODULE_NAME, XNExtendedModule.MODULE_VERSION));
		versions.put("java", new Version(System.getProperty("java.runtime.name"), System.getProperty("java.runtime.version")));
		versions.put("javavm", new Version(System.getProperty("java.vm.name"), System.getProperty("java.vm.version")));
		versions.put("vm", new Version(System.getProperty("java.vm.name"), System.getProperty("java.vm.version")));
		
		ioManagers.add(XOMURLIOManager.instance);
		
		if (XIONUtil.isMacOS()) {
			externalLanguages.put("applescript", e_applescript);
		}
		if (XIONUtil.isWindows()) {
			externalLanguages.put("vbscript", e_vbscript);
		}
		if (!XIONUtil.isWindows()) {
			externalLanguages.put("bash", e_bash);
			externalLanguages.put("perl", e_perl);
			externalLanguages.put("php", e_php);
			externalLanguages.put("python", e_python);
			externalLanguages.put("ruby", e_ruby);
		}
	}
	
	public String toString() {
		return "XNExtendedModule";
	}
	
	private static XNExpression getTokenExpression(XNParser p, String s) {
		if (p.lookToken(1).image.equalsIgnoreCase(s)) {
			return new XNStringExpression(p.getToken());
		} else {
			throw new XNParseError(s, p.lookToken(1));
		}
	}
	
	private static XNExpression getTokenExpression(XNParser p, int n) {
		if (n < 1) {
			return new XNEmptyExpression(p.getSource(), 0, 0);
		} else {
			XNToken t = p.getToken(); n--;
			while (n > 0) {
				XNToken u = p.getToken(); n--;
				XNToken v = new XNToken(t.kind, t.image+" "+u.image, t.source, t.beginLine, t.beginColumn, u.endLine, u.endColumn);
				v.specialToken = t.specialToken;
				v.next = u.next;
				t = v;
			}
			return new XNStringExpression(t);
		}
	}
	
	private static XNExpression createTokenExpression(XNParser p, String s) {
		if (s == null || s.trim().length() == 0) {
			return new XNEmptyExpression(p.getSource(), 0, 0);
		} else {
			return new XNStringExpression(new XNToken(XNToken.ID, s, p.getSource(), 0, 0, 0, 0));
		}
	}
	
	private static String myDescribeCommand(String commandName, List<XNExpression> parameters) {
		String s = "";
		for (XNExpression p : parameters) {
			if (p instanceof XNEmptyExpression) {
				XNEmptyExpression ee = (XNEmptyExpression)p;
				if (ee.getBeginCol() != 0 || ee.getBeginLine() != 0) {
					s += " "+p.toString();
				}
			} else {
				s += " "+p.toString();
			}
		}
		return s.trim();
	}
	
	private static final CommandParser p_sql = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			String kind;
			if (
					p.lookToken(1).toString().equalsIgnoreCase("connect") ||
					p.lookToken(1).toString().equalsIgnoreCase("disconnect") ||
					p.lookToken(1).toString().equalsIgnoreCase("prepare") ||
					p.lookToken(1).toString().equalsIgnoreCase("execute")
			) {
				kind = getTokenExpression(p,1).toString().toLowerCase();
			} else {
				kind = "direct";
			}
			List<XNExpression> following = new Vector<XNExpression>();
			following.add(createTokenExpression(p, kind));
			if (kind.equalsIgnoreCase("connect")) {
				HashSet<String> myKeywords = new HashSet<String>();
				if (keywords != null) myKeywords.addAll(keywords);
				myKeywords.add("with");
				following.add(getTokenExpression(p,"to"));
				following.add(p.getListExpression(myKeywords));
				while (p.lookToken(1).toString().equalsIgnoreCase("with")) {
					if (
							p.lookToken(2).toString().equalsIgnoreCase("driver") ||
							p.lookToken(2).toString().equalsIgnoreCase("username") ||
							p.lookToken(2).toString().equalsIgnoreCase("password")
					) {
						following.add(getTokenExpression(p,2));
						following.add(p.getListExpression(myKeywords));
					} else {
						break;
					}
				}
			} else if (kind.equalsIgnoreCase("disconnect")) {
				if (p.lookToken(1).toString().equalsIgnoreCase("from")) {
					following.add(getTokenExpression(p,"from"));
					following.add(p.getListExpression(keywords));
				}
			} else if (kind.equalsIgnoreCase("prepare")) {
				if (p.lookToken(1).toString().equalsIgnoreCase("execute")) {
					following.add(getTokenExpression(p,"execute"));
				} else if (p.lookToken(1).toString().equalsIgnoreCase("set")) {
					HashSet<String> myKeywords = new HashSet<String>();
					if (keywords != null) myKeywords.addAll(keywords);
					myKeywords.add("to");
					following.add(getTokenExpression(p,"set"));
					following.add(p.getListExpression(myKeywords));
					following.add(getTokenExpression(p,"to"));
					following.add(p.getListExpression(keywords));
				} else if (p.lookToken(1).toString().equalsIgnoreCase("statement")) {
					following.add(getTokenExpression(p,"statement"));
					following.add(p.getListExpression(keywords));
				} else {
					following.add(createTokenExpression(p,"statement"));
					following.add(p.getListExpression(keywords));
				}
				if (p.lookToken(1).toString().equalsIgnoreCase("using") && p.lookToken(2).toString().equalsIgnoreCase("connection")) {
					following.add(getTokenExpression(p,2));
					following.add(p.getListExpression(keywords));
				}
			} else if (kind.equalsIgnoreCase("execute")) {
				if (p.lookToken(1).toString().equalsIgnoreCase("prepared")) {
					following.add(getTokenExpression(p,"prepared"));
				} else {
					following.add(p.getListExpression(keywords));
				}
				if (p.lookToken(1).toString().equalsIgnoreCase("using") && p.lookToken(2).toString().equalsIgnoreCase("connection")) {
					following.add(getTokenExpression(p,2));
					following.add(p.getListExpression(keywords));
				}
			} else {
				StringBuffer sql = new StringBuffer();
				while (true) {
					if (p.lookEOL(1) || !p.isNotKeyword(1, keywords)) {
						following.add(new XNStringExpression(new XNToken(XNToken.QUOTED,XIONUtil.quote(sql.toString().trim()),p.getSource(),0,0,0,0)));
						break;
					} else if (p.lookToken(1).toString().equalsIgnoreCase("using") && p.lookToken(2).toString().equalsIgnoreCase("connection")) {
						following.add(new XNStringExpression(new XNToken(XNToken.QUOTED,XIONUtil.quote(sql.toString().trim()),p.getSource(),0,0,0,0)));
						following.add(getTokenExpression(p,2));
						following.add(p.getListExpression(keywords));
						break;
					} else {
						sql.append(" ");
						sql.append(p.getToken().toString());
					}
				}
			}
			return following;
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final Command c_sql = new Command() {
		private Map<String,Connection> connections = new HashMap<String,Connection>();
		private Map<String,PreparedStatement> prepareds = new HashMap<String,PreparedStatement>();
		private String lastConnection = null;
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.size() == 0) {
				throw new XNScriptError("Can't understand arguments to sql");
			}
			String kind = interp.evaluateExpression(parameters.get(0)).toTextString(ctx);
			if (kind.equalsIgnoreCase("connect")) {
				String to = null;
				String driver = null;
				String username = null;
				String password = null;
				for (int i = 1; i+1 < parameters.size(); i += 2) {
					String k = interp.evaluateExpression(parameters.get(i)).toTextString(ctx);
					String v = interp.evaluateExpression(parameters.get(i+1)).toTextString(ctx);
					if (k.equalsIgnoreCase("to")) to = v;
					else if (k.equalsIgnoreCase("with driver")) driver = v;
					else if (k.equalsIgnoreCase("with username")) username = v;
					else if (k.equalsIgnoreCase("with password")) password = v;
					else throw new XNScriptError("Can't understand arguments to sql: "+k);
				}
				if (to == null) {
					throw new XNScriptError("Can't understand arguments to sql");
				} else if (connections.containsKey(to)) {
					throw new XNScriptError("SQL connection \""+to+"\" is already open");
				} else {
					if (driver != null) {
						try {
							Class.forName(driver);
						} catch (Exception e) {
							throw new XNScriptError("SQL driver \""+driver+"\" not found");
						}
					}
					try {
						Connection conn;
						if (username == null && password == null) {
							conn = DriverManager.getConnection("jdbc:" + to);
						} else {
							conn = DriverManager.getConnection("jdbc:" + to, ((username == null) ? "" : username), ((password == null) ? "" : password));
						}
						connections.put(to, conn);
						lastConnection = to;
					} catch (SQLException e) {
						throw new XNScriptError("SQL error: " + e.getMessage());
					}
				}
			} else if (kind.equalsIgnoreCase("disconnect")) {
				String from = null;
				for (int i = 1; i+1 < parameters.size(); i += 2) {
					String k = interp.evaluateExpression(parameters.get(i)).toTextString(ctx);
					String v = interp.evaluateExpression(parameters.get(i+1)).toTextString(ctx);
					if (k.equalsIgnoreCase("from")) from = v;
					else throw new XNScriptError("Can't understand arguments to sql");
				}
				if (from == null) {
					from = lastConnection;
				}
				if (from == null || !connections.containsKey(from)) {
					throw new XNScriptError("SQL connection not open");
				} else {
					try {
						if (prepareds.containsKey(from)) {
							prepareds.get(from).close();
						}
						connections.get(from).close();
					} catch (SQLException e) {
						// ignored for disconnect statements
					}
					if (lastConnection.equals(from)) {
						lastConnection = null;
					}
					prepareds.remove(from);
					connections.remove(from);
				}
			} else if (kind.equalsIgnoreCase("prepare")) {
				if (parameters.size() > 1) {
					kind = interp.evaluateExpression(parameters.get(1)).toTextString(ctx);
					if (kind.equalsIgnoreCase("execute")) {
						String from = null;
						if (parameters.size() > 3) {
							from = interp.evaluateExpression(parameters.get(3)).toTextString(ctx);
						}
						if (from == null) {
							from = lastConnection;
						}
						if (from == null || !connections.containsKey(from)) {
							throw new XNScriptError("SQL connection not open");
						} else if (!prepareds.containsKey(from)) {
							throw new XNScriptError("Can't sql prepare execute until after sql prepare statement");
						} else {
							try {
								PreparedStatement ps = prepareds.get(from);
								if (ps.execute()) {
									// resultset
									ResultSet rs = ps.getResultSet();
									ResultSetMetaData rsmd = rs.getMetaData();
									char rd = ctx.getRowDelimiter();
									char cd = ctx.getColumnDelimiter();
									StringBuffer it = new StringBuffer();
									int result = 0;
									while (rs.next()) {
										StringBuffer row = new StringBuffer();
										for (int i = 1; i <= rsmd.getColumnCount(); i++) {
											row.append(cd);
											row.append(rs.getString(i).replace(Character.toString(cd), ""));
										}
										if (row.length() > 0) {
											row.deleteCharAt(0);
										}
										it.append(rd);
										it.append(row.toString().replace(Character.toString(rd), ""));
										result++;
									}
									if (it.length() > 0) {
										it.deleteCharAt(0);
									}
									lastConnection = from;
									ctx.getVariableMap("it").setVariable(ctx, "it", new XOMString(it.toString()));
									return new XOMInteger(result);
								} else {
									// updatecount
									lastConnection = from;
									return new XOMInteger(ps.getUpdateCount());
								}
							} catch (SQLException e) {
								throw new XNScriptError("SQL error: " + e.getMessage());
							}
						}
					} else if (kind.equalsIgnoreCase("set")) {
						int param = 0;
						String value = null;
						String from = null;
						if (parameters.size() > 2) {
							XOMVariant v = interp.evaluateExpression(parameters.get(2)).asPrimitive(ctx);
							XOMInteger i = XOMIntegerType.instance.makeInstanceFrom(ctx, v, true);
							param = i.toInt();
						}
						if (parameters.size() > 4) {
							value = interp.evaluateExpression(parameters.get(4)).toTextString(ctx);
						}
						if (parameters.size() > 6) {
							from = interp.evaluateExpression(parameters.get(6)).toTextString(ctx);
						}
						if (param < 1 || value == null) {
							throw new XNScriptError("Can't understand arguments to sql");
						}
						if (from == null) {
							from = lastConnection;
						}
						if (from == null || !connections.containsKey(from)) {
							throw new XNScriptError("SQL connection not open");
						} else if (!prepareds.containsKey(from)) {
							throw new XNScriptError("Can't sql prepare set until after sql prepare statement");
						} else {
							try {
								prepareds.get(from).setString(param, value);
								lastConnection = from;
							} catch (SQLException e) {
								throw new XNScriptError("SQL error: " + e.getMessage());
							}
						}
					} else {
						String query = null;
						String from = null;
						if (parameters.size() > 2) {
							query = interp.evaluateExpression(parameters.get(2)).toTextString(ctx);
						}
						if (parameters.size() > 4) {
							from = interp.evaluateExpression(parameters.get(4)).toTextString(ctx);
						}
						if (query == null) {
							throw new XNScriptError("Can't understand arguments to sql");
						}
						if (from == null) {
							from = lastConnection;
						}
						if (from == null || !connections.containsKey(from)) {
							throw new XNScriptError("SQL connection not open");
						} else {
							try {
								if (prepareds.containsKey(from)) {
									prepareds.get(from).close();
									prepareds.remove(from);
								}
								Connection conn = connections.get(from);
								prepareds.put(from, conn.prepareStatement(query));
								lastConnection = from;
							} catch (SQLException e) {
								throw new XNScriptError("SQL error: " + e.getMessage());
							}
						}
					}
				} else {
					throw new XNScriptError("Can't understand arguments to sql");
				}
			} else {
				String query = null;
				String from = null;
				if (parameters.size() > 1) {
					query = interp.evaluateExpression(parameters.get(1)).toTextString(ctx);
				}
				if (parameters.size() > 3) {
					from = interp.evaluateExpression(parameters.get(3)).toTextString(ctx);
				}
				if (query == null) {
					throw new XNScriptError("Can't understand arguments to sql");
				}
				if (from == null) {
					from = lastConnection;
				}
				if (from == null || !connections.containsKey(from)) {
					throw new XNScriptError("SQL connection not open");
				} else {
					try {
						Connection conn = connections.get(from);
						if (query.trim().equalsIgnoreCase("prepared")) {
							if (!prepareds.containsKey(from)) {
								throw new XNScriptError("Can't sql execute prepared until after sql prepare statement");
							} else {
								PreparedStatement ps = prepareds.get(from);
								if (ps.execute()) {
									// resultset
									ResultSet rs = ps.getResultSet();
									ResultSetMetaData rsmd = rs.getMetaData();
									char rd = ctx.getRowDelimiter();
									char cd = ctx.getColumnDelimiter();
									StringBuffer it = new StringBuffer();
									int result = 0;
									while (rs.next()) {
										StringBuffer row = new StringBuffer();
										for (int i = 1; i <= rsmd.getColumnCount(); i++) {
											row.append(cd);
											row.append(rs.getString(i).replace(Character.toString(cd), ""));
										}
										if (row.length() > 0) {
											row.deleteCharAt(0);
										}
										it.append(rd);
										it.append(row.toString().replace(Character.toString(rd), ""));
										result++;
									}
									if (it.length() > 0) {
										it.deleteCharAt(0);
									}
									lastConnection = from;
									ctx.getVariableMap("it").setVariable(ctx, "it", new XOMString(it.toString()));
									return new XOMInteger(result);
								} else {
									// updatecount
									lastConnection = from;
									return new XOMInteger(ps.getUpdateCount());
								}
							}
						} else {
							Statement st = conn.createStatement();
							if (st.execute(query)) {
								// resultset
								ResultSet rs = st.getResultSet();
								ResultSetMetaData rsmd = rs.getMetaData();
								char rd = ctx.getRowDelimiter();
								char cd = ctx.getColumnDelimiter();
								StringBuffer it = new StringBuffer();
								int result = 0;
								while (rs.next()) {
									StringBuffer row = new StringBuffer();
									for (int i = 1; i <= rsmd.getColumnCount(); i++) {
										row.append(cd);
										row.append(rs.getString(i).replace(Character.toString(cd), ""));
									}
									if (row.length() > 0) {
										row.deleteCharAt(0);
									}
									it.append(rd);
									it.append(row.toString().replace(Character.toString(rd), ""));
									result++;
								}
								if (it.length() > 0) {
									it.deleteCharAt(0);
								}
								lastConnection = from;
								ctx.getVariableMap("it").setVariable(ctx, "it", new XOMString(it.toString()));
								return new XOMInteger(result);
							} else {
								// updatecount
								lastConnection = from;
								return new XOMInteger(st.getUpdateCount());
							}
						}
					} catch (SQLException e) {
						throw new XNScriptError("SQL error: " + e.getMessage());
					}
				}
			}
			return null;
		}
	};
	
	private static void assertEmptyParameter(String functionName, XOMVariant parameter) {
		if (!(parameter == null || parameter instanceof XOMEmpty)) {
			throw new XNScriptError("Can't understand arguments to "+functionName);
		}
	}
	
	private static List<? extends XOMVariant> listParameter(XNContext ctx, String functionName, XOMVariant parameter, boolean primitive) {
		List<? extends XOMVariant> l = (parameter == null) ? new Vector<XOMVariant>() : primitive ? parameter.toPrimitiveList(ctx) : parameter.toVariantList(ctx);
		return l;
	}
	
	private static List<? extends XOMVariant> listParameter(XNContext ctx, String functionName, XOMVariant parameter, int np, boolean primitive) {
		List<? extends XOMVariant> l = (parameter == null) ? new Vector<XOMVariant>() : primitive ? parameter.toPrimitiveList(ctx) : parameter.toVariantList(ctx);
		if (l.size() != np) {
			throw new XNScriptError("Can't understand arguments to "+functionName);
		} else {
			return l;
		}
	}
	
	private static List<? extends XOMVariant> listParameter(XNContext ctx, String functionName, XOMVariant parameter, int min, int max, boolean primitive) {
		List<? extends XOMVariant> l = (parameter == null) ? new Vector<XOMVariant>() : primitive ? parameter.toPrimitiveList(ctx) : parameter.toVariantList(ctx);
		if (l.size() < min || l.size() > max) {
			throw new XNScriptError("Can't understand arguments to "+functionName);
		} else {
			return l;
		}
	}
	
	private static final Function f_atob = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 1, 2, true);
			String e = (l.size() > 1) ? l.get(1).toTextString(ctx) : "base64";
			String a = l.get(0).toTextString(ctx);
			if (e.equalsIgnoreCase("b64") || e.equalsIgnoreCase("base64"))
				return new XOMBinary(Base64.decodeBase64(a));
			else if (e.equalsIgnoreCase("uu") || e.equalsIgnoreCase("uud") || e.equalsIgnoreCase("uudecode"))
				return new XOMBinary(Base64.decodeUU(a));
			else if (e.equalsIgnoreCase("xx") || e.equalsIgnoreCase("xxd") || e.equalsIgnoreCase("xxdecode"))
				return new XOMBinary(Base64.decodeXX(a));
			else if (e.equalsIgnoreCase("hqx") || e.equalsIgnoreCase("binhex"))
				return new XOMBinary(Base64.decodeBinHex(a));
			else if (e.equalsIgnoreCase("a85") || e.equalsIgnoreCase("ascii85"))
				return new XOMBinary(Base64.decodeASCII85(a));
			else if (e.equalsIgnoreCase("k85") || e.equalsIgnoreCase("kreative85"))
				return new XOMBinary(Base64.decodeKreative85(a));
			else if (e.equalsIgnoreCase("l85") || e.equalsIgnoreCase("legacy85"))
				return new XOMBinary(Base64.decodeLegacy85(a));
			else
				return XOMEmpty.EMPTY;
		}
	};
	
	private static final Function f_btoa = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 1, 2, true);
			String e = (l.size() > 1) ? l.get(1).toTextString(ctx) : "base64";
			byte[] b = XOMBinaryType.instance.makeInstanceFrom(ctx, l.get(0)).toByteArray();
			if (e.equalsIgnoreCase("b64") || e.equalsIgnoreCase("base64"))
				return new XOMString(Base64.encodeBase64(b));
			else if (e.equalsIgnoreCase("uu") || e.equalsIgnoreCase("uue") || e.equalsIgnoreCase("uuencode"))
				return new XOMString(Base64.encodeUU(b));
			else if (e.equalsIgnoreCase("xx") || e.equalsIgnoreCase("xxe") || e.equalsIgnoreCase("xxencode"))
				return new XOMString(Base64.encodeXX(b));
			else if (e.equalsIgnoreCase("hqx") || e.equalsIgnoreCase("binhex"))
				return new XOMString(Base64.encodeBinHex(b));
			else if (e.equalsIgnoreCase("a85") || e.equalsIgnoreCase("ascii85"))
				return new XOMString(Base64.encodeASCII85(b));
			else if (e.equalsIgnoreCase("k85") || e.equalsIgnoreCase("kreative85"))
				return new XOMString(Base64.encodeKreative85(b));
			else if (e.equalsIgnoreCase("l85") || e.equalsIgnoreCase("legacy85"))
				return new XOMString(Base64.encodeLegacy85(b));
			else
				return XOMEmpty.EMPTY;
		}
	};
	
	private static final Function f_getenv = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to getenv");
			else {
				String var = parameter.toTextString(ctx);
				if (!ctx.allow(XNSecurityKey.SYSTEM_INFO, "Function", functionName, "Variable", var))
					throw new XNScriptError("Security settings do not allow getenv");
				if (var.length() == 0) {
					Collection<String> varc = System.getenv().keySet();
					List<String> varl = new Vector<String>();
					varl.addAll(varc);
					Collections.sort(varl, String.CASE_INSENSITIVE_ORDER);
					List<XOMString> varx = new Vector<XOMString>();
					for (String v : varl) varx.add(new XOMString(v));
					return new XOMList(varx);
				} else {
					String val = System.getenv(var);
					return (val == null) ? XOMString.EMPTY_STRING : new XOMString(val);
				}
			}
		}
	};
	
	private static final Function f_heapspace = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			if (!ctx.allow(XNSecurityKey.SYSTEM_INFO, "Function", functionName))
				throw new XNScriptError("Security settings do not allow heapSpace");
			return new XOMInteger(Runtime.getRuntime().freeMemory());
		}
	};
	
	private static final Function f_htmldecode = new Function() {
		private Map<String,Character> entityMap = null;
		private Map<String,Character> entityMap() {
			if (entityMap == null) {
				entityMap = new HashMap<String,Character>();
				entityMap.put("amp", '&');
				entityMap.put("lt", '<');
				entityMap.put("gt", '>');
				entityMap.put("quot", '\"');
				entityMap.put("apos", '\'');
				entityMap.put("nbsp", '\u00A0');
				entityMap.put("iexcl", '\u00A1');
				entityMap.put("cent", '\u00A2');
				entityMap.put("pound", '\u00A3');
				entityMap.put("curren", '\u00A4');
				entityMap.put("yen", '\u00A5');
				entityMap.put("brvbar", '\u00A6');
				entityMap.put("sect", '\u00A7');
				entityMap.put("uml", '\u00A8');
				entityMap.put("copy", '\u00A9');
				entityMap.put("ordf", '\u00AA');
				entityMap.put("laquo", '\u00AB');
				entityMap.put("not", '\u00AC');
				entityMap.put("shy", '\u00AD');
				entityMap.put("reg", '\u00AE');
				entityMap.put("macr", '\u00AF');
				entityMap.put("deg", '\u00B0');
				entityMap.put("plusmn", '\u00B1');
				entityMap.put("sup2", '\u00B2');
				entityMap.put("sup3", '\u00B3');
				entityMap.put("acute", '\u00B4');
				entityMap.put("micro", '\u00B5');
				entityMap.put("para", '\u00B6');
				entityMap.put("middot", '\u00B7');
				entityMap.put("cedil", '\u00B8');
				entityMap.put("sup1", '\u00B9');
				entityMap.put("ordm", '\u00BA');
				entityMap.put("raquo", '\u00BB');
				entityMap.put("frac14", '\u00BC');
				entityMap.put("frac12", '\u00BD');
				entityMap.put("frac34", '\u00BE');
				entityMap.put("iquest", '\u00BF');
				entityMap.put("Agrave", '\u00C0');
				entityMap.put("Aacute", '\u00C1');
				entityMap.put("Acirc", '\u00C2');
				entityMap.put("Atilde", '\u00C3');
				entityMap.put("Auml", '\u00C4');
				entityMap.put("Aring", '\u00C5');
				entityMap.put("AElig", '\u00C6');
				entityMap.put("Ccedil", '\u00C7');
				entityMap.put("Egrave", '\u00C8');
				entityMap.put("Eacute", '\u00C9');
				entityMap.put("Ecirc", '\u00CA');
				entityMap.put("Euml", '\u00CB');
				entityMap.put("Igrave", '\u00CC');
				entityMap.put("Iacute", '\u00CD');
				entityMap.put("Icirc", '\u00CE');
				entityMap.put("Iuml", '\u00CF');
				entityMap.put("ETH", '\u00D0');
				entityMap.put("Ntilde", '\u00D1');
				entityMap.put("Ograve", '\u00D2');
				entityMap.put("Oacute", '\u00D3');
				entityMap.put("Ocirc", '\u00D4');
				entityMap.put("Otilde", '\u00D5');
				entityMap.put("Ouml", '\u00D6');
				entityMap.put("times", '\u00D7');
				entityMap.put("Oslash", '\u00D8');
				entityMap.put("Ugrave", '\u00D9');
				entityMap.put("Uacute", '\u00DA');
				entityMap.put("Ucirc", '\u00DB');
				entityMap.put("Uuml", '\u00DC');
				entityMap.put("Yacute", '\u00DD');
				entityMap.put("THORN", '\u00DE');
				entityMap.put("szlig", '\u00DF');
				entityMap.put("agrave", '\u00E0');
				entityMap.put("aacute", '\u00E1');
				entityMap.put("acirc", '\u00E2');
				entityMap.put("atilde", '\u00E3');
				entityMap.put("auml", '\u00E4');
				entityMap.put("aring", '\u00E5');
				entityMap.put("aelig", '\u00E6');
				entityMap.put("ccedil", '\u00E7');
				entityMap.put("egrave", '\u00E8');
				entityMap.put("eacute", '\u00E9');
				entityMap.put("ecirc", '\u00EA');
				entityMap.put("euml", '\u00EB');
				entityMap.put("igrave", '\u00EC');
				entityMap.put("iacute", '\u00ED');
				entityMap.put("icirc", '\u00EE');
				entityMap.put("iuml", '\u00EF');
				entityMap.put("eth", '\u00F0');
				entityMap.put("ntilde", '\u00F1');
				entityMap.put("ograve", '\u00F2');
				entityMap.put("oacute", '\u00F3');
				entityMap.put("ocirc", '\u00F4');
				entityMap.put("otilde", '\u00F5');
				entityMap.put("ouml", '\u00F6');
				entityMap.put("divide", '\u00F7');
				entityMap.put("oslash", '\u00F8');
				entityMap.put("ugrave", '\u00F9');
				entityMap.put("uacute", '\u00FA');
				entityMap.put("ucirc", '\u00FB');
				entityMap.put("uuml", '\u00FC');
				entityMap.put("yacute", '\u00FD');
				entityMap.put("thorn", '\u00FE');
				entityMap.put("yuml", '\u00FF');
				entityMap.put("OElig", '\u0152');
				entityMap.put("oelig", '\u0153');
				entityMap.put("Scaron", '\u0160');
				entityMap.put("scaron", '\u0161');
				entityMap.put("Yuml", '\u0178');
				entityMap.put("fnof", '\u0192');
				entityMap.put("circ", '\u02C6');
				entityMap.put("tilde", '\u02DC');
				entityMap.put("Alpha", '\u0391');
				entityMap.put("Beta", '\u0392');
				entityMap.put("Gamma", '\u0393');
				entityMap.put("Delta", '\u0394');
				entityMap.put("Epsilon", '\u0395');
				entityMap.put("Zeta", '\u0396');
				entityMap.put("Eta", '\u0397');
				entityMap.put("Theta", '\u0398');
				entityMap.put("Iota", '\u0399');
				entityMap.put("Kappa", '\u039A');
				entityMap.put("Lambda", '\u039B');
				entityMap.put("Mu", '\u039C');
				entityMap.put("Nu", '\u039D');
				entityMap.put("Xi", '\u039E');
				entityMap.put("Omicron", '\u039F');
				entityMap.put("Pi", '\u03A0');
				entityMap.put("Rho", '\u03A1');
				entityMap.put("Sigma", '\u03A3');
				entityMap.put("Tau", '\u03A4');
				entityMap.put("Upsilon", '\u03A5');
				entityMap.put("Phi", '\u03A6');
				entityMap.put("Chi", '\u03A7');
				entityMap.put("Psi", '\u03A8');
				entityMap.put("Omega", '\u03A9');
				entityMap.put("alpha", '\u03B1');
				entityMap.put("beta", '\u03B2');
				entityMap.put("gamma", '\u03B3');
				entityMap.put("delta", '\u03B4');
				entityMap.put("epsilon", '\u03B5');
				entityMap.put("zeta", '\u03B6');
				entityMap.put("eta", '\u03B7');
				entityMap.put("theta", '\u03B8');
				entityMap.put("iota", '\u03B9');
				entityMap.put("kappa", '\u03BA');
				entityMap.put("lambda", '\u03BB');
				entityMap.put("mu", '\u03BC');
				entityMap.put("nu", '\u03BD');
				entityMap.put("xi", '\u03BE');
				entityMap.put("omicron", '\u03BF');
				entityMap.put("pi", '\u03C0');
				entityMap.put("rho", '\u03C1');
				entityMap.put("sigmaf", '\u03C2');
				entityMap.put("sigma", '\u03C3');
				entityMap.put("tau", '\u03C4');
				entityMap.put("upsilon", '\u03C5');
				entityMap.put("phi", '\u03C6');
				entityMap.put("chi", '\u03C7');
				entityMap.put("psi", '\u03C8');
				entityMap.put("omega", '\u03C9');
				entityMap.put("thetasym", '\u03D1');
				entityMap.put("upsih", '\u03D2');
				entityMap.put("piv", '\u03D6');
				entityMap.put("ensp", '\u2002');
				entityMap.put("emsp", '\u2003');
				entityMap.put("thinsp", '\u2009');
				entityMap.put("zwnj", '\u200C');
				entityMap.put("zwj", '\u200D');
				entityMap.put("lrm", '\u200E');
				entityMap.put("rlm", '\u200F');
				entityMap.put("ndash", '\u2013');
				entityMap.put("mdash", '\u2014');
				entityMap.put("lsquo", '\u2018');
				entityMap.put("rsquo", '\u2019');
				entityMap.put("sbquo", '\u201A');
				entityMap.put("ldquo", '\u201C');
				entityMap.put("rdquo", '\u201D');
				entityMap.put("bdquo", '\u201E');
				entityMap.put("dagger", '\u2020');
				entityMap.put("Dagger", '\u2021');
				entityMap.put("bull", '\u2022');
				entityMap.put("hellip", '\u2026');
				entityMap.put("permil", '\u2030');
				entityMap.put("prime", '\u2032');
				entityMap.put("Prime", '\u2033');
				entityMap.put("lsaquo", '\u2039');
				entityMap.put("rsaquo", '\u203A');
				entityMap.put("oline", '\u203E');
				entityMap.put("frasl", '\u2044');
				entityMap.put("euro", '\u20AC');
				entityMap.put("image", '\u2111');
				entityMap.put("weierp", '\u2118');
				entityMap.put("real", '\u211C');
				entityMap.put("trade", '\u2122');
				entityMap.put("alefsym", '\u2135');
				entityMap.put("larr", '\u2190');
				entityMap.put("uarr", '\u2191');
				entityMap.put("rarr", '\u2192');
				entityMap.put("darr", '\u2193');
				entityMap.put("harr", '\u2194');
				entityMap.put("crarr", '\u21B5');
				entityMap.put("lArr", '\u21D0');
				entityMap.put("uArr", '\u21D1');
				entityMap.put("rArr", '\u21D2');
				entityMap.put("dArr", '\u21D3');
				entityMap.put("hArr", '\u21D4');
				entityMap.put("forall", '\u2200');
				entityMap.put("part", '\u2202');
				entityMap.put("exist", '\u2203');
				entityMap.put("empty", '\u2205');
				entityMap.put("nabla", '\u2207');
				entityMap.put("isin", '\u2208');
				entityMap.put("notin", '\u2209');
				entityMap.put("ni", '\u220B');
				entityMap.put("prod", '\u220F');
				entityMap.put("sum", '\u2211');
				entityMap.put("minus", '\u2212');
				entityMap.put("lowast", '\u2217');
				entityMap.put("radic", '\u221A');
				entityMap.put("prop", '\u221D');
				entityMap.put("infin", '\u221E');
				entityMap.put("ang", '\u2220');
				entityMap.put("and", '\u2227');
				entityMap.put("or", '\u2228');
				entityMap.put("cap", '\u2229');
				entityMap.put("cup", '\u222A');
				entityMap.put("int", '\u222B');
				entityMap.put("there4", '\u2234');
				entityMap.put("sim", '\u223C');
				entityMap.put("cong", '\u2245');
				entityMap.put("asymp", '\u2248');
				entityMap.put("ne", '\u2260');
				entityMap.put("equiv", '\u2261');
				entityMap.put("le", '\u2264');
				entityMap.put("ge", '\u2265');
				entityMap.put("sub", '\u2282');
				entityMap.put("sup", '\u2283');
				entityMap.put("nsub", '\u2284');
				entityMap.put("sube", '\u2286');
				entityMap.put("supe", '\u2287');
				entityMap.put("oplus", '\u2295');
				entityMap.put("otimes", '\u2297');
				entityMap.put("perp", '\u22A5');
				entityMap.put("sdot", '\u22C5');
				entityMap.put("lceil", '\u2308');
				entityMap.put("rceil", '\u2309');
				entityMap.put("lfloor", '\u230A');
				entityMap.put("rfloor", '\u230B');
				entityMap.put("lang", '\u2329');
				entityMap.put("rang", '\u232A');
				entityMap.put("loz", '\u25CA');
				entityMap.put("spades", '\u2660');
				entityMap.put("clubs", '\u2663');
				entityMap.put("hearts", '\u2665');
				entityMap.put("diams", '\u2666');
			}
			return entityMap;
		}
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to htmldecode");
			else {
				String in = parameter.toTextString(ctx);
				StringBuffer out = new StringBuffer();
				while (in.length() > 0) {
					int a = in.indexOf('&');
					if (a < 0) {
						out.append(in);
						in = "";
						break;
					} else {
						out.append(in.substring(0, a));
						in = in.substring(a);
						int s = in.indexOf(';');
						int a2 = in.indexOf('&',1);
						if (s < 0) {
							out.append(in);
							in = "";
							break;
						} else if (a2 >= 0 && a2 < s) {
							out.append(in.substring(0, a2));
							in = in.substring(a2);
						} else {
							String ent = in.substring(0, s+1);
							in = in.substring(s+1);
							if (ent.toLowerCase().startsWith("&#x")) {
								String h = ent.substring(3, ent.length()-1);
								try {
									out.append(Character.toChars(Integer.parseInt(h, 16)));
								} catch (NumberFormatException nfe) {
									out.append(ent);
								}
							}
							else if (ent.startsWith("&#")) {
								String d = ent.substring(2, ent.length()-1);
								try {
									out.append(Character.toChars(Integer.parseInt(d)));
								} catch (NumberFormatException nfe) {
									out.append(ent);
								}
							}
							else {
								String e = ent.substring(1, ent.length()-1);
								if (entityMap().containsKey(e)) {
									out.append((char)entityMap().get(e));
								} else {
									out.append(ent);
								}
							}
						}
					}
				}
				return new XOMString(out.toString());
			}
		}
	};
	
	private static final Function f_htmlencode = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to htmlencode");
			else {
				String in = parameter.toTextString(ctx);
				CharacterIterator it = new StringCharacterIterator(in);
				StringBuffer out = new StringBuffer();
				for (char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
					switch (ch) {
					case '&': out.append("&amp;"); break;
					case '<': out.append("&lt;"); break;
					case '>': out.append("&gt;"); break;
					case '\"': out.append("&quot;"); break;
					case '\'': out.append("&#39;"); break;
					case '\u00A0': out.append("&nbsp;"); break;
					default:
						if (ch < 32 || ch >= 127) {
							out.append("&#"+(int)ch+";");
						} else {
							out.append(ch);
						}
						break;
					}
				}
				return new XOMString(out.toString());
			}
		}
	};
	
	private static final Function f_javaname = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			if (!ctx.allow(XNSecurityKey.SYSTEM_INFO, "Function", functionName))
				throw new XNScriptError("Security settings do not allow javaName");
			return new XOMString(System.getProperty("java.runtime.name"));
		}
	};
	
	private static final Function f_javaversion = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			if (!ctx.allow(XNSecurityKey.SYSTEM_INFO, "Function", functionName))
				throw new XNScriptError("Security settings do not allow javaVersion");
			return new XOMString(System.getProperty("java.runtime.version"));
		}
	};
	
	private static final Function f_pack = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, true);
			if (l.size() < 1) throw new XNScriptError("Can't understand arguments to "+functionName);
			List<DataField> format;
			try {
				format = new DataFormatParser(new StringReader(l.get(0).toTextString(ctx))).parseAuto();
			} catch (Exception e) {
				throw new XNScriptError("Invalid format string: " + e.getMessage());
			}
			List<Object> things = XOMtoNative(format, l.subList(1, l.size()), ctx);
			byte[] res;
			try {
				res = new DataWriter(format).pack(things);
			} catch (NumberFormatException e) {
				throw new XNScriptError("Expected numeric value here");
			} catch (Exception e) {
				throw new XNScriptError(e.getMessage());
			}
			return new XOMBinary(res);
		}
		private List<Object> XOMtoNative(List<DataField> format, List<? extends XOMVariant> l, XNContext ctx) {
			List<Object> things = new ArrayList<Object>();
			Iterator<? extends XOMVariant> li = l.iterator();
			for (DataField df : format) {
				if (df.type().returns()) {
					XOMVariant xo = li.hasNext() ? li.next() : XOMEmpty.EMPTY;
					things.add(XOMtoNativeWithCount(df, xo, ctx));
				}
			}
			return things;
		}
		private Object XOMtoNativeWithCount(DataField df, XOMVariant xo, XNContext ctx) {
			if (df.count() == null || df.type().usesCustomCount()) {
				return XOMtoNativeWithoutCount(df, xo, ctx);
			} else {
				List<? extends XOMVariant> l = xo.toPrimitiveList(ctx);
				List<Object> things = new ArrayList<Object>();
				for (XOMVariant xoi : l) things.add(XOMtoNativeWithoutCount(df, xoi, ctx));
				return things;
			}
		}
		private Object XOMtoNativeWithoutCount(DataField df, XOMVariant xo, XNContext ctx) {
			switch (df.type()) {
			case BOOLEAN: return XOMBooleanType.instance.makeInstanceFrom(ctx, xo).toBoolean();
			case ENUM: return xo.toTextString(ctx);
			case BITFIELD: return Arrays.asList(xo.toTextString(ctx).split("\\s*,\\s*"));
			case BINT: return xo.toTextString(ctx);
			case OINT: return xo.toTextString(ctx);
			case HINT: return xo.toTextString(ctx);
			case UINT: return XOMIntegerType.instance.makeInstanceFrom(ctx, xo, true).toBigInteger();
			case SINT: return XOMIntegerType.instance.makeInstanceFrom(ctx, xo, true).toBigInteger();
			case UFIXED: return XOMNumberType.instance.makeInstanceFrom(ctx, xo, true).toBigDecimal();
			case SFIXED: return XOMNumberType.instance.makeInstanceFrom(ctx, xo, true).toBigDecimal();
			case FLOAT: return XOMNumberType.instance.makeInstanceFrom(ctx, xo, true).toNumber();
			case COMPLEX: return XOMComplexType.instance.makeInstanceFrom(ctx, xo, true).toNumbers();
			case CHAR: return xo.toTextString(ctx);
			case PSTRING: return xo.toTextString(ctx);
			case CSTRING: return xo.toTextString(ctx);
			case DATE: return XOMDateType.instance.makeInstanceFrom(ctx, xo).toCalendar();
			case COLOR: return XOMColorType.instance.makeInstanceFrom(ctx, xo).toRGBAFloatArray();
			case BINARY: return XOMBinaryType.instance.makeInstanceFrom(ctx, xo).toByteArray();
			case STRUCT:
				@SuppressWarnings("unchecked")
				List<DataField> sfmt = (List<DataField>)df.elaboration();
				return XOMtoNative(sfmt, xo.toPrimitiveList(ctx), ctx);
			default: throw new XNScriptError("Unknown data type: " + df.type().toString());
			}
		}
	};
	
	private static final Function f_regcountfields = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			String s = l.get(0).toTextString(ctx);
			if (s.length() == 0) return XOMInteger.ZERO;
			String d = l.get(1).toTextString(ctx);
			return new XOMInteger(s.split(d).length);
		}
	};
	
	private static final Function f_regexplode = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			String s = l.get(0).toTextString(ctx);
			if (s.length() == 0) return XOMString.EMPTY_STRING;
			String d = l.get(1).toTextString(ctx);
			String[] flds = s.split(d);
			List<XOMVariant> vlds = new Vector<XOMVariant>();
			for (String fld : flds) {
				vlds.add(new XOMString(fld));
			}
			return new XOMList(vlds);
		}
	};
	
	private static final Function f_reginstr = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			String s = l.get(0).toTextString(ctx);
			String d = l.get(1).toTextString(ctx);
			Matcher m = Pattern.compile(d).matcher(s);
			if (m.find()) {
				return new XOMInteger(m.start()+1);
			} else {
				return XOMInteger.ZERO;
			}
		}
	};
	
	private static final Function f_regmatch = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			String s = l.get(0).toTextString(ctx);
			String d = l.get(1).toTextString(ctx);
			return s.matches(d) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		}
	};
	
	private static final Function f_regnthfield = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 3, true);
			String s = l.get(0).toTextString(ctx);
			if (s.length() == 0) return XOMString.EMPTY_STRING;
			String d = l.get(1).toTextString(ctx);
			String[] flds = s.split(d);
			int n = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(2), true).toInt();
			if (n < 1 || n > flds.length) return XOMString.EMPTY_STRING;
			else return new XOMString(flds[n-1]);
		}
	};
	
	private static final Function f_regoffset = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			String s = l.get(1).toTextString(ctx);
			String d = l.get(0).toTextString(ctx);
			Matcher m = Pattern.compile(d).matcher(s);
			if (m.find()) {
				return new XOMInteger(m.start()+1);
			} else {
				return XOMInteger.ZERO;
			}
		}
	};
	
	private static final Function f_regreplace = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 3, true);
			String str = l.get(0).toTextString(ctx);
			String src = l.get(1).toTextString(ctx);
			String rep = l.get(2).toTextString(ctx);
			return new XOMString(str.replaceFirst(src, rep));
		}
	};
	
	private static final Function f_regreplaceall = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 3, true);
			String str = l.get(0).toTextString(ctx);
			String src = l.get(1).toTextString(ctx);
			String rep = l.get(2).toTextString(ctx);
			return new XOMString(str.replaceAll(src, rep));
		}
	};
	
	private static final Function f_regrinstr = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			String s = l.get(0).toTextString(ctx);
			String d = l.get(1).toTextString(ctx);
			Matcher m = Pattern.compile(d).matcher(s);
			int i = 0;
			while (m.find()) i = m.start()+1;
			return new XOMInteger(i);
		}
	};
	
	private static final Function f_trcountfields = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			String s = l.get(0).toTextString(ctx);
			if (s.length() == 0) return XOMInteger.ZERO;
			String d = l.get(1).toTextString(ctx);
			Matchor m = TrPattern.compile(d, false).matchor();
			return new XOMInteger(m.split(s).length);
		}
	};
	
	private static final Function f_trexplode = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			String s = l.get(0).toTextString(ctx);
			if (s.length() == 0) return XOMString.EMPTY_STRING;
			String d = l.get(1).toTextString(ctx);
			Matchor m = TrPattern.compile(d, false).matchor();
			String[] flds = m.split(s);
			List<XOMVariant> vlds = new Vector<XOMVariant>();
			for (String fld : flds) {
				vlds.add(new XOMString(fld));
			}
			return new XOMList(vlds);
		}
	};
	
	private static final Function f_trinstr = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			String s = l.get(0).toTextString(ctx);
			String d = l.get(1).toTextString(ctx);
			Matchor m = TrPattern.compile(d, false).matchor();
			return new XOMInteger(m.findIn(s)+1);
		}
	};
	
	private static final Function f_trmatch = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			String s = l.get(0).toTextString(ctx);
			String d = l.get(1).toTextString(ctx);
			Matchor m = TrPattern.compile(d, false).matchor();
			return m.matchesAll(s) ? XOMBoolean.TRUE : XOMBoolean.FALSE;
		}
	};
	
	private static final Function f_trnthfield = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 3, true);
			String s = l.get(0).toTextString(ctx);
			if (s.length() == 0) return XOMString.EMPTY_STRING;
			String d = l.get(1).toTextString(ctx);
			Matchor m = TrPattern.compile(d, false).matchor();
			String[] flds = m.split(s);
			int n = XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(2), true).toInt();
			if (n < 1 || n > flds.length) return XOMString.EMPTY_STRING;
			else return new XOMString(flds[n-1]);
		}
	};
	
	private static final Function f_troffset = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			String s = l.get(1).toTextString(ctx);
			String d = l.get(0).toTextString(ctx);
			Matchor m = TrPattern.compile(d, false).matchor();
			return new XOMInteger(m.findIn(s)+1);
		}
	};
	
	private static final Function f_trreplace = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 3, 4, true);
			String str = l.get(0).toTextString(ctx);
			String a1 = l.get(1).toTextString(ctx);
			String a2 = l.get(2).toTextString(ctx);
			String a3 = (l.size() > 3) ? l.get(3).toTextString(ctx) : "";
			Transformor tx;
			if (a1.startsWith("-")) {
				boolean c = a1.contains("c");
				boolean d = a1.contains("d");
				boolean s = a1.contains("s");
				if (d) {
					if (s) {
						if (l.size() != 4)
							throw new XNScriptError("Can't understand arguments to "+functionName);
						TrPattern a = TrPattern.compile(a2, c);
						TrPattern b = TrPattern.compile(a3, false);
						tx = new Multiplexor(a.deletor(), b.squeezor());
					} else {
						if (l.size() != 3)
							throw new XNScriptError("Can't understand arguments to "+functionName);
						TrPattern a = TrPattern.compile(a2, c);
						tx = a.deletor();
					}
				} else {
					if (s) {
						if (l.size() == 3) {
							TrPattern a = TrPattern.compile(a2, c);
							tx = a.squeezor();
						} else {
							TrPattern a = TrPattern.compile(a2, c);
							TrPattern b = TrPattern.compile(a3, false);
							tx = new Multiplexor(TrPattern.translator(a, b), b.squeezor());
						}
					} else {
						if (l.size() != 4)
							throw new XNScriptError("Can't understand arguments to "+functionName);
						TrPattern a = TrPattern.compile(a2, c);
						TrPattern b = TrPattern.compile(a3, false);
						tx = TrPattern.translator(a, b);
					}
				}
			} else {
				if (l.size() != 3)
					throw new XNScriptError("Can't understand arguments to "+functionName);
				TrPattern a = TrPattern.compile(a1, false);
				TrPattern b = TrPattern.compile(a2, false);
				tx = TrPattern.translator(a, b);
			}
			return new XOMString(tx.transformFirst(str));
		}
	};
	
	private static final Function f_trreplaceall = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 3, 4, true);
			String str = l.get(0).toTextString(ctx);
			String a1 = l.get(1).toTextString(ctx);
			String a2 = l.get(2).toTextString(ctx);
			String a3 = (l.size() > 3) ? l.get(3).toTextString(ctx) : "";
			Transformor tx;
			if (a1.startsWith("-")) {
				boolean c = a1.contains("c");
				boolean d = a1.contains("d");
				boolean s = a1.contains("s");
				if (d) {
					if (s) {
						if (l.size() != 4)
							throw new XNScriptError("Can't understand arguments to "+functionName);
						TrPattern a = TrPattern.compile(a2, c);
						TrPattern b = TrPattern.compile(a3, false);
						tx = new Multiplexor(a.deletor(), b.squeezor());
					} else {
						if (l.size() != 3)
							throw new XNScriptError("Can't understand arguments to "+functionName);
						TrPattern a = TrPattern.compile(a2, c);
						tx = a.deletor();
					}
				} else {
					if (s) {
						if (l.size() == 3) {
							TrPattern a = TrPattern.compile(a2, c);
							tx = a.squeezor();
						} else {
							TrPattern a = TrPattern.compile(a2, c);
							TrPattern b = TrPattern.compile(a3, false);
							tx = new Multiplexor(TrPattern.translator(a, b), b.squeezor());
						}
					} else {
						if (l.size() != 4)
							throw new XNScriptError("Can't understand arguments to "+functionName);
						TrPattern a = TrPattern.compile(a2, c);
						TrPattern b = TrPattern.compile(a3, false);
						tx = TrPattern.translator(a, b);
					}
				}
			} else {
				if (l.size() != 3)
					throw new XNScriptError("Can't understand arguments to "+functionName);
				TrPattern a = TrPattern.compile(a1, false);
				TrPattern b = TrPattern.compile(a2, false);
				tx = TrPattern.translator(a, b);
			}
			return new XOMString(tx.transformAll(str));
		}
	};
	
	private static final Function f_trrinstr = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			String s = l.get(0).toTextString(ctx);
			String d = l.get(1).toTextString(ctx);
			Matchor m = TrPattern.compile(d, false).matchor();
			return new XOMInteger(m.findLastIn(s)+1);
		}
	};
	
	private static final Function f_unpack = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 2, true);
			List<DataField> format;
			try {
				format = new DataFormatParser(new StringReader(l.get(0).toTextString(ctx))).parseAuto();
			} catch (Exception e) {
				throw new XNScriptError("Invalid format string: " + e.getMessage());
			}
			byte[] data = XOMBinaryType.instance.makeInstanceFrom(ctx, l.get(1)).toByteArray();
			List<Object> things;
			try {
				things = new DataReader(format).unpack(new EndlessInputStream(new ByteArrayInputStream(data)), data.length);
			} catch (NumberFormatException e) {
				throw new XNScriptError("Expected number here");
			} catch (EOFException e) {
				throw new XNScriptError("Not enough data to unpack");
			} catch (Exception e) {
				throw new XNScriptError(e.getMessage());
			}
			return new XOMList(nativeToXOM(format, things, ctx));
		}
		private List<XOMVariant> nativeToXOM(List<DataField> format, List<Object> things, XNContext ctx) {
			List<XOMVariant> l = new ArrayList<XOMVariant>();
			Iterator<Object> ti = things.iterator();
			for (DataField df : format) {
				if (df.type().returns()) {
					Object o = ti.hasNext() ? ti.next() : null;
					l.add(nativeToXOMwithCount(df, o, ctx));
				}
			}
			return l;
		}
		private XOMVariant nativeToXOMwithCount(DataField df, Object o, XNContext ctx) {
			if (df.count() == null || df.type().usesCustomCount()) {
				return nativeToXOMwithoutCount(df, o, ctx);
			} else {
				List<?> things;
				if (o instanceof List) things = (List<?>)o;
				else { ArrayList<Object> tmp = new ArrayList<Object>(); tmp.add(o); things = tmp; }
				List<XOMVariant> l = new ArrayList<XOMVariant>();
				for (Object oi : things) l.add(nativeToXOMwithoutCount(df, oi, ctx));
				return new XOMList(l);
			}
		}
		@SuppressWarnings("unchecked")
		private XOMVariant nativeToXOMwithoutCount(DataField df, Object o, XNContext ctx) {
			switch (df.type()) {
			case BOOLEAN: return ((Boolean)o).booleanValue() ? XOMBoolean.TRUE : XOMBoolean.FALSE;
			case ENUM: return new XOMString(o.toString());
			case BITFIELD:
				List<Object> bfl = (List<Object>)o;
				StringBuffer bfs = new StringBuffer();
				for (Object oi : bfl) {
					bfs.append(oi.toString());
					bfs.append(',');
				}
				if (bfs.length() > 0 && bfs.charAt(bfs.length()-1) == ',') {
					bfs.deleteCharAt(bfs.length()-1);
				}
				return new XOMString(bfs.toString());
			case BINT: return new XOMString(o.toString());
			case OINT: return new XOMString(o.toString());
			case HINT: return new XOMString(o.toString());
			case UINT: return new XOMInteger((Number)o);
			case SINT: return new XOMInteger((Number)o);
			case UFIXED: return new XOMNumber((Number)o);
			case SFIXED: return new XOMNumber((Number)o);
			case FLOAT: return new XOMNumber((Number)o);
			case COMPLEX: return new XOMComplex(
					new XOMNumber(((Number[])o)[0]),
					new XOMNumber(((Number[])o)[1])
				);
			case CHAR: return new XOMString(o.toString());
			case PSTRING: return new XOMString(o.toString());
			case CSTRING: return new XOMString(o.toString());
			case DATE: return new XOMDate((GregorianCalendar)o);
			case COLOR: return new XOMColor((float[])o);
			case BINARY: return new XOMBinary((byte[])o);
			case STRUCT:
				List<DataField> sfmt = (List<DataField>)df.elaboration();
				List<Object> things;
				if (o instanceof List) things = (List<Object>)o;
				else { ArrayList<Object> tmp = new ArrayList<Object>(); tmp.add(o); things = tmp; }
				return new XOMList(nativeToXOM(sfmt, things, ctx));
			default: throw new XNScriptError("Unknown data type: " + df.type().toString());
			}
		}
	};
	
	private static final Function f_urldecode = new Function() {
		@SuppressWarnings("deprecation")
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to urldecode");
			else try {
				return new XOMString(URLDecoder.decode(parameter.toTextString(ctx), ctx.getTextEncoding()));
			} catch (UnsupportedEncodingException uee) {
				return new XOMString(URLDecoder.decode(parameter.toTextString(ctx)));
			}
		}
	};
	
	private static final Function f_urlencode = new Function() {
		@SuppressWarnings("deprecation")
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to urlencode");
			else try {
				return new XOMString(URLEncoder.encode(parameter.toTextString(ctx), ctx.getTextEncoding()));
			} catch (UnsupportedEncodingException uee) {
				return new XOMString(URLEncoder.encode(parameter.toTextString(ctx)));
			}
		}
	};
	
	private static final Function f_urlquerydecode = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null) throw new XNScriptError("Can't understand arguments to urlquerydecode");
			else {
				String queryString = parameter.toTextString(ctx);
				Map<String, XOMVariant> queryMap = XIONUtil.urlQueryDecode(ctx, queryString, ctx.getTextEncoding());
				return new XOMDictionary(queryMap);
			}
		}
	};
	
	private static final Function f_urlqueryencode = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			if (parameter == null || !XOMDictionaryType.instance.canMakeInstanceFrom(ctx, parameter)) {
				throw new XNScriptError("Can't understand arguments to urlqueryencode");
			} else {
				XOMDictionary dict = XOMDictionaryType.instance.makeInstanceFrom(ctx, parameter);
				String queryString = XIONUtil.urlQueryEncode(ctx, dict.toMap(), ctx.getTextEncoding());
				return new XOMString(queryString);
			}
		}
	};
	
	private static final Function f_vmname = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			if (!ctx.allow(XNSecurityKey.SYSTEM_INFO, "Function", functionName))
				throw new XNScriptError("Security settings do not allow vmName");
			return new XOMString(System.getProperty("java.vm.name"));
		}
	};
	
	private static final Function f_vmversion = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			if (!ctx.allow(XNSecurityKey.SYSTEM_INFO, "Function", functionName))
				throw new XNScriptError("Security settings do not allow vmVersion");
			return new XOMString(System.getProperty("java.vm.version"));
		}
	};
	
	private static final Function f_ygndecode = new Function() {
		private final Pattern XPATT = Pattern.compile("^=X([0-9A-F]+)=");
		private final Pattern BPATT = Pattern.compile("^(=B=)*=([ABQ])=");
		private final Pattern LPATT = Pattern.compile("^=([A-Z])=");
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 1, 2, true);
			boolean backslash = (l.size() > 1) ? (XOMBooleanType.instance.makeInstanceFrom(ctx, l.get(1)).toBoolean()) : false;
			String in = l.get(0).toTextString(ctx);
			StringBuffer out = new StringBuffer();
			while (in.length() > 0) {
				int a = in.indexOf('=');
				if (a < 0) {
					out.append(in);
					in = "";
					break;
				} else {
					out.append(in.substring(0, a));
					in = in.substring(a);
					Matcher m;
					if ((m = XPATT.matcher(in)).find() && m.start() == 0) {
						String ent = m.group(1);
						in = in.substring(m.end());
						out.append(Character.toChars(Integer.parseInt(ent, 16)));
					}
					else if (backslash && (m = BPATT.matcher(in)).find() && m.start() == 0) {
						char ent = m.group(2).charAt(0);
						in = in.substring(m.end());
						switch (ent) {
						case 'B': out.append('\\'); break;
						case 'A': out.append('\''); break;
						case 'Q': out.append('\"'); break;
						}
					}
					else if ((m = LPATT.matcher(in)).find() && m.start() == 0) {
						char ent = m.group(1).charAt(0);
						in = in.substring(m.end());
						switch (ent) {
						case 'B': out.append('\\'); break; // Backslash - escape character
						case 'A': out.append('\''); break; // Apostrophe - delimits strings
						case 'Q': out.append('\"'); break; // Quote - delimits strings
						case 'E': out.append('='); break; // Equals - escape character
						// LEVEL 0
						case 'P': out.append('%'); break; // Percent - SQL wildcard
						case 'U': out.append('_'); break; // Underscore - SQL wildcard
						case 'H': out.append('?'); break; // Huh - generic wildcard
						case 'S': out.append('*'); break; // Star - generic wildcard
						// LEVEL 1
						case 'L': out.append('<'); break; // Lessthan - HTML tag character
						case 'G': out.append('>'); break; // Greaterthan - HTML tag character
						case 'M': out.append('&'); break; // Mpersand - HTML entity character
						// LEVEL 2
						case 'X': out.append('!'); break; // Xclamation
						case 'O': out.append('#'); break; // Octothorpe
						case 'D': out.append('$'); break; // Dollar
						case 'N': out.append('+'); break;
						case 'F': out.append('/'); break; // Fraction
						case 'C': out.append(':'); break; // Colon 
						case 'K': out.append(';'); break; // Kindalikeacolon
						case 'I': out.append('['); break;
						case 'J': out.append(']'); break;
						case 'R': out.append('^'); break; // Raise
						case 'W': out.append('`'); break; // Which
						case 'Y': out.append('{'); break;
						case 'V': out.append('|'); break; // Verticalbar
						case 'Z': out.append('}'); break;
						case 'T': out.append('~'); break; // Tilde
						}
					}
					else {
						out.append(in.substring(0, 1));
						in = in.substring(1);
					}
				}
			}
			return new XOMString(out.toString());
		}
	};
	
	private static final Function f_ygnencode = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			List<? extends XOMVariant> l = listParameter(ctx, functionName, parameter, 1, 3, true);
			boolean backslash = (l.size() > 2) ? (XOMBooleanType.instance.makeInstanceFrom(ctx, l.get(2)).toBoolean()) : false;
			int level = (l.size() > 1) ? (XOMIntegerType.instance.makeInstanceFrom(ctx, l.get(1), true).toInt()) : 0;
			String in = l.get(0).toTextString(ctx);
			CharacterIterator it = new StringCharacterIterator(in);
			StringBuffer out = new StringBuffer();
			for (char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
				if (backslash && ch == '\\') {
					while (ch == '\\') {
						ch = it.next();
					}
					if (ch == CharacterIterator.DONE) {
						out.append("=B="); break;
					} else if (ch != '\'' && ch != '\"') {
						out.append("=B=");
					}
				}
				switch (ch) {
				case '\\': out.append("=B="); break; // Backslash - escape character
				case '\'': out.append("=A="); break; // Apostrophe - delimits strings
				case '\"': out.append("=Q="); break; // Quote - delimits strings
				case '=': out.append("=E="); break; // Equals - escape character
				// LEVEL 0
				case '%': if (level >= 0) out.append("=P="); else out.append(ch); break; // Percent - SQL wildcard
				case '_': if (level >= 0) out.append("=U="); else out.append(ch); break; // Underscore - SQL wildcard
				case '?': if (level >= 0) out.append("=H="); else out.append(ch); break; // Huh - generic wildcard
				case '*': if (level >= 0) out.append("=S="); else out.append(ch); break; // Star - generic wildcard
				// LEVEL 1
				case '<': if (level >= 1) out.append("=L="); else out.append(ch); break; // Lessthan - HTML tag character
				case '>': if (level >= 1) out.append("=G="); else out.append(ch); break; // Greaterthan - HTML tag character
				case '&': if (level >= 1) out.append("=M="); else out.append(ch); break; // Mpersand - HTML entity character
				// LEVEL 2
				case '!': if (level >= 2) out.append("=X="); else out.append(ch); break; // Xclamation
				case '#': if (level >= 2) out.append("=O="); else out.append(ch); break; // Octothorpe
				case '$': if (level >= 2) out.append("=D="); else out.append(ch); break; // Dollar
				case '+': if (level >= 2) out.append("=N="); else out.append(ch); break;
				case '/': if (level >= 2) out.append("=F="); else out.append(ch); break; // Fraction
				case ':': if (level >= 2) out.append("=C="); else out.append(ch); break; // Colon 
				case ';': if (level >= 2) out.append("=K="); else out.append(ch); break; // Kindalikeacolon
				case '[': if (level >= 2) out.append("=I="); else out.append(ch); break;
				case ']': if (level >= 2) out.append("=J="); else out.append(ch); break;
				case '^': if (level >= 2) out.append("=R="); else out.append(ch); break; // Raise
				case '`': if (level >= 2) out.append("=W="); else out.append(ch); break; // Which
				case '{': if (level >= 2) out.append("=Y="); else out.append(ch); break;
				case '|': if (level >= 2) out.append("=V="); else out.append(ch); break; // Verticalbar
				case '}': if (level >= 2) out.append("=Z="); else out.append(ch); break;
				case '~': if (level >= 2) out.append("=T="); else out.append(ch); break; // Tilde
				// LEVEL 3
				default:
					if (level >= 3 && (ch < 32 || ch >= 127)) {
						out.append("=X");
						out.append(Integer.toHexString((int)ch).toUpperCase());
						out.append("=");
					} else {
						out.append(ch);
					}
					break;
				}
			}
			return new XOMString(out.toString());
		}
	};
	
	private static final ExternalLanguage e_applescript = new ExternalLanguage() {
		public XOMVariant execute(String script) {
			String[] lines = script.split("\n|\r\n|\r|\u2028|\u2029");
			String[] args = new String[lines.length*2+1];
			args[0] = "osascript";
			for (int i = 0; i < lines.length; i++) {
				args[i*2+1] = "-e";
				args[i*2+2] = lines[i];
			}
			try {
				String result = XIONUtil.captureProcessOutput(args);
				if (result.endsWith("\r\n")) {
					result = result.substring(0, result.length()-2);
				} else if (result.endsWith("\n") || result.endsWith("\r")) {
					result = result.substring(0, result.length()-1);
				}
				return new XOMString(result);
			} catch (IOException e) {
				throw new XNScriptError("Error running AppleScript: "+e.getMessage());
			}
		}
	};
	
	private static final ExternalLanguage e_vbscript = new ExternalLanguage() {
		public XOMVariant execute(String script) {
			File tmp;
			try {
				tmp = File.createTempFile("XNExt-", ".vbs");
			} catch (IOException e2) {
				tmp = new File("XNExt-"+System.currentTimeMillis()+".vbs");
				tmp.deleteOnExit();
			}
			try {
				String[] lines = script.split("\n|\r\n|\r|\u2028|\u2029");
				PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(tmp), "UTF-8"), true);
				for (String line : lines) out.println(line);
				out.close();
				String result = XIONUtil.captureProcessOutput(new String[]{"cmd", "/c", "start", "\"X\"", tmp.getAbsolutePath()});
				if (result.endsWith("\r\n")) {
					result = result.substring(0, result.length()-2);
				} else if (result.endsWith("\n") || result.endsWith("\r")) {
					result = result.substring(0, result.length()-1);
				}
				return new XOMString(result);
			} catch (IOException e) {
				throw new XNScriptError("Error running Windows Scripting Host: "+e.getMessage());
			}
		}
	};
	
	private static final ExternalLanguage e_bash = new InterpreterLauncher("bash","sh");
	private static final ExternalLanguage e_perl = new InterpreterLauncher("perl","pl");
	private static final ExternalLanguage e_php = new InterpreterLauncher("php","php");
	private static final ExternalLanguage e_python = new InterpreterLauncher("python","py");
	private static final ExternalLanguage e_ruby = new InterpreterLauncher("ruby","rb");
	
	private static class InterpreterLauncher implements ExternalLanguage {
		private String lang;
		private String ext;
		public InterpreterLauncher(String lang, String ext) {
			this.lang = lang;
			this.ext = ext;
		}
		public XOMVariant execute(String script) {
			File tmp;
			try {
				tmp = File.createTempFile("XNExt-", "."+ext);
			} catch (IOException e2) {
				tmp = new File("XNExt-"+System.currentTimeMillis()+"."+ext);
				tmp.deleteOnExit();
			}
			try {
				String[] lines = script.split("\n|\r\n|\r|\u2028|\u2029");
				PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(tmp), "UTF-8"), true);
				for (String line : lines) out.println(line);
				out.close();
				String result = XIONUtil.captureProcessOutput(new String[]{lang, tmp.getAbsolutePath()});
				if (result.endsWith("\r\n")) {
					result = result.substring(0, result.length()-2);
				} else if (result.endsWith("\n") || result.endsWith("\r")) {
					result = result.substring(0, result.length()-1);
				}
				return new XOMString(result);
			} catch (IOException e) {
				throw new XNScriptError("Error running "+lang+": "+e.getMessage());
			}
		}
	}
}
