package org.eclipse.digitaltwin.basyx.core.query.elasticsearch;

import java.util.List;
import org.eclipse.digitaltwin.basyx.core.query.StringValue;
import org.eclipse.digitaltwin.basyx.core.query.Value;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.index.query.RegexpQueryBuilder;

/**
 * Converter for string operations (contains, starts-with, ends-with, regex) from AAS query structure to Elasticsearch queries
 */
public class StringOperatorConverter {
    
    /**
     * Converts a contains expression to an Elasticsearch wildcard query
     * 
     * @param values List of string values to check
     * @return Elasticsearch QueryBuilder
     */
    public static QueryBuilder convertContainsExpression(List<StringValue> values) {
        if (values.size() != 2) {
            return QueryBuilders.matchAllQuery();
        }
        
        String fieldName = extractStringFieldName(values.get(0), values.get(1));
        String fieldValue = extractStringFieldValue(values.get(0), values.get(1));
        
        if (fieldName == null || fieldValue == null) {
            return QueryBuilders.matchAllQuery();
        }
        
        // Use wildcard query with * before and after to represent "contains"
        return QueryBuilders.wildcardQuery(fieldName, "*" + fieldValue + "*");
    }
    
    /**
     * Converts a starts-with expression to an Elasticsearch prefix query
     * 
     * @param values List of string values to check
     * @return Elasticsearch QueryBuilder
     */
    public static QueryBuilder convertStartsWithExpression(List<StringValue> values) {
        if (values.size() != 2) {
            return QueryBuilders.matchAllQuery();
        }
        
        String fieldName = extractStringFieldName(values.get(0), values.get(1));
        String fieldValue = extractStringFieldValue(values.get(0), values.get(1));
        
        if (fieldName == null || fieldValue == null) {
            return QueryBuilders.matchAllQuery();
        }
        
        // Use prefix query for "starts with"
        return QueryBuilders.prefixQuery(fieldName, fieldValue);
    }
    
    /**
     * Converts an ends-with expression to an Elasticsearch wildcard query
     * 
     * @param values List of string values to check
     * @return Elasticsearch QueryBuilder
     */
    public static QueryBuilder convertEndsWithExpression(List<StringValue> values) {
        if (values.size() != 2) {
            return QueryBuilders.matchAllQuery();
        }
        
        String fieldName = extractStringFieldName(values.get(0), values.get(1));
        String fieldValue = extractStringFieldValue(values.get(0), values.get(1));
        
        if (fieldName == null || fieldValue == null) {
            return QueryBuilders.matchAllQuery();
        }
        
        // Use wildcard query with * at beginning to represent "ends with"
        return QueryBuilders.wildcardQuery(fieldName, "*" + fieldValue);
    }
    
    /**
     * Converts a regex expression to an Elasticsearch regexp query
     * 
     * @param values List of string values to check
     * @return Elasticsearch QueryBuilder
     */
    public static QueryBuilder convertRegexExpression(List<StringValue> values) {
        if (values.size() != 2) {
            return QueryBuilders.matchAllQuery();
        }
        
        String fieldName = extractStringFieldName(values.get(0), values.get(1));
        String fieldValue = extractStringFieldValue(values.get(0), values.get(1));
        
        if (fieldName == null || fieldValue == null) {
            return QueryBuilders.matchAllQuery();
        }
        
        // Use regexp query for regex matching
        return QueryBuilders.regexpQuery(fieldName, fieldValue);
    }
    
    /**
     * Helper method to extract field name from a pair of string values
     * 
     * @param value1 First string value
     * @param value2 Second string value
     * @return The field name or null if not found
     */
    private static String extractStringFieldName(StringValue value1, StringValue value2) {
        // Check if the first value has a field name
        if (value1.get$field() != null) {
            return normalizeFieldName(value1.get$field());
        }
        
        // Check if the second value has a field name
        if (value2.get$field() != null) {
            return normalizeFieldName(value2.get$field());
        }
        
        // Handle strCast field if present
        if (value1.get$strCast() != null && value1.get$strCast().get$field() != null) {
            return normalizeFieldName(value1.get$strCast().get$field());
        }
        
        if (value2.get$strCast() != null && value2.get$strCast().get$field() != null) {
            return normalizeFieldName(value2.get$strCast().get$field());
        }
        
        return null;
    }
    
    /**
     * Helper method to extract field value from a pair of string values
     * 
     * @param value1 First string value
     * @param value2 Second string value
     * @return The field value or null if not found
     */
    private static String extractStringFieldValue(StringValue value1, StringValue value2) {
        // If first value is a field reference, then second value should contain the value
        if (value1.get$field() != null || (value1.get$strCast() != null && value1.get$strCast().get$field() != null)) {
            return extractValueFromStringValue(value2);
        }
        
        // If second value is a field reference, then first value should contain the value
        if (value2.get$field() != null || (value2.get$strCast() != null && value2.get$strCast().get$field() != null)) {
            return extractValueFromStringValue(value1);
        }
        
        return null;
    }
    
    /**
     * Extract actual string value from StringValue object
     * 
     * @param value The StringValue object
     * @return The extracted string value
     */
    private static String extractValueFromStringValue(StringValue value) {
        if (value.get$strVal() != null) {
            return value.get$strVal();
        }
        
        // Handle strCast if present
        if (value.get$strCast() != null) {
            Value castedValue = value.get$strCast();
            if (castedValue.get$strVal() != null) {
                return castedValue.get$strVal();
            } else if (castedValue.get$numVal() != null) {
                return String.valueOf(castedValue.get$numVal());
            } else if (castedValue.get$boolean() != null) {
                return String.valueOf(castedValue.get$boolean());
            }
        }
        
        // Handle attribute reference if present
        if (value.get$attribute() != null) {
            if (value.get$attribute().getClaim() != null) {
                return value.get$attribute().getClaim();
            } else if (value.get$attribute().getReference() != null) {
                return value.get$attribute().getReference();
            }
        }
        
        return null;
    }
    
    /**
     * Normalize field name for Elasticsearch
     * Handles the special AAS field patterns
     * 
     * @param fieldName The AAS field name
     * @return Normalized field name for Elasticsearch
     */
    private static String normalizeFieldName(String fieldName) {
        // Reuse the normalization logic from ComparisonOperatorConverter
        return ComparisonOperatorConverter.extractFieldName(
            new Value() {{ set$field(fieldName); }}, 
            new Value());
    }
}