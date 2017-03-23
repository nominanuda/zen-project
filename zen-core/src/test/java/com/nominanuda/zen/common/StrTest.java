package com.nominanuda.zen.common;

import static com.nominanuda.zen.common.Str.STR;
import static org.junit.Assert.*;

import org.junit.Test;

public class StrTest {

	@Test
	public void test() {
		assertEquals("eeuoaa", STR.diacriticsAndMoreReplace("èéüöàä"));
		assertEquals("eeuoaa'i", STR.diacriticsAndMoreReplace("èéüöàä'i"));
		assertEquals("TH th ss ae o DdDdDd", STR.diacriticsAndMoreReplace("Þ þ ß æ ø ÐðĐđƉɖ"));
	}

}
