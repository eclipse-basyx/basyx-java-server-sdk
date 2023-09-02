/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
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
package org.eclipse.digitaltwin.basyx.authorization.rbac;

import java.util.List;

/**
 * Interface for checking role based access rules against some target
 * information.
 *
 * @author wege
 */
public interface IRbacRuleChecker {
	/**
	 * Checks if the given the given roles x action x target information tuple is
	 * satisfied in some context. The context with the rules to match against should
	 * originate from the implementing class.
	 *
	 * @param roles
	 *            a list of user roles as delivered by the auth provider, e.g. in a
	 *            Keycloak access token.
	 * @param action
	 *            the action to check for like
	 *            {@link org.eclipse.basyx.extensions.aas.aggregator.authorization.AASAggregatorScopes#READ_SCOPE}.
	 * @param targetInformation
	 *            the features of the target object which access should be checked
	 *            for like {@link BaSyxObjectTargetInformation}.
	 * @return true if the rule is satisfied, false otherwise
	 */
	public boolean checkRbacRuleIsSatisfied(final List<String> roles, final String action, final ITargetInformation targetInformation);
}
