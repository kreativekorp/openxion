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
 * @since XIONDoc 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.xiondoc;

import java.awt.Color;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;

import com.kreative.xiondoc.sdom.Script;
import com.kreative.xiondoc.xdom.*;

public class HTMLXDOMGenerator {
	private DocumentationSet ds;
	private File base;
	private String basePath;
	private HTMLSDOMGenerator sdomg;
	private PrintWriter out;
	
	public HTMLXDOMGenerator(DocumentationSet ds, File base, boolean output) {
		this.ds = ds;
		this.base = base;
		this.basePath = base.getAbsolutePath();
		this.sdomg = new HTMLSDOMGenerator();
		if (output) {
			try {
				this.out = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"), true);
			} catch (UnsupportedEncodingException uee) {
				this.out = new PrintWriter(new OutputStreamWriter(System.out), true);
			}
		} else {
			this.out = null;
		}
	}
	
	public HTMLXDOMGenerator(DocumentationSet ds, File base, PrintWriter out) {
		this.ds = ds;
		this.base = base;
		this.basePath = base.getAbsolutePath();
		this.sdomg = new HTMLSDOMGenerator();
		this.out = out;
	}
	
	public void writeAll() throws IOException {
		deltree(base);
		base.mkdir();
		for (Dialect d : ds.dialects()) {
			for (VersionNumber v : d.versions()) {
				if (out != null) out.println("Writing " + d.type().toString().toLowerCase() + " " + d.name() + " " + v.toString() + "...");
				write(d, v);
			}
			if (out != null) out.println("Writing " + d.type().toString().toLowerCase() + " " + d.name() + " (all versions)...");
			write(d, null);
		}
		if (out != null) out.println("Writing all dialects...");
		write(null, null);
	}
	
	private void write(Dialect dialect, VersionNumber dialectVersion) throws IOException {
		String dialectName;
		VersionNumber realVersion;
		if (dialect == null) {
			dialectName = null;
			realVersion = null;
			sdomg.unsetDialect();
		} else {
			dialectName = dialect.name();
			realVersion = ((dialectVersion == null) ? dialect.versions().last() : dialectVersion);
			sdomg.setDialect(dialectName, dialect.getTitle(), realVersion);
		}
		List<TermSpec> allTerms = new Vector<TermSpec>();
		for (Term term : ds.terms().getTerms(null, null, dialectName, realVersion)) {
			for (TermName termName : term.names()) {
				if (dialect == null || termName.getDialects().matches(dialectName, realVersion)) {
					allTerms.add(new TermSpec(term.type(), termName.getName()));
				}
			}
		}
		Collections.sort(allTerms, new Comparator<TermSpec>() {
			@Override
			public int compare(TermSpec a, TermSpec b) {
				if (a.getName().equalsIgnoreCase(b.getName())) {
					return a.getType().compareTo(b.getType());
				} else {
					return a.getName().compareToIgnoreCase(b.getName());
				}
			}
		});
		for (TermType termType : TermType.values()) {
			SortedMap<TermSpec, Term> allTermsOfType = new TreeMap<TermSpec, Term>();
			for (Term term : ds.terms().getTerms(termType, null, dialectName, realVersion)) {
				for (TermName termName : term.names()) {
					if (dialect == null || termName.getDialects().matches(dialectName, realVersion)) {
						allTermsOfType.put(new TermSpec(termType, termName.getName()), term);
					}
				}
			}
			for (Map.Entry<TermSpec, Term> e : allTermsOfType.entrySet()) {
				sdomg.setTerm(e.getKey().getType(), e.getKey().getName());
				writeVocabularyItem(
						dialect, dialectVersion, termType,
						e.getKey().getName(), e.getValue(),
						allTerms, allTermsOfType
				);
			}
			sdomg.unsetTerm();
			writeChapter(dialect, dialectVersion, termType, allTermsOfType);
			writeIndex(dialect, dialectVersion, termType, allTermsOfType);
		}
		writeAllIndex(dialect, dialectVersion, allTerms);
		writeSuperIndex(dialect, dialectVersion, allTerms);
		writeColors(dialect, dialectVersion);
		writeOperators(dialect, dialectVersion);
		writeConstants(dialect, dialectVersion);
		writeSynonyms(dialect, dialectVersion);
		List<Article> articles = ((dialect == null) ? ds.articles() : dialect.articles());
		for (Article article : articles) {
			writeArticle(dialect, dialectVersion, article);
		}
		writeVocabTypeIndex(dialect, dialectVersion, articles);
		writeDialectIndex(dialect, dialectVersion);
		writeIntro(dialect, dialectVersion);
		writeMainCSS(dialect, dialectVersion);
		writeNavCSS(dialect, dialectVersion);
		writeFrameset(dialect, dialectVersion);
	}
	
	private void writeVocabularyItem(
		Dialect dialect, VersionNumber dialectVersion,
		TermType termType, String termName, Term term,
		List<TermSpec> allTerms, SortedMap<TermSpec, Term> allTermsOfType
	) throws IOException {
		sdomg.setURLPrefix("../");
		PrintWriter out = openFile(dialect, dialectVersion, termType, termName);
		out.println("<html>");
		out.println("<head>");
		if (dialect == null) {
			out.println("<title>XION " + htmlencode(termType.getPluralTitleCase()) + " - " + htmlencode(termName) + "</title>");
		} else if (dialectVersion == null) {
			out.println("<title>" + htmlencode(dialect.getTitle()) + " " + htmlencode(termType.getPluralTitleCase()) + " - " + htmlencode(termName) + "</title>");
		} else {
			out.println("<title>" + htmlencode(dialect.getTitle()) + " " + htmlencode(dialectVersion.toString()) + " " + htmlencode(termType.getPluralTitleCase()) + " - " + htmlencode(termName) + "</title>");
		}
		out.println("<meta name=\"generator\" content=\""+XIONDoc.XIONDOC_NAME+" "+XIONDoc.XIONDOC_VERSION+"\">");
		out.println("<meta name=\"keywords\" content=\""+baseKeywords(dialect, dialectVersion)+", "+htmlencode(termType.getSingular())+", "+htmlencode(termType.getPlural())+", "+htmlencode(termName)+"\">");
		out.println("<meta name=\"description\" content=\"This page describes the "+htmlencode(termName)+" "+htmlencode(termType.getSingular())+".\">");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"../xiondoc.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1 class=\"vocabtypeheader\">"+htmlencode(termType.getPluralTitleCase())+"</h1>");
		out.println("<h2 class=\"vocabnameheader\">"+htmlencode(termName)+"</h2>");
		out.println("<h3 class=\"vocabsectionheader\">Supported By</h3>");
		out.println("<ul class=\"dialectlist supportedby\">");
		for (Dialect d : ds.dialects()) {
			boolean first = true;
			for (VersionNumber dv : d.versions()) {
				for (TermName n : term.names()) {
					if (n.getDialects().matches(d.name(), dv)) {
						if (first) {
							out.print("<li>" + htmlencode(d.getTitle()) + " (" + htmlencode(dv.toString()));
							first = false;
						} else {
							out.print(", " + htmlencode(dv.toString()));
						}
						break;
					}
				}
			}
			if (!first) {
				out.println(")</li>");
			}
		}
		out.println("</ul>");
		if (term.hasAppliesTo()) {
			out.println("<h3 class=\"vocabsectionheader\">Applies To</h3>");
			out.println(sdomg.generateSectionHTML(term.getAppliesTo()));
		}
		if (term.descriptors() != null && !term.descriptors().isEmpty()) {
			out.println("<h3 class=\"vocabsectionheader\">Descriptors</h3>");
			out.println("<ul class=\"descriptorlist\">");
			for (Descriptor descriptor : term.descriptors()) {
				String example = descriptor.getExample(termName);
				if (example.contains("steve")) {
					example = "put "+example+" into bill";
				} else {
					example = "put "+example+" into steve";
				}
				out.println("<li>"+htmlencode(descriptor.getName())+": <code>"+htmlencode(example)+"</code></li>");
			}
			out.println("</ul>");
		}
		if (term.properties() != null && !term.properties().isEmpty()) {
			out.println("<h3 class=\"vocabsectionheader\">Properties</h3>");
			out.print("<p class=\"termlist\">");
			boolean first = true;
			for (TermSpec termSpec : term.properties()) {
				if (first) first = false;
				else out.print(", ");
				out.print("<code><a href=\"../" + htmlencode(fnencode(termSpec.getType().getCode()) + "/" + fnencode(termSpec.getName())) + "\">" + htmlencode(termSpec.getName()) + "</a></code>");
			}
			out.println("</p>");
		}
		if (term.hasSyntax()) {
			out.println("<h3 class=\"vocabsectionheader\">Syntax</h3>");
			out.println(sdomg.generateSectionHTML(term.getSyntax()));
		}
		if (term.hasExamples()) {
			if (term.getExamples().size(Script.class) > 1) {
				out.println("<h3 class=\"vocabsectionheader\">Examples</h3>");
			} else {
				out.println("<h3 class=\"vocabsectionheader\">Example</h3>");
			}
			out.println(sdomg.generateSectionHTML(term.getExamples()));
		}
		if (term.hasDescription()) {
			out.println("<h3 class=\"vocabsectionheader\">Description</h3>");
			out.println(sdomg.generateSectionHTML(term.getDescription()));
		}
		if (term.hasScripts()) {
			if (term.getScripts().size(Script.class) > 1) {
				out.println("<h3 class=\"vocabsectionheader\">Scripts</h3>");
			} else {
				out.println("<h3 class=\"vocabsectionheader\">Script</h3>");
			}
			out.println(sdomg.generateSectionHTML(term.getScripts()));
		}
		if (term.hasNotes()) {
			if (term.getNotes().size() > 1) {
				out.println("<h3 class=\"vocabsectionheader\">Notes</h3>");
			} else {
				out.println("<h3 class=\"vocabsectionheader\">Note</h3>");
			}
			out.println(sdomg.generateSectionHTML(term.getNotes()));
		}
		if (term.hasSecurity()) {
			out.println("<h3 class=\"vocabsectionheader\">Security</h3>");
			out.println(sdomg.generateSectionHTML(term.getSecurity()));
		}
		if (term.hasCompatibility()) {
			out.println("<h3 class=\"vocabsectionheader\">Compatibility</h3>");
			out.println(sdomg.generateSectionHTML(term.getCompatibility()));
		}
		if (term.hasSynonyms(((dialect == null) ? null : dialect.name()), dialectVersion)) {
			TermSpecList synonyms = term.getSynonyms(((dialect == null) ? null : dialect.name()), dialectVersion, termName);
			if (synonyms.size() > 1) {
				out.println("<h3 class=\"vocabsectionheader\">Synonyms</h3>");
			} else {
				out.println("<h3 class=\"vocabsectionheader\">Synonym</h3>");
			}
			out.print("<p class=\"termlist\">");
			boolean first = true;
			for (TermSpec termSpec : synonyms) {
				if (first) first = false;
				else out.print(", ");
				out.print("<code><a href=\"../" + htmlencode(fnencode(termSpec.getType().getCode()) + "/" + fnencode(termSpec.getName())) + ".html\">" + htmlencode(termSpec.getName()) + "</a></code>");
			}
			out.println("</p>");
		}
		if (term.seeAlso() != null && !term.seeAlso().isEmpty()) {
			out.println("<h3 class=\"vocabsectionheader\">See Also</h3>");
			out.print("<p class=\"termlist\">");
			boolean first = true;
			for (TermSpec termSpec : term.seeAlso()) {
				if (first) first = false;
				else out.print(", ");
				out.print("<code><a href=\"../" + htmlencode(fnencode(termSpec.getType().getCode()) + "/" + fnencode(termSpec.getName())) + ".html\">" + htmlencode(termSpec.getName()) + "</a></code>");
			}
			out.println("</p>");
		}
		out.println("</body>");
		out.println("</html>");
		out.close();
		sdomg.unsetURLPrefix();
	}
	
	private void writeChapter(
		Dialect dialect, VersionNumber dialectVersion,
		TermType termType, SortedMap<TermSpec, Term> allTermsOfType
	) throws IOException {
		PrintWriter out = openFile(dialect, dialectVersion, termType, "index");
		out.println("<html>");
		out.println("<head>");
		if (dialect == null) {
			out.println("<title>XION " + htmlencode(termType.getPluralTitleCase()) + "</title>");
		} else if (dialectVersion == null) {
			out.println("<title>" + htmlencode(dialect.getTitle()) + " " + htmlencode(termType.getPluralTitleCase()) + "</title>");
		} else {
			out.println("<title>" + htmlencode(dialect.getTitle()) + " " + htmlencode(dialectVersion.toString()) + " " + htmlencode(termType.getPluralTitleCase()) + "</title>");
		}
		out.println("<meta name=\"generator\" content=\""+XIONDoc.XIONDOC_NAME+" "+XIONDoc.XIONDOC_VERSION+"\">");
		out.println("<meta name=\"keywords\" content=\""+baseKeywords(dialect, dialectVersion)+", "+htmlencode(termType.getSingular())+", "+htmlencode(termType.getPlural())+", "+htmlencode(termType.getSingular())+" descriptions\">");
		out.println("<meta name=\"description\" content=\"This page describes the "+htmlencode(termType.getPlural())+" supported by "+((dialect == null) ? "all dialects, modules, and libraries in this documentation set" : htmlencode(dialect.getTitle()))+".\">");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"../xiondoc.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1 class=\"vocabindexheader\">"+htmlencode(termType.getPluralTitleCase())+"</h1>");
		out.println("<p class=\"vocabindexintro\">This page describes the "+htmlencode(termType.getPlural())+" supported by "+((dialect == null) ? "all dialects, modules, and libraries in this documentation set" : htmlencode(dialect.getTitle()))+".</p>");
		out.println("<ul class=\"vocabindexlist\">");
		for (TermSpec termSpec : allTermsOfType.keySet()) {
			out.println("<li class=\"vocabindexlistitem\"><code><a href=\""+fnencode(termSpec.getName())+".html\">"+htmlencode(termSpec.getName())+"</a></code></li>");
		}
		out.println("</ul>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private void writeIndex(
		Dialect dialect, VersionNumber dialectVersion,
		TermType termType, SortedMap<TermSpec, Term> allTermsOfType
	) throws IOException {
		PrintWriter out = openFile(dialect, dialectVersion, termType.getCode()+"-index.html");
		out.println("<html>");
		out.println("<head>");
		if (dialect == null) {
			out.println("<title>XION " + htmlencode(termType.getPluralTitleCase()) + "</title>");
		} else if (dialectVersion == null) {
			out.println("<title>" + htmlencode(dialect.getTitle()) + " " + htmlencode(termType.getPluralTitleCase()) + "</title>");
		} else {
			out.println("<title>" + htmlencode(dialect.getTitle()) + " " + htmlencode(dialectVersion.toString()) + " " + htmlencode(termType.getPluralTitleCase()) + "</title>");
		}
		out.println("<meta name=\"generator\" content=\""+XIONDoc.XIONDOC_NAME+" "+XIONDoc.XIONDOC_VERSION+"\">");
		out.println("<meta name=\"keywords\" content=\""+baseKeywords(dialect, dialectVersion)+", "+htmlencode(termType.getSingular())+", "+htmlencode(termType.getPlural())+", "+htmlencode(termType.getSingular())+" index\">");
		out.println("<meta name=\"description\" content=\"A list of XION "+htmlencode(termType.getPlural())+" with documentation available in "+((dialect == null) ? "all dialects, modules, and libraries in this documentation set" : htmlencode(dialect.getTitle()))+".\">");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xionnav.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1 class=\"vocablistheader\">"+htmlencode(termType.getPluralTitleCase())+"</h1>");
		out.println("<ul class=\"vocablist\">");
		for (TermSpec termSpec : allTermsOfType.keySet()) {
			out.println("<li class=\"vocablistitem\"><code><a href=\""+fnencode(termType.getCode())+"/"+fnencode(termSpec.getName())+".html\" target=\"xncontent\">"+htmlencode(termSpec.getName())+"</a></code></li>");
		}
		out.println("</ul>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private void writeAllIndex(Dialect dialect, VersionNumber dialectVersion, List<TermSpec> allTerms) throws IOException {
		PrintWriter out = openFile(dialect, dialectVersion, "all-index.html");
		out.println("<html>");
		out.println("<head>");
		if (dialect == null) {
			out.println("<title>XION Vocabulary Index</title>");
		} else if (dialectVersion == null) {
			out.println("<title>" + htmlencode(dialect.getTitle()) + " Vocabulary Index</title>");
		} else {
			out.println("<title>" + htmlencode(dialect.getTitle()) + " " + htmlencode(dialectVersion.toString()) + " Vocabulary Index</title>");
		}
		out.println("<meta name=\"generator\" content=\""+XIONDoc.XIONDOC_NAME+" "+XIONDoc.XIONDOC_VERSION+"\">");
		out.println("<meta name=\"keywords\" content=\""+baseKeywords(dialect, dialectVersion)+", vocabulary, all vocabulary, vocabulary index\">");
		out.println("<meta name=\"description\" content=\"A list of XION vocabulary terms with documentation available in "+((dialect == null) ? "all dialects, modules, and libraries in this documentation set" : htmlencode(dialect.getTitle()))+".\">");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xionnav.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1 class=\"vocablistheader\">All Vocabulary</h1>");
		out.println("<ul class=\"vocablist\">");
		for (TermSpec termSpec : allTerms) {
			out.println("<li class=\"vocablistitem\"><code><a href=\""+fnencode(termSpec.getType().getCode())+"/"+fnencode(termSpec.getName())+".html\" target=\"xncontent\">"+htmlencode(termSpec.getName())+"</a></code> <span class=\"expl\">("+htmlencode(termSpec.getType().getSingular())+")</span></li>");
		}
		out.println("</ul>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private void writeSuperIndex(Dialect dialect, VersionNumber dialectVersion, List<TermSpec> allTerms) throws IOException {
		PrintWriter[] out = new PrintWriter[27];
		out[0] = openFile(dialect, dialectVersion, "index-symb.html");
		for (int i = 1, ch = 'a'; i < out.length && ch <= 'z'; i++, ch++) {
			out[i] = openFile(dialect, dialectVersion, "index-"+(char)ch+".html");
		}
		for (int i = 0; i < out.length; i++) {
			out[i].println("<html>");
			out[i].println("<head>");
			if (dialect == null) {
				out[i].println("<title>XION Vocabulary Index - "+(i == 0 ? "Symbols" : ""+(i-1+'A'))+"</title>");
			} else if (dialectVersion == null) {
				out[i].println("<title>" + htmlencode(dialect.getTitle()) + " Vocabulary Index - "+(i == 0 ? "Symbols" : ""+(i-1+'A'))+"</title>");
			} else {
				out[i].println("<title>" + htmlencode(dialect.getTitle()) + " " + htmlencode(dialectVersion.toString()) + " Vocabulary Index - "+(i == 0 ? "Symbols" : ""+(i-1+'A'))+"</title>");
			}
			out[i].println("<meta name=\"generator\" content=\""+XIONDoc.XIONDOC_NAME+" "+XIONDoc.XIONDOC_VERSION+"\">");
			out[i].println("<meta name=\"keywords\" content=\""+baseKeywords(dialect, dialectVersion)+", vocabulary, all vocabulary, vocabulary index\">");
			out[i].println("<meta name=\"description\" content=\"A list of XION vocabulary terms with documentation available in "+((dialect == null) ? "all dialects, modules, and libraries in this documentation set" : htmlencode(dialect.getTitle()))+".\">");
			out[i].println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xiondoc.css\">");
			out[i].println("</head>");
			out[i].println("<body>");
			out[i].println("<h1 class=\"indexheader\">"+((dialect == null) ? "XION" : htmlencode(dialect.getTitle()))+" Vocabulary Index</h1>");
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
			out[i].println("<table class=\"indextable\">");
			out[i].println("<tr><th>Term</th><th>Type</th></tr>");
		}
		for (TermSpec termSpec : allTerms) {
			char ch = termSpec.getName().charAt(0);
			int i = (ch >= 'a' && ch <= 'z') ? (ch-'a'+1) : (ch >= 'A' && ch <= 'Z') ? (ch-'A'+1) : 0;
			out[i].println(
					"<tr>" +
					"<td>" +
					"<code>" +
					"<a href=\"" +
					fnencode(termSpec.getType().getCode()) +
					"/" +
					fnencode(termSpec.getName()) +
					".html\">" +
					htmlencode(termSpec.getName()) +
					"</a>" +
					"</td>" +
					"<td>" +
					htmlencode(termSpec.getType().getSingular()) +
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
	
	private void writeColors(Dialect dialect, VersionNumber dialectVersion) throws IOException {
		PrintWriter out = openFile(dialect, dialectVersion, "colors.html");
		out.println("<html>");
		out.println("<head>");
		if (dialect == null) {
			out.println("<title>XION Color Chart</title>");
		} else if (dialectVersion == null) {
			out.println("<title>" + htmlencode(dialect.getTitle()) + " Color Chart</title>");
		} else {
			out.println("<title>" + htmlencode(dialect.getTitle()) + " " + htmlencode(dialectVersion.toString()) + " Color Chart</title>");
		}
		out.println("<meta name=\"generator\" content=\""+XIONDoc.XIONDOC_NAME+" "+XIONDoc.XIONDOC_VERSION+"\">");
		out.println("<meta name=\"keywords\" content=\""+baseKeywords(dialect, dialectVersion)+", color, colors, colour, colours, color constants, colour constants, RGB values\">");
		out.println("<meta name=\"description\" content=\"This page lists the names, RGB values, and color swatches of each color defined as a constant in "+((dialect == null) ? "XION" : htmlencode(dialect.getTitle()))+".\">");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xiondoc.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>"+((dialect == null) ? "XION" : htmlencode(dialect.getTitle()))+" Color Chart</h1>");
		
		final List<TermSpec> colorTermSpecs = new Vector<TermSpec>();
		final Map<TermSpec,int[]> colorTermRGB = new HashMap<TermSpec,int[]>();
		for (Term term : ds.terms().getTerms(TermType.CONSTANT, null, ((dialect == null) ? null : dialect.name()), dialectVersion)) {
			if (term.getDataType() != null && (term.getDataType().getName().equalsIgnoreCase("color") || term.getDataType().getName().equalsIgnoreCase("colour")) && term.getDataValue() != null) {
				for (TermName termName : term.names()) {
					if (dialect == null || termName.getDialects().matches(dialect.name(), dialectVersion)) {
						String[] rgbStrings = term.getDataValue().trim().split("\\s*,\\s*");
						if (rgbStrings.length == 3) {
							try {
								int[] rgb = new int[3];
								rgb[0] = Integer.parseInt(rgbStrings[0]);
								rgb[1] = Integer.parseInt(rgbStrings[1]);
								rgb[2] = Integer.parseInt(rgbStrings[2]);
								TermSpec termSpec = new TermSpec(TermType.CONSTANT, termName.getName());
								colorTermSpecs.add(termSpec);
								colorTermRGB.put(termSpec, rgb);
							} catch (NumberFormatException nfe) {}
						}
					}
				}
			}
		}
		
		Collections.sort(colorTermSpecs, new Comparator<TermSpec>() {
			@Override
			public int compare(TermSpec a, TermSpec b) {
				return a.getName().compareToIgnoreCase(b.getName());
			}
		});
		out.println("<p class=\"colortableintro\"><a name=\"byname\">The table below lists the name, " +
				"the RGB value, and a color swatch for each <code><a href=\"dt/color.html\">color</a></code> " +
				"defined as a constant, sorted by name. See also <a href=\"#byhue\">by hue</a>.</a></p>");
		out.println("<table class=\"colortable\">");
		out.println("<tr><th>Name</th><th>R</th><th>G</th><th>B</th><th>Swatch</th></tr>");
		for (TermSpec termSpec : colorTermSpecs) {
			int[] rgb = colorTermRGB.get(termSpec);
			String h = "000000"+Integer.toString(((rgb[0]/257)<<16) | ((rgb[1]/257)<<8) | ((rgb[2]/257)), 16);
			h = h.substring(h.length()-6);
			out.print("<tr>");
			out.print("<td><code><a href=\"cn/"+fnencode(termSpec.getName())+".html\">"+htmlencode(termSpec.getName())+"</a></code></td>");
			out.print("<td>"+rgb[0]+"</td><td>"+rgb[1]+"</td><td>"+rgb[2]+"</td>");
			out.print("<td style=\"width: 100px; background: #"+h+";\">&nbsp;</td>");
			out.println("</tr>");
		}
		out.println("</table>");
		
		Collections.sort(colorTermSpecs, new Comparator<TermSpec>() {
			@Override
			public int compare(TermSpec a, TermSpec b) {
				int[] rgba = colorTermRGB.get(a);
				int[] rgbb = colorTermRGB.get(b);
				float[] hsva = Color.RGBtoHSB(rgba[0]/257, rgba[1]/257, rgba[2]/257, new float[3]);
				float[] hsvb = Color.RGBtoHSB(rgbb[0]/257, rgbb[1]/257, rgbb[2]/257, new float[3]);
				double aa = Math.atan2(hsva[1],hsva[2]);
				double ba = Math.atan2(hsvb[1],hsvb[2]);
				double ad = Math.hypot(hsva[1],hsva[2]);
				double bd = Math.hypot(hsvb[1],hsvb[2]);
				if (Math.abs(hsva[0]-hsvb[0]) > 0.01) return (int)Math.signum(hsva[0]-hsvb[0]);
				if (Math.abs(aa-ba) > 0.01) return (int)Math.signum(aa-ba);
				if (Math.abs(ad-bd) > 0.01) return (int)Math.signum(ad-bd);
				return a.getName().compareToIgnoreCase(b.getName());
			}
		});
		out.println("<p class=\"colortableintro\"><a name=\"byhue\">The table below lists the name, " +
				"the RGB value, and a color swatch for each <code><a href=\"dt/color.html\">color</a></code> " +
				"defined as a constant, sorted by hue. See also <a href=\"#byname\">by name</a>.</a></p>");
		out.println("<table class=\"colortable\">");
		out.println("<tr><th>Name</th><th>R</th><th>G</th><th>B</th><th>Swatch</th></tr>");
		for (TermSpec termSpec : colorTermSpecs) {
			int[] rgb = colorTermRGB.get(termSpec);
			String h = "000000"+Integer.toString(((rgb[0]/257)<<16) | ((rgb[1]/257)<<8) | ((rgb[2]/257)), 16);
			h = h.substring(h.length()-6);
			out.print("<tr>");
			out.print("<td><code><a href=\"cn/"+fnencode(termSpec.getName())+".html\">"+htmlencode(termSpec.getName())+"</a></code></td>");
			out.print("<td>"+rgb[0]+"</td><td>"+rgb[1]+"</td><td>"+rgb[2]+"</td>");
			out.print("<td style=\"width: 100px; background: #"+h+";\">&nbsp;</td>");
			out.println("</tr>");
		}
		out.println("</table>");
		
		out.println("</body>");
		out.println("</html>");
		out.close();
		out.close();
	}
	
	private void writeOperators(Dialect dialect, VersionNumber dialectVersion) throws IOException {
		PrintWriter out = openFile(dialect, dialectVersion, "precedence.html");
		out.println("<html>");
		out.println("<head>");
		if (dialect == null) {
			out.println("<title>XION Operator Precedence Table</title>");
		} else if (dialectVersion == null) {
			out.println("<title>" + htmlencode(dialect.getTitle()) + " Operator Precedence Table</title>");
		} else {
			out.println("<title>" + htmlencode(dialect.getTitle()) + " " + htmlencode(dialectVersion.toString()) + " Operator Precedence Table</title>");
		}
		out.println("<meta name=\"generator\" content=\""+XIONDoc.XIONDOC_NAME+" "+XIONDoc.XIONDOC_VERSION+"\">");
		out.println("<meta name=\"keywords\" content=\""+baseKeywords(dialect, dialectVersion)+", operator, operators, operator precedence, operator precedence chart, operator precedence table\">");
		out.println("<meta name=\"description\" content=\"This page shows the order of precedence of operators in "+((dialect == null) ? "XION" : htmlencode(dialect.getTitle()))+".\">");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xiondoc.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>"+((dialect == null) ? "XION" : htmlencode(dialect.getTitle()))+" Operator Precedence Table</h1>");
		out.println("<p class=\"operatorintro\">The table below shows the order of precedence of operators in " +
				((dialect == null) ? "XION" : htmlencode(dialect.getTitle())) + ". " +
				"In a complex expression containing more than one operator, the operations " +
				"indicated by operators with lower-numbered precedence will be performed before " +
				"those with higher-numbered precedence. Operators of equal precedence are " +
				"evaluated left-to-right, except for exponentiation, which goes right-to-left. " +
				"If you use parentheses, the innermost parenthetical expression is evaluated " +
				"first.</p>");
		out.println("<table class=\"operatortable\">");
		out.println("<thead>");
		out.println("<tr>");
		out.println("<th>Order</th>");
		out.println("<th>Operators</th>");
		out.println("<th>Type of Operator</th>");
		out.println("</tr>");
		out.println("</thead>");
		Map<TermSpec,Term> operators = new HashMap<TermSpec,Term>();
		List<TermSpec> operatorList = new Vector<TermSpec>();
		for (Term term : ds.terms().getTerms(TermType.OPERATOR, null, ((dialect == null) ? null : dialect.name()), dialectVersion)) {
			for (TermName termName : term.names()) {
				if (dialect == null || termName.getDialects().matches(dialect.name(), dialectVersion)) {
					operators.put(new TermSpec(TermType.OPERATOR, termName.getName()), term);
				}
			}
		}
		for (Precedence precedence : Precedence.values()) {
			operatorList.clear();
			for (Map.Entry<TermSpec,Term> e : operators.entrySet()) {
				if (e.getValue().hasPrecedence() && e.getValue().getPrecedence() == precedence) {
					operatorList.add(e.getKey());
				}
			}
			Collections.sort(operatorList);
			if (!operatorList.isEmpty()) {
				out.println("<tbody>");
				boolean first = true;
				for (TermSpec termSpec : operatorList) {
					out.print("<tr>");
					if (first) {
						out.print("<td>"+precedence.getNumber()+" - "+htmlencode(precedence.getName())+"</td>");
						first = false;
					} else {
						out.print("<td></td>");
					}
					out.print("<td><code><a href=\"op/"+fnencode(termSpec.getName())+".html\">"+htmlencode(termSpec.getName())+"</a></code></td>");
					Term term = operators.get(termSpec);
					out.print("<td>"+(term.hasDescriptionShort() ? htmlencode(term.getDescriptionShort()) : "")+"</td>");
					out.println("</tr>");
				}
				out.println("</tbody>");
			}
		}
		out.println("</table>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private void writeConstants(Dialect dialect, VersionNumber dialectVersion) throws IOException {
		PrintWriter out = openFile(dialect, dialectVersion, "constants.html");
		out.println("<html>");
		out.println("<head>");
		if (dialect == null) {
			out.println("<title>XION Constant Summary</title>");
		} else if (dialectVersion == null) {
			out.println("<title>" + htmlencode(dialect.getTitle()) + " Constant Summary</title>");
		} else {
			out.println("<title>" + htmlencode(dialect.getTitle()) + " " + htmlencode(dialectVersion.toString()) + " Constant Summary</title>");
		}
		out.println("<meta name=\"generator\" content=\""+XIONDoc.XIONDOC_NAME+" "+XIONDoc.XIONDOC_VERSION+"\">");
		out.println("<meta name=\"keywords\" content=\""+baseKeywords(dialect, dialectVersion)+", constant, constants, built-in constant, built-in constants, constant summary\">");
		out.println("<meta name=\"description\" content=\"This page summarizes "+((dialect == null) ? "XION" : htmlencode(dialect.getTitle()))+"'s built-in constants.\">");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xiondoc.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>"+((dialect == null) ? "XION" : htmlencode(dialect.getTitle()))+" Constant Summary</h1>");
		out.println("<p>This page summarizes " + ((dialect == null) ? "XION" : htmlencode(dialect.getTitle())) + "'s built-in constants." +
				" A constant is a named value that never changes." +
				" You cannot change its value or use its name as a variable name." +
				" If you try, the interpreter will trigger a script error.</p>");

		final Map<TermSpec,Term> constants = new HashMap<TermSpec,Term>();
		final List<TermSpec> constantList = new Vector<TermSpec>();
		for (Term term : ds.terms().getTerms(TermType.CONSTANT, null, ((dialect == null) ? null : dialect.name()), dialectVersion)) {
			for (TermName termName : term.names()) {
				if (dialect == null || termName.getDialects().matches(dialect.name(), dialectVersion)) {
					TermSpec termSpec = new TermSpec(TermType.CONSTANT, termName.getName());
					constants.put(termSpec, term);
					constantList.add(termSpec);
				}
			}
		}
		
		Collections.sort(constantList, new Comparator<TermSpec>() {
			@Override
			public int compare(TermSpec a, TermSpec b) {
				return a.getName().compareToIgnoreCase(b.getName());
			}
		});
		out.println("<p class=\"constantintro\"><a name=\"byname\">The table below lists all the built-in constants by name." +
				" See also <a href=\"#bytype\">by data type</a> and <a href=\"#byvalue\">by value</a>.</a></p>");
		out.println("<table class=\"constanttable\">");
		out.println("<thead>");
		out.println("<tr>");
		out.println("<th>Constant Name</th>");
		out.println("<th>Data Type</th>");
		out.println("<th>Value</th>");
		out.println("</tr>");
		out.println("</thead>");
		out.println("<tbody>");
		for (TermSpec ts : constantList) {
			Term t = constants.get(ts);
			out.println("<tr>");
			out.println("<td><code><a href=\"cn/"+fnencode(ts.getName())+".html\">"+htmlencode(ts.getName())+"</a></code></td>");
			if (t.hasDataType()) {
				out.println("<td><code><a href=\"dt/"+fnencode(t.getDataType().getName())+"\">"+htmlencode(t.getDataType().getName())+"</a></code></td>");
			} else {
				out.println("<td></td>");
			}
			if (t.hasDataValue()) {
				out.println("<td><code>"+htmlencode(t.getDataValue())+"</code></td>");
			} else {
				out.println("<td></td>");
			}
			out.println("</tr>");
		}
		out.println("</tbody>");
		out.println("</table>");
		
		Collections.sort(constantList, new Comparator<TermSpec>() {
			@Override
			public int compare(TermSpec a, TermSpec b) {
				Term ta = constants.get(a);
				Term tb = constants.get(b);
				String tta = ta.hasDataType() ? ta.getDataType().getName() : "variant";
				String ttb = tb.hasDataType() ? tb.getDataType().getName() : "variant";
				return tta.compareToIgnoreCase(ttb);
			}
		});
		out.println("<p class=\"constantintro\"><a name=\"bytype\">The table below lists all the built-in constants by data type." +
				" See also <a href=\"#byname\">by name</a> and <a href=\"#byvalue\">by value</a>.</a></p>");
		out.println("<table class=\"constanttable\">");
		out.println("<thead>");
		out.println("<tr>");
		out.println("<th>Constant Name</th>");
		out.println("<th>Data Type</th>");
		out.println("<th>Value</th>");
		out.println("</tr>");
		out.println("</thead>");
		out.println("<tbody>");
		for (TermSpec ts : constantList) {
			Term t = constants.get(ts);
			out.println("<tr>");
			out.println("<td><code><a href=\"cn/"+fnencode(ts.getName())+".html\">"+htmlencode(ts.getName())+"</a></code></td>");
			if (t.hasDataType()) {
				out.println("<td><code><a href=\"dt/"+fnencode(t.getDataType().getName())+"\">"+htmlencode(t.getDataType().getName())+"</a></code></td>");
			} else {
				out.println("<td></td>");
			}
			if (t.hasDataValue()) {
				out.println("<td><code>"+htmlencode(t.getDataValue())+"</code></td>");
			} else {
				out.println("<td></td>");
			}
			out.println("</tr>");
		}
		out.println("</tbody>");
		out.println("</table>");

		Collections.sort(constantList, new Comparator<TermSpec>() {
			@Override
			public int compare(TermSpec a, TermSpec b) {
				Term ta = constants.get(a);
				Term tb = constants.get(b);
				String tta = ta.hasDataValue() ? ta.getDataValue() : "";
				String ttb = tb.hasDataValue() ? tb.getDataValue() : "";
				try {
					BigDecimal da = new BigDecimal(tta);
					BigDecimal db = new BigDecimal(ttb);
					return da.compareTo(db);
				} catch (NumberFormatException nfe) {
					return tta.compareToIgnoreCase(ttb);
				}
			}
		});
		out.println("<p class=\"constantintro\"><a name=\"byvalue\">The table below lists all the built-in constants by value." +
				" See also <a href=\"#byname\">by name</a> and <a href=\"#bytype\">by data type</a>.</a></p>");
		out.println("<table class=\"constanttable\">");
		out.println("<thead>");
		out.println("<tr>");
		out.println("<th>Constant Name</th>");
		out.println("<th>Data Type</th>");
		out.println("<th>Value</th>");
		out.println("</tr>");
		out.println("</thead>");
		out.println("<tbody>");
		for (TermSpec ts : constantList) {
			Term t = constants.get(ts);
			out.println("<tr>");
			out.println("<td><code><a href=\"cn/"+fnencode(ts.getName())+".html\">"+htmlencode(ts.getName())+"</a></code></td>");
			if (t.hasDataType()) {
				out.println("<td><code><a href=\"dt/"+fnencode(t.getDataType().getName())+".html\">"+htmlencode(t.getDataType().getName())+"</a></code></td>");
			} else {
				out.println("<td></td>");
			}
			if (t.hasDataValue()) {
				out.println("<td><code>"+htmlencode(t.getDataValue())+"</code></td>");
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
	
	private void writeSynonyms(Dialect dialect, VersionNumber dialectVersion) throws IOException {
		PrintWriter out = openFile(dialect, dialectVersion, "synonyms.html");
		out.println("<html>");
		out.println("<head>");
		if (dialect == null) {
			out.println("<title>XION Synonyms</title>");
		} else if (dialectVersion == null) {
			out.println("<title>" + htmlencode(dialect.getTitle()) + " Synonyms</title>");
		} else {
			out.println("<title>" + htmlencode(dialect.getTitle()) + " " + htmlencode(dialectVersion.toString()) + " Synonyms</title>");
		}
		out.println("<meta name=\"generator\" content=\""+XIONDoc.XIONDOC_NAME+" "+XIONDoc.XIONDOC_VERSION+"\">");
		out.println("<meta name=\"keywords\" content=\""+baseKeywords(dialect, dialectVersion)+", synonym, synonyms\">");
		out.println("<meta name=\"description\" content=\"This page lists the alternative ways that "+((dialect == null) ? "XION" : htmlencode(dialect.getTitle()))+" terms can be used.\">");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xiondoc.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>"+((dialect == null) ? "XION" : htmlencode(dialect.getTitle()))+" Synonyms</h1>");
		out.println("<p class=\"synonymintro\">The table below lists the alternative ways that "+((dialect == null) ? "XION" : htmlencode(dialect.getTitle()))+" terms can be used.</p>");
		
		final List<Term> termsWithSynonyms = new Vector<Term>();
		final Map<Term,List<TermSpec>> termSynonyms = new HashMap<Term,List<TermSpec>>();
		for (Term term : ds.terms().getTerms(null, null, ((dialect == null) ? null : dialect.name()), dialectVersion)) {
			if (term.hasSynonyms(((dialect == null) ? null : dialect.name()), dialectVersion)) {
				TermSpecList termSpecs = term.getSynonyms(((dialect == null) ? null : dialect.name()), dialectVersion, null);
				List<TermSpec> termSpecs2 = new Vector<TermSpec>();
				termSpecs2.addAll(termSpecs);
				Collections.sort(termSpecs2);
				termsWithSynonyms.add(term);
				termSynonyms.put(term, termSpecs2);
			}
		}
		Collections.sort(termsWithSynonyms, new Comparator<Term>() {
			@Override
			public int compare(Term a, Term b) {
				return termSynonyms.get(a).get(0).getName().compareToIgnoreCase(
						termSynonyms.get(b).get(0).getName());
			}
		});
		
		out.println("<table class=\"synonymtable\">");
		out.println("<thead>");
		out.println("<tr>");
		out.println("<th>Synonym</th>");
		out.println("<th>Term</th>");
		out.println("</tr>");
		out.println("</thead>");
		for (Term term : termsWithSynonyms) {
			out.println("<tbody>");
			List<TermSpec> termSpecs = termSynonyms.get(term);
			int n = termSpecs.size();
			for (int i = 0; i < n; i++) {
				TermSpec termSpec = termSpecs.get(i);
				if ((i % 4) == 0) out.println("<tr>");
				out.println("<td><code><a href=\""+fnencode(termSpec.getType().getCode())+"/"+fnencode(termSpec.getName())+".html\">"+htmlencode(termSpec.getName())+"</a></td>");
				if (((i % 4) == 3) || (i == (n-1))) out.println("</tr>");
			}
			out.println("</tbody>");
		}
		out.println("</table>");
		
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private void writeArticle(Dialect dialect, VersionNumber dialectVersion, Article article) throws IOException {
		PrintWriter out = openFile(dialect, dialectVersion, fnencode(article.name())+".html");
		out.println("<html>");
		out.println("<head>");
		out.println("<title>"+htmlencode(article.getTitle())+"</title>");
		out.println("<meta name=\"generator\" content=\""+XIONDoc.XIONDOC_NAME+" "+XIONDoc.XIONDOC_VERSION+"\">");
		out.println("<meta name=\"keywords\" content=\""+baseKeywords(dialect, dialectVersion)+", "+htmlencode(article.getTitle())+"\">");
		if (article.hasSummary()) {
			out.println("<meta name=\"description\" content=\""+htmlencode(article.getSummary())+"\">");
		}
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xiondoc.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1 class=\"articleheader\">"+htmlencode(article.getTitle())+"</h1>");
		if (article.hasContent()) {
			out.println(sdomg.generateSectionHTML(article.getContent()));
		}
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private void writeVocabTypeIndex(Dialect dialect, VersionNumber dialectVersion, List<Article> articles) throws IOException {
		PrintWriter out = openFile(dialect, dialectVersion, "vocabtypes.html");
		out.println("<html>");
		out.println("<head>");
		if (dialect == null) {
			out.println("<title>XION Vocabulary Type Index</title>");
		} else if (dialectVersion == null) {
			out.println("<title>" + htmlencode(dialect.getTitle()) + " Vocabulary Type Index</title>");
		} else {
			out.println("<title>" + htmlencode(dialect.getTitle()) + " " + htmlencode(dialectVersion.toString()) + " Vocabulary Type Index</title>");
		}
		out.println("<meta name=\"generator\" content=\""+XIONDoc.XIONDOC_NAME+" "+XIONDoc.XIONDOC_VERSION+"\">");
		out.println("<meta name=\"keywords\" content=\""+baseKeywords(dialect, dialectVersion)+", vocabulary type, vocabulary type index\">");
		out.println("<meta name=\"description\" content=\"A list of the types of XION vocabulary terms in "+((dialect == null) ? "all dialects, modules, and libraries in this documentation set" : htmlencode(dialect.getTitle()))+".\">");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xionnav.css\">");
		out.println("<script language=\"javascript\" type=\"text/javascript\">");
		out.println("<!--");
		out.println("function loadAllVocab() {");
		out.println("parent.xnvocab.location.href='all-index.html';");
		out.println("parent.xncontent.location.href='intro.html';");
		out.println("return false;");
		out.println("}");
		out.println("function loadVocabType(x) {");
		out.println("parent.xnvocab.location.href=x+'-index.html';");
		out.println("parent.xncontent.location.href=x+'/index.html';");
		out.println("return false;");
		out.println("}");
		out.println("//-->");
		out.println("</script>");
		out.println("</head>");
		out.println("<body>");
		if (dialect == null) {
			out.println("<h1 class=\"vocabtypelistheader\">" + allString() + "</h1>");
		} else if (dialectVersion == null) {
			out.println("<h1 class=\"vocabtypelistheader\">" + htmlencode(dialect.getTitle()) + "</h1>");
		} else {
			out.println("<h1 class=\"vocabtypelistheader\">" + htmlencode(dialect.getTitle()) + " " + htmlencode(dialectVersion.toString()) + "</h1>");
		}
		out.println("<ul class=\"vocabtypelist\">");
		out.println("<li class=\"vocabtypelistitem\"><a href=\"all-index.html\" target=\"xnvocab\" onclick=\"return loadAllVocab();\">All Vocabulary</a></li>");
		for (TermType termType : TermType.values()) {
			if (!ds.terms().getTerms(termType, null, ((dialect == null) ? null : dialect.name()), dialectVersion).isEmpty()) {
				out.println("<li class=\"vocabtypelistitem\"><a href=\""+htmlencode(termType.getCode())+"-index.html\" target=\"xnvocab\" onclick=\"return loadVocabType('"+htmlencode(termType.getCode())+"');\">"+htmlencode(termType.getPluralTitleCase())+"</a></li>");
			}
		}
		out.println("</ul>");
		if (articles != null && !articles.isEmpty()) {
			out.println("<ul class=\"vocabtypelist\">");
			for (Article article : articles) {
				out.println("<li class=\"vocabtypelistitem\"><a href=\""+htmlencode(article.name())+".html\" target=\"xncontent\">"+htmlencode(article.getTitle())+"</a></li>");
			}
			out.println("</ul>");
		}
		out.println("<ul class=\"vocabtypelist\">");
		if (!ds.terms().getTerms(TermType.CONSTANT, null, ((dialect == null) ? null : dialect.name()), dialectVersion).isEmpty()) {
			out.println("<li class=\"vocabtypelistitem\"><a href=\"constants.html\" target=\"xncontent\">Constant Summary</a></li>");
		}
		if (!ds.terms().getTerms(TermType.OPERATOR, null, ((dialect == null) ? null : dialect.name()), dialectVersion).isEmpty()) {
			out.println("<li class=\"vocabtypelistitem\"><a href=\"precedence.html\" target=\"xncontent\">Operator Precedence Table</a></li>");
		}
		boolean iHasASynonym = false;
		for (Term term : ds.terms().getTerms(null, null, ((dialect == null) ? null : dialect.name()), dialectVersion)) {
			if (term.hasSynonyms(((dialect == null) ? null : dialect.name()), dialectVersion)) {
				iHasASynonym = true;
				break;
			}
		}
		if (iHasASynonym) {
			out.println("<li class=\"vocabtypelistitem\"><a href=\"synonyms.html\" target=\"xncontent\">Synonyms</a></li>");
		}
		boolean iHasAColor = false;
		for (Term term : ds.terms().getTerms(TermType.CONSTANT, null, ((dialect == null) ? null : dialect.name()), dialectVersion)) {
			if (term.getDataType() != null && (term.getDataType().getName().equalsIgnoreCase("color") || term.getDataType().getName().equalsIgnoreCase("colour"))) {
				iHasAColor = true;
				break;
			}
		}
		if (iHasAColor) {
			out.println("<li class=\"vocabtypelistitem\"><a href=\"colors.html\" target=\"xncontent\">Color Chart</a></li>");
		}
		out.println("<li class=\"vocabtypelistitem\"><a href=\"index-a.html\" target=\"xncontent\">Index</a></li>");
		out.println("</ul>");
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private String allString() {
		boolean hasDialects = false;
		boolean hasModules = false;
		boolean hasLibraries = false;
		for (Dialect d : ds.dialects()) {
			switch (d.type()) {
			case DIALECT: hasDialects = true; break;
			case MODULE: hasModules = true; break;
			case LIBRARY: hasLibraries = true; break;
			}
		}
		List<String> things = new Vector<String>();
		String allString;
		if (hasDialects) things.add("Dialects");
		if (hasModules) things.add("Modules");
		if (hasLibraries) things.add("Libraries");
		switch (things.size()) {
		case 0: allString = "All Dialects, Modules, and Libraries"; break;
		case 1: allString = "All " + things.get(0); break;
		case 2: allString = "All " + things.get(0) + " and " + things.get(1); break;
		default:
			allString = "All ";
			for (int i = 0; i < things.size(); i++) {
				allString += things.get(i);
				if (i <= things.size()-2) allString += ", ";
				if (i == things.size()-2) allString += "and ";
			}
		}
		return allString;
	}
	
	private void writeDialectIndex(Dialect dialect, VersionNumber dialectVersion) throws IOException {
		PrintWriter out = openFile(dialect, dialectVersion, "dialects.html");
		out.println("<html>");
		out.println("<head>");
		out.println("<title>XION Dialect Index</title>");
		out.println("<meta name=\"generator\" content=\""+XIONDoc.XIONDOC_NAME+" "+XIONDoc.XIONDOC_VERSION+"\">");
		out.println("<meta name=\"keywords\" content=\""+baseKeywords(dialect, dialectVersion)+", dialect, dialects, dialect index, module, modules, module index, library, libraries, library index\">");
		out.println("<meta name=\"description\" content=\"A list of XION dialects, OpenXION modules, and XION code libraries with documentation available in this documentation set.\">");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xionnav.css\">");
		out.println("<script language=\"javascript\" type=\"text/javascript\">");
		out.println("<!--");
		out.println("function loadAllDialects() {");
		out.println("parent.xnvocabtypes.location.href='vocabtypes.html';");
		out.println("parent.xnvocab.location.href='all-index.html';");
		out.println("parent.xncontent.location.href='intro.html';");
		out.println("return false;");
		out.println("}");
		if (dialect == null) {
			out.println("function loadDialect(x) {");
			out.println("parent.xnvocabtypes.location.href=x+'/vocabtypes.html';");
			out.println("parent.xnvocab.location.href=x+'/all-index.html';");
			out.println("parent.xncontent.location.href=x+'/intro.html';");
			out.println("return false;");
			out.println("}");
		} else {
			out.println("function loadDialect(x) {");
			out.println("parent.xnvocabtypes.location.href='vocabtypes.html';");
			out.println("parent.xnvocab.location.href='all-index.html';");
			out.println("parent.xncontent.location.href='intro.html';");
			out.println("return false;");
			out.println("}");
		}
		if (dialect == null) {
			out.println("function loadDialectVersion(x, y) {");
			out.println("parent.xnvocabtypes.location.href=x+'/'+y+'/vocabtypes.html';");
			out.println("parent.xnvocab.location.href=x+'/'+y+'/all-index.html';");
			out.println("parent.xncontent.location.href=x+'/'+y+'/intro.html';");
			out.println("return false;");
			out.println("}");
		} else if (dialectVersion == null) {
			out.println("function loadDialectVersion(x, y) {");
			out.println("parent.xnvocabtypes.location.href=y+'/vocabtypes.html';");
			out.println("parent.xnvocab.location.href=y+'/all-index.html';");
			out.println("parent.xncontent.location.href=y+'/intro.html';");
			out.println("return false;");
			out.println("}");
		} else {
			out.println("function loadDialectVersion(x, y) {");
			out.println("parent.xnvocabtypes.location.href='vocabtypes.html';");
			out.println("parent.xnvocab.location.href='all-index.html';");
			out.println("parent.xncontent.location.href='intro.html';");
			out.println("return false;");
			out.println("}");
		}
		out.println("//-->");
		out.println("</script>");
		out.println("</head>");
		out.println("<body>");
		
		List<Dialect> dialects = new Vector<Dialect>();
		List<Dialect> modules = new Vector<Dialect>();
		List<Dialect> libraries = new Vector<Dialect>();
		if (dialect == null) {
			for (Dialect d : ds.dialects()) {
				switch (d.type()) {
				case DIALECT: dialects.add(d); break;
				case MODULE: modules.add(d); break;
				case LIBRARY: libraries.add(d); break;
				}
			}
		} else {
			switch (dialect.type()) {
			case DIALECT: dialects.add(dialect); break;
			case MODULE: modules.add(dialect); break;
			case LIBRARY: libraries.add(dialect); break;
			}
		}
		List<String> things = new Vector<String>();
		String allString;
		if (!dialects.isEmpty()) things.add("Dialects");
		if (!modules.isEmpty()) things.add("Modules");
		if (!libraries.isEmpty()) things.add("Libraries");
		switch (things.size()) {
		case 0: allString = "All Dialects, Modules, and Libraries"; break;
		case 1: allString = "All " + things.get(0); break;
		case 2: allString = "All " + things.get(0) + " and " + things.get(1); break;
		default:
			allString = "All ";
			for (int i = 0; i < things.size(); i++) {
				allString += things.get(i);
				if (i <= things.size()-2) allString += ", ";
				if (i == things.size()-2) allString += "and ";
			}
		}
		
		out.println("<ul class=\"dialectlist\">");
		out.println("<li class=\"dialectlistitem\">");
		out.println("<a href=\"vocabtypes.html\" target=\"xnvocabtypes\" onclick=\"return loadAllDialects();\">" + allString + "</a>");
		out.println("</li>");
		out.println("</ul>");
		if (!dialects.isEmpty()) {
			out.println("<h1 class=\"dialectlistheader\">Dialects</h1>");
			writeDialectList(dialect, dialectVersion, out, dialects);
		}
		if (!modules.isEmpty()) {
			out.println("<h1 class=\"dialectlistheader\">Modules</h1>");
			writeDialectList(dialect, dialectVersion, out, modules);
		}
		if (!libraries.isEmpty()) {
			out.println("<h1 class=\"dialectlistheader\">Libraries</h1>");
			writeDialectList(dialect, dialectVersion, out, libraries);
		}
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private void writeDialectList(Dialect dialect, VersionNumber dialectVersion, PrintWriter out, List<Dialect> dialects) {
		out.println("<ul class=\"dialectlist\">");
		for (Dialect d : dialects) {
			String href = ((dialect == null) ? (htmlencode(d.name()) + "/vocabtypes.html") : "vocabtypes.html");
			out.println("<li class=\"dialectlistitem\">");
			out.println("<a href=\""+href+"\" target=\"xnvocabtypes\" onclick=\"return loadDialect('"+htmlencode(d.name())+"');\">"+htmlencode(d.getTitle())+"</a>");
			out.print("<span class=\"dversion\">(");
			if (dialectVersion == null || dialect == null) {
				boolean first = true;
				for (VersionNumber dv : d.versions().descendingSet()) {
					String vhref = (
							(dialect == null)
							? (htmlencode(d.name()) + "/" + htmlencode(dv.toString()) + "/vocabtypes.html")
							: (htmlencode(dv.toString()) + "/vocabtypes.html")
					);
					if (first) first = false;
					else out.print(", ");
					out.print("<a href=\""+vhref+"\" target=\"xnvocabtypes\" onclick=\"return loadDialectVersion('"+htmlencode(d.name())+"','"+htmlencode(dv.toString())+"');\">"+htmlencode(dv.toString())+"</a>");
				}
			} else {
				out.print("<a href=\"vocabtypes.html\" target=\"xnvocabtypes\" onclick=\"return loadDialectVersion('"+htmlencode(d.name())+"','"+htmlencode(dialectVersion.toString())+"');\">"+htmlencode(dialectVersion.toString())+"</a>");
			}
			out.println(")</span>");
			out.println("</li>");
		}
		out.println("</ul>");
	}
	
	private void writeIntro(Dialect dialect, VersionNumber dialectVersion) throws IOException {
		PrintWriter out = openFile(dialect, dialectVersion, "intro.html");
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Welcome to XION</title>");
		out.println("<meta name=\"generator\" content=\""+XIONDoc.XIONDOC_NAME+" "+XIONDoc.XIONDOC_VERSION+"\">");
		out.println("<meta name=\"keywords\" content=\""+baseKeywords(dialect, dialectVersion)+", intro, introduction\">");
		if (dialect == null) {
			if (ds.hasSummary()) {
				out.println("<meta name=\"description\" content=\"" + htmlencode(ds.getSummary()) + "\">");
			}
		} else {
			if (dialect.hasSummary()) {
				out.println("<meta name=\"description\" content=\"" + htmlencode(dialect.getSummary()) + "\">");
			}
		}
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"xiondoc.css\">");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1 class=\"articleheader\">Welcome to XION</h1>");
		if (dialect == null) {
			if (ds.hasDescription()) {
				out.println(sdomg.generateSectionHTML(ds.getDescription()));
			}
		} else {
			if (dialect.hasDescription()) {
				out.println(sdomg.generateSectionHTML(dialect.getDescription()));
			}
		}
		out.println("</body>");
		out.println("</html>");
		out.close();
	}
	
	private static String mainCSS = null;
	private void writeMainCSS(Dialect dialect, VersionNumber dialectVersion) throws IOException {
		if (mainCSS == null) {
			URL u = HTMLXDOMGenerator.class.getResource("xiondoc.css");
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
		PrintWriter out = openFile(dialect, dialectVersion, "xiondoc.css");
		out.print(mainCSS);
		out.close();
	}
	
	private static String navCSS = null;
	private void writeNavCSS(Dialect dialect, VersionNumber dialectVersion) throws IOException {
		if (navCSS == null) {
			URL u = HTMLXDOMGenerator.class.getResource("xionnav.css");
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
		PrintWriter out = openFile(dialect, dialectVersion, "xionnav.css");
		out.print(navCSS);
		out.close();
	}
	
	private void writeFrameset(Dialect dialect, VersionNumber dialectVersion) throws IOException {
		PrintWriter out = openFile(dialect, dialectVersion, "index.html");
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" \"http://www.w3.org/TR/html4/frameset.dtd\">");
		out.println("<html>");
		out.println("<head>");
		if (dialect == null) {
			out.println("<title>XION Documentation</title>");
		} else if (dialectVersion == null) {
			out.println("<title>" + htmlencode(dialect.getTitle()) + " Documentation</title>");
		} else {
			out.println("<title>" + htmlencode(dialect.getTitle()) + " " + htmlencode(dialectVersion.toString()) + " Documentation</title>");
		}
		out.println("<meta name=\"generator\" content=\""+XIONDoc.XIONDOC_NAME+" "+XIONDoc.XIONDOC_VERSION+"\">");
		out.println("<meta name=\"keywords\" content=\""+baseKeywords(dialect, dialectVersion)+"\">");
		if (dialect == null) {
			if (ds.hasSummary()) {
				out.println("<meta name=\"description\" content=\"" + htmlencode(ds.getSummary()) + "\">");
			}
		} else {
			if (dialect.hasSummary()) {
				out.println("<meta name=\"description\" content=\"" + htmlencode(dialect.getSummary()) + "\">");
			}
		}
		out.println("</head>");
		out.println("<frameset cols=\"320,*\">");
		out.println("<frameset rows=\"160,240,*\">");
		out.println("<frame name=\"xndialects\" src=\"dialects.html\" />");
		out.println("<frame name=\"xnvocabtypes\" src=\"vocabtypes.html\" />");
		out.println("<frame name=\"xnvocab\" src=\"all-index.html\" />");
		out.println("</frameset>");
		out.println("<frame name=\"xncontent\" src=\"intro.html\" />");
		out.println("</frameset>");
		out.println("</html>");
		out.close();
	}
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	private String baseKeywords(Dialect dialect, VersionNumber dialectVersion) {
		StringBuffer s = new StringBuffer();
		s.append("XION, OpenXION, XIONDoc, XIONDocs, XION docs, OpenXION docs, ");
		s.append("XION documentation, OpenXION documentation, XION manual, OpenXION manual, ");
		s.append("HyperTalk, HyperTalk clone, xTalk");
		if (dialect != null) {
			s.append(", ");
			s.append(htmlencode(dialect.getTitle()));
			if (dialectVersion != null) {
				s.append(", ");
				s.append(htmlencode(dialect.getTitle()));
				s.append(" ");
				s.append(htmlencode(dialectVersion.toString()));
			}
		}
		return s.toString();
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
				if (ch < 0x20 || (ch >= 0x7F && ch < 0xA0)) {
					out.append(" ");
				} else if (ch >= 0xA0) {
					out.append("&#"+(int)ch+";");
				} else {
					out.append(ch);
				}
				break;
			}
		}
		return out.toString().trim().replaceAll("\\s+", " ");
	}
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	private void deltree(File f) {
		if (f.isDirectory()) {
			for (File ff : f.listFiles()) {
				deltree(ff);
			}
		}
		f.delete();
	}
	
	private PrintWriter openFile(Dialect dialect, VersionNumber dialectVersion, TermType type, String name) throws IOException {
		File outf = getFile(((dialect == null) ? null : dialect.name()), dialectVersion, type, name);
		if (out != null) out.println("Writing " + fileToString(outf) + "...");
		return new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
	}
	
	private PrintWriter openFile(Dialect dialect, VersionNumber dialectVersion, String name) throws IOException {
		File outf = getFile(((dialect == null) ? null : dialect.name()), dialectVersion, name);
		if (out != null) out.println("Writing " + fileToString(outf) + "...");
		return new PrintWriter(new OutputStreamWriter(new FileOutputStream(outf), "UTF-8"), true);
	}
	
	private File getFile(String dialectName, VersionNumber dialectVersion, TermType type, String name) {
		return new File(getFile(dialectName, dialectVersion, type), fnencode(name.toLowerCase()) + ".html");
	}
	
	private File getFile(String dialectName, VersionNumber dialectVersion, TermType type) {
		File tbase = new File(getFile(dialectName, dialectVersion), type.getCode());
		if (!tbase.exists()) tbase.mkdir();
		return tbase;
	}
	
	private File getFile(String dialectName, VersionNumber dialectVersion, String name) {
		return new File(getFile(dialectName, dialectVersion), name.toLowerCase());
	}
	
	private File getFile(String dialectName, VersionNumber dialectVersion) {
		if (!base.exists()) base.mkdir();
		if (dialectName != null) {
			File dbase = new File(base, dialectName.toLowerCase());
			if (!dbase.exists()) dbase.mkdir();
			if (dialectVersion != null) {
				File vbase = new File(dbase, dialectVersion.toString().toLowerCase());
				if (!vbase.exists()) vbase.mkdir();
				return vbase;
			} else {
				return dbase;
			}
		} else {
			return base;
		}
	}
	
	private String fileToString(File f) {
		return f.getAbsolutePath().substring(basePath.length());
	}
	
	private static String fnencode(String in) {
		CharacterIterator it = new StringCharacterIterator(in);
		StringBuffer out = new StringBuffer();
		boolean seenLetter = false;
		for (char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
			if (Character.isLetterOrDigit(ch)) {
				out.append(ch);
				seenLetter = true;
			} else if ((ch == ' ' || ch == '-' || ch == '_' || ch == '.') && seenLetter) {
				out.append(ch);
			} else {
				String h = "0000" + Integer.toHexString((int)ch).toUpperCase();
				out.append('$');
				out.append(h.substring(h.length() - 4));
			}
		}
		return out.toString().trim().replaceAll("\\s+", "_");
	}
}
