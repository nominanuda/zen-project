grammar Jcl;

options {
output = AST;
backtrack=true;
//k=10;

}

tokens {
OBJECT;ARRAY;NAME;ENTRY;PRIMITIVE;VALUECHOICE;VALUESEQ;ENTRYSEQ;TYPEDEF;TYPEREF;EXISTENTIAL;ARRAYVAL;
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
	| (LvalTkn '@' value)+
	 -> ^(TYPEDEF LvalTkn value)+
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
	: LvalTkn
	-> ^(PRIMITIVE LvalTkn)//TODO token overlap
	;
//PrimitiveTkn
//	:'n' | 's' | 'b';	

object	: '{' members?  '}' 
	  -> ^(OBJECT members?)
	;
	
array	: '[' elements? ']'
	  -> ^(ARRAY elements?)
	;

existentialValue	: rval ExistentialTkn
		->  ^(ARRAYVAL rval ^(EXISTENTIAL ExistentialTkn))
		| rval
		->  ^(ARRAYVAL rval EXISTENTIAL)
	; 

elements	: valueseq | (existentialValue (','! existentialValue)* (','! valueseq)?)
//elements	: value (','! value)* 
	;

valueseq	: '*' rval? -> ^(VALUESEQ rval?);

members	: entryseq | (entry (','! entry)* (','! entryseq)?)
	;

entryseq:	'*' rval? -> ^(ENTRYSEQ rval?);
//entry	: StringExpr ':' value 
//	  -> ^(ENTRY StringExpr value) 
//
entry
	: lval ':'  rval
	 -> ^(ENTRY lval rval)
	| lval
	 -> ^(ENTRY lval PRIMITIVE)
//	| '*'
//	 -> ENTRYSEQ
	;
lval
	: LvalTkn 
	-> ^(NAME LvalTkn) EXISTENTIAL
	| LvalTkn ExistentialTkn
	-> ^(NAME LvalTkn) ^(EXISTENTIAL ExistentialTkn)
	;
ExistentialTkn
	: '!?'|'?!'|'!'|'?'
	;

LvalTkn
	: ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'$')+
	;

rval
	: value
	| '@' LvalTkn
	 -> ^(TYPEREF LvalTkn)
	;

WS
	: (' '|'\n'|'\r'|'\t')+ {$channel=HIDDEN;} ;
COMMENT
	: '/*' (options {greedy=false;} : .)* '*/' {$channel=HIDDEN;};
