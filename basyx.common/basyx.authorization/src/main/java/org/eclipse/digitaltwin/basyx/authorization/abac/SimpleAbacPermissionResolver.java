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

package org.eclipse.digitaltwin.basyx.authorization.abac;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.digitaltwin.basyx.authorization.SubjectInformation;
import org.eclipse.digitaltwin.basyx.authorization.SubjectInformationProvider;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RoleProvider;
import org.eclipse.digitaltwin.basyx.core.exceptions.NullSubjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * An abstract permission resolver for {@link TargetInformation}
 * 
 * @param <T>
 * 
 */
public class SimpleAbacPermissionResolver implements AbacPermissionResolver {

    private Logger logger = LoggerFactory.getLogger(SimpleAbacPermissionResolver.class);

    private AbacStorage abacStorage;
    private RoleProvider roleAuthenticator;
    private SubjectInformationProvider<Object> subjectInformationProvider;

    public SimpleAbacPermissionResolver(AbacStorage abacStorage, RoleProvider roleAuthenticator,
            SubjectInformationProvider<Object> subjectInformationProvider) {
        super();
        this.abacStorage = abacStorage;
        this.roleAuthenticator = roleAuthenticator;
        this.subjectInformationProvider = subjectInformationProvider;
    }

    public AbacStorage getAbacStorage() {
        return abacStorage;
    }

    public RoleProvider getRoleProvider() {
        return roleAuthenticator;
    }

    // Function that checks if a query satisfies any rule
    public boolean hasPermission(QueriesJsonSchema querySchema) {
        LogicalComponent queryParameter = querySchema.getQueryParameter(); // LogicalComponent type
        if (queryParameter == null) {
            throw new IllegalArgumentException("Invalid query: missing 'queryParameter'");
        }

        List<AllRule> allRules = abacStorage.getAbacRules();
        SubjectInformation<Object> subjectInfo = getSubjectInformation();
        Jwt jwt = (Jwt) subjectInfo.get();

        for (AllRule rule : allRules) {
            if (isRuleMatchingQuery(rule, queryParameter) 
//            		&&
//                areAttributesMatching(rule, jwt) &&
//                areRightsMatching(rule.getRights(), querySchema.getRights()) &&
//                isAccessAllowed(rule) &&
//                areObjectsMatching(rule)
                
            		) {
                return true;
            }
        }
        return false;
    }

    // Recursive method to evaluate logical and simple expressions
    private boolean isRuleMatchingQuery(AllRule rule, LogicalComponent queryComponent) {
        if (queryComponent instanceof LogicalExpression__1) {
            LogicalExpression__1 logicalExpression = (LogicalExpression__1) queryComponent;

            // Handle $and
            if (logicalExpression.get$and() != null && !logicalExpression.get$and().isEmpty()) {
                for (LogicalComponent condition : logicalExpression.get$and()) {
                    if (!isRuleMatchingQuery(rule, condition)) {
                        return false; // One of the AND conditions is not satisfied
                    }
                }
                return true;
            }

            // Handle $or
            if (logicalExpression.get$or() != null && !logicalExpression.get$or().isEmpty()) {
                for (LogicalComponent condition : logicalExpression.get$or()) {
                    if (isRuleMatchingQuery(rule, condition)) {
                        return true; // One of the OR conditions is satisfied
                    }
                }
                return false;
            }

            // Handle $not
            if (logicalExpression.get$not() != null) {
                return !isRuleMatchingQuery(rule, logicalExpression.get$not());
            }
        } else if (queryComponent instanceof SimpleExpression) {
            return evaluateSimpleExpression(rule, (SimpleExpression) queryComponent);
        }

        return false; // If no condition matched
    }

    // Evaluate simple expressions (e.g., $eq, $gt)
    private boolean evaluateSimpleExpression(AllRule rule, SimpleExpression simpleExpression) {
        if (simpleExpression.get$eq() != null) {
            return evaluateEquality(rule, simpleExpression.get$eq());
        }
        if (simpleExpression.get$gt() != null) {
            return evaluateGreaterThan(rule, simpleExpression.get$gt());
        }
        // Add other evaluations ($lt, $contains, etc.)
        return false;
    }

 // Evaluate $eq condition by checking if the rule's formula satisfies the query formula
    private boolean evaluateEquality(AllRule rule, Object eqCondition) {
        if (!(eqCondition instanceof List) || ((List<?>) eqCondition).size() != 2) {
            throw new IllegalArgumentException("Invalid $eq condition: must have exactly two elements");
        }

        List<?> conditions = (List<?>) eqCondition;
        String left = conditions.get(0).toString();
        String right = conditions.get(1).toString();

        LogicalComponent ruleFormula = rule.getFormula();
        if (ruleFormula == null) {
            return false; // Base case: If the rule has no formula, return false
        }

        // Create a SimpleExpression to represent the query condition
        SimpleExpression queryExpression = new SimpleExpression();
        queryExpression.set$eq(List.of(left, right));

        // Check if the rule's formula matches the query condition
        return matchFormula(ruleFormula, queryExpression);
    }

    
    private boolean matchFormula(LogicalComponent ruleFormula, LogicalComponent queryFormula) {
        // Base case: If both are simple expressions, compare directly
        if (ruleFormula instanceof SimpleExpression && queryFormula instanceof SimpleExpression) {
            return compareSimpleExpressions((SimpleExpression) ruleFormula, (SimpleExpression) queryFormula);
        }

        // Recursive case: If the rule is a logical expression, evaluate its conditions
        if (ruleFormula instanceof LogicalExpression__1) {
            LogicalExpression__1 logicalRule = (LogicalExpression__1) ruleFormula;

            if (logicalRule.get$and() != null && !logicalRule.get$and().isEmpty()) {
                // Check if the query formula satisfies all AND conditions
                for (LogicalComponent condition : logicalRule.get$and()) {
                    if (!matchFormula(condition, queryFormula)) {
                        return false; // One AND condition fails
                    }
                }
                return true; // All AND conditions pass
            }

            if (logicalRule.get$or() != null && !logicalRule.get$or().isEmpty()) {
                // Check if the query formula satisfies at least one OR condition
                for (LogicalComponent condition : logicalRule.get$or()) {
                    if (matchFormula(condition, queryFormula)) {
                        return true; // At least one OR condition matches
                    }
                }
                return false; // No OR condition matches
            }

            if (logicalRule.get$not() != null) {
                // Negate the match result for NOT condition
                return !matchFormula(logicalRule.get$not(), queryFormula);
            }
        }

        // Default: If no match criteria met, return false
        return false;
    }
    
    private boolean compareSimpleExpressions(SimpleExpression ruleExpr, SimpleExpression queryExpr) {
        // Compare $eq conditions
        if (ruleExpr.get$eq() != null && queryExpr.get$eq() != null) {
            List<?> ruleEq = (List<?>) ruleExpr.get$eq();
            List<?> queryEq = (List<?>) queryExpr.get$eq();
            return ruleEq.equals(queryEq); // Compare the $eq lists
        }

        // Add comparisons for other types like $gt, $lt if needed
        return false;
    }



    // Evaluate $gt condition
    private boolean evaluateGreaterThan(AllRule rule, Object gtCondition) {
        if (!(gtCondition instanceof List) || ((List<?>) gtCondition).size() != 2) {
            throw new IllegalArgumentException("Invalid $gt condition: must have exactly two elements");
        }

        List<?> conditions = (List<?>) gtCondition;
        double left = getNumericValue(rule, conditions.get(0));
        double right = getNumericValue(rule, conditions.get(1));

        return left > right;
    }

    // Helper function to extract numeric values
    private double getNumericValue(AllRule rule, Object node) {
        if (node instanceof Number) {
            return ((Number) node).doubleValue();
        } else if (node instanceof String) {
            String attributeName = (String) node;
            String value = getAttributeValueFromRule(rule, attributeName);
            if (value != null) {
                return Double.parseDouble(value);
            }
        }
        throw new IllegalArgumentException("Invalid numeric value in condition");
    }

    // Retrieve attribute value from rule
    private String getAttributeValueFromRule(AllRule rule, String attributeName) {
        for (AttributeItem attribute : rule.getAttributes()) {
            if (attribute.getClaim() != null && attribute.getClaim().equals(attributeName)) {
                return attribute.getClaim();
            }
        }
        return null;
    }

    // Check if rule attributes match JWT claims
    private boolean areAttributesMatching(AllRule rule, Jwt jwt) {
        for (AttributeItem attribute : rule.getAttributes()) {
            if (attribute.getClaim() != null) {
                String claimValue = jwt.getClaim(attribute.getClaim());
                if (claimValue == null || !claimValue.equals(attribute.getClaim())) {
                    return false;
                }
            }
        }
        return true;
    }

    // Check if required rights match
    private boolean areRightsMatching(List<RightsEnum> requiredRights, List<RightsEnum> queryRights) {
        return requiredRights.containsAll(queryRights);
    }

    // Check if access is allowed
    private boolean isAccessAllowed(AllRule rule) {
        return rule.getAccess() == AllRule.Access.ALLOW;
    }

    // Evaluate object matching (stub for now)
    private boolean areObjectsMatching(AllRule rule) {
        return true; // Assume all objects match for now
    }

    // Get subject information
    private SubjectInformation<Object> getSubjectInformation() {
        SubjectInformation<Object> subjectInfo = subjectInformationProvider.get();

        if (subjectInfo == null)
            throw new NullSubjectException("Subject information is null.");

        return subjectInfo;
    }
}

