package org.eclipse.digitaltwin.basyx.submodelregistry.service.authorization;

import org.eclipse.digitaltwin.basyx.authorization.Action;
import org.eclipse.digitaltwin.basyx.authorization.ISubjectInfo;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class InMemoryLocalPermissionResolver implements PermissionResolver<Predicate<SubmodelDescriptor>> {
    private final String SECURITY_SUBMODEL_ID = "SECURITY";

    @Autowired
    private final Environment environment;

    public InMemoryLocalPermissionResolver(Environment environment) {
        this.environment = environment;
    }

    @Override
    public boolean hasPermission(SubmodelDescriptor submodelDescriptor, Action action, ISubjectInfo<?> subjectInfo) {
        return true;
    }

    @Override
    public FilterInfo<Predicate<SubmodelDescriptor>> getGetAllSubmodelDescriptorsFilterInfo() {
        throw new UnsupportedOperationException("NYI"); // TODO: implement
    }
}
