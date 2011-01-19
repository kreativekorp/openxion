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

package com.kreative.openxion;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import com.kreative.openxion.ast.XNEmptyExpression;
import com.kreative.openxion.ast.XNExpression;
import com.kreative.openxion.ast.XNModifier;
import com.kreative.openxion.ast.XNNumberExpression;
import com.kreative.openxion.ast.XNStringExpression;
import com.kreative.openxion.audio.NoteParser;
import com.kreative.openxion.audio.XNAudioManager;
import com.kreative.openxion.xom.XOMVariant;
import com.kreative.openxion.xom.inst.XOMEmpty;
import com.kreative.openxion.xom.inst.XOMInteger;
import com.kreative.openxion.xom.inst.XOMNumber;
import com.kreative.openxion.xom.inst.XOMString;
import com.kreative.openxion.xom.type.XOMIntegerType;
import com.kreative.openxion.xom.type.XOMNumberType;

/**
 * XNAudioModule is the XNModule responsible for the
 * dial, play, speak, and tone commands and associated
 * functions and properties.
 * <p>
 * The commands, functions, and properties
 * provided by this module are not guaranteed to be in any
 * other particular XION implementation.
 * @since OpenXION 1.2
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class XNAudioModule extends XNModule {
	private static final long serialVersionUID = 33L;
	
	public static final String MODULE_NAME = "OpenXION Audio Module";
	public static final String MODULE_VERSION = "1.2";
	
	private static XNAudioModule instance = null;
	public static final synchronized XNAudioModule instance() {
		if (instance == null) {
			instance = new XNAudioModule(XNAudioManager.instance());
		}
		return instance;
	}
	
	private XNAudioManager mgr;
	private String dialingPort;
	private long dialingTime;
	private float dialingVolume;
	private float toneVolume;
	
	private XNAudioModule(XNAudioManager mgr) {
		super();
		
		this.mgr = mgr;
		this.dialingPort = (mgr.supportsModemDial() && mgr.getModemDialer().getSerialPorts().length > 0) ? mgr.getModemDialer().getSerialPorts()[0] : "";
		this.dialingTime = 3000;
		this.dialingVolume = mgr.supportsAudioDial() ? mgr.getAudioDialer().getMaximumAmplitude() : 0.0f;
		this.toneVolume = mgr.supportsTone() ? mgr.getToner().getMaximumAmplitude() : 0.0f;
		
		commandParsers.put("dial", p_dial);
		commandParsers.put("play", p_play);
		commandParsers.put("speak", p_speak);
		commandParsers.put("stop", p_stop);
		commandParsers.put("tone", p_tone);
		
		commands.put("dial", c_dial);
		commands.put("play", c_play);
		commands.put("speak", c_speak);
		commands.put("stop", c_stop);
		commands.put("tone", c_tone);
		
		functions.put("serialports", f_serialPorts);
		functions.put("sound", f_sound);
		functions.put("sounds", f_sounds);
		functions.put("speech", f_speech);
		functions.put("tone", f_tone);
		functions.put("voice", f_voice);
		functions.put("voices", f_voices);
		
		properties.put("dialingport", p_dialingPort);
		properties.put("dialingtime", p_dialingTime);
		properties.put("dialingvolume", p_dialingVolume);
		properties.put("soundchannel", p_soundChannel);
		properties.put("tonechannel", p_toneChannel);
		properties.put("tonevolume", p_toneVolume);
		
		versions.put("audiomodule", new Version(XNAudioModule.MODULE_NAME, XNAudioModule.MODULE_VERSION));
	}
	
	public XNAudioManager getAudioManager() {
		return mgr;
	}
	
	public void setAudioManager(XNAudioManager mgr) {
		this.mgr = mgr;
	}
	
	public String toString() {
		return "XNAudioModule";
	}
	
	private static XNExpression getTokenExpression(XNParser p, int n) {
		if (n < 1) {
			return new XNEmptyExpression(p.getSource(), 0, 0);
		} else {
			XNToken t = p.getToken(); n--;
			while (n > 0) {
				XNToken u = p.getToken(); n--;
				XNToken v = new XNToken(t.kind, t.image+" "+u.image, t.source, t.beginLine, t.beginColumn, u.endLine, u.endColumn);
				v.specialToken = t.specialToken;
				v.next = u.next;
				t = v;
			}
			return new XNStringExpression(t);
		}
	}
	
	private static XNExpression createTokenExpression(XNParser p, String s) {
		if (s == null || s.trim().length() == 0) {
			return new XNEmptyExpression(p.getSource(), 0, 0);
		} else {
			return new XNStringExpression(new XNToken(XNToken.ID, s, p.getSource(), 0, 0, 0, 0));
		}
	}
	
	private static String myDescribeCommand(String commandName, List<XNExpression> parameters) {
		String s = "";
		for (XNExpression p : parameters) {
			if (p instanceof XNEmptyExpression) {
				XNEmptyExpression ee = (XNEmptyExpression)p;
				if (ee.getBeginCol() != 0 || ee.getBeginLine() != 0) {
					s += " "+p.toString();
				}
			} else {
				s += " "+p.toString();
			}
		}
		return s.trim();
	}
	
	private static final CommandParser p_dial = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			HashSet<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add("with");
			List<XNExpression> following = new Vector<XNExpression>();
			following.add(p.getListExpression(myKeywords));
			if (p.lookToken(1).toString().equalsIgnoreCase("with")) {
				if (p.lookToken(2).toString().equalsIgnoreCase("modem")) {
					following.add(getTokenExpression(p, 2));
					if (p.lookListExpression(1, keywords)) {
						following.add(p.getListExpression(keywords));
					}
				} else {
					following.add(getTokenExpression(p, 1));
					following.add(p.getListExpression(keywords));
				}
			}
			return following;
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_play = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			List<XNExpression> following = new Vector<XNExpression>();
			following.add(p.getListExpression(keywords));
			if (p.lookToken(1).toString().equalsIgnoreCase("tempo")) {
				following.add(getTokenExpression(p, 1));
				following.add(p.getListExpression(keywords));
				following.add(p.getListExpression(keywords));
				while (p.lookListExpression(1, keywords)) {
					following.add(p.getListExpression(keywords));
				}
			} else if (p.lookListExpression(1, keywords)) {
				following.add(createTokenExpression(p, "tempo"));
				following.add(new XNNumberExpression(new XNToken(XNToken.NUMBER, "120", p.getSource(), 0, 0, 0, 0)));
				following.add(p.getListExpression(keywords));
				while (p.lookListExpression(1, keywords)) {
					following.add(p.getListExpression(keywords));
				}
			}
			return following;
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_speak = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			HashSet<String> myKeywords = new HashSet<String>();
			if (keywords != null) myKeywords.addAll(keywords);
			myKeywords.add("with");
			List<XNExpression> following = new Vector<XNExpression>();
			following.add(p.getListExpression(myKeywords));
			if (p.lookToken(1).toString().equalsIgnoreCase("with")) {
				if (p.lookToken(2).toString().equalsIgnoreCase("voice")) {
					following.add(getTokenExpression(p, 2));
					following.add(p.getListExpression(keywords));
				} else if (
					(
						p.lookToken(2).toString().equalsIgnoreCase("male")
						|| p.lookToken(2).toString().equalsIgnoreCase("female")
						|| p.lookToken(2).toString().equalsIgnoreCase("neuter")
					)
					&& p.lookToken(3).toString().equalsIgnoreCase("voice")
				) {
					following.add(getTokenExpression(p, 1));
					following.add(getTokenExpression(p, 1));
					following.add(getTokenExpression(p, 1));
				}
			}
			return following;
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_stop = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			List<XNExpression> following = new Vector<XNExpression>();
			if (
					p.lookToken(1).toString().equalsIgnoreCase("sound")
					|| p.lookToken(1).toString().equalsIgnoreCase("speech")
					|| p.lookToken(1).toString().equalsIgnoreCase("tone")
			) {
				following.add(getTokenExpression(p,1));
			} else {
				throw new XNParseError("sound, speech, or tone", p.getToken());
			}
			return following;
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private static final CommandParser p_tone = new CommandParser() {
		public List<XNExpression> parseCommand(String commandName, XNParser p, Collection<String> keywords) {
			List<XNExpression> following = new Vector<XNExpression>();
			following.add(p.getListExpression(keywords));
			if (p.lookToken(1).toString().equalsIgnoreCase("for")) {
				following.add(getTokenExpression(p, 1));
				following.add(p.getListExpression(keywords));
				if (
						p.lookToken(1).toString().equalsIgnoreCase("milliseconds") |
						p.lookToken(1).toString().equalsIgnoreCase("millisecond") |
						p.lookToken(1).toString().equalsIgnoreCase("millisecs") |
						p.lookToken(1).toString().equalsIgnoreCase("millisec") |
						p.lookToken(1).toString().equalsIgnoreCase("millis") |
						p.lookToken(1).toString().equalsIgnoreCase("milli") |
						p.lookToken(1).toString().equalsIgnoreCase("seconds") |
						p.lookToken(1).toString().equalsIgnoreCase("second") |
						p.lookToken(1).toString().equalsIgnoreCase("secs") |
						p.lookToken(1).toString().equalsIgnoreCase("sec") |
						p.lookToken(1).toString().equalsIgnoreCase("ticks") |
						p.lookToken(1).toString().equalsIgnoreCase("tick") |
						p.lookToken(1).toString().equalsIgnoreCase("minutes") |
						p.lookToken(1).toString().equalsIgnoreCase("minute") |
						p.lookToken(1).toString().equalsIgnoreCase("mins") |
						p.lookToken(1).toString().equalsIgnoreCase("min") |
						p.lookToken(1).toString().equalsIgnoreCase("hours") |
						p.lookToken(1).toString().equalsIgnoreCase("hour")
				) {
					following.add(getTokenExpression(p, 1));
				} else {
					following.add(createTokenExpression(p, "ticks"));
				}
			}
			return following;
		}
		public String describeCommand(String commandName, List<XNExpression> parameters) {
			return myDescribeCommand(commandName, parameters);
		}
	};
	
	private final Command c_dial = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.size() == 0 || parameters.size() > 3) {
				throw new XNScriptError("Can't understand arguments to dial");
			}
			if (parameters.size() > 1) {
				if (!mgr.supportsModemDial()) {
					return new XOMString("Dial not supported on this system");
				}
				String number = interp.evaluateExpression(parameters.get(0)).unwrap().toTextString(ctx);
				String command;
				if (parameters.size() > 2) {
					command = interp.evaluateExpression(parameters.get(2)).unwrap().toTextString(ctx);
				} else {
					command = "ATDT";
				}
				if (ctx.allow(XNSecurityKey.TELEPHONY, "Modem Command", command, "Number", number)) {
					mgr.getModemDialer().dial(dialingPort, command, number, 1000, dialingTime);
				} else {
					throw new XNScriptError("Security settings do not allow dial");
				}
			} else {
				if (!mgr.supportsAudioDial()) {
					return new XOMString("Dial not supported on this system");
				}
				String number = interp.evaluateExpression(parameters.get(0)).unwrap().toTextString(ctx);
				mgr.getAudioDialer().dial(number, 250, dialingVolume);
				mgr.getAudioDialer().finishDialing();
			}
			return null;
		}
	};
	
	private final Command c_play = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.size() == 0 || parameters.size() == 2 || parameters.size() == 3) {
				throw new XNScriptError("Can't understand arguments to play");
			} else if (!mgr.supportsPlay()) {
				return new XOMString("Play not supported on this system");
			}
			if (parameters.size() == 1) {
				String inst = interp.evaluateExpression(parameters.get(0)).unwrap().toTextString(ctx);
				if (inst.equalsIgnoreCase("stop")) {
					mgr.getPlayer().stopPlaying();
				} else {
					mgr.getPlayer().play(inst);
				}
			} else {
				String inst = interp.evaluateExpression(parameters.get(0)).unwrap().toTextString(ctx);
				float tempo = (float)XOMNumberType.instance.makeInstanceFrom(ctx, interp.evaluateExpression(parameters.get(2)).unwrap()).toDouble();
				StringBuffer notes = new StringBuffer();
				for (int i = 3; i < parameters.size(); i++) {
					notes.append(" ");
					notes.append(interp.evaluateExpression(parameters.get(i)).unwrap().toTextString(ctx).trim());
				}
				mgr.getPlayer().play(inst, tempo, new NoteParser().parseNotes(notes.toString()));
			}
			return null;
		}
	};
	
	private final Command c_speak = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.size() == 0 || parameters.size() == 2 || parameters.size() > 4) {
				throw new XNScriptError("Can't understand arguments to speak");
			} else if (!mgr.supportsSpeak()) {
				return new XOMString("Speak not supported on this system");
			}
			String phrase = interp.evaluateExpression(parameters.get(0)).unwrap().toTextString(ctx);
			String voice;
			if (parameters.size() == 3) {
				voice = interp.evaluateExpression(parameters.get(2)).unwrap().toTextString(ctx);
			} else if (parameters.size() == 4) {
				voice = interp.evaluateExpression(parameters.get(2)).unwrap().toTextString(ctx);
				if (voice.equalsIgnoreCase("male")) voice = mgr.getSpeaker().getMaleVoice();
				else if (voice.equalsIgnoreCase("female")) voice = mgr.getSpeaker().getFemaleVoice();
				else if (voice.equalsIgnoreCase("neuter")) voice = mgr.getSpeaker().getNeuterVoice();
				else voice = mgr.getSpeaker().getDefaultVoice();
			} else {
				voice = mgr.getSpeaker().getDefaultVoice();
			}
			mgr.getSpeaker().speak(voice, phrase);
			return null;
		}
	};
	
	private final Command c_stop = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.size() != 1) {
				throw new XNScriptError("Can't understand arguments to stop");
			}
			String what = interp.evaluateExpression(parameters.get(0)).unwrap().toTextString(ctx);
			if (what.equalsIgnoreCase("sound")) {
				if (mgr.supportsPlay()) {
					for (int i = 0; i < mgr.getPlayerChannels(); i++) {
						mgr.getPlayer(i).stopPlaying();
					}
				}
				return null;
			}
			else if (what.equalsIgnoreCase("speech")) {
				if (mgr.supportsSpeak()) {
					mgr.getSpeaker().stopSpeaking();
				}
				return null;
			}
			else if (what.equalsIgnoreCase("tone")) {
				if (mgr.supportsTone()) {
					for (int i = 0; i < mgr.getTonerChannels(); i++) {
						mgr.getToner(i).stopToning();
					}
				}
				return null;
			}
			else {
				throw new XNScriptError("Can't understand arguments to stop");
			}
		}
	};
	
	private final Command c_tone = new Command() {
		public XOMVariant executeCommand(XNInterpreter interp, XNContext ctx, String commandName, List<XNExpression> parameters) {
			if (parameters == null || parameters.size() == 0 || parameters.size() > 4) {
				throw new XNScriptError("Can't understand arguments to tone");
			}
			float f = (float)XOMNumberType.instance.makeInstanceFrom(ctx, interp.evaluateExpression(parameters.get(0)).unwrap()).toDouble();
			if (parameters.size() > 2) {
				double d = XOMNumberType.instance.makeInstanceFrom(ctx, interp.evaluateExpression(parameters.get(2))).toDouble();
				String type = (parameters.size() > 3) ? interp.evaluateExpression(parameters.get(3)).toTextString(ctx) : "ticks";
				type = type.toLowerCase();
				if (type.startsWith("sec")) {
					d *= 1000L;
				} else if (type.startsWith("milli")) {
					d *= 1L;
				} else if (type.startsWith("hour")) {
					d *= 3600000L;
				} else if (type.startsWith("min")) {
					d *= 60000L;
				} else {
					d *= 16.6666666666666666666666666666666667;
				}
				mgr.getToner().tone((long)d, toneVolume, f);
			} else {
				mgr.getToner().tone(1000, toneVolume, f);
			}
			return null;
		}
	};
	
	private static void assertEmptyParameter(String functionName, XOMVariant parameter) {
		if (!(parameter == null || parameter instanceof XOMEmpty)) {
			throw new XNScriptError("Can't understand arguments to "+functionName);
		}
	}
	
	private final Function f_serialPorts = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			if (!ctx.allow(XNSecurityKey.SYSTEM_INFO, "Function", functionName))
				throw new XNScriptError("Security settings do not allow serialPorts");
			if (mgr.supportsModemDial()) {
				StringBuffer s = new StringBuffer();
				String[] ports = mgr.getModemDialer().getSerialPorts();
				for (String port : ports) {
					s.append(ctx.getLineEnding());
					s.append(port);
				}
				if (s.length() > 0) {
					s.delete(0, ctx.getLineEnding().length());
				}
				return new XOMString(s.toString());
			} else {
				return XOMEmpty.EMPTY;
			}
		}
	};
	
	private final Function f_sound = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			if (mgr.supportsPlay()) {
				if (mgr.getPlayer().isPlaying()) {
					return new XOMString(mgr.getPlayer().getInstrumentPlayed());
				} else {
					return new XOMString("done");
				}
			} else {
				return new XOMString("done");
			}
		}
	};
	
	private final Function f_sounds = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			if (mgr.supportsPlay()) {
				StringBuffer s = new StringBuffer();
				String[] insts = mgr.getPlayer().getInstruments();
				for (String inst : insts) {
					s.append(ctx.getLineEnding());
					s.append(inst);
				}
				if (s.length() > 0) {
					s.delete(0, ctx.getLineEnding().length());
				}
				return new XOMString(s.toString());
			} else {
				return XOMEmpty.EMPTY;
			}
		}
	};
	
	private final Function f_speech = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			if (mgr.supportsSpeak()) {
				if (mgr.getSpeaker().isSpeaking()) {
					return new XOMString(mgr.getSpeaker().getPhraseSpoken());
				} else {
					return new XOMString("done");
				}
			} else {
				return new XOMString("done");
			}
		}
	};
	
	private final Function f_tone = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			if (mgr.supportsTone()) {
				if (mgr.getToner().isToning()) {
					return new XOMNumber(mgr.getToner().getFrequencyToned());
				} else {
					return new XOMString("done");
				}
			} else {
				return new XOMString("done");
			}
		}
	};
	
	private final Function f_voice = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			if (mgr.supportsSpeak()) {
				if (mgr.getSpeaker().isSpeaking()) {
					return new XOMString(mgr.getSpeaker().getVoiceSpoken());
				} else {
					return new XOMString("done");
				}
			} else {
				return new XOMString("done");
			}
		}
	};
	
	private final Function f_voices = new Function() {
		public XOMVariant evaluateFunction(XNContext ctx, String functionName, XNModifier modifier, XOMVariant parameter) {
			assertEmptyParameter(functionName, parameter);
			if (mgr.supportsSpeak()) {
				StringBuffer s = new StringBuffer();
				String[] voices = mgr.getSpeaker().getVoices();
				for (String voice : voices) {
					s.append(ctx.getLineEnding());
					s.append(voice);
				}
				if (s.length() > 0) {
					s.delete(0, ctx.getLineEnding().length());
				}
				return new XOMString(s.toString());
			} else {
				return XOMEmpty.EMPTY;
			}
		}
	};
	
	private final Property p_dialingVolume = new Property() {
		public boolean canGetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public boolean canSetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String propertyName) {
			if (mgr.supportsAudioDial()) {
				return new XOMNumber(dialingVolume * 7f / mgr.getAudioDialer().getMaximumAmplitude());
			} else {
				return XOMNumber.ZERO;
			}
		}
		public void setProperty(XNContext ctx, String propertyName, XOMVariant value) {
			if (mgr.supportsAudioDial()) {
				dialingVolume = (float)XOMNumberType.instance.makeInstanceFrom(ctx, value).toDouble() * mgr.getAudioDialer().getMaximumAmplitude() / 7f;
			}
		}
	};
	
	private final Property p_dialingTime = new Property() {
		public boolean canGetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public boolean canSetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String propertyName) {
			if (mgr.supportsModemDial()) {
				return new XOMInteger(dialingTime * 60L / 1000L);
			} else {
				return XOMInteger.ZERO;
			}
		}
		public void setProperty(XNContext ctx, String propertyName, XOMVariant value) {
			if (mgr.supportsModemDial()) {
				dialingTime = XOMIntegerType.instance.makeInstanceFrom(ctx, value).toLong() * 1000L / 60L;
			}
		}
	};
	
	private final Property p_dialingPort = new Property() {
		public boolean canGetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public boolean canSetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String propertyName) {
			if (!ctx.allow(XNSecurityKey.TELEPHONY, "Property", propertyName))
				throw new XNScriptError("Security settings do not allow dialingPort");
			if (mgr.supportsModemDial()) {
				return new XOMString(dialingPort);
			} else {
				return XOMEmpty.EMPTY;
			}
		}
		public void setProperty(XNContext ctx, String propertyName, XOMVariant value) {
			if (!ctx.allow(XNSecurityKey.TELEPHONY, "Property", propertyName))
				throw new XNScriptError("Security settings do not allow dialingPort");
			if (mgr.supportsModemDial()) {
				dialingPort = value.toTextString(ctx);
			}
		}
	};
	
	private final Property p_soundChannel = new Property() {
		public boolean canGetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public boolean canSetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String propertyName) {
			if (mgr.supportsPlay()) {
				return new XOMInteger(mgr.getPlayerChannel()+1);
			} else {
				return XOMInteger.ZERO;
			}
		}
		public void setProperty(XNContext ctx, String propertyName, XOMVariant value) {
			if (mgr.supportsPlay()) {
				mgr.setPlayerChannel(XOMIntegerType.instance.makeInstanceFrom(ctx, value).toInt()-1);
			}
		}
	};
	
	private final Property p_toneVolume = new Property() {
		public boolean canGetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public boolean canSetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String propertyName) {
			if (mgr.supportsTone()) {
				return new XOMNumber(toneVolume * 7f / mgr.getToner().getMaximumAmplitude());
			} else {
				return XOMNumber.ZERO;
			}
		}
		public void setProperty(XNContext ctx, String propertyName, XOMVariant value) {
			if (mgr.supportsTone()) {
				toneVolume = (float)XOMNumberType.instance.makeInstanceFrom(ctx, value).toDouble() * mgr.getToner().getMaximumAmplitude() / 7f;
			}
		}
	};
	
	private final Property p_toneChannel = new Property() {
		public boolean canGetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public boolean canSetProperty(XNContext ctx, String propertyName) {
			return true;
		}
		public XOMVariant getProperty(XNContext ctx, XNModifier modifier, String propertyName) {
			if (mgr.supportsTone()) {
				return new XOMInteger(mgr.getTonerChannel()+1);
			} else {
				return XOMInteger.ZERO;
			}
		}
		public void setProperty(XNContext ctx, String propertyName, XOMVariant value) {
			if (mgr.supportsTone()) {
				mgr.setTonerChannel(XOMIntegerType.instance.makeInstanceFrom(ctx, value).toInt()-1);
			}
		}
	};
}
