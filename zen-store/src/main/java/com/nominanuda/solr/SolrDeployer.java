package com.nominanuda.solr;

import static com.nominanuda.zen.oio.OioUtils.IO;
import static com.nominanuda.zen.common.Check.notNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nominanuda.zen.stereotype.Initializable;

public class SolrDeployer implements Initializable {
	public static final String POLICY_OVERWRITE = "overwite-existing";
	public static final String POLICY_USE_EXISTING = "use-existing";
	public static final String POLICY_FORBID_EXISTING = "forbid-existing";
	private static final Logger log = LoggerFactory.getLogger(SolrDeployer.class);

	private URL sourceUrl;
	private File schemaDir;
	private String policy = POLICY_FORBID_EXISTING;

	@Override
	public void init() throws Exception {
		notNull(sourceUrl);
		String schemaDirPath = schemaDir.getAbsolutePath();
		log.info("attempting to deploy schema from {} to {} with policy:{}", 
				sourceUrl.toString(), schemaDirPath, policy);
		if(! schemaDir.exists()) {
			log.info("schema dir {} does not exits, trying to create", schemaDirPath);
			if(!schemaDir.mkdirs()) {
				log.error("unable to create schema dir {} ; bailing out", schemaDirPath);
				throw new IOException("unable to create schema dir "+schemaDirPath);
			}
		} else if(! schemaDir.isDirectory()) {
			log.error("schema dir {} is a file; bailing out", schemaDirPath);
			throw new IOException("unable to create schema dir "+schemaDirPath);
		} else {//existing directory
			if(IO.isEmptyDir(schemaDir)) {
				log.info("schema dir {} is empty directory, using it", schemaDirPath);
			} else if(POLICY_FORBID_EXISTING.equals(policy)) {
				log.error("policy:"+policy+" refusing to use non empty schema dir "+schemaDirPath);
				throw new IOException("policy:"+policy+"refusing to use non empty schema dir; bailing out "+schemaDirPath);
			} else if(POLICY_USE_EXISTING.equals(policy)) {
				log.info("policy:"+policy+" non empty schema dir "+schemaDirPath+" considered ok and already installed");
			} else if(POLICY_OVERWRITE.equals(policy)) {
				log.info("policy:"+policy+" erasing non empty schema dir "+schemaDirPath);
				IO.deleteRecursiveContent(schemaDir);
			} else {
				throw new IllegalStateException("unknown policy "+policy);
			}
		}
		IO.copyResourceContentRecursively(sourceUrl, schemaDir, true);
	}

	public void setSourceUrl(URL sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public void setSchemaDir(File schemaDir) {
		this.schemaDir = schemaDir;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}
}
