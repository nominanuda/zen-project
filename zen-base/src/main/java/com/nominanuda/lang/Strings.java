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
package com.nominanuda.lang;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.nominanuda.code.ThreadSafe;

@ThreadSafe
public abstract class Strings {
	public static boolean nullOrEmpty(String s) {
		return s == null || 0 == s.length();
	}
	public static boolean notNullOrEmpty(String s) {
		return ! nullOrEmpty(s);
	}
	public static boolean nullOrBlank(String s) {
		return s == null || "".equals(s.trim());
	}
	public static boolean notNullOrBlank(String s) {
		return ! nullOrBlank(s);
	}

	public static String join(String separator, Object... strings) {
		int len = strings.length;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < len; i++) {
			sb.append(strings[i].toString());
			if(i < len - 1) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}

	public static String join(String separator, Iterable<?> collection) {
		return join(separator, collection.iterator());
	}

	public static String join(String separator, Iterator<?> itr) {
		StringBuilder sb = new StringBuilder();
		while(itr.hasNext()) {
			sb.append(itr.next().toString());
			if(itr.hasNext()) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}
	public static List<String> splitAndTrim(String str, String regex) {
		String[] arr = str.split(regex);
		LinkedList<String> l = new LinkedList<String>();
		for(String s : arr) {
			l.add(s.trim());
		}
		return l;
	}
}
