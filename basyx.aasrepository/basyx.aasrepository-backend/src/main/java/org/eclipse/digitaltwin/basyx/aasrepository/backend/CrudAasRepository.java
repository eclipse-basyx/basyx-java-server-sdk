/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.CrudRepository;

/**
 * Default Implementation for the {@link AasRepository} based on Spring
 * {@link CrudRepository}
 * 
 * @author mateusmolina, despen, zhangzai, kammognie
 *
 */
public class CrudAasRepository implements AasRepository {

	private final AasRepositoryBackend aasBackend;

	private final String aasRepositoryName;

	public CrudAasRepository(AasRepositoryBackend aasBackend, @Value("${basyx.aasrepo.name:aas-repo}") String aasRepositoryName) {
		this.aasBackend = aasBackend;
		this.aasRepositoryName = aasRepositoryName;
	}

	@Override
	public CursorResult<List<AssetAdministrationShell>> getAllAas(PaginationInfo pInfo) {

		Iterable<AssetAdministrationShell> iterable = aasBackend.findAll();
		List<AssetAdministrationShell> allAas = StreamSupport.stream(iterable.spliterator(), false).toList();

		TreeMap<String, AssetAdministrationShell> aasMap = allAas.stream().collect(Collectors.toMap(AssetAdministrationShell::getId, aas -> aas, (a, b) -> a, TreeMap::new));

		PaginationSupport<AssetAdministrationShell> paginationSupport = new PaginationSupport<>(aasMap, AssetAdministrationShell::getId);

		return paginationSupport.getPaged(pInfo);
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
		return aasBackend.getSubmodelReferences(aasId, pInfo);
	}

	@Override
	public void addSubmodelReference(String aasId, Reference submodelReference) {
		aasBackend.addSubmodelReference(aasId, submodelReference);
	}

	@Override
	public void removeSubmodelReference(String aasId, String submodelId) {
		aasBackend.removeSubmodelReference(aasId, submodelId);
	}

	@Override
	public void setAssetInformation(String aasId, AssetInformation aasInfo) throws ElementDoesNotExistException {
		aasBackend.setAssetInformation(aasId, aasInfo);
	}

	@Override
	public AssetInformation getAssetInformation(String aasId) throws ElementDoesNotExistException {
		return aasBackend.getAssetInformation(aasId);
	}

	@Override
	public String getName() {
		return aasRepositoryName == null ? AasRepository.super.getName() : aasRepositoryName;
	}

	@Override
	public File getThumbnail(String aasId) {
		return aasBackend.getThumbnail(aasId);
	}

	@Override
	public void setThumbnail(String aasId, String fileName, String contentType, InputStream inputStream) {
		aasBackend.setThumbnail(aasId, fileName, contentType, inputStream);
	}

	@Override
	public void deleteThumbnail(String aasId) {
		aasBackend.deleteThumbnail(aasId);
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
		if (aasId == null || aasId.isBlank())
			throw new MissingIdentifierException(aasId);
	}

	private void throwIfAasDoesNotExist(String aasId) {
		if (!aasBackend.existsById(aasId))
			throw new ElementDoesNotExistException(aasId);
	}
}
