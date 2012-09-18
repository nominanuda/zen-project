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

import java.lang.annotation.Annotation;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import com.nominanuda.code.Nullable;
import com.nominanuda.lang.Check;

public class AnnotatedType {
	private final Class<?> type;
	private final Annotation[] annotations;

	public AnnotatedType(Class<?> type, Annotation[] annotations) {
		this.type = type;
		this.annotations = Check.ifNull(annotations, new Annotation[0]);
	}

	public boolean isAssignableFrom(Class<?> cl) {
		return cl.isAssignableFrom(type);
	}

	public @Nullable String mediaType() {
		for(Annotation a : annotations) {
			Class<? extends Annotation> t = a.annotationType();
			if(Consumes.class.equals(t)) {
				String[] vals = ((Consumes)a).value();
				return vals == null ? null : vals[0];
			} else if(Produces.class.equals(t)) {
				String[] vals = ((Produces)a).value();
				return vals == null ? null : vals[0];
			}
		}
		return null;
	}
}