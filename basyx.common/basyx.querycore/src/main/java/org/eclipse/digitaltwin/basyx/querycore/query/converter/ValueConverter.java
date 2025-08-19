package org.eclipse.digitaltwin.basyx.querycore.query.converter;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.json.JsonData;
import org.eclipse.digitaltwin.basyx.querycore.query.model.StringValue;
import org.eclipse.digitaltwin.basyx.querycore.query.model.Value;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Converts Value and StringValue objects to ElasticSearch QueryDSL operations
 */
public class ValueConverter {
    
    private static final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    
    /**
     * Converts equality comparison between two values (supports field-to-field comparison)
     */
    public Query convertEqualityComparison(Value leftValue, Value rightValue) {
        String leftField = extractFieldName(leftValue);
        String rightField = extractFieldName(rightValue);

        if(!leftField.endsWith(".keyword")) {
            leftField = leftField + ".keyword";
        }

        // Field-to-field comparison
        if (leftField != null && rightField != null) {
            return createFieldToFieldComparison(leftField, rightField, "eq");
        }
        
        // Field-to-value comparison (existing logic)
        String fieldName = leftField != null ? leftField : extractFieldName(rightValue);
        Object value = leftField != null ? extractValue(rightValue) : extractValue(leftValue);
        
        if (fieldName != null && value != null) {
            // Check if this is an SME wildcard field
            if (isSmeWildcardField(fieldName)) {
                return createSmeWildcardQuery(fieldName, value.toString());
            }
            
            return QueryBuilders.term()
                .field(fieldName)
                .value(convertToFieldValue(value))
                .build()._toQuery();
        }

        return QueryBuilders.matchAll().build()._toQuery();
    }

    /**
     * Converts inequality comparison between two values (supports field-to-field comparison)
     */
    public Query convertInequalityComparison(Value leftValue, Value rightValue) {
        String leftField = extractFieldName(leftValue);
        String rightField = extractFieldName(rightValue);
        
        // Field-to-field comparison
        if (leftField != null && rightField != null) {
            return createFieldToFieldComparison(leftField, rightField, "ne");
        }
        
        // Field-to-value comparison (existing logic)
        String fieldName = leftField != null ? leftField : extractFieldName(rightValue);
        Object value = leftField != null ? extractValue(rightValue) : extractValue(leftValue);

        if (fieldName != null && value != null) {
            // Check if this is an SME wildcard field
            if (isSmeWildcardField(fieldName)) {
                return QueryBuilders.bool()
                    .mustNot(createSmeWildcardQuery(fieldName, value.toString()))
                    .build()._toQuery();
            }
            
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
 * Converts range comparison between two values (supports field-to-field comparison)
 */
    public Query convertRangeComparison(Value leftValue,
                                        Value rightValue,
                                        String operator) {
        String leftField = extractFieldName(leftValue);
        String rightField = extractFieldName(rightValue);
        
        // Field-to-field comparison
        if (leftField != null && rightField != null) {
            // Check if either value involves casting operations
            String leftCastType = detectCastType(leftValue);
            String rightCastType = detectCastType(rightValue);
            
            if (leftCastType != null || rightCastType != null) {
                return createFieldToFieldCastComparison(leftField, rightField, leftCastType, rightCastType, operator);
            } else {
                return createFieldToFieldComparison(leftField, rightField, operator);
            }
        }
        
        // Field-to-value comparison (existing logic)
        String fieldName = leftField != null ? leftField : extractFieldName(rightValue);
        Object rawValue = leftField != null ? extractValue(rightValue) : extractValue(leftValue);

        if (fieldName != null && rawValue != null) {
            // Check if this is an SME wildcard field
            if (isSmeWildcardField(fieldName)) {
                return createSmeWildcardRangeQuery(fieldName, rawValue, operator);
            }
            
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
     * Converts string comparison operations (supports field-to-field comparison)
     */
    public Query convertStringComparison(StringValue leftValue, StringValue rightValue, String operation) {
        String leftField = extractStringFieldName(leftValue);
        String rightField = extractStringFieldName(rightValue);
        
        // Field-to-field string comparison
        if (leftField != null && rightField != null) {
            return createFieldToFieldStringComparison(leftField, rightField, operation);
        }
        
        // Field-to-value comparison (existing logic)
        String fieldName = leftField != null ? leftField : extractStringFieldName(rightValue);
        String value = leftField != null ? extractStringValue(rightValue) : extractStringValue(leftValue);

        if (fieldName != null && value != null) {
            // Check if this is an SME wildcard field
            if (isSmeWildcardField(fieldName)) {
                return createSmeWildcardStringQuery(fieldName, value, operation);
            }

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
                    if (value.startsWith("^") && value.endsWith("$")) {
                        value = value.substring(1, value.length() - 1);
                    }
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
     * Creates field-to-field comparison using ElasticSearch script queries
     */
    private Query createFieldToFieldComparison(String leftField, String rightField, String operator) {
        String scriptSource;

        String scriptLeftField = convertToESFields(leftField);
        String scriptRightField = convertToESFields(rightField);

        if (!leftField.endsWith(".keyword")) {
            scriptLeftField += ".keyword";
        }
        if (!rightField.endsWith(".keyword")) {
            scriptRightField += ".keyword";
        }

        switch (operator) {
            case "eq":
                scriptSource = String.format("doc['%s'].value == doc['%s'].value", scriptLeftField, scriptRightField);
                break;
            case "ne":
                scriptSource = String.format("doc['%s'].value != doc['%s'].value", scriptLeftField, scriptRightField);
                break;
            case "gt":
                scriptSource = String.format("doc['%s'].value > doc['%s'].value", scriptLeftField, scriptRightField);
                break;
            case "gte":
                scriptSource = String.format("doc['%s'].value >= doc['%s'].value", scriptLeftField, scriptRightField);
                break;
            case "lt":
                scriptSource = String.format("doc['%s'].value < doc['%s'].value", scriptLeftField, scriptRightField);
                break;
            case "lte":
                scriptSource = String.format("doc['%s'].value <= doc['%s'].value", scriptLeftField, scriptRightField);
                break;
            default:
                throw new IllegalArgumentException("Unsupported field-to-field operator: " + operator);
        }

        String finalScriptLeftField = scriptLeftField;
        String finalScriptRightField = scriptRightField;
        return QueryBuilders.bool(b -> b
            .must(QueryBuilders.exists(e -> e.field(finalScriptLeftField)))
            .must(QueryBuilders.exists(e -> e.field(finalScriptRightField)))
            .must(QueryBuilders.script(s -> s
                .script(script -> script
                    .source(source -> source.scriptString(scriptSource))
                    .lang("painless")
                )
            ))
        );
    }
    
    /**
     * Creates field-to-field string comparison using script queries
     */
    private Query createFieldToFieldStringComparison(String leftField, String rightField, String operation) {
        String scriptSource;
        
        switch (operation) {
            case "contains":
                scriptSource = String.format("doc['%s'].value.contains(doc['%s'].value)", leftField, rightField);
                break;
            case "starts-with":
                scriptSource = String.format("doc['%s'].value.startsWith(doc['%s'].value)", leftField, rightField);
                break;
            case "ends-with":
                scriptSource = String.format("doc['%s'].value.endsWith(doc['%s'].value)", leftField, rightField);
                break;
            case "regex":
                scriptSource = String.format("doc['%s'].value ==~ doc['%s'].value", leftField, rightField);
                break;
            default:
                scriptSource = String.format("doc['%s'].value.equals(doc['%s'].value)", leftField, rightField);
                break;
        }
        
        return QueryBuilders.bool(b -> b
            .must(QueryBuilders.exists(e -> e.field(leftField)))
            .must(QueryBuilders.exists(e -> e.field(rightField)))
            .must(QueryBuilders.script(s -> s
                .script(script -> script
                    .source(source -> source.scriptString(scriptSource))
                    .lang("painless")
                )
            ))
        );
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
            boolean hasIdShortPath = !result.startsWith("$sme#");
            String idShortPath = "";
            if (hasIdShortPath) {
                int indexOfRaute = result.indexOf("#");
                idShortPath = result.substring(5, indexOfRaute);
            }
            result = modelField.replaceFirst("\\$sme(?:\\.[^#]*)?#", "");
            // Mark as SME field for wildcard handling
            if (!hasIdShortPath) {
                result = "SME_WILDCARD:" + result;
            } else {
                result = "submodelElements." + idShortPath+ "." + result;
            }
        } else if (modelField.startsWith("$cd#")) {
            result = modelField.replace("$cd#", "");
        } else if (modelField.startsWith("$aasdesc#")) {
            result = modelField.replace("$aasdesc#", "");
        } else if (modelField.startsWith("$smdesc#")) {
            result = modelField.replace("$smdesc#", "");
        }
        
        // Add .keyword suffix for string fields that need exact matching
        // This is typically needed for fields that contain text values
        if (isStringField(result)) {
            result = result + ".keyword";
        }
//        result = result.replace("semanticId.keys[]", "semanticId.keys.value");
//        result = result.replace("supplementalSemanticIds[].keys[]", "supplementalSemanticIds.keys.value");
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
            return FieldValue.of(value);
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
    
    private Number convertToNumber(Object value) {
        try{
            return (Number) value;
        }catch (ClassCastException e){
            return null;
        }
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
    
    /**
     * Detects the cast type of a Value object (str, num, bool, hex, date, time)
     */
    private String detectCastType(Value value) {
        if (value == null) return null;
        
        if (value.get$strCast() != null) {
            return "str";
        }
        if (value.get$numCast() != null) {
            return "num";
        }
        if (value.get$boolCast() != null) {
            return "bool";
        }
        if (value.get$hexCast() != null) {
            return "hex";
        }
        if (value.get$dateTimeCast() != null) {
            return "date";
        }
        if (value.get$timeCast() != null) {
            return "time";
        }
        
        return null;
    }
    
    /**
     * Creates field-to-field comparison with type casting using ElasticSearch script queries
     */
    private Query createFieldToFieldCastComparison(String leftField, String rightField, String leftCastType, String rightCastType, String operator) {
        String scriptSource;

        String scriptLeftField = convertToESFields(leftField);
        String scriptRightField = convertToESFields(rightField);

        if (!leftField.endsWith(".keyword")) {
            scriptLeftField += ".keyword";
        }
        if (!rightField.endsWith(".keyword")) {
            scriptRightField += ".keyword";
        }

        // Generate Painless script for type casting
        String leftCastScript = generateCastScript("doc['" + scriptLeftField + "'].value", leftCastType);
        String rightCastScript = generateCastScript("doc['" + scriptRightField + "'].value", rightCastType);

        switch (operator) {
            case "eq":
                scriptSource = String.format("%s == %s", leftCastScript, rightCastScript);
                break;
            case "ne":
                scriptSource = String.format("%s != %s", leftCastScript, rightCastScript);
                break;
            case "gt":
                scriptSource = String.format("%s > %s", leftCastScript, rightCastScript);
                break;
            case "gte":
                scriptSource = String.format("%s >= %s", leftCastScript, rightCastScript);
                break;
            case "lt":
                scriptSource = String.format("%s < %s", leftCastScript, rightCastScript);
                break;
            case "lte":
                scriptSource = String.format("%s <= %s", leftCastScript, rightCastScript);
                break;
            default:
                throw new IllegalArgumentException("Unsupported field-to-field operator: " + operator);
        }
        
        return QueryBuilders.bool(b -> b
            .must(QueryBuilders.exists(e -> e.field(leftField)))
            .must(QueryBuilders.exists(e -> e.field(rightField)))
            .must(QueryBuilders.script(s -> s
                .script(script -> script
                    .source(source -> source.scriptString(scriptSource))
                    .lang("painless")
                )
            ))
        );
    }
    
    /**
     * Generates Painless script for type casting
     */
    private String generateCastScript(String fieldExpression, String castType) {
        if (castType == null) {
            return fieldExpression; // No casting
        }
        
        switch (castType) {
            case "str":
                return fieldExpression + ".toString()";
            case "num":
                return String.format("Double.parseDouble(%s.toString())", fieldExpression);
            case "bool":
                return String.format("Boolean.parseBoolean(%s.toString())", fieldExpression);
            case "hex":
                // For hex values like "16#ABC", extract the hex part and convert to number
                return String.format("Integer.parseInt(%s.toString().substring(3), 16)", fieldExpression);
            case "date":
            case "time":
                // For date/time, return as string representation
                return fieldExpression + ".toString()";
            default:
                return fieldExpression; // No casting applied
        }
    }
    
    /**
     * Checks if a field is an SME wildcard field that needs special handling
     */
    private boolean isSmeWildcardField(String fieldName) {
        return fieldName != null && (fieldName.startsWith("SME_WILDCARD:") || fieldName.contains("[]"));
    }
    
    /**
     * Extracts the actual field name from an SME wildcard field
     */
    private String extractSmeFieldName(String wildcardField) {
        if (wildcardField.startsWith("SME_WILDCARD:")) {
            return wildcardField.substring("SME_WILDCARD:".length());
        }
        return wildcardField;
    }
    
    /**
     * Creates a wildcard query using QueryBuilders.queryString for SME fields at any nesting level
     */
    private Query createSmeWildcardQuery(String wildcardField, String value) {
        String fieldName = extractSmeFieldName(wildcardField);
        fieldName = convertToESFields(fieldName);
        // Add .keyword suffix for string fields that need exact matching
        String searchField = fieldName;

        // Create a wildcard pattern that matches the field at any nesting level
        // Pattern: submodelElements.*{fieldName}:{value} OR submodelElements.*.smcChildren.*{fieldName}:{value}

        String queryPattern = "*"+searchField;
        return QueryBuilders.queryString(q -> q
                .query(escapeQueryString(value))
                .fields(queryPattern.replace("[]","[*]"))
        );
    }

    /**
     * Creates a wildcard string query using QueryBuilders.queryString for SME fields at any nesting level
     */
    private Query createSmeWildcardStringQuery(String wildcardField, String value, String operation) {
        String fieldName = extractSmeFieldName(wildcardField);
        String searchField = convertToESFields(fieldName);

        String searchValue;
        switch (operation) {
            case "contains":
                searchValue = "*" + value + "*";
                break;
            case "starts-with":
                searchValue = value + "*";
                break;
            case "ends-with":
                searchValue = "*" + value;
                break;
            case "regex":
                searchValue = value;
                break;
            default:
                searchValue = escapeQueryString(value);
                break;
        }
        
        // Create a wildcard pattern that matches the field at any nesting level
        String queryPattern = "*"+searchField;

        return QueryBuilders.queryString(q -> q
                .query(searchValue)
                .fields(queryPattern)
        );
    }
    
    /**
     * Creates a wildcard range query using QueryBuilders.queryString for SME fields at any nesting level
     */
    private Query createSmeWildcardRangeQuery(String wildcardField, Object value, String operator) {
        String fieldName = extractSmeFieldName(wildcardField);
        
        String searchField = fieldName;

        String rangeOperator;
        switch (operator) {
            case "gt": rangeOperator = ">"; break;
            case "gte": rangeOperator = ">="; break;
            case "lt": rangeOperator = "<"; break;
            case "lte": rangeOperator = "<="; break;
            default: throw new IllegalArgumentException("Unsupported range operator: " + operator);
        }
        
        // Create a wildcard pattern that matches the field at any nesting level with range comparison
        String queryPattern = "*"+searchField;

        // Construct the range query string with proper syntax: fieldPattern:(>=value)
        String rangeQuery = queryPattern + ":(" + rangeOperator + value.toString() + ")";

        return QueryBuilders.queryString(q -> q
                .query(rangeQuery)
        );
    }
    
    /**
     * Escapes special characters for Elasticsearch query string queries
     */
    private String escapeQueryString(String value) {
        // Escape special query string characters
        return value.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("+", "\\+")
                   .replace("-", "\\-")
                   .replace("=", "\\=")
                   .replace("&&", "\\&&")
                   .replace("||", "\\||")
                   .replace("!", "\\!")
                   .replace("(", "\\(")
                   .replace(")", "\\)")
                   .replace("{", "\\{")
                   .replace("}", "\\}")
                   .replace("[", "\\[")
                   .replace("]", "\\]")
                   .replace("^", "\\^")
                   .replace("~", "\\~")
                   .replace("*", "\\*")
                   .replace(":", "\\:");
    }


    private static String convertToESFields(String fieldName) {
        fieldName = fieldName.replace("semanticId.keys[]", "semanticId.keys.value")
                .replace("supplementalSemanticIds[].keys[]", "supplementalSemanticIds.keys.value")
                .replace("submodels.keys[]", "submodels.keys.value")
                .replace("description[].language", "description.language")
                .replace("description[].text", "description.text")
                .replace("isCaseOf.keys[]", "isCaseOf.keys.value")
                .replace("endpoints[]", "endpoints");
        return fieldName;
    }
}