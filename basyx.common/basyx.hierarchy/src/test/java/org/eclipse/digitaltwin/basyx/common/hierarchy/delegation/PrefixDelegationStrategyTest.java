/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.common.hierarchy.delegation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for the PrefixDelegationStrategy
 *
 * @author mateusmolina
 *
 */

public class PrefixDelegationStrategyTest {

	private PrefixDelegationStrategy prefixDelegationStrategy;

	@Before
	public void setUp() {
		prefixDelegationStrategy = new PrefixDelegationStrategy("registry");
	}

	@Test
	public void testBuildDelegatedRegistryUrl_WithPort() {
		String aasId = "http://fraunhofer:8042/example/aas";
		String expectedUrl = "http://registry.fraunhofer:8042";
		String actualUrl = prefixDelegationStrategy.buildDelegatedRegistryUrl(aasId).get();
		assertEquals(expectedUrl, actualUrl);
	}

	@Test
	public void testBuildDelegatedRegistryUrl_WithoutPort() {
		String aasId = "http://fraunhofer/example/aas";
		String expectedUrl = "http://registry.fraunhofer";
		String actualUrl = prefixDelegationStrategy.buildDelegatedRegistryUrl(aasId).get();
		assertEquals(expectedUrl, actualUrl);
	}

	@Test
	public void testBuildDelegatedRegistryUrl_WithTLD() {
		String aasId = "http://fraunhofer.com:8080/example/aas";
		String expectedUrl = "http://registry.fraunhofer.com:8080";
		String actualUrl = prefixDelegationStrategy.buildDelegatedRegistryUrl(aasId).get();
		assertEquals(expectedUrl, actualUrl);
	}

	@Test
	public void testBuildDelegatedRegistryUrl_InvalidUrl() {
		String aasId = "invalid_url";
		assertTrue(prefixDelegationStrategy.buildDelegatedRegistryUrl(aasId).isEmpty());
	}

	@Test
	public void testBuildDelegatedRegistryUrl_WithBlankPrefix() {
		PrefixDelegationStrategy blankPrefixDelegationStrategy = new PrefixDelegationStrategy("");
		String aasId = "http://fraunhofer.com:8080/example/aas";
		String expectedUrl = "http://fraunhofer.com:8080";
		String actualUrl = blankPrefixDelegationStrategy.buildDelegatedRegistryUrl(aasId).get();
		assertEquals(expectedUrl, actualUrl);
	}

	@Test
	public void testBuildDelegatedRegistryUrl_NullAasId() {
		assertTrue(prefixDelegationStrategy.buildDelegatedRegistryUrl(null).isEmpty());
	}
}