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

import static com.nominanuda.zen.common.Check.illegalargument;
import static com.nominanuda.zen.common.Check.illegalstate;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Supplier;

import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.nominanuda.zen.stereotype.Factory;

@SuppressWarnings("unchecked")//TODO
public class Pipe<IN,OUT> {

	public interface LeftClosed<X,T> extends Supplier<X>, Publisher<T> {
	}
	public interface RightClosed<IN,X> extends Supplier<X>, Subscriber<IN> {
	}
	public interface ClosedPipe<X,Y> {
		X head();
		Y tail();
	}

	
	private final boolean leftOpen;
	private final boolean rightOpen;
	private final List<Object> components;

	private Pipe(List<Object> components, boolean leftOpen,boolean rightOpen) {
		this.leftOpen = leftOpen;
		this.rightOpen = rightOpen;
		LinkedList<Object> l = new LinkedList<>(components);
		this.components = Collections.unmodifiableList(l);
	}
	private Pipe(Object component, boolean leftOpen,boolean rightOpen) {
		this.leftOpen = leftOpen;
		this.rightOpen = rightOpen;
		LinkedList<Object> l = new LinkedList<>();
		l.add(component);
		this.components = Collections.unmodifiableList(l);
	}
	private Pipe(Object component, List<Object> components, boolean leftOpen,boolean rightOpen) {
		this.leftOpen = leftOpen;
		this.rightOpen = rightOpen;
		LinkedList<Object> l = new LinkedList<>();
		l.add(component);
		l.addAll(components);
		this.components = Collections.unmodifiableList(l);
	}
	private Pipe(List<Object> components, Object component, boolean leftOpen,boolean rightOpen) {
		this.leftOpen = leftOpen;
		this.rightOpen = rightOpen;
		LinkedList<Object> l = new LinkedList<>(components);
		l.add(component);
		this.components = Collections.unmodifiableList(l);
	}
	private Pipe(List<Object> components1, List<Object> components2, boolean leftOpen,boolean rightOpen) {
		this.leftOpen = leftOpen;
		this.rightOpen = rightOpen;
		LinkedList<Object> l = new LinkedList<>(components1);
		l.addAll(components2);
		this.components = Collections.unmodifiableList(l);
	}
	public static <T> Pipe<Void,T> fromPublisher(Factory<? extends Publisher<? extends T>> pubFactory) {
		return new Pipe<Void,T>(pubFactory, false, true);
	}

	public static <T> Pipe<Void,T> fromPublisher(Publisher<? extends T> pub) {
		return new Pipe<Void,T>(pub, false, true);
	}

	public static <T> Pipe<T,Void> fromSubscriber(Factory<? extends Subscriber<? extends T>> subFactory) {
		return new Pipe<T,Void>(subFactory, true, false);
	}

	public static <T> Pipe<T,Void> fromSubscriber(Subscriber<? extends T> sub) {
		return new Pipe<T,Void>(sub, true, false);
	}

	public static <X,Y> Pipe<X,Y> fromProcessor(Factory<? extends Processor<? extends X, ? extends Y>> procFactory) {
		return new Pipe<X,Y>(procFactory, true, true);
	}

	public static <X,Y> Pipe<X,Y> fromProcessor(Processor<? extends X, ? extends Y> procFactory) {
		return new Pipe<X,Y>(procFactory, true, true);
	}

	
	public Pipe<Void,OUT> prependPublisher(Factory<? extends Publisher<? extends IN>> pubFactory) {
		illegalargument.assertTrue(leftOpen, "pipe is left closed");
		return new Pipe<Void, OUT>(pubFactory, components, false, rightOpen);
	}

	public Pipe<Void,OUT> prependPublisher(Publisher<? extends IN> pub) {
		illegalargument.assertTrue(leftOpen, "pipe is left closed");
		return new Pipe<Void, OUT>(pub, components, false, rightOpen);
	}

	public <T> Pipe<T,OUT> prependProcessor(Factory<? extends Processor<? extends T, ? extends IN>> procFactory) {
		illegalargument.assertTrue(leftOpen, "pipe is left closed");
		return new Pipe<T, OUT>(procFactory, components, true, rightOpen);
	}


	public <T> Pipe<T,OUT> prependProcessor(Processor<? extends T, ? extends IN> proc) {
		illegalargument.assertTrue(leftOpen, "pipe is left closed");
		return new Pipe<T, OUT>(proc, components, true, rightOpen);
	}

	public <T> Pipe<IN,T> appendProcessor(Factory<? extends Processor<? extends OUT,? extends T>> procFactory) {
		illegalargument.assertTrue(rightOpen, "pipe is right closed");
		return new Pipe<IN,T>(components, procFactory, leftOpen, true);
	}

	public <T> Pipe<IN,T> appendProcessor(Processor<? extends OUT,? extends T> proc) {
		illegalargument.assertTrue(rightOpen, "pipe is right closed");
		return new Pipe<IN,T>(components, proc, leftOpen, true);
	}

	public Pipe<IN,Void> appendSubscriber(Factory<? extends Subscriber<? extends OUT>> subFactory) {
		illegalargument.assertTrue(rightOpen, "pipe is right closed");
		return new Pipe<IN,Void>(components, subFactory, leftOpen, false);
	}

	public Pipe<IN,Void> appendSubscriber(Subscriber<? extends OUT> sub) {
		illegalargument.assertTrue(rightOpen, "pipe is right closed");
		return new Pipe<IN,Void>(components, sub, leftOpen, false);
	}

	public <T> Pipe<IN, T> appendPipe(Pipe<? extends OUT, T> p1) {
		illegalargument.assertTrue(rightOpen, "pipe is right closed");
		illegalargument.assertTrue(p1.leftOpen, "supplied pipe is left closed");
		return new Pipe<IN, T>(components, p1.components, leftOpen, p1.rightOpen);
	}

	public <T> Pipe<T, OUT> prependPipe(Pipe<T, ? extends IN> p1) {
		illegalargument.assertTrue(leftOpen, "pipe is left closed");
		illegalargument.assertTrue(p1.rightOpen, "supplied pipe is right closed");
		return new Pipe<T, OUT>(p1, components, p1.leftOpen, rightOpen);
	}

	public boolean isRightOpen() {
		return rightOpen;
	}

	public boolean isLeftOpen() {
		return leftOpen;
	}

	public boolean isRightClosed() {
		return ! isRightOpen();
	}

	public boolean isLeftClosed() {
		return ! isLeftOpen();
	}

	public Processor<IN,OUT> buildProcessor() {
		List<Object> runtimeComponents = instantiateComponents();
		Object first = runtimeComponents.get(0);
		Object last = runtimeComponents.get(runtimeComponents.size()-1);//can be the same
		illegalstate.assertTrue(isLeftOpen() && isRightOpen());
		Publisher<OUT> end = (Publisher<OUT>)last;
		Subscriber<IN> start = (Subscriber<IN>)first;
		return new Processor<IN,OUT>() {
			@Override
			public void onSubscribe(Subscription s) {
				start.onSubscribe(s);
			}
			@Override
			public void onNext(IN t) {
				start.onNext(t);
			}
			@Override
			public void onError(Throwable t) {
				start.onError(t);
			}
			@Override
			public void onComplete() {
				start.onComplete();
			}
			@Override
			public void subscribe(Subscriber<? super OUT> s) {
				end.subscribe(s);
			}
		};
	}

	public <T> LeftClosed<T,OUT> buildLeftClosed(Class<T> cl) {
		List<Object> runtimeComponents = instantiateComponents();
		Object first = runtimeComponents.get(0);
		Object last = runtimeComponents.get(runtimeComponents.size()-1);//can be the same
		Publisher<OUT> end = (Publisher<OUT>)last;
		Publisher<?> start = (Publisher<?>)first;
		return new LeftClosed<T, OUT>() {
			@Override
			public T get() {
				return (T)start;
			}
			@Override
			public void subscribe(Subscriber<? super OUT> s) {
				end.subscribe(s);
			}
		};
	}
	public <T> RightClosed<IN,T> buildRightClosed(Class<T> cl) {
		List<Object> runtimeComponents = instantiateComponents();
		Object first = runtimeComponents.get(0);
		Object last = runtimeComponents.get(runtimeComponents.size()-1);//can be the same
		Subscriber<IN> start = (Subscriber<IN>)first;
		return new RightClosed<IN, T>() {
			@Override
			public T get() {
				return (T)last;
			}
			@Override
			public void onSubscribe(Subscription s) {
				start.onSubscribe(s);
			}
			@Override
			public void onNext(IN t) {
				start.onNext(t);
			}
			@Override
			public void onError(Throwable t) {
				start.onError(t);
			}
			@Override
			public void onComplete() {
				start.onComplete();
			}
		};
	}

	public <X,Y> ClosedPipe<X,Y> buildClosed(Class<X> clx, Class<Y> cly) {
		List<Object> runtimeComponents = instantiateComponents();
		Object first = runtimeComponents.get(0);
		Object last = runtimeComponents.get(runtimeComponents.size()-1);//can be the same
		return new ClosedPipe<X, Y>() {
			@Override
			public X head() {
				return (X)first;
			}
			@Override
			public Y tail() {
				return (Y)last;
			}
		};
	}

	private List<Object> instantiateComponents() {
		LinkedList<Object> res = new LinkedList<>();
		ListIterator<Object> itr = components.listIterator();
		Object prev = instantiateComponent(itr.next());
		res.add(prev);
		while(itr.hasNext()) {
			Object next = instantiateComponent(itr.next());
			link(prev, next);
			res.add(next);
			prev = next;
		}
		return res;
	}

	//@SuppressWarnings("unchecked")
	private void link(Object prev, Object next) {
		((Publisher<?>)prev).subscribe((Subscriber<? super Object>)next);
	}

	private Object instantiateComponent(Object c) {
		return c instanceof Factory ? ((Factory<?>)c).get() : c;
	}

}
