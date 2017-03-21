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
package com.nominanuda.zen.reactivestreams;

import static java.lang.Character.isSpaceChar;
import static java.lang.Character.toLowerCase;
import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.reactivestreams.Processor;

import com.nominanuda.zen.reactivestreams.Pipe.ClosedPipe;
import com.nominanuda.zen.reactivestreams.Pipe.LeftClosed;
import com.nominanuda.zen.reactivestreams.Pipe.RightClosed;
import com.nominanuda.zen.stereotype.Factory;

public class PipeTest {
//	Poem poem = new Poem();//Publisher<String>
//	Factory<Processor<String,Character>> letterSplit = () -> new LetterSplitter();
//	Processor<Character,Integer> letterAscii = new LetterAscii();
//	DistinctCounter distinctCounter = new DistinctCounter();//Subscriber<Integer>
//
//	@Test
//	public void test1() {
//		Pipe<String,Integer> p1 = Pipe
//				.fromProcessor(letterSplit)
//				.appendProcessor(letterAscii);
//		Processor<String,Integer> proc = p1.buildProcessor();
//		
//		proc.subscribe(distinctCounter);
//		poem.subscribe(proc);
//		poem.run();
//		assertEquals(new Integer(26), distinctCounter.get());
//	}
//
//	@Test
//	public void test2() {
//		Pipe<String,Integer> p1 = Pipe
//				.fromProcessor(letterSplit)
//				.appendProcessor(letterAscii);
//		Pipe<Void,Integer> p2 = p1.prependPublisher(poem);
//		LeftClosed<Poem, Integer> poem2 = p2.buildLeftClosed(Poem.class);
//		
//		poem2.subscribe(distinctCounter);
//		poem2.get().run();
//		assertEquals(new Integer(26), distinctCounter.get());
//	}
//
//	@Test
//	public void test3() {
//		Pipe<String,Integer> p1 = Pipe
//				.fromProcessor(letterSplit)
//				.appendProcessor(letterAscii);
//		Pipe<String,Void> p2 = p1.appendSubscriber(distinctCounter);
//		RightClosed<String, DistinctCounter> countingObj = p2.buildRightClosed(DistinctCounter.class);
//		poem.subscribe(countingObj);
//		poem.run();
//		assertEquals(new Integer(26), countingObj.get().get());
//	}
//
//	@Test
//	public void test4() {
//		Pipe<Void,Void> p1 = Pipe
//				.fromPublisher(poem)
//				.appendProcessor(letterSplit)
//				.appendProcessor(letterAscii)
//				.appendSubscriber(distinctCounter);
//		ClosedPipe<Poem,DistinctCounter> o = p1.buildClosed(Poem.class, DistinctCounter.class);
//		o.head().run();
//		assertEquals(new Integer(26), o.tail().get());
//	}
//
//	private class Poem extends RunnableBufferingPublisher<String> {
//		@Override
//		protected void work() throws Exception {
//			String[] words = new String[] {"The","quick","brown","fox","jumps","over","the","lazy","dog"};
//			for(String t : words) {
//				publishInternal(t);
//			}
//		}
//	}
//
//	private class LetterAscii extends BufferingProcessor<Character,Integer> implements Processor<Character,Integer> {
//		@Override
//		protected Integer applyTransformation(Character t) throws Exception {
//			return (int)(char)t;
//		};
//	}
//	private class LetterSplitter extends AbstractBufferingProcessor<String,Character> implements Processor<String,Character> {
//		@Override
//		public void onNext(String t) {
//			int len = t.length();
//			for(int i = 0; i < len; i++) {
//				if(! isSpaceChar(t.charAt(i))) {
//					publishInternal(toLowerCase(t.charAt(i)));
//				}
//			}
//		}
//	}
//
//	private class DistinctCounter extends Accumulator<Integer,Integer> {
//		private  Set<Integer> s = new HashSet<Integer>();
//
//		@Override
//		public void onNext(Integer t) {
//			s.add(t);
//		}
//
//		@Override
//		public void onComplete() {
//			setResult(s.size());
//		}
//		
//	}
}
