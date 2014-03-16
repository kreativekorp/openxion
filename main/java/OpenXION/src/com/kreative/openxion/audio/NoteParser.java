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

import java.util.ArrayList;
import java.util.List;

public class NoteParser {
	private int step = 0;
	private int octave = 4;
	private int dur = Note.WHOLE_NOTE_DURATION / 4;
	private int vel = 127;
	
	public void reset() {
		step = 0;
		octave = 4;
		dur = Note.WHOLE_NOTE_DURATION / 4;
		vel = 127;
	}
	
	public List<Note> parseNotes(List<String> notesIn) {
		return parseNotes(notesIn, null);
	}
	
	public List<Note> parseNotes(String[] notesIn) {
		return parseNotes(notesIn, null);
	}
	
	public List<Note> parseNotes(String notesIn) {
		return parseNotes(notesIn, null);
	}
	
	public List<Note> parseNotes(List<String> notesIn, List<Note> notesOut) {
		if (notesOut == null) notesOut = new ArrayList<Note>();
		for (String s : notesIn) {
			parseNotes(s, notesOut);
		}
		return notesOut;
	}
	
	public List<Note> parseNotes(String[] notesIn, List<Note> notesOut) {
		if (notesOut == null) notesOut = new ArrayList<Note>();
		for (String s : notesIn) {
			parseNotes(s, notesOut);
		}
		return notesOut;
	}
	
	public List<Note> parseNotes(String notesIn, List<Note> notesOut) {
		if (notesOut == null) notesOut = new ArrayList<Note>();
		int i = 0, j = notesIn.length();
		while (i < j && isWhiteSpace(notesIn.charAt(i))) i++;
		while (i < j) {
			int wordStart = i;
			while (i < j && !isWhiteSpace(notesIn.charAt(i))) i++;
			int wordEnd = i;
			while (i < j && isWhiteSpace(notesIn.charAt(i))) i++;
			String noteIn = notesIn.substring(wordStart, wordEnd);
			Note noteOut = parseNote(noteIn);
			notesOut.add(noteOut);
		}
		return notesOut;
	}
	
	public Note parseNote(String noteIn) {
		noteIn = noteIn.trim().toLowerCase();
		boolean rest = noteIn.contains("r");
		if (rest) noteIn = noteIn.replace("r", "");
		int p = 0, n = noteIn.length();
		/* Pitch */
		if (!rest && p < n) {
			if (isPitch(noteIn.charAt(p))) {
				step = pitchValue(noteIn.charAt(p));
				p++;
				while (p < n && isAccidental(noteIn.charAt(p))) {
					step += accidentalValue(noteIn.charAt(p));
					p++;
				}
				if (p < n && Character.isDigit(noteIn.charAt(p))) {
					octave = 0;
					while (p < n && Character.isDigit(noteIn.charAt(p))) {
						octave *= 10;
						octave += Character.getNumericValue(noteIn.charAt(p));
						p++;
					}
				}
			} else if (isNPM(noteIn.charAt(p))) {
				int midiValue = 0;
				p++;
				while (p < n && Character.isDigit(noteIn.charAt(p))) {
					midiValue *= 10;
					midiValue += Character.getNumericValue(noteIn.charAt(p));
					p++;
				}
				step = midiValue % 12;
				octave = midiValue / 12 - 1;
			}
		}
		/* Duration */
		if (p < n) {
			if (isDuration(noteIn.charAt(p))) {
				dur = durationValue(noteIn.charAt(p));
				p++;
				while (p < n && isDurationModifier(noteIn.charAt(p))) {
					dur = durationModifierValue(noteIn.charAt(p), dur);
					p++;
				}
			} else if (noteIn.charAt(p) == 'd') {
				dur = 0;
				p++;
				while (p < n && Character.isDigit(noteIn.charAt(p))) {
					dur *= 10;
					dur += Character.getNumericValue(noteIn.charAt(p));
					p++;
				}
			}
		}
		/* Velocity */
		if (!rest && p < n) {
			switch (noteIn.charAt(p)) {
				case 'p':
					vel = 48;
					p++;
					while (p < n && noteIn.charAt(p) == 'p') {
						vel -= 16;
						p++;
					}
					if (vel < 1) vel = 1;
					break;
				case 'm':
					vel = 72;
					p++;
					if (p < n) {
						if (noteIn.charAt(p) == 'p') {
							while (p < n && noteIn.charAt(p) == 'p') {
								vel -= 8;
								p++;
							}
							if (vel < 1) vel = 1;
						} else if (noteIn.charAt(p) == 'f') {
							while (p < n && noteIn.charAt(p) == 'f') {
								vel += 8;
								p++;
							}
							if (vel > 127) vel = 127;
						}
					}
					break;
				case 'f':
					vel = 96;
					p++;
					while (p < n && noteIn.charAt(p) == 'f') {
						vel += 16;
						p++;
					}
					if (vel > 127) vel = 127;
					break;
				case 'v':
					vel = 0;
					p++;
					while (p < n && Character.isDigit(noteIn.charAt(p))) {
						vel *= 10;
						vel += Character.getNumericValue(noteIn.charAt(p));
						p++;
					}
					break;
			}
		}
		/* Effect */
		if (!rest) {
			boolean stoccato = false, fermata = false, silent = false, chord = false;
			while (p < n && isEffect(noteIn.charAt(p))) {
				switch (noteIn.charAt(p)) {
					case ',': stoccato = true; break;
					case '*': fermata = true; silent = false; break;
					case '!': fermata = false; silent = true; break;
					case '+': chord = true; break;
				}
				p++;
			}
			return new Note(
				false, octave * 12 + step + 12, dur, vel,
				stoccato, fermata, silent, chord
			);
		} else {
			return new Note(
				true, octave * 12 + step + 12, dur, vel
			);
		}
	}
	
	private static boolean isWhiteSpace(char ch) {
		return ch <= 0x20 || (ch >= 0x7F && ch <= 0xA0) || java.lang.Character.isSpaceChar(ch);
	}
	
	private static boolean isPitch(char ch) {
		return ch >= 'a' && ch <= 'g';
	}
	
	private static int pitchValue(char ch) {
		switch (ch) {
			case 'c': return 0;
			case 'd': return 2;
			case 'e': return 4;
			case 'f': return 5;
			case 'g': return 7;
			case 'a': return 9;
			case 'b': return 11;
			default: return 0;
		}
	}
	
	private static boolean isAccidental(char ch) {
		return ch == 'b' || ch == '#';
	}
	
	private static int accidentalValue(char ch) {
		switch (ch) {
			case 'b': return -1;
			case '#': return +1;
			default: return 0;
		}
	}
	
	private static boolean isNPM(char ch) {
		return ch == 'n' || ch == 'p' || ch == 'm' || ch == '$';
	}
	
	private static boolean isDuration(char ch) {
		return ch == 'z' || ch == 'l' || ch == 'i' || ch == 'u'
			|| ch == 'w' || ch == 'h' || ch == 'q' || ch == 'e'
			|| ch == 's' || ch == 't' || ch == 'x' || ch == 'o';
	}
	
	private static int durationValue(char ch) {
		switch (ch) {
			case 'z': return Note.WHOLE_NOTE_DURATION * 8;
			case 'l': return Note.WHOLE_NOTE_DURATION * 6;
			case 'i': return Note.WHOLE_NOTE_DURATION * 4;
			case 'u': return Note.WHOLE_NOTE_DURATION * 2;
			case 'w': return Note.WHOLE_NOTE_DURATION;
			case 'h': return Note.WHOLE_NOTE_DURATION / 2;
			case 'q': return Note.WHOLE_NOTE_DURATION / 4;
			case 'e': return Note.WHOLE_NOTE_DURATION / 8;
			case 's': return Note.WHOLE_NOTE_DURATION / 16;
			case 't': return Note.WHOLE_NOTE_DURATION / 32;
			case 'x': return Note.WHOLE_NOTE_DURATION / 64;
			case 'o': return Note.WHOLE_NOTE_DURATION / 128;
			default: return 0;
		}
	}
	
	private static boolean isDurationModifier(char ch) {
		return ch == '.' || (Character.isDigit(ch) && Character.getNumericValue(ch) > 1);
	}
	
	private static int durationModifierValue(char ch, int dur) {
		if (ch == '.') return dur + dur / 2;
		if (Character.isDigit(ch)) {
			int divisor = Character.getNumericValue(ch);
			if (divisor > 1) return dur / divisor;
		}
		return dur;
	}
	
	private static boolean isEffect(char ch) {
		return ch == ',' || ch == '*' || ch == '!' || ch == '+';
	}
}
