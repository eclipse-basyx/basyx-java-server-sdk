package org.eclipse.digitaltwin.basyx.conceptdescription.feature.authorization;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;

public interface PermissionResolver<ConceptDescriptionFilterType> {
    public FilterInfo<ConceptDescriptionFilterType> getGetAllConceptDescriptionsFilterInfo();

    public FilterInfo<ConceptDescriptionFilterType> getGetAllConceptDescriptionsByIdShortFilterInfo();

    public FilterInfo<ConceptDescriptionFilterType> getGetAllConceptDescriptionsByIsCaseOfFilterInfo();

    public FilterInfo<ConceptDescriptionFilterType> getGetAllConceptDescriptionsByDataSpecificationReferenceFilterInfo();

    public void getConceptDescription(String conceptDescriptionId);

    public void updateConceptDescription(String conceptDescriptionId, ConceptDescription conceptDescription);

    public void createConceptDescription(ConceptDescription conceptDescription);

    public void deleteConceptDescription(String conceptDescriptionId);
}
