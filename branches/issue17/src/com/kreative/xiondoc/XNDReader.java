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

package com.kreative.xiondoc;

import java.io.File;
import java.util.*;
import java.util.regex.*;

public class XNDReader implements XIONDocReader {
	private static final Pattern ELEMENT_PATTERN = Pattern.compile("<([A-Za-z0-9:_-]+)(.*?)>(.*?)</\\1>", Pattern.DOTALL);
	private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile(" ([A-Za-z0-9:_-]+)=\"(.*?)\"", Pattern.DOTALL);
	
	public String derive(File f) {
		String fn = f.getAbsolutePath();
		return (fn.endsWith(".xnd") ? fn.substring(0, fn.length()-4) : fn);
	}
	
	public void read(String xnd, DocumentationSet d) {
		Matcher om = ELEMENT_PATTERN.matcher(xnd);
		while (om.find()) {
			//String ob = om.group(0);
			String ot = om.group(1);
			//String oa = om.group(2);
			String oc = om.group(3);
			if (ot.equalsIgnoreCase("documentationset")) {
				Matcher m = ELEMENT_PATTERN.matcher(oc);
				while (m.find()) {
					//String b = m.group(0);
					String t = m.group(1);
					//String a = m.group(2);
					String c = m.group(3);
					if (t.equalsIgnoreCase("dialect")) {
						Dialect dl = new Dialect();
						Matcher im = ELEMENT_PATTERN.matcher(c);
						while (im.find()) {
							//String ib = im.group(0);
							String it = im.group(1);
							//String ia = im.group(2);
							String ic = im.group(3);
							if (it.equalsIgnoreCase("name")) dl.setCode(htmldecode(ic.trim()));
							else if (it.equalsIgnoreCase("title")) dl.setTitle(htmldecode(ic.trim()));
							else if (it.equalsIgnoreCase("vers")) dl.setVersion(htmldecode(ic.trim()));
							else if (it.equalsIgnoreCase("summary")) dl.setSummary(htmldecode(ic.trim()));
							else if (it.equalsIgnoreCase("description")) dl.addDescription(htmldecode(ic.trim()));
							else if (it.equalsIgnoreCase("article")) {
								String name = null;
								String title = null;
								String summary = null;
								String content = null;
								Matcher am = ELEMENT_PATTERN.matcher(c);
								while (am.find()) {
									//String ab = am.group(0);
									String at = am.group(1);
									//String aa = am.group(2);
									String ac = am.group(3);
									if (at.equalsIgnoreCase("name")) name = htmldecode(ac.trim());
									else if (at.equalsIgnoreCase("title")) title = htmldecode(ac.trim());
									else if (at.equalsIgnoreCase("summary")) summary = htmldecode(ac.trim());
									else if (at.equalsIgnoreCase("content")) content = htmldecode(ac.trim());
									else System.err.println("Ignoring invalid tag <"+at+"> inside <"+it+">");
								}
								dl.addArticle(name, title, summary, content);
							}
							else System.err.println("Ignoring invalid tag <"+it+"> inside <"+t+">");
						}
						d.addDialect(dl);
					} else if (t.equalsIgnoreCase("summary")) {
						d.setSummary(htmldecode(c.trim()));
					} else if (t.equalsIgnoreCase("description")) {
						d.addDescription(htmldecode(c.trim()));
					} else if (t.equalsIgnoreCase("article")) {
						String name = null;
						String title = null;
						String summary = null;
						String content = null;
						Matcher am = ELEMENT_PATTERN.matcher(c);
						while (am.find()) {
							//String ab = am.group(0);
							String at = am.group(1);
							//String aa = am.group(2);
							String ac = am.group(3);
							if (at.equalsIgnoreCase("name")) name = htmldecode(ac.trim());
							else if (at.equalsIgnoreCase("title")) title = htmldecode(ac.trim());
							else if (at.equalsIgnoreCase("summary")) summary = htmldecode(ac.trim());
							else if (at.equalsIgnoreCase("content")) content = htmldecode(ac.trim());
							else System.err.println("Ignoring invalid tag <"+at+"> inside <"+t+">");
						}
						d.addArticle(name, title, summary, content);
					} else {
						Term.Type vt = Term.Type.forTagName(t);
						if (vt != null) {
							Term v = new Term(vt);
							Matcher im = ELEMENT_PATTERN.matcher(c);
							while (im.find()) {
								//String ib = im.group(0);
								String it = im.group(1);
								String ia = im.group(2);
								String ic = im.group(3);
								if (it.equalsIgnoreCase("name")) {
									String n = htmldecode(ic.trim());
									Set<Dialect> dl = new HashSet<Dialect>();
									Matcher am = ATTRIBUTE_PATTERN.matcher(ia);
									while (am.find()) {
										String at = am.group(1);
										String av = am.group(2);
										if (at.equalsIgnoreCase("dialects")) {
											String[] dialectNames = htmldecode(av.trim()).split(",");
											for (String dialectName : dialectNames) {
												Dialect dialect = d.getDialect(dialectName.trim());
												if (dialect != null) {
													dl.add(dialect);
												} else {
													System.err.println("Ignoring unknown dialect \""+dialectName.trim()+"\"");
												}
											}
										}
										else System.err.println("Ignoring invalid attribute \""+it+"\" of tag <"+it+">");
									}
									v.addName(n, dl);
								}
								else if (it.equalsIgnoreCase("precedence")) {
									String pn = htmldecode(ic.trim());
									Term.Precedence p = Term.Precedence.forString(pn);
									if (p != null) v.setPrecedence(p);
									else System.err.println("Ignoring invalid precedence name \""+pn+"\"");
								}
								else if (it.equalsIgnoreCase("description-short")) v.setShortDescription(htmldecode(ic.trim()));
								else if (it.equalsIgnoreCase("type")) v.setConstantType(htmldecode(ic.trim()));
								else if (it.equalsIgnoreCase("value")) v.setConstantValue(htmldecode(ic.trim()));
								else if (it.equalsIgnoreCase("applies-to")) v.getAppliesTo().add(htmldecode(ic.trim()));
								else if (it.equalsIgnoreCase("syntax")) v.getSyntax().add(htmldecode(ic.trim()));
								else if (it.equalsIgnoreCase("syntax-note")) v.getSyntaxNotes().add(htmldecode(ic.trim()));
								else if (it.equalsIgnoreCase("example")) v.getExamples().add(htmldecode(ic.trim()));
								else if (it.equalsIgnoreCase("description")) v.getDescription().add(htmldecode(ic.trim()));
								else if (it.equalsIgnoreCase("descriptors")) {
									String[] descs = htmldecode(ic.trim()).split("[,;]");
									for (String desc : descs) {
										Term.Descriptor dt = Term.Descriptor.forCode(desc.trim());
										if (dt != null) v.getDescriptors().add(dt);
										else System.err.println("Ignoring invalid descriptor type \""+desc.trim()+"\"");
									}
								}
								else if (it.equalsIgnoreCase("properties")) {
									String[] props = htmldecode(ic.trim()).split("[,;]");
									for (String prop : props) {
										v.getProperties().add(prop.trim());
									}
								}
								else if (it.equalsIgnoreCase("script")) v.getScripts().add(htmldecode(ic.trim()));
								else if (it.equalsIgnoreCase("note")) v.getNotes().add(htmldecode(ic.trim()));
								else if (it.equalsIgnoreCase("security")) v.getSecurity().add(htmldecode(ic.trim()));
								else if (it.equalsIgnoreCase("see-also")) {
									String[] sas = htmldecode(ic.trim()).split("[,;]");
									for (String sa : sas) {
										v.getSeeAlso().add(sa.trim());
									}
								}
								else System.err.println("Ignoring invalid tag <"+it+"> inside type <"+t+">");
							}
							d.addTerm(v);
							for (Dialect dl : v.getDialects()) {
								dl.addTerm(v);
							}
						} else {
							System.err.println("Ignoring invalid type <"+t+">");
						}
					}
				}
			} else {
				System.err.println("Ignoring invalid content <"+ot+">");
			}
		}
	}
	
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
	
	private String htmldecode(String in) {
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
		return out.toString();
	}
}
