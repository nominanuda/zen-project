package com.nominanuda.solr;

import static java.net.URLDecoder.decode;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;
import java.util.Properties;

import javax.annotation.Nullable;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import com.nominanuda.zen.common.Check;

//Doc solrHome {coreName}.solr.data.dir special case
public class SolrClientFactory {
	private final CoreContainerLifeCycle coreContainerLifeCycle;

	public SolrClientFactory() {
		this.coreContainerLifeCycle = new CoreContainerLifeCycle();
	}

	public SolrClientFactory(CoreContainerLifeCycle sharedContainerLifeCycle) {
		this.coreContainerLifeCycle = sharedContainerLifeCycle;
	}

	public SolrClient getOrBootstrapByURL(String url) throws Exception {
		URI loc = URI.create(url);
		String scheme = loc.getScheme();
		
		if ("http".equals(scheme) || "https".equals(scheme)) {
			return new HttpSolrClient.Builder(url).build();
			
		} else if (scheme == null || "file".equals(scheme)) {
			final String solrHome = loc.getPath();
			final String coreName = loc.getFragment();
			final Properties props = splitQuery(loc.getQuery());
			props.put("solr.solr.home", solrHome);

			List<SolrEndpoint> endpointsBySolrHome = coreContainerLifeCycle.findEndpointsBySolrHome(solrHome);
			switch (endpointsBySolrHome.size()) {
			case 0:
				break; // go to bootstrap
				
			case 1:
				SolrEndpoint endpoint = endpointsBySolrHome.get(0);
				return endpoint.createEmbeddedSolrServer(coreName);
			
			default: // how do we get here? endpointsBySolrHome.size() is never bigger than 1
				final String dataDir = props.getProperty(coreName + ".solr.data.dir");
				Check.illegalstate.assertNotNull(dataDir, "ambiguous solr.solr.home " + solrHome);
				SolrEndpoint endpointByDataDir = coreContainerLifeCycle.findEndpointByDataDir(dataDir);
				if (endpointByDataDir != null) {
					Check.illegalstate.assertEquals(solrHome, endpointByDataDir.getSolrHome());
					Check.illegalstate.assertEquals(dataDir, endpointByDataDir.getCoreByName(coreName).getDataDir());
					return endpointByDataDir.createEmbeddedSolrServer(coreName);
				}
				// go to bootstrap
			}
			
			SolrEndpoint sep = coreContainerLifeCycle.bootstrap(props);
			return sep.createEmbeddedSolrServer(coreName);
			
			
			/*
			 * Old code version for reference (how do we get to lcc.size() > 1?)
			 * 
			String solrHome = loc.getPath();
			String coreName = loc.getFragment();
			String q = loc.getQuery();
			Properties props = q == null ? new Properties() : splitQuery(q);
			props.put("solr.solr.home", solrHome);
			List<SolrEndpoint> lcc = coreContainerLifeCycle.findEndPointsBySolrHome(solrHome);
			if(lcc.isEmpty()) {
				SolrEndpoint sep = coreContainerLifeCycle.bootstrap(props);
				EmbeddedSolrServer ess = sep.createEmbeddedSolrServer(coreName);
				return ess;
			} else if(lcc.size() > 1) {
				String dDir = (String)props.getProperty(coreName + ".solr.data.dir");
				if(dDir != null) {
					SolrEndpoint sep = coreContainerLifeCycle.findEndpointByDataDir(dDir);
					if(sep != null) {
						illegalstate.assertEquals(solrHome, sep.getSolrHome());
						illegalstate.assertEquals(dDir, sep.getCoreByName(coreName).getDataDir());
						EmbeddedSolrServer ess = sep.createEmbeddedSolrServer(coreName);
						return ess;
					} else {
						//try to bootstrap
						try {
							//calls core.open() and thus sets ref-count to 1
							sep = coreContainerLifeCycle.bootstrap(props);
							EmbeddedSolrServer ess = sep.createEmbeddedSolrServer(coreName);
							return ess;
						} catch(Exception e) {
							throw new IllegalArgumentException(e);
						}
					}
				} else {
					throw new IllegalStateException("ambiguous solr.solr.home " + solrHome);
				}
			} else {
				SolrEndpoint sep = lcc.get(0);
				EmbeddedSolrServer ess = sep.createEmbeddedSolrServer(coreName);
				return ess;
			}
			*/
		}
		
		throw new IllegalArgumentException("missing or wrong URI scheme in locator " + url);
	}
	

	private static Properties splitQuery(@Nullable String query) {
		Properties props = new Properties();
		if (query != null) {
			String[] pairs = query.split("&");
			for (String pair : pairs) {
				int idx = pair.indexOf("=");
				try {
					props.put(
						decode(pair.substring(0, idx), "UTF-8"),
						decode(pair.substring(idx + 1), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					throw new IllegalArgumentException(e);
				}
			}
		}
		return props;
	}
}
