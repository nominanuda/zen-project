package com.nominanuda.springmvc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.ParserContext;

import com.nominanuda.urispec.Utils;
import com.nominanuda.web.mvc.ObjURISpec;
import com.nominanuda.zen.obj.Obj;

public class Sitemap {
	public static final String BEAN_ID = "__zen-webservice-sitemap__"; // use uuid instead?
	private static final String BEAN_PROP_ENTRIES = "entries";
	
	private final Map<String, ObjURISpec> specs = new HashMap<>();
	
	
	public String getUrl(String id, Obj o) {
		ObjURISpec s = specs.get(id);
		if (null != s) {
			return s.template(o != null ? o : Obj.make());
		}
		return null;
	}
	
	
	/* setter */
	
	public void setEntries(Map<String, String> entries) {
		for (String id : entries.keySet()) {
			specs.put(id, new ObjURISpec(Utils.extracturiSpecFromSitemapMatch(entries.get(id))));
		}
	}
	
	
	/* static helper */
	
	@SuppressWarnings("unchecked")
	public static void registerPattern(String id, String pattern, ParserContext parserContext) {
		if (!"".equals(id)) { // add to sitemap only if it has an id
			BeanDefinitionRegistry registry = parserContext.getRegistry();
			BeanDefinition sitemapBean;
			try {
				sitemapBean = registry.getBeanDefinition(BEAN_ID);
			} catch (NoSuchBeanDefinitionException e) {
				sitemapBean = BeanDefinitionBuilder.genericBeanDefinition(Sitemap.class)
					.addPropertyValue(BEAN_PROP_ENTRIES, new HashMap<String, String>())
					.getBeanDefinition();
				registry.registerBeanDefinition(BEAN_ID, sitemapBean);
			}
			((Map<String, String>) sitemapBean.getPropertyValues().getPropertyValue(BEAN_PROP_ENTRIES).getValue()).put(id, pattern);
		}
	}
}
