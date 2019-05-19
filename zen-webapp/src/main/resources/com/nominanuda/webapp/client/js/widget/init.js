/*
 * Copyright 2008-2019 the original author or authors.
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


define('zen-webapp/widget/init', [
        'jquery',
        'require',
        'zen-webapp/util/dom'
        ], function($, require, UTIL_DOM) {

	var CLASS_COMBOX = 'combox';
	var CLASS_ELASTIC = 'zen-webapp-elastic';
	var CLASS_INPUTMASK = 'zen-webapp-inputmask';
	
	var REQUIRE_ELASTIC = 'zen-webapp-lib/jquery/jquery.elastic';
	var REQUIRE_INPUTMASK = 'zen-webapp-lib/inputmask';

	var SELECTOR_ELASTIC = 'input.' + CLASS_ELASTIC + ',textarea.' + CLASS_ELASTIC;
	var SELECTOR_INPUTMASK = 'input.' + CLASS_INPUTMASK + ',textarea.' + CLASS_INPUTMASK;
	
	
	return function($html) {
		UTIL_DOM.widgetize($html, CLASS_COMBOX);
		
		var $elastics = UTIL_DOM.findter(SELECTOR_ELASTIC, $html);
		if ($elastics.length) {
			require([REQUIRE_ELASTIC], function(ELASTIC) {
				UTIL_DOM.findter(SELECTOR_ELASTIC, $html).elastic({ // need to find again elements (using $elastics doesn't work... why?)
					clss: CLASS_ELASTIC
				});
			});
		}
		
		var $inputmasks = UTIL_DOM.findter(SELECTOR_INPUTMASK, $html);
		if ($inputmasks.length) {
			require([REQUIRE_INPUTMASK], function(INPUTMASK) {
				INPUTMASK(UTIL_DOM.findter(SELECTOR_INPUTMASK, $html)); // need to find again elements (using $inputmasks doesn't work... why?)
			});
		}
		
		return $html;
	};
});