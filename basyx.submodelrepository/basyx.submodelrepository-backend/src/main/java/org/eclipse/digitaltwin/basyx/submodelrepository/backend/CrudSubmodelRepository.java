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

package org.eclipse.digitaltwin.basyx.submodelrepository.backend;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.*;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;
import org.eclipse.digitaltwin.basyx.serialization.SubmodelMetadataUtil;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.backend.SubmodelBackend;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Default Implementation for the {@link SubmodelRepository} using a {@link CrudRepository} as backend.
 *
 * @author danish, mateusmolina
 *
 */
public class CrudSubmodelRepository implements SubmodelRepository {

	private final SubmodelBackend submodelBackend;
	private final SubmodelServiceFactory submodelServiceFactory;
	private final String submodelRepositoryName;

	public CrudSubmodelRepository(SubmodelBackend submodelBackend, SubmodelServiceFactory submodelServiceFactory, String submodelRepositoryName) {
		this.submodelBackend = submodelBackend;
		this.submodelServiceFactory = submodelServiceFactory;
		this.submodelRepositoryName = submodelRepositoryName;

	}

	public CrudSubmodelRepository(SubmodelBackend submodelBackend, SubmodelServiceFactory submodelServiceFactory, String submodelRepositoryName,
			Collection<Submodel> submodels) {
		this(submodelBackend, submodelServiceFactory, submodelRepositoryName);

		initializeRemoteCollection(submodels);
	}

	@Override
	public String getName() {
		return submodelRepositoryName == null ? SubmodelRepository.super.getName() : submodelRepositoryName;
	}

	@Override
	public CursorResult<List<Submodel>> getAllSubmodels(PaginationInfo pInfo) {
		Iterable<Submodel> iterable = submodelBackend.findAll();
		List<Submodel> submodels = StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());

		TreeMap<String, Submodel> submodelMap = submodels.stream().collect(Collectors.toMap(Submodel::getId, submodel -> submodel, (a, b) -> a, TreeMap::new));

		PaginationSupport<Submodel> paginationSupport = new PaginationSupport<>(submodelMap, Submodel::getId);

		return paginationSupport.getPaged(pInfo);
	}

	@Override
	public CursorResult<List<Submodel>> getAllSubmodels(String semanticId, PaginationInfo pInfo) {
		return submodelBackend.getSubmodels(semanticId, pInfo);
	}

	@Override
	public Submodel getSubmodel(String submodelId) throws ElementDoesNotExistException {
		return submodelBackend.findById(submodelId).orElseThrow(() -> new ElementDoesNotExistException(submodelId));
	}

	@Override
	public void updateSubmodel(String submodelId, Submodel submodel) throws ElementDoesNotExistException {
		throwIfSubmodelDoesNotExist(submodelId);

		throwIfMismatchingIds(submodelId, submodel.getId());

		submodelBackend.save(submodel);
	}

	@Override
	public void createSubmodel(Submodel submodel) throws CollidingIdentifierException, MissingIdentifierException {
		throwIfSubmodelIdEmptyOrNull(submodel.getId());

		throwIfSubmodelExists(submodel.getId());

		submodelBackend.save(submodel);
	}

	@Override
	public void deleteSubmodel(String submodelId) throws ElementDoesNotExistException {
		throwIfSubmodelDoesNotExist(submodelId);

		submodelBackend.deleteById(submodelId);
	}

	@Override
	public CursorResult<List<SubmodelElement>> getSubmodelElements(String submodelId, PaginationInfo pInfo) throws ElementDoesNotExistException {
		return getService(submodelId).getSubmodelElements(pInfo);
	}

	@Override
	public SubmodelElement getSubmodelElement(String submodelId, String smeIdShortPath) throws ElementDoesNotExistException {
		return getService(submodelId).getSubmodelElement(smeIdShortPath);
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
		return getService(submodelId).getSubmodelElementValue(smeIdShort);
	}

	@Override
	public void setSubmodelElementValue(String submodelId, String smeIdShort, SubmodelElementValue value) throws ElementDoesNotExistException {
		getService(submodelId).setSubmodelElementValue(smeIdShort, value);
	}

	@Override
	public void createSubmodelElement(String submodelId, SubmodelElement smElement) {
		getService(submodelId).createSubmodelElement(smElement);
	}

	@Override
	public void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement smElement) throws ElementDoesNotExistException {
		getService(submodelId).createSubmodelElement(idShortPath, smElement);
	}

	@Override
	public void updateSubmodelElement(String submodelId, String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
		getService(submodelId).updateSubmodelElement(idShortPath, submodelElement);
	}

	@Override
	public void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
		getService(submodelId).deleteSubmodelElement(idShortPath);
	}

	@Override
	public OperationVariable[] invokeOperation(String submodelId, String idShortPath, OperationVariable[] input) throws ElementDoesNotExistException {
		return getService(submodelId).invokeOperation(idShortPath, input);
	}

	@Override
	public SubmodelValueOnly getSubmodelByIdValueOnly(String submodelId) throws ElementDoesNotExistException {
		return new SubmodelValueOnly(getSubmodelElements(submodelId, PaginationInfo.NO_LIMIT).getResult());
	}

	@Override
	public Submodel getSubmodelByIdMetadata(String submodelId) throws ElementDoesNotExistException {
		Submodel submodel = getSubmodel(submodelId);

		return SubmodelMetadataUtil.extractMetadata(submodel);
	}

	@Override
	public java.io.File getFileByPathSubmodel(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		return getService(submodelId).getFileByPath(idShortPath);
	}

	@Override
	public void setFileValue(String submodelId, String idShortPath, String fileName, String contentType, InputStream inputStream) throws ElementDoesNotExistException, ElementNotAFileException {
		getService(submodelId).setFileValue(idShortPath, fileName, contentType, inputStream);
	}

	@Override
	public void deleteFileValue(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		getService(submodelId).deleteFileValue(idShortPath);
	}

	@Override
	public void patchSubmodelElements(String submodelId, List<SubmodelElement> submodelElementList) {
		getService(submodelId).patchSubmodelElements(submodelElementList);
	}

	@Override
	public InputStream getFileByFilePath(String submodelId, String filePath) {
		return getService(submodelId).getFileByFilePath(filePath);
	}

	private void initializeRemoteCollection(@NonNull Collection<Submodel> submodels) {
		if (submodels.isEmpty())
			return;

		throwIfMissingId(submodels);
		throwIfHasCollidingIds(submodels);

		submodels.stream().forEach(this::createSubmodel);
	}

	private void throwIfHasCollidingIds(Collection<Submodel> submodelsToCheck) {
		Set<String> ids = new HashSet<>();

		submodelsToCheck.stream().map(Submodel::getId).filter(id -> !ids.add(id)).findAny().ifPresent(id -> {
			throw new CollidingIdentifierException(id);
		});
	}

	private void throwIfMissingId(Collection<Submodel> submodels) {
		submodels.stream().map(Submodel::getId).forEach(this::throwIfSubmodelIdEmptyOrNull);
	}

	private void throwIfMismatchingIds(String existingId, String idToBeUpdated) {

		if (!existingId.equals(idToBeUpdated))
			throw new IdentificationMismatchException();
	}

	private void throwIfSubmodelExists(String submodelId) {

		if (submodelBackend.existsById(submodelId))
			throw new CollidingIdentifierException(submodelId);
	}

	private void throwIfSubmodelIdEmptyOrNull(String submodelId) {

		if (submodelId == null || submodelId.isBlank())
			throw new MissingIdentifierException(submodelId);
	}

	private void throwIfSubmodelDoesNotExist(String submodelId) {

		if (!submodelBackend.existsById(submodelId))
			throw new ElementDoesNotExistException(submodelId);
	}

	private SubmodelService getService(String submodelId) {
		return submodelServiceFactory.create(submodelId);
	}

}
