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

import static com.nominanuda.zen.obj.wrap.Wrap.WF;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.nominanuda.zen.obj.wrap.ObjWrapper;
import com.nominanuda.zen.obj.wrap.WrapType;

public class ObjWrapperTest {

	@Test
	public void testItemFactoryAnno() {
		Obj anApiJson = SimpleJixParser.obj("{aList[{type:t1},{type:t2}]}");
		AnApi anApi = WF.wrap(anApiJson, AnApi.class);
		List<TypeChooser> aList = anApi.aList();
		assertTrue(aList.get(0) instanceof BizObject);
		assertTrue(aList.get(1) instanceof BizObject2);
	}
	@WrapType(values={"t1","t2"},types={BizObject.class, BizObject2.class})
	interface TypeChooser {}
	interface AnApi {
		List<TypeChooser> aList();
	}

	@Test
	public void testObjWrapper() {
		BizObject bo = WF.wrap(BizObject.class);
		bo.chain2();
		assertNull(bo.foo());
		bo.foo("FOO");
		assertEquals("FOO", bo.foo());
		assertEquals("FOO", bo.unwrap().fetch("foo"));
		assertEquals("GOT IT !!", bo.overridden());
		
		assertEquals("FOO", bo.chain1("x").chain2(1).foo());
		assertEquals("x", bo.chain1());
		assertEquals(new Integer(1), bo.chain2());

		TArr<BizObject2> arr = Arr.makeTyped(BizObject2.class);
		arr.push(WF.wrap(BizObject2.class));
		bo.unwrap().store("subObjects", arr);
		BizObject2 bo2 = bo.subObjects().get(0);
		assertTrue(bo2 instanceof BizObject2);
	}

	interface BizObject extends ObjWrapper, TypeChooser {
		void foo(String s);
		String foo();
		String chain1();
		BizObject chain1(String s);
		Integer chain2();
		BizObject chain2(int s);
		default String overridden() {
			return "GOT IT !!";
		}
		List<BizObject2> subObjects();
	}

	interface BizObject2 extends ObjWrapper, Obj, TypeChooser {
		
	}

	
	@Test
	public void testPrimitiveWrapping() {
		PrimitiveObject o = WF.wrap(PrimitiveObject.class);
		assertFalse(o.booleanNull());
		assertNull(o.getBooleanNull());
		assertNull(o.getIntegerNull());
		assertNull(o.getDoubleNull());
		assertNull(o.getFloatNull());
		try {
			o.intNull();
			o.doubleNull();
			o.floatNull();
			fail("didn't throw exception as expected"); // should not arrive here
		} catch (Exception e) {
			assertTrue(e instanceof NullPointerException);
		}
	}
	
	interface PrimitiveObject extends ObjWrapper {
		boolean booleanNull();
		Boolean getBooleanNull();
		int intNull();
		Integer getIntegerNull();
		float floatNull();
		Float getFloatNull();
		double doubleNull();
		Double getDoubleNull();
	}
}
