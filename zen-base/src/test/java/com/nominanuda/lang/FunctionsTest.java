package com.nominanuda.lang;

import java.util.function.Function;

import org.junit.Assert;
import org.junit.Test;

import com.nominanuda.lang.Functions.Chain;


public class FunctionsTest {
	
	@Test
	public void testChain() {
		String result = new Chain<Integer>(1).apply(i -> {
			return i.toString();
		}).apply(s -> {
			return s + "a";
		}).get();
		
		Assert.assertEquals(result, "1a");
	}
	
	@Test
	public void testFunctions() {
		String result = new Functions<Integer, String>(new Function<Integer, String>() {
			@Override
			public String apply(Integer t) {
				return t.toString();
			}
		}, new Function<String, String>() {
			@Override
			public String apply(String t) {
				return t + "a";
			}
		}).apply(1);
		
		Assert.assertEquals(result, "1a");
	}
}
