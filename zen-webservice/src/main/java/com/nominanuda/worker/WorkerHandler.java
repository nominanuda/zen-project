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
package com.nominanuda.worker;

import com.nominanuda.web.http.Http500Exception;
import com.nominanuda.web.mvc.CommandHandler;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;
import com.nominanuda.zen.worker.AbsWorker;


/**
 * Use an instance of this class to wrap a worker
 * in order to control it through http requests.
 *
 */
public class WorkerHandler implements CommandHandler {
	public static final String P_COMMAND = "_cmd";
	public static final String CMD_STATUS = "status", CMD_WORK = "work", CMD_STOP = "stop";
	
	private final AbsWorker worker;
	
	public WorkerHandler(AbsWorker worker) {
		this.worker = worker;
	}
	
	
	@Override
	public Object handle(Stru _cmd) throws Exception {
		try {
			Obj cmd = _cmd.asObj();
			String command = cmd.remove(P_COMMAND).toString();
			switch (command) {
			case CMD_WORK:
				return worker.work(cmd);
			case CMD_STATUS:
				return worker.status();
			case CMD_STOP:
				return worker.stop();
			default:
				throw new IllegalArgumentException("unknown command:" + command);
			}
		} catch (Exception e) {
			throw new Http500Exception(e);
		}
	}
}
