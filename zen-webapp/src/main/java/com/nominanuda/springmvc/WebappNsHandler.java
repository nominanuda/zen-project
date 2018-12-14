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

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.nominanuda.springsoy.SoyBeanDefinitionParser;

public class WebappNsHandler extends NamespaceHandlerSupport {
	public static final String SITEMAP_NS = "http://nominanuda.com/ns/2018/zen-webapp-1.0";

	@Override
	public void init() {
		registerBeanDefinitionParser("resource", new ResourceBeanDefinitionParser());
		registerBeanDefinitionParser("pattern", new PatternBeanDefinitionParser());
		registerBeanDefinitionParser("ctrl", new CtrlBeanDefinitionParser());
		registerBeanDefinitionParser("soy", new SoyBeanDefinitionParser());
	}
}