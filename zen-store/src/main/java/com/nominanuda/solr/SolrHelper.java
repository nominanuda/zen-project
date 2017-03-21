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
 */package com.nominanuda.solr;

 //TODO
public class SolrHelper {
	public static final SolrHelper SOLR = new SolrHelper();

//	public Arr listResultsDotAware(SolrServer solr, SolrQuery sq, int start, int count) throws SolrServerException {
//		Arr res = new DataArrayImpl();
//		sq.setStart(start);
//		sq.setRows(count);
//		QueryResponse qr = solr.query(sq);
//		qr.getResults();
//		SolrDocumentList sdl = qr.getResults();
//		for(Map<String,Object> d : sdl) {
//			Obj o = solrDoc2DataObject(d);
//			res.add(o);
//		}
//		return res;
//	}

//	public Obj solrDoc2DataObject(Map<String, Object> d) {
//		return (Obj)struct.fromFlatMap(normalizeDynFields(d));
//	}
//
//	public Obj sid2DataObject(SolrInputDocument sid) {
//		Map<String, Object> m = new LinkedHashMap<String, Object>();
//		Iterator<SolrInputField> itr = sid.iterator();
//		while(itr.hasNext()) {
//			SolrInputField f = itr.next();
//			m.put(f.getName(), f.getValue());
//		}
//		return solrDoc2DataObject(m);
//	}
//
//	private Map<String, Object> normalizeDynFields(Map<String, Object> d) {
//		Map<String, Object> res = new LinkedHashMap<String, Object>();
//		for(Entry<String, Object> e : d.entrySet()) {
//			String name = e.getKey();
//			if(isDynamicFieldName(name)) {
//				name = fromDynamicFieldName(name);
//			}
//			res.put(name, e.getValue());
//		}
//		return res;
//	}
//
//	private boolean isDynamicFieldName(String s) {
//		return s.endsWith("_l")
//			|| s.endsWith("_d")
//			|| s.endsWith("_s")
//			|| s.endsWith("_b")
//			;
//	}
//	
//	private String makeDynamicFieldName(String name, Object val) {
//		switch (struct.getDataType(val)) {
//		case bool:
//			return name + "_b";
//		case number:
//			return val instanceof Long ? name + "_l" : name + "_d";
//		case string:
//			return name + "_s";
//		default:
//			return Check.illegalargument.fail();
//		}
//	}
//	private String fromDynamicFieldName(String dName) {
//		return dName.substring(0, dName.length() - 2);
//	}
//
//	public String escAndQuote(String s) {
//		return '"' + esc(s) + '"';
//	}
//
//	public String esc(String s) {
//		return ClientUtils.escapeQueryChars(s);
//	}
//
//	//TODO more than one level + nested fields naming convention
//	public SolrInputDocument sid(Obj obj, Set<String> solrFields) {
//		SolrInputDocument sid = new SolrInputDocument();
//		for(String k : obj.getKeys()) {
//			Object val = obj.get(k);
//			if(val == null) {
//				continue;
//			} else if(struct.isPrimitiveOrNull(val)) {
//				addField(sid, k, val, solrFields);
//			} else if(struct.isDataObject(val)) {
//				Obj o = (Obj) val;
//				for(String k1 : o.getKeys()) {
//					Object val1 = o.get(k1);
//					if(val1 == null) {
//						continue;
//					} else if(struct.isPrimitiveOrNull(val1)) {
//						if(solrFields.contains(k+"."+k1)) {
//							addField(sid, k+"."+k1, val1, solrFields);
//						} else {
//							addField(sid, k+"."+makeDynamicFieldName(k1, val1), val1, solrFields);
//						}
//					}
//				}
//			} else if(struct.isDataArray(val)) {
//				Arr a = (Arr) val;
//				Collection<Object> vals = new LinkedList<Object>();
//				for(Object val1 : a) {
//					if(val1 == null) {
//						continue;
//					} else if(struct.isPrimitiveOrNull(val1)) {
//						vals.add(val1);
//					}
//				}
//				setField(sid, k, vals, solrFields);
//			} 
//		}
//		return sid;
//	}
//	private void addField(SolrInputDocument sid, String key, Object val, Set<String> solrFields) {
//		if(solrFields.contains(key) || isDynamicFieldName(key)) {
//			sid.addField(key, val);
//		}
//	}
//	private void setField(SolrInputDocument sid, String key, Object val, Set<String> solrFields) {
//		if(solrFields.contains(key)) {
//			sid.setField(key, val);
//		}
//	}

}
