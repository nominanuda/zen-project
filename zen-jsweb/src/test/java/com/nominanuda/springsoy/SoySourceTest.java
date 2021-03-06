/*
 * Copyright 2008-2011 the original author or authors.
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
package com.nominanuda.springsoy;

import static com.nominanuda.zen.oio.OioUtils.IO;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.View;

import com.nominanuda.springmvc.QueryParamLocaleResolver;
import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.web.mvc.CommandRequestHandlerAdapter;
import com.nominanuda.zen.obj.Obj;

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
		
		//TODO
		
//		ObjURISpec spec = new ObjURISpec("/somepath/{tpl **.soy.js}?{lang en|it}");
//		Obj cmd = spec.match(req.getRequestLine().getUri());
		Obj cmd = Obj.make("tpl","foo","lang","en");
		CommandRequestHandlerAdapter adapter = new CommandRequestHandlerAdapter();
		StringEntity se = (StringEntity)adapter.invoke(soyJsTemplateServer, req, cmd);
		String jsFile = IO.readAndClose(se.getContent(), HttpProtocol.CS_UTF_8);
		Assert.assertTrue(jsFile.contains("examples.simple.helloWorld2 = function"));
		Assert.assertEquals(HttpProtocol.CT_TEXT_JAVASCRIPT_CS_UTF8, se.getContentType().getValue());
	}

}
