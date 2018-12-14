/*
 * Copyright 2008-2018 the original author or authors.
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


public class Stepper {
	public static interface Controller {
		boolean next();
		boolean paused();
		boolean enabled();
		void enable(boolean enable);
	}
	
	private Boolean paused = false;
	private boolean enabled = false;
	private final Controller controller = new Controller() {
		@Override
		public boolean next() {
			return Stepper.this.next();
		}
		
		@Override
		public boolean paused() {
			return paused;
		}
		
		@Override
		public boolean enabled() {
			return enabled;
		}
		
		@Override
		public void enable(boolean enable) {
			enabled = enable;
		}
	};
	
	private synchronized boolean next() {
		if (paused) {
			paused = false;
			notifyAll();
			return true;
		}
		return false;
	}
	
	public synchronized void interrupt() {
		if (enabled) {
			paused = true;
			while (paused) {
				try {
					wait();
				} catch (InterruptedException e) {
					paused = false;
				}
			}
		}
	}
	
	public Controller controller() {
		return controller;
	}
}
