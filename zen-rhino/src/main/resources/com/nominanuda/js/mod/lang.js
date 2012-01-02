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
var extend_ = function(o1, o2, deep) {
	var _deep = true === deep;
	if (_deep) {
		throw "deep exended not implemented";
	} else {
		for(var p in o2) {
			o1[p] = o2[p];
		}
	}
	return o1;
};
var notEmpty_ = function(val) {
	return "" !== val && val !== null && (typeof val !== "undefined");
};
var isEmpty_ = function(val) {
	return !notEmpty_(val);
};
var isset_ = function(val) {
	val !== null && (typeof val !== "undefined");
};
// ///////////////////
extend_(exports, {
	isset : isset_,
	unset : function(x) { return ! isset_(x); },
	notEmpty : notEmpty_,
	isEmpty : isEmpty_,
	unsetDel : function(o) {
		for ( var p in o) {
			if (! isset_(o[p])) {
				delete o[p];
			}
		}
		return o;
	},
	extend : extend_,
	isArray : function(a) {
		return !!a.splice;
	},
	throwError : function(msg) {
		throw Error((msg || "unspecified error"));
	},
	assert : function(x, msg) {
		if (!!!x) {
			throw Error("assertion failed " + (msg || ""));
		}
		return x;
	}
});