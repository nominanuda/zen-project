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

import static com.nominanuda.zen.oio.OioUtils.IO;

import java.io.IOException;
import java.io.InputStream;

import com.nominanuda.zen.common.Tuple2;

public class FakeImageStore implements ImageStore {

	private byte[] legend;
	private String imagePath = "com/nominanuda/image/legend.jpg";

	public FakeImageStore() throws IOException {
		InputStream defaultImageStream = getClass().getResourceAsStream(imagePath);
		if (null != defaultImageStream) {
			legend = IO.readAndClose(defaultImageStream);
		}
	}

	public Tuple2<String, byte[]> get(String key, String preferredFormat) throws IOException {
		return new Tuple2<String, byte[]>("jpg", legend);
	}

	public void put(String key, String format, byte[] content) throws IOException {
	}

	public void remove(String key) throws IOException {
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
}
