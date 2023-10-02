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

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;

import java.util.List;

/**
 * Repository decorator for the authorization on the concept description level.
 * 
 * @author wege
 */

public class AuthorizationConceptDescriptionRepository<ConceptDescriptionFilterType> implements ConceptDescriptionRepository<ConceptDescriptionFilterType> {
	private ConceptDescriptionRepository<ConceptDescriptionFilterType> decorated;

	public ConceptDescriptionRepository<ConceptDescriptionFilterType> getDecorated() {
		return decorated;
	}

	private final PermissionResolver<ConceptDescriptionFilterType> permissionResolver;

	public AuthorizationConceptDescriptionRepository(ConceptDescriptionRepository<ConceptDescriptionFilterType> decorated, PermissionResolver<ConceptDescriptionFilterType> permissionResolver) {
		this.decorated = decorated;
		this.permissionResolver = permissionResolver;
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptions(PaginationInfo pInfo, FilterInfo<ConceptDescriptionFilterType> filterInfo) {
		return decorated.getAllConceptDescriptions(pInfo, permissionResolver.getGetAllConceptDescriptionsFilterInfo());
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByIdShort(String idShort, PaginationInfo pInfo, FilterInfo<ConceptDescriptionFilterType> filterInfo) {
		return decorated.getAllConceptDescriptionsByIdShort(idShort, pInfo, permissionResolver.getGetAllConceptDescriptionsByIdShortFilterInfo());
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByIsCaseOf(Reference isCaseOf, PaginationInfo pInfo, FilterInfo<ConceptDescriptionFilterType> filterInfo) {
		return decorated.getAllConceptDescriptionsByIsCaseOf(isCaseOf, pInfo, permissionResolver.getGetAllConceptDescriptionsByIsCaseOfFilterInfo());
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptionsByDataSpecificationReference(Reference dataSpecificationReference, PaginationInfo pInfo, FilterInfo<ConceptDescriptionFilterType> filterInfo) {
		return decorated.getAllConceptDescriptionsByDataSpecificationReference(dataSpecificationReference, pInfo, permissionResolver.getGetAllConceptDescriptionsByDataSpecificationReferenceFilterInfo());
	}

	@Override
	public ConceptDescription getConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException {
		permissionResolver.getConceptDescription(conceptDescriptionId);
		return decorated.getConceptDescription(conceptDescriptionId);
	}

	@Override
	public void updateConceptDescription(String conceptDescriptionId, ConceptDescription conceptDescription) throws ElementDoesNotExistException {
		permissionResolver.updateConceptDescription(conceptDescriptionId, conceptDescription);
		decorated.updateConceptDescription(conceptDescriptionId, conceptDescription);
	}

	@Override
	public void createConceptDescription(ConceptDescription conceptDescription) throws CollidingIdentifierException {
		permissionResolver.createConceptDescription(conceptDescription);
		decorated.createConceptDescription(conceptDescription);
	}

	@Override
	public void deleteConceptDescription(String conceptDescriptionId) throws ElementDoesNotExistException {
		permissionResolver.deleteConceptDescription(conceptDescriptionId);
		decorated.deleteConceptDescription(conceptDescriptionId);
	}
}
