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
package com.nominanuda.springmvc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nominanuda.springmvc.MultiHandlerMatcherMapping;
import com.nominanuda.web.mvc.URISpecMatcher;
import com.nominanuda.zen.common.Tuple2;

public abstract class AbsHandlerBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
	
	@Override
	protected Class<?> getBeanClass(Element element) {
		return MultiHandlerMatcherMapping.class;
	}
	
	@Override
	protected boolean shouldGenerateId() {
		return true;
	}
	
	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		BeanDefinitionRegistry registry = parserContext.getRegistry();
		ManagedList<BeanDefinition> matchers = new ManagedList<BeanDefinition>();
		
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (WebappNsHandler.SITEMAP_NS.equals(child.getNamespaceURI())) {
				Map<String, Object> hardParams = new HashMap<String, Object>();
				if ("match".equals(child.getLocalName())) {
					Element match = (Element) child;
					NodeList params = match.getElementsByTagNameNS(WebappNsHandler.SITEMAP_NS, "param");
					for (int j=0; j < params.getLength(); j++) {
						Element param = (Element) params.item(j);
						hardParams.put(param.getAttribute("name"), param.getTextContent());
					}
					child = match.getElementsByTagNameNS(WebappNsHandler.SITEMAP_NS, "pattern").item(0); // and continue to "pattern"
				}
				if ("pattern".equals(child.getLocalName())) {
					String handlerId = UUID.randomUUID().toString();
					Tuple2<String, String> idAndPattern = PatternBeanDefinitionParser.doParseAndGetPattern((Element) child, parserContext, builder);
					registry.registerBeanDefinition(handlerId, getHandlerDefinition(element, parserContext, idAndPattern, hardParams));
					matchers.add(BeanDefinitionBuilder.genericBeanDefinition(URISpecMatcher.class)
						.addPropertyReference("handler", handlerId)
						.addPropertyValue("spec", idAndPattern.get1())
						.getBeanDefinition());
				}
			}
		}
		
		builder.addPropertyValue(MultiHandlerMatcherMapping.BEAN_PROP_MATCHERS, matchers);
	}
	
	protected abstract BeanDefinition getHandlerDefinition(Element element, ParserContext parserContext, Tuple2<String, String> idAndPattern, Map<String, Object> hardParams);
}
