package com.nominanuda.zen.obj;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class JsonSerializerTest {
	
	@Test
	public void testStrangeChars() {
		Obj test = Obj.make(
			"title", "Pourquoi chérie"
        );
		byte[] payloadBinary = JsonSerializer.JSON_SERIALIZER.serialize(test);
		byte[] payloadString = JsonSerializer.JSON_SERIALIZER.toString(test).getBytes();
		
		assertArrayEquals(payloadString, payloadBinary);
	}
}
