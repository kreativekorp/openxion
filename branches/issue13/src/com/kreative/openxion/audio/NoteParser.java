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
 * @since OpenXION 1.2
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.audio;

public class NoteParser {
	private int step = 0;
	private int octave = 4;
	private int dur = Note.WHOLE_NOTE_DURATION/4;
	private int vel = 127;
	
	public Note parseNote(String noteIn) {
		noteIn = noteIn.trim().toLowerCase();
		boolean rest = noteIn.contains("r");
		if (rest) noteIn = noteIn.replace("r", "");
		int p = 0;
		int n = noteIn.length();
		if (!rest) {
			/* note value */
			if (p < n) switch (noteIn.charAt(p)) {
				case 'C': case 'c': step = 0; p++; break;
				case 'D': case 'd': step = 2; p++; break;
				case 'E': case 'e': step = 4; p++; break;
				case 'F': case 'f': step = 5; p++; break;
				case 'G': case 'g': step = 7; p++; break;
				case 'A': case 'a': step = 9; p++; break;
				case 'B': case 'b': step = 11; p++; break;
				case 'N': case 'P': case 'M':
				case 'n': case 'p': case 'm':
				{
					p++;
					int midivalue = 0;
					while (p < n && Character.isDigit(noteIn.charAt(p))) {
						midivalue = midivalue*10 + Character.getNumericValue(noteIn.charAt(p++));
					}
					step = midivalue % 12;
					octave = midivalue / 12 - 1;
				}
				break;
			}
			/* accidental */
			while (p < n && noteIn.charAt(p) == 'b') {
				step--; p++;
			}
			while (p < n && noteIn.charAt(p) == '#') {
				step++; p++;
			}
			/* octave */
			if (p < n && Character.isDigit(noteIn.charAt(p))) {
				octave = 0;
				while (p < n && Character.isDigit(noteIn.charAt(p))) {
					octave = octave*10 + Character.getNumericValue(noteIn.charAt(p++));
				}
			}
		}
		/* duration */
		if (p < n) switch (noteIn.charAt(p)) {
			case 'w': dur = Note.WHOLE_NOTE_DURATION; p++; break;
			case 'h': dur = Note.WHOLE_NOTE_DURATION/2; p++; break;
			case 'q': dur = Note.WHOLE_NOTE_DURATION/4; p++; break;
			case 'e': dur = Note.WHOLE_NOTE_DURATION/8; p++; break;
			case 's': dur = Note.WHOLE_NOTE_DURATION/16; p++; break;
			case 't': dur = Note.WHOLE_NOTE_DURATION/32; p++; break;
			case 'x': dur = Note.WHOLE_NOTE_DURATION/64; p++; break;
			case 'o': dur = Note.WHOLE_NOTE_DURATION/128; p++; break;
			case 'd': {
				p++;
				dur = 0;
				while (p < n && Character.isDigit(noteIn.charAt(p))) {
					dur = dur*10 + Character.getNumericValue(noteIn.charAt(p++));
				}
			}
			break;
		}
		/* dotted and triplet */
		while (p < n && noteIn.charAt(p) == '.') {
			dur += dur/2; p++;
		}
		while (p < n && Character.isDigit(noteIn.charAt(p))) {
			dur /= Character.getNumericValue(noteIn.charAt(p++));
		}
		if (!rest) {
			/* velocity */
			if (p < n) switch (noteIn.charAt(p)) {
				// midi value
				case 'v': {
					p++;
					vel = 0;
					while (p < n && Character.isDigit(noteIn.charAt(p))) {
						vel = vel*10 + Character.getNumericValue(noteIn.charAt(p++));
					}
				}
				break;
				// mezzo
				case 'm': {
					p++;
					vel = 72;
					while (p < n && noteIn.charAt(p) == 'p') {
						vel -= 8; p++;
					}
					while (p < n && noteIn.charAt(p) == 'f') {
						vel += 8; p++;
					}
					if (vel < 1) vel = 1;
					else if (vel > 127) vel = 127;
				}
				break;
				// piano
				case 'p': {
					p++;
					vel = 48;
					while (p < n && noteIn.charAt(p) == 'p') {
						vel -= 16; p++;
					}
					if (vel < 1) vel = 1;
				}
				break;
				// forte
				case 'f': {
					p++;
					vel = 96;
					while (p < n && noteIn.charAt(p) == 'f') {
						vel += 16; p++;
					}
					if (vel > 127) vel = 127;
				}
				break;
			}
			/* special effects */
			boolean inEffects = true;
			boolean stoccato = false, fermata = false, silent = false, chord = false;
			while (p < n && inEffects) switch (noteIn.charAt(p)) {
				case ',': stoccato = true; p++; break;
				case '*': fermata = true; silent = false; p++; break;
				case '!': silent = true; fermata = false; p++; break;
				case '+': chord = true; p++; break;
				default: inEffects = false; break;
			}
			Note noteOut = new Note();
			noteOut.rest = false;
			noteOut.note = octave*12+step+12;
			noteOut.duration = dur;
			noteOut.velocity = vel;
			noteOut.stoccato = stoccato;
			noteOut.fermata = fermata;
			noteOut.silent = silent;
			noteOut.chord = chord;
			return noteOut;
		} else {
			Note noteOut = new Note();
			noteOut.rest = true;
			noteOut.note = octave*12+step+12;
			noteOut.duration = dur;
			noteOut.velocity = vel;
			noteOut.stoccato = false;
			noteOut.fermata = false;
			noteOut.silent = false;
			noteOut.chord = false;
			return noteOut;
		}
	}
	
	public Note[] parseNotes(String[] notesIn) {
		Note[] notesOut = new Note[notesIn.length];
		for (int i = 0; i < notesIn.length; i++) {
			notesOut[i] = parseNote(notesIn[i]);
		}
		return notesOut;
	}
	
	public Note[] parseNotes(String notesIn) {
		String[] n = notesIn.trim().split("\\s+");
		return parseNotes(n);
	}
}
