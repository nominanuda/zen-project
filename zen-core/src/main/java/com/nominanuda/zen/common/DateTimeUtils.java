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
package com.nominanuda.zen.common;

import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.datatype.DatatypeFactory;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;


@ThreadSafe
public class DateTimeUtils { 
	public static final DateTimeUtils DT = new DateTimeUtils();
	public final static String ISO_EPOCH_DT_SEC = "1970-01-01T00:00:00Z";
	
	private static final String DATE_RE = "\\d{4}-\\d{2}-\\d{2}";
	private static final String SECS_RE = "T\\d{2}:\\d{2}:\\d{2}";
	private static final String MILLIS_RE = "\\.\\d{3}";
	private static final String TZ_RE = "(?:Z|[\\+-]\\d{2}[:]?(?:\\d{2})?)?";
	private static final Pattern PATTERN__DATE = Pattern.compile("^" + DATE_RE + "$");
	private static final Pattern PATTERN__DATE_SECS = Pattern.compile("^" + DATE_RE + SECS_RE + "$");
	private static final Pattern PATTERN__DATE_SECS_TZ = Pattern.compile("^" + DATE_RE + SECS_RE + TZ_RE + "$");
	private static final Pattern PATTERN__DATE_SECS_MILLIS = Pattern.compile("^" + DATE_RE + SECS_RE + MILLIS_RE + "$");
	private static final Pattern PATTERN__DATE_SECS_MILLIS_TZ = Pattern.compile("^" + DATE_RE + SECS_RE + MILLIS_RE + TZ_RE + "$");

	private static final DateTimeFormatter FORMATTER__ISO_DATE = ISODateTimeFormat.date();
	private static final DateTimeFormatter FORMATTER__ISO_SECS = ISODateTimeFormat.dateHourMinuteSecond();
	private static final DateTimeFormatter FORMATTER__ISO_SECS_TZ = ISODateTimeFormat.dateTimeNoMillis();
	private static final DateTimeFormatter FORMATTER__ISO_MILLIS = ISODateTimeFormat.dateHourMinuteSecondMillis();
	private static final DateTimeFormatter FORMATTER__ISO_MILLIS_TZ = ISODateTimeFormat.dateTime();
	private static final DateTimeFormatter FORMATTER__ISO_UTC_DATE = FORMATTER__ISO_DATE.withZoneUTC();
	private static final DateTimeFormatter FORMATTER__ISO_UTC_SECS_TZ = FORMATTER__ISO_SECS_TZ.withZoneUTC();
	private static final DateTimeFormatter FORMATTER__ISO_UTC_MILLIS_TZ = FORMATTER__ISO_MILLIS_TZ.withZoneUTC();
	
	private final DatatypeFactory dtf;
	{
		try {
			dtf = DatatypeFactory.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	
	/* ISO8601 -> long */
	
	public long fromISO8601Date(String isoDt, DateTimeZone dtz) throws IllegalArgumentException {
		LocalDate lDate = FORMATTER__ISO_DATE.parseLocalDate(isoDt);
		return lDate.toDateTimeAtStartOfDay(dtz).getMillis(); // avoids bizarre situations like 1969-06-01 in IT (there was no 00:00)
	}
	public long fromISO8601Secs(String isoDt, DateTimeZone dtz) throws IllegalArgumentException {
		return FORMATTER__ISO_SECS.withZone(dtz).parseDateTime(isoDt).getMillis();
	}
	public long fromISO8601Secs(String isoDt) throws IllegalArgumentException {
		return FORMATTER__ISO_SECS_TZ.parseDateTime(isoDt).getMillis();
	}
	public long fromISO8601Millis(String isoDt, DateTimeZone dtz) throws IllegalArgumentException {
		return FORMATTER__ISO_MILLIS.withZone(dtz).parseDateTime(isoDt).getMillis();
	}
	public long fromISO8601Millis(String isoDt) throws IllegalArgumentException {
		return FORMATTER__ISO_MILLIS_TZ.parseDateTime(isoDt).getMillis();
	}
	public long fromISO8601Any(String isoDt, DateTimeZone dtz) throws IllegalArgumentException {
		try {
			if (PATTERN__DATE.matcher(isoDt).matches()) {
				return fromISO8601Date(isoDt, dtz);
			} else if (PATTERN__DATE_SECS.matcher(isoDt).matches()) {
				return fromISO8601Secs(isoDt, dtz);
			} else if (PATTERN__DATE_SECS_TZ.matcher(isoDt).matches()) {
				return fromISO8601Secs(isoDt);
			} else if (PATTERN__DATE_SECS_MILLIS.matcher(isoDt).matches()) {
				return fromISO8601Millis(isoDt, dtz);
			} else if (PATTERN__DATE_SECS_MILLIS_TZ.matcher(isoDt).matches()) {
				return fromISO8601Millis(isoDt);
			}
		} catch (Exception e) {
			// continue to throw
		}
		throw new IllegalArgumentException("unrecognized ISO 8601 date:" + isoDt);
	}

	public long fromISO8601UtcDate(String isoDt) throws IllegalArgumentException {
		return FORMATTER__ISO_UTC_DATE.parseDateTime(isoDt).getMillis();
	}
	public long fromISO8601UtcSecs(String isoDt) throws IllegalArgumentException {
		if (PATTERN__DATE_SECS.matcher(isoDt).matches()) isoDt += "Z";
		return FORMATTER__ISO_UTC_SECS_TZ.parseDateTime(isoDt).getMillis();
	}
	public long fromISO8601UtcMillis(String isoDt) throws IllegalArgumentException {
		if (PATTERN__DATE_SECS_MILLIS.matcher(isoDt).matches()) isoDt += "Z";
		return FORMATTER__ISO_UTC_MILLIS_TZ.parseDateTime(isoDt).getMillis();
	}
	public long fromISO8601UtcAny(String isoDt) throws IllegalArgumentException {
		try {
			if (PATTERN__DATE.matcher(isoDt).matches()) {
				return fromISO8601UtcDate(isoDt);
			} else if (PATTERN__DATE_SECS_TZ.matcher(isoDt).matches()) {
				return fromISO8601UtcSecs(isoDt);
			} else if (PATTERN__DATE_SECS_MILLIS_TZ.matcher(isoDt).matches()) {
				return fromISO8601UtcMillis(isoDt);
			} else {
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("unrecognized ISO 8601 date:" + isoDt, e);
		}
		throw new IllegalArgumentException("unrecognized ISO 8601 date:" + isoDt);
	}
	
	
	/* DateTime -> ISO8601 */

	public String toISO8601Date(DateTime dt, @Nullable DateTimeZone dtz) {
		return FORMATTER__ISO_DATE.withZone(dtz).print(dt);
	}
	public String toISO8601Secs(DateTime dt, @Nullable DateTimeZone dtz) {
		return FORMATTER__ISO_SECS_TZ.withZone(dtz).print(dt);
	}
	public String toISO8601Millis(DateTime dt, @Nullable DateTimeZone dtz) {
		return FORMATTER__ISO_MILLIS_TZ.withZone(dtz).print(dt);
	}
	
	public String toISO8601UtcDate(DateTime dt) {
		return FORMATTER__ISO_UTC_DATE.print(dt);
	}
	public String toISO8601UtcSecs(DateTime dt) {
		return FORMATTER__ISO_UTC_SECS_TZ.print(dt);
	}
	public String toISO8601UtcMillis(DateTime dt) {
		return FORMATTER__ISO_UTC_MILLIS_TZ.print(dt);
	}
	

	/* long -> ISO8601 */
	
	public String toISO8601Date(long utcEpochMillis, @Nullable DateTimeZone dtz) {
		return toISO8601Date(new DateTime(utcEpochMillis, dtz), dtz);
	}
	public String toISO8601Secs(long utcEpochMillis, @Nullable DateTimeZone dtz) {
		return toISO8601Secs(new DateTime(utcEpochMillis, dtz), dtz);
	}
	public String toISO8601Millis(long utcEpochMillis, @Nullable DateTimeZone dtz) {
		return toISO8601Millis(new DateTime(utcEpochMillis, dtz), dtz);
	}
	
	public String toISO8601UtcDate(long utcEpochMillis) {
		return toISO8601UtcDate(new DateTime(utcEpochMillis, DateTimeZone.UTC));
	}
	public String toISO8601UtcSecs(long utcEpochMillis) {
		return toISO8601UtcSecs(new DateTime(utcEpochMillis, DateTimeZone.UTC));
	}
	public String toISO8601UtcMillis(long utcEpochMillis) {
		return toISO8601UtcMillis(new DateTime(utcEpochMillis, DateTimeZone.UTC));
	}
	
	
	/* now -> ISO8601 */
	
	public String nowToISO8601Date(@Nullable DateTimeZone dtz) {
		return toISO8601Secs(new DateTime(dtz), dtz);
	}
	public String nowToISO8601Secs(@Nullable DateTimeZone dtz) {
		return toISO8601Secs(new DateTime(dtz), dtz);
	}
	public String nowToISO8601Millis(@Nullable DateTimeZone dtz) {
		return toISO8601Millis(new DateTime(dtz), dtz);
	}

	public String nowToISO8601UtcDate() {
		return toISO8601UtcSecs(new DateTime(DateTimeZone.UTC));
	}
	public String nowToISO8601UtcSecs() {
		return toISO8601UtcSecs(new DateTime(DateTimeZone.UTC));
	}
	public String nowToISO8601UtcMillis() {
		return toISO8601UtcMillis(new DateTime(DateTimeZone.UTC));
	}
	
	
	
	/* utils */
	
	public boolean isISO8601(String isoDt) {
		try {
			fromISO8601UtcAny(isoDt);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * @return the difference, measured in milliseconds, between
	 * the current time and midnight, January 1, 1970 UTC.
	 */
	public long epochMillis() {
		return System.currentTimeMillis();
	}

	public boolean epochMillisBefore(long millis) {
		return epochMillis() < millis;
	}

	public String msecToXsdDur(long msDur) {
		return dtf.newDuration(msDur).toString();
	}
}
