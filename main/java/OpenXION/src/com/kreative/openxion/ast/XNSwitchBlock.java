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

public class XNSwitchBlock extends XNStatement {
	private static final long serialVersionUID = 114L;
	public XNToken switchToken;
	public XNExpression switchOn;
	public List<XNCaseBlock> cases;
	public XNToken endSwitchToken;
	
	public int getSwitchLine() { if (switchToken == null) return 0; else return switchToken.beginLine; }
	public int getSwitchCol() { if (switchToken == null) return 0; else return switchToken.beginColumn; }
	public int getEndSwitchLine() { if (endSwitchToken == null) return 0; else return endSwitchToken.beginLine; }
	public int getEndSwitchCol() { if (endSwitchToken == null) return 0; else return endSwitchToken.beginColumn; }
	
	protected String toString(String indent) {
		String s = indent+"switch "+switchOn.toString();
		if (cases != null) {
			for (XNCaseBlock blk : cases) {
				s += "\n" + blk.toString(indent);
			}
		}
		s += "\n"+indent+"end switch";
		return s;
	}
}
