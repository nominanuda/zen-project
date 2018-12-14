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


define('zen-webapp/util/soy', [
	    'jquery',
	    'require'
        ], function($, require) {
	
	
	var PATH_POSTFIX = '___soy'; // as in boot.soy
	
	return {
		load: function(templates, cback) {
			templates = $.map(templates ? templates.splice ? templates : [templates] : [], function(template) {
				var i = template.indexOf('/'); // prefix cut point
				return template.substr(0, i) + PATH_POSTFIX + template.substr(i) + '.soy';
			});
			if (templates.length) {
				require(templates, function() {
					cback && cback();
				});
			} else {
				cback && cback(); // call in any case
			}
		}
	};
});