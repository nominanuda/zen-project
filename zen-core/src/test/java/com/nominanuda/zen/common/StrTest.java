package com.nominanuda.zen.common;

import static com.nominanuda.zen.common.Str.STR;
import static org.junit.Assert.*;

import org.junit.Test;

public class StrTest {

	@Test
	public void testDiacritics() {
		assertEquals("n", STR.diacriticsReplace("ñ"));
		assertEquals("eeuoaa", STR.diacriticsReplace("èéüöàä"));
		assertEquals("eeuoaa'i", STR.diacriticsReplace("èéüöàä'i"));
		assertEquals("TH th ss ae o DdDdDd", STR.diacriticsAndMoreReplace("Þ þ ß æ ø ÐðĐđƉɖ"));
	}

	@Test
	public void testStripWs() {
		assertEquals("aaa", STR.stripWs(" \na\r a\ta"));
	}
}
