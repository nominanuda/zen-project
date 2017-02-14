package com.nominanuda.dataobject.schema;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.function.Function;

import com.nominanuda.code.Nullable;
import com.nominanuda.dataobject.DataStruct;
import com.nominanuda.dataobject.transform.DevNullJsonContentHandler;
import com.nominanuda.dataobject.transform.JsonPipeline;
import com.nominanuda.dataobject.transform.JsonTransformer;
import com.nominanuda.io.IOHelper;
import com.nominanuda.lang.ObjectFactory;

public class JclValidator {
	private JclValidatorFactory jvf;

	public Function<DataStruct, Boolean> buildValidatorFor(DataStruct ds, @Nullable String type) {
		final JsonPipeline p = new JsonPipeline()
			.add(buildValidatorTransformerFactory(type));
		return new Function<DataStruct, Boolean>() {
			public Boolean apply(final DataStruct param) {
				try {
					p.build(param, new DevNullJsonContentHandler()).run();
					return true;
				} catch(Exception e) {
					return false;
				}
			}
		};
	}
	public JsonPipeline buildValidatorPipe(@Nullable String type) {
		JsonPipeline p = new JsonPipeline()
			.add(buildValidatorTransformerFactory(type));
		return p;
	}
	public ObjectFactory<JsonTransformer> buildValidatorTransformerFactory(
			@Nullable String type) {
		return jvf.buildValidatorFactory(type);
	}
	public void setSchema(InputStream schema) throws Exception {
		setSchema(new InputStreamReader(schema));
	}
	public void setSchema(String schema) throws Exception {
		jvf = new JclValidatorFactory(schema);
	}
	public void setSchemaUrl(String schemaUrl) throws Exception {
		InputStream is = new URL(schemaUrl).openStream();
		setSchema(is);
	}
	public void setSchema(Reader schema) throws IOException, Exception {
		setSchema(IOHelper.IO.readAndClose(schema));
		
	}

}
