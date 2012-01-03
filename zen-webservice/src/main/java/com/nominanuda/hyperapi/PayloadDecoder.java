package com.nominanuda.hyperapi;

public interface PayloadDecoder {

	<H,R extends H> R decode (byte[] payload, String mediaType, Class<H> hint);
	boolean canDecode(byte[] payload, String mediaType, Class<?> hint);
}
