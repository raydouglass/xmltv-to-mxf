/*
 * Copyright (c) 2015, Raymond R Douglass III. All rights reserved. DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS
 * FILE HEADER.
 *
 * This file is part of XMLTV-to-MXF.
 *
 * XMLTV-to-MXF is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * XMLTV-to-MXF is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with XMLTV-to-MXF. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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
