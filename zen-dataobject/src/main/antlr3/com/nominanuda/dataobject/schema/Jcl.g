grammar Jcl;

options {
output = AST;
//backtrack=true;
//k=10;

}

tokens {
OBJECT;ARRAY;NAME;ENTRY;PRIMITIVE;VALUECHOICE;
}

@header {
package com.nominanuda.dataobject.schema;
}

@lexer::header {
package com.nominanuda.dataobject.schema;
}

@members { 
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
	: value
	;

value
	: singleValue
	| choiceValue
	;

singleValue
	: primitive
	| object
	| array
	;

choiceValue
	: '('singleValue ( '|' singleValue)+')'
	-> ^(VALUECHOICE singleValue+)
	;

primitive
	: PrimitiveTkn
	-> ^(PRIMITIVE PrimitiveTkn)
	;
PrimitiveTkn
	:'n' | 's' | 'b';	

object	: '{' members? '}' 
	  -> ^(OBJECT members?)
	;
	
array	: '[' elements? ']'
	  -> ^(ARRAY elements?)
	;

elements	: value (','! value)*
	;
	
members	: entry (','! entry)*
	;
	 
//entry	: StringExpr ':' value 
//	  -> ^(ENTRY StringExpr value) 
//
entry
	: lval ':'  rval
	-> ^(ENTRY lval rval)
	| lval
	-> ^(ENTRY lval PRIMITIVE)
	;
lval
	: LvalTkn 
	-> ^(NAME LvalTkn)
	;
LvalTkn
	: ('a'..'z')+
	;

rval
	: value
	;

WS
	: (' '|'\n'|'\r'|'\t')+ {$channel=HIDDEN;} ;
