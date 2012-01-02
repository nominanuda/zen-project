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
package com.nominanuda.io;

import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nominanuda.code.ThreadSafe;

@ThreadSafe
public class DataURLHelper {
	private static final String DATA_URL_REX = "^data:([\\w/-]+)?(;charset=[\"|']?[\\w/-]+[\"|']?)?(;base64)?,(.+)$";
	private static final Pattern DATA_URL_REX_PATTERN = Pattern
			.compile(DATA_URL_REX);

	public boolean isDataUrlBinary(String dataUrl)
			throws MalformedURLException {
		Matcher m = DATA_URL_REX_PATTERN.matcher(dataUrl);
		if (!m.find()) {
			throw new MalformedURLException();
		}
		return (m.start(3) > -1);
	}

	public boolean isDataUrl(String dataUrl) {
		Matcher m = DATA_URL_REX_PATTERN.matcher(dataUrl);
		return (m.matches());
	}

	public String getDataUrlCharset(String dataUrl)
			throws MalformedURLException {
		Matcher m = DATA_URL_REX_PATTERN.matcher(dataUrl);
		if (!m.find()) {
			throw new MalformedURLException();
		}
		String cs = m.start(2) > 0 ? dataUrl.substring(m.start(2), m.end(2))
				: null;
		if (cs == null) {
			return null;
		}
		Pattern p = Pattern.compile(";charset=[\"|']?([\\w/-]+)[\"|']?");
		Matcher m1 = p.matcher(cs);
		if (!m1.find()) {
			return null;
		}
		return cs.substring(m1.start(1), m1.end(1));
	}

	public String getDataUrlMimeType(String dataUrl)
			throws MalformedURLException {
		Matcher m = DATA_URL_REX_PATTERN.matcher(dataUrl);
		if (!m.find()) {
			throw new MalformedURLException();
		}
		return m.start(1) > 0 ? dataUrl.substring(m.start(1), m.end(1)) : null;
	}

	public String getDataUrlData(String dataUrl)
			throws MalformedURLException {
		Matcher m = DATA_URL_REX_PATTERN.matcher(dataUrl);
		if (!m.find()) {
			throw new MalformedURLException();
		}
		return m.start(4) > 0 ? dataUrl.substring(m.start(4)) : null;
	}
}
