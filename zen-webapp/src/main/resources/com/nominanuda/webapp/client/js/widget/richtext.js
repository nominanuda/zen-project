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


require.config({
	shim: {
		'zen-webapp-lib/wysihtml5/wysihtml5-0.3.0.min': {
			exports: 'wysihtml5'
		}
	}
});


define('zen-webapp/edit/richtext', [
        'jquery',
        'require',
        'zen-webapp-lib/wysihtml5/wysihtml5-0.3.0.min',
        'zen-webapp/util/dom'
        ], function($, require, WYSIHTML5, UTIL_DOM) {
	
	
	var DATA_RULES = 'rules';
	var DATA_STYLE = 'style';
	var SELECTOR_WIDGET = '.zen-webapp-richtext';
	
	
	function notAllowed(tags, tag) {
		return !tags[tag] || tags[tag].remove || tags[tag].rename_tag;
	}
	
	
	function init($richtext) {
		var rules = $richtext.data(DATA_RULES) || 'zen-webapp-lib/wysihtml5/rules/simple';
		var style = $richtext.data(DATA_STYLE) || 'zen-webapp-lib/wysihtml5/wysihtml5';
		
		require([rules], function(RULES) {
			var tags = RULES.tags || {};
			var $toolbar = $richtext.children('.toolbar');
			notAllowed(tags, 'a') && $('.button.a', $toolbar).remove();
			notAllowed(tags, 'b') && notAllowed(tags, 'strong') && $('.button.b', $toolbar).remove();
			notAllowed(tags, 'i') && notAllowed(tags, 'em') && $('.button.i', $toolbar).remove();
			notAllowed(tags, 'ol') && $('.button.ol', $toolbar).remove();
			notAllowed(tags, 'ul') && $('.button.ul', $toolbar).remove();
			var editor = new WYSIHTML5.Editor($richtext.children('textarea').get(0), {
				toolbar: $toolbar.get(0),
				stylesheets: [require.toUrl(style + '.css')],
				parserRules: RULES
			});
			
			if ($richtext.hasClass('elastic')) {
				var $iframe = $('iframe', $richtext);
				var $body = $iframe.contents().find('body').css('overflow', 'hidden');
				function resize() {
					$iframe.height($body.prop('scrollHeight'));
				}
				editor.on('load', function() {
					editor.on('aftercommand:composer', resize);
					editor.on('newword:composer', resize);
					editor.on('change:composer', resize);
					editor.on('paste:composer', resize);
					editor.on('focus:composer', resize);
					editor.on('blur:composer', resize);
					resize();
				});
			}
		});
	}
	
	
	function widget($elms) {
		UTIL_DOM.findter(SELECTOR_WIDGET, $elms).each(function() {
			init($(this));
		});
	}
	

	return {
		init: init,
		widget: widget
	};
});