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
package com.nominanuda.zen.oio;

import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.stereotype.ScopedSingletonFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import static com.nominanuda.zen.codec.Base62.B62;
import static com.nominanuda.zen.common.Maths.MATHS;
import static com.nominanuda.zen.common.Str.UTF8;

public class OioUtils {
	public static final OioUtils IO = ScopedSingletonFactory.getInstance().buildJvmSingleton(OioUtils.class);
	private static final File TMP = new File(System.getProperty("java.io.tmpdir"));
	public static final Pattern NAMED_PLACEHOLDER = Pattern.compile("(\\{[^\\}]+\\})", Pattern.MULTILINE);
	public static final Pattern UNNAMED_PLACEHOLDER = Pattern.compile("\\{\\}", Pattern.MULTILINE);

	public String readAndCloseUtf8(InputStream is) throws IOException {
		return readAndClose(is, UTF8);
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
					return MATHS.asUnsignedByte(b[0]);
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

	public int pipe(InputStream is, OutputStream os, boolean closeIs,
			boolean closeOs) throws IOException {
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

	public File newTmpDir(File tmpPath, String prefix) {
		if (tmpPath == null) tmpPath = TMP;
		File res = null;
		do {
			Double d = Math.random() * Long.MAX_VALUE;
			byte[] b = MATHS.getBytes(d.longValue());
			res = new File(tmpPath, prefix + B62.encode(b));
		} while (res.exists());
		res.mkdir();
		return res;
	}
	public File newTmpDir(String prefix) {
		return newTmpDir(null, prefix);
	}

	public File newTmpFile(File tmpPath, String prefix) throws IOException {
		if (tmpPath == null) tmpPath = TMP;
		File res = null;
		do {
			Double d = Math.random() * Long.MAX_VALUE;
			byte[] b = MATHS.getBytes(d.longValue());
			res = new File(tmpPath, prefix + B62.encode(b));
		} while (res.exists());
		res.createNewFile();
		return res;
	}
	public File newTmpFile(String prefix) throws IOException {
		return newTmpFile(null, prefix);
	}

	public void writeToFile(File dest, InputStream src) throws IOException {
		FileOutputStream fos = new FileOutputStream(dest);
		pipeAndClose(src, fos);
	}

	public void copyResourceContentRecursively(URL originUrl, File destination, boolean allowCreateDirectory) throws IOException {
		URLConnection urlConnection = originUrl.openConnection();
		if (urlConnection instanceof JarURLConnection) {
			copyJarResourcesRecursively((JarURLConnection) urlConnection, destination, allowCreateDirectory);
		} else {
			copyDirContentRecusively(new File(originUrl.getPath()), destination, allowCreateDirectory);
		}
	}

	private void copyDirContentRecusively(File sourceDir, File destDir, boolean allowCreateDirectory) throws IOException {
		if(destDir.exists() && !destDir.isDirectory()) {
			throw new IOException("not a directory:"+destDir.toString());
		}
		if (!sourceDir.isDirectory()) {
			throw new IOException("not a directory:"+sourceDir.toString());
		} else {
			for(File f : sourceDir.listFiles()) {
				copyFilesRecusively(f, destDir, allowCreateDirectory);
			}
		}
	}

	private void copyFilesRecusively(File toCopy, File destDir, boolean allowCreateDirectory) throws IOException {
		if(destDir.exists() && !destDir.isDirectory()) {
			throw new IOException("not a directory:"+destDir.toString());
		}
		if (!toCopy.isDirectory()) {
			pipeAndClose(new FileInputStream(toCopy), new FileOutputStream(new File(destDir, toCopy.getName())));
		} else {
			File newDestDir = new File(destDir, toCopy.getName());
			if (!newDestDir.exists()) {
				if(!allowCreateDirectory) {
					throw new IOException(destDir.toString()+ "target dir does not exist");
				}
				if(!newDestDir.mkdir()) {
					throw new IOException("cannot create dir:"+newDestDir.toString());
				}
			}
			for (File child : toCopy.listFiles()) {
				copyFilesRecusively(child, newDestDir, allowCreateDirectory);
			}
		}
	}

	public void copyResourcesRecursively(URL originUrl, File destination, boolean allowCreateDirectory) throws IOException {
		URLConnection urlConnection = originUrl.openConnection();
		if (urlConnection instanceof JarURLConnection) {
			copyJarResourcesRecursively((JarURLConnection) urlConnection, destination, allowCreateDirectory);
		} else {
			copyFilesRecusively(new File(originUrl.getPath()), destination, allowCreateDirectory);
		}
	}

	public void copyJarResourcesRecursively(JarURLConnection jarConnection, File destDir, boolean allowCreateDirectory)
			throws IOException {
		if(destDir.exists()) {
			if(!destDir.isDirectory()) {
				throw new IOException("not a directory:"+destDir.toString());
			}
		} else if(!allowCreateDirectory ){
			throw new IOException(destDir.toString()+ "target dir does not exist");
		} else if(! destDir.mkdir()){
			throw new IOException("cannot create dir:"+destDir.toString());
		}

		JarFile jarFile = jarConnection.getJarFile();

		for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
			JarEntry entry = e.nextElement();
			if (entry.getName().startsWith(jarConnection.getEntryName())) {
				String filename = entry.getName().substring(jarConnection.getEntryName().length());

				File f = new File(destDir, filename);
				if (!entry.isDirectory()) {
					InputStream entryInputStream = jarFile.getInputStream(entry);
					pipeAndClose(entryInputStream, new FileOutputStream(f));
					entryInputStream.close();
				} else {
					if (!tryCreateDirIfNotExists(f)) {
						throw new IOException("Could not create directory: " + f.getAbsolutePath());
					}
				}
			}
		}
	}

	private boolean tryCreateDirIfNotExists(File f) {
		return f.exists() || f.mkdir();
	}

	public boolean isEmptyDir(File dir) throws IOException {
		if(! Check.notNull(dir).isDirectory()) {
			return false;
		} else {
			String[] content = dir.list();
			if(content == null) {
				throw new IOException("unespected error listing "+dir.getAbsolutePath());
			}
			return content.length == 0;
		}
	}

	public boolean deleteRecursive(File path) throws FileNotFoundException {
		if (!path.exists()) {
			throw new FileNotFoundException(path.getAbsolutePath());
		}
		boolean ret = true;
		if (path.isDirectory()) {
			for (File f : path.listFiles()) {
				ret = ret && deleteRecursive(f);
			}
		}
		return ret && path.delete();
	}

	public void deleteRecursiveContent(File path) throws IOException {
		if (!path.exists()) {
			throw new FileNotFoundException(path.getAbsolutePath());
		}
		if(!path.isDirectory()) {
			throw new IOException(path.getAbsolutePath()+" is not a directory");
		}
		for (File f : path.listFiles()) {
			deleteRecursive(f);
		}
	}


	public long computeSizeRecursive(File path) {
		long size = 0;
		for (File file : path.listFiles()) {
			size += file.isFile() ? file.length() : computeSizeRecursive(file);
		}
		return size;
	}
}
