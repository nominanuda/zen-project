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

import static com.nominanuda.springmvc.MvcFrontControllerNsHandler.SITEMAP_NS;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class JsParserPlugin implements MvcFrontControllerBeanDefinitionParserPlugin {
	public static final String RUNTIMEPROFILE_DEV = "dev";
	public static final String RUNTIMEPROFILE = "runtimeprofile";

	public boolean supports(Element el) {
		return null != el.getElementsByTagNameNS(SITEMAP_NS, "js").item(0);
	}

	public String generateHandler(Element element, ParserContext parserContext, String uriSpec) {
		Element handler = (Element) element.getElementsByTagNameNS(SITEMAP_NS, "js").item(0);
		String url = handler.getElementsByTagNameNS(SITEMAP_NS, "url").item(0).getTextContent();
		
		String runtimeprofile = System.getProperty(RUNTIMEPROFILE);
		BeanDefinitionBuilder sourceBuilder = RUNTIMEPROFILE_DEV.equals(runtimeprofile)
			? BeanDefinitionBuilder.genericBeanDefinition(RhinoHandler.class)
			: BeanDefinitionBuilder.genericBeanDefinition(CompilingRhinoHandler.class);
		sourceBuilder.addPropertyValue("spec", url);
		sourceBuilder.addPropertyReference("rhinoEmbedding", "rhinoEmbedding");
		sourceBuilder.addPropertyReference("scopeFactory", "scopeFactory");
		String id = MvcFrontControllerBeanDefinitionParser.uuid();
		parserContext.getRegistry().registerBeanDefinition(id, sourceBuilder.getBeanDefinition());
		return id;
	}
}
