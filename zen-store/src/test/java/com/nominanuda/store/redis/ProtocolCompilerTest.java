package com.nominanuda.store.redis;

import java.io.*;

import org.junit.*;

public class ProtocolCompilerTest {
	@Test
	public void shouldCompileProtocol() throws Exception {
		InputStream source = new FileInputStream(new File(System.getProperty("user.dir").concat("/src/test/java/com/nominanuda/store/redis/protocol.r")));
		ProtocolCompiler compiler = new ProtocolCompiler(source);
		OutputStream redis = compiler.getProtocol();
		FileWriter writer = new FileWriter(new File(System.getProperty("user.dir").concat("/src/main/java/com/nominanuda/store/redis/Redis.java")));
		writer.append(redis.toString());
		writer.flush();
		writer.close();
	}
}
