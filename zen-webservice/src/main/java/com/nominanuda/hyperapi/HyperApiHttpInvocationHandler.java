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
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

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
import org.apache.http.entity.ByteArrayEntity;
import org.xml.sax.SAXException;

import com.nominanuda.code.Nullable;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataObjectImpl;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.dataobject.DataStructHelper;
import com.nominanuda.dataobject.jsonparser.JSONParser;
import com.nominanuda.dataobject.jsonparser.ParseException;
import com.nominanuda.io.IOHelper;
import com.nominanuda.lang.Check;
import com.nominanuda.lang.Maths;
import com.nominanuda.urispec.URISpec;
import com.nominanuda.web.http.Http400Exception;
import com.nominanuda.web.http.Http500Exception;
import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.web.mvc.DataObjectURISpec;

public class HyperApiHttpInvocationHandler implements InvocationHandler {
	private List<PayloadDecoder> decoders = new LinkedList<PayloadDecoder>();
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
			throw new Http500Exception(resp.getStatusLine().getReasonPhrase());
		} else if(status >= 400) {
			throw new Http400Exception(resp.getStatusLine().getReasonPhrase());
		}
		Object result = decode(hyperApi, method, resp);
		return result;
	}
	@Nullable private Object decode(Class<?> hyperApi2, Method method, HttpResponse resp) throws UnsupportedEncodingException, IllegalStateException, IOException, ParseException, SAXException {
		if(resp.getEntity() == null || resp.getEntity().getContent() == null) {
			return null;
		}
		IOHelper io = new IOHelper();
		String s = io.readAndCloseUtf8(resp.getEntity().getContent());
		if("null".equals(s)) {
			return null;
		} else if("true".equals(s)||"false".equals(s)) {
			return Boolean.valueOf(s);
		} else if(Maths.isNumber(s)) {
			if(Maths.isInteger(s)) {
				return Long.valueOf(s);
			} else {
				return Double.valueOf(s);
			}
		} else {//TODO
			DataStruct<?> ds = new JSONParser().parse(new StringReader(s));
			Class<?> returnType = method.getReturnType();
			return ds;
		}
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
			@SuppressWarnings("unused")
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
				entity = getEntity(arg);
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
	private HttpEntity getEntity(Object arg) {
		DataStructHelper dataStructHelper = new DataStructHelper();
		try {
			byte[] payload = dataStructHelper.toJsonString(arg).getBytes("UTF-8");
			ByteArrayEntity e = new ByteArrayEntity(payload);
			e.setContentType(HttpProtocol.CT_APPLICATION_JSON_CS_UTF8);
			return e;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException();
		}
	}

	public void setPayloadDecoders(List<? extends PayloadDecoder> l) {
		decoders.clear();
		decoders.addAll(l);
	}
}
