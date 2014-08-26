OpenXION
========

XION *(EK-shun)* is a kind of scripting language that enables ordinary people to do extraordinary things. You do not need to learn a bunch of cryptic symbols and how to put them in exactly the right places in order to tell your computer what to do. Since XION has been created to resemble natural English, all you need is a basic understanding of the English language.

XION is an xTalk language similar to the ones used by HyperCard, SuperCard, and Runtime Revolution. OpenXION *(OH-pen-EK-shun)* is the reference implementation of the XION scripting language, an open standard.

A typical XION function looks like:

    function isprime x
      repeat with i = 2 to the sqrt of x
        if x mod i = 0 then
          return false
        end if
      end repeat
      return true
    end isprime

Version 1.x of OpenXION is written in Java for maximum portability and exposure. This also allows us to easily squash the most glaring bugs early in the interpreter's development. Version 2 will be ported to a lower-level language.
