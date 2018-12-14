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


define('zen-webapp/edit/repeater', [
        'jquery',
        'zen-webapp/util/dom',
        'zen-webapp-lib/jqueryui' // for sorting
        ], function($, UTIL_DOM) {
	
	var DATA_SCOPE = 'scope';
	var DATA_TEMPLATE = 'template';
	var DATA_ADD_TYPE = 'type';
	
	var CLASS_ROW = 'zen-webapp-repeater-row';
	
	var SELECTOR_WIDGET = '.zen-webapp-repeater';
	var SELECTOR_ADD = '.zen-webapp-repeater-add';
	
	var TEMPLATE_ROW = com.nominanuda.webapp.widget.repeater_row;
	var TEMPLATE_DEL = com.nominanuda.webapp.widget.repeater_del;
	
	
	function init($repeater) {
		var scope = $repeater.data(DATA_SCOPE);
		var template = $repeater.data(DATA_TEMPLATE);
		return {
			add: function(obj, type) {
				obj = obj || {};
				obj.clss = null; // forces clss resetting
				obj.scope = scope;
				obj.template = template;
				obj.index = $repeater.children().length;
				obj.row = $.extend(obj.row || {}, {
					type: type
				});
				obj.rows = obj.index + 1;
				var $row = $(TEMPLATE_ROW(obj));
				$repeater.append($row); // first add, then effects (for tables)
				$row.hide().fadeIn();
				return $row;
			}
		};
	}
	
	
	function dynamize($row, $addBtn) {
		var $del = $(TEMPLATE_DEL({}));
		$del.on('click', function(e) {
			if ($row.is('tr')) { // no animation
				$row.remove();
			} else {
				$row.fadeOut(function() {
					$row.remove();
				});
			}
			return UTIL_DOM.noevents(e);
		});
		
		if ($row.is('tr')) {
			$row.append($('<td />').append($del));
		} else {
			$row.prepend($del);
		}
		$row.addClass(CLASS_ROW).on('keypress', 'input', function(e) {
			switch (e.keyCode) {
			case 13: // enter
				var $btn = $(SELECTOR_ADD, $row).first();
				($btn.length ? $btn : $addBtn).trigger('click');
				return UTIL_DOM.noevents(e);
			}
			return true;
		});
	}
	
	
	function widget($elms, json, cback) {
		UTIL_DOM.findter(SELECTOR_WIDGET, $elms).each(function() {
			var $repeater = $(this);
			var r = init($repeater);
			if (json) {
				var $addBtn = $repeater.siblings(SELECTOR_ADD);
				if ($addBtn.length == 0 && $repeater.is('thead,tbody')) { // help for tables
					$addBtn = $repeater.closest('table').siblings(SELECTOR_ADD);
				}
				if ($addBtn.length > 0) {
					$addBtn.on('click', function() {
						var obj = (typeof json == 'function' ? json() : json); // TODO args to pass to json()?
						var $row = r.add(obj, $(this).data(DATA_ADD_TYPE));
						if (cback) {
							cback($row);
						} else {
							$(SELECTOR, $row).each(function() {
								var $this = $(this);
								widget($this, json);
							});
						}
						dynamize($row, $addBtn);
						$('input', $row).first().focus();
					});
					$repeater.children().each(function() {
						dynamize($(this), $addBtn);
					});
				}
			}
		});
	}
	
	
	return {
		init: init,
		widget: widget
	};
});