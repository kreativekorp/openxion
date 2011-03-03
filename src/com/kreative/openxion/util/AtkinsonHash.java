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

package com.kreative.openxion.util;

import java.io.UnsupportedEncodingException;

/**
 * The AtkinsonHash utility class implements the
 * hash algorithm used by HyperCard's ask password command.
 * @since OpenXION 0.9
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class AtkinsonHash {
	private AtkinsonHash() {}
	
	public static int hash(String password) {
		try {
			return hash(password.toLowerCase().getBytes("MACROMAN"));
		} catch (UnsupportedEncodingException uee) {
			return hash(password.toLowerCase().getBytes());
		}
	}
	
	/* From: Anthony DeRobertis <derobert@erols.com> */
	/* Date: 22-May-2001 11:08:56 GMT */
	
	/* HyperCard's password hashing is essentially as follows: */
	public static int hash(byte[] data) {
		if (data == null || data.length == 0) return 0x42696C6C;
		else {
			int hash = 0;
			/* Seed the Toolbox PRNG with chartonum(char 1 of password) + the length of password */
			int seed = (data[0] & 0xFF) + data.length;
			/* Loop through all the BITS of password */
			for (byte b : data) {
				for (int m = 0x80; m != 0; m >>>= 1) {
					/* Call Random() */
					long newSeed = (seed & 0xFFFFFFFFL) * 0x41A7L;
					while (newSeed >= 0x80000000L) {
						newSeed = (newSeed & 0x7FFFFFFFL) + (newSeed >>> 31L);
					}
					seed = (newSeed == 0x7FFFFFFFL) ? 0 : (int)newSeed;
					/* If the bit is set, add in the _new_ random seed */
					if ((b & m) != 0) hash += seed;
				}
			}
			/* Return accumulator */
			return hash;
		}
	}
	/* [ A little reverse engineering can go a long way ;-) ] */
}
