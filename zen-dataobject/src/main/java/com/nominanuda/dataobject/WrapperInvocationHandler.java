package com.nominanuda.dataobject;

import static com.nominanuda.dataobject.DataStructHelper.STRUCT;
import static com.nominanuda.dataobject.WrappingFactory.WF;
import static java.util.Arrays.asList;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.nominanuda.dataobject.DataArray;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.dataobject.PropertyBag;
import com.nominanuda.lang.Check;

public class WrapperInvocationHandler implements InvocationHandler {
	private final DataObject o;
	//private final Class<?> role;
	private final Set<Method> roleMethods;
	private static final HashSet<Method> nonRoleMethods = new HashSet<Method>();
	static {
		nonRoleMethods.addAll(asList(Object.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(DataObjectWrapper.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(DataStruct.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(DataObject.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(Iterable.class.getDeclaredMethods()));
		nonRoleMethods.addAll(asList(PropertyBag.class.getDeclaredMethods()));
	}

	public WrapperInvocationHandler(DataObject o, Class<?> role) {
		this.o = o != null ? o : STRUCT.newObject();
		//this.role = role;
		roleMethods = new HashSet<Method>(asList(role.getMethods()));
		roleMethods.removeAll(nonRoleMethods);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String name = method.getName();
		try {
			if ("unwrap".equals(name)) {
				return o;
			} else if (roleMethods.contains(method)) {
				Class<?> type = method.getReturnType();
				int argsL = (args == null ? 0 : args.length);
				if (argsL == 0) { // simple getter
					return fromDataObjectValue(o.get(name), type);
				} else if (argsL == 1) {
					if (Collection.class.isAssignableFrom(type)) { // collection getter
						DataArray arr = o.getArray(name);
						if (arr != null) {
							Class<?> itemType = (Class<?>) args[0];
							Collection<Object> coll = (Collection<Object>) type.newInstance();
							for (Object v : STRUCT.castAsIterable(arr)) {
								coll.add(fromDataObjectValue(v, itemType));
							}
							return coll;
						}
						return null;
					} else if (Map.class.isAssignableFrom(type)) { // map getter
						DataObject obj = o.getObject(name);
						if (obj != null) {
							Class<?> itemType = (Class<?>) args[0];
							Map<String, Object> map = (Map<String, Object>) type.newInstance();
							for (String key : obj.getKeys()) {
								map.put(key, fromDataObjectValue(obj.get(key), itemType));
							}
							return map;
						}
						return null;
					} else { // setter
						o.put(name, toDataObjectValue(args[0]));
						return proxy;
					}
				} else { // bail out
					throw new RuntimeException();
				}
			} else {
				return method.invoke(o, args);
			}
		} catch(InvocationTargetException e) {
			throw Check.ifNull((Exception)e.getCause(), e);
		}
	}
	
	
	private Object fromDataObjectValue(Object v, Class<?> type) {
		if (Boolean.TYPE.equals(type)) { // expected boolean
			return Boolean.TRUE.equals(v); // force true/false (also when v == null)
		}
		if (v != null) {
			if (DataObjectWrapper.class.isAssignableFrom(type)) { // sub object
				return WF.wrap((DataObject)v, type);
			}
		}
		return v;
	}
	
	
	@SuppressWarnings("unchecked")
	private Object toDataObjectValue(Object v) {
		if (v != null) {
			if (v instanceof Collection) {
				DataArray arr = STRUCT.newArray();
				for (Object o : (Collection<Object>)v) {
					arr.add(toDataObjectValue(o));
				}
				return arr;
			}
			if (v instanceof Map) {
				DataObject obj = STRUCT.newObject();
				for (Entry<String, Object> entry : ((Map<String, Object>)v).entrySet()) {
					obj.put(entry.getKey(), entry.getValue());
				}
				return obj;
			}
			if (v instanceof DataObjectWrapper) { // sub object
				return ((DataObjectWrapper)v).unwrap();
			}
		}
		return v;
	}
}