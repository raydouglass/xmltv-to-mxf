package com.dontocsata.xmltv;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProgressInputStream extends InputStream implements AutoCloseable {

	private InputStream delegate;
	private long fileSize;

	private volatile long current = 0;

	public ProgressInputStream(File file) throws IOException {
		delegate = new FileInputStream(file);
		fileSize = file.length();
	}

	@Override
	public int read(byte[] b) throws IOException {
		int read = delegate.read(b);
		if (read > 0) {
			current += read;
		}
		return read;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int read = delegate.read(b, off, len);
		if (read > 0) {
			current += read;
		}
		return read;
	}

	@Override
	public long skip(long n) throws IOException {
		long read = delegate.skip(n);
		if (read > 0) {
			current += read;
		}
		return read;
	}

	@Override
	public void close() throws IOException {
		delegate.close();
	}

	@Override
	public int read() throws IOException {
		int read = delegate.read();
		if (read > 0) {
			current += read;
		}
		return read;
	}

	public long getFileSize() {
		return fileSize;
	}

	public long getCurrentPosition() {
		return current;
	}

}
