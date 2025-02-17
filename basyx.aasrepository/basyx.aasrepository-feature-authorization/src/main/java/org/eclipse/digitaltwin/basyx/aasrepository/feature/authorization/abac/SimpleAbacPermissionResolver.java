/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization.abac;

import java.nio.file.attribute.AclEntryFlag;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.basyx.authorization.SubjectInformation;
import org.eclipse.digitaltwin.basyx.authorization.SubjectInformationProvider;
import org.eclipse.digitaltwin.basyx.authorization.abac.AbacPermissionResolver;
import org.eclipse.digitaltwin.basyx.authorization.abac.AbacStorage;
import org.eclipse.digitaltwin.basyx.authorization.abac.AccessPermissionRule;
import org.eclipse.digitaltwin.basyx.authorization.abac.Acl;
import org.eclipse.digitaltwin.basyx.authorization.abac.AttributeItem;
import org.eclipse.digitaltwin.basyx.authorization.abac.AttributeItem.Global;
import org.eclipse.digitaltwin.basyx.authorization.abac.LogicalExpression;
import org.eclipse.digitaltwin.basyx.authorization.abac.ObjectItem;
import org.eclipse.digitaltwin.basyx.authorization.abac.RightsEnum;
import org.eclipse.digitaltwin.basyx.authorization.abac.StringValue;
import org.eclipse.digitaltwin.basyx.authorization.abac.Value;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RoleProvider;
import org.eclipse.digitaltwin.basyx.core.exceptions.NullSubjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * An abstract permission resolver for ABAC
 * 
 * @param <T>
 * 
 */
public class SimpleAbacPermissionResolver implements AbacPermissionResolver {

	private Logger logger = LoggerFactory.getLogger(SimpleAbacPermissionResolver.class);
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

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
		List<AccessPermissionRule> allRules = abacStorage.getAbacRules();

//		List<AccessPermissionRule> filteredRules = filterAccessRules(allRules, rightsEnum, objectItem);
		List<AccessPermissionRule> filteredRules = abacStorage.getFilteredAbacRules(rightsEnum, Acl.Access.ALLOW, objectItem);
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
            return null; 
        }
    }

	private static List<AccessPermissionRule> filterAccessRules(List<AccessPermissionRule> accessRules, RightsEnum rightsEnum, ObjectItem objectItem) {
		return accessRules.stream().filter(rule -> containsRightForAction(rule.getAcl().getRights(), rightsEnum)).filter(rule -> Acl.Access.ALLOW.equals(rule.getAcl().getAccess()))
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

	private static boolean validateAccessRules(List<AccessPermissionRule> filteredRules, Map<String, Value> attributesMap, Jwt jwt) {
		
		return filteredRules.stream().anyMatch(rule -> evaluateFormula(rule.getFormula(), attributesMap, populateAttrItemsMapWithAttributeValue(rule.getAcl().getAttributes(), jwt)));
	}

	private static Map<String, Object> populateAttrItemsMapWithAttributeValue(List<AttributeItem> attributeItems, Jwt jwt) {
		
		Map<String, Object> attributeItemsMap = new HashMap<String, Object>();
		
		List<String> claims = attributeItems.stream().map(attributeItem -> attributeItem.getClaim()).filter(Objects::nonNull).collect(Collectors.toList());
		List<Global> globals = attributeItems.stream().map(attributeItem -> attributeItem.getGlobal()).filter(Objects::nonNull).collect(Collectors.toList());
			
		for (String claim : claims) {
			Object nestedClaimObject = getNestedClaim(jwt, claim);
			
			if (nestedClaimObject != null)
				attributeItemsMap.put("CLAIM#" + claim, nestedClaimObject);
		}
		
		for (Global global : globals) {
			if (global == Global.UTCNOW) {
				
				LocalTime utcNow = ZonedDateTime.now(ZoneId.of("UTC")).toLocalTime();
				
				attributeItemsMap.put("GLOBAL#UTCNOW", utcNow);
			} else if (global == Global.LOCALNOW) {
				
				LocalTime localNow = LocalTime.now();
				
				attributeItemsMap.put("GLOBAL#LOCALNOW", localNow);
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

		if (formula.get$eq() != null && !formula.get$eq().isEmpty()) {
			return evaluateEquality(formula.get$eq(), attributesMap, attributeItemsMap);
		}
		
		if (formula.get$ne() != null && !formula.get$ne().isEmpty()) {
			return evaluateInequality(formula.get$ne(), attributesMap, attributeItemsMap);
		}
		
		if (formula.get$le() != null && !formula.get$le().isEmpty()) {
			return evaluateLessOrEqual(formula.get$le(), attributesMap, attributeItemsMap);
		}
		
		if (formula.get$lt() != null && !formula.get$lt().isEmpty()) {
			return evaluateLessThan(formula.get$lt(), attributesMap, attributeItemsMap);
		}
		
		if (formula.get$ge() != null && !formula.get$ge().isEmpty()) {
			return evaluateGreaterOrEqual(formula.get$ge(), attributesMap, attributeItemsMap);
		}
		
		if (formula.get$gt() != null && !formula.get$gt().isEmpty()) {
			return evaluateGreaterThan(formula.get$gt(), attributesMap, attributeItemsMap);
		}
		// Add more cases for other operators ($gt, $lt, etc.)
		return false;
	}

	private static boolean evaluateEquality(List<Value> operands, Map<String, Value> attributesMap, Map<String, Object> attributeItemsMap) {
	    if (operands.size() != 2) return false;

	    if (operands.get(0).get$field() != null && operands.get(0).get$field().startsWith("$aas")) {
	        return evaluateFieldlEquality(operands, attributesMap);
	    }

	    if (operands.get(0).get$attribute() != null) {
	        return evaluateAttributeEquality(operands, attributeItemsMap);
	    }

	    return false;
	}

	private static boolean evaluateFieldlEquality(List<Value> operands, Map<String, Value> attributesMap) {
	    String field = operands.get(0).get$field();

	    if (operands.get(1).get$strVal() != null) {
	        String ruleValue = operands.get(1).get$strVal();

	        if (attributesMap.containsKey(field)) {
	            String objectValue = attributesMap.get(field).get$strVal();
	            return ruleValue.equals(objectValue);
	        }
	    } else if (operands.get(1).get$timeVal() != null) {
	        String ruleValue = operands.get(1).get$timeVal();

	        if (attributesMap.containsKey(field)) {
	            String objectValue = attributesMap.get(field).get$timeVal();
	            return ruleValue.equals(objectValue);
	        }
	    }
	    return false;
	}

	private static boolean evaluateAttributeEquality(List<Value> operands, Map<String, Object> attributeItemsMap) {
	    AttributeItem ruleAttributeItem = operands.get(0).get$attribute();

	    if (ruleAttributeItem.getClaim() != null) {
	        return evaluateClaimEquality(operands, attributeItemsMap);
	    } else if (ruleAttributeItem.getGlobal() != null) {
	        return evaluateGlobalEquality(operands, attributeItemsMap);
	    }
	    return false;
	}

	private static boolean evaluateClaimEquality(List<Value> operands, Map<String, Object> attributeItemsMap) {
	    String ruleClaimItem = operands.get(0).get$attribute().getClaim();
	    if (attributeItemsMap.containsKey("CLAIM#" + ruleClaimItem)) {
	        Object objectAttributeClaimValue = attributeItemsMap.get("CLAIM#" + ruleClaimItem);
	        String ruleAttributeClaimValue = operands.get(1).get$strVal();

	        if (ruleAttributeClaimValue != null) {
	        	
	        	if (objectAttributeClaimValue instanceof String)
	        		return ruleAttributeClaimValue.equals((String) objectAttributeClaimValue);
	        	else if (objectAttributeClaimValue instanceof List) {
	        		@SuppressWarnings("unchecked")
					List<String> claimValueList = (List<String>) objectAttributeClaimValue;
	        		
	        		return claimValueList.contains(ruleAttributeClaimValue);
				}
	        }
	    }
	    return false;
	}

	private static boolean evaluateGlobalEquality(List<Value> operands, Map<String, Object> attributeItemsMap) {
	    Global ruleGlobalItem = operands.get(0).get$attribute().getGlobal();

	    if (ruleGlobalItem == Global.LOCALNOW) {
	        System.out.println("The Global enum is set to LOCALNOW");

	        if (attributeItemsMap.containsKey("GLOBAL#LOCALNOW")) {
	            LocalTime objectAttributeGlobalValue = (LocalTime) attributeItemsMap.get("GLOBAL#LOCALNOW");

	            if (operands.get(1).get$attribute() != null) {
	                String ruleAttributeGlobalValue = operands.get(1).get$strVal();
	                return ruleAttributeGlobalValue.equals(objectAttributeGlobalValue);
	            } else if (operands.get(1).get$timeVal() != null) {
	                String ruleAttributeGlobalValue = operands.get(1).get$timeVal();
	                
	                LocalTime ruleTime = LocalTime.parse(ruleAttributeGlobalValue, FORMATTER);
	                
	                return !ruleTime.isBefore(objectAttributeGlobalValue) && !ruleTime.isAfter(objectAttributeGlobalValue);
	            } else {
	                return false;
	            }
	        } else {
	            return false;
	        }
	    } else if (ruleGlobalItem == Global.UTCNOW) {
	        System.out.println("The Global enum is set to UTCNOW");

	        if (attributeItemsMap.containsKey("GLOBAL#UTCNOW")) {
	            LocalTime objectAttributeGlobalValue = (LocalTime) attributeItemsMap.get("GLOBAL#UTCNOW");

	            if (operands.get(1).get$strVal() != null) {
	                String ruleAttributeGlobalValue = operands.get(1).get$strVal();
	                return ruleAttributeGlobalValue.equals(objectAttributeGlobalValue);
	            } else if (operands.get(1).get$timeVal() != null) {
	                String ruleAttributeGlobalValue = operands.get(1).get$timeVal();
	                
	                LocalTime ruleTime = LocalTime.parse(ruleAttributeGlobalValue, FORMATTER);
	                
	                return !ruleTime.isBefore(objectAttributeGlobalValue) && !ruleTime.isAfter(objectAttributeGlobalValue);
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
	    return operands.get(1).get$strVal() != null &&
	           operands.get(0).get$strVal() != null &&
	           operands.get(0).get$strVal().contains(operands.get(1).get$strVal());
	}

	private static boolean evaluateStartsWith(List<StringValue> operands, Map<String, Value> attributesMap) {
	    if (operands.size() != 2) return false;
	    return operands.get(1).get$strVal() != null &&
	           operands.get(0).get$strVal() != null &&
	           operands.get(0).get$strVal().startsWith(operands.get(1).get$strVal());
	}

	private static boolean evaluateEndsWith(List<StringValue> operands, Map<String, Value> attributesMap) {
	    if (operands.size() != 2) return false;
	    return operands.get(1).get$strVal() != null &&
	           operands.get(0).get$strVal() != null &&
	           operands.get(0).get$strVal().endsWith(operands.get(1).get$strVal());
	}

	private static int compareValues(List<Value> operands, Map<String, Value> attributesMap, Map<String, Object> attributeItemsMap) {
	    Value operand1Value = operands.get(0);
	    
	    if (operand1Value.get$field() != null && operand1Value.get$field().startsWith("$aas")) {
	        return evaluateFieldComparison(operands, attributesMap);
	    }
	    
	    if (operand1Value.get$attribute() != null) {
	        return evaluateAttributeComparison(operands, attributeItemsMap);
	    }

	    return 0;
	}
	
	private static int evaluateFieldComparison(List<Value> operands, Map<String, Value> attributesMap) {
	    String field = operands.get(0).get$field();

	    if (operands.get(1).get$strVal() != null) {
	        String ruleValue = operands.get(1).get$strVal();

	        if (attributesMap.containsKey(field)) {
	            String objectValue = attributesMap.get(field).get$strVal();
	            return ruleValue.compareTo(objectValue);
	        }
	    } else if (operands.get(1).get$timeVal() != null) {
	        String ruleTimeValueString = operands.get(1).get$timeVal();
	        
	        LocalTime ruleTimeValue = LocalTime.parse(ruleTimeValueString);

	        if (attributesMap.containsKey(field)) {
	            String objectTimeValueString = attributesMap.get(field).get$timeVal();
	            
	            LocalTime objectTimeValue = LocalTime.parse(objectTimeValueString);
	            
	            return ruleTimeValue.compareTo(objectTimeValue);
	        }
	    }
	    return 0;
	}
	
	private static int evaluateAttributeComparison(List<Value> operands, Map<String, Object> attributeItemsMap) {
	    AttributeItem ruleAttributeItem = operands.get(0).get$attribute();

	    if (ruleAttributeItem.getClaim() != null) {
	        return evaluateClaimComparison(operands, attributeItemsMap);
	    } else if (ruleAttributeItem.getGlobal() != null) {
	        return evaluateGlobalComparison(operands, attributeItemsMap);
	    }
	    return 0;
	}
	
	private static int evaluateClaimComparison(List<Value> operands, Map<String, Object> attributeItemsMap) {
	    String ruleClaimItem = operands.get(0).get$attribute().getClaim();
	    if (attributeItemsMap.containsKey("CLAIM#" + ruleClaimItem)) {
	        Object objectAttributeClaimValue = attributeItemsMap.get("CLAIM#" + ruleClaimItem);
	        String ruleAttributeClaimValue = operands.get(1).get$strVal();

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
	
	private static int evaluateGlobalComparison(List<Value> operands, Map<String, Object> attributeItemsMap) {
	    Global ruleGlobalItem = operands.get(0).get$attribute().getGlobal();

	    if (ruleGlobalItem == Global.LOCALNOW) {
	        System.out.println("The Global enum is set to LOCALNOW");

	        if (attributeItemsMap.containsKey("GLOBAL#LOCALNOW")) {
	            LocalTime objectAttributeGlobalValue = (LocalTime) attributeItemsMap.get("GLOBAL#LOCALNOW");

	            if (operands.get(1).get$strVal() != null) {
	                String ruleAttributeGlobalValue = operands.get(1).get$strVal();
	                
	                LocalTime ruleTime = LocalTime.parse(ruleAttributeGlobalValue, FORMATTER);
	                
	                return ruleTime.compareTo(objectAttributeGlobalValue);
	            } else if (operands.get(1).get$timeVal() != null) {
	            	String ruleTimeValueString = operands.get(1).get$timeVal();
		            
	            	LocalTime ruleTime = LocalTime.parse(ruleTimeValueString, FORMATTER);
		            
		            return objectAttributeGlobalValue.compareTo(ruleTime);
	            } else {
	                return 0;
	            }
	        } else {
	            return 0;
	        }
	    } else if (ruleGlobalItem == Global.UTCNOW) {
	        System.out.println("The Global enum is set to UTCNOW");

	        if (attributeItemsMap.containsKey("GLOBAL#UTCNOW")) {
	            LocalTime objectAttributeGlobalValue = (LocalTime) attributeItemsMap.get("GLOBAL#UTCNOW");

	            if (operands.get(1).get$strVal() != null) {
	                String ruleAttributeGlobalValue = operands.get(1).get$strVal();
	                
	                LocalTime ruleTime = LocalTime.parse(ruleAttributeGlobalValue, FORMATTER);
	                
	                return ruleTime.compareTo(objectAttributeGlobalValue);
	            } else if (operands.get(1).get$timeVal() != null) {
	            	String ruleTimeValueString = operands.get(1).get$timeVal();
		            
	            	LocalTime ruleTime = LocalTime.parse(ruleTimeValueString, FORMATTER);
		            
		            return objectAttributeGlobalValue.compareTo(ruleTime);
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

	// // Get subject information
	private SubjectInformation<Object> getSubjectInformation() {
		SubjectInformation<Object> subjectInfo = subjectInformationProvider.get();

		if (subjectInfo == null)
			throw new NullSubjectException("Subject information is null.");

		return subjectInfo;
	}
}
