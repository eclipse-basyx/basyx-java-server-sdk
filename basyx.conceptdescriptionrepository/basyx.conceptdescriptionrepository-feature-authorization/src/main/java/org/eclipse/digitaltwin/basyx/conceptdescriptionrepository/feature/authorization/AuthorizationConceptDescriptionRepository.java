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
