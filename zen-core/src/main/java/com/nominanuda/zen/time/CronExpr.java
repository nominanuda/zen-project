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
package com.nominanuda.zen.time;

import java.text.ParseException;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

import javax.annotation.Nullable;


class CronExpr {
	private final CronExpression quartzExpr;

	/**
	 * timezone is defaulted to UTC
	 */
	public CronExpr(String cronExpression) throws IllegalArgumentException {
		this(cronExpression, ZoneId.of("UTC"));
	}
	/**
	 * @see org.joda.time.DateTimeZone#forID(String)
	 */
	public CronExpr(String cronExpression, String timeZone) throws IllegalArgumentException {
			this(cronExpression, ZoneId.of(timeZone));
	}
	private CronExpr(String cronExpression, ZoneId zid) throws IllegalArgumentException {
		try {
			quartzExpr = new CronExpression(cronExpression);
			quartzExpr.setTimeZone(TimeZone.getTimeZone(zid.getId()));
		} catch (ParseException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public @Nullable Long getTimeAfter(long afterTimeUtcEpochMills) {
		Date res = quartzExpr.getTimeAfter(new Date(afterTimeUtcEpochMills));
		return res == null ? null : res.getTime();
	}

	public @Nullable Long getTimeAfterNow() {
		return getTimeAfter(System.currentTimeMillis());
	}

	public boolean hasTimeAfter(long afterTimeUtcEpochMills) {
		return getTimeAfter(afterTimeUtcEpochMills) != null;
	}

	@Override
	public String toString() {
		return quartzExpr.getCronExpression();
	}
}