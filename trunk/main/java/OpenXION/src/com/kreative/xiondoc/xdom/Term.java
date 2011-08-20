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
 * @since XIONDoc 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.xiondoc.xdom;

import java.io.Serializable;
import java.util.EnumSet;
import com.kreative.xiondoc.sdom.Section;

/**
 * A term and the various sections of its documentation.
 * This class provides properties for all possible sections of a term's documentation;
 * not all sections will apply to all term types.
 * @since XIONDoc 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class Term implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private TermType type;
	private TermNameList names;
	private String appliesTo;
	private Precedence precedence;
	private String descriptionShort;
	private TermSpec dataType;
	private String dataValue;
	private Section syntax;
	private Section examples;
	private Section description;
	private EnumSet<Descriptor> descriptors;
	private TermSpecList properties;
	private Section scripts;
	private Section notes;
	private Section security;
	private Section compatibility;
	private TermSpecList seeAlso;
	
	public Term(TermType type) {
		this.type = type;
		this.names = new TermNameList();
		this.appliesTo = null;
		this.precedence = null;
		this.descriptionShort = null;
		this.dataType = null;
		this.dataValue = null;
		this.syntax = null;
		this.examples = null;
		this.description = null;
		this.descriptors = EnumSet.noneOf(Descriptor.class);
		this.properties = new TermSpecList();
		this.scripts = null;
		this.notes = null;
		this.security = null;
		this.compatibility = null;
		this.seeAlso = new TermSpecList();
	}
	
	public TermType type() {
		return type;
	}
	
	public TermNameList names() {
		return names;
	}
	
	public boolean hasAppliesTo() {
		return (appliesTo != null && appliesTo.length() > 0);
	}
	
	public String getAppliesTo() {
		return appliesTo;
	}
	
	public void setAppliesTo(String appliesTo) {
		this.appliesTo = appliesTo;
	}
	
	public void appendAppliesTo(String appliesTo) {
		if (this.appliesTo == null) this.appliesTo = appliesTo;
		else this.appliesTo += " " + appliesTo;
	}
	
	public boolean hasPrecedence() {
		return (precedence != null && precedence != Precedence.NULL);
	}
	
	public Precedence getPrecedence() {
		return precedence;
	}
	
	public void setPrecedence(Precedence precedence) {
		this.precedence = precedence;
	}
	
	public boolean hasDescriptionShort() {
		return (descriptionShort != null && descriptionShort.length() > 0);
	}
	
	public String getDescriptionShort() {
		return descriptionShort;
	}
	
	public void setDescriptionShort(String descriptionShort) {
		this.descriptionShort = descriptionShort;
	}
	
	public void appendDescriptionShort(String descriptionShort) {
		if (this.descriptionShort == null) this.descriptionShort = descriptionShort;
		else this.descriptionShort += " " + descriptionShort;
	}
	
	public boolean hasDataType() {
		return (dataType != null);
	}
	
	public TermSpec getDataType() {
		return dataType;
	}
	
	public void setDataType(TermSpec dataType) {
		this.dataType = dataType;
	}
	
	public boolean hasDataValue() {
		return (dataValue != null && dataValue.length() > 0);
	}
	
	public String getDataValue() {
		return dataValue;
	}
	
	public void setDataValue(String dataValue) {
		this.dataValue = dataValue;
	}
	
	public boolean hasSyntax() {
		return (syntax != null && !syntax.isEmpty());
	}
	
	public Section getSyntax() {
		return syntax;
	}
	
	public void setSyntax(Section syntax) {
		this.syntax = syntax;
	}
	
	public void appendSyntax(Section syntax) {
		if (this.syntax == null) this.syntax = syntax;
		else this.syntax.addAll(syntax);
	}
	
	public boolean hasExamples() {
		return (examples != null && !examples.isEmpty());
	}
	
	public Section getExamples() {
		return examples;
	}
	
	public void setExamples(Section examples) {
		this.examples = examples;
	}
	
	public void appendExamples(Section examples) {
		if (this.examples == null) this.examples = examples;
		else this.examples.addAll(examples);
	}
	
	public boolean hasDescription() {
		return (description != null && !description.isEmpty());
	}
	
	public Section getDescription() {
		return description;
	}
	
	public void setDescription(Section description) {
		this.description = description;
	}
	
	public void appendDescription(Section description) {
		if (this.description == null) this.description = description;
		else this.description.addAll(description);
	}
	
	public EnumSet<Descriptor> descriptors() {
		return descriptors;
	}
	
	public TermSpecList properties() {
		return properties;
	}
	
	public boolean hasScripts() {
		return (scripts != null && !scripts.isEmpty());
	}
	
	public Section getScripts() {
		return scripts;
	}
	
	public void setScripts(Section scripts) {
		this.scripts = scripts;
	}
	
	public void appendScripts(Section scripts) {
		if (this.scripts == null) this.scripts = scripts;
		else this.scripts.addAll(scripts);
	}
	
	public boolean hasNotes() {
		return (notes != null && !notes.isEmpty());
	}
	
	public Section getNotes() {
		return notes;
	}
	
	public void setNotes(Section notes) {
		this.notes = notes;
	}
	
	public void appendNotes(Section notes) {
		if (this.notes == null) this.notes = notes;
		else this.notes.addAll(notes);
	}
	
	public boolean hasSecurity() {
		return (security != null && !security.isEmpty());
	}
	
	public Section getSecurity() {
		return security;
	}
	
	public void setSecurity(Section security) {
		this.security = security;
	}
	
	public void appendSecurity(Section security) {
		if (this.security == null) this.security = security;
		else this.security.addAll(security);
	}
	
	public boolean hasCompatibility() {
		return (compatibility != null && !compatibility.isEmpty());
	}
	
	public Section getCompatibility() {
		return compatibility;
	}
	
	public void setCompatibility(Section compatibility) {
		this.compatibility = compatibility;
	}
	
	public void appendCompatibility(Section compatibility) {
		if (this.compatibility == null) this.compatibility = compatibility;
		else this.compatibility.addAll(compatibility);
	}
	
	public boolean hasSynonyms(String dialectName, VersionNumber dialectVersion) {
		if (names == null || names.isEmpty()) return false;
		
		int count = 0;
		for (TermName name : names) {
			if (dialectName == null || name.getDialects().matches(dialectName, dialectVersion)) {
				count++;
			}
		}
		return (count > 1);
	}
	
	public TermSpecList getSynonyms(String dialectName, VersionNumber dialectVersion, String termName) {
		if (names == null || names.isEmpty()) return new TermSpecList();
		
		TermSpecList synonyms = new TermSpecList();
		for (TermName name : names) {
			if (
					(dialectName == null || name.getDialects().matches(dialectName, dialectVersion)) &&
					(termName == null || !name.getName().equalsIgnoreCase(termName)))
			{
				synonyms.add(new TermSpec(type, name.getName()));
			}
		}
		return synonyms;
	}
	
	public TermSpecList seeAlso() {
		return seeAlso;
	}
}
