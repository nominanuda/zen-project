package com.nominanuda.io;

import java.io.IOException;
import java.io.InputStream;

import com.nominanuda.code.Nullable;

public interface RichInputStream {
	@Nullable String getName();
	@Nullable Integer getLength();
	InputStream getInputStream() throws IOException;
	boolean hasNamed();
	boolean hasLength();
}
