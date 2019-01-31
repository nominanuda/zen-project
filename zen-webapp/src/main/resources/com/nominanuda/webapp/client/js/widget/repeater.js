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


define('zen-webapp/widget/repeater', [
        'jquery',
        'zen-webapp/util/dom',
        'zen-webapp-lib/jqueryui' // for sorting
        ], function($, UTIL_DOM) {
	
	var DATA_SCOPE = 'scope';
	var DATA_VARIANT = 'variant';
	var DATA_ADD_TYPE = 'type';
	var DATA_NEXT_INDEX = '_index';
	
	var CLASS_ROW = 'zen-webapp-repeater-row';
	
	var SELECTOR_WIDGET = '.zen-webapp-repeater';
	var SELECTOR_ADD = '.zen-webapp-repeater-add';
	
	var TEMPLATE_ROW = com.nominanuda.webapp.widget.repeater_row;
	var TEMPLATE_DEL = com.nominanuda.webapp.widget.repeater_del;
	
	
	function dynamize($row, $addBtn, customDelCback) {
		var delCback = function(e) {
			if ($row.is('tr')) { // no animation
				$row.remove();
			} else {
				$row.fadeOut(function() {
					$row.remove();
				});
			}
		};
		var $delBtn = $(TEMPLATE_DEL({})).on('click', function(e) {
			if (customDelCback) {
				customDelCback($delBtn, delCback);
			} else {
				delCback();
			};
			return UTIL_DOM.noevents(e);
		});
		
		if ($row.is('tr')) {
			$row.append($('<td />').append($delBtn));
		} else {
			$row.prepend($delBtn);
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
	
	
	function init($repeater, json, addCback, delCback) {
		var scope = $repeater.data(DATA_SCOPE);
		var variant = $repeater.data(DATA_VARIANT);
		
		var addFnc = function(obj, type) {
			var size = $repeater.children().length;
			var index = $repeater.data(DATA_NEXT_INDEX) || size;
			$repeater.data(DATA_NEXT_INDEX, index + 1);
			
			!obj && (obj = {});
			obj.row = $.extend(obj.row || {}, {
				type: type
			});
			obj.rows = size + 1;
			obj.index = index;
			obj.scope = scope;
			obj.variant = variant;
			obj.clss = null; // forces clss resetting
			
			var $row = $(TEMPLATE_ROW(obj));
			$repeater.append($row); // first add, then effects (for tables)
			$row.hide().fadeIn();
			return $row;
		};
		
		if (json) {
			var $addBtn = $repeater.siblings(SELECTOR_ADD);
			if ($addBtn.length == 0 && $repeater.is('thead,tbody')) { // help for tables
				$addBtn = $repeater.closest('table').siblings(SELECTOR_ADD);
			}
			if ($addBtn.length > 0) {
				$addBtn.on('click', function() {
					var obj = (typeof json == 'function' ? json() : json); // TODO args to pass to json()?
					var $row = addFnc(obj, $(this).data(DATA_ADD_TYPE));
					addCback && addCback($row);
					dynamize($row, $addBtn, delCback);
					$('input', $row).first().focus();
				});
				$repeater.children().each(function() {
					dynamize($(this), $addBtn, delCback);
				});
			}
		}
		
		return {
			add: addFnc
		};
	}
	
	
	function widget($elms, json, cback) {
		UTIL_DOM.findter(SELECTOR_WIDGET, $elms).each(function() {
			var $repeater = $(this);
			var addCback = function($row) {
				$(SELECTOR, $row).each(function() {
					widget($(this), json, addCback);
				});
			};
			init($repeater, json, cback || addCback);
		});
	}
	
	
	return {
		init: init,
		widget: widget
	};
});