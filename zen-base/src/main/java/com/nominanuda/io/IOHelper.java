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
package com.nominanuda.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nominanuda.codec.Base64Codec;
import com.nominanuda.lang.Maths;

public class IOHelper {
	public static final IOHelper IO = new IOHelper();
	private static File TMP = new File(System.getProperty("java.io.tmpdir"));
	private static final Charset CSUTF8 = Charset.forName("UTF-8");
	public static final Pattern NAMED_PLACEHOLDER = Pattern.compile(
			"(\\{[^\\}]+\\})", Pattern.MULTILINE);
	public static final Pattern UNNAMED_PLACEHOLDER = Pattern.compile("\\{\\}",
			Pattern.MULTILINE);
	private static final Base64Codec base64 = new Base64Codec();

	public String readAndCloseUtf8(InputStream is) throws IOException {
		return readAndClose(is, CSUTF8);
	}

	public String readAndClose(InputStream is, Charset cs) throws IOException {
		return new String(readAndClose(is), cs);
	}

	public byte[] readAndClose(InputStream is) throws IOException {
		return read(is, true);
	}

	public byte[] read(InputStream is, boolean close) throws IOException {
		try {
			byte[] buffer = new byte[4096];
			int cursor = 0;
			for (;;) {
				int n = is.read(buffer, cursor, buffer.length - cursor);
				if (n < 0) {
					break;
				}
				cursor += n;
				if (cursor == buffer.length) {
					byte[] tmp = new byte[buffer.length * 2];
					System.arraycopy(buffer, 0, tmp, 0, cursor);
					buffer = tmp;
				}
			}
			if (cursor != buffer.length) {
				byte[] tmp = new byte[cursor];
				System.arraycopy(buffer, 0, tmp, 0, cursor);
				buffer = tmp;
			}
			return buffer;
		} finally {
			if (close) {
				is.close();
			}
		}
	}

	public String readAndClose(Reader r) throws IOException {
		final boolean finallyClose = true;
		char[] buffer = new char[4096];
		int cursor = 0;
		try {
			for (;;) {
				int n = r.read(buffer, cursor, buffer.length - cursor);
				if (n < 0) {
					break;
				}
				cursor += n;
				if (cursor == buffer.length) {
					char[] tmp = new char[buffer.length * 2];
					System.arraycopy(buffer, 0, tmp, 0, cursor);
					buffer = tmp;
				}
			}
			if (cursor != buffer.length) {
				char[] tmp = new char[cursor];
				System.arraycopy(buffer, 0, tmp, 0, cursor);
				buffer = tmp;
			}
		} finally {
			if (finallyClose) {
				r.close();
			}
		}
		return new String(buffer);
	}

	public Reader concat(Reader... readers) {
		return concatReaders(Arrays.asList(readers));
	}

	/**
	 * use {@link IOHelper#concatReaders(Iterable<Reader>)}
	 * @param readers
	 * @return
	 */
	@Deprecated
	public Reader concat(final Iterable<Reader> readers) {
		return concatReaders(readers);
	}
	public Reader concatReaders(final Iterable<Reader> readers) {
		return new Reader() {
			private Iterator<Reader> itr = readers.iterator();
			private Reader cur = itr.next();

			@Override
			public int read(char[] cbuf, int off, int len) throws IOException {
				int res = cur.read(cbuf, off, len);
				if (res < 0) {
					if (itr.hasNext()) {
						cur = itr.next();
						return read(cbuf, off, len);
					} else {
						return -1;
					}
				} else {
					return res;
				}
			}

			@Override
			public void close() throws IOException {
				Iterator<Reader> closeItr = readers.iterator();
				while (closeItr.hasNext()) {
					closeItr.next().close();
				}
			}
		};
	}

	public InputStream concat(InputStream... streams) {
		return concatStreams(Arrays.asList(streams));
	}

	public InputStream concatStreams(final Iterable<InputStream> streams) {
		return new InputStream() {
			private Iterator<InputStream> itr = streams.iterator();
			private InputStream cur = itr.next();

			@Override
			public int read() throws IOException {
				byte[] b = new byte[1];
				int numRead = read(b, 0, 1);
				if(numRead != 1) {
					return -1;
				} else {
					return Maths.asUnsignedByte(b[0]);
				}
			}
			@Override
			public int read(byte[] bbuf, int off, int len) throws IOException {
				int res = cur.read(bbuf, off, len);
				if (res < 0) {
					if (itr.hasNext()) {
						cur = itr.next();
						return read(bbuf, off, len);
					} else {
						return -1;
					}
				} else {
					return res;
				}
			}

			@Override
			public void close() throws IOException {
				Iterator<InputStream> closeItr = streams.iterator();
				while (closeItr.hasNext()) {
					closeItr.next().close();
				}
			}
		};
	}

	public int pipeAndClose(InputStream is, OutputStream os) throws IOException {
		return pipe(is, os, true, true);
	}

	public int pipe(InputStream is, OutputStream os, boolean closeIs, boolean closeOs) throws IOException {
		byte[] buffer = new byte[4096];
		int totWritten = 0;
		try {
			for (;;) {
				int n = is.read(buffer, 0, buffer.length);
				if (n < 0) {
					break;
				}
				os.write(buffer, 0, n);
				totWritten += n;
			}
			os.flush();
			return totWritten;
		} finally {
			if (closeIs) {
				is.close();
			}
			if (closeOs) {
				os.close();
			}
		}
	}

	public File newTmpDir(String prefix) {
		File res = null;
		do {
			Double d = Math.random() * Long.MAX_VALUE;
			byte[] b = Maths.getBytes(d.longValue());
			res = new File(TMP, prefix + base64.encodeUrlSafeNoPad(b));
		} while (res.exists());
		res.mkdir();
		return res;
	}

	public File newTmpFile(String prefix) throws IOException {
		File res = null;
		do {
			Double d = Math.random() * Long.MAX_VALUE;
			byte[] b = Maths.getBytes(d.longValue());
			res = new File(TMP, prefix + base64.encodeUrlSafeNoPad(b));
		} while (res.exists());
		res.createNewFile();
		return res;
	}

	public void copyRecursive(URL srcDir, File dstDir) throws IOException {
		copyRecursive(srcDir, dstDir, null);
	}
	public void copyRecursive(URL srcDir, File dstDir, Map<String, ?> replacementMap) throws IOException {
		if (replacementMap == null) {
			replacementMap = Collections.emptyMap();
		}
		URI uri;
		try {
			uri = srcDir.toURI();
		} catch (Exception e) {
			throw new IOException(e);
		}
		String uriStr = uri.toString();
		if (uriStr.startsWith("jar:")) {
			copyJarContentRecursive(uriStr, dstDir, replacementMap);
		} else {
			copyRecursive(new File(uri), dstDir, replacementMap);
		}
	}

	private void copyJarContentRecursive(String jarSrcDir, File dstDir, Map<String, ?> replacementMap) throws IOException {
		String[] arr = jarSrcDir.substring("jar:file:".length()).split("!");
		String jarPath = arr[0];
		String dirPathInJar = arr[1].substring(1) + "/";
		int dirPathInJarLen = dirPathInJar.length();
		try (JarFile jf = new JarFile(new File(jarPath))) {
			Enumeration<JarEntry> entries = jf.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String entryName = entry.getName();
				if (entryName.startsWith(dirPathInJar) && !dirPathInJar.equals(entryName)) {
					String subResource = entryName.substring(dirPathInJarLen);
					if (subResource.endsWith("/")) {
						File subResourceAsDir = new File(dstDir, subResource);
						if (!subResourceAsDir.exists()) {
							subResourceAsDir.mkdirs();
						} else if (!subResourceAsDir.isDirectory()) {
							throw new IOException(subResourceAsDir + " is a file and not a directory");
						}
					} else {
						int lastSlashPos = subResource.lastIndexOf("/");
						if (lastSlashPos == -1) {
							File dstFile = new File(dstDir, subResource);
							writeToFile(dstFile, simpleTemplate(jf.getInputStream(entry), replacementMap, CSUTF8));
						} else {
							File destDir = new File(dstDir, subResource.substring(0, lastSlashPos));
							if (!destDir.exists()) {
								destDir.mkdirs();
							} else if (!destDir.isDirectory()) {
								throw new IOException(destDir + " is a file and not a directory");
							}
							File dstFile = new File(destDir, getLastPathSegment(entryName));
							writeToFile(dstFile, simpleTemplate(jf.getInputStream(entry), replacementMap, CSUTF8));
						}
					}
				}
			}
		}
	}

	public String getLastPathSegment(String path) {
		String[] bits = path.split("/");
		return bits[bits.length - 1];
	}

	public String simpleTemplate(String s, Map<String, ?> map) {
		Matcher m = NAMED_PLACEHOLDER.matcher(s);
		StringBuffer sb = new StringBuffer();
		boolean found = false;
		while (m.find()) {
			found = true;
			String k = m.group(1);
			Object o = map.get(k.substring(1, k.length() - 1));
			if (o != null) {
				String repl = o.toString();
				m.appendReplacement(sb, repl);
			}
		}
		if (found) {
			m.appendTail(sb);
			return sb.toString();
		} else {
			return s;
		}
	}

	public InputStream simpleTemplate(InputStream is, Map<String, ?> map, Charset cs) throws IOException {
		String s = readAndClose(is, cs);
		String replaced = simpleTemplate(s, map);
		byte[] buf = replaced.getBytes(cs.name());
		return new ByteArrayInputStream(buf);
	}

	public void writeToFile(File dest, InputStream src) throws IOException {
		FileOutputStream fos = new FileOutputStream(dest);
		pipeAndClose(src, fos);
	}

	public void copyRecursive(File srcDir, File dstDir, Map<String, ?> replacementMap) throws FileNotFoundException, IOException {
		if (replacementMap == null) {
			replacementMap = Collections.emptyMap();
		}
		if (!(srcDir.isDirectory() && dstDir.isDirectory())) {
			throw new IllegalArgumentException();
		}
		for (File f : srcDir.listFiles()) {
			File dst = new File(dstDir, f.getName());
			if (f.isDirectory()) {
				if (dst.exists() || (!dst.mkdir())) {
					throw new IllegalStateException();
				}
				copyRecursive(f, dst, replacementMap);
			} else {
				if (dst.exists() || (!dst.createNewFile())) {
					throw new IllegalStateException();
				}
				//TODO user given charset
				pipeAndClose(simpleTemplate(new FileInputStream(f), replacementMap, CSUTF8), new FileOutputStream(dst));
			}
		}
	}
}
