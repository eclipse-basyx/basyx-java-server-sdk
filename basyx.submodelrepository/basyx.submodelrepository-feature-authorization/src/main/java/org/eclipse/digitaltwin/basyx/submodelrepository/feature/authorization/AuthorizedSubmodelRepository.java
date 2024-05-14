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

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.authorization.rbac.Action;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacPermissionResolver;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.InsufficientPermissionException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;

/**
 * Decorator for authorized {@link SubmodelRepository}
 *
 * @author danish
 *
 */
public class AuthorizedSubmodelRepository implements SubmodelRepository {

	private static final String ALL_ALLOWED_WILDCARD = "*";
	private SubmodelRepository decorated;
	private RbacPermissionResolver<SubmodelTargetInformation> permissionResolver;

	public AuthorizedSubmodelRepository(SubmodelRepository decorated, RbacPermissionResolver<SubmodelTargetInformation> permissionResolver) {
		this.decorated = decorated;
		this.permissionResolver = permissionResolver;
	}

	@Override
	public CursorResult<List<Submodel>> getAllSubmodels(PaginationInfo pInfo) {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new SubmodelTargetInformation(getIdAsList(ALL_ALLOWED_WILDCARD), getIdAsList(ALL_ALLOWED_WILDCARD)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		return decorated.getAllSubmodels(pInfo);
	}

	@Override
	public Submodel getSubmodel(String submodelId) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new SubmodelTargetInformation(getIdAsList(submodelId), getIdAsList(ALL_ALLOWED_WILDCARD)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		return decorated.getSubmodel(submodelId);
	}

	@Override
	public void updateSubmodel(String submodelId, Submodel submodel) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.UPDATE, new SubmodelTargetInformation(getIdAsList(submodelId), getIdAsList(ALL_ALLOWED_WILDCARD)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		decorated.updateSubmodel(submodelId, submodel);
	}

	@Override
	public void createSubmodel(Submodel submodel) throws CollidingIdentifierException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.CREATE, new SubmodelTargetInformation(getIdAsList(submodel.getId()), getIdAsList(ALL_ALLOWED_WILDCARD)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		decorated.createSubmodel(submodel);
	}

	@Override
	public void deleteSubmodel(String submodelId) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.DELETE, new SubmodelTargetInformation(getIdAsList(submodelId), getIdAsList(ALL_ALLOWED_WILDCARD)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		decorated.deleteSubmodel(submodelId);
	}

	@Override
	public CursorResult<List<SubmodelElement>> getSubmodelElements(String submodelId, PaginationInfo pInfo) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new SubmodelTargetInformation(getIdAsList(submodelId), getIdAsList(ALL_ALLOWED_WILDCARD)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		return decorated.getSubmodelElements(submodelId, pInfo);
	}

	@Override
	public SubmodelElement getSubmodelElement(String submodelId, String smeIdShortPath) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new SubmodelTargetInformation(getIdAsList(submodelId), getIdAsList(smeIdShortPath)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		return decorated.getSubmodelElement(submodelId, smeIdShortPath);
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String submodelId, String smeIdShortPath) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new SubmodelTargetInformation(getIdAsList(submodelId), getIdAsList(smeIdShortPath)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		return decorated.getSubmodelElementValue(submodelId, smeIdShortPath);
	}

	@Override
	public void setSubmodelElementValue(String submodelId, String smeIdShortPath, SubmodelElementValue value) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.UPDATE, new SubmodelTargetInformation(getIdAsList(submodelId), getIdAsList(smeIdShortPath)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		decorated.setSubmodelElementValue(submodelId, smeIdShortPath, value);
	}

	@Override
	public void createSubmodelElement(String submodelId, SubmodelElement smElement) {
		boolean isAuthorized = permissionResolver.hasPermission(Action.UPDATE, new SubmodelTargetInformation(getIdAsList(submodelId), getIdAsList(ALL_ALLOWED_WILDCARD)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		decorated.createSubmodelElement(submodelId, smElement);
	}

	@Override
	public void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement smElement) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.UPDATE, new SubmodelTargetInformation(getIdAsList(submodelId), getIdAsList(idShortPath)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		decorated.createSubmodelElement(submodelId, idShortPath, smElement);
	}

	@Override
	public void updateSubmodelElement(String submodelId, String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.UPDATE, new SubmodelTargetInformation(getIdAsList(submodelId), getIdAsList(idShortPath)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		decorated.updateSubmodelElement(submodelId, idShortPath, submodelElement);
	}

	@Override
	public void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.UPDATE, new SubmodelTargetInformation(getIdAsList(submodelId), getIdAsList(idShortPath)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		decorated.deleteSubmodelElement(submodelId, idShortPath);
	}

	@Override
	public OperationVariable[] invokeOperation(String submodelId, String idShortPath, OperationVariable[] input) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.EXECUTE, new SubmodelTargetInformation(getIdAsList(submodelId), getIdAsList(idShortPath)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		return decorated.invokeOperation(submodelId, idShortPath, input);
	}

	@Override
	public SubmodelValueOnly getSubmodelByIdValueOnly(String submodelId) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new SubmodelTargetInformation(getIdAsList(submodelId), getIdAsList(ALL_ALLOWED_WILDCARD)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		return decorated.getSubmodelByIdValueOnly(submodelId);
	}

	@Override
	public Submodel getSubmodelByIdMetadata(String submodelId) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new SubmodelTargetInformation(getIdAsList(submodelId), getIdAsList(ALL_ALLOWED_WILDCARD)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		return decorated.getSubmodelByIdMetadata(submodelId);
	}

	@Override
	public File getFileByPathSubmodel(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new SubmodelTargetInformation(getIdAsList(submodelId), getIdAsList(idShortPath)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		return decorated.getFileByPathSubmodel(submodelId, idShortPath);
	}

	@Override
	public void setFileValue(String submodelId, String idShortPath, String fileName, InputStream inputStream) throws ElementDoesNotExistException, ElementNotAFileException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.UPDATE, new SubmodelTargetInformation(getIdAsList(submodelId), getIdAsList(idShortPath)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		decorated.setFileValue(submodelId, idShortPath, fileName, inputStream);
	}

	@Override
	public void deleteFileValue(String submodelId, String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.UPDATE, new SubmodelTargetInformation(getIdAsList(submodelId), getIdAsList(idShortPath)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		decorated.deleteFileValue(submodelId, idShortPath);
	}

	@Override
	public void patchSubmodelElements(String submodelId, List<SubmodelElement> submodelElementList) {
		boolean isAuthorized = permissionResolver.hasPermission(Action.UPDATE, new SubmodelTargetInformation(getIdAsList(submodelId), getIdAsList(ALL_ALLOWED_WILDCARD)));

		throwExceptionIfInsufficientPermission(isAuthorized);

		decorated.patchSubmodelElements(submodelId, submodelElementList);
	}
	
	private List<String> getIdAsList(String id) {
		return new ArrayList<>(Arrays.asList(id));
	}

	private void throwExceptionIfInsufficientPermission(boolean isAuthorized) {
		if (!isAuthorized)
			throw new InsufficientPermissionException("Insufficient Permission: The current subject does not have the required permissions for this operation.");
	}

}
