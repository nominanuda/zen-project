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


define('zen-webapp/widget/keywords', [
        'jquery',
        'zen-webapp/util/dom',
        'zen-webapp/widget/init',
        'zen-webapp-lib/jqueryui'
        ], function($, UTIL_DOM, WIDGET_INIT) {
	
	
	var SELECTOR_WIDGET = '.zen-webapp-keywords';
	
	
	function cleanup($field) {
		var kword = $.trim($field.val());
		$field.val(kword);
		if (kword) {
			$field.closest('label').siblings('label').filter(function() {
				return ($('.text,textarea', $(this)).val() == kword)
			}).remove();
			return true;
		}
		return false;
	}
	
	function remove($label) {
		if ($label.siblings('label').not('.ui-sortable-helper').length > 0) {
			$label.fadeOut(function() {
				$(this).remove();
			});
			return true;
		}
		return false;
	}
	
	
	function init($keywords) {
		$keywords
			.on('keydown', '.text,textarea', function(e) {
				switch(e.which) {
				case 9:
				case 13:
					if (!(e.ctrlKey || e.shiftKey)) {
						e.preventDefault();
						var $this = $(this);
						var $label = $this.closest('label');
						if (cleanup($this)) {
							var $new = $label.clone().insertAfter($label.blur());
							var $field = $('.text,textarea', $new).val('');
							WIDGET_INIT($new).hide().fadeIn('fast', function() {
								$field.focus();
							});
						} else {
							remove($label);
						}
					}
					break;
				}
			})
			.on('blur', '.text,textarea', function() {
				var $this = $(this);
				var $label = $this.closest('label');
				if (!$label.hasClass('ui-sortable-helper')) {
					!cleanup($this) && remove($label);
				}
			})
			.on('click', 'label a.x', function() {
				var $label = $(this).closest('label');
				if (!remove($label)) {
					$('.text,textarea', $label).val('');
				}
			})
			.sortable({
				handle: 'a.m',
				tolerance: 'pointer'
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