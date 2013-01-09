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

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.io.Resource;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.tofu.SoyTofu;
import com.nominanuda.code.Nullable;
import com.nominanuda.lang.Check;

public class SoySource {
	private final static Pattern FUNC_DECL = Pattern.compile("^\\s*([\\.\\w]+)\\s*=\\s*function\\s*\\(");
	private final static String NULL_LANG_KEY = "NO LANGUAGE";
	private boolean cache = true;
	private Resource templatesLocation;
	private ConcurrentHashMap<String, Map<String, String>> jsTemplatesCache = new ConcurrentHashMap<String, Map<String,String>>();
	private ConcurrentHashMap<String, SoyTofu> tofuCache = new ConcurrentHashMap<String, SoyTofu>();
	private Set<String> functionNames = new HashSet<String>();


	public String getJsTemplate(String name, @Nullable String lang) throws IOException {
		lang = Check.ifNull(lang, NULL_LANG_KEY);
		if(jsTemplatesCache.get(lang) == null || !cache) {
			compile(lang);
		}
		Map<String, String> tpls = jsTemplatesCache.get(lang);
		String tpl = tpls.get(name);
		if(tpl == null) {
			throw new IOException("not found template named "+name);
		}
		return tpl;
	}
	
	public boolean hasFunction(String name, @Nullable String lang) throws IOException {
		lang = Check.ifNull(lang, NULL_LANG_KEY);
		if(jsTemplatesCache.get(lang) == null || !cache) {
			compile(lang);
		}
		return functionNames.contains(name);
	}

	public SoyTofu getSoyTofu(@Nullable String lang) throws IOException {
		lang = Check.ifNull(lang, NULL_LANG_KEY);
		if(tofuCache.get(lang) == null || !cache) {
			compile(lang);
		}
		return tofuCache.get(lang);
	}

	private void compile(String lang) throws IOException {
		File[] templateFiles = templatesLocation.getFile().listFiles(
			new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".soy");
				}
			});
		SoyFileSet.Builder builder = new SoyFileSet.Builder();
		List<String> jsTplNames = new LinkedList<String>();
		for (File templateFile: templateFiles) {
			builder.add(templateFile);
			jsTplNames.add(templateFile.getName());
		}
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

	private @Nullable SoyMsgBundle getBundle(String lang) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTemplatesLocation(Resource templatesLocation) {
		this.templatesLocation = templatesLocation;
	}

	public void setCache(boolean cache) {
		this.cache = cache;
	}

}
