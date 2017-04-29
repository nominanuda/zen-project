package com.nominanuda.zen.obj.wrap;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by azum on 29/04/17.
 */

public class EnhancedInvocationHandler implements InvocationHandler {
	private final Object mOriginalProxy, mEnhancementProxy;
	private final Class<?> mEnhancement;

	EnhancedInvocationHandler(Object enhancementProxy, Class<?> originalRole, Object originalProxy) throws Exception {
		mOriginalProxy = originalProxy;
		mEnhancementProxy = enhancementProxy;
		mEnhancement = enhancementProxy.getClass();
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
