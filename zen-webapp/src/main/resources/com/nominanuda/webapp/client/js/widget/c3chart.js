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



define('zen-webapp/widget/c3chart', [
        'jquery',
        'zen-webapp/util/css',
        'zen-webapp/util/dom',
        'zen-webapp-lib/d3js/c3js/c3.min'
        ], function($, UTIL_CSS, UTIL_DOM, C3) {
	
	var DATA_CHART_REF = 'chart-ref';
	var SELECTOR_WIDGET = '.zen-webapp-c3chart';
	
	
	UTIL_CSS.loadAbs([
		'zen-webapp-lib/d3js/c3js/c3.min'
	]);
	
	
	function init($chart, config) {
		config.bindto = $chart.get(0);
		return C3.generate(config);
	}
	
	
	function widget($elms) {
		UTIL_DOM.findter(SELECTOR_WIDGET, $elms).each(function() {
			var $this = $(this);
//			init($this, [config extracted from contained table]); TODO
		});
	}
	
	return {
		init: init,
		widget: widget
	};
});