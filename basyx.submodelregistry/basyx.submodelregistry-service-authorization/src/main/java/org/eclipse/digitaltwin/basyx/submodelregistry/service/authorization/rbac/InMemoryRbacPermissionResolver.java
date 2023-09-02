package org.eclipse.digitaltwin.basyx.submodelregistry.service.authorization.rbac;

import org.eclipse.digitaltwin.basyx.authorization.Action;
import org.eclipse.digitaltwin.basyx.authorization.ISubjectInfo;
import org.eclipse.digitaltwin.basyx.authorization.ISubjectInfoProvider;
import org.eclipse.digitaltwin.basyx.authorization.rbac.*;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.authorization.PermissionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.function.Predicate;

@Primary
@ConditionalOnExpression(value = "'${basyx.submodelregistry.feature.authorization.type}' == 'rbac' and '${registry.type}'.equals('inMemory')")
@Service
public class InMemoryRbacPermissionResolver implements PermissionResolver<Predicate<SubmodelDescriptor>> {
    @Autowired
    private final Environment environment;

    @Autowired
    private final IRbacStorage storage;

    @Autowired
    private final ISubjectInfoProvider subjectInfoProvider;

    public InMemoryRbacPermissionResolver(Environment environment, IRbacStorage storage, ISubjectInfoProvider subjectInfoProvider) {
        this.environment = environment;
        this.storage = storage;
        this.subjectInfoProvider = subjectInfoProvider;
    }

    @Override
    public boolean hasPermission(SubmodelDescriptor submodelDescriptor, Action action, ISubjectInfo<?> subjectInfo) {
        final IRbacRuleChecker rbacRuleChecker = new PredefinedSetRbacRuleChecker(storage.getRbacRuleSet());
        final ITargetInformation targetInformation = new BaSyxObjectTargetInformation(null, submodelDescriptor.getId(), null);
        return rbacRuleChecker.checkRbacRuleIsSatisfied(Arrays.asList("admin"), action.toString(), targetInformation);
    }

    @Override
    public FilterInfo<Predicate<SubmodelDescriptor>> getGetAllSubmodelDescriptorsFilterInfo() {
        ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        return new FilterInfo<>(submodelDescriptor -> hasPermission(submodelDescriptor, Action.READ, subjectInfo));
    }
}
