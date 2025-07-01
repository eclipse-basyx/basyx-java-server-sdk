package org.eclipse.digitaltwin.basyx.submodelrepository.feature.search;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;

import java.util.ArrayList;
import java.util.List;

public final class IndexNormalizer {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static JsonNode toIndexable(Submodel sm) {
        JsonNode root = MAPPER.valueToTree(sm);
        rewriteRecursively(root);
        return root;
    }


    private static void rewriteRecursively(JsonNode node) {
        if (!node.isObject()) return;

        ObjectNode obj = (ObjectNode) node;
        String modelType = extractModelType(obj);

        switch (modelType) {
            case "SubmodelElementCollection":
            case "SubmodelElementList":
                move(obj, "value", "children");
                break;

            case "Reference":
            case "MultiLanguageProperty":
                move(obj, "value", "content");
                break;

            case "Blob":
                obj.remove("value");
                break;

            default:
                // nothing special
        }

        List<String> names = new ArrayList<>();
        obj.fieldNames().forEachRemaining(names::add);

        for (String name : names) {
            JsonNode child = obj.get(name);
            if (child.isArray())  child.forEach(IndexNormalizer::rewriteRecursively);
            else if (child.isObject()) rewriteRecursively(child);
        }
    }

    private static String extractModelType(ObjectNode node) {
        JsonNode mt = node.get("modelType");
        if (mt == null) return "";
        if (mt.isTextual()) return mt.asText();
        if (mt.isObject())  return mt.path("name").asText();
        return "";
    }

    private static void move(ObjectNode node, String from, String to) {
        JsonNode v = node.remove(from);
        if (v != null) node.set(to, v);
    }
}
