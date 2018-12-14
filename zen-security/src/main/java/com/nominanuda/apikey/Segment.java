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

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import com.nominanuda.zen.common.Check;

public class Segment {
	private final KeySet authority;
	byte[] binary;
	private final Field[] fields;

	public Segment(Field[] sFields, @Nullable KeySet authority) {
		this.fields = sFields;
		this.authority = authority == null ? KeySet.nullOne() : authority;
	}

	boolean modified = false;

	public void load(byte[] s) {
		binary = s;
	}

	public void setField(int fieldIndex, Field f) {
		modified = true;
		fields[fieldIndex] = f;
	}

	public Field getField(int fieldIndex) {
		readEmptyFields();
		return fields[fieldIndex];
	}

	public byte[] save() {
		if(! (modified || binary == null)) {
			return binary;
		} else {
			readEmptyFields();
			return serializeAndSign();
		}
	}


	private void readEmptyFields() {
		boolean hasEmpty = false;
		for(Field f : fields) {
			if(f.isEmpty()) {
				hasEmpty = true;
				break;
			}
		}
		if(hasEmpty) {
			if(binary != null) {
				byte[] clearSegment = null;
				clearSegment = verifyAndRead(binary);
				List<byte[]> encodedFields = Segments.splitByHeader(clearSegment);
				Check.illegalstate.assertTrue(encodedFields.size() == fields.length);
				for(int i = 0; i < fields.length; i++) {
					Field f = fields[i];
					if(f.isEmpty()) {
						f.load(encodedFields.get(i));
					}
				}
			} else {
				Check.illegalstate.fail("Segment not completely initialized");
			}
		}
	}

	private byte[] serializeAndSign() {
		List<byte[]> encodedFields = new LinkedList<byte[]>();
		for(Field f : fields) {
			encodedFields.add(f.save());
		}
		byte[] msg = Segments.joinByHeader(encodedFields);
		if (msg.length > 2) { // if not, it's a NULL segment
			byte[] signature = authority.sign(msg);
			byte[] cypherText = authority.encrypt(msg);
			return Segments.joinHeadAndTail(cypherText, signature);
		}
		return msg;
	}

	private byte[] verifyAndRead(byte[] binary) {
		if (binary.length > 2) { // if not, it's a NULL segment
			List<byte[]> headAndTail = Segments.splitHeadAndTail(binary);
			byte[] cypherText = headAndTail.get(0);
			byte[] signature = headAndTail.get(1);
			byte[] clearText = authority.decrypt(cypherText);
			if(! authority.verify(clearText, signature)) {
				throw new SecurityException("apikey verification failed");
			}
			return clearText;
		}
		return binary;
	}

}
