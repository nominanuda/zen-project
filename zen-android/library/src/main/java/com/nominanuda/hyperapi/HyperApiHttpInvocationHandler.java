package com.nominanuda.hyperapi;

import com.nominanuda.urispec.StringMapURISpec;
import com.nominanuda.web.http.Http400Exception;
import com.nominanuda.web.http.Http401Exception;
import com.nominanuda.web.http.Http403Exception;
import com.nominanuda.web.http.Http404Exception;
import com.nominanuda.web.http.Http4xxException;
import com.nominanuda.web.http.Http5xxException;
import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.obj.Obj;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.nominanuda.hyperapi.EntityCodec.ENC;
import static com.nominanuda.zen.io.Uris.URIS;

/**
 * Created by azum on 20/03/17.
 */

public class HyperApiHttpInvocationHandler implements InvocationHandler {
	private final static CacheControl CACHE_CONTROL = new CacheControl.Builder().noCache().noStore().build();
	protected final OkHttpClient client;
	protected final String uriPrefix;

	protected HyperApiHttpInvocationHandler(OkHttpClient client, String uriPrefix) {
		this.client = client;
		this.uriPrefix = uriPrefix;
	}


	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Request request = encode(method, args);
		Response response = client.newCall(request).execute();
		int status = response.code();
		if (status >= 400) {
			String message = response.message();
			if (status < 500) {
				switch (status) {
				case 400:
					throw new Http400Exception(message);
				case 401:
					throw new Http401Exception(message);
				case 403:
					throw new Http403Exception(message);
				case 404:
					throw new Http404Exception(message);
				default:
					throw new Http4xxException(message, status);
				}
			}
			throw new Http5xxException(message, status);
		}
		return ENC.decode(response.body(), new AnnotatedType(method.getReturnType(), method.getAnnotations()));
	}


	protected Request encode(Method method, Object[] args) {
		RequestBody entity = null;
		Map<String, Object> uriParams = new HashMap<>();
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
							? toStringsList((Collection<?>) arg)
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
			}
			if (!annotationFound) {
				Check.unsupportedoperation.assertNull(entity);
				entity = ENC.encode(arg, new AnnotatedType(parameterType, annotations));
			}
		}

		String httpMethod = null;
		StringMapURISpec spec = null;
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
				spec = new StringMapURISpec(URIS.pathJoin(uriPrefix, ((Path) a).value()));
			}
		}

		FormBody formBody = formBodyBuilder.build();
		if (formBody.size() > 0) {
			httpMethod = "POST";
			entity = formBody;
		}

		return new Request.Builder()
			.cacheControl(CACHE_CONTROL)
			.url(spec.template(uriParams))
			.headers(headersBuilder
				.add("Accept", Struct.class.isAssignableFrom(method.getReturnType())
					? "application/json"
					: "*/*")
				.add("User-Agent", "zen-android")
				.add("Cache-Control", "no-cache")
				.add("Connection", "close") // TODO remove?
				.add("Pragma", "no-cache").build())
			.method(httpMethod, entity)
			.build();
	}


	private List<String> toStringsList(Collection<?> c) {
		List<String> l = new ArrayList<>();
		for (Object o : c) {
			l.add(o != null ? o.toString() : null);
		}
		return l;
	}
}
