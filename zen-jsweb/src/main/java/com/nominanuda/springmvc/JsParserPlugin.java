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
//	private static final String RUNTIMEPROFILE_DEV = "dev";
//	private static final String RUNTIMEPROFILE = "runtimeprofile";
	
	// TODO configurable
	private boolean mergeEntityDataObject = true;
	private boolean mergeGetAndPostFormParams = true;
	private String scopeFactoryId = IRequiredBeansIds.RHINO_SCOPE_FACTORY;
	private String function = "handle";

	public boolean supports(Element el) {
		return null != el.getElementsByTagNameNS(SITEMAP_NS, "js").item(0);
	}

	public String generateHandler(Element element, ParserContext parserContext, String uriSpec) {
		Element handler = (Element) element.getElementsByTagNameNS(SITEMAP_NS, "js").item(0);
		String url = handler.getElementsByTagNameNS(SITEMAP_NS, "url").item(0).getTextContent();
		String uuid = MvcFrontControllerBeanDefinitionParser.uuid();
		parserContext.getRegistry().registerBeanDefinition(uuid, BeanDefinitionBuilder.genericBeanDefinition(CompilingRhinoHandler.class)
			.addPropertyReference("sitemap", Sitemap.BEAN_ID)
			.addPropertyValue("patternId", element.getAttribute("id"))
			.addPropertyValue("uriSpec", url)
			.addPropertyReference("springScopeFactory", scopeFactoryId)
//			.addPropertyValue("develMode", RUNTIMEPROFILE_DEV.equals(System.getProperty(RUNTIMEPROFILE)))
			.addPropertyValue("mergeGetAndPostFormParams", mergeGetAndPostFormParams)
			.addPropertyValue("mergeEntityDataObject", mergeEntityDataObject)
			.addPropertyValue("function", function)
			.setInitMethodName("init")
			.getBeanDefinition());
		return uuid;
	}
}
