/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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
package org.eclipse.digitaltwin.basyx.aasenvironment.feature.authorization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.digitaltwin.basyx.aasenvironment.AasEnvironment;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformation;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformationSubtype;

/**
 * Specialization of {@link TargetInformation} for {@link AasEnvironment} target information
 *
 * @author danish
 */
@TargetInformationSubtype(getValue = "aas-environment")
public class AasEnvironmentTargetInformation implements TargetInformation {
	
	private List<String> aasIds;
	private List<String> submodelIds;
	private SerializationType serializationType;

	@JsonCreator
	public AasEnvironmentTargetInformation(final @JsonProperty("aasIds") List<String> aasIds, final @JsonProperty("submodelIds") List<String> submodelIds, final @JsonProperty("serializationType") SerializationType serializationType) {
		this.aasIds = aasIds;
		this.submodelIds = submodelIds;
		this.serializationType = serializationType;
	}

	@Override
	public Map<String, Object> toMap() {
		final Map<String, Object> map = new HashMap<>();
		map.put("aasIds", aasIds);
		map.put("submodelIds", submodelIds);
		map.put("serializationType", serializationType);
		
		return map;
	}

	public List<String> getAasIds() {
		return aasIds;
	}

	public List<String> getSubmodelIds() {
		return submodelIds;
	}

	public SerializationType getSerializationType() {
		return serializationType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(aasIds, serializationType, submodelIds);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AasEnvironmentTargetInformation other = (AasEnvironmentTargetInformation) obj;
		return Objects.equals(aasIds, other.aasIds) && serializationType == other.serializationType && Objects.equals(submodelIds, other.submodelIds);
	}

	@Override
	public String toString() {
		return "AasEnvironmentTargetInformation [aasIds=" + aasIds + ", submodelIds=" + submodelIds + ", serializationType=" + serializationType + "]";
	}

}
