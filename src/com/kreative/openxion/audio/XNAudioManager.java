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

import com.kreative.openxion.util.XIONUtil;

public class XNAudioManager {
	private static XNAudioManager instance = null;
	public static final synchronized XNAudioManager instance() {
		if (instance == null) {
			instance = new XNAudioManager();
		}
		return instance;
	}
	
	private AudioDialer ad;
	private ModemDialer md;
	private Player[] p;
	private int pi;
	private Speaker s;
	private Toner[] t;
	private int ti;
	
	private XNAudioManager() {
		this.ad = new ToneGenerator();
		try {
			this.md = (ModemDialer)Class.forName("com.kreative.openxion.audio.RXTXDialer").newInstance();
		} catch (Throwable e1) {
			try {
				this.md = (ModemDialer)Class.forName("com.kreative.openxion.audio.CommDialer").newInstance();
			} catch (Throwable e2) {
				this.md = null;
			}
		}
		this.p = new Player[]{
				new MIDIPlayer(), new MIDIPlayer(), new MIDIPlayer(), new MIDIPlayer(),
				new MIDIPlayer(), new MIDIPlayer(), new MIDIPlayer(), new MIDIPlayer(),
		};
		this.pi = 0;
		try {
			if (XIONUtil.isMacOS()) {
				this.s = new MacSpeaker();
			} else {
				this.s = (Speaker)Class.forName("com.kreative.openxion.audio.JSAPISpeaker").newInstance();
			}
		} catch (Throwable e) {
			this.s = null;
		}
		this.t = new Toner[]{
				new ToneGenerator(), new ToneGenerator(), new ToneGenerator(), new ToneGenerator(),
				new ToneGenerator(), new ToneGenerator(), new ToneGenerator(), new ToneGenerator(),
		};
		this.ti = 0;
	}
	
	public XNAudioManager(AudioDialer ad, ModemDialer md, Player p, Speaker s, Toner t) {
		this.ad = ad;
		this.md = md;
		this.p = new Player[]{p};
		this.pi = 0;
		this.s = s;
		this.t = new Toner[]{t};
		this.ti = 0;
	}
	
	public XNAudioManager(AudioDialer ad, ModemDialer md, Player[] p, Speaker s, Toner[] t) {
		this.ad = ad;
		this.md = md;
		this.p = p;
		this.pi = 0;
		this.s = s;
		this.t = t;
		this.ti = 0;
	}
	
	public boolean supportsAudioDial() {
		return ad != null;
	}
	
	public boolean supportsModemDial() {
		return md != null;
	}
	
	public boolean supportsPlay() {
		return p != null;
	}
	
	public boolean supportsSpeak() {
		return s != null;
	}
	
	public boolean supportsTone() {
		return t != null;
	}
	
	public AudioDialer getAudioDialer() {
		return ad;
	}
	
	public ModemDialer getModemDialer() {
		return md;
	}
	
	public Player getPlayer() {
		return p[pi];
	}
	
	public Player getPlayer(int channel) {
		return p[channel];
	}
	
	public int getPlayerChannel() {
		return pi;
	}
	
	public int getPlayerChannels() {
		return p.length;
	}
	
	public void setPlayerChannel(int pi) {
		this.pi = Math.max(0, Math.min(pi, p.length-1));
	}
	
	public Speaker getSpeaker() { /* "public speaker" lol */
		return s;
	}
	
	public Toner getToner() {
		return t[ti];
	}
	
	public Toner getToner(int channel) {
		return t[channel];
	}
	
	public int getTonerChannel() {
		return ti;
	}
	
	public int getTonerChannels() {
		return t.length;
	}
	
	public void setTonerChannel(int ti) {
		this.ti = Math.max(0, Math.min(ti, t.length-1));
	}
}
