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
import static com.nominanuda.zen.common.Maths.MATHS;
import static com.nominanuda.zen.common.Str.STR;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;

import javax.annotation.concurrent.ThreadSafe;


//TODO test , test unicode escapeAll
@ThreadSafe
public class JsonSerializer {
	public static final JsonSerializer JSON_SERIALIZER = new JsonSerializer();

	public static final byte[] NULL_ON_WIRE = STR.getBytesUtf8("null");
	public static final int DQUOTE_ON_WIRE = '"';
	public static final byte[] TRUE_ON_WIRE = STR.getBytesUtf8("true");
	public static final byte[] FALSE_ON_WIRE = STR.getBytesUtf8("false");
	public static final int LCURLY_ON_WIRE = '{';
	public static final int RCURLY_ON_WIRE = '}';
	public static final int LSQUARE_ON_WIRE = '[';
	public static final int RSQUARE_ON_WIRE = ']';
	public static final int COLON_ON_WIRE = ':';
	public static final int COMMA_ON_WIRE = ',';
	public static final int NEWLINE_ON_WIRE = '\n';

	public String toString(Object v) {
		StringWriter sw = new StringWriter();
		JsonOioPrinter printer = new JsonOioPrinter(sw);
		serialize(v, printer);
		return sw.toString();
	}

	public void serialize(Object v, OutputStream os) {
		JsonOioPrinter printer = new JsonOioPrinter(os);
		serialize(v, printer);
	}

	public void serialize(Object v, Writer w) {
		JsonOioPrinter printer = new JsonOioPrinter(w);
		serialize(v, printer);
	}

	public void serialize(Object v, JixHandler h) {
		Any x = Any.toStruObjModel(v);
		x.sendTo(h);
	}

	public byte[] serialize(Object v) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		JsonOioPrinter printer = new JsonOioPrinter(os);
		serialize(v, printer);
		return os.toByteArray();
	}

	@SuppressWarnings("unused")
	private String serializeAndQuotePrimitive(Object val) throws UncheckedIOException {
		StringWriter sw = new StringWriter();
		serializeAndQuotePrimitiveTo(val, sw, false, false);
		return sw.toString();
	}

	void serializeAndQuotePrimitiveTo(Object val, Writer w, boolean escapeSlash, boolean unicodeEscapeAll) throws UncheckedIOException {
		try {
			if(val == null) {
				w.write("null");
			} else if(val instanceof String) {
				w.write("\"");
				serializeNoQuoteTo((String)val, w, escapeSlash, unicodeEscapeAll);
				w.write("\"");
			} else if(val instanceof Number) {
				w.write(MATHS.toString((Number)val));
			} else {
				w.write(((Boolean)val).toString());
			}
		} catch(IOException e) {
			throw EX.uncheckedIO(e);
		}
	}

	void serializeAndQuotePrimitiveTo(Object val, OutputStream os, boolean escapeSlash, boolean unicodeEscapeAll) throws UncheckedIOException {
		try {
			if(val == null) {
				os.write(NULL_ON_WIRE);
			} else if(val instanceof String) {
				os.write(DQUOTE_ON_WIRE);
				serializeNoQuoteTo((String)val, os, escapeSlash, unicodeEscapeAll);
				os.write(DQUOTE_ON_WIRE);
			} else if(val instanceof Number) {
				serializeNoQuoteTo(MATHS.toString((Number)val), os, false, false);
			} else {
				os.write(((Boolean)val) ? TRUE_ON_WIRE : FALSE_ON_WIRE);
			}
		} catch(IOException e) {
			throw EX.uncheckedIO(e);
		}
	}

	/**
	 * \" \\ \/ \b \f \n \r \t \u1234 four-hex-digits
	 */
	private void serializeNoQuoteTo(String s, Writer writer, boolean escapeSlash, boolean unicodeEscapeAll) throws UncheckedIOException {
		try {
			char[] carr = s.toCharArray();
			int len = carr.length;
			for(int i = 0; i < len; i++) {
				char c = carr[i];
				switch (c) {
				case '"':
					writer.write("\\\"");
					break;
				case '/':
					writer.write(escapeSlash ? "\\/" : "/");
					break;
				case '\\':
					writer.write("\\\\");
					break;
				case '\b':
					writer.write("\\b");
					break;
				case '\f':
					writer.write("\\f");
					break;
				case '\n':
					writer.write("\\n");
					break;
				case '\r':
					writer.write("\\r");
					break;
				case '\t':
					writer.write("\\t");
					break;
				default:
					if(unicodeEscapeAll) {
						if(c < 127) {
							writer.append(c);
						} else {
							String hex = Integer.toHexString(c);
							writer.write("\\u");
							switch (hex.length()) {
							case 1:
								writer.write("000");
								break;
							case 2:
								writer.write("00");
								break;
							case 3:
								writer.write("0");
								break;
							default:
								break;
							}
							writer.write(Integer.toHexString(c));
						}
					} else {
						writer.append(c);
					}
					break;
				}
			}
		} catch(IOException e) {
			throw EX.uncheckedIO(e);
		}
	}
	private void serializeNoQuoteTo(String s, OutputStream os, boolean escapeSlash, boolean unicodeEscapeAll) throws UncheckedIOException {
		try {
			char[] carr = s.toCharArray();
			int len = carr.length;
			for(int i = 0; i < len; i++) {
				char c = carr[i];
				switch (c) {
				case '/':
					if(escapeSlash) {
						os.write('\\');
					}
					os.write('/');
					break;
				case '"':
					os.write('\\');
					os.write('"');
					break;
				case '\\':
					os.write('\\');
					os.write('\\');
					break;
				case '\b':
					os.write('\\');
					os.write('b');
					break;
				case '\f':
					os.write('\\');
					os.write('f');
					break;
				case '\n':
					os.write('\\');
					os.write('n');
					break;
				case '\r':
					os.write('\\');
					os.write('r');
					break;
				case '\t':
					os.write('\\');
					os.write('t');
					break;
				default:
					if(unicodeEscapeAll) {
						if(c < 127) {
							os.write(c);
						} else {
							String hex = Integer.toHexString(c);
							os.write('\\');
							os.write('u');
							switch (hex.length()) {
							case 1:
								os.write('0');
								os.write('0');
								os.write('0');
								break;
							case 2:
								os.write('0');
								os.write('0');
								break;
							case 3:
								os.write('0');
								break;
							default:
								break;
							}
							String hx = Integer.toHexString(c);
							os.write(hx.charAt(0));
							os.write(hx.charAt(1));
						}
					} else {
						os.write(c);
					}
					break;
				}
			}
		} catch(IOException e) {
			throw EX.uncheckedIO(e);
		}
	}
}
