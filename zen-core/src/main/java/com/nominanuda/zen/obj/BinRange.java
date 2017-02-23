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

import static com.nominanuda.zen.common.Check.notNull;
import static com.nominanuda.zen.obj.JsonDeserializer.JSON_DESERIALIZER;
import static java.nio.charset.StandardCharsets.US_ASCII;
import io.netty.buffer.ByteBuf;

public class BinRange {
	private ByteBuf bb;
	public BinRange(ByteBuf bb) {
		this.bb = notNull(bb);
	}

	byte at(int pos) {
		return bb.getByte(pos);
	}

	public int getLength() {
		return bb.readableBytes();
	}

	public BinRange range(int start, int len) {
		return new BinRange(bb.slice(start, len));
	}

	public Object decode() throws IllegalArgumentException {
		if(bb.readableBytes() < 1) {
			throw new IllegalArgumentException("empty input");
		}
		byte first = bb.getByte(0);
		switch (first) {
		case '"':
			return JSON_DESERIALIZER.decodeString(bb.nioBuffer());
		case 'f':
			return false;
		case 't':
			return true;
		case 'n':
			return null;
		default:
			return JSON_DESERIALIZER.deserializeNumber(bb.toString(US_ASCII));
		}
	}
}