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


define('zen-webapp/util/dom', [
        'jquery',
        'zen-webapp/util/image'
        ], function($, UTIL_IMG) {
	
	
	function findter(sel, $elm) { // find + filter: searches for matching sel in $elm, but also $elm can be a match 
		return $(sel, $elm).add($elm.filter(sel));
	}
	
	function restrict(sel, $elm) { // return what matches sel in $elm, or return $elm 
		var $match = $(sel, $elm);
		return ($match.length ? $match : $elm);
	}
	
	
	function imgAdapt($elms, dir, img, cback) { // dir = optional direction (h,v) to freeze (other will be freely scaled according to aspect ratio)
		return findter('img', $elms).each(function() {
			var $this = $(this);
			var src = $this.attr('src');
			var obj = (img || src);
			if (obj) {
				$this.removeAttr('src'); // to get dimensions from the element itself (not the current img)
				// here below: $this.parent() in case of max-width:100% (for auto scaling), parseInt in case of decimal pixels
				var w = (dir != 'v' ? parseInt($this.width() || $this.parent().width()) : null);
				var h = (dir != 'h' ? parseInt($this.height() || $this.parent().height()) : null);
				if (w || h) { // do it only if there are dimensions to use
					var newSrc = UTIL_IMG.render(obj, w, h);
					$this.one('load', function() {
						cback && cback($this, true, newSrc);
					}).one('error', function() {
						src && $this.attr('src', src);
						cback && cback($this, false, src);
					});
					$this.attr('src', newSrc);
				}
			}
		});
	}
	
	function imgInsert($elms, img, dir, cback) {
		img && imgAdapt($elms.append($('<img />')), dir, img, cback);
		return $elms;
	}
	
	function imgInit($elms, cback) {
		var $imgs = findter('img', $elms);
		var count = 0, tot = $imgs.length;
		if (tot > 0) {
			$imgs.one('load', function() {
				if (cback) {
					cback($(this), true); // cback($img, true) -> loaded $img, ok
					(++count == tot) && cback(null, true); // cback(null, true) -> loaded all, imgs found
				}
			}).one('error', function() {
				if (cback) {
					cback($(this), false); // cback($img, false) -> loaded $img, ko
					(++count == tot) && cback(null, true); // cback(null, true) -> loaded all, imgs found
				}
			}).each(function() {
				this.complete && $(this).triggerHandler('load');
			});
		} else {
			cback && cback(null, false); // cback(null, false) -> loaded all, no imgs found
		}
		return $imgs;
	}
	
	
	function to$hidden($elm, name, val) { // name and val optional
		name = name || $elm.attr('name');
		if (name) { // without name it's not useful
			$elm.next('input[type=hidden][name="' + name + '"]').remove(); // avoid accumulation from previous requests
			new$hidden(name, val || $elm.val()).insertAfter($elm); // not insertBefore -> distrupts :first-child and "+" css selectors
		}
	}
	
	function new$hidden(name, value) {
		return $('<input type="hidden"/>').attr('name', name).val(value);
	}
	
	function deep$hiddens(name, obj) {
		var $hiddens = $();
		if (obj) {
			if (obj.splice || typeof obj == 'object') { // array or obj
				$.each(obj, function(p, v) {
					$hiddens = $hiddens.add(new$hidden(name + '.' + p, v));
				});
			} else {
				$hiddens = $hiddens.add(new$hidden(name, obj));
			}
		}
		return $hiddens;
	}
	
	
	function ajaxize($elm, noAjaxClass) {
		$elm.on('click tap', 'form input[type=submit], form button', function() {
			if (!noAjaxClass || !$(this.form).hasClass(noAjaxClass)) {
				to$hidden($(this));
			}
		});
	}
	
	
	function widgetize($elm, comboxClass, submitClass, submitData) {
		$elm.on('keypress', 'form input', function(e) {
			switch (e.keyCode) {
			case 13: // enter
				var $btns = $(':submit', $(this).closest('form'));
				if ($btns.length) {
					$($btns.sort(function(a, b) {
						return ($(a).attr('tabindex') || 1000) - ($(b).attr('tabindex') || 1000);
					})[0]).trigger('click');
					return noevents(e);
				}
			}
			return true;
		});
		comboxClass && findter('select.' + comboxClass, $elm).each(function() {
			var select = this, lastIndex = this.length - 1;
			var $select = $(this), $input = $select.next('input');
			var inputVal = $input.val();
			if (!inputVal || inputVal == $select.val()) { // select value has priority
				$input.val('').hide();
			} else {
				select.selectedIndex = lastIndex; // ensure it's on combox trigger
				$select.hide();
			}
			$input.on('blur search', function() { // 'search' for X button
				if (!$.trim($input.val())) {
					$input.val('').hide();
					select.selectedIndex = 0; // as it will not be the combox trigger (would make no sense)
					$select.show().focus();
				}
			});
			$select.on('change', function() {
				if (this.selectedIndex == lastIndex) { // combox trigger
					$select.hide();
					$input.val('').show().focus();
				}
			});
		});
		submitClass && $elm.on('click tap', 'form .check.' + submitClass, function() {
			var $this = $(this);
			submitData && to$hidden($this, $this.data(submitData), true);
			$this.closest('form').trigger('submit');
		}).on('change', 'form select.' + submitClass, function() {
			var $this = $(this);
			submitData && to$hidden($this, $this.data(submitData), true);
			$this.closest('form').trigger('submit');
		});
	}
	
	
	function noevents(e) {
		e.stopImmediatePropagation();
		e.preventDefault();
		return false;
	}
	
	
	function limitrigger($elm, fnc, gapX, gapY, ms) {
		ms = ms || 20;
		gapX = gapX || 0;
		gapY = gapY || 0;
		
		var pos = $elm.offset();
		var x0 = pos.left, x1 = x0 + $elm.width();
		var y0 = pos.top, y1 = y0 + $elm.height();
		var timer = null;
		var mx, my;
		
		function trigger() {
			clearTimeout(timer);
			var dx = (mx - x0 < gapX ? -1 : x1 - mx < gapX ? + 1 : 0); // left, right, none
			var dy = (my - y0 < gapY ? -1 : y1 - my < gapY ? + 1 : 0); // above, below, none
			if (fnc(dx, dy)) { // call fnc even if dx/dy are 0 (comunicates an "exit" from the trigger zone)
				timer = setTimeout(trigger, ms);
			}
		}
		
		return function(x, y) {
			mx = x;
			my = y;
			trigger();
		};
	}
	
	
	
	return {
		findter: findter,
		restrict: restrict,
		to$hidden: to$hidden,
		new$hidden: new$hidden,
		deep$hiddens: deep$hiddens,
		ajaxize: ajaxize,
		widgetize: widgetize,
		noevents: noevents,
		limitrigger: limitrigger,
		img: {
			init: imgInit,
			adapt: imgAdapt,
			insert: imgInsert
		}
	};
});