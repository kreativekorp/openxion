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

import java.text.*;
import java.util.*;

public class Term {
	private Type type;
	private Map<CIString,Set<Dialect>> names;
	private Precedence precedence;
	private String shortDescription;
	private String constantType;
	private String constantValue;
	private List<String> appliesTo;
	private List<String> syntax;
	private List<String> syntaxNotes;
	private List<String> examples;
	private List<String> description;
	private EnumSet<Descriptor> descriptors;
	private List<String> properties;
	private List<String> scripts;
	private List<String> notes;
	private List<String> seeAlso;
	
	public Term(Type type) {
		this.type = type;
		this.names = new HashMap<CIString,Set<Dialect>>();
		this.precedence = null;
		this.shortDescription = null;
		this.constantType = null;
		this.constantValue = null;
		this.appliesTo = new Vector<String>();
		this.syntax = new Vector<String>();
		this.syntaxNotes = new Vector<String>();
		this.examples = new Vector<String>();
		this.description = new Vector<String>();
		this.descriptors = EnumSet.noneOf(Descriptor.class);
		this.properties = new Vector<String>();
		this.scripts = new Vector<String>();
		this.notes = new Vector<String>();
		this.seeAlso = new Vector<String>();
	}

	public Type getType() {
		return type;
	}

	public void setType(Type vocabType) {
		this.type = vocabType;
	}
	
	public void addName(String name, Dialect dialect) {
		CIString n = new CIString(name);
		if (names.containsKey(n)) {
			Set<Dialect> d = names.get(n);
			d.add(dialect);
		} else {
			Set<Dialect> d = new HashSet<Dialect>();
			d.add(dialect);
			names.put(n, d);
		}
	}
	
	public void addName(String name, Collection<Dialect> dialects) {
		CIString n = new CIString(name);
		if (names.containsKey(n)) {
			Set<Dialect> d = names.get(n);
			d.addAll(dialects);
		} else {
			Set<Dialect> d = new HashSet<Dialect>();
			d.addAll(dialects);
			names.put(n, d);
		}
	}
	
	public void removeName(String name, Dialect dialect) {
		CIString n = new CIString(name);
		if (names.containsKey(n)) {
			Set<Dialect> d = names.get(n);
			d.remove(dialect);
			if (d.isEmpty()) names.remove(n);
		}
	}
	
	public void removeName(String name, Collection<Dialect> dialects) {
		CIString n = new CIString(name);
		if (names.containsKey(n)) {
			Set<Dialect> d = names.get(n);
			d.removeAll(dialects);
			if (d.isEmpty()) names.remove(n);
		}
	}
	
	public void removeName(String name) {
		CIString n = new CIString(name);
		if (names.containsKey(n)) {
			names.remove(n);
		}
	}
	
	public Collection<String> getNames() {
		Set<String> n = new HashSet<String>();
		for (CIString name : names.keySet()) {
			n.add(name.toString());
		}
		return n;
	}
	
	public Collection<String> getNames(Dialect dialect) {
		Set<String> n = new HashSet<String>();
		for (Map.Entry<CIString, Set<Dialect>> e : names.entrySet()) {
			if (e.getValue().contains(dialect)) {
				n.add(e.getKey().toString());
			}
		}
		return n;
	}
	
	public void removeDialect(Dialect dialect) {
		Set<CIString> toRemove = new HashSet<CIString>();
		for (Map.Entry<CIString, Set<Dialect>> e : names.entrySet()) {
			if (e.getValue().contains(dialect)) {
				e.getValue().remove(dialect);
				if (e.getValue().isEmpty()) toRemove.add(e.getKey());
			}
		}
		for (CIString s : toRemove) {
			names.remove(s);
		}
	}
	
	public Collection<Dialect> getDialects() {
		Set<Dialect> d = new HashSet<Dialect>();
		for (Set<Dialect> dialects : names.values()) {
			d.addAll(dialects);
		}
		return d;
	}
	
	public Collection<Dialect> getDialects(String name) {
		CIString n = new CIString(name);
		if (names.containsKey(n)) return names.get(n);
		else return new HashSet<Dialect>();
	}

	public Precedence getPrecedence() {
		return precedence;
	}

	public void setPrecedence(Precedence precedence) {
		this.precedence = precedence;
	}
	
	public String getShortDescription() {
		return shortDescription;
	}
	
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	
	public String getConstantType() {
		return constantType;
	}
	
	public void setConstantType(String constantType) {
		this.constantType = constantType;
	}
	
	public String getConstantValue() {
		return constantValue;
	}
	
	public void setConstantValue(String constantValue) {
		this.constantValue = constantValue;
	}

	public List<String> getAppliesTo() {
		return appliesTo;
	}

	public List<String> getSyntax() {
		return syntax;
	}

	public List<String> getSyntaxNotes() {
		return syntaxNotes;
	}

	public List<String> getExamples() {
		return examples;
	}

	public List<String> getDescription() {
		return description;
	}
	
	public EnumSet<Descriptor> getDescriptors() {
		return descriptors;
	}
	
	public List<String> getProperties() {
		return properties;
	}
	
	public List<String> getProperties(Dialect d) {
		List<String> l = new Vector<String>();
		for (String p : properties) {
			if (d.hasTerm(Term.Type.PROPERTY, p)) {
				l.add(p);
			}
		}
		return l;
	}

	public List<String> getScripts() {
		return scripts;
	}

	public List<String> getNotes() {
		return notes;
	}
	
	public List<String> getSeeAlso() {
		return seeAlso;
	}
	
	public List<String> getSeeAlso(Dialect d) {
		List<String> l = new Vector<String>();
		for (String s : seeAlso) {
			if (s.startsWith("[[") && s.endsWith("]]") && s.contains(":")) {
				String[] ss = s.substring(2, s.length()-2).split(":", 2);
				Type t = Type.forCode(ss[0]);
				if (t != null && d.hasTerm(t, ss[1])) {
					l.add(s);
				}
			} else {
				l.add(s);
			}
		}
		return l;
	}
	
	public static enum Type {
		CONTROL_STRUCTURE("cs","structure","control structure","control structures"),
		KEYWORD("kw","keyword","other keyword","other keywords"),
		EVENT("ev","event","event","events"),
		COMMAND("cm","command","command","commands"),
		FUNCTION("fn","function","function","functions"),
		DATA_TYPE("dt","datatype","data type","data types"),
		PROPERTY("pr","property","property","properties"),
		OPERATOR("op","operator","operator","operators"),
		CONSTANT("cn","constant","constant","constants"),
		ORDINAL("or","ordinal","ordinal","ordinals"),
		IO_METHOD("mt","iomethod","I/O method","I/O methods"),
		IO_MANAGER("mg","iomanager","I/O manager","I/O managers"),
		EXTERNAL_LANGUAGE("xl","extlang","external language","external languages"),
		VERSION("vr","version","version","versions");
		
		private String twoletter, xml, singular, plural;
		
		private Type(String twoletter, String xml, String singular, String plural) {
			this.twoletter = twoletter;
			this.xml = xml;
			this.singular = singular;
			this.plural = plural;
		}
		
		private static String tcase(String oldstr) {
			StringBuffer newstr = new StringBuffer(oldstr.length());
			CharacterIterator ci = new StringCharacterIterator(oldstr);
			for (char pch = ' ', ch = ci.first(); ch != CharacterIterator.DONE; pch = ch, ch = ci.next()) {
				if (!Character.isLetter(pch)) newstr.append(Character.toTitleCase(ch));
				else newstr.append(Character.toLowerCase(ch));
			}
			return newstr.toString();
		}
		
		public String getCode() { return twoletter; }
		public String getTagName() { return xml; }
		public String getSingular() { return singular; }
		public String getSingularTitleCase() { return tcase(singular); }
		public String getPlural() { return plural; }
		public String getPluralTitleCase() { return tcase(plural); }
		
		public static Type forCode(String code) {
			for (Type t : values()) {
				if (t.twoletter.equalsIgnoreCase(code)) return t;
			}
			return null;
		}
		
		public static Type forTagName(String tag) {
			for (Type t : values()) {
				if (t.xml.equalsIgnoreCase(tag)) return t;
			}
			return null;
		}
		
		public static Type forSingular(String s) {
			for (Type t : values()) {
				if (t.singular.equalsIgnoreCase(s)) return t;
			}
			return null;
		}
		
		public static Type forPlural(String p) {
			for (Type t : values()) {
				if (t.plural.equalsIgnoreCase(p)) return t;
			}
			return null;
		}
	}
	
	public static enum Precedence {
		NULL(0,""),
		UNARY(1,"Unary & Conditional"),
		EXPONENT(2,"Exponentiation"),
		MULTIPLY(3,"Multiplication"),
		ADD(4,"Addition"),
		SHIFT(5,"Bit Shift"),
		BIT_AND(6,"Bitwise AND"),
		BIT_XOR(7,"Bitwise XOR"),
		BIT_OR(8,"Bitwise OR"),
		STR_CONCAT(9,"String Concatenation"),
		LIST_CONCAT(10,"List Concatenation"),
		RELATION(11,"Relational"),
		IS_A(12,"Polymorphic"),
		EQUAL(13,"Equivalence"),
		AND(14,"Boolean AND"),
		XOR(15,"Boolean XOR"),
		OR(16,"Boolean OR"),
		LIST(17,"List Construction");
		
		private int number;
		private String name;
		
		private Precedence(int number, String name) {
			this.number = number;
			this.name = name;
		}
		
		public int getNumber() {
			return number;
		}
		
		public String getName() {
			return name;
		}
		
		public static Precedence forNumber(int number) {
			for (Precedence p : values()) {
				if (p.number == number) return p;
			}
			return null;
		}
		
		public static Precedence forName(String name) {
			for (Precedence p : values()) {
				if (p.name.equalsIgnoreCase(name)) return p;
			}
			return null;
		}
		
		public static Precedence forString(String s) {
			try {
				int i = Integer.parseInt(s);
				for (Precedence p : values()) {
					if (p.number == i) return p;
				}
			} catch (NumberFormatException e) {}
			for (Precedence p : values()) {
				if (p.name.equalsIgnoreCase(s)) return p;
			}
			for (Precedence p : values()) {
				if (p.name.toLowerCase().contains(s.toLowerCase())) return p;
			}
			return null;
		}
	}
	
	public static enum Descriptor {
		SINGLETON("singleton", "the $", "singleton"),
		CHILD_SINGLETON("child-singleton", "the $ of steve", "singleton"),
		INDEX("index", "$ 12", "index"),
		CHILD_INDEX("child-index", "$ 12 of steve", "index"),
		INDEX_RANGE("index-range", "$ 2 through 7", "index range"),
		CHILD_INDEX_RANGE("child-index-range", "$ 2 through 7 of steve", "index range"),
		ORDINAL("ordinal", "the fifth $", "ordinal"),
		CHILD_ORDINAL("child-ordinal", "the fifth $ of steve", "ordinal"),
		ORDINAL_RANGE("ordinal-range", "the third through eighth $", "ordinal range"),
		CHILD_ORDINAL_RANGE("child-ordinal-range", "the third through eighth $ of steve", "ordinal range"),
		ID("id", "$ id 1719", "ID"),
		CHILD_ID("child-id", "$ id 1719 of steve", "ID"),
		NAME("name", "$ \"steve\"", "name"),
		CHILD_NAME("child-name", "$ \"andy\" of steve", "name"),
		MASS("mass", "the $", "mass"),
		CHILD_MASS("child-mass", "the $ of steve", "mass");
		
		private String code, example, name;
		
		private Descriptor(String code, String example, String name) {
			this.code = code;
			this.example = example;
			this.name = name;
		}
		
		public String getCode() {
			return code;
		}
		
		public String getExample(String term) {
			return example.replace("$", term);
		}
		
		public String getName() {
			return name+" descriptor";
		}
		
		public static Descriptor forCode(String code) {
			for (Descriptor dt : values()) {
				if (dt.code.equalsIgnoreCase(code)) return dt;
			}
			return null;
		}
	}
	
	private static class CIString implements Comparable<CIString> {
		private String s;
		
		public CIString(String s) {
			this.s = ((s == null) ? "" : s);
		}
		
		public String toString() {
			return s;
		}
		
		public boolean equals(Object o) {
			return s.equalsIgnoreCase((o == null) ? "" : o.toString());
		}
		
		public int compareTo(CIString other) {
			return this.s.compareToIgnoreCase((other == null) ? "" : other.s);
		}
		
		public int hashCode() {
			return s.toLowerCase().hashCode();
		}
	}
}
