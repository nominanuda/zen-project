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
package com.nominanuda.dataobject.schema;

import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

import com.nominanuda.dataobject.JsonContentHandler;
import com.nominanuda.dataobject.WrappingRecognitionException;
import com.nominanuda.dataobject.transform.BaseJsonTransformer;
import com.nominanuda.dataobject.transform.JsonTransformer;
import com.nominanuda.lang.Check;
import com.nominanuda.lang.Fun1;
import com.nominanuda.lang.ObjectFactory;

import static com.nominanuda.dataobject.schema.JclLexer.*;


public class JclValidatorFactory implements ObjectFactory<JsonTransformer>{
	private CommonTree root;
	private PrimitiveValidatorFactory primitiveValidatorFactory = new DefaultPrimitiveValidatorFactory();

	public JclValidatorFactory(String jclExpr) throws Exception {
		try {
			CharStream cs = new ANTLRReaderStream(new StringReader(jclExpr));
			JclLexer lexer = new JclLexer(cs);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			JclParser parser = new JclParser(tokens);
			CommonTree tree = (CommonTree)parser.program().getTree();
			root = tree;
		} catch(WrappingRecognitionException e) {
			throw e.getWrappedException();
		}
	}

	public JsonTransformer getObject() {
		Tree cur = root;
		final Stack<EventConsumer> stack = new Stack<EventConsumer>();
		stack.push(makeConsumer(cur, stack));
		return new BaseJsonTransformer() {
			public boolean startObjectEntry(String key) throws RuntimeException {
				stack.peek().startObjectEntry(key);
				return super.startObjectEntry(key);
			}
			public boolean startObject() throws RuntimeException {
				stack.peek().startObject();
				return super.startObject();
			}
			public void startJSON() throws RuntimeException {
				super.startJSON();
			}
			public boolean startArray() throws RuntimeException {
				stack.peek().startArray();
				return super.startArray();
			}
			public boolean primitive(Object value) throws RuntimeException {
				stack.peek().primitive(value);
				return super.primitive(value);
			}
			public boolean endObjectEntry() throws RuntimeException {
				stack.peek().endObjectEntry();
				return super.endObjectEntry();
			}
			public boolean endObject() throws RuntimeException {
				stack.peek().endObject();
				return super.endObject();
			}
			public void endJSON() throws RuntimeException {
				super.endJSON();
			}
			public boolean endArray() throws RuntimeException {
				stack.peek().endArray();
				return super.endArray();
			}
		};
	}

	private EventConsumer makeConsumer(Tree node, Stack<EventConsumer> stack) {
		switch (node.getType()) {
		case OBJECT:
			Map<String, EventConsumer> entryConsumers = new LinkedHashMap<String, EventConsumer>();
			for(int i = 0; i < node.getChildCount(); i++) {
				Tree entry = node.getChild(i);
				if(entry.getType() == ENTRY) {
					EventConsumer c = makeConsumer(entry.getChild(1), stack);
					String key = entry.getChild(0).getChild(0).getText();
					entryConsumers.put(key, c);
				} else if(entry.getType() == ENTRYSEQ) {
					entryConsumers.put("*", new EntrySeqConsumer(stack));
				} else {
					Check.illegalstate.fail();
				}
			}
			return new ObjectConsumer(stack, entryConsumers);
		case ARRAY:
			List<EventConsumer> elementsConsumers = new LinkedList<EventConsumer>();
			for(int i = 0; i < node.getChildCount(); i++) {
				Tree value = node.getChild(i);
				if(value.getType() == VALUESEQ) {
					elementsConsumers.add(new ValueSeqConsumer(stack));
				} else {
					elementsConsumers.add(makeConsumer(value, stack));
				}
			}
			return new ArrayConsumer(stack, elementsConsumers);
		case PRIMITIVE:
			return new PrimitiveConsumer(stack, makePrimitiveValidator(node));
		default:
			return Check.illegalstate.fail();
		}
	}

	private Fun1<Object, String> makePrimitiveValidator(Tree node) {
		Tree t = node.getChild(0);
		String typeDef = t == null 
			? DefaultPrimitiveValidatorFactory.ANYPRIMITIVE
			: t.getText();
		return primitiveValidatorFactory.create(typeDef);
	}
	////////////////////////////////////////
	public class ObjectConsumer extends EventConsumer {
		private final Map<String,EventConsumer> entryConsumers;

		public ObjectConsumer(Stack<EventConsumer> stack, Map<String,EventConsumer> entryConsumers) {
			super(stack);
			this.entryConsumers = entryConsumers;
		}

		@Override
		public boolean startObject() throws RuntimeException {
			return true;
		}

		@Override
		public boolean endObject() throws RuntimeException {
			//TODO validate
			pop();
			return true;
		}

		@Override
		public boolean startObjectEntry(String key) throws RuntimeException {
			EventConsumer c = entryConsumers.remove(key);
			if(c == null && entryConsumers.size() == 1 && entryConsumers.containsKey("*")) {
				c = entryConsumers.values().iterator().next();
			}
			push(Check.notNull(c));
			return true;
		}

		@Override
		public boolean endObjectEntry() throws RuntimeException {
			return true;
		}

	}

	public class ArrayConsumer extends EventConsumer {
		private final List<EventConsumer> elementsConsumers;
		private int i = 0;

		public ArrayConsumer(Stack<EventConsumer> stack, List<EventConsumer> elementsConsumers) {
			super(stack);
			this.elementsConsumers = elementsConsumers;
		}

		@Override
		public boolean primitive(Object value) throws RuntimeException {
			nextConsumer().primitive(value);
			return true;
		}

		@Override
		public boolean startObject() throws RuntimeException {
			push(nextConsumer());
			return true;
		}

		@Override
		public boolean startArray() throws RuntimeException {
			push(nextConsumer());
			return true;
		}

		@Override
		public boolean endArray() throws RuntimeException {
			pop();
			Check.illegalargument.assertEquals(i, elementsConsumers.size());
			return true;
		}

		private EventConsumer nextConsumer() {
			EventConsumer c = elementsConsumers.get(i);
			i++;
			return c;
		}
	}

	public class EntrySeqConsumer extends EventConsumer {
		private int deep = 0;

		public EntrySeqConsumer(Stack<EventConsumer> stack) {
			super(stack);
		}

		public boolean startObject() throws RuntimeException {
			return true;
		}

		public boolean endObject() throws RuntimeException {
			return true;
		}

		public boolean startObjectEntry(String key) throws RuntimeException {
			deep++;
			return true;
		}

		public boolean endObjectEntry() throws RuntimeException {
			if(deep == 0) {
				pop();
			} else {
				deep--;
			}
			return true;
		}

		public boolean startArray() throws RuntimeException {
			return true;
		}

		public boolean endArray() throws RuntimeException {
			return true;
		}

		public boolean primitive(Object value) throws RuntimeException {
			return true;
		}

	}

	public class ValueSeqConsumer extends EventConsumer {
		private int deep = 0;

		public ValueSeqConsumer(Stack<EventConsumer> stack) {
			super(stack);
		}

		public boolean startObject() throws RuntimeException {
			return true;
		}

		public boolean endObject() throws RuntimeException {
			return true;
		}

		public boolean startObjectEntry(String key) throws RuntimeException {
			return true;
		}

		public boolean endObjectEntry() throws RuntimeException {
			return true;
		}

		public boolean startArray() throws RuntimeException {
			deep++;
			return true;
		}

		public boolean endArray() throws RuntimeException {
			if(deep == 0) {
				pop();
			} else {
				deep--;
			}
			return true;
		}

		public boolean primitive(Object value) throws RuntimeException {
			return true;
		}

	}

	public class PrimitiveConsumer extends EventConsumer {
		private final Fun1<Object, String> validator;

		public PrimitiveConsumer(Stack<EventConsumer> stack, Fun1<Object, String> validator) {
			super(stack);
			this.validator = validator;
		}

		@Override
		public boolean primitive(Object value) throws RuntimeException {
			String errorMessage = validator.apply(value);
			if(errorMessage != null) {
				throw new IllegalArgumentException(errorMessage);
			}
			pop();
			return true;
		}
	}

	public void setPrimitiveValidatorFactory(
			PrimitiveValidatorFactory primitiveValidatorFactory) {
		this.primitiveValidatorFactory = primitiveValidatorFactory;
	}
}
