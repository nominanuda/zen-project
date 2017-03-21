package com.nominanuda.zen.ffi;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import jnr.ffi.Library;
import jnr.ffi.Platform;
import jnr.ffi.annotations.Out;
import jnr.posix.POSIX;
import jnr.posix.POSIXFactory;
import jnr.posix.SpawnFileAction;
import jnr.posix.util.DefaultPOSIXHandler;


import static org.junit.Assert.*;
import static jnr.posix.SpawnFileAction.*;

public class TestProcessBuilder {
	 private static POSIX posix;
	    private static LibC libc;
	    private static final List<String> emptyEnv = Arrays.asList(new String[0]);
	    private static final List<SpawnFileAction> emptyActions = Arrays.asList(new SpawnFileAction[0]);

	    public static interface LibC {
	        int pipe(@Out int[] fds);
	    }

	    @BeforeClass
	    public static void setUpClass() throws Exception {
	        if (Platform.getNativePlatform().isUnix()) {
	            posix = POSIXFactory.getPOSIX(new DefaultPOSIXHandler(), true);
	            libc = Library.loadLibrary(LibC.class, "c");
	        }
	    }
	    

	    @Test public void inputPipe() {
	        if (Platform.getNativePlatform().isUnix()) {
	            int[] outputPipe = { -1, -1 };
	            int[] inputPipe = { -1, -1 };
	            long pid = -1;
	            try {
	            assertFalse(libc.pipe(outputPipe) < 0);
	                assertFalse(libc.pipe(inputPipe) < 0);
	                assertNotSame(-1, outputPipe[0]);
	                assertNotSame(-1, outputPipe[1]);
	                assertNotSame(-1, inputPipe[0]);
	                assertNotSame(-1, inputPipe[1]);

	                List<SpawnFileAction> actions = Arrays.asList(dup(inputPipe[0], 0), dup(outputPipe[1], 1));
	                pid = posix.posix_spawnp("cat", actions, Arrays.asList("cat", "-"), emptyEnv);
	                assertTrue(pid != -1);
	                posix.libc().close(inputPipe[0]);
	                assertEquals(3, posix.libc().write(inputPipe[1], ByteBuffer.wrap("foo".getBytes(Charset.forName("US-ASCII"))), 3));
	                posix.libc().close(inputPipe[1]); // send EOF to process

	                // close the write side of the output pipe, so read() will return immediately once the process has exited
	                posix.libc().close(outputPipe[1]);

	                ByteBuffer output = ByteBuffer.allocate(100);
	                long nbytes = posix.libc().read(outputPipe[0], output, output.remaining());
	                assertEquals(3L, nbytes);
	                output.position((int) nbytes).flip();
	                byte[] bytes = new byte[output.remaining()];
	                output.get(bytes);
	                assertEquals("foo", new String(bytes).trim());
	            } finally {
	                closePipe(outputPipe);
	                closePipe(inputPipe);
	                killChild(pid);
	            }
	        }
	    }
	    private static void closePipe(int[] fds) {
	        posix.libc().close(fds[0]);
	        posix.libc().close(fds[1]);
	    }

	    private static void killChild(long pid) {
	        if (pid > 0) {
	            posix.libc().kill((int) pid, 9); posix.libc().waitpid((int) pid, null, 0);
	        }
	    }

}
