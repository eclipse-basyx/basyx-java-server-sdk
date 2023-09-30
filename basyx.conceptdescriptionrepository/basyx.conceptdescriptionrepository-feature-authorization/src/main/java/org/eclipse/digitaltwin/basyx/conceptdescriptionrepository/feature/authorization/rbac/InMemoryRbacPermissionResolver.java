package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.feature.authorization.rbac;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;

import org.eclipse.digitaltwin.basyx.authorization.*;
import org.eclipse.digitaltwin.basyx.authorization.rbac.*;
import org.eclipse.digitaltwin.basyx.authorization.rbac.CommonRbacConfig;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotAuthorizedException;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.feature.authorization.ConceptDescriptionTargetInfo;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.feature.authorization.PermissionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.function.Predicate;

@Service
@ConditionalOnProperty(CommonAuthorizationConfig.ENABLED_PROPERTY_KEY)
@ConditionalOnExpression(value = "'${" + CommonAuthorizationConfig.TYPE_PROPERTY_KEY + "}' == '" + CommonRbacConfig.RBAC_AUTHORIZATION_TYPE + "' and '${basyx.backend}'.equals('InMemory')")
public class InMemoryRbacPermissionResolver implements PermissionResolver<Predicate<ConceptDescription>>, RbacPermissionResolver<Predicate<RbacRule>> {
    @Autowired
    private final IRbacStorage<Predicate<RbacRule>> storage;

    @Autowired
    private final ISubjectInfoProvider subjectInfoProvider;

    @Autowired
    private final IRoleAuthenticator roleAuthenticator;

    public InMemoryRbacPermissionResolver(IRbacStorage<Predicate<RbacRule>> storage, ISubjectInfoProvider subjectInfoProvider, IRoleAuthenticator roleAuthenticator) {
        this.storage = storage;
        this.subjectInfoProvider = subjectInfoProvider;
        this.roleAuthenticator = roleAuthenticator;
    }

    private boolean hasPermission(ITargetInfo targetInfo, Action action, ISubjectInfo<?> subjectInfo) {
        final IRbacRuleChecker rbacRuleChecker = new PredefinedSetRbacRuleChecker(storage.getRbacRuleSet(null));
        final List<String> roles = roleAuthenticator.getRoles();
        return rbacRuleChecker.checkRbacRuleIsSatisfied(roles, action.toString(), targetInfo);
    }

    @Override
    public FilterInfo<Predicate<ConceptDescription>> getGetAllConceptDescriptionsFilterInfo() {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        return new FilterInfo<>(conceptDescription -> {
            final ConceptDescriptionTargetInfo targetInfo = new ConceptDescriptionTargetInfo(conceptDescription.getId());
            return hasPermission(targetInfo, Action.READ, subjectInfo);
        });
    }

    @Override
    public FilterInfo<Predicate<ConceptDescription>> getGetAllConceptDescriptionsByIdShortFilterInfo() {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        return new FilterInfo<>(conceptDescription -> {
            final ConceptDescriptionTargetInfo targetInfo = new ConceptDescriptionTargetInfo(conceptDescription.getId());
            return hasPermission(targetInfo, Action.READ, subjectInfo);
        });
    }

    @Override
    public FilterInfo<Predicate<ConceptDescription>> getGetAllConceptDescriptionsByIsCaseOfFilterInfo() {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        return new FilterInfo<>(conceptDescription -> {
            final ConceptDescriptionTargetInfo targetInfo = new ConceptDescriptionTargetInfo(conceptDescription.getId());
            return hasPermission(targetInfo, Action.READ, subjectInfo);
        });
    }

    @Override
    public FilterInfo<Predicate<ConceptDescription>> getGetAllConceptDescriptionsByDataSpecificationReferenceFilterInfo() {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        return new FilterInfo<>(conceptDescription -> {
            final ConceptDescriptionTargetInfo targetInfo = new ConceptDescriptionTargetInfo(conceptDescription.getId());
            return hasPermission(targetInfo, Action.READ, subjectInfo);
        });
    }

    @Override
    public void getConceptDescription(String conceptDescriptionId) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final ConceptDescriptionTargetInfo targetInfo = new ConceptDescriptionTargetInfo(conceptDescriptionId);
        if (!hasPermission(targetInfo, Action.READ, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public void updateConceptDescription(String conceptDescriptionId, ConceptDescription conceptDescription) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final ConceptDescriptionTargetInfo targetInfo = new ConceptDescriptionTargetInfo(conceptDescriptionId);
        if (!hasPermission(targetInfo, Action.WRITE, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public void createConceptDescription(ConceptDescription conceptDescription) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final ConceptDescriptionTargetInfo targetInfo = new ConceptDescriptionTargetInfo(conceptDescription.getId());
        if (!hasPermission(targetInfo, Action.WRITE, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public void deleteConceptDescription(String conceptDescriptionId) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final ConceptDescriptionTargetInfo targetInfo = new ConceptDescriptionTargetInfo(conceptDescriptionId);
        if (!hasPermission(targetInfo, Action.WRITE, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public FilterInfo<Predicate<RbacRule>> getGetRbacRuleSetFilterInfo() {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final RbacRuleTargetInfo targetInfo = new RbacRuleTargetInfo();
        final boolean result = hasPermission(targetInfo, Action.READ, subjectInfo);
        return new FilterInfo<>(rbacRule -> result);
    }

    @Override
    public void addRule(RbacRule rbacRule) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final RbacRuleTargetInfo targetInfo = new RbacRuleTargetInfo();
        if (!hasPermission(targetInfo, Action.WRITE, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public void removeRule(RbacRule rbacRule) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final RbacRuleTargetInfo targetInfo = new RbacRuleTargetInfo();
        if (!hasPermission(targetInfo, Action.WRITE, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }
}
