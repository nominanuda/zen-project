const RE_DDMMYYYY = /(\d+)\D+(\d+)\D+(\d{4})/;
const RE_DDMMYYYYHHMM = /(\d+)\D+(\d+)\D+(\d{4})\D+(\d+)\D+(\d+)/;

exports = {
	ms2ddMMyyyy: function(ms, sep) {
		sep = sep || '.';
		var date = ms ? new Date(ms) : new Date();
		var d = date.getDate(), m = date.getMonth() + 1;
		return ('0' + d).slice(-2) + sep + ('0' + m).slice(-2) + sep + date.getFullYear();
	},
//	ms2MMddyyyy TODO
	
	ms2ddMMyyyyHHmm: function(ms, sep) {
		var date = ms ? new Date(ms) : new Date();
		var h = date.getHours(), m = date.getMinutes();
		return this.ms2ddMMyyyy(ms, sep) + ', ' + ('0' + h).slice(-2) + ':' + ('0' + m).slice(-2);
	},
//	ms2MMddyyyyHHmm TODO
	
	ms2ddMMyyyyHHmmssxxx: function(ms, sep) {
		var date = ms ? new Date(ms) : new Date();
		var s = date.getSeconds(), m = date.getMilliseconds();
		return this.ms2ddMMyyyyHHmm(ms, sep) + ':' + ('0' + s).slice(-2) + '.' + ('00' + m).slice(-3);
	},
//	ms2MMddyyyyHHmmssxxx TODO
	
	ddMMyyyy2ms: function(datetime) {
		(arguments.length > 1) && (datetime = Array.prototype.slice.call(arguments)); // when passing day, month, year,... as params
		(datetime && datetime.splice) && (datetime = datetime.join('.')); // if is array
		if (!RE_DDMMYYYYHHMM.test(datetime)) {
			if (RE_DDMMYYYY.test(datetime)) {
				datetime += ', 00:00';
			} else {
				return null;
			}
		}
		var params = RE_DDMMYYYYHHMM.exec(datetime);
		var dt = new Date(params[3], parseInt(params[2], 10) - 1, params[1], params[4], params[5]);
		return dt.getTime();
	},
//	MMddyyyy2ms TODO
	
	secs2time: function(secs) {
		if (typeof secs == 'number') {
			var s = secs % 60;
			var m = Math.floor(secs / 60) % 60;
			var h = Math.floor(secs / 3600);
			var time = ('0' + m).slice(-2) + ':' + ('0' + s).slice(-2);
			return h > 0 ? ('0' + h).slice(-2) + ':' + time : time;
		}
		return secs;
	}
};