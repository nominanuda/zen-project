const CONSOLE = require('console');
const JUNIT = require('classpath:com/nominanuda/rhino/JUnit.js');


var nmap = require('numberedMap');

CONSOLE.log(nmap);

for (var k in nmap) {
	CONSOLE.log(k + ' is a ' + (typeof k));
	JUNIT.assert(k === nmap[k].key);
}