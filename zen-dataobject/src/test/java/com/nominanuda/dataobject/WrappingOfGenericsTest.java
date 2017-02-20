package com.nominanuda.dataobject;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class WrappingOfGenericsTest {

	@Test
	public void test() {
		Paged<String> p = paged();
		assertEquals("2", p.res().get(1));
		assertEquals("1", p.res1().get(0));
	}
	private Paged<String> paged() {
		return (Paged<String>) WrappingFactory.WF.wrap(Paged.class)
			.res(Arrays.asList("1","2"))
			.res1(Arrays.asList("1","2"))
		;
	}

	interface Paged<T> {
		List<T> res();
		Paged<T> res(List<T> l);

		List<String> res1();
		Paged<T> res1(List<String> l);
	}
}
