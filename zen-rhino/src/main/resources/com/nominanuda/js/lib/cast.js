
function toFloat(o, v) {
	if (o) {
		if (o.splice) { // array
			return o.map(function(o) {
				return toFloat(o, v);
			});
		}
		if (typeof o == 'object') {
			for (var p in o) {
				o[p] = toFloat(o[p], v);
			}
			return o;
		}
		var f = parseFloat(o);
		if (!isNaN(f)) {
			return f;
		}
	}
	return v;
}


exports = {
	isArray: function(obj) {
		return obj && obj.splice ? true : false;
	},
	isBoolean: function(obj) {
		return typeof obj == 'boolean';
	},
	isDate: function(obj) {
		return obj && obj.getMonth ? true : false;
	},
	isFalse: function(obj) {
		return obj === false;
	},
	isFunction: function(obj) {
		return typeof obj == 'function';
	},
	isNumber: function(obj) {
		return typeof obj == 'number';
	},
	isNull: function(obj) {
		return obj === null;
	},
	isObject: function(obj) {
		return typeof obj == 'object';
	},
	isRegexp: function(obj) {
		return obj instanceof RegExp;
	},
	isString: function(obj) {
		return typeof obj == 'string';
	},
	isTrue: function(obj) {
		return obj === true;
	},
	isUndefined: function(obj) {
		return obj === undefined;
	},
	
	toArray: function(o, fnc) {
		var arr = ((o !== undefined && o !== null) ? o.splice ? o : [o] : []);
		return fnc ? arr.map(fnc) : arr;
	},
	toString: function(o, j) {
		return (o ? o.splice ? o.join(j||'') : o : '');
	},
	toFloat: toFloat
};