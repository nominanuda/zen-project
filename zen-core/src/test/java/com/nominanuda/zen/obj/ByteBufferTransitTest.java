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

import static com.nominanuda.zen.common.Str.UTF8;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

public class ByteBufferTransitTest {
	int dbPort = 19999;
	int webPort = 19998;
	int webCliReadBufSize = 65535;
	int nbytes = 4 * 65535;

	@Test
	public void test() throws Exception {
		Server dbSrv = new Server(dbPort, new DbHandler());
		Server webSrv = new Server(webPort, new WebHandler());
		ExecutorService tp = Executors.newCachedThreadPool();
		tp.execute(dbSrv);
		tp.execute(webSrv);
		Thread.sleep(2000);
		WebClient webCli = new WebClient();
		webCli.run();
	}


	class DbHandler implements ChannelHandler {
		public void handle(SocketChannel socketChannel) {
			try {
				int bufsize = 4096;
				ByteBuffer wbuf = ByteBuffer.allocate(bufsize);
				int nints = nbytes / 4;
				int nintsPerBuf = bufsize / 4;
				int nintsWrittenToBuf = 0;
				for(int i = 0; i < nints; i++) {
					wbuf.putInt(i);
					nintsWrittenToBuf++;
					if(nintsWrittenToBuf >= nintsPerBuf) {
						wbuf.flip();
						while(wbuf.hasRemaining()) {
							socketChannel.write(wbuf);
						}
						wbuf.clear();
					}
				}
				//socketChannel.close();
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	class WebHandler implements ChannelHandler {
		public void handle(SocketChannel webChannel) {
			try {
				SocketChannel dbChannel = SocketChannel.open();
				dbChannel.connect(new InetSocketAddress("localhost", dbPort));
				int readBufSize = 4096;
				ByteBuffer rbuf = ByteBuffer.allocate(readBufSize);
				List<ByteBuffer> l = new LinkedList<ByteBuffer>();
				int tot = 0;
				int nread = 0;
				while((nread = dbChannel.read(rbuf)) >= 0) {
					if(rbuf.remaining() < 1) {
						l.add(rbuf);
						rbuf = ByteBuffer.allocate(readBufSize);
					}
					tot += nread;
					if(nbytes == tot) {
						break;
					}
				}
				l.add(rbuf);
				for(ByteBuffer b : l) {
					b.flip();
					while(b.hasRemaining()) {
						webChannel.write(b);
					}
				}
				//webChannel.close();
			} catch(Exception e) {
				e.printStackTrace();
			}

		}
	}
	class WebClient implements Runnable {
		public void run() {
			try {
				SocketChannel socketChannel = SocketChannel.open();
				socketChannel.connect(new InetSocketAddress("localhost", webPort));

				String req = "GET /foo HTTP/1.1\r\n\r\n";
				
				ByteBuffer wbuf = ByteBuffer.allocate(req.getBytes(UTF8).length);
				wbuf.clear();
				wbuf.put(req.getBytes());
				wbuf.flip();
				while(wbuf.hasRemaining()) {
					socketChannel.write(wbuf);
				}
				System.err.println("WebClient sent request:"+req);

				ByteBuffer rbuf = ByteBuffer.allocate(webCliReadBufSize);
				int tot = 0;
				int nread = 0;
				while((nread = socketChannel.read(rbuf)) >= 0) {
					System.err.println("WebClient received:"+nread);
					rbuf.clear();
					tot += nread;
					if(nbytes == tot) {
						break;
					}
				}
				System.err.println("WebClient received total:"+tot);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	static class Server implements Runnable {
		final int port;
		final ChannelHandler handler;
		public Server(final int port, final ChannelHandler handler) {
			this.handler = handler;
			this.port = port;
		}
		public void run() {
			try {
				ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
				ServerSocket ss = serverSocketChannel.socket();
				ss.setReuseAddress(true);
				ss.bind(new InetSocketAddress(port));

				while (true) {
					SocketChannel socketChannel = serverSocketChannel.accept();
					 if(socketChannel != null) {
						 handler.handle(socketChannel);
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	interface ChannelHandler {
		void handle(SocketChannel socketChannel);
	}

}
