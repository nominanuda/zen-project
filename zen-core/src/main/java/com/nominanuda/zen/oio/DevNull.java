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
package com.nominanuda.zen.oio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public abstract class DevNull {

	public static Writer asWriter() {
		return new DevNullWriter();
	}
	public static Reader asReader() {
		return new DevNullReader();
	}
	public static InputStream asInputStream() {
		return new DevNullInputStream();
	}
	public static OutputStream asOutputStream() {
		return new DevNullOutputStream();
	}
	
	private static class DevNullWriter extends Writer {
		@Override
		public void close() throws IOException {
		}
		@Override
		public void flush() throws IOException {
		}
		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
		}
		
	}
	private static class DevNullReader extends Reader {
		@Override
		public void close() throws IOException {
		}

		@Override
		public int read(char[] cbuf, int off, int len) throws IOException {
			return -1;
		}
	}
	private static class DevNullInputStream extends InputStream {
		@Override
		public int read() throws IOException {
			return -1;
		}
	}
	private static class DevNullOutputStream extends OutputStream {
		@Override
		public void write(int b) throws IOException {
		}
	}
}
