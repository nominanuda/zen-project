package com.nominanuda.springmvc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.springframework.web.servlet.ModelAndView;

import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataObjectImpl;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.dataobject.MapsAndListsObjectDecorator;
import com.nominanuda.lang.Check;
import com.nominanuda.web.http.ServletHelper;
import com.nominanuda.web.mvc.HandlerAdapter;
import com.nominanuda.web.mvc.PathAndJsonViewSpec;

public class HandlerAdapterWrapper implements org.springframework.web.servlet.HandlerAdapter {
	private static final ServletHelper servletHelper = new ServletHelper();
	private HandlerAdapter handlerAdapter;

	public boolean supports(Object handler) {
		return handlerAdapter.supports(handler);
	}

	public ModelAndView handle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		HttpRequest httpReq = servletHelper.getOrCreateRequest(request, true);
		DataStruct<?> command = Check.ifNull(servletHelper.getCommand(request), new DataObjectImpl());
		Object result = handlerAdapter.invoke(handler, httpReq, command);
		if(result instanceof HttpResponse) {
			servletHelper.copyResponse((HttpResponse)result, response);
			return null;
		} else if(result instanceof HttpEntity) {
			return new ModelAndView(new HttpEntityView((HttpEntity)result));
		} else if(result instanceof PathAndJsonViewSpec) {
			PathAndJsonViewSpec viewSpec = (PathAndJsonViewSpec)result;
			Map<String, ?> model = new MapsAndListsObjectDecorator(
					(DataObject)viewSpec.getModel());
			return new ModelAndView(viewSpec.getPath(), model);
		} else {
			throw new IllegalStateException();
		}
	}

	public long getLastModified(HttpServletRequest request, Object handler) {
		return 0;
	}

	public void setHandlerAdapter(HandlerAdapter handlerAdapter) {
		this.handlerAdapter = handlerAdapter;
	}

}
