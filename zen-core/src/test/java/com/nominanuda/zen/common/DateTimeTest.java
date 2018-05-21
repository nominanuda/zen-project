package com.nominanuda.zen.common;

import static com.nominanuda.zen.common.DateTimeUtils.DT;

import org.joda.time.DateTimeZone;
import org.junit.Test;

public class DateTimeTest {
	private final static String[] TEST_DATES = new String[] {
		"2017-11-23",
		"1969-06-01",
		"2015-12-01T09:30:00",
		"2015-12-01T09:30:00Z",
		"2015-12-01T09:30:00-08:00",
		"2017-09-08T00:00:00+00:00"
	};
	private final static DateTimeZone[] TEST_ZONES = new DateTimeZone[] {
		DateTimeZone.forID("Europe/Rome"),
		DateTimeZone.forID("Europe/Zurich"),
		DateTimeZone.forID("Europe/London"),
		DateTimeZone.forID("US/Eastern")
	};
	
	@Test
	public void test() {
		for (String date : TEST_DATES) {
			System.out.println("\n === " + date + " ===");
			long m = DT.fromISO8601UtcAny(date);
			System.out.println(DT.toISO8601Millis(m, null) + " (parsed as UTC)");
			for (DateTimeZone dtz : TEST_ZONES) {
				m = DT.fromISO8601Any(date, dtz);
				System.out.println(DT.toISO8601Millis(m, null) + " (parsed as " + dtz.getID() + ", local: " + DT.toISO8601Millis(m, dtz) + ")");
			}
		}
	}
}
