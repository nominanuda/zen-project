/*
 * Copyright 2008-2016 the original author or authors.
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
package com.nominanuda.zen.obj;

import static com.nominanuda.zen.common.Check.illegalstate;

import java.util.Stack;

import com.nominanuda.zen.stereotype.Factory;

public class JixBuilder implements JixHandler, Factory<Object> {
	private Any result;
	private Any cur;
	private Key pendingKey;
	private Stack<Any> parentHierarchy = new Stack<Any>();

	public void startObj() {
		if(result == null) {
			result = Obj.make();
			cur = result;
		} else {
			if(cur.isArr()) {
				cur = cur.asArr().push(Obj.make());
			} else {
				cur = ((ObjImpl)cur).storeAny(pendingKey, Obj.make());
			}
		}
		parentHierarchy.push(cur);
	}

	public void endObj() {
		parentHierarchy.pop();
		cur = parentHierarchy.isEmpty() ? null : parentHierarchy.peek();
	}

	public void key(Key key) {
		pendingKey = key;
	}

	public void startArr() {
		if(result == null) {
			result = Arr.make();
			cur = result;
		} else {
			if(cur.isArr()) {
				cur = cur.asArr().push(Arr.make());
			} else {
				cur = ((ObjImpl)cur).storeAny(pendingKey, Arr.make());
			}
		}
		parentHierarchy.push(cur);
	}

	public void endArr() {
		parentHierarchy.pop();
		cur = parentHierarchy.isEmpty() ? null : parentHierarchy.peek();
	}

	public void val(Val value) {
		if(result == null) {
			result = value;
			cur = result;
		} else if(cur.isArr()) {
			((Arr)cur).push(value.get());
		} else {
			((Obj)cur).storeAny(pendingKey, value);
		}
	}

	@Override
	public Object get() {
		illegalstate.assertTrue(parentHierarchy.isEmpty(), "result not set");
		return result.toJavaObjModel();
	}

	public Any getAny() {
		illegalstate.assertTrue(parentHierarchy.isEmpty(), "result not set");
		return illegalstate.assertNotNull(result, "result not set");
	}

	public Obj getObj() {
		illegalstate.assertTrue(parentHierarchy.isEmpty(), "result not set");
		return illegalstate.assertNotNull(result.asObj());
	}

	public Arr getArr() {
		illegalstate.assertTrue(parentHierarchy.isEmpty(), "result not set");
		return illegalstate.assertNotNull(result.asArr());
	}

}
