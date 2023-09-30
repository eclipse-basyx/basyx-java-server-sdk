package org.eclipse.digitaltwin.basyx.authorization.rbac;

import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashSet;
import java.util.Set;

public class MongoDBAuthorizationRbacStorage implements IRbacStorage<Criteria> {
    private final MongoTemplate mongoTemplate;

    public MongoDBAuthorizationRbacStorage(MongoTemplate mongoTemplate, RbacRuleSet rbacRuleSet) {
        this.mongoTemplate = mongoTemplate;

        rbacRuleSet.getRules().forEach(this::addRule);
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
                .and("targetInfo").is(rbacRule.getTargetInfo())
        );
        mongoTemplate.remove(query, RbacRule.class);
    }
}
