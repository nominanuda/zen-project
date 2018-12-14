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

import java.util.UUID;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.nominanuda.springmvc.Sitemap;
import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.common.Tuple2;


public class PatternBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
	@Override
	protected Class<?> getBeanClass(Element element) {
		return Void.class;
	}
	
	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		doParseAndGetPattern(element, parserContext, builder);
	}
	
	@Override
	protected boolean shouldGenerateId() {
		return true;
	};
	
	
	/* static helper */
	
	static Tuple2<String, String> doParseAndGetPattern(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		String id = Check.ifNullOrBlank(element.getAttribute("id"), UUID.randomUUID().toString());
		String pattern = element.getTextContent();
		Sitemap.registerPattern(id, pattern, parserContext);
		return new Tuple2<String, String>(id, pattern);
	}
}
