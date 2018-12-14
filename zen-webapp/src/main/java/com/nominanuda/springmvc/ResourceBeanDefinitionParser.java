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

import javax.annotation.Nullable;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.nominanuda.urispec.Utils;
import com.nominanuda.web.mvc.URITransformerURLStreamer;
import com.nominanuda.zen.common.Tuple2;


public class ResourceBeanDefinitionParser extends AbsHandlerBeanDefinitionParser {
	@Override
	protected BeanDefinition getHandlerDefinition(Element element, ParserContext parserContext, Tuple2<String, String> idAndPattern, Map<String, Object> hardParams) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(URITransformerURLStreamer.class)
//			.addConstructorArgValue(hardParams) // TODO
			.addPropertyValue("match", Utils.extractUriSpecFromSitemapMatch(idAndPattern.get1()))
			.addPropertyValue("template", element.getAttribute("src"));
		@Nullable String defaultContentType = element.getAttribute("default-content-type");
		if (defaultContentType != null) {
			builder.addPropertyValue("defaultContentType", defaultContentType);
		}
		return builder.getBeanDefinition();
	}
}
