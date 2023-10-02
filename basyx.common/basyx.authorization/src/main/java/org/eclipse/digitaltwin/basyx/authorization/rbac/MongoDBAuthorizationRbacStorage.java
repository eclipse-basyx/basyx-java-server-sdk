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
