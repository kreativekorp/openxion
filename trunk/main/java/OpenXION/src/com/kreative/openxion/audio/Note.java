/*
 * Copyright &copy; 2011-2014 Rebecca G. Bettencourt / Kreative Software
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
 * @since OpenXION 1.2
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.audio;

public class Note {
	public static final double WHOLE_NOTE_DURATION = 256;
	
	public final boolean rest;
	public final int pitch;
	public final double duration;
	public final int velocity;
	public final boolean stoccato;
	public final boolean fermata;
	public final boolean silent;
	public final boolean chord;
	
	public Note(boolean rest, int pitch, double duration, int velocity) {
		this(rest, pitch, duration, velocity, false, false, false, false);
	}
	
	public Note(boolean rest, int pitch, double duration, int velocity, boolean stoccato, boolean fermata, boolean silent, boolean chord) {
		this.rest = rest;
		this.pitch = pitch;
		this.duration = duration;
		this.velocity = velocity;
		this.stoccato = stoccato;
		this.fermata = fermata;
		this.silent = silent;
		this.chord = chord;
	}
	
	public double playDuration() {
		if (stoccato) return duration / 2;
		return duration;
	}
}
