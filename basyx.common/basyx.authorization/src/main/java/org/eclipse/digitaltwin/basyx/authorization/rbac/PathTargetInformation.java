/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Target information for {@link SimpleRbacFilesAuthorizer} file paths for RBAC
 * authorization scheme.
 *
 * @author wege
 */
@SuppressWarnings("serial")
public class PathTargetInformation implements ITargetInformation {
	private Path path;

	public Path getPath() {
		return path;
	}

	public PathTargetInformation(final Path path) {
		this.path = path;
	}

	@JsonCreator
	public PathTargetInformation(final @JsonProperty("path") String path) {
		this(Paths.get(path));
	}

	@Override
	public Map<String, String> toMap() {
		final Map<String, String> map = new HashMap<>();
		map.put("path", path.toString());
		return map;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof TagTargetInformation)) {
			return false;
		}

		final PathTargetInformation other = (PathTargetInformation) o;

		return new EqualsBuilder().append(getPath(), other.getPath()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(getPath()).toHashCode();
	}

	@Override
	public String toString() {
		return new StringBuilder("BaSyxObjectTargetInformation{").append("tag='").append(path).append('\'').append('}').toString();
	}
}
