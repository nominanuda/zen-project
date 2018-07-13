package com.nominanuda.solr;

import static com.nominanuda.zen.seq.Seq.SEQ;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Nullable;

import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrCoreInitializationException;
import org.apache.solr.core.SolrResourceLoader;
import org.apache.solr.core.SolrXmlConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nominanuda.zen.common.Check;

public class CoreContainerLifeCycle {
	private final static Logger LOG = LoggerFactory.getLogger(CoreContainerLifeCycle.class);
	private Map<String, SolrEndpoint> endpointsByDataDir = new HashMap<>();
	private Map<String, List<SolrEndpoint>> endpointsBySolrHome = new HashMap<>();

	/**
	 * @param props At least solr.solr.home must be configured. Other properties are used for variable
	 * substitution in config files. (e.g. solr.data.dir)
	 * @return
	 */
	public SolrEndpoint bootstrap(Properties props) throws Exception {
		String solrHome = Check.illegalargument.assertNotNull((String)props.remove("solr.solr.home"), "missing solr.solr.home");
		SolrResourceLoader loader = new SolrResourceLoader(Paths.get(solrHome), getClass().getClassLoader(), props);

		CoreContainer cores = new CoreContainer(
			SolrXmlConfig.fromSolrHome(loader, loader.getInstancePath()),
			new Properties(),
			false); // asyncload
		cores.load();

		// no exception maybe solrhome already used but different data dirs
		List<SolrEndpoint> endpoints = SEQ.getList(endpointsBySolrHome, solrHome, LinkedList.class);
		SolrEndpoint endpoint = new SolrEndpoint(cores);
		endpoints.add(endpoint);
		for (String name : cores.getAllCoreNames()) {
			try {
				String dataDir = cores.getCore(name).getDataDir();
				endpointsByDataDir.put(dataDir, endpoint);
			} catch (SolrCoreInitializationException e) {
				// we allow some cores to stay uninitialized because they could be not needed (ex: testing cores,...)
				LOG.error("Solr core not initialized! Permitted to continue but a blocking exception will be thrown in case of usage", e);
			}
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
		return endpoints == null ? new LinkedList<>() : endpoints;
	}

	public @Nullable SolrEndpoint findEndpointByDataDir(String dataDir) {
		return endpointsByDataDir.get(dataDir);
	}
}
