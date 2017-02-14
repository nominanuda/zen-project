const RE_EMAIL = /\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}\b/i;


exports = {
	value: function(v, msg) {
		if (!v) return msg;
	},
	
	email: function(e, msg) {
		if (!RE_EMAIL.test(e)) return msg;
	}
};