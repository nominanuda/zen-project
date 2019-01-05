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


define('zen-webapp/widget/sorter', [
        'jquery',
        'zen-webapp/util/dom',
        'zen-webapp-lib/jqueryui'
        ], function($, UTIL_DOM) {
	
	
	var CLASS_PARENT = 'zen-webapp-sortable';
	var CLASS_ROOT = 'zen-webapp-sorters'
	var SELECTOR_WIDGET = '.zen-webapp-sorter';
	
	
	function init($sortable) {
		$sortable.sortable({
			tolerance: 'pointer'
		}).addClass(CLASS_ROOT);
	}
	
	
	function widget($elms) {
		var $sortables = [];
		UTIL_DOM.findter(SELECTOR_WIDGET, $elms).parent().addClass(CLASS_PARENT).parent().each(function() {
			init($(this));
		});
	}
	
	
	return {
		init: init,
		widget: widget
	};
});