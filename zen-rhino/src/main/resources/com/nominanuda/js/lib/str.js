function trim(str) {
	if (typeof str == 'object') {
		for (var p in str) {
			str[p] = trim(str[p]);
		}
		return str;
	}
	return (str || '').trim();
}


exports = {
	trim: trim,
	
	capitalize: function(str) {
		return str && (str.charAt(0).toUpperCase() + str.substr(1)) || str;
	},
	
	noBreaks: function(str) {
		return str && str.replace && str.replace(/[\n\r\t]+/g, ' ').trim() || str;
	},
	
	cleanBreaks: function(str) {
		return str && str.replace && str.replace(/\r\n?/g, '\n').trim() || str;
	},
	
	cleanMsWord: function(str) {
		return str && str.replace && str.replace(/<!--[\s\S]*?-->/gi, '') || str;
	},
	
	format: function() { // str, param, param, param,...
		var args = arguments;
		return args[0].replace(/\{(\d+)\}/g, function() {
			return args[parseInt(arguments[1]) + 1];
		});
	},
	
	concat: function() { // returns '' in case of undefined args, so to replace (x ? x + ' - ' : '') constructs
		var str = '';
		for (var i=0; i<arguments.length; i++) {
			if (arguments[i] !== undefined) {
				str += arguments[i];
			} else {
				return '';
			}
		}
		return str;
	},
	
	remap: function(str, map) {
		return map && map[str] || str;
	}
};