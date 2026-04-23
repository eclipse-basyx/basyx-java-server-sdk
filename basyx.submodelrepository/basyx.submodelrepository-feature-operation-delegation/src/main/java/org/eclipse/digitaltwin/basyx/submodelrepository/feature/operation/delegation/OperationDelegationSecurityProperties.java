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

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Security properties for outbound operation delegation requests.
 */
@ConfigurationProperties(prefix = OperationDelegationSubmodelRepositoryFeature.FEATURENAME + ".security")
public class OperationDelegationSecurityProperties {

	private boolean enabled = true;
	private boolean denyPrivateTargets = true;
	private boolean denyLinkLocalTargets = true;
	private boolean denyLoopbackTargets = true;
	private boolean denyMetadataTargets = true;
	private boolean denyRedirects = true;
	private Allowlist allowlist = new Allowlist();

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isDenyPrivateTargets() {
		return denyPrivateTargets;
	}

	public void setDenyPrivateTargets(boolean denyPrivateTargets) {
		this.denyPrivateTargets = denyPrivateTargets;
	}

	public boolean isDenyLinkLocalTargets() {
		return denyLinkLocalTargets;
	}

	public void setDenyLinkLocalTargets(boolean denyLinkLocalTargets) {
		this.denyLinkLocalTargets = denyLinkLocalTargets;
	}

	public boolean isDenyLoopbackTargets() {
		return denyLoopbackTargets;
	}

	public void setDenyLoopbackTargets(boolean denyLoopbackTargets) {
		this.denyLoopbackTargets = denyLoopbackTargets;
	}

	public boolean isDenyMetadataTargets() {
		return denyMetadataTargets;
	}

	public void setDenyMetadataTargets(boolean denyMetadataTargets) {
		this.denyMetadataTargets = denyMetadataTargets;
	}

	public boolean isDenyRedirects() {
		return denyRedirects;
	}

	public void setDenyRedirects(boolean denyRedirects) {
		this.denyRedirects = denyRedirects;
	}

	public Allowlist getAllowlist() {
		return allowlist;
	}

	public void setAllowlist(Allowlist allowlist) {
		this.allowlist = allowlist;
	}

	public static class Allowlist {
		private List<String> hosts = new ArrayList<>();
		private List<String> cidrs = new ArrayList<>();
		private List<Integer> ports = new ArrayList<>();

		public List<String> getHosts() {
			return hosts;
		}

		public void setHosts(List<String> hosts) {
			this.hosts = hosts;
		}

		public List<String> getCidrs() {
			return cidrs;
		}

		public void setCidrs(List<String> cidrs) {
			this.cidrs = cidrs;
		}

		public List<Integer> getPorts() {
			return ports;
		}

		public void setPorts(List<Integer> ports) {
			this.ports = ports;
		}
	}
}
