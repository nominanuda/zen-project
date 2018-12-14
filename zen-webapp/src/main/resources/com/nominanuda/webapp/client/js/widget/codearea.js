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
		'zen-webapp-lib/codemirror/lib/codemirror': {
			exports: 'CodeMirror'
		}
	}
});


define('zen-webapp/widget/codearea', [
        'jquery',
        'require',
        'zen-webapp/overlay',
        'zen-webapp/util/css',
        'zen-webapp/util/dom',
        'zen-webapp/util/image',
        'zen-webapp/widget/upload'
        ], function($, require, OVERLAY, UTIL_CSS, UTIL_DOM, UTIL_IMG, WIDGET_UPLOAD) {
	
	var DATA_SYNTAX = 'syntax';
	var DATA_IMAGES = 'images';
	var CLASS_DOSUBMIT = 'doSubmit';
	var SELECTOR_CODEAREA = 'div.zen-webapp-codearea';
	var SELECTOR_LAUNCHER = 'span.zen-webapp-codearea';
	
	
	UTIL_CSS.loadAbs([
		'zen-webapp-lib/codemirror/lib/codemirror',
		'zen-webapp-lib/codemirror/addon/display/fullscreen'
	]);
	
	
	function init($textarea, syntax, options) {
		var editor = null;
		options = options || {};
		if ($textarea && $textarea.length) {
			var modes = [], addons = [], config = {
//				autofocus: true,
				tabSize: 4,
				indentUnit: 4,
				indentWithTabs: true,
				lineNumbers: true,
				lineWrapping: true,
				autoClearEmptyLines: true
			};
			
			switch(syntax) {
			case 'css':
				modes = ['css'];
				addons = ['edit/closebrackets'];
				config.mode = 'text/css';
				break;
				
			case 'html':
				modes = ['css', 'javascript', 'xml', 'htmlmixed'];
				addons = ['edit/closetag'];
				config.mode = 'text/html';
				config.autoCloseTags = true;
				break;
				
			case 'xml':
				modes = ['xml'];
				addons = ['edit/closetag'];
				config.mode = 'text/xml';
				config.autoCloseTags = true;
				break;
				
			case 'javascript':
				modes = ['javascript'];
				addons = ['edit/closebrackets'];
				config.mode = 'text/javascript';
				config.autoCloseBrackets = true;
				break;
				
			case 'json':
				modes = ['javascript'];
				addons = ['edit/closebrackets'];
				config.mode = 'application/json';
				config.autoCloseBrackets = true;
				break;
				
			default:
				modes = [syntax];
			}
			
			if (modes.length) {
				require(['zen-webapp-lib/codemirror/lib/codemirror'], function(CODEMIRROR) { // first load codemirror...
					addons.push('edit/closebrackets', 'edit/formatting');
					var i, deps = [];
					for (i=0; i<modes.length; i++) {
						deps.push('zen-webapp-lib/codemirror/mode/' + modes[i] + '/' + modes[i]);
					}
					for (i=0; i<addons.length; i++) {
						deps.push('zen-webapp-lib/codemirror/addon/' + addons[i]);
					}
					require(deps, function() { // ...then all dependencies
						editor = CODEMIRROR.fromTextArea($textarea.get(0), config);
						if (options.autoformat) {
							editor.autoFormatRange({
								line: 0,
								ch: 0
							}, {
								line: editor.lineCount(),
								ch: 0
							});
							editor.scrollTo(0, 0);
						}
						editor.setCursor(0, 0, true);
					});
				});
			}
		}
		return function(flgSave) { // to be called before submit
			if (editor) {
				flgSave && editor.toTextArea();
				return editor.getValue();
			}
		};
	}
	
	
	
	function autosubmit($elm, flgOk, content, cback) {
		if (cback ? cback(flgOk, content) : true) {
			flgOk && $elm.hasClass(CLASS_DOSUBMIT) && $elm.closest('form').trigger('submit');
		}
	}
	
	
	
	function codeareas($elm, cback) {
		UTIL_DOM.findter(SELECTOR_CODEAREA, $elm).each(function() {
			var $this = $(this);
			var doneCback = init($('textarea', $this), $this.data(DATA_SYNTAX), {
				autoformat: true
			});
			
			$('div.images', $this).each(function() {
				var $this = $(this);
				var $text = $('.text', $this).on({
					'mousedown': function() {
						this.select();
					},
					'mouseup': false,
					'click': false
				});
				WIDGET_UPLOAD.init($('.button', $this), 'zen-image', function(json) {
					$text.val(UTIL_IMG.obj2url(json)).trigger('mousedown');
				});
			});
			
			$('.zen-webapp-ctrls button', $this).on('click', function() {
				autosubmit($this, true, doneCback(true), cback);
			});
			$('.zen-webapp-ctrls .cancel', $this).on('click', function() {
				cback && cback(false, doneCback(false)); // TODO verify if it does "cancel"
			});
		});
	}
	
	
	
	function launchers($elm, cback) {
		UTIL_DOM.findter(SELECTOR_LAUNCHER, $elm).each(function() {
			var $this = $(this);
			var $btn = $('a.button', $this);
			var $hidden = $('input[type=hidden]', $this);
			$btn.on('click', function() {
				var $content = $(com.nominanuda.webapp.widget.edit.codearea({
					name: 'none',
					value: $hidden.val(),
					syntax: $this.data(DATA_SYNTAX),
					images: $this.data(DATA_IMAGES)
				}));
				OVERLAY.lightbox.flipIn($content, $btn, function() {
					codeareas($content, function(flgOk, content) {
						flgOk && $hidden.val(content);
						OVERLAY.lightbox.flipOut(function() {
							autosubmit($this, flgOk, content, cback);
						});
						return false; // avoids autosubmit of codearea
					});
				});
			});
		});
	}
	
	
	
	function widget($elm, cback) {
		codeareas($elm, cback);
		launchers($elm, cback);
	}
	
	

	return {
		init: init,
		widget: widget
	};
});