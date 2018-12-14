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


const LIB_CAST = __WEBAPP_REQUIRE('classpath:com/nominanuda/js/lib/cast.js');


exports = {
	pagination: function(page, count, available, config) {
//		var tot = Math.ceil((available || 0) / count);
//
//		if (tot > 1 && config) {
//			var pages = [], prevPages = [], nextPages = [], sIndex = -1;
//			var link = config.link, param = link.param, data = link.data;
//			
//			function pageJson(p) {
//				var d = Object.create(data); d[param] = p;
//				return {
//					url: urlFnc('', { // TODO allow custom patternId
//						entity: link.entity,
//						id: link.id
//					}, link.action, d),
//					num: '' + (p + 1)
//				};
//			}
//			
//			page = Math.max(0, Math.min(parseInt(page), tot - 1));
//			var from = page -1, to = page + 2;
//			
//			from > 2 && prevPages.unshift((from > 3 ? null : pageJson(2)));
//			from > 1 ? prevPages.unshift(pageJson(1)) : to++;
//			from > 0 ? prevPages.unshift(pageJson(0)) : to++;
//			to = Math.min(to, tot);
//			
//			to < (tot-2) && nextPages.push((to < (tot-3) ? null : pageJson(tot-3)));
//			to < (tot-1) ? nextPages.push(pageJson(tot-2)) : (from > 3 && from--); // TODO fix
//			to < tot ? nextPages.push(pageJson(tot-1)) : (from > 3 && from--);
//			from = Math.max(0, from);
//			
//			for (var p = from; p < to; p++) {
//				var json = pageJson(p);
//				if (p == page) {
//					json.selected = true;
//					sIndex = pages.length;
//				}
//				pages.push(json);
//			}
//			
//			return {
//				page: page,
//				ajax: config.ajax,
//				prev: pages[sIndex - 1],
//				next: pages[sIndex + 1],
//				pages: prevPages.concat(pages, nextPages)
//			};
//		}
		return {};
	},
	
	valuesAndLabels: function(values, labels) {
		values = LIB_CAST.toArray(values);
		if (values.length) { // do something only if there are values
			if (labels) {
				var l = {};
				if (LIB_CAST.isObject(labels)) { // it's already a map
					return {
						values: values.map(function(value, i) {
							value = (value || '').trim();
							l['_' + value] = labels[value]; // '_' to be sure it doesn't start with number
							return value;
						}),
						labels: l
					};
				}
				labels = LIB_CAST.toArray(labels);
				return {
					values: values.map(function(value, i) {
						value = (value || '').trim();
						l['_' + value] = labels[i]; // '_' to be sure it doesn't start with number
						return value;
					}),
					labels: l
				};
			}
			return {
				values: values.map(function(value) {
					return String(value);
				})
			};
		}
		return null;
	},
	
	fromLabels: function(labels) {
//		supported syntax:
//		{
//			value: 'Label',
//			'Group Label': [value, ...],
//			'Group Label': {
//				value: 'Label',
//				...
//			},
//			...
//		}
		
		var v = [], l = {}, g = {};
		for (var K in labels) {
			var value = labels[K];
			if (LIB_CAST.isObject(value)) { // also possible array
				var notArray = !LIB_CAST.isArray(value);
				var keys = notArray ? Object.keys(value) : value;
				var lastIndex = keys.length - 1;
				keys.forEach(function(k, i) {
					v.push(k);
					notArray && (l[k] = value[k]);
					if (i == 0) { // first value of group
						g[k] = {
							label: K,
							last: (lastIndex == 0 ? true : undefined) // and also end of group
						};
					} else if (i == lastIndex) {
						g[k] = {
							last: true
						};
					}
				});
			} else {
				v.push(K);
				l[K] = value;
			}
		}
		return {
			values: v,
			labels: l,
			groups: g
		};
	}
};