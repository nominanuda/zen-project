/*
 * Copyright 2008-2011 the original author or authors.
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
grammar UriSpec;


options {
//ASTLabelType=CommonTree;
//output=AST;
language=Java;
//k=1;
//backtrack=true;
}
tokens {
ANONYMOUS_EXPRESSION,
CHARACTERS,
NAMED_EXPRESSION,
PARAM_DECL,
PARAM_GROUP,
SEQUENCE,
PARAM_SEQUENCE,
STRICT_PARAMS,
URI_SPEC,
SCHEME_AUTH_PATH_PART,
QUERY_PART,
FRAGMENT_PART
}

program	:	urispec EOF 
	//-> 	^( URI_SPEC urispec )
        ;
urispec	:	schemeAuthorityAndPath (QMARK/*!*/ query?)? (POUND/*!*/ fragment_?)?;
schemeAuthorityAndPath 	:	sequence
	//->	^(SCHEME_AUTH_PATH_PART sequence)
                        ;
fragment_ 	:	sequence
	//->	^(FRAGMENT_PART sequence)
            ;
sequence	:	sequence_t
	//->	^(SEQUENCE sequence_t)
            ;
sequence_t	:	(namedExpr | anonExpr|uriChars)+;
anySequence	:	anySequence_t
	//->	^(SEQUENCE anySequence_t)
            ;
anySequence_t:	(namedExpr | anonExpr|anyChars)+;

uriChars	:	uriChars_t
	//-> 	^(CHARACTERS uriChars_t)
            ;
uriChars_t:	(URI_CHARS)+;
anyChars:	anyChars_t 
	//->	^(CHARACTERS anyChars_t)
        ;
anyChars_t:	(URI_CHARS|MATCHER)+;

namedExpr	:	namedExprTokens
	//->	^(NAMED_EXPRESSION namedExprTokens)
            ;
namedExprTokens:	BEGIN_NAMED_EXPR/*!*/ varName (WS/*!*/ anySequence ( ALTERNATIVE/*!*/ anySequence )*)? (ALTERNATIVE/*!*/)? END_NAMED_EXPR/*!*/;

anonExpr	:	anonExprTokens
	//->	^(ANONYMOUS_EXPRESSION anonExprTokens )
            ;
anonExprTokens:	anonExprTokens1 | anonExprTokens2 | anonExprTokens3; 
anonExprTokens1:	(BEGIN_ANON_EXPR/*!*/ sequence ( ALTERNATIVE/*!*/ sequence )* END_ANON_EXPR/*!*/);
anonExprTokens2
	:		
		(BEGIN_ANON_EXPR sequence ( ALTERNATIVE sequence )* ALTERNATIVE END_ANON_EXPR)
	//-> sequence* ^(SEQUENCE ^(CHARACTERS URI_CHARS[""]))
		;
anonExprTokens3
	:		
		(BEGIN_ANON_EXPR ALTERNATIVE sequence ( ALTERNATIVE sequence )* END_ANON_EXPR)
	//-> ^(SEQUENCE ^(CHARACTERS URI_CHARS[""])) sequence* 
		;
                
varName 	:	URI_CHARS;

query	:	pSeq paramSep //-> ^(QUERY_PART pSeq STRICT_PARAMS)
	|	pSeq //-> ^(QUERY_PART pSeq)
	;
pSeq	:	pSeqTokens
	//->	^(PARAM_SEQUENCE pSeqTokens)
        ;
pSeqTokens	:	pExpr (paramSep/*!*/ pExpr)*
;
pExpr	:	(pDecl | paramsGroup)
	//->	^(PARAM_DECL pDecl)? ^(PARAM_GROUP paramsGroup)?
        ;
pDecl	:	namedExpr |paramName EQUALS/*!*/ sequence?;

paramsGroup	: 	BEGIN_ANON_EXPR/*!*/ pSeq ( ALTERNATIVE/*!*/ pSeq )* END_ANON_EXPR/*!*/;
paramName	:	URI_CHARS;


paramSep: WS |
AMPERSAND; 
//TOKENS
AMPERSAND:  '&';
MATCHER	: 
	('*'
	(  '~' ('\\' '~'|~'~')+ '~')
	| ('*' ('\\' '*'|~'*')*)
	'*') ;
ALTERNATIVE	: '|';
BEGIN_NAMED_EXPR : '{';
END_NAMED_EXPR : '}';
URI_CHARS:	(
	UNRESERVED_CHAR | PERCENT_ENCODED_CHAR
	|'/'|'$'|':'|'@'|'!'|'\''|';'|'+'|','|'['|']' //reserved unused
	|'\\' '('|'\\' ')'|'\\' '*' //escaped reserved used by language
	|'\\' '&' |'\\' '='|'\\' '?' |'\\' '#'//escaped reserved used by http scheme
	)+;
BEGIN_ANON_EXPR : '(';
END_ANON_EXPR : ')';
EQUALS: '=';
POUND:'#';
QMARK:'?';
WS	: ' ';

//FRAGMENTS
//RESERVED ! * ' ( ) ; : @ & = + $ , / ? # [ ]
fragment PERCENT_ENCODED_CHAR:'%' ('a'..'f' | 'A'..'F'|'0'..'9')('a'..'f' | 'A'..'F'|'0'..'9');
fragment UNRESERVED_CHAR : '0'..'9' | 'a'..'z' | 'A'..'Z' | '-' | '_' | '.' | '~';
