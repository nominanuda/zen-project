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
package com.nominanuda.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.nominanuda.store.api.KeyValueStore;
import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.common.Ex.NoException;
import com.nominanuda.zen.stereotype.Disposable;
import com.nominanuda.zen.stereotype.Initializable;

public class PropertyFileKeyValueStore implements KeyValueStore<String, String, NoException>, Disposable, Initializable {
	private Properties p;
	private String filePath;

	public PropertyFileKeyValueStore(String file) {
		setFile(file);
	}

	public PropertyFileKeyValueStore() {
	}

	@Override
	public void put(String k, String v) {
		p.setProperty(k, v);
	}

	@Override
	public String get(String k) {
		return p.getProperty(k);
	}

	@Override
	public boolean exists(String k) {
		return get(k) != null;
	}

	public synchronized void sync() throws IOException {
		File f = new File(filePath);
		if(! f.exists()) {
			f.createNewFile();
		}
		Check.illegalargument.assertTrue(f.isFile());
		FileOutputStream fos = new FileOutputStream(f);
		p.store(fos, null);
		fos.flush();
		fos.close();
	}

	@Override
	public void dispose() throws IOException {
		sync();
	}

	@Override
	public void init() throws IOException {
		File f = new File(Check.notNull(filePath));
		p = new Properties();
		if(f.exists()) {
			Check.illegalargument.assertTrue(f.isFile());
			FileInputStream fis = new FileInputStream(f);
			p.load(fis);
			fis.close();
		}
	}

	public void setFile(String path) {
		this.filePath = path;
	}
}
