package com.nominanuda.dataobject;

import java.io.IOException;
import java.io.Writer;
import java.util.Stack;

import com.nominanuda.lang.Maths;
import static com.nominanuda.dataobject.DataType.*;

public class JsonPrinter implements JsonContentHandler {
	private final Writer w;
	private final CommaInsCtx commas = new CommaInsCtx();
	private final boolean pretty;

	public JsonPrinter(Writer writer, boolean pretty) {
		w = writer;
		this.pretty = pretty;
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
			w.write("\""+jsonStringEscape(key)+"\":");
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
				Number n = (Number) o;
				if (Maths.isInteger(n.doubleValue())) {
					w.write(new Long(n.longValue()).toString());
				} else {
					w.write(n.toString());
				}
			} else if (o instanceof String) {
				w.write("\"" + jsonStringEscape((String) o) + "\"");
			} else if (o instanceof Boolean) {
				w.write(((Boolean) o).toString());
			} else {
				throw new IllegalStateException();
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	public String jsonStringEscape(String s) {
		return s.replace("\\", "\\\\").replace("\"", "\\\"")
				.replace("\n", "\\n");
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
