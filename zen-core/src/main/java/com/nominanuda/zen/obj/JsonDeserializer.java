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
package com.nominanuda.zen.obj;

import static com.nominanuda.zen.common.Ex.EX;
import static com.nominanuda.zen.common.Str.UTF8;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;

import javax.annotation.concurrent.ThreadSafe;

import com.nominanuda.zen.common.Str;
import com.nominanuda.zen.oio.ReaderInputStream;
import com.nominanuda.zen.reactivestreams.SubscriptionImpl;

@ThreadSafe
public class JsonDeserializer {
	public static final JsonDeserializer JSON_DESERIALIZER = new JsonDeserializer();
	private final int readBufSize = 8192;

	public Object deserialize(InputStream is) throws UncheckedIOException {
		AnyNioDeserializer d = new AnyNioDeserializer();
		d.onSubscribe(new SubscriptionImpl());
		byte[] buf = new byte[readBufSize];
		int nread = 0;
		try {
			while((nread = is.read(buf)) >= 0) {
				if(nread == 0) {
					continue;
				} else {
					d.onNext(ByteBuffer.wrap(Arrays.copyOf(buf, nread)));
				}
			}
			d.onComplete();
		} catch (IOException e) {
			d.onError(e);
			throw EX.uncheckedIO(e);
		}
		return d.get().toJavaObjModel();
	}

	public Object deserialize(byte[] barr) {
		return deserialize(ByteBuffer.wrap(barr));
	}

	public Object deserialize(ByteBuffer bb) {
		return deserializeToAny(bb).toJavaObjModel();
	}

	public Any deserializeToAny(ByteBuffer bb) {
		AnyNioDeserializer d = new AnyNioDeserializer();
		d.onSubscribe(new SubscriptionImpl());
		d.onNext(bb);
		d.onComplete();
		return d.get();
	}

	public Object deserializeVal(ByteBuffer bb) {
		AnyNioDeserializer d = new AnyNioDeserializer();
		d.onSubscribe(new SubscriptionImpl());
		d.onNext(bb);
		d.onComplete();
		return d.get().toJavaObjModel();
	}

	public Object deserialize(Reader r) {
		return deserialize(new ReaderInputStream(r, UTF8));
	}

	public Object deserialize(String s) {
		return deserialize(s.getBytes(UTF8));
	}

	//TODO faster and less new()
	public Number deserializeNumber(String s) {
		if(s.indexOf('.') >= 0 || s.indexOf('e') >= 0 || s.indexOf('E') >= 0) {
			return Double.valueOf(s);
		} else {
			Long l = Long.valueOf(s);
			if(l > Integer.MAX_VALUE || l <Integer.MIN_VALUE) {
				return l;
			} else {
				return new Integer(l.intValue());
			}
		}
	}
	public String decodeString(ByteBuffer nioBuffer) throws IllegalArgumentException {
		CharBuffer cbuf = Str.UTF8.decode(nioBuffer);
		int len = cbuf.length();
		int widx = 0;
		for(int ridx = 1; ridx < len - 1; ridx++) {
			char c = cbuf.get(ridx);
			if('\\' == c) {
				if(ridx > len - 2) {
					throw new IllegalArgumentException("spare \\ at the end of stream");
				}
				ridx++;
				char c1 = cbuf.get(ridx);
				switch (c1) {
				case 'b':
					cbuf.put(widx++, '\b');
					break;
				case 't':
					cbuf.put(widx++, '\t');
					break;
				case 'n':
					cbuf.put(widx++, '\n');
					break;
				case 'f':
					cbuf.put(widx++, '\f');
					break;
				case 'r':
					cbuf.put(widx++, '\r');
					break;
				case '\'':
					cbuf.put(widx++, '\'');
					break;
				case '\"':
					cbuf.put(widx++, '"');
					break;
				case '\\':
					cbuf.put(widx++, '\\');
					break;
				case '/':
					cbuf.put(widx++, '/');
					break;
				case 'u':
					char unicode = parseUnicode(cbuf.get(++ridx),cbuf.get(++ridx),cbuf.get(++ridx),cbuf.get(++ridx));
					cbuf.put(widx++, unicode);
					break;
				default:
					throw new IllegalArgumentException("unrecognized escape sequence start "+c1);
				}
			} else {
				switch (c) {
				case '"':
					throw new IllegalArgumentException("unescaped double quote in string literal");
				default:
					cbuf.put(widx++, c);
					break;
				}
			}
		}
		return new String(cbuf.array(), 0, widx);
	}

	private char parseUnicode(char c0, char c1, char c2, char c3) {
		String s = new String(new char[] {c0,c1,c2,c3});
		return (char)Integer.parseInt(s, 16);
	}

}
