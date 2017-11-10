package com.nominanuda.hyperapi;

import android.support.annotation.Nullable;

import com.nominanuda.urispec.StringMapURISpec;
import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.obj.Obj;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http.HttpMethod;

import static com.nominanuda.hyperapi.EntityCodec.ENC;
import static com.nominanuda.zen.io.Uris.URIS;

/**
 * Created by azum on 20/03/17.
 */

public class HyperApiHttpInvocationHandler implements InvocationHandler {
	private final static CacheControl CACHE_CONTROL = new CacheControl.Builder().noCache().noStore().build();
	private final static RequestBody EMPTY_REQUEST_BODY = RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), "".getBytes());
	private final static Logger LOG = LoggerFactory.getLogger(HyperApiHttpInvocationHandler.class);
	@Nullable private final IHttpAppExceptionRenderer exceptionRenderer;
	private final OkHttpClient okHttpClient;
	private final String uriPrefix;

	HyperApiHttpInvocationHandler(OkHttpClient client, String uriPrefix, @Nullable IHttpAppExceptionRenderer exceptionRenderer) {
		this.okHttpClient = client;
		this.uriPrefix = uriPrefix;
		this.exceptionRenderer = exceptionRenderer;
	}


	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Request request = encode(method, args);
		LOG.debug("{} {}", request.method(), request.url());
		Response response = okHttpClient.newCall(request).execute();
		try {
			Object result = ENC.decode(response.body(), new AnnotatedType(method.getReturnType(), method.getAnnotations()));
			if (exceptionRenderer != null) { // can be null if we don't want to throw exceptions
				exceptionRenderer.parseAndThrow(response.code(), result);
			}
			return result;
		} finally {
			response.close();
		}
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
			entity = formBody;
			if ("GET".equals(httpMethod)) {
				// just to prevent a common mistake when writing hyperapis...
				httpMethod = "POST";
			}
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
				.method(httpMethod, entity == null && HttpMethod.requiresRequestBody(httpMethod) ? EMPTY_REQUEST_BODY : entity)
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
