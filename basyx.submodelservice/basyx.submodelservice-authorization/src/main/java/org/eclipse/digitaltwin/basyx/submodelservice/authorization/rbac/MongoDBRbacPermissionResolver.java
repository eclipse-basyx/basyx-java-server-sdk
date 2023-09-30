package org.eclipse.digitaltwin.basyx.submodelservice.authorization.rbac;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.authorization.*;
import org.eclipse.digitaltwin.basyx.authorization.rbac.*;
import org.eclipse.digitaltwin.basyx.authorization.rbac.CommonRbacConfig;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotAuthorizedException;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;
import org.eclipse.digitaltwin.basyx.submodelservice.authorization.PermissionResolver;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(CommonAuthorizationConfig.ENABLED_PROPERTY_KEY)
@ConditionalOnExpression(value = "'${" + CommonAuthorizationConfig.TYPE_PROPERTY_KEY + "}' == '" + CommonRbacConfig.RBAC_AUTHORIZATION_TYPE + "' and '${basyx.submodelservice.backend}'.equals('mongodb')")
public class MongoDBRbacPermissionResolver implements PermissionResolver<Criteria> {
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
    public void deleteSubmodelElement(Submodel submodel, String idShortPath) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final ITargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setSmId(submodel.getId())
                .setSmSemanticId(IdHelper.getSubmodelSemanticIdString(submodel.getSemanticID()))
                .setSmElIdShortPath(idShortPath)
                .build();
        if (!hasPermission(targetInfo, Action.WRITE, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public FilterInfo<Criteria> getGetSubmodelElementsFilterInfo(Submodel submodel) {
        final RbacRuleSet rbacRuleSet = storage.getRbacRuleSet(null);
        final Set<RbacRule> rbacRules = rbacRuleSet.getRules();
        final List<String> roles = roleAuthenticator.getRoles();

        final Set<String> relevantSubmodelIds = rbacRules.stream()
                .filter(rbacRule -> rbacRule.getTargetInfo() instanceof BaSyxObjectTargetInfo)
                .filter(rbacRule -> rbacRule.getAction().equals(Action.READ.toString()))
                .filter(rbacRule -> roles.contains(rbacRule.getRole()))
                .map(rbacRule -> (BaSyxObjectTargetInfo) rbacRule.getTargetInfo())
                .map(BaSyxObjectTargetInfo::getSmId)
                .collect(Collectors.toSet());

        final Set<String> relevantSubmodelSemanticIds = rbacRules.stream()
                .filter(rbacRule -> rbacRule.getTargetInfo() instanceof BaSyxObjectTargetInfo)
                .filter(rbacRule -> rbacRule.getAction().equals(Action.READ.toString()))
                .filter(rbacRule -> roles.contains(rbacRule.getRole()))
                .map(rbacRule -> (BaSyxObjectTargetInfo) rbacRule.getTargetInfo())
                .map(BaSyxObjectTargetInfo::getSmSemanticId)
                .collect(Collectors.toSet());

        return new FilterInfo<>(new Criteria().andOperator(
                Criteria.where("_id").in(relevantSubmodelIds),
                Criteria.where("_semanticId").in(relevantSubmodelSemanticIds)
        ));
    }

    @Override
    public void getSubmodelElement(Submodel submodel, String idShortPath) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final ITargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setSmId(submodel.getId())
                .setSmSemanticId(IdHelper.getSubmodelSemanticIdString(submodel.getSemanticID()))
                .setSmElIdShortPath(idShortPath)
                .build();
        if (!hasPermission(targetInfo, Action.READ, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public void getSubmodelMetaData(Submodel submodel) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final ITargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setSmId(submodel.getId())
                .setSmSemanticId(IdHelper.getSubmodelSemanticIdString(submodel.getSemanticID()))
                .build();
        if (!hasPermission(targetInfo, Action.READ, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public void getSubmodel() {

    }

    @Override
    public void getSubmodelElementValue(Submodel submodel, String idShortPath) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final ITargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setSmId(submodel.getId())
                .setSmSemanticId(IdHelper.getSubmodelSemanticIdString(submodel.getSemanticID()))
                .setSmElIdShortPath(idShortPath)
                .build();
        if (!hasPermission(targetInfo, Action.READ, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public FilterInfo<Criteria> getSubmodelValueOnlyFilterInfo(Submodel submodel) {
        final RbacRuleSet rbacRuleSet = storage.getRbacRuleSet(null);
        final Set<RbacRule> rbacRules = rbacRuleSet.getRules();
        final List<String> roles = roleAuthenticator.getRoles();

        final Set<String> relevantSubmodelIds = rbacRules.stream()
                .filter(rbacRule -> rbacRule.getTargetInfo() instanceof BaSyxObjectTargetInfo)
                .filter(rbacRule -> rbacRule.getAction().equals(Action.READ.toString()))
                .filter(rbacRule -> roles.contains(rbacRule.getRole()))
                .map(rbacRule -> (BaSyxObjectTargetInfo) rbacRule.getTargetInfo())
                .map(BaSyxObjectTargetInfo::getSmId)
                .collect(Collectors.toSet());

        final Set<String> relevantSubmodelSemanticIds = rbacRules.stream()
                .filter(rbacRule -> rbacRule.getTargetInfo() instanceof BaSyxObjectTargetInfo)
                .filter(rbacRule -> rbacRule.getAction().equals(Action.READ.toString()))
                .filter(rbacRule -> roles.contains(rbacRule.getRole()))
                .map(rbacRule -> (BaSyxObjectTargetInfo) rbacRule.getTargetInfo())
                .map(BaSyxObjectTargetInfo::getSmSemanticId)
                .collect(Collectors.toSet());

        return new FilterInfo<>(new Criteria().andOperator(
                Criteria.where("_id").in(relevantSubmodelIds),
                Criteria.where("_semanticId").in(relevantSubmodelSemanticIds)
        ));
    }

    @Override
    public void setSubmodelElementValue(Submodel submodel, String idShortPath, SubmodelElementValue body) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final ITargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setSmId(submodel.getId())
                .setSmSemanticId(IdHelper.getSubmodelSemanticIdString(submodel.getSemanticID()))
                .setSmElIdShortPath(idShortPath)
                .build();
        if (!hasPermission(targetInfo, Action.WRITE, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public void createSubmodelElement(Submodel submodel, SubmodelElement body) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final ITargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setSmId(submodel.getId())
                .setSmSemanticId(IdHelper.getSubmodelSemanticIdString(submodel.getSemanticID()))
                .setSmElIdShortPath(body.getIdShort())
                .build();
        if (!hasPermission(targetInfo, Action.WRITE, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public void createSubmodelElement(Submodel submodel, String idShortPath, SubmodelElement body) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final ITargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setSmId(submodel.getId())
                .setSmSemanticId(IdHelper.getSubmodelSemanticIdString(submodel.getSemanticID()))
                .setSmElIdShortPath(idShortPath)
                .build();
        if (!hasPermission(targetInfo, Action.WRITE, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }
}
