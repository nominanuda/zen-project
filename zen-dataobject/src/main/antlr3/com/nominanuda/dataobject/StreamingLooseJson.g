grammar StreamingLooseJson;

options {
output = AST;
}

tokens {
OBJECT;ARRAY;OBJECTENTRY;STRING;NUMBER;TRUE;FALSE;NULL;
}

@header {
package com.nominanuda.dataobject;
}

@lexer::header {
package com.nominanuda.dataobject;
}

@members { 
	ParserUtils utils = new ParserUtils();
	JsonContentHandler ch = new DataStructContentHandler();
	public void setJsonContentHandler(JsonContentHandler jch) {
		ch = jch;
	}
	public JsonContentHandler getJsonContentHandler() {
		return ch;
	}
	protected void mismatch(IntStream input, int ttype, BitSet follow) 
	throws RecognitionException 
	{ 
	throw new MismatchedTokenException(ttype, input); 
	} 
	public Object recoverFromMismatchedSet(IntStream input, 
	RecognitionException e, 
	BitSet follow) 
	throws RecognitionException 
	{ 
	throw e; 
	} 
} 

@rulecatch { 
catch (RecognitionException e) { 
throw e; 
} 
} 

program
	:
	{try{ch.startJSON();}catch(Exception e){throw new WrappingRecognitionException(e);}}
	datastruct
	{try{ch.endJSON();}catch(Exception e){throw new WrappingRecognitionException(e);}}
	;

datastruct
	: (object | array)
	;

value
	: object
	| array
	| string
	| number
	| 'true' {try{ch.primitive(Boolean.TRUE);}catch(Exception e){throw new WrappingRecognitionException(e);}}
	| 'false' {try{ch.primitive(Boolean.FALSE);}catch(Exception e){throw new WrappingRecognitionException(e);}}
	| 'null' {try{ch.primitive(null);}catch(Exception e){throw new WrappingRecognitionException(e);}}
	;

object	: 
	{try{ch.startObject();}catch(Exception e){throw new WrappingRecognitionException(e);}}
	'{' members? '}' 
	{try{ch.endObject();}catch(Exception e){throw new WrappingRecognitionException(e);}}
	;
	
array	:
	{try{ch.startArray();}catch(Exception e){throw new WrappingRecognitionException(e);}} 
	'[' elements? ']'
	{try{ch.endArray();}catch(Exception e){throw new WrappingRecognitionException(e);}}
	;

elements	: value (',' value)*
	;
	
members	: pair (',' pair)*
	;

pair	: k=ObjectKey
	{try{ch.startObjectEntry(utils.parseKey(k));}catch(Exception e){throw new WrappingRecognitionException(e);}} 
	 ':' value
	{try{ch.endObjectEntry();}catch(Exception e){throw new WrappingRecognitionException(e);}}
	;
ObjectKey	:
	('a'..'z'|'A'..'Z'|'_'|'$') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'$')*
	;
string 	: StringExpr {try{ch.primitive(utils.parseString($StringExpr));}catch(Exception e){throw new WrappingRecognitionException(e);}}
	;

number	: NumberExpr {try{ch.primitive(utils.parseNumber($NumberExpr));}catch(Exception e){throw new WrappingRecognitionException(e);}}
	;

NumberExpr	: INT FRAC? EXP?
	;

StringExpr 	:
	'\'' ( ESCAPEDCHAR | ~('\u0000'..'\u001f' | '\\' | '\'' ) )* '\''
	;


fragment ESCAPEDCHAR
    	:   '\\' (UCODEPOINT |'b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\'|'\/')
    	;

fragment UCODEPOINT
	: 'u' HEX HEX HEX HEX
	;

fragment HEX
	: '0'..'9' | 'A'..'F' | 'a'..'f'
	;

fragment DIGIT
	: '0'..'9'
	;

fragment INT	
	:  '-'? DIGIT+
	;

fragment FRAC
	:  '.' DIGIT+
	;

fragment EXP	
	: ('E'|'e') '-'? DIGIT+
	;

WS
	: (' '|'\n'|'\r'|'\t')+ {$channel=HIDDEN;} ;
