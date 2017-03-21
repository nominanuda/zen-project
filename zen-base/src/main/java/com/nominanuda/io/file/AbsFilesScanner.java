package com.nominanuda.io.file;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.nominanuda.lang.Check;
import com.nominanuda.lang.Tuple2;

public abstract class AbsFilesScanner<T> {
	private final Comparator<File> sorter;
	public AbsFilesScanner(final boolean reverse) {
		sorter = new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				return (reverse ? -1 : +1) * f1.getName().compareToIgnoreCase(f2.getName());
			}
		};
	}
	
	public static interface FilesFilterResult<T> {
		public List<T> list();
		public List<T> list(int start, int count);
		public Map<String, T> map();
		public Map<String, T> map(int start, int count);
		public Iterable<T> cursor();
	}
	
	
	
	/* filter */
	
	public FilesFilterResult<T> filter(File[] files, FileFilter filter) {
		List<File> acceptedFiles = new LinkedList<>();
		if (files != null) {
			Arrays.sort(files, sorter);
			for (File file : files) {
				if (filter.accept(file)) {
					acceptedFiles.add(file);
				}
			}
		}
		final File[] filteredFiles = acceptedFiles.toArray(new File[acceptedFiles.size()]);
		return new FilesFilterResult<T>() {
			@Override
			public List<T> list() {
				return list(0, filteredFiles.length);
			}
			@Override
			public List<T> list(int start, int count) {
				return AbsFilesScanner.this.list(filteredFiles, start, count);
			}
			@Override
			public Map<String, T> map() {
				return map(0, filteredFiles.length);
			}
			@Override
			public Map<String, T> map(int start, int count) {
				return AbsFilesScanner.this.map(filteredFiles, start, count);
			}
			@Override
			public Iterable<T> cursor() {
				return AbsFilesScanner.this.cursor(filteredFiles);
			}
		};
	}
	public FilesFilterResult<T> filter(File parent, FileFilter filter) {
		return filter(parent != null ? parent.listFiles() : new File[0], filter);
	}
	
	
	
	/* list */
	
	public List<T> list(File[] files, int start, int count) {
		final List<T> results = new ArrayList<>();
		scan(files, start, count, new Function<File, Void>() {
			@Override
			public Void apply(File file) {
				results.add(file2list(file));
				return null;
			}
		});
		return results;
	}
	public List<T> list(File[] files) {
		return list(files, 0, files != null ? files.length : 0);
	}
	public List<T> list(File parent) {
		return parent != null ? list(parent.listFiles()) : new ArrayList<T>();
	}
	
	
	
	/* map */
	
	public Map<String, T> map(File[] files, int start, int count) {
		final Map<String, T> results = new LinkedHashMap<>();
		scan(files, start, count, new Function<File, Void>() {
			@Override
			public Void apply(File file) {
				Tuple2<String, T> mapping = file2map(file);
				results.put(mapping.get0(), mapping.get1());
				return null;
			}
		});
		return results;
	}
	public Map<String, T> map(File[] files) {
		return map(files, 0, files != null ? files.length : 0);
	}
	public Map<String, T> map(File parent) {
		return parent != null ? map(parent.listFiles()) : new HashMap<String, T>();
	}
	
	
	
	/* cursor */
	
	public Iterable<T> cursor(final File[] files) {
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				if (files != null) {
					final Iterator<File> i = Arrays.asList(files).iterator();
					return new Iterator<T>() {
						@Override
						public boolean hasNext() {
							return i.hasNext();
						}
						@Override
						public T next() {
							return file2list(i.next());
						}
						@Override
						public void remove() {
							Check.unsupportedoperation.fail("remove not supported");
						}
					};

				}
				return Collections.emptyIterator();
			}
		};
	}
	
	
	
	/* transformations */
	
	protected T file2list(File file) {
		return null;
	};
	protected Tuple2<String, T> file2map(File file) {
		return null;
	};
	
	protected final void scan(File[] files, int start, int count, Function<File, Void> fnc) {
		if (files != null) {
			Arrays.sort(files, sorter);
			if (start < files.length) {
				for (File file : Arrays.copyOfRange(files, start, Math.min(start + count, files.length))) {
					fnc.apply(file);
				}
			}
		}
	}
}
