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


const MODEL_DATA = 'data';
const MODEL_DATA_COMBOX = 'data_combox';

const PARAM_SNIPID = 'snipId';
const PARAM_ISAJAX = 'isAjax';

const JSON_PROPERTY_RELOAD = '__webappReload';
const JSON_PROPERTY_REDIRECT = '__webappRedirect';
const JSON_PROPERTY_REQUIREJS = '__webappRequireJs';
const JSON_PROPERTY_REQUIRECSS = '__webappRequireCss';
const JSON_PROPERTY_REQUIRESOY = '__webappRequireSoy';
const JSON_PROPERTY_SNIPPET = 'webappSnippet';


function optionalRequire(require, message, methods) {
	try {
		return __WEBAPP_REQUIRE(require);
	} catch (e) {
		var lib = {};
		__WEBAPP_HELPER.logInfo(message + ' - loading fallback');
		function alert() {
			__WEBAPP_HELPER.logError(message);
		}
		for (var method in methods) {
			lib[method] = alert;
		}
		return lib;
	}
}


function closuredPusher(host, name, fnc) {
	var arr = [];
	host[name] = function(obj) {
		(obj !== undefined) && arr.push.apply(arr, fnc
			? [fnc.apply(this, arguments)] // push result of fnc(arguments)...
			: arguments // ...or directly the arguments if there is no fnc
		);
		return obj;
	};
	return arr;
}


const HELPER = {
	arr: __WEBAPP_REQUIRE('classpath:com/nominanuda/js/lib/arr.js'),
	cast: __WEBAPP_REQUIRE('classpath:com/nominanuda/js/lib/cast.js'),
	check: __WEBAPP_REQUIRE('classpath:com/nominanuda/js/lib/check.js'),
	client: {
		// filled dynamically with closurePushers
	},
	cookie: {
		get: function(name) {
			return __WEBAPP_HELPER.getCookie(name);
		},
		set: function(name, value) {
			__WEBAPP_HELPER.setCookie(name, value);
		},
		setFor: function(name, value, duration) {
			__WEBAPP_HELPER.setCookieFor(name, value, duration);
		},
		setUntil: function(name, value, datetime) {
			__WEBAPP_HELPER.setCookieUntil(name, value, datetime);
		},
		del: function(name) {
			__WEBAPP_HELPER.resetCookie(name);
		}
	},
	exception: function(e, lbl) {
		var ex = e.javaException;
		if (lbl) {
			__WEBAPP_HELPER.logError(lbl + ': ' + e);
			ex && ex.printStackTrace();
		}
		return ex && (ex.getCause() || ex).getMessage() || 'nomsg';
	},
	getter: function(name, value) {
		HELPER.getter[name] = function() {
			return value;
		};
		return value;
	},
	img: optionalRequire('classpath:com/nominanuda/js/lib/img.js', 'zen-image dependency missing!', ['render', 'img2obj', 'obj2url']),
	log: function(obj, lbl) {
		var msg;
		try {
			msg = obj.toString ? obj.toString() : JSON.stringify(obj);
		} catch (e) {
			msg = '[could not stringify] ' + obj;
		}
		__WEBAPP_HELPER.logInfo('\n\n' + (lbl ? lbl + ': ' : '') + msg + '\n\n');
		return obj;
	},
	obj: __WEBAPP_REQUIRE('classpath:com/nominanuda/js/lib/obj.js'),
	push: function(name, fnc) {
		return closuredPusher(HELPER.push, name, fnc);
	},
	request: {
		// ajax: filled dynamically
		ip: function() {
			return __WEBAPP_HELPER.getClientIp();
		},
		proto: function() {
			return __WEBAPP_HELPER.getRequestProtocol();
		},
		host: function() {
			return __WEBAPP_HELPER.getRequestHost();
		},
		port: function() {
			return __WEBAPP_HELPER.getRequestPort();
		},
		tld: function() {
			return __WEBAPP_HELPER.getRequestTopLevelDomain();
		}
	},
	sitemap: {
		// filled by calls to WEBAPP.sitemap(...)
	},
	str: __WEBAPP_REQUIRE('classpath:com/nominanuda/js/lib/str.js'),
	time: __WEBAPP_REQUIRE('classpath:com/nominanuda/js/lib/time.js'),
	url: function(pathOrPatternId, patternParams) {
		return HELPER.cast.isUndefined(patternParams)
			? __WEBAPP_HELPER.absUrl(pathOrPatternId) // abs url from path[OrPatternId] taken as flat string, no uritemplate
			: __WEBAPP_HELPER.absUrl(pathOrPatternId, patternParams || {}); // abs url from uritemplate of [pathOr]patternId + patternParams
	},
	widget: __WEBAPP_REQUIRE('classpath:com/nominanuda/webapp/ctrl/lib/widget.js'),
	xml: __WEBAPP_REQUIRE('classpath:com/nominanuda/js/lib/xml.js')
};


function WebappContext(model, request) {
	var _bootFnc, _cbackFnc, _view;
	var _snipId = (model[PARAM_SNIPID] || null);
	var _isAjax = (_snipId || model[PARAM_ISAJAX] || false);
	
	HELPER.request.ajax = function() {
		return _isAjax;
	};
	
	var _data = model[MODEL_DATA] || {}, _data_combox = model[MODEL_DATA_COMBOX] || {};
	for (var name in _data_combox) { // possible combox custom entries
		!_data[name] && (_data[name] = _data_combox[name]); // select value has priority
	}
	
	return {
		boot: function(bootFnc) {
			_bootFnc = bootFnc;
			return this;
		},
		cback: function(cbackFnc) {
			_cbackFnc = cbackFnc;
			return this;
		},
		path: function(path) {
			__WEBAPP_HELPER.setServletPath(path);
			return this;
		},
		view: function(view) {
			_view = view;
			return this;
		},
		run: function() {
			var requireJs = closuredPusher(HELPER.client, 'js')
			var requireCss = closuredPusher(HELPER.client, 'css')
			var requireSoy = closuredPusher(HELPER.client, 'soy')
			
			var redirectUrl = null, andReload = false;
			HELPER.url.redirect = function(pathOrPatternId, patternParams) {
				redirectUrl = HELPER.url(pathOrPatternId, patternParams);
				return null;
			};
			HELPER.url.reload = function(pathOrPatternId, patternParams) {
				redirectUrl = HELPER.url(pathOrPatternId, patternParams);
				andReload = true;
				return null;
			};
			
			var model = _cbackFnc && _cbackFnc(_snipId, _data, HELPER) || {};
			if (_isAjax) {
				if (redirectUrl) {
					return HELPER.obj.build(andReload ?
						JSON_PROPERTY_RELOAD : JSON_PROPERTY_REDIRECT,
					redirectUrl);
				}
				if (_snipId) {
					model[JSON_PROPERTY_SNIPPET] = _snipId;
				} else {
					model[JSON_PROPERTY_REQUIRESOY] = requireSoy;
					model[JSON_PROPERTY_REQUIRECSS] = requireCss;
					model[JSON_PROPERTY_REQUIREJS] = requireJs;
				}
				return model;
			}
			
			if (redirectUrl) {
				return {
					view_: 'redirect:' + redirectUrl
				}
			}
			
			HELPER.obj.merge(_bootFnc && _bootFnc(HELPER), model);
			model.__webapp = {
				servlet: __WEBAPP_HELPER.getServletPath(),
				soy: requireSoy.map(function(soy) {
					var i = soy.indexOf('/'); // prefix cut point
					return {
						prefix: soy.substr(0, i),
						template: soy.substr(i)
					};
				}),
				css: requireCss.map(function(css) {
					var i = css.indexOf('/'); // prefix cut point
					return {
						prefix: css.substr(0, i),
						stylesheet: css.substr(i)
					};
				}),
				js: requireJs
			};
			return {
				data_: model,
				view_: _view
			};
		}
	};
}


exports = {
	init: function(model, request) {
		return new WebappContext(model, request);
	},
	helper: function() {
		return HELPER;
	},
	sitemap: function(key, fnc) {
		HELPER.sitemap[key] = function() {
			var pattern = fnc.apply(HELPER, arguments);
			return HELPER.url(pattern.id, pattern.params);
		};
		return this;
	}
};