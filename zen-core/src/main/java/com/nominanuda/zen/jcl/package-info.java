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
/**
<pre>
Jcl is a costraining formalism for JSON.

 It is implemented as an ANTLR 4 grammar, so usable in java and javascript.

Its intended applications are type checking, value representation templating, 
code generation and object stubbing.

Jcl expression are of two kinds, one is to specify an unnamed type eg. {/} 
(the empty object); the other is a set of named type definitions that form 
a library eg. T1 [/] (type named T1  as the empty array).

 Unnamed expression can be evaluated in the context of library defined types 
as in {a:T1}; where T1 is a type reference.

Object and array definitions are open; that is, they validate additional 
properties and values not defined; thus for instance the object 
{"a":null,"b":null} is validated by the expression {a:null}.
 To avoid this, the seal operator '/' can be added as last member.
 In the preceding example the expression {a:null /} prohibits the 'b' property.
 As a consequence, {} is any object and [] is any array.

 Primary types loosely follow the JSON specification with the notable exception
for the null value that can be an instance of any type.
 They can be validated by builtin validators as follows:
i - integer values
p TODO better name - non negative integer values
n - any number
s -string
b - boolean
a - any primitive
As such [1,false,"xxx"] is an instance of [n,b,s].
 The string type is the default type for object entries, and so {a:s} can be 
written {a}

 Additional validators (not just for primitives) can be added by a 
Validator spi and accept any expression.
 They must be delimited by '*' symbols, as in *email* or 
 *int 0 &lt;= x &lt; 10*. 
 '**' is used as a wildcard for any value, primitive or not.

 Named types can be reused as references all over the places where a value is 
expected.
 Arrays are used for tuples and collections, when used as tuples all its member 
value types are declared and the array is size bounded.
 Conversely if just one value type is specified, the array is considered an 
unbounded sequence of the given type.
[] is any array, functionally equivalent to [**]
[i,i,i] is a tuple3 of integers
[{}] an unbounded array of objects
and [/] the empty array
a one string value tuple is specified as this [s/]

 Objects are defined specifying it members and optionally sealing them.
 They also support expansion as a rudimentary form of inheritance.
 So given the type definitions A {a:i} B {b:i} C {c:i},
the expression {A,B,foo:C} is the equivalent of {a:i,b:i,foo:{c:i}}

 Jcl also supports existential predicates, they are meant to capture 
nullability of values and optionality of object properties.
{
a:i?,//a nullable non optional integer property
b?:i,//an optional non nullable integer property
c?:s?,//an optional and nullable string property
d??,//a shorthand for c?:s?
e?,//a shorthand for e:s? and not for e?:s
}
 Accordingly this object collection can contain nulls [{}?]
for array elements also the postfix + sign is used as a specifier of a non 
empty sequence. [i]+ does not validate [] and [i?]+ validates [null] but not []

 Values can be expressed by type(builtin, Validator spi, 
type reference) or can be literals as: null, 1.2, true, 'foobar' 
(single quotes are used for strings). Choice values are supported as a 
combination of the above methods following the (A1|A2|A3) syntax; 
a full example is {a:(1|s|[/]|null|MyType|'kissme')}

 Comments are expressed in both C++ styles.

 Templates are a primitive form of type parametrization loosley modeled after 
C macros. So for instance, given the following library
Response&lt;T,A&gt; {count:i,results:[T],attachment:A} 
which introduces two type parameters,
Item {title}
SearchResponse&lt;A&gt; Response&lt;Item,A&gt; 
HomeSearchResponse SearchResponse&lt;{banners:[s]}&gt; 

 HomeSearchResponse validates 
{"count":10,"results":[{"title":"foo"}],"attachment":{"banners":["bar"]}}

 In this other case
Response&lt;T&gt; {results:T}
Items&lt;T&gt; [T]

Response&lt;Items&lt;({msg?}|b)&gt;&gt; validates {"results":[true,{"msg":null}]}
</pre>
 */
package com.nominanuda.zen.jcl;