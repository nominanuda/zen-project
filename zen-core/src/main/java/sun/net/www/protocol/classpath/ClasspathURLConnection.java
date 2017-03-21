package sun.net.www.protocol.classpath;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.nominanuda.zen.jvmurl.ProxyURLConnection;

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
