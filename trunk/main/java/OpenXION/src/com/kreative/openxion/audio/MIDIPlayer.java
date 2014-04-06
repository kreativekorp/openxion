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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
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
	private static final String[] DISPLAY_NAMES = {
		"Acoustic Grand Piano", "Bright Acoustic Piano", "Electric Grand Piano",
		"Honky-Tonk Piano", "Electric Piano 1", "Electric Piano 2", "Harpsichord",
		"Clavi", "Celesta", "Glockenspiel", "Music Box", "Vibraphone", "Marimba",
		"Xylophone", "Tubular Bells", "Dulcimer", "Drawbar Organ", "Percussive Organ",
		"Rock Organ", "Church Organ", "Reed Organ", "Accordion", "Harmonica",
		"Tango Accordion", "Acoustic Guitar (Nylon)", "Acoustic Guitar (Steel)",
		"Electric Guitar (Jazz)", "Electric Guitar (Clean)", "Electric Guitar (Muted)",
		"Overdriven Guitar", "Distortion Guitar", "Guitar Harmonics",
		"Acoustic Bass", "Electric Bass (Finger)", "Electric Bass (Pick)",
		"Fretless Bass", "Slap Bass 1", "Slap Bass 2", "Synth Bass 1",
		"Synth Bass 2", "Violin", "Viola", "Cello", "Contrabass", "Tremolo Strings",
		"Pizzicato Strings", "Orchestral Harp", "Timpani", "String Ensemble 1",
		"String Ensemble 2", "Synth Strings 1", "Synth Strings 2", "Choir Aahs",
		"Voice Oohs", "Synth Voice", "Orchestra Hit", "Trumpet", "Trombone",
		"Tuba", "Muted Trumpet", "French Horn", "Brass Section", "Synth Brass 1",
		"Synth Brass 2", "Soprano Sax", "Alto Sax", "Tenor Sax", "Baritone Sax",
		"Oboe", "English Horn", "Bassoon", "Clarinet", "Piccolo", "Flute",
		"Recorder", "Pan Flute", "Blown Bottle", "Shakuhachi", "Whistle",
		"Ocarina", "Lead 1 (Square)", "Lead 2 (Sawtooth)", "Lead 3 (Calliope)",
		"Lead 4 (Chiff)", "Lead 5 (Charang)", "Lead 6 (Voice)", "Lead 7 (Fifths)",
		"Lead 8 (Bass + Lead)", "Pad 1 (New Age)", "Pad 2 (Warm)", "Pad 3 (Polysynth)",
		"Pad 4 (Choir)", "Pad 5 (Bowed)", "Pad 6 (Metallic)", "Pad 7 (Halo)",
		"Pad 8 (Sweep)", "FX 1 (Rain)", "FX 2 (Soundtrack)", "FX 3 (Crystal)",
		"FX 4 (Atmosphere)", "FX 5 (Brightness)", "FX 6 (Goblins)", "FX 7 (Echoes)",
		"FX 8 (Sci-Fi)", "Sitar", "Banjo", "Shamisen", "Koto", "Kalimba",
		"Bagpipe", "Fiddle", "Shanai", "Tinkle Bell", "Agogo", "Steel Drums",
		"Woodblock", "Taiko Drum", "Melodic Tom", "Synth Drum", "Reverse Cymbal",
		"Guitar Fret Noise", "Breath Noise", "Seashore", "Bird Tweet",
		"Telephone Ring", "Helicopter", "Applause", "Gunshot"
	};
	private static final String[][] NORMALIZED_NAMES = {
		{ "acousticgrandpiano", "acousticgrand", "accousticgrandpiano", "accousticgrand" },
		{ "brightacousticpiano", "brightacoustic", "brightaccousticpiano", "brightaccoustic" },
		{ "electricgrandpiano", "electricgrand" }, { "honkytonkpiano" },
		{ "electricpiano1", "rhodespiano" }, { "electricpiano2", "chorusedpiano" },
		{ "harpsichord" }, { "clavi", "clavinet" }, { "celesta" }, { "glockenspiel" },
		{ "musicbox" }, { "vibraphone" }, { "marimba" }, { "xylophone" }, { "tubularbells" },
		{ "dulcimer" }, { "drawbarorgan", "draworgan" }, { "percussiveorgan" }, { "rockorgan" },
		{ "churchorgan" }, { "reedorgan" }, { "accordion" }, { "harmonica" }, { "tangoaccordion" },
		{ "acousticguitarnylon", "acousticnylonguitar", "accousticguitarnylon", "accousticnylonguitar" },
		{ "acousticguitarsteel", "acousticsteelguitar", "accousticguitarsteel", "accousticsteelguitar" },
		{ "electricguitarjazz", "electricjazzguitar" }, { "electricguitarclean", "electriccleanguitar" },
		{ "electricguitarmuted", "electricmutedguitar" }, { "overdrivenguitar" }, { "distortionguitar" },
		{ "guitarharmonics" }, { "acousticbass", "accousticbass", "woodbass" },
		{ "electricbassfinger", "electricbassfingered" }, { "electricbasspick", "electricbasspicked" },
		{ "fretlessbass" }, { "slapbass1" }, { "slapbass2" }, { "synthbass1" }, { "synthbass2" },
		{ "violin" }, { "viola" }, { "cello" }, { "contrabass" }, { "tremolostrings" },
		{ "pizzicatostrings" }, { "orchestralharp" }, { "timpani" },
		{ "stringensemble1", "acousticstringensemble1", "accousticstringensemble1" },
		{ "stringensemble2", "acousticstringensemble2", "accousticstringensemble2" },
		{ "synthstrings1" }, { "synthstrings2" }, { "choiraahs", "aahchoir" },
		{ "voiceoohs", "oohchoir" }, { "synthvoice", "synvox" }, { "orchestrahit" },
		{ "trumpet" }, { "trombone" }, { "tuba" }, { "mutedtrumpet" }, { "frenchhorn" },
		{ "brasssection" }, { "synthbrass1" }, { "synthbrass2" }, { "sopranosax", "sopranosaxophone" },
		{ "altosax", "altosaxophone" }, { "tenorsax", "tenorsaxophone" }, { "baritonesax", "baritonesaxophone" },
		{ "oboe" }, { "englishhorn" }, { "bassoon" }, { "clarinet" }, { "piccolo", "piccollo" },
		{ "flute" }, { "recorder" }, { "panflute" }, { "blownbottle", "bottleblow" },
		{ "shakuhachi" }, { "whistle" }, { "ocarina" },
		{ "lead1", "synthlead1", "lead1square", "synthlead1square", "square", "lead1squarelead", "synthlead1squarelead", "squarelead" },
		{ "lead2", "synthlead2", "lead2sawtooth", "synthlead2sawtooth", "sawtooth", "lead2sawlead", "synthlead2sawlead", "sawlead" },
		{ "lead3", "synthlead3", "lead3calliope", "synthlead3calliope", "calliope" },
		{ "lead4", "synthlead4", "lead4chiff", "synthlead4chiff", "chiff", "lead4chiffer", "synthlead4chiffer", "chiffer" },
		{ "lead5", "synthlead5", "lead5charang", "synthlead5charang", "charang" },
		{ "lead6", "synthlead6", "lead6voice", "synthlead6voice", "voice" },
		{ "lead7", "synthlead7", "lead7fifths", "synthlead7fifths", "fifths" },
		{ "lead8", "synthlead8", "lead8basslead", "synthlead8basslead", "basslead" },
		{ "pad1", "synthpad1", "pad1newage", "synthpad1newage", "newage" },
		{ "pad2", "synthpad2", "pad2warm", "synthpad2warm", "warm" },
		{ "pad3", "synthpad3", "pad3polysynth", "synthpad3polysynth", "polysynth" },
		{ "pad4", "synthpad4", "pad4choir", "synthpad4choir", "choir" },
		{ "pad5", "synthpad5", "pad5bowed", "synthpad5bowed", "bowed" },
		{ "pad6", "synthpad6", "pad6metallic", "synthpad6metallic", "metallic" },
		{ "pad7", "synthpad7", "pad7halo", "synthpad7halo", "halo" },
		{ "pad8", "synthpad8", "pad8sweep", "synthpad8sweep", "sweep" },
		{ "fx1", "synthfx1", "fx1rain", "synthfx1rain", "rain", "fx1icerain", "synthfx1icerain", "icerain" },
		{ "fx2", "synthfx2", "fx2soundtrack", "synthfx2soundtrack", "soundtrack", "fx2soundtracks", "synthfx2soundtracks", "soundtracks" },
		{ "fx3", "synthfx3", "fx3crystal", "synthfx3crystal", "crystal" },
		{ "fx4", "synthfx4", "fx4atmosphere", "synthfx4atmosphere", "atmosphere" },
		{ "fx5", "synthfx5", "fx5brightness", "synthfx5brightness", "brightness", "fx5bright", "synthfx5bright", "bright" },
		{ "fx6", "synthfx6", "fx6goblins", "synthfx6goblins", "goblins", "fx6goblin", "synthfx6goblin", "goblin" },
		{ "fx7", "synthfx7", "fx7echoes", "synthfx7echoes", "echoes" },
		{ "fx8", "synthfx8", "fx8scifi", "synthfx8scifi", "scifi", "fx8space", "synthfx8space", "space" },
		{ "sitar" }, { "banjo" }, { "shamisen" }, { "koto" }, { "kalimba" }, { "bagpipe" },
		{ "fiddle" }, { "shanai" }, { "tinklebell" }, { "agogo" }, { "steeldrums" },
		{ "woodblock" }, { "taikodrum" }, { "melodictom" }, { "synthdrum", "synthtom" },
		{ "reversecymbal" }, { "guitarfretnoise" }, { "breathnoise" }, { "seashore" },
		{ "birdtweet" }, { "telephonering" }, { "helicopter" }, { "applause" }, { "gunshot" }
	};

	private static final Map<Integer,String> idToNameTable;
	private static final Map<String,Integer> nameToIdTable;
	private static final SortedSet<String> displayNameSet;
	static {
		Map<Integer,String> itn = new HashMap<Integer,String>();
		for (int id = 0; id < DISPLAY_NAMES.length; id++) {
			itn.put(id, DISPLAY_NAMES[id]);
		}
		idToNameTable = Collections.unmodifiableMap(itn);
		Map<String,Integer> nti = new HashMap<String,Integer>();
		for (int id = 0; id < NORMALIZED_NAMES.length; id++) {
			for (String name : NORMALIZED_NAMES[id]) {
				nti.put(name, id);
			}
		}
		nameToIdTable = Collections.unmodifiableMap(nti);
		SortedSet<String> dns = new TreeSet<String>();
		for (String name : DISPLAY_NAMES) {
			dns.add(name);
		}
		displayNameSet = Collections.unmodifiableSortedSet(dns);
	}

	@Override
	public String[] getInstruments() {
		return displayNameSet.toArray(new String[0]);
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
