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
package org.eclipse.digitaltwin.basyx.aasrepository.backend;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasservice.AasService;
import org.eclipse.digitaltwin.basyx.aasservice.AasServiceFactory;
import org.eclipse.digitaltwin.basyx.core.BaSyxCrudRepository;
import org.eclipse.digitaltwin.basyx.core.Filter;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationUtilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.CrudRepository;

/**
 * Default Implementation for the {@link AasRepository} based on Spring
 * {@link CrudRepository}
 * 
 * @author mateusmolina, despen, zhangzai, kammognie, danish
 *
 */
public class CrudAasRepository implements AasRepository {

	private BaSyxCrudRepository<AssetAdministrationShell> aasBackend;

	private AasServiceFactory aasServiceFactory;

	private String aasRepositoryName = null;

	public CrudAasRepository(AasBackendProvider aasBackendProvider, AasServiceFactory aasServiceFactory) {
		this.aasBackend = aasBackendProvider.getCrudRepository();
		this.aasServiceFactory = aasServiceFactory;
	}

	public CrudAasRepository(AasBackendProvider aasBackendProvider, AasServiceFactory aasServiceFactory, @Value("${basyx.aasrepo.name:aas-repo}") String aasRepositoryName) {
		this(aasBackendProvider, aasServiceFactory);

		this.aasRepositoryName = aasRepositoryName;
	}

	@Override
	public CursorResult<List<AssetAdministrationShell>> getAllAas(PaginationInfo pInfo, Filter filter) {

		Iterable<AssetAdministrationShell> iterableAas = aasBackend.findAll(pInfo, filter);
		List<AssetAdministrationShell> filteredAas = StreamSupport.stream(iterableAas.spliterator(), false).collect(Collectors.toList());

		String cursor = PaginationUtilities.resolveCursor(pInfo, filteredAas, AssetAdministrationShell::getId);
		
		return new CursorResult<>(cursor, filteredAas);
	}

	@Override
	public AssetAdministrationShell getAas(String aasId) throws ElementDoesNotExistException {
		return aasBackend.findById(aasId).orElseThrow(() -> new ElementDoesNotExistException(aasId));
	}

	@Override
	public void createAas(AssetAdministrationShell aas) throws CollidingIdentifierException, MissingIdentifierException {
		throwIfAasIdEmptyOrNull(aas.getId());
		
		throwIfAasExists(aas);

		aasBackend.save(aas);
	}

	@Override
	public void deleteAas(String aasId) {
		throwIfAasDoesNotExist(aasId);

		aasBackend.deleteById(aasId);
	}

	@Override
	public void updateAas(String aasId, AssetAdministrationShell aas) {
		throwIfAasDoesNotExist(aasId);

		throwIfMismatchingIds(aasId, aas);

		aasBackend.save(aas);
	}

	@Override
	public CursorResult<List<Reference>> getSubmodelReferences(String aasId, PaginationInfo pInfo) {
		return getAasServiceOrThrow(aasId).getSubmodelReferences(pInfo);
	}

	@Override
	public void addSubmodelReference(String aasId, Reference submodelReference) {
		AasService aasService = getAasServiceOrThrow(aasId);

		aasService.addSubmodelReference(submodelReference);

		updateAas(aasId, aasService.getAAS());
	}

	@Override
	public void removeSubmodelReference(String aasId, String submodelId) {
		AasService aasService = getAasServiceOrThrow(aasId);

		aasService.removeSubmodelReference(submodelId);

		updateAas(aasId, aasService.getAAS());
	}

	@Override
	public void setAssetInformation(String aasId, AssetInformation aasInfo) throws ElementDoesNotExistException {
		AasService aasService = getAasServiceOrThrow(aasId);

		aasService.setAssetInformation(aasInfo);

		updateAas(aasId, aasService.getAAS());
	}

	@Override
	public AssetInformation getAssetInformation(String aasId) throws ElementDoesNotExistException {
		return getAasServiceOrThrow(aasId).getAssetInformation();
	}


	@Override
	public String getName() {
		return aasRepositoryName == null ? AasRepository.super.getName() : aasRepositoryName;
	}

	@Override
	public File getThumbnail(String aasId) {
		return getAasServiceOrThrow(aasId).getThumbnail();
	}

	@Override
	public void setThumbnail(String aasId, String fileName, String contentType, InputStream inputStream) {
		AasService aasService = getAasServiceOrThrow(aasId);

		aasService.setThumbnail(fileName, contentType, inputStream);

		updateAas(aasId, aasService.getAAS());
	}

	@Override
	public void deleteThumbnail(String aasId) {
		AasService aasService = getAasServiceOrThrow(aasId);

		aasService.deleteThumbnail();

		updateAas(aasId, aasService.getAAS());
	}

	private AasService getAasServiceOrThrow(String aasId) {
		AssetAdministrationShell aas = aasBackend.findById(aasId).orElseThrow(() -> new ElementDoesNotExistException(aasId));

		return aasServiceFactory.create(aas);
	}

	private void throwIfMismatchingIds(String aasId, AssetAdministrationShell newAas) {
		String newAasId = newAas.getId();

		if (!aasId.equals(newAasId))
			throw new IdentificationMismatchException();
	}

	private void throwIfAasExists(AssetAdministrationShell aas) {
		String aasId = aas.getId();
		if (aasBackend.existsById(aasId))
			throw new CollidingIdentifierException(aasId);
	}
	
	private void throwIfAasIdEmptyOrNull(String aasId) {
		if(aasId == null || aasId.isBlank())
			throw new MissingIdentifierException(aasId);
	}

	private void throwIfAasDoesNotExist(String aasId) {
		if (!aasBackend.existsById(aasId))
			throw new ElementDoesNotExistException(aasId);
	}
}
