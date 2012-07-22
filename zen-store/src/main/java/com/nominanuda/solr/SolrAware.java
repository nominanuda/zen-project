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
package com.nominanuda.solr;

import java.io.IOException;
import java.util.Collection;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.util.plugin.SolrCoreAware;


public class SolrAware extends SearchComponent implements SolrCoreAware {
	private SingletonSolrAware singletonSolrAware = SingletonSolrAware.getInstance();

	public static SolrAware getInstance() {
		return new SolrAware();
	}

	public void inform(SolrCore core) {
		singletonSolrAware.inform(core);
	}

	public Collection<String> getCoreNames() {
		return singletonSolrAware.getCoreNames();
	}

	public SolrCore getCoreByName(String coreName) {
		return singletonSolrAware.getCoreByName(coreName);
	}

	public EmbeddedSolrServer getEmbeddedSolrServerByCoreName(String coreName) {
		return singletonSolrAware.getEmbeddedSolrServerByCoreName(coreName);
	}

	public void prepare(ResponseBuilder rb) throws IOException {
		singletonSolrAware.prepare(rb);
	}

	public void process(ResponseBuilder rb) throws IOException {
		singletonSolrAware.process(rb);
	}

	public String getDescription() {
		return singletonSolrAware.getDescription();
	}

	public String getSource() {
		return singletonSolrAware.getSource();
	}

	public String getVersion() {
		return singletonSolrAware.getVersion();
	}
}
