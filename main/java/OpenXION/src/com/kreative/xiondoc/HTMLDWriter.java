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

package com.kreative.xiondoc;

import java.awt.Color;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

public class HTMLDWriter implements XIONDocWriter {
	public File derive(String path) {
		return new File(path.endsWith(".htmld") ? path : (path + ".htmld"));
	}
	
	private static void deltree(File f) {
		if (f.isDirectory()) {
			for (File ff : f.listFiles()) {
				deltree(ff);
			}
		}
		f.delete();
	}
	
	private static String htmlencode(String in) {
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
		return out.toString();
	}
	
	private static String format(String s, NameTermPair n, boolean inSyntax) {
		s = s.trim().replaceAll("\\s+", " ");
		s = htmlencode(s);
		s = s.replaceAll("\\{\\{\\{\\{\\{\\{(.*?)\\}\\}\\}\\}\\}\\}", "<h1>$1</h1>");
		s = s.replaceAll("\\{\\{\\{\\{\\{(.*?)\\}\\}\\}\\}\\}", "<h2>$1</h2>");
		s = s.replaceAll("\\{\\{\\{\\{(.*?)\\}\\}\\}\\}", "<h3>$1</h3>");
		s = s.replaceAll("\\{\\{\\{(.*?)\\}\\}\\}", "<b><i>$1</i></b>");
		s = s.replaceAll("\\{\\{(.*?)\\}\\}", "<b>$1</b>");
		s = s.replaceAll("\\{(.*?)\\}", "<i>$1</i>");
		
		Matcher mt = Pattern.compile("::TABLE (.*?) ELBAT::").matcher(s);
		StringBuffer st = new StringBuffer();
		while (mt.find()) {
			StringBuffer tableCode = new StringBuffer();
			String table = mt.group(1);
			tableCode.append("<table>");
			String[] rows = table.split(" \\.\\.\\.\\.\\.* ");
			for (String row : rows) {
				tableCode.append("<tr>");
				String[] cols = row.split(" :::* ");
				for (String col : cols) {
					tableCode.append("<td>");
					tableCode.append(col);
					tableCode.append("</td>");
				}
				tableCode.append("</tr>");
			}
			tableCode.append("</table>");
			mt.appendReplacement(st, tableCode.toString());
		}
		mt.appendTail(st);
		s = st.toString();
		
		Matcher m0 = Pattern.compile("\\[\\[\\*\\]\\]").matcher(s);
		StringBuffer s0 = new StringBuffer();
		while (m0.find()) {
			if (inSyntax) {
				m0.appendReplacement(s0, htmlencode(n.getName()));
			} else {
				m0.appendReplacement(s0, "<code><a href=\""+htmlencode(n.getTerm().getType().getCode())+".html#"+htmlencode(n.getName().toLowerCase())+"\">"+htmlencode(n.getName())+"</a></code>");
			}
		}
		m0.appendTail(s0);
		s = s0.toString();
		
		Matcher m1 = Pattern.compile("\\[\\[([a-z]+):(.*?)\\]\\]").matcher(s);
		StringBuffer s1 = new StringBuffer();
		while (m1.find()) {
			m1.appendReplacement(s1, "<code><a href=\"$1.html#"+m1.group(2).toLowerCase()+"\">$2</a></code>");
		}
		m1.appendTail(s1);
		s = s1.toString();
		
		s = s.replaceAll("\\[\\[(.*?)\\]\\]", "<code>$1</code>");
		s = s.replaceAll("\\\\\\\\","<br />");
		s = s.replaceAll("</code><code>","");
		s = s.replaceAll("</i><i>","");
		s = s.replaceAll("</b><b>","");
		s = s.replaceAll("</h3><h3>","");
		s = s.replaceAll("</h2><h2>","");
		s = s.replaceAll("</h1><h1>","");
		return s;
	}
	
	private static void writeDialectIndex(DocumentationSet d, File f) throws IOException {
		File outf = new File(f, "dialects.html");
		System.out.println("Creating "+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<html>");
		out.println("<head>");
		out.println("<title>XION Dialect Index</title>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xionnav.css\">");
		out.println("<script language=\"javascript\" type=\"text/javascript\">");
		out.println("<!--");
		out.println("function loadAllDialect() {");
		out.println("parent.xnvocabtypes.location.href='vocabtypes.html';");
		out.println("parent.xnvocab.location.href='all-index.html';");
		out.println("parent.xncontent.location.href='intro.html';");
		out.println("return false;");
		out.println("}");
		out.println("function loadDialect(x) {");
		out.println("parent.xnvocabtypes.location.href=x+'/vocabtypes.html';");
		out.println("parent.xnvocab.location.href=x+'/all-index.html';");
		out.println("parent.xncontent.location.href=x+'/intro.html';");
		out.println("return false;");
		out.println("}");
		out.println("//-->");
		out.println("</script>");
		out.println("</head>");
		out.println("<body>");
		out.println("<ul>");
		out.println("<li><a href=\"vocabtypes.html\" target=\"xnvocabtypes\" onclick=\"return loadAllDialect();\">All Dialects</a></li>");
		Iterator<Dialect> i = d.dialectIterator();
		while (i.hasNext()) {
			Dialect dl = i.next();
			out.println("<li><a href=\""+htmlencode(dl.getCode())+"/vocabtypes.html\" target=\"xnvocabtypes\" onclick=\"return loadDialect('"+htmlencode(dl.getCode())+"');\">"+htmlencode(dl.toString())+"</a></li>");
		}
		out.println("</ul>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private static void writeDialectIndex(Dialect d, File f) throws IOException {
		File outf = new File(f, "dialects.html");
		System.out.println("Creating "+f.getName()+"/"+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<html>");
		out.println("<head>");
		out.println("<title>XION Dialect Index</title>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xionnav.css\">");
		out.println("<script language=\"javascript\" type=\"text/javascript\">");
		out.println("<!--");
		out.println("function loadAllDialect() {");
		out.println("parent.xnvocabtypes.location.href='vocabtypes.html';");
		out.println("parent.xnvocab.location.href='all-index.html';");
		out.println("parent.xncontent.location.href='intro.html';");
		out.println("return false;");
		out.println("}");
		out.println("//-->");
		out.println("</script>");
		out.println("</head>");
		out.println("<body>");
		out.println("<ul>");
		out.println("<li><a href=\"vocabtypes.html\" target=\"xnvocabtypes\" onclick=\"return loadAllDialect();\">All Dialects</a></li>");
		out.println("<li><a href=\"vocabtypes.html\" target=\"xnvocabtypes\" onclick=\"return loadAllDialect();\">"+htmlencode(d.toString())+"</a></li>");
		out.println("</ul>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private static void writeVocabTypeIndex(DocumentationSet d, File f) throws IOException {
		File outf = new File(f, "vocabtypes.html");
		System.out.println("Creating "+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<html>");
		out.println("<head>");
		out.println("<title>XION Vocabulary Type Index</title>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xionnav.css\">");
		out.println("<script language=\"javascript\" type=\"text/javascript\">");
		out.println("<!--");
		out.println("function loadVocabType(x) {");
		out.println("parent.xnvocab.location.href=x+'-index.html';");
		out.println("parent.xncontent.location.href=x+'.html';");
		out.println("return false;");
		out.println("}");
		out.println("function loadAllVocab() {");
		out.println("parent.xnvocab.location.href='all-index.html';");
		out.println("parent.xncontent.location.href='intro.html';");
		out.println("return false;");
		out.println("}");
		out.println("//-->");
		out.println("</script>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>All Dialects</h1>");
		out.println("<ul>");
		out.println("<li><a href=\"all-index.html\" target=\"xnvocab\" onclick=\"return loadAllVocab();\">All Vocabulary</a></li>");
		for (Term.Type vt : Term.Type.values()) {
			if (d.termIterator(vt).hasNext()) {
				out.println("<li><a href=\""+htmlencode(vt.getCode())+"-index.html\" target=\"xnvocab\" onclick=\"return loadVocabType('"+htmlencode(vt.getCode())+"');\">"+htmlencode(vt.getPluralTitleCase())+"</a></li>");
			}
		}
		out.println("</ul>");
		if (d.articleIterator().hasNext()) {
			out.println("<ul>");
			Iterator<Article> ai = d.articleIterator();
			while (ai.hasNext()) {
				Article a = ai.next();
				out.println("<li><a href=\""+htmlencode(a.getCode())+".html\" target=\"xncontent\">"+htmlencode(a.getTitle())+"</a></li>");
			}
			out.println("</ul>");
		}
		out.println("<ul>");
		if (d.termIterator(Term.Type.CONSTANT).hasNext()) {
			out.println("<li><a href=\"constants.html\" target=\"xncontent\">Constant Summary</a></li>");
		}
		if (d.termIterator(Term.Type.OPERATOR).hasNext()) {
			out.println("<li><a href=\"precedence.html\" target=\"xncontent\">Operator Precedence Table</a></li>");
		}
		{
			boolean iHasASinanim = false;
			Iterator<NameTermPair> vi = d.termIterator();
			while (vi.hasNext()) {
				if (vi.next().getTerm().getNames().size() > 1) {
					iHasASinanim = true;
					break;
				}
			}
			if (iHasASinanim) {
				out.println("<li><a href=\"synonyms.html\" target=\"xncontent\">Synonyms</a></li>");
			}
		}
		{
			boolean iHasAColor = false;
			Iterator<NameTermPair> vi = d.termIterator(Term.Type.CONSTANT);
			while (vi.hasNext()) {
				for (String s : vi.next().getTerm().getDescription()) {
					if (s.trim().matches("A \\[\\[dt:color\\]\\] with RGB values [0-9]+, [0-9]+, [0-9]+\\.")) {
						iHasAColor = true;
					}
				}
			}
			if (iHasAColor) {
				out.println("<li><a href=\"colors.html\" target=\"xncontent\">Color Chart</a></li>");
			}
		}
		out.println("<li><a href=\"index-a.html\" target=\"xncontent\">Index</a></li>");
		out.println("</ul>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private static void writeVocabTypeIndex(Dialect d, File f) throws IOException {
		File outf = new File(f, "vocabtypes.html");
		System.out.println("Creating "+f.getName()+"/"+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<html>");
		out.println("<head>");
		out.println("<title>"+htmlencode(d.getTitle())+" Vocabulary Type Index</title>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xionnav.css\">");
		out.println("<script language=\"javascript\" type=\"text/javascript\">");
		out.println("<!--");
		out.println("function loadVocabType(x) {");
		out.println("parent.xnvocab.location.href=x+'-index.html';");
		out.println("parent.xncontent.location.href=x+'.html';");
		out.println("return false;");
		out.println("}");
		out.println("function loadAllVocab() {");
		out.println("parent.xnvocab.location.href='all-index.html';");
		out.println("parent.xncontent.location.href='intro.html';");
		out.println("return false;");
		out.println("}");
		out.println("//-->");
		out.println("</script>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>"+htmlencode(d.toString())+"</h1>");
		out.println("<ul>");
		out.println("<li><a href=\"all-index.html\" target=\"xnvocab\" onclick=\"return loadAllVocab();\">All Vocabulary</a></li>");
		for (Term.Type vt : Term.Type.values()) {
			if (d.termIterator(vt).hasNext()) {
				out.println("<li><a href=\""+htmlencode(vt.getCode())+"-index.html\" target=\"xnvocab\" onclick=\"return loadVocabType('"+htmlencode(vt.getCode())+"');\">"+htmlencode(vt.getPluralTitleCase())+"</a></li>");
			}
		}
		out.println("</ul>");
		if (d.articleIterator().hasNext()) {
			out.println("<ul>");
			Iterator<Article> ai = d.articleIterator();
			while (ai.hasNext()) {
				Article a = ai.next();
				out.println("<li><a href=\""+htmlencode(a.getCode())+".html\" target=\"xncontent\">"+htmlencode(a.getTitle())+"</a></li>");
			}
			out.println("</ul>");
		}
		out.println("<ul>");
		if (d.termIterator(Term.Type.CONSTANT).hasNext()) {
			out.println("<li><a href=\"constants.html\" target=\"xncontent\">Constant Summary</a></li>");
		}
		if (d.termIterator(Term.Type.OPERATOR).hasNext()) {
			out.println("<li><a href=\"precedence.html\" target=\"xncontent\">Operator Precedence Table</a></li>");
		}
		{
			boolean iHasASinanim = false;
			Iterator<NameTermPair> vi = d.termIterator();
			while (vi.hasNext()) {
				if (vi.next().getTerm().getNames(d).size() > 1) {
					iHasASinanim = true;
					break;
				}
			}
			if (iHasASinanim) {
				out.println("<li><a href=\"synonyms.html\" target=\"xncontent\">Synonyms</a></li>");
			}
		}
		{
			boolean iHasAColor = false;
			Iterator<NameTermPair> vi = d.termIterator(Term.Type.CONSTANT);
			while (vi.hasNext()) {
				for (String s : vi.next().getTerm().getDescription()) {
					if (s.trim().matches("A \\[\\[dt:color\\]\\] with RGB values [0-9]+, [0-9]+, [0-9]+\\.")) {
						iHasAColor = true;
					}
				}
			}
			if (iHasAColor) {
				out.println("<li><a href=\"colors.html\" target=\"xncontent\">Color Chart</a></li>");
			}
		}
		out.println("<li><a href=\"index-a.html\" target=\"xncontent\">Index</a></li>");
		out.println("</ul>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private static void writeAllIndex(DocumentationSet d, File f) throws IOException {
		File outf = new File(f, "all-index.html");
		System.out.println("Creating "+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<html>");
		out.println("<head>");
		out.println("<title>XION Vocabulary Index</title>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xionnav.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>All Vocabulary</h1>");
		out.println("<ul class=\"code\">");
		Iterator<NameTermPair> i = d.termIterator();
		while (i.hasNext()) {
			NameTermPair e = i.next();
			out.println("<li class=\"code\"><a href=\""+htmlencode(e.getTerm().getType().getCode())+".html#"+htmlencode(e.getName().toLowerCase())+"\" target=\"xncontent\">"+htmlencode(e.getName())+"</a> <span class=\"expl\">("+htmlencode(e.getTerm().getType().getSingular())+")</span></li>");
		}
		out.println("</ul>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private static void writeAllIndex(Dialect d, File f) throws IOException {
		File outf = new File(f, "all-index.html");
		System.out.println("Creating "+f.getName()+"/"+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<html>");
		out.println("<head>");
		out.println("<title>"+htmlencode(d.getTitle())+" Vocabulary Index</title>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xionnav.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>All Vocabulary</h1>");
		out.println("<ul class=\"code\">");
		Iterator<NameTermPair> i = d.termIterator();
		while (i.hasNext()) {
			NameTermPair e = i.next();
			out.println("<li class=\"code\"><a href=\""+htmlencode(e.getTerm().getType().getCode())+".html#"+htmlencode(e.getName().toLowerCase())+"\" target=\"xncontent\">"+htmlencode(e.getName())+"</a> <span class=\"expl\">("+htmlencode(e.getTerm().getType().getSingular())+")</span></li>");
		}
		out.println("</ul>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private static void writeIndex(DocumentationSet d, Term.Type vt, File f) throws IOException {
		File outf = new File(f, vt.getCode()+"-index.html");
		System.out.println("Creating "+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<html>");
		out.println("<head>");
		out.println("<title>XION "+htmlencode(vt.getSingularTitleCase())+" Index</title>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xionnav.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>"+htmlencode(vt.getPluralTitleCase())+"</h1>");
		out.println("<ul class=\"code\">");
		Iterator<NameTermPair> i = d.termIterator(vt);
		while (i.hasNext()) {
			NameTermPair e = i.next();
			out.println("<li class=\"code\"><a href=\""+htmlencode(vt.getCode())+".html#"+htmlencode(e.getName().toLowerCase())+"\" target=\"xncontent\">"+htmlencode(e.getName())+"</a></li>");
		}
		out.println("</ul>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private static void writeIndex(Dialect d, Term.Type vt, File f) throws IOException {
		File outf = new File(f, vt.getCode()+"-index.html");
		System.out.println("Creating "+f.getName()+"/"+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<html>");
		out.println("<head>");
		out.println("<title>"+htmlencode(d.getTitle())+" "+htmlencode(vt.getSingularTitleCase())+" Index</title>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xionnav.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>"+htmlencode(vt.getPluralTitleCase())+"</h1>");
		out.println("<ul class=\"code\">");
		Iterator<NameTermPair> i = d.termIterator(vt);
		while (i.hasNext()) {
			NameTermPair e = i.next();
			out.println("<li class=\"code\"><a href=\""+htmlencode(vt.getCode())+".html#"+htmlencode(e.getName().toLowerCase())+"\" target=\"xncontent\">"+htmlencode(e.getName())+"</a></li>");
		}
		out.println("</ul>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private static void writeSuperIndex(DocumentationSet d, File f) throws IOException {
		File[] outf = new File[27];
		outf[0] = new File(f, "index-symb.html");
		for (int i = 1, ch = 'a'; i < outf.length && ch <= 'z'; i++, ch++)
			outf[i] = new File(f, "index-"+(char)ch+".html");
		System.out.println("Creating index-*.html...");
		PrintWriter[] out = new PrintWriter[27];
		for (int i = 0; i < outf.length && i < out.length; i++)
			out[i] = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf[i]), "UTF-8"), true);
		for (int i = 0; i < out.length; i++) {
			out[i].println("<html>");
			out[i].println("<head>");
			out[i].println("<title>XION Vocabulary Index - "+(i == 0 ? "Symbols" : ""+(i-1+'A'))+"</title>");
			out[i].println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xiondoc.css\">");
			out[i].println("</head>");
			out[i].println("<body>");
			out[i].println("<h1>XION Vocabulary Index</h1>");
			out[i].println("<p class=\"indexnav\">" +
					"<a href=\"index-a.html\">A</a> - " +
					"<a href=\"index-b.html\">B</a> - " +
					"<a href=\"index-c.html\">C</a> - " +
					"<a href=\"index-d.html\">D</a> - " +
					"<a href=\"index-e.html\">E</a> - " +
					"<a href=\"index-f.html\">F</a> - " +
					"<a href=\"index-g.html\">G</a> - " +
					"<a href=\"index-h.html\">H</a> - " +
					"<a href=\"index-i.html\">I</a> - " +
					"<a href=\"index-j.html\">J</a> - " +
					"<a href=\"index-k.html\">K</a> - " +
					"<a href=\"index-l.html\">L</a> - " +
					"<a href=\"index-m.html\">M</a> - " +
					"<a href=\"index-n.html\">N</a> - " +
					"<a href=\"index-o.html\">O</a> - " +
					"<a href=\"index-p.html\">P</a> - " +
					"<a href=\"index-q.html\">Q</a> - " +
					"<a href=\"index-r.html\">R</a> - " +
					"<a href=\"index-s.html\">S</a> - " +
					"<a href=\"index-t.html\">T</a> - " +
					"<a href=\"index-u.html\">U</a> - " +
					"<a href=\"index-v.html\">V</a> - " +
					"<a href=\"index-w.html\">W</a> - " +
					"<a href=\"index-x.html\">X</a> - " +
					"<a href=\"index-y.html\">Y</a> - " +
					"<a href=\"index-z.html\">Z</a> - " +
					"<a href=\"index-symb.html\">#</a>" +
					"</p>");
			out[i].println("<table>");
			out[i].println("<tr><th>Term</th><th>Type</th></tr>");
		}
		Iterator<NameTermPair> vi = d.termIterator();
		while (vi.hasNext()) {
			NameTermPair e = vi.next();
			char ch = e.getName().charAt(0);
			int i = (ch >= 'a' && ch <= 'z') ? (ch-'a'+1) : (ch >= 'A' && ch <= 'Z') ? (ch-'A'+1) : 0;
			out[i].println(
					"<tr>" +
					"<td class=\"code\">" +
					"<a href=\"" +
					htmlencode(e.getTerm().getType().getCode()) +
					".html#" +
					htmlencode(e.getName().toLowerCase()) +
					"\">" +
					htmlencode(e.getName()) +
					"</a>" +
					"</td>" +
					"<td>" +
					htmlencode(e.getTerm().getType().getSingular()) +
					"</td>" +
					"</tr>");
		}
		for (int i = 0; i < out.length; i++) {
			out[i].println("</table>");
			out[i].println("</body>");
			out[i].println("</html>");
			out[i].close();
		}
	}
	
	private static void writeSuperIndex(Dialect d, File f) throws IOException {
		File[] outf = new File[27];
		outf[0] = new File(f, "index-symb.html");
		for (int i = 1, ch = 'a'; i < outf.length && ch <= 'z'; i++, ch++)
			outf[i] = new File(f, "index-"+(char)ch+".html");
		System.out.println("Creating "+f.getName()+"/index-*.html...");
		PrintWriter[] out = new PrintWriter[27];
		for (int i = 0; i < outf.length && i < out.length; i++)
			out[i] = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf[i]), "UTF-8"), true);
		for (int i = 0; i < out.length; i++) {
			out[i].println("<html>");
			out[i].println("<head>");
			out[i].println("<title>"+htmlencode(d.getTitle())+" Vocabulary Index - "+(i == 0 ? "Symbols" : ""+(i-1+'A'))+"</title>");
			out[i].println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xiondoc.css\">");
			out[i].println("</head>");
			out[i].println("<body>");
			out[i].println("<h1>"+htmlencode(d.getTitle())+" Vocabulary Index</h1>");
			out[i].println("<p class=\"indexnav\">" +
					"<a href=\"index-a.html\">A</a> - " +
					"<a href=\"index-b.html\">B</a> - " +
					"<a href=\"index-c.html\">C</a> - " +
					"<a href=\"index-d.html\">D</a> - " +
					"<a href=\"index-e.html\">E</a> - " +
					"<a href=\"index-f.html\">F</a> - " +
					"<a href=\"index-g.html\">G</a> - " +
					"<a href=\"index-h.html\">H</a> - " +
					"<a href=\"index-i.html\">I</a> - " +
					"<a href=\"index-j.html\">J</a> - " +
					"<a href=\"index-k.html\">K</a> - " +
					"<a href=\"index-l.html\">L</a> - " +
					"<a href=\"index-m.html\">M</a> - " +
					"<a href=\"index-n.html\">N</a> - " +
					"<a href=\"index-o.html\">O</a> - " +
					"<a href=\"index-p.html\">P</a> - " +
					"<a href=\"index-q.html\">Q</a> - " +
					"<a href=\"index-r.html\">R</a> - " +
					"<a href=\"index-s.html\">S</a> - " +
					"<a href=\"index-t.html\">T</a> - " +
					"<a href=\"index-u.html\">U</a> - " +
					"<a href=\"index-v.html\">V</a> - " +
					"<a href=\"index-w.html\">W</a> - " +
					"<a href=\"index-x.html\">X</a> - " +
					"<a href=\"index-y.html\">Y</a> - " +
					"<a href=\"index-z.html\">Z</a> - " +
					"<a href=\"index-symb.html\">#</a>" +
					"</p>");
			out[i].println("<table>");
			out[i].println("<tr><th>Term</th><th>Type</th></tr>");
		}
		Iterator<NameTermPair> vi = d.termIterator();
		while (vi.hasNext()) {
			NameTermPair e = vi.next();
			char ch = e.getName().charAt(0);
			int i = (ch >= 'a' && ch <= 'z') ? (ch-'a'+1) : (ch >= 'A' && ch <= 'Z') ? (ch-'A'+1) : 0;
			out[i].println(
					"<tr>" +
					"<td class=\"code\">" +
					"<a href=\"" +
					htmlencode(e.getTerm().getType().getCode()) +
					".html#" +
					htmlencode(e.getName().toLowerCase()) +
					"\">" +
					htmlencode(e.getName()) +
					"</a>" +
					"</td>" +
					"<td>" +
					htmlencode(e.getTerm().getType().getSingular()) +
					"</td>" +
					"</tr>");
		}
		for (int i = 0; i < out.length; i++) {
			out[i].println("</table>");
			out[i].println("</body>");
			out[i].println("</html>");
			out[i].close();
		}
	}
	
	private static void writeVocabularyItem(DocumentationSet ds, Dialect dl, NameTermPair n, PrintWriter out) throws IOException {
		if (n.getName() != null && n.getName().length() > 0) {
			out.print("<a name=\""+htmlencode(n.getName().toLowerCase())+"\">");
			out.print("<h2>"+htmlencode(n.getName())+"</h2>");
			out.println("</a>");
		}
		if (n.getTerm().getDialects(n.getName()).size() > 0) {
			out.println("<h3>Supported By</h3>");
			out.println("<ul>");
			Collection<Dialect> supported = n.getTerm().getDialects(n.getName());
			Iterator<Dialect> di = ds.dialectIterator();
			while (di.hasNext()) {
				Dialect d = di.next();
				if (supported.contains(d)) {
					out.println("<li>"+htmlencode(d.toString())+"</li>");
				}
			}
			out.println("</ul>");
		}
		/*
		if (n.getTerm().getConstantValue() != null) {
			out.println("<h3>Value</h3>");
			out.print("<p><code>"+htmlencode(n.getTerm().getConstantValue())+"</code>");
			if (n.getTerm().getConstantType() != null) {
				out.print(" &nbsp; &nbsp; &nbsp; (<a class=\"code\" href=\"dt.html#"+htmlencode(n.getTerm().getConstantType().toLowerCase())+"\">"+htmlencode(n.getTerm().getConstantType())+"</a>)");
			}
			out.println("</p>");
		}
		if (n.getTerm().getPrecedence() != null) {
			out.println("<h3>Precedence</h3>");
			out.println("<p>"+n.getTerm().getPrecedence().getNumber()+" - "+htmlencode(n.getTerm().getPrecedence().getName())+"</p>");
		}
		*/
		if (n.getTerm().getAppliesTo() != null && n.getTerm().getAppliesTo().size() > 0) {
			out.println("<h3>Applies To</h3>");
			for (String p : n.getTerm().getAppliesTo()) {
				out.println("<p>"+format(p,n,false)+"</p>");
			}
		}
		if (n.getTerm().getSyntax() != null && n.getTerm().getSyntax().size() > 0 || n.getTerm().getSyntaxNotes() != null && n.getTerm().getSyntaxNotes().size() > 0) {
			out.println("<h3>Syntax</h3>");
			if (n.getTerm().getSyntax() != null && n.getTerm().getSyntax().size() > 0) {
				out.println("<ul class=\"code\">");
				for (String p : n.getTerm().getSyntax()) {
					out.println("<li class=\"code\">"+format(p,n,true)+"</li>");
				}
				out.println("</ul>");
			}
			if (n.getTerm().getSyntaxNotes() != null && n.getTerm().getSyntaxNotes().size() > 0) {
				for (String p : n.getTerm().getSyntaxNotes()) {
					out.println("<p>"+format(p,n,false)+"</p>");
				}
			}
		}
		if (n.getTerm().getExamples() != null && n.getTerm().getExamples().size() > 0) {
			out.println((n.getTerm().getExamples().size() == 1) ? "<h3>Example</h3>" : "<h3>Examples</h3>");
			out.println("<ul class=\"code\">");
			for (String p : n.getTerm().getExamples()) {
				out.println("<li class=\"code\">"+format(p,n,true)+"</li>");
			}
			out.println("</ul>");
		}
		if (n.getTerm().getDescription() != null && n.getTerm().getDescription().size() > 0) {
			out.println("<h3>Description</h3>");
			for (String p : n.getTerm().getDescription()) {
				out.println("<p>"+format(p,n,false)+"</p>");
			}
		}
		if (n.getTerm().getDescriptors() != null && n.getTerm().getDescriptors().size() > 0) {
			out.println("<h3>Descriptors</h3>");
			out.println("<ul>");
			for (Term.Descriptor dt : Term.Descriptor.values()) {
				if (n.getTerm().getDescriptors().contains(dt)) {
					String example = dt.getExample(n.getName());
					if (example.contains("steve")) {
						example = "put "+example+" into bill";
					} else {
						example = "put "+example+" into steve";
					}
					out.println("<li>"+htmlencode(dt.getName())+": <code>"+htmlencode(example)+"</code></li>");
				}
			}
			out.println("</ul>");
		}
		Collection<String> pr = (dl == null) ? n.getTerm().getProperties() : n.getTerm().getProperties(dl);
		if (pr != null && pr.size() > 0) {
			out.println("<h3>Properties</h3>");
			out.print("<p>");
			boolean first = true;
			for (String prop : pr) {
				if (first) first = false;
				else out.print(", ");
				out.print("<code><a href=\"pr.html#"+htmlencode(prop.toLowerCase())+"\">"+htmlencode(prop)+"</a></code>");
			}
			out.println("</p>");
		}
		if (n.getTerm().getScripts() != null && n.getTerm().getScripts().size() > 0) {
			out.println((n.getTerm().getScripts().size() == 1) ? "<h3>Script</h3>" : "<h3>Scripts</h3>");
			for (String p : n.getTerm().getScripts()) {
				out.println("<p>"+format(p,n,false)+"</p>");
			}
		}
		if (n.getTerm().getNotes() != null && n.getTerm().getNotes().size() > 0) {
			out.println((n.getTerm().getNotes().size() == 1) ? "<h3>Note</h3>" : "<h3>Notes</h3>");
			for (String p : n.getTerm().getNotes()) {
				out.println("<p>"+format(p,n,false)+"</p>");
			}
		}
		if (n.getTerm().getSecurity() != null && n.getTerm().getSecurity().size() > 0) {
			out.println("<h3>Security</h3>");
			for (String p : n.getTerm().getSecurity()) {
				out.println("<p>"+format(p,n,false)+"</p>");
			}
		}
		Collection<String> sy = (dl == null) ? n.getTerm().getNames() : n.getTerm().getNames(dl);
		if (sy != null && sy.size() > 1) {
			out.println("<h3>Synonyms</h3>");
			out.print("<p>");
			boolean first = true;
			for (String syn : sy) {
				if (syn.equalsIgnoreCase(n.getName())) continue;
				if (first) first = false;
				else out.print(", ");
				out.print("<code><a href=\""+htmlencode(n.getTerm().getType().getCode())+".html#"+htmlencode(syn.toLowerCase())+"\">"+htmlencode(syn)+"</a></code>");
			}
			out.println("</p>");
		}
		Collection<String> sa = (dl == null) ? n.getTerm().getSeeAlso() : n.getTerm().getSeeAlso(dl);
		if (sa != null && sa.size() > 0) {
			out.println("<h3>See Also</h3>");
			out.print("<p>");
			boolean first = true;
			for (String sa0 : sa) {
				if (first) first = false;
				else out.print(", ");
				out.print(format(sa0,n,false));
			}
			out.println("</p>");
		}
	}
	
	private static void writeArticle(Article a, File f, boolean inside) throws IOException {
		File outf = new File(f, a.getCode()+".html");
		System.out.println("Creating "+(inside?(f.getName()+"/"):"")+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<html>");
		out.println("<head>");
		out.println("<title>"+htmlencode(a.getTitle())+"</title>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xiondoc.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>"+htmlencode(a.getTitle())+"</h1>");
		String c = "<p>\n"+format(a.getContent(), null, false)+"\n</p>";
		c = c.replaceAll("<br />[ \t\n\r]*<br />", "</p>\n<p>");
		c = c.replaceAll("<h1>", "</p>\n<h1>");
		c = c.replaceAll("<h2>", "</p>\n<h2>");
		c = c.replaceAll("<h3>", "</p>\n<h3>");
		c = c.replaceAll("<table", "</p>\n<table");
		c = c.replaceAll("</h1>", "</h1>\n<p>");
		c = c.replaceAll("</h2>", "</h2>\n<p>");
		c = c.replaceAll("</h3>", "</h3>\n<p>");
		c = c.replaceAll("</table>", "</table>\n<p>");
		c = c.replaceAll("<p>[ \t\n\r]*</p>", "");
		c = c.replaceAll("[\n\r]+", "\n");
		out.println(c);
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private static void writeChapter(DocumentationSet d, Term.Type vt, File f) throws IOException {
		File outf = new File(f, vt.getCode()+".html");
		System.out.println("Creating "+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<html>");
		out.println("<head>");
		out.println("<title>XION "+htmlencode(vt.getPluralTitleCase())+"</title>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xiondoc.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>"+htmlencode(vt.getPluralTitleCase())+"</h1>");
		out.println("<p>This page describes the "+htmlencode(vt.getPlural())+" supported by all XION dialects in this documentation set.</p>");
		Iterator<NameTermPair> i = d.termIterator(vt);
		while (i.hasNext()) {
			NameTermPair n = i.next();
			writeVocabularyItem(d, null, n, out);
		}
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private static void writeChapter(DocumentationSet ds, Dialect d, Term.Type vt, File f) throws IOException {
		File outf = new File(f, vt.getCode()+".html");
		System.out.println("Creating "+f.getName()+"/"+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<html>");
		out.println("<head>");
		out.println("<title>"+htmlencode(d.getTitle())+" "+htmlencode(vt.getPluralTitleCase())+"</title>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xiondoc.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>"+htmlencode(vt.getPluralTitleCase())+"</h1>");
		out.println("<p>This page describes the "+htmlencode(vt.getPlural())+" supported by "+htmlencode(d.getTitle())+".</p>");
		Iterator<NameTermPair> i = d.termIterator(vt);
		while (i.hasNext()) {
			NameTermPair n = i.next();
			writeVocabularyItem(ds, d, n, out);
		}
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private static void writeSynonyms(DocumentationSet d, File f) throws IOException {
		File outf = new File(f, "synonyms.html");
		System.out.println("Creating "+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<html>");
		out.println("<head>");
		out.println("<title>XION Synonyms</title>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xiondoc.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>XION Synonyms</h1>");
		out.println("<p>The table below lists the alternative ways that XION terms can be used.</p>");
		out.println("<table class=\"linedrowgroups\">");
		out.println("<thead class=\"tbfirst\"><tr class=\"trfirst\"><th>Synonym</th><th>Term</th></tr></thead>");
		List<Term> terms = new Vector<Term>();
		Iterator<NameTermPair> it = d.termIterator();
		while (it.hasNext()) {
			Term t = it.next().getTerm();
			if (t.getNames().size() > 1) {
				if (!terms.contains(t)) {
					terms.add(t);
				}
			}
		}
		Collections.sort(terms, new Comparator<Term>() {
			public int compare(Term a, Term b) {
				String an = null;
				String bn = null;
				for (String ani : a.getNames()) {
					if (an == null || ani.compareToIgnoreCase(an) < 0) an = ani;
				}
				for (String bni : b.getNames()) {
					if (bn == null || bni.compareToIgnoreCase(bn) < 0) bn = bni;
				}
				return an.compareToIgnoreCase(bn);
			}
		});
		for (Term t : terms) {
			String vt = t.getType().getCode();
			out.print("<tbody class=\"tbrest\"><tr class=\"trrest\">");
			int numWritten = 0;
			List<String> words = new Vector<String>();
			words.addAll(t.getNames());
			Collections.sort(words, String.CASE_INSENSITIVE_ORDER);
			for (String word : words) {
				if (numWritten > 0 && (numWritten % 4) == 0) {
					out.println("</tr>");
					out.print("<tr class=\"trrest\">");
				}
				out.print("<td class=\"code\"><a href=\""+htmlencode(vt)+".html#"+htmlencode(word.toLowerCase())+"\">"+htmlencode(word)+"</a></td>");
				numWritten++;
			}
			out.println("</tr></tbody>");
		}
		out.println("</table>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private static void writeSynonyms(Dialect d, File f) throws IOException {
		File outf = new File(f, "synonyms.html");
		System.out.println("Creating "+f.getName()+"/"+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<html>");
		out.println("<head>");
		out.println("<title>"+htmlencode(d.getTitle())+" Synonyms</title>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xiondoc.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>"+htmlencode(d.getTitle())+" Synonyms</h1>");
		out.println("<p>The table below lists the alternative ways that "+htmlencode(d.getTitle())+" terms can be used.</p>");
		out.println("<table class=\"linedrowgroups\">");
		out.println("<thead class=\"tbfirst\"><tr class=\"trfirst\"><th>Synonym</th><th>Term</th></tr></thead>");
		List<Term> terms = new Vector<Term>();
		Iterator<NameTermPair> it = d.termIterator();
		while (it.hasNext()) {
			Term t = it.next().getTerm();
			if (t.getNames(d).size() > 1) {
				if (!terms.contains(t)) {
					terms.add(t);
				}
			}
		}
		final Dialect dd = d;
		Collections.sort(terms, new Comparator<Term>() {
			public int compare(Term a, Term b) {
				String an = null;
				String bn = null;
				for (String ani : a.getNames(dd)) {
					if (an == null || ani.compareToIgnoreCase(an) < 0) an = ani;
				}
				for (String bni : b.getNames(dd)) {
					if (bn == null || bni.compareToIgnoreCase(bn) < 0) bn = bni;
				}
				return an.compareToIgnoreCase(bn);
			}
		});
		for (Term t : terms) {
			String vt = t.getType().getCode();
			out.print("<tbody class=\"tbrest\"><tr class=\"trrest\">");
			int numWritten = 0;
			List<String> words = new Vector<String>();
			words.addAll(t.getNames(d));
			Collections.sort(words, String.CASE_INSENSITIVE_ORDER);
			for (String word : words) {
				if (numWritten > 0 && (numWritten % 4) == 0) {
					out.println("</tr>");
					out.print("<tr class=\"trrest\">");
				}
				out.print("<td class=\"code\"><a href=\""+htmlencode(vt)+".html#"+htmlencode(word.toLowerCase())+"\">"+htmlencode(word)+"</a></td>");
				numWritten++;
			}
			out.println("</tr></tbody>");
		}
		out.println("</table>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private static void writeConstants(DocumentationSet d, File f) throws IOException {
		File outf = new File(f, "constants.html");
		System.out.println("Creating "+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<html>");
		out.println("<head>");
		out.println("<title>XION Constant Summary</title>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xiondoc.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>XION Constant Summary</h1>");
		out.println("<p>This page summarizes XION's built-in constants." +
				" A constant is a named value that never changes." +
				" You cannot change its value or use its name as a variable name." +
				" If you try, the interpreter will trigger a script error.</p>");
		
		Vector<NameTermPair> c = new Vector<NameTermPair>();
		out.println("<p><a name=\"byname\">The table below lists all the built-in constants by name." +
				" See also <a href=\"#bytype\">by data type</a> and <a href=\"#byvalue\">by value</a>.</a></p>");
		out.println("<table class=\"linedrows\">");
		out.println("<thead class=\"tbfirst\">\n<tr class=\"trfirst\">\n<th>Constant Name</th>\n<th>Data Type</th>\n<th>Value</th>\n</tr>\n</thead>");
		out.println("<tbody class=\"tbrest\">");
		Iterator<NameTermPair> i = d.termIterator(Term.Type.CONSTANT);
		while (i.hasNext()) {
			NameTermPair n = i.next();
			Term t = n.getTerm();
			out.println("<tr class=\"trrest\">");
			out.println("<td class=\"code\"><a class=\"code\" href=\"cn.html#"+htmlencode(n.getName().toLowerCase())+"\">"+htmlencode(n.getName())+"</a></td>");
			if (t.getConstantType() != null) {
				out.println("<td class=\"code\"><a class=\"code\" href=\"dt.html#"+htmlencode(t.getConstantType())+"\">"+htmlencode(t.getConstantType())+"</a></td>");
			} else {
				out.println("<td></td>");
			}
			if (t.getConstantValue() != null) {
				out.println("<td class=\"code\">"+htmlencode(t.getConstantValue())+"</td>");
			} else {
				out.println("<td></td>");
			}
			out.println("</tr>");
			c.add(n);
		}
		out.println("</tbody>");
		out.println("</table>");
		
		Collections.sort(c, new Comparator<NameTermPair>() {
			public int compare(NameTermPair a, NameTermPair b) {
				String at = a.getTerm().getConstantType();
				String bt = b.getTerm().getConstantType();
				int tc = ((at == null) ? "" : at).compareToIgnoreCase((bt == null) ? "" : bt);
				return ((tc == 0) ? a.compareTo(b) : tc);
			}
		});
		out.println("<p><a name=\"bytype\">The table below lists all the built-in constants by data type." +
				" See also <a href=\"#byname\">by name</a> and <a href=\"#byvalue\">by value</a>.</a></p>");
		out.println("<table class=\"linedrows\">");
		out.println("<thead class=\"tbfirst\">\n<tr class=\"trfirst\">\n<th>Constant Name</th>\n<th>Data Type</th>\n<th>Value</th>\n</tr>\n</thead>");
		out.println("<tbody class=\"tbrest\">");
		for (NameTermPair n : c) {
			Term t = n.getTerm();
			out.println("<tr class=\"trrest\">");
			out.println("<td class=\"code\"><a class=\"code\" href=\"cn.html#"+htmlencode(n.getName().toLowerCase())+"\">"+htmlencode(n.getName())+"</a></td>");
			if (t.getConstantType() != null) {
				out.println("<td class=\"code\"><a class=\"code\" href=\"dt.html#"+htmlencode(t.getConstantType())+"\">"+htmlencode(t.getConstantType())+"</a></td>");
			} else {
				out.println("<td></td>");
			}
			if (t.getConstantValue() != null) {
				out.println("<td class=\"code\">"+htmlencode(t.getConstantValue())+"</td>");
			} else {
				out.println("<td></td>");
			}
			out.println("</tr>");
		}
		out.println("</tbody>");
		out.println("</table>");
		
		Collections.sort(c, new Comparator<NameTermPair>() {
			public int compare(NameTermPair a, NameTermPair b) {
				String at = a.getTerm().getConstantValue();
				String bt = b.getTerm().getConstantValue();
				int tc = ((at == null) ? "" : at).compareToIgnoreCase((bt == null) ? "" : bt);
				return ((tc == 0) ? a.compareTo(b) : tc);
			}
		});
		out.println("<p><a name=\"byvalue\">The table below lists all the built-in constants by value." +
				" See also <a href=\"#byname\">by name</a> and <a href=\"#bytype\">by data type</a>.</a></p>");
		out.println("<table class=\"linedrows\">");
		out.println("<thead class=\"tbfirst\">\n<tr class=\"trfirst\">\n<th>Constant Name</th>\n<th>Data Type</th>\n<th>Value</th>\n</tr>\n</thead>");
		out.println("<tbody class=\"tbrest\">");
		for (NameTermPair n : c) {
			Term t = n.getTerm();
			out.println("<tr class=\"trrest\">");
			out.println("<td class=\"code\"><a class=\"code\" href=\"cn.html#"+htmlencode(n.getName().toLowerCase())+"\">"+htmlencode(n.getName())+"</a></td>");
			if (t.getConstantType() != null) {
				out.println("<td class=\"code\"><a class=\"code\" href=\"dt.html#"+htmlencode(t.getConstantType())+"\">"+htmlencode(t.getConstantType())+"</a></td>");
			} else {
				out.println("<td></td>");
			}
			if (t.getConstantValue() != null) {
				out.println("<td class=\"code\">"+htmlencode(t.getConstantValue())+"</td>");
			} else {
				out.println("<td></td>");
			}
			out.println("</tr>");
		}
		out.println("</tbody>");
		out.println("</table>");
		
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private static void writeConstants(Dialect d, File f) throws IOException {
		File outf = new File(f, "constants.html");
		System.out.println("Creating "+f.getName()+"/"+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<html>");
		out.println("<head>");
		out.println("<title>"+htmlencode(d.getTitle())+" Constant Summary</title>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xiondoc.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>"+htmlencode(d.getTitle())+" Constant Summary</h1>");
		out.println("<p>This page summarizes "+htmlencode(d.getTitle())+"'s built-in constants." +
				" A constant is a named value that never changes." +
				" You cannot change its value or use its name as a variable name." +
				" If you try, the interpreter will trigger a script error.</p>");

		Vector<NameTermPair> c = new Vector<NameTermPair>();
		out.println("<p><a name=\"byname\">The table below lists all the built-in constants by name." +
				" See also <a href=\"#bytype\">by data type</a> and <a href=\"#byvalue\">by value</a>.</a></p>");
		out.println("<table class=\"linedrows\">");
		out.println("<thead class=\"tbfirst\">\n<tr class=\"trfirst\">\n<th>Constant Name</th>\n<th>Data Type</th>\n<th>Value</th>\n</tr>\n</thead>");
		out.println("<tbody class=\"tbrest\">");
		Iterator<NameTermPair> i = d.termIterator(Term.Type.CONSTANT);
		while (i.hasNext()) {
			NameTermPair n = i.next();
			Term t = n.getTerm();
			out.println("<tr class=\"trrest\">");
			out.println("<td class=\"code\"><a class=\"code\" href=\"cn.html#"+htmlencode(n.getName().toLowerCase())+"\">"+htmlencode(n.getName())+"</a></td>");
			if (t.getConstantType() != null) {
				out.println("<td class=\"code\"><a class=\"code\" href=\"dt.html#"+htmlencode(t.getConstantType())+"\">"+htmlencode(t.getConstantType())+"</a></td>");
			} else {
				out.println("<td></td>");
			}
			if (t.getConstantValue() != null) {
				out.println("<td class=\"code\">"+htmlencode(t.getConstantValue())+"</td>");
			} else {
				out.println("<td></td>");
			}
			out.println("</tr>");
			c.add(n);
		}
		out.println("</tbody>");
		out.println("</table>");
		
		Collections.sort(c, new Comparator<NameTermPair>() {
			public int compare(NameTermPair a, NameTermPair b) {
				String at = a.getTerm().getConstantType();
				String bt = b.getTerm().getConstantType();
				int tc = ((at == null) ? "" : at).compareToIgnoreCase((bt == null) ? "" : bt);
				return ((tc == 0) ? a.compareTo(b) : tc);
			}
		});
		out.println("<p><a name=\"bytype\">The table below lists all the built-in constants by data type." +
				" See also <a href=\"#byname\">by name</a> and <a href=\"#byvalue\">by value</a>.</a></p>");
		out.println("<table class=\"linedrows\">");
		out.println("<thead class=\"tbfirst\">\n<tr class=\"trfirst\">\n<th>Constant Name</th>\n<th>Data Type</th>\n<th>Value</th>\n</tr>\n</thead>");
		out.println("<tbody class=\"tbrest\">");
		for (NameTermPair n : c) {
			Term t = n.getTerm();
			out.println("<tr class=\"trrest\">");
			out.println("<td class=\"code\"><a class=\"code\" href=\"cn.html#"+htmlencode(n.getName().toLowerCase())+"\">"+htmlencode(n.getName())+"</a></td>");
			if (t.getConstantType() != null) {
				out.println("<td class=\"code\"><a class=\"code\" href=\"dt.html#"+htmlencode(t.getConstantType())+"\">"+htmlencode(t.getConstantType())+"</a></td>");
			} else {
				out.println("<td></td>");
			}
			if (t.getConstantValue() != null) {
				out.println("<td class=\"code\">"+htmlencode(t.getConstantValue())+"</td>");
			} else {
				out.println("<td></td>");
			}
			out.println("</tr>");
		}
		out.println("</tbody>");
		out.println("</table>");
		
		Collections.sort(c, new Comparator<NameTermPair>() {
			public int compare(NameTermPair a, NameTermPair b) {
				String at = a.getTerm().getConstantValue();
				String bt = b.getTerm().getConstantValue();
				int tc = ((at == null) ? "" : at).compareToIgnoreCase((bt == null) ? "" : bt);
				return ((tc == 0) ? a.compareTo(b) : tc);
			}
		});
		out.println("<p><a name=\"byvalue\">The table below lists all the built-in constants by value." +
				" See also <a href=\"#byname\">by name</a> and <a href=\"#bytype\">by data type</a>.</a></p>");
		out.println("<table class=\"linedrows\">");
		out.println("<thead class=\"tbfirst\">\n<tr class=\"trfirst\">\n<th>Constant Name</th>\n<th>Data Type</th>\n<th>Value</th>\n</tr>\n</thead>");
		out.println("<tbody class=\"tbrest\">");
		for (NameTermPair n : c) {
			Term t = n.getTerm();
			out.println("<tr class=\"trrest\">");
			out.println("<td class=\"code\"><a class=\"code\" href=\"cn.html#"+htmlencode(n.getName().toLowerCase())+"\">"+htmlencode(n.getName())+"</a></td>");
			if (t.getConstantType() != null) {
				out.println("<td class=\"code\"><a class=\"code\" href=\"dt.html#"+htmlencode(t.getConstantType())+"\">"+htmlencode(t.getConstantType())+"</a></td>");
			} else {
				out.println("<td></td>");
			}
			if (t.getConstantValue() != null) {
				out.println("<td class=\"code\">"+htmlencode(t.getConstantValue())+"</td>");
			} else {
				out.println("<td></td>");
			}
			out.println("</tr>");
		}
		out.println("</tbody>");
		out.println("</table>");
		
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private static void writeOperators(DocumentationSet d, File f) throws IOException {
		File outf = new File(f, "precedence.html");
		System.out.println("Creating "+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<html>");
		out.println("<head>");
		out.println("<title>XION Operator Precedence Table</title>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xiondoc.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>XION Operator Precedence Table</h1>");
		out.println("<p>The table below shows the order of precedence of operators in XION." +
				" In a complex expression containing more than one operator, the operations" +
				" indicated by operators with lower-numbered precedence will be performed before" +
				" those with higher-numbered precedence. Operators of equal precedence are" +
				" evaluated left-to-right, except for exponentiation, which goes right-to-left." +
				" If you use parentheses, the innermost parenthetical expression is evaluated" +
				" first.</p>");
		out.println("<table class=\"linedrowgroups\">");
		out.println("<thead class=\"tbfirst\"><tr class=\"trfirst\"><th>Order</th><th>Operators</th><th>Type of Operator</th></tr></thead>");
		for (Term.Precedence prec : Term.Precedence.values()) {
			Iterator<NameTermPair> i = d.termIterator(Term.Type.OPERATOR);
			boolean first = true;
			while (i.hasNext()) {
				NameTermPair vi = i.next();
				if (vi.getTerm().getPrecedence() == prec) {
					if (first) out.println("<tbody class=\"tbrest\">");
					out.print("<tr class=\"trrest\">");
					if (first) {
						out.print("<td>"+prec.getNumber()+" - "+htmlencode(prec.getName())+"</td>");
						first = false;
					} else {
						out.print("<td></td>");
					}
					out.print("<td class=\"code\"><a href=\"op.html#"+htmlencode(vi.getName().toLowerCase())+"\">"+htmlencode(vi.getName())+"</a></td>");
					out.print("<td>"+(vi.getTerm().getShortDescription() == null ? "" : htmlencode(vi.getTerm().getShortDescription()))+"</td>");
					out.println("</tr>");
				}
			}
			if (!first) out.println("</tbody>");
		}
		out.println("</table>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private static void writeOperators(Dialect d, File f) throws IOException {
		File outf = new File(f, "precedence.html");
		System.out.println("Creating "+f.getName()+"/"+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<html>");
		out.println("<head>");
		out.println("<title>"+htmlencode(d.getTitle())+" Operator Precedence Table</title>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xiondoc.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>"+htmlencode(d.getTitle())+" Operator Precedence Table</h1>");
		out.println("<p>The table below shows the order of precedence of operators in "+htmlencode(d.getTitle())+"." +
				" In a complex expression containing more than one operator, the operations" +
				" indicated by operators with lower-numbered precedence will be performed before" +
				" those with higher-numbered precedence. Operators of equal precedence are" +
				" evaluated left-to-right, except for exponentiation, which goes right-to-left." +
				" If you use parentheses, the innermost parenthetical expression is evaluated" +
				" first.</p>");
		out.println("<table class=\"linedrowgroups\">");
		out.println("<thead class=\"tbfirst\"><tr class=\"trfirst\"><th>Order</th><th>Operators</th><th>Type of Operator</th></tr></thead>");
		for (Term.Precedence prec : Term.Precedence.values()) {
			Iterator<NameTermPair> i = d.termIterator(Term.Type.OPERATOR);
			boolean first = true;
			while (i.hasNext()) {
				NameTermPair vi = i.next();
				if (vi.getTerm().getPrecedence() == prec) {
					if (first) out.println("<tbody class=\"tbrest\">");
					out.print("<tr class=\"trrest\">");
					if (first) {
						out.print("<td>"+prec.getNumber()+" - "+htmlencode(prec.getName())+"</td>");
						first = false;
					} else {
						out.print("<td></td>");
					}
					out.print("<td class=\"code\"><a href=\"op.html#"+htmlencode(vi.getName().toLowerCase())+"\">"+htmlencode(vi.getName())+"</a></td>");
					out.print("<td>"+(vi.getTerm().getShortDescription() == null ? "" : htmlencode(vi.getTerm().getShortDescription()))+"</td>");
					out.println("</tr>");
				}
			}
			if (!first) out.println("</tbody>");
		}
		out.println("</table>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private static void writeColors(DocumentationSet d, File f) throws IOException {
		File outf = new File(f, "colors.html");
		System.out.println("Creating "+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<html>");
		out.println("<head>");
		out.println("<title>XION Color Chart</title>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xiondoc.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>XION Color Chart</h1>");
		
		Vector<NameTermPair> cv = new Vector<NameTermPair>();
		
		out.println("<p><a name=\"byname\">The table below lists the name, the RGB value, and a color swatch " +
				"for each <code><a href=\"dt.html#color\">color</a></code> defined as a constant, " +
				"sorted by name. See also <a href=\"#byhue\">by hue</a>.</a></p>");
		out.println("<table>");
		out.println("<tr><th>Name</th><th>R</th><th>G</th><th>B</th><th>Swatch</th></tr>");
		Iterator<NameTermPair> vi = d.termIterator(Term.Type.CONSTANT);
		while (vi.hasNext()) {
			NameTermPair e = vi.next();
			String cnt = e.getTerm().getConstantType();
			if (cnt != null && (cnt.equalsIgnoreCase("color") || cnt.equalsIgnoreCase("colour"))) {
				String[] cnv = e.getTerm().getConstantValue().split(",");
				if (cnv.length == 3) try {
					int r = Integer.parseInt(cnv[0]);
					int g = Integer.parseInt(cnv[1]);
					int b = Integer.parseInt(cnv[2]);
					cv.add(e);
					String h = "000000"+Integer.toString(((r/257)<<16) | ((g/257)<<8) | ((b/257)), 16);
					h = h.substring(h.length()-6);
					out.print("<tr>");
					out.print("<td class=\"code\"><a href=\"cn.html#"+htmlencode(e.getName().toLowerCase())+"\">"+htmlencode(e.getName())+"</a></td>");
					out.print("<td>"+r+"</td><td>"+g+"</td><td>"+b+"</td>");
					out.print("<td style=\"width: 100px; background: #"+h+";\">&nbsp;</td>");
					out.println("</tr>");
				} catch (NumberFormatException nfe) {}
			}
		}
		out.println("</table>");
		
		Collections.sort(cv, new Comparator<NameTermPair>() {
			public int compare(NameTermPair a, NameTermPair b) {
				int ar = 0, ag = 0, ab = 0;
				int br = 0, bg = 0, bb = 0;
				String ah = null, bh = null;
				
				String cnta = a.getTerm().getConstantType();
				if (cnta != null && (cnta.equalsIgnoreCase("color") || cnta.equalsIgnoreCase("colour"))) {
					String[] cnv = a.getTerm().getConstantValue().split(",");
					if (cnv.length == 3) try {
						ar = Integer.parseInt(cnv[0]);
						ag = Integer.parseInt(cnv[1]);
						ab = Integer.parseInt(cnv[2]);
						ah = "000000"+Integer.toString(((ar/257)<<16) | ((ag/257)<<8) | ((ab/257)), 16);
						ah = ah.substring(ah.length()-6);
					} catch (NumberFormatException nfe) {}
				}
				
				String cntb = b.getTerm().getConstantType();
				if (cntb != null && (cntb.equalsIgnoreCase("color") || cntb.equalsIgnoreCase("colour"))) {
					String[] cnv = b.getTerm().getConstantValue().split(",");
					if (cnv.length == 3) try {
						br = Integer.parseInt(cnv[0]);
						bg = Integer.parseInt(cnv[1]);
						bb = Integer.parseInt(cnv[2]);
						bh = "000000"+Integer.toString(((br/257)<<16) | ((bg/257)<<8) | ((bb/257)), 16);
						bh = bh.substring(bh.length()-6);
					} catch (NumberFormatException nfe) {}
				}
				
				if (ah != null && bh != null) {
					float[] ahsb = Color.RGBtoHSB(ar/257, ag/257, ab/257, new float[3]);
					float[] bhsb = Color.RGBtoHSB(br/257, bg/257, bb/257, new float[3]);
					double aa = Math.atan2(ahsb[1],ahsb[2]);
					double ba = Math.atan2(bhsb[1],bhsb[2]);
					double ad = Math.hypot(ahsb[1],ahsb[2]);
					double bd = Math.hypot(bhsb[1],bhsb[2]);
					if (Math.abs(ahsb[0]-bhsb[0]) > 0.01) return (int)Math.signum(ahsb[0]-bhsb[0]);
					if (Math.abs(aa-ba) > 0.01) return (int)Math.signum(aa-ba);
					if (Math.abs(ad-bd) > 0.01) return (int)Math.signum(ad-bd);
				}
				return a.getName().compareToIgnoreCase(b.getName());
			}
		});
		
		out.println("<p>&nbsp;</p>");
		
		out.println("<p><a name=\"byhue\">The table below lists the name, the RGB value, and a color swatch " +
				"for each <code><a href=\"dt.html#color\">color</a></code> defined as a constant, " +
				"sorted by hue. See also <a href=\"#byname\">by name</a>.</a></p>");
		out.println("<table>");
		out.println("<tr><th>Name</th><th>R</th><th>G</th><th>B</th><th>Swatch</th></tr>");
		for (NameTermPair v : cv) {
			String cnt = v.getTerm().getConstantType();
			if (cnt != null && (cnt.equalsIgnoreCase("color") || cnt.equalsIgnoreCase("colour"))) {
				String[] cnv = v.getTerm().getConstantValue().split(",");
				if (cnv.length == 3) try {
					int r = Integer.parseInt(cnv[0]);
					int g = Integer.parseInt(cnv[1]);
					int b = Integer.parseInt(cnv[2]);
					String h = "000000"+Integer.toString(((r/257)<<16) | ((g/257)<<8) | ((b/257)), 16);
					h = h.substring(h.length()-6);
					out.print("<tr>");
					out.print("<td class=\"code\"><a href=\"cn.html#"+htmlencode(v.getName().toLowerCase())+"\">"+htmlencode(v.getName())+"</a></td>");
					out.print("<td>"+r+"</td><td>"+g+"</td><td>"+b+"</td>");
					out.print("<td style=\"width: 100px; background: #"+h+";\">&nbsp;</td>");
					out.println("</tr>");
				} catch (NumberFormatException nfe) {}
			}
		}
		out.println("</table>");
		
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private static void writeColors(Dialect d, File f) throws IOException {
		File outf = new File(f, "colors.html");
		System.out.println("Creating "+f.getName()+"/"+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<html>");
		out.println("<head>");
		out.println("<title>"+htmlencode(d.getTitle())+" Color Chart</title>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xiondoc.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>"+htmlencode(d.getTitle())+" Color Chart</h1>");
		
		Vector<NameTermPair> cv = new Vector<NameTermPair>();
		
		out.println("<p><a name=\"byname\">The table below lists the name, the RGB value, and a color swatch " +
				"for each <code><a href=\"dt.html#color\">color</a></code> defined as a constant, " +
				"sorted by name. See also <a href=\"#byhue\">by hue</a>.</a></p>");
		out.println("<table>");
		out.println("<tr><th>Name</th><th>R</th><th>G</th><th>B</th><th>Swatch</th></tr>");
		Iterator<NameTermPair> vi = d.termIterator(Term.Type.CONSTANT);
		while (vi.hasNext()) {
			NameTermPair e = vi.next();
			String cnt = e.getTerm().getConstantType();
			if (cnt != null && (cnt.equalsIgnoreCase("color") || cnt.equalsIgnoreCase("colour"))) {
				String[] cnv = e.getTerm().getConstantValue().split(",");
				if (cnv.length == 3) try {
					int r = Integer.parseInt(cnv[0]);
					int g = Integer.parseInt(cnv[1]);
					int b = Integer.parseInt(cnv[2]);
					cv.add(e);
					String h = "000000"+Integer.toString(((r/257)<<16) | ((g/257)<<8) | ((b/257)), 16);
					h = h.substring(h.length()-6);
					out.print("<tr>");
					out.print("<td class=\"code\"><a href=\"cn.html#"+htmlencode(e.getName().toLowerCase())+"\">"+htmlencode(e.getName())+"</a></td>");
					out.print("<td>"+r+"</td><td>"+g+"</td><td>"+b+"</td>");
					out.print("<td style=\"width: 100px; background: #"+h+";\">&nbsp;</td>");
					out.println("</tr>");
				} catch (NumberFormatException nfe) {}
			}
		}
		out.println("</table>");
		
		Collections.sort(cv, new Comparator<NameTermPair>() {
			public int compare(NameTermPair a, NameTermPair b) {
				int ar = 0, ag = 0, ab = 0;
				int br = 0, bg = 0, bb = 0;
				String ah = null, bh = null;
				
				String cnta = a.getTerm().getConstantType();
				if (cnta != null && (cnta.equalsIgnoreCase("color") || cnta.equalsIgnoreCase("colour"))) {
					String[] cnv = a.getTerm().getConstantValue().split(",");
					if (cnv.length == 3) try {
						ar = Integer.parseInt(cnv[0]);
						ag = Integer.parseInt(cnv[1]);
						ab = Integer.parseInt(cnv[2]);
						ah = "000000"+Integer.toString(((ar/257)<<16) | ((ag/257)<<8) | ((ab/257)), 16);
						ah = ah.substring(ah.length()-6);
					} catch (NumberFormatException nfe) {}
				}
				
				String cntb = b.getTerm().getConstantType();
				if (cntb != null && (cntb.equalsIgnoreCase("color") || cntb.equalsIgnoreCase("colour"))) {
					String[] cnv = b.getTerm().getConstantValue().split(",");
					if (cnv.length == 3) try {
						br = Integer.parseInt(cnv[0]);
						bg = Integer.parseInt(cnv[1]);
						bb = Integer.parseInt(cnv[2]);
						bh = "000000"+Integer.toString(((br/257)<<16) | ((bg/257)<<8) | ((bb/257)), 16);
						bh = bh.substring(bh.length()-6);
					} catch (NumberFormatException nfe) {}
				}
				
				if (ah != null && bh != null) {
					float[] ahsb = Color.RGBtoHSB(ar/257, ag/257, ab/257, new float[3]);
					float[] bhsb = Color.RGBtoHSB(br/257, bg/257, bb/257, new float[3]);
					double aa = Math.atan2(ahsb[1],ahsb[2]);
					double ba = Math.atan2(bhsb[1],bhsb[2]);
					double ad = Math.hypot(ahsb[1],ahsb[2]);
					double bd = Math.hypot(bhsb[1],bhsb[2]);
					if (Math.abs(ahsb[0]-bhsb[0]) > 0.01) return (int)Math.signum(ahsb[0]-bhsb[0]);
					if (Math.abs(aa-ba) > 0.01) return (int)Math.signum(aa-ba);
					if (Math.abs(ad-bd) > 0.01) return (int)Math.signum(ad-bd);
				}
				return a.getName().compareToIgnoreCase(b.getName());
			}
		});
		
		out.println("<p>&nbsp;</p>");
		
		out.println("<p><a name=\"byhue\">The table below lists the name, the RGB value, and a color swatch " +
				"for each <code><a href=\"dt.html#color\">color</a></code> defined as a constant, " +
				"sorted by hue. See also <a href=\"#byname\">by name</a>.</a></p>");
		out.println("<table>");
		out.println("<tr><th>Name</th><th>R</th><th>G</th><th>B</th><th>Swatch</th></tr>");
		for (NameTermPair v : cv) {
			String cnt = v.getTerm().getConstantType();
			if (cnt != null && (cnt.equalsIgnoreCase("color") || cnt.equalsIgnoreCase("colour"))) {
				String[] cnv = v.getTerm().getConstantValue().split(",");
				if (cnv.length == 3) try {
					int r = Integer.parseInt(cnv[0]);
					int g = Integer.parseInt(cnv[1]);
					int b = Integer.parseInt(cnv[2]);
					String h = "000000"+Integer.toString(((r/257)<<16) | ((g/257)<<8) | ((b/257)), 16);
					h = h.substring(h.length()-6);
					out.print("<tr>");
					out.print("<td class=\"code\"><a href=\"cn.html#"+htmlencode(v.getName().toLowerCase())+"\">"+htmlencode(v.getName())+"</a></td>");
					out.print("<td>"+r+"</td><td>"+g+"</td><td>"+b+"</td>");
					out.print("<td style=\"width: 100px; background: #"+h+";\">&nbsp;</td>");
					out.println("</tr>");
				} catch (NumberFormatException nfe) {}
			}
		}
		out.println("</table>");
		
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private static void writeIntro(DocumentationSet d, File f) throws IOException {
		File outf = new File(f, "intro.html");
		System.out.println("Creating "+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Welcome to XION</title>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xiondoc.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>Welcome to XION</h1>");
		out.println("<p>XION is a kind of scripting language that enables ordinary people" +
				" to do extraordinary things. You do not need to learn a bunch of cryptic" +
				" symbols and how to put them in exactly the right places in order to tell" +
				" your computer what to do. Since XION has been created to resemble natural" +
				" English, all you need is a basic understanding of the English language.</p>");
		out.println("<p>A particular variant of XION is called a <i>dialect</i>. This" +
				" documentation set provides detailed information on the dialects of" +
				" XION listed to the left. Below the list of dialects is a list of types" +
				" of vocabulary terms, which you can use to narrow down the vocabulary" +
				" you are looking for, as well as some appendices for quick reference.</p>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private static void writeIntro(Dialect d, File f) throws IOException {
		File outf = new File(f, "intro.html");
		System.out.println("Creating "+f.getName()+"/"+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Welcome to XION</title>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xiondoc.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>Welcome to XION</h1>");
		out.println("<p>XION is a kind of scripting language that enables ordinary people" +
				" to do extraordinary things. You do not need to learn a bunch of cryptic" +
				" symbols and how to put them in exactly the right places in order to tell" +
				" your computer what to do. Since XION has been created to resemble natural" +
				" English, all you need is a basic understanding of the English language.</p>");
		out.println("<p>A particular variant of XION is called a <i>dialect</i>. This" +
				" documentation set provides detailed information on the dialects of" +
				" XION listed to the left. Below the list of dialects is a list of types" +
				" of vocabulary terms, which you can use to narrow down the vocabulary" +
				" you are looking for, as well as some appendices for quick reference.</p>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private static String mainCSS = null;
	private static void writeMainCSS(File f, boolean inside) throws IOException {
		if (mainCSS == null) {
			URL u = HTMLDWriter.class.getResource("xiondoc.css");
			URLConnection uc = u.openConnection();
			InputStream ui = uc.getInputStream();
			ByteArrayOutputStream uo = new ByteArrayOutputStream();
			byte[] ub = new byte[16384];
			int ul;
			while ((ul = ui.read(ub)) >= 0) {
				uo.write(ub, 0, ul);
			}
			ui.close();
			uo.close();
			String us = new String(uo.toByteArray(), "UTF-8");
			mainCSS = us;
		}
		File outf = new File(f, "xiondoc.css");
		System.out.println("Creating "+(inside?(f.getName()+"/"):"")+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.print(mainCSS);
		out.close();
	}
	
	private static String navCSS = null;
	private static void writeNavCSS(File f, boolean inside) throws IOException {
		if (navCSS == null) {
			URL u = HTMLDWriter.class.getResource("xionnav.css");
			URLConnection uc = u.openConnection();
			InputStream ui = uc.getInputStream();
			ByteArrayOutputStream uo = new ByteArrayOutputStream();
			byte[] ub = new byte[16384];
			int ul;
			while ((ul = ui.read(ub)) >= 0) {
				uo.write(ub, 0, ul);
			}
			ui.close();
			uo.close();
			String us = new String(uo.toByteArray(), "UTF-8");
			navCSS = us;
		}
		File outf = new File(f, "xionnav.css");
		System.out.println("Creating "+(inside?(f.getName()+"/"):"")+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.print(navCSS);
		out.close();
	}
	
	private static void writeFrameset(DocumentationSet d, File f) throws IOException {
		File outf = new File(f, "index.html");
		System.out.println("Creating "+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" \"http://www.w3.org/TR/html4/frameset.dtd\">");
		out.println("<html>");
		out.println("<head>");
		out.println("<title>XION Documentation</title>");
		out.println("</head>");
		out.println("<frameset cols=\"312,*\">");
		out.println("<frameset rows=\"120,200,*\">");
		out.println("<frame name=\"xndialects\" src=\"dialects.html\" />");
		out.println("<frame name=\"xnvocabtypes\" src=\"vocabtypes.html\" />");
		out.println("<frame name=\"xnvocab\" src=\"all-index.html\" />");
		out.println("</frameset>");
		out.println("<frame name=\"xncontent\" src=\"intro.html\" />");
		out.println("</frameset>");
		out.println("</html>");
		out.close();
	}
	
	private static void writeFrameset(Dialect d, File f) throws IOException {
		File outf = new File(f, "index.html");
		System.out.println("Creating "+f.getName()+"/"+outf.getName()+"...");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" \"http://www.w3.org/TR/html4/frameset.dtd\">");
		out.println("<html>");
		out.println("<head>");
		out.println("<title>"+htmlencode(d.toString())+" Documentation</title>");
		out.println("</head>");
		out.println("<frameset cols=\"312,*\">");
		out.println("<frameset rows=\"120,200,*\">");
		out.println("<frame name=\"xndialects\" src=\"dialects.html\" />");
		out.println("<frame name=\"xnvocabtypes\" src=\"vocabtypes.html\" />");
		out.println("<frame name=\"xnvocab\" src=\"all-index.html\" />");
		out.println("</frameset>");
		out.println("<frame name=\"xncontent\" src=\"intro.html\" />");
		out.println("</frameset>");
		out.println("</html>");
		out.close();
	}
	
	public void write(DocumentationSet d, File f) throws IOException {
		if (f.exists()) deltree(f);
		f.mkdir();
		
		Iterator<Dialect> i = d.dialectIterator();
		while (i.hasNext()) {
			Dialect dl = i.next();
			File ff = new File(f, dl.getCode());
			ff.mkdir();
			
			writeDialectIndex(dl,ff);
			writeVocabTypeIndex(dl,ff);
			for (Term.Type vt : Term.Type.values()) {
				writeIndex(dl,vt,ff);
				writeChapter(d,dl,vt,ff);
			}
			Iterator<Article> ai = dl.articleIterator();
			while (ai.hasNext()) {
				writeArticle(ai.next(),ff,true);
			}
			writeAllIndex(dl,ff);
			writeSuperIndex(dl,ff);
			writeSynonyms(dl,ff);
			writeConstants(dl,ff);
			writeOperators(dl,ff);
			writeColors(dl,ff);
			writeIntro(dl,ff);
			writeMainCSS(ff,true);
			writeNavCSS(ff,true);
			writeFrameset(dl,ff);
		}
		
		writeDialectIndex(d,f);
		writeVocabTypeIndex(d,f);
		for (Term.Type t : Term.Type.values()) {
			writeIndex(d,t,f);
			writeChapter(d,t,f);
		}
		Iterator<Article> ai = d.articleIterator();
		while (ai.hasNext()) {
			writeArticle(ai.next(),f,true);
		}
		writeAllIndex(d,f);
		writeSuperIndex(d,f);
		writeSynonyms(d,f);
		writeConstants(d,f);
		writeOperators(d,f);
		writeColors(d,f);
		writeIntro(d,f);
		writeMainCSS(f,false);
		writeNavCSS(f,false);
		writeFrameset(d,f);
	}
}
