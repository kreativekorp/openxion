/*
 * Copyright © 2014-2015 Rebecca G. Bettencourt / Kreative Software
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

jsChunkEx = {
	VERSION: 2,
	/* CHUNK TYPES */
	CHARACTER: 'character',
	LINE: 'line',
	ITEM: 'item',
	COLUMN: 'column',
	ROW: 'row',
	WORD: 'word',
	SENTENCE: 'sentence',
	PARAGRAPH: 'paragraph',
	/* OPERATIONS */
	LINE_ENDING: 'lineEnding',
	ITEM_DELIMITER: 'itemDelimiter',
	COLUMN_DELIMITER: 'columnDelimiter',
	ROW_DELIMITER: 'rowDelimiter',
	/* DEFAULT DELIMITERS */
	DEFAULT_LINE_ENDING: '\n',
	DEFAULT_ITEM_DELIMITER: ',',
	DEFAULT_COLUMN_DELIMITER: '\uFFF0',
	DEFAULT_ROW_DELIMITER: '\uFFF1',
	/* SPECIAL VALUES */
	ANY: 'any',
	FIRST: 'first',
	MIDDLE: 'middle',
	LAST: 'last',
	BY_CONTENT: 'byContent',
	/* EXTERNAL API */
	countChunks: function() {
		if (arguments.length < 2) return 0;
		var s = arguments[0];
		var ei = s.length;
		var si = 0;
		var e = jsChunkEx._parseArguments(arguments, 1, arguments.length - 1, true);
		var t = arguments[arguments.length - 1];
		for (var i = 0; i < e.length - 1; i++) {
			var se = jsChunkEx._resolveChunk(s, si, ei, e[i], false, false);
			if (!se.location) return 0;
			ei = Math.min(ei, se.location.endIndex);
			si = Math.max(si, se.location.startIndex);
		}
		var nl = e[e.length - 1].lineEnding;
		var id = e[e.length - 1].itemDelimiter;
		var cd = e[e.length - 1].columnDelimiter;
		var rd = e[e.length - 1].rowDelimiter;
		var def = jsChunkEx._getChunkDef(t, nl, id, cd, rd);
		return jsChunkEx._countChunks(s, si, ei, def);
	},
	splitChunks: function() {
		if (arguments.length < 2) return [];
		var s = arguments[0];
		var ei = s.length;
		var si = 0;
		var e = jsChunkEx._parseArguments(arguments, 1, arguments.length - 1, true);
		var t = arguments[arguments.length - 1];
		for (var i = 0; i < e.length - 1; i++) {
			var se = jsChunkEx._resolveChunk(s, si, ei, e[i], false, false);
			if (!se.location) return [];
			ei = Math.min(ei, se.location.endIndex);
			si = Math.max(si, se.location.startIndex);
		}
		var nl = e[e.length - 1].lineEnding;
		var id = e[e.length - 1].itemDelimiter;
		var cd = e[e.length - 1].columnDelimiter;
		var rd = e[e.length - 1].rowDelimiter;
		var def = jsChunkEx._getChunkDef(t, nl, id, cd, rd);
		return jsChunkEx._splitChunks(s, si, ei, def);
	},
	findChunk: function() {
		if (arguments.length < 1) return null;
		var s = arguments[0];
		var ei = s.length;
		var si = 0;
		var e = jsChunkEx._parseArguments(arguments, 1, arguments.length, false);
		for (var i = 0; i < e.length; i++) {
			var se = jsChunkEx._resolveChunk(s, si, ei, e[i], false, false);
			if (!se.location) return null;
			ei = Math.min(ei, se.location.endIndex);
			si = Math.max(si, se.location.startIndex);
		}
		return {
			startIndex: si,
			endIndex: ei
		};
	},
	findChunkToDelete: function() {
		if (arguments.length < 1) return null;
		var s = arguments[0];
		var ei = s.length;
		var si = 0;
		var e = jsChunkEx._parseArguments(arguments, 1, arguments.length, false);
		for (var i = 0; i < e.length; i++) {
			var se = jsChunkEx._resolveChunk(s, si, ei, e[i], false, false);
			if (!se.location) return null;
			ei = Math.min(ei, ((i < e.length - 1) ? se.location.endIndex : se.location.deleteEndIndex));
			si = Math.max(si, se.location.startIndex);
		}
		return {
			startIndex: si,
			endIndex: ei
		};
	},
	getChunk: function() {
		if (arguments.length < 1) return '';
		var s = arguments[0];
		var ei = s.length;
		var si = 0;
		var e = jsChunkEx._parseArguments(arguments, 1, arguments.length, false);
		for (var i = 0; i < e.length; i++) {
			var se = jsChunkEx._resolveChunk(s, si, ei, e[i], false, false);
			if (!se.location) return '';
			ei = Math.min(ei, se.location.endIndex);
			si = Math.max(si, se.location.startIndex);
		}
		return s.substring(si, ei);
	},
	deleteChunk: function() {
		if (arguments.length < 1) return '';
		var s = arguments[0];
		var ei = s.length;
		var si = 0;
		var e = jsChunkEx._parseArguments(arguments, 1, arguments.length, false);
		for (var i = 0; i < e.length; i++) {
			var se = jsChunkEx._resolveChunk(s, si, ei, e[i], false, false);
			if (!se.location) return s;
			ei = Math.min(ei, ((i < e.length - 1) ? se.location.endIndex : se.location.deleteEndIndex));
			si = Math.max(si, se.location.startIndex);
		}
		return s.substring(0, si) + s.substring(ei);
	},
	replaceChunk: function() {
		if (arguments.length < 2) return '';
		var s = arguments[0];
		var e = jsChunkEx._parseArguments(arguments, 1, arguments.length - 1, false);
		var r = arguments[arguments.length - 1];
		return jsChunkEx._replaceChunk(s, 0, s.length, e, 0, r);
	},
	prependToChunk: function() {
		if (arguments.length < 2) return '';
		var s = arguments[0];
		var e = jsChunkEx._parseArguments(arguments, 1, arguments.length - 1, false);
		var r = arguments[arguments.length - 1];
		return jsChunkEx._prependToChunk(s, 0, s.length, e, 0, r);
	},
	appendToChunk: function() {
		if (arguments.length < 2) return '';
		var s = arguments[0];
		var e = jsChunkEx._parseArguments(arguments, 1, arguments.length - 1, false);
		var r = arguments[arguments.length - 1];
		return jsChunkEx._appendToChunk(s, 0, s.length, e, 0, r);
	},
	resolveChunk: function(s, i, j, type, start, end, lineEnding, itemDelimiter, columnDelimiter, rowDelimiter, forPrepend, forAppend) {
		if (jsChunkEx._isStartEndArgument(start) && jsChunkEx._isStartEndArgument(end)) {
			return jsChunkEx._resolveChunk(s, i, j, {
				type: type,
				byStartEnd: true,
				start: start,
				end: end,
				lineEnding: lineEnding,
				itemDelimiter: itemDelimiter,
				columnDelimiter: columnDelimiter,
				rowDelimiter: rowDelimiter
			}, forPrepend, forAppend);
		} else if (jsChunkEx._isByContentArgument(start)) {
			return jsChunkEx._resolveChunk(s, i, j, {
				type: type,
				byContent: true,
				content: end,
				lineEnding: lineEnding,
				itemDelimiter: itemDelimiter,
				columnDelimiter: columnDelimiter,
				rowDelimiter: rowDelimiter
			}, forPrepend, forAppend);
		} else {
			throw new Error('Expected numbers but found "' + start + '", "' + end + '".');
		}
	},
	/* INTERNAL */
	_isStartEndArgument: function(a) {
		return (!isNaN(a)) || (a == jsChunkEx.ANY) || (a == jsChunkEx.FIRST) || (a == jsChunkEx.MIDDLE) || (a == jsChunkEx.LAST);
	},
	_isByContentArgument: function(a) {
		return (a == jsChunkEx.BY_CONTENT);
	},
	_parseArguments: function(a, i, j, appendDelimiters) {
		var e = [];
		var nl = jsChunkEx.DEFAULT_LINE_ENDING;
		var id = jsChunkEx.DEFAULT_ITEM_DELIMITER;
		var cd = jsChunkEx.DEFAULT_COLUMN_DELIMITER;
		var rd = jsChunkEx.DEFAULT_ROW_DELIMITER;
		while (i < j) {
			switch (a[i]) {
				case jsChunkEx.CHARACTER:
				case jsChunkEx.LINE:
				case jsChunkEx.ITEM:
				case jsChunkEx.COLUMN:
				case jsChunkEx.ROW:
				case jsChunkEx.WORD:
				case jsChunkEx.SENTENCE:
				case jsChunkEx.PARAGRAPH:
					var type = a[i++];
					if (i < j) {
						if (jsChunkEx._isStartEndArgument(a[i])) {
							var start = a[i++];
							var end = (i < j && jsChunkEx._isStartEndArgument(a[i])) ? a[i++] : start;
							e.push({
								type: type,
								byStartEnd: true,
								start: start,
								end: end,
								lineEnding: nl,
								itemDelimiter: id,
								columnDelimiter: cd,
								rowDelimiter: rd
							});
						} else if (jsChunkEx._isByContentArgument(a[i])) {
							i++;
							if (i < j) {
								var content = a[i++];
								e.push({
									type: type,
									byContent: true,
									content: content,
									lineEnding: nl,
									itemDelimiter: id,
									columnDelimiter: cd,
									rowDelimiter: rd
								});
							} else {
								throw new Error('Expected string for argument ' + i + ' but found end of arguments.');
							}
						} else {
							throw new Error('Expected number for argument ' + i + ' but found "' + a[i] + '".');
						}
					} else {
						throw new Error('Expected number for argument ' + i + ' but found end of arguments.');
					}
					break;
				case jsChunkEx.LINE_ENDING:
					i++;
					if (i < j) {
						nl = a[i++];
					} else {
						throw new Error('Expected string for argument ' + i + ' but found end of arguments.');
					}
					break;
				case jsChunkEx.ITEM_DELIMITER:
					i++;
					if (i < j) {
						id = a[i++];
					} else {
						throw new Error('Expected string for argument ' + i + ' but found end of arguments.');
					}
					break;
				case jsChunkEx.COLUMN_DELIMITER:
					i++;
					if (i < j) {
						cd = a[i++];
					} else {
						throw new Error('Expected string for argument ' + i + ' but found end of arguments.');
					}
					break;
				case jsChunkEx.ROW_DELIMITER:
					i++;
					if (i < j) {
						rd = a[i++];
					} else {
						throw new Error('Expected string for argument ' + i + ' but found end of arguments.');
					}
					break;
				default:
					throw new Error('Expected chunk type for argument ' + i + ' but found "' + a[i] + '".');
			}
		}
		if (appendDelimiters) {
			e.push({
				lineEnding: nl,
				itemDelimiter: id,
				columnDelimiter: cd,
				rowDelimiter: rd
			});
		}
		return e;
	},
	_resolveChunk: function(s, i, j, e, forPrepend, forAppend) {
		/* Get Context */
		var nl = e.lineEnding;
		var id = e.itemDelimiter;
		var cd = e.columnDelimiter;
		var rd = e.rowDelimiter;
		var def = jsChunkEx._getChunkDef(e.type, nl, id, cd, rd);
		if (e.byStartEnd) {
			/* Calculate Chunk Index Values */
			var n = jsChunkEx._countChunks(s, i, j, def);
			if (e.start == jsChunkEx.ANY) {
				e.start = 1 + Math.floor(Math.random() * n);
				if (e.end == jsChunkEx.ANY) {
					e.end = e.start;
				}
			} else if (e.end == jsChunkEx.ANY) {
				e.end = 1 + Math.floor(Math.random() * n);
			}
			if (e.start == jsChunkEx.FIRST) e.start = 1;
			if (e.end == jsChunkEx.FIRST) e.end = 1;
			if (e.start == jsChunkEx.MIDDLE) e.start = 1 + Math.floor(n / 2);
			if (e.end == jsChunkEx.MIDDLE) e.end = 1 + Math.floor(n / 2);
			if (e.start == jsChunkEx.LAST) e.start = n;
			if (e.end == jsChunkEx.LAST) e.end = n;
			if (e.start < 0) e.start += n + 1;
			if (e.end < 0) e.end += n + 1;
			/* If Chunk Out of Range, Extend String */
			if (def.delimited) {
				if ((forPrepend && e.start > n) || (forAppend && e.end > n)) {
					var a = '';
					var m = ((forPrepend && forAppend) ? Math.max(e.start, e.end) : forPrepend ? e.start : forAppend ? e.end : n) - n;
					if (n == 0) { m--; n++; }
					while (m-- > 0) { a += def.delimiter; n++; }
					s = s.substring(0, j) + a + s.substring(j);
					j += a.length;
					e.stringToAppend = a;
				}
				if ((forPrepend && e.start < 1) || (forAppend && e.end < 1)) {
					var a = '';
					var m = 1 - ((forPrepend && forAppend) ? Math.min(e.start, e.end) : forPrepend ? e.start : forAppend ? e.end : 1);
					while (m-- > 0) { a += def.delimiter; n++; e.start++; e.end++; }
					s = s.substring(0, i) + a + s.substring(i);
					j += a.length;
					e.stringToPrepend = a;
				}
			}
			/* Find the Chunk */
			e.sourceString = s;
			e.location = jsChunkEx._findChunk(s, i, j, def, e.start, e.end);
		} else if (e.byContent) {
			/* Find the Chunk */
			e.sourceString = s;
			e.location = jsChunkEx._findChunkByContent(s, i, j, def, e.content);
		}
		return e;
	},
	_replaceChunk: function(s, i, j, e, k, r) {
		if (k < e.length) {
			var ee = jsChunkEx._resolveChunk(s, i, j, e[k], true, true);
			if (!ee.location) throw new Error('There is no ' + ee.type + ' "' + ee.content + '".');
			return jsChunkEx._replaceChunk(ee.sourceString, ee.location.startIndex, ee.location.endIndex, e, k + 1, r);
		} else {
			return s.substring(0, i) + r + s.substring(j);
		}
	},
	_prependToChunk: function(s, i, j, e, k, r) {
		if (k < e.length) {
			var ee = jsChunkEx._resolveChunk(s, i, j, e[k], true, false);
			if (!ee.location) throw new Error('There is no ' + ee.type + ' "' + ee.content + '".');
			return jsChunkEx._prependToChunk(ee.sourceString, ee.location.startIndex, ee.location.endIndex, e, k + 1, r);
		} else {
			return s.substring(0, i) + r + s.substring(i);
		}
	},
	_appendToChunk: function(s, i, j, e, k, r) {
		if (k < e.length) {
			var ee = jsChunkEx._resolveChunk(s, i, j, e[k], false, true);
			if (!ee.location) throw new Error('There is no ' + ee.type + ' "' + ee.content + '".');
			return jsChunkEx._appendToChunk(ee.sourceString, ee.location.startIndex, ee.location.endIndex, e, k + 1, r);
		} else {
			return s.substring(0, j) + r + s.substring(j);
		}
	},
	_wrapForPrototype: function(f) {
		return function() {
			var a = Array.prototype.slice.call(arguments);
			return f.apply(this, [this].concat(a));
		};
	},
	/* CHUNK CALCULATIONS */
	_getChunkDef: function(t, nl, id, cd, rd) {
		switch (t) {
			case jsChunkEx.CHARACTER: return jsChunkEx._chunkDefs.characterDef;
			case jsChunkEx.LINE: return jsChunkEx._chunkDefs.lineDef(nl);
			case jsChunkEx.ITEM: return jsChunkEx._chunkDefs.delimiterDef(id);
			case jsChunkEx.COLUMN: return jsChunkEx._chunkDefs.delimiterDef(cd);
			case jsChunkEx.ROW: return jsChunkEx._chunkDefs.delimiterDef(rd);
			case jsChunkEx.WORD: return jsChunkEx._chunkDefs.wordDef;
			case jsChunkEx.SENTENCE: return jsChunkEx._chunkDefs.sentenceDef;
			case jsChunkEx.PARAGRAPH: return jsChunkEx._chunkDefs.paragraphDef;
			default: throw new Error('Expected chunk type but found "' + t + '".');
		}
	},
	_countChunks: function(s, i, j, def) {
		var n = 0;
		i = def.findFirst(s, i, j);
		while (i < j) {
			n++;
			i = def.findEnd(s, i, j);
			i = def.findNext(s, i, j);
		}
		return n;
	},
	_splitChunks: function(s, i, j, def) {
		var r = [];
		var n = 0;
		i = def.findFirst(s, i, j);
		while (i < j) {
			n++;
			var o = def.findEnd(s, i, j);
			var u = def.findNext(s, o, j);
			r.push({
				content: s.substring(i, o),
				chunkIndex: n,
				startIndex: i,
				endIndex: o,
				deleteEndIndex: u
			});
			i = u;
		}
		return r;
	},
	_findChunk: function(s, i, j, def, ws, we) {
		var si = ((ws < 1) ? i : undefined);
		var ei = ((we < 1) ? i : undefined);
		var di = ((we < 0) ? i : undefined);
		if (si == undefined || ei == undefined || di == undefined) {
			var n = 0;
			i = def.findFirst(s, i, j);
			while (i < j) {
				if (n == we) {
					di = i;
					if (si != undefined && ei != undefined) break;
				}
				n++;
				if (n == ws) {
					si = i;
					if (ei != undefined && di != undefined) break;
				}
				i = def.findEnd(s, i, j);
				if (n == we) {
					ei = i;
					if (si != undefined && di != undefined) break;
				}
				i = def.findNext(s, i, j);
			}
			if (si == undefined) si = j;
			if (ei == undefined) ei = j;
			if (di == undefined) di = j;
			if (ei < si) ei = si;
			if (di < ei) di = ei;
		}
		return {
			startIndex: si,
			endIndex: ei,
			deleteEndIndex: di
		};
	},
	_findChunkByContent: function(s, i, j, def, content) {
		var n = 0;
		i = def.findFirst(s, i, j);
		while (i < j) {
			n++;
			var o = def.findEnd(s, i, j);
			var u = def.findNext(s, o, j);
			var c = s.substring(i, o);
			if ((content instanceof RegExp) ? content.test(c) : (content == c)) {
				return {
					chunkIndex: n,
					startIndex: i,
					endIndex: o,
					deleteEndIndex: u
				};
			}
			i = u;
		}
		return null;
	},
	/* CHUNK DEFINITIONS */
	_chunkDefs: function() {
		var isHighSurrogate = function(chi) {
			return chi >= 0xD800 && chi < 0xDC00;
		};
		var isLowSurrogate = function(chi) {
			return chi >= 0xDC00 && chi < 0xE000;
		};
		var isLineBreak = function(ch) {
			return ch == '\n' || ch == '\r' || ch == '\u2028' || ch == '\u2029';
		};
		var isWhiteSpace = function(ch) {
			var chi = ch.charCodeAt(0);
			return chi <= 0x20 || (chi >= 0x7F && chi <= 0xA0) || /\s/.test(ch);
		};
		var isSentenceEnder = function(ch) {
			return ch == '.' || ch == '!' || ch == '?';
		};
		var skipNothing = function(s, i, j) {
			return i;
		};
		var skipCharacter = function(s, i, j) {
			if (i < j) {
				var hi = isHighSurrogate(s.charCodeAt(i));
				i++;
				if (hi && i < j && isLowSurrogate(s.charCodeAt(i))) i++;
			}
			return i;
		};
		var skipToLineBreak = function(s, i, j) {
			while (i < j && !isLineBreak(s.charAt(i))) i++;
			return i;
		};
		var skipLineBreak = function(s, i, j) {
			if (i < j && isLineBreak(s.charAt(i))) {
				var cr = (s.charAt(i) == '\r');
				i++;
				if (cr && i < j && s.charAt(i) == '\n') i++;
			}
			return i;
		};
		var skipLineBreaks = function(s, i, j) {
			while (i < j && isLineBreak(s.charAt(i))) i++;
			return i;
		};
		var skipToDelimiter = function(d) {
			return function(s, i, j) {
				i = s.indexOf(d, i);
				if (i < 0 || i > j) return j;
				return i;
			};
		};
		var skipDelimiter = function(d) {
			return function(s, i, j) {
				if (i < j && s.indexOf(d, i) == i) i += d.length;
				return i;
			};
		};
		var skipWhiteSpace = function(s, i, j) {
			while (i < j && isWhiteSpace(s.charAt(i))) i++;
			return i;
		};
		var skipToWhiteSpace = function(s, i, j) {
			while (i < j && !isWhiteSpace(s.charAt(i))) i++;
			return i;
		};
		var skipToSentenceEnd = function(s, i, j) {
			while (i < j && !isSentenceEnder(s.charAt(i))) i++;
			while (i < j && !isWhiteSpace(s.charAt(i))) i++;
			return i;
		};
		var characterDef = {
			findFirst: skipNothing,
			findEnd: skipCharacter,
			findNext: skipNothing
		};
		var lineDef = function(nl) {
			return {
				findFirst: skipNothing,
				findEnd: skipToLineBreak,
				findNext: skipLineBreak,
				delimited: true,
				delimiter: nl
			};
		};
		var delimiterDef = function(d) {
			return {
				findFirst: skipNothing,
				findEnd: skipToDelimiter(d),
				findNext: skipDelimiter(d),
				delimited: true,
				delimiter: d
			};
		};
		var wordDef = {
			findFirst: skipWhiteSpace,
			findEnd: skipToWhiteSpace,
			findNext: skipWhiteSpace
		};
		var sentenceDef = {
			findFirst: skipWhiteSpace,
			findEnd: skipToSentenceEnd,
			findNext: skipWhiteSpace
		};
		var paragraphDef = {
			findFirst: skipLineBreaks,
			findEnd: skipToLineBreak,
			findNext: skipLineBreaks
		};
		return {
			characterDef: characterDef,
			lineDef: lineDef,
			delimiterDef: delimiterDef,
			wordDef: wordDef,
			sentenceDef: sentenceDef,
			paragraphDef: paragraphDef
		};
	}()
};

String.prototype.countChunks = jsChunkEx._wrapForPrototype(jsChunkEx.countChunks);
String.prototype.splitChunks = jsChunkEx._wrapForPrototype(jsChunkEx.splitChunks);
String.prototype.findChunk = jsChunkEx._wrapForPrototype(jsChunkEx.findChunk);
String.prototype.findChunkToDelete = jsChunkEx._wrapForPrototype(jsChunkEx.findChunkToDelete);
String.prototype.getChunk = jsChunkEx._wrapForPrototype(jsChunkEx.getChunk);
String.prototype.deleteChunk = jsChunkEx._wrapForPrototype(jsChunkEx.deleteChunk);
String.prototype.replaceChunk = jsChunkEx._wrapForPrototype(jsChunkEx.replaceChunk);
String.prototype.prependToChunk = jsChunkEx._wrapForPrototype(jsChunkEx.prependToChunk);
String.prototype.appendToChunk = jsChunkEx._wrapForPrototype(jsChunkEx.appendToChunk);
String.prototype.resolveChunk = jsChunkEx._wrapForPrototype(jsChunkEx.resolveChunk);
