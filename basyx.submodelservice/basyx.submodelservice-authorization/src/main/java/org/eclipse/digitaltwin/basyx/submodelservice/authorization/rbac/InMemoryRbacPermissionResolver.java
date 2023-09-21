package org.eclipse.digitaltwin.basyx.submodelservice.authorization.rbac;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.authorization.*;
import org.eclipse.digitaltwin.basyx.authorization.rbac.*;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotAuthorizedException;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;
import org.eclipse.digitaltwin.basyx.submodelservice.authorization.PermissionResolver;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@ConditionalOnExpression(value = "'${basyx.submodelservice.feature.authorization.type}' == 'rbac' and '${basyx.submodelservice.backend}'.equals('inMemory')")
@Service
public class InMemoryRbacPermissionResolver implements PermissionResolver<Predicate<SubmodelElement>> {
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
    public void deleteSubmodelElement(Submodel submodel, String idShortPath) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                .setSmId(submodel.getId())
                .setSmElIdShortPath(idShortPath)
                .build();
        if (!hasPermission(targetInfo, Action.WRITE, subjectInfo)) {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public FilterInfo<Predicate<SubmodelElement>> getGetSubmodelElementsFilterInfo(Submodel submodel) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final String submodelId = submodel.getId();
        final String submodelSemanticId = IdHelper.getSubmodelSemanticIdString(submodel.getSemanticID());
        return new FilterInfo<>(submodelElement -> {
            final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                    .setSmId(submodelId)
                    .setSmSemanticId(submodelSemanticId)
                    .setSmElIdShortPath(submodelElement.getIdShort())
                    .build();
            return hasPermission(targetInfo, Action.READ, subjectInfo);
        });
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
    public FilterInfo<Predicate<SubmodelElement>> getSubmodelValueOnlyFilterInfo(Submodel submodel) {
        final ISubjectInfo<?> subjectInfo = subjectInfoProvider.get();
        final String submodelId = submodel.getId();
        final String submodelSemanticId = IdHelper.getSubmodelSemanticIdString(submodel.getSemanticID());
        return new FilterInfo<>(submodelElement -> {
            final BaSyxObjectTargetInfo targetInfo = new BaSyxObjectTargetInfo.Builder()
                    .setSmId(submodelId)
                    .setSmSemanticId(submodelSemanticId)
                    .setSmElIdShortPath(submodelElement.getIdShort())
                    .build();
            return hasPermission(targetInfo, Action.READ, subjectInfo);
        });
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
}
