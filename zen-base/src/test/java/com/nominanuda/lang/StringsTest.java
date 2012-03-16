package com.nominanuda.lang;

import static org.junit.Assert.*;

import org.junit.*;

public class StringsTest {
	@Test
	public void shouldFormatGivenArguments() {
		assertEquals("a=b", Strings.F("{0}={1}", "a", "b"));
	}

	@Test
	public void shouldThrowNullPointerExceptionOneArgumentIsNull() {
		try {
			Strings.F("{0}", null);
			fail("expected NullPointerException because argument is null");
		} catch (NullPointerException expectedException) {
		}
	}
}
