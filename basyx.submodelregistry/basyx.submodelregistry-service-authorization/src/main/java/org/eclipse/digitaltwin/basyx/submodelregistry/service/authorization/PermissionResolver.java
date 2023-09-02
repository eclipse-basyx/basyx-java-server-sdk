package org.eclipse.digitaltwin.basyx.submodelregistry.service.authorization;

import org.eclipse.digitaltwin.basyx.authorization.Action;
import org.eclipse.digitaltwin.basyx.authorization.ISubjectInfo;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;

public interface PermissionResolver<FilterType> {
    public boolean hasPermission(SubmodelDescriptor submodelDescriptor, Action action, ISubjectInfo<?> subjectInfo);

    public FilterInfo<FilterType> getGetAllSubmodelDescriptorsFilterInfo();
}
