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

import java.beans.PropertyVetoException;
import javax.speech.*;
import javax.speech.synthesis.*;

public class JSAPISpeaker implements Speaker, SpeakableListener {
	private Synthesizer synth;
	private SynthesizerModeDesc smd;
	private boolean speaking;
	private String text;
	
	public JSAPISpeaker() throws AudioException, EngineException {
		synth = Central.createSynthesizer(
				new SynthesizerModeDesc(
						null, "general", null, null, null
				)
		);
		synth.allocate();
		synth.resume();
		synth.addSpeakableListener(this);
		smd = (SynthesizerModeDesc)synth.getEngineModeDesc();
		speaking = false;
		text = "";
	}
	
	protected void finalize() throws EngineException {
		synth.cancelAll();
		synth.deallocate();
	}
	
	@Override
	public String[] getVoices() {
		Voice[] voices = smd.getVoices();
		String[] vstrings = new String[voices.length];
		for (int i = 0; i < voices.length; i++) {
			vstrings[i] = voices[i].getName();
		}
		return vstrings;
	}

	@Override
	public String getDefaultVoice() {
		Voice[] voices = smd.getVoices();
		return voices[0].getName();
	}

	@Override
	public String getMaleVoice() {
		Voice[] voices = smd.getVoices();
		for (Voice v : voices) {
			if (v.getGender() == Voice.GENDER_MALE) {
				return v.getName();
			}
		}
		return voices[0].getName();
	}

	@Override
	public String getFemaleVoice() {
		Voice[] voices = smd.getVoices();
		for (Voice v : voices) {
			if (v.getGender() == Voice.GENDER_FEMALE) {
				return v.getName();
			}
		}
		return voices[0].getName();
	}

	@Override
	public String getNeuterVoice() {
		Voice[] voices = smd.getVoices();
		for (Voice v : voices) {
			if (v.getGender() == Voice.GENDER_NEUTRAL) {
				return v.getName();
			}
		}
		return voices[0].getName();
	}

	@Override
	public boolean hasVoice(String voice) {
		Voice[] voices = smd.getVoices();
		for (Voice v : voices) {
			if (v.getName().equalsIgnoreCase(voice)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void speak(String phrase) {
		synth.speakPlainText(phrase, null);
	}

	@Override
	public void speak(String voice, String phrase) {
		try {
			Voice[] voices = smd.getVoices();
			for (Voice v : voices) {
				if (v.getName().equalsIgnoreCase(voice)) {
					synth.getSynthesizerProperties().setVoice(v);
					break;
				}
			}
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		synth.speakPlainText(phrase, null);
	}

	@Override
	public boolean isSpeaking() {
		return speaking || synth.testEngineState(Synthesizer.QUEUE_NOT_EMPTY);
	}

	@Override
	public String getVoiceSpoken() {
		String s = synth.getSynthesizerProperties().getVoice().getName();
		if (s != null && s.length() > 0) return s;
		else return getDefaultVoice();
	}

	@Override
	public String getPhonemeSpoken() {
		return text;
	}

	@Override
	public String getPhraseSpoken() {
		return text;
	}

	@Override
	public void finishSpeaking() {
		try {
			synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stopSpeaking() {
		synth.cancelAll();
	}

	@Override
	public void markerReached(SpeakableEvent arg0) {}

	@Override
	public void speakableCancelled(SpeakableEvent arg0) {
		speaking = false;
	}

	@Override
	public void speakableEnded(SpeakableEvent arg0) {
		speaking = false;
	}

	@Override
	public void speakablePaused(SpeakableEvent arg0) {}

	@Override
	public void speakableResumed(SpeakableEvent arg0) {}

	@Override
	public void speakableStarted(SpeakableEvent arg0) {
		speaking = true;
		text = (arg0.getText() != null) ? arg0.getText() : arg0.getSource().toString();
	}

	@Override
	public void topOfQueue(SpeakableEvent arg0) {}

	@Override
	public void wordStarted(SpeakableEvent arg0) {}
}
