package com.nominanuda.dataobject;

import static com.nominanuda.dataobject.DataStructHelper.Z;
import static com.nominanuda.dataobject.WrappingFactory.WF;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.nominanuda.lang.Check;

public class WrapperItemFactoryTest {

	@Test
	public void test() {
		DataObject o1 = Z.obj("type", "t1");
		DataObject o2 = Z.obj("type", "t2");
		DataArray arr = Z.newArray();
		arr.add(o1);
		arr.add(o2);
		DataObject o3 = Z.obj("p1", o1, "p2", o2);
		DataObject o4 = Z.obj("list", arr, "map", o3);
		Holder<SomeObjDomain> h  = (Holder<SomeObjDomain>)WF.wrap(o4, Holder.class);
		assertTrue(h.list().get(0) instanceof Type1);
		assertTrue(h.map().get("p2") instanceof Type2);
	}

	interface Holder<T> {
		List<SomeObjDomain> list();
		Holder<SomeObjDomain> list(List<SomeObjDomain> l);

		Map<String,SomeObjDomain> map();
		Holder<SomeObjDomain> map(Map<String, SomeObjDomain> m);
	}
	interface SomeObjDomain extends WrapperItemFactory {
		static SomeObjDomain wrap(DataObject ds) {
			String type = ds.asObject().getStrictString("type");
			return 
				"t1".equals(type)
					? WF.wrap(ds, Type1.class)
				: "t2".equals(type)
					? WF.wrap(ds, Type2.class)
				: Check.illegalargument.fail();
		}
	}

	interface Type1 extends SomeObjDomain {}
	interface Type2 extends SomeObjDomain {}
}
