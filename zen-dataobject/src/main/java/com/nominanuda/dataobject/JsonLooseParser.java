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

	public DataStruct parse(String json) throws IllegalArgumentException {
		return parse(new StringReader(json));
	}
	public DataStruct parse(Reader json) throws IllegalArgumentException {
		try {
			try {
				CharStream cs = new ANTLRReaderStream(json);
				StreamingLooseJsonLexer lexer = new StreamingLooseJsonLexer(cs);
				CommonTokenStream tokens = new CommonTokenStream(lexer);
				StreamingLooseJsonParser parser = new StreamingLooseJsonParser(tokens);
				parser.program();
				return ((DataStructContentHandler)parser.getJsonContentHandler()).getResult();
			} catch(WrappingRecognitionException e) {
				throw e.getWrappedException();
			}
		} catch(Exception e) {
			throw new IllegalArgumentException(e);
		}
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
