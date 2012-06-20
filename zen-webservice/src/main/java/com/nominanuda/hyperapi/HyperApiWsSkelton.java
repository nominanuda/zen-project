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

import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.dataobject.DataStructHelper;
import com.nominanuda.dataobject.jsonparser.JSONParser;
import com.nominanuda.lang.Check;
import com.nominanuda.web.http.Http404Exception;
import com.nominanuda.web.http.Http500Exception;
import com.nominanuda.web.http.HttpCoreHelper;
import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.web.mvc.DataObjectURISpec;
import com.nominanuda.web.mvc.WebService;

public class HyperApiWsSkelton implements WebService {
	private HyperApiIntrospector apiIntrospector = new HyperApiIntrospector();
	private Class<?> api;
	private String requestUriPrefix = "";
	private Object service;

	public HttpResponse handle(HttpRequest request) throws Exception {
		try {
			Object result = handleCall(request);
			return response(result);
		} catch(IllegalArgumentException e) {
			throw new Http404Exception(e);
		} catch(Exception e) {
			throw new Http500Exception(e);
		}
	}

	protected Object handleCall(HttpRequest request) throws Exception, IllegalArgumentException/*method not found*/ {
		String requestUri = request.getRequestLine().getUri();
		Check.illegalargument.assertTrue(requestUri.startsWith(requestUriPrefix));
		String apiRequestUri = requestUri.substring(requestUriPrefix.length());
		for(Method m : api.getDeclaredMethods()) {
			Path pathAnno = apiIntrospector.findPathAnno(m);
			if(pathAnno != null) {
				DataObjectURISpec spec = new DataObjectURISpec(pathAnno.value());
				DataObject uriParams = spec.match(apiRequestUri);
				if(uriParams != null) {
					Annotation httpMethod = apiIntrospector.findHttpMethod(m);
					if(httpMethod != null
					&& httpMethod.annotationType().getSimpleName()
							.equals(request.getRequestLine().getMethod())) {
						DataStruct<?> dataEntity = null;
						if(request instanceof HttpEntityEnclosingRequest) {
							HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
							dataEntity = new JSONParser().parse(new InputStreamReader(
									entity.getContent()));
						}
						Object[] args = createArgs(uriParams, dataEntity, api, m);
						Object result = m.invoke(service, args);
						return result;
					}
				}
			}
		}
		throw new IllegalArgumentException("could not find any suitable method to call " +
			"for api request: "+apiRequestUri);
	}

	private Object[] createArgs(DataObject uriParams, DataStruct<?> dataEntity,
			Class<?> api2, Method method) {
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		Class<?>[] parameterTypes = method.getParameterTypes();
		Object[] args = new Object[parameterTypes.length];
		for(int i = 0; i < parameterTypes.length; i++) {
			Class<?> parameterType = parameterTypes[i];
			Annotation[] annotations = parameterAnnotations[i];
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
				args[i] = Check.notNull(dataEntity);
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

	protected HttpResponse response(Object result) {
		HttpCoreHelper httpCoreHelper = new HttpCoreHelper();
		DataStructHelper dataStructHelper = new DataStructHelper();
		return httpCoreHelper.createBasicResponse(200, dataStructHelper.toJsonString(result), 
								HttpProtocol.CT_APPLICATION_JSON_CS_UTF8);
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
