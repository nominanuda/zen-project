package com.nominanuda.zen.io;

import java.io.File;
import java.io.FileFilter;

import com.nominanuda.zen.common.Tuple2;
import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.Obj;

public class JsonFilesScanner extends AbsFilesScanner<Object> {
	private final static String JSON_FILE_NAME = "name";
	private final static String JSON_FILE_PATH = "path";
	
	public JsonFilesScanner(final boolean reverse) {
		super(reverse);
	}
	
	public static interface JsonFilesFilterResult {
		public Arr array();
		public Arr array(int start, int count);
		public Obj object();
		public Obj object(int start, int count);
	}
	
	
	/* filter */
	
	public JsonFilesFilterResult jfilter(File[] files, FileFilter filter) {
		final FilesFilterResult<Object> f = filter(files, filter);
		return new JsonFilesFilterResult() {
			@Override
			public Arr array() {
				return Arr.fromList(f.list());
			}
			@Override
			public Arr array(int start, int count) {
				return Arr.fromList(f.list(start, count));
			}
			@Override
			public Obj object() {
				return Obj.fromMap(f.map());
			}
			@Override
			public Obj object(int start, int count) {
				return Obj.fromMap(f.map(start, count));
			}
		};
	}
	public JsonFilesFilterResult jfilter(File parent, FileFilter filter) {
		return jfilter(parent != null ? parent.listFiles() : new File[0], filter);
	}
	
	
	
	/* list -> array */
	
	public Arr array(File[] files, int start, int count) {
		return Arr.fromList(list(files, start, count));
	}
	public Arr array(File[] files) {
		return array(files, 0, files != null ? files.length : 0);
	}
	public Arr array(File parent) {
		return parent != null ? array(parent.listFiles()) : Arr.make();
	}
	
	
	
	/* map -> object */
	
	public Obj object(File[] files, int start, int count) {
		return Obj.fromMap(map(files, start, count));
	}
	public Obj object(File[] files) {
		return object(files, 0, files != null ? files.length : 0);
	}
	public Obj object(File parent) {
		return parent != null ? object(parent.listFiles()) : Obj.make();
	}
	
	
	
	/* transformations */
	
	@Override
	protected Object file2list(File file) {
		return file2json(file);
	}
	
	@Override
	protected Tuple2<String, Object> file2map(File file) {
		return new Tuple2<String, Object>(file.getName(), file2json(file));
	}
	
	protected final Obj file2json(File file) {
		return Obj.make(
			JSON_FILE_NAME, file.getName(),
			JSON_FILE_PATH, file.getPath()
		);
	}
}