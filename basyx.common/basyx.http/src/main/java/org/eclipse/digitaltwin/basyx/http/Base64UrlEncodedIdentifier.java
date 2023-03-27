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

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Represents a Base64URL encoded identifier
 * 
 * @author gordt, schnicke
 *
 */
public class Base64UrlEncodedIdentifier {

	public Base64UrlEncodedIdentifier(String identifier) {
		this.identifier = identifier;
	}

	private final String identifier;

	public String getIdentifier() {
		return identifier;
	}

	public static Base64UrlEncodedIdentifier fromEncodedValue(String value) {
		// Some will encode the padding...
		if (value.contains("%3D")) {
			value = URLDecoder.decode(value, StandardCharsets.US_ASCII);
		}
		// Some will cut the padding...
		var incompleteFourChars = value.length() % 4;

		if (incompleteFourChars > 0) {
			value += "==".substring(0, 4 - incompleteFourChars);
		}

		byte[] decode = Base64.getUrlDecoder().decode(value.getBytes(StandardCharsets.US_ASCII));
		return new Base64UrlEncodedIdentifier(new String(decode, StandardCharsets.UTF_8));
	}

	public String getEncodedIdentifier() {
		return encodeIdentifier(identifier);
	}

	public static String encodeIdentifier(String unencodedIdentifier) {
		// There are multiple ways to handle the padding - we decided
		// to cut off the padding, since multiple encodings may lead to confusion.
		// https://www.rfc-editor.org/rfc/rfc4648#section-5
		// https://www.rfc-editor.org/rfc/rfc4648#section-3.2
		byte[] bytes = unencodedIdentifier.getBytes(StandardCharsets.UTF_8);
		String base64urlEncoded = Base64.getUrlEncoder().encodeToString(bytes);
		return base64urlEncoded.replaceAll("=", "");
	}

	@Override
	public String toString() {
		return "Base64UrlEncodedIdentifier [identifier=" + identifier + "]";
	}
}