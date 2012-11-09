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
import java.util.Map.Entry;
import java.util.Stack;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

import com.nominanuda.code.Nullable;
import com.nominanuda.dataobject.WrappingRecognitionException;
import com.nominanuda.dataobject.transform.BaseJsonTransformer;
import com.nominanuda.dataobject.transform.JsonTransformer;
import com.nominanuda.lang.Check;
import com.nominanuda.lang.Fun1;
import com.nominanuda.lang.ObjectFactory;

import static com.nominanuda.dataobject.schema.JclLexer.*;


public class JclValidatorFactory {
	private CommonTree root;
	private PrimitiveValidatorFactory primitiveValidatorFactory = new DefaultPrimitiveValidatorFactory();
	private final LinkedHashMap<String, Tree> tMap = new LinkedHashMap<String, Tree>();

	public JclValidatorFactory(String jclExpr) throws Exception {
		try {
			CharStream cs = new ANTLRReaderStream(new StringReader(jclExpr));
			JclLexer lexer = new JclLexer(cs);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			JclParser parser = new JclParser(tokens);
			CommonTree tree = (CommonTree)parser.program().getTree();
			root = tree;
			Tree cur = root;
			if(cur.getText() != null) {
				tMap.put("", cur);
			} else {
				int len = cur.getChildCount();
				boolean defaultAdded = false;
				for(int i = 0; i < len; i++) {
					Tree t = cur.getChild(i);
					Check.illegalstate.assertEquals(TYPEDEF, t.getType());
					String tname = t.getChild(0).getText();
					tMap.put(tname, t.getChild(1));
					if(!defaultAdded) {
						tMap.put("", t.getChild(1));
						defaultAdded = true;
					}
				}
			}
		} catch(WrappingRecognitionException e) {
			throw e.getWrappedException();
		}
	}

	public ObjectFactory<JsonTransformer> buildValidatorFactory(@Nullable String type) {
		String t = Check.ifNull(type, "");
		final Tree tree = tMap.get(t);
		return new ObjectFactory<JsonTransformer>() {
			public JsonTransformer getObject() {
				return makeValueTransformer(tree);
			}
		};
	}

	public JsonTransformer buildValidator(@Nullable String type) {
		String t = Check.ifNull(type, "");
		Tree tree = tMap.get(t);
		return makeValueTransformer(tree);
	}

	private JsonTransformer makeValueTransformer(Tree cur) {
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
					EventConsumer c = makeConsumer(entry.getChild(2), stack);
					c.setPredicate(tokenToPredicate(entry.getChild(1)));
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
		case ARRAYVAL:
			Tree primitive = node.getChild(0);
			Tree existential = node.getChild(1);
			return new PrimitiveConsumer(stack, makePrimitiveValidator(primitive), tokenToPredicate(existential));
		case TYPEREF:
			String tname = node.getChild(0).getText();
			Tree typedef = Check.illegalargument.assertNotNull(
					tMap.get(tname), "type not found "+tname);
			return makeConsumer(typedef, stack);
		default:
			return Check.illegalstate.fail();
		}
	} 

	private ExistentialPredicate tokenToPredicate(Tree predTnkNode) {
		String p = predTnkNode.getChildCount() > 0 ? predTnkNode.getChild(0).getText() : null;
		return new ExistentialPredicate(p);
	}

	private Fun1<Object, String> makePrimitiveValidator(Tree node) {
		Tree t = node.getChild(0);
		String valDef = t == null 
			? DefaultPrimitiveValidatorFactory.ANYPRIMITIVE
			: t.getText();
		return primitiveValidatorFactory.create(valDef);
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
			validateExit();
			pop();
			return true;
		}

		@Override
		public boolean primitive(Object value) throws RuntimeException {
			if(value == null) {
				if(! isNullable()) {
					throw new ValidationException("null value of not-nul property");
				} else {
					return true;
				}
			} else {
				throw new ValidationException("found "+value.toString()+" where object expected");
			}
		}
		private void validateExit() {
			for(Entry<String,EventConsumer> e : entryConsumers.entrySet()) {
				String k = e.getKey();
				EventConsumer c = e.getValue();
				if(! isEntrySequence(k)) {
					if(! c.isOptional()) {
						throw new ValidationException("missing property:"+k);
					}
				}
			}
			
		}

		private boolean isEntrySequence(String k) {
			return "*".equals(k);
		}

		@Override
		public boolean startObjectEntry(String key) throws RuntimeException {
			EventConsumer c = entryConsumers.remove(key);
			if(c == null && entryConsumers.containsKey("*")) {
				c = entryConsumers.get("*");
			}
			if(c == null) {
				throw new ValidationException("unexpected entry with key "+key);
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
		private boolean startArraySeen = false;

		public ArrayConsumer(Stack<EventConsumer> stack, List<EventConsumer> elementsConsumers) {
			super(stack);
			this.elementsConsumers = elementsConsumers;
		}

		@Override
		public boolean primitive(Object value) throws RuntimeException {
			EventConsumer c = pushNextConsumer();
			c.primitive(value);
			return true;
		}

		private EventConsumer pushNextConsumer() {
			EventConsumer c = nextConsumer();
			if(c == null) {
				throw new ValidationException("wrong number of elements in array, at position "+i);
			}
			push(c);
			return c;
		}

		@Override
		public boolean startObject() throws RuntimeException {
			EventConsumer c = pushNextConsumer();
			c.startObject();
			return true;
		}

		@Override
		public boolean startArray() throws RuntimeException {
			if(startArraySeen) {
				EventConsumer c = pushNextConsumer();
				c.startArray();
			} else {
				startArraySeen = true;
			}
			return true;
		}

		@Override
		public boolean endArray() throws RuntimeException {
			pop();
			if(i != elementsConsumers.size()) {
				throw new ValidationException("array expected "+elementsConsumers.size()+" elements, but got "+i);
			}
			return true;
		}

		private @Nullable EventConsumer nextConsumer() {
			if(i >= elementsConsumers.size()) {
				return null;
			} else {
				EventConsumer c = elementsConsumers.get(i);
				i++;
				return c;
			}
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

		public PrimitiveConsumer(Stack<EventConsumer> stack, Fun1<Object, String> validator, ExistentialPredicate pred) {
			super(stack, pred);
			this.validator = validator;
		}

		@Override
		public boolean primitive(Object value) throws RuntimeException {
			if(value == null) {
				if(! isNullable()) {
					throw new ValidationException("null value of not-nul property");
				} else {
					pop();
					return true;
				}
			} else {
				String errorMessage = validator.apply(value);
				if(errorMessage != null) {
					throw new ValidationException(errorMessage);
				}
				pop();
				return true;
			}
		}

	}

	public void setPrimitiveValidatorFactory(
			PrimitiveValidatorFactory primitiveValidatorFactory) {
		this.primitiveValidatorFactory = primitiveValidatorFactory;
	}
}
