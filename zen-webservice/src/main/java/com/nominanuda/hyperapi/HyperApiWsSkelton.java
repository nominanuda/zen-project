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

import static com.nominanuda.web.http.HttpCoreHelper.HTTP;
import static com.nominanuda.zen.obj.JsonPath.JPATH;
import static com.nominanuda.zen.obj.wrap.Wrap.WF;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHttpResponse;

import com.nominanuda.web.http.Http500Exception;
import com.nominanuda.web.http.HttpCoreHelper;
import com.nominanuda.web.mvc.ObjURISpec;
import com.nominanuda.web.mvc.WebService;
import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.common.Tuple2;
import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.wrap.ObjWrapper;

public class HyperApiWsSkelton implements WebService {
	private final EntityCodec entityCodec = EntityCodec.createBasic();
	private final HyperApiIntrospector apiIntrospector = new HyperApiIntrospector();
	
	private Class<?> api;
	private String requestUriPrefix = "";
	private String jsonDurationProperty;
	private Object service;

	
	public HttpResponse handle(HttpRequest request) throws Exception {
		long start = System.currentTimeMillis();
		try {
			Tuple2<Object, AnnotatedType> result = handleCall(request);
			Object handlerResult = result.get0();
			if (jsonDurationProperty != null && handlerResult instanceof Obj) {
				((Obj)handlerResult).put(jsonDurationProperty, System.currentTimeMillis() - start);
			}
			return response(handlerResult, result.get1());
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause != null && cause instanceof Exception) {
				throw (Exception) cause;
			} else {
				throw new Http500Exception(e);
			}
		}
	}

	protected Tuple2<Object, AnnotatedType> handleCall(HttpRequest request) throws Exception, IllegalArgumentException/*method not found*/ {
		String requestUri = request.getRequestLine().getUri();
		Check.illegalargument.assertTrue(requestUri.startsWith(requestUriPrefix));
		String apiRequestUri = requestUri.substring(requestUriPrefix.length());
		for (Method m : api.getMethods()) { // better than getDeclaredMethods(), as we use interfaces and they could extend one another
			Path pathAnno = apiIntrospector.findPathAnno(m);
			if (pathAnno != null) {
				ObjURISpec spec = new ObjURISpec(pathAnno.value());
				Obj uriParams = spec.match(apiRequestUri);
				if (uriParams != null) {
					if (supportsHttpMethod(m, request.getRequestLine().getMethod())) {
						Object[] args = createArgs(uriParams, new HttpCoreHelper().getEntity(request), api, m);
						Object result = invokeMethod(m, args);
						return new Tuple2<Object, AnnotatedType>(result, new AnnotatedType(m.getReturnType(), m.getAnnotations()));
					}
				}
			}
		}
		throw new IllegalArgumentException("could not find any suitable method to call " + "for api request: " + apiRequestUri);
	}

	protected Object invokeMethod(Method m, Object[] args) throws IllegalAccessException, InvocationTargetException {
		Object result = m.invoke(service, args);
		return result;
	}
	
	private boolean supportsHttpMethod(Method method, String httpMethod) {
		for (Annotation a : method.getAnnotations()) {
			if (a instanceof GET
				|| a instanceof POST
				|| a instanceof PUT
				|| a instanceof DELETE) {
				if (a.annotationType().getSimpleName().equals(httpMethod)) {
					return true;
				}
			}
		}
		return false;
	}

	private Object[] createArgs(Obj uriParams, HttpEntity entity, Class<?> api2, Method method) throws IOException {
		List<NameValuePair> formParams = Collections.emptyList();
		if (entity != null) {
			formParams = HTTP.parseEntityWithDefaultUtf8(entity);
		}
		Class<?>[] parameterTypes = method.getParameterTypes();
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		Object[] args = new Object[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			Class<?> parameterType = parameterTypes[i];
			Annotation[] annotations = parameterAnnotations[i];
			AnnotatedType p = new AnnotatedType(parameterType, annotations);
			boolean annotationFound = false;
			for (Annotation annotation : annotations){
				if (annotation instanceof HeaderParam) {
					// TODO
				} else if (annotation instanceof PathParam) {
					annotationFound = true;
					Object o = JPATH.getPathSafe(uriParams, ((PathParam) annotation).value());
					args[i] = decast(o, parameterType);
					break;
				} else if (annotation instanceof QueryParam) {
					annotationFound = true;
					Object o = JPATH.getPathSafe(uriParams, ((QueryParam) annotation).value());
					args[i] = decast(o, parameterType);
					break;
				} else if (annotation instanceof FormParam) {
					annotationFound = true;
					if (Obj.class.equals(parameterType)) {
						args[i] = HTTP.toStru(formParams).asObj();
					} else if (parameterType.isInterface() && ObjWrapper.class.isAssignableFrom(parameterType)) {
						args[i] = WF.wrap(HTTP.toStru(formParams).asObj(), parameterType);
					} else {
						Object o = getFormParams(formParams, ((FormParam) annotation).value());
						args[i] = decast(o, parameterType);
					}
					break;
				}
			}
			if(! annotationFound) {
				if(entity == null) {
					args[i] = null;
				}/* else if(parameterType.isInterface() && ObjWrapper.class.isAssignableFrom(parameterType)) {
					args[i] = WF.wrap((ObjWrapper)entityCodec.decode(entity, p), parameterType);
				}*/ else {
					args[i] = entityCodec.decode(entity, p);
				}
			}
		}
		return args;
	}
	
	private Object getFormParams(List<NameValuePair> formParams, String name) {
		List<Object> params = new ArrayList<>();
		for (NameValuePair pair : formParams) {
			if (name.equals(pair.getName())) {
				params.add(pair.getValue());
			}
		}
		switch (params.size()) {
		case 0:
			return null;
		case 1:
			return params.get(0);
		}
		return params;
	}

	private Object decast(Object val, Class<?> targetType) {
		if (val != null) {
			if (Collection.class.isAssignableFrom(targetType)) {
				if (val instanceof Collection) {
					return val;
				}
				if (val instanceof Arr) {
					return val;//
				}
				Collection<String> result = new ArrayList<String>();
				result.add((String) decast(val, String.class));
				return result;
			}
			if (Arr.class.equals(targetType)) {
				if (val instanceof Arr) {
					return val;
				}
//Andreas				if (val instanceof Collection) {
//					STRUCT.fromMapsAndCollections((Collection<?>) val);
//				}
				return Arr.make(decast(val, String.class));
			}
			String sval = (String) val;
			if (String.class.equals(targetType)) {
				return sval;
			} else if (Integer.class.equals(targetType) || int.class.equals(targetType) || "int".equals(targetType.getSimpleName())) {
				return Integer.parseInt(sval);
			} else if (Long.class.equals(targetType) || long.class.equals(targetType) || "long".equals(targetType.getSimpleName())) {
				return Long.parseLong(sval);
			} else if (Double.class.equals(targetType) || double.class.equals(targetType) || "double".equals(targetType.getSimpleName())) {
				return Double.parseDouble(sval);
			} else if (Boolean.class.equals(targetType) || boolean.class.equals(targetType) || "boolean".equals(targetType.getSimpleName())) {
				return Boolean.parseBoolean(sval);
			}
		}
		return null;
	}
	

	protected HttpResponse response(Object result, AnnotatedType ap) {
		if(result instanceof HttpResponse) {
			return (HttpResponse)result;
		} else {
			BasicHttpResponse resp = new BasicHttpResponse(HTTP.statusLine(200));
			HttpEntity entity = null;
			if(result instanceof HttpEntity) {
				entity = (HttpEntity)result;
			} else if(result != null) {
				entity = entityCodec.encode(result, ap);
			}
			if (entity != null) {
				resp.setEntity(entity);
			}
			return resp;
		}
	}

	/* proxy magic */
	
	private void evCreateProxy() {
		if (api != null && service != null) {
			if (!api.isInstance(service)) {
				final Object origService = service;
				service = Proxy.newProxyInstance(api.getClassLoader(), new java.lang.Class[] { api }, new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						try {
							Method m = origService.getClass().getMethod(method.getName(), method.getParameterTypes()); // "same" method in different objs hierarchy
							return m.invoke(origService, args);
						} catch (InvocationTargetException e) {
							Throwable cause = e.getCause();
							if (cause != null && cause instanceof Exception) {
								throw (Exception) cause;
							} else {
								throw new Http500Exception(e);
							}
						}
					}
				});
			}
		}
	}
	
	
	
	/* setters */

	public void setApi(Class<?> api) {
		this.api = api;
		evCreateProxy();
	}

	public void setService(Object service) {
		this.service = service;
		evCreateProxy();
	}

	public void setRequestUriPrefix(String requestUriPrefix) {
		this.requestUriPrefix = requestUriPrefix;
	}
	
	public void setJsonDurationProperty(String jsonDurationProperty) {
		this.jsonDurationProperty = jsonDurationProperty;
	}
}