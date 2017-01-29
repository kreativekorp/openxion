var saveSelection, restoreSelection;
if (window.getSelection && document.createRange) {
    saveSelection = function(containerEl) {
        var doc = containerEl.ownerDocument, win = doc.defaultView;
        var range = win.getSelection().getRangeAt(0);
        var preSelectionRange = range.cloneRange();
        preSelectionRange.selectNodeContents(containerEl);
        preSelectionRange.setEnd(range.startContainer, range.startOffset);
        var start = preSelectionRange.toString().length;
        return {
            start: start,
            end: start + range.toString().length
        }
    };
    restoreSelection = function(containerEl, savedSel) {
        var doc = containerEl.ownerDocument, win = doc.defaultView;
        var charIndex = 0, range = doc.createRange();
        range.setStart(containerEl, 0);
        range.collapse(true);
        var nodeStack = [containerEl], node, foundStart = false, stop = false;
        while (!stop && (node = nodeStack.pop())) {
            if (node.nodeType == 3) {
                var nextCharIndex = charIndex + node.length;
                if (!foundStart && savedSel.start >= charIndex && savedSel.start <= nextCharIndex) {
                    range.setStart(node, savedSel.start - charIndex);
                    foundStart = true;
                }
                if (foundStart && savedSel.end >= charIndex && savedSel.end <= nextCharIndex) {
                    range.setEnd(node, savedSel.end - charIndex);
                    stop = true;
                }
                charIndex = nextCharIndex;
            } else {
                var i = node.childNodes.length;
                while (i--) {
                    nodeStack.push(node.childNodes[i]);
                }
            }
        }
        var sel = win.getSelection();
        sel.removeAllRanges();
        sel.addRange(range);
    }
} else if (document.selection) {
    saveSelection = function(containerEl) {
        var doc = containerEl.ownerDocument, win = doc.defaultView || doc.parentWindow;
        var selectedTextRange = doc.selection.createRange();
        var preSelectionTextRange = doc.body.createTextRange();
        preSelectionTextRange.moveToElementText(containerEl);
        preSelectionTextRange.setEndPoint('EndToStart', selectedTextRange);
        var start = preSelectionTextRange.text.length;
        return {
            start: start,
            end: start + selectedTextRange.text.length
        }
    };
    restoreSelection = function(containerEl, savedSel) {
        var doc = containerEl.ownerDocument, win = doc.defaultView || doc.parentWindow;
        var textRange = doc.body.createTextRange();
        textRange.moveToElementText(containerEl);
        textRange.collapse(true);
        textRange.moveEnd('character', savedSel.end);
        textRange.moveStart('character', savedSel.start);
        textRange.select();
    };
}

jsChunkExDemo = {
	highlightChunks: function(div, type) {
		var text = div.get(0).innerText || div.get(0).textContent;
		var chunks = jsChunkEx.splitChunks(text, type);
		div.empty();
		var o = 0;
		for (var i = 0; i < chunks.length; i++) {
			var c = chunks[i];
			if (c.startIndex > o) div.append($('<span>').text(text.substring(o, c.startIndex)).addClass('chunk-gap'));
			if (c.endIndex > c.startIndex) div.append($('<span>').text(text.substring(c.startIndex, c.endIndex)).addClass('chunk-content').addClass('chunk-content-' + (i % 5)).attr('title', type + ' ' + (i + 1)));
			o = c.endIndex;
		}
		if (text.length > o) div.append($('<span>').text(text.substring(o, text.length)).addClass('chunk-gap'));
		div.unbind('keyup');
		div.bind('keyup', function() {
			var ss = saveSelection(div.get(0));
			jsChunkExDemo.highlightChunks(div, type);
			restoreSelection(div.get(0), ss);
		});
	}
};
