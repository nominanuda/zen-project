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
package com.nominanuda.zen.stereotype;

import java.lang.annotation.ElementType;

/**
 * said of a computation that is CPU-bound only e.g. wait free;
 * still can interact with kernel (e.g. calling {@link System#currentTimeMillis()}}) and thus schedule out the thread,
 * but avoids {@link Thread#sleep(long)} or I/O operations.
 * if applied to {@link ElementType#PARAMETER} or {@link ElementType#FIELD} documenting an assumption of the using code;
 * while on {@link ElementType#METHOD} or {@link ElementType#TYPE} documents the implementation
 * in the latter case all methods must comply. It is more or less a synonymous for asynchronous
 */
//@Retention(RetentionPolicy.RUNTIME)
//@Target({
//	ElementType.PARAMETER, ElementType.FIELD,//in this position is for documenting an assumption
//	ElementType.METHOD, ElementType.TYPE//here is for describing an implementation that is CPU-bound only e.g. wait free
//	})
public interface NonBlocking {
}
