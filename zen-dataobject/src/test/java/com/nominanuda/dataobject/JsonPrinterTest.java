package com.nominanuda.dataobject;

import static org.junit.Assert.*;

import org.junit.Test;

import com.nominanuda.io.DevNull;

public class JsonPrinterTest {

	@Test
	public void testNoUnicode() {
		JsonPrinter jp = new JsonPrinter(DevNull.asWriter());
		assertEquals("", jp.stringEncode(""));
		assertEquals("1", jp.stringEncode("1"));
		assertEquals("\\\\", jp.stringEncode("\\"));
		assertEquals("é", jp.stringEncode("é"));
		assertEquals("\\\\u1234", jp.stringEncode("\\u1234"));
		assertEquals("ሴ", jp.stringEncode("\u1234"));
		assertEquals("\\n", jp.stringEncode("\n"));
		assertEquals("\\\"", jp.stringEncode("\""));
		assertEquals("\\r", jp.stringEncode("\r"));
		assertEquals("\\b", jp.stringEncode("\b"));
		assertEquals("\\f", jp.stringEncode("\f"));
		assertEquals("\\t", jp.stringEncode("\t"));
		
	}

	@Test
	public void testUnicode() {
		JsonPrinter jp = new JsonPrinter(DevNull.asWriter(),false, true, true);
		assertEquals("", jp.stringEncode(""));
		assertEquals("1", jp.stringEncode("1"));
		assertEquals("\\\\", jp.stringEncode("\\"));
		assertEquals("\\\\u1234", jp.stringEncode("\\u1234"));
		assertEquals("\\u1234", jp.stringEncode("\u1234"));
		assertEquals("\\u1234", jp.stringEncode("ሴ"));
		assertEquals("\\n", jp.stringEncode("\n"));
		assertEquals("\\\"", jp.stringEncode("\""));
		assertEquals("\\r", jp.stringEncode("\r"));
		assertEquals("\\b", jp.stringEncode("\b"));
		assertEquals("\\f", jp.stringEncode("\f"));
		assertEquals("\\t", jp.stringEncode("\t"));
		assertEquals("\\/\\/", jp.stringEncode("//"));
	}
}
