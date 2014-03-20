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

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class MIDIPlayer implements Player {
	private static final String[] stdNames = {
		"Acoustic Grand Piano", "Bright Acoustic Piano", "Electric Grand Piano",
		"Honky-Tonk Piano", "Rhodes Piano", "Chorused Piano", "Harpsichord",
		"Clavinet", "Celesta", "Glockenspiel", "Music Box",
		"Vibraphone", "Marimba", "Xylophone", "Tubular Bells",
		"Dulcimer", "Draw Organ", "Percussive Organ", "Rock Organ",
		"Church Organ", "Reed Organ", "Accordion", "Harmonica",
		"Tango Accordion", "Accoustic Nylon Guitar", "Accoustic Steel Guitar", "Electric Jazz Guitar",
		"Electric Clean Guitar", "Electric Guitar Muted", "Overdriven Guitar", "Distortion Guitar",
		"Guitar Harmonics", "Wood Bass", "Electric Bass Fingered", "Electric Bass Picked",
		"Fretless Bass", "Slap Bass 1", "Slap Bass 2", "Synth Bass 1",
		"Synth Bass 2", "Violin", "Viola", "Cello",
		"Contrabass", "Tremolo Strings", "Pizzicato Strings", "Orchestral Harp",
		"Timpani", "Acoustic String Ensemble 1", "Acoustic String Ensemble 2", "Synth Strings 1",
		"Synth Strings 2", "Aah Choir", "Ooh Choir", "Synvox",
		"Orchestra Hit", "Trumpet", "Trombone", "Tuba",
		"Muted Trumpet", "French Horn", "Brass Section", "Synth Brass 1",
		"Synth Brass 2", "Soprano Sax", "Alto Sax", "Tenor Sax",
		"Baritone Sax", "Oboe", "English Horn", "Bassoon",
		"Clarinet", "Piccolo", "Flute", "Recorder",
		"Pan Flute", "Bottle Blow", "Shakuhachi", "Whistle",
		"Ocarina", "Square Lead", "Saw Lead", "Calliope",
		"Chiffer", "Synth Lead 5", "Synth Lead 6", "Synth Lead 7",
		"Synth Lead 8", "Synth Pad 1", "Synth Pad 2", "Synth Pad 3",
		"Synth Pad 4", "Synth Pad 5", "Synth Pad 6", "Synth Pad 7",
		"Synth Pad 8", "Ice Rain", "Soundtracks", "Crystal",
		"Atmosphere", "Bright", "Goblin", "Echoes",
		"Space", "Sitar", "Banjo", "Shamisen",
		"Koto", "Kalimba", "Bagpipe", "Fiddle",
		"Shanai", "Tinkle Bell", "Agogo", "Steel Drums",
		"Woodblock", "Taiko Drum", "Melodic Tom", "Synth Tom",
		"Reverse Cymbal", "Guitar Fret Noise", "Breath Noise", "Seashore",
		"Bird Tweet", "Telephone Ring", "Helicopter", "Applause", "Gunshot"
	};
	private static SortedMap<Integer,String> idToNameTable;
	private static Map<String,Integer> nameToIdTable;
	static {
		idToNameTable = new TreeMap<Integer,String>();
		nameToIdTable = new HashMap<String,Integer>();
		for (int id = 0; id < stdNames.length; id++) {
			String name = stdNames[id];
			String normalizedname = name.trim().replaceAll("[^A-Za-z0-9]", "").toLowerCase();
			idToNameTable.put(id, name);
			nameToIdTable.put(normalizedname, id);
		}
	}

	@Override
	public String[] getInstruments() {
		return idToNameTable.values().toArray(new String[0]);
	}

	@Override
	public boolean hasInstrument(String instrument) {
		instrument = instrument.trim().replaceAll("[^A-Za-z0-9]", "").toLowerCase();
		return nameToIdTable.containsKey(instrument);
	}

	@Override
	public synchronized void play(String instrument) {
		instrument = instrument.trim().replaceAll("[^A-Za-z0-9]", "").toLowerCase();
		int inst = nameToIdTable.containsKey(instrument) ? nameToIdTable.get(instrument).intValue() : 0;
		Note n = new Note(false, 60, Note.WHOLE_NOTE_DURATION / 4, 127);
		q.add(new CynthiaSequence(inst, 120, new Note[]{n}));
		if (qt == null || !qt.isAlive()) {
			qt = new CynthiaThread();
			qt.start();
		}
	}

	@Override
	public synchronized void play(String instrument, float bpm, Note[] notes) {
		instrument = instrument.trim().replaceAll("[^A-Za-z0-9]", "").toLowerCase();
		int inst = nameToIdTable.containsKey(instrument) ? nameToIdTable.get(instrument).intValue() : 0;
		q.add(new CynthiaSequence(inst, bpm, notes));
		if (qt == null || !qt.isAlive()) {
			qt = new CynthiaThread();
			qt.start();
		}
	}

	@Override
	public synchronized boolean isPlaying() {
		return (qt != null && qt.isAlive()) || !q.isEmpty();
	}

	@Override
	public synchronized String getInstrumentPlayed() {
		return idToNameTable.get(qp.instrument);
	}

	@Override
	public synchronized Note[] getNotesPlayed() {
		return qp.notes;
	}

	@Override
	public synchronized void finishPlaying() {
		if (qt != null) {
			try {
				qt.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			qt = null;
		}
		q.clear();
	}

	@Override
	public synchronized void stopPlaying() {
		if (qt != null) {
			qt.interrupt();
			qt = null;
		}
		q.clear();
	}
	
	// QUEUE
	
	private static class CynthiaSequence {
		public final int instrument;
		public final float bpm;
		public final Note[] notes;
		public CynthiaSequence(int inst, float bpm, Note[] notes) {
			this.instrument = inst;
			this.bpm = bpm;
			this.notes = notes;
		}
	}
	
	private Vector<CynthiaSequence> q = new Vector<CynthiaSequence>();
	private CynthiaSequence qp = new CynthiaSequence(0, 0, new Note[0]);
	private CynthiaThread qt = null;
	
	private class CynthiaThread extends Thread {
		public void run() {
			while (!Thread.interrupted() && !q.isEmpty()) {
				qp = q.remove(0);
				try {
					Sequencer seq = MidiSystem.getSequencer();
					seq.open();
					seq.setSequence(createSequence(qp.instrument, qp.notes));
					seq.setTempoInBPM(qp.bpm);
					seq.start();
					while (!Thread.interrupted() && seq.isRunning());
					seq.stop();
					seq.close();
				} catch (MidiUnavailableException e) {
					q.add(0, qp);
				} catch (InvalidMidiDataException e) {
					// skip it
				}
			}
		}
	}
	
	// MIDI
	
	private static MidiEvent makeInstEvent(int channel, int instNum, double tick) throws InvalidMidiDataException {
		ShortMessage m = new ShortMessage();
		m.setMessage(ShortMessage.PROGRAM_CHANGE, channel, instNum, 0);
		return new MidiEvent(m, (long)Math.round(tick));
	}
	
	private static MidiEvent makeNoteEvent(int channel, int pitch, int vel, double tick) throws InvalidMidiDataException {
		ShortMessage m = new ShortMessage();
		m.setMessage(ShortMessage.NOTE_ON, channel, pitch, vel);
		return new MidiEvent(m, (long)Math.round(tick));
	}
	
	private static double addToTrack(Track trk, int channel, double time, Note[] notes) throws InvalidMidiDataException {
		for (Note note : notes) {
			if (note.rest) {
				time += note.duration;
			} else {
				if (!note.silent) trk.add(makeNoteEvent(channel, note.pitch, note.velocity, time));
				if (!note.fermata) trk.add(makeNoteEvent(channel, note.pitch, 0, time + note.playDuration()));
				if (!note.chord) time += note.duration;
			}
		}
		return time;
	}
	
	private static Sequence createSequence(int inst, Note[] notes) throws InvalidMidiDataException {
		Sequence seq = new Sequence(Sequence.PPQ, (int)Math.round(Note.WHOLE_NOTE_DURATION / 4));
		Track trk = seq.createTrack();
		trk.add(makeInstEvent(1, inst, 0));
		addToTrack(trk, 1, 0, notes);
		return seq;
	}
}
