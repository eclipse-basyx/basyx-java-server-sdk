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
package org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformation;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformationSubtype;

/**
 * Specialization of {@link TargetInformation} for Aas target information
 *
 * @author danish
 */
@TargetInformationSubtype(getValue = "aas")
public class AasTargetInformation implements TargetInformation {
	
	private List<String> aasIds;

	@JsonCreator
	public AasTargetInformation(final @JsonProperty("aasIds") List<String> aasIds) {
		this.aasIds = aasIds;
	}

	@Override
	public Map<String, Object> toMap() {
		final Map<String, Object> map = new HashMap<>();
		map.put("aasIds", aasIds);
		return map;
	}

	@Override
	public int hashCode() {
		return Objects.hash(aasIds);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AasTargetInformation other = (AasTargetInformation) obj;
		return Objects.equals(aasIds, other.aasIds);
	}

	@Override
	public String toString() {
		return "AasTargetInformation [aasIds=" + aasIds + "]";
	}
	
	public List<String> getAasIds() {
		return aasIds;
	}

}
