/*
 * Copyright 2008-2018 the original author or authors.
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
package com.nominanuda.apikey;

import static com.nominanuda.zen.common.Str.UTF8;
import static com.nominanuda.zen.obj.wrap.Wrap.WF;

import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.wrap.ObjWrapper;

public class Field {
	private static final Object NULL = new Object();
	private FieldConfig cfg;
	private byte[] encoded;
	private Object decoded;

	public Field(FieldConfig cfg) {
		this.cfg = cfg;
	}

	public byte[] save() {
		if(encoded == null) {
			encoded = toBinary(Check.notNull(decoded));
		}
		return encoded;
	}

	//TODO review with zen 2 marshalling tools
	private byte[] toBinary(Object val) {
		if(val == NULL) {
			return new byte[0];
		} else {
			if(val instanceof ObjWrapper) {
				val = ((ObjWrapper)val).unwrap();
			}
			if(val instanceof ObjWrapper) {
				val = ((ObjWrapper)val).unwrap();
			}
			return val.toString().getBytes(UTF8);
		}
	}

	private Object fromBinary(byte[] clearText) {
		if(clearText.length == 0) {
			return NULL;
		}
		String s = new String(clearText, UTF8);
		if(cfg.multi) {
			Arr a = Arr.parse(s);
			return a;
		} else {
			if(Boolean.class.equals(cfg.dataType)) {
				return Boolean.valueOf(s);
			} else if(Integer.class.equals(cfg.dataType)) {
				return Integer.valueOf(s);
			} else if(Long.class.equals(cfg.dataType)) {
				return Long.valueOf(s);
			} else if(Double.class.equals(cfg.dataType)) {
				return Double.valueOf(s);
			} else if(String.class.equals(cfg.dataType)) {
				return s;
			} else {
				try {
					Obj o = Obj.parse(s);
					if(Obj.class.equals(cfg.dataType)) {
						return o;
					} else /*if(Obj.class.isAssignableFrom(cfg.dataType))*/ {
						return WF.wrap(o, cfg.dataType);
					}
				} catch(Exception e) {
					throw new IllegalArgumentException(e);
				}
			}
		}
	}

	private static void assertValueCompliant(FieldConfig cfg, Object value) {
		//Check.notNull(value);
		if(value == NULL) {
			return;
		}
		if(value instanceof Arr) {
			Check.illegalargument.assertTrue(cfg.multi);
			for(Object member : (Arr)value) {
				Check.illegalargument.assertInstanceOf(member, cfg.dataType);
			}
		} else {
			Check.illegalargument.assertInstanceOf(value, cfg.dataType);
		}
	}
	public FieldConfig getFieldConfig() {
		return cfg;
	}

	public Object get() {
		if(decoded == null) {
			decoded = fromBinary(Check.notNull(encoded));
			assertValueCompliant(cfg, decoded);
		}
		return decoded == NULL ? null : decoded;
	}

	public void update(Object val) {
		if(val == null) {
			val = NULL;
		}
		assertValueCompliant(cfg, val);
		encoded = null;
		decoded = val;
	}

	public void load(byte[] b) {
		encoded = b;
		decoded = null;
	}

	public boolean isEmpty() {
		return encoded == null && decoded == null;
	}
}
