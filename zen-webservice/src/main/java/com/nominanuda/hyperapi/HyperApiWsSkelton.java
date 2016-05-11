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
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHttpResponse;

import com.nominanuda.code.Nullable;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.lang.Check;
import com.nominanuda.lang.Tuple2;
import com.nominanuda.lang.Tuple3;
import com.nominanuda.web.http.Http404Exception;
import com.nominanuda.web.http.Http500Exception;
import com.nominanuda.web.http.HttpCoreHelper;
import com.nominanuda.web.mvc.DataObjectURISpec;
import com.nominanuda.web.mvc.WebService;

import static com.nominanuda.web.http.HttpCoreHelper.HTTP;

public class HyperApiWsSkelton implements WebService {
	private EntityCodec entityCodec = EntityCodec.createBasic();
	private HyperApiIntrospector apiIntrospector = new HyperApiIntrospector();
	private Class<?> api;
	private String requestUriPrefix = "";
	private Object service;

	public HttpResponse handle(HttpRequest request) throws Exception {
		try {
			Tuple2<Object, AnnotatedType> result = handleCall(request);
			return response(result.get0(), result.get1());
		} catch(IllegalArgumentException e) {
			throw new Http404Exception(e);
		} catch(Exception e) {
			throw new Http500Exception(e);
		}
	}

	protected Tuple2<Object, AnnotatedType> handleCall(HttpRequest request) throws Exception, IllegalArgumentException/*method not found*/ {
		String requestUri = request.getRequestLine().getUri();
		Check.illegalargument.assertTrue(requestUri.startsWith(requestUriPrefix));
		String apiRequestUri = requestUri.substring(requestUriPrefix.length());
		HttpEntity entity = HTTP.getEntity(request);
		String reqHttpMethod = request.getRequestLine().getMethod();

		Tuple2<Method, DataObject> boundMethod = bindMethod(reqHttpMethod, apiRequestUri);
		if(boundMethod == null) {
			throw new IllegalArgumentException("could not find any suitable method to call for api request: " + apiRequestUri);
		} else {
			Method m = boundMethod.get0();
			DataObject uriParams = boundMethod.get1();
			return handleFoundMethodCall(entity, m, uriParams);
		}
	}

	private List<Tuple3<Method, DataObjectURISpec, Set<String>>> methodUriCache = null;
	protected @Nullable Tuple2<Method, DataObject> bindMethod(String reqHttpMethod, String apiRequestUri) {
		if(methodUriCache == null) {
			populateMethodUriCache();
		}
		for(Tuple3<Method, DataObjectURISpec, Set<String>> cachedMethod : methodUriCache) {
			Method m = cachedMethod.get0();
			DataObjectURISpec spec = cachedMethod.get1();
			Set<String> allowedHttpMethods = cachedMethod.get2();
			DataObject uriParams = spec.match(apiRequestUri);
			if(uriParams != null && allowedHttpMethods.contains(reqHttpMethod)) {
				return new Tuple2<Method, DataObject>(m, uriParams);
			}
		}
		return null;
	}

	private synchronized void populateMethodUriCache() {
		if(methodUriCache != null) {
			return;
		}
		List<Tuple3<Method, DataObjectURISpec, Set<String>>> muc = 
				new LinkedList<Tuple3<Method,DataObjectURISpec,Set<String>>>();
		for(Method m : api.getDeclaredMethods()) {
			Path pathAnno = apiIntrospector.findPathAnno(m);
			if(pathAnno != null) {
				Set<Annotation> httpMethodAnnots = apiIntrospector.findAllHttpMethods(m);
				Set<String> httpMethods = new LinkedHashSet<String>();
				if(httpMethodAnnots == null) {
					httpMethods.add("GET");
				} else {
					for(Annotation a : httpMethodAnnots) {
						httpMethods.add(a.annotationType().getSimpleName());
					}
				}
				DataObjectURISpec spec = new DataObjectURISpec(pathAnno.value());
				muc.add(new Tuple3<Method, DataObjectURISpec, Set<String>>(m, spec, httpMethods));
			}
		}
		methodUriCache = muc;
	}

	protected Tuple2<Object, AnnotatedType> handleFoundMethodCall(
			@Nullable HttpEntity entity, Method m, DataObject uriParams)
			throws IOException, IllegalAccessException,
			InvocationTargetException {
		AnnotatedType at = getAnnotatedReturnType(m);
		Object[] args = createArgs(uriParams, entity, api, m);
		Object result = invokeMethod(m, args);
		return new Tuple2<Object, AnnotatedType>(result, at);
	}

	private Object invokeMethod(Method m, Object[] args)
			throws IllegalAccessException, InvocationTargetException {
		return m.invoke(service, args);
	}

	protected AnnotatedType getAnnotatedReturnType(Method m) {
		AnnotatedType at = new AnnotatedType(m.getReturnType(), m.getAnnotations());
		return at;
	}

	protected Object decodeEntity(HttpEntity entity, AnnotatedType p) throws IOException {
		return entityCodec.decode(entity, p);
	}

	protected Object[] createArgs(DataObject uriParams, HttpEntity entity, Class<?> api2, Method method) throws IOException {
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		Class<?>[] parameterTypes = method.getParameterTypes();
		Object[] args = new Object[parameterTypes.length];
		for(int i = 0; i < parameterTypes.length; i++) {
			Class<?> parameterType = parameterTypes[i];
			Annotation[] annotations = parameterAnnotations[i];
			AnnotatedType p = new AnnotatedType(parameterType, annotations);
			boolean annotationFound = false;
			for(Annotation annotation : annotations){
				if(annotation instanceof PathParam) {
					annotationFound = true;
					String s = (String)uriParams.getPathSafe(((PathParam) annotation).value());
					args[i] = cast(s, parameterType);
					break;
				} else if(annotation instanceof QueryParam) {
					annotationFound = true;
					String s = (String)uriParams.getPathSafe(((QueryParam) annotation).value());
					args[i] = cast(s, parameterType);
					break;
				}
			}
			if(! annotationFound) {
				Object dataEntity = entity == null ? null : decodeEntity(entity, p);
				args[i] = dataEntity;
			}
		}
		return args;
	}

	private Object cast(String sval, Class<?> targetType) {
		if(sval == null) {
			return null;
		} else if(String.class.equals(targetType)) {
			return sval;
		} else if(Integer.class.equals(targetType) || int.class.equals(targetType) || "int".equals(targetType.getSimpleName())) {
			return Integer.parseInt(sval);
		} else if(Long.class.equals(targetType) || long.class.equals(targetType) || "long".equals(targetType.getSimpleName())) {
			return Long.parseLong(sval);
		} else if(Double.class.equals(targetType) || double.class.equals(targetType) || "double".equals(targetType.getSimpleName())) {
			return Double.parseDouble(sval);
		} else if(Boolean.class.equals(targetType) || boolean.class.equals(targetType) || "boolean".equals(targetType.getSimpleName())) {
			return Boolean.parseBoolean(sval);
		}
		return null;
	}

	protected HttpResponse response(Object result, AnnotatedType ap) {
		HttpCoreHelper d = new HttpCoreHelper();
		BasicHttpResponse resp = new BasicHttpResponse(d.statusLine(200));
		HttpEntity entity = entityCodec.encode(result, ap);
		if(entity != null) {
			resp.setEntity(entity);
		}
		return resp;
	}

	public void setApi(Class<?> api) {
		this.api = api;
	}

	public void setRequestUriPrefix(String requestUriPrefix) {
		this.requestUriPrefix = requestUriPrefix;
	}

	public void setService(Object service) {
		this.service = service;
	}
}
