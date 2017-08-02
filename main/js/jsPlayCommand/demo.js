jsPlayCommandDemo = {
	parseVerbose: function() {
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
			var noteLetter = 'c';
			var noteAccidental = 0;
			var noteOctave = 4;
			var noteDuration = 'q';
			var noteColons = 0;
			var noteDots = 0;
			var noteTuplet = 1;
			var noteVelocity = 'fff';
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
							noteLetter = noteIn.charAt(p);
							noteAccidental = 0;
							p++;
							while (p < n && isAccidental(noteIn.charAt(p))) {
								noteAccidental += accidentalValue(noteIn.charAt(p));
								p++;
							}
							if (p < n && isDigit(noteIn.charCodeAt(p))) {
								noteOctave = digitValue(noteIn.charCodeAt(p));
								p++;
								if (noteOctave == 0 && p < n && isDigit(noteIn.charCodeAt(p)) && digitValue(noteIn.charCodeAt(p)) == 0) {
									noteOctave--;
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
							switch (midivalue % 12) {
								default: noteLetter = 'c'; noteAccidental = 0; break;
								case  1: noteLetter = 'c'; noteAccidental = 1; break;
								case  2: noteLetter = 'd'; noteAccidental = 0; break;
								case  3: noteLetter = 'd'; noteAccidental = 1; break;
								case  4: noteLetter = 'e'; noteAccidental = 0; break;
								case  5: noteLetter = 'f'; noteAccidental = 0; break;
								case  6: noteLetter = 'f'; noteAccidental = 1; break;
								case  7: noteLetter = 'g'; noteAccidental = 0; break;
								case  8: noteLetter = 'g'; noteAccidental = 1; break;
								case  9: noteLetter = 'a'; noteAccidental = 0; break;
								case 10: noteLetter = 'a'; noteAccidental = 1; break;
								case 11: noteLetter = 'b'; noteAccidental = 0; break;
							}
							noteOctave = Math.floor(midivalue / 12) - 1;
						}
					}
					/* Duration */
					if (p < n) {
						if (isDuration(noteIn.charAt(p))) {
							noteDuration = noteIn.charAt(p);
							noteColons = 0;
							noteDots = 0;
							noteTuplet = 1;
							p++;
							while (p < n && isDurationModifier(noteIn.charAt(p))) {
								switch (noteIn.charAt(p)) {
									case':': noteColons++; break;
									case'.': noteDots++; break;
									default: noteTuplet *= digitValue(noteIn.charCodeAt(p)); break;
								}
								p++;
							}
						} else if (noteIn.charAt(p) == 'd') {
							noteDuration = 0;
							noteColons = 0;
							noteDots = 0;
							noteTuplet = 1;
							p++;
							while (p < n && isDigit(noteIn.charCodeAt(p))) {
								noteDuration *= 10;
								noteDuration += digitValue(noteIn.charCodeAt(p));
								p++;
							}
						}
					}
					/* Velocity */
					if (!rest && p < n) {
						switch (noteIn.charAt(p)) {
							case 'p':
								noteVelocity = 'p';
								p++;
								while (p < n && noteIn.charAt(p) == 'p') {
									noteVelocity += 'p';
									p++;
								}
								break;
							case 'm':
								noteVelocity = 'm';
								p++;
								if (p < n) {
									if (noteIn.charAt(p) == 'p') {
										while (p < n && noteIn.charAt(p) == 'p') {
											noteVelocity += 'p';
											p++;
										}
									} else if (noteIn.charAt(p) == 'f') {
										while (p < n && noteIn.charAt(p) == 'f') {
											noteVelocity += 'f';
											p++;
										}
									}
								}
								break;
							case 'f':
								noteVelocity = 'f';
								p++;
								while (p < n && noteIn.charAt(p) == 'f') {
									noteVelocity += 'f';
									p++;
								}
								break;
							case 'v':
								noteVelocity = 0;
								p++;
								while (p < n && isDigit(noteIn.charCodeAt(p))) {
									noteVelocity *= 10;
									noteVelocity += digitValue(noteIn.charCodeAt(p));
									p++;
								}
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
							letter: noteLetter,
							accidental: noteAccidental,
							octave: noteOctave,
							duration: noteDuration,
							colons: noteColons,
							dots: noteDots,
							tuplet: noteTuplet,
							velocity: noteVelocity,
							stoccato: stoccato,
							fermata: fermata,
							silent: silent,
							chord: chord
						});
					} else {
						notesOut.push({
							rest: true,
							duration: noteDuration,
							colons: noteColons,
							dots: noteDots,
							tuplet: noteTuplet,
						});
					}
					/* End Parse Note */
				}
				/* End For Each Word In Note String */
			}
			/* End For Each Element In Note Array */
			return notesOut;
		};
	}(),
	generateNoteHTML: function(notes) {
		var staff = $('<div/>').addClass('staff');
		var lastVelocity = null;
		var chord = null;
		for (var i = 0; i < notes.length; i++) {
			var note = notes[i];
			var container = $('<div/>').addClass('note');
			if (note.rest || note.silent) {
				if (note.tuplet != 1) {
					container.append($('<div/>').addClass('note-tuplet').text(note.tuplet));
				}
				if (isNaN(note.duration)) {
					container.append($('<div/>').addClass('note-rest').addClass('note-rest-' + note.duration));
				} else {
					var numberContainer = $('<div/>').addClass('note-rest').addClass('note-rest-d');
					var dvalue = String(note.duration);
					for (var j = 0; j < dvalue.length; j++) {
						numberContainer.append($('<div/>').addClass('note-rest-digit').addClass('note-rest-digit-' + dvalue.charAt(j)));
					}
					container.append(numberContainer);
				}
				for (var j = 0; j < note.colons; j++) {
					container.append($('<div/>').addClass('note-rest-colon'));
				}
				for (var j = 0; j < note.dots; j++) {
					container.append($('<div/>').addClass('note-rest-dot'));
				}
			} else {
				if (note.velocity != lastVelocity) {
					var text = isNaN(note.velocity) ? note.velocity : ('v' + note.velocity);
					container.append($('<div/>').addClass('note-velocity').text(text));
					lastVelocity = note.velocity;
				}
				if (note.tuplet != 1) {
					container.append($('<div/>').addClass('note-tuplet').text(note.tuplet));
				}
				var position = (28 - (7 * note.octave + 'cdefgab'.indexOf(note.letter)));
				var innerContainer = $('<div/>').addClass('note-inner').addClass('note-inner-' + ((position & 1) ? 'odd' : 'even')).css('top', (position * 4) + 'px');
				if (note.accidental) {
					var a = note.accidental;
					while (a < 0) {
						innerContainer.append($('<div/>').addClass('note-flat'));
						a++;
					}
					while (a > 0) {
						innerContainer.append($('<div/>').addClass('note-sharp'));
						a--;
					}
				}
				var noteValue;
				if (isNaN(note.duration)) {
					noteValue = $('<div/>').addClass('note-value').addClass('note-value-' + note.duration);
				} else {
					noteValue = $('<div/>').addClass('note-value').addClass('note-value-d');
					var dvalue = String(note.duration);
					for (var j = 0; j < dvalue.length; j++) {
						noteValue.append($('<div/>').addClass('note-value-digit').addClass('note-value-digit-' + dvalue.charAt(j)));
					}
				}
				if (note.stoccato) {
					noteValue.append($('<div/>').addClass('note-stoccato'));
				}
				if (position <= -12 || position == 0 || position >= 12) {
					var ledger = $('<div/>').addClass('note-ledger-lines');
					if (position <= 0) {
						ledger.css('top', (position & 1) ? '60px' : '56px');
						ledger.css('height', position ? (((((-position - 12) &~1) * 4) + 1) + 'px') : '1px');
					} else {
						ledger.css('bottom', (position & 1) ? '59px' : '55px');
						ledger.css('height', ((((position - 12) &~1) * 4) + 1) + 'px');
					}
					noteValue.append(ledger);
				}
				innerContainer.append(noteValue);
				for (var j = 0; j < note.colons; j++) {
					innerContainer.append($('<div/>').addClass('note-colon'));
				}
				for (var j = 0; j < note.dots; j++) {
					innerContainer.append($('<div/>').addClass('note-dot'));
				}
				container.append(innerContainer);
				if (note.fermata) {
					container.append($('<div/>').addClass('note-fermata'));
				}
			}
			if (chord) {
				chord.append(container);
				if (!note.chord) {
					staff.append(chord);
					chord = null;
				}
			} else if (note.chord) {
				chord = $('<div/>').addClass('note-chord');
				chord.append(container);
			} else {
				staff.append(container);
			}
		}
		if (chord) {
			staff.append(chord);
		}
		return staff;
	},
	collapseChords: function(e) {
		e.find('.note-chord').each(function() {
			var offsets = [];
			var maxOffset = 0;
			var maxWidth = 0;
			var notes = $(this).find('.note');
			notes.each(function(i) {
				var inner = $(this).find('.note-value');
				var outer = $(this);
				if (inner.length) {
					offsets[i] = inner.offset().left - outer.offset().left;
					if (offsets[i] > maxOffset) maxOffset = offsets[i];
				} else {
					offsets[i] = 0;
				}
				var width = outer.width();
				if (width > maxWidth) maxWidth = width;
			});
			$(this).width(maxWidth + 8);
			notes.css('position', 'absolute');
			notes.css('top', '0');
			notes.each(function(i) {
				var left = maxOffset - offsets[i];
				$(this).css('left', left + 'px');
			});
		});
	},
	renderNoteString: function(inputString, outputElement) {
		var notes = jsPlayCommandDemo.parseVerbose(inputString);
		var staff = jsPlayCommandDemo.generateNoteHTML(notes);
		outputElement.find('.staff').replaceWith(staff);
		jsPlayCommandDemo.collapseChords(outputElement);
	},
	loadInstruments: function() {
		var instruments = [
			'Acoustic Grand Piano', 'Bright Acoustic Piano', 'Electric Grand Piano',
			'Honky-Tonk Piano', 'Electric Piano 1', 'Electric Piano 2', 'Harpsichord',
			'Clavi', 'Celesta', 'Glockenspiel', 'Music Box', 'Vibraphone', 'Marimba',
			'Xylophone', 'Tubular Bells', 'Dulcimer', 'Drawbar Organ', 'Percussive Organ',
			'Rock Organ', 'Church Organ', 'Reed Organ', 'Accordion', 'Harmonica',
			'Tango Accordion', 'Acoustic Guitar (Nylon)', 'Acoustic Guitar (Steel)',
			'Electric Guitar (Jazz)', 'Electric Guitar (Clean)', 'Electric Guitar (Muted)',
			'Overdriven Guitar', 'Distortion Guitar', 'Guitar Harmonics',
			'Acoustic Bass', 'Electric Bass (Finger)', 'Electric Bass (Pick)',
			'Fretless Bass', 'Slap Bass 1', 'Slap Bass 2', 'Synth Bass 1',
			'Synth Bass 2', 'Violin', 'Viola', 'Cello', 'Contrabass', 'Tremolo Strings',
			'Pizzicato Strings', 'Orchestral Harp', 'Timpani', 'String Ensemble 1',
			'String Ensemble 2', 'Synth Strings 1', 'Synth Strings 2', 'Choir Aahs',
			'Voice Oohs', 'Synth Voice', 'Orchestra Hit', 'Trumpet', 'Trombone',
			'Tuba', 'Muted Trumpet', 'French Horn', 'Brass Section', 'Synth Brass 1',
			'Synth Brass 2', 'Soprano Sax', 'Alto Sax', 'Tenor Sax', 'Baritone Sax',
			'Oboe', 'English Horn', 'Bassoon', 'Clarinet', 'Piccolo', 'Flute',
			'Recorder', 'Pan Flute', 'Blown Bottle', 'Shakuhachi', 'Whistle',
			'Ocarina', 'Lead 1 (Square)', 'Lead 2 (Sawtooth)', 'Lead 3 (Calliope)',
			'Lead 4 (Chiff)', 'Lead 5 (Charang)', 'Lead 6 (Voice)', 'Lead 7 (Fifths)',
			'Lead 8 (Bass + Lead)', 'Pad 1 (New Age)', 'Pad 2 (Warm)', 'Pad 3 (Polysynth)',
			'Pad 4 (Choir)', 'Pad 5 (Bowed)', 'Pad 6 (Metallic)', 'Pad 7 (Halo)',
			'Pad 8 (Sweep)', 'FX 1 (Rain)', 'FX 2 (Soundtrack)', 'FX 3 (Crystal)',
			'FX 4 (Atmosphere)', 'FX 5 (Brightness)', 'FX 6 (Goblins)', 'FX 7 (Echoes)',
			'FX 8 (Sci-Fi)', 'Sitar', 'Banjo', 'Shamisen', 'Koto', 'Kalimba',
			'Bagpipe', 'Fiddle', 'Shanai', 'Tinkle Bell', 'Agogo', 'Steel Drums',
			'Woodblock', 'Taiko Drum', 'Melodic Tom', 'Synth Drum', 'Reverse Cymbal',
			'Guitar Fret Noise', 'Breath Noise', 'Seashore', 'Bird Tweet',
			'Telephone Ring', 'Helicopter', 'Applause', 'Gunshot'
		];
		return function() {
			$('.instrument-selector').each(function() {
				if (!$(this).find('option').length) {
					for (var i = 0; i < instruments.length; i++) {
						var option = $('<option/>').attr('value', i).text(instruments[i]);
						if (!i) option.attr('selected', 'selected');
						$(this).append(option);
					}
				}
			});
		};
	}(),
	loadDevices: function() {
		$('.output-device-selector').each(function() {
			if (!$(this).find('option').length) {
				var devices = jsPlayCommand.outputs();
				for (var i = 0; i < devices.length; i++) {
					var option = $('<option/>').attr('value', i).text(devices[i].name);
					if (devices[i].name == jsPlayCommand.getDefaultOutput().name) option.attr('selected', 'selected');
					$(this).append(option);
				}
			}
		});
	},
	parseLines: function(panel) {
		var args = [];
		panel.find('.play-line').each(function() {
			var channel = $(this).find('.channel-input').val();
			var instrument = $(this).find('.instrument-selector').val();
			var tempo = $(this).find('.tempo-input').val();
			var notes = $(this).find('.note-input').val();
			if (notes && notes.trim()) {
				args.push([channel, instrument, tempo, notes]);
			}
		});
		return args;
	},
	playLines: function(panel) {
		var args = jsPlayCommandDemo.parseLines(panel);
		if (args.length) {
			jsPlayCommand.playMultiple.apply(this, args);
		}
	},
	downloadLines: function(panel) {
		panel.find('.download-link-temp').remove();
		var args = jsPlayCommandDemo.parseLines(panel);
		if (args.length) {
			var data = jsPlayCommand.compileMultipleBase64.apply(this, args);
			var dataURL = 'data:audio/mid;base64,' + data;
			var link = $('<a/>').attr(
				'download', 'output.mid').attr(
				'href', dataURL).attr(
				'target', '_blank').addClass(
				'download-link-temp').text(
				'Download MIDI File');
			var span = $('<span/>');
			span.append($('<span/>').text(' '));
			span.append(link);
			var button = panel.find('.download-button');
			button.after(span);
		}
	},
	addAChannel: function(panel) {
		var channels = panel.find('.play-line');
		var channelCount = channels.length;
		if (channelCount < 16) {
			var lastChannel = $(channels[channelCount - 1]);
			var newChannel = lastChannel.clone(true);
			lastChannel.after(newChannel);
			newChannel.find('.channel-input').val(channelCount);
		}
	},
	removeAChannel: function(panel) {
		var channels = panel.find('.play-line');
		var channelCount = channels.length;
		if (channelCount > 4) {
			var lastChannel = $(channels[channelCount - 1]);
			lastChannel.remove();
		}
	},
	loadMIDI: function() {
		console.log('Looking for MIDI devices...');
		$('.play-line-ctrl').hide();
		$('.play-line-ctrl.loading').show();
		jsPlayCommand.start(function() {
			console.log('Found MIDI devices!');
			$('.play-line-ctrl').hide();
			$('.play-line-ctrl.loaded').show();
			jsPlayCommandDemo.loadDevices();
			jsPlayCommand.setDefaultBankSelect(jsPlayCommand.BANK_SELECT_MMA);
			$('.output-device-selector').unbind('change');
			$('.output-device-selector').bind('change', function() {
				jsPlayCommand.setDefaultOutput(jsPlayCommand.outputs()[$(this).val()]);
			});
		}, function() {
			console.log('Could not get access to MIDI devices.');
			$('.play-line-ctrl').hide();
			$('.play-line-ctrl.error').show();
		});
	},
	loadPreset: function(panel, preset) {
		panel.find('.instrument-selector').val(0);
		panel.find('.tempo-input').val(120);
		panel.find('.note-input').val('');
		panel.find('.note-input').change();
		if (preset && preset.length) {
			var lines = panel.find('.play-line');
			for (var i = 0; i < preset.length; i++) {
				$(lines[i]).find('.instrument-selector').val(preset[i].instrument);
				$(lines[i]).find('.tempo-input').val(preset[i].tempo);
				$(lines[i]).find('.note-input').val(preset[i].notes);
				if (!i) $(lines[i]).find('.note-input').change();
			}
		}
	},
	loadPresets: function() {
		var presets = {
			'Clear': [],
			'Scales': [
				{ instrument: 0, tempo: 120, notes: 'c4 d e f g a b c5' },
				{ instrument: 0, tempo: 120, notes: 'c3 d e f g a b c4' },
				{ instrument: 0, tempo: 120, notes: 'c5 d e f g a b c6' },
				{ instrument: 0, tempo: 120, notes: 'c2 d e f g a b c3' },
			],
			'Public Domain': [
				{ instrument: 113, tempo: 200, notes: 'c4e c dq c f eh' },
				{ instrument: 115, tempo: 200, notes: 'c4e c dq c f eh' },
			],
			'Inigo\'s Song': [
				{ instrument: 6, tempo: 120, notes: 'c4 a3 f c4 a3 f c4 d c c c' },
				{ instrument: 10, tempo: 120, notes: 'c4 a3 f c4 a3 f c4 d c c c' },
				{ instrument: 0, tempo: 120, notes: 'c2h. h. h q q q' },
			],
			'iMarimba': [
				{ instrument: 12, tempo: 90, notes: 'bs g d5+ g g4 d5+ g e+ b d+ g g4 e5+ b d+ g g4 d5+ g qr'},
			],
			'Tune GS': [
				{ instrument: 44, tempo: 110, notes: 'f4e a4e f4e c5e f4e f5e e5s d5s c5s d5s c5s b4s a4s b4s a4s g4s f4e' },
			],
			'Elena': [
				{ instrument: 0, tempo: 200, notes: 'eq c e r e c e r c d e f g a g r e c e r e c e r e d c d ch r cq d e f g g g r g f e d eh r eq c e r e c e r e d c d cw' },
				{ instrument: 0, tempo: 200, notes: 'e2w c e g e c e c e g e c e c e c' },
			],
		};
		return function() {
			$('.play-line-panel').each(function() {
				var panel = $(this);
				var sel = panel.find('.preset-selector');
				if (!sel.find('option').length) {
					sel.append($('<option/>'));
					for (var name in presets) {
						var option = $('<option/>').attr('value', name).text(name);
						sel.append(option);
					}
				}
				sel.unbind('change');
				sel.val(null);
				sel.bind('change', function() {
					var name = $(this).val();
					if (name) {
						jsPlayCommandDemo.loadPreset(panel, presets[sel.val()]);
						sel.val(null);
					}
				});
			});
		};
	}(),
};
$(document).ready(function() {
	jsPlayCommandDemo.loadInstruments();
	$('.note-input').focus(function() {
		jsPlayCommandDemo.renderNoteString($(this).val(), $('.note-panel'));
	});
	$('.note-input').keydown(function() {
		jsPlayCommandDemo.renderNoteString($(this).val(), $('.note-panel'));
		$('.download-link-temp').remove();
	});
	$('.note-input').keyup(function() {
		jsPlayCommandDemo.renderNoteString($(this).val(), $('.note-panel'));
		$('.download-link-temp').remove();
	});
	$('.note-input').change(function() {
		jsPlayCommandDemo.renderNoteString($(this).val(), $('.note-panel'));
		$('.download-link-temp').remove();
	});
	$('.play-line-panel').each(function() {
		var panel = $(this);
		var button = panel.find('.play-button');
		button.unbind('click');
		button.bind('click', function() {
			jsPlayCommandDemo.playLines(panel);
		});
		button = panel.find('.download-button');
		button.unbind('click');
		button.bind('click', function() {
			jsPlayCommandDemo.downloadLines(panel);
		});
		button = panel.find('.add-channel-button');
		button.unbind('click');
		button.bind('click', function() {
			jsPlayCommandDemo.addAChannel(panel);
		});
		button = panel.find('.remove-channel-button');
		button.unbind('click');
		button.bind('click', function() {
			jsPlayCommandDemo.removeAChannel(panel);
		});
		button = panel.find('.reload-button');
		button.unbind('click');
		button.bind('click', function() {
			jsPlayCommandDemo.loadMIDI();
		});
	});
	jsPlayCommandDemo.loadMIDI();
	jsPlayCommandDemo.loadPresets();
});