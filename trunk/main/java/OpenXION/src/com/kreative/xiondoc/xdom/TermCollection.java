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

package com.kreative.xiondoc.xdom;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A collection of terms.
 * Terms are indexed by their types, their names, and the dialects that support them.
 * This allows terms to be looked up by any combination of type, name, dialect, and version.
 * @since XIONDoc 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class TermCollection implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Set<Term> allTerms;
	private Map<TermType,Set<Term>> termsByType;
	private Map<String,Set<Term>> termsByName;
	private Map<String,Set<Term>> termsByDialect;
	
	public TermCollection() {
		allTerms = new HashSet<Term>();
		termsByType = new HashMap<TermType,Set<Term>>();
		termsByName = new HashMap<String,Set<Term>>();
		termsByDialect = new HashMap<String,Set<Term>>();
	}
	
	public void addTerm(Term t) {
		allTerms.add(t);
		if (termsByType.containsKey(t.type())) {
			termsByType.get(t.type()).add(t);
		} else {
			Set<Term> s = new HashSet<Term>();
			s.add(t);
			termsByType.put(t.type(), s);
		}
		for (TermName n : t.names()) {
			String name = n.getName().toLowerCase();
			if (termsByName.containsKey(name)) {
				termsByName.get(name).add(t);
			} else {
				Set<Term> s = new HashSet<Term>();
				s.add(t);
				termsByName.put(name, s);
			}
			for (DialectSpec d : n.getDialects()) {
				String dialect = d.getName().toLowerCase();
				if (termsByDialect.containsKey(dialect)) {
					termsByDialect.get(dialect).add(t);
				} else {
					Set<Term> s = new HashSet<Term>();
					s.add(t);
					termsByDialect.put(dialect, s);
				}
			}
		}
	}
	
	public Collection<Term> getTerms(TermType termType, String termName, String dialectName, VersionNumber dialectVersion) {
		Set<Term> results = new HashSet<Term>();
		if (termType != null) {
			if (termsByType.containsKey(termType)) {
				results.addAll(termsByType.get(termType));
			}
			if (termName != null) {
				termName = termName.toLowerCase();
				if (termsByName.containsKey(termName)) {
					results.retainAll(termsByName.get(termName));
				} else {
					results.clear();
				}
			}
			if (dialectName != null) {
				dialectName = dialectName.toLowerCase();
				if (termsByDialect.containsKey(dialectName)) {
					results.retainAll(termsByDialect.get(dialectName));
				} else {
					results.clear();
				}
			}
		} else if (termName != null) {
			termName = termName.toLowerCase();
			if (termsByName.containsKey(termName)) {
				results.addAll(termsByName.get(termName));
			}
			if (dialectName != null) {
				dialectName = dialectName.toLowerCase();
				if (termsByDialect.containsKey(dialectName)) {
					results.retainAll(termsByDialect.get(dialectName));
				} else {
					results.clear();
				}
			}
		} else if (dialectName != null) {
			dialectName = dialectName.toLowerCase();
			if (termsByDialect.containsKey(dialectName)) {
				results.addAll(termsByDialect.get(dialectName));
			}
		} else {
			results.addAll(allTerms);
		}
		if (dialectVersion != null) {
			Iterator<Term> it = results.iterator();
			while (it.hasNext()) {
				Term t = it.next();
				boolean keep = false;
				for (TermName n : t.names()) {
					for (DialectSpec d : n.getDialects()) {
						if (d.getVersions().contains(dialectVersion)) {
							keep = true;
							break;
						}
					}
					if (keep) break;
				}
				if (!keep) {
					it.remove();
				}
			}
		}
		return results;
	}
}
