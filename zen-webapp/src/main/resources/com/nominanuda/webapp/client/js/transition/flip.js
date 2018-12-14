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


define('zen-webapp/transition/flip', [
        'jquery',
        'zen-webapp/overlay'
        ], function($, OVERLAY) {
	
	var $loader = $('<div id="zen-webapp-ajax-flip-loader"></div>');
	
	function transition($oldPage, $newPage, oLoading, cback) {
		var animMs = 800;
		var $body = $oldPage.parent();
		$loader.css({
			width: $body.width()
		}).html($page).show();
		var oLoader = OVERLAY.append($loader, true).css($body.position());
		OVERLAY.medal(oLoading.spinner(false), oLoader).flip(true, null, animMs, function() {
			$oldPage.replaceWith($page);
			cback();
		});
		/*
		setTimeout(function() {
			OVERLAY.ontop();
		}, animMs / 2); // half of the spin -> below player
		*/
	}
	
	return transition;
});