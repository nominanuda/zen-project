package com.nominanuda.solr;

import static com.nominanuda.zen.common.Check.illegalstate;
import static java.net.URLDecoder.decode;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

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
		if("http".equals(scheme)||"https".equals(scheme)) {
			SolrClient s = new HttpSolrClient.Builder(url).build();
			return s;
		} else if(scheme == null || "file".equals(scheme)) {
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
		} else {
			throw new IllegalArgumentException(
				"missing or wrong URI scheme in locator "+url);
		}
	}

	public static Properties splitQuery(String query) {
		Properties params = new Properties();
		String[] pairs = query.split("&");
		for (String pair : pairs) {
			int idx = pair.indexOf("=");
			try {
				params.put(
					decode(pair.substring(0, idx), "UTF-8"),
					decode(pair.substring(idx + 1), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return params;
	}
}
