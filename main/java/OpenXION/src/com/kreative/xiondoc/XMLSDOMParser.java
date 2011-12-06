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

import java.util.List;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.kreative.xiondoc.sdom.*;

/**
 * The XMLSDOMParser class takes a W3C DOM node as input
 * and produces an XIONDoc SDOM Section from its children.
 * @since XIONDoc 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XMLSDOMParser {
	public Section parseSection(Node node) {
		return parseBlockList(node, new Section());
	}
	
	private <L extends List<Block>> L parseBlockList(Node node, L blocks) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String type = child.getNodeName();
			NamedNodeMap attr = child.getAttributes();
			Node indent = (attr == null) ? null : attr.getNamedItem("indent");
			int in;
			if (indent != null) {
				try {
					in = Integer.parseInt(indent.getTextContent().trim());
				} catch (NumberFormatException nfe) {
					in = 0;
				}
			} else {
				in = 0;
			}
			if (type.equalsIgnoreCase("scr")) blocks.add(new Script(in, child.getTextContent(), true));
			else if (type.equalsIgnoreCase("syn")) blocks.add(parseSyntacticList(child, new Syntax(in)));
			else if (type.equalsIgnoreCase("ul")) {
				Node border = (attr == null) ? null : attr.getNamedItem("border");
				int bd;
				if (border != null) {
					try {
						bd = Integer.parseInt(border.getTextContent().trim());
					} catch (NumberFormatException nfe) {
						bd = 0;
					}
				} else {
					bd = 0;
				}
				blocks.add(parseListItemList(child, new UnorderedList(in, bd)));
			}
			else if (type.equalsIgnoreCase("ol")) {
				Node border = (attr == null) ? null : attr.getNamedItem("border");
				int bd;
				if (border != null) {
					try {
						bd = Integer.parseInt(border.getTextContent().trim());
					} catch (NumberFormatException nfe) {
						bd = 0;
					}
				} else {
					bd = 0;
				}
				blocks.add(parseListItemList(child, new OrderedList(in, bd)));
			}
			else if (type.equalsIgnoreCase("table")) {
				Node border = (attr == null) ? null : attr.getNamedItem("border");
				int bd;
				if (border != null) {
					try {
						bd = Integer.parseInt(border.getTextContent().trim());
					} catch (NumberFormatException nfe) {
						bd = 0;
					}
				} else {
					bd = 0;
				}
				blocks.add(parseTableRowList(child, new Table(in, bd)));
			}
			else if (type.equalsIgnoreCase("p")) blocks.add(parseSpanList(child, new Paragraph(in)));
			else if (type.equalsIgnoreCase("blockquote")) blocks.add(parseSpanList(child, new Paragraph(in+1)));
			else if (type.equalsIgnoreCase("h1")) blocks.add(parseSpanList(child, new H1(in)));
			else if (type.equalsIgnoreCase("h2")) blocks.add(parseSpanList(child, new H2(in)));
			else if (type.equalsIgnoreCase("h3")) blocks.add(parseSpanList(child, new H3(in)));
			else if (type.equalsIgnoreCase("h4")) blocks.add(parseSpanList(child, new H4(in)));
			else if (type.equalsIgnoreCase("h5")) blocks.add(parseSpanList(child, new H5(in)));
			else if (type.equalsIgnoreCase("h6")) blocks.add(parseSpanList(child, new H6(in)));
			else if (type.equalsIgnoreCase("hr")) blocks.add(new HorizontalRule(in));
			else if (type.equalsIgnoreCase("img")) {
				Node src = (attr == null) ? null : attr.getNamedItem("src");
				Node width = (attr == null) ? null : attr.getNamedItem("width");
				Node height = (attr == null) ? null : attr.getNamedItem("height");
				Node alt = (attr == null) ? null : attr.getNamedItem("alt");
				Node title = (attr == null) ? null : attr.getNamedItem("title");
				String srcs = ((src == null) ? "" : src.getTextContent().trim());
				if (srcs.length() == 0) srcs = child.getTextContent().trim();
				blocks.add(new Image(
						in,
						srcs,
						((width == null) ? "" : width.getTextContent().trim()),
						((height == null) ? "" : height.getTextContent().trim()),
						((alt == null) ? "" : alt.getTextContent().trim()),
						((title == null) ? "" : title.getTextContent().trim())
				));
			}
		}
		return blocks;
	}
	
	private <L extends List<Syntactic>> L parseSyntacticList(Node node, L syntax) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String type = child.getNodeName();
			if (type.equalsIgnoreCase("#text") || type.equalsIgnoreCase("kwd")) {
				String[] words = child.getTextContent().trim().split("\\s+");
				for (String word : words) {
					if (word.length() > 0) {
						syntax.add(new Keyword(word));
					}
				}
			}
			else if (type.equalsIgnoreCase("mv")) {
				String[] words = child.getTextContent().trim().split("\\s+");
				for (String word : words) {
					if (word.length() > 0) {
						syntax.add(new Metavariable(word));
					}
				}
			}
			else if (type.equalsIgnoreCase("opt")) syntax.add(parseSyntacticList(child, new Optional()));
			else if (type.equalsIgnoreCase("ch")) syntax.add(parseChoiceItemList(child, new Choice()));
			else if (type.equalsIgnoreCase("br")) {
				NamedNodeMap attr = child.getAttributes();
				Node indent = (attr == null) ? null : attr.getNamedItem("indent");
				int in;
				if (indent != null) {
					try {
						in = Integer.parseInt(indent.getTextContent().trim());
					} catch (NumberFormatException nfe) {
						in = 0;
					}
				} else {
					in = 0;
				}
				syntax.add(new Break(in));
			}
			else if (type.equalsIgnoreCase("me")) syntax.add(new TermName());
		}
		return syntax;
	}
	
	private <L extends List<ChoiceItem>> L parseChoiceItemList(Node node, L choiceItems) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String type = child.getNodeName();
			if (type.equalsIgnoreCase("ci")) choiceItems.add(parseSyntacticList(child, new ChoiceItem()));
		}
		return choiceItems;
	}
	
	private <L extends List<ListOrListItem>> L parseListItemList(Node node, L listItems) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String type = child.getNodeName();
			if (type.equalsIgnoreCase("li")) listItems.add(parseSpanList(child, new ListItem()));
			else if (type.equalsIgnoreCase("ul")) listItems.add(parseListItemList(child, new UnorderedList()));
			else if (type.equalsIgnoreCase("ol")) listItems.add(parseListItemList(child, new OrderedList()));
		}
		return listItems;
	}
	
	private <L extends List<TableRow>> L parseTableRowList(Node node, L rows) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String type = child.getNodeName();
			if (type.equalsIgnoreCase("tr")) rows.add(parseTableCellList(child, new TableRow()));
		}
		return rows;
	}
	
	private <L extends List<TableCell>> L parseTableCellList(Node node, L cells) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String type = child.getNodeName();
			NamedNodeMap attr = child.getAttributes();
			Node colspan = (attr == null) ? null : attr.getNamedItem("colspan");
			Node rowspan = (attr == null) ? null : attr.getNamedItem("rowspan");
			int cs, rs;
			if (colspan != null) {
				try {
					cs = Integer.parseInt(colspan.getTextContent().trim());
				} catch (NumberFormatException nfe) {
					cs = 1;
				}
			} else {
				cs = 1;
			}
			if (rowspan != null) {
				try {
					rs = Integer.parseInt(rowspan.getTextContent().trim());
				} catch (NumberFormatException nfe) {
					rs = 1;
				}
			} else {
				rs = 1;
			}
			if (type.equalsIgnoreCase("th")) cells.add(parseSpanList(child, new TableHeader(cs, rs)));
			else if (type.equalsIgnoreCase("td")) cells.add(parseSpanList(child, new TableData(cs, rs)));
		}
		return cells;
	}
	
	private <L extends List<Span>> L parseSpanList(Node node, L spans) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String type = child.getNodeName();
			if (type.equalsIgnoreCase("#text")) spans.add(new Literal(child.getTextContent().replaceAll("\\s+", " ")));
			else if (type.equalsIgnoreCase("img")) {
				NamedNodeMap attr = child.getAttributes();
				Node src = (attr == null) ? null : attr.getNamedItem("src");
				Node width = (attr == null) ? null : attr.getNamedItem("width");
				Node height = (attr == null) ? null : attr.getNamedItem("height");
				Node alt = (attr == null) ? null : attr.getNamedItem("alt");
				Node title = (attr == null) ? null : attr.getNamedItem("title");
				String srcs = ((src == null) ? "" : src.getTextContent().trim());
				if (srcs.length() == 0) srcs = child.getTextContent().trim();
				spans.add(new Image(
						srcs,
						((width == null) ? "" : width.getTextContent().trim()),
						((height == null) ? "" : height.getTextContent().trim()),
						((alt == null) ? "" : alt.getTextContent().trim()),
						((title == null) ? "" : title.getTextContent().trim())
				));
			}
			else if (type.equalsIgnoreCase("b")) spans.add(parseSpanList(child, new Bold()));
			else if (type.equalsIgnoreCase("strong")) spans.add(parseSpanList(child, new Bold()));
			else if (type.equalsIgnoreCase("i")) spans.add(parseSpanList(child, new Italic()));
			else if (type.equalsIgnoreCase("em")) spans.add(parseSpanList(child, new Italic()));
			else if (type.equalsIgnoreCase("u")) spans.add(parseSpanList(child, new Underline()));
			else if (type.equalsIgnoreCase("s")) spans.add(parseSpanList(child, new Strikethrough()));
			else if (type.equalsIgnoreCase("sup")) spans.add(parseSpanList(child, new Superscript()));
			else if (type.equalsIgnoreCase("sub")) spans.add(parseSpanList(child, new Subscript()));
			else if (type.equalsIgnoreCase("big")) spans.add(parseSpanList(child, new Big()));
			else if (type.equalsIgnoreCase("small")) spans.add(parseSpanList(child, new Small()));
			else if (type.equalsIgnoreCase("c")) spans.add(parseSpanList(child, new Code()));
			else if (type.equalsIgnoreCase("tt")) spans.add(parseSpanList(child, new Code()));
			else if (type.equalsIgnoreCase("code")) spans.add(parseSpanList(child, new Code()));
			else if (type.equalsIgnoreCase("kwd")) spans.add(new Keyword(child.getTextContent().replaceAll("\\s+", " ")));
			else if (type.equalsIgnoreCase("mv")) spans.add(new Metavariable(child.getTextContent().replaceAll("\\s+", " ")));
			else if (type.equalsIgnoreCase("span")) {
				NamedNodeMap attr = child.getAttributes();
				Node style = (attr == null) ? null : attr.getNamedItem("style");
				spans.add(parseSpanList(child, new Generic(
						((style == null) ? "" : style.getTextContent().trim())
				)));
			}
			else if (type.equalsIgnoreCase("a")) {
				NamedNodeMap attr = child.getAttributes();
				Node href = (attr == null) ? null : attr.getNamedItem("href");
				String hrefs = ((href == null) ? "" : href.getTextContent().trim());
				if (hrefs.length() == 0) hrefs = child.getTextContent().trim();
				spans.add(parseSpanList(child, new Anchor(hrefs)));
			}
			else if (type.equalsIgnoreCase("if")) {
				NamedNodeMap attr = child.getAttributes();
				Node dialect = (attr == null) ? null : attr.getNamedItem("dialect");
				Node module = (attr == null) ? null : attr.getNamedItem("module");
				Node library = (attr == null) ? null : attr.getNamedItem("library");
				StringBuffer cond = new StringBuffer();
				if (dialect != null) {
					if (cond.length() > 0) cond.append(',');
					cond.append(dialect.getTextContent().trim());
				}
				if (module != null) {
					if (cond.length() > 0) cond.append(',');
					cond.append(module.getTextContent().trim());
				}
				if (library != null) {
					if (cond.length() > 0) cond.append(',');
					cond.append(library.getTextContent().trim());
				}
				spans.add(parseSpanList(child, new If(cond.toString())));
			}
			else if (type.equalsIgnoreCase("else")) spans.add(parseSpanList(child, new Else()));
			else if (type.equalsIgnoreCase("br")) spans.add(new Break());
			else if (type.equalsIgnoreCase("dia")) spans.add(new DialectName());
			else if (type.equalsIgnoreCase("mod")) spans.add(new DialectName());
			else if (type.equalsIgnoreCase("lib")) spans.add(new DialectName());
			else if (type.equalsIgnoreCase("ver")) spans.add(new DialectVersion());
			else if (type.equalsIgnoreCase("me")) spans.add(new TermName());
		}
		return spans;
	}
}
