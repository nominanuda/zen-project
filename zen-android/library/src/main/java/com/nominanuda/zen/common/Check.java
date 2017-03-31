package com.nominanuda.zen.common;

import java.lang.reflect.Constructor;
import java.util.Collection;

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
			exStringCtor = exClass.getDeclaredConstructor(new Class<?>[]{String.class});
		} catch (Exception e) {
			throw new IllegalStateException();
		}
	}

	private RuntimeException buildEx() {
		try {
			return exClass.newInstance();
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
		if (o != null) {
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
		if (o1 == null || !type.isAssignableFrom(o1.getClass())) {
			throw buildEx(reason);
		}
		return type.cast(o1);
	}

	public <T> T assertInstanceOf(Object o1, Class<T> type) {
		if (o1 == null || !type.isAssignableFrom(o1.getClass())) {
			throw buildEx();
		}
		return type.cast(o1);
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
		assertFalse(Util.notEmpty(coll));
		return coll;
	}

	public <T> Collection<T> notNullOrEmpty(Collection<T> coll, String reason) {
		assertFalse(Util.notEmpty(coll), reason);
		return coll;
	}

	public String notNullOrEmpty(String s) {
		assertFalse(Util.isEmpty(s));
		return s;
	}

	public String notNullOrEmpty(String s, String reason) {
		assertFalse(Util.isEmpty(s), reason);
		return s;
	}

	public String notNullOrBlank(String s) {
		assertFalse(Util.isBlank(s));
		return s;
	}

	public String notNullOrBlank(String s, String reason) {
		assertFalse(Util.isBlank(s), reason);
		return s;
	}

	public <T> T fail() {
		throw buildEx();
	}

	public <T> T fail(String reason) {
		throw buildEx(reason);
	}

	public <T> T assertTrue(boolean cond) {
		if (!cond) {
			fail();
		}
		return null;
	}

	public <T> T assertTrue(boolean cond, String reason) {
		if (!cond) {
			fail(reason);
		}
		return null;
	}

	public <T> T assertFalse(boolean cond) {
		if (cond) {
			fail();
		}
		return null;
	}

	public <T> T assertFalse(boolean cond, String reason) {
		if (cond) {
			fail(reason);
		}
		return null;
	}
}
