var assertEquals = require('classpath:/com/nominanuda/rhino/JUnit.js').assertEquals;

var tpl = function(spec, model) {
	var pathAndQuery = spec.split('?');
	var re = /\{[^\}]+\}/g;
	var result = pathAndQuery[0].replace(re,function(str, p1, p2, offset, s) {
		return model[str.replace(/\{(\w+).*/, '$1')];
	});
	if(pathAndQuery.length > 1) {
		result += '?' + pathAndQuery[1].replace(re,function(str, offset) {
			var x = str.replace(/\{(\w+).*/, '$1');
			var p = model[x];
			var isPname = offset === 0 
				|| pathAndQuery[1].charAt(offset - 1) ==! '=';
			return ''+ (isPname ? x+'='+p : p);
		});
	}
	return result;
};

assertEquals(
'/a/FOO/BAR?x=X&Y=YY'
,
tpl('/a/{foo **}/{bar}?{x}&Y={y}',
{foo:'FOO',bar:'BAR',x:'X',y:'YY'})
);