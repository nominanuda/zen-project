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
package org.mozilla.javascript;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.mozilla.javascript.RhinoException;

public class WrappedRhinoException extends RuntimeException {
	private static final long serialVersionUID = 7289223499267384901L;
	private RhinoException wrappedEx;
	private StackTraceFilter stackTraceFilter = new StackTraceFilter();

	public WrappedRhinoException(RhinoException rhinoException) {
		this.wrappedEx = rhinoException;
	}

	public final String getMessage() {
		return wrappedEx.getMessage();
	}

	public String getLocalizedMessage() {
		return wrappedEx.getLocalizedMessage();
	}

	public void printStackTrace() {
		printStackTrace(System.err);
	}

	public void printStackTrace(PrintWriter s) {
		if (stackTraceFilter != null) {
			s.append(wrappedEx.getMessage());
			s.append("\n");
			StackTraceElement[] stack = stackTraceFilter
					.filter(getStackTrace());
			for (int i = 0; i < stack.length; i++) {
				s.println("\tat " + stack[i]);
			}
			s.append("\n");
			wrappedEx.printStackTrace(s);
		} else {
			wrappedEx.printStackTrace(s);
		}
	}

	public void printStackTrace(PrintStream s) {
		this.printStackTrace(new PrintWriter(s));
	}

	public Throwable getCause() {
		return wrappedEx.getCause();
	}

	public synchronized Throwable initCause(Throwable cause) {
		return wrappedEx.initCause(cause);
	}

	public synchronized Throwable filslInStackTrace() {
		return wrappedEx.fillInStackTrace();
	}

	public StackTraceElement[] getStackTrace() {
		return wrappedEx.getStackTrace();
	}

	public void setStackTrace(StackTraceElement[] stackTrace) {
		wrappedEx.setStackTrace(stackTrace);
	}

	public String toString() {
		return wrappedEx.toString();
	}

	public class StackTraceFilter {
		public StackTraceElement[] filter(StackTraceElement[] stackTraceElements){
			List<StackTraceElement> newStacks = new LinkedList<StackTraceElement>();
			for (StackTraceElement stack : stackTraceElements){
				if (stack.getFileName().endsWith(".js") && stack.getLineNumber() > -1){
					newStacks.add(stack);
				}
			}
			newStacks.addAll(Arrays.asList(stackTraceElements));
			return newStacks.toArray(new StackTraceElement[newStacks.size()]);
		}
	}
}
