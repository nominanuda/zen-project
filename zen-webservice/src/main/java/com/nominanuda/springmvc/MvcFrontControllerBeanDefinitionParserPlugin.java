package com.nominanuda.springmvc;

import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public interface MvcFrontControllerBeanDefinitionParserPlugin {

	boolean supports(Element el);
	/**
	 * 
	 * @param uriSpec 
	 * @param parserContext 
	 * @param element 
	 * @return the handler id
	 */
	String generateHandler(Element element, ParserContext parserContext, String uriSpec);
}
