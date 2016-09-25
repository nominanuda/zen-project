package com.nominanuda.springmvc;

import static com.nominanuda.dataobject.DataStructHelper.STRUCT;

import java.util.HashMap;
import java.util.Map;

import com.nominanuda.dataobject.DataObject;
import com.nominanuda.urispec.Utils;
import com.nominanuda.web.mvc.DataObjectURISpec;

public class Sitemap {
	public static final String BEAN_ID = "zenWebserviceSitemap";
	public static final String BEAN_PROP_ENTRIES = "entries";
	
	private Map<String, DataObjectURISpec> specs = new HashMap<String, DataObjectURISpec>();
	
	
	public String getUrl(String id, DataObject o) {
		DataObjectURISpec s = specs.get(id);
		if (null != s) {
			return s.template(o != null ? o : STRUCT.newObject());
		}
		return null;
	}
	
	
	/* setter */
	
	public void setEntries(Map<String, String> entries) {
		for (String id : entries.keySet()) {
			specs.put(id, new DataObjectURISpec(Utils.uriSpec(entries.get(id))));
		}
	}
}
