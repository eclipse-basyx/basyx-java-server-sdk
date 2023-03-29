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
package org.eclipse.digitaltwin.basyx.aasrepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasservice.AasService;
import org.eclipse.digitaltwin.basyx.aasservice.AasServiceFactory;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;

/**
 * In-memory implementation of the AasRepository
 *
 * @author schnicke, danish
 *
 */
public class InMemoryAasRepository implements AasRepository {

	private Map<String, AasService> aasServices = new LinkedHashMap<>();

	private AasServiceFactory aasServiceFactory;

	/**
	 * Creates the AasRepository using an in-memory backend.
	 * 
	 * @param aasServiceFactory
	 *            Used for creating the AasService for new AAS
	 */
	public InMemoryAasRepository(AasServiceFactory aasServiceFactory) {
		this.aasServiceFactory = aasServiceFactory;
	}

	@Override
	public List<AssetAdministrationShell> getAllAas() {
		return aasServices.values().stream().map(p -> p.getAAS()).collect(Collectors.toList());
	}

	@Override
	public AssetAdministrationShell getAas(String aasId) throws ElementDoesNotExistException {
		throwIfAasDoesNotExist(aasId);

		return aasServices.get(aasId).getAAS();
	}

	@Override
	public void createAas(AssetAdministrationShell aas) throws CollidingIdentifierException {
		throwIfAasExists(aas);

		aasServices.put(aas.getId(), aasServiceFactory.create(aas));
	}

	@Override
	public void deleteAas(String aasId) {
		throwIfAasDoesNotExist(aasId);

		aasServices.remove(aasId);
	}

	@Override
	public void updateAas(String aasId, AssetAdministrationShell aas) {
		throwIfAasDoesNotExist(aasId);
		
		aasServices.put(aasId, aasServiceFactory.create(aas));
	}

	private void throwIfAasExists(AssetAdministrationShell aas) {
		if (aasServices.containsKey(aas.getId())) {
			throw new CollidingIdentifierException();
		}
	}

	private void throwIfAasDoesNotExist(String aasId) {
		if (!aasServices.containsKey(aasId)) {
			throw new ElementDoesNotExistException(aasId);
		}
	}

	@Override
	public List<Reference> getSubmodelReferences(String aasId) {
		throwIfAasDoesNotExist(aasId);
		return aasServices.get(aasId).getSubmodelReferences();
	}

	@Override
	public void addSubmodelReference(String aasId, Reference submodelReference) {
		throwIfAasDoesNotExist(aasId);
		aasServices.get(aasId).addSubmodelReference(submodelReference);
	}

	@Override
	public void removeSubmodelReference(String aasId, String submodelId) {
		throwIfAasDoesNotExist(aasId);
		aasServices.get(aasId).removeSubmodelReference(submodelId);
	}

	@Override
	public void setAssetInformation(String aasId, AssetInformation aasInfo) throws ElementDoesNotExistException {
		throwIfAasDoesNotExist(aasId);
		aasServices.get(aasId).getAAS().setAssetInformation(aasInfo);
	}
	
	@Override
	public AssetInformation getAssetInformation(String aasId) throws ElementDoesNotExistException{
		throwIfAasDoesNotExist(aasId);
		return aasServices.get(aasId).getAAS().getAssetInformation();
	}

}
