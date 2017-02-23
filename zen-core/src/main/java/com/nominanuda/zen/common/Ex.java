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
package com.nominanuda.zen.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.concurrent.ExecutionException;

import javax.annotation.concurrent.ThreadSafe;

import com.nominanuda.zen.stereotype.ScopedSingletonFactory;
import com.nominanuda.zen.stereotype.UncheckedExecutionException;

@ThreadSafe
public class Ex {
	public static final Ex EX = ScopedSingletonFactory.getInstance().buildJvmSingleton(Ex.class);

	public String toStackTrace(Exception e) {
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			PrintWriter writer = new PrintWriter(buf);
			e.printStackTrace(writer);
			writer.flush();
			return new String(buf.toByteArray(), Str.UTF8);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	public static final class NoException extends RuntimeException {
		private static final long serialVersionUID = 7813677042673120866L;
	}
	
	public UncheckedIOException uncheckedIO(Exception e) {
		if(! (e instanceof IOException)) {
			e = new IOException(e);
		}
		return new UncheckedIOException((IOException)e);
	}
	public UncheckedIOException uncheckedIO(String msg) {
		return new UncheckedIOException(new IOException(msg));
	}

	public UncheckedExecutionException uncheckedExecution(Exception e) {
		if(! (e instanceof ExecutionException)) {
			e = new ExecutionException(e);
		}
		return new UncheckedExecutionException((ExecutionException)e);
	}
	public UncheckedExecutionException uncheckedExecution(String msg) {
		return new UncheckedExecutionException(msg);
	}
	public Exception asException(Throwable t) {
		return t instanceof Exception
			? (Exception)t
			: new RuntimeException(t);
	}
}
