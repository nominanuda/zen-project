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
var js = require('classpath:/com/nominanuda/js/mod/lang.js');
var dtHelper = new Packages.com.nominanuda.lang.DateTimeHelper();

var isodt_pad = function(n) {
	return n < 10 ? '0' + n : n;
};
var dateTimeISO8601_ = function(jsDt) {// see
										// https://developer.mozilla.org/en/Core_JavaScript_1.5_Reference:Global_Objects:Date
	return jsDt.getUTCFullYear() + '-' + isodt_pad(jsDt.getUTCMonth() + 1)
			+ '-' + isodt_pad(jsDt.getUTCDate()) + 'T'
			+ isodt_pad(jsDt.getUTCHours()) + ':'
			+ isodt_pad(jsDt.getUTCMinutes()) + ':'
			+ isodt_pad(jsDt.getUTCSeconds()) + 'Z';
};
js.extend(exports, {
	dateISO8601 : function(jsDt) {
		return jsDt.getUTCFullYear() + '-' + isodt_pad(jsDt.getUTCMonth() + 1)
				+ '-' + isodt_pad(jsDt.getUTCDate());
	},
	dateTimeISO8601 : dateTimeISO8601_,
	nowISO8601 : function() {
		return dateTimeISO8601_(new Date());
	},
	msecToXsdDur : function(ms) {
		return dtHelper.msecToXsdDur(ms);
	}
});

