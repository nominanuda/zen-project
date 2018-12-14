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

import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nominanuda.springmvc.IRequiredBeansIds;
import com.nominanuda.springmvc.Sitemap;
import com.nominanuda.zen.common.Tuple2;


public class CtrlBeanDefinitionParser extends AbsHandlerBeanDefinitionParser {
	// TODO configurable
	private boolean mergeEntityDataObject = true;
	private boolean mergeGetAndPostFormParams = true;
	private String scopeFactoryId = IRequiredBeansIds.RHINO_SCOPE_FACTORY;
	private String function = "handle";
	
	@Override
	protected BeanDefinition getHandlerDefinition(Element element, ParserContext parserContext, Tuple2<String, String> idAndPattern, Map<String, Object> hardParams) {
		ManagedMap<String, Object> requiresMap = new ManagedMap<String, Object>();
		NodeList requires = element.getElementsByTagNameNS(WebappNsHandler.SITEMAP_NS, "require");
		for (int i = 0; i < requires.getLength(); i++) {
			Element require = (Element) requires.item(i);
			if (require.hasAttribute("name")) {
				String name = require.getAttribute("name");
				if (require.hasAttribute("value")) {
					requiresMap.put(name, require.getAttribute("value"));
				} else if (require.hasAttribute("ref")) {
					requiresMap.put(name, new RuntimeBeanReference(require.getAttribute("ref")));
				} else if (require.hasChildNodes()) {
					Element subElement = null;
					NodeList nl = require.getChildNodes();
					for (int j = 0; j < nl.getLength(); j++) {
						Node node = nl.item(j);
						if (node instanceof Element) {
							if (subElement == null) {
								subElement = (Element) node;
							}
						}
					}
					if (subElement != null) {
						requiresMap.put(name, parserContext.getDelegate().parsePropertySubElement(subElement, null));
					}
				}
			}
		}
		
		return BeanDefinitionBuilder.genericBeanDefinition(WebappRhinoHandler.class)
			.addConstructorArgValue(hardParams)
			.addConstructorArgValue(requiresMap)
			.addPropertyReference("sitemap", Sitemap.BEAN_ID)
			.addPropertyValue("patternId", idAndPattern.get0())
			.addPropertyValue("uriSpec", element.getAttribute("src"))
			.addPropertyReference("springScopeFactory", scopeFactoryId)
//			.addPropertyValue("develMode", RUNTIMEPROFILE_DEV.equals(System.getProperty(RUNTIMEPROFILE)))
			.addPropertyValue("mergeGetAndPostFormParams", mergeGetAndPostFormParams)
			.addPropertyValue("mergeEntityDataObject", mergeEntityDataObject)
			.addPropertyValue("function", function)
			.setInitMethodName("init")
			.getBeanDefinition();
	}
}
