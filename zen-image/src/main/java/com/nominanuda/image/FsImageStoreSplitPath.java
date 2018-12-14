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
package com.nominanuda.image;

import java.io.File;

import com.nominanuda.zen.common.Check;


public class FsImageStoreSplitPath extends FsImageStore {
	private ImgFsPathConvertor pathEncoder = new ImgFsPathConvertor();


	@Override
	protected File file(String key, boolean allowCreate) {
		File dir = new File(path, pathEncoder.getDirPath(key));
		if (allowCreate && !dir.exists()) {
			Check.illegalstate.assertTrue(dir.mkdirs());
		}
		return new File(dir, key);
	}
	
	
	/* setters */

	public void setPathEncoder(ImgFsPathConvertor pathEncoder) {
		this.pathEncoder = pathEncoder;
	}
}
