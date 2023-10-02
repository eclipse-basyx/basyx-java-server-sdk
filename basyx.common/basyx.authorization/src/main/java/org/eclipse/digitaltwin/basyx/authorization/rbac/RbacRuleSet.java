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
package org.eclipse.digitaltwin.basyx.authorization.rbac;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A set of {@link RbacRule} used in authorization points or in policy
 * information points.
 *
 * @author wege
 */
public class RbacRuleSet {
	private final Set<RbacRule> rules;

	public RbacRuleSet() {
		this.rules = new HashSet<>();
	}

	public RbacRuleSet(Set<RbacRule> rules) {
		this.rules = new HashSet<>(rules);
	}

	public Set<RbacRule> getRules() {
		return Collections.unmodifiableSet(this.rules);
	}

	public boolean addRule(final RbacRule rbacRule) {
		return this.rules.add(rbacRule);
	}

	public boolean deleteRule(final RbacRule rbacRule) {
		return this.rules.remove(rbacRule);
	}

	@Override
	public String toString() {
		return new StringBuilder("RbacRuleSet{").append("rules=").append(rules).append('}').toString();
	}
}
