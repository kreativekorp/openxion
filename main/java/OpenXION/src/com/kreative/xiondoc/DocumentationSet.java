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

import java.util.*;

public class DocumentationSet {
	private TreeSet<NameTermPair> allvocab;
	private Map<Term.Type,TreeSet<NameTermPair>> vocab;
	private List<Dialect> dialects;
	private String summary;
	private List<String> description;
	private List<Article> articles;
	
	public DocumentationSet() {
		allvocab = new TreeSet<NameTermPair>();
		vocab = new HashMap<Term.Type,TreeSet<NameTermPair>>();
		for (Term.Type t : Term.Type.values()) {
			vocab.put(t, new TreeSet<NameTermPair>());
		}
		dialects = new Vector<Dialect>();
		summary = null;
		description = new Vector<String>();
		articles = new Vector<Article>();
	}
	
	public void addDialect(Dialect d) {
		dialects.add(d);
	}
	
	public void removeDialect(Dialect d) {
		dialects.remove(d);
	}
	
	public Dialect getDialect(String code) {
		for (Dialect dl : dialects) {
			if (dl.getCode().equalsIgnoreCase(code)) return dl;
		}
		return null;
	}
	
	public Iterator<Dialect> dialectIterator() {
		return dialects.iterator();
	}
	
	public String getSummary() {
		return summary;
	}
	
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public List<String> getDescription() {
		return this.description;
	}
	
	public void addDescription(String description) {
		this.description.add(description);
	}
	
	public void removeDescription(String description) {
		this.description.remove(description);
	}
	
	public void addArticle(String code, String title, String summary, String content) {
		articles.add(new Article(code, title, summary, content));
	}
	
	public void removeArticle(String code) {
		for (Article a : articles) {
			if (a.getCode().equalsIgnoreCase(code)) {
				articles.remove(a);
				return;
			}
		}
	}
	
	public Article getArticle(String code) {
		for (Article a : articles) {
			if (a.getCode().equalsIgnoreCase(code)) {
				return a;
			}
		}
		return null;
	}
	
	public Iterator<Article> articleIterator() {
		return articles.iterator();
	}
	
	public void addTerm(Term v) {
		for (String name : v.getNames()) {
			NameTermPair n = new NameTermPair(name,v);
			allvocab.add(n);
			vocab.get(v.getType()).add(n);
		}
	}
	
	public void removeTerm(Term v) {
		Set<NameTermPair> toRemove = new HashSet<NameTermPair>();
		for (NameTermPair n : allvocab) {
			if (n.getTerm() == v) toRemove.add(n);
		}
		allvocab.removeAll(toRemove);
		vocab.get(v.getType()).removeAll(toRemove);
	}
	
	public boolean hasTerm(String name) {
		for (NameTermPair n : allvocab) {
			if (n.getName().equalsIgnoreCase(name)) return true;
		}
		return false;
	}
	
	public boolean hasTerm(Term.Type t, String name) {
		for (NameTermPair n : vocab.get(t)) {
			if (n.getName().equalsIgnoreCase(name)) return true;
		}
		return false;
	}
	
	public Iterator<NameTermPair> termIterator() {
		return allvocab.iterator();
	}
	
	public Iterator<NameTermPair> termIterator(Term.Type type) {
		return vocab.get(type).iterator();
	}
}
