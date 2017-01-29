/*
 * Copyright © 2014-2015 Rebecca G. Bettencourt / Kreative Software
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

jsPlayCommand = {
	VERSION: 2,
	BANK_SELECT_GM: 'gm',
	BANK_SELECT_GS: 'gs',
	BANK_SELECT_XG: 'xg',
	BANK_SELECT_MMA: 'mma',
	WHOLE_NOTE_DURATION: 256,
	/* EXTERNAL */
	start: function(success, failure) {
		if (navigator.requestMIDIAccess) {
			navigator.requestMIDIAccess().then(function(access) {
				jsPlayCommand._midiAccess = access;
				if (typeof access.outputs === 'function') {
					jsPlayCommand._defaultOutput = access.outputs()[0];
				} else if (typeof access.outputs.values === 'function') {
					jsPlayCommand._defaultOutput = access.outputs.values().next().value;
				} else {
					jsPlayCommand._defaultOutput = access.outputs[0];
				}
				if (success) success(access);
			}, function(error) {
				delete jsPlayCommand._midiAccess;
				delete jsPlayCommand._defaultOutput;
				if (failure) failure(error);
			});
		} else {
			if (failure) failure('navigator.requestMIDIAccess is undefined');
		}
	},
	running: function() {
		return !!jsPlayCommand._midiAccess;
	},
	stop: function() {
		delete jsPlayCommand._midiAccess;
		delete jsPlayCommand._defaultOutput;
	},
	outputs: function() {
		if (jsPlayCommand._midiAccess) {
			if (typeof jsPlayCommand._midiAccess.outputs === 'function') {
				return jsPlayCommand._midiAccess.outputs();
			} else if (typeof jsPlayCommand._midiAccess.outputs.values === 'function') {
				var i = jsPlayCommand._midiAccess.outputs.values();
				var a = [];
				for (var n = i.next(); !n.done; n = i.next()) {
					a.push(n.value);
				}
				return a;
			} else {
				return jsPlayCommand._midiAccess.outputs;
			}
		} else {
			return [];
		}
	},
	getDefaultOutput: function() {
		if (!jsPlayCommand._midiAccess) return null;
		if (jsPlayCommand._defaultOutput) {
			return jsPlayCommand._defaultOutput;
		} else if (typeof jsPlayCommand._midiAccess.outputs === 'function') {
			return jsPlayCommand._midiAccess.outputs()[0];
		} else if (typeof jsPlayCommand._midiAccess.outputs.values === 'function') {
			return jsPlayCommand._midiAccess.outputs.values().next().value;
		} else {
			return jsPlayCommand._midiAccess.outputs[0];
		}
	},
	setDefaultOutput: function(output) {
		jsPlayCommand._defaultOutput = output;
	},
	getDefaultBankSelect: function() {
		return jsPlayCommand._defaultBankSelect || jsPlayCommand.BANK_SELECT_GM;
	},
	setDefaultBankSelect: function(bankSelect) {
		jsPlayCommand._defaultBankSelect = bankSelect;
	},
	getDefaultChannel: function() {
		return jsPlayCommand._defaultChannel || 0;
	},
	setDefaultChannel: function(channel) {
		jsPlayCommand._defaultChannel = channel;
	},
	getDefaultInstrument: function() {
		return jsPlayCommand._defaultInstrument || 0;
	},
	setDefaultInstrument: function(instrument) {
		jsPlayCommand._defaultInstrument = instrument;
	},
	getDefaultTempo: function() {
		return jsPlayCommand._defaultTempo || 120;
	},
	setDefaultTempo: function(tempo) {
		jsPlayCommand._defaultTempo = tempo;
	},
	play: function() {
		if (!jsPlayCommand._midiAccess) return;
		var a = jsPlayCommand._parsePlayArguments(arguments);
		var b = window.performance.now();
		jsPlayCommand._playSequence(a.output, b, a.tickScale, a.sequence);
	},
	playMultiple: function() {
		if (!jsPlayCommand._midiAccess) return;
		var a = [];
		for (var i = 0; i < arguments.length; i++) {
			a.push(jsPlayCommand._parsePlayArguments(arguments[i]));
		}
		var b = window.performance.now();
		for (var i = 0; i < a.length; i++) {
			jsPlayCommand._playSequence(a[i].output, b, a[i].tickScale, a[i].sequence);
		}
	},
	compile: function() {
		var a = jsPlayCommand._parsePlayArguments(arguments);
		var header = jsPlayCommand._compileHeader(1);
		var trackName = (a.output && a.output.charCodeAt) ? a.output : null;
		var track = jsPlayCommand._compileSequence(trackName, a.midiTempo, a.sequence);
		header = header.concat(track);
		return header;
	},
	compileMultiple: function() {
		var a = [];
		for (var i = 0; i < arguments.length; i++) {
			a.push(jsPlayCommand._parsePlayArguments(arguments[i]));
		}
		var header = jsPlayCommand._compileHeader(a.length);
		for (var i = 0; i < a.length; i++) {
			var trackName = (a[i].output && a[i].output.charCodeAt) ? a[i].output : null;
			var track = jsPlayCommand._compileSequence(trackName, a[i].midiTempo, a[i].sequence);
			header = header.concat(track);
		}
		return header;
	},
	compileBase64: function() {
		var bytes = jsPlayCommand.compile.apply(this, arguments);
		var s = String.fromCharCode.apply(String, bytes);
		return btoa(s);
	},
	compileMultipleBase64: function() {
		var bytes = jsPlayCommand.compileMultiple.apply(this, arguments);
		var s = String.fromCharCode.apply(String, bytes);
		return btoa(s);
	},
	/* INTERNAL */
	_playSequence: function(output, tickBase, tickScale, sequence) {
		for (var i = 0; i < sequence.events.length; i++) {
			var event = sequence.events[i];
			output.send(event.command, tickBase + event.tick * tickScale);
		}
	},
	_compileHeader: function(trackCount) {
		var header = [
			0x4D, 0x54, 0x68, 0x64,
			0x00, 0x00, 0x00, 0x06,
			0x00, 0x01
		];
		header.push((trackCount >> 8) & 0xFF);
		header.push((trackCount >> 0) & 0xFF);
		header.push((jsPlayCommand.WHOLE_NOTE_DURATION >> 8) & 0xFF);
		header.push((jsPlayCommand.WHOLE_NOTE_DURATION >> 0) & 0xFF);
		return header;
	},
	_compileSequence: function(trackName, midiTempo, sequence) {
		var track = [];
		/* Promo String */
		var promo = 'Generated by jsPlayCommand';
		track.push(0x00);
		track.push(0xFF);
		track.push(0x01);
		track.push(promo.length);
		for (var i = 0; i < promo.length; i++) {
			track.push(promo.charCodeAt(i) & 0xFF);
		}
		/* Track Name String */
		if (trackName) {
			track.push(0x00);
			track.push(0xFF);
			track.push(0x03);
			track.push(trackName.length);
			for (var i = 0; i < trackName.length; i++) {
				track.push(trackName.charCodeAt(i) & 0xFF);
			}
		}
		/* Set Tempo */
		track.push(0x00);
		track.push(0xFF);
		track.push(0x51);
		track.push(0x03);
		track.push((midiTempo >> 16) & 0xFF);
		track.push((midiTempo >>  8) & 0xFF);
		track.push((midiTempo >>  0) & 0xFF);
		/* Events */
		var lastTick = 0;
		var lastCommand = 0;
		for (var i = 0; i < sequence.events.length; i++) {
			var event = sequence.events[i];
			var tick = Math.floor(event.tick * 4);
			jsPlayCommand._pushTimeStamp(track, tick - lastTick);
			if (event.command[0] != lastCommand) {
				track.push(event.command[0]);
			}
			for (var j = 1; j < event.command.length; j++) {
				track.push(event.command[j]);
			}
			lastTick = tick;
			lastCommand = event.command[0];
		}
		/* End of Track */
		var tick = Math.floor(sequence.tick * 4);
		jsPlayCommand._pushTimeStamp(track, tick - lastTick);
		track.push(0xFF);
		track.push(0x2F);
		track.push(0x00);
		/* Add Header */
		var header = [0x4D, 0x54, 0x72, 0x6B];
		header.push((track.length >> 24) & 0xFF);
		header.push((track.length >> 16) & 0xFF);
		header.push((track.length >>  8) & 0xFF);
		header.push((track.length >>  0) & 0xFF);
		/* Done */
		return header.concat(track);
	},
	_pushTimeStamp: function(track, timeStamp) {
		var encoded = [timeStamp & 0x7F];
		timeStamp >>>= 7;
		while (timeStamp) {
			encoded.push(0x80 | (timeStamp & 0x7F));
			timeStamp >>>= 7;
		}
		for (var i = encoded.length - 1; i >= 0; i--) {
			track.push(encoded[i]);
		}
	},
	_parsePlayArguments: function(args) {
		var output, bankSelect, channel, bank, instrument, tempo, notes;
		if (args.length >= 6) {
			output = args[0];
			bankSelect = args[1];
			channel = args[2];
			instrument = args[3];
			tempo = args[4];
			notes = args[5];
		} else if (args.length >= 3) {
			output = args[args.length - 6];
			bankSelect = args[args.length - 5];
			channel = args[args.length - 4];
			instrument = args[args.length - 3];
			tempo = args[args.length - 2];
			notes = args[args.length - 1];
		} else {
			instrument = args[0];
			notes = args[1];
		}
		if (!output) output = jsPlayCommand.getDefaultOutput();
		if (!bankSelect) bankSelect = jsPlayCommand.getDefaultBankSelect();
		if (isNaN(channel)) channel = jsPlayCommand.getDefaultChannel();
		if (!instrument) instrument = jsPlayCommand.getDefaultInstrument();
		if (!tempo) tempo = jsPlayCommand.getDefaultTempo();
		if (!notes) notes = 'cq';
		instrument = jsPlayCommand._parseInstrument(instrument);
		bank = instrument.bank;
		instrument = instrument.instrument;
		notes = jsPlayCommand._parseNotes(notes);
		var sequence = jsPlayCommand._createSequence(bankSelect, channel, 0, bank, instrument, notes);
		var tickScale = 240000 / (tempo * jsPlayCommand.WHOLE_NOTE_DURATION);
		var midiTempo = Math.floor(60000000 / tempo);
		return {
			output: output,
			sequence: sequence,
			tickScale: tickScale,
			midiTempo: midiTempo
		};
	},
	_createSequence: function(bankSelect, channel, tick, bank, instrument, notes) {
		var ie = jsPlayCommand._createInstrumentEvents(bankSelect, channel, tick, bank, instrument);
		var ne = jsPlayCommand._createNoteEvents(channel, tick, notes);
		return {
			events: ie.events.concat(ne.events),
			tick: ne.tick
		};
	},
	_createInstrumentEvents: function(bankSelect, channel, tick, bank, instrument) {
		var events = [];
		switch (bankSelect) {
			case jsPlayCommand.BANK_SELECT_GS:
				events.push({
					tick: tick,
					command: [0xB0 + (channel & 0xF), 0, bank & 0x7F]
				});
				break;
			case jsPlayCommand.BANK_SELECT_MMA:
				events.push({
					tick: tick,
					command: [0xB0 + (channel & 0xF), 0, (bank >> 7) & 0x7F]
				});
				/* continue; */
			case jsPlayCommand.BANK_SELECT_XG:
				events.push({
					tick: tick,
					command: [0xB0 + (channel & 0xF), 32, bank & 0x7F]
				});
				break;
		}
		events.push({
			tick: tick,
			command: [0xC0 + (channel & 0xF), instrument & 0x7F]
		});
		return {
			events: events,
			tick: tick
		};
	},
	_createNoteEvents: function() {
		var clamp = function(value) {
			if (value < 0) return 0;
			if (value > 127) return 127;
			return value;
		};
		var playDuration = function(note) {
			if (note.stoccato) return note.duration / 2;
			return note.duration;
		};
		return function(channel, tick, notes) {
			var events = [];
			for (var i = 0; i < notes.length; i++) {
				var note = notes[i];
				if (note.rest) {
					tick += note.duration;
				} else {
					if (!note.silent) {
						events.push({
							tick: tick,
							command: [0x90 + (channel & 0xF), clamp(note.pitch), clamp(note.velocity)]
						});
					}
					if (!note.fermata) {
						events.push({
							tick: tick + playDuration(note),
							command: [0x90 + (channel & 0xF), clamp(note.pitch), 0]
						});
					}
					if (!note.chord) {
						tick += note.duration;
					}
				}
			}
			return {
				events: events,
				tick: tick
			};
		};
	}(),
	_parseInstrument: function(i) {
		if (!isNaN(i)) {
			return { bank: ((i >> 7) & 0x3FFF), instrument: (i & 0x7F) };
		} else if (!isNaN(i.bank) && !isNaN(i.instrument)) {
			return { bank: (i.bank & 0x3FFF), instrument: (i.instrument & 0x7F) };
		} else if (i.replace && i.toLowerCase) {
			i = i.replace(/[^A-Za-z0-9]+/g, '').toLowerCase();
			switch (i) {
				case 'acousticgrandpiano': case 'acousticgrand': case 'accousticgrandpiano': case 'accousticgrand': return { bank: 0, instrument: 0 };
				case 'brightacousticpiano': case 'brightacoustic': case 'brightaccousticpiano': case 'brightaccoustic': return { bank: 0, instrument: 1 };
				case 'electricgrandpiano': case 'electricgrand': return { bank: 0, instrument: 2 };
				case 'honkytonkpiano': return { bank: 0, instrument: 3 };
				case 'electricpiano1': case 'rhodespiano': return { bank: 0, instrument: 4 };
				case 'electricpiano2': case 'chorusedpiano': return { bank: 0, instrument: 5 };
				case 'harpsichord': return { bank: 0, instrument: 6 };
				case 'clavi': case 'clavinet': return { bank: 0, instrument: 7 };
				case 'celesta': return { bank: 0, instrument: 8 };
				case 'glockenspiel': return { bank: 0, instrument: 9 };
				case 'musicbox': return { bank: 0, instrument: 10 };
				case 'vibraphone': return { bank: 0, instrument: 11 };
				case 'marimba': return { bank: 0, instrument: 12 };
				case 'xylophone': return { bank: 0, instrument: 13 };
				case 'tubularbells': return { bank: 0, instrument: 14 };
				case 'dulcimer': return { bank: 0, instrument: 15 };
				case 'drawbarorgan': case 'draworgan': return { bank: 0, instrument: 16 };
				case 'percussiveorgan': return { bank: 0, instrument: 17 };
				case 'rockorgan': return { bank: 0, instrument: 18 };
				case 'churchorgan': return { bank: 0, instrument: 19 };
				case 'reedorgan': return { bank: 0, instrument: 20 };
				case 'accordion': return { bank: 0, instrument: 21 };
				case 'harmonica': return { bank: 0, instrument: 22 };
				case 'tangoaccordion': return { bank: 0, instrument: 23 };
				case 'acousticguitarnylon': case 'acousticnylonguitar': case 'accousticguitarnylon': case 'accousticnylonguitar': return { bank: 0, instrument: 24 };
				case 'acousticguitarsteel': case 'acousticsteelguitar': case 'accousticguitarsteel': case 'accousticsteelguitar': return { bank: 0, instrument: 25 };
				case 'electricguitarjazz': case 'electricjazzguitar': return { bank: 0, instrument: 26 };
				case 'electricguitarclean': case 'electriccleanguitar': return { bank: 0, instrument: 27 };
				case 'electricguitarmuted': case 'electricmutedguitar': return { bank: 0, instrument: 28 };
				case 'overdrivenguitar': return { bank: 0, instrument: 29 };
				case 'distortionguitar': return { bank: 0, instrument: 30 };
				case 'guitarharmonics': return { bank: 0, instrument: 31 };
				case 'acousticbass': case 'accousticbass': case 'woodbass': return { bank: 0, instrument: 32 };
				case 'electricbassfinger': case 'electricbassfingered': return { bank: 0, instrument: 33 };
				case 'electricbasspick': case 'electricbasspicked': return { bank: 0, instrument: 34 };
				case 'fretlessbass': return { bank: 0, instrument: 35 };
				case 'slapbass1': return { bank: 0, instrument: 36 };
				case 'slapbass2': return { bank: 0, instrument: 37 };
				case 'synthbass1': return { bank: 0, instrument: 38 };
				case 'synthbass2': return { bank: 0, instrument: 39 };
				case 'violin': return { bank: 0, instrument: 40 };
				case 'viola': return { bank: 0, instrument: 41 };
				case 'cello': return { bank: 0, instrument: 42 };
				case 'contrabass': return { bank: 0, instrument: 43 };
				case 'tremolostrings': return { bank: 0, instrument: 44 };
				case 'pizzicatostrings': return { bank: 0, instrument: 45 };
				case 'orchestralharp': return { bank: 0, instrument: 46 };
				case 'timpani': return { bank: 0, instrument: 47 };
				case 'stringensemble1': case 'acousticstringensemble1': case 'accousticstringensemble1': return { bank: 0, instrument: 48 };
				case 'stringensemble2': case 'acousticstringensemble2': case 'accousticstringensemble2': return { bank: 0, instrument: 49 };
				case 'synthstrings1': return { bank: 0, instrument: 50 };
				case 'synthstrings2': return { bank: 0, instrument: 51 };
				case 'choiraahs': case 'aahchoir': return { bank: 0, instrument: 52 };
				case 'voiceoohs': case 'oohchoir': return { bank: 0, instrument: 53 };
				case 'synthvoice': case 'synvox': return { bank: 0, instrument: 54 };
				case 'orchestrahit': return { bank: 0, instrument: 55 };
				case 'trumpet': return { bank: 0, instrument: 56 };
				case 'trombone': return { bank: 0, instrument: 57 };
				case 'tuba': return { bank: 0, instrument: 58 };
				case 'mutedtrumpet': return { bank: 0, instrument: 59 };
				case 'frenchhorn': return { bank: 0, instrument: 60 };
				case 'brasssection': return { bank: 0, instrument: 61 };
				case 'synthbrass1': return { bank: 0, instrument: 62 };
				case 'synthbrass2': return { bank: 0, instrument: 63 };
				case 'sopranosax': case 'sopranosaxophone': return { bank: 0, instrument: 64 };
				case 'altosax': case 'altosaxophone': return { bank: 0, instrument: 65 };
				case 'tenorsax': case 'tenorsaxophone': return { bank: 0, instrument: 66 };
				case 'baritonesax': case 'baritonesaxophone': return { bank: 0, instrument: 67 };
				case 'oboe': return { bank: 0, instrument: 68 };
				case 'englishhorn': return { bank: 0, instrument: 69 };
				case 'bassoon': return { bank: 0, instrument: 70 };
				case 'clarinet': return { bank: 0, instrument: 71 };
				case 'piccolo': case 'piccollo': return { bank: 0, instrument: 72 };
				case 'flute': return { bank: 0, instrument: 73 };
				case 'recorder': return { bank: 0, instrument: 74 };
				case 'panflute': return { bank: 0, instrument: 75 };
				case 'blownbottle': case 'bottleblow': return { bank: 0, instrument: 76 };
				case 'shakuhachi': return { bank: 0, instrument: 77 };
				case 'whistle': return { bank: 0, instrument: 78 };
				case 'ocarina': return { bank: 0, instrument: 79 };
				case 'lead1': case 'synthlead1': case 'lead1square': case 'synthlead1square': case 'square': case 'lead1squarelead': case 'synthlead1squarelead': case 'squarelead': return { bank: 0, instrument: 80 };
				case 'lead2': case 'synthlead2': case 'lead2sawtooth': case 'synthlead2sawtooth': case 'sawtooth': case 'lead2sawlead': case 'synthlead2sawlead': case 'sawlead': return { bank: 0, instrument: 81 };
				case 'lead3': case 'synthlead3': case 'lead3calliope': case 'synthlead3calliope': case 'calliope': return { bank: 0, instrument: 82 };
				case 'lead4': case 'synthlead4': case 'lead4chiff': case 'synthlead4chiff': case 'chiff': case 'lead4chiffer': case 'synthlead4chiffer': case 'chiffer': return { bank: 0, instrument: 83 };
				case 'lead5': case 'synthlead5': case 'lead5charang': case 'synthlead5charang': case 'charang': return { bank: 0, instrument: 84 };
				case 'lead6': case 'synthlead6': case 'lead6voice': case 'synthlead6voice': case 'voice': return { bank: 0, instrument: 85 };
				case 'lead7': case 'synthlead7': case 'lead7fifths': case 'synthlead7fifths': case 'fifths': return { bank: 0, instrument: 86 };
				case 'lead8': case 'synthlead8': case 'lead8basslead': case 'synthlead8basslead': case 'basslead': return { bank: 0, instrument: 87 };
				case 'pad1': case 'synthpad1': case 'pad1newage': case 'synthpad1newage': case 'newage': return { bank: 0, instrument: 88 };
				case 'pad2': case 'synthpad2': case 'pad2warm': case 'synthpad2warm': case 'warm': return { bank: 0, instrument: 89 };
				case 'pad3': case 'synthpad3': case 'pad3polysynth': case 'synthpad3polysynth': case 'polysynth': return { bank: 0, instrument: 90 };
				case 'pad4': case 'synthpad4': case 'pad4choir': case 'synthpad4choir': case 'choir': return { bank: 0, instrument: 91 };
				case 'pad5': case 'synthpad5': case 'pad5bowed': case 'synthpad5bowed': case 'bowed': return { bank: 0, instrument: 92 };
				case 'pad6': case 'synthpad6': case 'pad6metallic': case 'synthpad6metallic': case 'metallic': return { bank: 0, instrument: 93 };
				case 'pad7': case 'synthpad7': case 'pad7halo': case 'synthpad7halo': case 'halo': return { bank: 0, instrument: 94 };
				case 'pad8': case 'synthpad8': case 'pad8sweep': case 'synthpad8sweep': case 'sweep': return { bank: 0, instrument: 95 };
				case 'fx1': case 'synthfx1': case 'fx1rain': case 'synthfx1rain': case 'rain': case 'fx1icerain': case 'synthfx1icerain': case 'icerain': return { bank: 0, instrument: 96 };
				case 'fx2': case 'synthfx2': case 'fx2soundtrack': case 'synthfx2soundtrack': case 'soundtrack': case 'fx2soundtracks': case 'synthfx2soundtracks': case 'soundtracks': return { bank: 0, instrument: 97 };
				case 'fx3': case 'synthfx3': case 'fx3crystal': case 'synthfx3crystal': case 'crystal': return { bank: 0, instrument: 98 };
				case 'fx4': case 'synthfx4': case 'fx4atmosphere': case 'synthfx4atmosphere': case 'atmosphere': return { bank: 0, instrument: 99 };
				case 'fx5': case 'synthfx5': case 'fx5brightness': case 'synthfx5brightness': case 'brightness': case 'fx5bright': case 'synthfx5bright': case 'bright': return { bank: 0, instrument: 100 };
				case 'fx6': case 'synthfx6': case 'fx6goblins': case 'synthfx6goblins': case 'goblins': case 'fx6goblin': case 'synthfx6goblin': case 'goblin': return { bank: 0, instrument: 101 };
				case 'fx7': case 'synthfx7': case 'fx7echoes': case 'synthfx7echoes': case 'echoes': return { bank: 0, instrument: 102 };
				case 'fx8': case 'synthfx8': case 'fx8scifi': case 'synthfx8scifi': case 'scifi': case 'fx8space': case 'synthfx8space': case 'space': return { bank: 0, instrument: 103 };
				case 'sitar': return { bank: 0, instrument: 104 };
				case 'banjo': return { bank: 0, instrument: 105 };
				case 'shamisen': return { bank: 0, instrument: 106 };
				case 'koto': return { bank: 0, instrument: 107 };
				case 'kalimba': return { bank: 0, instrument: 108 };
				case 'bagpipe': return { bank: 0, instrument: 109 };
				case 'fiddle': return { bank: 0, instrument: 110 };
				case 'shanai': return { bank: 0, instrument: 111 };
				case 'tinklebell': return { bank: 0, instrument: 112 };
				case 'agogo': return { bank: 0, instrument: 113 };
				case 'steeldrums': return { bank: 0, instrument: 114 };
				case 'woodblock': return { bank: 0, instrument: 115 };
				case 'taikodrum': return { bank: 0, instrument: 116 };
				case 'melodictom': return { bank: 0, instrument: 117 };
				case 'synthdrum': case 'synthtom': return { bank: 0, instrument: 118 };
				case 'reversecymbal': return { bank: 0, instrument: 119 };
				case 'guitarfretnoise': return { bank: 0, instrument: 120 };
				case 'breathnoise': return { bank: 0, instrument: 121 };
				case 'seashore': return { bank: 0, instrument: 122 };
				case 'birdtweet': return { bank: 0, instrument: 123 };
				case 'telephonering': return { bank: 0, instrument: 124 };
				case 'helicopter': return { bank: 0, instrument: 125 };
				case 'applause': return { bank: 0, instrument: 126 };
				case 'gunshot': return { bank: 0, instrument: 127 };
				case 'simplebeep': return { bank: 72, instrument: 0 };
				case 'clinkklank': case 'clinkclank': case 'klinkklank': return { bank: 72, instrument: 1 };
				case 'boing': return { bank: 72, instrument: 2 };
				case 'monkey': return { bank: 72, instrument: 3 };
				case 'quack': return { bank: 72, instrument: 4 };
				case 'droplet': return { bank: 72, instrument: 5 };
				case 'indigo': return { bank: 72, instrument: 6 };
				case 'wildeep': return { bank: 72, instrument: 7 };
				case 'sosumi': return { bank: 72, instrument: 8 };
				case 'moof': return { bank: 72, instrument: 9 };
				case 'hypsichord': return { bank: 73, instrument: 6 };
				case 'hyperboing': return { bank: 73, instrument: 103 };
				case 'hyperflute': return { bank: 73, instrument: 73 };
				case 'emajorchord': return { bank: 74, instrument: 0 };
				case 'bassdrum': return { bank: 74, instrument: 1 };
				case 'resonantbass': return { bank: 74, instrument: 2 };
				case 'bip': return { bank: 74, instrument: 3 };
				case 'powerguitar': return { bank: 74, instrument: 4 };
				case 'cartoonwinddown': return { bank: 74, instrument: 5 };
				case 'cartoonpop': return { bank: 74, instrument: 6 };
				case 'electricstablow': return { bank: 74, instrument: 7 };
				default: return { bank: 0, instrument: 0 };
			}
		} else {
			return { bank: 0, instrument: 0 };
		}
	},
	_parseNotes: function() {
		var isWhiteSpace = function(ch) {
			var chi = ch.charCodeAt(0);
			return chi <= 0x20 || (chi >= 0x7F && chi <= 0xA0) || /\s/.test(ch);
		};
		var isPitch = function(ch) {
			return ch == 'c' || ch == 'd' || ch == 'e' || ch == 'f' || ch == 'g' || ch == 'a' || ch == 'b';
		};
		var pitchValue = function(ch) {
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
		};
		var isAccidental = function(ch) {
			return ch == 'b' || ch == '#';
		};
		var accidentalValue = function(ch) {
			switch (ch) {
				case 'b': return -1;
				case '#': return +1;
				default: return 0;
			}
		};
		var isDigit = function(chi) {
			return chi >= 0x30 && chi <= 0x39;
		};
		var digitValue = function(chi) {
			return chi - 0x30;
		};
		var isNPM = function(ch) {
			return ch == 'n' || ch == 'p' || ch == 'm' || ch == '$';
		};
		var isDuration = function(ch) {
			return ch == 'z' || ch == 'l' || ch == 'i' || ch == 'u' || ch == 'w' || ch == 'h' || ch == 'q' || ch == 'e' || ch == 's' || ch == 't' || ch == 'x' || ch == 'o';
		};
		var durationValue = function(ch) {
			switch (ch) {
				case 'z': return jsPlayCommand.WHOLE_NOTE_DURATION * 8;
				case 'l': return jsPlayCommand.WHOLE_NOTE_DURATION * 6;
				case 'i': return jsPlayCommand.WHOLE_NOTE_DURATION * 4;
				case 'u': return jsPlayCommand.WHOLE_NOTE_DURATION * 2;
				case 'w': return jsPlayCommand.WHOLE_NOTE_DURATION;
				case 'h': return jsPlayCommand.WHOLE_NOTE_DURATION / 2;
				case 'q': return jsPlayCommand.WHOLE_NOTE_DURATION / 4;
				case 'e': return jsPlayCommand.WHOLE_NOTE_DURATION / 8;
				case 's': return jsPlayCommand.WHOLE_NOTE_DURATION / 16;
				case 't': return jsPlayCommand.WHOLE_NOTE_DURATION / 32;
				case 'x': return jsPlayCommand.WHOLE_NOTE_DURATION / 64;
				case 'o': return jsPlayCommand.WHOLE_NOTE_DURATION / 128;
				default: return 0;
			}
		};
		var isDurationModifier = function(ch) {
			var chi = ch.charCodeAt(0);
			return (chi >= 0x31 && chi <= 0x39) || ch == '.' || ch == ':';
		};
		var durationModifierValue = function(ch, dur) {
			if (ch == '.') return dur + (dur / 2);
			if (ch == ':') return dur * 2;
			var chi = ch.charCodeAt(0);
			if (chi >= 0x31 && chi <= 0x39) {
				return dur / (chi - 0x30);
			}
			return dur;
		};
		var isEffect = function(ch) {
			return ch == ',' || ch == '*' || ch == '!' || ch == '+';
		};
		return function(notes) {
			var step = 0;
			var octave = 4;
			var dur = jsPlayCommand.WHOLE_NOTE_DURATION / 4;
			var vel = 127;
			var notesOut = [];
			/* For Each Element In Note Array */
			var notesIn = [].concat(notes);
			for (var ai = 0; ai < notesIn.length; ai++) {
				/* For Each Word In Note String */
				var i = 0;
				var j = notesIn[ai].length;
				while (i < j && isWhiteSpace(notesIn[ai].charAt(i))) i++;
				while (i < j) {
					var wordStart = i;
					while (i < j && !isWhiteSpace(notesIn[ai].charAt(i))) i++;
					var wordEnd = i;
					while (i < j && isWhiteSpace(notesIn[ai].charAt(i))) i++;
					/* Parse Note */
					var noteIn = notesIn[ai].substring(wordStart, wordEnd).toLowerCase();
					var rest = noteIn.indexOf('r');
					if (rest >= 0) {
						noteIn = noteIn.substring(0, rest) + noteIn.substring(rest + 1);
						rest = true;
					} else {
						rest = false;
					}
					var p = 0;
					var n = noteIn.length;
					/* Pitch */
					if (!rest && p < n) {
						if (isPitch(noteIn.charAt(p))) {
							step = pitchValue(noteIn.charAt(p));
							p++;
							while (p < n && isAccidental(noteIn.charAt(p))) {
								step += accidentalValue(noteIn.charAt(p));
								p++;
							}
							if (p < n && isDigit(noteIn.charCodeAt(p))) {
								octave = digitValue(noteIn.charCodeAt(p));
								p++;
								if (octave == 0 && p < n && isDigit(noteIn.charCodeAt(p)) && digitValue(noteIn.charCodeAt(p)) == 0) {
									octave--;
									p++;
								}
							}
						} else if (isNPM(noteIn.charAt(p))) {
							var midivalue = 0;
							p++;
							while (p < n && isDigit(noteIn.charCodeAt(p))) {
								midivalue *= 10;
								midivalue += digitValue(noteIn.charCodeAt(p));
								p++;
							}
							step = midivalue % 12;
							octave = Math.floor(midivalue / 12) - 1;
						}
					}
					var pitch = octave * 12 + step + 12;
					if (pitch < 0) pitch = 0;
					if (pitch > 127) pitch = 127;
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
							while (p < n && isDigit(noteIn.charCodeAt(p))) {
								dur *= 10;
								dur += digitValue(noteIn.charCodeAt(p));
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
								while (p < n && isDigit(noteIn.charCodeAt(p))) {
									vel *= 10;
									vel += digitValue(noteIn.charCodeAt(p));
									p++;
								}
								if (vel < 0) vel = 0;
								if (vel > 127) vel = 127;
								break;
						}
					}
					/* Effect */
					if (!rest) {
						var stoccato = false;
						var fermata = false;
						var silent = false;
						var chord = false;
						while (p < n && isEffect(noteIn.charAt(p))) {
							switch (noteIn.charAt(p)) {
								case ',':
									stoccato = true;
									break;
								case '*':
									fermata = true;
									silent = false;
									break;
								case '!':
									fermata = false;
									silent = true;
									break;
								case '+':
									chord = true;
									break;
							}
							p++;
						}
						notesOut.push({
							rest: false,
							pitch: pitch,
							duration: dur,
							velocity: vel,
							stoccato: stoccato,
							fermata: fermata,
							silent: silent,
							chord: chord
						});
					} else {
						notesOut.push({
							rest: true,
							duration: dur
						});
					}
					/* End Parse Note */
				}
				/* End For Each Word In Note String */
			}
			/* End For Each Element In Note Array */
			return notesOut;
		};
	}()
};
