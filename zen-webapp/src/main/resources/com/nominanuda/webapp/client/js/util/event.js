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


define('zen-webapp/util/event', [
        'jquery'
        ], function($) {
	
	
	var Manager = function(ctx) {
		this.ctx = ctx;
		this.events = {};
	}
	Manager.prototype.on = function(event, fnc) {
		this.events[event] = fnc;
	};
	Manager.prototype.off = function(event) {
		delete this.events[event];
	};
	Manager.prototype.call = function(event) {
		if (this.events[event]) {
			var args = Array.prototype.slice.call(arguments, 1);
			return (this.events[event].apply(this.ctx || window, args) !== false); // returns true if not explicitly false
		}
		return true;
	};
	

	return {
		manager: function(ctx) {
			return new Manager(ctx);
		},
		lazyleave: function($elm, ms, cback) {
			var timer;
			return $elm.on('mouseleave', function(e) {
				timer = setTimeout(function() {
					cback(e);
				}, ms);
			}).on('mouseenter', function() {
				clearTimeout(timer);
			});
		}
	};
});