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

import static com.nominanuda.zen.common.Ex.EX;
import static com.nominanuda.zen.common.Str.STR;
import static com.nominanuda.zen.obj.JsonSerializer.COLON_ON_WIRE;
import static com.nominanuda.zen.obj.JsonSerializer.COMMA_ON_WIRE;
import static com.nominanuda.zen.obj.JsonSerializer.LCURLY_ON_WIRE;
import static com.nominanuda.zen.obj.JsonSerializer.LSQUARE_ON_WIRE;
import static com.nominanuda.zen.obj.JsonSerializer.NEWLINE_ON_WIRE;
import static com.nominanuda.zen.obj.JsonSerializer.RCURLY_ON_WIRE;
import static com.nominanuda.zen.obj.JsonSerializer.RSQUARE_ON_WIRE;
import static com.nominanuda.zen.obj.JsonType.arr;
import static com.nominanuda.zen.obj.JsonType.obj;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Stack;

import com.nominanuda.zen.common.Check;

public class JsonOioPrinter implements JixHandler {
	private final Writer w;
	private final boolean binary;
	private final OutputStream os;
	private final CommaInsCtx commas = new CommaInsCtx();
	private final boolean pretty;
	private boolean escapeSlash = false;
	private JsonSerializer jsonSerializer = new JsonSerializer();

	public JsonOioPrinter(Writer writer) {
		this(writer, false, false);
	}

	public JsonOioPrinter(Writer writer, boolean pretty) {
		this(writer, pretty, false);
	}

	public JsonOioPrinter(OutputStream os) {
		this(os, false, false);
	}

	public JsonOioPrinter(OutputStream os, boolean pretty) {
		this(os, pretty, false);
	}

	public JsonOioPrinter(Writer writer, boolean pretty, boolean escapeSlash) {
		this.w = Check.notNull(writer);
		this.os = null;
		this.binary = false;
		this.pretty = pretty;
		this.escapeSlash = escapeSlash;
	}

	public JsonOioPrinter(OutputStream os, boolean pretty, boolean escapeSlash) {
		this.os = Check.notNull(os);
		this.w = null;
		this.binary = true;
		this.pretty = pretty;
		this.escapeSlash = escapeSlash;
	}

	@Override
	public void startObj() throws RuntimeException {
		try {
			commas.startObj();
			writeLCurly();
			indent++;
			indent();
		} catch (IOException e) {
			throw EX.uncheckedIO(e);
		}
	}

	@Override
	public void endObj() throws RuntimeException {
		try {
			commas.endObj();
			indent--;
			indent();
			writeRCurly();
		} catch (IOException e) {
			throw EX.uncheckedIO(e);
		}
	}

	@Override
	public void key(Key key) throws RuntimeException {
		try {
			commas.key(key);
			writeString(key.toString());
			writeColon();
		} catch (IOException e) {
			throw EX.uncheckedIO(e);
		}
	}

	@Override
	public void startArr() throws RuntimeException {
		try {
			commas.startArr();
			writeLSquare();
			indent++;
			indent();
		} catch (IOException e) {
			throw EX.uncheckedIO(e);
		}
	}

	@Override
	public void endArr() throws RuntimeException {
		try {
			commas.endArr();
			indent--;
			indent();
			writeRSquare();
		} catch (IOException e) {
			throw EX.uncheckedIO(e);
		}
	}

	@Override
	public void val(Val o) throws RuntimeException {
		commas.val(o);
		if(binary) {
			jsonSerializer.serializeAndQuotePrimitiveTo(o.toJavaObjModel(), os, escapeSlash, false);
		} else {
			jsonSerializer.serializeAndQuotePrimitiveTo(o.toJavaObjModel(), w, escapeSlash, false);
		}
	}

	private int indent = 0;
	private final static String[] INDENTS = new String[] {
		"","  ","    ","      ","        ","          ","               "
	};

	private void indent() throws UncheckedIOException {
			try {
				if(pretty) {
					writeNewLine();
					writeIndentSpaces(indent);
				}
			} catch (IOException e) {
				throw EX.uncheckedIO(e);
			}
	}

	private class CommaInsCtx implements JixHandler {
		Stack<Cx> stack = new Stack<Cx>();

		@Override
		public void startObj() throws RuntimeException {
			startValue();
			stack.push(new Cx(obj));
		}
		@Override
		public void endObj() throws RuntimeException {
			stack.pop();
		}
		@Override
		public void key(Key key) throws RuntimeException {
			Cx cx = stack.peek();
			if(cx.firstGone) {
				try {
					writeComma();
					indent();
				} catch (IOException e) {
				}
			} else {
				cx.firstGone = true;
			}
		}

		@Override
		public void startArr() throws RuntimeException {
			startValue();
			stack.push(new Cx(arr));
		}
		@Override
		public void endArr() throws RuntimeException {
			stack.pop();
		}
		@Override
		public void val(Val value) throws RuntimeException {
			startValue();
		}

		private void startValue() throws RuntimeException {
			if(stack.isEmpty()) {
				return;
			}
			Cx cx = stack.peek();
			if(cx.t == arr) {
				if(cx.firstGone) {
					try {
						writeComma();
						indent();
					} catch (IOException e) {
						throw EX.uncheckedIO(e);
					}
				} else {
					cx.firstGone = true;
				}
			}
		}
	}

	private final static byte[][] INDENTS_BYTE = new byte[INDENTS.length][];
	static {
		for(int i = 0; i < INDENTS.length; i++) {
			INDENTS_BYTE[i] = STR.getBytesUtf8(INDENTS[i]);
		}
	};
	private String indentSpaces(int size) {
		if(size < INDENTS.length) {
			return INDENTS[size];
		} else {
			return new String(indentSpaceBytes(size));
		}
	}
	private byte[] indentSpaceBytes(int size) {
		if(size < INDENTS_BYTE.length) {
			return INDENTS_BYTE[size];
		} else {
			byte[] barr = new byte[size];
			for(int i = 0; i < size; i++) {
				barr[i] = ' ';
			}
			return barr;
		}
	}

	private void writeIndentSpaces(int _indent) throws IOException {
		if(binary) {
			os.write(indentSpaceBytes(_indent));
		} else {
			w.write(indentSpaces(_indent));
		}
	}

	private void writeNewLine() throws IOException {
		if(binary) {
			os.write(NEWLINE_ON_WIRE);
		} else {
			w.write("\n");
		}
	}

	private void writeComma() throws IOException {
		if(binary) {
			os.write(COMMA_ON_WIRE);
		} else {
			w.write(",");
		}
	}
	private void writeLSquare() throws IOException {
		if(binary) {
			os.write(LSQUARE_ON_WIRE);
		} else {
			w.write("[");
		}
	}
	private void writeRSquare() throws IOException {
		if(binary) {
			os.write(RSQUARE_ON_WIRE);
		} else {
			w.write("]");
		}
	}

	private void writeRCurly() throws IOException {
		if(binary) {
			os.write(RCURLY_ON_WIRE);
		} else {
			w.write("}");
		}
	}
	private void writeLCurly() throws IOException {
		if(binary) {
			os.write(LCURLY_ON_WIRE);
		} else {
			w.write("{");
		}
	}
	private void writeColon() throws IOException {
		if(binary) {
			os.write(COLON_ON_WIRE);
		} else {
			w.write(":");
		}
	}

	private void writeString(String key) {
		if(binary) {
			jsonSerializer.serializeAndQuotePrimitiveTo(key, os, escapeSlash, false);
		} else {
			jsonSerializer.serializeAndQuotePrimitiveTo(key, w, escapeSlash, false);
		}
	}

	private class Cx {
		public JsonType t;
		public boolean firstGone;

		public Cx(JsonType t) {
			this.t = t;
			this.firstGone = false;
		}
	}


	public void flush() throws IOException {
		if(binary) {
			os.flush();
		} else {
			w.flush();
		}
	}
}
