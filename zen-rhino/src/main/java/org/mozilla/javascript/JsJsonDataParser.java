package org.mozilla.javascript;

import static com.nominanuda.rhino.ScriptableConvertor.SCONVERTOR;
import static org.mozilla.javascript.RhinoHelper.RHINO;

import java.io.Reader;
import java.io.StringReader;

import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;

/**
 * a permissive parser
 *
 */
public class JsJsonDataParser {
	public Stru parse(Reader json) {
		Context cx = Context.enter();
		try {
			return SCONVERTOR.fromScriptable(
					RHINO.jsonToScriptable(cx,json));
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
