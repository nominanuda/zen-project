package com.nominanuda.zen.obj;

import static com.nominanuda.zen.seq.Seq.SEQ;
import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.nominanuda.zen.obj.wrap.ObjWrapper;
import com.nominanuda.zen.obj.wrap.Wrap;
import com.nominanuda.zen.obj.wrap.getter.CollectionCastGetter;
import com.nominanuda.zen.obj.wrap.getter.MapGetter;
import com.nominanuda.zen.obj.wrap.getter.SimpleGetter;

public class WrapGettersTest {
	private final static Wrap DEFAULT_WF = Wrap.WF;
	private final static Wrap CASTCOLL_WF = new Wrap(MapGetter.GETTER, CollectionCastGetter.GETTER, SimpleGetter.GETTER);

	private final static Obj OBJ = Obj.make(
		"aNullbool", null,
		"abool", false,
		"aNullBool", null,
		"aBool", true,
		"aBoolList", Arr.make(true,  null, false),
		"aBoolMap", Obj.make("first", true, "second", null, "third", false),
		"aNullNum", null,
		"aNum", 123,
		"aNumList", Arr.make(1, null, 3),
		"aNumMap", Obj.make("first", 1, "second", null, "third", 3),
		"aNullStr", null,
		"aStr", "abc",
		"aStrList", Arr.make("a", null, "c"),
		"aStrMap", Obj.make("first", "a", "second", null, "third", "c"),
		"testWrapper", Obj.make(
			// all nulls
		)
	);
	
	interface TypedMap<T> extends ObjWrapper {
		T first();
		T second();
		T third();
	}
	interface BoolMap extends TypedMap<Boolean> {}
	interface NumMap extends TypedMap<Number> {}
	interface StrMap extends TypedMap<String> {}
	
	interface DefaultWrapper extends ObjWrapper {
		boolean aNullbool();
		boolean abool();
		Boolean aNullBool();
		Boolean aBool();
		List<Boolean> aBoolList();
		BoolMap aBoolMap();
		Number aNullNum();
		Number aNum();
		List<Number> aNumList();
		NumMap aNumMap();
		String aNullStr();
		String aStr();
		List<String> aStrList();
		StrMap aStrMap();
	}
	
	interface CastCollWrapper extends ObjWrapper {
		List<Boolean> aNullbool();
		List<Boolean> abool();
		List<Boolean> aNullBool();
		List<Boolean> aBool();
		List<Boolean> aBoolList();
		List<BoolMap> aBoolMap();
		List<Number> aNullNum();
		List<Number> aNum();
		List<Number> aNumList();
		List<NumMap> aNumMap();
		List<String> aNullStr();
		List<String> aStr();
		List<String> aStrList();
		List<StrMap> aStrMap();
		List<CastCollWrapper> testWrapper();
		List<CastCollWrapper> unexistingWrapper();
	}
	
	
	@Test
	public void test() {
		DefaultWrapper defaultWrap = DEFAULT_WF.wrap(OBJ, DefaultWrapper.class);
		
		assertEquals(false, defaultWrap.aNullbool());
		assertEquals(false, defaultWrap.abool());
		
		assertEquals(null, defaultWrap.aNullBool());
		assertEquals(true, defaultWrap.aBool());
		assertEquals(SEQ.buildList(LinkedList.class, true, null, false), defaultWrap.aBoolList());
		assertEquals(true, defaultWrap.aBoolMap().first());
		assertEquals(null, defaultWrap.aBoolMap().second());
		assertEquals(false, defaultWrap.aBoolMap().third());
		
		assertEquals(null, defaultWrap.aNullNum());
		assertEquals(123, defaultWrap.aNum());
		assertEquals(SEQ.buildList(LinkedList.class, 1, null, 3), defaultWrap.aNumList());
		assertEquals(1, defaultWrap.aNumMap().first());
		assertEquals(null, defaultWrap.aNumMap().second());
		assertEquals(3, defaultWrap.aNumMap().third());
		
		assertEquals(null, defaultWrap.aNullStr());
		assertEquals("abc", defaultWrap.aStr());
		assertEquals(SEQ.buildList(LinkedList.class, "a", null, "c"), defaultWrap.aStrList());
		assertEquals("a", defaultWrap.aStrMap().first());
		assertEquals(null, defaultWrap.aStrMap().second());
		assertEquals("c", defaultWrap.aStrMap().third());

	
		CastCollWrapper castCollWrap = CASTCOLL_WF.wrap(OBJ, CastCollWrapper.class);
		
		assertList(castCollWrap.aNullbool());
		assertList(castCollWrap.abool(), false);
		
		assertList(castCollWrap.aNullBool());
		assertList(castCollWrap.aBool(), true);
		assertList(castCollWrap.aBoolList(), true, null, false);
		assertEquals(true, castCollWrap.aBoolMap().get(0).first());
		assertEquals(null, castCollWrap.aBoolMap().get(0).second());
		assertEquals(false, castCollWrap.aBoolMap().get(0).third());
		
		assertList(castCollWrap.aNullNum());
		assertList(castCollWrap.aNum(), 123);
		assertList(castCollWrap.aNumList(), 1, null, 3);
		assertEquals(1, castCollWrap.aNumMap().get(0).first());
		assertEquals(null, castCollWrap.aNumMap().get(0).second());
		assertEquals(3, castCollWrap.aNumMap().get(0).third());
		
		assertList(castCollWrap.aNullStr());
		assertList(castCollWrap.aStr(), "abc");
		assertList(castCollWrap.aStrList(), "a", null, "c");
		assertEquals("a", castCollWrap.aStrMap().get(0).first());
		assertEquals(null, castCollWrap.aStrMap().get(0).second());
		assertEquals("c", castCollWrap.aStrMap().get(0).third());
		
		
		assertList(castCollWrap.testWrapper().get(0).aNullbool());
		assertList(castCollWrap.testWrapper().get(0).abool());
		assertList(castCollWrap.testWrapper().get(0).aNullBool());
		assertList(castCollWrap.testWrapper().get(0).aBool());
		assertList(castCollWrap.testWrapper().get(0).aBoolList());
		assertList(castCollWrap.testWrapper().get(0).aBoolMap());
		assertList(castCollWrap.testWrapper().get(0).aNullNum());
		assertList(castCollWrap.testWrapper().get(0).aNum());
		assertList(castCollWrap.testWrapper().get(0).aNumList());
		assertList(castCollWrap.testWrapper().get(0).aNumMap());
		assertList(castCollWrap.testWrapper().get(0).aNullStr());
		assertList(castCollWrap.testWrapper().get(0).aStr());
		assertList(castCollWrap.testWrapper().get(0).aStrList());
		assertList(castCollWrap.testWrapper().get(0).aStrMap());
		assertList(castCollWrap.testWrapper().get(0).testWrapper());
		
		assertList(castCollWrap.unexistingWrapper());
	}
	
	
	private <T> void assertList(List<T> actualList, T... expectedValues) {
		final int l = expectedValues.length;
		assertEquals(l, actualList.size());
		for (int i = 0; i < l; i++) {
			assertEquals(expectedValues[i], actualList.get(i));
		}
	}
}
