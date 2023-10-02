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
package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.feature.authorization.rbac;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.basyx.authorization.*;
import org.eclipse.digitaltwin.basyx.authorization.rbac.*;
import org.eclipse.digitaltwin.basyx.authorization.rbac.CommonRbacConfig;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.feature.authorization.ConceptDescriptionTargetInfo;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.feature.authorization.PermissionResolver;
import org.eclipse.digitaltwin.basyx.core.exceptions.NotAuthorizedException;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;
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
@ConditionalOnExpression(value = "'${" + CommonAuthorizationConfig.TYPE_PROPERTY_KEY + "}' == '" + CommonRbacConfig.RBAC_AUTHORIZATION_TYPE + "' and '${basyx.backend}'.equals('MongoDB')")
public class MongoDBRbacPermissionResolver implements PermissionResolver<Criteria>, RbacPermissionResolver<Criteria> {
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
    public FilterInfo<Criteria> getGetAllConceptDescriptionsFilterInfo() {
        final RbacRuleSet rbacRuleSet = storage.getRbacRuleSet(null);
        final Set<RbacRule> rbacRules = rbacRuleSet.getRules();
        final List<String> roles = roleAuthenticator.getRoles();

        final Set<String> relevantSubmodelIds = rbacRules.stream()
                .filter(rbacRule -> rbacRule.getTargetInfo() instanceof ConceptDescriptionTargetInfo)
                .filter(rbacRule -> rbacRule.getAction().equals(Action.READ.toString()))
                .filter(rbacRule -> roles.contains(rbacRule.getRole()))
                .map(rbacRule -> (ConceptDescriptionTargetInfo) rbacRule.getTargetInfo())
                .map(ConceptDescriptionTargetInfo::getConceptDescriptionId)
                .collect(Collectors.toSet());
        return new FilterInfo<>(Criteria.where("_id").in(relevantSubmodelIds));
    }

    @Override
    public FilterInfo<Criteria> getGetAllConceptDescriptionsByIdShortFilterInfo() {
        final RbacRuleSet rbacRuleSet = storage.getRbacRuleSet(null);
        final Set<RbacRule> rbacRules = rbacRuleSet.getRules();
        final List<String> roles = roleAuthenticator.getRoles();

        final Set<String> relevantSubmodelIds = rbacRules.stream()
                .filter(rbacRule -> rbacRule.getTargetInfo() instanceof ConceptDescriptionTargetInfo)
                .filter(rbacRule -> rbacRule.getAction().equals(Action.READ.toString()))
                .filter(rbacRule -> roles.contains(rbacRule.getRole()))
                .map(rbacRule -> (ConceptDescriptionTargetInfo) rbacRule.getTargetInfo())
                .map(ConceptDescriptionTargetInfo::getConceptDescriptionId)
                .collect(Collectors.toSet());
        return new FilterInfo<>(Criteria.where("_id").in(relevantSubmodelIds));
    }

    @Override
    public FilterInfo<Criteria> getGetAllConceptDescriptionsByIsCaseOfFilterInfo() {
        final RbacRuleSet rbacRuleSet = storage.getRbacRuleSet(null);
        final Set<RbacRule> rbacRules = rbacRuleSet.getRules();
        final List<String> roles = roleAuthenticator.getRoles();

        final Set<String> relevantSubmodelIds = rbacRules.stream()
                .filter(rbacRule -> rbacRule.getTargetInfo() instanceof ConceptDescriptionTargetInfo)
                .filter(rbacRule -> rbacRule.getAction().equals(Action.READ.toString()))
                .filter(rbacRule -> roles.contains(rbacRule.getRole()))
                .map(rbacRule -> (ConceptDescriptionTargetInfo) rbacRule.getTargetInfo())
                .map(ConceptDescriptionTargetInfo::getConceptDescriptionId)
                .collect(Collectors.toSet());
        return new FilterInfo<>(Criteria.where("_id").in(relevantSubmodelIds));
    }

    @Override
    public FilterInfo<Criteria> getGetAllConceptDescriptionsByDataSpecificationReferenceFilterInfo() {
        final RbacRuleSet rbacRuleSet = storage.getRbacRuleSet(null);
        final Set<RbacRule> rbacRules = rbacRuleSet.getRules();
        final List<String> roles = roleAuthenticator.getRoles();

        final Set<String> relevantSubmodelIds = rbacRules.stream()
                .filter(rbacRule -> rbacRule.getTargetInfo() instanceof ConceptDescriptionTargetInfo)
                .filter(rbacRule -> rbacRule.getAction().equals(Action.READ.toString()))
                .filter(rbacRule -> roles.contains(rbacRule.getRole()))
                .map(rbacRule -> (ConceptDescriptionTargetInfo) rbacRule.getTargetInfo())
                .map(ConceptDescriptionTargetInfo::getConceptDescriptionId)
                .collect(Collectors.toSet());
        return new FilterInfo<>(Criteria.where("_id").in(relevantSubmodelIds));
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
