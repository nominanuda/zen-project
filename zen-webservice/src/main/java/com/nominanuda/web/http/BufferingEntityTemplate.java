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
package com.nominanuda.web.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ContentProducer;

public class BufferingEntityTemplate extends AbstractHttpEntity {
	private byte[] content;
	private boolean streaming;
	private ContentProducer contentProducer;

	public BufferingEntityTemplate(ContentProducer cp, boolean _streaming) throws IOException {
		contentProducer = cp;
		streaming =_streaming;
		if(!streaming) {
			produce();
		}
	}

	private void produce() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		contentProducer.writeTo(os);
		content = os.toByteArray();
	}

	public InputStream getContent() throws IOException, IllegalStateException {
		if(streaming) {
			throw new IllegalStateException("streaming entity does not support getContent()");
		} else {
			return new ByteArrayInputStream(content);
		}
	}

	public long getContentLength() {
		return (streaming) ? -1 : content.length;
	}

	public boolean isRepeatable() {
		return true;
	}

	public boolean isStreaming() {
		return streaming;
	}

	public void writeTo(OutputStream outstream) throws IOException {
		if(streaming) {
			contentProducer.writeTo(outstream);
		} else {
			outstream.write(content);
		}
	}

	@Override
	public void consumeContent() throws IOException,
			UnsupportedOperationException {
	}

}
