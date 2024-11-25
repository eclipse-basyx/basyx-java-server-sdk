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

import org.eclipse.digitaltwin.basyx.authorization.SubjectInformation;
import org.eclipse.digitaltwin.basyx.authorization.SubjectInformationProvider;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RoleProvider;
import org.eclipse.digitaltwin.basyx.core.exceptions.NullSubjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;

/**
 * An abstract permission resolver for {@link TargetInformation}
 * 
 * @param <T>
 * 
 * @author danish
 */
public class SimpleAbacPermissionResolver implements AbacPermissionResolver {

	private Logger logger = LoggerFactory.getLogger(SimpleAbacPermissionResolver.class);

	private AbacStorage abacStorage;
	private RoleProvider roleAuthenticator;
	private SubjectInformationProvider<Object> subjectInformationProvider;

	public SimpleAbacPermissionResolver(AbacStorage abacStorage, RoleProvider roleAuthenticator, SubjectInformationProvider<Object> subjectInformationProvider) {
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

	// Function that accepts a QueriesJsonSchema object and JWT token, and returns whether any rule satisfies it
    public boolean hasPermission(QueriesJsonSchema querySchema) {
        // Step 1: Extract the logical expression from QueriesJsonSchema
        LogicalExpression__1 queryParameter = querySchema.getQueryParameter();
        if (queryParameter == null) {
            throw new IllegalArgumentException("Invalid query: missing 'queryParameter'");
        }

        // Step 2: Get All Available Access Rules
        List<AllRule> allRules = abacStorage.getAbacRules();

        // Step 3: Parse JWT token to extract claims
        SubjectInformation<Object> subjectInfo = getSubjectInformation();

		Jwt jwt = (Jwt) subjectInfo.get();

        // Step 4: Check if any Rule Satisfies the Query
        for (AllRule rule : allRules) {
            if (isRuleMatchingQuery(rule, queryParameter) && areAttributesMatching(rule, jwt) && areRightsMatching(rule.getRights(), querySchema.getRights()) && isAccessAllowed(rule) && areObjectsMatching(rule)) {
                return true; // Query is satisfied by this rule
            }
        }

        // If no rule satisfies the query
        return false;
    }

    // Helper function to parse JWT token and extract claims
    private Jwt parseJwtToken(String jwtToken) {
        // Use Spring Security's Jwt class to parse the JWT token
        return Jwt.withTokenValue(jwtToken).build();
    }

    // Helper function to check if a rule matches the query
    private boolean isRuleMatchingQuery(AllRule rule, LogicalExpression__1 queryParameter) {
        // Step 4.1: Evaluate the logical expression (AND, OR, NOT, etc.)
        if (queryParameter.get$and() != null && !queryParameter.get$and().isEmpty()) {
            for (Object condition : queryParameter.get$and()) {
                if (!isRuleMatchingQuery(rule, (LogicalExpression__1) condition)) {
                    return false; // One of the AND conditions is not satisfied
                }
            }
            return true;
        } else if (queryParameter.get$or() != null && !queryParameter.get$or().isEmpty()) {
            for (Object condition : queryParameter.get$or()) {
                if (isRuleMatchingQuery(rule, (LogicalExpression__1) condition)) {
                    return true; // One of the OR conditions is satisfied
                }
            }
            return false;
        } else if (queryParameter.get$not() != null) {
            return !isRuleMatchingQuery(rule, (LogicalExpression__1) queryParameter.get$not());
        } else if (queryParameter.get$eq() != null) {
            return evaluateEquality(rule, queryParameter.get$eq());
        } else if (queryParameter.get$gt() != null) {
            return evaluateGreaterThan(rule, queryParameter.get$gt());
        }
        // Add more conditions as needed ($lt, $contains, etc.)

        return false; // If no condition matched
    }

    // Helper function to evaluate equality
    private boolean evaluateEquality(AllRule rule, Object eqCondition) {
        if (!(eqCondition instanceof List) || ((List<?>) eqCondition).size() != 2) {
            throw new IllegalArgumentException("Invalid $eq condition: must have exactly two elements");
        }

        List<?> conditions = (List<?>) eqCondition;
        String left = conditions.get(0).toString();
        String right = conditions.get(1).toString();

        // Here we assume that the rule has a method to get the attribute value by its name
        String ruleValue = getAttributeValueFromRule(rule, left);

        return ruleValue != null && ruleValue.equals(right);
    }

    // Helper function to evaluate greater than
    private boolean evaluateGreaterThan(AllRule rule, Object gtCondition) {
        if (!(gtCondition instanceof List) || ((List<?>) gtCondition).size() != 2) {
            throw new IllegalArgumentException("Invalid $gt condition: must have exactly two elements");
        }

        List<?> conditions = (List<?>) gtCondition;
        double left = getNumericValue(rule, conditions.get(0));
        double right = getNumericValue(rule, conditions.get(1));

        return left > right;
    }

    // Helper function to get numeric value from rule or constant
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

    // Helper method to get attribute value from rule
    private String getAttributeValueFromRule(AllRule rule, String attributeName) {
        for (AttributeItem attribute : rule.getAttributes()) {
            if (attribute.getClaim() != null && attribute.getClaim().equals(attributeName)) {
                return attribute.getClaim();
            }
            // Implement similar logic for GLOBAL and REFERENCE if needed
        }
        return null;
    }

    // Helper function to evaluate ATTRIBUTES of the rule against JWT claims
    private boolean areAttributesMatching(AllRule rule, Jwt jwt) {
        for (AttributeItem attribute : rule.getAttributes()) {
            if (attribute.getClaim() != null) {
                String claimValue = jwt.getClaim(attribute.getClaim());
                if (claimValue == null || !claimValue.equals(attribute.getClaim())) {
                    return false; // Claim does not match
                }
            }
            // Implement similar logic for GLOBAL and REFERENCE if needed
        }
        return true; // All attributes match
    }

    // Helper function to evaluate RIGHTS of the rule against JWT claims
    private boolean areRightsMatching(List<RightsEnum> requiredRights, List<RightsEnum> queryRights) {
        
        return requiredRights.containsAll(queryRights);
    }

    // Helper function to evaluate ACCESS of the rule
    private boolean isAccessAllowed(AllRule rule) {
        // Check if the access is set to ALLOW
        return rule.getAccess() == AllRule.Access.ALLOW;
    }

    // Helper function to evaluate OBJECTS of the rule
    private boolean areObjectsMatching(AllRule rule) {
        // Implement logic to evaluate if objects in the rule match the requested objects
        // For simplicity, we assume all objects match for now
        return true;
    }

    // Function to create an instance of QueriesJsonSchema with query parameters matching an AAS
    public static QueriesJsonSchema createSampleQuery() {
        QueriesJsonSchema querySchema = new QueriesJsonSchema();
        LogicalExpression__1 logicalExpression = new LogicalExpression__1();

        // Example: Create an equality condition to match a specific AAS ID
        List<Object> eqCondition = new ArrayList<>();
        eqCondition.add("$aas.idShort"); // Assuming "$aas.idShort" represents an attribute of the AAS
        eqCondition.add("AAS_123"); // Value to match

        // Set the equality condition in the logical expression
        logicalExpression.set$eq(eqCondition);

        // Set the logical expression in the query schema
        querySchema.setQueryParameter(logicalExpression);

        return querySchema;
    }	
    
    private SubjectInformation<Object> getSubjectInformation() {
		SubjectInformation<Object> subjectInfo = subjectInformationProvider.get();

		if (subjectInfo == null)
			throw new NullSubjectException("Subject information is null.");
		
		return subjectInfo;
	}
}
