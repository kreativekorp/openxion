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

public class Dialect {
	private String code;
	private String title;
	private String version;
	private TreeSet<NameTermPair> allvocab;
	private Map<Term.Type,TreeSet<NameTermPair>> vocab;
	private List<Article> articles;
	
	public Dialect(String code, String title, String version) {
		this.code = code;
		this.title = title;
		this.version = version;
		allvocab = new TreeSet<NameTermPair>();
		vocab = new HashMap<Term.Type,TreeSet<NameTermPair>>();
		for (Term.Type t : Term.Type.values()) {
			vocab.put(t, new TreeSet<NameTermPair>());
		}
		articles = new Vector<Article>();
	}
	
	public Dialect(String code, String title) {
		this(code, title, null);
	}
	
	public Dialect(String code) {
		this(code, null, null);
	}
	
	public Dialect() {
		this(null, null, null);
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String toString() {
		if (version == null) {
			if (title == null) {
				if (code == null) {
					return "";
				} else {
					return code;
				}
			} else {
				return title;
			}
		} else {
			if (title == null) {
				if (code == null) {
					return version;
				} else {
					return code+" "+version;
				}
			} else {
				return title+" "+version;
			}
		}
	}
	
	public void addArticle(String code, String title, String content) {
		articles.add(new Article(code, title, content));
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
		for (String name : v.getNames(this)) {
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
