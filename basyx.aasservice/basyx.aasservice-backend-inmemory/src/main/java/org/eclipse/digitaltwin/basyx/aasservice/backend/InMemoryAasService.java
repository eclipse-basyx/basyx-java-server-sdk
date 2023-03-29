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
package org.eclipse.digitaltwin.basyx.aasservice.backend;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasservice.AasService;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;

/**
 * Implements the AasService as in-memory variant
 * 
 * @author schnicke
 * 
 */
public class InMemoryAasService implements AasService {
	private AssetAdministrationShell aas;

	/**
	 * Creates the InMemory AasService containing the passed AAS
	 * 
	 * @param aas
	 */
	public InMemoryAasService(AssetAdministrationShell aas) {
		this.aas = aas;
	}

	@Override
	public AssetAdministrationShell getAAS() {
		return aas;
	}

	@Override
	public List<Reference> getSubmodelReferences() {
		return aas.getSubmodels();
	}

	@Override
	public void addSubmodelReference(Reference submodelReference) {
		aas.getSubmodels().add(submodelReference);
	}

	@Override
	public void removeSubmodelReference(String submodelId) {
		Reference specificSubmodelReference = getSubmodelReferenceById(submodelId);

		aas.getSubmodels().remove(specificSubmodelReference);
	}

	@Override
	public void setAssetInformation(AssetInformation aasInfo) {
		aas.setAssetInformation(aasInfo);		
	}
	
	@Override
	public AssetInformation getAssetInformation() {		
		return aas.getAssetInformation();
	}

	private Reference getSubmodelReferenceById(String submodelId) {
		List<Reference> submodelReferences = aas.getSubmodels();

		Reference specificSubmodelReference = submodelReferences.stream().filter(reference -> {
			List<Key> keys = reference.getKeys();
			Key foundKey = keys.stream().filter(key -> key.getType().equals(KeyTypes.SUBMODEL)).findFirst().get();
			return foundKey.getValue().equals(submodelId);
		}).findFirst().orElseThrow(() -> new ElementDoesNotExistException(submodelId));

		return specificSubmodelReference;
	}
	
}
