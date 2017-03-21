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

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class Pipe {
	public static Subscriber<? super JixEvent> adapt(JixHandler jixHandler) {
		return new JixHandlerToSubscriberAdapter(jixHandler);
	}

	private static class JixHandlerToSubscriberAdapter implements Subscriber<JixEvent> {
		private JixHandler jixHandler;
		public JixHandlerToSubscriberAdapter(JixHandler jixHandler) {
			this.jixHandler = jixHandler;
		}

		@Override
		public void onSubscribe(Subscription s) {
			s.request(Long.MAX_VALUE);
		}

		@Override
		public void onNext(JixEvent t) {
			t.sendTo(jixHandler);
		}

		@Override
		public void onError(Throwable t) {
			//TODO
		}

		@Override
		public void onComplete() {
			//TODO
		}
	}
}
