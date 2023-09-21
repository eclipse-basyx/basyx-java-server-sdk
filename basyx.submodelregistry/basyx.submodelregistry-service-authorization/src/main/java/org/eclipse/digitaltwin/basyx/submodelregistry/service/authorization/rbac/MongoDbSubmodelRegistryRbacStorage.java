package org.eclipse.digitaltwin.basyx.submodelregistry.service.authorization.rbac;

import org.eclipse.digitaltwin.basyx.authorization.rbac.IRbacStorage;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRuleSet;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@ConditionalOnExpression(value = "'${basyx.submodelregistry.feature.authorization.type}' == 'rbac' and '${registry.type}'.equals('mongodb')")
@Service
public class MongoDbSubmodelRegistryRbacStorage implements IRbacStorage<Criteria> {
    private final MongoTemplate mongoTemplate;

    public MongoDbSubmodelRegistryRbacStorage(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public RbacRuleSet getRbacRuleSet(FilterInfo<Criteria> filterInfo) {
        final Query query = new Query();
        if (filterInfo != null) {
            query.addCriteria(filterInfo.getFilter());
        }
        final Set<RbacRule> rbacRules = new HashSet<>(mongoTemplate.find(query, RbacRule.class));
        return new RbacRuleSet(rbacRules);
    }

    @Override
    public void addRule(RbacRule rbacRule) {
        mongoTemplate.insert(rbacRule);
    }

    @Override
    public void removeRule(RbacRule rbacRule) {
        final Query query = new Query(
                Criteria.where("role").is(rbacRule.getRole())
                .and("action").is(rbacRule.getAction())
                .and("targetInformation").is(rbacRule.getTargetInformation())
        );
        mongoTemplate.remove(query, RbacRule.class);
    }
}
