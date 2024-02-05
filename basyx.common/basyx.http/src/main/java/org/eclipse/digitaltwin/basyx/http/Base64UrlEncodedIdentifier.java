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

package org.eclipse.digitaltwin.basyx.http;

/**
 * Represents a Base64URL encoded identifier
 * 
 * @author gordt, schnicke, mateusmolina
 *
 */
public class Base64UrlEncodedIdentifier {

	private final String identifier;

	/**
	 * @param identifier unencoded identifier
	 */
	public Base64UrlEncodedIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return encoded identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	public String getEncodedIdentifier() {
		return encodeIdentifier(identifier);
	}

	@Override
	public String toString() {
		return "Base64UrlEncodedIdentifier [identifier=" + identifier + "]";
	}

	public static Base64UrlEncodedIdentifier fromEncodedValue(String value) {
		return new Base64UrlEncodedIdentifier(Base64UrlEncoder.decode(value));
	}

	public static String encodeIdentifier(String unencodedIdentifier) {
		return Base64UrlEncoder.encode(unencodedIdentifier);
	}

}