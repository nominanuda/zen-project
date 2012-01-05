package com.nominanuda.springsoy;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import junit.framework.Assert;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.View;

import com.nominanuda.dataobject.DataObject;
import com.nominanuda.io.IOHelper;
import com.nominanuda.springmvc.QueryParamLocaleResolver;
import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.web.mvc.CommandRequestHandlerAdapter;
import com.nominanuda.web.mvc.DataObjectURISpec;

public class SoySourceTest {
	private SoySource soySource;
	@Before
	public void setUp() {
		soySource = new SoySource();
		soySource.setCache(false);
		soySource.setTemplatesLocation(
			new ClassPathResource(getClass().getPackage().getName().replace('.', '/')));
	}

	@Test
	public void testJavaView() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest("GET","/");
		req.addParameter("lang", "en");
		MockHttpServletResponse resp = new MockHttpServletResponse();

		SoyViewResolver viewResolver = new SoyViewResolver();
		viewResolver.setSoySource(soySource);
		
		LocaleResolver localeResolver = new QueryParamLocaleResolver();
		Locale loc = localeResolver.resolveLocale(req);
		View view =  viewResolver.resolveViewName("examples.simple.helloWorld2", loc);
		Map<String, ?> m = Collections.emptyMap();
		view.render(m, req, resp);
		Assert.assertEquals("Hello world!", resp.getContentAsString());
	}

	@Test
	public void testJs() throws Exception {
		SoyJsTemplateServer soyJsTemplateServer = new SoyJsTemplateServer();
		soyJsTemplateServer.setSoySource(soySource);
		HttpRequest req = new HttpGet("/somepath/foo.soy.js?lang=en");
		DataObjectURISpec spec = new DataObjectURISpec("/somepath/{tpl **.soy.js}?{lang en|it}");
		DataObject cmd =  spec.match(req.getRequestLine().getUri());
		CommandRequestHandlerAdapter adapter = new CommandRequestHandlerAdapter();
		StringEntity se = (StringEntity)adapter.invoke(soyJsTemplateServer, req, cmd);
		String jsFile = new IOHelper().readAndClose(se.getContent(), HttpProtocol.CS_UTF_8);
		Assert.assertTrue(jsFile.contains("examples.simple.helloWorld2 = function"));
		Assert.assertEquals(HttpProtocol.CT_TEXT_JAVASCRIPT_CS_UTF8, se.getContentType().getValue());
	}

}
