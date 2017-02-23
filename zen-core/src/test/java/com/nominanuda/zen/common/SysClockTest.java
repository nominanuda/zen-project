/*
 * Copyright 2008-2016 the original author or authors.
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
package com.nominanuda.zen.common;

import static com.nominanuda.zen.time.SysClock.CLOCK;
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.nominanuda.zen.time.SysClock;
import com.nominanuda.zen.time.SysClock.TimeDiff;

public class SysClockTest {

	@Test
	public void testDiff() throws InterruptedException {
		SysClock c = new SysClock();
		TimeDiff d = c.start();
		assertTrue(d.isRunning());
		assertFalse(d.isStopped());
		Thread.sleep(101);
		d.stop();
		assertFalse(d.stopIfRunning());
		try {
			d.stop();
			fail();
		} catch(IllegalStateException e) {}
		assertTrue(d.getMillis() > 100);
		assertTrue(d.getMicro() > 100000);
		assertTrue(d.getNano() > 100000000);
		assertTrue(d.getMillis() < 200);
	}

	@Test
	public void testDiffTryStop() throws InterruptedException {
		TimeDiff d = CLOCK.start();
		Thread.sleep(101);
		assertTrue(d.stopIfRunning());
		assertFalse(d.stopIfRunning());
		assertFalse(d.stopIfRunning());
		assertTrue(d.getMillis() > 100);
		assertTrue(d.getMillis() < 200);
	}

	@Test
	public void testWallClock() {
		long epoch = CLOCK.getWallClockEpochUtcMillis();
		long epoch2 = System.currentTimeMillis();
		assertTrue(Math.abs(epoch - epoch2) < 20);
	}

	@Test
	public void testMeasure() throws InterruptedException {
		long dur = CLOCK.elapsed(() -> {try {Thread.sleep(300);}catch(Exception e){}});
		assertTrue(dur >= 300 && dur < 300 + 10);
	}

	
	
	
	@Test
	public void testToISO() {
		long epochMillis = 23 * 3600 * 1000;//23.00  1/1/1970
		assertEquals("1970-01-01T23:00:00Z", CLOCK.toISO8601UtcSecs(epochMillis));
		
//		IsoChronology chrono = IsoChronology.INSTANCE;
//		//03:00 of 10 feb 2010 in Switzerland (localtime) (UTC+1)
//		long l1 = chrono.localDateTime(temporal)withZone(ZoneId.of("Europe/Zurich"))
//					.getDateTimeMillis(2011, 2, 10, 3*3600000);
//		//03:00 of 10 aug 2010 in Switzerland (localtime) (UTC+2)
//		long l2 = chrono.withZone(DateTimeZone.forID("Europe/Zurich"))
//					.getDateTimeMillis(2011, 8, 10, 3*3600000);
//		assertEquals("2011-02-10T02:00:00Z", dt.toISO8601UtcSecs(l1));
//		assertEquals("2011-08-10T01:00:00Z", dt.toISO8601UtcSecs(l2));
		
//TODO make it work		assertEquals("1999-02-02T00:00:00Z", CLOCK.toISO8601UtcSecs(
//				CLOCK.fromISO8601UtcSecs("1999-02-02")));
	}

	//TODO
	@Ignore
	@Test
	public void testParseRfc882() {
//		DateTimeZone zz = DateTimeZone.forID("PDT");
		//DateTimeFormatter f = 
//		DateTimeFormat.forPattern("EEE dd MMM yyyy HH:mm:ss zzz").withLocale(Locale.US)
		//DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss zzz");
		//TODO DateTime t = f.parseDateTime("Fri, 21 Oct 2011 00:00:41 PST");
		//dt.parseHttpDate822("Fri, 21 Oct 2011 00:00:41 PDT");
	}
}
