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

import static com.nominanuda.web.http.HttpCoreHelper.HTTP;
import static com.nominanuda.zen.common.Check.illegalargument;

import java.io.CharArrayWriter;
import java.io.StringReader;

import javax.annotation.Nullable;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.xml.sax.InputSource;

import com.nominanuda.urispec.URISpec;
import com.nominanuda.web.http.Http500Exception;
import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.web.mvc.ObjURISpec;
import com.nominanuda.web.mvc.WebService;
import com.nominanuda.zen.common.InstanceFactory;
import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.xml.SAXPipeline;
import com.nominanuda.zen.xml.obj.JsonXmlReader;
import com.nominanuda.zen.xml.obj.SimpleJsonXmlTransformer;


public abstract class DataImportHandlerWs implements WebService, HttpProtocol {
	private static final String FAKE_ID = "foobar";
	private static final DateTimeFormatter GMT_SOLR_FMT = DateTimeFormat
			.forPattern("yyyy-MM-dd' 'HH:mm:ss").withZone(DateTimeZone.UTC);
	private static final DateTimeFormatter LOCAL_SOLR_FMT = DateTimeFormat
			.forPattern("yyyy-MM-dd' 'HH:mm:ss");
	//compulsory conf
	private URISpec<Obj> uriSpec;
	private String nextUrlPrefix;
	//defaulted conf
	private int rows = 100;
	private String idField = "id";
	private String startUrlParam = "start";
	private String fullImportUrlParam = "fullImport";
	private String sinceUrlParam = "since";
	private String typeUrlParam = "type";
	private String jsonField = null;//"__RAWJSON__";
	private String resultsTag = "results";
	private boolean solrTimestampInLocalTime = true;
	private final SAXPipeline pipe = new SAXPipeline()
		.add(new InstanceFactory<TransformerHandler>(
				new SimpleJsonXmlTransformer("root")))
		.complete();

	protected String computeNextUrl(Obj cmd, Integer nextStart) {
		Obj params = cmd.copy();
		params.put(startUrlParam, nextStart.toString());
		return nextUrlPrefix + uriSpec.template(params);
	}

	protected abstract Arr computeDeleted(Obj cmd, String type_,
			long since, int start, int rows);

	protected abstract Arr computeAddedOrModified(Obj cmd, String type_,
			long since, int start, int rows);

	private long decodeSinceDate(@Nullable String sinceStr) {
		if (sinceStr == null) {
			return 0;
		} else {
			long dt = solrTimestampInLocalTime
				? LOCAL_SOLR_FMT.parseDateTime(sinceStr).getMillis()
				: GMT_SOLR_FMT.parseDateTime(sinceStr).getMillis();
			return dt;
		}
	}

	public HttpResponse handle(HttpRequest request) throws Exception {
		try {
			Obj cmd = illegalargument.assertNotNull(uriSpec
					.match(request.getRequestLine().getUri()));
			String type_ = (String) cmd.getStrict(typeUrlParam);
			long since = decodeSinceDate(cmd.getStr(sinceUrlParam));
			boolean fullImport = Boolean.valueOf(cmd.getStr(fullImportUrlParam));
			String startParam = cmd.getStr(startUrlParam);
			int start = startParam == null ? 0 : Integer.valueOf(startParam);
			if (fullImport) {
				since = 0;
			}
			Arr resultsArray = computeAddedOrModified(cmd, type_, since, start, rows);
			if(jsonField != null) {
				for (Object record : resultsArray) {
					Obj o = (Obj) record;
					if(!o.exists(jsonField)) {
						o.put(jsonField, o.toString());
					}
				}
			}
			int resLen = resultsArray.len();
			Arr deletedEntities = computeDeleted(cmd, type_, since, start, rows);
			for (Object o : deletedEntities) {
				Obj obj = Obj.make();
				obj.put(idField, FAKE_ID);
				obj.put("deleteDocById", o.toString());
				resultsArray.add(obj);
			}
			if (resLen >= rows || deletedEntities.len() >= rows) {
				Integer nextStart = start + rows;
				Obj hasMore = resultsArray.addObj();
				hasMore.put(idField, "1");
				hasMore.put("hasMore", true);
				hasMore.put("nextUrl", computeNextUrl(cmd, nextStart));
			}
			String message = toXml(Obj.make(resultsTag, resultsArray));
			HttpResponse resp = HTTP.createBasicResponse(200, message,
					CT_APPLICATION_XML_CS_UTF8);
			return resp;
		} catch (Exception e) {
			throw new Http500Exception(e);
		}
	}

	private String toXml(Obj o) {
		CharArrayWriter w = new CharArrayWriter();
		pipe.build(new SAXSource(new JsonXmlReader(), new InputSource(
						new StringReader(o.toString()))), new StreamResult(w))
				.run();
		return new String(w.toCharArray());
	}

	public void setUriSpec(String uriSpec) {
		this.uriSpec = new ObjURISpec(uriSpec);
	}

	public void setNextUrlPrefix(String nextUrlPrefix) {
		this.nextUrlPrefix = nextUrlPrefix;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public void setIdField(String idField) {
		this.idField = idField;
	}

	public void setStartUrlParam(String startUrlParam) {
		this.startUrlParam = startUrlParam;
	}

	public void setFullImportUrlParam(String fullImportUrlParam) {
		this.fullImportUrlParam = fullImportUrlParam;
	}

	public void setSinceUrlParam(String sinceUrlParam) {
		this.sinceUrlParam = sinceUrlParam;
	}

	public void setTypeUrlParam(String typeUrlParam) {
		this.typeUrlParam = typeUrlParam;
	}

	public void setJsonField(String jsonField) {
		this.jsonField = jsonField;
	}

	public void setResultsTag(String resultsTag) {
		this.resultsTag = resultsTag;
	}

	public void setSolrTimestampInLocalTime(boolean solrTimestampInLocalTime) {
		this.solrTimestampInLocalTime = solrTimestampInLocalTime;
	}
}
