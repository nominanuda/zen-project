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
package com.nominanuda.zen.reactivestreams;

import java.util.concurrent.Executor;

import org.reactivestreams.Subscriber;

import com.nominanuda.zen.stereotype.Copier;

public class ReactiveEvent<T> {
	private T t;
	private Throwable err;
	private boolean complete;

	public void reset() {
		t = null;
		complete = false;
		err = null;
	}
	/**
	 * shorthand for {@link ReactiveEvent#copyTo(ReactiveEvent)} and {@link ReactiveEvent#recycle()}
	 * @param ev
	 */
	public void flushTo(ReactiveEvent<T> ev) {
		copyTo(ev);
		reset();
	}

	public void flushTo(ReactiveEvent<T> ev, Copier<T> cloner) {
		copyTo(ev, cloner);
		reset();
	}

	public void flushTo(Subscriber<? super T> s) {
		if(complete) {
			s.onComplete();
		} else if(err != null) {
			s.onError(err);
		} else {
			if(t == null) {
				s.onError(new NullPointerException("empty reactive event"));
			} else {
				s.onNext(t);
			}
		}
		reset();
	}
	public void flushTo(final Subscriber<T> s, Executor exe) {
		exe.execute(() -> { flushTo(s); });
	}

	public void copyTo(ReactiveEvent<T> ev) {
		ev.t = this.t;
		ev.err = this.err;
		ev.complete = this.complete;
	}

	public void copyTo(ReactiveEvent<T> ev, Copier<T> cloner) {
		if(this.t == null) {
			ev.t = null;
		} else {
			ev.t = cloner.copy(this.t);
		}
		ev.err = this.err;
		ev.complete = this.complete;
	}

	public void set(T t) {
		reset();
		this.t = t;
	}

	public void err(Throwable err) {
		reset();
		this.err = err;
	}

	public void complete() {
		reset();
		complete = true;
	}

	public boolean isComplete() {
		return true == complete;
	}

	public boolean isNotSet() {
		return ! isSet();
	}

	public boolean isSet() {
		return t != null || complete || err != null;
	}
}