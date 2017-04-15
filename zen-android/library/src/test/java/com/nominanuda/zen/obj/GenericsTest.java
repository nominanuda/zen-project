package com.nominanuda.zen.obj;

import com.nominanuda.zen.obj.wrap.ObjWrapper;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class GenericsTest {
	
	interface PagedResults<T> extends ObjWrapper {
		Integer start();
		PagedResults<T> start(Integer i);
		Integer count();
		PagedResults<T> count(Integer i);
		List<T> results();
		PagedResults<T> results(List<T> l);
		String cursor();
		PagedResults<T> cursor(String off);
	}
	
	interface Post extends ObjWrapper {
		Post code(Integer id);
		Integer code();
	}
	
	interface MyTest {
		PagedResults<Post> listWall();
	}
	
	
	@Test
	public void test() throws ClassNotFoundException {
		for (Method m : MyTest.class.getMethods()) {
			Type t1 = m.getGenericReturnType();
			if (t1 instanceof ParameterizedType) {
				ParameterizedType t2 = (ParameterizedType)t1;
				Type[] actualTypeArgs = t2.getActualTypeArguments();
				if (actualTypeArgs != null && actualTypeArgs.length == 1) {
					Type t = actualTypeArgs[0];
					System.out.println(t.toString());
					Class.forName(t.toString()).toString();
//					System.out.println(Class.forName(t.getTypeName()).toString());
				}
			}
		}
	}
}
