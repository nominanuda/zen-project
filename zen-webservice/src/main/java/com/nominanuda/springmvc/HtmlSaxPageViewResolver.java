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

import com.nominanuda.lang.Check;
import com.nominanuda.lang.Initializable;
import com.nominanuda.lang.InstanceFactory;
import com.nominanuda.lang.Tuple4;
import com.nominanuda.web.htmlcomposer.DomManipulationStmt;
import com.nominanuda.web.htmlcomposer.DomOp;
import com.nominanuda.web.htmlcomposer.HtmlSaxPage;
import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.xml.ForwardingTransformerHandlerBase;
import com.nominanuda.xml.HtmlFragmentParser;
import com.nominanuda.xml.SAXPipeline;
import com.nominanuda.xml.SaxBuffer;
import com.nominanuda.xml.XHtml5Serializer;

public class HtmlSaxPageViewResolver implements ViewResolver, ApplicationContextAware, Initializable, HttpProtocol {
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
	class AsyncView implements View {
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
			ContentHandler ch = createSerializer(new OutputStreamWriter(baos));
			HtmlSaxPage p = new HtmlSaxPage();
			
			List<Tuple4<View,Map<String, ?>,DomOp,String>> springMavs = getSpringViewsAndModels(model);
			for(Tuple4<View,Map<String, ?>,DomOp,String> mav : springMavs) {
				View v = mav.get0();
				Map<String, ?> m = mav.get1();
				String selector = Check.illegalargument.assertNotNull(mav.get3());
				DomOp domOp = Check.illegalargument.assertNotNull(mav.get2());
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
				SAXSource src = new SAXSource(new HtmlFragmentParser(parser), new InputSource(is));
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

		private List<Tuple4<View,Map<String, ?>,DomOp,String>> getSpringViewsAndModels(Map<String, ?> model) throws Exception {
			List<Tuple4<View,Map<String, ?>,DomOp,String>> mavs = new LinkedList<Tuple4<View,Map<String, ?>,DomOp,String>>();
			List<ViewResolver> resolvers = getViewResolvers();
			List<Map<String, ?>> viewDefs = (List<Map<String, ?>>)model.get("views_");
			for(Map<String, ?> viewDef : viewDefs) {
				String viewName = (String)viewDef.get("view_");
				View mav = null;
				for(ViewResolver r : resolvers) {
					mav = r.resolveViewName(viewName, locale);
					if(mav != null) {
						
						DomOp op = DomOp.valueOf((String)viewDef.get("domOp_"));
						mavs.add(new Tuple4<View,Map<String, ?>,DomOp,String>(
								mav,
								(Map<String, ?>)viewDef.get("data_"),
								op,
								(String)viewDef.get("selector_")
						));
						break;
					}
				}
				Check.illegalargument.assertNotNull(mav, "cannot resolve view named:"+viewName);
			}
			return mavs;
		}
		
	}
	private class CollectingResponse extends HttpServletResponseWrapper {
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
			};
		}

		public PrintWriter getWriter() throws IOException {
			return new PrintWriter(getOutputStream());
		}
		
	}
}
