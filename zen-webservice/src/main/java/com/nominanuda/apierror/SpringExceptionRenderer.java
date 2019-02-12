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

import java.io.IOException;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.nominanuda.web.http.HttpAppException;
import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.zen.common.Tuple2;
import com.nominanuda.zen.obj.Obj;

@ThreadSafe
public class SpringExceptionRenderer extends JsonExceptionRenderer implements HandlerExceptionResolver, HttpProtocol {
	private final static boolean PLAIN_TEXT_STACKTRACE = false; // be careful with this one, if clients depend on json response they will break
	private boolean alwaysAnswer200 = false;


	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, final Exception e) {
		if (PLAIN_TEXT_STACKTRACE) {
			return new ModelAndView() {
				@Override public boolean hasView() {
					return true;
				}
				@Override public boolean isEmpty() {
					return false;
				}
				
				@Override public View getView() {
					return new View() {
						@Override public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
							setResponseStatus(response, 500);
							renderResponse(EX.toStackTrace(e), response);
						}
						
						@Override public String getContentType() {
							return CT_TEXT_PLAIN_CS_UTF8;
						}
					};
				}
			};
		} else {
			return new ModelAndView() {
				@Override public boolean hasView() {
					return true;
				}
				@Override public boolean isEmpty() {
					return false;
				}
				
				@Override public View getView() {
					return new View() {
						@Override public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
							Tuple2<Integer, Object> statusAndJson = statusAndRender(HttpAppException.from(e), Obj.class);
							setResponseStatus(response, statusAndJson.get0());
							renderResponse(statusAndJson.get1().toString(), response);
						}

						@Override public String getContentType() {
							return CT_APPLICATION_JSON_CS_UTF8;
						}
					};
				}
			};
		}
	}
	
	
	protected void renderResponse(String body, HttpServletResponse response) throws IOException {
		response.getWriter().write(body);
	}

	protected void setResponseStatus(HttpServletResponse response, int status) {
		response.setStatus(alwaysAnswer200 ? 200 : status);
	}
	
	
	/* setters */
	
	public void setAlwaysAnswer200(boolean alwaysAnswer200) {
		this.alwaysAnswer200 = alwaysAnswer200;
	}
}
