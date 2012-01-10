package com.nominanuda.uri;

import java.net.MalformedURLException;
import java.net.URL;

public class DefaultURLResolver implements URLResolver {

	public URL url(String url) throws IllegalArgumentException {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
