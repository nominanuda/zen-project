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

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.nominanuda.zen.stereotype.Decorator;

@Immutable
public
interface Val extends JixEvent, Any, Decorator<Object> {
	public static final Val NULL = new ValImpl(null);
	public static final Val TRUE = new ValImpl(true);
	public static final Val FALSE = new ValImpl(false);
	public static final Val EMPTY_STR = new ValImpl("");

	static Val of(@Nullable Object v) {
		return new ValImpl(v);
	}

	@Override
	default boolean isVal() {
		return true;
	}

	@Override
	default Val copy() {
		return this;/*since @Immutable*/
	}


	@Override
	default JixEventType eventType() {
		return JixEventType.val;
	}

	@Override
	default void sendTo(JixHandler sink) {
		sink.val(this);
	}

	default boolean isNull() {
		return NULL == this;
	}
}
