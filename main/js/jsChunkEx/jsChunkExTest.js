/*
 * Copyright © 2014-2015 Rebecca G. Bettencourt / Kreative Software
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

jsChunkExTest = {
	runTests: function() {
		var total = 0;
		var passed = 0;
		var failed = 0;
		var results = [];
		for (var k in jsChunkExTest) {
			if (k.length > 4 && k.substring(0, 4) == 'test') {
				total++;
				try {
					jsChunkExTest[k].call(jsChunkExTest);
					passed++;
					console.log('PASSED: ' + k);
					results.push('PASSED: ' + k);
				} catch (e) {
					failed++;
					console.log('FAILED: ' + k + ': ' + e);
					results.push('FAILED: ' + k + ': ' + e);
					if (e.stack) {
						console.log(e.stack);
						results.push(e.stack);
					}
				}
			}
		}
		console.log('Total: ' + total);
		console.log('Passed: ' + passed);
		console.log('Failed: ' + failed);
		return {
			total: total,
			passed: passed,
			failed: failed,
			results: results
		};
	},
	assertEquals: function(expected, actual) {
		if (expected != actual) {
			throw new Error('Expected "' + expected + '" but found "' + actual + '".');
		}
	},
	assertArrayEquals: function(expected, actual) {
		if (expected.length != actual.length) {
			throw new Error('Expected "' + expected + '" but found "' + actual + '".');
		} else {
			for (var i = 0, n = expected.length; i < n; i++) {
				if (expected[i] != actual[i]) {
					throw new Error('Expected "' + expected + '" but found "' + actual + '".');
				}
			}
		}
	},
	assertNull: function(actual) {
		if (actual !== null) {
			throw new Error('Expected null but found "' + actual + '".');
		}
	},
	assertNotNull: function(actual) {
		if (actual === null) {
			throw new Error('Expected not null but found null.');
		}
	},
	testGetCharacter1: function() {
		var sh = 'hello';
		this.assertEquals('', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, 0));
		this.assertEquals('h', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, 1));
		this.assertEquals('e', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, 2));
		this.assertEquals('l', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, 3));
		this.assertEquals('l', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, 4));
		this.assertEquals('o', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, 5));
		this.assertEquals('', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, 6));
		this.assertEquals('', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, 7));
		this.assertEquals('', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, -7));
		this.assertEquals('', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, -6));
		this.assertEquals('h', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, -5));
		this.assertEquals('e', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, -4));
		this.assertEquals('l', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, -3));
		this.assertEquals('l', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, -2));
		this.assertEquals('o', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, -1));
		this.assertEquals('', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, -0));
		this.assertEquals('h', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.FIRST));
		this.assertEquals('l', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.MIDDLE));
		this.assertEquals('o', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.LAST));
		this.assertEquals('hello', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, 0, 6));
		this.assertEquals('hello', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, 1, 5));
		this.assertEquals('ell', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, 2, 4));
		this.assertEquals('hello', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, 1, -1));
		this.assertEquals('ell', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, 2, -2));
		this.assertEquals('hello', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, -6, -1));
		this.assertEquals('hello', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, -5, -1));
		this.assertEquals('ell', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, -4, -2));
		this.assertEquals('hel', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.FIRST, jsChunkEx.MIDDLE));
		this.assertEquals('llo', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.MIDDLE, 5));
		this.assertEquals('llo', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.MIDDLE, jsChunkEx.LAST));
		this.assertEquals('hel', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, 0, 3));
		this.assertEquals('llo', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, 3, 10));
	},
	testGetLine1: function() {
		var lt1 = 'red\ngreen\nblue\ncyan\nmagenta\nyellow';
		this.assertEquals('', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, 0));
		this.assertEquals('red', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, 1));
		this.assertEquals('green', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, 2));
		this.assertEquals('blue', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, 3));
		this.assertEquals('cyan', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, 4));
		this.assertEquals('magenta', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, 5));
		this.assertEquals('yellow', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, 6));
		this.assertEquals('', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, 7));
		this.assertEquals('', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, -7));
		this.assertEquals('red', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, -6));
		this.assertEquals('green', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, -5));
		this.assertEquals('blue', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, -4));
		this.assertEquals('cyan', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, -3));
		this.assertEquals('magenta', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, -2));
		this.assertEquals('yellow', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, -1));
		this.assertEquals('', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, -0));
		this.assertEquals('red', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, jsChunkEx.FIRST));
		this.assertEquals('cyan', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, jsChunkEx.MIDDLE));
		this.assertEquals('yellow', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, jsChunkEx.LAST));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, 0, 7));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, 1, 6));
		this.assertEquals('green\nblue\ncyan\nmagenta', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, 2, 5));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, -7, -1));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, -6, -1));
		this.assertEquals('green\nblue\ncyan\nmagenta', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, -5, -2));
		this.assertEquals('red\ngreen\nblue\ncyan', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, jsChunkEx.FIRST, jsChunkEx.MIDDLE));
		this.assertEquals('cyan\nmagenta\nyellow', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, jsChunkEx.MIDDLE, jsChunkEx.LAST));
		this.assertEquals('red\ngreen', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, 0, 2));
		this.assertEquals('magenta\nyellow', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, 5, 10));
	},
	testGetParagraph1: function() {
		var lt2 = '\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n';
		this.assertEquals('yellow', jsChunkEx.getChunk(lt2, jsChunkEx.PARAGRAPH, 6));
		this.assertEquals('magenta', jsChunkEx.getChunk(lt2, jsChunkEx.PARAGRAPH, 5));
		this.assertEquals('cyan', jsChunkEx.getChunk(lt2, jsChunkEx.PARAGRAPH, 4));
		this.assertEquals('blue', jsChunkEx.getChunk(lt2, jsChunkEx.PARAGRAPH, 3));
		this.assertEquals('green', jsChunkEx.getChunk(lt2, jsChunkEx.PARAGRAPH, 2));
		this.assertEquals('red', jsChunkEx.getChunk(lt2, jsChunkEx.PARAGRAPH, 1));
		this.assertEquals('red', jsChunkEx.getChunk(lt2, jsChunkEx.PARAGRAPH, jsChunkEx.FIRST));
		this.assertEquals('cyan', jsChunkEx.getChunk(lt2, jsChunkEx.PARAGRAPH, jsChunkEx.MIDDLE));
		this.assertEquals('yellow', jsChunkEx.getChunk(lt2, jsChunkEx.PARAGRAPH, jsChunkEx.LAST));
		this.assertEquals('yellow', jsChunkEx.getChunk(lt2, jsChunkEx.PARAGRAPH, -1));
		this.assertEquals('magenta', jsChunkEx.getChunk(lt2, jsChunkEx.PARAGRAPH, -2));
	},
	testGetWord1: function() {
		var std = 'Hello, my name is Rebecca. But you can call me Beckie.';
		var lt2 = '\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n';
		this.assertEquals('Hello', jsChunkEx.getChunk(std, jsChunkEx.ITEM, 1));
		this.assertEquals(' my name is Rebecca. But you can call me Beckie.', jsChunkEx.getChunk(std, jsChunkEx.ITEM, 2));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.getChunk(std, jsChunkEx.ITEM, 1, 2));
		this.assertEquals('Hello', jsChunkEx.getChunk(std, jsChunkEx.ITEM, -100, 1));
		this.assertEquals(' my name is Rebecca. But you can call me Beckie.', jsChunkEx.getChunk(std, jsChunkEx.ITEM, 2, 100));
		this.assertEquals('Hello, my name is Rebecca.', jsChunkEx.getChunk(std, jsChunkEx.SENTENCE, 1));
		this.assertEquals('But you can call me Beckie.', jsChunkEx.getChunk(std, jsChunkEx.SENTENCE, 2));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.getChunk(std, jsChunkEx.SENTENCE, 1, 2));
		this.assertEquals('Hello, my name is Rebecca.', jsChunkEx.getChunk(std, jsChunkEx.SENTENCE, -100, 1));
		this.assertEquals('But you can call me Beckie.', jsChunkEx.getChunk(std, jsChunkEx.SENTENCE, 2, 100));
		this.assertEquals('', jsChunkEx.getChunk(std, jsChunkEx.WORD, 0));
		this.assertEquals('Hello,', jsChunkEx.getChunk(std, jsChunkEx.WORD, 1));
		this.assertEquals('my', jsChunkEx.getChunk(std, jsChunkEx.WORD, 2));
		this.assertEquals('name', jsChunkEx.getChunk(std, jsChunkEx.WORD, 3));
		this.assertEquals('is', jsChunkEx.getChunk(std, jsChunkEx.WORD, 4));
		this.assertEquals('Rebecca.', jsChunkEx.getChunk(std, jsChunkEx.WORD, 5));
		this.assertEquals('But', jsChunkEx.getChunk(std, jsChunkEx.WORD, 6));
		this.assertEquals('you', jsChunkEx.getChunk(std, jsChunkEx.WORD, 7));
		this.assertEquals('can', jsChunkEx.getChunk(std, jsChunkEx.WORD, 8));
		this.assertEquals('call', jsChunkEx.getChunk(std, jsChunkEx.WORD, 9));
		this.assertEquals('me', jsChunkEx.getChunk(std, jsChunkEx.WORD, 10));
		this.assertEquals('Beckie.', jsChunkEx.getChunk(std, jsChunkEx.WORD, 11));
		this.assertEquals('', jsChunkEx.getChunk(std, jsChunkEx.WORD, 12));
		this.assertEquals('', jsChunkEx.getChunk(std, jsChunkEx.WORD, -0));
		this.assertEquals('Beckie.', jsChunkEx.getChunk(std, jsChunkEx.WORD, -1));
		this.assertEquals('me', jsChunkEx.getChunk(std, jsChunkEx.WORD, -2));
		this.assertEquals('call', jsChunkEx.getChunk(std, jsChunkEx.WORD, -3));
		this.assertEquals('can', jsChunkEx.getChunk(std, jsChunkEx.WORD, -4));
		this.assertEquals('you', jsChunkEx.getChunk(std, jsChunkEx.WORD, -5));
		this.assertEquals('But', jsChunkEx.getChunk(std, jsChunkEx.WORD, -6));
		this.assertEquals('Rebecca.', jsChunkEx.getChunk(std, jsChunkEx.WORD, -7));
		this.assertEquals('is', jsChunkEx.getChunk(std, jsChunkEx.WORD, -8));
		this.assertEquals('name', jsChunkEx.getChunk(std, jsChunkEx.WORD, -9));
		this.assertEquals('my', jsChunkEx.getChunk(std, jsChunkEx.WORD, -10));
		this.assertEquals('Hello,', jsChunkEx.getChunk(std, jsChunkEx.WORD, -11));
		this.assertEquals('', jsChunkEx.getChunk(std, jsChunkEx.WORD, -12));
		this.assertEquals('Hello,', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.FIRST));
		this.assertEquals('But', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.MIDDLE));
		this.assertEquals('Beckie.', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.LAST));
		this.assertEquals('red', jsChunkEx.getChunk(lt2, jsChunkEx.WORD, jsChunkEx.FIRST));
		this.assertEquals('cyan', jsChunkEx.getChunk(lt2, jsChunkEx.WORD, jsChunkEx.MIDDLE));
		this.assertEquals('yellow', jsChunkEx.getChunk(lt2, jsChunkEx.WORD, jsChunkEx.LAST));
		this.assertEquals('Hello, my name', jsChunkEx.getChunk(std, jsChunkEx.WORD, 0, 3));
		this.assertEquals('Hello, my name is Rebecca. But', jsChunkEx.getChunk(std, jsChunkEx.WORD, 1, 6));
		this.assertEquals('my name is Rebecca.', jsChunkEx.getChunk(std, jsChunkEx.WORD, 2, 5));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.getChunk(std, jsChunkEx.WORD, 1, -1));
		this.assertEquals('my name is Rebecca. But you can call me', jsChunkEx.getChunk(std, jsChunkEx.WORD, 2, -2));
		this.assertEquals('But you can call me Beckie.', jsChunkEx.getChunk(std, jsChunkEx.WORD, -6, -1));
		this.assertEquals('you can call me', jsChunkEx.getChunk(std, jsChunkEx.WORD, -5, -2));
		this.assertEquals('me Beckie.', jsChunkEx.getChunk(std, jsChunkEx.WORD, -2, -1));
		this.assertEquals('Hello, my name is Rebecca. But', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.FIRST, jsChunkEx.MIDDLE));
		this.assertEquals('But you can call me Beckie.', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.MIDDLE, jsChunkEx.LAST));
		this.assertEquals('But', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.MIDDLE, 6));
	},
	testReplaceChunk1: function() {
		this.assertEquals('hellox', jsChunkEx.replaceChunk('hello', jsChunkEx.CHARACTER, 10, 'x'));
		this.assertEquals('xhello', jsChunkEx.replaceChunk('hello', jsChunkEx.CHARACTER, -10, 'x'));
		this.assertEquals('hello worldx', jsChunkEx.replaceChunk('hello world', jsChunkEx.WORD, 10, 'x'));
		this.assertEquals('xhello world', jsChunkEx.replaceChunk('hello world', jsChunkEx.WORD, -10, 'x'));
		this.assertEquals('hello,world,,,,,,,,x', jsChunkEx.replaceChunk('hello,world', jsChunkEx.ITEM, 10, 'x'));
		this.assertEquals('x,,,,,,,,hello,world', jsChunkEx.replaceChunk('hello,world', jsChunkEx.ITEM, -10, 'x'));
		this.assertEquals('hellox', jsChunkEx.replaceChunk('hello', jsChunkEx.CHARACTER, 6, 'x'));
		this.assertEquals('xhello', jsChunkEx.replaceChunk('hello', jsChunkEx.CHARACTER, -6, 'x'));
		this.assertEquals('hello worldx', jsChunkEx.replaceChunk('hello world', jsChunkEx.WORD, 3, 'x'));
		this.assertEquals('xhello world', jsChunkEx.replaceChunk('hello world', jsChunkEx.WORD, -3, 'x'));
		this.assertEquals('hello,world,x', jsChunkEx.replaceChunk('hello,world', jsChunkEx.ITEM, 3, 'x'));
		this.assertEquals('x,hello,world', jsChunkEx.replaceChunk('hello,world', jsChunkEx.ITEM, -3, 'x'));
		this.assertEquals('hello\nworld\nx', jsChunkEx.replaceChunk('hello\nworld', jsChunkEx.LINE, 3, 'x'));
		this.assertEquals('x\nhello\nworld', jsChunkEx.replaceChunk('hello\nworld', jsChunkEx.LINE, -3, 'x'));
		this.assertEquals('hello\nworld\n\n\nx', jsChunkEx.replaceChunk('hello\nworld', jsChunkEx.LINE, 5, 'x'));
		this.assertEquals('x\n\n\nhello\nworld', jsChunkEx.replaceChunk('hello\nworld', jsChunkEx.LINE, -5, 'x'));
		this.assertEquals('x', jsChunkEx.replaceChunk('hello', jsChunkEx.CHARACTER, -10, 10, 'x'));
		this.assertEquals('x', jsChunkEx.replaceChunk('hello', jsChunkEx.WORD, -10, 10, 'x'));
		this.assertEquals('x', jsChunkEx.replaceChunk('hello,world', jsChunkEx.ITEM, -10, 10, 'x'));
		this.assertEquals('x', jsChunkEx.replaceChunk('hello\nworld', jsChunkEx.LINE, -10, 10, 'x'));
	},
	testReversedBounds1: function() {
		this.assertEquals('', jsChunkEx.getChunk('Hello', jsChunkEx.CHARACTER, 4, 2));
		this.assertEquals('HelloWoxrld', jsChunkEx.replaceChunk('HelloWorld', jsChunkEx.CHARACTER, 8, 1, 'x'));
	},
	testDeleteCharacter1: function() {
		var sh = 'hello';
		this.assertEquals('hello', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, 0));
		this.assertEquals('ello', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, 1));
		this.assertEquals('hllo', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, 2));
		this.assertEquals('helo', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, 3));
		this.assertEquals('helo', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, 4));
		this.assertEquals('hell', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, 5));
		this.assertEquals('hello', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, 6));
		this.assertEquals('hello', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, 7));
		this.assertEquals('hello', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, -7));
		this.assertEquals('hello', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, -6));
		this.assertEquals('ello', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, -5));
		this.assertEquals('hllo', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, -4));
		this.assertEquals('helo', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, -3));
		this.assertEquals('helo', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, -2));
		this.assertEquals('hell', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, -1));
		this.assertEquals('hello', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, -0));
		this.assertEquals('helo', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.MIDDLE));
		this.assertEquals('hell', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.LAST));
		this.assertEquals('', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, 0, 6));
		this.assertEquals('', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, 1, 5));
		this.assertEquals('ho', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, 2, 4));
		this.assertEquals('', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, 1, -1));
		this.assertEquals('ho', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, 2, -2));
		this.assertEquals('', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, -6, -1));
		this.assertEquals('', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, -5, -1));
		this.assertEquals('ho', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, -4, -2));
		this.assertEquals('lo', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.FIRST, jsChunkEx.MIDDLE));
		this.assertEquals('he', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.MIDDLE, 5));
		this.assertEquals('he', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.MIDDLE, jsChunkEx.LAST));
		this.assertEquals('lo', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, 0, 3));
		this.assertEquals('he', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, 3, 10));
	},
	testDeleteLine1: function() {
		var lt1 = 'red\ngreen\nblue\ncyan\nmagenta\nyellow';
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, 0));
		this.assertEquals('green\nblue\ncyan\nmagenta\nyellow', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, 1));
		this.assertEquals('red\nblue\ncyan\nmagenta\nyellow', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, 2));
		this.assertEquals('red\ngreen\ncyan\nmagenta\nyellow', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, 3));
		this.assertEquals('red\ngreen\nblue\nmagenta\nyellow', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, 4));
		this.assertEquals('red\ngreen\nblue\ncyan\nyellow', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, 5));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\n', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, 6));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, 7));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, -7));
		this.assertEquals('green\nblue\ncyan\nmagenta\nyellow', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, -6));
		this.assertEquals('red\nblue\ncyan\nmagenta\nyellow', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, -5));
		this.assertEquals('red\ngreen\ncyan\nmagenta\nyellow', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, -4));
		this.assertEquals('red\ngreen\nblue\nmagenta\nyellow', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, -3));
		this.assertEquals('red\ngreen\nblue\ncyan\nyellow', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, -2));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\n', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, -1));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, -0));
		this.assertEquals('red\ngreen\nblue\nmagenta\nyellow', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, jsChunkEx.MIDDLE));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\n', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, jsChunkEx.LAST));
		this.assertEquals('', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, 0, 7));
		this.assertEquals('', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, 1, 6));
		this.assertEquals('red\nyellow', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, 2, 5));
		this.assertEquals('', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, -7, -1));
		this.assertEquals('', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, -6, -1));
		this.assertEquals('red\nyellow', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, -5, -2));
		this.assertEquals('magenta\nyellow', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, jsChunkEx.FIRST, jsChunkEx.MIDDLE));
		this.assertEquals('red\ngreen\nblue\n', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, jsChunkEx.MIDDLE, jsChunkEx.LAST));
		this.assertEquals('blue\ncyan\nmagenta\nyellow', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, 0, 2));
		this.assertEquals('red\ngreen\nblue\ncyan\n', jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, 5, 10));
	},
	testDeleteParagraph1: function() {
		var lt2 = '\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n';
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.deleteChunk(lt2, jsChunkEx.PARAGRAPH, 7));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\n', jsChunkEx.deleteChunk(lt2, jsChunkEx.PARAGRAPH, 6));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nyellow\n\n', jsChunkEx.deleteChunk(lt2, jsChunkEx.PARAGRAPH, 5));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.deleteChunk(lt2, jsChunkEx.PARAGRAPH, 4));
		this.assertEquals('\nred\n\ngreen\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.deleteChunk(lt2, jsChunkEx.PARAGRAPH, 3));
		this.assertEquals('\nred\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.deleteChunk(lt2, jsChunkEx.PARAGRAPH, 2));
		this.assertEquals('\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.deleteChunk(lt2, jsChunkEx.PARAGRAPH, 1));
		this.assertEquals('red\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.deleteChunk(lt2, jsChunkEx.PARAGRAPH, 0));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.deleteChunk(lt2, jsChunkEx.PARAGRAPH, jsChunkEx.MIDDLE));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\n', jsChunkEx.deleteChunk(lt2, jsChunkEx.PARAGRAPH, -1));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nyellow\n\n', jsChunkEx.deleteChunk(lt2, jsChunkEx.PARAGRAPH, -2));
		this.assertEquals('red\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.deleteChunk(lt2, jsChunkEx.PARAGRAPH, -7));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.deleteChunk(lt2, jsChunkEx.PARAGRAPH, -8));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.deleteChunk(lt2, jsChunkEx.PARAGRAPH, -9));
	},
	testDeleteWord1: function() {
		var std = 'Hello, my name is Rebecca. But you can call me Beckie.';
		var lt2 = '\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n';
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.ITEM, 0));
		this.assertEquals(' my name is Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.ITEM, 1));
		this.assertEquals('Hello,', jsChunkEx.deleteChunk(std, jsChunkEx.ITEM, 2));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.ITEM, 3));
		this.assertEquals('', jsChunkEx.deleteChunk(std, jsChunkEx.ITEM, 1, 2));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.ITEM, -100));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.ITEM, 100));
		this.assertEquals(' my name is Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.ITEM, -100, 1));
		this.assertEquals('Hello,', jsChunkEx.deleteChunk(std, jsChunkEx.ITEM, 2, 100));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.SENTENCE, 0));
		this.assertEquals('But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.SENTENCE, 1));
		this.assertEquals('Hello, my name is Rebecca. ', jsChunkEx.deleteChunk(std, jsChunkEx.SENTENCE, 2));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.SENTENCE, 3));
		this.assertEquals('', jsChunkEx.deleteChunk(std, jsChunkEx.SENTENCE, 1, 2));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.SENTENCE, -100));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.SENTENCE, 100));
		this.assertEquals('But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.SENTENCE, -100, 1));
		this.assertEquals('Hello, my name is Rebecca. ', jsChunkEx.deleteChunk(std, jsChunkEx.SENTENCE, 2, 100));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, 0));
		this.assertEquals('my name is Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, 1));
		this.assertEquals('Hello, name is Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, 2));
		this.assertEquals('Hello, my is Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, 3));
		this.assertEquals('Hello, my name Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, 4));
		this.assertEquals('Hello, my name is But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, 5));
		this.assertEquals('Hello, my name is Rebecca. you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, 6));
		this.assertEquals('Hello, my name is Rebecca. But can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, 7));
		this.assertEquals('Hello, my name is Rebecca. But you call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, 8));
		this.assertEquals('Hello, my name is Rebecca. But you can me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, 9));
		this.assertEquals('Hello, my name is Rebecca. But you can call Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, 10));
		this.assertEquals('Hello, my name is Rebecca. But you can call me ', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, 11));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, 12));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, -0));
		this.assertEquals('Hello, my name is Rebecca. But you can call me ', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, -1));
		this.assertEquals('Hello, my name is Rebecca. But you can call Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, -2));
		this.assertEquals('Hello, my name is Rebecca. But you can me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, -3));
		this.assertEquals('Hello, my name is Rebecca. But you call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, -4));
		this.assertEquals('Hello, my name is Rebecca. But can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, -5));
		this.assertEquals('Hello, my name is Rebecca. you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, -6));
		this.assertEquals('Hello, my name is But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, -7));
		this.assertEquals('Hello, my name Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, -8));
		this.assertEquals('Hello, my is Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, -9));
		this.assertEquals('Hello, name is Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, -10));
		this.assertEquals('my name is Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, -11));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, -12));
		this.assertEquals('Hello, my name is Rebecca. you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, jsChunkEx.MIDDLE));
		this.assertEquals('Hello, my name is Rebecca. But you can call me ', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, jsChunkEx.LAST));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.deleteChunk(lt2, jsChunkEx.WORD, jsChunkEx.MIDDLE));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\n', jsChunkEx.deleteChunk(lt2, jsChunkEx.WORD, jsChunkEx.LAST));
		this.assertEquals('is Rebecca. But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, 0, 3));
		this.assertEquals('you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, 1, 6));
		this.assertEquals('Hello, But you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, 2, 5));
		this.assertEquals('', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, 1, -1));
		this.assertEquals('Hello, Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, 2, -2));
		this.assertEquals('Hello, my name is Rebecca. ', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, -6, -1));
		this.assertEquals('Hello, my name is Rebecca. But Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, -5, -2));
		this.assertEquals('Hello, my name is Rebecca. But you can call ', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, -2, -1));
		this.assertEquals('you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, jsChunkEx.FIRST, jsChunkEx.MIDDLE));
		this.assertEquals('Hello, my name is Rebecca. ', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, jsChunkEx.MIDDLE, jsChunkEx.LAST));
		this.assertEquals('Hello, my name is Rebecca. you can call me Beckie.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, jsChunkEx.MIDDLE, 6));
	},
	testDeleteWord2: function() {
		this.assertEquals('Hello World ', jsChunkEx.deleteChunk('  Hello World ', jsChunkEx.WORD, 0));
		this.assertEquals('  World ', jsChunkEx.deleteChunk('  Hello World ', jsChunkEx.WORD, 1));
		this.assertEquals('  Hello ', jsChunkEx.deleteChunk('  Hello World ', jsChunkEx.WORD, 2));
		this.assertEquals('  Hello World ', jsChunkEx.deleteChunk('  Hello World ', jsChunkEx.WORD, 3));
		this.assertEquals('  Hello World ', jsChunkEx.deleteChunk('  Hello World ', jsChunkEx.WORD, 4));
		this.assertEquals('  Hello World ', jsChunkEx.deleteChunk('  Hello World ', jsChunkEx.WORD, 5));
		this.assertEquals('  Hello ', jsChunkEx.deleteChunk('  Hello World ', jsChunkEx.WORD, -1));
		this.assertEquals('  World ', jsChunkEx.deleteChunk('  Hello World ', jsChunkEx.WORD, -2));
		this.assertEquals('Hello World ', jsChunkEx.deleteChunk('  Hello World ', jsChunkEx.WORD, -3));
		this.assertEquals('  Hello World ', jsChunkEx.deleteChunk('  Hello World ', jsChunkEx.WORD, -4));
		this.assertEquals('  Hello World ', jsChunkEx.deleteChunk('  Hello World ', jsChunkEx.WORD, -5));
	},
	testReplaceCharacter1: function() {
		var sh = 'hello';
		this.assertEquals('xhello', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, 0, 'x'));
		this.assertEquals('xello', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, 1, 'x'));
		this.assertEquals('hxllo', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, 2, 'x'));
		this.assertEquals('hexlo', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, 3, 'x'));
		this.assertEquals('helxo', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, 4, 'x'));
		this.assertEquals('hellx', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, 5, 'x'));
		this.assertEquals('hellox', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, 6, 'x'));
		this.assertEquals('hellox', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, 7, 'x'));
		this.assertEquals('xhello', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, -7, 'x'));
		this.assertEquals('xhello', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, -6, 'x'));
		this.assertEquals('xello', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, -5, 'x'));
		this.assertEquals('hxllo', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, -4, 'x'));
		this.assertEquals('hexlo', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, -3, 'x'));
		this.assertEquals('helxo', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, -2, 'x'));
		this.assertEquals('hellx', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, -1, 'x'));
		this.assertEquals('xhello', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, -0, 'x'));
		this.assertEquals('hexlo', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('hellx', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.LAST, 'x'));
		this.assertEquals('x', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, 0, 6, 'x'));
		this.assertEquals('x', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, 1, 5, 'x'));
		this.assertEquals('hxo', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, 2, 4, 'x'));
		this.assertEquals('x', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, 1, -1, 'x'));
		this.assertEquals('hxo', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, 2, -2, 'x'));
		this.assertEquals('x', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, -6, -1, 'x'));
		this.assertEquals('x', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, -5, -1, 'x'));
		this.assertEquals('hxo', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, -4, -2, 'x'));
		this.assertEquals('xlo', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.FIRST, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('hex', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.MIDDLE, 5, 'x'));
		this.assertEquals('hex', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.MIDDLE, jsChunkEx.LAST, 'x'));
		this.assertEquals('xlo', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, 0, 3, 'x'));
		this.assertEquals('hex', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, 3, 10, 'x'));
	},
	testReplaceLine1: function() {
		var lt1 = 'red\ngreen\nblue\ncyan\nmagenta\nyellow';
		this.assertEquals('x\nred\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, 0, 'x'));
		this.assertEquals('x\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, 1, 'x'));
		this.assertEquals('red\nx\nblue\ncyan\nmagenta\nyellow', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, 2, 'x'));
		this.assertEquals('red\ngreen\nx\ncyan\nmagenta\nyellow', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, 3, 'x'));
		this.assertEquals('red\ngreen\nblue\nx\nmagenta\nyellow', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, 4, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nx\nyellow', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, 5, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nx', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, 6, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nyellow\nx', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, 7, 'x'));
		this.assertEquals('x\nred\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, -7, 'x'));
		this.assertEquals('x\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, -6, 'x'));
		this.assertEquals('red\nx\nblue\ncyan\nmagenta\nyellow', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, -5, 'x'));
		this.assertEquals('red\ngreen\nx\ncyan\nmagenta\nyellow', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, -4, 'x'));
		this.assertEquals('red\ngreen\nblue\nx\nmagenta\nyellow', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, -3, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nx\nyellow', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, -2, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nx', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, -1, 'x'));
		this.assertEquals('x\nred\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, -0, 'x'));
		this.assertEquals('red\ngreen\nblue\nx\nmagenta\nyellow', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nx', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, jsChunkEx.LAST, 'x'));
		this.assertEquals('x', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, 0, 7, 'x'));
		this.assertEquals('x', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, 1, 6, 'x'));
		this.assertEquals('red\nx\nyellow', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, 2, 5, 'x'));
		this.assertEquals('x', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, -7, -1, 'x'));
		this.assertEquals('x', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, -6, -1, 'x'));
		this.assertEquals('red\nx\nyellow', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, -5, -2, 'x'));
		this.assertEquals('x\nmagenta\nyellow', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, jsChunkEx.FIRST, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('red\ngreen\nblue\nx', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, jsChunkEx.MIDDLE, jsChunkEx.LAST, 'x'));
		this.assertEquals('x\nblue\ncyan\nmagenta\nyellow', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, 0, 2, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nx', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, 5, 10, 'x'));
	},
	testReplaceParagraph1: function() {
		var lt2 = '\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n';
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\nx', jsChunkEx.replaceChunk(lt2, jsChunkEx.PARAGRAPH, 7, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nx\n\n', jsChunkEx.replaceChunk(lt2, jsChunkEx.PARAGRAPH, 6, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nx\n\n\n\nyellow\n\n', jsChunkEx.replaceChunk(lt2, jsChunkEx.PARAGRAPH, 5, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\nx\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.replaceChunk(lt2, jsChunkEx.PARAGRAPH, 4, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nx\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.replaceChunk(lt2, jsChunkEx.PARAGRAPH, 3, 'x'));
		this.assertEquals('\nred\n\nx\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.replaceChunk(lt2, jsChunkEx.PARAGRAPH, 2, 'x'));
		this.assertEquals('\nx\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.replaceChunk(lt2, jsChunkEx.PARAGRAPH, 1, 'x'));
		this.assertEquals('x\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.replaceChunk(lt2, jsChunkEx.PARAGRAPH, 0, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\nx\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.replaceChunk(lt2, jsChunkEx.PARAGRAPH, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nx\n\n', jsChunkEx.replaceChunk(lt2, jsChunkEx.PARAGRAPH, -1, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nx\n\n\n\nyellow\n\n', jsChunkEx.replaceChunk(lt2, jsChunkEx.PARAGRAPH, -2, 'x'));
		this.assertEquals('x\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.replaceChunk(lt2, jsChunkEx.PARAGRAPH, -7, 'x'));
		this.assertEquals('x\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.replaceChunk(lt2, jsChunkEx.PARAGRAPH, -8, 'x'));
		this.assertEquals('x\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.replaceChunk(lt2, jsChunkEx.PARAGRAPH, -9, 'x'));
	},
	testReplaceWord1: function() {
		var std = 'Hello, my name is Rebecca. But you can call me Beckie.';
		var lt2 = '\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n';
		this.assertEquals('x,Hello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.ITEM, 0, 'x'));
		this.assertEquals('x, my name is Rebecca. But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.ITEM, 1, 'x'));
		this.assertEquals('Hello,x', jsChunkEx.replaceChunk(std, jsChunkEx.ITEM, 2, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.,x', jsChunkEx.replaceChunk(std, jsChunkEx.ITEM, 3, 'x'));
		this.assertEquals('x', jsChunkEx.replaceChunk(std, jsChunkEx.ITEM, 1, 2, 'x'));
		this.assertEquals('x,,,,,,,,Hello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.ITEM, -10, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.,,,,,,,,x', jsChunkEx.replaceChunk(std, jsChunkEx.ITEM, 10, 'x'));
		this.assertEquals('x, my name is Rebecca. But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.ITEM, -10, 1, 'x'));
		this.assertEquals('Hello,x', jsChunkEx.replaceChunk(std, jsChunkEx.ITEM, 2, 10, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.SENTENCE, 0, 'x'));
		this.assertEquals('x But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.SENTENCE, 1, 'x'));
		this.assertEquals('Hello, my name is Rebecca. x', jsChunkEx.replaceChunk(std, jsChunkEx.SENTENCE, 2, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.x', jsChunkEx.replaceChunk(std, jsChunkEx.SENTENCE, 3, 'x'));
		this.assertEquals('x', jsChunkEx.replaceChunk(std, jsChunkEx.SENTENCE, 1, 2, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.SENTENCE, -100, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.x', jsChunkEx.replaceChunk(std, jsChunkEx.SENTENCE, 100, 'x'));
		this.assertEquals('x But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.SENTENCE, -100, 1, 'x'));
		this.assertEquals('Hello, my name is Rebecca. x', jsChunkEx.replaceChunk(std, jsChunkEx.SENTENCE, 2, 100, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, 0, 'x'));
		this.assertEquals('x my name is Rebecca. But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, 1, 'x'));
		this.assertEquals('Hello, x name is Rebecca. But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, 2, 'x'));
		this.assertEquals('Hello, my x is Rebecca. But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, 3, 'x'));
		this.assertEquals('Hello, my name x Rebecca. But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, 4, 'x'));
		this.assertEquals('Hello, my name is x But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, 5, 'x'));
		this.assertEquals('Hello, my name is Rebecca. x you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, 6, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But x can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, 7, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you x call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, 8, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can x me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, 9, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call x Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, 10, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me x', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, 11, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.x', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, 12, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, -0, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me x', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, -1, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call x Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, -2, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can x me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, -3, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you x call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, -4, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But x can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, -5, 'x'));
		this.assertEquals('Hello, my name is Rebecca. x you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, -6, 'x'));
		this.assertEquals('Hello, my name is x But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, -7, 'x'));
		this.assertEquals('Hello, my name x Rebecca. But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, -8, 'x'));
		this.assertEquals('Hello, my x is Rebecca. But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, -9, 'x'));
		this.assertEquals('Hello, x name is Rebecca. But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, -10, 'x'));
		this.assertEquals('x my name is Rebecca. But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, -11, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, -12, 'x'));
		this.assertEquals('Hello, my name is Rebecca. x you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me x', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, jsChunkEx.LAST, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\nx\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.replaceChunk(lt2, jsChunkEx.WORD, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nx\n\n', jsChunkEx.replaceChunk(lt2, jsChunkEx.WORD, jsChunkEx.LAST, 'x'));
		this.assertEquals('x is Rebecca. But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, 0, 3, 'x'));
		this.assertEquals('x you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, 1, 6, 'x'));
		this.assertEquals('Hello, x But you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, 2, 5, 'x'));
		this.assertEquals('x', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, 1, -1, 'x'));
		this.assertEquals('Hello, x Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, 2, -2, 'x'));
		this.assertEquals('Hello, my name is Rebecca. x', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, -6, -1, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But x Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, -5, -2, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call x', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, -2, -1, 'x'));
		this.assertEquals('x you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, jsChunkEx.FIRST, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('Hello, my name is Rebecca. x', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, jsChunkEx.MIDDLE, jsChunkEx.LAST, 'x'));
		this.assertEquals('Hello, my name is Rebecca. x you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, jsChunkEx.MIDDLE, 6, 'x'));
	},
	testPrependToCharacter1: function() {
		var sh = 'hello';
		this.assertEquals('xhello', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, 0, 'x'));
		this.assertEquals('xhello', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, 1, 'x'));
		this.assertEquals('hxello', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, 2, 'x'));
		this.assertEquals('hexllo', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, 3, 'x'));
		this.assertEquals('helxlo', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, 4, 'x'));
		this.assertEquals('hellxo', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, 5, 'x'));
		this.assertEquals('hellox', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, 6, 'x'));
		this.assertEquals('hellox', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, 7, 'x'));
		this.assertEquals('xhello', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, -7, 'x'));
		this.assertEquals('xhello', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, -6, 'x'));
		this.assertEquals('xhello', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, -5, 'x'));
		this.assertEquals('hxello', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, -4, 'x'));
		this.assertEquals('hexllo', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, -3, 'x'));
		this.assertEquals('helxlo', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, -2, 'x'));
		this.assertEquals('hellxo', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, -1, 'x'));
		this.assertEquals('xhello', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, -0, 'x'));
		this.assertEquals('hexllo', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('hellxo', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.LAST, 'x'));
		this.assertEquals('xhello', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, 0, 6, 'x'));
		this.assertEquals('xhello', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, 1, 5, 'x'));
		this.assertEquals('hxello', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, 2, 4, 'x'));
		this.assertEquals('xhello', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, 1, -1, 'x'));
		this.assertEquals('hxello', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, 2, -2, 'x'));
		this.assertEquals('xhello', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, -6, -1, 'x'));
		this.assertEquals('xhello', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, -5, -1, 'x'));
		this.assertEquals('hxello', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, -4, -2, 'x'));
		this.assertEquals('xhello', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.FIRST, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('hexllo', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.MIDDLE, 5, 'x'));
		this.assertEquals('hexllo', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.MIDDLE, jsChunkEx.LAST, 'x'));
		this.assertEquals('xhello', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, 0, 3, 'x'));
		this.assertEquals('hexllo', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, 3, 10, 'x'));
	},
	testPrependToLine1: function() {
		var lt1 = 'red\ngreen\nblue\ncyan\nmagenta\nyellow';
		this.assertEquals('x\nred\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, 0, 'x'));
		this.assertEquals('xred\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, 1, 'x'));
		this.assertEquals('red\nxgreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, 2, 'x'));
		this.assertEquals('red\ngreen\nxblue\ncyan\nmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, 3, 'x'));
		this.assertEquals('red\ngreen\nblue\nxcyan\nmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, 4, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nxmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, 5, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nxyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, 6, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nyellow\nx', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, 7, 'x'));
		this.assertEquals('x\nred\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, -7, 'x'));
		this.assertEquals('xred\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, -6, 'x'));
		this.assertEquals('red\nxgreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, -5, 'x'));
		this.assertEquals('red\ngreen\nxblue\ncyan\nmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, -4, 'x'));
		this.assertEquals('red\ngreen\nblue\nxcyan\nmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, -3, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nxmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, -2, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nxyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, -1, 'x'));
		this.assertEquals('x\nred\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, -0, 'x'));
		this.assertEquals('red\ngreen\nblue\nxcyan\nmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nxyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, jsChunkEx.LAST, 'x'));
		this.assertEquals('x\nred\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, 0, 7, 'x'));
		this.assertEquals('xred\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, 1, 6, 'x'));
		this.assertEquals('red\nxgreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, 2, 5, 'x'));
		this.assertEquals('x\nred\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, -7, -1, 'x'));
		this.assertEquals('xred\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, -6, -1, 'x'));
		this.assertEquals('red\nxgreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, -5, -2, 'x'));
		this.assertEquals('xred\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, jsChunkEx.FIRST, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('red\ngreen\nblue\nxcyan\nmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, jsChunkEx.MIDDLE, jsChunkEx.LAST, 'x'));
		this.assertEquals('x\nred\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, 0, 2, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nxmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, 5, 10, 'x'));
	},
	testPrependToParagraph1: function() {
		var lt2 = '\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n';
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\nx', jsChunkEx.prependToChunk(lt2, jsChunkEx.PARAGRAPH, 7, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nxyellow\n\n', jsChunkEx.prependToChunk(lt2, jsChunkEx.PARAGRAPH, 6, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nxmagenta\n\n\n\nyellow\n\n', jsChunkEx.prependToChunk(lt2, jsChunkEx.PARAGRAPH, 5, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\nxcyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.prependToChunk(lt2, jsChunkEx.PARAGRAPH, 4, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nxblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.prependToChunk(lt2, jsChunkEx.PARAGRAPH, 3, 'x'));
		this.assertEquals('\nred\n\nxgreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.prependToChunk(lt2, jsChunkEx.PARAGRAPH, 2, 'x'));
		this.assertEquals('\nxred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.prependToChunk(lt2, jsChunkEx.PARAGRAPH, 1, 'x'));
		this.assertEquals('x\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.prependToChunk(lt2, jsChunkEx.PARAGRAPH, 0, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\nxcyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.prependToChunk(lt2, jsChunkEx.PARAGRAPH, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nxyellow\n\n', jsChunkEx.prependToChunk(lt2, jsChunkEx.PARAGRAPH, -1, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nxmagenta\n\n\n\nyellow\n\n', jsChunkEx.prependToChunk(lt2, jsChunkEx.PARAGRAPH, -2, 'x'));
		this.assertEquals('x\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.prependToChunk(lt2, jsChunkEx.PARAGRAPH, -7, 'x'));
		this.assertEquals('x\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.prependToChunk(lt2, jsChunkEx.PARAGRAPH, -8, 'x'));
		this.assertEquals('x\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.prependToChunk(lt2, jsChunkEx.PARAGRAPH, -9, 'x'));
	},
	testPrependToWord1: function() {
		var std = 'Hello, my name is Rebecca. But you can call me Beckie.';
		var lt2 = '\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n';
		this.assertEquals('x,Hello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.ITEM, 0, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.ITEM, 1, 'x'));
		this.assertEquals('Hello,x my name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.ITEM, 2, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.,x', jsChunkEx.prependToChunk(std, jsChunkEx.ITEM, 3, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.ITEM, 1, 2, 'x'));
		this.assertEquals('x,,,,,,,,Hello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.ITEM, -10, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.,,,,,,,,x', jsChunkEx.prependToChunk(std, jsChunkEx.ITEM, 10, 'x'));
		this.assertEquals('x,,,,,,,,Hello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.ITEM, -10, 1, 'x'));
		this.assertEquals('Hello,x my name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.ITEM, 2, 10, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.SENTENCE, 0, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.SENTENCE, 1, 'x'));
		this.assertEquals('Hello, my name is Rebecca. xBut you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.SENTENCE, 2, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.x', jsChunkEx.prependToChunk(std, jsChunkEx.SENTENCE, 3, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.SENTENCE, 1, 2, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.SENTENCE, -100, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.x', jsChunkEx.prependToChunk(std, jsChunkEx.SENTENCE, 100, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.SENTENCE, -100, 1, 'x'));
		this.assertEquals('Hello, my name is Rebecca. xBut you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.SENTENCE, 2, 100, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, 0, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, 1, 'x'));
		this.assertEquals('Hello, xmy name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, 2, 'x'));
		this.assertEquals('Hello, my xname is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, 3, 'x'));
		this.assertEquals('Hello, my name xis Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, 4, 'x'));
		this.assertEquals('Hello, my name is xRebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, 5, 'x'));
		this.assertEquals('Hello, my name is Rebecca. xBut you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, 6, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But xyou can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, 7, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you xcan call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, 8, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can xcall me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, 9, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call xme Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, 10, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me xBeckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, 11, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.x', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, 12, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, -0, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me xBeckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, -1, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call xme Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, -2, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can xcall me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, -3, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you xcan call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, -4, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But xyou can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, -5, 'x'));
		this.assertEquals('Hello, my name is Rebecca. xBut you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, -6, 'x'));
		this.assertEquals('Hello, my name is xRebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, -7, 'x'));
		this.assertEquals('Hello, my name xis Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, -8, 'x'));
		this.assertEquals('Hello, my xname is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, -9, 'x'));
		this.assertEquals('Hello, xmy name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, -10, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, -11, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, -12, 'x'));
		this.assertEquals('Hello, my name is Rebecca. xBut you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me xBeckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, jsChunkEx.LAST, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\nxcyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.prependToChunk(lt2, jsChunkEx.WORD, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nxyellow\n\n', jsChunkEx.prependToChunk(lt2, jsChunkEx.WORD, jsChunkEx.LAST, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, 0, 3, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, 1, 6, 'x'));
		this.assertEquals('Hello, xmy name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, 2, 5, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, 1, -1, 'x'));
		this.assertEquals('Hello, xmy name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, 2, -2, 'x'));
		this.assertEquals('Hello, my name is Rebecca. xBut you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, -6, -1, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But xyou can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, -5, -2, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call xme Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, -2, -1, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, jsChunkEx.FIRST, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('Hello, my name is Rebecca. xBut you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, jsChunkEx.MIDDLE, jsChunkEx.LAST, 'x'));
		this.assertEquals('Hello, my name is Rebecca. xBut you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, jsChunkEx.MIDDLE, 6, 'x'));
	},
	testAppendToCharacter1: function() {
		var sh = 'hello';
		this.assertEquals('xhello', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, 0, 'x'));
		this.assertEquals('hxello', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, 1, 'x'));
		this.assertEquals('hexllo', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, 2, 'x'));
		this.assertEquals('helxlo', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, 3, 'x'));
		this.assertEquals('hellxo', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, 4, 'x'));
		this.assertEquals('hellox', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, 5, 'x'));
		this.assertEquals('hellox', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, 6, 'x'));
		this.assertEquals('hellox', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, 7, 'x'));
		this.assertEquals('xhello', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, -7, 'x'));
		this.assertEquals('xhello', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, -6, 'x'));
		this.assertEquals('hxello', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, -5, 'x'));
		this.assertEquals('hexllo', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, -4, 'x'));
		this.assertEquals('helxlo', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, -3, 'x'));
		this.assertEquals('hellxo', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, -2, 'x'));
		this.assertEquals('hellox', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, -1, 'x'));
		this.assertEquals('xhello', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, -0, 'x'));
		this.assertEquals('helxlo', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('hellox', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.LAST, 'x'));
		this.assertEquals('hellox', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, 0, 6, 'x'));
		this.assertEquals('hellox', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, 1, 5, 'x'));
		this.assertEquals('hellxo', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, 2, 4, 'x'));
		this.assertEquals('hellox', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, 1, -1, 'x'));
		this.assertEquals('hellxo', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, 2, -2, 'x'));
		this.assertEquals('hellox', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, -6, -1, 'x'));
		this.assertEquals('hellox', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, -5, -1, 'x'));
		this.assertEquals('hellxo', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, -4, -2, 'x'));
		this.assertEquals('helxlo', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.FIRST, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('hellox', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.MIDDLE, 5, 'x'));
		this.assertEquals('hellox', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.MIDDLE, jsChunkEx.LAST, 'x'));
		this.assertEquals('helxlo', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, 0, 3, 'x'));
		this.assertEquals('hellox', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, 3, 10, 'x'));
	},
	testAppendToLine1: function() {
		var lt1 = 'red\ngreen\nblue\ncyan\nmagenta\nyellow';
		this.assertEquals('x\nred\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, 0, 'x'));
		this.assertEquals('redx\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, 1, 'x'));
		this.assertEquals('red\ngreenx\nblue\ncyan\nmagenta\nyellow', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, 2, 'x'));
		this.assertEquals('red\ngreen\nbluex\ncyan\nmagenta\nyellow', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, 3, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyanx\nmagenta\nyellow', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, 4, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagentax\nyellow', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, 5, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nyellowx', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, 6, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nyellow\nx', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, 7, 'x'));
		this.assertEquals('x\nred\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, -7, 'x'));
		this.assertEquals('redx\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, -6, 'x'));
		this.assertEquals('red\ngreenx\nblue\ncyan\nmagenta\nyellow', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, -5, 'x'));
		this.assertEquals('red\ngreen\nbluex\ncyan\nmagenta\nyellow', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, -4, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyanx\nmagenta\nyellow', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, -3, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagentax\nyellow', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, -2, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nyellowx', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, -1, 'x'));
		this.assertEquals('x\nred\ngreen\nblue\ncyan\nmagenta\nyellow', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, -0, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyanx\nmagenta\nyellow', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nyellowx', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, jsChunkEx.LAST, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nyellow\nx', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, 0, 7, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nyellowx', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, 1, 6, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagentax\nyellow', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, 2, 5, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nyellowx', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, -7, -1, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nyellowx', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, -6, -1, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagentax\nyellow', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, -5, -2, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyanx\nmagenta\nyellow', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, jsChunkEx.FIRST, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nyellowx', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, jsChunkEx.MIDDLE, jsChunkEx.LAST, 'x'));
		this.assertEquals('red\ngreenx\nblue\ncyan\nmagenta\nyellow', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, 0, 2, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nyellow\n\n\n\nx', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, 5, 10, 'x'));
	},
	testAppendToParagraph1: function() {
		var lt2 = '\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n';
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\nx', jsChunkEx.appendToChunk(lt2, jsChunkEx.PARAGRAPH, 7, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellowx\n\n', jsChunkEx.appendToChunk(lt2, jsChunkEx.PARAGRAPH, 6, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagentax\n\n\n\nyellow\n\n', jsChunkEx.appendToChunk(lt2, jsChunkEx.PARAGRAPH, 5, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyanx\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.appendToChunk(lt2, jsChunkEx.PARAGRAPH, 4, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nbluex\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.appendToChunk(lt2, jsChunkEx.PARAGRAPH, 3, 'x'));
		this.assertEquals('\nred\n\ngreenx\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.appendToChunk(lt2, jsChunkEx.PARAGRAPH, 2, 'x'));
		this.assertEquals('\nredx\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.appendToChunk(lt2, jsChunkEx.PARAGRAPH, 1, 'x'));
		this.assertEquals('x\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.appendToChunk(lt2, jsChunkEx.PARAGRAPH, 0, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyanx\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.appendToChunk(lt2, jsChunkEx.PARAGRAPH, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellowx\n\n', jsChunkEx.appendToChunk(lt2, jsChunkEx.PARAGRAPH, -1, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagentax\n\n\n\nyellow\n\n', jsChunkEx.appendToChunk(lt2, jsChunkEx.PARAGRAPH, -2, 'x'));
		this.assertEquals('x\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.appendToChunk(lt2, jsChunkEx.PARAGRAPH, -7, 'x'));
		this.assertEquals('x\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.appendToChunk(lt2, jsChunkEx.PARAGRAPH, -8, 'x'));
		this.assertEquals('x\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.appendToChunk(lt2, jsChunkEx.PARAGRAPH, -9, 'x'));
	},
	testAppendToWord1: function() {
		var std = 'Hello, my name is Rebecca. But you can call me Beckie.';
		var lt2 = '\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n';
		this.assertEquals('x,Hello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.ITEM, 0, 'x'));
		this.assertEquals('Hellox, my name is Rebecca. But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.ITEM, 1, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.x', jsChunkEx.appendToChunk(std, jsChunkEx.ITEM, 2, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.,x', jsChunkEx.appendToChunk(std, jsChunkEx.ITEM, 3, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.x', jsChunkEx.appendToChunk(std, jsChunkEx.ITEM, 1, 2, 'x'));
		this.assertEquals('x,,,,,,,,Hello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.ITEM, -10, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.,,,,,,,,x', jsChunkEx.appendToChunk(std, jsChunkEx.ITEM, 10, 'x'));
		this.assertEquals('Hellox, my name is Rebecca. But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.ITEM, -10, 1, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.,,,,,,,,x', jsChunkEx.appendToChunk(std, jsChunkEx.ITEM, 2, 10, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.SENTENCE, 0, 'x'));
		this.assertEquals('Hello, my name is Rebecca.x But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.SENTENCE, 1, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.x', jsChunkEx.appendToChunk(std, jsChunkEx.SENTENCE, 2, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.x', jsChunkEx.appendToChunk(std, jsChunkEx.SENTENCE, 3, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.x', jsChunkEx.appendToChunk(std, jsChunkEx.SENTENCE, 1, 2, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.SENTENCE, -100, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.x', jsChunkEx.appendToChunk(std, jsChunkEx.SENTENCE, 100, 'x'));
		this.assertEquals('Hello, my name is Rebecca.x But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.SENTENCE, -100, 1, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.x', jsChunkEx.appendToChunk(std, jsChunkEx.SENTENCE, 2, 100, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, 0, 'x'));
		this.assertEquals('Hello,x my name is Rebecca. But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, 1, 'x'));
		this.assertEquals('Hello, myx name is Rebecca. But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, 2, 'x'));
		this.assertEquals('Hello, my namex is Rebecca. But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, 3, 'x'));
		this.assertEquals('Hello, my name isx Rebecca. But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, 4, 'x'));
		this.assertEquals('Hello, my name is Rebecca.x But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, 5, 'x'));
		this.assertEquals('Hello, my name is Rebecca. Butx you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, 6, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But youx can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, 7, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you canx call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, 8, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can callx me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, 9, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call mex Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, 10, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.x', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, 11, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.x', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, 12, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, -0, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.x', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, -1, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call mex Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, -2, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can callx me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, -3, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you canx call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, -4, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But youx can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, -5, 'x'));
		this.assertEquals('Hello, my name is Rebecca. Butx you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, -6, 'x'));
		this.assertEquals('Hello, my name is Rebecca.x But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, -7, 'x'));
		this.assertEquals('Hello, my name isx Rebecca. But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, -8, 'x'));
		this.assertEquals('Hello, my namex is Rebecca. But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, -9, 'x'));
		this.assertEquals('Hello, myx name is Rebecca. But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, -10, 'x'));
		this.assertEquals('Hello,x my name is Rebecca. But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, -11, 'x'));
		this.assertEquals('xHello, my name is Rebecca. But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, -12, 'x'));
		this.assertEquals('Hello, my name is Rebecca. Butx you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.x', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, jsChunkEx.LAST, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyanx\n\nmagenta\n\n\n\nyellow\n\n', jsChunkEx.appendToChunk(lt2, jsChunkEx.WORD, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellowx\n\n', jsChunkEx.appendToChunk(lt2, jsChunkEx.WORD, jsChunkEx.LAST, 'x'));
		this.assertEquals('Hello, my namex is Rebecca. But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, 0, 3, 'x'));
		this.assertEquals('Hello, my name is Rebecca. Butx you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, 1, 6, 'x'));
		this.assertEquals('Hello, my name is Rebecca.x But you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, 2, 5, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.x', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, 1, -1, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call mex Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, 2, -2, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.x', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, -6, -1, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call mex Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, -5, -2, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.x', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, -2, -1, 'x'));
		this.assertEquals('Hello, my name is Rebecca. Butx you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, jsChunkEx.FIRST, jsChunkEx.MIDDLE, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me Beckie.x', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, jsChunkEx.MIDDLE, jsChunkEx.LAST, 'x'));
		this.assertEquals('Hello, my name is Rebecca. Butx you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, jsChunkEx.MIDDLE, 6, 'x'));
	},
	testCountChunks1: function() {
		var sh = 'hello';
		var std = 'Hello, my name is Rebecca. But you can call me Beckie.';
		var lt1 = 'red\ngreen\nblue\ncyan\nmagenta\nyellow';
		var lt2 = '\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n';
		var it1 = 'George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,Belita Moreno';
		var it2 = 'George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,Belita Moreno,';
		this.assertEquals(0, jsChunkEx.countChunks('', jsChunkEx.CHARACTER));
		this.assertEquals(0, jsChunkEx.countChunks('', jsChunkEx.LINE));
		this.assertEquals(0, jsChunkEx.countChunks('', jsChunkEx.ITEM));
		this.assertEquals(0, jsChunkEx.countChunks('', jsChunkEx.WORD));
		this.assertEquals(0, jsChunkEx.countChunks('', jsChunkEx.SENTENCE));
		this.assertEquals(0, jsChunkEx.countChunks('', jsChunkEx.PARAGRAPH));
		this.assertEquals(5, jsChunkEx.countChunks(sh, jsChunkEx.CHARACTER));
		this.assertEquals(1, jsChunkEx.countChunks(sh, jsChunkEx.LINE));
		this.assertEquals(1, jsChunkEx.countChunks(sh, jsChunkEx.ITEM));
		this.assertEquals(1, jsChunkEx.countChunks(sh, jsChunkEx.WORD));
		this.assertEquals(1, jsChunkEx.countChunks(sh, jsChunkEx.SENTENCE));
		this.assertEquals(1, jsChunkEx.countChunks(sh, jsChunkEx.PARAGRAPH));
		this.assertEquals(54, jsChunkEx.countChunks(std, jsChunkEx.CHARACTER));
		this.assertEquals(1, jsChunkEx.countChunks(std, jsChunkEx.LINE));
		this.assertEquals(2, jsChunkEx.countChunks(std, jsChunkEx.ITEM));
		this.assertEquals(11, jsChunkEx.countChunks(std, jsChunkEx.WORD));
		this.assertEquals(2, jsChunkEx.countChunks(std, jsChunkEx.SENTENCE));
		this.assertEquals(1, jsChunkEx.countChunks(std, jsChunkEx.PARAGRAPH));
		this.assertEquals(34, jsChunkEx.countChunks(lt1, jsChunkEx.CHARACTER));
		this.assertEquals(6, jsChunkEx.countChunks(lt1, jsChunkEx.LINE));
		this.assertEquals(1, jsChunkEx.countChunks(lt1, jsChunkEx.ITEM));
		this.assertEquals(6, jsChunkEx.countChunks(lt1, jsChunkEx.WORD));
		this.assertEquals(1, jsChunkEx.countChunks(lt1, jsChunkEx.SENTENCE));
		this.assertEquals(6, jsChunkEx.countChunks(lt1, jsChunkEx.PARAGRAPH));
		this.assertEquals(44, jsChunkEx.countChunks(lt2, jsChunkEx.CHARACTER));
		this.assertEquals(15, jsChunkEx.countChunks(lt2, jsChunkEx.LINE));
		this.assertEquals(1, jsChunkEx.countChunks(lt2, jsChunkEx.ITEM));
		this.assertEquals(6, jsChunkEx.countChunks(lt2, jsChunkEx.WORD));
		this.assertEquals(1, jsChunkEx.countChunks(lt2, jsChunkEx.SENTENCE));
		this.assertEquals(6, jsChunkEx.countChunks(lt2, jsChunkEx.PARAGRAPH));
		this.assertEquals(75, jsChunkEx.countChunks(it1, jsChunkEx.CHARACTER));
		this.assertEquals(1, jsChunkEx.countChunks(it1, jsChunkEx.LINE));
		this.assertEquals(5, jsChunkEx.countChunks(it1, jsChunkEx.ITEM));
		this.assertEquals(7, jsChunkEx.countChunks(it1, jsChunkEx.WORD));
		this.assertEquals(1, jsChunkEx.countChunks(it1, jsChunkEx.SENTENCE));
		this.assertEquals(1, jsChunkEx.countChunks(it1, jsChunkEx.PARAGRAPH));
		this.assertEquals(76, jsChunkEx.countChunks(it2, jsChunkEx.CHARACTER));
		this.assertEquals(1, jsChunkEx.countChunks(it2, jsChunkEx.LINE));
		this.assertEquals(5, jsChunkEx.countChunks(it2, jsChunkEx.ITEM));
		this.assertEquals(7, jsChunkEx.countChunks(it2, jsChunkEx.WORD));
		this.assertEquals(1, jsChunkEx.countChunks(it2, jsChunkEx.SENTENCE));
		this.assertEquals(1, jsChunkEx.countChunks(it2, jsChunkEx.PARAGRAPH));
	},
	testGetChunkReversedBounds1: function() {
		var sh = 'hello';
		var std = 'Hello, my name is Rebecca. But you can call me Beckie.';
		var lt1 = 'red\ngreen\nblue\ncyan\nmagenta\nyellow';
		var lt2 = '\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n';
		var it1 = 'George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,Belita Moreno';
		var it2 = 'George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,Belita Moreno,';
		this.assertEquals('', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, 5, 1));
		this.assertEquals('', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, 4, 2));
		this.assertEquals('', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, -1, -5));
		this.assertEquals('', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, -2, -4));
		this.assertEquals('', jsChunkEx.getChunk(std, jsChunkEx.WORD, 11, 1));
		this.assertEquals('', jsChunkEx.getChunk(std, jsChunkEx.WORD, 10, 2));
		this.assertEquals('', jsChunkEx.getChunk(std, jsChunkEx.WORD, -1, -11));
		this.assertEquals('', jsChunkEx.getChunk(std, jsChunkEx.WORD, -2, -10));
		this.assertEquals('', jsChunkEx.getChunk(std, jsChunkEx.SENTENCE, 2, 1));
		this.assertEquals('', jsChunkEx.getChunk(std, jsChunkEx.SENTENCE, -1, -2));
		this.assertEquals('', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, 6, 1));
		this.assertEquals('', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, 5, 2));
		this.assertEquals('', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, -1, -6));
		this.assertEquals('', jsChunkEx.getChunk(lt1, jsChunkEx.LINE, -2, -5));
		this.assertEquals('', jsChunkEx.getChunk(lt2, jsChunkEx.PARAGRAPH, 6, 1));
		this.assertEquals('', jsChunkEx.getChunk(lt2, jsChunkEx.PARAGRAPH, 5, 2));
		this.assertEquals('', jsChunkEx.getChunk(lt2, jsChunkEx.PARAGRAPH, -1, -6));
		this.assertEquals('', jsChunkEx.getChunk(lt2, jsChunkEx.PARAGRAPH, -2, -5));
		this.assertEquals('', jsChunkEx.getChunk(it1, jsChunkEx.ITEM, 5, 1));
		this.assertEquals('', jsChunkEx.getChunk(it1, jsChunkEx.ITEM, 4, 2));
		this.assertEquals('', jsChunkEx.getChunk(it1, jsChunkEx.ITEM, -1, -5));
		this.assertEquals('', jsChunkEx.getChunk(it1, jsChunkEx.ITEM, -2, -4));
		this.assertEquals('', jsChunkEx.getChunk(it2, jsChunkEx.ITEM, 5, 1));
		this.assertEquals('', jsChunkEx.getChunk(it2, jsChunkEx.ITEM, 4, 2));
		this.assertEquals('', jsChunkEx.getChunk(it2, jsChunkEx.ITEM, -1, -5));
		this.assertEquals('', jsChunkEx.getChunk(it2, jsChunkEx.ITEM, -2, -4));
	},
	testDeleteChunkReversedBounds1: function() {
		var sh = 'hello';
		var std = 'Hello, my name is Rebecca. But you can call me Beckie.';
		var lt1 = 'red\ngreen\nblue\ncyan\nmagenta\nyellow';
		var lt2 = '\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n';
		var it1 = 'George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,Belita Moreno';
		var it2 = 'George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,Belita Moreno,';
		this.assertEquals(sh, jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, 5, 1));
		this.assertEquals(sh, jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, 4, 2));
		this.assertEquals(sh, jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, -1, -5));
		this.assertEquals(sh, jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, -2, -4));
		this.assertEquals(std, jsChunkEx.deleteChunk(std, jsChunkEx.WORD, 11, 1));
		this.assertEquals(std, jsChunkEx.deleteChunk(std, jsChunkEx.WORD, 10, 2));
		this.assertEquals(std, jsChunkEx.deleteChunk(std, jsChunkEx.WORD, -1, -11));
		this.assertEquals(std, jsChunkEx.deleteChunk(std, jsChunkEx.WORD, -2, -10));
		this.assertEquals(std, jsChunkEx.deleteChunk(std, jsChunkEx.SENTENCE, 2, 1));
		this.assertEquals(std, jsChunkEx.deleteChunk(std, jsChunkEx.SENTENCE, -1, -2));
		this.assertEquals(lt1, jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, 6, 1));
		this.assertEquals(lt1, jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, 5, 2));
		this.assertEquals(lt1, jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, -1, -6));
		this.assertEquals(lt1, jsChunkEx.deleteChunk(lt1, jsChunkEx.LINE, -2, -5));
		this.assertEquals(lt2, jsChunkEx.deleteChunk(lt2, jsChunkEx.PARAGRAPH, 6, 1));
		this.assertEquals(lt2, jsChunkEx.deleteChunk(lt2, jsChunkEx.PARAGRAPH, 5, 2));
		this.assertEquals(lt2, jsChunkEx.deleteChunk(lt2, jsChunkEx.PARAGRAPH, -1, -6));
		this.assertEquals(lt2, jsChunkEx.deleteChunk(lt2, jsChunkEx.PARAGRAPH, -2, -5));
		this.assertEquals(it1, jsChunkEx.deleteChunk(it1, jsChunkEx.ITEM, 5, 1));
		this.assertEquals(it1, jsChunkEx.deleteChunk(it1, jsChunkEx.ITEM, 4, 2));
		this.assertEquals(it1, jsChunkEx.deleteChunk(it1, jsChunkEx.ITEM, -1, -5));
		this.assertEquals(it1, jsChunkEx.deleteChunk(it1, jsChunkEx.ITEM, -2, -4));
		this.assertEquals(it2, jsChunkEx.deleteChunk(it2, jsChunkEx.ITEM, 5, 1));
		this.assertEquals(it2, jsChunkEx.deleteChunk(it2, jsChunkEx.ITEM, 4, 2));
		this.assertEquals(it2, jsChunkEx.deleteChunk(it2, jsChunkEx.ITEM, -1, -5));
		this.assertEquals(it2, jsChunkEx.deleteChunk(it2, jsChunkEx.ITEM, -2, -4));
	},
	testReplaceChunkReversedBounds1: function() {
		var sh = 'hello';
		var std = 'Hello, my name is Rebecca. But you can call me Beckie.';
		var lt1 = 'red\ngreen\nblue\ncyan\nmagenta\nyellow';
		var lt2 = '\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n';
		var it1 = 'George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,Belita Moreno';
		var it2 = 'George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,Belita Moreno,';
		this.assertEquals('hellxo', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, 5, 1, 'x'));
		this.assertEquals('helxlo', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, 4, 2, 'x'));
		this.assertEquals('hellxo', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, -1, -5, 'x'));
		this.assertEquals('helxlo', jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, -2, -4, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me xBeckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, 11, 1, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call xme Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, 10, 2, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me xBeckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, -1, -11, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call xme Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.WORD, -2, -10, 'x'));
		this.assertEquals('Hello, my name is Rebecca. xBut you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.SENTENCE, 2, 1, 'x'));
		this.assertEquals('Hello, my name is Rebecca. xBut you can call me Beckie.', jsChunkEx.replaceChunk(std, jsChunkEx.SENTENCE, -1, -2, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nxyellow', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, 6, 1, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nxmagenta\nyellow', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, 5, 2, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nxyellow', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, -1, -6, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nxmagenta\nyellow', jsChunkEx.replaceChunk(lt1, jsChunkEx.LINE, -2, -5, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nxyellow\n\n', jsChunkEx.replaceChunk(lt2, jsChunkEx.PARAGRAPH, 6, 1, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nxmagenta\n\n\n\nyellow\n\n', jsChunkEx.replaceChunk(lt2, jsChunkEx.PARAGRAPH, 5, 2, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nxyellow\n\n', jsChunkEx.replaceChunk(lt2, jsChunkEx.PARAGRAPH, -1, -6, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nxmagenta\n\n\n\nyellow\n\n', jsChunkEx.replaceChunk(lt2, jsChunkEx.PARAGRAPH, -2, -5, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,xBelita Moreno', jsChunkEx.replaceChunk(it1, jsChunkEx.ITEM, 5, 1, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,xMasiela Lusha,Belita Moreno', jsChunkEx.replaceChunk(it1, jsChunkEx.ITEM, 4, 2, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,xBelita Moreno', jsChunkEx.replaceChunk(it1, jsChunkEx.ITEM, -1, -5, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,xMasiela Lusha,Belita Moreno', jsChunkEx.replaceChunk(it1, jsChunkEx.ITEM, -2, -4, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,xBelita Moreno,', jsChunkEx.replaceChunk(it2, jsChunkEx.ITEM, 5, 1, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,xMasiela Lusha,Belita Moreno,', jsChunkEx.replaceChunk(it2, jsChunkEx.ITEM, 4, 2, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,xBelita Moreno,', jsChunkEx.replaceChunk(it2, jsChunkEx.ITEM, -1, -5, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,xMasiela Lusha,Belita Moreno,', jsChunkEx.replaceChunk(it2, jsChunkEx.ITEM, -2, -4, 'x'));
	},
	testPrependToChunkReversedBounds1: function() {
		var sh = 'hello';
		var std = 'Hello, my name is Rebecca. But you can call me Beckie.';
		var lt1 = 'red\ngreen\nblue\ncyan\nmagenta\nyellow';
		var lt2 = '\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n';
		var it1 = 'George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,Belita Moreno';
		var it2 = 'George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,Belita Moreno,';
		this.assertEquals('hellxo', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, 5, 1, 'x'));
		this.assertEquals('helxlo', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, 4, 2, 'x'));
		this.assertEquals('hellxo', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, -1, -5, 'x'));
		this.assertEquals('helxlo', jsChunkEx.prependToChunk(sh, jsChunkEx.CHARACTER, -2, -4, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me xBeckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, 11, 1, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call xme Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, 10, 2, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me xBeckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, -1, -11, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call xme Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.WORD, -2, -10, 'x'));
		this.assertEquals('Hello, my name is Rebecca. xBut you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.SENTENCE, 2, 1, 'x'));
		this.assertEquals('Hello, my name is Rebecca. xBut you can call me Beckie.', jsChunkEx.prependToChunk(std, jsChunkEx.SENTENCE, -1, -2, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nxyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, 6, 1, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nxmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, 5, 2, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nxyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, -1, -6, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nxmagenta\nyellow', jsChunkEx.prependToChunk(lt1, jsChunkEx.LINE, -2, -5, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nxyellow\n\n', jsChunkEx.prependToChunk(lt2, jsChunkEx.PARAGRAPH, 6, 1, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nxmagenta\n\n\n\nyellow\n\n', jsChunkEx.prependToChunk(lt2, jsChunkEx.PARAGRAPH, 5, 2, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nxyellow\n\n', jsChunkEx.prependToChunk(lt2, jsChunkEx.PARAGRAPH, -1, -6, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nxmagenta\n\n\n\nyellow\n\n', jsChunkEx.prependToChunk(lt2, jsChunkEx.PARAGRAPH, -2, -5, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,xBelita Moreno', jsChunkEx.prependToChunk(it1, jsChunkEx.ITEM, 5, 1, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,xMasiela Lusha,Belita Moreno', jsChunkEx.prependToChunk(it1, jsChunkEx.ITEM, 4, 2, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,xBelita Moreno', jsChunkEx.prependToChunk(it1, jsChunkEx.ITEM, -1, -5, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,xMasiela Lusha,Belita Moreno', jsChunkEx.prependToChunk(it1, jsChunkEx.ITEM, -2, -4, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,xBelita Moreno,', jsChunkEx.prependToChunk(it2, jsChunkEx.ITEM, 5, 1, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,xMasiela Lusha,Belita Moreno,', jsChunkEx.prependToChunk(it2, jsChunkEx.ITEM, 4, 2, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,xBelita Moreno,', jsChunkEx.prependToChunk(it2, jsChunkEx.ITEM, -1, -5, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,xMasiela Lusha,Belita Moreno,', jsChunkEx.prependToChunk(it2, jsChunkEx.ITEM, -2, -4, 'x'));
	},
	testAppendToChunkReversedBounds1: function() {
		var sh = 'hello';
		var std = 'Hello, my name is Rebecca. But you can call me Beckie.';
		var lt1 = 'red\ngreen\nblue\ncyan\nmagenta\nyellow';
		var lt2 = '\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nyellow\n\n';
		var it1 = 'George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,Belita Moreno';
		var it2 = 'George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,Belita Moreno,';
		this.assertEquals('hellxo', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, 5, 1, 'x'));
		this.assertEquals('helxlo', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, 4, 2, 'x'));
		this.assertEquals('hellxo', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, -1, -5, 'x'));
		this.assertEquals('helxlo', jsChunkEx.appendToChunk(sh, jsChunkEx.CHARACTER, -2, -4, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me xBeckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, 11, 1, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call xme Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, 10, 2, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call me xBeckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, -1, -11, 'x'));
		this.assertEquals('Hello, my name is Rebecca. But you can call xme Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.WORD, -2, -10, 'x'));
		this.assertEquals('Hello, my name is Rebecca. xBut you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.SENTENCE, 2, 1, 'x'));
		this.assertEquals('Hello, my name is Rebecca. xBut you can call me Beckie.', jsChunkEx.appendToChunk(std, jsChunkEx.SENTENCE, -1, -2, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nxyellow', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, 6, 1, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nxmagenta\nyellow', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, 5, 2, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nmagenta\nxyellow', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, -1, -6, 'x'));
		this.assertEquals('red\ngreen\nblue\ncyan\nxmagenta\nyellow', jsChunkEx.appendToChunk(lt1, jsChunkEx.LINE, -2, -5, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nxyellow\n\n', jsChunkEx.appendToChunk(lt2, jsChunkEx.PARAGRAPH, 6, 1, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nxmagenta\n\n\n\nyellow\n\n', jsChunkEx.appendToChunk(lt2, jsChunkEx.PARAGRAPH, 5, 2, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nmagenta\n\n\n\nxyellow\n\n', jsChunkEx.appendToChunk(lt2, jsChunkEx.PARAGRAPH, -1, -6, 'x'));
		this.assertEquals('\nred\n\ngreen\n\nblue\n\ncyan\n\nxmagenta\n\n\n\nyellow\n\n', jsChunkEx.appendToChunk(lt2, jsChunkEx.PARAGRAPH, -2, -5, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,xBelita Moreno', jsChunkEx.appendToChunk(it1, jsChunkEx.ITEM, 5, 1, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,xMasiela Lusha,Belita Moreno', jsChunkEx.appendToChunk(it1, jsChunkEx.ITEM, 4, 2, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,xBelita Moreno', jsChunkEx.appendToChunk(it1, jsChunkEx.ITEM, -1, -5, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,xMasiela Lusha,Belita Moreno', jsChunkEx.appendToChunk(it1, jsChunkEx.ITEM, -2, -4, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,xBelita Moreno,', jsChunkEx.appendToChunk(it2, jsChunkEx.ITEM, 5, 1, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,xMasiela Lusha,Belita Moreno,', jsChunkEx.appendToChunk(it2, jsChunkEx.ITEM, 4, 2, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,Masiela Lusha,xBelita Moreno,', jsChunkEx.appendToChunk(it2, jsChunkEx.ITEM, -1, -5, 'x'));
		this.assertEquals('George Lopez,Constance Marie,Luis Armand Garcia,xMasiela Lusha,Belita Moreno,', jsChunkEx.appendToChunk(it2, jsChunkEx.ITEM, -2, -4, 'x'));
	},
	testEmpty1: function() {
		this.assertEquals('x', jsChunkEx.replaceChunk('', jsChunkEx.CHARACTER, 4, 'x'));
		this.assertEquals('x', jsChunkEx.replaceChunk('', jsChunkEx.CHARACTER, -4, 'x'));
		this.assertEquals('x', jsChunkEx.replaceChunk('', jsChunkEx.WORD, 4, 'x'));
		this.assertEquals('x', jsChunkEx.replaceChunk('', jsChunkEx.WORD, -4, 'x'));
		this.assertEquals(',,,x', jsChunkEx.replaceChunk('', jsChunkEx.ITEM, 4, 'x'));
		this.assertEquals('x,,,,', jsChunkEx.replaceChunk('', jsChunkEx.ITEM, -4, 'x'));
		this.assertEquals('\n\n\nx', jsChunkEx.replaceChunk('', jsChunkEx.LINE, 4, 'x'));
		this.assertEquals('x\n\n\n\n', jsChunkEx.replaceChunk('', jsChunkEx.LINE, -4, 'x'));
		this.assertEquals('x', jsChunkEx.prependToChunk('', jsChunkEx.CHARACTER, 4, 'x'));
		this.assertEquals('x', jsChunkEx.prependToChunk('', jsChunkEx.CHARACTER, -4, 'x'));
		this.assertEquals('x', jsChunkEx.prependToChunk('', jsChunkEx.WORD, 4, 'x'));
		this.assertEquals('x', jsChunkEx.prependToChunk('', jsChunkEx.WORD, -4, 'x'));
		this.assertEquals(',,,x', jsChunkEx.prependToChunk('', jsChunkEx.ITEM, 4, 'x'));
		this.assertEquals('x,,,,', jsChunkEx.prependToChunk('', jsChunkEx.ITEM, -4, 'x'));
		this.assertEquals('\n\n\nx', jsChunkEx.prependToChunk('', jsChunkEx.LINE, 4, 'x'));
		this.assertEquals('x\n\n\n\n', jsChunkEx.prependToChunk('', jsChunkEx.LINE, -4, 'x'));
		this.assertEquals('x', jsChunkEx.appendToChunk('', jsChunkEx.CHARACTER, 4, 'x'));
		this.assertEquals('x', jsChunkEx.appendToChunk('', jsChunkEx.CHARACTER, -4, 'x'));
		this.assertEquals('x', jsChunkEx.appendToChunk('', jsChunkEx.WORD, 4, 'x'));
		this.assertEquals('x', jsChunkEx.appendToChunk('', jsChunkEx.WORD, -4, 'x'));
		this.assertEquals(',,,x', jsChunkEx.appendToChunk('', jsChunkEx.ITEM, 4, 'x'));
		this.assertEquals('x,,,,', jsChunkEx.appendToChunk('', jsChunkEx.ITEM, -4, 'x'));
		this.assertEquals('\n\n\nx', jsChunkEx.appendToChunk('', jsChunkEx.LINE, 4, 'x'));
		this.assertEquals('x\n\n\n\n', jsChunkEx.appendToChunk('', jsChunkEx.LINE, -4, 'x'));
	},
	testFindChunkByContent1: function() {
		var sh = 'Hello';
		this.assertNotNull(jsChunkEx.findChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, 'H'));
		this.assertNull(jsChunkEx.findChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, 'h'));
		this.assertNotNull(jsChunkEx.findChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /H/));
		this.assertNull(jsChunkEx.findChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /h/));
		this.assertNotNull(jsChunkEx.findChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /H/i));
		this.assertNotNull(jsChunkEx.findChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /h/i));
		this.assertNull(jsChunkEx.findChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, 'E'));
		this.assertNotNull(jsChunkEx.findChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, 'e'));
		this.assertNull(jsChunkEx.findChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /E/));
		this.assertNotNull(jsChunkEx.findChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /e/));
		this.assertNotNull(jsChunkEx.findChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /E/i));
		this.assertNotNull(jsChunkEx.findChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /e/i));
	},
	testGetChunkByContent1: function() {
		var sh = 'Hello';
		this.assertEquals('H', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, 'H'));
		this.assertEquals('', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, 'h'));
		this.assertEquals('H', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /H/));
		this.assertEquals('', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /h/));
		this.assertEquals('H', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /H/i));
		this.assertEquals('H', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /h/i));
		this.assertEquals('', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, 'E'));
		this.assertEquals('e', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, 'e'));
		this.assertEquals('', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /E/));
		this.assertEquals('e', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /e/));
		this.assertEquals('e', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /E/i));
		this.assertEquals('e', jsChunkEx.getChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /e/i));
	},
	testDeleteChunkByContent1: function() {
		var sh = 'Hello';
		this.assertEquals('ello', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, 'H'));
		this.assertEquals('Hello', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, 'h'));
		this.assertEquals('ello', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /H/));
		this.assertEquals('Hello', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /h/));
		this.assertEquals('ello', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /H/i));
		this.assertEquals('ello', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /h/i));
		this.assertEquals('Hello', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, 'E'));
		this.assertEquals('Hllo', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, 'e'));
		this.assertEquals('Hello', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /E/));
		this.assertEquals('Hllo', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /e/));
		this.assertEquals('Hllo', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /E/i));
		this.assertEquals('Hllo', jsChunkEx.deleteChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, /e/i));
	},
	testReplaceChunkByContent1: function() {
		var sh = 'Hello';
		while (jsChunkEx.findChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, 'l')) {
			sh = jsChunkEx.replaceChunk(sh, jsChunkEx.CHARACTER, jsChunkEx.BY_CONTENT, 'l', 'x');
		}
		this.assertEquals('Hexxo', sh);
	},
	testFindChunkByContent2: function() {
		var std = 'Hello, my name is Rebecca.';
		this.assertNotNull(jsChunkEx.findChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'Hello,'));
		this.assertNull(jsChunkEx.findChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'HeLLo,'));
		this.assertNull(jsChunkEx.findChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'Hello'));
		this.assertNull(jsChunkEx.findChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'HeLLO'));
		this.assertNotNull(jsChunkEx.findChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /Hello/));
		this.assertNull(jsChunkEx.findChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /HeLLo/));
		this.assertNotNull(jsChunkEx.findChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /Hello/i));
		this.assertNotNull(jsChunkEx.findChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /HeLLo/i));
		this.assertNotNull(jsChunkEx.findChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'Rebecca.'));
		this.assertNull(jsChunkEx.findChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'RebeCCa.'));
		this.assertNull(jsChunkEx.findChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'Rebecca'));
		this.assertNull(jsChunkEx.findChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'RebeCCa'));
		this.assertNotNull(jsChunkEx.findChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /Rebecca/));
		this.assertNull(jsChunkEx.findChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /RebeCCa/));
		this.assertNotNull(jsChunkEx.findChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /Rebecca/i));
		this.assertNotNull(jsChunkEx.findChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /RebeCCa/i));
		this.assertNull(jsChunkEx.findChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'Ginny'));
	},
	testGetChunkByContent2: function() {
		var std = 'Hello, my name is Rebecca.';
		this.assertEquals('Hello,', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'Hello,'));
		this.assertEquals('', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'HeLLo,'));
		this.assertEquals('', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'Hello'));
		this.assertEquals('', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'HeLLO'));
		this.assertEquals('Hello,', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /Hello/));
		this.assertEquals('', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /HeLLo/));
		this.assertEquals('Hello,', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /Hello/i));
		this.assertEquals('Hello,', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /HeLLo/i));
		this.assertEquals('Rebecca.', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'Rebecca.'));
		this.assertEquals('', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'RebeCCa.'));
		this.assertEquals('', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'Rebecca'));
		this.assertEquals('', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'RebeCCa'));
		this.assertEquals('Rebecca.', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /Rebecca/));
		this.assertEquals('', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /RebeCCa/));
		this.assertEquals('Rebecca.', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /Rebecca/i));
		this.assertEquals('Rebecca.', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /RebeCCa/i));
		this.assertEquals('', jsChunkEx.getChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'Ginny'));
	},
	testDeleteChunkByContent2: function() {
		var std = 'Hello, my name is Rebecca.';
		this.assertEquals('my name is Rebecca.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'Hello,'));
		this.assertEquals(std, jsChunkEx.deleteChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'HeLLo,'));
		this.assertEquals(std, jsChunkEx.deleteChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'Hello'));
		this.assertEquals(std, jsChunkEx.deleteChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'HeLLO'));
		this.assertEquals('my name is Rebecca.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /Hello/));
		this.assertEquals(std, jsChunkEx.deleteChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /HeLLo/));
		this.assertEquals('my name is Rebecca.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /Hello/i));
		this.assertEquals('my name is Rebecca.', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /HeLLo/i));
		this.assertEquals('Hello, my name is ', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'Rebecca.'));
		this.assertEquals(std, jsChunkEx.deleteChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'RebeCCa.'));
		this.assertEquals(std, jsChunkEx.deleteChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'Rebecca'));
		this.assertEquals(std, jsChunkEx.deleteChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'RebeCCa'));
		this.assertEquals('Hello, my name is ', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /Rebecca/));
		this.assertEquals(std, jsChunkEx.deleteChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /RebeCCa/));
		this.assertEquals('Hello, my name is ', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /Rebecca/i));
		this.assertEquals('Hello, my name is ', jsChunkEx.deleteChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, /RebeCCa/i));
		this.assertEquals(std, jsChunkEx.deleteChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'Ginny'));
	},
	testReplaceChunkByContent2: function() {
		var std = 'Hello, my name is Rebecca.';
		var dev = 'Hello, my name is Ginny.';
		this.assertEquals(dev, jsChunkEx.replaceChunk(std, jsChunkEx.WORD, jsChunkEx.BY_CONTENT, 'Rebecca.', 'Ginny.'));
	},
	testNestedCountChunks: function() {
		var s = 'a bc def, ghij klmno pqrstu, vwxyz01 23456789 !@#$%&?[]';
		this.assertEquals(6, jsChunkEx.countChunks(s, jsChunkEx.ITEM, 2, jsChunkEx.WORD, 3, jsChunkEx.CHARACTER));
	},
	testNestedSplitChunks: function() {
		var s = 'a bc def, ghij klmno pqrstu, vwxyz01 23456789 !@#$%&?[]';
		var sc = jsChunkEx.splitChunks(s, jsChunkEx.ITEM, 2, jsChunkEx.WORD, 3, jsChunkEx.CHARACTER);
		this.assertArrayEquals(['p','q','r','s','t','u'], sc.map(function(ch){ return ch.content; }));
	},
	testNestedFindChunk: function() {
		var s = 'a bc def, ghij klmno pqrstu, vwxyz01 23456789 !@#$%&?[]';
		var fc = jsChunkEx.findChunk(s, jsChunkEx.ITEM, 2, jsChunkEx.WORD, 3, jsChunkEx.CHARACTER, 4);
		this.assertEquals(24, fc.startIndex);
		this.assertEquals(25, fc.endIndex);
		fc = jsChunkEx.findChunk(s, jsChunkEx.ITEM, 2, jsChunkEx.WORD, 2);
		this.assertEquals(15, fc.startIndex);
		this.assertEquals(20, fc.endIndex);
	},
	testNestedFindChunkToDelete: function() {
		var s = 'a bc def, ghij klmno pqrstu, vwxyz01 23456789 !@#$%&?[]';
		var fc = jsChunkEx.findChunkToDelete(s, jsChunkEx.ITEM, 2, jsChunkEx.WORD, 3, jsChunkEx.CHARACTER, 4);
		this.assertEquals(24, fc.startIndex);
		this.assertEquals(25, fc.endIndex);
		fc = jsChunkEx.findChunkToDelete(s, jsChunkEx.ITEM, 2, jsChunkEx.WORD, 2);
		this.assertEquals(15, fc.startIndex);
		this.assertEquals(21, fc.endIndex);
	},
	testNestedGetChunk: function() {
		var s = 'a bc def, ghij klmno pqrstu, vwxyz01 23456789 !@#$%&?[]';
		this.assertEquals('s', jsChunkEx.getChunk(s, jsChunkEx.ITEM, 2, jsChunkEx.WORD, 3, jsChunkEx.CHARACTER, 4));
	},
	testNestedDeleteChunk: function() {
		var s = 'a bc def, ghij klmno pqrstu, vwxyz01 23456789 !@#$%&?[]';
		var d1 = 'a bc def, ghij klmno pqrtu, vwxyz01 23456789 !@#$%&?[]';
		var d2 = 'a bc def, ghij pqrstu, vwxyz01 23456789 !@#$%&?[]';
		this.assertEquals(d1, jsChunkEx.deleteChunk(s, jsChunkEx.ITEM, 2, jsChunkEx.WORD, 3, jsChunkEx.CHARACTER, 4));
		this.assertEquals(d2, jsChunkEx.deleteChunk(s, jsChunkEx.ITEM, 2, jsChunkEx.WORD, 2));
	},
	testNestedReplaceChunk: function() {
		var s = 'a bc def, ghij klmno pqrstu, vwxyz01 23456789 !@#$%&?[]';
		var r1 = 'a bc def, ghij klmno pqr***tu, vwxyz01 23456789 !@#$%&?[]';
		var r2 = 'a bc def, ghij *** pqrstu, vwxyz01 23456789 !@#$%&?[]';
		this.assertEquals(r1, jsChunkEx.replaceChunk(s, jsChunkEx.ITEM, 2, jsChunkEx.WORD, 3, jsChunkEx.CHARACTER, 4, '***'));
		this.assertEquals(r2, jsChunkEx.replaceChunk(s, jsChunkEx.ITEM, 2, jsChunkEx.WORD, 2, '***'));
	},
	testNestedPrependToChunk: function() {
		var s = 'a bc def, ghij klmno pqrstu, vwxyz01 23456789 !@#$%&?[]';
		var r1 = 'a bc def, ghij klmno pqr***stu, vwxyz01 23456789 !@#$%&?[]';
		var r2 = 'a bc def, ghij ***klmno pqrstu, vwxyz01 23456789 !@#$%&?[]';
		this.assertEquals(r1, jsChunkEx.prependToChunk(s, jsChunkEx.ITEM, 2, jsChunkEx.WORD, 3, jsChunkEx.CHARACTER, 4, '***'));
		this.assertEquals(r2, jsChunkEx.prependToChunk(s, jsChunkEx.ITEM, 2, jsChunkEx.WORD, 2, '***'));
	},
	testNestedAppendToChunk: function() {
		var s = 'a bc def, ghij klmno pqrstu, vwxyz01 23456789 !@#$%&?[]';
		var r1 = 'a bc def, ghij klmno pqrs***tu, vwxyz01 23456789 !@#$%&?[]';
		var r2 = 'a bc def, ghij klmno*** pqrstu, vwxyz01 23456789 !@#$%&?[]';
		this.assertEquals(r1, jsChunkEx.appendToChunk(s, jsChunkEx.ITEM, 2, jsChunkEx.WORD, 3, jsChunkEx.CHARACTER, 4, '***'));
		this.assertEquals(r2, jsChunkEx.appendToChunk(s, jsChunkEx.ITEM, 2, jsChunkEx.WORD, 2, '***'));
	},
};
