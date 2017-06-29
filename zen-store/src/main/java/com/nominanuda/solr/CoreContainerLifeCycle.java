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
	private Map<String, SolrCore> coresByDataDir = new HashMap<String, SolrCore>();
	private Map<String, List<SolrEndpoint>> coreContainersBySolrHome = new HashMap<String, List<SolrEndpoint>>();
	private Map<SolrCore, SolrEndpoint> endpointsByCore = new HashMap<SolrCore, SolrEndpoint>();

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
		//no exception maybe sorlrhome already used but different data dirs
		List<SolrEndpoint> l = coreContainersBySolrHome.get(solrHome);
		if(l == null) {
			l = new LinkedList<SolrEndpoint>();
			coreContainersBySolrHome.put(solrHome, l);
		}
		SolrEndpoint sep = new SolrEndpoint(cores);
		l.add(sep);
		for(String cn : cores.getAllCoreNames()) {
			SolrCore cr = cores.getCore(cn);
			String dataDir = cr.getDataDir();
			coresByDataDir.put(dataDir, cr);
			endpointsByCore.put(cr, sep);
		}
		return sep;
	}

	public void shutdown(CoreContainer cc) {
		cc.shutdown();
		for(SolrCore sc : cc.getCores()) {
			coresByDataDir.remove(sc.getDataDir());
			coreContainersBySolrHome.remove(cc.getSolrHome());
			endpointsByCore.remove(sc);
		}
	}

	public List<SolrEndpoint> findEndPointsBySolrHome(String solrHome) {
		List<SolrEndpoint> l = coreContainersBySolrHome.get(solrHome);
		return l == null ? new LinkedList<SolrEndpoint>() : l;
	}

	public @Nullable SolrEndpoint findEndpointByDataDir(String dataDir) {
		SolrCore c = coresByDataDir.get(dataDir);
		return c == null? null : endpointsByCore.get(c);
	}
}
