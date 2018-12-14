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
package com.nominanuda.springsoy;

import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.nominanuda.springmvc.AbsHandlerBeanDefinitionParser;
import com.nominanuda.springmvc.IRequiredBeansIds;
import com.nominanuda.springsoy.SoyJsTemplateServer;
import com.nominanuda.zen.common.Tuple2;



public class SoyBeanDefinitionParser extends AbsHandlerBeanDefinitionParser {
	// TODO configurable
	private String soySourceId = IRequiredBeansIds.SOY_SOURCE;
	
	@Override
	protected BeanDefinition getHandlerDefinition(Element element, ParserContext parserContext, Tuple2<String, String> idAndPattern, Map<String, Object> hardParams) {
		return BeanDefinitionBuilder.genericBeanDefinition(SoyJsTemplateServer.class)
			.addConstructorArgValue(hardParams)
			.addPropertyValue("template", element.getAttribute("src"))
			.addPropertyReference("soySource", soySourceId)
			.getBeanDefinition();
	}
}
