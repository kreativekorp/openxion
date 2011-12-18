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
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.ast;

import java.util.List;
import com.kreative.openxion.XNToken;

public class XNRepeatBlock extends XNStatement {
	private static final long serialVersionUID = 113L;
	public XNToken repeatToken;
	public XNRepeatParameters params;
	public List<XNStatement> body;
	public XNToken lastlyToken;
	public List<XNStatement> lastlyBody;
	public XNToken endRepeatToken;
	
	public int getRepeatLine() { if (repeatToken == null) return 0; else return repeatToken.beginLine; }
	public int getRepeatCol() { if (repeatToken == null) return 0; else return repeatToken.beginColumn; }
	public int getLastlyLine() { if (lastlyToken == null) return 0; else return lastlyToken.beginLine; }
	public int getLastlyCol() { if (lastlyToken == null) return 0; else return lastlyToken.beginColumn; }
	public int getEndRepeatLine() { if (endRepeatToken == null) return 0; else return endRepeatToken.beginLine; }
	public int getEndRepeatCol() { if (endRepeatToken == null) return 0; else return endRepeatToken.beginColumn; }
	
	protected String toString(String indent) {
		String s = indent+"repeat "+params.toString();
		for (XNStatement st : body) {
			s += "\n" + st.toString(indent+"\t");
		}
		if (lastlyBody != null) {
			s += "\n" + indent + "lastly";
			for (XNStatement st : lastlyBody) {
				s += "\n" + st.toString(indent+"\t");
			}
		}
		s += "\n"+indent+"end repeat";
		return s;
	}
}
