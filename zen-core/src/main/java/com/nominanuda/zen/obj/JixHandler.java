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
import static com.nominanuda.zen.common.Check.notNull;
import static com.nominanuda.zen.obj.JixEvent.E_ARR;
import static com.nominanuda.zen.obj.JixEvent.E_OBJ;
import static com.nominanuda.zen.obj.JixEvent.S_ARR;
import static com.nominanuda.zen.obj.JixEvent.S_OBJ;

import javax.annotation.Nullable;

import org.reactivestreams.Subscriber;

import com.nominanuda.zen.common.Check;


public interface JixHandler {
//TODO remove	/**
//	 * emits a sequence of {@link JixHandler#key(Key)} {@link JixHandler#val(Val)} events
//	 * only primitive values supported. Args are in the form k,v,k,v,k,v where k can be a {@link String} or 
//	 * a {@link Key} and v can be a {@link Val} or a primitive
//	 * @param kvs
//	 * @throws RuntimeException
//	 */
//	default void keyVals(Object... kvs) throws RuntimeException {
//		int len = kvs.length / 2;
//		illegalargument.assertTrue(kvs.length == len * 2, "odd number of arguments");
//		for(int i = 0; i < len; i++) {
//			Object k = kvs[i*2];
//			if(notNull(k) instanceof Key) {
//				key((Key)k);
//			} else {
//				key(Key.of((String)k));
//			}
//			Object v = kvs[i*2+1];
//			if(Check.isInstanceOf(v, Val.class)) {
//				val((Val)v);
//			} else {
//				val(Val.of(v));
//			}
//		}
//	}


	void startObj() throws RuntimeException;

	void endObj() throws RuntimeException;

	void key(Key key) throws RuntimeException;

//TODO remove	/**
//	 * emits a sequence of {@link JixHandler#val(Val)} events
//	 * only primitive values supported. Args are in the form v,v,v where v can be a {@link Val} or a primitive
//	 * @param members
//	 * @throws RuntimeException
//	 */
//	default void vals(Object... members) throws RuntimeException {
//		int len = members.length;
//		for(int i = 0; i < len; i++) {
//			Object v = members[i];
//			if(Check.isInstanceOf(v, Val.class)) {
//				val((Val)v);
//			} else {
//				val(Val.of(v));
//			}
//		}
//	}

	void startArr() throws RuntimeException;

	void endArr() throws RuntimeException;

	void val(Val value) throws RuntimeException;

//	/**
//	 * @param value (a java json primitive)
//	 * @throws RuntimeException
//	 */
//	default void valOf(@Nullable Object value) throws RuntimeException {
//		Check.isNotInstanceOf(value, Val.class);
//		val(Val.of(value));
//	}

//TODO move	public static class JixEventSubscriberToHandlerAdapter implements JixHandler {
//		protected final Subscriber<? super JixEvent> sink;
//
//		public JixEventSubscriberToHandlerAdapter(Subscriber<? super JixEvent> sink) {
//			this.sink = sink;
//		}
//		@Override
//		public void val(Val value) throws RuntimeException {
//			sink.onNext(value);
//		}
//		@Override
//		public void sObj() throws RuntimeException {
//			sink.onNext(S_OBJ);
//		}
//		@Override
//		public void sArr() throws RuntimeException {
//			sink.onNext(S_ARR);
//		}
//		@Override
//		public void key(Key key) throws RuntimeException {
//			sink.onNext(key);
//		}
//		@Override
//		public void eObj() throws RuntimeException {
//			sink.onNext(E_OBJ);
//		}
//		@Override
//		public void eArr() throws RuntimeException {
//			sink.onNext(E_ARR);
//		}
//
//	}
//	public static JixHandler adapt(final Subscriber<? super JixEvent> sink) {
//		return new JixEventSubscriberToHandlerAdapter(sink);
//	}
}
