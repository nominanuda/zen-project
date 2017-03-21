/*
 * Copyright 2008-2016 the original author or authors.
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
package com.nominanuda.zen.jvmurl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import java.util.Map;

public class ProxyURLConnection extends URLConnection {
	protected URL proxyUrl;
	protected URLConnection proxyConnection;

	public ProxyURLConnection(final URL url) throws MalformedURLException,
			IOException {
		super(url);
		proxyUrl = generateProxyUrl(url);
		proxyConnection = openProxyConnection(proxyUrl);
	}

	protected URL generateProxyUrl(final URL url) throws MalformedURLException,
			IOException {
		return url;
	}

	protected URLConnection openProxyConnection(final URL url)
			throws IOException {
		return url.openConnection();
	}

	public void connect() throws IOException {
		proxyConnection.connect();
	}

	public URL getURL() {
		return proxyConnection.getURL();
	}

	public int getContentLength() {
		return proxyConnection.getContentLength();
	}

	public String getContentType() {
		return proxyConnection.getContentType();
	}

	public String getContentEncoding() {
		return proxyConnection.getContentEncoding();
	}

	public long getExpiration() {
		return proxyConnection.getExpiration();
	}

	public long getDate() {
		return proxyConnection.getDate();
	}

	public long getLastModified() {
		return proxyConnection.getLastModified();
	}

	public String getHeaderField(String name) {
		return proxyConnection.getHeaderField(name);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map getHeaderFields() {
		return proxyConnection.getHeaderFields();
	}

	public int getHeaderFieldInt(String name, int _default) {
		return proxyConnection.getHeaderFieldInt(name, _default);
	}

	public long getHeaderFieldDate(String name, long _default) {
		return proxyConnection.getHeaderFieldDate(name, _default);
	}

	public String getHeaderFieldKey(int n) {
		return proxyConnection.getHeaderFieldKey(n);
	}

	public String getHeaderField(int n) {
		return proxyConnection.getHeaderField(n);
	}

	public Object getContent() throws IOException {
		return proxyConnection.getContent();
	}

	@SuppressWarnings("rawtypes")
	public Object getContent(Class[] classes) throws IOException {
		return proxyConnection.getContent(classes);
	}

	public Permission getPermission() throws IOException {
		return proxyConnection.getPermission();
	}

	public InputStream getInputStream() throws IOException {
		return proxyConnection.getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		return proxyConnection.getOutputStream();
	}

	public String toString() {
		return super.toString() + "[" + proxyConnection + "]";
	}

	public void setDoInput(boolean doinput) {
		proxyConnection.setDoInput(doinput);
	}

	public boolean getDoInput() {
		return proxyConnection.getDoInput();
	}

	public void setDoOutput(boolean dooutput) {
		proxyConnection.setDoOutput(dooutput);
	}

	public boolean getDoOutput() {
		return proxyConnection.getDoOutput();
	}

	public void setAllowUserInteraction(boolean allowuserinteraction) {
		proxyConnection.setAllowUserInteraction(allowuserinteraction);
	}

	public boolean getAllowUserInteraction() {
		return proxyConnection.getAllowUserInteraction();
	}

	public void setUseCaches(boolean usecaches) {
		proxyConnection.setUseCaches(usecaches);
	}

	public boolean getUseCaches() {
		return proxyConnection.getUseCaches();
	}

	public void setIfModifiedSince(long ifmodifiedsince) {
		proxyConnection.setIfModifiedSince(ifmodifiedsince);
	}

	public long getIfModifiedSince() {
		return proxyConnection.getIfModifiedSince();
	}

	public boolean getDefaultUseCaches() {
		return proxyConnection.getDefaultUseCaches();
	}

	public void setDefaultUseCaches(boolean defaultusecaches) {
		proxyConnection.setDefaultUseCaches(defaultusecaches);
	}

	public void setRequestProperty(String key, String value) {
		proxyConnection.setRequestProperty(key, value);
	}

	public void addRequestProperty(String key, String value) {
		proxyConnection.addRequestProperty(key, value);
	}

	public String getRequestProperty(String key) {
		return proxyConnection.getRequestProperty(key);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map getRequestProperties() {
		return proxyConnection.getRequestProperties();
	}

}