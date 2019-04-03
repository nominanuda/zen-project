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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.Normalizer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.concurrent.ThreadSafe;

import com.nominanuda.zen.stereotype.ScopedSingletonFactory;


@ThreadSafe
public class Str {
	public static final Str STR = ScopedSingletonFactory.getInstance().buildJvmSingleton(Str.class);
	public static final Charset UTF8 = Charset.forName("UTF-8");
	public static final Charset ASCII = StandardCharsets.US_ASCII;

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

	private class ToString<T> implements Function<T, String> {
		@Override
		public String apply(T t) {
			return t.toString();
		}
	};
	@SuppressWarnings("unchecked")
	private final <T> ToString<T> toStringy() {
		return (ToString<T>) toStringyObj; 
	}
	private final ToString<Object> toStringyObj = new ToString<Object>(); 

	public <T> String join(String separator, Iterable<T> collection) {
		return join(separator, collection, toStringy());
	}

	public <T> String join(String separator, Iterable<T> collection, Function<T, String> f) {
		return join(separator, collection.iterator(), f);
	}
	
	public <T> String join(String separator, Iterator<T> itr) {
		return join(separator, itr, toStringy());
	}

	public <T> String join(String separator, Iterator<T> itr, Function<T, String> f) {
		StringBuilder sb = new StringBuilder();
		while(itr.hasNext()) {
			sb.append(f.apply(itr.next()));
			if(itr.hasNext()) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}
	
	public <T> String join(String separator, Object[] strings) {
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

	public  String joinArgs(String separator, Object... strings) {
		return join(separator, strings);
	}
	
	/**
	 * use {@link Str#joinArgs(String, Object...)}
	 * @param separator
	 * @param strings
	 * @return
	 */
	@Deprecated
	public <T> String join1(String separator, Object... strings) {
		return joinArgs(separator, strings);
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
	public <R, A, T> T splitAndTrim(String str, String regex, Function<String, R> mapper, Collector<R, A, T> collector) {
		return splitAndTrim(str, regex).stream().map(mapper).collect(collector);
	}
	public <R, A, T> T splitAndTrim(String str, Pattern regex, Function<String, R> mapper, Collector<R, A, T> collector) {
		return splitAndTrim(str, regex).stream().map(mapper).collect(collector);
	}
	public <R> List<R> splitAndTrim(String str, String regex, Function<String, R> mapper) {
		return splitAndTrim(str, regex, mapper, Collectors.toList());
	}
	public <R> List<R> splitAndTrim(String str, Pattern regex, Function<String, R> mapper) {
		return splitAndTrim(str, regex, mapper, Collectors.toList());
	}
	

	public String ntimes(CharSequence cs, int times) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < times; i++) {
			sb.append(cs);
		}
		return sb.toString();
	}

	public String fmt(String pattern, Object... arguments) throws NullPointerException {
		for(int i = 0; i < arguments.length; i++) {
			if(null == arguments[i]) {
				throw new NullPointerException("arguments " + i + " is null");
			} else {
				arguments[i] = arguments[i].toString();
			}
  		}
		return MessageFormat.format(pattern, arguments);
	}


	public String diacriticsAndMoreReplace(String str) {
		return nonDiacriticsReplace(diacriticsReplace(str));
	}

	private static final Pattern DIACRITICS_REX = Pattern
			.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");
	public String diacriticsReplace(String str) {
		str = Normalizer.normalize(str, Normalizer.Form.NFD);
		str = DIACRITICS_REX.matcher(str).replaceAll("");
		return str;
	}

	private String nonDiacriticsReplace(String cs) {
		StringBuilder sb = new StringBuilder();
		int len = cs.length();

		for (int i = 0; i < len; i++) {
			sb.append(nonDiacriticsOf(cs.charAt(i)));
		}
		return sb.toString();
	}

	private Object nonDiacriticsOf(char ch) {
		switch (ch) {
		case 'ß':
			return "ss";
		case 'æ':
			return "ae";
		case 'ø':
			return 'o';
//		case '©':
//			return 'c';
		case '\u00D0':// German Ð ð
		case '\u0110':
		case '\u0189':
			return 'D';
		case '\u00F0':
		case '\u0256':
		case '\u0111':
			return 'd';
		case '\u00DE':// Þ þ
			return "TH"; // thorn þ
		case '\u00FE':
			return "th"; // thorn þ
		default:
			return ch;
		}
	}

	public String stripWs(CharSequence s) {
		int len = s.length();
		char[] res = new char[len];
		int written = 0;
		for(int i = 0; i < len; i++) {
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
	
	public String random(int length) {
		byte[] array = new byte[length];
		new Random().nextBytes(array);
	    return new String(array, UTF8);
	}
}
