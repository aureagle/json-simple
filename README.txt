Modified version of json-simple to remove raw types, and better handling of stream readers.

The original lexer has an issue where it consumes characters that may not be parsed from within JSONParser.  If the JSONParser completes before exhausting the internal buffer within the lexer, then those extra characters are discarded and lost.  This makes stream processing of JSON responses impossible.

The code has been modified so that characters in the lexer are consumed only as they are processed.  To improve reading efficiency, wrap any readers with a BufferedReader.  This modification should allow sequential processing of JSON objects coming from the same Reader.

Original project site:
http://code.google.com/p/json-simple/

Original GitHub clone:
https://github.com/fangyidong/json-simple.git
