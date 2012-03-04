package org.mozilla.javascript;

public aspect ProxyJavaCallArgsAspect {

	pointcut getConversionWeightCall():
		execution(static int NativeJavaObject.getConversionWeight(Object, Class<?>));

	pointcut coerceTypeImplCall():
		execution(static Object NativeJavaObject.coerceTypeImpl(Class<?>, Object));

	Object around(Object value, Class<?> type): getConversionWeightCall()
			&& args(value, type) {
		Context cx = Context.getContext();
		ContextFactory cxf = cx.getFactory();
		if(cxf instanceof RhinoEmbedding) {
			RhinoEmbedding re = (RhinoEmbedding)cxf;
			int weight = re.getConversionWeight(value, type);
			return weight == Integer.MIN_VALUE ? proceed(value, type) : weight;
		} else {
			return proceed(value, type);
		}
	}
	Object around(Class<?> type, Object value): coerceTypeImplCall()
			&& args(type, value) {
		Context cx = Context.getContext();
		ContextFactory cxf = cx.getFactory();
		if(cxf instanceof RhinoEmbedding) {
			RhinoEmbedding re = (RhinoEmbedding)cxf;
			Object res = re.coerceTypeImpl(type, value);
			return res == RhinoEmbedding.REGULAR_COERCE_OP ? proceed(type, value) : res;
		} else {
			return proceed(type, value);
		}
	}
}
