package com.nominanuda.lang;

import static org.junit.Assert.*;

import org.junit.*;

public class StringsTest {
	@Test
	public void shouldFormatGivenArguments() {
		assertEquals("a=b", Strings.F("{0}={1}", "a", "b"));
		assertEquals("120000000", Strings.F("{0}", 120000000));
	}

	@Test
	public void shouldThrowNullPointerExceptionOneArgumentIsNull() {
		try {
			Strings.F("{0}", new Object[] {null});
			fail("expected NullPointerException because argument is null");
		} catch (NullPointerException expectedException) {
		}
	}
	
	@Test
	public void testJoins() {
		Assert.assertEquals(Strings.joinNotNull("-", 1, "sdfd", null, "sdfd", "", "aaaa"), "1-sdfd-sdfd--aaaa");
		Assert.assertEquals(Strings.joinNotNullOrEmpty("-", 1, "sdfd", null, "sdfd", "", "aaaa"), "1-sdfd-sdfd-aaaa");
	}
}
