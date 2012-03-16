package com.nominanuda.springmvc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
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
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.xml.sax.ContentHandler;

import com.nominanuda.codec.Digester;
import com.nominanuda.lang.Check;
import com.nominanuda.lang.Initializable;
import com.nominanuda.lang.Tuple2;
import com.nominanuda.lang.Tuple3;
import com.nominanuda.lang.Tuple4;
import com.nominanuda.saxpipe.ForwardingTransformerHandlerBase;
import com.nominanuda.saxpipe.SAXPipeline;
import com.nominanuda.saxpipe.SaxBuffer;
import com.nominanuda.saxpipe.XHtml5Serializer;
import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.web.mvc.HtmlComposer;
import com.nominanuda.web.mvc.HtmlComposer.DomOp;

public class AsyncJsonViewResolver implements ViewResolver, ApplicationContextAware, Initializable, HttpProtocol {
	private List<ViewResolver> resolvers = null;
	private ApplicationContext applicationContext;

	public void init() {
		resolvers = new LinkedList<ViewResolver>();
		resolvers.addAll(applicationContext.getBeansOfType(ViewResolver.class).values());
	}

	public View resolveViewName(String viewName, Locale locale)
			throws Exception {
		return "async_".equals(viewName) ? makeView(locale) : null;
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
			List<Tuple4<View,Map<String, ?>,DomOp,String>> springMavs = getSpringViewsAndModels(model);
			boolean firstRound = true;
			List<Tuple3<String, SaxBuffer, DomOp>> saxViews = new LinkedList<Tuple3<String, SaxBuffer, DomOp>>();
			for(Tuple4<View,Map<String, ?>,DomOp,String> mav : springMavs) {
				View v = mav.get0();
				Map<String, ?> m = mav.get1();
				String selector = firstRound ? null : 
					Check.illegalargument.assertNotNull(mav.get3());
				DomOp domOp = firstRound ? null : mav.get2();
				firstRound = false;
				CollectingResponse cr = new CollectingResponse(response);
				v.render(m, request, cr);
				SaxBuffer sbuf = new SaxBuffer();
				new SAXPipeline().complete().build(new StreamSource(cr.getBuffer()), new SAXResult(sbuf)).run();
				saxViews.add(new Tuple3<String, SaxBuffer, DomOp>(selector, sbuf, domOp));
			}
			HtmlComposer composer = new HtmlComposer(ch);
			composer.render(saxViews);
			byte[] page = baos.toByteArray();
			response.setHeader(HDR_CONTENT_LENGTH, new Integer(page.length).toString());
			response.getOutputStream().write(page);
		}

		private ContentHandler createSerializer(Writer out) {
			ForwardingTransformerHandlerBase tx = new ForwardingTransformerHandlerBase();
			tx.setResult(new StreamResult(out));
			return tx;
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
						DomOp op = viewDef.containsKey("domOp_") ? DomOp.valueOf((String)viewDef.get("domOp_")) : null;
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
