package com.nominanuda.dataobject.schema;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.StringReader;

import org.junit.Test;

import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.dataobject.DataStructHelper;
import com.nominanuda.dataobject.transform.DevNullJsonContentHandler;
import com.nominanuda.dataobject.transform.JsonPipeline;

public class FullValidatorTest {

	@Test
	public void test() throws Exception {
		InputStream schema = getClass().getResourceAsStream("sampleschema.jcl");
		JclValidator validator = new JclValidator();
		validator.setSchema(schema);
		String[] jsons = {
			"User::{name:'foo',password:'bar'}",
			"User::{name:'foo',password:'bar',addr:{city:null}}",
		};
		for(String json : jsons) {
			String type = json.split("::")[0];
			DataStruct ds = DataStructHelper.STRUCT.parse(json.split("::")[1], true);
			JsonPipeline p = validator.buildValidatorPipe(type).withLooseParser();
			p.build(new StringReader(json.split("::")[1]), new DevNullJsonContentHandler()).run();
			assertTrue(validator.buildValidatorFor(ds, type).apply(ds));
		}
	}

}
