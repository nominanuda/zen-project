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
package com.nominanuda.dataobject.transform;

import java.io.Reader;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.TransformerConfigurationException;

import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.dataobject.DataStructContentHandler;
import com.nominanuda.dataobject.DataStructStreamer;
import com.nominanuda.dataobject.JsonContentHandler;
import com.nominanuda.dataobject.JsonStreamer;
import com.nominanuda.dataobject.JsonStreamingParser;
import com.nominanuda.lang.Check;
import com.nominanuda.lang.Fun0;
import com.nominanuda.lang.ObjectFactory;

public class JsonPipeline {
	private List<Object> components = new LinkedList<Object>();
	private boolean completed = false;
	private boolean looseParser = false;
	
	public JsonPipeline add(ObjectFactory<? extends JsonTransformer> transformer) {
		Check.illegalstate.assertFalse(completed);
		components.add(transformer);
		return this;
	}

	public JsonPipeline setComponents(List<?> components) {
		Check.illegalstate.assertFalse(completed);
		this.components.clear();
		for(Object c : components) {
			this.components.add(c);
		}
		return this;
	}

	public JsonPipeline withLooseParser() {
		looseParser = true;
		return this;
	}
	

	public JsonPipeline complete() {
		Check.illegalstate.assertFalse(completed);
		completed = true;
		Collections.reverse(components);
		return this;
	}

	public Runnable build(final JsonStreamer starter, final JsonContentHandler ender) {
		if(! completed) {
			complete();
		}
		return new Runnable() {
			public void run() {
				try {
					starter.stream(buildPipe(ender));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

		};
	}

	public Fun0<DataStruct> build(final JsonStreamer starter) {
		final DataStructContentHandler dsch = new DataStructContentHandler();
		final Runnable r = build(starter, dsch);
		return runnableToFun(r, dsch);
	}


	public Runnable build(final Reader json, final JsonContentHandler ender) {
		return build(new JsonStreamingParser(looseParser, json), ender);
	}

	public Fun0<DataStruct> build(final Reader json) {
		final DataStructContentHandler dsch = new DataStructContentHandler();
		final Runnable r = build(json, dsch);
		return runnableToFun(r, dsch);
	}

	public Runnable build(final DataStruct struct, final JsonContentHandler ender) {
		return build(new DataStructStreamer(struct), ender);
	}

	public Fun0<DataStruct> build(final DataStruct ds) {
		final DataStructContentHandler dsch = new DataStructContentHandler();
		final Runnable r = build(ds, dsch);
		return runnableToFun(r, dsch);
	}

	private JsonTransformer buildJsonTransformer(Object c) throws TransformerConfigurationException {
		Check.illegalstate.assertTrue(completed);
		if(c instanceof ObjectFactory) {
			return (JsonTransformer)((ObjectFactory<?>)c).getObject();
		} else {
			return Check.illegalstate.fail();
		}
	}

	private JsonContentHandler buildPipe(final JsonContentHandler ender) throws TransformerConfigurationException {
		Check.illegalstate.assertTrue(completed);
		JsonContentHandler nextTarget = ender;
		Iterator<Object> itr = components.iterator();
		boolean first = true;
		while (itr.hasNext()) {
			Object c = itr.next();
			JsonTransformer th = buildJsonTransformer(c);
			if(first) {
				first = false;
			}
			th.setTarget(nextTarget);
			nextTarget = th;
		}
		return nextTarget;
	}

	private Fun0<DataStruct> runnableToFun(final Runnable r, final DataStructContentHandler dsch) {
		return new Fun0<DataStruct>() {
			public DataStruct apply() {
				r.run();
				return dsch.getResult();
			}
		};
	}
}
