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

import static com.nominanuda.zen.obj.JixEvent.E_ARR;
import static com.nominanuda.zen.obj.JixEvent.E_OBJ;
import static com.nominanuda.zen.obj.JixEvent.S_ARR;
import static com.nominanuda.zen.obj.JixEvent.S_OBJ;

import java.util.Iterator;
import java.util.LinkedList;

import com.nominanuda.zen.seq.ReadOnlyCollection;

public class JixBuffer implements JixHandler, JixSrc, ReadOnlyCollection<JixEvent> {
	private final LinkedList<JixEvent> events = new LinkedList<JixEvent>();

	@Override
	public void val(Val value) throws RuntimeException {
		onNext(value);
	}
	@Override
	public void startObj() throws RuntimeException {
		onNext(S_OBJ);
	}
	@Override
	public void startArr() throws RuntimeException {
		onNext(S_ARR);
	}
	@Override
	public void key(Key key) throws RuntimeException {
		onNext(key);
	}
	@Override
	public void endObj() throws RuntimeException {
		onNext(E_OBJ);
	}
	@Override
	public void endArr() throws RuntimeException {
		onNext(E_ARR);
	}
	private void onNext(JixEvent t) {
		events.add(t);
	}
	@Override
	public void sendTo(JixHandler sink) {
		for(JixEvent ev : events) {
			ev.sendTo(sink);
		}
	}

	@Override
	public Iterator<JixEvent> iterator() {
		return events.iterator();
	}

	@Override
	public int size() {
		return events.size();
	}

}
