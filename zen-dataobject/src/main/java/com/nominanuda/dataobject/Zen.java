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
