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
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasservice.AasService;
import org.eclipse.digitaltwin.basyx.aasservice.AasServiceFactory;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;

/**
 * In-memory implementation of the AasRepository
 *
 * @author schnicke, danish, kammognie
 *
 */
public class InMemoryAasRepository implements AasRepository {

	private Map<String, AasService> aasServices = new LinkedHashMap<>();

	private AasServiceFactory aasServiceFactory;
	
	private String aasRepositoryName;
	
	/**
	 * Creates the AasRepository using an in-memory backend.
	 * 
	 * @param aasServiceFactory
	 *            Used for creating the AasService for new AAS
	 */
	public InMemoryAasRepository(AasServiceFactory aasServiceFactory) {
		this.aasServiceFactory = aasServiceFactory;
	}
	
	/**
	 * Creates the AasRepository using an in-memory backend.
	 * 
	 * @param aasServiceFactory Used for creating the AasService for new AAS
	 * @param aasRepositoryName Name of the AASRepository
	 */
	public InMemoryAasRepository(AasServiceFactory aasServiceFactory, String aasRepositoryName) {
		this(aasServiceFactory);
		this.aasRepositoryName = aasRepositoryName;
	}

	@Override
	public CursorResult<List<AssetAdministrationShell>> getAllAas(PaginationInfo pInfo) {
		List<AssetAdministrationShell> allAas = aasServices.values()
				.stream()
				.map(AasService::getAAS)
				.collect(Collectors.toList());

		TreeMap<String, AssetAdministrationShell> aasMap = allAas.stream()
				.collect(Collectors.toMap(AssetAdministrationShell::getId, aas -> aas, (a, b) -> a, TreeMap::new));

		PaginationSupport<AssetAdministrationShell> paginationSupport = new PaginationSupport<>(aasMap, AssetAdministrationShell::getId);
		CursorResult<List<AssetAdministrationShell>> paginatedAAS = paginationSupport.getPaged(pInfo);
		return paginatedAAS;
	}

	@Override
	public AssetAdministrationShell getAas(String aasId) throws ElementDoesNotExistException {
		throwIfAasDoesNotExist(aasId);

		return aasServices.get(aasId)
				.getAAS();
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

		throwIfMismatchingIds(aasId, aas);

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
	public CursorResult<List<Reference>> getSubmodelReferences(String aasId, PaginationInfo pInfo) {
		throwIfAasDoesNotExist(aasId);

		CursorResult<List<Reference>> paginatedSubmodelReference = aasServices.get(aasId).getSubmodelReferences(pInfo);

		return paginatedSubmodelReference;
	}

	@Override
	public void addSubmodelReference(String aasId, Reference submodelReference) {
		throwIfAasDoesNotExist(aasId);
		aasServices.get(aasId)
				.addSubmodelReference(submodelReference);
	}

	@Override
	public void removeSubmodelReference(String aasId, String submodelId) {
		throwIfAasDoesNotExist(aasId);
		aasServices.get(aasId)
				.removeSubmodelReference(submodelId);
	}

	@Override
	public void setAssetInformation(String aasId, AssetInformation aasInfo) throws ElementDoesNotExistException {
		throwIfAasDoesNotExist(aasId);
		aasServices.get(aasId)
				.getAAS()
				.setAssetInformation(aasInfo);
	}

	@Override
	public AssetInformation getAssetInformation(String aasId) throws ElementDoesNotExistException {
		throwIfAasDoesNotExist(aasId);
		return aasServices.get(aasId)
				.getAAS()
				.getAssetInformation();
	}
  
	@Override
	public String getName() {
		return aasRepositoryName == null ? AasRepository.super.getName() : aasRepositoryName;
	}
  
	private void throwIfMismatchingIds(String aasId, AssetAdministrationShell newAas) {
		String newAasId = newAas.getId();

		if (!aasId.equals(newAasId))
			throw new IdentificationMismatchException();
	}

}
