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
package com.nominanuda.apikey;

import static com.nominanuda.zen.codec.Base62.B62;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.nominanuda.zen.common.Check;;


public class Segments {
	private List<Segment> segments;
	Segment[] _segments;
	FieldMap fm;
	public Segments(Segment[] _segments, FieldMap fm) {
		this.segments = Arrays.asList(_segments);
		this.fm = fm;
	}

	public void setField(String name, Object value) {
		FieldCoordinates coo = fm.getCoordinates(name);
		Segment s = segments.get(coo.getSegmentIndex());
		Field f = s.getField(coo.getFieldIndex());
		f.update(value);
		s.setField(coo.getFieldIndex(), f);
	}

	public Field getField(String name) {
		FieldCoordinates coo = fm.getCoordinates(name);
		Segment s = segments.get(coo.getSegmentIndex());
		return s.getField(coo.getFieldIndex());
	}
	public String serialize() {
		List<byte[]> l = new LinkedList<byte[]>();
		for(Segment s : segments) {
			byte[] b = s.save();
			l.add(b);
		}
		byte[] res = joinByHeader(l);
		return B62.encode(res);
	}
	public static List<byte[]> splitApikeyToSegments(String apikey) {
		return splitByHeader(B62.decode(apikey));
	}

	public static List<byte[]> splitByHeader(byte[] msg) {
		List<byte[]> l = new LinkedList<byte[]>();
		ByteBuffer bb = ByteBuffer.wrap(msg);
		while(bb.hasRemaining()) {
			char len = bb.getChar();
			byte[] b = new byte[len];
			bb.get(b);
			l.add(b);
		}
		return l;
	}
	public static byte[] joinByHeader(List<byte[]> l) {
		int len = 0;
		for(byte[] b : l) {
			len += b.length + 2;
		}
		ByteBuffer bb = ByteBuffer.allocate(len);
		for(byte[] b : l) {
			bb.putChar((char)b.length);
			bb.put(b);
		}
		byte[] res = bb.array();
		return res;
	}

	public static byte[] joinHeadAndTail(byte[] head, byte[] tail) {
		ByteBuffer bb = ByteBuffer.allocate(head.length + tail.length + 2);
		bb.putChar((char)head.length);
		bb.put(head);
		bb.put(tail);
		byte[] res = bb.array();
		return res;
	}
	public static List<byte[]> splitHeadAndTail(byte[] msg) {
		ByteBuffer bb = ByteBuffer.wrap(msg);
		char headLen = bb.getChar();
		byte[] head = new byte[headLen];
		byte[] tail = new byte[msg.length - headLen - 2];
		bb.get(head);
		bb.get(tail);
		Check.illegalstate.assertFalse(bb.hasRemaining());
		return Arrays.asList(head, tail);
	}

}
