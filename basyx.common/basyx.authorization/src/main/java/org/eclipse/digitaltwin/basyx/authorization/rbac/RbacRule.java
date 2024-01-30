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

import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.annotation.Nonnull;

/**
 * A single role based access control rule consisting of role x action y target
 * information.
 *
 * @author wege, danish
 */
public class RbacRule {
	private String role;
	private List<Action> action;
	private TargetInformation targetInformation;

	public RbacRule() {
	}

	public RbacRule(@Nonnull final String role, @Nonnull final List<Action> action, @Nonnull final TargetInformation targetInfo) {
		this.role = role;
		this.action = action;
		this.targetInformation = targetInfo;
	}

	public String getRole() {
		return role;
	}

	public List<Action> getAction() {
		return action;
	}

	public TargetInformation getTargetInformation() {
		return targetInformation;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof RbacRule)) {
			return false;
		}

		final RbacRule rbacRule = (RbacRule) o;

		return new EqualsBuilder().append(getRole(), rbacRule.getRole()).append(getAction(), rbacRule.getAction()).append(getTargetInformation(), rbacRule.getTargetInformation()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(getRole()).append(getAction()).append(getTargetInformation()).toHashCode();
	}

	@Override
	public String toString() {
		return new StringBuilder("RbacRule{").append("role='").append(role).append('\'').append(", action='").append(action).append('\'').append(", targetInformation='").append(targetInformation).append('\'').append('}').toString();
	}
}
