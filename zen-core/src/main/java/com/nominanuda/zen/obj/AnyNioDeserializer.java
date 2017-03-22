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

import static com.nominanuda.zen.common.Check.illegalargument;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.nominanuda.zen.stereotype.Factory;


class AnyNioDeserializer implements Subscriber<ByteBuffer>, Factory<Any> {
	private static final byte EOF = 0;
	private BinRange range;
	private int len;
	private int pos = 0;
	private byte cur = 0;

	private byte incr() {
		if(pos < len -1) {
			cur = range.at(++pos);
			return cur;
		} else if(pos < len) {
			++pos;
			cur = EOF;
			return cur;
		} else {
			throw new IllegalStateException("trying to move beyond eof");
		}
	}

	private byte stayOrIncrToNextNonWs() {
		while(true) {
			switch (cur) {
			case ' ':
			case '\n':
			case '\t':
			case '\r':
				break;
			default:
				return cur;
			}
			incr();
		}
	}

	private void eof() {
		while(pos < len - 1) {
			switch (cur) {
			case ' ':
			case '\n':
			case '\t':
			case '\r':
				break;
			default: 
				throw new IllegalArgumentException("trailing garbage at position "+pos);
			}
			incr();
		}
	}

	public Any expr() {
		stayOrIncrToNextNonWs();
		Any res = any();
		eof();
		return res;
	}

	private Any any() {
		stayOrIncrToNextNonWs();
		switch (cur) {
		case '{':
			return object();
		case '[':
			return array();
		case '-':
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			return num();
		case '"':
			return str();
		case 't':
			return _true();
		case 'f':
			return _false();
		case 'n':
			return _null();
		default:
			throw new IllegalArgumentException("unespected char "+((char)cur)+" at position "+pos);
		}
	}

	private Val _true() {
		if(incr() != 'r' || incr() != 'u' || incr() != 'e') {
			illegalargument.fail("invalid literal at position "+pos);
		}
		Val v = new ValImpl(range.range(pos - 3, 4), JsonType.bool, true);
		incr();
		return v;
	}
	private Val _false() {
		if(incr() != 'a' || incr() != 'l' || incr() != 's' || incr() != 'e') {
			illegalargument.fail("invalid literal at position "+pos);
		}
		Val v = new ValImpl(range.range(pos - 4, 5), JsonType.bool, false);
		incr();
		return v;
	}
	private Val _null() {
		if(incr() != 'u' || incr() != 'l' || incr() != 'l') {
			illegalargument.fail("invalid literal at position "+pos);
		}
		Val v = new ValImpl(range.range(pos - 3, 4), JsonType.nil, null);
		incr();
		return v;
	}
	private Val str() {
		int start = pos;
		boolean backSlashSeen = false;
		while(true) {
			incr();
			if(backSlashSeen) {
				switch (cur) {
				case '\\':
				case '"':
				case '/':
				case 'b':
				case 'f':
				case 'n':
				case 'r':
				case 't':
					backSlashSeen = false;
					break;
				case 'u':
					for(int j = 0; j < 4; j++) {
						illegalargument.assertTrue(incr() >= '/' && cur <= ':', "illegal unicode digit "+cur+" at position "+pos);
					}
					backSlashSeen = false;
					break;
				default:
					throw new IllegalArgumentException("illegal escape code "+cur+" at position "+pos);
				}
			} else {
				switch (cur) {
				case '\\':
					backSlashSeen = true;
					break;
				case '"':
					incr();
					Val v = new ValImpl(range.range(start, pos - start), JsonType.str);
					return v;
				default:
					break;
				}
				
			}
		}
	}
	private Val num() {
		int start = pos;
		boolean dotSeen = false;
		while(true) {
			incr();
			switch (cur) {
			case '.':
				if(dotSeen) {
					throw new IllegalArgumentException("malformed number at position"+pos);
				} else {
					dotSeen = true;
				}
				break;
			case '-':
				if(pos != start) {
					throw new IllegalArgumentException("usespected not leading '-' in number at position"+pos);
				}
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				break;
			case EOF:
			default:
				//decr();
				return new ValImpl(range.range(start, pos - start), JsonType.num);
			}
		}
	}
	private Arr array() {
		int start = pos;
		boolean first = true;
		LinkedList<Any> members = new LinkedList<>();
		incr();
		while(true) {
			stayOrIncrToNextNonWs();
			if(cur == ']') {
				Arr a = new ArrImpl(range.range(start, pos - start), members);
				incr();
				return a;
			}
			if(! first) {
				comma();
			}
			stayOrIncrToNextNonWs();
			Any memberVal = any();
			members.add(memberVal);
			first = false;
		}
	}
	private Obj object() {
		int start = pos;
		boolean first = true;
		LinkedHashMap<Key,Any> members = new LinkedHashMap<>();
		incr();
		while(true) {
			stayOrIncrToNextNonWs();
			if(cur == '}') {
				Obj o = new ObjImpl(range.range(start, pos - start), members);
				incr();
				return o;
			}
			if(! first) {
				comma();
			}
			stayOrIncrToNextNonWs();
			Key memberKey = keyAndDot();
			stayOrIncrToNextNonWs();
			Any memberVal = any();
			members.put(memberKey, memberVal);
			first = false;
		}
	}
	private void comma() {
		if(cur != ',') {
			throw new IllegalArgumentException("not found expected comma at "+pos);
		}
		incr();
	}
	private Key keyAndDot() {
		Val k = str();
		stayOrIncrToNextNonWs();
		if(cur != ':') {
			throw new IllegalArgumentException("not found expected colon at "+pos);
		}
		incr();
		//TODO
		return new KeyImpl(((ValImpl)k).range());
	}

	@Override
	public void onSubscribe(Subscription s) {
		s.request(Long.MAX_VALUE);
	}

	@Override
	public final void onNext(ByteBuffer t) {
		events.add(t);
	}

	@Override
	public void onError(Throwable t) {
		events.clear();
	}

	@Override
	public void onComplete() {
		ByteBuf bb = Unpooled.copiedBuffer(events.toArray(new ByteBuffer[events.size()]));
		range = new BinRange(bb);
		len = range.getLength();
		cur = range.at(pos);
	}

	@Override
	public Any get() {
		return expr();
	}

	private final List<ByteBuffer> events = new LinkedList<ByteBuffer>();


}
