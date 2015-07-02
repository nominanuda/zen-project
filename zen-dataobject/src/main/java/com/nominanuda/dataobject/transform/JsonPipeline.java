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
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

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
import com.nominanuda.lang.Tuple2;

public class JsonPipeline {
	private final List<Object> components = new LinkedList<Object>();
	private boolean looseParser = false;

	class WrappingTransformer extends BaseJsonTransformer {
		JsonTransformer last;

		public WrappingTransformer(Tuple2<JsonTransformer, JsonTransformer> pair) {
			super.setTarget(pair.get0());
			this.last = pair.get1();
		}

		@Override
		public void setTarget(JsonContentHandler target) {
			last.setTarget(target);
		}
	}
	
	
	public JsonPipeline add(ObjectFactory<? extends JsonTransformer> transformer) {
		components.add(transformer);
		return this;
	}

	public JsonPipeline setComponents(List<?> components) {
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
	

	@Deprecated
	public JsonPipeline complete() {
		return this;
	}


	/**
	 * Build a raw pipeline without a terminating transformer. It is the caller's responsibility to set the final transformer (@see JsonTransformer.setTarget()) and to apply the pipeline to a stream.
	 * @return
	 */
	public JsonTransformer build() {
		try {
			return new WrappingTransformer(buildPipe());
		}
		catch(Exception e) {
			throw new RuntimeException("Failed to build pipeline: " + e.getMessage(), e);
		}
	}

	public Runnable build(final JsonStreamer starter, final JsonContentHandler ender) {
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
		if(c instanceof ObjectFactory) {
			return (JsonTransformer)((ObjectFactory<?>)c).getObject();
		} else {
			return Check.illegalstate.fail();
		}
	}

	private JsonContentHandler buildPipe(final JsonContentHandler ender) throws TransformerConfigurationException {
		if(ender == null)
			throw new TransformerConfigurationException("Terminating transformer not supplied.");
		JsonContentHandler nextTarget = ender;
		ListIterator<Object> litr = components.listIterator(components.size());
		while(litr.hasPrevious()) {
			Object c = litr.previous();
			JsonTransformer th = buildJsonTransformer(c);
			th.setTarget(nextTarget);
			nextTarget = th;
		}
		return nextTarget;
	}

	private Tuple2<JsonTransformer, JsonTransformer> buildPipe() throws TransformerConfigurationException {
		JsonTransformer lastTarget = null;
		JsonTransformer nextTarget = null;
		int n = components.size();
		if(n == 0) {
			// let's guarantee at least one transformer, so that we always return a pipeline
			JsonTransformer tr = new BaseJsonTransformer();
			tr.setTarget(null);
			nextTarget = tr;
			lastTarget = tr;
		}
		else {
			// Iterate backwards and finish with first target in pipeline
			ListIterator<Object> litr = components.listIterator(n);
			while(litr.hasPrevious()) {
				Object c = litr.previous();
				JsonTransformer th = buildJsonTransformer(c);
				th.setTarget(nextTarget);
				nextTarget = th;
				if(lastTarget == null)
					lastTarget = th;
			}
		}
		return new Tuple2<JsonTransformer, JsonTransformer>(nextTarget, lastTarget);
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
