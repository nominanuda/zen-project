package com.nominanuda.springmvc;

import static com.nominanuda.springmvc.MvcFrontControllerNsHandler.SITEMAP_NS;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class JsParserPlugin implements
		MvcFrontControllerBeanDefinitionParserPlugin {

	public boolean supports(Element el) {
		return null != el.getElementsByTagNameNS(SITEMAP_NS, "js").item(0);
	}

	public String generateHandler(Element element, ParserContext parserContext,
			String uriSpec) {
		Element handler = (Element) element.getElementsByTagNameNS(SITEMAP_NS,
				"js").item(0);
		String url = handler.getElementsByTagNameNS(SITEMAP_NS, "url").item(0)
				.getTextContent();
		BeanDefinitionBuilder sourceBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(RhinoHandler.class);
		sourceBuilder.addPropertyValue("spec", url);
		sourceBuilder.addPropertyReference("rhinoEmbedding", "rhinoEmbedding");
		sourceBuilder.addPropertyReference("scopeFactory", "scopeFactory");
		String id = MvcFrontControllerBeanDefinitionParser.uuid();
		parserContext.getRegistry().registerBeanDefinition(id,
				sourceBuilder.getBeanDefinition());
		return id;
	}

}
