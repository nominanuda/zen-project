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

import static com.nominanuda.zen.obj.JixEvent.JixEventType.*;

import java.nio.ByteBuffer;


public interface JixEvent extends JixSrc {
	public static StartObject S_OBJ = new StartObject();
	public static EndObject E_OBJ = new EndObject();
	public static StartArray S_ARR = new StartArray();
	public static EndArray E_ARR = new EndArray();
	
	ByteBuffer[] byteBuffer();
	JixEventType eventType();

	default void sendTo(JixHandler sink) {
		switch(eventType()) {
		case startObj:
			sink.startObj();
			break;
		case endObj:
			sink.endObj();
			break;
		case startArr:
			sink.startArr();
			break;
		case endArr:
			sink.endArr();
			break;
		case key:
			sink.key((Key)this);
			break;
		case val:
			sink.val((Val)this);
			break;
		}
	}

//	static abstract class BinRangeEvent extends JixEvent {
//		public BinRangeEvent(BinRange br) {
//			// TODO Auto-generated constructor stub
//		}
//	}
	
	public static final class StartObject implements JixEvent {
		private StartObject() {}
		@Override
		public JixEventType eventType() {
			return startObj;
		}
		@Override
		public ByteBuffer[] byteBuffer() {
			return new ByteBuffer[] { ByteBuffer.wrap(new byte[] {'{'})};
		}
	}

	public static final class EndObject implements JixEvent {
		private EndObject() {}
		@Override
		public JixEventType eventType() {
			return endObj;
		}
		@Override
		public ByteBuffer[] byteBuffer() {
			return new ByteBuffer[] { ByteBuffer.wrap(new byte[] {'}'})};
		}
	}

	public static final class StartArray implements JixEvent {
		private StartArray() {}
		//TODO ?? public int isValArr() {}
		@Override
		public JixEventType eventType() {
			return startArr;
		}
		@Override
		public ByteBuffer[] byteBuffer() {
			return new ByteBuffer[] { ByteBuffer.wrap(new byte[] {'['})};
		}
	}

	public static final class EndArray implements JixEvent {
		private EndArray() {}
		@Override
		public JixEventType eventType() {
			return endArr;
		}
		@Override
		public ByteBuffer[] byteBuffer() {
			return new ByteBuffer[] { ByteBuffer.wrap(new byte[] {']'})};
		}
	}
	public static enum JixEventType {startObj,endObj,startArr,endArr,key,val}
}
