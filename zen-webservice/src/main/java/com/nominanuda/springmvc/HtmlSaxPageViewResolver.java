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
package com.nominanuda.springmvc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import nu.validator.htmlparser.sax.HtmlParser;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

import com.nominanuda.code.CodeConstants;
import com.nominanuda.code.Immutable;
import com.nominanuda.lang.Check;
import com.nominanuda.lang.Initializable;
import com.nominanuda.lang.InstanceFactory;
import com.nominanuda.web.htmlcomposer.DomManipulationStmt;
import com.nominanuda.web.htmlcomposer.DomOp;
import com.nominanuda.web.htmlcomposer.HtmlSaxPage;
import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.xml.ForwardingTransformerHandlerBase;
import com.nominanuda.xml.HtmlFragmentParser;
import com.nominanuda.xml.SAXPipeline;
import com.nominanuda.xml.SaxBuffer;
import com.nominanuda.xml.XHtml5Serializer;

public class HtmlSaxPageViewResolver implements CodeConstants, ViewResolver, ApplicationContextAware, Initializable, HttpProtocol {
	private List<ViewResolver> resolvers = null;
	private ApplicationContext applicationContext;
	private boolean html = true;

	public void init() {
		resolvers = new LinkedList<ViewResolver>();
		resolvers.addAll(applicationContext.getBeansOfType(ViewResolver.class).values());
	}

	public View resolveViewName(String viewName, Locale locale)
			throws Exception {
		return "htmlcomposer_".equals(viewName) ? makeView(locale) : null;
	}

	private View makeView(Locale locale) {
		return new AsyncView(locale);
	}

	private List<ViewResolver> getViewResolvers() {
		return resolvers;
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
	@Immutable
	public static class JqFragmentAndManipulationStmt {
		final View view;
		final Map<String, ?> model;
		final String selector;
		final DomOp domOp;

		public JqFragmentAndManipulationStmt(View view, Map<String, ?> model, String selector,
				DomOp domOp) {
			this.view = Check.illegalargument.assertNotNull(view);
			this.model = Check.illegalargument.assertNotNull(model);
			this.selector = Check.illegalargument.assertNotNull(selector);
			this.domOp = Check.illegalargument.assertNotNull(domOp);
		}

		public View getView() {
			return view;
		}
		public Map<String, ?> getModel() {
			return model;
		}
		public String getSelector() {
			return selector;
		}
		public DomOp getDomOp() {
			return domOp;
		}
	}

	private class AsyncView implements View {
		private Locale locale;

		public AsyncView(Locale locale) {
			this.locale = locale;
		}

		public String getContentType() {
			return HttpProtocol.CT_TEXT_HTML_CS_UTF8;
		}

		public void render(Map<String, ?> model, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ContentHandler ch = createSerializer(new OutputStreamWriter(baos, UTF_8));
			HtmlSaxPage p = new HtmlSaxPage();
			
			List<JqFragmentAndManipulationStmt> springMavs = getSpringViewsAndModels(model);
			for(JqFragmentAndManipulationStmt mav : springMavs) {
				View v = mav.getView();
				Map<String, ?> m = mav.getModel();
				String selector = mav.getSelector();
				DomOp domOp = mav.getDomOp();
				CollectingResponse cr = new CollectingResponse(response);
				v.render(m, request, cr);
				SaxBuffer sbuf = new SaxBuffer();
				new SAXPipeline()
					.complete()
					.build(saxSource(cr.getBuffer()), new SAXResult(sbuf))
					.run();
				p.applyStmt(new DomManipulationStmt(selector, new InstanceFactory<SaxBuffer>(sbuf), domOp));
			}
			new SaxBuffer.StartDocument().send(ch);
			new SaxBuffer.StartElement(HTMLNS,"html","html",new AttributesImpl()).send(ch);
			p.toSAX(ch);
			new SaxBuffer.EndElement(HTMLNS,"html","html").send(ch);
			new SaxBuffer.EndDocument().send(ch);
			byte[] page = baos.toByteArray();
			response.setHeader(HDR_CONTENT_LENGTH, new Integer(page.length).toString());
			response.getOutputStream().write(page);
		}

		
		private Source saxSource(InputStream is) {
			if(html) {
				HtmlParser parser = new HtmlParser();
				parser.setMappingLangToXmlLang(true);
				parser.setReportingDoctype(false);
				InputSource inputSource = new InputSource(is);
				inputSource.setEncoding(UTF_8);
				SAXSource src = new SAXSource(new HtmlFragmentParser(parser), inputSource);
				return src;
			} else {
				return new StreamSource(is);
			}
		}

		private ContentHandler createSerializer(Writer out) {
			if(html) {
				XHtml5Serializer ser = new XHtml5Serializer(out);
				return ser;
			} else {
				ForwardingTransformerHandlerBase tx = new ForwardingTransformerHandlerBase();
				tx.setResult(new StreamResult(out));
				return tx;
			}
		}

		@SuppressWarnings("unchecked")
		private List<JqFragmentAndManipulationStmt> getSpringViewsAndModels(Map<String, ?> model) throws Exception {
			List<JqFragmentAndManipulationStmt> mavs = new LinkedList<JqFragmentAndManipulationStmt>();
			List<ViewResolver> resolvers = getViewResolvers();
			List<Map<String, ?>> viewDefs = (List<Map<String, ?>>)model.get("views_");
			for(Map<String, ?> viewDef : viewDefs) {
				String viewName = (String)viewDef.get("view_");
				View v = null;
				for(ViewResolver r : resolvers) {
					v = r.resolveViewName(viewName, locale);
					if(v != null) {
						DomOp op = DomOp.valueOf((String)viewDef.get("domOp_"));
						mavs.add(new JqFragmentAndManipulationStmt(
								v,
								(Map<String, ?>)viewDef.get("data_"),
								(String)viewDef.get("selector_"),
								op
						));
						break;
					}
				}
				Check.illegalargument.assertNotNull(v, "cannot resolve view named:"+viewName);
			}
			return mavs;
		}
	}

	public static class CollectingResponse extends HttpServletResponseWrapper {
		private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		public CollectingResponse(HttpServletResponse response) {
			//TODO shield setters headers etc
			super(response);
		}
		public InputStream getBuffer() {
			return new ByteArrayInputStream(baos.toByteArray());
		}
		public ServletOutputStream getOutputStream() throws IOException {
			return new ServletOutputStream() {
				public void write(int b) throws IOException {
					baos.write(b);
				}
				public void write(byte[] b, int off, int len)
						throws IOException {
					baos.write(b, off, len);
				}
				public void write(byte[] b) throws IOException {
					baos.write(b);
				}
				//@Override
				public boolean isReady() {
					return true;
				}
				//@Override
				public void setWriteListener(WriteListener arg0) {
					Check.illegalstate.fail(NOT_IMPLEMENTED);
				}
			};
		}

		public PrintWriter getWriter() throws IOException {
			throw new UnsupportedOperationException(NOT_IMPLEMENTED);
			//return new PrintWriter(getOutputStream());
		}
		
	}
}
