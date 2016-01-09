package org.mozilla.javascript;


public class DebuggableRhinoEmbedding extends RhinoEmbedding {
	private final static String DEBUG_CONFIG = "transport=socket,suspend=n,address=";
	private boolean doDebug = false;
	private String debugPort = "8989";

	public void init() {
		super.init();
//		if (doDebug) {
//			try {
//				RhinoDebugger debugger = new RhinoDebugger(DEBUG_CONFIG + debugPort);
//				debugger.start();
//				addListener(debugger);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
	}
	
	public void setDebug(boolean debug) {
		super.setDebug(false); // disable native rhino debugger
		doDebug = debug;
	}
	
	public void setDebugPort(String port) {
		if (!"".equals(port)) {
			debugPort = port;
			if (!doDebug) { // allows to just set the port to enable debugging
				setDebug(true);
			}
		}
	}
}
