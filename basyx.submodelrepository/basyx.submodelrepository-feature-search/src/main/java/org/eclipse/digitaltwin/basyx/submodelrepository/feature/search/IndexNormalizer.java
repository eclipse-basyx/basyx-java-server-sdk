package org.eclipse.digitaltwin.basyx.submodelrepository.feature.search;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
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

        ProcessingNode(JsonNode jsonNode, Object sourceObject) {
            this.jsonNode = jsonNode;
            this.sourceObject = sourceObject;
        }
    }

    public static JsonNode toIndexable(Submodel sm) throws SerializationException {
        JsonNode root = MAPPER.valueToTree(sm);
        rewriteIteratively(root, sm);
        return root;
    }

    private static void rewriteIteratively(JsonNode rootNode, Object rootSourceObject) {
        Deque<ProcessingNode> stack = new ArrayDeque<>();
        stack.push(new ProcessingNode(rootNode, rootSourceObject));

        while (!stack.isEmpty()) {
            ProcessingNode current = stack.pop();
            JsonNode node = current.jsonNode;
            Object sourceObject = current.sourceObject;

            if (!node.isObject() || sourceObject == null) {
                continue;
            }

            ObjectNode obj = (ObjectNode) node;
            
            JsonNode valueNode = obj.get("value");
            if (valueNode != null && (valueNode.isObject() || valueNode.isArray())) {
                if (sourceObject instanceof SubmodelElementCollection) {
                    move(obj, "value", "smcChildren");
                } else if (sourceObject instanceof SubmodelElementList) {
                    move(obj, "value", "smlChildren");
                } else if (sourceObject instanceof Reference) {
                    move(obj, "value", "referenceChildren");
                } else if (sourceObject instanceof MultiLanguageProperty) {
                    move(obj, "value", "langContent");
                }
            }

            List<String> names = new ArrayList<>();
            obj.fieldNames().forEachRemaining(names::add);

            for (int i = names.size() - 1; i >= 0; i--) {
                String name = names.get(i);
                JsonNode child = obj.get(name);
                Object childSourceObject = getChildSourceObject(sourceObject, name);
                
                if (child.isArray()) {
                    for (int j = child.size() - 1; j >= 0; j--) {
                        JsonNode arrayChild = child.get(j);
                        Object arrayChildSource = getArrayChildSourceObject(childSourceObject, j);
                        stack.push(new ProcessingNode(arrayChild, arrayChildSource));
                    }
                } else if (child.isObject()) {
                    stack.push(new ProcessingNode(child, childSourceObject));
                }
            }
        }
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
