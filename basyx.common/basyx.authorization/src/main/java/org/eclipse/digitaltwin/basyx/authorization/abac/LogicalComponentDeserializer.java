package org.eclipse.digitaltwin.basyx.authorization.abac;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class LogicalComponentDeserializer extends JsonDeserializer<LogicalComponent> {

    @Override
    public LogicalComponent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // Check the type field
        String type = node.get("type").asText();
        if ("logicalExpression".equals(type)) {
            return p.getCodec().treeToValue(node, LogicalExpression__1.class);
        } else if ("simpleExpression".equals(type)) {
            return p.getCodec().treeToValue(node, SimpleExpression.class);
        }

        throw new IllegalArgumentException("Unknown type: " + type);
    }
}
