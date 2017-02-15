package com.nominanuda.springmvc;

import static com.nominanuda.web.http.ServletHelper.SERVLET;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpRequest;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.lang.Tuple2;
import com.nominanuda.web.mvc.HandlerMatcher;

public class MultiHandlerMatcherMapping implements HandlerMapping, ApplicationContextAware {
	public final static String BEAN_PROP_MATCHERS = "handlerMatchers";
	
	protected List<HandlerMatcher> matchers;
	

	public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		HttpRequest httpRequest = SERVLET.getOrCreateRequest(request, true);
		Tuple2<Object, DataStruct> res = match(httpRequest);
		if (res == null) {
			return null;
		} else {
			SERVLET.storeCommand(request, res.get1());
			return new HandlerExecutionChain(res.get0());
		}
	}
	
	private Tuple2<Object, DataStruct> match(HttpRequest request) {
		for (HandlerMatcher matcher : matchers) {
			Tuple2<Object, DataStruct> res = matcher.match(request);
			if (null != res) {
				return res;
			}
		}
		return null;
	}

	
	/* setters */

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//		this.applicationContext = applicationContext;
	}
	
	public void setHandlerMatchers(List<HandlerMatcher> matchers) {
		this.matchers = matchers;
	}
}
