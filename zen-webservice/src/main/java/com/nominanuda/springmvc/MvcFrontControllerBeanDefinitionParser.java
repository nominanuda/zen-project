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

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.nominanuda.springmvc.HandlerMatcherMapping;
import com.nominanuda.web.mvc.URISpecMatcher;
import com.nominanuda.web.mvc.URITransformerURLStreamer;

public class MvcFrontControllerBeanDefinitionParser extends
		AbstractBeanDefinitionParser {
	private static final String ns = MvcFrontControllerNsHandler.SITEMAP_NS;
	private static final String beansNs = MvcFrontControllerNsHandler.BEANS_NS;
	private List<MvcFrontControllerBeanDefinitionParserPlugin> plugins = null;

	protected AbstractBeanDefinition parseInternal(Element element,
			ParserContext parserContext) {
		if (plugins == null) {
			try {
				initPlugins(parserContext);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		BeanDefinitionBuilder bdBuilder = BeanDefinitionBuilder
				.rootBeanDefinition(HandlerMatcherMapping.class);

		String pattern = ((Element) element.getElementsByTagNameNS(ns,
				"pattern").item(0)).getTextContent();

		String uriSpec = getUriSpec(pattern);
		BeanDefinitionBuilder matchBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(URISpecMatcher.class);
		matchBuilder.addPropertyValue("spec", pattern);
		String matchId = uuid();
		parserContext.getRegistry().registerBeanDefinition(matchId,
				matchBuilder.getBeanDefinition());
		bdBuilder.addPropertyReference("handlerMatcher", matchId);
		String handlerId = generateHandler(element, parserContext, uriSpec);
		matchBuilder.addPropertyReference("handler", handlerId);
		AbstractBeanDefinition abd = bdBuilder.getBeanDefinition();
		abd.setAutowireCandidate(false);
		return abd;
	}

	private void initPlugins(ParserContext parserContext)
			throws InstantiationException, IllegalAccessException {
		plugins = new LinkedList<MvcFrontControllerBeanDefinitionParserPlugin>();
		plugins.add(new HandlerParserPlugin());
		plugins.add(new SourceParserPlugin());

		String[] beanNames = parserContext.getRegistry()
				.getBeanDefinitionNames();
		for (String beanName : beanNames) {
			BeanDefinition bd = parserContext.getRegistry().getBeanDefinition(
					beanName);
			String clsName = bd.getBeanClassName();
			if (clsName != null) {
				try {
					Class<?> cls = Class.forName(clsName);
					if (MvcFrontControllerBeanDefinitionParserPlugin.class
							.isAssignableFrom(cls)) {
						plugins.add((MvcFrontControllerBeanDefinitionParserPlugin) cls
								.newInstance());
					}
				} catch (ClassNotFoundException e) {
					continue;
				}

			}
		}
	}

	private String getUriSpec(String pattern) {
		return pattern
				.replaceAll(
						"(?:(?:GET|PUT|POST|DELETE|HEAD|OPTIONS)\\|)*(?:GET|PUT|POST|DELETE|HEAD|OPTIONS)?\\s*(.+)",
						"$1");
	}

	private String generateHandler(Element element,
			ParserContext parserContext, String uriSpec) {
		for (MvcFrontControllerBeanDefinitionParserPlugin p : plugins) {
			if (p.supports(element)) {
				return p.generateHandler(element, parserContext, uriSpec);
			}
		}
		throw new IllegalStateException(
				"could not find a suitable plugin for element "
						+ element.getNodeName());
	}

	public static String uuid() {
		return UUID.randomUUID().toString();
	}

	public static String getAttribute(Element el, String name) {
		String val = el.getAttribute(name);
		if (val == null || "".equals(val)) {
			return null;
		}
		return val;
	}

	private static class HandlerParserPlugin implements
			MvcFrontControllerBeanDefinitionParserPlugin {

		public boolean supports(Element el) {
			return null != el.getElementsByTagNameNS(ns, "handler").item(0);
		}

		public String generateHandler(Element element,
				ParserContext parserContext, String uriSpec) {
			Element handler = (Element) element.getElementsByTagNameNS(ns,
					"handler").item(0);
			String ref = getAttribute(handler, "ref");
			if (ref != null) {
				return ref;
			} else {
				Element innerBean = (Element) handler.getElementsByTagNameNS(
						beansNs, "bean").item(0);
				String innerBeanId = getAttribute(innerBean, "id");
				if (innerBeanId == null) {
					innerBeanId = uuid();
				}
				BeanDefinition bd = parserContext.getDelegate()
						.parseBeanDefinitionElement(innerBean)
						.getBeanDefinition();
				parserContext.getRegistry().registerBeanDefinition(innerBeanId,
						bd);
				return innerBeanId;
			}
		}
	}

	private class SourceParserPlugin implements
			MvcFrontControllerBeanDefinitionParserPlugin {

		public boolean supports(Element el) {
			return null != el.getElementsByTagNameNS(ns, "source").item(0);
		}

		public String generateHandler(Element element,
				ParserContext parserContext, String uriSpec) {
			Element handler = (Element) element.getElementsByTagNameNS(ns, "source").item(0);
			String url = handler.getElementsByTagNameNS(ns, "url").item(0)
					.getTextContent();
			BeanDefinitionBuilder sourceBuilder = BeanDefinitionBuilder
					.genericBeanDefinition(URITransformerURLStreamer.class);
			sourceBuilder.addPropertyValue("match", uriSpec);
			sourceBuilder.addPropertyValue("template", url);
			sourceBuilder.addPropertyReference("urlResolver", "urlResolver");
			String id = uuid();
			parserContext.getRegistry().registerBeanDefinition(id,
					sourceBuilder.getBeanDefinition());
			return id;
		}

	}
}
