package org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization.rbac;

import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization.PermissionResolver;
import org.eclipse.digitaltwin.basyx.authorization.*;
import org.eclipse.digitaltwin.basyx.authorization.rbac.*;
import org.eclipse.digitaltwin.basyx.authorization.rbac.CommonRbacConfig;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotAuthorizedException;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@ConditionalOnProperty(CommonAuthorizationConfig.ENABLED_PROPERTY_KEY)
@ConditionalOnExpression(value = "'${" + CommonAuthorizationConfig.TYPE_PROPERTY_KEY + "}' == '" + CommonRbacConfig.RBAC_AUTHORIZATION_TYPE + "' and '${basyx.backend}'.equals('InMemory')")
public class InMemoryRbacPermissionResolver implements PermissionResolver<Predicate<AssetAdministrationShell>, Predicate<Reference>>, RbacPermissionResolver<Predicate<RbacRule>> {
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
    public FilterInfo<Predicate<AssetAdministrationShell>> getGetAllAasFilterInfo() {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        return new FilterInfo<>(aas -> {
            final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                    .setAasId(aas.getId())
                    .build();
            return hasPermission(targetInfo, Action.READ, subjectInfo);
        });
    }

    @Override
    public void getAas(String aasId) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setAasId(aasId)
                .build();
        if (!hasPermission(targetInfo, Action.READ, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public void createAas(AssetAdministrationShell aas) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setAasId(aas.getId())
                .build();
        if (!hasPermission(targetInfo, Action.WRITE, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public void updateAas(String aasId, AssetAdministrationShell aas) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setAasId(aasId)
                .build();
        if (!hasPermission(targetInfo, Action.WRITE, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public void deleteAas(String aasId) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setAasId(aasId)
                .build();
        if (!hasPermission(targetInfo, Action.WRITE, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public FilterInfo<Predicate<Reference>> getGetSubmodelReferencesFilterInfo(String aasId) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        return new FilterInfo<>(submodelReference -> {
            final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                    .setAasId(aasId)
                    .setSmId(Optional.ofNullable(submodelReference.getKeys().get(0)).map(Key::getValue).orElse(null))
                    .setSmSemanticId(IdHelper.getSubmodelSemanticIdString(submodelReference.getReferredSemanticID()))
                    .build();
            return hasPermission(targetInfo, Action.READ, subjectInfo);
        });
    }

    @Override
    public void addSubmodelReference(String aasId, Reference submodelReference) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setAasId(aasId)
                .build();
        if (!hasPermission(targetInfo, Action.WRITE, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public void removeSubmodelReference(String aasId, String submodelId) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setAasId(aasId)
                .setSmId(submodelId)
                .build();
        if (!hasPermission(targetInfo, Action.WRITE, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public void setAssetInformation(String aasId, AssetInformation aasInfo) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setAasId(aasId)
                .build();
        if (!hasPermission(targetInfo, Action.WRITE, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public void getAssetInformation(String aasId) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setAasId(aasId)
                .build();
        if (!hasPermission(targetInfo, Action.READ, subjectInfo)) {
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
