/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestRepositoryUrlHelper {

	private static final String BASE_URL = "http://localhost:8081";
	private static final String BASE_URL_WITH_CONTEXT = "http://localhost:8081/context";
	private static final String ADDITIONAL_PATH = "shells";

	private static final String EXPECTED_URL = BASE_URL + "/" + ADDITIONAL_PATH;
	private static final String EXPECTED_URL_WITH_CONTEXT = BASE_URL_WITH_CONTEXT + "/" + ADDITIONAL_PATH;

	@Test
	public void testUrlWithTrailingSlashAndPathNameWithNoLeadingSlash() {
		assertEquals(EXPECTED_URL, RepositoryUrlHelper.createRepositoryUrl(BASE_URL + "/", ADDITIONAL_PATH));
	}

	@Test
	public void testUrlWithoutTrailingSlashAndPathNameWithNoLeadingSlash() {
		assertEquals(EXPECTED_URL, RepositoryUrlHelper.createRepositoryUrl(BASE_URL, ADDITIONAL_PATH));
	}

	@Test
	public void testUrlWithTrailingSlashAndPathNameWithLeadingSlash() {
		assertEquals(EXPECTED_URL, RepositoryUrlHelper.createRepositoryUrl(BASE_URL + "/", "/" + ADDITIONAL_PATH));
	}

	@Test
	public void testUrlWithoutTrailingSlashAndPathNameWithLeadingSlash() {
		assertEquals(EXPECTED_URL, RepositoryUrlHelper.createRepositoryUrl(BASE_URL, "/" + ADDITIONAL_PATH));
	}

	@Test
	public void testUrlWithContextAndTrailingSlashAndPathNameWithLeadingSlash() {
		assertEquals(EXPECTED_URL_WITH_CONTEXT,
				RepositoryUrlHelper.createRepositoryUrl(BASE_URL_WITH_CONTEXT + "/", "/" + ADDITIONAL_PATH));
	}

	@Test
	public void testUrlWithContextAndNoTrailingSlashAndPathNameWithLeadingSlash() {
		assertEquals(EXPECTED_URL_WITH_CONTEXT,
				RepositoryUrlHelper.createRepositoryUrl(BASE_URL_WITH_CONTEXT, "/" + ADDITIONAL_PATH));
	}

	@Test
	public void testUrlWithContextAndTrailingSlashAndPathNameWithNoLeadingSlash() {
		assertEquals(EXPECTED_URL_WITH_CONTEXT,
				RepositoryUrlHelper.createRepositoryUrl(BASE_URL_WITH_CONTEXT + "/", ADDITIONAL_PATH));
	}

	@Test
	public void testUrlWithContextAndNoTrailingSlashAndPathNameWithNoLeadingSlash() {
		assertEquals(EXPECTED_URL_WITH_CONTEXT,
				RepositoryUrlHelper.createRepositoryUrl(BASE_URL_WITH_CONTEXT, ADDITIONAL_PATH));
	}
}
