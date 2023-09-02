package org.eclipse.digitaltwin.basyx.submodelregistry.service.authorization.rbac;

import org.eclipse.digitaltwin.basyx.authorization.Action;
import org.eclipse.digitaltwin.basyx.authorization.IRoleAuthenticator;
import org.eclipse.digitaltwin.basyx.authorization.ISubjectInfo;
import org.eclipse.digitaltwin.basyx.authorization.rbac.*;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.authorization.PermissionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// TODO: remove comment
//@ConditionalOnProperty(prefix = "basyx.submodelregistry.feature.authorization", name = "type", havingValue = "rbac")

@Primary
@ConditionalOnExpression(value = "'${basyx.submodelregistry.feature.authorization.type}' == 'rbac' and '${registry.type}'.equals('mongodb')")
@Service
public class MongoDBRbacPermissionResolver implements PermissionResolver<Criteria> {
    @Autowired
    private final Environment environment;

    @Autowired
    private final IRbacStorage storage;

    @Autowired
    private final IRoleAuthenticator roleAuthenticator;

    public MongoDBRbacPermissionResolver(Environment environment, IRbacStorage storage, IRoleAuthenticator roleAuthenticator) {
        this.environment = environment;
        this.storage = storage;
        this.roleAuthenticator = roleAuthenticator;
    }

    final String ROLE = "admin"; // TODO: replace with actual roles from request

    @Override
    public boolean hasPermission(SubmodelDescriptor submodelDescriptor, Action action, ISubjectInfo<?> subjectInfo) {
        final IRbacRuleChecker rbacRuleChecker = new PredefinedSetRbacRuleChecker(storage.getRbacRuleSet());
        final ITargetInformation targetInformation = new BaSyxObjectTargetInformation(null, submodelDescriptor.getId(), null);
        return rbacRuleChecker.checkRbacRuleIsSatisfied(Arrays.asList(ROLE), action.toString(), targetInformation);
    }

    @Override
    public FilterInfo<Criteria> getGetAllSubmodelDescriptorsFilterInfo() {
        final RbacRuleSet rbacRuleSet = storage.getRbacRuleSet();
        final Set<RbacRule> rbacRules = rbacRuleSet.getRules();
        final List<String> roles = roleAuthenticator.getRoles();

        final Set<String> relevantSubmodelIds = rbacRules.stream()
                .filter(rbacRule -> rbacRule.getTargetInformation() instanceof BaSyxObjectTargetInformation)
                .filter(rbacRule -> rbacRule.getAction().equals(Action.READ.toString()))
                .filter(rbacRule -> roles.contains(rbacRule.getRole()))
                .map(rbacRule -> (BaSyxObjectTargetInformation) rbacRule.getTargetInformation())
                .map(BaSyxObjectTargetInformation::getSmId)
                .collect(Collectors.toSet());
        return new FilterInfo<>(Criteria.where("_id").in(relevantSubmodelIds));
    }
}
