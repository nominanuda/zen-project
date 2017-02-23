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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.nominanuda.urispec.Assert.*;

public class StringListModelAdapter implements StringModelAdapter<Map<String,Object>> {

	@SuppressWarnings("unchecked")
	/*@Nullable*/ private List<String> multivalue(Object o) throws NullPointerException {
		if (o == null) {
			return null;
		} else if (o instanceof List) {
			return ((List<String>) o);
		} else if (o instanceof Collection) {
			return new LinkedList<String>((Collection<String>) o);
		} else {
			return new LinkedList<String>(Arrays.asList(o.toString()));
		}
	}

	public Map<String,Object> createStringModel() {
		return new LinkedHashMap<String, Object>();
	}

	@SuppressWarnings("unchecked")
	public void push(Map<String,Object> model, String key, String val) {
		val = decode(val);
		Object oldval = model.get(key);
		if (oldval == null) {
			model.put(key, val);
		} else if (oldval instanceof List) {
			((List<String>) oldval).add(val);
		} else {
			List<String> l = new LinkedList<String>();
			l.add((String)oldval);
			l.add(val);
			model.put(key, l);
		}
	}

	private String decode(String encodedVal) {
		try {
			return URLDecoder.decode(encodedVal, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	private List<String> decode(List<String> encodedVals) {
		List<String> l = new LinkedList<String>();
		for(String s : encodedVals) {
			l.add(decode(s));
		}
		return l;
	}

	public void set(Map<String,Object> model, String key, String val) {
		val = decode(val);
		model.put(key, val);
	}

	@SuppressWarnings("unchecked")
	public boolean validateModel(Object m) {
		try {
			isTrue(m instanceof Map<?, ?>);
			for(Entry<String, Object> e : ((Map<String,Object>)m).entrySet()) {
				isTrue(e.getKey() instanceof String);
				Object v = e.getValue();
				if(v == null ) {
					continue;
				} else if(v instanceof String) {
					continue;
				} else if(v instanceof List<?>) {
					for(Object o : (List<?>)v) {
						isTrue(o == null || o instanceof String);
					}
				} else {
					fail();
				}
			}
		} catch(IllegalStateException e) {
			return false;
		}
		return true;
	}

	public void set(Map<String, Object> model, String key, List<String> val) {
		val = decode(val);
		model.put(key, copyList(val));
	}

	/*@Nullable*/ public List<String> getAsList(Map<String,Object> model, String key) {
		return multivalue(model.get(key));
	}

	/*@Nullable*/ public String getFirst(Map<String, Object> model, String key) {
		List<String> l = getAsList(model, key);
		return l == null || l.isEmpty() ? null : l.get(0);
	}
	@SuppressWarnings("unchecked")
	public void setAll(Map<String,Object> from, Map<String,Object> to) {
		for (Entry<String, ?> e : from.entrySet()) {
			String k = e.getKey();
			Object val = e.getValue();
			if(val == null) {
				throw new NullPointerException();
			} else if(val instanceof String) {
				set(to, k, (String)val);
			} else {
				set(to, k, (List<String>)val);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void pushAll(Map<String,Object> from, Map<String,Object> to) {
		for (Entry<String, ?> e : from.entrySet()) {
			String k = e.getKey();
			Object v = e.getValue();
			if(v instanceof List) {
				for(Object s : (List<Object>)v) {
					push(to, k, s.toString());
				}
			} else {
				push(to, k, v.toString());
			}
		}
	}

	private LinkedList<String> copyList(List<?> value) {
		LinkedList<String> res = new LinkedList<String>();
		for(Object o : (List<?>)value) {
			res.add(o == null ? null : o.toString());
		}
		return res;
	}
}
