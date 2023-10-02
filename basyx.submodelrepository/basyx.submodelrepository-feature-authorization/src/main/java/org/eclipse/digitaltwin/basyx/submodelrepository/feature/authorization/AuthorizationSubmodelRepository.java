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
package org.eclipse.digitaltwin.basyx.submodelrepository.feature.authorization;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * Repository decorator for the authorization on the submodel level.
 * 
 * @author wege
 */

public class AuthorizationSubmodelRepository<SubmodelFilterType, SubmodelElementFilterType> implements SubmodelRepository<SubmodelFilterType, SubmodelElementFilterType> {
	private SubmodelRepository<SubmodelFilterType, SubmodelElementFilterType> decorated;

	public SubmodelRepository<SubmodelFilterType, SubmodelElementFilterType> getDecorated() {
		return decorated;
	}

	private final PermissionResolver<SubmodelFilterType, SubmodelElementFilterType> permissionResolver;

	public AuthorizationSubmodelRepository(SubmodelRepository<SubmodelFilterType, SubmodelElementFilterType> decorated, PermissionResolver<SubmodelFilterType, SubmodelElementFilterType> permissionResolver) {
		this.decorated = decorated;
		this.permissionResolver = permissionResolver;
	}

	@Override
	public CursorResult<List<Submodel>> getAllSubmodels(PaginationInfo pInfo, FilterInfo<SubmodelFilterType> filterInfo) {
		return decorated.getAllSubmodels(pInfo, permissionResolver.getGetAllSubmodelsFilterInfo());
	}

	@Override
	public Submodel getSubmodel(String submodelId) throws ElementDoesNotExistException {
		permissionResolver.getSubmodel(submodelId);
		return decorated.getSubmodel(submodelId);
	}

	@Override
	public void updateSubmodel(String submodelId, Submodel submodel) throws ElementDoesNotExistException {
		permissionResolver.updateSubmodel(submodelId, submodel);
		decorated.updateSubmodel(submodelId, submodel);
	}

	@Override
	public void createSubmodel(Submodel submodel) throws CollidingIdentifierException {
		permissionResolver.createSubmodel(submodel);
		decorated.createSubmodel(submodel);
	}

	@Override
	public void deleteSubmodel(String submodelId) throws ElementDoesNotExistException {
		Submodel submodel = decorated.getSubmodel(submodelId);
		permissionResolver.deleteSubmodel(submodel);
		decorated.deleteSubmodel(submodelId);
	}

	@Override
	public CursorResult<List<SubmodelElement>> getSubmodelElements(String submodelId, PaginationInfo pInfo, FilterInfo<SubmodelElementFilterType> filterInfo) throws ElementDoesNotExistException {
		return decorated.getSubmodelElements(submodelId, pInfo, permissionResolver.getGetSubmodelElementsFilterInfo(getSubmodel(submodelId)));
	}

	@Override
	public SubmodelElement getSubmodelElement(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
		permissionResolver.getSubmodelElement(submodelId, smeIdShort);
		return decorated.getSubmodelElement(submodelId, smeIdShort);
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
		permissionResolver.getSubmodelElementValue(submodelId, smeIdShort);
		return decorated.getSubmodelElementValue(submodelId, smeIdShort);
	}

	@Override
	public void setSubmodelElementValue(String submodelId, String idShortPath, SubmodelElementValue value) throws ElementDoesNotExistException {
		permissionResolver.setSubmodelElementValue(submodelId, idShortPath, value);
		decorated.setSubmodelElementValue(submodelId, idShortPath, value);
	}

	@Override
	public void createSubmodelElement(String submodelId, SubmodelElement smElement) {
		permissionResolver.createSubmodelElement(submodelId, smElement);
		decorated.createSubmodelElement(submodelId, smElement);
	}

	@Override
	public void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement smElement) throws ElementDoesNotExistException {
		permissionResolver.createSubmodelElement(submodelId, idShortPath, smElement);
		decorated.createSubmodelElement(submodelId, smElement);
	}

	@Override
	public void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
		permissionResolver.deleteSubmodelElement(submodelId, idShortPath);
		decorated.deleteSubmodelElement(submodelId, idShortPath);
	}

	@Override
	public String getName() {
		return decorated.getName();
	}

	@Override
	public OperationVariable[] invokeOperation(String submodelId, String idShortPath, OperationVariable[] input) throws ElementDoesNotExistException {
		return decorated.invokeOperation(submodelId, idShortPath, input);
	}

	@Override
	public SubmodelValueOnly getSubmodelByIdValueOnly(String submodelId) {
		permissionResolver.getSubmodelByIdValueOnly(submodelId);
		return decorated.getSubmodelByIdValueOnly(submodelId);
	}

	@Override
	public Submodel getSubmodelByIdMetadata(String submodelId) {
		permissionResolver.getSubmodelByIdMetadata(submodelId);
		return decorated.getSubmodelByIdMetadata(submodelId);
	}

}
