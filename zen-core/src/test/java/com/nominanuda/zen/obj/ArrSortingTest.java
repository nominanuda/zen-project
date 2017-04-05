package com.nominanuda.zen.obj;

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Comparator;

import org.junit.Test;

public class ArrSortingTest {
	
	@Test
	public void test() {
		Arr arr = Arr.make(
			Obj.make("p", 3),
			Obj.make("p", 7),
			Obj.make("p", 2),
			Obj.make("p", 1)
		);
		
		Collections.sort(arr.ofObj(), new Comparator<Obj>() {
			@Override
			public int compare(Obj o1, Obj o2) {
				return o1.getInt("p") - o2.getInt("p");
			}
		});
		
		int i = 0;
		for (Obj o : arr.ofObj()) {
			int p = o.getInt("p");
			System.out.println(p);
			assertTrue(i < p);
			i = p;
		}
	}
}
