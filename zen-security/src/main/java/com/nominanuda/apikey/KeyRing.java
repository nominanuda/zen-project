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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.nominanuda.zen.obj.Obj;

public class KeyRing {
	private Map<String, KeySet> keySetMap;


	public KeyRing(Map<String, KeySet> keySetDef) throws Exception {
		keySetMap = keySetDef;
		for (KeySet k : keySetMap.values()) {
			k.init();
		}
	}
	
	public KeyRing(Obj o) throws Exception {
		keySetMap = new HashMap<String, KeySet>();
		for (String key : o.keySet()) {
			KeySet k = new KeySet(o.getObj(key));
			keySetMap.put(key, k);
			k.init();
		}
	}
	
	@Nullable KeySet findKeySet(String keyname) {
		return keySetMap.get(keyname);
	}
}
