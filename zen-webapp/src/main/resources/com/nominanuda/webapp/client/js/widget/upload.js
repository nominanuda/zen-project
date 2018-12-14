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


require.config({
	shim: {
		'zen-webapp-lib/fineuploader/fine-uploader.min': {
			exports: 'qq'
		}
	}
});


define('zen-webapp/widget/upload', [
        'jquery',
        'module',
        'zen-webapp/util/dom',
        'zen-webapp/util/image',
        'zen-webapp-lib/fineuploader/fine-uploader.min',
        'zen-webapp-lib/jquery/jquery.json-2.3.min'
        ], function($, module, UTIL_DOM, UTIL_IMG, FINEUPLOADER) {
	
	
	var DATA_NAME = 'name';
	var DATA_TYPE = 'type';
	var DATA_EXPLODE = 'explode';
	var CLASS_DOSUBMIT = 'doSubmit';
	var SELECTOR_WIDGET = 'span.zen-webapp-upload';
	
	var urls = module.config().urls || {};
	
	
	function init($btn, type, cback) {
		if (!type) {
			alert('Type of upload missing!');
			return;
		}
		if (!urls[type]) {
			alert('Upload url not defined for type "' + type + '"!');
			return;
		}
		
		var extensions = [];
		switch (type) {
		case 'zen-image':
			extensions = ['png', 'jpeg', 'jpg'];
			break;
		}
		
		new FINEUPLOADER.FineUploaderBasic({
			button: $btn.get(0),
			multiple: false,
			request: {
				endpoint: urls[type],
				forceMultipart: true
			},
			validation: {
				allowedExtensions: extensions
			},
			callbacks: {
				onComplete: function(id, file, json) {
					cback && cback(json);
				}
			}
		});
	}
	
	
	
	function refresh($elm, $del) {
		var $hiddens = $('input[type=hidden]', $elm);
		$del.toggle($hiddens.val() ? true : false);
		return $hiddens;
	}
	
	
	function autosubmit($elm, json, cback) {
		if (cback ? cback(json) : true) {
			$elm.hasClass(CLASS_DOSUBMIT) && $elm.closest('form').trigger('submit');
		}
	}
	
	
	function widget($elm, cback) {
		UTIL_DOM.findter(SELECTOR_WIDGET, $elm).each(function() {
			var $this = $(this);
			var $upload = $('.action-upload', $this);
			var $delete = $('.action-delete', $this).on('click', function(e) {
				$hiddens.val('');
				refresh($this, $delete);
				autosubmit($this, null, cback);
				return UTIL_DOM.noevents(e);
			});
			var $hiddens = refresh($this, $delete);
			
			var type = $this.data(DATA_TYPE);
			init($upload, type, function(json) {
				$hiddens.remove();
				var name = $this.data(DATA_NAME);
				if ($this.data(DATA_EXPLODE)) {
					$this.append(UTIL_DOM.deep$hiddens(name, json));
				} else {
					var value;
					switch (type) {
					case 'zen-image':
						value = UTIL_IMG.obj2file(json);
						break;
					default:
						value = $.toJSON(json);
					}
					$this.append(UTIL_DOM.new$hidden(name, value));
				}
				$hiddens = refresh($this, $delete);
				autosubmit($this, json, cback);
			});
		});
	}
	
	

	return {
		init: init,
		widget: widget
	};
});