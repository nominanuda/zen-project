const RE_DDMMYYYY = /(\d+)\D+(\d+)\D+(\d{4})/; // same as in time.js
const RE_DDMMYYYYHHMM = /(\d+)\D+(\d+)\D+(\d{4})\D+(\d+)\D+(\d+)/; // same as in time.js
const RE_EMAIL = /\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}\b/i;

//cfr https://it.wikipedia.org/wiki/Codice_fiscale
const FISCAL_CODE_ODD = {
	'0':  1,	'9': 21,	'I': 19,	'R':  8,
	'1':  0,	'A':  1,	'J': 21,	'S': 12,
	'2':  5,	'B':  0,	'K':  2,	'T': 14,
	'3':  7,	'C':  5,	'L':  4,	'U': 16,
	'4':  9,	'D':  7,	'M': 18,	'V': 10,
	'5': 13,	'E':  9,	'N': 20,	'W': 22,
	'6': 15,	'F': 13,	'O': 11,	'X': 25,
	'7': 17,	'G': 15,	'P':  3,	'Y': 24,
	'8': 19,	'H': 17,	'Q':  6,	'Z': 23
};
const FISCAL_CODE_EVEN = {
	'0':  0,	'9':  9,	'I':  8,	'R': 17,
	'1':  1,	'A':  0,	'J':  9,	'S': 18,
	'2':  2,	'B':  1,	'K': 10,	'T': 19,
	'3':  3,	'C':  2,	'L': 11,	'U': 20,
	'4':  4,	'D':  3,	'M': 12,	'V': 21,
	'5':  5,	'E':  4,	'N': 13,	'W': 22,
	'6':  6,	'F':  5,	'O': 14,	'X': 23,
	'7':  7,	'G':  6,	'P': 15,	'Y': 24,
	'8':  8,	'H':  7,	'Q': 16,	'Z': 25
};
const FISCAL_CODE_CHECK = {
	 0: 'A',	 7: 'H',	14: 'O',	21: 'V',
	 1: 'B',	 8: 'I',	15: 'P',	22: 'W',
	 2: 'C',	 9: 'J',	16: 'Q',	23: 'X',
	 3: 'D',	10: 'K',	17: 'R',	24: 'Y',
	 4: 'E',	11: 'L',	18: 'S',	25: 'Z',
	 5: 'F',	12: 'M',	19: 'T',
	 6: 'G',	13: 'N',	20: 'U'
};


exports = {
	value: function(v, msg) {
		if (!v) return msg;
	},
	
	ddMMyyyy: function(d, msg) {
		if (!RE_DDMMYYYY.test(d)) return msg;
	},
	ddMMyyyyHHmm: function(d, msg) {
		if (!RE_DDMMYYYYHHMM.test(d)) return msg;
	},
	
	email: function(e, msg) {
		if (!RE_EMAIL.test(e)) return msg;
	},
	
	italianFiscalCode: function(fc, msg) {
		fc = (fc || '').toUpperCase().replace(/[^A-Z0-9]/g, '');
		if (fc.length == 16) {
			var checkSum = 0;
			for (var i = 0; i < 15; i++) {
				checkSum += (i % 2
					? FISCAL_CODE_EVEN	// mod2 is 1 -> 1-based position is even
					: FISCAL_CODE_ODD	// mod2 is 0 -> 1-based position is odd
				)[fc.charAt(i)];
			}
			if (FISCAL_CODE_CHECK[checkSum % 26] == fc.charAt(15)) {
				return; // check ok
			}
		}
		return msg;
	},
	
	italianVatNumber: function(vn, msg) {
		vn = (vn || '').replace(/\D/g, '');
		if (vn.length == 11) {
			var checkSum = 0;
			for (var i = 0; i < 11; i++) {
				var n = parseInt(vn.charAt(i));
				checkSum += (i % 2
					? n * 2 + (n >= 5 ? 1 : 0)	// mod2 is 1 -> 1-based position is even
					: n							// mod2 is 0 -> 1-based position is odd
				);
			}
			if (checkSum > 0 && checkSum % 10 == 0) {
				return; // check ok
			}
		}
		return msg;
	},
	
	regexp: function(v, re, msg) {
		if (!re.test(v)) return msg;
	}
};