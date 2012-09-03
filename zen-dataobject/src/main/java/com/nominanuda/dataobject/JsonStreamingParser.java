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
 * 
 */
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
			new JsonLooseParser().parse(json, jch);
		} else {
			try {
				new JSONParser().parse(json, jch);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

}
