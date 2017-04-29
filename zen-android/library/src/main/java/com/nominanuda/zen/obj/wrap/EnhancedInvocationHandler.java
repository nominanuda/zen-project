package com.nominanuda.zen.obj.wrap;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by azum on 29/04/17.
 */

public class EnhancedInvocationHandler implements InvocationHandler {
	private final Class<?> mEnhancement;
	private final Object mOriginalProxy, mEnhancementProxy;

	EnhancedInvocationHandler(Class<?> enhancement, Class<?> originalRole, Object originalProxy) throws Exception {
		mEnhancement = enhancement;
		mOriginalProxy = originalProxy;
		mEnhancementProxy = mEnhancement.getConstructor(originalRole).newInstance(new Object[] { originalProxy });
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			Method m = mEnhancement.getMethod(method.getName(), method.getParameterTypes());
			return m.invoke(mEnhancementProxy, args);
		} catch (NoSuchMethodException e) {
			return method.invoke(mOriginalProxy, args);
		}
	}
}
