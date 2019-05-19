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


define('zen-webapp/ajax', [
        'jquery',
        'module',
        'require',
        'zen-webapp/overlay',
        'zen-webapp/util/css',
        'zen-webapp/util/dom',
        'zen-webapp/util/soy',
        'zen-webapp/widget/init',
        'zen-webapp-lib/jquery/jquery.cssmap',
        'zen-webapp-lib/jquery/jquery.history'
        ], function($, module, require, OVERLAY, UTIL_CSS, UTIL_DOM, UTIL_SOY, WIDGET_INIT) {
	
	var CLASS_DOAJAX = 'doAjax';
	var CLASS_NOAJAX = 'noAjax';
	var CLASS_DOSUBMIT = 'doSubmit';
	var CLASS_LOADING = 'zen-webapp-loading';
	var CLASS_ONBEFORESUBMIT = 'zen-webapp-onbeforesubmit';
	
	var DATA_ELMNAME = 'name';
	var DATA_DOSUBMIT = 'doSubmit';
	
	var JSON_PROPERTY_RELOAD = '__webappReload';
	var JSON_PROPERTY_REDIRECT = '__webappRedirect';
	var JSON_PROPERTY_REQUIREJS = '__webappRequireJs';
	var JSON_PROPERTY_REQUIRECSS = '__webappRequireCss';
	var JSON_PROPERTY_REQUIRESOY = '__webappRequireSoy';
	
	var PARAM_SNIPID = 'snipId';
	var PARAM_ISAJAX = 'isAjax';
	
	var REQUIREJS_TARGET = 'zen-webapp/target/';
	var REQUIREJS_TRANSITIONS = 'zen-webapp/transition/';
	
	var SELECTOR_FORM = 'form';
	var SELECTOR_LINK = 'a[href]';
	var SELECTOR_ONBEFORESUBMIT = '.' + CLASS_ONBEFORESUBMIT;
	var SELECTOR_SNIPPET = '.zen-webapp-snippet';
	
	var SERVLET_PATH = expandProtocol(module.config().servletPath);
	
	var TRIGGER_ONBEFORESUBMIT = 'beforeSubmit';
	
	
	function expandProtocol(url) {
		return url.indexOf('//') == 0 ? location.protocol + url : url;
	}
	
	function navigate(url, params, doReload, flgNewWin) {
		url = expandProtocol($.trim(url || ''));
		
		if (url && url.indexOf(SERVLET_PATH) != 0) { // cannot do ajax
			doReload = true;
		}
		if (doReload || flgNewWin) {
			if (flgNewWin) {
				window.open(url)
			} else {
				window.location = url;
			}
			return;
		}
		if (canGo(url, params)) { // here we block explicit navigation (before address bar is changed), back/fwd is done below
			navigationTs = $.now();
			History.pushState({
				ts: navigationTs,
				params: params
			}, document.title, url);
		}
	}
	var navigationTs = 0;
	
	function canGo(url, params) {
		var msg = beforegoCback && beforegoCback(url, params);
		return (msg && !confirm(msg) ? false : true);
	}
	var beforegoCback = null;
	
	function doRedirect(json, cback) {
		var url = null;
		var doReload = false;
		if (json) {
			if (url = json[JSON_PROPERTY_RELOAD]) {
				delete json[JSON_PROPERTY_RELOAD];
				doReload = true;
			} else {
				url = json[JSON_PROPERTY_REDIRECT];
				delete json[JSON_PROPERTY_REDIRECT];
			}
			if (url) {
				cback && cback(url, doReload, json);
				return true;
			}
		}
		return false;
	}

	
	function enableAjaxEvent($elms, event, selector, cback) {
		if (cback) {
			$elms.on(event, selector, function(e) {
				var $this = $(this);
				if (this.target == '_blank' || $this.hasClass(CLASS_NOAJAX)) {
					return true;
				}
				var $snippet = $this.hasClass(CLASS_DOAJAX) && $this.closest(SELECTOR_SNIPPET);
				return cback(this, $this, $snippet, e) || UTIL_DOM.noevents(e); // cback can return true if needed
			});
		}
	}
	
	function intercept$elms($elms, aCback, formCback) {
		enableAjaxEvent($elms, 'click tap', SELECTOR_LINK, aCback && function(a, $a, $snippet, e) {
			return aCback(a, $a, $snippet, e, $a.prop('href'), null); // was decodeURIComponent(href)... why?
		});
		enableAjaxEvent($elms, 'submit', SELECTOR_FORM, formCback && function(form, $form, $snippet, e) {
			$(SELECTOR_ONBEFORESUBMIT, $form).trigger(TRIGGER_ONBEFORESUBMIT); // allow registered components to do something
			var params = $form.serializeArray();
			var url = ($form.attr('action') || currentUrl); // was decodeURIComponent(action)... why?
			if ((form.method || '').toLowerCase() != 'post') {
				url = url + (url.indexOf('?') > -1 ? '&' : '?') + $.param(params, true);
				params = null;
			}
			return formCback(form, $form, $snippet, e, url, params);
		});
		return $elms;
	}
	
	
	function buildAjaxParams(params, snipId) {
		params = params || [];
		params.push({
			name: PARAM_ISAJAX,
			value: true
		});
		for (var p in ajaxParams) {
			params.push({
				name: p,
				value: ajaxParams[p]
			});
		}
		if (snipId) {
			params.push({
				name: PARAM_SNIPID,
				value: snipId
			});
		}
		return params;
	}
	var ajaxParams = {};
	
	function doAjax(url, params, snipId, cback, ecback, rcback) { // cback = success, ecback = error, rcback = redirect
		return $.ajax({ // TODO ev return interface
			url: (url || currentUrl),
			data: buildAjaxParams(params, snipId),
			dataType: 'json',
			cache: false, // TODO ev policy
			type: (params ? 'POST' : 'GET'),
			traditional: true,
			error: function(xhr, status, error) {
				ecback && ecback(error);
			},
			success: function(data) {
				if (!doRedirect(data, rcback || function(url, doReload, json) {
					navigate(url, null, doReload);
				})) {
					dataInterceptor && (data = dataInterceptor(data, snipId));
					cback && cback(data);
				}
			}
		});
	}
	var dataInterceptor = null;
	
	
	function enable($page, init$html, pageTplRenderer, transition) {
		var $window = $(window);
		var originalInit$html = init$html;
		init$html = function($html, snippet) { // we do our $html initializations
			originalInit$html(WIDGET_INIT($html), snippet);
		};

		if (History && History.enabled) { // we have ajax
			function getTransition($elm, snipId) {
				if (transition) {
					/*
					 * transition: directly type (string) or function($elm) returning obj:
					 * {
					 * 		type: 'requirejs module', // that will do the actual transition
					 * 		$overlay: $elm_or_child, // if existing, this will be cssCloned and transformed into oPageTransitionOverlayed
					 * 		overlay: function(oPageTransitionOverlayed, $page) {...} // ev do something with the $overlay clone and the $page while loading
					 * }
					 */
					var t = (typeof transition == 'function' ? transition($elm, snipId) : transition);
					return (typeof t == 'string' || typeof t == 'function') ? {
						type: t
					} : t;
				}
				return null;
			}
			function loadTransition(transition, defType, cback) {
				if (transition && typeof transition.type == 'function') {
					cback(transition.type);
				} else {
					require([REQUIREJS_TRANSITIONS + (transition && transition.type || defType)], function(TRANSITION) {
						TRANSITION
							? cback(TRANSITION)
							: cback(function($old, $new, oLoading, cback) { // fallback transition (just does a replaceAll)
								$new.replaceAll($old);
								cback();
							});
					});
				}
			}
			var currentPageTransition = null;
			var oPageTransitionOverlayed = null;
			
			
			function doSnippet(url, params, $snippet) {
				var snipId = $snippet && $snippet.data(DATA_ELMNAME) || null;
				if (snipId && $snippet.closest(CLASS_LOADING).length == 0) {
					$snippet.addClass(CLASS_LOADING);
					doAjax(url, params, snipId, function(data) { // success cback
						var $oldSnippet = $snippet;
						$snippet = $(pageTplRenderer(data));
						loadTransition(getTransition($oldSnippet, snipId), 'snippet', function(TRANSITION) {
							TRANSITION($oldSnippet, $snippet, null, function() {
								init$html($snippet, snipId);
							});
						});
					}, function() { // error cback
						$snippet.removeClass(CLASS_LOADING);
					});
					return true;
				}
				return false;
			}
			
			
			var $body = $(document.body);
			intercept$elms($body, function(a, $a, $snippet, e, url) {
				if (a.target) {
					require([REQUIREJS_TARGET + a.target], function(TARGET) {
						TARGET && TARGET(url, $a);
					});
				} else if (!$page.hasClass(CLASS_LOADING)) {
					var href = ($a.attr('href') || ''); // different from url, which is $a.prop('href')
					if (href.charAt(0) == '#') {
						var id = href.substr(1);
						// TODO scroll to elm
					} else if (!doSnippet(url, null, $snippet)) {
						currentPageTransition = getTransition($a);
						navigate(url, null, false, e.ctrlKey || e.shiftKey);
					}
				}
			}, function(form, $form, $snippet, e, action, params) {
				if (!$page.hasClass(CLASS_LOADING)) {
					if (!doSnippet(action, params, $snippet)) {
						currentPageTransition = null;
						navigate(action, params);
					}
				}
			});
			UTIL_DOM.widgetize($body, null, CLASS_DOSUBMIT, DATA_DOSUBMIT); // no combox, will be activated in each init$html
			UTIL_DOM.ajaxize($body, CLASS_NOAJAX);
			
			
			var xhr, currentTs = 0;
			$window.on('statechange',function() {
				var state = History.getState();
				var params = state.data.params;
				var url = state.url;
				
				url && (currentUrl = url); // update only if not empty
				
				var ts = state.data.ts || 0;
				var goingFwd = false, goingBack = false;
				if (ts < currentTs) {
					goingBack = true;
				} else if (ts < navigationTs) {
					goingFwd = true;
				} else {
					navigationTs++; // make sure ts will be past if going back/forward immediately
				}
				currentTs = ts;
				
				if (goingBack || goingFwd) {
					if (!canGo(url, params)) {
						// only place where we can stop back/fwd (but address bar will change)
						return false;
					}
					params = null; // no resubmit when going back or forward
					if (goingBack) {
						setTimeout(function() { // avoid scroll when going back
							$window.scrollTop(0);
						}, 1);
					}
				}
				
				
				OVERLAY.reset();
				if (currentPageTransition) {
					oPageTransitionOverlayed = null;
					var $overlay = currentPageTransition.$overlay;
					if ($overlay) {
						var w = $overlay.width(), h = $overlay.height();
						oPageTransitionOverlayed = OVERLAY.append($overlay.cssClone().insertBefore($overlay).css({
							width: w,
							height: h
						}));
						($page.has($overlay).length) && $overlay.css({ // hide only if in $page
							visibility: 'hidden'
						});
					}
					currentPageTransition.overlay && currentPageTransition.overlay(oPageTransitionOverlayed, $page);
				}
				

				xhr && xhr.abort();
				xhr = doAjax(url, params, null, function(data) {
					UTIL_SOY.load(data[JSON_PROPERTY_REQUIRESOY], function() {
						var $oldPage = $page;
						$page = $(pageTplRenderer(data));
						UTIL_CSS.load(data[JSON_PROPERTY_REQUIRECSS], function() {
							loadTransition(currentPageTransition, 'flat', function(TRANSITION) {
								TRANSITION($oldPage, $page, oPageTransitionOverlayed, function() {
									OVERLAY.close();
									$window.scrollTop(0);
									init$html($page, null);
									var modules = data[JSON_PROPERTY_REQUIREJS];
									modules && modules.length && require(modules, function() {
										for (var i = 0; i < arguments.length; i++) {
											var module = arguments[i];
											module && typeof module == 'function' && module(); // same as in bootloader.js
										}
									});
								});
							});
						});
					});
				}, function(error) {
					//alert(error);
					OVERLAY.close();
					$page.removeClass(CLASS_LOADING);
					History.back();
				});
				
				$page.addClass(CLASS_LOADING);
			});
			
			init$html($page, null);
			return true; // can work as virtual

		} else {
			UTIL_DOM.widgetize($body, null, CLASS_DOSUBMIT, DATA_DOSUBMIT);
			init$html($page);
			return false; // cannot work as virtual
		}
	}
	var currentUrl = window.location.href; // needed to retrieve "window.location" when using hashtags (will be updated by History.js if enabled)
	
	
	return {
		ajax: doAjax,
		enable: enable,
		buildRequestParams: buildAjaxParams,
		defaultRequestParams: function(params) {
			ajaxParams = params || {};
		},
		interceptData: function(interceptor) {
			(typeof interceptor == 'function') && (dataInterceptor = interceptor);
		},
		beforeSubmit: function($elm, cback, reset) {
			reset && $elm.off(TRIGGER_ONBEFORESUBMIT);
			$elm.addClass(CLASS_ONBEFORESUBMIT).on(TRIGGER_ONBEFORESUBMIT, cback);
		},
		beforeGo: function(cback) {
			(typeof cback == 'function') && (beforegoCback = cback);
		},
		go: function(url) {
			navigate(url);
		}
	};
});