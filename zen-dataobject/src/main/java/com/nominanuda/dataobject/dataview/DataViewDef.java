/*
 * Copyright 2008-2011 the original author or authors.
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
package com.nominanuda.dataobject.dataview;

import java.util.LinkedList;
import java.util.List;

import com.nominanuda.code.Nullable;
import com.nominanuda.lang.Check;

public class DataViewDef {
	private @Nullable/*in case of value object expansion*/ DataNode tree;
	private String sofar;

	public DataViewDef(String spec) {
		sofar = "";
		spec = spec.replaceAll("\\s", "");
		tree = new DataNode("");
		List<DataNode> chs = parseSeq(spec);
		for(DataNode ch : chs) {
			tree.addChild(ch);
		}
	}
	public DataViewDef(DataNode t, String sof) {
		sofar = sof;
		tree = t;
	}
	
	public String soFar() {
		return sofar;
	}
	public DataViewDef traverse(String k) {
		Check.illegalargument.assertTrue(isTraversable(k));
		return new DataViewDef(tree.getChild(k), "".equals(sofar) ? k : sofar + SEP + k);
	}
	public DataViewDef traverseNoChek(String k) {
		return new DataViewDef(null, "".equals(sofar) ? k : sofar + SEP + k);
	}
	public boolean isTraversable(String k) {
		return tree != null && tree.hasChild(k);
	}
	private final char SEP = '.';
	private List<DataNode> parseSeq(String s) {
		List<DataNode> nodes = new LinkedList<DataNode>();
		Check.illegalargument.assertFalse(s.startsWith("("));
		final char[] carr = s.toCharArray();
		StringBuilder pathBit = new StringBuilder();
		int nestLevel = 0;
		for(int i = 0; i < carr.length; i++) {
			char c = carr[i];
			switch (c) {
			case '(':
				pathBit.append(c);
				nestLevel++;
				break;
			case ')':
				pathBit.append(c);
				nestLevel--;
				break;
			case ',':
				if(nestLevel == 0) {
					nodes.add(parseSingle(pathBit.toString()));
					pathBit = new StringBuilder();
				} else {
					pathBit.append(c);
				}
				break;
			default:
				if (c == '_' || c == SEP || (c >= '0' && c <= '9')
				|| (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
					pathBit.append(c);
				} else {
					throw new IllegalArgumentException("wrong char in identifier:"+c);
				}
				break;
			}
		}
		nodes.add(parseSingle(pathBit.toString()));
		return nodes;
	}

	private DataNode parseSingle(String s) {
		Check.illegalstate.assertFalse(s.startsWith("("));
		DataNode root = null;
		DataNode cur = null;
		final char[] carr = s.toCharArray();
		StringBuilder pathBit = new StringBuilder();
		for(int i = 0; i < carr.length; i++) {
			char c = carr[i];
			switch (c) {
			case '(':
				Check.illegalstate.assertFalse(root == null);
				Check.illegalstate.assertFalse(pathBit.length() > 0);
				int last = searchCloseParens(carr, i);
				Check.illegalstate.assertFalse(last < carr.length - 1);
				String expr = new String(carr, i + 1, last - i - 1);
				List<DataNode> l = parseSeq(expr);
				for(DataNode n : l) {
					cur.addChild(n);
				}
				return root;
			case ')':
				Check.illegalstate.fail();
				break;
			case ',':
				Check.illegalstate.fail();
				break;
			case SEP:
				DataNode n = new DataNode(pathBit.toString());
				if(root == null) {
					root = n;
				}
				if(cur != null) {
					cur.addChild(n);
				}
				pathBit = new StringBuilder();
				cur = n;
				break;
			default:
				if (c == '_' || (c >= '0' && c <= '9')
				|| (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
					pathBit.append(c);
				} else {
					throw new IllegalArgumentException("wrong char in identifier:"+c);
				}
				break;
			}
		}
		DataNode n = new DataNode(pathBit.toString());
		if(root == null) {
			root = n;
		}
		if(cur != null) {
			cur.addChild(n);
		}
		return root;
	}

	private int searchCloseParens(char[] carr, int start) {
		int nestLevel = 0;
		if(carr[start] == '(') {
			start++;
		}
		for(int i = start; i < carr.length; i++) {
			char c = carr[i];
			switch (c) {
			case '(':
				nestLevel++;
				break;
			case ')':
				if(nestLevel == 0) {
					return i;
				} else {
					nestLevel--;
				}
				break;
			default:
				break;
			}
		}
		throw new IllegalStateException("close parens not found");
	}
	private class DataNode {
		private final List<DataNode> nodes = new LinkedList<DataNode>();
		private final String name;

		public DataNode(String s) {
			name = s;
		}

		public DataNode getChild(String k) {
			for(DataNode n : nodes) {
				if(k.equals(n.getName())) {
					return n;
				}
			}
			return Check.illegalstate.fail();
		}

		public boolean hasChild(String k) {
			for(DataNode n : nodes) {
				if(k.equals(n.getName())) {
					return true;
				}
			}
			return false;
		}

		public void addChild(DataNode ch) {
			Check.illegalstate.assertFalse(hasChild(ch.getName()));
			nodes.add(ch);
		}

		private String getName() {
			return name;
		}
		
	}
}