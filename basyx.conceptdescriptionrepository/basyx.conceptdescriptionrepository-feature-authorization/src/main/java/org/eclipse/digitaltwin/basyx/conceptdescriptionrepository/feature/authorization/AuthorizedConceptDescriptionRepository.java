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

package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.feature.authorization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.authorization.rbac.Action;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacPermissionResolver;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.InsufficientPermissionException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

/**
 * Decorator for authorized {@link ConceptDescriptionRepository}
 *
 * @author danish
 *
 */
public class AuthorizedConceptDescriptionRepository implements ConceptDescriptionRepository {

	private ConceptDescriptionRepository decorated;
	private RbacPermissionResolver<ConceptDescriptionTargetInformation> permissionResolver;
	
	public AuthorizedConceptDescriptionRepository(ConceptDescriptionRepository decorated, RbacPermissionResolver<ConceptDescriptionTargetInformation> permissionResolver) {
		this.decorated = decorated;
		this.permissionResolver = permissionResolver;
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptions(PaginationInfo pInfo) {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new ConceptDescriptionTargetInformation(getIdAsList("*")));
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		return decorated.getAllConceptDescriptions(pInfo);
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByIdShort(String idShort, PaginationInfo pInfo) {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new ConceptDescriptionTargetInformation(getIdAsList("*")));
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		return decorated.getAllConceptDescriptionsByIdShort(idShort, pInfo);
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByIsCaseOf(Reference isCaseOf, PaginationInfo pInfo) {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new ConceptDescriptionTargetInformation(getIdAsList("*")));
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		return decorated.getAllConceptDescriptionsByIsCaseOf(isCaseOf, pInfo);
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByDataSpecificationReference(Reference dataSpecificationReference, PaginationInfo pInfo) {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new ConceptDescriptionTargetInformation(getIdAsList("*")));
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		return decorated.getAllConceptDescriptionsByDataSpecificationReference(dataSpecificationReference, pInfo);
	}

	@Override
	public ConceptDescription getConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new ConceptDescriptionTargetInformation(getIdAsList(conceptDescriptionId)));
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		return decorated.getConceptDescription(conceptDescriptionId);
	}

	@Override
	public void updateConceptDescription(String conceptDescriptionId, ConceptDescription conceptDescription) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.UPDATE, new ConceptDescriptionTargetInformation(getIdAsList(conceptDescriptionId)));
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		decorated.updateConceptDescription(conceptDescriptionId, conceptDescription);
	}

	@Override
	public void createConceptDescription(ConceptDescription conceptDescription) throws CollidingIdentifierException, MissingIdentifierException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.CREATE, new ConceptDescriptionTargetInformation(getIdAsList(conceptDescription.getId())));
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		decorated.createConceptDescription(conceptDescription);
		
	}

	@Override
	public void deleteConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException {
		boolean isAuthorized = permissionResolver.hasPermission(Action.DELETE, new ConceptDescriptionTargetInformation(getIdAsList(conceptDescriptionId)));
		
		throwExceptionIfInsufficientPermission(isAuthorized);
		
		decorated.deleteConceptDescription(conceptDescriptionId);
	}

	private List<String> getIdAsList(String id) {
		return new ArrayList<>(Arrays.asList(id));
	}
	
	private void throwExceptionIfInsufficientPermission(boolean isAuthorized) {
		if (!isAuthorized)
			throw new InsufficientPermissionException("Insufficient Permission: The current subject does not have the required permissions for this operation.");
	}

}
