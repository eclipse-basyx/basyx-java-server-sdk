package org.eclipse.digitaltwin.basyx.aasregistry.service.authorization.rbac;

import org.eclipse.digitaltwin.basyx.aasregistry.service.authorization.IdHelper;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.service.authorization.PermissionResolver;
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
import java.util.function.Predicate;

@Service
@ConditionalOnProperty(CommonAuthorizationConfig.ENABLED_PROPERTY_KEY)
@ConditionalOnExpression(value = "'${" + CommonAuthorizationConfig.TYPE_PROPERTY_KEY + "}' == '" + CommonRbacConfig.RBAC_AUTHORIZATION_TYPE + "' and '${registry.type}'.equals('inMemory')")
public class InMemoryRbacPermissionResolver implements PermissionResolver<Predicate<AssetAdministrationShellDescriptor>, Predicate<SubmodelDescriptor>>, RbacPermissionResolver<Predicate<RbacRule>> {
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
    public void deleteAssetAdministrationShellDescriptorById(String aasIdentifier) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setAasId(aasIdentifier)
                .build();
        if (!hasPermission(targetInfo, Action.WRITE, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public void deleteSubmodelDescriptorByIdThroughSuperpath(String aasIdentifier, String submodelIdentifier) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setAasId(aasIdentifier)
                .setSmId(submodelIdentifier)
                .build();
        if (!hasPermission(targetInfo, Action.WRITE, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public FilterInfo<Predicate<AssetAdministrationShellDescriptor>> getGetAllAssetAdministrationShellDescriptorsFilterInfo() {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        return new FilterInfo<>(aasDescriptor -> {
            final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                    .setAasId(aasDescriptor.getId())
                    .build();
            return hasPermission(targetInfo, Action.READ, subjectInfo);
        });
    }

    @Override
    public FilterInfo<Predicate<SubmodelDescriptor>> getGetAllSubmodelDescriptorsThroughSuperpathFilterInfo() {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        return new FilterInfo<>(submodelDescriptor -> {
            final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                    .setSmId(submodelDescriptor.getId())
                    .setSmSemanticId(IdHelper.getSubmodelDescriptorSemanticIdString(submodelDescriptor.getSemanticId()))
                    .build();
            return hasPermission(targetInfo, Action.READ, subjectInfo);
        });
    }

    @Override
    public void getAssetAdministrationShellDescriptorById(String aasIdentifier) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setAasId(aasIdentifier)
                .build();
        if (!hasPermission(targetInfo, Action.READ, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public void getSubmodelDescriptorByIdThroughSuperpath(String aasIdentifier, String submodelIdentifier) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setAasId(aasIdentifier)
                .build();
        if (!hasPermission(targetInfo, Action.READ, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public void postSubmodelDescriptorThroughSuperpath(String aasIdentifier, SubmodelDescriptor body) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setAasId(aasIdentifier)
                .setSmId(body.getId())
                .setSmSemanticId(IdHelper.getSubmodelDescriptorSemanticIdString(body.getSemanticId()))
                .build();
        if (!hasPermission(targetInfo, Action.READ, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public void putAssetAdministrationShellDescriptorById(String aasIdentifier, AssetAdministrationShellDescriptor body) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setAasId(aasIdentifier)
                .build();
        if (!hasPermission(targetInfo, Action.READ, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public void postAssetAdministrationShellDescriptor(AssetAdministrationShellDescriptor body) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setAasId(body.getId())
                .build();
        if (!hasPermission(targetInfo, Action.READ, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public void putSubmodelDescriptorByIdThroughSuperpath(String aasIdentifier, String submodelIdentifier, SubmodelDescriptor descriptor) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setAasId(aasIdentifier)
                .setSmId(submodelIdentifier)
                .setSmSemanticId(IdHelper.getSubmodelDescriptorSemanticIdString(descriptor.getSemanticId()))
                .build();
        if (!hasPermission(targetInfo, Action.WRITE, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public FilterInfo<Predicate<AssetAdministrationShellDescriptor>> getDeleteAllShellDescriptorsFilterInfo() {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        return new FilterInfo<>(aasDescriptor -> {
            final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                    .setAasId(aasDescriptor.getId())
                    .build();
            return hasPermission(targetInfo, Action.READ, subjectInfo);
        });
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
