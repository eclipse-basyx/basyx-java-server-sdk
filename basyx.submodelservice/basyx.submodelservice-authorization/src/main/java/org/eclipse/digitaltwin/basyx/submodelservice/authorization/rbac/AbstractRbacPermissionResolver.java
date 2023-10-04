/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.submodelservice.authorization.rbac;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.authorization.*;
import org.eclipse.digitaltwin.basyx.authorization.rbac.*;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotAuthorizedException;
import org.eclipse.digitaltwin.basyx.submodelservice.authorization.PermissionResolver;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class AbstractRbacPermissionResolver<SubmodelElementFilterType, RbacRuleFilterType> implements PermissionResolver<SubmodelElementFilterType>, RbacRulePermissionResolver<RbacRuleFilterType> {
    @Autowired
    protected final IRbacStorage<RbacRuleFilterType> storage;

    @Autowired
    protected final ISubjectInfoProvider subjectInfoProvider;

    @Autowired
    protected final IRoleAuthenticator roleAuthenticator;

    protected AbstractRbacPermissionResolver(IRbacStorage<RbacRuleFilterType> storage, ISubjectInfoProvider subjectInfoProvider, IRoleAuthenticator roleAuthenticator) {
        this.storage = storage;
        this.subjectInfoProvider = subjectInfoProvider;
        this.roleAuthenticator = roleAuthenticator;
    }

    protected boolean hasPermission(ITargetInfo targetInfo, Action action, ISubjectInfo<?> subjectInfo) {
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
