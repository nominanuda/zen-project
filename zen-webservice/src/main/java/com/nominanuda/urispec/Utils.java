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
package com.nominanuda.urispec;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;


public class Utils {
	private static final Pattern PIPEDMETHODS = Pattern.compile("\\s*\\p{Upper}+(?:\\s*\\|\\s*\\p{Upper}+)*\\s*");

	public static LinkedHashMap<String, List<String>> parseQueryString(String queryString) {
		final Scanner scanner = new Scanner(queryString);
		try {
			scanner.useDelimiter("&");
			LinkedHashMap<String, List<String>> res = new LinkedHashMap<String, List<String>>();
			while (scanner.hasNext()) {
				final String[] nameValue = scanner.next().split("=");
				if (nameValue.length == 0 || nameValue.length > 2) {
					throw new IllegalArgumentException("bad parameter");
				}
				final String name = nameValue[0];
				String value = null;
				if (nameValue.length == 2) {
					value = nameValue[1];
				}
				if (value == null) {
					value = "";
				}
				putVarVal(res, name, value);
			}
			return res;
		} finally {
			scanner.close();
		}
	}
	public static void putVarVal(Map<String, List<String>> m, String key, String val) {
		if(key == null || val == null) {
			throw new IllegalArgumentException();
		}
		List<String> o = m.get(key);
		if(o == null) {
			o = new LinkedList<String>();
			m.put(key, o); 
		}
		o.add(val);
	}

	public static String extractUriSpecFromSitemapMatch(String spec) {
		if (spec.contains(" ")) {
			String[] parts = spec.split("\\s+");
			if (PIPEDMETHODS.matcher(parts[0]).matches()) {
				return spec.substring(parts[0].length()).trim();
			}
		}
		return spec;
	}

}
