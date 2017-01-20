package com.nominanuda.solr;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;

public class SolrEndpoint {
	private final CoreContainer coreContainer;

	public SolrEndpoint(CoreContainer coreContainer) {
		this.coreContainer = coreContainer;
	}

	public EmbeddedSolrServer createEmbeddedSolrServer(String coreName) {
		return new EmbeddedSolrServer(coreContainer, coreName);
	}

	public String getSolrHome() {
		return coreContainer.getSolrHome();
	}

	public SolrCore getCoreByName(String coreName) {
		return coreContainer.getCore(coreName);
	}

	public CoreContainer getCoreContainer() {
		return coreContainer;
	}
}
