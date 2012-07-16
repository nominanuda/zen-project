package com.nominanuda.dataobject;

import org.junit.*;
import static org.junit.Assert.*;
import static com.nominanuda.dataobject.Zen.*;

public class ZenTest {
	@Test
	public void shouldFlatDataObject$MakeAttrValuesList() {
		assertEquals(Z.object("a", "1", "b", "2").toString(), Z.object(Z.array(Z.object("a", "1", "b", "2"))).toString());
	}
}
