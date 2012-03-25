package com.nominanuda.io;

import java.io.IOException;
import java.io.OutputStream;

public interface OutputStreamWriter {
	void writeTo(OutputStream os) throws IOException;
}
