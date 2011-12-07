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

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.kreative.xiondoc.sdom.*;
import com.kreative.xiondoc.xdom.DialectSpecList;
import com.kreative.xiondoc.xdom.VersionNumber;

/**
 * The HTMLSDOMGenerator generates the final HTML for an XIONDoc SDOM Section.
 * @since XIONDoc 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class HTMLSDOMGenerator {
	private static final Pattern INTERNAL_HREF_PATTERN = Pattern.compile("([A-Za-z]{2}):(.*)", Pattern.DOTALL);
	
	private String currentTermName = null;
	private String currentDialectCode = null;
	private String currentDialectTitle = null;
	private VersionNumber currentDialectVersion = null;
	private String urlPrefix = null;
	
	public void setTerm(String name) {
		this.currentTermName = name;
	}
	
	public void unsetTerm() {
		this.currentTermName = null;
	}
	
	public void setDialect(String code, String title, VersionNumber version) {
		this.currentDialectCode = code;
		this.currentDialectTitle = title;
		this.currentDialectVersion = version;
	}
	
	public void unsetDialect() {
		this.currentDialectCode = null;
		this.currentDialectTitle = null;
		this.currentDialectVersion = null;
	}
	
	public void setURLPrefix(String prefix) {
		this.urlPrefix = prefix;
	}
	
	public void unsetURLPrefix() {
		this.urlPrefix = null;
	}
	
	public String generateSectionHTML(Section section) {
		StringBuffer s = new StringBuffer();
		boolean first = true;
		for (Block block : section) {
			if (first) first = false;
			else s.append('\n');
			generateBlockHTML(s, block);
		}
		return s.toString();
	}
	
	private void generateBlockHTML(StringBuffer out, Block block) {
		int indent = block.getIndent();
		if (block instanceof Script) {
			out.append("<pre class=\"block script indent" + indent + "\">");
			out.append('\n');
			out.append(((Script)block).getScript());
			out.append('\n');
			out.append("</pre>");
		}
		else if (block instanceof Syntax) {
			out.append("<p class=\"block syntax indent" + indent + "\">");
			generateSyntaxHTML(out, (Syntax)block);
			out.append("</p>");
		}
		else if (block instanceof UnorderedList) {
			int border = ((UnorderedList)block).getBorder();
			out.append("<ul class=\"block unorderedlist indent" + indent + " border" + border + "\">");
			out.append('\n');
			for (ListOrListItem item : (UnorderedList)block) {
				generateListItemHTML(out, item);
				out.append('\n');
			}
			out.append("</ul>");
		}
		else if (block instanceof OrderedList) {
			int border = ((OrderedList)block).getBorder();
			out.append("<ol class=\"block orderedlist indent" + indent + " border" + border + "\">");
			out.append('\n');
			for (ListOrListItem item : (OrderedList)block) {
				generateListItemHTML(out, item);
				out.append('\n');
			}
			out.append("</ol>");
		}
		else if (block instanceof Table) {
			int border = ((Table)block).getBorder();
			out.append("<table class=\"block table indent" + indent + " border" + border + "\">");
			out.append('\n');
			for (TableRow row : (Table)block) {
				generateTableRowHTML(out, row);
				out.append('\n');
			}
			out.append("</table>");
		}
		else if (block instanceof Paragraph) {
			out.append("<p class=\"block paragraph indent" + indent + "\">");
			for (Span span : (Paragraph)block) {
				generateSpanHTML(out, span);
			}
			out.append("</p>");
		}
		else if (block instanceof H1) {
			out.append("<h1 class=\"block h1 indent" + indent + "\">");
			for (Span span : (H1)block) {
				generateSpanHTML(out, span);
			}
			out.append("</h1>");
		}
		else if (block instanceof H2) {
			out.append("<h2 class=\"block h2 indent" + indent + "\">");
			for (Span span : (H2)block) {
				generateSpanHTML(out, span);
			}
			out.append("</h2>");
		}
		else if (block instanceof H3) {
			out.append("<h3 class=\"block h3 indent" + indent + "\">");
			for (Span span : (H3)block) {
				generateSpanHTML(out, span);
			}
			out.append("</h3>");
		}
		else if (block instanceof H4) {
			out.append("<h4 class=\"block h4 indent" + indent + "\">");
			for (Span span : (H4)block) {
				generateSpanHTML(out, span);
			}
			out.append("</h4>");
		}
		else if (block instanceof H5) {
			out.append("<h5 class=\"block h5 indent" + indent + "\">");
			for (Span span : (H5)block) {
				generateSpanHTML(out, span);
			}
			out.append("</h5>");
		}
		else if (block instanceof H6) {
			out.append("<h6 class=\"block h6 indent" + indent + "\">");
			for (Span span : (H6)block) {
				generateSpanHTML(out, span);
			}
			out.append("</h6>");
		}
		else if (block instanceof HorizontalRule) {
			out.append("<hr class=\"block horizontalrule indent" + indent + "\" />");
		}
		else if (block instanceof Image) {
			out.append("<p class=\"block image indent" + indent + "\">");
			generateSpanHTML(out, (Image)block);
			out.append("</p>");
		}
	}
	
	private void generateSyntaxHTML(StringBuffer out, List<Syntactic> block) {
		boolean first = true;
		for (Syntactic syn : block) {
			if (first) first = false;
			else out.append(' ');
			generateSyntacticHTML(out, syn);
		}
	}
	
	private void generateSyntacticHTML(StringBuffer out, Syntactic syn) {
		if (syn instanceof Keyword) {
			out.append("<code class=\"keyword\">");
			out.append(htmlencode(((Keyword)syn).toString(),true));
			out.append("</code>");
		}
		else if (syn instanceof Metavariable) {
			out.append("<em class=\"metavariable\">");
			out.append(htmlencode(((Metavariable)syn).toString(),true));
			out.append("</em>");
		}
		else if (syn instanceof Optional) {
			if ((((Optional)syn).size() == 1) && (((Optional)syn).get(0) instanceof Choice)) {
				out.append("<span class=\"optional choice\">");
				out.append("<span class=\"metasymbol\">[</span>");
				boolean first = true;
				for (ChoiceItem ci : (Choice)((Optional)syn).get(0)) {
					if (first) first = false;
					else out.append("<span class=\"metasymbol\">|</span>");
					generateSyntaxHTML(out, ci);
				}
				out.append("<span class=\"metasymbol\">]</span>");
				out.append("</span>");
			} else {
				out.append("<span class=\"optional\">");
				out.append("<span class=\"metasymbol\">[</span>");
				generateSyntaxHTML(out, (Optional)syn);
				out.append("<span class=\"metasymbol\">]</span>");
				out.append("</span>");
			}
		}
		else if (syn instanceof Choice) {
			out.append("<span class=\"choice\">");
			out.append("<span class=\"metasymbol\">(</span>");
			boolean first = true;
			for (ChoiceItem ci : (Choice)syn) {
				if (first) first = false;
				else out.append("<span class=\"metasymbol\">|</span>");
				generateSyntaxHTML(out, ci);
			}
			out.append("<span class=\"metasymbol\">)</span>");
			out.append("</span>");
		}
		else if (syn instanceof Break) {
			out.append("<br/>");
			int indent = ((Break)syn).getIndent();
			if (indent > 0) {
				out.append(' ');
				out.append("<span class=\"syntaxindent\">");
				while (indent-->0) {
					out.append("&nbsp;&nbsp;");
				}
				out.append("</span>");
			}
		}
		else if (syn instanceof TermName) {
			if (this.currentTermName != null) {
				String[] words = this.currentTermName.trim().split("\\s+");
				boolean first = true;
				for (String word : words) {
					if (word.length() > 0) {
						if (first) first = false;
						else out.append(' ');
						out.append("<code class=\"keyword\">");
						out.append(htmlencode(word, true));
						out.append("</code>");
					}
				}
			}
		}
	}
	
	private void generateListItemHTML(StringBuffer out, ListOrListItem item) {
		if (item instanceof ListItem) {
			out.append("<li>");
			for (Span span : (ListItem)item) {
				generateSpanHTML(out, span);
			}
			out.append("</li>");
		}
		else if (item instanceof UnorderedList) {
			out.append("<li>");
			out.append('\n');
			out.append("<ul>");
			out.append('\n');
			for (ListOrListItem subitem : (UnorderedList)item) {
				generateListItemHTML(out, subitem);
				out.append('\n');
			}
			out.append("</ul>");
			out.append('\n');
			out.append("</li>");
		}
		else if (item instanceof OrderedList) {
			out.append("<li>");
			out.append('\n');
			out.append("<ol>");
			out.append('\n');
			for (ListOrListItem subitem : (OrderedList)item) {
				generateListItemHTML(out, subitem);
				out.append('\n');
			}
			out.append("</ol>");
			out.append('\n');
			out.append("</li>");
		}
	}
	
	private void generateTableRowHTML(StringBuffer out, TableRow row) {
		out.append("<tr>");
		out.append('\n');
		for (TableCell cell : row) {
			generateTableCellHTML(out, cell);
			out.append('\n');
		}
		out.append("</tr>");
	}
	
	private void generateTableCellHTML(StringBuffer out, TableCell cell) {
		int cs = cell.getColSpan();
		int rs = cell.getRowSpan();
		if (cell instanceof TableHeader) {
			out.append("<th");
			if (cs != 1) out.append(" colspan=\"" + cs + "\"");
			if (rs != 1) out.append(" rowspan=\"" + rs + "\"");
			out.append(">");
			for (Span span : (TableHeader)cell) {
				generateSpanHTML(out, span);
			}
			out.append("</th>");
		}
		else if (cell instanceof TableData) {
			out.append("<td");
			if (cs != 1) out.append(" colspan=\"" + cs + "\"");
			if (rs != 1) out.append(" rowspan=\"" + rs + "\"");
			out.append(">");
			for (Span span : (TableData)cell) {
				generateSpanHTML(out, span);
			}
			out.append("</td>");
		}
	}
	
	private void generateSpanHTML(StringBuffer out, Span span) {
		if (span instanceof Literal) {
			out.append(htmlencode(((Literal)span).toString(), false));
		}
		else if (span instanceof Image) {
			String src = ((Image)span).getSrc();
			if (!src.contains(":")) {
				src = "images/" + src;
				if (urlPrefix != null) {
					src = urlPrefix + src;
				}
			}
			out.append("<img src=\"" + htmlencode(src, true) + "\"");
			if (((Image)span).hasWidth()) out.append(" width=\"" + htmlencode(((Image)span).getWidth(), true) + "\"");
			if (((Image)span).hasHeight()) out.append(" height=\"" + htmlencode(((Image)span).getHeight(), true) + "\"");
			if (((Image)span).hasAlt()) out.append(" alt=\"" + htmlencode(((Image)span).getAlt(), true) + "\"");
			if (((Image)span).hasTitle()) out.append(" title=\"" + htmlencode(((Image)span).getTitle(), true) + "\"");
			out.append(" />");
		}
		else if (span instanceof Bold) {
			out.append("<strong>");
			for (Span subspan : (Bold)span) {
				generateSpanHTML(out, subspan);
			}
			out.append("</strong>");
		}
		else if (span instanceof Italic) {
			out.append("<em>");
			for (Span subspan : (Italic)span) {
				generateSpanHTML(out, subspan);
			}
			out.append("</em>");
		}
		else if (span instanceof Underline) {
			out.append("<u>");
			for (Span subspan : (Underline)span) {
				generateSpanHTML(out, subspan);
			}
			out.append("</u>");
		}
		else if (span instanceof Strikethrough) {
			out.append("<s>");
			for (Span subspan : (Strikethrough)span) {
				generateSpanHTML(out, subspan);
			}
			out.append("</s>");
		}
		else if (span instanceof Superscript) {
			out.append("<sup>");
			for (Span subspan : (Superscript)span) {
				generateSpanHTML(out, subspan);
			}
			out.append("</sup>");
		}
		else if (span instanceof Subscript) {
			out.append("<sub>");
			for (Span subspan : (Subscript)span) {
				generateSpanHTML(out, subspan);
			}
			out.append("</sub>");
		}
		else if (span instanceof Big) {
			out.append("<big>");
			for (Span subspan : (Big)span) {
				generateSpanHTML(out, subspan);
			}
			out.append("</big>");
		}
		else if (span instanceof Small) {
			out.append("<small>");
			for (Span subspan : (Small)span) {
				generateSpanHTML(out, subspan);
			}
			out.append("</small>");
		}
		else if (span instanceof Code) {
			out.append("<code>");
			for (Span subspan : (Code)span) {
				generateSpanHTML(out, subspan);
			}
			out.append("</code>");
		}
		else if (span instanceof Keyword) {
			out.append("<code class=\"keyword\">");
			out.append(htmlencode(((Keyword)span).toString(),true));
			out.append("</code>");
		}
		else if (span instanceof Metavariable) {
			out.append("<em class=\"metavariable\">");
			out.append(htmlencode(((Metavariable)span).toString(),true));
			out.append("</em>");
		}
		else if (span instanceof Generic) {
			out.append("<span class=\"generic\" style=\"");
			out.append(htmlencode(((Generic)span).getStyle(), true));
			out.append("\">");
			for (Span subspan : (Generic)span) {
				generateSpanHTML(out, subspan);
			}
			out.append("</span>");
		}
		else if (span instanceof Anchor) {
			String href = ((Anchor)span).getHref();
			Matcher m = INTERNAL_HREF_PATTERN.matcher(href);
			if (m.matches()) {
				boolean textOnly = ((Anchor)span).isEmpty() || (
						(((Anchor)span).size() == 1)
						&& (((Anchor)span).get(0) instanceof Literal)
				);
				boolean textSame = textOnly && (
						((Anchor)span).isEmpty()
						|| (((Anchor)span).get(0).toString().equalsIgnoreCase(href))
						|| (((Anchor)span).get(0).toString().equalsIgnoreCase(m.group(2)))
				);
				if (textOnly) {
					out.append("<code>");
				}
				out.append("<a href=\"");
				if (urlPrefix != null) {
					out.append(htmlencode(urlPrefix, false));
				}
				out.append(
						htmlencode(
								fnencode(m.group(1).toLowerCase()) +
								"/" +
								fnencode(m.group(2).toLowerCase()) +
								".html",
								true
						)
				);
				out.append("\">");
				if (textSame) {
					out.append(htmlencode(m.group(2), false));
				} else {
					for (Span subspan : (Anchor)span) {
						generateSpanHTML(out, subspan);
					}
				}
				out.append("</a>");
				if (textOnly) {
					out.append("</code>");
				}
			} else {
				out.append("<a href=\"");
				out.append(htmlencode(href, true));
				out.append("\">");
				for (Span subspan : (Anchor)span) {
					generateSpanHTML(out, subspan);
				}
				out.append("</a>");
			}
		}
		else if (span instanceof If) {
			DialectSpecList dsl = new DialectSpecList(((If)span).getCondition());
			if (
					(this.currentDialectCode != null)
					&& (this.currentDialectVersion != null)
					&& dsl.matches(this.currentDialectCode, this.currentDialectVersion)
			) {
				for (Span subspan : (If)span) {
					if (!(subspan instanceof Else)) {
						generateSpanHTML(out, subspan);
					}
				}
			} else {
				for (Span subspan : (If)span) {
					if (subspan instanceof Else) {
						generateSpanHTML(out, subspan);
					}
				}
			}
		}
		else if (span instanceof Else) {
			for (Span subspan : (Else)span) {
				generateSpanHTML(out, subspan);
			}
		}
		else if (span instanceof Break) {
			out.append("<br/>");
		}
		else if (span instanceof DialectName) {
			if (this.currentDialectTitle != null) {
				out.append(htmlencode(this.currentDialectTitle, true));
			}
		}
		else if (span instanceof DialectVersion) {
			if (this.currentDialectVersion != null) {
				out.append(htmlencode(this.currentDialectVersion.toString(), true));
			}
		}
		else if (span instanceof TermName) {
			if (this.currentTermName != null) {
				out.append("<code>");
				out.append(htmlencode(this.currentTermName, true));
				out.append("</code>");
			}
		}
	}
	
	private static String htmlencode(String in, boolean trim) {
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
		if (trim) {
			return out.toString().trim().replaceAll("\\s+", " ");
		} else {
			return out.toString().replaceAll("\\s+", " ");
		}
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
