const CONSOLE = require('console');
const JUNIT = require('classpath:com/nominanuda/rhino/JUnit.js');

const O = require('overloads');
const N = require('numberOverload');
const S = require('stringOverload');
const A = require('arrayOverload');
const J = require('jsonOverload');
const _ = require('noOverloads');


function test(v, r, t) {
	if (t) {
		JUNIT.assert(r === t);
		CONSOLE.log(v + ' as ' + r + ' OK!');
	} else {
		CONSOLE.log(v + ' as ' + r + ' ???');
	}
}


CONSOLE.log('\n\n overloads');
test(1, O.method(1), 'number');
test('s', O.method('s'), 'string');
test([], O.method([]), 'json');
test({}, O.method({}), 'json');


CONSOLE.log('\n\n number overload');
test(1, N.method(1), 'number');
test('s', N.method('s'), 'object');
test([], N.method([]), 'object');
test({}, N.method({}), 'object');


CONSOLE.log('\n\n string overload');
test(1, S.method(1));
test('s', S.method('s'), 'string');
test([], S.method([]), 'object');
test({}, S.method({}), 'object');


CONSOLE.log('\n\n array overload');
test(1, A.method(1), 'object');
test('s', A.method('s'), 'object');
test([], A.method([]), 'array');
test({}, A.method({}), 'object');


CONSOLE.log('\n\n json overload');
test(1, J.method(1), 'object');
test('s', J.method('s'), 'object');
test([], J.method([]), 'json');
test({}, J.method({}), 'json');


CONSOLE.log('\n\n no overloads');
test(1, _.method(1), 'object');
test('s', _.method('s'), 'object');
test([], _.method([]), 'object');
test({}, _.method({}), 'object');