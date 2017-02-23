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
package com.nominanuda.springsoy;

import static com.nominanuda.zen.common.Str.STR;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.SoyMsgBundleHandler;
import com.google.template.soy.msgs.SoyMsgException;
import com.google.template.soy.tofu.SoyTofu;
import com.google.template.soy.xliffmsgplugin.XliffMsgPlugin;
import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.obj.Obj;

public class SoySource {
	private final static Pattern FUNC_DECL = Pattern.compile("^\\s*([\\.\\w]+)\\s*=\\s*function\\s*\\(");
	private final static String NULL_LANG_KEY = "NO LANGUAGE";
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	private boolean cache = true;
	private String bundleUrlPrefix;
	private Resource templatesLocation;
	private ConcurrentHashMap<String, Map<String, String>> jsTemplatesCache = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, SoyTofu> tofuCache = new ConcurrentHashMap<>();
	private Set<String> functionNames = new HashSet<>();
	private boolean i18n = false;
	private String i18nXlfUrl;

	private Set<String> skipLangs = Collections.emptySet();
	
	public String getJsTemplate(String name, @Nullable String lang) throws IOException {
		lang = Check.ifNull(lang, NULL_LANG_KEY);
		if (jsTemplatesCache.get(lang) == null || !cache) {
			compile(lang);
		}
		Map<String, String> tpls = jsTemplatesCache.get(lang);
		String tpl = tpls.get(name);
		if (tpl == null) {
			throw new IOException("not found template named "+name);
		}
		return tpl;
	}
	
	public boolean hasFunction(String name, @Nullable String lang) throws IOException {
		lang = Check.ifNull(lang, NULL_LANG_KEY);
		if (jsTemplatesCache.get(lang) == null || !cache) {
			compile(lang);
		}
		return functionNames.contains(name);
	}

	public SoyTofu getSoyTofu(@Nullable String lang) throws IOException {
		lang = Check.ifNull(lang, NULL_LANG_KEY);
		if (tofuCache.get(lang) == null || !cache) {
			compile(lang);
		}
		return tofuCache.get(lang);
	}
	
	/**
	 * simple helper method to quickly render a template
	 * @param model
	 * @param view
	 * @return
	 * @throws IOException 
	 */
	public String render(SoyMapData model, String view) throws IOException {
		return getSoyTofu(null).newRenderer(view).setData(model).render();
	}
	public String render(Map<String, ?> model, String view) throws IOException {
		return getSoyTofu(null).newRenderer(view).setData(model).render();
	}
	public String render(Obj model, String view) throws IOException {
		return render(SoyHelper.model2soy(model), view);
	}
	
	protected void cumulate(SoyFileSet.Builder builder, List<String> jsTplNames) throws IOException {
		File[] templateFiles = templatesLocation.getFile().listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".soy");
			}
		});
		for (File templateFile: templateFiles) {
			builder.add(templateFile);
			jsTplNames.add(templateFile.getName());
		}
	}

	private void compile(String lang) throws IOException {
		final List<String> jsTplNames = new LinkedList<String>();
		final SoyFileSet.Builder builder = SoyFileSet.builder();
		cumulate(builder, jsTplNames);
		SoyFileSet soyFileSet = builder.build();
		List<String> jsTpls = soyFileSet.compileToJsSrc(new SoyJsSrcOptions(), getBundle(lang));
		for(String jsSrc : jsTpls) {
			BufferedReader br = new BufferedReader(new StringReader(jsSrc));
			String line;
			while ((line = br.readLine()) != null) {
				Matcher m = FUNC_DECL.matcher(line);
				if(m.find()) {
					functionNames.add(m.group(1));
				}
			}
		}
		Map<String, String> jsTplMap = new HashMap<String, String>();
		int len = jsTplNames.size();
		for(int i = 0; i < len; i++) {
			jsTplMap.put(jsTplNames.get(i), jsTpls.get(i));
		}
		jsTemplatesCache.put(lang, jsTplMap);
		tofuCache.put(lang, soyFileSet.compileToTofu());
	}

	public @Nullable SoyMsgBundle getBundle(String lang) throws SoyMsgException, IOException {
		if (!i18n || lang == null || skipLangs.contains(lang) || bundleUrlPrefix == null) {
			return null;
		} else {
			SoyMsgBundleHandler msgBundleHandler = new SoyMsgBundleHandler(new XliffMsgPlugin());
			return msgBundleHandler.createFromResource(new URL(STR.joinArgs(".", Check.ifNull(bundleUrlPrefix, i18nXlfUrl), lang, "xlf")));
		}
	}
	
	
	/* setters */

	public void setTemplatesLocation(Resource templatesLocation) {
		this.templatesLocation = templatesLocation;
	}
	
	public void setCache(boolean cache) {
		this.cache = cache;
	}
	
	public void setBundleUrlPrefix(String bundleUrlPrefix) {
		this.bundleUrlPrefix = bundleUrlPrefix;
	}

	/**
	 * 
	 * @param i18nXlfBaseUrl e.g. classpath:/com/foo/messages which for Locale en becomes classpath:/com/foo/messages.en.xlf
	 */
	public void setI18nXlfBaseUrl(String i18nXlfBaseUrl) {
		this.i18nXlfUrl = i18nXlfBaseUrl;
	}
	
	/**
	 * 
	 * @param skipLangs list of locales to skip (i.e. not to translate) comma separated
	 */
	public void setSkipLangs(String skipLangs) {
		this.skipLangs = new HashSet<String>(STR.splitAndTrim(skipLangs, ","));
	}
	/**
	 * @param i18n enable translation, default is false
	 */
	public void setI18n(boolean i18n) {
		this.i18n = i18n;
	}
}
