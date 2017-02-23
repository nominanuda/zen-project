package org.mozilla.javascript;

import java.io.Reader;
import java.io.StringReader;

import com.nominanuda.rhino.StruScriptableConvertor;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;

/**
 * a permissive parser
 *
 */
public class JsJsonDataParser {
	private static final StruScriptableConvertor convertor = new StruScriptableConvertor();
	private static final RhinoHelper r = new RhinoHelper();

	public Stru parse(Reader json) {
		Context cx = Context.enter();
		try {
			return convertor.fromScriptable(
					r.jsonToScriptable(cx,json));
		} finally {
			Context.exit();
		}
	}
	public Stru parse(String json) {
		return parse(new StringReader(json));
	}

	public Obj parseObj(String json) {
		return parseObj(new StringReader(json));
	}
	public Obj parseObj(Reader json) {
		return (Obj)parse(json);
	}

}
