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

import static com.nominanuda.dataobject.DataStructHelper.STRUCT;
import static com.nominanuda.lang.Check.illegalargument;
import static com.nominanuda.web.http.HttpCoreHelper.HTTP;

import java.io.CharArrayWriter;
import java.io.StringReader;

import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.xml.sax.InputSource;

import com.nominanuda.code.Nullable;
import com.nominanuda.dataobject.DataArray;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.JsonXmlReader;
import com.nominanuda.dataobject.SimpleJsonXmlTransformer;
import com.nominanuda.lang.InstanceFactory;
import com.nominanuda.urispec.URISpec;
import com.nominanuda.web.http.Http500Exception;
import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.web.mvc.DataObjectURISpec;
import com.nominanuda.web.mvc.WebService;
import com.nominanuda.xml.SAXPipeline;


public abstract class DataImportHandlerWs implements WebService, HttpProtocol {
	private static final String FAKE_ID = "foobar";
	private static final DateTimeFormatter SOLR_FMT = DateTimeFormat
			.forPattern("yyyy-MM-dd' 'HH:mm:ss").withZone(DateTimeZone.UTC);
	//compulsory conf
	private URISpec<DataObject> uriSpec;
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

	private final SAXPipeline pipe = new SAXPipeline()
		.add(new InstanceFactory<TransformerHandler>(
				new SimpleJsonXmlTransformer("root")))
		.complete();

	protected String computeNextUrl(DataObject cmd, Integer nextStart) {
		DataObject params = STRUCT.clone(cmd);
		params.put(startUrlParam, nextStart.toString());
		return nextUrlPrefix + uriSpec.template(params);
	}

	protected abstract DataArray computeDeleted(DataObject cmd, String type_,
			long since, int start, int rows);

	protected abstract DataArray computeAddedOrModified(DataObject cmd, String type_,
			long since, int start, int rows);

	private long decodeSinceDate(@Nullable String sinceStr) {
		if (sinceStr == null) {
			return 0;
		} else {
			return SOLR_FMT.parseDateTime(sinceStr).getMillis();
		}
	}

	public HttpResponse handle(HttpRequest request) throws Exception {
		try {
			DataObject cmd = illegalargument.assertNotNull(uriSpec
					.match(request.getRequestLine().getUri()));
			String type_ = (String) cmd.getStrict(typeUrlParam);
			long since = decodeSinceDate(cmd.getString(sinceUrlParam));
			boolean fullImport = Boolean.valueOf(cmd.getString(fullImportUrlParam));
			String startParam = cmd.getString(startUrlParam);
			int start = startParam == null ? 0 : Integer.valueOf(startParam);
			if (fullImport) {
				since = 0;
			}
			DataArray resultsArray = computeAddedOrModified(cmd, type_, since, start, rows);
			if(jsonField != null) {
				for (Object record : resultsArray) {
					DataObject o = (DataObject) record;
					if(!o.exists(jsonField)) {
						o.put(jsonField, o.toString());
					}
				}
			}
			DataArray deletedEntities = computeDeleted(cmd, type_, since, start, rows);
			for (Object o : deletedEntities) {
				DataObject obj = STRUCT.newObject();
				obj.put(idField, FAKE_ID);
				obj.put("deleteDocById", o.toString());
				resultsArray.add(obj);
			}
			if (resultsArray.getLength() >= rows || deletedEntities.getLength() >= rows) {
				Integer nextStart = start + rows;
				DataObject hasMore = resultsArray.addNewObject();
				hasMore.put(idField, "1");
				hasMore.put("hasMore", true);
				hasMore.put("nextUrl", computeNextUrl(cmd, nextStart));
			}
			String message = toXml(STRUCT.newObject().with(resultsTag, resultsArray));
			HttpResponse resp = HTTP.createBasicResponse(200, message,
					CT_APPLICATION_XML_CS_UTF8);
			return resp;
		} catch (Exception e) {
			throw new Http500Exception(e);
		}
	}

	private String toXml(DataObject o) {
		CharArrayWriter w = new CharArrayWriter();
		pipe.build(new SAXSource(new JsonXmlReader(), new InputSource(
						new StringReader(o.toString()))), new StreamResult(w))
				.run();
		return new String(w.toCharArray());
	}

	public void setUriSpec(String uriSpec) {
		this.uriSpec = new DataObjectURISpec(uriSpec);
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
}
