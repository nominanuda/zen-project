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

public class ImgFsPathConvertor {
	
	public String apply(String x) {
		return getDirPath(x) + "/" + x;
	}

	public boolean canConvert(Object o) {
		return true;
	
	}

	public String getDirPath(String x) {
		return x.substring(0, 1) + "/" + x.substring(0, 2);
	}

}
