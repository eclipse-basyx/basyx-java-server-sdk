package org.eclipse.digitaltwin.basyx.aasregistry.service.authorization.rbac;

import org.eclipse.digitaltwin.basyx.aasregistry.service.authorization.IdHelper;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.authorization.*;
import org.eclipse.digitaltwin.basyx.authorization.rbac.*;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotAuthorizedException;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;
import org.eclipse.digitaltwin.basyx.aasregistry.service.authorization.PermissionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ConditionalOnExpression(value = "'${basyx.aasregistry.feature.authorization.type}' == 'rbac' and '${registry.type}'.equals('mongodb')")
@Service
public class MongoDBRbacPermissionResolver implements PermissionResolver<Criteria, Criteria>, RbacPermissionResolver<Criteria> {
    @Autowired
    private final IRbacStorage<Criteria> storage;

    @Autowired
    private final ISubjectInfoProvider subjectInfoProvider;

    @Autowired
    private final IRoleAuthenticator roleAuthenticator;

    public MongoDBRbacPermissionResolver(IRbacStorage<Criteria> storage, ISubjectInfoProvider subjectInfoProvider, IRoleAuthenticator roleAuthenticator) {
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
    public FilterInfo<Criteria> getGetAllAssetAdministrationShellDescriptorsFilterInfo() {
        final RbacRuleSet rbacRuleSet = storage.getRbacRuleSet(null);
        final Set<RbacRule> rbacRules = rbacRuleSet.getRules();
        final List<String> roles = roleAuthenticator.getRoles();

        final Set<String> relevantSubmodelIds = rbacRules.stream()
                .filter(rbacRule -> rbacRule.getTargetInformation() instanceof BaSyxObjectTargetInfo)
                .filter(rbacRule -> rbacRule.getAction().equals(Action.READ.toString()))
                .filter(rbacRule -> roles.contains(rbacRule.getRole()))
                .map(rbacRule -> (BaSyxObjectTargetInfo) rbacRule.getTargetInformation())
                .map(BaSyxObjectTargetInfo::getSmId)
                .collect(Collectors.toSet());

        final Set<String> relevantSubmodelSemanticIds = rbacRules.stream()
                .filter(rbacRule -> rbacRule.getTargetInformation() instanceof BaSyxObjectTargetInfo)
                .filter(rbacRule -> rbacRule.getAction().equals(Action.READ.toString()))
                .filter(rbacRule -> roles.contains(rbacRule.getRole()))
                .map(rbacRule -> (BaSyxObjectTargetInfo) rbacRule.getTargetInformation())
                .map(BaSyxObjectTargetInfo::getSmSemanticId)
                .collect(Collectors.toSet());

        return new FilterInfo<>(new Criteria().andOperator(
                Criteria.where("_id").in(relevantSubmodelIds),
                Criteria.where("_semanticId").in(relevantSubmodelSemanticIds)
        ));
    }

    @Override
    public FilterInfo<Criteria> getGetAllSubmodelDescriptorsThroughSuperpathFilterInfo() {
        final RbacRuleSet rbacRuleSet = storage.getRbacRuleSet(null);
        final Set<RbacRule> rbacRules = rbacRuleSet.getRules();
        final List<String> roles = roleAuthenticator.getRoles();

        final Set<String> relevantSubmodelIds = rbacRules.stream()
                .filter(rbacRule -> rbacRule.getTargetInformation() instanceof BaSyxObjectTargetInfo)
                .filter(rbacRule -> rbacRule.getAction().equals(Action.READ.toString()))
                .filter(rbacRule -> roles.contains(rbacRule.getRole()))
                .map(rbacRule -> (BaSyxObjectTargetInfo) rbacRule.getTargetInformation())
                .map(BaSyxObjectTargetInfo::getSmId)
                .collect(Collectors.toSet());

        final Set<String> relevantSubmodelSemanticIds = rbacRules.stream()
                .filter(rbacRule -> rbacRule.getTargetInformation() instanceof BaSyxObjectTargetInfo)
                .filter(rbacRule -> rbacRule.getAction().equals(Action.READ.toString()))
                .filter(rbacRule -> roles.contains(rbacRule.getRole()))
                .map(rbacRule -> (BaSyxObjectTargetInfo) rbacRule.getTargetInformation())
                .map(BaSyxObjectTargetInfo::getSmSemanticId)
                .collect(Collectors.toSet());

        return new FilterInfo<>(new Criteria().andOperator(
                Criteria.where("_id").in(relevantSubmodelIds),
                Criteria.where("_semanticId").in(relevantSubmodelSemanticIds)
        ));
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
                .setSmId(submodelIdentifier)
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
        if (!hasPermission(targetInfo, Action.WRITE, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public void putAssetAdministrationShellDescriptorById(String aasIdentifier, AssetAdministrationShellDescriptor body) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setAasId(aasIdentifier)
                .build();
        if (!hasPermission(targetInfo, Action.WRITE, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public void postAssetAdministrationShellDescriptor(AssetAdministrationShellDescriptor body) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setAasId(body.getId())
                .build();
        if (!hasPermission(targetInfo, Action.WRITE, subjectInfo)) {
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
    public FilterInfo<Criteria> getDeleteAllShellDescriptorsFilterInfo() {
        final RbacRuleSet rbacRuleSet = storage.getRbacRuleSet(null);
        final Set<RbacRule> rbacRules = rbacRuleSet.getRules();
        final List<String> roles = roleAuthenticator.getRoles();

        final Set<String> relevantSubmodelIds = rbacRules.stream()
                .filter(rbacRule -> rbacRule.getTargetInformation() instanceof BaSyxObjectTargetInfo)
                .filter(rbacRule -> rbacRule.getAction().equals(Action.READ.toString()))
                .filter(rbacRule -> roles.contains(rbacRule.getRole()))
                .map(rbacRule -> (BaSyxObjectTargetInfo) rbacRule.getTargetInformation())
                .map(BaSyxObjectTargetInfo::getSmId)
                .collect(Collectors.toSet());

        final Set<String> relevantSubmodelSemanticIds = rbacRules.stream()
                .filter(rbacRule -> rbacRule.getTargetInformation() instanceof BaSyxObjectTargetInfo)
                .filter(rbacRule -> rbacRule.getAction().equals(Action.READ.toString()))
                .filter(rbacRule -> roles.contains(rbacRule.getRole()))
                .map(rbacRule -> (BaSyxObjectTargetInfo) rbacRule.getTargetInformation())
                .map(BaSyxObjectTargetInfo::getSmSemanticId)
                .collect(Collectors.toSet());

        return new FilterInfo<>(new Criteria().andOperator(
                Criteria.where("_id").in(relevantSubmodelIds),
                Criteria.where("_semanticId").in(relevantSubmodelSemanticIds)
        ));
    }

    @Override
    public FilterInfo<Criteria> getGetRbacRuleSetFilterInfo() {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final RbacRuleTargetInfo targetInfo = new RbacRuleTargetInfo();
        if (!hasPermission(targetInfo, Action.READ, subjectInfo)) {
            return new FilterInfo<>(Criteria.where("true").is("false"));
        }
        return null;
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
