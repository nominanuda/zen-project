package com.nominanuda.dataobject;

import java.io.Reader;
import java.io.StringReader;

import com.nominanuda.lang.Check;

public class JsonStreamingParser implements JsonStreamer {
	private final boolean loose;
	private final Reader json;
	private boolean consumed = false;

	public JsonStreamingParser(boolean loose, Reader json) {
		this.loose = loose;
		this.json = json;
	}

	public JsonStreamingParser(Reader json) {
		this(false, json);
	}

	public JsonStreamingParser(String json) {
		this(false, new StringReader(json));
	}

	@Override
	public synchronized void stream(JsonContentHandler jch) throws RuntimeException {
		Check.illegalstate.assertFalse(consumed);
		consumed = true;
		if(loose) {
			new JsonLooseParser().parse(json);
		} else {
			try {
				new JSONParser().parse(json, jch);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

}
