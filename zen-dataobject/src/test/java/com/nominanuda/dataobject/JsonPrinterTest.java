package com.nominanuda.dataobject;

import static org.junit.Assert.*;

import org.junit.Test;

import com.nominanuda.io.DevNull;

public class JsonPrinterTest {

	@Test
	public void testNoUnicode() {
		JsonPrinter jp = new JsonPrinter(DevNull.asWriter());
		assertEquals("", jp.jsonStringEscape(""));
		assertEquals("1", jp.jsonStringEscape("1"));
		assertEquals("\\\\", jp.jsonStringEscape("\\"));
		assertEquals("é", jp.jsonStringEscape("é"));
		assertEquals("\\\\u1234", jp.jsonStringEscape("\\u1234"));
		assertEquals("ሴ", jp.jsonStringEscape("\u1234"));
		assertEquals("\\n", jp.jsonStringEscape("\n"));
		assertEquals("\\\"", jp.jsonStringEscape("\""));
		assertEquals("\\r", jp.jsonStringEscape("\r"));
		assertEquals("\\b", jp.jsonStringEscape("\b"));
		assertEquals("\\f", jp.jsonStringEscape("\f"));
		assertEquals("\\t", jp.jsonStringEscape("\t"));
		
	}

	@Test
	public void testUnicode() {
		JsonPrinter jp = new JsonPrinter(DevNull.asWriter(),false, true, true);
		System.err.println(jp.jsonStringEscape("u"));
		assertEquals("", jp.jsonStringEscape(""));
		assertEquals("1", jp.jsonStringEscape("1"));
		assertEquals("\\\\", jp.jsonStringEscape("\\"));
		assertEquals("\\\\u1234", jp.jsonStringEscape("\\u1234"));
		assertEquals("\\u1234", jp.jsonStringEscape("\u1234"));
		assertEquals("\\u1234", jp.jsonStringEscape("ሴ"));
		assertEquals("\\n", jp.jsonStringEscape("\n"));
		assertEquals("\\\"", jp.jsonStringEscape("\""));
		assertEquals("\\r", jp.jsonStringEscape("\r"));
		assertEquals("\\b", jp.jsonStringEscape("\b"));
		assertEquals("\\f", jp.jsonStringEscape("\f"));
		assertEquals("\\t", jp.jsonStringEscape("\t"));
		assertEquals("\\/\\/", jp.jsonStringEscape("//"));
	}
}
