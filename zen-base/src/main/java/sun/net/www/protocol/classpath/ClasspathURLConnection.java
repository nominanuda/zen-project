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
package sun.net.www.protocol.classpath;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.nominanuda.uri.ProxyURLConnection;

public class ClasspathURLConnection extends ProxyURLConnection {

	public ClasspathURLConnection(final URL url) throws MalformedURLException,
			IOException {
		super(url);
	}

	protected URL generateProxyUrl(final URL url) throws MalformedURLException,
			IOException {
		String name = url.getHost();
		String file = url.getFile();
		if (file != null && !file.equals("")) {
			name += file;
		}

		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if(name.startsWith("/")) {
			name = name.substring(1);
		}
		URL target = cl.getResource(name);

		if (target == null) {
			cl = ClassLoader.getSystemClassLoader();
			target = cl.getResource(name);
		}

		if (target == null)
			throw new FileNotFoundException(
				"classpath resource not found: "+ name);
		return new URL(target.toExternalForm());
	}
}
