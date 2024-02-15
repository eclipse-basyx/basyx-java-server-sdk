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
package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.feature.authorization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformation;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformationSubtype;

/**
 * Specialization of {@link TargetInformation} for ConceptDescription target information
 *
 * @author danish
 */
@TargetInformationSubtype(getValue = "concept-description")
public class ConceptDescriptionTargetInformation implements TargetInformation {
	
	private String conceptDescriptionId;

	@JsonCreator
	public ConceptDescriptionTargetInformation(final @JsonProperty("conceptDescriptionId") String conceptDescriptionId) {
		this.conceptDescriptionId = conceptDescriptionId;
	}

	@Override
	public Map<String, Object> toMap() {
		final Map<String, Object> map = new HashMap<>();
		map.put("conceptDescriptionId", conceptDescriptionId);
		return map;
	}

	@Override
	public int hashCode() {
		return Objects.hash(conceptDescriptionId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConceptDescriptionTargetInformation other = (ConceptDescriptionTargetInformation) obj;
		return Objects.equals(conceptDescriptionId, other.conceptDescriptionId);
	}

	@Override
	public String toString() {
		return "ConceptDescriptionTargetInformation [conceptDescriptionId=" + conceptDescriptionId + "]";
	}

	public String getConceptDescriptionId() {
		return conceptDescriptionId;
	}

}
