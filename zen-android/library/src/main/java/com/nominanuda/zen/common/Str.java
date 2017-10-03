/*
 * Copyright 2008-2016 the original author or authors.
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
package com.nominanuda.zen.common;

import com.nominanuda.zen.stereotype.ScopedSingletonFactory;

import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.concurrent.ThreadSafe;


@ThreadSafe
public class Str {
	public static final Str STR = ScopedSingletonFactory.getInstance().buildJvmSingleton(Str.class);
	public static final Charset UTF8 = Charset.forName("UTF-8");

	public boolean nullOrEmpty(String s) {
		return s == null || 0 == s.length();
	}
	public boolean notNullOrEmpty(String s) {
		return ! nullOrEmpty(s);
	}
	public boolean nullOrBlank(String s) {
		return s == null || "".equals(s.trim());
	}
	public boolean notNullOrBlank(String s) {
		return ! nullOrBlank(s);
	}

	public byte[] getBytesUtf8(String s) {
		return s.getBytes(UTF8);
	}

	public <T> String join1(String separator, Object... strings) {
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

	public <T> String join(String separator, Object[] strings) {
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

	private class ToString<T> {
		public String apply(T t) {
			return t.toString();
		}
	};
	@SuppressWarnings("unchecked")
	private final <T> ToString<T> toStringy() {
		return (ToString<T>) toStringyObj; 
	}
	private final ToString<Object> toStringyObj = new ToString<>();

	public <T> String join(String separator, Iterable<T> collection) {
		return join(separator, collection, toStringy());
	}

	public <T> String join(String separator, Iterable<T> collection, ToString<T> f) {
		return join(separator, collection.iterator(), f);
	}

	public  String joinArgs(String separator, Object... strings) {
		int len = strings.length;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			sb.append(strings[i].toString());
			if (i < len - 1) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}

	public <T> String join(String separator, Iterator<T> itr) {
		return join(separator, itr, toStringy());
	}

	public <T> String join(String separator, Iterator<T> itr, ToString<T> f) {
		StringBuilder sb = new StringBuilder();
		while(itr.hasNext()) {
			sb.append(f.apply(itr.next()));
			if(itr.hasNext()) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}

	public List<String> splitAndTrim(String str, String regex) {
		return splitAndTrim(str, Pattern.compile(regex));
	}
	public List<String> splitAndTrim(String str, Pattern regex) {
		String[] arr = regex.split(str);
		LinkedList<String> l = new LinkedList<String>();
		for(String s : arr) {
			l.add(s.trim());
		}
		return l;
	}

	public String ntimes(CharSequence cs, int times) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < times; i++) {
			sb.append(cs);
		}
		return sb.toString();
	}

	public String fmt(String pattern, Object... arguments) {
		for(int i = 0; i < arguments.length; i++) {
			if(null == arguments[i]) {
				throw new NullPointerException("arguments " + i + " is null");
			} else {
				arguments[i] = arguments[i].toString();
			}
  		}
		return MessageFormat.format(pattern, arguments);
	}

	public String stripWs(CharSequence s) {
		int len = s.length();
		char[] res = new char[len];
		int written = 0;
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if(! Character.isWhitespace(c)) {
				res[written++] = c;
			}
		}
		return new String(res, 0, written);
	}

	public StringBuilder builder(String... strings) {
		StringBuilder sb = new StringBuilder();
		for (String string : strings) {
			sb.append(string);
		}
		return sb;
	}
}
