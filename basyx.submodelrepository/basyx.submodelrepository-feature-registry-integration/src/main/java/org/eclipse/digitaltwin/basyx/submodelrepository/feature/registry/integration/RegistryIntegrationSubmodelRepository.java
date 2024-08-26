/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
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
package org.eclipse.digitaltwin.basyx.submodelrepository.feature.registry.integration;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.RepositoryRegistryLinkException;
import org.eclipse.digitaltwin.basyx.core.exceptions.RepositoryRegistryUnlinkException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.ApiException;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.api.SubmodelRegistryApi;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.factory.SubmodelDescriptorFactory;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.mapper.AttributeMapper;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decorator for linking {@link SubmodelRepository} with SubmodelRegistry
 *
 * @author danish
 *
 */
public class RegistryIntegrationSubmodelRepository implements SubmodelRepository {
	private static Logger logger = LoggerFactory.getLogger(RegistryIntegrationSubmodelRepository.class);

	private SubmodelRepository decorated;
	private SubmodelRepositoryRegistryLink submodelRepositoryRegistryLink;
	private AttributeMapper attributeMapper;

	public RegistryIntegrationSubmodelRepository(SubmodelRepository decorated, SubmodelRepositoryRegistryLink submodelRepositoryRegistryLink, AttributeMapper attributeMapper) {
		this.decorated = decorated;
		this.submodelRepositoryRegistryLink = submodelRepositoryRegistryLink;
		this.attributeMapper = attributeMapper;
	}

	@Override
	public CursorResult<List<Submodel>> getAllSubmodels(PaginationInfo paginationInfo) {
		return decorated.getAllSubmodels(paginationInfo);
	}

	@Override
	public Submodel getSubmodel(String submodelId) throws ElementDoesNotExistException {
		return decorated.getSubmodel(submodelId);
	}

	@Override
	public void updateSubmodel(String submodelId, Submodel submodel) throws ElementDoesNotExistException {
		decorated.updateSubmodel(submodelId, submodel);
	}

	@Override
	public void createSubmodel(Submodel submodel) throws CollidingIdentifierException {
		decorated.createSubmodel(submodel);

		integrateSubmodelWithRegistry(submodel, submodelRepositoryRegistryLink.getSubmodelRepositoryBaseURLs());
	}

	@Override
	public void deleteSubmodel(String submodelId) throws ElementDoesNotExistException {
		deleteFromRegistry(submodelId);
		
		decorated.deleteSubmodel(submodelId);
	}

	@Override
	public CursorResult<List<SubmodelElement>> getSubmodelElements(String submodelId, PaginationInfo paginationInfo) throws ElementDoesNotExistException {
		return decorated.getSubmodelElements(submodelId, paginationInfo);
	}

	@Override
	public SubmodelElement getSubmodelElement(String submodelId, String submodelElementIdShort) throws ElementDoesNotExistException {
		return decorated.getSubmodelElement(submodelId, submodelElementIdShort);
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String submodelId, String submodelElementIdShort) throws ElementDoesNotExistException {
		return decorated.getSubmodelElementValue(submodelId, submodelElementIdShort);
	}

	@Override
	public void setSubmodelElementValue(String submodelId, String idShortPath, SubmodelElementValue value) throws ElementDoesNotExistException {
		decorated.setSubmodelElementValue(submodelId, idShortPath, value);
	}

	@Override
	public void createSubmodelElement(String submodelId, SubmodelElement submodelElement) {
		decorated.createSubmodelElement(submodelId, submodelElement);
	}

	@Override
	public void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
		decorated.createSubmodelElement(submodelId, idShortPath, submodelElement);
	}
	
	@Override
	public void updateSubmodelElement(String submodelIdentifier, String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
		decorated.updateSubmodelElement(submodelIdentifier, idShortPath, submodelElement);
	}

	@Override
	public void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
		decorated.deleteSubmodelElement(submodelId, idShortPath);
	}

	@Override
	public OperationVariable[] invokeOperation(String submodelId, String idShortPath, OperationVariable[] input) throws ElementDoesNotExistException {
		return decorated.invokeOperation(submodelId, idShortPath, input);
	}

	@Override
	public SubmodelValueOnly getSubmodelByIdValueOnly(String submodelId) throws ElementDoesNotExistException {
		return decorated.getSubmodelByIdValueOnly(submodelId);
	}

	@Override
	public Submodel getSubmodelByIdMetadata(String submodelId) throws ElementDoesNotExistException {
		return decorated.getSubmodelByIdMetadata(submodelId);
	}

	@Override
	public File getFileByPathSubmodel(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		return decorated.getFileByPathSubmodel(submodelId, idShortPath);
	}

	@Override
	public void setFileValue(String submodelId, String idShortPath, String fileName, InputStream inputStream) throws ElementDoesNotExistException, ElementNotAFileException {
		decorated.setFileValue(submodelId, idShortPath, fileName, inputStream);
	}

	@Override
	public void deleteFileValue(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		decorated.deleteFileValue(submodelId, idShortPath);
	}

	private void integrateSubmodelWithRegistry(Submodel submodel, List<String> submodelRepositoryURLs) {
		SubmodelDescriptor descriptor = new SubmodelDescriptorFactory(submodel, submodelRepositoryURLs, attributeMapper).create();

		SubmodelRegistryApi registryApi = submodelRepositoryRegistryLink.getRegistryApi();

		try {
			registryApi.postSubmodelDescriptor(descriptor);

			logger.info("Submodel '{}' has been automatically linked with the Registry", submodel.getId());
		} catch (ApiException e) {
			throw new RepositoryRegistryLinkException(submodel.getId(), e);
		}
	}

	private void deleteFromRegistry(String submodelId) {
		SubmodelRegistryApi registryApi = submodelRepositoryRegistryLink.getRegistryApi();
		
		if (!submodelExistsOnRegistry(submodelId, registryApi)) {
			logger.error("Unable to un-link the Submodel descriptor '{}' from the Registry because it does not exist on the Registry.", submodelId);
			
			return;
		}

		try {
			registryApi.deleteSubmodelDescriptorById(submodelId);

			logger.info("Submodel '{}' has been automatically un-linked from the Registry.", submodelId);
		} catch (ApiException e) {
			throw new RepositoryRegistryUnlinkException(submodelId, e);
		}
	}
	
	private boolean submodelExistsOnRegistry(String submodelId, SubmodelRegistryApi registryApi) {
		try {
			registryApi.getSubmodelDescriptorById(submodelId);
			
			return true;
		} catch (ApiException e) {
			return false;
		}
	}

	@Override
	public void patchSubmodelElements(String submodelId, List<SubmodelElement> submodelElementList) {
		decorated.patchSubmodelElements(submodelId, submodelElementList);
	}

}
