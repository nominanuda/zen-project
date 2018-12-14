(function($) {
	var defaultProps = ['font-size', 'font-style', 'font-weight', 'width', 'height', 'color',
	                    'background-color', 'background-image', 'background-repeat', 'background-position', 'background-size',
	                    'border-top', 'border-right', 'border-bottom', 'border-left', 'border-radius',
	                    'padding-left', 'padding-top', 'padding-right', 'padding-bottom',
	                    'text-align', 'text-transform', 'vertical-align', 'line-height', 'white-space'];
	
	var allProps = defaultProps.concat(['float', 'display', 'position',
	                                    'top', 'right', 'bottom', 'left',
	                                    'margin-top', 'margin-right', 'margin-bottom', 'margin-left']);

	
	function cssMap($src, props) {
		var map = {};
		props = props || defaultProps;
		!$.isArray(props) && (props = props.split(' '));
		$.each(props, function(i, p) {
			map[p] = $src.css(p);
		});
		return map;
	}
	
	function map$obj($src, $dst, props) {
		$dst.css(cssMap($src, props));
		return $src;
	}
	
	
	$.fn.cssMap = function(obj, props) {
		var $src = this.first();
		if (obj instanceof $) {
			return map$obj($src, obj, props);
		} else {
			return cssMap($src, obj);
		}
	};
	
	$.fn.cssClone = function() {
		var $src = this.first();
		var $clone = $src.clone();
		var $clonelms = $clone.find('*');
		map$obj($src, $clone).find('*').each(function(i) {
			map$obj($(this), $clonelms.eq(i), allProps);
		});
		return $clone;
	};
})(jQuery);