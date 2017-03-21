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
package com.nominanuda.zen.obj;

import static com.nominanuda.zen.common.Check.notNull;
import static com.nominanuda.zen.common.Ex.EX;
import static com.nominanuda.zen.obj.JsonDeserializer.JSON_DESERIALIZER;
import static org.antlr.v4.runtime.tree.ParseTreeWalker.DEFAULT;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;

import javax.annotation.Nullable;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.nominanuda.zen.obj.SimpleJsonLexer;
import com.nominanuda.zen.obj.SimpleJsonListener;
import com.nominanuda.zen.obj.SimpleJsonParser;
import com.nominanuda.zen.obj.SimpleJsonParser.ArrayContext;
import com.nominanuda.zen.obj.SimpleJsonParser.BoolContext;
import com.nominanuda.zen.obj.SimpleJsonParser.ElementsContext;
import com.nominanuda.zen.obj.SimpleJsonParser.MembersContext;
import com.nominanuda.zen.obj.SimpleJsonParser.NullValueContext;
import com.nominanuda.zen.obj.SimpleJsonParser.NumberContext;
import com.nominanuda.zen.obj.SimpleJsonParser.ObjectContext;
import com.nominanuda.zen.obj.SimpleJsonParser.PairContext;
import com.nominanuda.zen.obj.SimpleJsonParser.ProgramContext;
import com.nominanuda.zen.obj.SimpleJsonParser.StringContext;
import com.nominanuda.zen.obj.SimpleJsonParser.ValueContext;

/**
 * <pre>
 * SimpleJson is a simplification of JSON syntax: single quotes substitute double quotes
 * strings that don't contain spaces and don't start with a number char can omit quotes
 * commas and colons are optional
 * {a:b c 1 'my key' true} is the equivalent of {"a":"b", "c":1, "my key":true}
 * </pre>
 */
public class SimpleJixParser implements SimpleJsonListener {
	private final Reader in;
	private final JixHandler jixHandler;

	private SimpleJixParser(Reader in, JixHandler jixHandler) {
		this.in = notNull(in);
		this.jixHandler = notNull(jixHandler);
	}

	public static @Nullable Object parse(String simpleJson) {
		JixBuilder b = new JixBuilder();
		parse(new StringReader(simpleJson), b);
		return b.get();
	}

	public static void parse(Reader in, JixHandler jixHandler) {
		SimpleJixParser p = new SimpleJixParser(in, jixHandler);
		p.parseInternal();
	}

	public static Obj obj(String simpleJson) {
		return (Obj)parse(simpleJson);
	}
	public static Arr arr(String simpleJson) {
		return (Arr)parse(simpleJson);
	}


	private void parseInternal()
			throws UncheckedIOException {
		try {
			ANTLRInputStream src = new ANTLRInputStream(in);
			SimpleJsonLexer lexer = new SimpleJsonLexer(src);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			SimpleJsonParser parser = new SimpleJsonParser(tokens);
			ParserRuleContext tree = new ParserRuleContext();
			parser.setContext(tree);
			parser.setErrorHandler(new BailErrorStrategy());
			SimpleJsonListener listener = this;
			ParseTree /*ProgramContext*/ parseTree = parser.program();
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

	@Override
	public void enterNumber(NumberContext ctx) {
		jixHandler.val(Val.of(JSON_DESERIALIZER.deserializeNumber(ctx.start.getText())));
	}

	@Override
	public void exitNumber(NumberContext ctx) {
	}


	@Override
	public void enterBool(BoolContext ctx) {
		jixHandler.val(Val.of(Boolean.valueOf(ctx.start.getText())));
	}

	@Override
	public void exitBool(BoolContext ctx) {
	}

	@Override
	public void enterArray(ArrayContext ctx) {
		jixHandler.startArr();
	}

	@Override
	public void exitArray(ArrayContext ctx) {
		jixHandler.endArr();
	}

	@Override
	public void enterElements(ElementsContext ctx) {
	}

	@Override
	public void exitElements(ElementsContext ctx) {
	}

	@Override
	public void enterMembers(MembersContext ctx) {
	}

	@Override
	public void exitMembers(MembersContext ctx) {
	}

	@Override
	public void enterProgram(ProgramContext ctx) {
	}

	@Override
	public void exitProgram(ProgramContext ctx) {
	}

	@Override
	public void enterValue(ValueContext ctx) {
	}

	@Override
	public void exitValue(ValueContext ctx) {
	}

	@Override
	public void enterNullValue(NullValueContext ctx) {
		jixHandler.val(Val.NULL);
	}

	@Override
	public void exitNullValue(NullValueContext ctx) {
	}


	private boolean keyWaiting = false;
	@Override
	public void enterString(StringContext ctx) {
		if(keyWaiting) {
			jixHandler.key(Key.of(unquoteIfQuoted(ctx.start.getText())));
			keyWaiting = false;
		} else {
			jixHandler.val(Val.of(unquoteIfQuoted(ctx.start.getText())));
		}
	}

	private static final char SINGLE_QUOTE = '\'';
	private String unquoteIfQuoted(String text) {
		int lenMinusOne = text.length() - 1;
		return text.charAt(0) == SINGLE_QUOTE && text.charAt(lenMinusOne) == SINGLE_QUOTE
			? text.substring(1, lenMinusOne) : text;
	}

	@Override
	public void exitString(StringContext ctx) {
	}

	@Override
	public void enterPair(PairContext ctx) {
		keyWaiting = true;
	}

	@Override
	public void exitPair(PairContext ctx) {
	}

	@Override
	public void enterObject(ObjectContext ctx) {
		jixHandler.startObj();
	}

	@Override
	public void exitObject(ObjectContext ctx) {
		jixHandler.endObj();
	}

	@Override
	public void enterEveryRule(ParserRuleContext ctx) {
	}

	@Override
	public void exitEveryRule(ParserRuleContext ctx) {
	}

	@Override
	public void visitTerminal(TerminalNode node) {
	}

	@Override
	public void visitErrorNode(ErrorNode node) {
	}
}
