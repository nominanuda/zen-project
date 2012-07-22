package org.mozilla.javascript;

import java.io.Reader;
import java.io.StringReader;

import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.rhino.DataStructScriptableConvertor;

/**
 * a permissive parser
 *
 */
public class JsJsonDataParser {
	private static final DataStructScriptableConvertor convertor = new DataStructScriptableConvertor();
	private static final RhinoHelper r = new RhinoHelper();

	public DataStruct<?> parse(Reader json) {
		Context cx = Context.enter();
		try {
			return convertor.fromScriptable(
					r.jsonToScriptable(cx,json));
		} finally {
			Context.exit();
		}
	}
	public DataStruct<?> parse(String json) {
		return parse(new StringReader(json));
	}

	public DataObject parseObj(String json) {
		return parseObj(new StringReader(json));
	}
	public DataObject parseObj(Reader json) {
		return (DataObject)parse(json);
	}

}
