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
package sun.net.www.protocol.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.UnknownServiceException;
import java.nio.charset.Charset;
import java.security.AllPermission;
import java.security.Permission;
import java.util.List;
import java.util.Map;

import com.nominanuda.codec.Base64Codec;
import com.nominanuda.io.DataURLHelper;

public class DataURLConnection extends URLConnection {
	private final static DataURLHelper dataUrlHelper = new DataURLHelper();
	private byte[] dataAsBinary;
	private URL url;
	private boolean isBase64;
	private Charset charset;
	private String mimeType;
	private static final Base64Codec base64 = new Base64Codec();

	public DataURLConnection(final URL _url) throws MalformedURLException,
			IOException {
		super(_url);
		String dataUrl = _url.toString();//get_url.getFile();
		if(! dataUrlHelper.isDataUrl(dataUrl)) {
			throw new MalformedURLException();
		}
		isBase64 = dataUrlHelper.isDataUrlBinary(dataUrl);
		String cs = dataUrlHelper.getDataUrlCharset(dataUrl);
		if(cs != null) {
			try {
				charset = Charset.forName(cs);
			} catch(Exception e) {}
		}
		mimeType = dataUrlHelper.getDataUrlMimeType(dataUrl);
		String urlData = dataUrlHelper.getDataUrlData(dataUrl);
		if(isBase64) {
			dataAsBinary = base64.decodeNoGzip(urlData);
		} else {
			String chst = charset == null ? "UTF-8" : charset.displayName();
			dataAsBinary = URLDecoder.decode(urlData, chst).getBytes();
		}
		this.url = _url;
	}
	@Override
	public void addRequestProperty(String key, String value) {
	}

	@Override
	public boolean getAllowUserInteraction() {
		return false;
	}

	@Override
	public int getConnectTimeout() {
		return 0;
	}

	@Override
	public Object getContent() throws IOException {
		return super.getContent();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getContent(Class[] classes) throws IOException {
		return super.getContent(classes);
	}

	@Override
	public String getContentEncoding() {
		return null;
	}

	@Override
	public int getContentLength() {
		return dataAsBinary.length;
	}

	@Override
	public String getContentType() {
		return mimeType;
	}

	@Override
	public long getDate() {
		return 0;
	}

	@Override
	public boolean getDefaultUseCaches() {
		return false;
	}

	@Override
	public boolean getDoInput() {
		return true;
	}

	@Override
	public boolean getDoOutput() {
		return false;
	}

	@Override
	public long getExpiration() {
		return 0;
	}

	@Override
	public String getHeaderField(int n) {
		return null;
	}

	@Override
	public String getHeaderField(String name) {
		if(name.equalsIgnoreCase("content-type")
		|| name.equalsIgnoreCase("contenttype")
		|| name.equalsIgnoreCase("mime-type")
		|| name.equalsIgnoreCase("mimetype")) {
			return getContentType();
		}
		return null;
	}

	@Override
	public long getHeaderFieldDate(String name, long Default) {
		return 0;
	}

	@Override
	public int getHeaderFieldInt(String name, int Default) {
		return 0;
	}

	@Override
	public String getHeaderFieldKey(int n) {
		return null;
	}

	@Override
	public Map<String, List<String>> getHeaderFields() {
		return null;
	}

	@Override
	public long getIfModifiedSince() {
		return 0;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(dataAsBinary);
	}


	@Override
	public long getLastModified() {
		return 0;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new UnknownServiceException();
	}

	@Override
	public Permission getPermission() throws IOException {
		return new AllPermission();
	}

	@Override
	public int getReadTimeout() {
		return 0;
	}

	@Override
	public Map<String, List<String>> getRequestProperties() {
		return null;
	}

	@Override
	public String getRequestProperty(String key) {
		return null;
	}

	@Override
	public URL getURL() {
		return url;
	}

	@Override
	public boolean getUseCaches() {
		return false;
	}

	@Override
	public void setAllowUserInteraction(boolean allowuserinteraction) {
	}

	@Override
	public void setConnectTimeout(int timeout) {
	}

	@Override
	public void setDefaultUseCaches(boolean defaultusecaches) {
	}

	@Override
	public void setDoInput(boolean doinput) {
	}

	@Override
	public void setDoOutput(boolean dooutput) {
	}

	@Override
	public void setIfModifiedSince(long ifmodifiedsince) {
	}

	@Override
	public void setReadTimeout(int timeout) {
	}

	@Override
	public void setRequestProperty(String key, String value) {
	}

	@Override
	public void setUseCaches(boolean usecaches) {
	}

	@Override
	public String toString() {
		String string = url.getFile();
		return "URL[data:"+
			((string.length() > 10 ) ? string.substring(0, 10) + "..." : string)
			+"]";
	}

	@Override
	public void connect() throws IOException {
	}
}
