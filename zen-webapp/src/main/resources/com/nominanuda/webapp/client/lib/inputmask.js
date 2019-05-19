/*
 * Copyright 2008-2018 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


define('zen-webapp-lib/inputmask', [
        'jquery',
        'zen-webapp-lib/jquery/jquery.inputmask.bundle-4.0.6.min'
        ], function($) {
	
	
	var CLASS_INVALID = 'invalid';
	var DATA_INPUTMASK = 'inputmask'; // as in widget.soy
	
	
	// cfr https://it.wikipedia.org/wiki/Codice_fiscale
	var FISCALCODE_ODD = {
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
	var FISCALCODE_EVEN = {
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
	var FISCALCODE_CHECK = {
		 0: 'A',	 7: 'H',	14: 'O',	21: 'V',
		 1: 'B',	 8: 'I',	15: 'P',	22: 'W',
		 2: 'C',	 9: 'J',	16: 'Q',	23: 'X',
		 3: 'D',	10: 'K',	17: 'R',	24: 'Y',
		 4: 'E',	11: 'L',	18: 'S',	25: 'Z',
		 5: 'F',	12: 'M',	19: 'T',
		 6: 'G',	13: 'N',	20: 'U'
	};
	
	
	Inputmask.extendDefaults({
		showMaskOnHover: false,
		removeMaskOnSubmit: true,
		importDataAttributes: false,
		onincomplete: function() {
			var $this = $(this);
			$this.val() && $this.addClass(CLASS_INVALID); // only if there is content
		},
		oncomplete: function() {
			$(this).removeClass(CLASS_INVALID);
		},
		oncleared: function() {
			$(this).removeClass(CLASS_INVALID);
		}
	});
	
	
	function $fix($elm) {
		return $elm.removeAttr('maxlength'); // needed or inputmask accepts shorter inputs as valid (why?)
	}
	
	
	function maskItalianFiscalCode($elm) {
		return $elm.inputmask({
			mask: 'X{16}',
			definitions: {
				'X': {
					validator: '[a-zA-Z0-9]',
					cardinality: 1
				}
			},
			isComplete: function(buffer, opts) {
				var code = buffer.join('');
				if (code.length == 16) {
					var checkSum = 0;
					code = code.toUpperCase();
					for (var i = 0; i < 15; i++) {
						checkSum += (i % 2
							? FISCALCODE_EVEN	// mod2 is 1 -> 1-based position is even
							: FISCALCODE_ODD	// mod2 is 0 -> 1-based position is odd
						)[code.charAt(i)];
					}
					return (FISCALCODE_CHECK[checkSum % 26] == code.charAt(15));
				}
				return false;
			}
		});
	}
	
	function maskItalianVatNumber($elm) {
		return $elm.inputmask({
			mask: '9{11}',
			isComplete: function(buffer, opts) {
				var checkSum = 0;
				for (var i = 0; i < 11; i++) {
					var n = parseInt(buffer[i]);
					if (isNaN(n)) return false;
					checkSum += (i % 2
						? n * 2 + (n >= 5 ? 1 : 0)	// mod2 is 1 -> 1-based position is even
						: n							// mod2 is 0 -> 1-based position is odd
					);
				}
				return checkSum > 0 && checkSum % 10 == 0;
			}
		});
	}
	
	
	return function($inputmasks) {
		$inputmasks.each(function() {
			var $inputmask = $fix($(this));
			var config = $inputmask.data(DATA_INPUTMASK);
				$inputmask.removeAttr('data-' + DATA_INPUTMASK); // avoid inputmask using data-inputmask by itself
			var placeholder = $inputmask.attr('placeholder');
				
			switch (typeof config) {
			case 'object':
				$inputmask.inputmask(placeholder !== undefined
					? $.extend(config, {
						placeholder: placeholder
					})
					: config);
				break;
				
			case 'string':
				switch (config) {
				case 'italianFiscalCode':
					maskItalianFiscalCode($inputmask);
					break;
				case 'italianVatNumber':
					maskItalianVatNumber($inputmask);
					break;
				default:
					$inputmask.inputmask(config, placeholder !== undefined
						? {
							placeholder: placeholder
						}
						: {});
				}
				break;
			}
			
			$inputmask.trigger('blur'); // to validate immediately
		});
	};
});