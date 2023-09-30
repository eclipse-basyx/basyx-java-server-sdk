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

import java.util.Objects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A single role based access control rule consisting of role x action x target
 * information.
 *
 * @author wege
 */
public class RbacRule {
	private String role;
	private String action;
	private ITargetInfo targetInfo;

	public RbacRule(final String role, final String action, final ITargetInfo targetInfo) {
		if (Objects.isNull(role)) {
			throw new IllegalArgumentException("role must not be null");
		}
		if (Objects.isNull(action)) {
			throw new IllegalArgumentException("action must not be null");
		}
		if (Objects.isNull(targetInfo)) {
			throw new IllegalArgumentException("targetInfo must not be null");
		}
		this.role = role;
		this.action = action;
		this.targetInfo = targetInfo;
	}

	public String getRole() {
		return role;
	}

	public String getAction() {
		return action;
	}

	public ITargetInfo getTargetInfo() {
		return targetInfo;
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

		return new EqualsBuilder().append(getRole(), rbacRule.getRole()).append(getAction(), rbacRule.getAction()).append(getTargetInfo(), rbacRule.getTargetInfo()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(getRole()).append(getAction()).append(getTargetInfo()).toHashCode();
	}

	@Override
	public String toString() {
		return new StringBuilder("RbacRule{").append("role='").append(role).append('\'').append(", action='").append(action).append('\'').append(", targetInfo='").append(targetInfo).append('\'').append('}').toString();
	}
}
