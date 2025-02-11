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
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.authorization.rbac.Action;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacPermissionResolver;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformation;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.exceptions.InsufficientPermissionException;
import org.eclipse.digitaltwin.basyx.core.exceptions.MissingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decorator for authorized {@link ConceptDescriptionRepository}
 *
 * @author danish
 *
 */
public class AuthorizedConceptDescriptionRepository implements ConceptDescriptionRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizedConceptDescriptionRepository.class);
	private ConceptDescriptionRepository decorated;
	private RbacPermissionResolver<ConceptDescriptionTargetInformation> permissionResolver;
	
	public AuthorizedConceptDescriptionRepository(ConceptDescriptionRepository decorated, RbacPermissionResolver<ConceptDescriptionTargetInformation> permissionResolver) {
		this.decorated = decorated;
		this.permissionResolver = permissionResolver;
	}

	@Override
	public CursorResult<List<ConceptDescription>> getAllConceptDescriptions(PaginationInfo pInfo) {
		boolean isAuthorized = permissionResolver.hasPermission(Action.READ, new ConceptDescriptionTargetInformation(getIdAsList("*")));
		
		if (isAuthorized)
			return decorated.getAllConceptDescriptions(pInfo);
		
		List<TargetInformation> targetInformations = permissionResolver.getMatchingTargetInformationInRules(Action.READ, new ConceptDescriptionTargetInformation(getIdAsList("*")));
		
		List<String> allIds = targetInformations.stream().map(ConceptDescriptionTargetInformation.class::cast)
				.map(ConceptDescriptionTargetInformation::getConceptDescriptionIds).flatMap(List::stream).collect(Collectors.toList());
		
		List<ConceptDescription> conceptDesc = allIds.stream().map(id -> {
			try {
				return getConceptDescription(id);
			} catch (ElementDoesNotExistException e) {
				LOGGER.error("Concept Description: '{}' not found, Error: {}", id, e.getMessage());
				return null;
			} catch (Exception e) {
				LOGGER.error("Exception occurred while retrieving the Concept Description: {}, Error: {}", id, e.getMessage());
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());
		
		TreeMap<String, ConceptDescription> aasMap = conceptDesc.stream().collect(Collectors.toMap(ConceptDescription::getId, aas -> aas, (a, b) -> a, TreeMap::new));

		PaginationSupport<ConceptDescription> paginationSupport = new PaginationSupport<>(aasMap, ConceptDescription::getId);

		return paginationSupport.getPaged(pInfo);
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
