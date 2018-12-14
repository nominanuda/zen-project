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

//import com.nominanuda.web.http.Http500Exception;
//import com.nominanuda.web.mvc.CommandHandler;
import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.obj.Obj;

public abstract class AbsWorker {
	public static final String P_BUSY = "busy", P_LRUN = "lastRun", P_STATUS = "status", P_TASK = "task";
	public static final String STATUS_BUSY = "busy", STATUS_FREE = "free", STATUS_STARTED = "started", STATUS_STOPPING = "stopping";
	public static final String LRUN_OK = "ok", LRUN_KO = "ko";
	
	private Obj taskStatus = Obj.make();
	private boolean stopping = false;
	private Boolean lastRun = null; // null not run , false err , true ok

	
	public Obj work(Obj cmd) throws Exception {
		stopping = false;
		return status().with(P_STATUS, STATUS_STARTED);
	}
	public Obj work() throws Exception {
		return work(Obj.make());
	}
	
	public abstract boolean busy();
	
	public Obj status() {
		if (busy()) {
			onTaskStatus(taskStatus);
			return Obj.make(
				P_STATUS, stopping ? STATUS_STOPPING : STATUS_BUSY,
				P_BUSY, true,
				P_TASK, taskStatus
			);
		}
		return Obj.make(
			P_STATUS, STATUS_FREE,
			P_BUSY, false,
			P_TASK, (lastRun != null ? taskStatus : null),
			P_LRUN, (lastRun != null ? lastRun ? LRUN_KO : LRUN_OK : null)
		);
	}
	
	public Obj stop() {
		stopping = true;
		return status();
	}
	
	protected final void lastRun(boolean ok) {
		lastRun = ok;
	}
	

	/* task methods */
	
	protected void onTaskStatus(Obj taskStatus) {
		// to be overridden
	}
	protected final void updateTaskStatus(Obj status) {
		if (busy()) {
			taskStatus = Check.ifNull(status, taskStatus);
		}
	}
	protected final void updateTaskStatus(String name, Object value) {
		if (busy()) {
			taskStatus.put(name, value);
		}
	}
}
