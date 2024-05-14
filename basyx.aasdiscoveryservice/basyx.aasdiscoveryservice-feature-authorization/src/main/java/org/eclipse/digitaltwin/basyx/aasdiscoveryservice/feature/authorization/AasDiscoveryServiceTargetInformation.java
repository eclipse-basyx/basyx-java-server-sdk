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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.feature.authorization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformation;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformationSubtype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AasDiscoveryServiceTargetInformation
 *
 * @author mateusmolina
 *
 */
@TargetInformationSubtype(getValue = "aas-discovery-service")
public class AasDiscoveryServiceTargetInformation implements TargetInformation {

	private final List<AssetLink> assetLinks;
	private final List<String> aasIds;
	
	@JsonCreator
	public AasDiscoveryServiceTargetInformation(@JsonProperty("assetIds") List<AssetLink> assetLinks, @JsonProperty("aasIds") List<String> aasIds) {
		this.assetLinks = assetLinks;
		this.aasIds = aasIds;
	}

	@Override
	public Map<String, Object> toMap() {
		final Map<String, Object> map = new HashMap<>();
		map.put("aasIds", aasIds);
		map.put("assetIds", assetLinks);

		return map;
	}

	public List<AssetLink> getAssetLinks() {
		return assetLinks;
	}

	public List<String> getAasIds() {
		return aasIds;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		AasDiscoveryServiceTargetInformation other = (AasDiscoveryServiceTargetInformation) obj;
		return Objects.equals(aasIds, other.aasIds) && Objects.equals(assetLinks, other.assetLinks);
	}

	@Override
	public String toString() {
		return "AasDiscoveryServiceTargetInformation [aasIds=" + aasIds + ", assetIds=" + assetLinks + "]";
	}

}
