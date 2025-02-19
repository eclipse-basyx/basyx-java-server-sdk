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

package org.eclipse.digitaltwin.basyx.submodelrepository.backend;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotInvokableException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;
import org.eclipse.digitaltwin.basyx.serialization.SubmodelMetadataUtil;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.backend.SubmodelBackend;
import org.eclipse.digitaltwin.basyx.submodelservice.backend.SubmodelFileOperations;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

/**
 * Default Implementation for the {@link SubmodelRepository} based on Spring
 * {@link CrudRepository}
 * 
 * @author danish, mateusmolina
 *
 */
public class CrudSubmodelRepository implements SubmodelRepository {

	private final SubmodelBackend submodelBackend;
	private final String submodelRepositoryName;
	private final SubmodelFileOperations submodelFileOperations;

	public CrudSubmodelRepository(SubmodelBackend submodelBackend, SubmodelFileOperations submodelFileOperations, String submodelRepositoryName) {
		this.submodelBackend = submodelBackend;
		this.submodelFileOperations = submodelFileOperations;
		this.submodelRepositoryName = submodelRepositoryName;

	}

	public CrudSubmodelRepository(SubmodelBackend submodelBackend, SubmodelFileOperations submodelFileOperations, String submodelRepositoryName,
			Collection<Submodel> submodels) {
		this(submodelBackend, submodelFileOperations, submodelRepositoryName);

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
		Iterable<Submodel> iterable = submodelBackend.findAll(); 
		List<Submodel> submodels = StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());

	    List<Submodel> filteredSubmodels = submodels.stream()
	    		.filter((submodel) -> {
	    			return submodel.getSemanticId() != null && 
	    				submodel.getSemanticId().getKeys().stream().filter((key) -> {
	    					return key.getValue().equals(semanticId);
	    				}).findAny().isPresent();
	    		})
	    		.collect(Collectors.toList());
	    
		TreeMap<String, Submodel> submodelMap = filteredSubmodels.stream().collect(Collectors.toMap(Submodel::getId, submodel -> submodel, (a, b) -> a, TreeMap::new));

		PaginationSupport<Submodel> paginationSupport = new PaginationSupport<>(submodelMap, Submodel::getId);

		return paginationSupport.getPaged(pInfo);
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
		return submodelBackend.getSubmodelElements(submodelId, pInfo);
	}

	@Override
	public SubmodelElement getSubmodelElement(String submodelId, String smeIdShortPath) throws ElementDoesNotExistException {
		return submodelBackend.getSubmodelElement(submodelId, smeIdShortPath);
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
		return submodelBackend.getSubmodelElementValue(submodelId, smeIdShort);
	}

	@Override
	public void setSubmodelElementValue(String submodelId, String smeIdShort, SubmodelElementValue value) throws ElementDoesNotExistException {
		submodelBackend.setSubmodelElementValue(submodelId, smeIdShort, value);
	}

	@Override
	public void createSubmodelElement(String submodelId, SubmodelElement smElement) {
		submodelBackend.createSubmodelElement(submodelId, smElement);
	}

	@Override
	public void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement smElement) throws ElementDoesNotExistException {
		submodelBackend.createSubmodelElement(submodelId, idShortPath, smElement);
	}

	@Override
	public void updateSubmodelElement(String submodelId, String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
		deleteAssociatedFileIfAny(submodelId, idShortPath);

		submodelBackend.updateSubmodelElement(submodelId, idShortPath, submodelElement);
	}

	@Override
	public void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
		deleteAssociatedFileIfAny(submodelId, idShortPath);

		submodelBackend.deleteSubmodelElement(submodelId, idShortPath);
	}

	@Override
	public OperationVariable[] invokeOperation(String submodelId, String idShortPath, OperationVariable[] input) throws ElementDoesNotExistException {
		throw new NotInvokableException(idShortPath);
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
		return submodelFileOperations.getFile(submodelId, idShortPath);
	}

	@Override
	public void setFileValue(String submodelId, String idShortPath, String fileName, InputStream inputStream) throws ElementDoesNotExistException, ElementNotAFileException {
		submodelFileOperations.setFileValue(submodelId, idShortPath, fileName, inputStream);
	}

	@Override
	public void deleteFileValue(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		submodelFileOperations.deleteFileValue(submodelId, idShortPath);
	}

	@Override
	public void patchSubmodelElements(String submodelId, List<SubmodelElement> submodelElementList) {
		submodelBackend.patchSubmodelElements(submodelId, submodelElementList);
	}

	@Override
	public InputStream getFileByFilePath(String submodelId, String filePath) {
		return submodelFileOperations.getInputStream(submodelId, filePath);
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

	private void deleteAssociatedFileIfAny(String submodelId, String idShortPath) {
		try {
			submodelFileOperations.deleteFileValue(submodelId, idShortPath);
		} catch (Exception e) {
		}
	}

}
