/*******************************************************************************
 * Copyright (C) 2024 DFKI GmbH (https://www.dfki.de/en/web)
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
 * 
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.aasrepository.feature.kafka.events.model;

import java.util.Objects;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
/**
 * @author geso02 (Sonnenberg DFKI GmbH)
 */
public class AasEvent {

	private AasEventType type;
	private String id;
	private String submodelId;
	private AssetAdministrationShell aas;
	private Reference reference;
	private AssetInformation assetInformation;
	
	public AasEventType getType() {
		return type;
	}
	
	public void setType(AasEventType type) {
		this.type = type;
	}

	public void setAas(AssetAdministrationShell shell) {
		this.aas = shell;
	}
	
	public AssetAdministrationShell getAas() {
		return aas;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}
	
	public Reference getReference() {
		return reference;
	}

	public void setSubmodelId(String submodelId) {
		this.submodelId = submodelId;
	}
	
	public String getSubmodelId() {
		return submodelId;
	}

	public void setAssetInformation(AssetInformation aasInfo) {
		this.assetInformation = aasInfo;
	}	
	
	public AssetInformation getAssetInformation() {
		return assetInformation;
	}

	@Override
	public int hashCode() {
		return Objects.hash(aas, assetInformation, id, reference, submodelId, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AasEvent other = (AasEvent) obj;
		return Objects.equals(aas, other.aas) && Objects.equals(assetInformation, other.assetInformation)
				&& Objects.equals(id, other.id) && Objects.equals(reference, other.reference)
				&& Objects.equals(submodelId, other.submodelId) && type == other.type;
	}

	@Override
	public String toString() {
		return "AasEvent [type=" + type + ", id=" + id + ", submodelId=" + submodelId + ", aas=" + aas + ", reference="
				+ reference + ", assetInformation=" + assetInformation + "]";
	}
}
