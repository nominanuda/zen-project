/*
 * HTML Parser By John Resig (ejohn.org)
 * Original code by Erik Arvidsson, Mozilla Public License
 * http://erik.eae.net/simplehtmlparser/simplehtmlparser.js
 *
 * // Use like so:
 * HTMLParser(htmlString, {
 *	 start: function(tag, attrs, unary) {},
 *	 end: function(tag) {},
 *	 chars: function(text) {},
 *	 comment: function(text) {}
 * });
 */

/*
 * formatXml
 * 
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 Stuart Powers
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

/*
 * AZ: modified to support exports + different interface
 */

// Regular Expressions for parsing tags and attributes
var startTag = /^<([-A-Za-z0-9_]+)((?:\s+\w+(?:\s*=\s*(?:(?:"[^"]*")|(?:'[^']*')|[^>\s]+))?)*)\s*(\/?)>/,
	endTag = /^<\/([-A-Za-z0-9_]+)[^>]*>/,
	attr = /([-A-Za-z0-9_]+)(?:\s*=\s*(?:(?:"((?:\\.|[^"])*)")|(?:'((?:\\.|[^'])*)')|([^>\s]+)))?/g;
// Empty Elements - HTML 4.01
var empty = makeMap('area,base,basefont,br,col,frame,hr,img,input,isindex,link,meta,param,embed');
// Block Elements - HTML 4.01
var block = makeMap('address,applet,blockquote,button,center,dd,del,dir,div,dl,dt,fieldset,form,frameset,hr,iframe,ins,isindex,li,map,menu,noframes,noscript,object,ol,p,pre,script,table,tbody,td,tfoot,th,thead,tr,ul');
// Inline Elements - HTML 4.01
var inline = makeMap('a,abbr,acronym,applet,b,basefont,bdo,big,br,button,cite,code,del,dfn,em,font,i,iframe,img,input,ins,kbd,label,map,object,q,s,samp,script,select,small,span,strike,strong,sub,sup,textarea,tt,u,var');
// Elements that you can, intentionally, leave open (and which close themselves)
var closeSelf = makeMap('colgroup,dd,dt,li,options,p,td,tfoot,th,thead,tr');
// Attributes that have their values filled in disabled="disabled"
var fillAttrs = makeMap('checked,compact,declare,defer,disabled,ismap,multiple,nohref,noresize,noshade,nowrap,readonly,selected');
// Special Elements (can contain anything)
var special = makeMap('script,style');

function makeMap(str) {
	var obj = {}, items = str.split(',');
	for (var i = 0; i < items.length; i++) {
		obj[items[i]] = true;
	}
	return obj;
}


function saxParse(html, handler) {
	handler = handler || {};
	!handler.comment && (handler.comment = function() {});
	!handler.chars && (handler.chars = function() {});
	!handler.start && (handler.start = function() {});
	!handler.end && (handler.end = function() {});
	
	var index, chars, match;
	var stack = [];
	stack.last = function() {
		return this[this.length - 1];
	};
	
	var last = html;
	while (html) {
		chars = true;

		// Make sure we're not in a script or style element
		if (!stack.last() || !special[stack.last()]) {

			// Comment
			if (html.indexOf('<!--') == 0) {
				index = html.indexOf('-->');
				if (index >= 0) {
					handler.comment(html.substring(4, index));
					html = html.substring(index + 3);
					chars = false;
				}

				// end tag
			} else if (html.indexOf('</') == 0) {
				match = html.match(endTag);
				if (match) {
					html = html.substring(match[0].length);
					match[0].replace(endTag, parseEndTag);
					chars = false;
				}

				// start tag
			} else if (html.indexOf('<') == 0) {
				match = html.match(startTag);
				if (match) {
					html = html.substring(match[0].length);
					match[0].replace(startTag, parseStartTag);
					chars = false;
				}
			}

			if (chars) {
				index = html.indexOf('<');
				var text = index < 0 ? html : html.substring(0, index);
				html = index < 0 ? '' : html.substring(index);
				handler.chars(text);
			}

		} else {
			html = html.replace(new RegExp('(.*)<\/' + stack.last() + '[^>]*>'), function(all, text) {
				text = text.replace(/<!--(.*?)-->/g, '$1').replace(/<!\[CDATA\[(.*?)]]>/g, '$1');
				handler.chars(text);
				return '';
			});
			parseEndTag('', stack.last());
		}

		if (html == last) {
			throw 'Parse Error: ' + html;
		}
		last = html;
	}
	// Clean up any remaining tags
	parseEndTag();

	function parseStartTag(tag, tagName, rest, unary) {
		tagName = tagName.toLowerCase();

		if (block[tagName]) {
			while (stack.last() && inline[stack.last()]) {
				parseEndTag('', stack.last());
			}
		}

		if (closeSelf[tagName] && stack.last() == tagName) {
			parseEndTag('', tagName);
		}

		unary = empty[tagName] || !! unary;
		(!unary) && stack.push(tagName);

		var attrs = [];
		rest.replace(attr, function(match, name) {
			var value = arguments[2] ? arguments[2] :
				arguments[3] ? arguments[3] :
				arguments[4] ? arguments[4] :
				fillAttrs[name] ? name : '';

			attrs.push({
				name: name,
				value: value,
				escaped: value.replace(/(^|[^\\])"/g, '$1\\\"') //"
			});
		});
		handler.start(tagName, attrs, unary);
	}

	function parseEndTag(tag, tagName) {
		var pos = 0;
		if (tagName) { // Find the closest opened tag of the same type
			for (var pos = stack.length - 1; pos >= 0; pos--) {
				if (stack[pos] == tagName) {
					break;
				}
			}
		}
		if (pos >= 0) {
			// Close all the open elements, up the stack
			for (var i = stack.length - 1; i >= pos; i--) {
				handler.end(stack[i]);
			}
			// Remove the open elements from the stack
			stack.length = pos;
		}
	}
};


exports = {
	saxParse: saxParse,
	
	cleanHtml: function(html, count, ellipsis) {
		var xml = '';
		var go = true;
		var deepness = 0;
		count = count || Number.POSITIVE_INFINITY;
		saxParse(html, {
			start: function(tag, attrs, unary) {
				if (go) {
					xml += '<' + tag;
					for (var i = 0; i < attrs.length; i++) {
						xml += ' ' + attrs[i].name + '="' + attrs[i].escaped + '"';
					}
					xml += (unary ? '/' : '') + '>';
					!unary && deepness++;
				}
			},
			end: function(tag) {
				if (go || deepness > 0) {
					xml += '</' + tag + '>';
					deepness--;
				}
			},
			chars: function(text) {
				xml += text.substr(0, count);
				count -= text.length;
				go = (count > 0);
			},
			comment: function(text) {
//				xml += '<!--' + text + '-->';
			}
		});
		(count < 0) && (xml += (ellipsis || ''));
		return xml;
	},

	formatXml: function(xml, token) {
	    var formatted = '';
	    token = token || '  ';
	    var reg = /(>)(<)(\/*)/g;
	    xml = xml.replace(reg, '$1\r\n$2$3');
	    var pad = 0;
	    xml.split('\r\n').forEach(function(node, index) {
	        var indent = 0;
	        if (node.match( /.+<\/\w[^>]*>$/ )) {
	            indent = 0;
	        } else if (node.match( /^<\/\w/ )) {
	            if (pad != 0) {
	                pad -= 1;
	            }
	        } else if (node.match( /^<\w[^>]*[^\/]>.*$/ )) {
	            indent = 1;
	        } else {
	            indent = 0;
	        }

	        var padding = '';
	        for (var i = 0; i < pad; i++) {
	        	padding += token;
	        }

	        formatted += padding + node + '\r\n';
	        pad += indent;
	    });

	    return formatted;
	}
};