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
 *****************************************************************************/

package org.eclipse.digitaltwin.basyx.submodelrepository.feature.search;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public final class IndexNormalizer {
    private static final Logger logger = LoggerFactory.getLogger(IndexNormalizer.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Represents a node and its corresponding source object for iterative processing
     */
    private static class ProcessingNode {
        final JsonNode jsonNode;
        final Object sourceObject;
        final String pathPrefix; // e.g. "submodelElements.1234."
        final int childIndex; // Used for array elements to track the index
        private final Object parentObject;

        ProcessingNode(JsonNode jsonNode, Object sourceObject, Object parentObject, String pathPrefix, int childIndex) {
            this.jsonNode = jsonNode;
            this.sourceObject = sourceObject;
            this.pathPrefix = pathPrefix;
            this.childIndex = childIndex;
            this.parentObject = parentObject;
        }
    }

    public static JsonNode toIndexable(Submodel sm) throws SerializationException, IOException {
        JsonSerializer serializer = new JsonSerializer();
        String submodelAsString = serializer.write(sm);
        JsonNode root = MAPPER.readTree(new StringReader(submodelAsString));
        rewriteIteratively(root, sm);
        return root;
    }

    private static void rewriteIteratively(JsonNode rootNode, Object rootSourceObject) {
        Deque<ProcessingNode> stack = new ArrayDeque<>();
        stack.push(new ProcessingNode(rootNode, rootSourceObject,null, "submodelElements.",0));

        while (!stack.isEmpty()) {
            ProcessingNode current = stack.pop();
            JsonNode node = current.jsonNode;
            Object sourceObject = current.sourceObject;

            if (!node.isObject() || sourceObject == null) {
                continue;
            }

            ObjectNode obj = (ObjectNode) node;

            // Get the idShort to build path
            String idShort = obj.has("idShort") && !(sourceObject instanceof Submodel) ? obj.get("idShort").asText() : null;
            String currentPath = "";
            if (current.parentObject instanceof SubmodelElementList) {
                currentPath = current.pathPrefix.endsWith(".") ?  current.pathPrefix.substring(0, current.pathPrefix.length() - 1) :  current.pathPrefix;
                currentPath = currentPath + "["+current.childIndex+"]" + (current.sourceObject instanceof SubmodelElementList ? "" : ".");
            } else {
                currentPath = idShort != null ? current.pathPrefix + idShort + "." : current.pathPrefix;
            }

            // If this element has a "value" that is primitive, we can directly move it to the flattened key
            JsonNode valueNode = indexField(obj, "value", (ObjectNode) rootNode, currentPath);

            indexField(obj, "valueType", (ObjectNode) rootNode, currentPath);
            indexField(obj, "idShort", (ObjectNode) rootNode, currentPath);
            indexField(obj, "language", (ObjectNode) rootNode, currentPath);
            indexField(obj, "semanticId", (ObjectNode) rootNode, currentPath);
            indexField(obj, "supplementalSemanticIds", (ObjectNode) rootNode, currentPath);

            // Traverse children if it's a collection/list/etc.
            List<SubmodelElement> children = null;
            if (sourceObject instanceof Submodel) {
                children = ((Submodel) sourceObject).getSubmodelElements();
            } else if (sourceObject instanceof SubmodelElementCollection) {
                children = ((SubmodelElementCollection) sourceObject).getValue();
            } else if (sourceObject instanceof SubmodelElementList) {
                children = ((SubmodelElementList) sourceObject).getValue();
            }

            if (children != null) {
                for (int i = children.size() - 1; i >= 0; i--) {
                    JsonNode childNode = obj.get("value") != null ? obj.get("value").get(i) : (obj.get("submodelElements") != null ? obj.get("submodelElements").get(i) : null);
                    if (childNode != null) {
                        stack.push(new ProcessingNode(childNode,children.get(i), current.sourceObject, currentPath, i));
                    }
                }
            }
            if(valueNode != null && (valueNode.isObject() || valueNode.isArray())){
                // Remove Value if is non-primitive
                move(obj,"value", "_value");
            }
        }
    }

    private static JsonNode indexField(ObjectNode obj, String fieldName, ObjectNode rootNode, String currentPath) {
        JsonNode node = obj.get(fieldName);
        if (node != null && !node.isObject() && !node.isArray()) {
            // Set it at the flattened path
            rootNode.set(currentPath + (currentPath.endsWith("]") ? "." : "") + fieldName, node);
        }
        return node;
    }

    /**
     * Gets the child source object corresponding to a field name
     */
    private static Object getChildSourceObject(Object parent, String fieldName) {
        if (parent == null) return null;
        
        try {
            switch (fieldName) {
                case "submodelElements":
                    if (parent instanceof Submodel) {
                        return ((Submodel) parent).getSubmodelElements();
                    }
                    break;
                case "value":
                    if (parent instanceof SubmodelElementCollection) {
                        return ((SubmodelElementCollection) parent).getValue();
                    } else if (parent instanceof SubmodelElementList) {
                        return ((SubmodelElementList) parent).getValue();
                    } else if (parent instanceof Reference) {
                        return ((Reference) parent).getKeys();
                    } else if (parent instanceof MultiLanguageProperty) {
                        return ((MultiLanguageProperty) parent).getValue();
                    }
                    break;
                case "smcChildren": // Handle renamed field
                    if (parent instanceof SubmodelElementCollection) {
                        return ((SubmodelElementCollection) parent).getValue();
                    }
                    break;
                case "smlChildren": // Handle renamed field
                    if (parent instanceof SubmodelElementList) {
                        return ((SubmodelElementList) parent).getValue();
                    }
                    break;
                case "referenceChildren": // Handle renamed field
                    if (parent instanceof Reference) {
                        return ((Reference) parent).getKeys();
                    }
                    break;
                case "langContent": // Handle renamed field
                    if (parent instanceof MultiLanguageProperty) {
                        return ((MultiLanguageProperty) parent).getValue();
                    }
                    break;
                default:
                    // Handle dynamically created fields based on SubmodelElement idShort
                    if (parent instanceof SubmodelElement parentElement) {
                        String parentIdShort = parentElement.getIdShort();
                        // Check if this field name matches the idShort or is a nested field
                        if (fieldName.equals(parentIdShort)) {
                            if (parent instanceof SubmodelElementCollection) {
                                return ((SubmodelElementCollection) parent).getValue();
                            } else if (parent instanceof SubmodelElementList) {
                                return ((SubmodelElementList) parent).getValue();
                            } else if (parent instanceof ReferenceElement) {
                                return ((ReferenceElement) parent).getValue();
                            } else if (parent instanceof MultiLanguageProperty) {
                                return ((MultiLanguageProperty) parent).getValue();
                            } else if (parent instanceof Property) {
                                return parent; // For Property.value nested structure
                            }
                        }
                        // Handle nested .value fields (e.g., "porpa" field containing {value: "123"})
                        if (fieldName.equals("value") && parent instanceof Property) {
                            return parent;
                        }
                    }
                    
                    // Handle cases where we're processing arrays of SubmodelElements
                    if (parent instanceof List<?> parentList && !parentList.isEmpty()) {
                        // Try to find a SubmodelElement in the list that matches the field name
                        for (Object item : parentList) {
                            if (item instanceof SubmodelElement element && 
                                fieldName.equals(element.getIdShort())) {
                                return element;
                            }
                        }
                    }
                    break;
                case "semanticId":
                    if (parent instanceof HasSemantics) {
                        return ((HasSemantics) parent).getSemanticId();
                    }
                    break;
                case "keys":
                    if (parent instanceof Reference) {
                        return ((Reference) parent).getKeys();
                    }
                    break;
                // Add more field mappings as needed
            }
        } catch (Exception e) {
            logger.debug("Could not get child source object for field '{}' from parent type '{}'", 
                        fieldName, parent.getClass().getSimpleName());
        }
        
        return null;
    }

    /**
     * Gets the array child source object at a specific index
     */
    private static Object getArrayChildSourceObject(Object arraySource, int index) {
        if (arraySource == null) return null;
        
        try {
            if (arraySource instanceof List<?>) {
                List<?> list = (List<?>) arraySource;
                if (index >= 0 && index < list.size()) {
                    return list.get(index);
                }
            }
        } catch (Exception e) {
            logger.debug("Could not get array child source object at index {} from array type '{}'", 
                        index, arraySource.getClass().getSimpleName());
        }
        
        return null;
    }

    private static void move(ObjectNode node, String from, String to) {
        JsonNode v = node.remove(from);
        if (v != null) node.set(to, v);
    }
}
