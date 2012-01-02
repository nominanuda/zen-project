/*
 * Copyright 2008-2011 the original author or authors.
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
package com.nominanuda.lang;

import java.lang.reflect.Constructor;
import java.util.Collection;

import com.nominanuda.code.ThreadSafe;

import static com.nominanuda.lang.Strings.*;

@ThreadSafe
public enum Check {
	nullpointer(NullPointerException.class), 
	illegalstate(IllegalStateException.class),
	illegalargument(IllegalArgumentException.class),
	runtime(RuntimeException.class),
	unsupportedoperation(UnsupportedOperationException.class);

	private Class<? extends RuntimeException> exClass;
	private Constructor<? extends RuntimeException> exStringCtor;
	private Check(Class<? extends RuntimeException> ex) {
		exClass = ex;
		try {
			exStringCtor = exClass.getDeclaredConstructor(new Class<?>[] {String.class});
		} catch (Exception e) {
			throw new IllegalStateException();
		}
	}
	private RuntimeException buildEx() {
		try {
			RuntimeException re = exClass.newInstance();
			return re;
		} catch (Exception e) {
			throw new IllegalStateException();
		}
	}
	private RuntimeException buildEx(String reason) {
		try {
			return exStringCtor.newInstance(reason);
		} catch (Exception e) {
			throw new IllegalStateException();
		}
	}

	public static void notNulls(Object... objs) throws NullPointerException {
		for(Object o : objs) {
			notNull(o);
		}
	}
	public static <T> T notNull(T o) throws NullPointerException {
		if(o == null) {
			throw nullpointer.buildEx();
		}
		return o;
	}
	public static <T> T notNull(T o, String reason) {
		if(o == null) {
			throw nullpointer.buildEx(reason);
		}
		return o;
	}

	public void assertNull(Object o) throws NullPointerException {
		if(o != null) {
			throw buildEx();
		}
	}
	public void assertNull(Object o, String reason) {
		if(o != null) {
			throw buildEx(reason);
		}
	}

	public void assertEquals(Object o1, Object o2) {
		if(o1 == null || o2 == null || !o1.equals(o2)) {
			throw buildEx();
		}
	}
	public void assertEquals(Object o1, Object o2, String reason) {
		if(o1 == null || o2 == null || !o1.equals(o2)) {
			throw buildEx(reason);
		}
	}

	public <T> T assertNotNull(T o) throws NullPointerException {
		if(o == null) {
			throw buildEx();
		}
		return o;
	}
	public <T> T assertNotNull(T o, String reason) {
		if(o == null) {
			throw buildEx(reason);
		}
		return o;
	}

	public <T> Collection<T> notNullOrEmpty(Collection<T> coll) {
		assertFalse(Collections.nullOrEmpty(coll));
		return coll;
	}
	public String notNullOrEmpty(String s) {
		assertFalse(nullOrEmpty(s));
		return s;
	}
	public String notNullOrBlank(String s) {
		assertFalse(nullOrBlank(s));
		return s;
	}

	public <T> T fail() {
		throw buildEx();
	}
	public <T> T fail(String reason) {
		throw buildEx(reason);
	}
	public <T> T assertTrue(boolean cond) {
		if(! cond) {
			fail();
		}
		return null;
	}
	public <T> T assertTrue(boolean cond, String reason) {
		if(! cond) {
			fail(reason);
		}
		return null;
	}
	public <T> T assertFalse(boolean cond) {
		if(cond) {
			fail();
		}
		return null;
	}
	public <T> T assertFalse(boolean cond, String reason) {
		if(cond) {
			fail(reason);
		}
		return null;
	}
	public static <T> T ifNull(T o, T defaultVal) {
		return o == null ? defaultVal : o;
	}
	public static String ifNullOrEmpty(String s, String defaultVal) {
		return nullOrEmpty(s) ? defaultVal : s;
	}
	public static String ifNullOrBlank(String s, String defaultVal) {
		return nullOrBlank(s) ? defaultVal : s;
	}
}
