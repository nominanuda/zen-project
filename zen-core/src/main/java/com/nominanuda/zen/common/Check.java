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
package com.nominanuda.zen.common;

import static com.nominanuda.zen.common.Str.STR;
import static com.nominanuda.zen.seq.Seq.SEQ;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.nominanuda.zen.stereotype.NotFoundException;
import com.nominanuda.zen.stereotype.UncheckedExecutionException;

@ThreadSafe
public enum Check {
	nullpointer(NullPointerException.class), 
	illegalstate(IllegalStateException.class),
	illegalargument(IllegalArgumentException.class),
	runtime(RuntimeException.class),
	unsupportedoperation(UnsupportedOperationException.class),
	notfound(NotFoundException.class),
	io(UncheckedIOException.class),
	security(SecurityException.class),
	execution(UncheckedExecutionException.class)
	;

	private Supplier<? extends RuntimeException> noArgsBuilder;
	private Function<String, ? extends RuntimeException> reasonBuilder;
	private Check(final Class<? extends RuntimeException> ex) {
		noArgsBuilder = () -> {
			try {
				return ex.newInstance();
			} catch (Exception e) {
				throw new IllegalStateException();
			}
		};
		if (UncheckedIOException.class.equals(ex)) {
			reasonBuilder = (reason) -> new UncheckedIOException(new IOException(reason));
		} else {
			try {
				Constructor<? extends RuntimeException> stringCtor = ex.getDeclaredConstructor(new Class<?>[] {String.class});
				reasonBuilder = (reason) -> {
					try {
						return stringCtor.newInstance(reason);
					} catch (Exception e) {
						throw new IllegalStateException();
					}};
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		};
	}
	private RuntimeException buildEx() {
		return noArgsBuilder.get();
	}

	private RuntimeException buildEx(@Nullable String reason) {
		return reason == null ? buildEx() : reasonBuilder.apply(reason);
	}

	public <T> T that(T t, Predicate<? super T> condition) {
		assertTrue(condition.test(t));
		return t;
	}

	public <T> T that(T t, Predicate<? super T> condition, String reason) {
		assertTrue(condition.test(t), reason);
		return t;
	}

	public static void notNulls(Object... objs) throws NullPointerException {
		for (Object o : objs) {
			notNull(o);
		}
	}
	public static <T> T notNull(T o) throws NullPointerException {
		if (o == null) {
			throw nullpointer.buildEx();
		}
		return o;
	}
	public static <T> T notNull(T o, String reason) {
		if (o == null) {
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
		if (o != null) {
			throw buildEx(reason);
		}
	}

	public void assertEquals(Object o1, Object o2) {
		if (o1 == null || o2 == null || !o1.equals(o2)) {
			throw buildEx();
		}
	}
	public void assertEquals(Object o1, Object o2, String reason) {
		if (o1 == null || o2 == null || !o1.equals(o2)) {
			throw buildEx(reason);
		}
	}

	public <T> T assertInstanceOf(Object o1, Class<T> type, String reason) {
		if (Check.isNotInstanceOf(o1, type)) {
			throw buildEx(reason);
		}
		return type.cast(o1);
	}

	public <T> T assertInstanceOf(Object o1, Class<T> type) {
		return assertInstanceOf(o1, type, null);
	}

	public <T> T assertNotNull(T o) throws NullPointerException {
		if (o == null) {
			throw buildEx();
		}
		return o;
	}
	public <T> T assertNotNull(T o, String reason) {
		if (o == null) {
			throw buildEx(reason);
		}
		return o;
	}

	public <T> Collection<T> notNullOrEmpty(Collection<T> coll) {
		assertFalse(SEQ.nullOrEmpty(coll));
		return coll;
	}

	public <T> Collection<T> notNullOrEmpty(Collection<T> coll, String reason) {
		assertFalse(SEQ.nullOrEmpty(coll), reason);
		return coll;
	}

	public String notNullOrEmpty(String s) {
		assertFalse(STR.nullOrEmpty(s));
		return s;
	}
	public String notNullOrEmpty(String s, String reason) {
		assertFalse(STR.nullOrEmpty(s), reason);
		return s;
	}

	public String notNullOrBlank(String s) {
		assertFalse(STR.nullOrBlank(s));
		return s;
	}
	public String notNullOrBlank(String s, String reason) {
		assertFalse(STR.nullOrBlank(s), reason);
		return s;
	}


	public <T> T fail() {
		throw buildEx();
	}
	public <T> T fail(String reason) {
		throw buildEx(reason);
	}
	public boolean assertTrue(boolean cond) {
		if (! cond) {
			fail();
		}
		return true;
	}
	public boolean assertTrue(boolean cond, String reason) {
		if (! cond) {
			fail(reason);
		}
		return true;
	}
	public boolean assertFalse(boolean cond) {
		if (cond) {
			fail();
		}
		return false;
	}
	public boolean assertFalse(boolean cond, String reason) {
		if (cond) {
			fail(reason);
		}
		return false;
	}

	public String matches(String s, Pattern regex) {
		if (! regex.matcher(s).matches()) {
			fail();
		}
		return s;
	}

	public String matches(String s, Pattern regex, String reason) {
		if (! regex.matcher(s).matches()) {
			fail(reason);
		}
		return s;
	}

	public <T extends Number> T assertNotNegative(T val) {
		if (val.doubleValue() < 0) {
			fail();
		}
		return val;
	}
	public int assertGtZero(int val) {
		if (val <= 0) {
			fail();
		}
		return val;
	}
	public long assertGtZero(long val) {
		if (val <= 0) {
			fail();
		}
		return val;
	}

//	public static <T> T ifNull(T o, T defaultVal) {
//		return o == null ? defaultVal : o;
//	}
	@SafeVarargs
	public static <T> T ifNull(T... objs) {
		for (T obj : objs) {
			if (obj != null) {
				return obj;
			}
		}
		return null;
	}

	/**
	 * @return o == null ? null : f()
	 */
	public static @Nullable <T> T nullOrGet(@Nullable T o, Supplier<T> f) {
		return o == null ? null : f.get();
	}
	@Deprecated
	public static @Nullable <T> T nullOr(@Nullable T o, Supplier<T> defaultVal) {
		// NOTE: had wrong implementation so we call notNullOrGet instead of nullOrGet for back-compatibility
		// old impl: return o != null ? o : defaultVal.get();
		System.out.println("THE METHOD Check.nullOr(T o, Supplier<T> defaultVal) HAD A WRONG IMPLEMENTATION, CHECK ITS USAGE!!!");
		return notNullOrGet(o, defaultVal);
	}
	
	/**
	 * @return o != null ? o : f()
	 */
	public static <T> T notNullOrGet(@Nullable T o, Supplier<T> f) {
		return o != null ? o : f.get();
	}
	@Deprecated
	public static <T> T ifNull(T o, Supplier<T> defaultVal) {
		// old impl: return o == null ? defaultVal.get() : o;
		return notNullOrGet(o, defaultVal);
	}
	@Deprecated
	public static <T> T notNullOr(@Nullable T o, Supplier<T> defaultVal) {
		// old impl: return o != null ? o : defaultVal.get();
		return notNullOrGet(o, defaultVal);
	}
	
	/**
	 * @return o != null -> f(o)
	 */
	public static <T> void nullOrAccept(@Nullable T o, Consumer<T> f) {
		if (o != null) f.accept(o);
	}

	/**
	 * @return o == null ? null : f(o)
	 */
	public static @Nullable <T, R> R nullOrApply(@Nullable T o, Function<T, R> f) {
		return o == null ? null : f.apply(o);
	}
	@Deprecated
	public static @Nullable <T, R> R nullOr(@Nullable T o, Function<T, R> defaultVal) {
		// old impl: return o == null ? null : defaultVal.apply(o);
		return nullOrApply(o, defaultVal);
	}
	@Deprecated
	public static @Nullable <T, R> R ifNotNullApply(@Nullable T o, Function<T, R> defaultVal) {
		// return o == null ? null : defaultVal.apply(o);
		return nullOrApply(o, defaultVal);
	}


	public static String ifNullOrEmpty(String s, String defaultVal) {
		return STR.nullOrEmpty(s) ? defaultVal : s;
	}
	
	public static String ifNullOrBlank(String s, String defaultVal) {
		return STR.nullOrBlank(s) ? defaultVal : s;
	}
	
	public static File ifNullOrNotExistent(File f, File defaultVal) {
		return f != null && f.exists() ? f : defaultVal;
	}

	public static boolean isInstanceOf(@Nullable Object o, Class<?>... types) {
		if (o == null) {
			return false;
		} else {
			for (Class<?> type : types) {
				if (type.isInstance(o)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> void ifInstanceAccept(Object o, Class<T> type, Consumer<T> f) {
		if (type.isInstance(o)) {
			f.accept((T) o);
		}
	}

	public static boolean isNotInstanceOf(@Nullable Object o, Class<?>... types) {
		return ! isInstanceOf(o, types);
	}
}
