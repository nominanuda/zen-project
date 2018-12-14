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


define('zen-webapp/util/css', [
        'jquery',
        'require'
        ], function($, require) {
	
	
	var PATH_POSTFIX = '___css'; // as in boot.soy
	
	
	function loadAbs(styles, cback) {
		if (styles.length) {
			var $head = $('head');
			$.each(styles, function(index, style) {
				var url = require.toUrl(style + '.css');
				if (!$('link', $head).is('[href="' + url +'"]')) { // don't reload if present
					var $link = $('<link />').attr('rel', 'stylesheet').attr('href', url);
					$head.append($link); // written like this for ie8 on xp
				}
			});
		}
		cback && cback(); // call in any case
	}
	
	function toArr(styles) {
		return styles ? styles.splice ? styles : [styles] : [];
	}
	
	
	return {
		load: function(styles, cback) {
			loadAbs($.map(toArr(styles), function(style) {
				var i = style.indexOf('/'); // prefix cut point
				return style.substr(0, i) + PATH_POSTFIX + style.substr(i);
			}), cback);
			
		},
		loadAbs: function(styles, cback) {
			loadAbs(toArr(styles), cback);
		}
	};
});