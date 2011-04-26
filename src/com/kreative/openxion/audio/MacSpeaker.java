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

import java.io.*;
import java.util.Vector;
import com.kreative.openxion.util.XIONUtil;

public class MacSpeaker implements Speaker {
	private String dv, mv, fv, nv;
	private String[] voices;
	
	public MacSpeaker() throws IOException {
		// is this cheating?
		File tmp = new File(System.getProperty("user.home"));
		tmp = new File(tmp, "Library");
		tmp = new File(tmp, "Preferences");
		tmp = new File(tmp, "com.kreative.openxion.voices.py");
		if (!tmp.exists()) {
			FileOutputStream out = new FileOutputStream(tmp);
			for (String line : VOICES_PY) out.write(line.getBytes());
			out.close();
		}
		String v = XIONUtil.captureProcessOutput(new String[]{"python", tmp.getAbsolutePath()});
		String[] vv = v.trim().split("\n");
		dv = vv[0]; mv = vv[1]; fv = vv[2]; nv = vv[3];
		voices = new String[vv.length-4];
		for (int i = 0; i < voices.length; i++) {
			voices[i] = vv[i+4];
		}
	}
	
	@Override
	public String[] getVoices() {
		return voices;
	}

	@Override
	public String getDefaultVoice() {
		return dv;
	}

	@Override
	public String getMaleVoice() {
		return mv;
	}

	@Override
	public String getFemaleVoice() {
		return fv;
	}

	@Override
	public String getNeuterVoice() {
		return nv;
	}

	@Override
	public boolean hasVoice(String voice) {
		for (String v : voices) {
			if (v.equalsIgnoreCase(voice)) {
				return true;
			}
		}
		return false;
	}

	private Vector<String[]> q = new Vector<String[]>();
	private String[] qmd = new String[]{"","","",""};
	private MacSpeakerThread qt = null;
	
	@Override
	public synchronized void speak(String phrase) {
		q.add(new String[]{"say", "-v", dv, phrase});
		if (qt == null || !qt.isAlive()) {
			qt = new MacSpeakerThread();
			qt.start();
		}
	}

	@Override
	public synchronized void speak(String voice, String phrase) {
		q.add(new String[]{"say", "-v", voice, phrase});
		if (qt == null || !qt.isAlive()) {
			qt = new MacSpeakerThread();
			qt.start();
		}
	}

	@Override
	public synchronized boolean isSpeaking() {
		return (qt != null && qt.isAlive()) || !q.isEmpty();
	}

	@Override
	public synchronized String getVoiceSpoken() {
		return qmd[2];
	}

	@Override
	public synchronized String getPhonemeSpoken() {
		return qmd[3];
	}

	@Override
	public synchronized String getPhraseSpoken() {
		return qmd[3];
	}

	@Override
	public synchronized void finishSpeaking() {
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
	public synchronized void stopSpeaking() {
		if (qt != null) {
			qt.interrupt();
			qt = null;
		}
		q.clear();
	}
	
	private class MacSpeakerThread extends Thread {
		public void run() {
			while (!Thread.interrupted() && !q.isEmpty()) {
				qmd = q.remove(0);
				Process p = null;
				try {
					p = Runtime.getRuntime().exec(qmd);
					p.waitFor();
				} catch (IOException e) {
					// ignored
				} catch (InterruptedException e) {
					if (p != null) p.destroy();
				}
			}
		}
	}
	
	private static final String[] VOICES_PY = {
		"from AppKit import NSSpeechSynthesizer\n",
		"defaultvoice = NSSpeechSynthesizer.defaultVoice()\n",
		"voices = NSSpeechSynthesizer.availableVoices()\n",
		"defaultvoiceattr = NSSpeechSynthesizer.attributesForVoice_(defaultvoice)\n",
		"defaultvoicename = defaultvoiceattr['VoiceName']\n",
		"print defaultvoicename\n",
		"for voice in voices:\n",
		"\tvoiceattr = NSSpeechSynthesizer.attributesForVoice_(voice)\n",
		"\tvoicename = voiceattr['VoiceName']\n",
		"\tvoicegender = voiceattr['VoiceGender']\n",
		"\tif voicegender == 'VoiceGenderMale':\n",
		"\t\tprint voicename\n",
		"\t\tbreak\n",
		"else:\n",
		"\tprint defaultvoicename\n",
		"for voice in voices:\n",
		"\tvoiceattr = NSSpeechSynthesizer.attributesForVoice_(voice)\n",
		"\tvoicename = voiceattr['VoiceName']\n",
		"\tvoicegender = voiceattr['VoiceGender']\n",
		"\tif voicegender == 'VoiceGenderFemale':\n",
		"\t\tprint voicename\n",
		"\t\tbreak\n",
		"else:\n",
		"\tprint defaultvoicename\n",
		"for voice in voices:\n",
		"\tvoiceattr = NSSpeechSynthesizer.attributesForVoice_(voice)\n",
		"\tvoicename = voiceattr['VoiceName']\n",
		"\tvoicegender = voiceattr['VoiceGender']\n",
		"\tif voicegender == 'VoiceGenderNeuter':\n",
		"\t\tprint voicename\n",
		"\t\tbreak\n",
		"else:\n",
		"\tprint defaultvoicename\n",
		"voicenames = []\n",
		"for voice in voices:\n",
		"\tvoiceattr = NSSpeechSynthesizer.attributesForVoice_(voice)\n",
		"\tvoicename = voiceattr['VoiceName']\n",
		"\tif voicename not in voicenames:\n",
		"\t\tvoicenames.append(voicename)\n",
		"for voicename in voicenames:\n",
		"\tprint voicename\n",
	};
}
