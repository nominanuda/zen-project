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
package com.nominanuda.zen.worker;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import com.nominanuda.zen.obj.Obj;


public abstract class AbsBusyFreeWorker<T> extends AbsWorker {
	private final AtomicBoolean busy = new AtomicBoolean(false);
	private IBusyFreeWorkerListener listener;
	private boolean doRun = false;
	private Thread thread = null;

	
	/**
	 * Useful to have another bean being alerted
	 * when reaching the different task statuses
	 */
	public interface IBusyFreeWorkerListener {
		void onTaskSucceeded();
		void onTaskFailed();
		void onTaskEnded();
	}
	
	
	/* admin methods */
	
	@Override
	public Obj work(Obj cmd) throws Exception {
		if (busy.compareAndSet(false, true)) {
			doRun = true;
			updateTaskStatus(Obj.make()); // reset it
			final Callable<T> task = newTask(cmd);
			(thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						T result = task.call();
						onTaskSuccess(result);
						if (listener != null) {
							listener.onTaskSucceeded();
						}
						lastRun(true);
					} catch (Exception e0) {
						lastRun(false);
						onTaskError(e0);
						if (listener != null) {
							listener.onTaskFailed();
						}
					} finally {
						doRun = false;
						thread = null;
						busy.set(false);
						onTaskEnd();
						if (listener != null) {
							listener.onTaskEnded();
						}
					}
				}
			})).start();
			return super.work(cmd);
		}
		return status();
	}
	
	
	@Override
	public Obj stop() {
		doRun = false;
		return super.stop();
	}
	
	
	@Override
	public boolean busy() {
		return busy.get();
	}


	
	/* task methods */
	
	protected abstract Callable<T> newTask(Obj cmd) throws Exception;
	
	protected void onTaskSuccess(T result) throws Exception {
		// to be overridden
	}
	protected void onTaskError(Exception e) {
		// to be overridden
	}
	protected void onTaskEnd() {
		// to be overridden;
	}
	
	protected final boolean doRun() {
		return doRun;
	}
	
	
	/* thread utils (ex for testing) */
	
	public void join() throws InterruptedException {
		if (thread != null) {
			thread.join();
		}
	}
	
	
	/* setters */
	
	public void setListener(IBusyFreeWorkerListener listener) {
		this.listener = listener;
	}
}
