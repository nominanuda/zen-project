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
grammar Jcl;

options {
language=Java;
}
/*
tokens {
OBJECT;ARRAY;NAME;ENTRY;PRIMITIVE;VALUECHOICE;VALUESEQ;ENTRYSEQ;TYPEDEF;TYPEREF;EXISTENTIAL;ARRAYVAL;
}
 */

program
	: jcl //EOF /*!*/
	;

jcl
	: typeDecl | (namedTypeDecl)+
	;

namedTypeDecl
	: typeNameDecl typeDecl
	;

typeNameDecl
	: typeName ( genericArgDecl )?
	;

genericArgDecl
	: Less typeName (Comma typeName)* Greater
	;

typeDecl
	: singleTypeDecl (Pipe singleTypeDecl)*
	;

singleTypeDecl
	: (primitiveLiteral | primitive | array | object | Any | validatorExpr | singleTypeRef) ExistentialSymbol?
	;


singleTypeRef
	: typeName genericArgRef ?
	;

genericArgRef
	: Less singleTypeDecl (Comma singleTypeDecl)* Greater
	;

array
	: sequenceOfAny | emptyArray | tuple | sequence
	;

sequenceOfAny
	: LeftBracket Any? RightBracket cardinalityPredicate?
	;

emptyArray
	: LeftBracket Slash RightBracket
	;

tuple
	: LeftBracket typeDecl (Slash | (Comma typeDecl)+) RightBracket
	;

sequence
	: LeftBracket typeDecl RightBracket cardinalityPredicate?
	;

cardinalityPredicate
	: Plus
	;

validatorExpr
	: ValidatorExpr
	;

object
	: LeftBrace members? Slash? RightBrace
	;

members
	: member (Comma member)* 
	;

member
	: key ExistentialSymbol ExistentialSymbol
	| key ExistentialSymbol
	| key ExistentialSymbol? Colon typeDecl
	;

key
	: id | Integer | PositiveInteger | Number | String | Boolean | AnyPrimitive
	;
	
id
	: StringLiteral | Identifier
	;

primitiveLiteral
	: True | False | Null | IntegerLiteral | StringLiteral | FloatLiteral
	;

primitive
	: Integer | Number | String | Boolean | AnyPrimitive | PositiveInteger
	;

typeName
	: Identifier
	;

Star : '*';
Any : '**';
ValidatorExpr : 'TODO';
ExistentialSymbol : '?';
Colon : ':';
Plus : '+';
Pipe : '|';
Comma : ',';
LeftBracket : '[';
RightBracket : ']';
LeftBrace : '{';
RightBrace : '}';
Slash : '/';
Less : '<';
Greater : '>';
Integer : 'i';
PositiveInteger : 'p';
Number : 'n';
String : 's';
Boolean : 'b';
AnyPrimitive: 'a';
True : 'true';
False : 'false';
Null : 'null';


Identifier
    :   IdentifierNondigit
        (   IdentifierNondigit
        |   Digit
        )*
    ;

fragment
IdentifierNondigit
    :   [a-zA-Z_]
    ;

fragment
Digit
    :   [0-9]
    ;

IntegerLiteral : Digit+;
fragment
Dot : '.';

FloatLiteral : Digit+ Dot Digit+;

//StringLiteral : '"www"';

StringLiteral
    :  '"' SCharSequence? '"'
    ;
fragment
SCharSequence
    :   SChar+
    ;
fragment
SChar
    :   ~["\\\r\n]
    |   EscapeSequence
    ;

//fragment
//UniversalCharacterName
//    :   '\\u' HexQuad
//    |   '\\U' HexQuad HexQuad
//    ;
//
//fragment
//HexQuad
//    :   HexadecimalDigit HexadecimalDigit HexadecimalDigit HexadecimalDigit
//    ;
//
//Constant
//    :   IntegerConstant
//    |   FloatingConstant
//    //|   EnumerationConstant
//    |   CharacterConstant
//    ;
//
//fragment
//IntegerConstant
//    :   DecimalConstant IntegerSuffix?
//    |   OctalConstant IntegerSuffix?
//    |   HexadecimalConstant IntegerSuffix?
//    |	BinaryConstant
//    ;
//
//fragment
//BinaryConstant
//	:	'0' [bB] [0-1]+
//	;
//
//fragment
//DecimalConstant
//    :   NonzeroDigit Digit*
//    ;
//
//fragment
//OctalConstant
//    :   '0' OctalDigit*
//    ;
//
//fragment
//HexadecimalConstant
//    :   HexadecimalPrefix HexadecimalDigit+
//    ;
//
//fragment
//HexadecimalPrefix
//    :   '0' [xX]
//    ;
//
//fragment
//NonzeroDigit
//    :   [1-9]
//    ;
//
//fragment
//OctalDigit
//    :   [0-7]
//    ;
//
//fragment
//HexadecimalDigit
//    :   [0-9a-fA-F]
//    ;
//
//fragment
//IntegerSuffix
//    :   UnsignedSuffix LongSuffix?
//    |   UnsignedSuffix LongLongSuffix
//    |   LongSuffix UnsignedSuffix?
//    |   LongLongSuffix UnsignedSuffix?
//    ;
//
//fragment
//UnsignedSuffix
//    :   [uU]
//    ;
//
//fragment
//LongSuffix
//    :   [lL]
//    ;
//
//fragment
//LongLongSuffix
//    :   'll' | 'LL'
//    ;
//
//fragment
//FloatingConstant
//    :   DecimalFloatingConstant
//    |   HexadecimalFloatingConstant
//    ;
//
//fragment
//DecimalFloatingConstant
//    :   FractionalConstant ExponentPart? FloatingSuffix?
//    |   DigitSequence ExponentPart FloatingSuffix?
//    ;
//
//fragment
//HexadecimalFloatingConstant
//    :   HexadecimalPrefix HexadecimalFractionalConstant BinaryExponentPart FloatingSuffix?
//    |   HexadecimalPrefix HexadecimalDigitSequence BinaryExponentPart FloatingSuffix?
//    ;
//
//fragment
//FractionalConstant
//    :   DigitSequence? '.' DigitSequence
//    |   DigitSequence '.'
//    ;
//
//fragment
//ExponentPart
//    :   'e' Sign? DigitSequence
//    |   'E' Sign? DigitSequence
//    ;
//
//fragment
//Sign
//    :   '+' | '-'
//    ;
//
//fragment
//DigitSequence
//    :   Digit+
//    ;
//
//fragment
//HexadecimalFractionalConstant
//    :   HexadecimalDigitSequence? '.' HexadecimalDigitSequence
//    |   HexadecimalDigitSequence '.'
//    ;
//
//fragment
//BinaryExponentPart
//    :   'p' Sign? DigitSequence
//    |   'P' Sign? DigitSequence
//    ;
//
//fragment
//HexadecimalDigitSequence
//    :   HexadecimalDigit+
//    ;
//
//fragment
//FloatingSuffix
//    :   'f' | 'l' | 'F' | 'L'
//    ;
//
//fragment
//CharacterConstant
//    :   '\'' CCharSequence '\''
//    |   'L\'' CCharSequence '\''
//    |   'u\'' CCharSequence '\''
//    |   'U\'' CCharSequence '\''
//    ;
//
//fragment
//CCharSequence
//    :   CChar+
//    ;
//
//fragment
//CChar
//    :   ~['\\\r\n]
//    |   EscapeSequence
//    ;
fragment
EscapeSequence
    :   '\\' ['"?abfnrtv\\]
    ;



Whitespace
    :   [ \t]+
        -> skip
    ;

Newline
    :   (   '\r' '\n'?
        |   '\n'
        )
        -> skip
    ;

BlockComment
    :   '/*' .*? '*/'
        -> skip
    ;

LineComment
    :   '//' ~[\r\n]*
        -> skip
    ;