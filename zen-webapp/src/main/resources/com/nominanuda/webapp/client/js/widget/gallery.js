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


define('zen-webapp/widget/gallery', [
        'jquery',
        'zen-webapp/overlay',
        'zen-webapp/util/dom',
        'zen-webapp/util/image',
        'zen-webapp/widget/upload',
        ], function($, OVERLAY, UTIL_DOM, UTIL_IMG, WIDGET_UPLOAD) {
	
	
	var DATA_OBJ = 'obj';
	var DATA_EXTRA = 'extra';
	var DATA_CONSTRAIN = 'constrain';
	var SELECTOR_WIDGET = '.zen-webapp-gallery';
	
	
	var resizingConstrain = null;
	
	var $box = $(com.nominanuda.webapp.widget.edit.gallery_box({}));
	OVERLAY.box.cancel($('.ctrls.box', $box), updateBox);
	
	var $preview = $('.preview', $box);
	var $caption = $('.caption', $box);
	var $extra = $('input.extra', $box);
	var $extraLabel = $extra.closest('label').hide();
	var $removeBtn;
	
	function updatePreview(obj) {
		obj ? $preview.data(DATA_OBJ, obj) : $preview.removeData(DATA_OBJ);
		UTIL_DOM.img.insert($preview.empty(), obj, 'v', function() {
			OVERLAY.box.center();
		});
		$removeBtn.toggle(obj ? true : false);
	}
	function updateBox(img, caption, extra, extraLabel) {
		updatePreview(img);
		$caption.val(caption);
		$extra.val(extra).prop('placeholder', extraLabel);
		$extraLabel.toggle(extraLabel ? true : false);
	}
	
	$('.ctrls.img', $box).each(function() {
		var $this = $(this);
		$removeBtn = $('.cancel', $this).on('click', function() {
			updatePreview(); // remove box img
			OVERLAY.box.center();
		});
		WIDGET_UPLOAD.init($('.upload', $this), 'zen-image', function(json) {
			updatePreview(json); // update box img
		});
	});
	
	
	
	$('.ctrls.box button', $box).on('click', function() {
		var obj = $preview.data(DATA_OBJ);
		OVERLAY.box.off(function() {
			var $img = $('img', $currentItem);
			var $hiddens = $('.hidden', $currentItem);
			if (obj) {
				$hiddens.filter('.extra').val($extra.val());
				$hiddens.filter('.caption').val($caption.val());
				$hiddens.filter('.file').val(UTIL_IMG.obj2file(obj));
				$currentItem.prop('href', UTIL_IMG.obj2url(obj));
				UTIL_DOM.img.adapt($img, resizingConstrain, obj);
			} else {
				$currentItem.removeAttr('href');
				$img.removeAttr('src');
				$hiddens.val('');
			}
			updatePreview();
		});
	});
	var $currentItem;
	
	
	function init($gallery) {
		var extraLabel = $gallery.data(DATA_EXTRA);
		resizingConstrain = $gallery.data(DATA_CONSTRAIN);
		$('a:has(.hidden)', $gallery).on('click', function(e) {
			$currentItem = $(this);
			OVERLAY.box.on($currentItem, $box, function() {
				var img = $('img', $currentItem).prop('src');
				var caption = $('.caption', $currentItem).val();
				var extra = $('.extra', $currentItem).val();
				updateBox(img, caption, extra, extraLabel);
			}, updateBox);
			return UTIL_DOM.noevents(e);
		});
	}
	
	
	function widget($elms) {
		UTIL_DOM.findter(SELECTOR_WIDGET, $elms).each(function() {
			init($(this));
		});
	}
	
	
	return {
		init: init,
		widget: widget
	};
});