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


// TODO move in zen-image?

define('zen-webapp/util/image', [
        'module'
        ], function(module) {
	
	var IMAGES_URL_RE = /(http.+\/[^\.]+)(\.((\d*)x(\d*)\.)?([a-z]+))?/;
	
	
	function img2obj(url) {
		var parts = IMAGES_URL_RE.exec(url);
		return parts ? {
			resource: parts[1],
			width: parseInt(parts[4]) || null,
			height: parseInt(parts[5]) || null,
			format: parts[6]
		} : null;
	}
	
	function obj2url(obj, width, height) {
		if (obj && obj.resource) {
			var url = obj.resource + '.';
			var w = (width !== undefined ? width : obj.width); // width == null -> img.xH.ext
			var h = (height !== undefined ? height : obj.height); // height == null -> img.Wx.ext 
			(w || h) && (url += (w || '') + 'x' + (h || '') + '.');
			return url + obj.format;
		}
		return null;
	}
	
	function render(obj, width, height, format) {
		obj = (typeof obj == 'string' ? img2obj(obj) : obj);
		format && (obj.format = format);
		return obj2url(obj, width, height);
	}
	
	
	return {
		render: render,
		img2obj: img2obj,
		obj2url: obj2url
	};
});