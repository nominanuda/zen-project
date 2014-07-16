package com.nominanuda.dataobject;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Stack;

import com.nominanuda.code.ThreadSafe;
import com.nominanuda.lang.Check;
import com.nominanuda.lang.Maths;

import static com.nominanuda.dataobject.DataType.*;

public class JsonPrinter implements JsonContentHandler {
	private final Writer w;
	private final CommaInsCtx commas = new CommaInsCtx();
	private final boolean pretty;
	private final boolean unicodeEscapeAll;
	private boolean escapeSlash;

	public JsonPrinter(Writer writer) {
		this(writer, false, false, false);
	}

	public JsonPrinter(Writer writer, boolean pretty) {
		this(writer, false, false, false);
	}

	public JsonPrinter(Writer writer, boolean pretty, boolean unicodeEscapeAll, boolean escapeSlash) {
		this.w = Check.notNull(writer);
		this.pretty = pretty;
		this.unicodeEscapeAll = unicodeEscapeAll;
		this.escapeSlash = escapeSlash;
	}

	public void startJSON() throws RuntimeException {
	}

	public void endJSON() throws RuntimeException {
		try {
			w.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean startObject() throws RuntimeException {
		try {
			commas.startObject();
			w.write("{");
			indent++;
			indent();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	public boolean endObject() throws RuntimeException {
		try {
			commas.endObject();
			indent--;
			indent();
			w.write("}");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	public boolean startObjectEntry(String key) throws RuntimeException {
		try {
			commas.startObjectEntry(key);
			w.write("\"");
			stringEncode((String) key, w);
			w.write("\":");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	public boolean endObjectEntry() throws RuntimeException {
		commas.endObjectEntry();
		return true;
	}

	public boolean startArray() throws RuntimeException {
		try {
			commas.startArray();
			w.write("[");
			indent++;
			indent();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	public boolean endArray() throws RuntimeException {
		try {
			commas.endArray();
			indent--;
			indent();
			w.write("]");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	public boolean primitive(Object o) throws RuntimeException {
		try {
			commas.primitive(o);
			if (o == null) {
				w.write("null");
			} else if (o instanceof Number) {
				w.write(numberEncode((Number) o));
			} else if (o instanceof String) {
				w.write("\"");
				stringEncode((String) o, w);
				w.write("\"");
			} else if (o instanceof Boolean) {
				w.write(booleanEncode((Boolean) o));
			} else {
				throw new IllegalStateException();
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@ThreadSafe public String numberEncode(Number n) {
		return Maths.isInteger(n.doubleValue())
			? new Long(n.longValue()).toString()
			: n.toString();
	}

	@ThreadSafe public String booleanEncode(Boolean b) {
		return b.toString();
	}

	@ThreadSafe public String stringEncode(String s) {
		StringWriter sw = new StringWriter();
		try {
			stringEncode(s, sw);
		} catch (IOException e) {
			throw new RuntimeException(e);//never happens
		}
		return sw.toString();
	}

	/**
	 * \" \\ \/ \b \f \n \r \t \u1234 four-hex-digits
	 */
	@ThreadSafe public void stringEncode(String s, Writer writer) throws IOException {
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
	}

	private class Cx {
		public DataType t;
		public boolean firstGone;

		public Cx(DataType t) {
			this.t = t;
			this.firstGone = false;
		}
	}
	private int indent = 0;
	private final static String[] INDENTS = new String[] {
		"","  ","    ","      ","        ","          ","               "
	};
	//TODO
	private void indent() throws RuntimeException {
		if(pretty) {
			try {
				w.write("\n");
				w.write(indentSpaces(indent));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	private String indentSpaces(int size) {
		if(size < INDENTS.length) {
			return INDENTS[size];
		} else {
			char[] carr = new char[size];
			for(int i = 0; i < size; i++) {
				carr[i] = ' ';
			}
			return new String(carr);
		}
	}

	private class CommaInsCtx implements JsonContentHandler {
		Stack<Cx> stack = new Stack<Cx>();

		public void startJSON() throws RuntimeException {
		}
		public void endJSON() throws RuntimeException {
		}
		public boolean startObject() throws RuntimeException {
			startValue();
			stack.push(new Cx(object));
			return true;
		}
		public boolean endObject() throws RuntimeException {
			stack.pop();
			return true;
		}
		public boolean startObjectEntry(String key) throws RuntimeException {
			Cx cx = stack.peek();
			if(cx.firstGone) {
				try {
					w.write(",");
					indent();
				} catch (IOException e) {
				}
			} else {
				cx.firstGone = true;
			}
			return true;
		}
		public boolean endObjectEntry() throws RuntimeException {
			return true;
		}
		public boolean startArray() throws RuntimeException {
			startValue();
			stack.push(new Cx(array));
			return true;
		}
		public boolean endArray() throws RuntimeException {
			stack.pop();
			return true;
		}
		public boolean primitive(Object value) throws RuntimeException {
			startValue();
			return true;
		}
		private void startValue() throws RuntimeException {
			if(stack.isEmpty()) {
				return;
			}
			Cx cx = stack.peek();
			if(cx.t == array) {
				if(cx.firstGone) {
					try {
						w.write(",");
						indent();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				} else {
					cx.firstGone = true;
				}
			}
		}
	}
}
