package com.nominanuda.zen.hyperapi.async;

import com.nominanuda.zen.lang.Check;
import com.nominanuda.zen.obj.Obj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by azum on 20/03/17.
 */

public class AsyncHyperApiHttpInvocationHandler implements InvocationHandler {
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return null;
	}


	private Request encode(String uriPrefix, Method method, Object[] args) {
		RequestBody entity = null;
		JSONObject uriParams = new JSONObject();
		Headers.Builder headersBuilder = new Headers.Builder();
		FormBody.Builder formBodyBuilder = new FormBody.Builder();

		Class<?>[] parameterTypes = method.getParameterTypes();
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		for (int i = 0; i < parameterTypes.length; i++) {
			Class<?> parameterType = parameterTypes[i];
			Annotation[] annotations = parameterAnnotations[i];
			Object arg = args[i];

			boolean annotationFound = false;
			for (Annotation annotation : annotations) {
				try {
					if (annotation instanceof HeaderParam) {
						annotationFound = true;
						if (arg != null) {
							headersBuilder.add(((HeaderParam) annotation).value(), arg.toString());
						}
						break;

					} else if (annotation instanceof PathParam) {
						annotationFound = true;
						if (arg != null) {
							uriParams.put(((PathParam) annotation).value(), arg.toString());
						}
						break;

					} else if (annotation instanceof QueryParam) {
						annotationFound = true;
						if (arg != null) {
							uriParams.put(((QueryParam) annotation).value(), arg instanceof Collection
								? new JSONArray((Collection<?>) arg)
								: arg.toString()
							);
						}
						break;

					} else if (annotation instanceof FormParam) {
						annotationFound = true;
						if (arg != null) {
							String name = ((FormParam) annotation).value();
							if (arg instanceof Obj) {
								Check.unsupportedoperation.fail("TODO arg instanceof Obj");
//								Map<String, Object> map = new HashMap<String, Object>();
//								STRUCT.toFlatMap(STRUCT.buildObject(name, arg), map);
//								for (Map.Entry<String, Object> entry : map.entrySet()) {
//									if (entry.getValue() != null) {
//										formBodyBuilder.add(entry.getKey(), entry.getValue().toString());
//									}
//								}
							} else if (arg instanceof Collection) {
								for (Object v : (Collection<?>) arg) {
									if (v != null) {
										formBodyBuilder.add(name, v.toString());
									}
								}
							} else {
								formBodyBuilder.add(name, arg.toString());
							}
						}
						break;
					}
				} catch (JSONException e) {

				}
			}
			if (!annotationFound) {
				Check.unsupportedoperation.assertNull(entity);
				Check.unsupportedoperation.fail("TODO entity");
//				entity = entityCodec.encode(arg, new AnnotatedType(parameterType, annotations));
			}
		}

		String httpMethod = null;
		URISpec<DataObject> spec = null;
		for (Annotation a : method.getAnnotations()) {
			if (a instanceof POST) {
				httpMethod = "POST";
			} else if (a instanceof GET) {
				httpMethod = "GET";
			} else if (a instanceof PUT) {
				httpMethod = "PUT";
			} else if (a instanceof DELETE) {
				httpMethod = "DELETE";
			} else if (a instanceof Path) {
				spec = new DataObjectURISpec(uriPrefix + ((Path) a).value());
//			} else if (a instanceof Consumes) {
//				consumedMediaTypes = ((Consumes) a).value();
			}
		}

		if (!formParams.isEmpty()) {
			try {
				httpMethod = "POST";
				entity = new UrlEncodedFormEntity(formParams, HttpProtocol.UTF_8);
			} catch (UnsupportedEncodingException e) {
				throw new Http500Exception("Unsupported parameter encoding");
			}
		}

		HttpRequestBase request = buildRequest(spec.template(uriParams), httpMethod, requestHeaders, method.getReturnType());
		if (entity != null && request instanceof HttpEntityEnclosingRequest) {
			((HttpEntityEnclosingRequest) request).setEntity(entity);
		}
		return request;
	}


	private HttpRequestBase buildRequest(String uri, String httpMethod, List<Header> headers, Class<?> returnType) {
		HttpRequestBase request = createRequest(uri, httpMethod);
		request.setConfig(requestConfig);
		request.setHeaders(headers.toArray(new Header[headers.size()]));
		if (DataStruct.class.isAssignableFrom(returnType)) {
			request.setHeader("Accept", HttpProtocol.CT_APPLICATION_JSON);
		} else {
			request.setHeader("Accept", "*/*");
		}
		request.setHeader("Cache-Control", "no-cache");
		request.setHeader("Connection", "close"); // TODO remove?
		request.setHeader("Pragma", "no-cache");
		request.setHeader("User-Agent", userAgent);
		return request;
	}

	private HttpRequestBase createRequest(String uri, String httpMethod) {
		switch (httpMethod) {
		case "GET":
			return new HttpGet(uri);
		case "POST":
			return new HttpPost(uri);
		case "PUT":
			return new HttpPut(uri);
		case "DELETE":
			return new HttpDelete(uri);
		}
		throw new IllegalArgumentException("unknown http method " + httpMethod);
	}
}
