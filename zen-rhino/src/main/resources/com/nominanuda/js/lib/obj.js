function CAST_toArray(o, fnc) { // redefined here just to avoid require
	var arr = ((o !== undefined && o !== null) ? o.splice ? o : [o] : []);
	return fnc ? arr.map(fnc) : arr;
}

function keys(obj) {
	return Object.keys(obj);
}

function vals(obj) {
	var vals = [];
	for (var k in obj) {
		vals.push(obj[k]);
	}
	return vals;
}

function map(obj, fnc) {
	if (obj) {
		for (var k in obj) {
			obj[k] = fnc(k, obj[k]);
		}
	}
	return obj;
}

function arr(obj, fnc) {
	!obj && (obj = {});
	return Object.keys(obj).map(function(key) {
		return fnc(key, obj[key]);
	});
}

function _eval(obj) { // with "_" to keep eval() visible
	cycle(obj, function(k, v) {
		delete obj[k];
		obj[eval(k)] = v;
	});
	return obj;
}

function cycle(obj, fnc) {
	if (obj) {
		for (var k in obj) {
			fnc(k, obj[k]);
		}
	}
}

function filter(obj, fnc) {
	if (obj) {
		var result = {};
		for (var k in obj) {
			fnc(k, obj[k]) && (result[k] = obj[k]);
		}
		return result;
	}
	return null;
}

function slice(src1, props, src2) { // shrink object to selected properties
	var dst = {};
	src1 && CAST_toArray(props).forEach(function(p) {
		dst[p] = src1[p];
	});
	return merge(src2, dst);
}

function merge(src, dst, fnc) { // add all properties (override)
	if (src) {
		dst = dst || {};
		for (var p in src) {
			dst[p] = (fnc ? fnc(src[p], p) : src[p]);
		}
	}
	return dst;
}

function expand(src, dst, fnc) { // add missing properties (no override)
	if (src) {
		dst = dst || {};
		for (var p in src) {
			if (!dst[p]) {
				dst[p] = (fnc ? fnc(src[p], p) : src[p]);
			}
		}
	}
	return dst;
}

function override(src, dst, fnc) { // override existing properties
	if (src) {
		dst = dst || {};
		for (var p in dst) {
			dst[p] = (fnc ? fnc(src[p], p) : src[p]);
		}
	}
	return dst;
}

function pour(src, dst, fnc, defs) { // keep all dst private properties but add all src public properties
	if (src) {
		dst = dst || {};
		if (defs) {
			if (fnc) {
				for (var p in defs) {
					dst[p] = fnc(src[p] !== undefined ? src[p] : defs[p], p);
				}
			} else {
				for (var p in defs) {
					dst[p] = (src[p] !== undefined ? src[p] : defs[p]);
				}
			}
		} else {
			for (var p in dst) {
				(p.charAt(0) != '_') && (dst[p] = undefined);
			}
			if (fnc) {
				for (var p in src) {
					(p.charAt(0) != '_') && (dst[p] = fnc(src[p], p));
				}
			} else {
				for (var p in src) {
					(p.charAt(0) != '_') && (dst[p] = src[p]);
				}
			}
		}
	}
	return dst;
}

function build() {
	var obj = {};
	for (var i=0; i<arguments.length;) {
		obj[arguments[i++]] = arguments[i++];
	}
	return obj;
}

function flatten(prefix, obj, map) {
	map = map || {};
	if (obj) {
		(function paramize(name, value) {
			if (value) {
				if (value.splice) {
					value.forEach(function(v) {
						paramize(name, v);
					});
				} else if (typeof value == 'object') {
					for (var p in value) {
						paramize((name && name + '.' || '') + p, value[p]);
					}
				} else {
					map[name] = value;
				}
			}
		})(prefix, obj);
	}
	return map;
}

function contains(obj, value) {
	for (var p in obj) {
		if (obj[p] === value) {
			return p
		}
	}
	return null;
}


exports = {
	keys: keys,
	vals: vals,
	map: map,
	arr: arr,
	eval: _eval,
	cycle: cycle,
	filter: filter,
	slice: slice,
	merge: merge,
	expand: expand,
	override: override,
	pour: pour,
	build: build,
	flatten: flatten,
	contains: contains
};