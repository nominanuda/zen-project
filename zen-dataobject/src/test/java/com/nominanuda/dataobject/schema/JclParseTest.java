package com.nominanuda.dataobject.schema;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.junit.Test;

import com.nominanuda.dataobject.WrappingRecognitionException;

public class JclParseTest {
	private String[] validExprs = new String[] {
		"(n|b)","[n]","n","{}","[]","{a:n,a:s,z:b}","{at:n,a:s,z}","{a,a,z}","{a,a,z : [({}|n|[])]}"
	};
	private String[] invalidExprs = new String[] {
		"a,b ", " ", "", "{[]} ", ":", "{a b}", "{} {}"
	};

	@Test
	public void testValid() throws Exception {
		for(String expr : validExprs) {
			parse(expr);
		}
	}

	@Test
	public void testInvalid() throws Exception {
		for(String expr : invalidExprs) {
			try {
				parse(expr);
				fail(expr + " -- should not be valid");
			} catch(Exception e) {
				assertTrue(e.getClass().getName().startsWith("org.antlr")
					|| e instanceof IllegalArgumentException);
			}
		}
	}

	private void parse(String expr) throws Exception {
		try {
			CharStream cs = new ANTLRReaderStream(new StringReader(expr));
			JclLexer lexer = new JclLexer(cs);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			JclParser parser = new JclParser(tokens);
			parser.program();
			
		} catch(WrappingRecognitionException e) {
			throw e.getWrappedException();
		}
	}

}
