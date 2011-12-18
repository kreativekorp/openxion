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
 * @since OpenXION 1.3
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.tr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrPattern {
	private static Map<String, TrPattern> patterns = new HashMap<String, TrPattern>();
	private static Map<String, TrPattern> cpatterns = new HashMap<String, TrPattern>();
	
	public static TrPattern compile(String pattern, boolean complement) {
		Map<String, TrPattern> ps = (complement ? cpatterns : patterns);
		if (ps.containsKey(pattern)) {
			return ps.get(pattern);
		} else {
			TrPattern p = new TrPattern(pattern, complement);
			ps.put(pattern, p);
			return p;
		}
	}
	
	public static Translator translator(TrPattern inputPattern, TrPattern outputPattern) {
		return new Translator(inputPattern.set, outputPattern.set);
	}
	
	public Deletor deletor() {
		return new Deletor(this.set);
	}
	
	public Squeezor squeezor() {
		return new Squeezor(this.set);
	}
	
	public Matchor matchor() {
		return new Matchor(this.set);
	}
	
	private TrCharacterSet set;
	private String pattern;
	
	private static final Pattern RANGE_PATTERN = Pattern.compile("^([^-])-(.)", Pattern.DOTALL);
	private TrPattern(String pattern, boolean complement) {
		List<TrCharacterSet> sets = new ArrayList<TrCharacterSet>();
		int i = 0;
		Matcher m = null;
		while (i < pattern.length()) {
			if ((m = RANGE_PATTERN.matcher(pattern.substring(i))).find() && m.start() == 0) {
				i += m.end();
				sets.add(new TrRangeCharacterSet(
						m.group(1).codePointAt(0),
						m.group(2).codePointAt(0)+1
				));
			} else {
				int ch = pattern.codePointAt(i++);
				if (ch >= 0x10000) i++;
				sets.add(new TrSingleCharacterSet(ch));
			}
		}
		this.set = new TrCollectionCharacterSet(sets);
		if (complement) this.set = new TrComplementCharacterSet(this.set);
		this.set = new TrCachedCharacterSet(this.set);
		this.pattern = pattern;
	}
	
	public String toString() {
		return pattern;
	}
	
	public int hashCode() {
		return pattern.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o instanceof TrPattern) {
			TrPattern other = (TrPattern)o;
			return (this.pattern.equals(other.pattern));
		} else {
			return false;
		}
	}
}
