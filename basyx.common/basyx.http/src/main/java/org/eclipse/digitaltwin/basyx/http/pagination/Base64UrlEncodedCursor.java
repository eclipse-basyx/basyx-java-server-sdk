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

package org.eclipse.digitaltwin.basyx.http.pagination;

import org.eclipse.digitaltwin.basyx.http.Base64UrlEncoder;

/**
 * Represents a Base64URL encoded cursor
 *
 * @author mateusmolina, despen
 *
 */
public class Base64UrlEncodedCursor {

	private final String encodedCursor;
	private final String decodedCursor;

	public Base64UrlEncodedCursor(String encodedCursor) {
		this.encodedCursor = encodedCursor;
		this.decodedCursor = encodedCursor == null ? null : Base64UrlEncoder.decode(encodedCursor);
	}

	public String getDecodedCursor() {
		return decodedCursor;
	}

	public String getEncodedCursor() {
		return encodedCursor;
	}

	@Override
	public String toString() {
		return "Base64UrlEncodedCursor [cursor=" + encodedCursor + "]";
	}

	public static Base64UrlEncodedCursor fromUnencodedCursor(String unencodedCursor) {
		return new Base64UrlEncodedCursor(encodeCursor(unencodedCursor));
	}

	public static String encodeCursor(String unencodedCursor) {
		return Base64UrlEncoder.encode(unencodedCursor);
	}
}