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
package com.nominanuda.dataobject;

import java.util.*;

public class Zen {

	public static final Zen Z = new Zen();

	private final DataStructHelper H = new DataStructHelper();

	public DataArray array(Object... attributes) {
		return H.fromMapsAndCollections(toList(attributes));
	}

	public DataObject object(Object... attributes) {
		return H.fromMapsAndCollections(toMap(attributes));
	}

	public DataObject object(Map map) {
		return H.fromMapsAndCollections(map);
	}

	public String[] array(DataObject dataObject) {
		List<String> keys = dataObject.getKeys();
		List<String> result = new LinkedList<String>();
		for (String key : keys) {
			result.add(key);
			result.add(String.valueOf(dataObject.get(key)));
		}
		return result.toArray(new String[0]);
	}

	private Collection toList(Object[] objects) {
		List result = new LinkedList();
		for (int i = 0; i < objects.length; i++) {
			result.add(objects[i]);
		}
		return result;
	}

	private Map toMap(Object[] objects) {
		Map result = new HashMap();
		for (int i = 0; i < objects.length; i += 2) {
			result.put(objects[i], objects[i + 1]);
		}
		return result;
	}
}
