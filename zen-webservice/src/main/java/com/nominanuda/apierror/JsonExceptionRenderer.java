/*
 * Copyright 2008-2018 the original author or authors.
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
package com.nominanuda.apierror;

import static com.nominanuda.zen.common.Ex.EX;
import static com.nominanuda.zen.common.Str.STR;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nominanuda.hyperapi.HttpExceptionRenderer;
import com.nominanuda.web.http.Http400Exception;
import com.nominanuda.web.http.Http401Exception;
import com.nominanuda.web.http.Http403Exception;
import com.nominanuda.web.http.Http404Exception;
import com.nominanuda.web.http.Http4xxException;
import com.nominanuda.web.http.Http500Exception;
import com.nominanuda.web.http.Http5xxException;
import com.nominanuda.web.http.HttpAppException;
import com.nominanuda.web.http.IApiError;
import com.nominanuda.zen.codec.Digester;
import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.common.Tuple2;
import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.Obj;

public class JsonExceptionRenderer extends HttpExceptionRenderer {
	private static final String STATUS = "status";
	private static final String MESSAGE = "message";
	private static final String ERROR = "error";
	private static final String PARAM = "param";
	private static final String CAUSE = "cause";
	
	/*
	 * default translator, able to accumulate messages received through json responses
	 * and use them when having to translate exceptions thrown by parseAndThrow(...)
	 * (useful when instantiating hyperapi clients)
	 */
	private ApiErrorTranslator apiErrorTranslator = new ApiErrorTranslator() {
		protected String error2message(IApiError err) {
			return Check.ifNull(cumulatedJsonMessages.get(err.name()), super.error2message(err));
		}
	};
	private final Map<String, String> cumulatedJsonMessages = new HashMap<String, String>();
	private boolean doCumulateJsonMessages = true;
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	private boolean logExpand = false, logDigest = false, debug = false;
	private int logDigestSize = 50;
	
	
	@Override
	public Tuple2<Integer, Object> statusAndRender(HttpAppException e, Class<?> returnType) {
		Check.unsupportedoperation.assertTrue(returnType.isAssignableFrom(Obj.class));
		IApiError apiError = apiErrorTranslator.extract(e);
		log(apiError, e);
		Obj json = Obj.make(
			STATUS, e.getStatusCode(),
			MESSAGE, apiErrorTranslator.translate(apiError),
			ERROR, apiError.name(),
			PARAM, apiError.param()
		);
		if (debug) {
			json.put(CAUSE, fmtErrorCause(e));
		}
		return new Tuple2<Integer, Object>(e.getStatusCode(), json);
	}
	
	private Obj fmtErrorCause(Throwable t) {
		if (t != null) {
			Arr stacktrace = Arr.make();
			for (StackTraceElement elm : t.getStackTrace()) {
				stacktrace.add(elm.toString());
			}
			return Obj.make(
				"type", t.getClass().getName(),
				"message", t.getMessage(),
				"stacktrace", stacktrace,
				CAUSE, fmtErrorCause(t.getCause())
			);
		}
		return null;
	}
	
	
	@Override
	public void parseAndThrow(int status, Object response) throws HttpAppException {
		if (response instanceof Obj) {
			Obj json = (Obj) response;
			int jsonStatus = json.getNum(STATUS).intValue();
			if (jsonStatus >= 400) {
				String message = json.getStr(MESSAGE);
				IApiError apiError = apiErrorTranslator.deserialize(json.getStr(ERROR), message);
				apiError.param(json.getStr(PARAM));
				if (doCumulateJsonMessages) {
					cumulatedJsonMessages.put(apiError.name(), message);
				}
				if (jsonStatus < 500) {
					switch (jsonStatus) {
					case 400:
						throw new Http400Exception(apiError);
					case 401:
						throw new Http401Exception(apiError);
					case 403:
						throw new Http403Exception(apiError);
					case 404:
						throw new Http404Exception(apiError);
					default:
						throw new Http4xxException(apiError, jsonStatus);
					}
				}
				throw jsonStatus == 500
					? new Http500Exception(apiError)
					: new Http5xxException(apiError, jsonStatus);
			}
		} else {
			super.parseAndThrow(status, response);
		}
	}
	
	
	/* logs */

	protected void log(IApiError error, Exception ex) {
		if (logDigest) {
			logDigest(error, ex);
		} else {
			logEvery(error, ex);
		}
	}

	protected void logEvery(IApiError error, Exception ex) {
		log.error(createTitle(error), logExpand ? ex : null);
	}

	private Map<String, Integer> recurrences = new HashMap<String, Integer>();
	private Map<String, Exception> causes = new HashMap<String, Exception>(); // used for?

	protected synchronized void logDigest(IApiError error, Exception ex) {
		String fingerprint = createFingerprint(ex);
		Integer rec = Check.ifNull(recurrences.get(fingerprint), 0);
		recurrences.put(fingerprint, ++rec);
		if (rec == 1) {
			log.error(STR.fmt("{0} - EX_ID: {1}", createTitle(error), fingerprint), logExpand ? ex : null);
		} else {
			log.error(STR.fmt("{0} - REF_EX_ID({1}): {2}", createTitle(error), rec, fingerprint));
			if (rec > logDigestSize) {
				recurrences.remove(fingerprint);
				causes.remove(fingerprint);
			}
		}
	}

	protected String createFingerprint(Exception ex) {
		String st = EX.toStackTrace(ex);
		return new Digester().sha1(st).toBase62().substring(0, 8);
	}
	
	protected String createTitle(IApiError error) {
		return error.name() + " " + Check.ifNull(error.param(), "");
	}
	
	
	
	/* to retrieve the default "cumulative" translator in spring */
	
	public ApiErrorTranslator getApiErrorTranslator() {
		return apiErrorTranslator;
	}
	
	
	/* setters */
	
	public void setApiErrorTranslator(ApiErrorTranslator translator) {
		doCumulateJsonMessages = false;
		apiErrorTranslator = translator;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setLogExpand(boolean logExpand) {
		this.logExpand = logExpand;
	}

	public void setLogDigest(boolean logDigest) {
		this.logDigest = logDigest;
	}

	public void setLogDigestSize(int logDigestSize) {
		this.logDigestSize = logDigestSize;
	}
}
