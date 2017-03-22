package com.nominanuda.io.file;

import static com.nominanuda.dataobject.DataStructHelper.STRUCT;

import java.io.File;
import java.io.FileFilter;

import com.nominanuda.dataobject.DataArray;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.lang.Tuple2;

public class JsonFilesScanner extends AbsFilesScanner<Object> {
	private final static String JSON_FILE_NAME = "name";
	private final static String JSON_FILE_PATH = "path";
	
	public JsonFilesScanner(final boolean reverse) {
		super(reverse);
	}
	
	public static interface JsonFilesFilterResult {
		public DataArray array();
		public DataArray array(int start, int count);
		public DataObject object();
		public DataObject object(int start, int count);
	}
	
	
	/* filter */
	
	public JsonFilesFilterResult jfilter(File[] files, FileFilter filter) {
		final FilesFilterResult<Object> f = filter(files, filter);
		return new JsonFilesFilterResult() {
			@Override
			public DataArray array() {
				return STRUCT.fromMapsAndCollections(f.list());
			}
			@Override
			public DataArray array(int start, int count) {
				return STRUCT.fromMapsAndCollections(f.list(start, count));
			}
			@Override
			public DataObject object() {
				return STRUCT.fromMapsAndCollections(f.map());
			}
			@Override
			public DataObject object(int start, int count) {
				return STRUCT.fromMapsAndCollections(f.map(start, count));
			}
		};
	}
	public JsonFilesFilterResult jfilter(File parent, FileFilter filter) {
		return jfilter(parent != null ? parent.listFiles() : new File[0], filter);
	}
	
	
	
	/* list -> array */
	
	public DataArray array(File[] files, int start, int count) {
		return STRUCT.fromMapsAndCollections(list(files, start, count));
	}
	public DataArray array(File[] files) {
		return array(files, 0, files != null ? files.length : 0);
	}
	public DataArray array(File parent) {
		return parent != null ? array(parent.listFiles()) : STRUCT.newArray();
	}
	
	
	
	/* map -> object */
	
	public DataObject object(File[] files, int start, int count) {
		return STRUCT.fromMapsAndCollections(map(files, start, count));
	}
	public DataObject object(File[] files) {
		return object(files, 0, files != null ? files.length : 0);
	}
	public DataObject object(File parent) {
		return parent != null ? object(parent.listFiles()) : STRUCT.newObject();
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
	
	protected final DataObject file2json(File file) {
		return STRUCT.buildObject(
			JSON_FILE_NAME, file.getName(),
			JSON_FILE_PATH, file.getPath()
		);
	}
}