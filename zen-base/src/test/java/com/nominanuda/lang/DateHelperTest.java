/*
 * Copyright 2008-2011 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nominanuda.lang;

import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class DateHelperTest {
	private DateTimeHelper dt = new DateTimeHelper();

	@Test
	public void testToISO() {
		long epochMillis = 23 * 3600 * 1000;//23.00  1/1/1970
		assertEquals("1970-01-01T23:00:00Z", dt.toISO8601UtcSecs(epochMillis));
		assertEquals("1970-01-01T23:00:00.000Z", dt.toISO8601UtcMillis(epochMillis));
		assertEquals("1970-01-01T23:00:00.010Z", dt.toISO8601UtcMillis(epochMillis + 10));
		assertEquals("1970-01-01", dt.toISO8601UtcDate(epochMillis + 10));
		
		ISOChronology chrono = ISOChronology.getInstance();
		//03:00 of 10 feb 2010 in Switzerland (localtime) (UTC+1)
		long l1 = chrono.withZone(DateTimeZone.forID("Europe/Zurich"))
					.getDateTimeMillis(2011, 2, 10, 3*3600000);
		//03:00 of 10 aug 2010 in Switzerland (localtime) (UTC+2)
		long l2 = chrono.withZone(DateTimeZone.forID("Europe/Zurich"))
					.getDateTimeMillis(2011, 8, 10, 3*3600000);
		assertEquals("2011-02-10T02:00:00Z", dt.toISO8601UtcSecs(l1));
		assertEquals("2011-08-10T01:00:00Z", dt.toISO8601UtcSecs(l2));
		
		assertEquals("1999-02-02T00:00:00.000Z", dt.toISO8601UtcMillis(
			dt.fromISO8601UtcDate("1999-02-02")));
		assertEquals("1999-02-02T00:00:00.000Z", dt.toISO8601UtcMillis(
			dt.fromISO8601UtcSecs("1999-02-02T00:00:00+00")));
		assertEquals("1999-02-02T00:00:00.000Z", dt.toISO8601UtcMillis(
			dt.fromISO8601UtcMillis("1999-02-02T00:00:00.000Z")));
		try {
			dt.fromISO8601Any("1999-02-02T");
		} catch(IllegalArgumentException e) {}
		try {
			dt.fromISO8601Any("1999-02-02T00:00:00.001");
		} catch(IllegalArgumentException e) {}
		assertEquals("1999-02-02T00:00:00Z", dt.toISO8601UtcSecs(
			dt.fromISO8601Any("1999-02-02")));
		assertEquals("1999-02-02T00:00:00Z", dt.toISO8601UtcSecs(
			dt.fromISO8601Any("1999-02-02T00:00:00Z")));
		assertEquals("1999-02-02T00:00:00Z", dt.toISO8601UtcSecs(
			dt.fromISO8601Any("1999-02-02T00:00:00+00")));
		assertEquals("1999-02-02T00:00:00Z", dt.toISO8601UtcSecs(
			dt.fromISO8601Any("1999-02-02T00:00:00+0000")));
		assertEquals("1999-02-02T00:00:00Z", dt.toISO8601UtcSecs(
			dt.fromISO8601Any("1999-02-02T00:00:00-00:00")));
		assertEquals("1999-02-02T00:00:00Z", dt.toISO8601UtcSecs(
			dt.fromISO8601Any("1999-02-02T00:00:00-0000")));
		assertEquals("1999-02-02T00:00:00.001Z", dt.toISO8601UtcMillis(
			dt.fromISO8601Any("1999-02-02T00:00:00.001-0000")));
	}

	//TODO
	@Ignore
	@Test
	public void testParseRfc882() {
//		DateTimeZone zz = DateTimeZone.forID("PDT");
		DateTimeFormatter f = 
//		DateTimeFormat.forPattern("EEE dd MMM yyyy HH:mm:ss zzz").withLocale(Locale.US)
		DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss zzz")
		;
		DateTime t = f.parseDateTime("Fri, 21 Oct 2011 00:00:41 PST");
		//dt.parseHttpDate822("Fri, 21 Oct 2011 00:00:41 PDT");
	}
}
