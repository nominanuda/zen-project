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


public class ImgReverseFsPathConvertor extends ImgFsPathConvertor {
	
	@Override
	public String getDirPath(String x) {
		int l = x.length();
		if (l < 2)
			return x + "/" + x + "_";

		// make sure we do not use filename extension
		int i = x.lastIndexOf(".");
		if(i > 0)
			l = i;
		
		return x.substring(l - 2, l - 1) + "/" + x.substring(l - 2, l);
	}

}
