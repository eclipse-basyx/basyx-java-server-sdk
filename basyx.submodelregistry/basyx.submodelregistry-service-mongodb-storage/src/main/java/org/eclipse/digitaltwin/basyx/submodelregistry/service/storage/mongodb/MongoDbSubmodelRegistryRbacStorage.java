package org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.mongodb;

import lombok.RequiredArgsConstructor;
import org.eclipse.digitaltwin.basyx.authorization.rbac.IRbacStorage;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRuleSet;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MongoDbSubmodelRegistryRbacStorage implements IRbacStorage {
    private final MongoTemplate mongoTemplate;
    @Override
    public RbacRuleSet getRbacRuleSet() {
        final RbacRuleSet result = new RbacRuleSet();
        mongoTemplate.query(RbacRule.class).stream().collect(Collectors.toList())
                .forEach(result::addRule);
        return result;
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
