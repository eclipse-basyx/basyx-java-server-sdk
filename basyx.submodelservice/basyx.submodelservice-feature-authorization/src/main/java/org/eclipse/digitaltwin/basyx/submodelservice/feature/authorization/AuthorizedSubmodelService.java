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

package org.eclipse.digitaltwin.basyx.submodelservice.feature.authorization;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.basyx.authorization.rbac.Action;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacPermissionResolver;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementNotAFileException;
import org.eclipse.digitaltwin.basyx.core.exceptions.FileDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.InsufficientPermissionException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;

/**
 * Decorator for authorized SubmodelService
 *
 * @author Gerhard Sonnenberg ( DFKI GmbH )
 *
 */
public class AuthorizedSubmodelService implements SubmodelService {

	private static final String ALL_ALLOWED_WILDCARD = "*";
	
	private final SubmodelService decorated;
	private final RbacPermissionResolver<SubmodelTargetInformation> permissionResolver;
	private final String smId;

	public AuthorizedSubmodelService(SubmodelService decorated, RbacPermissionResolver<SubmodelTargetInformation> permissionResolver, String smId) {
		this.decorated = decorated;
		this.permissionResolver = permissionResolver;
		this.smId = smId;
	}

	@Override
	public Submodel getSubmodel() {
		throwExceptionIfInsufficientSubmodelAccess();
		return decorated.getSubmodel();
	}

	@Override
	public InputStream getFileByFilePath(String filePath) {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new SubmodelTargetInformation(List.of(smId), List.of(filePath)));
		throwExceptionIfInsufficientPermission(isAuthorized);
		return decorated.getFileByFilePath(filePath);
	}

	@Override
	public File getFileByPath(String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new SubmodelTargetInformation(List.of(smId), List.of(idShortPath)));
		throwExceptionIfInsufficientPermission(isAuthorized);
		return decorated.getFileByPath(idShortPath);
	}

	@Override
	public SubmodelElement getSubmodelElement(String idShortPath) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new SubmodelTargetInformation(List.of(smId), List.of(idShortPath)));
		throwExceptionIfInsufficientPermission(isAuthorized);
		return decorated.getSubmodelElement(idShortPath);
	}

	@Override
	public CursorResult<List<SubmodelElement>> getSubmodelElements(PaginationInfo pInfo) {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new SubmodelTargetInformation(List.of(smId), List.of(ALL_ALLOWED_WILDCARD)));
		if (isAuthorized) {
			return decorated.getSubmodelElements(pInfo);
		}
		SubmodelTargetInformation targetInfo = new SubmodelTargetInformation(List.of(smId), List.of());
		isAuthorized = permissionResolver.hasPermission(Action.READ, targetInfo);
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		return new FilteringBasyxResourceFetcher<>(decorated::getSubmodelElements, this::filter).fetch(pInfo);
	}
	
	@Override
	public SubmodelElementValue getSubmodelElementValue(String idShortPath) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new SubmodelTargetInformation(List.of(smId), List.of(idShortPath)));
		throwExceptionIfInsufficientPermission(isAuthorized);
		return decorated.getSubmodelElementValue(idShortPath);
	}

	@Override
	public void createSubmodelElement(String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
		SubmodelElement parent = decorated.getSubmodelElement(idShortPath);
		String path;
		if (parent instanceof SubmodelElementList smeList) {
			path = idShortPath + "[" + smeList.getValue().size() + "]";
		} else {
			path = idShortPath + "." + submodelElement.getIdShort();
		}
		boolean isAuthorized = permissionResolver.hasPermission(Action.CREATE, new SubmodelTargetInformation(List.of(smId), List.of(path)));
		throwExceptionIfInsufficientPermission(isAuthorized);
		decorated.createSubmodelElement(idShortPath, submodelElement);
	}

	@Override
	public void createSubmodelElement(SubmodelElement submodelElement) {
		String idShort = submodelElement.getIdShort();
		boolean isAuthorized = permissionResolver.hasPermission(Action.CREATE, new SubmodelTargetInformation(List.of(smId), List.of(idShort)));
		throwExceptionIfInsufficientPermission(isAuthorized);
		decorated.createSubmodelElement(submodelElement);
	}

	@Override
	public void deleteFileValue(String idShortPath) throws ElementDoesNotExistException, ElementNotAFileException, FileDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.UPDATE, new SubmodelTargetInformation(List.of(smId), List.of(idShortPath)));
		throwExceptionIfInsufficientPermission(isAuthorized);
		decorated.deleteFileValue(idShortPath);
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.DELETE, new SubmodelTargetInformation(List.of(smId), List.of(idShortPath)));
		throwExceptionIfInsufficientPermission(isAuthorized);
		decorated.deleteSubmodelElement(idShortPath);
	}
	
	@Override
	public void setSubmodelElementValue(String idShortPath, SubmodelElementValue value) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.UPDATE, new SubmodelTargetInformation(List.of(smId), List.of(idShortPath)));
		throwExceptionIfInsufficientPermission(isAuthorized);
		decorated.setSubmodelElementValue(idShortPath, value);
	}
	
	@Override
	public OperationVariable[] invokeOperation(String idShortPath, OperationVariable[] input) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.EXECUTE, new SubmodelTargetInformation(List.of(smId), List.of(idShortPath)));
		throwExceptionIfInsufficientPermission(isAuthorized);
		return decorated.invokeOperation(idShortPath, input);
	}
	
	@Override
	public void patchSubmodelElements(List<SubmodelElement> submodelElementList) {
		List<String> ids = submodelElementList.stream().map(SubmodelElement::getIdShort).collect(Collectors.toList());
		boolean isAuthorized = permissionResolver.hasPermission(Action.UPDATE, new SubmodelTargetInformation(List.of(smId), ids));
		throwExceptionIfInsufficientPermission(isAuthorized);
		decorated.patchSubmodelElements(submodelElementList);
	}
	
	@Override
	public void updateSubmodelElement(String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.UPDATE, new SubmodelTargetInformation(List.of(smId), List.of(idShortPath)));
		throwExceptionIfInsufficientPermission(isAuthorized);
		decorated.updateSubmodelElement(idShortPath, submodelElement);
	}
	
	@Override
	public void setFileValue(String idShortPath, String fileName, String contentType, InputStream inputStream) throws ElementDoesNotExistException, ElementNotAFileException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.UPDATE, new SubmodelTargetInformation(List.of(smId), List.of(idShortPath)));
		throwExceptionIfInsufficientPermission(isAuthorized);
		decorated.setFileValue(idShortPath, fileName, contentType, inputStream);
	}

	private void throwExceptionIfInsufficientSubmodelAccess() {
		SubmodelTargetInformation targetInfo = new SubmodelTargetInformation(List.of(smId), List.of(ALL_ALLOWED_WILDCARD));
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, targetInfo);
		throwExceptionIfInsufficientPermission(isAuthorized);
	}

	private void throwExceptionIfInsufficientPermission(boolean isAuthorized) {
		if (!isAuthorized)
			throw new InsufficientPermissionException("Insufficient Permission: The current subject does not have the required permissions for this operation.");
	}
	
	private CursorResult<List<SubmodelElement>> filter(CursorResult<List<SubmodelElement>> input) {
		List<SubmodelElement> fetched = input.getResult();
		List<SubmodelElement> toReturn = new ArrayList<>();
		for (SubmodelElement eachElem : fetched) {
			boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new SubmodelTargetInformation(List.of(smId), List.of(eachElem.getIdShort())));
			if (isAuthorized) {
				toReturn.add(eachElem);
			}
		}
		return new CursorResult<List<SubmodelElement>>(input.getCursor(), toReturn);
	}


}
