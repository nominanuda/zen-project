/*
 * Copyright 2008-2016 the original author or authors.
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
package com.nominanuda.zen.jcl;

import static com.nominanuda.zen.common.Ex.EX;
import static com.nominanuda.zen.common.Str.STR;
import static org.antlr.v4.runtime.tree.ParseTreeWalker.DEFAULT;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.Test;

public class SimpleJsonTest {

	@Test
	public void test() throws IOException {
		InputStream is = getClass().getClassLoader().getResourceAsStream("com/nominanuda/zen/jcl/validexpressions/f01.jcl");
		Reader r  =
		new InputStreamReader(is);
		parse(r);
	}

	private void parse(Reader in)
			throws UncheckedIOException {
		try {
			ANTLRInputStream src = new ANTLRInputStream(in);
			JclLexer lexer = new JclLexer(src);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
//			for(Token t : tokens.getTokens()) {
//				System.err.println(t.getText());
//			}
			System.err.println(tokens.getText());
			
			JclParser parser = new JclParser(tokens);
			ParserRuleContext tree = new ParserRuleContext();
			parser.setContext(tree);
			parser.setErrorHandler(new BailErrorStrategy());
			ParseTree /*ProgramContext*/ parseTree = parser.program();
			System.err.println(parseTree.toStringTree(parser));
			final AtomicInteger indent = new AtomicInteger(0);
			JclListener listener = (JclListener)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {JclListener.class}, new InvocationHandler() {
				public Object invoke(Object proxy, Method method, Object[] args)
						throws Throwable {
					String m = method.getName();
					if(m.endsWith("EveryRule")) {
						
					} else {
						if(m.startsWith("exit")) {
							indent.addAndGet(-1);
						}
						if("visitTerminal".equals(m)) {
							TerminalNode tn = (TerminalNode)args[0];
							
							System.err.print(STR.ntimes("  ", indent.get())+"-> ");
							System.err.println(tn.toStringTree(parser));//tn.getText());
						} else {
							System.err.println(STR.ntimes("  ", indent.get())+method.getName());
						}
						if(m.startsWith("enter")) {
							indent.addAndGet(1);
						}
					}
					return null;
				}
			});
			DEFAULT.walk(listener, parseTree);
		} catch (IOException e) {
			throw EX.uncheckedIO(e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
	}
}
