/*
 * Copyright 2008-2011 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nominanuda.dataobject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;

import com.nominanuda.lang.Strings;

public class JsonLooseParser {

	public void parse(Reader json, JsonContentHandler jch) throws IllegalArgumentException {
		try {
			try {
				CharStream cs = new ANTLRReaderStream(json);
				StreamingLooseJsonLexer lexer = new StreamingLooseJsonLexer(cs);
				CommonTokenStream tokens = new CommonTokenStream(lexer);
				StreamingLooseJsonParser parser = new StreamingLooseJsonParser(tokens);
				parser.setJsonContentHandler(jch);
				parser.program();
			} catch(WrappingRecognitionException e) {
				throw e.getWrappedException();
			}
		} catch(Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	public DataStruct parse(String json) throws IllegalArgumentException {
		return parse(new StringReader(json));
	}
	public DataStruct parse(Reader json) throws IllegalArgumentException {
		DataStructContentHandler h = new DataStructContentHandler();
		parse(json, h);
		return h.getResult();
	}
	public DataStruct parseUtf8(InputStream json) throws IllegalArgumentException {
		return parse(new InputStreamReader(json, Strings.UTF8));
	}

	public DataArray parseArray(String json) throws IllegalArgumentException {
		return (DataArray)parse(json);
	}
	public DataArray parseArray(Reader json) throws IllegalArgumentException {
		return (DataArray)parse(json);
	}
	public DataArray parseUtf8Array(InputStream json) throws IllegalArgumentException {
		return (DataArray)parseUtf8(json);
	}
	public DataObject parseObject(String json) throws IllegalArgumentException {
		return (DataObject)parse(json);
	}
	public DataObject parseObject(Reader json) throws IllegalArgumentException {
		return (DataObject)parse(json);
	}
	public DataObject parseUtf8Object(InputStream json) throws IllegalArgumentException {
		return (DataObject)parseUtf8(json);
	}

}
