package org.eclipse.digitaltwin.basyx.core.query.elasticsearch;

import java.util.Date;
import java.util.List;
import org.eclipse.digitaltwin.basyx.core.query.Value;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

/**
 * Converter for comparison operators (=, !=, >, >=, <, <=) from AAS query structure to Elasticsearch queries
 */
public class ComparisonOperatorConverter {
    
    /**
     * Converts an equals expression to an Elasticsearch term query or script query for field-to-field comparison
     * 
     * @param values List of values to compare
     * @return Elasticsearch QueryBuilder
     */
    public static QueryBuilder convertEqualsExpression(List<Value> values) {
        if (values.size() != 2) {
            return QueryBuilders.matchAllQuery();
        }
        
        Value value1 = values.get(0);
        Value value2 = values.get(1);
        
        // Check if both values are field references (field-to-field comparison)
        if (value1.get$field() != null && value2.get$field() != null) {
            String field1 = normalizeFieldName(value1.get$field());
            String field2 = normalizeFieldName(value2.get$field());
            
            // Create a script query to compare the two fields
            String scriptSource = "doc['" + field1 + ".keyword'].value == doc['" + field2 + ".keyword'].value";
            return QueryBuilders.scriptQuery(
                new org.elasticsearch.script.Script(scriptSource)
            );
        }
        
        // Handle regular field-to-value comparison
        String fieldName = extractFieldName(value1, value2);
        Object fieldValue = extractFieldValue(value1, value2);
        
        if (fieldName == null || fieldValue == null) {
            return QueryBuilders.matchAllQuery();
        }
        
        // Use term query for exact matches
        return QueryBuilders.termQuery(fieldName, fieldValue);
    }
    
    /**
     * Converts a not equals expression to an Elasticsearch query
     * 
     * @param values List of values to compare
     * @return Elasticsearch QueryBuilder
     */
    public static QueryBuilder convertNotEqualsExpression(List<Value> values) {
        if (values.size() != 2) {
            return QueryBuilders.matchAllQuery();
        }
        
        QueryBuilder equalsQuery = convertEqualsExpression(values);
        
        BoolQueryBuilder notQuery = QueryBuilders.boolQuery();
        notQuery.mustNot(equalsQuery);
        
        return notQuery;
    }
    
    /**
     * Converts a greater than expression to an Elasticsearch range query
     * 
     * @param values List of values to compare
     * @return Elasticsearch QueryBuilder
     */
    public static QueryBuilder convertGreaterThanExpression(List<Value> values) {
        if (values.size() != 2) {
            return QueryBuilders.matchAllQuery();
        }
        
        String fieldName = extractFieldName(values.get(0), values.get(1));
        Object fieldValue = extractFieldValue(values.get(0), values.get(1));
        
        if (fieldName == null || fieldValue == null) {
            return QueryBuilders.matchAllQuery();
        }
        
        // Create range query with gt
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(fieldName);
        rangeQuery.gt(fieldValue);
        
        return rangeQuery;
    }
    
    /**
     * Converts a greater than or equals expression to an Elasticsearch range query
     * 
     * @param values List of values to compare
     * @return Elasticsearch QueryBuilder
     */
    public static QueryBuilder convertGreaterThanOrEqualsExpression(List<Value> values) {
        if (values.size() != 2) {
            return QueryBuilders.matchAllQuery();
        }
        
        String fieldName = extractFieldName(values.get(0), values.get(1));
        Object fieldValue = extractFieldValue(values.get(0), values.get(1));
        
        if (fieldName == null || fieldValue == null) {
            return QueryBuilders.matchAllQuery();
        }
        
        // Create range query with gte
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(fieldName);
        rangeQuery.gte(fieldValue);
        
        return rangeQuery;
    }
    
    /**
     * Converts a less than expression to an Elasticsearch range query
     * 
     * @param values List of values to compare
     * @return Elasticsearch QueryBuilder
     */
    public static QueryBuilder convertLessThanExpression(List<Value> values) {
        if (values.size() != 2) {
            return QueryBuilders.matchAllQuery();
        }
        
        String fieldName = extractFieldName(values.get(0), values.get(1));
        Object fieldValue = extractFieldValue(values.get(0), values.get(1));
        
        if (fieldName == null || fieldValue == null) {
            return QueryBuilders.matchAllQuery();
        }
        
        // Create range query with lt
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(fieldName);
        rangeQuery.lt(fieldValue);
        
        return rangeQuery;
    }
    
    /**
     * Converts a less than or equals expression to an Elasticsearch range query
     * 
     * @param values List of values to compare
     * @return Elasticsearch QueryBuilder
     */
    public static QueryBuilder convertLessThanOrEqualsExpression(List<Value> values) {
        if (values.size() != 2) {
            return QueryBuilders.matchAllQuery();
        }
        
        String fieldName = extractFieldName(values.get(0), values.get(1));
        Object fieldValue = extractFieldValue(values.get(0), values.get(1));
        
        if (fieldName == null || fieldValue == null) {
            return QueryBuilders.matchAllQuery();
        }
        
        // Create range query with lte
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(fieldName);
        rangeQuery.lte(fieldValue);
        
        return rangeQuery;
    }
    
    /**
     * Helper method to extract field name from a pair of values
     * 
     * @param value1 First value
     * @param value2 Second value
     * @return The field name or null if not found
     */
    public static String extractFieldName(Value value1, Value value2) {
        // Check if the first value has a field name
        if (value1.get$field() != null) {
            return normalizeFieldName(value1.get$field());
        }
        
        // Check if the second value has a field name
        if (value2.get$field() != null) {
            return normalizeFieldName(value2.get$field());
        }
        
        return null;
    }
    
    /**
     * Helper method to extract field value from a pair of values
     * 
     * @param value1 First value
     * @param value2 Second value
     * @return The field value or null if not found
     */
    public static Object extractFieldValue(Value value1, Value value2) {
        // If first value is a field reference, then second value should contain the value
        if (value1.get$field() != null) {
            return extractValueFromValue(value2);
        }
        
        // If second value is a field reference, then first value should contain the value
        if (value2.get$field() != null) {
            return extractValueFromValue(value1);
        }
        
        return null;
    }
    
    /**
     * Extract actual value from Value object
     * 
     * @param value The Value object
     * @return The extracted value as an Object
     */
    private static Object extractValueFromValue(Value value) {
        if (value.get$strVal() != null) {
            return value.get$strVal();
        } else if (value.get$numVal() != null) {
            return value.get$numVal();
        } else if (value.get$dateTimeVal() != null) {
            return value.get$dateTimeVal();
        } else if (value.get$timeVal() != null) {
            return value.get$timeVal();
        } else if (value.get$hexVal() != null) {
            return value.get$hexVal();
        } else if (value.get$boolean() != null) {
            return value.get$boolean();
        }
        
        // Handle casting if needed
        if (value.get$strCast() != null) {
            Object innerValue = extractValueFromValue(value.get$strCast());
            if (innerValue != null) {
                return String.valueOf(innerValue);
            }
        } else if (value.get$numCast() != null) {
            Object innerValue = extractValueFromValue(value.get$numCast());
            if (innerValue != null && innerValue instanceof String) {
                try {
                    return Double.parseDouble((String) innerValue);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        } else if (value.get$dateTimeCast() != null) {
            // Date handling would be more complex, simplified here
            return null;
        }
        
        return null;
    }
    
    /**
     * Normalize field name for Elasticsearch
     * Handles the special AAS field patterns by removing type prefixes
     * 
     * @param fieldName The AAS field name
     * @return Normalized field name for Elasticsearch
     */
    private static String normalizeFieldName(String fieldName) {
        // Handle AAS specific field patterns by removing the type prefixes completely
        if (fieldName.startsWith("$aas#")) {
            return fieldName.substring(5); // Remove the "$aas#" prefix
        } else if (fieldName.startsWith("$sm#")) {
            return fieldName.substring(4); // Remove the "$sm#" prefix
        } else if (fieldName.startsWith("$sme")) {
            // For $sme, extract everything after the '#' if present
            int hashIndex = fieldName.indexOf('#');
            if (hashIndex != -1) {
                return fieldName.substring(hashIndex + 1);
            }
            return fieldName.substring(4); // Remove the "$sme" prefix if no # found
        } else if (fieldName.startsWith("$cd#")) {
            return fieldName.substring(4); // Remove the "$cd#" prefix
        } else if (fieldName.startsWith("$aasdesc#")) {
            return fieldName.substring(9); // Remove the "$aasdesc#" prefix
        } else if (fieldName.startsWith("$smdesc#")) {
            return fieldName.substring(8); // Remove the "$smdesc#" prefix
        }
        
        // Replace dots with underscores for Elasticsearch compatibility
        return fieldName.replace(".", "_");
    }
}