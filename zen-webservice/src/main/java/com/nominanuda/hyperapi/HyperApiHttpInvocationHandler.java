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
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.xml.sax.SAXException;

import com.nominanuda.code.Nullable;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataObjectImpl;
import com.nominanuda.dataobject.ParseException;
import com.nominanuda.lang.Check;
import com.nominanuda.urispec.URISpec;
import com.nominanuda.web.http.Http400Exception;
import com.nominanuda.web.http.Http500Exception;
import com.nominanuda.web.mvc.DataObjectURISpec;

public class HyperApiHttpInvocationHandler implements InvocationHandler {
	private EntityCodec entityCodec = EntityCodec.createBasic();
	private Class<?> hyperApi;
	private HttpClient client;
	private String uriPrefix;

	public HyperApiHttpInvocationHandler(Class<?> hyperApi, HttpClient client, String uriPrefix) {
		this.hyperApi = hyperApi;
		this.client = client;
		this.uriPrefix = uriPrefix;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		HttpUriRequest req = encode(uriPrefix, hyperApi, method, args);
		HttpResponse resp = client.execute(req);
		int status = resp.getStatusLine().getStatusCode();
		if(status >= 500) {
			throw new Http500Exception(resp.getStatusLine().getReasonPhrase() + " for " + req.getURI());
		} else if(status >= 400) {
			throw new Http400Exception(resp.getStatusLine().getReasonPhrase() + " for " + req.getURI());
		}
		Object result = decode(hyperApi, method, resp);
		return result;
	}
	@Nullable private Object decode(Class<?> hyperApi2, Method method, HttpResponse resp) throws UnsupportedEncodingException, IllegalStateException, IOException, ParseException, SAXException {
		if(resp.getEntity() == null || resp.getEntity().getContent() == null) {
			return null;
		}
		return entityCodec.decode(resp.getEntity(), new AnnotatedType(method.getReturnType(), method.getAnnotations()));
	}
	private HttpUriRequest encode(String uriPrefix, Class<?> hyperApi2, Method method, Object[] args) {
		String httpMethod = null;
		URISpec<DataObject> spec = null;
		DataObject uriParams = new DataObjectImpl();
		@SuppressWarnings("unused")
		String[] consumedMediaTypes = null;
		Annotation[] methodAnnotations = method.getAnnotations();
		HttpEntity entity = null;
		for(Annotation a : methodAnnotations) {
			if(a instanceof POST) {
				httpMethod = "POST";
			} else if(a instanceof GET) {
				httpMethod = "GET";
			} else if(a instanceof PUT) {
				httpMethod = "PUT";
			} else if(a instanceof DELETE) {
				httpMethod = "DELETE";
			} else if(a instanceof Path) {
				spec = new DataObjectURISpec(uriPrefix+((Path)a).value());
			} else if(a instanceof Consumes) {
				consumedMediaTypes = (((Consumes)a).value());
			}
		}
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		Class<?>[] parameterTypes = method.getParameterTypes();
		for(int i = 0; i < parameterTypes.length; i++) {
			Class<?> parameterType = parameterTypes[i];
			Annotation[] annotations = parameterAnnotations[i];
			Object arg = args[i];
			boolean annotationFound = false;
			for(Annotation annotation : annotations){
				if(annotation instanceof PathParam) {
					annotationFound = true;
					if(arg != null) {
						uriParams.put(((PathParam)annotation).value(), arg.toString());//TODO multivalue or complex transformations
					}
					break;
				} else if(annotation instanceof QueryParam) {
					annotationFound = true;
					if(arg != null) {
						uriParams.put(((QueryParam)annotation).value(), arg.toString());//TODO multivalue or complex transformations
					}
					break;
				}
			}
			if(! annotationFound) {
				Check.unsupportedoperation.assertNull(entity);
				entity = getEntity(arg, parameterType, annotations);
			}
		}
		String uri = spec.template(uriParams);
		HttpUriRequest result = createRequest(uri, httpMethod);
		if(result instanceof HttpEntityEnclosingRequest && entity != null) {
			((HttpEntityEnclosingRequest)result).setEntity(entity);
		}
		return result;
	}
	private HttpUriRequest createRequest(String uri, String httpMethod) {
		if("POST".equals(httpMethod)) {
			return new HttpPost(uri);
		} else if("GET".equals(httpMethod)) {
			return new HttpGet(uri);
		} else if("PUT".equals(httpMethod)) {
			return new HttpPut(uri);
		} else if("DELETE".equals(httpMethod)) {
			return new HttpDelete(uri);
		}
		throw new IllegalArgumentException("unknown http method "+httpMethod);
	}
	private @Nullable HttpEntity getEntity(Object arg, Class<?> parameterType, Annotation[] parameterAnnotations) throws IllegalArgumentException {
		AnnotatedType ap = new AnnotatedType(parameterType, parameterAnnotations);
		return entityCodec.encode(arg, ap);
	}

	public void setEntityCodec(EntityCodec entityCodec) {
		this.entityCodec = entityCodec;
	}
}
