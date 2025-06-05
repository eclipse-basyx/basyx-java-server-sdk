package org.eclipse.digitaltwin.basyx.core.query;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.json.JsonData;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * Converts Value and StringValue objects to ElasticSearch QueryDSL operations
 */
public class ValueConverter {
    
    private static final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    
    /**
     * Converts equality comparison between two values
     */
    public Query convertEqualityComparison(Value leftValue, Value rightValue) {
        String fieldName = extractFieldName(leftValue);
        Object value = extractValue(rightValue);
        
        if (fieldName != null && value != null) {
            return QueryBuilders.term()
                .field(fieldName)
                .value(convertToFieldValue(value))
                .build()._toQuery();
        }

        return QueryBuilders.matchAll().build()._toQuery();
    }

    /**
     * Converts inequality comparison between two values
     */
    public Query convertInequalityComparison(Value leftValue, Value rightValue) {
        String fieldName = extractFieldName(leftValue);
        Object value = extractValue(rightValue);

        if (fieldName != null && value != null) {
            return QueryBuilders.bool()
                .mustNot(QueryBuilders.term()
                    .field(fieldName)
                    .value(convertToFieldValue(value))
                    .build())
                .build()._toQuery();
        }

        return QueryBuilders.matchAll().build()._toQuery();
    }

/**
 * Converts range comparison between two values
 */

    public Query convertRangeComparison(Value leftValue,
                                        Value rightValue,
                                        String operator) {

        String fieldName = extractFieldName(leftValue);
        Object rawValue   = extractValue(rightValue);

        if (fieldName != null && rawValue != null) {
            JsonData value = JsonData.of(rawValue);   // works for all scalar types

            return QueryBuilders.range(r -> r
                    .untyped(u -> {                      // <— pick *untyped* variant
                        u.field(fieldName);
                        switch (operator) {              // map your operators
                            case "gt"  -> u.gt(value);
                            case "gte" -> u.gte(value);
                            case "lt"  -> u.lt(value);
                            case "lte" -> u.lte(value);
                            default    -> throw new IllegalArgumentException(
                                    "Unsupported operator: " + operator);
                        }
                        return u;                        // must return the builder
                    })
            );
        }

        // fall-back: match-all
        return QueryBuilders.matchAll(m -> m);
    }
    /**
     * Converts string comparison operations
     */
    public Query convertStringComparison(StringValue leftValue, StringValue rightValue, String operation) {
        String fieldName = extractStringFieldName(leftValue);
        String value = extractStringValue(rightValue);

        if (fieldName != null && value != null) {
            switch (operation) {
                case "contains":
                    return QueryBuilders.wildcard()
                        .field(fieldName)
                        .value("*" + escapeWildcard(value) + "*")
                        .build()._toQuery();

                case "starts-with":
                    return QueryBuilders.wildcard()
                        .field(fieldName)
                        .value(escapeWildcard(value) + "*")
                        .build()._toQuery();

                case "ends-with":
                    return QueryBuilders.wildcard()
                        .field(fieldName)
                        .value("*" + escapeWildcard(value))
                        .build()._toQuery();

                case "regex":
                    return QueryBuilders.regexp()
                        .field(fieldName)
                        .value(value)
                        .build()._toQuery();

                default:
                    return QueryBuilders.term()
                        .field(fieldName)
                        .value(value)
                        .build()._toQuery();
            }
        }

        return QueryBuilders.matchAll().build()._toQuery();
    }
    
    /**
     * Extracts field name from a Value object
     */
    private String extractFieldName(Value value) {
        if (value == null) return null;
        
        if (value.get$field() != null) {
            return convertModelFieldToElasticField(value.get$field());
        }
        
        // Handle cast operations - extract field from the casted value
        if (value.get$strCast() != null) {
            return extractFieldName(value.get$strCast());
        }
        if (value.get$numCast() != null) {
            return extractFieldName(value.get$numCast());
        }
        if (value.get$boolCast() != null) {
            return extractFieldName(value.get$boolCast());
        }
        if (value.get$hexCast() != null) {
            return extractFieldName(value.get$hexCast());
        }
        if (value.get$dateTimeCast() != null) {
            return extractFieldName(value.get$dateTimeCast());
        }
        if (value.get$timeCast() != null) {
            return extractFieldName(value.get$timeCast());
        }
        
        return null;
    }
    
    /**
     * Extracts field name from a StringValue object
     */
    private String extractStringFieldName(StringValue value) {
        if (value == null) return null;
        
        if (value.get$field() != null) {
            return convertModelFieldToElasticField(value.get$field());
        }
        
        if (value.get$strCast() != null) {
            return extractFieldName(value.get$strCast());
        }
        
        return null;
    }
    
    /**
     * Extracts the actual value from a Value object
     */
    private Object extractValue(Value value) {
        if (value == null) return null;
        
        if (value.get$strVal() != null) return value.get$strVal();
        if (value.get$numVal() != null) return value.get$numVal();
        if (value.get$boolean() != null) return value.get$boolean();
        if (value.get$hexVal() != null) return parseHexValue(value.get$hexVal());
        if (value.get$dateTimeVal() != null) return formatDate(value.get$dateTimeVal());
        if (value.get$timeVal() != null) return value.get$timeVal();
        
        // Handle date extraction functions
        if (value.get$dayOfWeek() != null) return extractDayOfWeek(value.get$dayOfWeek());
        if (value.get$dayOfMonth() != null) return extractDayOfMonth(value.get$dayOfMonth());
        if (value.get$month() != null) return extractMonth(value.get$month());
        if (value.get$year() != null) return extractYear(value.get$year());
        
        // Handle cast operations - extract value from casted expression
        if (value.get$strCast() != null) return convertToString(extractValue(value.get$strCast()));
        if (value.get$numCast() != null) return convertToNumber(extractValue(value.get$numCast()));
        if (value.get$boolCast() != null) return convertToBoolean(extractValue(value.get$boolCast()));
        if (value.get$hexCast() != null) return convertToHex(extractValue(value.get$hexCast()));
        if (value.get$dateTimeCast() != null) return convertToDateTime(extractValue(value.get$dateTimeCast()));
        if (value.get$timeCast() != null) return convertToTime(extractValue(value.get$timeCast()));
        
        return null;
    }
    
    /**
     * Extracts string value from a StringValue object
     */
    private String extractStringValue(StringValue value) {
        if (value == null) return null;
        
        if (value.get$strVal() != null) return value.get$strVal();
        if (value.get$strCast() != null) {
            Object castValue = extractValue(value.get$strCast());
            return castValue != null ? castValue.toString() : null;
        }
        
        return null;
    }
    
    /**
     * Converts model field patterns to ElasticSearch field names
     */
    private String convertModelFieldToElasticField(String modelField) {
        if (modelField == null) {
            return null;
        }
        
        String result = modelField;
        
        // Remove model prefixes and convert to actual field names
        if (modelField.startsWith("$aas#")) {
            result = modelField.replace("$aas#", "");
        } else if (modelField.startsWith("$sm#")) {
            result = modelField.replace("$sm#", "");
        } else if (modelField.startsWith("$sme")) {
            result = modelField.replaceFirst("\\$sme(?:\\.[^#]*)?#", "");
        } else if (modelField.startsWith("$cd#")) {
            result = modelField.replace("$cd#", "");
        } else if (modelField.startsWith("$aasdesc#")) {
            result = modelField.replace("$aasdesc#", "");
        } else if (modelField.startsWith("$smdesc#")) {
            result = modelField.replace("$smdesc#", "");
        }
        
        // Remove array brackets [] from field names
        result = result.replaceAll("\\[\\]", "");
        
        // Add .keyword suffix for string fields that need exact matching
        // This is typically needed for fields that contain text values
        if (isStringField(result)) {
            result = result + ".keyword";
        }
        
        return result;
    }
    
    /**
     * Determines if a field should have .keyword suffix for exact matching
     */
    private boolean isStringField(String fieldName) {
        // Add .keyword suffix for fields that typically contain string values that need exact matching
        return fieldName.contains("name") || 
               fieldName.contains("value") || 
               fieldName.contains("idShort") || 
               fieldName.contains("id") || 
               fieldName.contains("type") || 
               fieldName.contains("assetKind") || 
               fieldName.contains("assetType") || 
               fieldName.contains("globalAssetId") || 
               fieldName.contains("externalSubjectId");
    }
    
    /**
     * Converts Java object to ElasticSearch FieldValue
     */
    private FieldValue convertToFieldValue(Object value) {
        if (value instanceof String) {
            return FieldValue.of((String) value);
        } else if (value instanceof Number) {
            return FieldValue.of(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            return FieldValue.of((Boolean) value);
        } else {
            return FieldValue.of(value.toString());
        }
    }
    
    // Utility methods for value conversion and extraction
    
    private String parseHexValue(String hexVal) {
        if (hexVal.startsWith("16#")) {
            return hexVal.substring(3);
        }
        return hexVal;
    }
    
    private String formatDate(Date date) {
        return ISO_DATE_FORMAT.format(date);
    }
    
    private Integer extractDayOfWeek(Date date) {
        return date.getDay() + 1; // Java getDay() returns 0-6, convert to 1-7
    }
    
    private Integer extractDayOfMonth(Date date) {
        return date.getDate();
    }
    
    private Integer extractMonth(Date date) {
        return date.getMonth() + 1; // Java getMonth() returns 0-11, convert to 1-12
    }
    
    private Integer extractYear(Date date) {
        return date.getYear() + 1900; // Java getYear() returns years since 1900
    }
    
    private String convertToString(Object value) {
        return value != null ? value.toString() : null;
    }
    
    private Double convertToNumber(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    private Boolean convertToBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return null;
    }
    
    private String convertToHex(Object value) {
        if (value instanceof Number) {
            return "16#" + Integer.toHexString(((Number) value).intValue()).toUpperCase();
        }
        return value != null ? value.toString() : null;
    }
    
    private String convertToDateTime(Object value) {
        if (value instanceof Date) {
            return formatDate((Date) value);
        }
        return value != null ? value.toString() : null;
    }
    
    private String convertToTime(Object value) {
        return value != null ? value.toString() : null;
    }
    
    private String escapeWildcard(String value) {
        // Escape ElasticSearch wildcard characters
        return value.replace("\\", "\\\\")
                   .replace("*", "\\*")
                   .replace("?", "\\?");
    }
}