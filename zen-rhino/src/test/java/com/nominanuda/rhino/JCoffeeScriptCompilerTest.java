package com.nominanuda.rhino;

import org.junit.Test;


public class JCoffeeScriptCompilerTest {

	@Test
	public void testSimpleScript() {
		String coffeeScriptSource = "square = (x) -> x * x";
		JCoffeeScriptCompiler compiler = new JCoffeeScriptCompiler();
		String jsSource = compiler.compile(coffeeScriptSource);
		System.err.println(jsSource);
	}
}
