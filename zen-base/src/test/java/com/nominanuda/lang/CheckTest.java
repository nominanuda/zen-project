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

import org.junit.Assert;

import org.junit.Test;

import static com.nominanuda.lang.Check.*;

public class CheckTest {

	@Test
	public void testAssertions() {
		try {
			notNull(null);
			Assert.fail();
		} catch(NullPointerException e) {}

		try {
			notNull(null, "miao");
			Assert.fail();
		} catch(NullPointerException e) {
			Assert.assertEquals("miao", e.getMessage());
		}

		try {
			illegalstate.fail();
			Assert.fail();
		} catch(IllegalStateException e) {}

		Assert.assertEquals("foo", notNull("foo"));

		Assert.assertEquals("foo", illegalargument.assertNotNull("foo"));

		try {
			illegalargument.assertNotNull(null);
			Assert.fail();
		} catch(IllegalArgumentException e) {}

		try {
			illegalargument.notNullOrBlank("\t \n");
			Assert.fail();
		} catch(IllegalArgumentException e) {}

		Assert.assertEquals(" foo", illegalargument.notNullOrBlank(" foo"));

	}

	@Test
	public void testDefaulting() {
		Assert.assertEquals("foo", Check.ifNull(null, "foo"));
		Assert.assertEquals("bar", Check.ifNull("bar", "foo"));
		Assert.assertEquals(new Integer(2), Check.ifNull(null, 2));
		Assert.assertEquals(new Integer(1), Check.ifNull(1, 2));
		Assert.assertEquals("foo", Check.ifNullOrEmpty("", "foo"));
		Assert.assertEquals("bar", Check.ifNullOrEmpty("bar", "foo"));
		Assert.assertEquals("bar ", Check.ifNullOrBlank("bar ", "foo"));
	}
}
