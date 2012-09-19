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
package com.nominanuda.hyperapi;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.http.HttpEntity;

import com.nominanuda.lang.Collections;

public class EntityCodec {
	private List<EntityEncoder> encoders = Collections.linkedList(
			(EntityEncoder)new InputStreamEntityEncoder(), 
			(EntityEncoder)new ByteArrayEntityEncoder(),
			(EntityEncoder)new JsonAnyValueEntityEncoder()
		);

	private List<EntityDecoder> decoders = Collections.linkedList(
			//(EntityDecoder)new DataStructJsonDecoder(),
			(EntityDecoder)new JsonAnyValueDecoder(),
			(EntityDecoder)new ByteArrayEntityDecoder(),
			(EntityDecoder)new InputStreamEntityDecoder(),
			(EntityDecoder)new JsonAnyValueDecoder(AbstractEntityDecoder.ANY_CONTENT_TYPE)//this is for buggy server compat 
			);

	public static EntityCodec createBasic() {
		return new EntityCodec();
	}
	public void setEntityEncoders(Collection<EntityEncoder> encoders) {
		this.encoders.clear();
		this.encoders.addAll(encoders);
	}
	public HttpEntity encode(Object arg, AnnotatedType ap) {
		for(EntityEncoder c : encoders) {
			if(c.supports(ap, arg)) {
				return c.encode(ap, arg);
			}
		}
		throw new IllegalArgumentException("no suitable converter found for encoding payload");
	}
	public void setEntityDecoders(List<? extends EntityDecoder> l) {
		decoders.clear();
		decoders.addAll(l);
	}
	public Object decode(HttpEntity entity, AnnotatedType p) throws IOException {
		for(EntityDecoder d : decoders) {
			if(d.supports(p, entity)) {
				return d.decode(p, entity);
			}
		}
		throw new IllegalArgumentException("cannot find a suitable entity decoder");
	}
}
