/*
 * Copyright 2008-2018 the original author or authors.
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
package com.nominanuda.image;


import static com.nominanuda.zen.io.Uris.URIS;
import static com.nominanuda.zen.oio.OioUtils.IO;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.common.Tuple2;


public class FsImageStore implements ImageStore {
	private final static String[] FORMATS = { "jpg", "jpeg", "png" };
	
	protected String path;
	private boolean fsPath = false;


	@Override
	public Tuple2<String, byte[]> get(String name, String preferredFormat) throws IOException {
		int i = 0;
		String format = preferredFormat;
		while (format != null) {
			String key = key(name, format);
			try {
				if (fsPath) {
					File f = file(key, false);
					if (f.exists()) {
						return new Tuple2<String, byte[]>(format, IO.readAndClose(new FileInputStream(f)));
					}
				} else {
					URL url = new URL(URIS.pathJoin(path, key));
					return new Tuple2<String, byte[]>(format, IO.readAndClose(url.openStream()));
				}
			} catch (IOException e) {
				// do nothing we'll try another format
			}
			format = (i < FORMATS.length ? FORMATS[i++] : null);
		}
		return null;
	}

	@Override
	public void put(String name, String format, byte[] content) throws IOException {
		IO.writeToFile(file(key(name, format), true), new ByteArrayInputStream(content));
	}
	
	@Override
	public void remove(String key) {
		File f = file(key, false);
		if (f.exists()) {
			f.delete();
		}
	}

	protected File file(String key, boolean allowCreate) {
		File dir = new File(path);
		if (allowCreate && !dir.exists()) {
			Check.illegalstate.assertTrue(dir.mkdirs());
		}
		return new File(dir, key);
	}
	
	private String key(String name, String format) {
		return name + "." + format;
	}
	
	
	/* setters */
	
	public void setPath(String path) {
		this.path = path;
		if (!path.contains("//")) {
			fsPath = true;
			File dir = new File(path);
			if (!dir.exists()) {
				Check.illegalstate.assertTrue(dir.mkdirs(), "cannot create directory " + path);
			} else {
				Check.illegalargument.assertTrue(dir.isDirectory());
				Check.illegalargument.assertTrue(dir.canWrite());
			}
		}
	}
}
