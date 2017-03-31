package com.nominanuda.zen.io;

import com.nominanuda.zen.stereotype.ScopedSingletonFactory;

/**
 * Created by azum on 22/03/17.
 */

public class Uris {
	public static final Uris URIS = ScopedSingletonFactory.getInstance().buildJvmSingleton(Uris.class);

	public String getLastPathSegment(String path) {
		String[] bits = path.split("/");
		return bits[bits.length - 1];
	}

	public String pathJoin(String... segments) {
		StringBuilder sb = new StringBuilder();
		boolean lastEndsWithSlash = false;
		boolean first = true;
		for (String s : segments) {
			if (s != null) {
				if (first) {
					sb.append(s);
					first = false;
				} else {
					if ((! lastEndsWithSlash) && (! s.startsWith("/"))) {
						sb.append("/");
						sb.append(s);
					} else if (lastEndsWithSlash && s.startsWith("/")) {
						sb.append(s.substring(1));
					} else {
						sb.append(s);
					}
				}
				lastEndsWithSlash = s.endsWith("/");
			}
		}
		return sb.toString();
	}

	public boolean isAbsolute(String url) {
		return (url.startsWith("http://") || url.startsWith("https://"));
	}
}
