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

package org.eclipse.digitaltwin.basyx.submodelrepository;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.IdentificationMismatchException;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * In-memory implementation of the SubmodelRepository
 *
 * @author schnicke, danish, kammognie
 *
 */
public class InMemorySubmodelRepository implements SubmodelRepository<Predicate<Submodel>> {

	private static final PaginationInfo NO_LIMIT_PAGINATION_INFO = new PaginationInfo(0, null);
	private Map<String, SubmodelService<Predicate<Submodel>>> submodelServices = new LinkedHashMap<>();
	private SubmodelServiceFactory<Predicate<Submodel>> submodelServiceFactory;
	private String smRepositoryName;

	/**
	 * Creates the InMemorySubmodelRepository utilizing the passed
	 * SubmodelServiceFactory for creating new SubmodelServices
	 * 
	 * @param submodelServiceFactory
	 */
	public InMemorySubmodelRepository(SubmodelServiceFactory<Predicate<Submodel>> submodelServiceFactory) {
		this.submodelServiceFactory = submodelServiceFactory;
	}
	
	/**
	 * Creates the InMemorySubmodelRepository utilizing the passed
	 * SubmodelServiceFactory for creating new SubmodelServices
	 * 
	 * @param submodelServiceFactory 
	 * @param smRepositoryName Name of the SubmodelRepository
	 */
	public InMemorySubmodelRepository(SubmodelServiceFactory<Predicate<Submodel>> submodelServiceFactory, String smRepositoryName) {
		this(submodelServiceFactory);
		this.smRepositoryName = smRepositoryName;
	}

	/**
	 * Creates the InMemorySubmodelRepository utilizing the passed
	 * SubmodelServiceFactory for creating new SubmodelServices and preconfiguring
	 * it with the passed Submodels
	 * 
	 * @param submodelServiceFactory
	 * @param submodels
	 */
	public InMemorySubmodelRepository(SubmodelServiceFactory<Predicate<Submodel>> submodelServiceFactory, Collection<Submodel> submodels) {
		this(submodelServiceFactory);
		throwIfHasCollidingIds(submodels);

		submodelServices = createServices(submodels);
	}
	
	/**
	 * Creates the InMemorySubmodelRepository utilizing the passed
	 * SubmodelServiceFactory for creating new SubmodelServices and preconfiguring
	 * it with the passed Submodels
	 * 
	 * @param submodelServiceFactory 
	 * @param submodels 
	 * @param smRepositoryName Name of the SubmodelRepository
	 */
	public InMemorySubmodelRepository(SubmodelServiceFactory<Predicate<Submodel>> submodelServiceFactory, Collection<Submodel> submodels, String smRepositoryName) {
		this(submodelServiceFactory, submodels);
		this.smRepositoryName = smRepositoryName;
	}

	private void throwIfHasCollidingIds(Collection<Submodel> submodelsToCheck) {
		Set<String> ids = new HashSet<>();

		submodelsToCheck.stream()
				.map(submodel -> submodel.getId())
				.filter(id -> !ids.add(id))
				.findAny()
				.ifPresent(id -> {
					throw new CollidingIdentifierException(id);
				});
	}

	private Map<String, SubmodelService<Predicate<Submodel>>> createServices(Collection<Submodel> submodels) {
		Map<String, SubmodelService<Predicate<Submodel>>> map = new LinkedHashMap<>();
		submodels.forEach(submodel -> map.put(submodel.getId(), submodelServiceFactory.create(submodel)));

		return map;
	}

	@Override
	public CursorResult<List<Submodel>> getAllSubmodels(PaginationInfo pInfo, FilterInfo<Predicate<Submodel>> filterInfo) {
		Stream<Submodel> allSubmodelsStream = submodelServices.values()
				.stream()
				.map(service -> service.getSubmodel());

		if (filterInfo != null) {
			final Predicate<Submodel> filter = filterInfo.getFilter();
			allSubmodelsStream = allSubmodelsStream.filter(filter);
		}

		List<Submodel> allSubmodels = allSubmodelsStream
				.collect(Collectors.toList());

		TreeMap<String, Submodel> submodelMap = allSubmodels.stream()
				.collect(Collectors.toMap(Submodel::getId, aas -> aas, (a, b) -> a, TreeMap::new));

		PaginationSupport<Submodel> paginationSupport = new PaginationSupport<>(submodelMap, Submodel::getId);
		CursorResult<List<Submodel>> paginatedSubmodels = paginationSupport.getPaged(pInfo);
		return paginatedSubmodels;
	}

	@Override
	public Submodel getSubmodel(String id) throws ElementDoesNotExistException {
		return getSubmodelService(id).getSubmodel();
	}

	@Override
	public void updateSubmodel(String id, Submodel submodel) throws ElementDoesNotExistException {
		throwIfSubmodelDoesNotExist(id);

		throwIfMismatchingIds(id, submodel);

		submodelServices.put(id, submodelServiceFactory.create(submodel));
	}

	@Override
	public void createSubmodel(Submodel submodel) throws CollidingIdentifierException {
		throwIfSubmodelExists(submodel.getId());

		submodelServices.put(submodel.getId(), submodelServiceFactory.create(submodel));
	}

	@Override
	public CursorResult<List<SubmodelElement>> getSubmodelElements(String submodelId, PaginationInfo pInfo, FilterInfo<Predicate<Submodel>> filterInfo) {
		return getSubmodelService(submodelId).getSubmodelElements(pInfo, filterInfo);
	}

	@Override
	public SubmodelElement getSubmodelElement(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
		return getSubmodelService(submodelId).getSubmodelElement(smeIdShort);
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
		return getSubmodelService(submodelId).getSubmodelElementValue(smeIdShort);
	}

	@Override
	public void setSubmodelElementValue(String submodelId, String smeIdShort, SubmodelElementValue value) throws ElementDoesNotExistException {
		getSubmodelService(submodelId).setSubmodelElementValue(smeIdShort, value);
	}

	@Override
	public void deleteSubmodel(String submodelId) throws ElementDoesNotExistException {
		throwIfSubmodelDoesNotExist(submodelId);

		submodelServices.remove(submodelId);
	}

	@Override
	public void createSubmodelElement(String submodelId, SubmodelElement smElement) {
		throwIfSubmodelDoesNotExist(submodelId);

		submodelServices.get(submodelId)
				.createSubmodelElement(smElement);
	}

	@Override
	public void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement smElement) throws ElementDoesNotExistException {
		getSubmodelService(submodelId).createSubmodelElement(idShortPath, smElement);
	}

	@Override
	public void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
		getSubmodelService(submodelId).deleteSubmodelElement(idShortPath);
	}

	@Override
	public SubmodelValueOnly getSubmodelByIdValueOnly(String submodelId) {
		return new SubmodelValueOnly(getSubmodelElements(submodelId, NO_LIMIT_PAGINATION_INFO, null).getResult());
	}

	@Override
	public Submodel getSubmodelByIdMetadata(String submodelId) {
		Submodel submodel = getSubmodel(submodelId);
		submodel.setSubmodelElements(null);
		return submodel;
	}
	
	@Override
	public String getName() {
		return smRepositoryName == null ? SubmodelRepository.super.getName() : smRepositoryName;
	}

	@Override
	public OperationVariable[] invokeOperation(String submodelId, String idShortPath, OperationVariable[] input) throws ElementDoesNotExistException {
		return getSubmodelService(submodelId).invokeOperation(idShortPath, input);
	}


	private void throwIfMismatchingIds(String smId, Submodel newSubmodel) {
		String newSubmodelId = newSubmodel.getId();

		if (!smId.equals(newSubmodelId))
			throw new IdentificationMismatchException();
	}


	private SubmodelService<Predicate<Submodel>> getSubmodelService(String submodelId) {
		throwIfSubmodelDoesNotExist(submodelId);

		return submodelServices.get(submodelId);
	}

	private void throwIfSubmodelExists(String id) {
		if (submodelServices.containsKey(id))
			throw new CollidingIdentifierException(id);
	}

	private void throwIfSubmodelDoesNotExist(String id) {
		if (!submodelServices.containsKey(id))
			throw new ElementDoesNotExistException(id);
	}

}
