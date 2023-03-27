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

package org.eclipse.digitaltwin.basyx.http.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.junit.Test;

/**
 * Tests the Base64UrlEncodedIdentifier class
 * 
 * @author gordt, schnicke
 *
 */
public class TestBase64UrlEncodedIdentifier {

	@Test
	public void encodingOfPaddedValue() throws UnsupportedEncodingException {
		String plainValue = "username:password";
		assertBase64UrlEncodingEndsWithEqualsSign(plainValue);

		Base64UrlEncodedIdentifier identifier = new Base64UrlEncodedIdentifier(plainValue);
		String encodedValue = identifier.getEncodedIdentifier();

		assertEquals("dXNlcm5hbWU6cGFzc3dvcmQ", encodedValue);
	}

	@Test
	public void decodingOfCutOffPaddingValue() {
		// Was YQ==
		Base64UrlEncodedIdentifier restoredIdentifier = Base64UrlEncodedIdentifier.fromEncodedValue("YQ");
		assertEquals("a", restoredIdentifier.getIdentifier());

		// Was dXNlcm5hbWU6cGFzc3dvcmQ=
		restoredIdentifier = Base64UrlEncodedIdentifier.fromEncodedValue("dXNlcm5hbWU6cGFzc3dvcmQ");
		assertEquals("username:password", restoredIdentifier.getIdentifier());
	}

	@Test
	public void decodingOfEncodedPaddingValue() {
		// Was YQ==
		Base64UrlEncodedIdentifier restoredIdentifier = Base64UrlEncodedIdentifier.fromEncodedValue("YQ%3D%3D");
		assertEquals("a", restoredIdentifier.getIdentifier());

		// Was dXNlcm5hbWU6cGFzc3dvcmQ=
		restoredIdentifier = Base64UrlEncodedIdentifier.fromEncodedValue("dXNlcm5hbWU6cGFzc3dvcmQ%3D");
		assertEquals("username:password", restoredIdentifier.getIdentifier());
	}

	private void assertBase64UrlEncodingEndsWithEqualsSign(String plainValue) throws UnsupportedEncodingException {
		byte[] bytes = plainValue.getBytes("UTF-8");
		String base64urlEncoded = Base64.getUrlEncoder().encodeToString(bytes);
		assertTrue(base64urlEncoded.endsWith("="));
	}
}
