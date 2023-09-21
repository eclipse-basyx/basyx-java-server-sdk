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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Specialization of {@link ITargetInfo} that uses the
 * aasId/smId/smElIdShortPath tuple.
 *
 * @author wege
 */ 
@SuppressWarnings("serial")
public class BaSyxObjectTargetInfo implements ITargetInfo {
	private String aasId;
	private String smId;

	private String smSemanticId;
	private String smElIdShortPath;

	public String getAasId() {
		return aasId;
	}

	public String getSmId() {
		return smId;
	}

	public String getSmSemanticId() {
		return smId;
	}

	public String getSmElIdShortPath() {
		return smElIdShortPath;
	}

	@JsonCreator
	public BaSyxObjectTargetInfo(
			final @JsonProperty("aasId") String aasId,
			final @JsonProperty("smId") String smId,
			final @JsonProperty("smSemanticId") String smSemanticId,
			final @JsonProperty("smElIdShortPath") String smElIdShortPath
	) {
		this.aasId = aasId;
		this.smId = smId;
		this.smSemanticId = smSemanticId;
		this.smElIdShortPath = smElIdShortPath;
	}

	public static class Builder {
		private String aasId;
		private String smId;
		private String smSemanticId;
		private String smElIdShortPath;

		public Builder setAasId(final String aasId) {
			this.aasId = aasId;
			return this;
		}

		public Builder setSmId(final String smId) {
			this.smId = smId;
			return this;
		}

		public Builder setSmSemanticId(final String smSemanticId) {
			this.smSemanticId = smSemanticId;
			return this;
		}

		public Builder setSmElIdShortPath(final String smElIdShortPath) {
			this.smElIdShortPath = smElIdShortPath;
			return this;
		}

		public BaSyxObjectTargetInfo build() {
			return new BaSyxObjectTargetInfo(aasId, smId, smSemanticId, smElIdShortPath);
		}
	}

	@Override
	public Map<String, String> toMap() {
		final Map<String, String> map = new HashMap<>();
		map.put("aasId", aasId);
		map.put("smId", smId);
		map.put("smSemanticId", smSemanticId);
		map.put("smElIdShortPath", smElIdShortPath);
		return map;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof BaSyxObjectTargetInfo)) {
			return false;
		}

		final BaSyxObjectTargetInfo other = (BaSyxObjectTargetInfo) o;

		return new EqualsBuilder()
				.append(getAasId(), other.getAasId())
				.append(getSmId(), other.getSmId())
				.append(getSmSemanticId(), other.getSmSemanticId())
				.append(getSmElIdShortPath(), other.getSmElIdShortPath())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(getAasId())
				.append(getSmId())
				.append(getSmSemanticId())
				.append(getSmElIdShortPath())
				.toHashCode();
	}

	@Override
	public String toString() {
		return new StringBuilder("BaSyxObjectTargetInfo{")
				.append("aasId='").append(aasId)
				.append('\'')
				.append(", smId='").append(smId)
				.append('\'')
				.append(", smSemanticId='").append(smSemanticId)
				.append('\'')
				.append(", smElIdShortPath='")
				.append(smElIdShortPath)
				.append('\'')
				.append('}')
				.toString();
	}
}
