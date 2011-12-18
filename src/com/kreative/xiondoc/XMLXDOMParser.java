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

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kreative.xiondoc.sdom.Section;
import com.kreative.xiondoc.xdom.*;

/**
 * The XMLXDOMParser class takes a W3C DOM node as input
 * and produces an XIONDoc XDOM DocumentationSet from its children.
 * @since XIONDoc 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XMLXDOMParser {
	private XMLSDOMParser sdomp;
	private PrintWriter out;
	
	public XMLXDOMParser(boolean output) {
		this.sdomp = new XMLSDOMParser();
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
	
	public XMLXDOMParser(PrintWriter out) {
		this.sdomp = new XMLSDOMParser();
		this.out = out;
	}
	
	public DocumentationSet parseDocument(Document node, DocumentationSet ds) {
		if (ds == null) ds = new DocumentationSet();
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String type = child.getNodeName();
			if (type.equalsIgnoreCase("xiondoc")) parseDocSet(child, ds);
			else if (type.equalsIgnoreCase("docset")) parseDocSet(child, ds);
			else if (type.equalsIgnoreCase("documentationset")) parseDocSet(child, ds);
		}
		return ds;
	}
	
	public DocumentationSet parseDocSet(Node node, DocumentationSet ds) {
		if (ds == null) ds = new DocumentationSet();
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String type = child.getNodeName();
			if (type.equalsIgnoreCase("summary")) {
				ds.appendSummary(child.getTextContent().trim().replaceAll("\\s+", " "));
			}
			else if (type.equalsIgnoreCase("description")) {
				ds.appendDescription(sdomp.parseSection(child));
			}
			else if (type.equalsIgnoreCase("article")) {
				ds.articles().add(parseArticle(child));
			}
			else if (DialectType.forString(type.toLowerCase()) != null) {
				ds.dialects().add(parseDialect(child));
			}
			else if (TermType.forTagName(type.toLowerCase()) != null) {
				ds.terms().addTerm(parseTerm(child));
			}
		}
		return ds;
	}
	
	public Dialect parseDialect(Node node) {
		DialectType dt = DialectType.forString(node.getNodeName().toLowerCase());
		String name = null;
		String title = null;
		VersionNumberList versions = null;
		String summary = null;
		Section description = null;
		List<Article> articles = new Vector<Article>();
		if (out != null) out.print("Reading " + dt.toString().toLowerCase() + "...");
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String type = child.getNodeName();
			if (type.equalsIgnoreCase("name")) {
				name = child.getTextContent().trim();
				if (out != null) out.print("\b\b\b " + name + "...");
			}
			else if (type.equalsIgnoreCase("title")) {
				if (title == null) title = child.getTextContent().trim().replaceAll("\\s+", " ");
				else title += " " + child.getTextContent().trim().replaceAll("\\s+", " ");
			}
			else if (type.equalsIgnoreCase("versions")) {
				if (versions == null) versions = new VersionNumberList(child.getTextContent().trim());
				else versions.addAll(new VersionNumberList(child.getTextContent().trim()));
			}
			else if (type.equalsIgnoreCase("summary")) {
				if (summary == null) summary = child.getTextContent().trim().replaceAll("\\s+", " ");
				else summary += " " + child.getTextContent().trim().replaceAll("\\s+", " ");
			}
			else if (type.equalsIgnoreCase("description")) {
				if (description == null) description = sdomp.parseSection(child);
				else description.addAll(sdomp.parseSection(child));
			}
			else if (type.equalsIgnoreCase("article")) {
				if (out != null) out.println();
				articles.add(parseArticle(child));
			}
		}
		if (out != null) out.println();
		Dialect d = new Dialect(dt, name);
		d.setTitle(title);
		if (versions != null) d.versions().addAll(versions);
		d.setSummary(summary);
		d.setDescription(description);
		d.articles().addAll(articles);
		return d;
	}
	
	public Article parseArticle(Node node) {
		String name = null;
		String title = null;
		String summary = null;
		Section content = null;
		if (out != null) out.print("Reading article...");
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String type = child.getNodeName();
			if (type.equalsIgnoreCase("name")) {
				name = child.getTextContent().trim();
				if (out != null) out.print("\b\b\b " + name + "...");
			}
			else if (type.equalsIgnoreCase("title")) {
				if (title == null) title = child.getTextContent().trim().replaceAll("\\s+", " ");
				else title += " " + child.getTextContent().trim().replaceAll("\\s+", " ");
			}
			else if (type.equalsIgnoreCase("summary")) {
				if (summary == null) summary = child.getTextContent().trim().replaceAll("\\s+", " ");
				else summary += " " + child.getTextContent().trim().replaceAll("\\s+", " ");
			}
			else if (type.equalsIgnoreCase("content")) {
				if (content == null) content = sdomp.parseSection(child);
				else content.addAll(sdomp.parseSection(child));
			}
		}
		if (out != null) out.println();
		Article a = new Article(name);
		a.setTitle(title);
		a.setSummary(summary);
		a.setContent(content);
		return a;
	}
	
	public Term parseTerm(Node node) {
		TermType tt = TermType.forTagName(node.getNodeName());
		Term t = new Term(tt);
		if (out != null) out.print("Reading " + tt.getSingular() + "...");
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String type = child.getNodeName();
			if (type.equalsIgnoreCase("name")) {
				NamedNodeMap attr = child.getAttributes();
				Node dialects = attr.getNamedItem("dialects");
				Node modules = attr.getNamedItem("modules");
				Node libraries = attr.getNamedItem("libraries");
				String theName = child.getTextContent().trim();
				StringBuffer theDialects = new StringBuffer();
				if (dialects != null) {
					theDialects.append(dialects.getTextContent().trim());
					theDialects.append(',');
				}
				if (modules != null) {
					theDialects.append(modules.getTextContent().trim());
					theDialects.append(',');
				}
				if (libraries != null) {
					theDialects.append(libraries.getTextContent().trim());
					theDialects.append(',');
				}
				if (out != null) out.print("\b\b\b " + theName + "...");
				t.names().add(new TermName(theName, new DialectSpecList(theDialects.toString())));
			}
			else if (type.equalsIgnoreCase("applies-to")) {
				t.appendAppliesTo(sdomp.parseSection(child));
			}
			else if (type.equalsIgnoreCase("precedence")) {
				t.setPrecedence(Precedence.forString(child.getTextContent().trim()));
			}
			else if (type.equalsIgnoreCase("description-short")) {
				t.appendDescriptionShort(child.getTextContent().trim().replaceAll("\\s+", " "));
			}
			else if (type.equalsIgnoreCase("type")) {
				t.setDataType(new TermSpec(child.getTextContent().trim(), TermType.DATA_TYPE));
			}
			else if (type.equalsIgnoreCase("value")) {
				t.setDataValue(child.getTextContent());
			}
			else if (type.equalsIgnoreCase("syntax")) {
				t.appendSyntax(sdomp.parseSection(child));
			}
			else if (type.equalsIgnoreCase("examples")) {
				t.appendExamples(sdomp.parseSection(child));
			}
			else if (type.equalsIgnoreCase("description")) {
				t.appendDescription(sdomp.parseSection(child));
			}
			else if (type.equalsIgnoreCase("descriptors")) {
				String[] descStrings = child.getTextContent().trim().split(",");
				for (int j = 0; j < descStrings.length; j++) {
					Descriptor d = Descriptor.forCode(descStrings[j].trim());
					if (d != null) t.descriptors().add(d);
				}
			}
			else if (type.equalsIgnoreCase("properties")) {
				t.properties().addAll(new TermSpecList(child.getTextContent().trim(), TermType.PROPERTY));
			}
			else if (type.equalsIgnoreCase("scripts")) {
				t.appendScripts(sdomp.parseSection(child));
			}
			else if (type.equalsIgnoreCase("notes")) {
				t.appendNotes(sdomp.parseSection(child));
			}
			else if (type.equalsIgnoreCase("security")) {
				t.appendSecurity(sdomp.parseSection(child));
			}
			else if (type.equalsIgnoreCase("compatibility")) {
				t.appendCompatibility(sdomp.parseSection(child));
			}
			else if (type.equalsIgnoreCase("see-also")) {
				t.seeAlso().addAll(new TermSpecList(child.getTextContent().trim(), tt));
			}
		}
		if (out != null) out.println();
		return t;
	}
}
