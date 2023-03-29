/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/


package org.eclipse.digitaltwin.basyx.submodelservice.value.mapper;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.eclipse.digitaltwin.aas4j.v3.model.Blob;
import org.eclipse.digitaltwin.basyx.submodelservice.value.FileBlobValue;

/**
 * Maps {@link Blob} value to {@link FileBlobValue} 
 * 
 * @author danish
 *
 */
public class BlobValueMapper implements ValueMapper<FileBlobValue> {
	private Blob blob;
	
	public BlobValueMapper(Blob blob) {
		this.blob = blob;
	}

	@Override
	public FileBlobValue getValue() {
		return new FileBlobValue(blob.getContentType(), getDecodedString(blob.getValue()));
	}

	@Override
	public void setValue(FileBlobValue fileValue) {
		blob.setContentType(fileValue.getContentType());
		blob.setValue(getEncodedByteArray(fileValue.getValue()));
	}

	private byte[] getEncodedByteArray(String value) {
		return value.getBytes();
	}
	
	private String getDecodedString(byte[] value) {
		return new String(value, StandardCharsets.UTF_8);
	}
}
