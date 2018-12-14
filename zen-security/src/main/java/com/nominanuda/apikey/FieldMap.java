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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class FieldMap {
	private final List<List<FieldCoordinates>> segmentConfig = new LinkedList<List<FieldCoordinates>>();
	private final Map<String, FieldCoordinates> cooByName = new HashMap<String, FieldCoordinates>();
	private final LinkedHashMap<String, FieldConfig> configsByName = new LinkedHashMap<String, FieldConfig>();
	private final List<KeySet> authorityBySegment = new LinkedList<KeySet>();
	private final KeyRing keyRing;

	public FieldMap(List<FieldConfig> list, KeyRing keyRing) {
		for(FieldConfig fc : list) {
			this.configsByName.put(fc.name, fc);
		}
		this.keyRing = keyRing;
		calcIndexes(configsByName);
	}

	public Field[] createBlankApiFields() {
		List<Field> l = new LinkedList<Field>();
		for(FieldConfig c : configsByName.values()) {
			l.add(new Field(c));
		}
		return l.toArray(new Field[l.size()]);
	}

	public Segment[] apiOrderedFieldsToSegments(Field[] apiOrderedFields, KeyRing keyRing) {
		List<Segment> l = new LinkedList<Segment>();
		Field[][] allSegFields = bySegment(apiOrderedFields);
		for(int i = 0; i < allSegFields.length; i++) {
			Field[] sFields = allSegFields[i];
			Segment s = new Segment(sFields, authorityBySegment.get(i));
			l.add(s);
		}
		return l.toArray(new Segment[allSegFields.length]);
	}

	public Segment[] apiOrderedFieldsToSegments(Field[] apiOrderedFields, KeyRing keyRing, List<byte[]> physicalSegments) {
		List<Segment> l = new LinkedList<Segment>();
		Field[][] allSegFields = bySegment(apiOrderedFields);
		for(int i = 0; i < allSegFields.length; i++) {
			Field[] sFields = allSegFields[i];
			Segment s = new Segment(sFields, authorityBySegment.get(i));
			s.load(physicalSegments.get(i));
			l.add(s);
		}
		return l.toArray(new Segment[allSegFields.length]);
	}

	private Field[][] bySegment(Field[] apiOrderedFields) {
		int nSeg = segmentConfig.size();
		Field[][] res = new Field[nSeg][];
		for(int i = 0; i < nSeg; i++) {
			List<FieldCoordinates> l = segmentConfig.get(i);
			int nFld = l.size();
			res[i] = new Field[nFld];
			for(int j = 0; j < nFld; j++) {
				FieldCoordinates c = l.get(j);
				res[i][j] = apiOrderedFields[c.getApiOrder()];
			}
		}
		return res;
	}

	public FieldCoordinates getCoordinates(String name) {
		FieldCoordinates coo = cooByName.get(name);
		return coo;
	}



	private void calcIndexes(LinkedHashMap<String, FieldConfig> fields) {
		Map<String, Integer>fieldOrder = new HashMap<String, Integer>();
		TreeMap<String, List<Entry<String, FieldConfig>>> byCryptoKeyPair = new TreeMap<String, List<Entry<String,FieldConfig>>>();
		int ord = 0;
		for(Entry<String, FieldConfig> f : fields.entrySet()) {
			String k = f.getValue().getSortingKey();
			List<Entry<String,FieldConfig>> lfc = byCryptoKeyPair.get(k);
			if(lfc == null) {
				lfc = new LinkedList<Entry<String,FieldConfig>>();
			}
			byCryptoKeyPair.put(k, lfc);
			lfc.add(f);
			fieldOrder.put(f.getKey(), ord++);
		}
		int sIdx = 0;
		for(List<Entry<String,FieldConfig>> lfc : byCryptoKeyPair.values()) {
			List<FieldCoordinates> lfcoo = new LinkedList<FieldCoordinates>();
			segmentConfig.add(lfcoo);
			int fIdx = 0;
			FieldConfig fc = lfc.get(0).getValue();
			authorityBySegment.add(keyRing.findKeySet(fc.authority));
			for(Entry<String,FieldConfig> e : lfc) {
				String fName = e.getKey();
				FieldCoordinates coo = new FieldCoordinates(sIdx, fIdx, fieldOrder.get(fName));
				lfcoo.add(coo);
				cooByName.put(fName, coo);
				fIdx++;
			}
			sIdx++;
		}
	}
}
