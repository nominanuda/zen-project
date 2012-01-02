package org.mozilla.javascript;


import org.junit.Assert;
import org.junit.Before;

import static org.junit.Assert.*;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;


public class TopScopeBuildTest {
	private RhinoEmbedding embedding;

	@Before
	public void setUp() {
		embedding = new RhinoEmbedding();
		embedding.init();
	}

	@Test
	public void testAccessJavaPackages() {
		Context cx = Context.enter(null, embedding);
		try {
			ScriptableObject root = new NativeObject();//ImporterTopLevel(cx);//NativeObject();
			ScriptableObject.putProperty(root, "foo", "bar");
			ScriptableObject.putProperty(root, "miki", "mouse");
			ScriptRuntime.initStandardObjects(cx, root, false);
			ScriptableObject.deleteProperty(root, "Packages");
			String[] topNames = { "java", "javax", "org", "com", "edu", "net" };
			for(String topPkgName : topNames) {
				ScriptableObject.deleteProperty(root, topPkgName);
			}
			//root.sealObject();
			ScriptableObject fakeRoot = new NativeObject();
			fakeRoot.setPrototype(root);

			Script script = cx.compileString("foo = 'baz'; foo;", "src", 0, null);
			assertEquals("baz", script.exec(cx, fakeRoot));
			assertEquals("mouse", ScriptRuntime.getTopLevelProp(fakeRoot, "miki"));
			assertEquals("mouse", ScriptRuntime.getTopLevelProp(root, "miki"));
			assertEquals("baz", ScriptRuntime.getTopLevelProp(fakeRoot, "foo"));
			assertEquals("bar", ScriptRuntime.getTopLevelProp(root, "foo"));
			assertEquals("baz", ScriptableObject.getProperty(fakeRoot, "foo"));
			assertEquals("bar", ScriptableObject.getProperty(root, "foo"));
			assertTrue(ScriptableObject.getProperty(fakeRoot, "Object") instanceof Function);
			assertSame(UniqueTag.NOT_FOUND, ScriptableObject.getProperty(root, "Package"));
			assertSame(UniqueTag.NOT_FOUND, ScriptableObject.getProperty(root, "Packages"));
			assertSame(UniqueTag.NOT_FOUND, ScriptableObject.getProperty(root, "java"));
			try {
				cx.compileString("new java.util.LinkedList();", "src", 0, null).exec(cx, fakeRoot);
			} catch(RhinoException e) {}
		} finally {
			Context.exit();
		}
	}
	public Object execContextAction(ContextAction ca) {
		Context cx = Context.enter();
		try {
			return execContextAction(ca, cx);
		} finally {
			Context.exit();
		}
	}
	public Object execContextAction(ContextAction ca, Context cx) {
		return ca.run(cx);
	}
	public ScriptableObject initEmptyTopScope(Context cx, boolean sealed) {
		ScriptableObject s = new ImporterTopLevel(cx, sealed);
		//alternative mode cx.initStandardObjects(null, sealed);
		return s;
	}
	@Test
	public void testContextEnter() {
		Context cx = embedding.enterContext();
		assertNotNull(Context.getCurrentContext());
		assertNotNull(cx);
		Context.getContext();
		try {
			cx.toString();
		} finally {
			Context.exit();
			Assert.assertNull(Context.getCurrentContext());
			try {
				Context.getContext();
				Assert.fail();
			} catch(RuntimeException e) {
				Assert.assertSame(RuntimeException.class, e.getClass());
			}
		}
	}

}
