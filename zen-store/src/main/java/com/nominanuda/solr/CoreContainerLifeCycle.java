package com.nominanuda.solr;

import static com.nominanuda.zen.common.Check.illegalargument;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Nullable;

import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.core.SolrXmlConfig;

public class CoreContainerLifeCycle {
	private Map<String, SolrEndpoint> endpointsByDataDir = new HashMap<>();
	private Map<String, List<SolrEndpoint>> endpointsBySolrHome = new HashMap<String, List<SolrEndpoint>>();

	/**
	 * @param props At least solr.solr.home must be configured. Other properties are used for variable
	 * substitution in config files. (e.g. solr.data.dir)
	 * @return
	 */
	public SolrEndpoint bootstrap(Properties props) throws Exception {
		String solrHome = illegalargument.assertNotNull((String)
			props.remove("solr.solr.home"), "missing solr.solr.home");
		SolrResourceLoader loader = new SolrResourceLoader(Paths.get(solrHome), getClass().getClassLoader(), props);

		CoreContainer cores = new CoreContainer(
			SolrXmlConfig.fromSolrHome(loader, loader.getInstancePath()), new Properties(), /*asyncLoad*/false);
		cores.load();

		//no exception maybe solrhome already used but different data dirs
		SolrEndpoint endpoint = new SolrEndpoint(cores);
		List<SolrEndpoint> endpoints = endpointsBySolrHome.get(solrHome);
		if (endpoints == null) {
			endpoints = new LinkedList<SolrEndpoint>();
			endpointsBySolrHome.put(solrHome, endpoints);
		}
		endpoints.add(endpoint);
		for (String name : cores.getAllCoreNames()) {
			String dataDir = cores.getCore(name).getDataDir();
			endpointsByDataDir.put(dataDir, endpoint);
		}
		return endpoint;
	}

	public void shutdown(CoreContainer cc) {
		cc.shutdown();
		endpointsBySolrHome.remove(cc.getSolrHome());
		for (SolrCore core : cc.getCores()) {
			endpointsByDataDir.remove(core.getDataDir());
		}
	}

	public List<SolrEndpoint> findEndpointsBySolrHome(String solrHome) {
		List<SolrEndpoint> endpoints = endpointsBySolrHome.get(solrHome);
		return endpoints == null ? new LinkedList<SolrEndpoint>() : endpoints;
	}

	public @Nullable SolrEndpoint findEndpointByDataDir(String dataDir) {
		return endpointsByDataDir.get(dataDir);
	}
}
