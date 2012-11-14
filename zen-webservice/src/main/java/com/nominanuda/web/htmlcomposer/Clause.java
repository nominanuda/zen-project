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
package com.nominanuda.web.htmlcomposer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;

import com.nominanuda.lang.Check;
import com.nominanuda.lang.Strings;


public abstract class Clause {

	public abstract boolean match(String tag, Attributes atts);

	private static final Pattern attEqualsRex = Pattern.compile("\\[(\\w+)=\"([^\"]*)\"\\]");
	private static final Pattern tagRex = Pattern.compile("(\\w+)");
	public static Clause build(String bit) {
		if(bit.startsWith(".")) {
			return new ClassMatchClause(bit.substring(1));
		} else if(bit.startsWith("#")) {
			return new ByIdMatchClause(bit.substring(1));
		} else if(bit.contains("#")) {
			String[] tagAndId = bit.split("#");
			Check.illegalargument.assertEquals(2, tagAndId.length);
			CompoundClause cc = new CompoundClause();
			cc.add(new TagMatchClause(tagAndId[0]));
			cc.add(new ByIdMatchClause(tagAndId[1]));
			return cc;
		} else if(bit.contains(".")) {
			String[] tagAndId = bit.split("\\.");
			Check.illegalargument.assertEquals(2, tagAndId.length);
			CompoundClause cc = new CompoundClause();
			cc.add(new TagMatchClause(tagAndId[0]));
			cc.add(new ClassMatchClause(tagAndId[1]));
			return cc;
		} else if("*".equals(bit)) {
			return new AsteriskMatchClause();
		}
		Matcher m = attEqualsRex.matcher(bit);
		if(m.find()) {
			return new AttrEqualsClause(m.group(1), m.group(2));
		}
		if(tagRex.matcher(bit).matches()) {
			return new TagMatchClause(bit);
		}
		throw new IllegalArgumentException("unrecognized selector expression"+bit);
	}
	public static boolean isAttributeClause(String bit) {
		return bit.startsWith("[");
	}

	/////////////////////////////////////////////////////////
	public static class AsteriskMatchClause extends Clause {

		@Override
		public boolean match(String tag, Attributes atts) {
			return true;
		}
		
	}

	public static class AttrEqualsClause extends Clause {
		private final String name;
		private final String value;
		public AttrEqualsClause(String name, String value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public boolean match(String tag, Attributes atts) {
			String val = atts.getValue(name);
			if(Strings.nullOrBlank(val)) {
				return false;
			}
			return Strings.splitAndTrim(val, "\\s+").contains(this.value);
		}
		
	}

	public static class ByIdMatchClause extends Clause {
		private final String value;
		public ByIdMatchClause(String value) {
			this.value = value;
		}

		@Override
		public boolean match(String tag, Attributes atts) {
			String val = atts.getValue("id");
			if(Strings.nullOrBlank(val)) {
				return false;
			}
			return val.trim().equals(this.value);
		}
		
	}

	public static class ClassMatchClause extends Clause {
		private final String clazz;
		public ClassMatchClause(String bit) {
			this.clazz = bit;
		}

		@Override
		public boolean match(String tag, Attributes atts) {
			String val = atts.getValue("class");
			if(Strings.nullOrBlank(val)) {
				return false;
			}
			return Strings.splitAndTrim(val, "\\s+").contains(this.clazz);
		}
		
	}

	public static class TagMatchClause extends Clause {
		private final String tag;
		public TagMatchClause(String bit) {
			this.tag = bit;
		}

		@Override
		public boolean match(String tag, Attributes atts) {
			return this.tag.equals(tag);
		}
		
	}
}