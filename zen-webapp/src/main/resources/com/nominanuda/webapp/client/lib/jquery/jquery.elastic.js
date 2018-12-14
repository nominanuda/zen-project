// Expanding Textareas v0.1.2
// MIT License
// https://github.com/bgrins/ExpandingTextareas

/*
 * AZ: modified to support input fields and fixed sized inputs/textareas
 */

(function(factory) {
	// Add jQuery via AMD registration or browser globals
	if (typeof define === 'function' && define.amd) {
		define([ 'jquery' ], factory);
	}
	else {
		factory(jQuery);
	}
}(function($) {

	var LINESCROLL_TOLERANCE_X = 0.51; // how to decide this?
	var LINESCROLL_TOLERANCE_Y = 5; // how to decide this?
	var CSS_TEXT = {
		'display': 'inline', 'position': 'static', 'line-height': 'inherit',
		'font-size': '1em', 'font-size-adjust': 'inherit', 'font-family': 'inherit',
		'font-style': 'inherit', 'font-weight': 'inherit',
		'margin': 0, 'border': 'none', 'padding': 0,
		'text-transform': 'inherit', 'text-align': 'inherit', 'text-decoration': 'inherit',
		'word-spacing': 'inherit', 'word-wrap': 'inherit', 'word-break': 'inherit',
		'letter-spacing': 'inherit', 'direction': 'inherit',
	};
	var CSS_CLONE_COPIED = [
		'min-width', 'max-width', 'min-height', 'max-height', 'line-height',
		'font-size', 'font-size-adjust', 'font-family', 'font-style', 'font-weight',
		'border-left-width', 'border-right-width', 'border-top-width', 'border-bottom-width',
		'padding-left', 'padding-right', 'padding-top','padding-bottom',
		'text-transform', 'text-align', 'text-decoration',
		'word-spacing', 'word-wrap', 'word-break',
		'letter-spacing', 'direction', 'display'
	];

	var Expanding = function($field, opts) {
		Expanding._registry.push(this);
		
		var isInput = $field.is('input');
		var $clone = this.$clone = $('<pre><br /></pre>');
		this.$text = $('<span />').prependTo($clone).css(CSS_TEXT);
		this.$field = $field;
		
		// proxy val() method
		var jQueryVal = $field.val;
		$field.val = function() {
			var result = jQueryVal.apply($field, arguments);
			arguments.length && $field.change();
			return result;
		};
		
		// cleanup if cloned
		var wrapClss = opts.clss + '-wrapper';
		var $parent = $field.parent();
		if ($parent.hasClass(wrapClss)) { // already the structure, maybe from cloning
			$field.replaceAll($parent).removeAttr('style');
		}
		
		// store important values
		this._oldFieldStyles = $field.attr('style');
		
		// styles for $field + $clone
		$field.add($clone).css({
			margin: 0,
			webkitBoxSizing: 'border-box',
			mozBoxSizing: 'border-box',
			boxSizing: 'border-box'
		});
		
		// styles for $clone
		var cloneCss = {
			border: '0 solid',
			visibility: 'hidden',
			overflow: 'hidden'
		};
		var w = $field.width();
		if (w > 10) { // explicit width
			cloneCss['width'] = w;
		}
		var h = $field.height();
		if (h > 0) { // explicit height
			cloneCss['height'] = h;
		}
		if ($field.attr('wrap') === 'off') {
			cloneCss['overflow-x'] = 'scroll';
		} else {
			cloneCss['white-space'] = (isInput ? 'pre' : 'pre-wrap');
		}
		$.each(CSS_CLONE_COPIED, function(i, property) {
			var val = $field.css(property);
			// Prevent overriding percentage css values. (AZ: ???)
			if ($clone.css(property) !== val) {
				cloneCss[property] = val;
				if (property === 'min-height' && parseInt(val) >= h) { // detected height was due to min-height
					delete cloneCss['height'];
				} else if (property === 'min-width' && parseInt(val) >= w) { // detected width was due to min-width
					delete cloneCss['width'];
				}
			}
		});
		$clone.css(cloneCss);
		
		$field.css({
			position: 'absolute',
			top: 0,
			left: 0,
			width: '100%',
			height: '100%',
			resize: 'none',
			overflow: 'hidden'
		})
		.wrap($('<div class="' + wrapClss + '" style="position:relative" />'))
		.after($clone);
		
		this.attach();
		this.update();
		opts.update && $field.on('update.jquery-elastic', opts.update);
	};

	// Stores (active) `Expanding` instances
	// Destroyed instances are removed
	Expanding._registry = [];

	// Returns the `Expanding` instance given a DOM node
	Expanding.getExpandingInstance = function(field) {
		var $fields = $.map(Expanding._registry, function(instance) {
			return instance.$field[0];
		}), index = $.inArray(field, $fields);
		return (index > -1 ? Expanding._registry[index] : null);
	};

	// Returns the version of Internet Explorer or -1
	// (indicating the use of another browser).
	// From: http://msdn.microsoft.com/en-us/library/ms537509(v=vs.85).aspx#ParsingUA
	var ieVersion = (function() {
		var v = -1;
		if (navigator.appName === 'Microsoft Internet Explorer') {
			var ua = navigator.userAgent;
			var re = new RegExp('MSIE ([0-9]{1,}[\\.0-9]{0,})');
			if (re.exec(ua) !== null) {
				v = parseFloat(RegExp.$1);
			}
		}
		return v;
	})();

	// Check for oninput support
	// IE9 supports oninput, but not when deleting text, so keyup is used.
	// onpropertychange _is_ supported by IE8/9, but may not be fired unless
	// attached with `attachEvent`
	// (see: http://stackoverflow.com/questions/18436424/ie-onpropertychange-event-doesnt-fire),
	// and so is avoided altogether.
	var inputSupported = 'oninput' in document.createElement('input') && ieVersion !== 9;

	Expanding.prototype = {
		// Attaches input events
		// Only attaches `keyup` events if `input` is not fully suported
		attach: function() {
			var _this = this;
			var events = 'input.jquery-elastic change.jquery-elastic';
			!inputSupported && (events += ' keyup.jquery-elastic');
			this.$field.on(events, function() {
				_this.update();
			});
		},

		// Updates the clone with the field value
		update: function() {
			var val = this.$field.val();
			this.$text.text(val.replace(/\r\n/g, '\n'));
			if (val && ((this.$clone.prop('scrollHeight') - this.$clone.innerHeight() > LINESCROLL_TOLERANCE_Y)
					|| (this.$clone.prop('scrollWidth') - this.$clone.innerWidth() > LINESCROLL_TOLERANCE_X))) {
				this.$field.val(val.slice(0, -1)); // overflowing, remove last character
			} else {
				// Use `triggerHandler` to prevent conflicts with `update` in Prototype.js
				this.$field.triggerHandler('update.jquery-elastic');
			}
		},

		// Tears down the plugin: removes generated elements, applies styles
		// that were prevously present, removes instance from registry,
		// unbinds events
		destroy: function() {
			this.$clone.remove();
			this.$field.unwrap().attr('style', this._oldFieldStyles || '');
			delete this._oldFieldStyles;
			var index = $.inArray(this, Expanding._registry);
			(index > -1) && Expanding._registry.splice(index, 1);
			this.$field.off('input.jquery-elastic change.jquery-elastic keyup.jquery-elastic update.jquery-elastic');
		}
	};

	$.elastic = $.extend({
		opts: {
			clss: 'elastic',
			update: function() {}
		}
	}, $.elastic || {});
	
	$.fn.elastic = function(o) {
		if (o === 'destroy') {
			this.each(function() {
				var instance = Expanding.getExpandingInstance(this);
				instance && instance.destroy();
			});
			return this;
		}
	
		// Checks to see if any of the given DOM nodes have the
		// expanding behaviour.
		if (o === 'active') {
			return !!this.filter(function() {
				return !!Expanding.getExpandingInstance(this);
			}).length;
		}
	
		var opts = $.extend({}, $.elastic.opts, o);
		
		this.filter('input:text,textarea').each(function() {
			var visible = (this.offsetWidth > 0 || this.offsetHeight > 0);
			var initialized = Expanding.getExpandingInstance(this);
			
			if (visible && !initialized) {
				new Expanding($(this), opts);
			} else {
				!visible && _warn('jquery.elastic: attempt to initialize an invisible field. Call expanding() again once it has been inserted into the page and/or is visible.');
				initialized && _warn('jquery.elastic: attempt to initialize a field that has already been initialized. Subsequent calls are ignored.');
			}
		});
		return this;
	};

	function _warn(text) {
		window.console && console.warn && console.warn(text);
	}
}));