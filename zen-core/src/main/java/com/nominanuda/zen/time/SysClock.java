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
package com.nominanuda.zen.time;

import static java.time.temporal.ChronoField.INSTANT_SECONDS;
import static java.time.temporal.ChronoField.MILLI_OF_SECOND;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.datatype.DatatypeFactory;

@ThreadSafe
public class SysClock {
	public static final SysClock CLOCK = new SysClock();

	/**
	 * @return static alias of {@link #getWallClockEpochUtcMillis()}
	 */
	public static long now() {
		return CLOCK.systime();
	}
	/**
	 * 
	 * @return epoch in millis with a coarse approximation up to 10ms
	 * it is implemented with performance and possibly context switching avoidance in mind
	 */
	public long getWallClockEpochUtcMillis() {
		return System.currentTimeMillis();
	}

	/**
	 * 
	 * @return alias of {@link #getWallClockEpochUtcMillis()}
	 */
	public long systime() {
		return getWallClockEpochUtcMillis();
	}

	public TimeDiff start() {
		return new TimeDiff(System.nanoTime());
	}

	@ThreadSafe
	public static final class TimeDiff {
		private static final long UNSET_VALUE = -1l;
		private final long start;
		private final AtomicLong end = new AtomicLong(UNSET_VALUE);
		private AtomicLong diff = new AtomicLong(UNSET_VALUE);

		public TimeDiff(long start) {
			this.start = start;
		}

		public TimeDiff stop() throws IllegalStateException {
			stop(true);
			return this;
		}

		public boolean stopIfRunning() {
			return stop(false);
		}

		public boolean isStopped() {
			return end.get() != UNSET_VALUE;
		}

		public boolean isRunning() {
			return end.get() == UNSET_VALUE;
		}

		private boolean stop(boolean throw_) throws IllegalStateException {
			long l = System.nanoTime();
			if(! end.compareAndSet(UNSET_VALUE, l)) {
				if(throw_) {
					throw new IllegalStateException("TimeDiff already stopped");
				} else {
					return false;
				}
			} else {
				diff.set(l - start);
				return true;
			}
		}

		public long getNano() throws IllegalStateException {
			long l = diff.get();
			if(l == UNSET_VALUE) {
				throw new IllegalStateException("TimeDiff still running");
			} else {
				return l;
			}
		}

		public long getMicro() throws IllegalStateException {
			return getNano() / 1000;
		}

		public long getMillis() throws IllegalStateException {
			return getNano() / 1000000;
		}
}

	/**
	 * 
	 * @param task
	 * @return task execution time in millis
	 */
	public long elapsed(Runnable task) {
		TimeDiff s = start();
		task.run();
		return s.stop().getMillis();
	}


	//from DateTimeHelper
	private final DatatypeFactory dtf;
	{
		try {
			dtf = DatatypeFactory.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	public final String ISO_EPOCH_DT_SEC = "1970-01-01T00:00:00Z";

	private static final DateTimeFormatter isoUtcSecs = DateTimeFormatter.ISO_INSTANT;

	private String toISO8601UtcSecs(Instant dt) {
		return isoUtcSecs.format(dt);
	}

	public String toISO8601UtcSecs(long utcEpochMillis) {
		return toISO8601UtcSecs(Instant.ofEpochMilli(utcEpochMillis));
	}

	public String nowToISO8601UtcSecs() {
		return toISO8601UtcSecs(now());
	}

	private long toEpochMillis(TemporalAccessor ta) {
		return ta.get(INSTANT_SECONDS) * 1000 + ta.get(MILLI_OF_SECOND);
	}

	public long fromISO8601UtcSecs(String isoDt) throws IllegalArgumentException {
		return toEpochMillis(isoUtcSecs.parse(isoDt));
	}

	public CronExpr createCronExpr(String cronExpression, String timeZone) {
		return new CronExpr(cronExpression, timeZone);
	}

	public CronExpr createUtcCronExpr(String cronExpression) {
		return new CronExpr(cronExpression);
	}

	public boolean isValidCronExpr(String cronExpression) {
		return CronExpression.isValidExpression(cronExpression);
	}

	/**
	 * @return the millis from epoch GMT or -1 if format is not recognized
	 */
	public long parseHttpDate822(String val) {
		val = val.trim();
		if (val.endsWith(" GMT")) {
			val = val.substring(0, val.length() - 4);
		}
		for(DateTimeFormatter fmt : STD_RFC_822_FMTS) {
			try {
				return toEpochMillis(fmt.parse(val));
			} catch(IllegalArgumentException e) {
			}
		}
		for(DateTimeFormatter fmt : NON_STD_RFC_822_FMTS) {
			try {
				return toEpochMillis(fmt.parse(val));
			} catch(IllegalArgumentException e) {
			}
		}
		return -1;
	}

	private final static List<DateTimeFormatter> STD_RFC_822_FMTS = Arrays.asList(new DateTimeFormatter[] {
		DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz").withLocale(Locale.US),
		DateTimeFormatter.ofPattern("EEE, dd-MMM-yy HH:mm:ss").withLocale(Locale.US).withZone(ZoneId.of("UTC")),
		DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy").withLocale(Locale.US).withZone(ZoneId.of("UTC"))
	});
	private final static List<DateTimeFormatter> NON_STD_RFC_822_FMTS = Arrays.asList(new DateTimeFormatter[] {
		DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss").withLocale(Locale.US).withZone(ZoneId.of("UTC")),
		DateTimeFormatter.ofPattern("EEE dd MMM yyyy HH:mm:ss zzz").withLocale(Locale.US),
		DateTimeFormatter.ofPattern("EEE dd MMM yyyy HH:mm:ss").withLocale(Locale.US).withZone(ZoneId.of("UTC")),
		DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss zzz").withLocale(Locale.US),
		DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss").withLocale(Locale.US).withZone(ZoneId.of("UTC")),
		DateTimeFormatter.ofPattern("EEE MMM-dd-yyyy HH:mm:ss zzz").withLocale(Locale.US),
		DateTimeFormatter.ofPattern("EEE MMM-dd-yyyy HH:mm:ss").withLocale(Locale.US).withZone(ZoneId.of("UTC")),
		DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss zzz").withLocale(Locale.US),
		DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss").withLocale(Locale.US).withZone(ZoneId.of("UTC")),
		DateTimeFormatter.ofPattern("dd-MMM-yy HH:mm:ss zzz").withLocale(Locale.US),
		DateTimeFormatter.ofPattern("dd-MMM-yy HH:mm:ss").withLocale(Locale.US).withZone(ZoneId.of("UTC")),
		DateTimeFormatter.ofPattern("MMM dd HH:mm:ss yyyy zzz").withLocale(Locale.US),
		DateTimeFormatter.ofPattern("MMM dd HH:mm:ss yyyy").withLocale(Locale.US).withZone(ZoneId.of("UTC")),
		DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy zzz").withLocale(Locale.US),
		DateTimeFormatter.ofPattern("EEE, MMM dd HH:mm:ss yyyy zzz").withLocale(Locale.US),
		DateTimeFormatter.ofPattern("EEE, MMM dd HH:mm:ss yyyy").withLocale(Locale.US).withZone(ZoneId.of("UTC")),
		DateTimeFormatter.ofPattern("EEE, dd-MMM-yy HH:mm:ss zzz").withLocale(Locale.US),
		DateTimeFormatter.ofPattern("EEE dd-MMM-yy HH:mm:ss zzz").withLocale(Locale.US),
		DateTimeFormatter.ofPattern("EEE dd-MMM-yy HH:mm:ss").withLocale(Locale.US).withZone(ZoneId.of("UTC")),
	});

	public String msecToXsdDur(long msDur) {
		return dtf.newDuration(msDur).toString();
	}

}
