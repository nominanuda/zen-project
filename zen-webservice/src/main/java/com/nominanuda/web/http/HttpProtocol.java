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
package com.nominanuda.web.http;

import static com.nominanuda.zen.seq.Seq.SEQ;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_METHOD_NOT_ALLOWED;
import static org.apache.http.HttpStatus.SC_MOVED_PERMANENTLY;
import static org.apache.http.HttpStatus.SC_MOVED_TEMPORARILY;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NOT_IMPLEMENTED;
import static org.apache.http.HttpStatus.SC_NOT_MODIFIED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_REQUEST_TIMEOUT;
import static org.apache.http.HttpStatus.SC_SERVICE_UNAVAILABLE;
import static org.apache.http.HttpStatus.SC_TEMPORARY_REDIRECT;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public interface HttpProtocol {
	String[] RFC2616_COMMON_METHODS = {"GET"};
	String[] RFC2616_ENTITY_ENC_METHODS = {"POST", "PUT"};
	String[] RFC2616_SPECIAL_METHODS = {"HEAD", "OPTIONS", "DELETE", "TRACE", "CONNECT"};

	String GET = "GET";
	String POST = "POST";
	String PUT = "PUT";
	String DELETE = "DELETE";
	String HEAD = "HEAD";
	String OPTIONS = "OPTIONS";

	String HDR_VIA = "Via";
	String HDR_DATE = "Date";
	String HDR_LOCATION = "Location";
	String HDR_CONTENT_ENCODING = "Content-Encoding";
	String HDR_CONTENT_TYPE = "Content-Type";
	String HDR_CONTENT_LENGTH = "Content-Length";
	String HDR_AUTHORIZATION = "Authorization";
	String HDR_WWW_AUTHENTICATE = "WWW-Authenticate";
	String HDR_CONNECTION = "Connection";
	String HDR_CLOSE = "close";
	String HDR_KEEP_ALIVE = "keep-alive";
	String HDR_HOST = "host";

	String UTF_8 = "UTF-8";
	Charset CS_UTF_8 = Charset.forName(UTF_8);
	String ISO_8859_1 = "ISO-8859-1";
	String ASCII = "ASCII";
	Charset CS_ASCII = Charset.forName(ASCII);
	String CT_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
	String CT_WWW_FORM_URLENCODED_CS_UTF8 = "application/x-www-form-urlencoded; charset=UTF-8";
	String CT_APPLICATION_JAVASCRIPT = "application/javascript";
	String CT_APPLICATION_JAVASCRIPT_CS_UTF8 = "application/javascript; charset=UTF-8";
	String CT_TEXT_JAVASCRIPT = "text/javascript";
	String CT_TEXT_JAVASCRIPT_CS_UTF8 = "text/javascript; charset=UTF-8";
	String CT_TEXT_CSS = "text/css";
	String CT_TEXT_XML_CS_UTF8 = "text/xml; charset=UTF-8";
	String CT_TEXT_XML = "text/xml";
	String CT_APPLICATION_XML_CS_UTF8 = "application/xml; charset=UTF-8";
	String CT_APPLICATION_XML = "application/xml";
	String CT_TEXT_HTML_CS_UTF8 = "text/html; charset=UTF-8";
	String CT_TEXT_HTML = "text/html";
	String CT_TEXT_PLAIN_CS_UTF8 = "text/plain; charset=UTF-8";
	String CT_TEXT_PLAIN = "text/plain";
	String CT_ATOM_CS_UTF8 = "application/atom+xml; charset=UTF-8";
	String CT_ATOM = "application/atom+xml";
	String CT_APPLICATION_XQUERY_CS_UTF8 = "application/xquery; charset=UTF-8";
	String CT_APPLICATION_XQUERY = "application/xquery";
	String CT_APPLICATION_OCTET_STREAM = "application/octet-stream";
	String CT_APPLICATION_JSON_CS_UTF8 = "application/json; charset=UTF-8";
	String CT_APPLICATION_JSON = "application/json";
	String CT_IMAGE_JPEG = "image/jpeg";
	String CT_IMAGE_GIF = "image/gif";
	String CT_IMAGE_PNG = "image/png";
	String CT_IMAGE_X_ICON = "image/x-icon";
	String CT_APPLICATION_SOAP = "application/soap+xml";
	String CT_APPLICATION_SOAP_UTF8 = "application/soap+xml; charset=utf-8";

	Set<String> ONE_HOP_HEADERS = SEQ.hashSet(
"proxy-connection","connection","keep-alive","transfer-encoding","te","trailer","proxy-authorization","proxy-authenticate","upgrade","content-length"
	);

	Map<Integer, String> statusToReason = SEQ.buildMap(HashMap.class,
			SC_OK, "200 OK",
			SC_CREATED, "201 Created",
			SC_MOVED_PERMANENTLY, "301 Moved Permanently",
			SC_MOVED_TEMPORARILY, "302 Moved Temporarily",
			SC_NOT_MODIFIED, "304 Not Modified",
			SC_TEMPORARY_REDIRECT, "307 Temporary Redirect",
			SC_BAD_REQUEST, "400 Bad Request",
			SC_UNAUTHORIZED, "401 Unauthorized",
			SC_FORBIDDEN, "403 Forbidden",
			SC_NOT_FOUND, "404 Not Found",
			SC_METHOD_NOT_ALLOWED, "405 Method Not Allowed",
			SC_REQUEST_TIMEOUT, "408 Request Timeout",
			SC_INTERNAL_SERVER_ERROR, "500 Server Error",
			SC_NOT_IMPLEMENTED, "501 Not Implemented",
			SC_SERVICE_UNAVAILABLE, "503 Service Unavailable"
	);

	String HDR_X_FORWARDED_SERVER = "X-Forwarded-Server";
	String HDR_X_FORWARDED_HOST = "X-Forwarded-Host";
	String HDR_X_FORWARDED_PROTO = "X-Forwarded-Proto";
	String HDR_X_FORWARDED_FOR = "X-Forwarded-For";
	String HDR_X_REQUEST_PROCESSING_TIME = "X-Request-Processing-Time";
	String HDR_X_FORWARDED_URL = "X-Forwarded-URL";

	String HTMLNS = "http://www.w3.org/1999/xhtml";
}
