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


define('zen-webapp/transition/snippet', [
        'jquery'
        ], function($) {
	
	return function($oldSnippet, $snippet, oLoading, cback) { // TODO manage situation when $snippet.length > 1
		var h = $oldSnippet.height();
		var H = $snippet.replaceAll($oldSnippet).height();
		$snippet
			.height(h)
			.animate({
				height: H
			}, 100, function() {
				$snippet.removeAttr('style');
				cback();
			});
	}
});