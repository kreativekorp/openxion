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

import java.util.List;
import java.util.Vector;
import javax.sound.sampled.*;

public class ToneGenerator implements Toner, AudioDialer {
	private float lastFreq = 0;
	private float[] lastFreqs = null;
	private char lastButton = 0;
	private char[] lastButtons = null;
	private String lastNumber = null;
	private Vector<Object> q = new Vector<Object>();
	private ToneGeneratorThread qt = null;
	
	@Override
	public float getMinimumAmplitude() {
		return 0;
	}

	@Override
	public float getMaximumAmplitude() {
		return 32000;
	}
	
	@Override
	public synchronized void tone(long duration, float amplitude, float frequency) {
		q.add(new PCMTone(new float[]{frequency}, new float[]{amplitude}, duration));
		if (qt == null || !qt.isAlive()) {
			qt = new ToneGeneratorThread();
			qt.start();
		}
	}

	@Override
	public synchronized void tone(long duration, float[] amplitude, float[] frequency) {
		q.add(new PCMTone(frequency, amplitude, duration));
		if (qt == null || !qt.isAlive()) {
			qt = new ToneGeneratorThread();
			qt.start();
		}
	}

	@Override
	public synchronized boolean isToning() {
		return (qt != null && qt.isAlive());
	}

	@Override
	public synchronized float getFrequencyToned() {
		return lastFreq;
	}

	@Override
	public synchronized float[] getFrequenciesToned() {
		return lastFreqs;
	}

	@Override
	public synchronized void finishToning() {
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
	public synchronized void stopToning() {
		if (qt != null) {
			qt.interrupt();
			qt = null;
		}
		q.clear();
	}

	@Override
	public synchronized void dial(char button, long duration, float amplitude) {
		q.add(new PCMDialtoneString(new char[]{button}, amplitude, duration));
		if (qt == null || !qt.isAlive()) {
			qt = new ToneGeneratorThread();
			qt.start();
		}
	}

	@Override
	public synchronized void dial(char[] buttons, long duration, float amplitude) {
		q.add(new PCMDialtoneString(buttons, amplitude, duration));
		if (qt == null || !qt.isAlive()) {
			qt = new ToneGeneratorThread();
			qt.start();
		}
	}

	@Override
	public synchronized void dial(String number, long duration, float amplitude) {
		q.add(new PCMDialtoneString(number, amplitude, duration));
		if (qt == null || !qt.isAlive()) {
			qt = new ToneGeneratorThread();
			qt.start();
		}
	}

	@Override
	public synchronized boolean isDialing() {
		return (qt != null && qt.isAlive());
	}

	@Override
	public synchronized char getButtonDialed() {
		return lastButton;
	}

	@Override
	public synchronized char[] getButtonsDialed() {
		return lastButtons;
	}

	@Override
	public synchronized String getNumberDialed() {
		return lastNumber;
	}

	@Override
	public synchronized void finishDialing() {
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
	public synchronized void stopDialing() {
		if (qt != null) {
			qt.interrupt();
			qt = null;
		}
		q.clear();
	}
	
	private class ToneGeneratorThread extends Thread {
		public void run() {
			while (!Thread.interrupted() && !q.isEmpty()) {
				Object o = q.remove(0);
				if (o instanceof PCMDialtoneString) {
					lastButtons = ((PCMDialtoneString)o).buttons();
					lastNumber = ((PCMDialtoneString)o).number();
					q.addAll(0, ((PCMDialtoneString)o).toList());
				}
				else if (o instanceof PCMDialtone) {
					lastFreq = ((PCMDialtone)o).frequency();
					lastFreqs = ((PCMDialtone)o).frequencies();
					lastButton = ((PCMDialtone)o).button();
					try {
						PCMTone.PlayerThread th = ((PCMDialtone)o).makePlayerThread();
						th.start();
						try { Thread.sleep(((PCMDialtone)o).duration() + 50); } catch (InterruptedException e) {}
						th.end();
						try { th.join(); } catch (InterruptedException e) {}
					} catch (LineUnavailableException e) {
						q.add(0, o);
					}
				}
				else if (o instanceof PCMTone) {
					lastFreq = ((PCMTone)o).frequency();
					lastFreqs = ((PCMTone)o).frequencies();
					try {
						PCMTone.PlayerThread th = ((PCMTone)o).makePlayerThread();
						th.start();
						try { Thread.sleep(((PCMTone)o).duration() + 50); } catch (InterruptedException e) {}
						th.end();
						try { th.join(); } catch (InterruptedException e) {}
					} catch (LineUnavailableException e) {
						q.add(0, o);
					}
				}
			}
		}
	}
	
	private static class PCMTone {
		private float[] freq;
		private float[] amp;
		private long dur;
		private int numTones;
		private long max;
		private byte[] stuff;
		private double[] m;
		public PCMTone(float[] freq, float[] amp, long dur) {
			this.freq = freq;
			this.amp = amp;
			this.dur = dur;
			numTones = Math.min(freq.length, amp.length);
			max = dur * 44100 / 1000;
			stuff = new byte[numTones * 2];
			m = new double[numTones];
			for (int n = 0; n < numTones; n++) {
				m[n] = 2 * Math.PI * freq[n] / 44100;
			}
		}
		public float frequency() {
			float f = 0;
			for (float fi : freq) f += fi;
			return f/freq.length;
		}
		public float[] frequencies() {
			return freq;
		}
		public long duration() {
			return dur;
		}
		public PlayerThread makePlayerThread() throws LineUnavailableException {
			return new PlayerThread();
		}
		public class PlayerThread extends Thread {
			private static final int ENV = 100;
			private AudioFormat af;
			private SourceDataLine dl;
			private boolean stop;
			public PlayerThread() throws LineUnavailableException {
				af = new AudioFormat(44100.0f, 16, numTones, true, false);
				dl = AudioSystem.getSourceDataLine(af);
				dl.open(af);
				stop = false;
			}
			public void run() {
				long i;
				int p, n;
				short s;
				dl.start();
				for (i = 0; i < max && !stop; i++) {
					p = 0;
					for (n = 0; n < numTones; n++) {
						s = (short)(Math.sin(i * m[n]) * amp[n]);
						if (i < ENV) s = (short)(s * i / ENV);
						if (i > max-ENV) s = (short)(s * (max-i) / ENV);
						stuff[p++] = (byte)(s & 0xFF);
						stuff[p++] = (byte)((s >>> 8) & 0xFF);
					}
					dl.write(stuff, 0, stuff.length);
				}
			}
			public void end() {
				stop = true;
				dl.stop();
				dl.flush();
				dl.close();
			}
		}
	}
	
	private static class PCMDialtone extends PCMTone {
		private static float[] dtmfFreq(char button) {
			switch (button) {
			case '0': return DTMF_0;
			case '1': return DTMF_1;
			case '2': return DTMF_2;
			case '3': return DTMF_3;
			case '4': return DTMF_4;
			case '5': return DTMF_5;
			case '6': return DTMF_6;
			case '7': return DTMF_7;
			case '8': return DTMF_8;
			case '9': return DTMF_9;
			case 'A': case 'a': return DTMF_A;
			case 'B': case 'b': return DTMF_B;
			case 'C': case 'c': return DTMF_C;
			case 'D': case 'd': return DTMF_D;
			case '*': return DTMF_STAR;
			case '#': return DTMF_POUND;
			default: return DTMF_NULL;
			}
		}
		private char button;
		public PCMDialtone(char button, float amp, long dur) {
			super(dtmfFreq(button), new float[]{amp,amp}, dur);
			this.button = button;
		}
		public char button() {
			return button;
		}
	}
	
	private static class PCMDialtoneString {
		private char[] buttons;
		private String number;
		private float amp;
		private long dur;
		public PCMDialtoneString(char[] buttons, float amp, long dur) {
			this.buttons = buttons;
			this.number = new String(buttons);
			this.amp = amp;
			this.dur = dur;
		}
		public PCMDialtoneString(String number, float amp, long dur) {
			number = number.replaceAll("[^0-9A-Da-d*#,]", "").toUpperCase();
			this.buttons = number.toCharArray();
			this.number = number;
			this.amp = amp;
			this.dur = dur;
		}
		public char[] buttons() {
			return buttons;
		}
		public String number() {
			return number;
		}
		public List<PCMDialtone> toList() {
			List<PCMDialtone> v = new Vector<PCMDialtone>();
			for (char b : buttons) {
				v.add(new PCMDialtone(b, amp, dur));
			}
			return v;
		}
	}
}
