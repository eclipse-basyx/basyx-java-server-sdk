package org.eclipse.digitaltwin.basyx.submodelregistry.service.authorization;

import org.eclipse.digitaltwin.basyx.authorization.Action;
import org.eclipse.digitaltwin.basyx.authorization.ISubjectInfo;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;

public interface PermissionResolver<FilterType> {
    public FilterInfo<FilterType> getGetAllSubmodelDescriptorsFilterInfo();

    public void getSubmodelDescriptorById(String submodelIdentifier);

    public void putSubmodelDescriptorById(String submodelIdentifier);

    public void postSubmodelDescriptor(String submodelIdentifier);

    public void deleteSubmodelDescriptorById(String submodelIdentifier);

    public FilterInfo<FilterType> getDeleteAllSubmodelDescriptorsFilterInfo();
}
