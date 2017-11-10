package com.nominanuda.hyperapi;

import com.nominanuda.web.http.Http400Exception;
import com.nominanuda.web.http.Http401Exception;
import com.nominanuda.web.http.Http403Exception;
import com.nominanuda.web.http.Http404Exception;
import com.nominanuda.web.http.Http4xxException;
import com.nominanuda.web.http.Http5xxException;
import com.nominanuda.web.http.HttpAppException;

import java.lang.reflect.Proxy;

import okhttp3.OkHttpClient;

import static com.nominanuda.zen.io.Uris.URIS;

/**
 * Created by azum on 27/03/17.
 */

public class HttpHyperApiFactory implements HyperApiFactory {
	private boolean allowExceptions = true;
	private final String uriPrefix;
	private final OkHttpClient okHttpClient;


	private IHttpAppExceptionRenderer exceptionRenderer = new IHttpAppExceptionRenderer() {
		@Override
		public void parseAndThrow(int status, Object response) throws HttpAppException {
			if (response == null) {
				response = "null";
			}
			if (status >= 400) {
				if (status < 500) {
					switch (status) {
					case 400:
						throw new Http400Exception(response.toString());
					case 401:
						throw new Http401Exception(response.toString());
					case 403:
						throw new Http403Exception(response.toString());
					case 404:
						throw new Http404Exception(response.toString());
					default:
						throw new Http4xxException(response.toString(), status);
					}
				}
				throw new Http5xxException(response.toString(), status);
			}
		}
	};


	public HttpHyperApiFactory(String prefix, OkHttpClient client) {
		uriPrefix = prefix;
		okHttpClient = client;
	}

	public HttpHyperApiFactory(OkHttpClient client) {
		this(null, client);
	}

	@Override
	public <T> T getInstance(String instanceHint, Class<? extends T> apiInterface) {
		return apiInterface.cast(Proxy.newProxyInstance(
				apiInterface.getClassLoader(), new Class[]{apiInterface},
				new HyperApiHttpInvocationHandler(okHttpClient, URIS.pathJoin(uriPrefix, instanceHint),
						allowExceptions ? exceptionRenderer : null)));
	}


	/* setters */

	public void setAllowExceptions(boolean allow) {
		allowExceptions = allow;
	}

	public void setExceptionRenderer(IHttpAppExceptionRenderer exceptionRenderer) {
		this.exceptionRenderer = exceptionRenderer;
	}
}
