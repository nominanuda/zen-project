/*
 * Copyright 2008-2018 the original author or authors.
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


define('zen-webapp/_alert_/basic', [
        ], function() {
	
	return {
		message: function(msg, $src, cback) {
			var result = alert(msg);
			cback && cback(result);
		},
		yesno: function(msg, $src, cback) {
			var result = (confirm(msg) == true);
			cback && cback(result);
		},
		prompt: function(msg, val, $src, cback) {
			var result = prompt(msg, val);
			cback && cback(result);
		}
	};
});