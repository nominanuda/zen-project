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
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

public class HyperApiIntrospector {

	public @Nullable Path findPathAnno(Method method) {
		Annotation[] methodAnnotations = method.getAnnotations();
		for(Annotation a : methodAnnotations) {
			if(a instanceof Path) {
				return (Path)a;
			}
		}
		return null;
	}

	public @Nullable Annotation findFirstHttpMethod(Method method) {
		Annotation[] methodAnnotations = method.getAnnotations();
		for(Annotation a : methodAnnotations) {
			if(a instanceof GET
			|| a instanceof POST
			|| a instanceof PUT
			|| a instanceof DELETE) {
				return a;
			}
		}
		return null;
	}

	public @Nullable Set<Annotation> findAllHttpMethods(Method method) {
		Set<Annotation> res = new LinkedHashSet<Annotation>();
		Annotation[] methodAnnotations = method.getAnnotations();
		for(Annotation a : methodAnnotations) {
			if(a instanceof GET
			|| a instanceof POST
			|| a instanceof PUT
			|| a instanceof DELETE) {
				res.add(a);
			}
		}
		return res.isEmpty() ? null : res;
	}

}
