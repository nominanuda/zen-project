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
package com.nominanuda.rhino.host;

import static com.nominanuda.zen.common.Str.STR;

import java.util.Arrays;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Console extends ScriptableObject {
	public final static String HOSTOBJ_KEY = "CONSOLE";
	private static final long serialVersionUID = 924498336863426363L;
	private static final RhinoLogger logger = new RhinoLogger();
	
	private static class RhinoLogger {
		private static final Logger log = LoggerFactory.getLogger("rhinoConsole");
		
		private void log(Object... args) {
			log.info(STR.join(",", Arrays.asList(args)));
		}
		
		private void info(Object... args) {
			log.info(STR.join(",", Arrays.asList(args)));
		}

		private void debug(Object... args) {
			log.debug(STR.join(",", Arrays.asList(args)));
		}

		private void warn(Object... args) {
			log.warn(STR.join(",", Arrays.asList(args)));
		}

		private void error(Object... args) {
			log.error(STR.join(",", Arrays.asList(args)));
		}
	}
	

	public void jsConstructor() {}

	@Override
	public String getClassName() {
		return "Console";
	}
	
	public static void jsFunction_log(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		logger.log(args);
	}
	
	public static void jsFunction_info(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		logger.info(args);
	}
	
	public static void jsFunction_debug(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		logger.debug(args);
	}
	
	public static void jsFunction_warn(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		logger.warn(args);
	}
	
	public static void jsFunction_error(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		logger.error(args);
	}
	
	
	public static Object asJavaHostObj() {
		return logger;
	}
}
