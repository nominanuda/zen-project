package com.nominanuda.zen.obj.wrap.getter;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.Obj;


@ThreadSafe
public class CollectionCastGetter extends CollectionGetter {
	public final static CollectionCastGetter GETTER = new CollectionCastGetter();
	
	protected CollectionCastGetter() {
		// just to avoid constructors during usage from outside
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public Object extract(Obj o, Method getter) throws Throwable {
		final Class<?> type = getter.getReturnType();
		Object ob = o.get(getter.getName());
		if (type.equals(Arr.class)) {
			if (ob instanceof Arr) {
				return ob;
			}
			Arr arr = Arr.make();
			if (ob != null) {
				arr.add(fromObjValue(ob, ob.getClass()));
			}
			return arr;
		}
		Collection<Object> coll = type.isInterface() ? new LinkedList<>() : (Collection<Object>) type.newInstance();
		@Nullable Class<?> itemType = null;
		try {
			itemType = getCollectionReturnComponentType(getter);
		} catch (Exception e) {
			// dynamic mode on
		}
		if (ob instanceof Arr) {
			for (Object v : (Arr)ob) {
				coll.add(fromObjValue(v, itemType));
			}
		} else if (ob != null) {
			coll.add(fromObjValue(ob, itemType));
		}
		return coll;
	}
}
