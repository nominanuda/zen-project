package com.nominanuda.springmvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.nominanuda.web.mvc.URITransformerURLStreamer;
import com.nominanuda.zen.obj.Obj;

public class WebappURLStreamer extends URITransformerURLStreamer {
	private final Map<String, Object> hardParams;
	
	public WebappURLStreamer(Map<String, Object> hardParams) {
		this.hardParams = hardParams != null ? hardParams : new HashMap<>();
	}

	@Override
	protected String calcResourceUrl(Obj model) {
		for (Entry<String, Object> entry : hardParams.entrySet()) {
			model.put(entry.getKey(), entry.getValue());
		}
		return super.calcResourceUrl(model);
	}
}
