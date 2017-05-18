/*
 * Copyright 2008-2016 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
grammar SimpleJson;

options {
language=Java;
}

program
	: value EOF
	;

value
	: object
	| array
	| number
	| bool
	| nullValue
	| string
	;

object	:
	'{' members? '}' 
	;

array	:
	'[' elements? ']'
	;

elements	: value (',' value)*
	;

members	: pair (','? pair)*
	;

pair	: string ':'? value
	;
string
	: StringExpr
	| UnquotedString
	;

number	: NumberExpr
	;

bool	: 'true' | 'false';

nullValue : 'null' ;

/* Lex */
NumberExpr	: INT FRAC?
	;

StringExpr 	:
	'\'' ( ESCAPEDCHAR | ~('\u0000'..'\u001f' | '\\' | '\'' ) )* '\''
	;
UnquotedString	:
	('a'..'z'|'A'..'Z'|'_'|'$')('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'$'|'-')*
	;

fragment ESCAPEDCHAR
    	:   '\\' (UCODEPOINT |'b'|'t'|'n'|'f'|'r'|'"'|'\''|'\\'|'\\/')
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

WS
	: (' '|'\n'|'\r'|'\t')+ -> skip ;
