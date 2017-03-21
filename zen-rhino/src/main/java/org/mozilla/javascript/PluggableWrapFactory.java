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

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

public class PluggableWrapFactory extends WrapFactory {
	@SuppressWarnings("unused")
	private final RhinoEmbedding rhinoEmbedding;
	private final List<ToScriptableConvertor> convertors = new LinkedList<ToScriptableConvertor>();

	public PluggableWrapFactory(RhinoEmbedding embedding) {
		this.rhinoEmbedding = embedding;
	}

	@Override
	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope,
			Object javaObject, Class<?> staticType) {
		Scriptable wrap = new NativeJavaObject(scope, javaObject, staticType);
		return wrap;
	}

	@Override
	public @Nullable Object wrap(Context cx, Scriptable scope, Object obj,
			Class<?> static_type) {
		if (obj == null) {
			return super.wrap(cx, scope, obj, static_type);
		} else if (obj instanceof char[]) {
			return super.wrap(cx, scope, new String((char[]) obj), static_type);
		} else {
			for(ToScriptableConvertor c : convertors) {
				if(c.canConvert(obj)) {
					return c.convert(cx, scope, obj);
				}
			}
			return super.wrap(cx, scope, obj, static_type);
		}
	}

	public void setConvertors(List<? extends ToScriptableConvertor> convertors) {
		this.convertors.clear();
		this.convertors.addAll(convertors);
	}
}
