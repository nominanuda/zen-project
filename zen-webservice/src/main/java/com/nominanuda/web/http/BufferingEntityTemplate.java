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
