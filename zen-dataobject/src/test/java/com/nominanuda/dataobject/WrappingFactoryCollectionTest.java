package com.nominanuda.dataobject;

import static com.nominanuda.dataobject.DataStructHelper.Z;
import static com.nominanuda.dataobject.WrappingFactory.WF;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class WrappingFactoryCollectionTest {

	@Test
	public void test2() {
		DataObject o11 = Z.obj();
		o11.putArray("obj2s", Z.arr(Z.obj("foo", "bar")));
		assertEquals("bar", WF.wrap(o11, Obj1.class).obj2s().get(0).foo());
		Obj1 o12 = Obj1.make()
			.obj2s(asList(
				Obj2.make()
					.foo("bar")));
		assertEquals(o11, o12.unwrap());
		Obj1 o13 = WF.wrap(o11, Obj1.class);
		assertTrue( DataObjectWrapper.deepEquals(o13, o12));
	}

	public interface Obj1 extends DataObjectWrapper {
		static Obj1 make() {
			return WF.wrap(Obj1.class);
		}
		@Cls(Obj2.class) 
		List<Obj2> obj2s();
		Obj1 obj2s(List<Obj2> l);
	}


	public interface Obj2 extends DataObjectWrapper {
		static Obj2 make() {
			return WF.wrap(Obj2.class);
		}

		Obj2 foo(String s);
		String foo();
	}
}
