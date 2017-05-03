function CAST_toArray(o, fnc) { // redefined here just to avoid require
	var arr = ((o !== undefined && o !== null) ? o.splice ? o : [o] : []);
	return fnc ? arr.map(fnc) : arr;
}

function filterEmpties(arr, fnc) {
	return CAST_toArray(arr).map(function(item) {
		if (item) {
			if (item.splice) { // array
				return filterEmpties(item, fnc);
			}
			if (typeof item == 'object') {
				for (var p in item) {
					item[p] = arguments.callee(item[p]);
				}
			} else if (fnc) {
				item = fnc(item);
			}
		}
		return item;
	}).filter(function(item) {
		if (item) {
			if (item.splice) { // array
				return item.filter(arguments.callee).length ? true : false;
			}
			if (typeof item == 'object') {
				var result = false;
				for (var p in item) {
					result = result || arguments.callee(item[p]);
				}
				return result;
			}
			return true;
		}
		return false;
	});
}


exports = {
	build: function(from, to, fnc) {
		var a = [];
		if (fnc) { // two params
			for (var i = from; i < to; i++) {
				a.push(fnc(i));
			}
		} else {
			if (to === undefined) { // single param
				from = 0;
				to = from;
			}
			for (var i = from; i < to; i++) {
				a.push(i);
			}
		}
		return a;
	},
	equals: function(a1, a2) {
		a1 = CAST_toArray(a1);
		a2 = CAST_toArray(a2);
		for (var i=0; i<a1.length; i++) {
			if (a1[i] !== a2[i]) {
				return false;
			}
		}
		return (a1.length == a2.length);
	},
	
	contains: function(arr, val) {
		return (CAST_toArray(arr).indexOf(val) > -1);
	},
	
	filterEmpties: filterEmpties,
	
	forceIn: function(v, arr) { // returns v if it's in arr, else the first arr item (for select widget values) DEPRECATED???
		return (arr.indexOf(v) == -1) ? arr[0] : v;
	},
	
	evUnshift: function(v, arr) {
		arr = CAST_toArray(arr);
		(arr.indexOf(v) == -1) && arr.unshift(v);
		return arr;
	},
	evPush: function(v, arr) {
		arr = CAST_toArray(arr);
		(arr.indexOf(v) == -1) && arr.push(v);
		return arr;
	},
	
	toMap: function(arr, propOrFnc, transFnc) {
		var map = {};
		!transFnc && (transFnc = function(item) {
			return item;
		});
		arr = CAST_toArray(arr);
		if (LIB_CAST.isFunction(propOrFnc)) {
			arr.forEach(function(item) {
				map[propOrFnc(item)] = transFnc(item);
			});
		} else {
			arr.forEach(function(item) {
				map[item[propOrFnc]] = transFnc(item);
			});
		}
		return map;
	}
};