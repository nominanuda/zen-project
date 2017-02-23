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

import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.charset.Charset;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class MimeHelper implements HttpProtocol {
	private FileNameMap fileNameMap = URLConnection.getFileNameMap();

	public boolean isMediaTypeWwwFormURLEncoded(String mediaType) {
		return mediaType.trim().startsWith(CT_WWW_FORM_URLENCODED);
	}
	public String extractCharsetNameFromContentType(String contentType) {
		Charset cs = extractCharsetFromContentType(contentType);
		return cs == null ? null : cs.name();
	}

	public Charset extractCharsetFromContentType(String contentType) {
		String[] bits = contentType.split("=");
		if (bits.length != 2) {
			return null;
		}
		String cs = bits[1].trim();
		return Charset.forName(cs);
	}

	public boolean isContentTypeXML(String contentType) {
		return contentType.contains("xml");
	}

	public @Nullable String guessContentTypeFromPath(String path) {
		if (path.endsWith(".atom.xml")) {
			return CT_ATOM;
		} else if (path.endsWith(".xml")) {
			return CT_TEXT_XML;
		} else if (path.endsWith(".js")) {
			return CT_TEXT_JAVASCRIPT;
		} else if (path.endsWith(".css")) {
			return CT_TEXT_CSS;
		} else if (path.endsWith(".xsl")) {
			return CT_TEXT_XML;
		} else if (path.endsWith(".html")) {
			return CT_TEXT_HTML;
		} else {
			String mimeType = fileNameMap.getContentTypeFor(path);
			return mimeType;
		}
	}
}
