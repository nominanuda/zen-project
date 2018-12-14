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


//require.config({
//	shim: {
//		'zen-webapp-lib/modernizr/modernizr-2.6.2-dev': {
//			exports: 'Modernizr'
//		}
//	}
//});


define('zen-webapp/sfx', [
        'jquery',
        'zen-webapp-lib/jquery/jquery.transit-0.9.12.min'
        ], function($) {
	
	
	var SELECTOR_LOADER = '.zen-webapp-loader';
	var MS_DEFAULT_ANIM = 5000;

	var CSS_RESET = {
		x: 0,
		y: 0,
		rotateX: 0,
		rotateY: 0,
		scale: 1,
		opacity: 1,
		visibility: 'visible'
	};
	
	
	function loader($elm, flg) {
		if (flg) {
			loader($elm, false);
			$elm.append(com.nominanuda.webapp.layout.loader({}));
		} else {
			$elm.children(SELECTOR_LOADER).remove();
		}
	}
	
	
	function center($elm, x, y) {
		var $parent = $elm.parent();
		$elm.css(CSS_RESET).css({
			top: (y || $parent.height()/2) - $elm.outerHeight()/2,
			left: (x || $parent.width()/2) - $elm.outerWidth()/2
		});
		return $elm;
	}
	
	
	function overlap($elm, $target, flgZoom) {
		var tOff = $target.offset();
		$elm.css(CSS_RESET).offset({
			top: (tOff.top + ($target.height() - $elm.height()) / 2),
			left: (tOff.left + ($target.width() - $elm.width()) / 2)
		});
		flgZoom && $elm.css({
			scale: [$target.outerWidth()/$elm.outerWidth(), $target.outerHeight()/$elm.outerHeight()]
		});
		return $elm;
	}
	
	
	function morph($1, $2, flg, dir, ms, cback) {
		var time = ms || MS_DEFAULT_ANIM;
		
		var $from = flg ? $1 : $2, $to = flg ? $2 : $1;
		var fPos = $from.position(), tPos = $to.position();
		var fWidth = $from.width(), tWidth = $to.width();
		var fHeight = $from.height(), tHeight = $to.height();
		
		$to.css({
			top: fPos.top,
			left: fPos.left,
			width: fWidth,
			height: fHeight,
			visibility: 'visible',
			opacity: 0
		}).animate({
			top: tPos.top,
			left: tPos.left,
			width: tWidth,
			height: tHeight,
			opacity: 1
		}, time, cback);
		
		$from.css({
			visibility: 'visible'
		}).animate({
			top: tPos.top,
			left: tPos.left,
			width: tWidth,
			height: tHeight,
			opacity: 0
		}, time, function() {
			$from.css({
				top: fPos.top,
				left: fPos.left,
				width: fWidth,
				height: fHeight,
				opacity: 1,
				visibility: 'hidden'
			})
		});
	}
	
	
	function flip($1, $2, flg, dir, ms, cback) {
		$1.css(CSS_RESET); $2.css(CSS_RESET);
		var w1 = $1.outerWidth(), w2 = $2.outerWidth(), h1 = $1.outerHeight(), h2 = $2.outerHeight(), zX = w2/w1, zY = h2/h1;
		var p1 = $1.position(), p2 = $2.position(), dX = p2.left - p1.left + (w2 - w1) / 2, dY = p2.top - p1.top + (h2 - h1) / 2;
		
		dir = (dir||(Math.abs(dX)>Math.abs(dY) ? dX>0 ? 'r':'l' : dY>0 ? 'b':'t')).charAt(0); // t r b l
		var hDir = (dir=='l' ? -1 : dir=='r' ? 1 : 0), vDir = (dir=='t' ? 1 : dir=='b' ? -1 : 0);
		
		var css1back = {
			x: dX,
			y: dY,
			rotateX: 180 * vDir,
			rotateY: 180 * hDir,
			scale: [zX, zY],
			opacity: 0
		}, css2back = {
			x: -dX,
			y: -dY,
			rotateX: -180 * vDir,
			rotateY: -180 * hDir,
			scale: [1/zX, 1/zY],
			opacity: 0
		};
		
		var time = ms || MS_DEFAULT_ANIM;
		
		if (flg) {
			$1.css(CSS_RESET);
			$2.css(css2back).insertAfter($1);
			setTimeout(function() {
				$1.transition(css1back, time, function() {
					$1.css('visibility', 'hidden');
				});
				$2.transition(CSS_RESET, time, cback);
			}, 10);
		} else {
			$2.css(CSS_RESET);
			$1.css(css1back).insertAfter($2);
			setTimeout(function() {
				$2.transition(css2back, time, function() {
					$2.css('visibility', 'hidden');
				});
				$1.transition(CSS_RESET, time, cback);
			}, 10);
		}
	}
	
	
	
	return {
		loader: loader,
		center: center,
		overlap: overlap,
		morph: morph,
		flip: flip
	};
});