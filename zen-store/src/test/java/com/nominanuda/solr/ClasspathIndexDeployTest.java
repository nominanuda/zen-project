package com.nominanuda.solr;

import static com.nominanuda.zen.oio.OioUtils.IO;

import java.io.File;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.eclipse.jetty.util.IO;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.nominanuda.zen.maven.FailSafeTest;
import com.nominanuda.zen.oio.OioUtils;


@Category(FailSafeTest.class)
public class ClasspathIndexDeployTest {

	@Test
	public void test() throws Exception {
		File schemaDir = IO.newTmpDir("lalla");
		IO.copyResourcesRecursively(getClass().getResource("solr"), schemaDir, true);

		SolrClientFactory scf = new SolrClientFactory();
		SolrClient sc = scf.getOrBootstrapByURL(schemaDir.getAbsolutePath()+"/solr"+"#testcoll");
		sc.query(new SolrQuery("*:*"));
		IO.deleteRecursive(schemaDir);
	}

}
