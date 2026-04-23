/*******************************************************************************
 * Copyright (C) 2026 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.submodelrepository.feature.operation.delegation;

import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.util.Arrays;

import org.eclipse.digitaltwin.basyx.core.exceptions.OperationDelegationException;
import org.junit.Test;

public class TestOperationDelegationTargetValidator {

	@Test(expected = OperationDelegationException.class)
	public void rejectUnsupportedScheme() {
		OperationDelegationTargetValidator validator = new OperationDelegationTargetValidator(new OperationDelegationSecurityProperties());
		validator.validate(URI.create("ftp://example.org/operation"));
	}

	@Test(expected = OperationDelegationException.class)
	public void rejectMissingHost() {
		OperationDelegationTargetValidator validator = new OperationDelegationTargetValidator(new OperationDelegationSecurityProperties());
		validator.validate(URI.create("http:///operation"));
	}

	@Test(expected = OperationDelegationException.class)
	public void rejectPrivateIpv4ByDefault() {
		OperationDelegationTargetValidator validator = new OperationDelegationTargetValidator(new OperationDelegationSecurityProperties());
		validator.validate(URI.create("http://10.1.2.3/operation"));
	}

	@Test
	public void allowAllowlistedCidr() {
		OperationDelegationSecurityProperties securityProperties = new OperationDelegationSecurityProperties();
		securityProperties.getAllowlist().setCidrs(Arrays.asList("10.0.0.0/8"));

		OperationDelegationTargetValidator validator = new OperationDelegationTargetValidator(securityProperties);
		validator.validate(URI.create("http://10.1.2.3/operation"));
	}

	@Test
	public void allowAllowlistedHost() {
		OperationDelegationSecurityProperties securityProperties = new OperationDelegationSecurityProperties();
		securityProperties.getAllowlist().setHosts(Arrays.asList("localhost"));

		OperationDelegationTargetValidator validator = new OperationDelegationTargetValidator(securityProperties);
		validator.validate(URI.create("http://localhost/operation"));
	}

	@Test(expected = OperationDelegationException.class)
	public void rejectBlockedPortWhenPortAllowlistIsConfigured() {
		OperationDelegationSecurityProperties securityProperties = new OperationDelegationSecurityProperties();
		securityProperties.getAllowlist().setPorts(Arrays.asList(443));

		OperationDelegationTargetValidator validator = new OperationDelegationTargetValidator(securityProperties);
		validator.validate(URI.create("http://8.8.8.8:80/operation"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void rejectInvalidCidrConfiguration() {
		OperationDelegationSecurityProperties securityProperties = new OperationDelegationSecurityProperties();
		securityProperties.getAllowlist().setCidrs(Arrays.asList("10.0.0.0/not-a-prefix"));
		new OperationDelegationTargetValidator(securityProperties);
	}

	@Test
	public void acceptValidPublicAddress() {
		OperationDelegationTargetValidator validator = new OperationDelegationTargetValidator(new OperationDelegationSecurityProperties());
		validator.validate(URI.create("http://93.184.216.34/operation"));
		assertNotNull(validator);
	}
}
