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


define('zen-webapp/overlay', [
        'jquery',
        'require',
        'zen-webapp/sfx',
        'zen-webapp/util/dom',
        'zen-webapp-lib/jquery/jquery.cssmap'
        ], function($, require, SFX, UTIL_DOM) {
	

	var REQUIREJS_FOCUS = 'zen-webapp/input/focus';
	
	var SELECTOR_LIGHTBOX = '.zen-webapp-lightbox';
	var SELECTOR_SHADOW = '.zen-webapp-shadow';
	
	var CLASS_KEEPED = 'zen-webapp-overlay-keeped';
	var CLASS_SPINNER = 'zen-webapp-spinner';
	var CLASS_OVERLAYED = 'overlayed';
	var CLASS_ZOOMED = 'zoomed';

	var ALLOW_SHADOW_FADING = true; // TODO ev use modernizr
	
	
	var $window = $(window);
	var $overlay = $(com.nominanuda.webapp.layout.overlay({
		shadow: true,
		lightbox: ' '
	}));
	
	var boxMedal = null, $boxSrc = null, boxAnimating = false;

	var $shadow = $(SELECTOR_SHADOW, $overlay).hide().on('click tap', function() {
		shadowClickFnc && shadowClickFnc();
	}), shadowClickFnc = null;

	var $lightbox = $(SELECTOR_LIGHTBOX, $overlay).hide();
	var $lightboxContent= $('.content', $lightbox);
	var $lightboxSrc = null, lightboxMedal = null;
	var $lightboxCloseBtn = $('.close', $lightbox).on('click tap', function() {
		$shadow.trigger('click');
	});
	var lightboxPendingCenter = false;


	
	var Overlayed = function($elm, flgKeep) {
		if (!$elm.is($lightbox)) {
			var oldOffset = $elm.offset();
			$elm.addClass(CLASS_OVERLAYED).appendTo($overlay).css({
				top: oldOffset.top - $window.scrollTop(),
				left: oldOffset.left - $window.scrollLeft()
			});
			flgKeep && $elm.addClass(CLASS_KEEPED);
		}
		
		this.center = function(x, y) {
			SFX.center($elm, x, y);
			return this;
		};
		
		this.overlap = function($obj) {
			$obj = $obj || this._$overlap;
			if ($obj) {
				SFX.overlap($elm, $obj);
				this._$overlap = $obj;
			}
			return this;
		};
		
		this.css = function(css) {
			$elm.css(css);
			return this;
		}
		
		this.spinner = function(flg, dir) { // flg = activate/deactivate spinning
			var pos = $elm.position();
			var $parent = $elm.parent();
			$parent && $parent.hasClass(CLASS_SPINNER) && $parent.replaceWith($elm);
			flg && $('<div/>').addClass(CLASS_SPINNER).append($elm).appendTo($overlay).css({
				top: pos.top,
				left: pos.left,
				width: $elm.width(),
				height: $elm.height()
			}).addClass(dir).addClass(CLASS_ZOOMED);
			return this;
		};
		
		this._$elm = function() { // to be used only internally (by Medal)
			return $elm;
		};
	};
	
	
	var Medal = function(o1, o2) {
		var $elm1 = o1._$elm(), $elm2 = o2._$elm();
		
		this.flip = function(flg, dir, ms, fnc) {
			o1.overlap(); o2.overlap();
			SFX.flip($elm1, $elm2, flg, dir, ms, fnc);
			return this;
		};
		
		this.morph = function(flg, dir, ms, fnc) {
			o1.overlap(); o2.overlap();
			SFX.morph($elm1, $elm2, flg, dir, ms, fnc);
			return this;
		};
		
		this.o1 = function() {
			return o1;
		};
		this.o2 = function() {
			return o2;
		}
	};
	
	
	
	var BOX = { // TODO merge with lightbox
		on: function($src, $box, cback, dCback, flgNokeep) {
			if (boxAnimating/* || boxMedal*/) { // TODO fix boxMedal can stay valued in case of ajax navigation
				return;
			}
			boxAnimating = true;
			OVERLAY.reset(true, function() {
				BOX.off(dCback);
			});
			$boxSrc = $src;
			var $srcClone = $src.cssClone();
			UTIL_DOM.img.init($srcClone, function($img) {
				if (!$img) {
					var oBox = OVERLAY.append($box, flgNokeep ? false : true).center(), oBtn = OVERLAY.append($srcClone).overlap($src);
					boxMedal = OVERLAY.medal(oBtn, oBox).flip(true, null, 500, function() {
						UTIL_DOM.img.adapt($box);
						if (require.defined(REQUIREJS_FOCUS)) {
							require([REQUIREJS_FOCUS], function(FOCUS) {
								FOCUS.pushScope($box);
								cback && cback();
							});
						} else {
							cback && cback();
						}
						boxAnimating = false;
					});
					$boxSrc.css('visibility', 'hidden');
				}
			});
			return this;
		},
		off: function(cback) {
			if (boxAnimating) {
				return;
			}
			boxAnimating = true;
			OVERLAY.shadow(false);
			boxMedal.flip(false, null, 500, function() {
				OVERLAY.close();
				$boxSrc.css('visibility', 'visible');
				if (require.defined(REQUIREJS_FOCUS)) {
					require([REQUIREJS_FOCUS], function(FOCUS) {
						FOCUS.popScope();
						cback && cback();
					});
				} else {
					cback && cback();
				}
				boxAnimating = false;
				boxMedal = null;
			});
			return this;
		},
		cancel: function($elm, cback) {
			UTIL_DOM.findter('.cancel', $elm).off('click tap').on('click tap', function() {
				BOX.off(cback);
			});
			return this;
		},
		center: function() {
			boxMedal && boxMedal.o2().center();
			return this;
		}
	};
	
	
	
	var LIGHTBOX = {
		load: function(content) {
			$lightboxContent.empty();
			if (typeof content == 'function') {
				var $content = content(function($content) {
					$lightboxContent.append($content);
				});
				if ($content) {
					$lightboxContent.empty().append($content);
				}
			} else {
				$lightboxContent.append(content);
			}
			lightboxPendingCenter && this.center();
			return this;
		},
		freeze: function() {
			$lightbox.width($lightbox.width());
			$lightbox.height($lightbox.height());
			return this;
		},
		center: function(cback) {
			if ($lightboxContent.children().length) {
				var w = $lightboxContent.outerWidth();
				var h = $lightboxContent.outerHeight();
				var limit = $window.innerHeight() - $lightbox.offset().top - h;
				(limit < 0) && $lightboxContent.outerHeight(h += limit);
				var dx = ($lightbox.width() - w) >> 1;
				var dy = ($lightbox.height() - h) >> 1;
				if (w && h && (dx || dy)) {
					$lightbox.animate({
//						top: '+=' + dy,
						left: '+=' + dx,
						width: w,
						height: h
					}, 600, function() {
						$lightbox.height('auto');
						cback && cback();
					});
				} else {
					cback && cback();
				}
				lightboxPendingCenter = false;
			} else {
				lightboxPendingCenter = true;
			}
			return this;
		},
		show: function(content, animFnc, cback, dCback, width, height) {
			var hideFnc; // animFnc can return an alternative one 
			OVERLAY.reset(true, dCback && function() {
				(hideFnc || LIGHTBOX.hide)((typeof dCback === 'function') && dCback);
			});
			$lightbox.removeAttr('style').show();
			$lightboxContent.removeAttr('style'); // if w/h were applied before
			if (width) {
				$lightbox.css('left', '-=' + (width-$lightbox.width())/2).width(width);
				$lightboxContent.width(width); // force also content to match size
			}
			if (height) {
				$lightbox.height(height);
				$lightboxContent.height(height);
			}
			LIGHTBOX.load(content); // content after dimensions and append (for ev img.adapt)
			hideFnc = (animFnc || function(aback) {
				ALLOW_SHADOW_FADING ? $lightbox.hide().fadeIn(function() {
					aback && aback();
				}) : aback && aback();
			})(function() { // animFnc callback function (aback)
				if (width && height) { // don't resize lightbox with explicit dimensions
					cback && cback();
				} else {
					LIGHTBOX.center(cback);
				}
			});
			return this;
		},
		hide: function(animFnc, cback) {
			OVERLAY.shadow(false);
			if ($lightboxContent.children('object,embed').length) {
				$lightboxContent.empty();
			}
			(animFnc || function(aback) {
				ALLOW_SHADOW_FADING ? $lightbox.fadeOut(function() {
					aback && aback();
				}) : aback && aback();
			})(function() {
				OVERLAY.close();
				cback && cback();
			});
			return this;
		},
		flipIn: function(content, $src, cback, dCback, width, height) {
			$lightboxSrc = $src;
			var $srcClone = $src.cssClone();
			UTIL_DOM.img.init($srcClone, function($img) {
				!$img && LIGHTBOX.show(content, function(aback) {
					var oBox = OVERLAY.append($lightbox);
					var oSrc = OVERLAY.append($srcClone).overlap($src);
					lightboxMedal = OVERLAY.medal(oSrc, oBox).flip(true, null, 500, aback);
					$lightboxSrc.css('visibility', 'hidden');
					return LIGHTBOX.flipOut;
				}, cback, dCback, width, height);
			});
			return this;
		},
		flipOut: function(cback) {
			LIGHTBOX.hide(function(aback) {
				lightboxMedal && lightboxMedal.flip(false, null, 500, function() {
					$lightboxSrc.css('visibility', 'visible');
					aback && aback();
				});
			}, cback);
			return this;
		}
	};
	
	
	
	var OVERLAY = {
		reset: function(flgS, fncS) {
			$shadow.hide();
			$overlay.appendTo('body');
			if (flgS) {
				shadowClickFnc = fncS;
				ALLOW_SHADOW_FADING ? $shadow.fadeIn() : $shadow.show();
			}
			$lightbox.hide();
			$lightboxContent.empty();
			$lightboxCloseBtn.toggle(fncS ? true : false);
			$lightbox.nextAll('.' + CLASS_KEEPED).detach();
			$lightbox.nextAll().remove();
			//ontop();
			return this;
		},
		/*ontop: function(flg) {
			$overlay.toggleClass('ontop', flg ? true : false);
			return this;
		},
		ontopof: function($guest, $host) {
			var onTop = $host.has($guest).length ? false : true;
			OVERLAY.ontop(onTop); // needed?
			return onTop;
		},*/
		close: function() {
			$overlay.detach();
			return this;
		},
		shadow: function(flgShow, flgInstant) {
			if (ALLOW_SHADOW_FADING && !flgInstant) {
				flgShow ? $shadow.fadeIn() : $shadow.fadeOut()
			} else {
				$shadow.toggle(flgShow);
			}
			return this;
		},
		append: function($elm, flgKeep) {
			return new Overlayed($elm, flgKeep);
		},
		medal: function(o1, o2) {
			return new Medal(o1, o2);
		},
		lightbox: LIGHTBOX,
		box: BOX
	};
	
	return OVERLAY;
});