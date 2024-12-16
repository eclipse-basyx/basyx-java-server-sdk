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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.basyx.authorization.SubjectInformation;
import org.eclipse.digitaltwin.basyx.authorization.SubjectInformationProvider;
import org.eclipse.digitaltwin.basyx.authorization.abac.AttributeItem.Global;
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

	// Function that checks if a query satisfies any rule
	public boolean hasPermission(RightsEnum rightsEnum, ObjectItem objectItem, Map<String, Value> attributesMap) {
		// LogicalComponent queryParameter = querySchema.getQueryParameter(); //
		// LogicalComponent type
		// if (queryParameter == null) {
		// throw new IllegalArgumentException("Invalid query: missing
		// 'queryParameter'");
		// }
		//
		List<AllAccessPermissionRule> allRules = abacStorage.getAbacRules();

		List<AllAccessPermissionRule> filteredRules = filterAccessRules(allRules, rightsEnum, objectItem);
		SubjectInformation<Object> subjectInfo = getSubjectInformation();
		Jwt jwt = (Jwt) subjectInfo.get();

		boolean accessGranted = validateAccessRules(filteredRules, attributesMap, jwt);

		return accessGranted;
	}
	
	private static Object getNestedClaim(Jwt jwt, String claim) {
        String[] keys = claim.split("\\.");
        Object value = jwt.getClaims();

        for (String key : keys) {
            if (value instanceof Map) {
                value = ((Map<?, ?>) value).get(key);
            } else {
                return null; // Key path does not exist or invalid structure
            }
        }

        // Check the final value type
        if (value instanceof String) {
            return value;
        } else if (value instanceof List) {
            return value;
        } else {
            return null; // Unsupported type
        }
    }

	private static List<AllAccessPermissionRule> filterAccessRules(List<AllAccessPermissionRule> accessRules, RightsEnum rightsEnum, ObjectItem objectItem) {
		return accessRules.stream().filter(rule -> containsRightForAction(rule.getRights(), rightsEnum)).filter(rule -> AllAccessPermissionRule.Access.ALLOW.equals(rule.getAccess()))
				.filter(rule -> objectMatches(rule.getObjects(), objectItem)).toList();
	}

	private static boolean containsRightForAction(List<RightsEnum> list, RightsEnum rightsEnum) {
		return list.contains(rightsEnum);
	}

	private static boolean objectMatches(List<ObjectItem> objects, ObjectItem objectItem) {

		if (objects.size() > 1)
			return false;

		if (objects.get(0).getRoute() != null)
			return objects.get(0).getRoute().equals(objectItem.getRoute());

		if (objects.get(0).getIdentifiable() != null)
			return objects.get(0).getIdentifiable().equals("(AAS)*") || objects.get(0).getIdentifiable().equals(objectItem.getIdentifiable());

		return false;
	}

	private static boolean validateAccessRules(List<AllAccessPermissionRule> filteredRules, Map<String, Value> attributesMap, Jwt jwt) {
		
		return filteredRules.stream().anyMatch(rule -> evaluateFormula(rule.getFormula(), attributesMap, populateAttrItemsMapWithAttributeValue(rule, jwt)));
	}

	private static Map<String, Object> populateAttrItemsMapWithAttributeValue(AllAccessPermissionRule rule, Jwt jwt) {
		
		Map<String, Object> attributeItemsMap = new HashMap<String, Object>();
		
		List<AttributeItem> attributeItems = rule.getAttributes();
		
		List<String> claims = attributeItems.stream().map(attributeItem -> attributeItem.getClaim()).filter(Objects::nonNull).collect(Collectors.toList());
		List<Global> globals = attributeItems.stream().map(attributeItem -> attributeItem.getGlobal()).filter(Objects::nonNull).collect(Collectors.toList());
			
		for (String claim : claims) {
			Object nestedClaimObject = getNestedClaim(jwt, claim);
			
			if (nestedClaimObject != null)
				attributeItemsMap.put("CLAIM#" + claim, nestedClaimObject);
		}
		
		for (Global global : globals) {
			if (global == Global.UTCNOW) {
				ZonedDateTime utcNow = ZonedDateTime.now(ZoneId.of("UTC"));
				
				Value value =  new Value();
				value.setTimeVal(utcNow.toString());
				
				attributeItemsMap.put("GLOBAL#UTCNOW", value);
			} else if (global == Global.LOCALNOW) {
				LocalDateTime localNow = LocalDateTime.now();
				
				Value value =  new Value();
				value.setTimeVal(localNow.toString());
				
				attributeItemsMap.put("GLOBAL#LOCALNOW", value);
			}
		}
		
		return attributeItemsMap;
	}

	private static boolean evaluateFormula(LogicalExpression formula, Map<String, Value> attributesMap, Map<String, Object> attributeItemsMap) {

		if (formula.get$and() != null && !formula.get$and().isEmpty()) {
			// All child expressions in $and must match
			return formula.get$and().stream().allMatch(rule -> evaluateFormula(rule, attributesMap, attributeItemsMap));
		}

		if (formula.get$or() != null && !formula.get$or().isEmpty()) {
			// At least one child expression in $or must match
			return formula.get$or().stream().anyMatch(rule -> evaluateFormula(rule, attributesMap, attributeItemsMap));
		}

		if (formula.get$not() != null) {
			// Negation: query must not match the $not condition
			return !evaluateFormula(formula.get$not(), attributesMap, attributeItemsMap);
		}

		if (formula.get$eq() != null) {
			return evaluateEquality(formula.get$eq(), attributesMap, attributeItemsMap);
		}
		
		if (formula.get$ne() != null) {
			return evaluateInequality(formula.get$ne(), attributesMap, attributeItemsMap);
		}
		
		if (formula.get$le() != null) {
			return evaluateLessOrEqual(formula.get$le(), attributesMap, attributeItemsMap);
		}
		
		if (formula.get$lt() != null) {
			return evaluateLessThan(formula.get$lt(), attributesMap, attributeItemsMap);
		}
		
		if (formula.get$ge() != null) {
			return evaluateGreaterOrEqual(formula.get$ge(), attributesMap, attributeItemsMap);
		}
		
		if (formula.get$gt() != null) {
			return evaluateGreaterThan(formula.get$gt(), attributesMap, attributeItemsMap);
		}
		// Add more cases for other operators ($gt, $lt, etc.)
		return false;
	}

	private static boolean evaluateEquality(List<Value> operands, Map<String, Value> attributesMap, Map<String, Object> attributeItemsMap) {
	    if (operands.size() != 2) return false;

	    if (operands.get(0).getStrModel() != null && operands.get(0).getStrModel().startsWith("$aas")) {
	        return evaluateStrModelEquality(operands, attributesMap);
	    }

	    if (operands.get(0).getAttribute() != null) {
	        return evaluateAttributeEquality(operands, attributeItemsMap);
	    }

	    return false;
	}

	private static boolean evaluateStrModelEquality(List<Value> operands, Map<String, Value> attributesMap) {
	    String strModel = operands.get(0).getStrModel();

	    if (operands.get(1).getStrVal() != null) {
	        String ruleValue = operands.get(1).getStrVal();

	        if (attributesMap.containsKey(strModel)) {
	            String objectValue = attributesMap.get(strModel).getStrVal();
	            return ruleValue.equals(objectValue);
	        }
	    } else if (operands.get(1).getTimeVal() != null) {
	        String ruleValue = operands.get(1).getTimeVal();

	        if (attributesMap.containsKey(strModel)) {
	            String objectValue = attributesMap.get(strModel).getTimeVal();
	            return ruleValue.equals(objectValue);
	        }
	    }
	    return false;
	}

	private static boolean evaluateAttributeEquality(List<Value> operands, Map<String, Object> attributeItemsMap) {
	    AttributeItem ruleAttributeItem = operands.get(0).getAttribute();

	    if (ruleAttributeItem.getClaim() != null) {
	        return evaluateClaimEquality(operands, attributeItemsMap);
	    } else if (ruleAttributeItem.getGlobal() != null) {
	        return evaluateGlobalEquality(operands, attributeItemsMap);
	    }
	    return false;
	}

	private static boolean evaluateClaimEquality(List<Value> operands, Map<String, Object> attributeItemsMap) {
	    String ruleClaimItem = operands.get(0).getAttribute().getClaim();
	    if (attributeItemsMap.containsKey("CLAIM#" + ruleClaimItem)) {
	        Object objectAttributeClaimValue = attributeItemsMap.get("CLAIM#" + ruleClaimItem);
	        String ruleAttributeClaimValue = operands.get(1).getStrVal();

	        if (ruleAttributeClaimValue != null) {
	        	
	        	if (objectAttributeClaimValue instanceof String)
	        		return ruleAttributeClaimValue.equals((String) objectAttributeClaimValue);
	        	else if (objectAttributeClaimValue instanceof List) {
	        		List<String> claimValueList = (List<String>) objectAttributeClaimValue;
	        		
	        		return claimValueList.contains(ruleAttributeClaimValue);
				}
	        }
	    }
	    return false;
	}

	private static boolean evaluateGlobalEquality(List<Value> operands, Map<String, Object> attributeItemsMap) {
	    Global ruleGlobalItem = operands.get(0).getAttribute().getGlobal();

	    if (ruleGlobalItem == Global.LOCALNOW) {
	        System.out.println("The Global enum is set to LOCALNOW");

	        if (attributeItemsMap.containsKey("GLOBAL#LOCALNOW")) {
	            String objectAttributeGlobalValue = ((Value) attributeItemsMap.get("GLOBAL#LOCALNOW")).getTimeVal();

	            if (operands.get(1).getStrVal() != null) {
	                String ruleAttributeGlobalValue = operands.get(1).getStrVal();
	                return ruleAttributeGlobalValue.equals(objectAttributeGlobalValue);
	            } else if (operands.get(1).getTimeVal() != null) {
	                String ruleAttributeGlobalValue = operands.get(1).getTimeVal();
	                return ruleAttributeGlobalValue.equals(objectAttributeGlobalValue);
	            } else {
	                return false;
	            }
	        } else {
	            return false;
	        }
	    } else if (ruleGlobalItem == Global.UTCNOW) {
	        System.out.println("The Global enum is set to UTCNOW");

	        if (attributeItemsMap.containsKey("GLOBAL#UTCNOW")) {
	            String objectAttributeGlobalValue = ((Value) attributeItemsMap.get("GLOBAL#LOCALNOW")).getTimeVal();

	            if (operands.get(1).getStrVal() != null) {
	                String ruleAttributeGlobalValue = operands.get(1).getStrVal();
	                return ruleAttributeGlobalValue.equals(objectAttributeGlobalValue);
	            } else if (operands.get(1).getTimeVal() != null) {
	                String ruleAttributeGlobalValue = operands.get(1).getTimeVal();
	                return ruleAttributeGlobalValue.equals(objectAttributeGlobalValue);
	            } else {
	                return false;
	            }
	        } else {
	            return false;
	        }
	    } else if (ruleGlobalItem == Global.CLIENTNOW) {
	        System.out.println("The Global enum is set to CLIENTNOW - Not Supported");
	        return false;
	    } else if (ruleGlobalItem == Global.ANONYMOUS) {
	        System.out.println("The Global enum is set to ANONYMOUS - Not Supported");
	        return false;
	    } else {
	        System.out.println("Unknown Global enum value");
	        return false;
	    }
	}


	private static boolean evaluateInequality(List<Value> operands, Map<String, Value> attributesMap, Map<String, Object> attributeItemsMap) {
	    if (operands.size() != 2) return false;
	    // Implement logic for $ne
	    return !evaluateEquality(operands, attributesMap, attributeItemsMap);
	}

	private static boolean evaluateGreaterThan(List<Value> operands, Map<String, Value> attributesMap, Map<String, Object> attributeItemsMap) {
	    if (operands.size() != 2) return false;
	    return compareValues(operands, attributesMap, attributeItemsMap) > 0;
	}

	private static boolean evaluateGreaterOrEqual(List<Value> operands, Map<String, Value> attributesMap, Map<String, Object> attributeItemsMap) {
	    if (operands.size() != 2) return false;
	    return compareValues(operands, attributesMap, attributeItemsMap) >= 0;
	}

	private static boolean evaluateLessThan(List<Value> operands, Map<String, Value> attributesMap, Map<String, Object> attributeItemsMap) {
	    if (operands.size() != 2) return false;
	    return compareValues(operands, attributesMap, attributeItemsMap) < 0;
	}

	private static boolean evaluateLessOrEqual(List<Value> operands, Map<String, Value> attributesMap, Map<String, Object> attributeItemsMap) {
	    if (operands.size() != 2) return false;
	    return compareValues(operands, attributesMap, attributeItemsMap) <= 0;
	}

	private static boolean evaluateContains(List<StringValue> operands, Map<String, Value> attributesMap) {
	    if (operands.size() != 2) return false;
	    return operands.get(1).getStrVal() != null &&
	           operands.get(0).getStrVal() != null &&
	           operands.get(0).getStrVal().contains(operands.get(1).getStrVal());
	}

	private static boolean evaluateStartsWith(List<StringValue> operands, Map<String, Value> attributesMap) {
	    if (operands.size() != 2) return false;
	    return operands.get(1).getStrVal() != null &&
	           operands.get(0).getStrVal() != null &&
	           operands.get(0).getStrVal().startsWith(operands.get(1).getStrVal());
	}

	private static boolean evaluateEndsWith(List<StringValue> operands, Map<String, Value> attributesMap) {
	    if (operands.size() != 2) return false;
	    return operands.get(1).getStrVal() != null &&
	           operands.get(0).getStrVal() != null &&
	           operands.get(0).getStrVal().endsWith(operands.get(1).getStrVal());
	}

	private static int compareValues(List<Value> operands, Map<String, Value> attributesMap, Map<String, Object> attributeItemsMap) {
	    Value operand1Value = operands.get(0);
	    
	    if (operand1Value.getStrModel() != null && operand1Value.getStrModel().startsWith("$aas")) {
	        return evaluateStrModelComparison(operands, attributesMap);
	    }
	    
	    if (operand1Value.getAttribute() != null) {
	        return evaluateAttributeComparison(operands, attributeItemsMap);
	    }

	    return 0;
	}
	
	private static int evaluateStrModelComparison(List<Value> operands, Map<String, Value> attributesMap) {
	    String strModel = operands.get(0).getStrModel();

	    if (operands.get(1).getStrVal() != null) {
	        String ruleValue = operands.get(1).getStrVal();

	        if (attributesMap.containsKey(strModel)) {
	            String objectValue = attributesMap.get(strModel).getStrVal();
	            return ruleValue.compareTo(objectValue);
	        }
	    } else if (operands.get(1).getTimeVal() != null) {
	        String ruleTimeValueString = operands.get(1).getTimeVal();
	        
	        LocalTime ruleTimeValue = LocalTime.parse(ruleTimeValueString);

	        if (attributesMap.containsKey(strModel)) {
	            String objectTimeValueString = attributesMap.get(strModel).getTimeVal();
	            
	            LocalTime objectTimeValue = LocalTime.parse(objectTimeValueString);
	            
	            return ruleTimeValue.compareTo(objectTimeValue);
	        }
	    }
	    return 0;
	}
	
	private static int evaluateAttributeComparison(List<Value> operands, Map<String, Object> attributeItemsMap) {
	    AttributeItem ruleAttributeItem = operands.get(0).getAttribute();

	    if (ruleAttributeItem.getClaim() != null) {
	        return evaluateClaimComparison(operands, attributeItemsMap);
	    } else if (ruleAttributeItem.getGlobal() != null) {
	        return evaluateGlobalComparison(operands, attributeItemsMap);
	    }
	    return 0;
	}
	
//	private static int evaluateClaimComparison(List<Value> operands, Map<String, Object> attributeItemsMap) {
//	    String ruleClaimItem = operands.get(0).getAttribute().getClaim();
//	    if (attributeItemsMap.containsKey("CLAIM#" + ruleClaimItem)) {
//	        String objectAttributeClaimValue = attributeItemsMap.get("CLAIM#" + ruleClaimItem).getStrVal();
//	        String ruleAttributeClaimValue = operands.get(1).getStrVal();
//
//	        if (ruleAttributeClaimValue != null) {
//	            return ruleAttributeClaimValue.compareTo(objectAttributeClaimValue);
//	        } else if (operands.get(1).getTimeVal() != null) {
//	            String ruleTimeValueString = operands.get(1).getTimeVal();
//	            String objectTimeValueString = attributeItemsMap.get("CLAIM#" + ruleClaimItem).getTimeVal();
//	            
//	            LocalTime ruleTimeValueTime = LocalTime.parse(ruleTimeValueString);
//	            LocalTime objectTimeValueTime = LocalTime.parse(objectTimeValueString);
//	            
//	            return ruleTimeValueTime.compareTo(objectTimeValueTime);
//	        }
//	    }
//	    
//	    return 0;
//	}
	
	private static int evaluateClaimComparison(List<Value> operands, Map<String, Object> attributeItemsMap) {
	    String ruleClaimItem = operands.get(0).getAttribute().getClaim();
	    if (attributeItemsMap.containsKey("CLAIM#" + ruleClaimItem)) {
	        Object objectAttributeClaimValue = attributeItemsMap.get("CLAIM#" + ruleClaimItem);
	        String ruleAttributeClaimValue = operands.get(1).getStrVal();

	        if (ruleAttributeClaimValue != null) {
	        	
	        	if (objectAttributeClaimValue instanceof String)
	        		return ruleAttributeClaimValue.compareTo((String) objectAttributeClaimValue);
	        	else if (objectAttributeClaimValue instanceof List) {
	        		List<String> claimValueList = (List<String>) objectAttributeClaimValue;
	        		
	        		return 0;
				}
	        }
	    }
	    return 0;
	}

//	private static int evaluateGlobalComparison(List<Value> operands, Map<String, Value> attributesMap) {
//	    Global ruleGlobalItem = operands.get(0).getAttribute().getGlobal();
//	    String globalKey = "GLOBAL#" + ruleGlobalItem;
//	    if (attributesMap.containsKey(globalKey)) {
//	        String objectAttributeGlobalValue = attributesMap.get(globalKey).getStrVal();
//	        String ruleAttributeGlobalValue = operands.get(1).getStrVal();
//
//	        if (ruleAttributeGlobalValue != null) {
//	            return ruleAttributeGlobalValue.compareTo(objectAttributeGlobalValue);
//	        } else if (operands.get(1).getTimeVal() != null) {
//	        	String ruleTimeValueString = operands.get(1).getTimeVal();
//	            String objectTimeValueString = attributesMap.get(globalKey).getTimeVal();
//	            
//	            LocalTime ruleTimeValueTime = LocalTime.parse(ruleTimeValueString);
//	            LocalTime objectTimeValueTime = LocalTime.parse(objectTimeValueString);
//	            
//	            return ruleTimeValueTime.compareTo(objectTimeValueTime);
//	        }
//	    }
//	    
//	    return 0;
//	}
	
	private static int evaluateGlobalComparison(List<Value> operands, Map<String, Object> attributeItemsMap) {
	    Global ruleGlobalItem = operands.get(0).getAttribute().getGlobal();

	    if (ruleGlobalItem == Global.LOCALNOW) {
	        System.out.println("The Global enum is set to LOCALNOW");

	        if (attributeItemsMap.containsKey("GLOBAL#LOCALNOW")) {
	            String objectAttributeGlobalValue = ((Value) attributeItemsMap.get("GLOBAL#LOCALNOW")).getTimeVal();

	            if (operands.get(1).getStrVal() != null) {
	                String ruleAttributeGlobalValue = operands.get(1).getStrVal();
	                return ruleAttributeGlobalValue.compareTo(objectAttributeGlobalValue);
	            } else if (operands.get(1).getTimeVal() != null) {
	            	String ruleTimeValueString = operands.get(1).getTimeVal();
		            
		            LocalTime ruleTimeValueTime = LocalTime.parse(ruleTimeValueString);
		            LocalTime objectTimeValueTime = LocalTime.parse(objectAttributeGlobalValue);
		            
		            return ruleTimeValueTime.compareTo(objectTimeValueTime);
	            } else {
	                return 0;
	            }
	        } else {
	            return 0;
	        }
	    } else if (ruleGlobalItem == Global.UTCNOW) {
	        System.out.println("The Global enum is set to UTCNOW");

	        if (attributeItemsMap.containsKey("GLOBAL#UTCNOW")) {
	            String objectAttributeGlobalValue = ((Value) attributeItemsMap.get("GLOBAL#UTCNOW")).getTimeVal();

	            if (operands.get(1).getStrVal() != null) {
	                String ruleAttributeGlobalValue = operands.get(1).getStrVal();
	                return ruleAttributeGlobalValue.compareTo(objectAttributeGlobalValue);
	            } else if (operands.get(1).getTimeVal() != null) {
	            	String ruleTimeValueString = operands.get(1).getTimeVal();
		            
		            LocalTime ruleTimeValueTime = LocalTime.parse(ruleTimeValueString);
		            LocalTime objectTimeValueTime = LocalTime.parse(objectAttributeGlobalValue);
		            
		            return ruleTimeValueTime.compareTo(objectTimeValueTime);
	            } else {
	                return 0;
	            }
	        } else {
	            return 0;
	        }
	    } else if (ruleGlobalItem == Global.CLIENTNOW) {
	        System.out.println("The Global enum is set to CLIENTNOW - Not Supported");
	        return 0;
	    } else if (ruleGlobalItem == Global.ANONYMOUS) {
	        System.out.println("The Global enum is set to ANONYMOUS - Not Supported");
	        return 0;
	    } else {
	        System.out.println("Unknown Global enum value");
	        return 0;
	    }
	}


	// ###############################################################################################################################################################

	// Recursive method to evaluate logical and simple expressions
	private boolean isRuleMatchingQuery(LogicalExpression query, LogicalExpression accessRule) {
		if (accessRule.get$and() != null && !accessRule.get$and().isEmpty()) {
			// All child expressions in $and must match
			return accessRule.get$and().stream().allMatch(rule -> query.get$and().stream().allMatch(query1 -> isRuleMatchingQuery(query1, rule)));
		}

		if (accessRule.get$or() != null && !accessRule.get$or().isEmpty()) {
			// At least one child expression in $or must match
			return accessRule.get$or().stream().anyMatch(rule -> query.get$or().stream().allMatch(query1 -> isRuleMatchingQuery(query1, rule)));
		}

		if (accessRule.get$not() != null) {
			// Negation: query must not match the $not condition
			return !isRuleMatchingQuery(query.get$not(), accessRule.get$not());
		}

		if (accessRule.get$eq() != null && !accessRule.get$eq().isEmpty()) {
			// Check equality
			return query.get$eq() != null && query.get$eq().equals(accessRule.get$eq());
		}

		if (accessRule.get$ne() != null && !accessRule.get$ne().isEmpty()) {
			// Check inequality
			return query.get$ne() != null && !query.get$ne().equals(accessRule.get$ne());
		}

		// Repeat similar logic for $gt, $ge, $lt, $le, $contains, etc.
		// Example for $contains:
		if (accessRule.get$contains() != null && !accessRule.get$contains().isEmpty()) {
			return query.get$contains() != null && query.get$contains().containsAll(accessRule.get$contains());
		}

		// Add other conditions as needed...

		// Default case: return false if no conditions are matched
		return false;
	}

	public boolean matchesAny(LogicalExpression query, List<LogicalExpression> accessRules) {
		for (LogicalExpression accessRule : accessRules) {
			if (isRuleMatchingQuery(query, accessRule)) {
				return true;
			}
		}
		return false;
	}

	//
	// // Evaluate simple expressions (e.g., $eq, $gt)
	// private boolean evaluateSimpleExpression(AllRule rule, SimpleExpression
	// simpleExpression) {
	// if (simpleExpression.get$eq() != null) {
	// return evaluateEquality(rule, simpleExpression.get$eq());
	// }
	// if (simpleExpression.get$gt() != null) {
	// return evaluateGreaterThan(rule, simpleExpression.get$gt());
	// }
	// // Add other evaluations ($lt, $contains, etc.)
	// return false;
	// }
	//
	// // Evaluate $eq condition by checking if the rule's formula satisfies the
	// query formula
	// private boolean evaluateEquality(AllRule rule, Object eqCondition) {
	// if (!(eqCondition instanceof List) || ((List<?>) eqCondition).size() != 2) {
	// throw new IllegalArgumentException("Invalid $eq condition: must have exactly
	// two elements");
	// }
	//
	// List<?> conditions = (List<?>) eqCondition;
	// String left = conditions.get(0).toString();
	// String right = conditions.get(1).toString();
	//
	// LogicalComponent ruleFormula = rule.getFormula();
	// if (ruleFormula == null) {
	// return false; // Base case: If the rule has no formula, return false
	// }
	//
	// // Create a SimpleExpression to represent the query condition
	// SimpleExpression queryExpression = new SimpleExpression();
	// queryExpression.set$eq(List.of(left, right));
	//
	// // Check if the rule's formula matches the query condition
	// return matchFormula(ruleFormula, queryExpression);
	// }
	//
	//
	// private boolean matchFormula(LogicalComponent ruleFormula, LogicalComponent
	// queryFormula) {
	// // Base case: If both are simple expressions, compare directly
	// if (ruleFormula instanceof SimpleExpression && queryFormula instanceof
	// SimpleExpression) {
	// return compareSimpleExpressions((SimpleExpression) ruleFormula,
	// (SimpleExpression) queryFormula);
	// }
	//
	// // Recursive case: If the rule is a logical expression, evaluate its
	// conditions
	// if (ruleFormula instanceof LogicalExpression__1) {
	// LogicalExpression__1 logicalRule = (LogicalExpression__1) ruleFormula;
	//
	// if (logicalRule.get$and() != null && !logicalRule.get$and().isEmpty()) {
	// // Check if the query formula satisfies all AND conditions
	// for (LogicalComponent condition : logicalRule.get$and()) {
	// if (!matchFormula(condition, queryFormula)) {
	// return false; // One AND condition fails
	// }
	// }
	// return true; // All AND conditions pass
	// }
	//
	// if (logicalRule.get$or() != null && !logicalRule.get$or().isEmpty()) {
	// // Check if the query formula satisfies at least one OR condition
	// for (LogicalComponent condition : logicalRule.get$or()) {
	// if (matchFormula(condition, queryFormula)) {
	// return true; // At least one OR condition matches
	// }
	// }
	// return false; // No OR condition matches
	// }
	//
	// if (logicalRule.get$not() != null) {
	// // Negate the match result for NOT condition
	// return !matchFormula(logicalRule.get$not(), queryFormula);
	// }
	// }
	//
	// // Default: If no match criteria met, return false
	// return false;
	// }
	//
	// private boolean compareSimpleExpressions(SimpleExpression ruleExpr,
	// SimpleExpression queryExpr) {
	// // Compare $eq conditions
	// if (ruleExpr.get$eq() != null && queryExpr.get$eq() != null) {
	// List<?> ruleEq = (List<?>) ruleExpr.get$eq();
	// List<?> queryEq = (List<?>) queryExpr.get$eq();
	// return ruleEq.equals(queryEq); // Compare the $eq lists
	// }
	//
	// // Add comparisons for other types like $gt, $lt if needed
	// return false;
	// }
	//
	//
	//
	// // Evaluate $gt condition
	// private boolean evaluateGreaterThan(AllRule rule, Object gtCondition) {
	// if (!(gtCondition instanceof List) || ((List<?>) gtCondition).size() != 2) {
	// throw new IllegalArgumentException("Invalid $gt condition: must have exactly
	// two elements");
	// }
	//
	// List<?> conditions = (List<?>) gtCondition;
	// double left = getNumericValue(rule, conditions.get(0));
	// double right = getNumericValue(rule, conditions.get(1));
	//
	// return left > right;
	// }
	//
	// // Helper function to extract numeric values
	// private double getNumericValue(AllRule rule, Object node) {
	// if (node instanceof Number) {
	// return ((Number) node).doubleValue();
	// } else if (node instanceof String) {
	// String attributeName = (String) node;
	// String value = getAttributeValueFromRule(rule, attributeName);
	// if (value != null) {
	// return Double.parseDouble(value);
	// }
	// }
	// throw new IllegalArgumentException("Invalid numeric value in condition");
	// }
	//
	// // Retrieve attribute value from rule
	// private String getAttributeValueFromRule(AllRule rule, String attributeName)
	// {
	// for (AttributeItem attribute : rule.getAttributes()) {
	// if (attribute.getClaim() != null &&
	// attribute.getClaim().equals(attributeName)) {
	// return attribute.getClaim();
	// }
	// }
	// return null;
	// }
	//
	// // Check if rule attributes match JWT claims
	// private boolean areAttributesMatching(AllRule rule, Jwt jwt) {
	// for (AttributeItem attribute : rule.getAttributes()) {
	// if (attribute.getClaim() != null) {
	// String claimValue = jwt.getClaim(attribute.getClaim());
	// if (claimValue == null || !claimValue.equals(attribute.getClaim())) {
	// return false;
	// }
	// }
	// }
	// return true;
	// }
	//
	// // Check if required rights match
	// private boolean areRightsMatching(List<RightsEnum> requiredRights,
	// List<RightsEnum> queryRights) {
	// return requiredRights.containsAll(queryRights);
	// }
	//
	// // Check if access is allowed
	// private boolean isAccessAllowed(AllRule rule) {
	// return rule.getAccess() == AllRule.Access.ALLOW;
	// }
	//
	// // Evaluate object matching (stub for now)
	// private boolean areObjectsMatching(AllRule rule) {
	// return true; // Assume all objects match for now
	// }
	//
	// // Get subject information
	private SubjectInformation<Object> getSubjectInformation() {
		SubjectInformation<Object> subjectInfo = subjectInformationProvider.get();

		if (subjectInfo == null)
			throw new NullSubjectException("Subject information is null.");

		return subjectInfo;
	}
}
