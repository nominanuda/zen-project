package org.springframework.web.servlet;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import com.nominanuda.code.Nullable;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.MapsAndListsObjectDecorator;
import com.nominanuda.springmvc.HttpEntityView;
import com.nominanuda.web.http.ServletHelper;
import com.nominanuda.web.mvc.PathAndJsonViewSpec;

public class DispatcherServletHelper implements ApplicationContextAware {
	private List<ViewResolver> resolvers = null;
	private ApplicationContext applicationContext;
	private static final ServletHelper servletHelper = new ServletHelper();
	private LocaleResolver localeResolver;
	public void init() {
		resolvers = new LinkedList<ViewResolver>();
		resolvers.addAll(applicationContext.getBeansOfType(ViewResolver.class)
				.values());
		localeResolver = applicationContext.getBean(DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME, LocaleResolver.class);

	}

	// String viewName = null;
	// Locale locale;
	// View mav = null;
	// Check.illegalargument.assertNotNull(mav,
	// "cannot resolve view named:"+viewName);
	private @Nullable
	View resolveViewName(String viewName, Locale locale) throws Exception {
		for (ViewResolver r : resolvers) {
			View view = r.resolveViewName(viewName, locale);
			if (view != null) {
				return view;
			}
		}
		return null;
	}

	public void renderHandlerOutput(HttpServletRequest request,
			HttpServletResponse response, Object result) throws Exception {
		if (result instanceof HttpResponse) {
			servletHelper.copyResponse((HttpResponse) result, response);
		} else {
			Locale locale = getLocale(request);
			response.setLocale(locale);
			ModelAndView mav = makeModelAndView(response, result);
			View view = mav.getView();
			if(view == null) {
				view = resolveViewName(mav.getViewName(), locale);
			}
			view.render(mav.getModelInternal(), request, response);
		}
	}

	private Locale getLocale(HttpServletRequest request) {
		if(localeResolver != null) {
			Locale locale = this.localeResolver.resolveLocale(request);
			return locale;
		} else {
			return null;
		}
	}

	private ModelAndView makeModelAndView(HttpServletResponse response,
			Object result) throws IOException {
		if (result instanceof HttpEntity) {
			return new ModelAndView(new HttpEntityView((HttpEntity) result));
		} else if (result instanceof PathAndJsonViewSpec) {
			PathAndJsonViewSpec viewSpec = (PathAndJsonViewSpec) result;
			Map<String, ?> model = new MapsAndListsObjectDecorator(
					(DataObject) viewSpec.getModel());
			return new ModelAndView(viewSpec.getPath(), model);
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

}
