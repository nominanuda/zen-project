package com.nominanuda.zen.obj.wrap;

import org.json.JSONObject;

/**
 * Created by azum on 17/03/17.
 */

public interface ObjWrapper {
	JSONObject unwrap();

	/* android specific */
	<ITYPE, IENHANCED extends ITYPE, METHODS extends ITYPE> IENHANCED as(Class<IENHANCED> enhanced, METHODS enhancement);
}
