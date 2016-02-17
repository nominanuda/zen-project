const CONSOLE = require('console');
const JUNIT = require('classpath:com/nominanuda/rhino/JUnit.js');


function test(v, r, t) {
	if (t) {
		JUNIT.assert(r === t);
		CONSOLE.log(v + ' as ' + r + ' OK!');
	} else {
		CONSOLE.log(v + ' as ' + r + ' ???');
	}
}


var o = require('overloads');
CONSOLE.log('\n\n overloads');
test(1, o.method(1), 'number');
test('s', o.method('s'), 'string');
test([], o.method([]), 'json');
test({}, o.method({}), 'json');


var o = require('numberOverload');
CONSOLE.log('\n\n number overload');
test(1, o.method(1), 'number');
test('s', o.method('s'), 'object');
test([], o.method([]), 'object');
test({}, o.method({}), 'object');


var o = require('stringOverload');
CONSOLE.log('\n\n string overload');
test(1, o.method(1));
test('s', o.method('s'), 'string');
test([], o.method([]), 'object');
test({}, o.method({}), 'object');


var o = require('arrayOverload');
CONSOLE.log('\n\n array overload');
test(1, o.method(1), 'object');
test('s', o.method('s'), 'object');
test([], o.method([]), 'array');
test({}, o.method({}), 'object');


var o = require('mapOverload');
CONSOLE.log('\n\n map overload');
test(1, o.method(1), 'object');
test('s', o.method('s'), 'object');
test([], o.method([]), 'object');
test({}, o.method({}), 'map');


var o = require('jsonOverload');
CONSOLE.log('\n\n json overload');
test(1, o.method(1), 'object');
test('s', o.method('s'), 'object');
test([], o.method([]), 'json');
test({}, o.method({}), 'json');


var o = require('noOverloads');
CONSOLE.log('\n\n no overloads');
test(1, o.method(1), 'object');
test('s', o.method('s'), 'object');
test([], o.method([]), 'object');
test({}, o.method({}), 'object');


var o = require('instanceOf');
CONSOLE.log('\n\n instance of');
test(1, o.method(1), 'object');
test('s', o.method('s'), 'object');
test([], o.method([]), 'array');
test({}, o.method({}), 'map');