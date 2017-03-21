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
package com.nominanuda.web.mvc;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;

import com.nominanuda.urispec.StringModelAdapter;
import com.nominanuda.urispec.URISpec;
import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.common.Ex.NoException;
import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.JsonPath;
import com.nominanuda.zen.obj.JsonSerializer;
import com.nominanuda.zen.obj.JsonType;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;
import com.nominanuda.zen.obj.JsonPath.SafeConvertor;

public class ObjURISpec extends URISpec<Obj> {
	private static final JsonSerializer jsonSerializer = new JsonSerializer();
	private static final ObjStringModelAdapter defaultModelAdapter = new ObjStringModelAdapter();
	public ObjURISpec(String spec) {
		super(spec, defaultModelAdapter);
	}

	static class ObjStringModelAdapter implements StringModelAdapter<Obj> {
		private  JsonPath M = new JsonPath();
		private final static XURLDecoder urlDecoder = new XURLDecoder();

		public List<String> getAsList(Obj model, String path) {
			LinkedList<String> res = new LinkedList<String>();
			Object val = M.getPathSafe(model, path);
			if(val == null) {
				return null;
			}
			if(JsonType.isArr(val)) {
				Arr a = (Arr)val;
				int len = a.len();
				for(int i = 0; i < len; i++) {
					while(res.size() < i+1) {
						res.add(null);
					}
					Object v = a.fetch(i);
					res.set(i, v == null ? null : v.toString());
				}
			} else {
				res.add(val.toString());
			}
			return  res;
		}

		public Obj createStringModel() {
			return Obj.make();
		}

		public void setAll(Obj from, Obj to) {
			M.copy(from, to, JsonPath.MERGE_POLICY_OVERRIDE);
		}

		public void pushAll(Obj from, Obj to) {
			M.copy(M.convertLeaves(from, urlDecoder), to, 
				JsonPath.MERGE_POLICY_PUSH);
		}

		public void push(Obj model, String path, String val) {
			M.setOrPushPathProperty(model, path, urlDecoder.apply(val));
		}

		@Override
		public void set(Obj model, String path, String val) {
			M.setPathProperty(model, path, urlDecoder.apply(val));
		}

		public boolean validateModel(Object m) {
			return m != null && m instanceof Obj;
		}

		public void set(Obj model, String key, List<String> val) {
			Arr arr = Arr.make();
			for(String s : val) {
				arr.push(urlDecoder.apply(s));
			}
			M.setPathProperty(model, key, arr);
		}

		public String getFirst(Obj model, String key) {
			Object o = M.getPathSafe(model, key);
			if(o == null) {
				return null;
			} else if(JsonType.isNullablePrimitive(o)) {
				return jsonSerializer.toString(o);
			} else if(JsonType.isArr(o)) {
				Arr a = (Arr)o;
				if(a.len() == 0) {
					return null;
				} else {
					Object v = a.fetch(0);
					Check.illegalargument.assertTrue(JsonType.isNullablePrimitive(v));
					return v == null
						? null : jsonSerializer.toString(v);
				}
			} else {
				return Check.illegalargument.fail();
			}
		}
		private static class XURLDecoder implements SafeConvertor<Object,Object> {
			public Object apply(Object x) throws NoException {
				try {
					return x == null ? null : URLDecoder.decode(((String)x), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					throw new IllegalStateException(e);
				}
			}
			public boolean canConvert(Object o) {
				return true;
			}
			
		}
	}
}