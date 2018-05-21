const CONSOLE = require('console');
const JUNIT = require('classpath:com/nominanuda/rhino/JUnit.js');


var nmap = require('numberedMap');
CONSOLE.log('numberedMap: ' + nmap);
for (var k in nmap) {
	CONSOLE.log('numberedMap: ' + k + ' is a ' + (typeof k));
	JUNIT.assert(k === nmap[k].key);
}

var narr = require('normalArray');
CONSOLE.log('normalArray: ' + narr);
narr.forEach(function(v) {
	CONSOLE.log('normalArray: ' + v);
});

var elist = require('emptyList');
CONSOLE.log('emptyList: ' + elist);
elist.forEach(function() {
//	this "works" (forEach call doesn't throw error)
});

var slist = require('scalarList');
CONSOLE.log('scalarList: ' + slist);
slist.forEach(function(v) {
	CONSOLE.log('scalarArray: ' + v);
});

var wlist = require('wrapperList');
CONSOLE.log('wrapperList: ' + wlist);
wlist.forEach(function(w) {
	CONSOLE.log('wrapperList: ' + w);
});

var emap = require('emptyMap');
CONSOLE.log('emptyMap: ' + emap);
for (var k in emap) {
	CONSOLE.log('emptyMap: ' + k + ' -> ' + emap[k]);
}

var smap = require('scalarMap');
CONSOLE.log('scalarMap: ' + smap);
for (var k in smap) {
	CONSOLE.log('scalarMap: ' + k + ' -> ' + smap[k]);
}

var wmap = require('wrapperMap');
CONSOLE.log('wrapperMap: ' + wmap);
for (var k in wmap) {
	CONSOLE.log('wrapperMap: ' + k + ' -> ' + wmap[k])
}
